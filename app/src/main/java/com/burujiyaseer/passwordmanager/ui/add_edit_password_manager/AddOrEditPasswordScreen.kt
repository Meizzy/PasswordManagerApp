package com.burujiyaseer.passwordmanager.ui.add_edit_password_manager

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.burujiyaseer.passwordmanager.R
import com.burujiyaseer.passwordmanager.ui.util.utilLog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrEditPasswordScreen(
    modifier: Modifier = Modifier,
    viewModel: AddOrEditPasswordManagerViewModel,
    popScreen: () -> Unit
) {
    val passwordState by viewModel.currentPasswordUIState.collectAsState()
    var dialogState by remember { mutableStateOf(DialogState.Dismiss) }
    Scaffold(
        modifier = modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(passwordState.titleResId))
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            dialogState = DialogState.Leave
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                },
                actions = {
                    if (passwordState.addDeleteMenu) {
                        IconButton(
                            onClick = {
                                dialogState = DialogState.Delete
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = stringResource(android.R.string.cancel)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                )
            )
        },
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal
                    ),
                )
                .verticalScroll(rememberScrollState()),
        ) {

            var loadingState by remember { mutableStateOf(false) }
            val doneButtonState =
                viewModel.shouldEnableDoneButton.collectAsState().value
            val state = viewModel.uiActionFlow.collectAsState().value
            utilLog("passwordState: $passwordState")
            loadingState = state is AddEditPasswordManagerAction.ShowLoader

            if (state == AddEditPasswordManagerAction.NavigateBack) {
                popScreen()
            }
            BuildDialog(
                dialogState = dialogState,
                onDismissClick = { dialogState = DialogState.Dismiss },
                onConfirmClick = {
                    dialogState = DialogState.Dismiss
                    if (it == DialogState.Delete) viewModel.positiveDeletePasswordEntry()
                    else popScreen()
                }
            )
            TextField(
                initialText = passwordState.uiPasswordModel.title,
                isRequired = true,
                labelTextId = R.string.title_hint,
                iconId = R.drawable.ic_title,
                onTextChanged = viewModel::updateTitle
            )
            TextField(
                initialText = passwordState.uiPasswordModel.account,
                isRequired = false,
                labelTextId = R.string.account_hint,
                iconId = R.drawable.ic_account_circle,
                onTextChanged = viewModel::updateAccount
            )
            TextField(
                initialText = passwordState.uiPasswordModel.username,
                isRequired = false,
                labelTextId = R.string.username_hint,
                iconId = R.drawable.ic_person,
                onTextChanged = viewModel::updateUsername
            )
            TextField(
                initialText = passwordState.uiPasswordModel.password,
                isRequired = true,
                isPassword = true,
                labelTextId = R.string.password_hint,
                iconId = R.drawable.ic_password,
                onTextChanged = viewModel::updatePassword
            )
            TextField(
                initialText = passwordState.uiPasswordModel.websiteUrl,
                isRequired = true,
                labelTextId = R.string.website_hint,
                iconId = R.drawable.ic_website,
                onTextChanged = viewModel::updateWebsite
            )
            TextField(
                initialText = passwordState.uiPasswordModel.description,
                isRequired = false,
                labelTextId = R.string.description_hint,
                iconId = R.drawable.ic_title,
                onTextChanged = viewModel::updateDescription
            )
            Button(
                enabled = doneButtonState.isEnabled,
                modifier = Modifier
                    .systemBarsPadding()
                    .padding(horizontal = 64.dp, vertical = 32.dp)
                    .fillMaxWidth(),
                onClick = {
                    viewModel.savePasswordEntry()
                }) {
                Text(
                    text = stringResource(R.string.done)
                )
            }

            if (loadingState) {
                LoadingScreen()
            }

        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun BuildDialog(
    dialogState: DialogState,
    onConfirmClick: (DialogState) -> Unit,
    onDismissClick: () -> Unit
) {
    if (dialogState.showDialog) {
        BasicAlertDialog(
            onDismissRequest = onDismissClick,
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.warning_dialog_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                    dialogState.supportTextId?.let {
                        Text(
                            text = stringResource(it),
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(16.dp)
                    ) {
                        TextButton(
                            onClick = onDismissClick
                        ) {
                            Text(
                                text = stringResource(android.R.string.cancel),
                                color = Color.Blue
                            )
                        }
                        TextButton(
                            onClick = {
                                onConfirmClick(dialogState)
                            }
                        ) {
                            Text(
                                text = stringResource(android.R.string.ok),
                                color = Color.Blue
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TextField(
    modifier: Modifier = Modifier,
    initialText: String,
    isPassword: Boolean = false,
    isRequired: Boolean,
    labelTextId: Int,
    iconId: Int,
    onTextChanged: (String) -> Unit
) {
    var text by rememberSaveable(initialText) {
        //trigger on initial
        onTextChanged(initialText)
        mutableStateOf(initialText)
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Icon(
            painter = painterResource(iconId),
            contentDescription = stringResource(labelTextId)
        )
        Spacer(Modifier.width(16.dp))
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                onTextChanged(it)
            },
            visualTransformation = if (isPassword) PasswordVisualTransformation()
            else VisualTransformation.None,
            supportingText = {
                if (isRequired) Text(text = stringResource(R.string.required_helper_text))
            },
            label = {
                Text(stringResource(labelTextId))
            }
        )
    }
}

@Composable
fun LoadingScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}

enum class DialogState(val showDialog: Boolean, val supportTextId: Int?) {
    Delete(true, R.string.delete_alert),
    Leave(true, R.string.leave_alert),
    Dismiss(false, null)
}