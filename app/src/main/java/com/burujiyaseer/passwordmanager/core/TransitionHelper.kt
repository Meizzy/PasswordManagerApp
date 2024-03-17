package com.burujiyaseer.passwordmanager.core

import android.view.animation.PathInterpolator
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis

class TransitionHelper(
    private val fragment: Fragment
) {

    private fun getSharedAxisInterpolator() =
        PathInterpolator(
            CONTROL_X_1,
            CONTROL_Y_1,
            CONTROL_X_2,
            CONTROL_Y_2
        )

    fun invokeSharedAxisTransitionForward() {
        fragment.reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
            duration = SHARED_AXIS_DURATION
            interpolator = getSharedAxisInterpolator()
        }
        fragment.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            duration = SHARED_AXIS_DURATION
            interpolator = getSharedAxisInterpolator()
        }
    }

    fun invokeSharedAxisTransitionBackward() {
        fragment.enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            duration = SHARED_AXIS_DURATION
            interpolator = getSharedAxisInterpolator()
        }
        fragment.returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
            duration = SHARED_AXIS_DURATION
            interpolator = getSharedAxisInterpolator()
        }
    }

    fun invokeContainerTransformTransition() {
        fragment.exitTransition = MaterialElevationScale(false).apply {
            duration = CONTAINER_TRANSFORM_DURATION
        }
        fragment.reenterTransition = MaterialElevationScale(true).apply {
            duration = CONTAINER_TRANSFORM_DURATION
        }
    }

    fun invokeFadeThroughTransition() {
        fragment.enterTransition = MaterialFadeThrough()
        fragment.exitTransition = MaterialFadeThrough()
    }

    fun invokeTopLevelEnterTransition() {
        fragment.enterTransition = MaterialFadeThrough()
        fragment.exitTransition = null
    }

    fun cancelTransition() {
        fragment.enterTransition = null
        fragment.returnTransition = null
    }

    companion object {
        private const val CONTROL_X_1 = 0.05f
        private const val CONTROL_Y_1 = 0.7f
        private const val CONTROL_X_2 = 0.1f
        private const val CONTROL_Y_2 = 1f
        private const val SHARED_AXIS_DURATION = 400L
        private const val CONTAINER_TRANSFORM_DURATION = 300L
    }
}