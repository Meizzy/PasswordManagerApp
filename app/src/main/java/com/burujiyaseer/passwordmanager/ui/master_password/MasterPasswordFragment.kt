package com.burujiyaseer.passwordmanager.ui.master_password

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import androidx.core.animation.addListener
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.burujiyaseer.passwordmanager.R
import com.burujiyaseer.passwordmanager.core.BaseFragment
import com.burujiyaseer.passwordmanager.databinding.FragmentMasterPasswordBinding
import com.burujiyaseer.passwordmanager.domain.model.PasswordStrength
import com.burujiyaseer.passwordmanager.ui.util.biometric_auth.BiometricAuthHelper
import com.burujiyaseer.passwordmanager.ui.util.biometric_auth.BiometricAuthHelperImpl
import com.burujiyaseer.passwordmanager.ui.util.biometric_auth.BiometricResult
import com.burujiyaseer.passwordmanager.ui.util.collectFlowLatest
import com.burujiyaseer.passwordmanager.ui.util.collectFlowLatestOnce
import com.burujiyaseer.passwordmanager.ui.util.onTextAdded
import com.burujiyaseer.passwordmanager.ui.util.utilLog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MasterPasswordFragment : BaseFragment<FragmentMasterPasswordBinding>(
    FragmentMasterPasswordBinding::inflate
) {

    private val biometricAuthHelper: BiometricAuthHelper by lazy { BiometricAuthHelperImpl() }

    private val viewModel by viewModels<MasterPasswordViewModel>()

    override fun FragmentMasterPasswordBinding.setupViewListeners() {
        binding.apply {
            etMasterPassword.requestFocus()
            btnSave.setOnClickListener { viewModel.registerMasterPassword() }
            etMasterPassword.onTextAdded { inputText ->
                viewModel.doAfterTextChanged(inputText)
            }
        }
    }

    override fun setupDataListeners() {
        collectFlowLatest(viewModel.masterPasswordUIState) { uiState ->
            onAction(uiState)
        }
        collectFlowLatestOnce(viewModel.hasNotPreviouslySignedIn) { firstTimeSignIn ->
            setupViewsBySignInStatus(firstTimeSignIn == true)
        }
    }

    private fun setupViewsBySignInStatus(isFirstTime: Boolean) {
        binding.apply {
            btnSave.isVisible = isFirstTime
            if (isFirstTime) showEducationalMasterPasswordDialog()
            else setupBiometricAuth()
        }
    }

    private fun setupBiometricAuth() {
        lifecycleScope.launch {
            biometricAuthHelper.authenticate(this@MasterPasswordFragment).also { biometricResult ->
                utilLog("authenticate: result: $biometricResult")
                when (biometricResult) {
                    BiometricResult.CanceledBySystem,
                    BiometricResult.CanceledByUser,
                    BiometricResult.HardwareUnavailableOrDisabled -> Unit

                    is BiometricResult.Failure -> getString(
                        R.string.biometric_error,
                        biometricResult.message
                    )

                    BiometricResult.Retry -> biometricAuthHelper.authenticate(this@MasterPasswordFragment)
                    is BiometricResult.Success -> signInAnimAndNavigate()
                }
            }

        }
    }

    private fun showEducationalMasterPasswordDialog() {
        MaterialAlertDialogBuilder(context ?: return).apply {
            setTitle(R.string.master_password_hint)
            setMessage(R.string.first_time_dialog_message)
            setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
        }.show()
    }

    private fun onAction(uiAction: MasterPasswordUIAction) {
        when (uiAction) {
            is MasterPasswordUIAction.PasswordStrengthResult -> showHelperTextByPasswordStrength(
                uiAction.passwordStrength
            )

            is MasterPasswordUIAction.ValidatePassword -> {
                if (uiAction.isCorrect) signInAnimAndNavigate()
            }
        }
    }

    private fun navigateToViewPasswordFragment() {
        findNavController().navigate(R.id.viewPasswordsFragment,
            bundleOf(), navOptions {
                //remove this fragment from back stack
                //and run custom transition animation
                launchSingleTop = true
                popUpTo(R.id.masterPasswordFragment) {
                    inclusive = true
                }
                anim { transitionHelper.invokeSharedAxisTransitionForward() }
            })
    }

    private fun showHelperTextByPasswordStrength(passwordStrength: PasswordStrength) {
        binding.tlMasterPassword.apply {
            binding.btnSave.isEnabled = passwordStrength != PasswordStrength.WEAK
            when (passwordStrength) {
                PasswordStrength.WEAK -> {
                    error = getString(R.string.weak_helper_text)
                    endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
                }

                PasswordStrength.OKAY -> {
                    val yellowColor = resources.getColor(R.color.warning_color, activity?.theme)
                    setHelperTextColor(ColorStateList.valueOf(yellowColor))
                    boxStrokeColor = yellowColor
                    hintTextColor = ColorStateList.valueOf(yellowColor)
                    helperText = getString(R.string.okay_helper_text)
                }

                PasswordStrength.STRONG -> {
                    val greenColor = resources.getColor(R.color.md_green_900, activity?.theme)
                    setHelperTextColor(ColorStateList.valueOf(greenColor))
                    boxStrokeColor = greenColor
                    hintTextColor = ColorStateList.valueOf(greenColor)
                    helperText = getString(R.string.strong_helper_text)
                }
            }

        }
    }

    private fun signInAnimAndNavigate() = AnimatorSet().apply {
        //deny access to input fields during animations and navigation
        binding.tlMasterPassword.isEnabled = false

        val lockedIconView = binding.ivLock
        val unlockedIconView = binding.ivUnLock

        // Initially hide the unlocked icon outside the visible area
        unlockedIconView.alpha = OPAQUE_ALPHA
        val height = unlockedIconView.height.toFloat() / HALF_FACTOR
        unlockedIconView.translationY = height// Move the unlocked icon below the visible area

        val fadeInUnlocked = ObjectAnimator.ofFloat(
            unlockedIconView,
            ALPHA_PROPERTY,
            OPAQUE_ALPHA,
            TRANSPARENT_ALPHA
        )
        val fadeOutLocked = ObjectAnimator.ofFloat(
            lockedIconView,
            ALPHA_PROPERTY,
            TRANSPARENT_ALPHA,
            OPAQUE_ALPHA
        )
        val translateUnlocked = ObjectAnimator.ofFloat(
            unlockedIconView,
            TRANSLATION_Y_PROPERTY,
            height,
            INITIAL_TRANSLATION
        )
        val translateLocked = ObjectAnimator.ofFloat(
            lockedIconView,
            TRANSLATION_Y_PROPERTY,
            INITIAL_TRANSLATION,
            height
        )

        // Create animator set to combine all animations
        playSequentially(fadeOutLocked, fadeInUnlocked)
        playSequentially(translateLocked, translateUnlocked)
        duration = ANIMATION_DURATION

        // Hide lockedIconView after animation has ended
        //and the navigate to the view password fragment
        addListener(onEnd = {
            lockedIconView.isVisible = false
            navigateToViewPasswordFragment()
        })
        // Start the animation
        start()
    }

    companion object {
        private const val OPAQUE_ALPHA = 0f
        private const val TRANSPARENT_ALPHA = 1f
        private const val HALF_FACTOR = 2
        private const val INITIAL_TRANSLATION = 0f
        private const val ANIMATION_DURATION = 1000L // 1 second
        private const val ALPHA_PROPERTY = "alpha"
        private const val TRANSLATION_Y_PROPERTY = "translationY"
    }
}