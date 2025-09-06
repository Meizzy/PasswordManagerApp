package com.burujiyaseer.passwordmanager.ui.view_password_manager

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarDefaults.InputField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.burujiyaseer.passwordmanager.R
import com.burujiyaseer.passwordmanager.ui.util.OnSVIcon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

@Preview(showSystemUi = true)
@Composable
fun ViewPasswordPreview() {
    ViewPasswordsScreen(
        value = ViewPasswordUIAction.ShowEmptyListPrompt,
        suggestions = listOf("amazing", "b").mapIndexed { i, s ->
            SuggestionModel(s, i < 1)
        }

//        SubmitData(
//            List(10) {
//                UIPasswordModel().run {
//                    PasswordManagerModel(
//                        entryId = "",
//                        title = it.toString()+"account",
//                        account = "account",
//                        username = username,
//                        websiteUrl = websiteUrl,
//                        favIconUrl = null,
//                        password = password,
//                        passwordFileName = "",
//                        description = "",
//                        createdAt = 0,
//                        updatedAt = 0
//                    )
//                }
//            }
//        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewPasswordsScreen(
    modifier: Modifier = Modifier,
    onSearchClick: ((String) -> Unit)? = null,
    onAddFabClick: ((String?) -> Unit)? = null,
    onSuggestion: ((String) -> Unit)? = null,
    onRemoveSuggestion: ((String) -> Unit)? = null,
    value: ViewPasswordUIAction? = ViewPasswordUIAction.ShowEmptyListPrompt,
    suggestions: List<SuggestionModel> = emptyList()
) {
    val listState = rememberLazyListState()
    val extendedFab by remember { derivedStateOf { !listState.isScrollInProgress } }
    var text by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        modifier = modifier.semantics { isTraversalGroup = true },
        topBar = {
            SearchBar(
                modifier = Modifier
                    .semantics { traversalIndex = 0f }
                    .padding(16.dp),
                inputField = {
                    InputField(
                        query = text,
                        onQueryChange = {
                            text = it
                            onSuggestion?.invoke(it)
                        },
                        onSearch = {
                            onSearchClick?.invoke(it)
                            expanded = false
                        },
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        placeholder = { Text(stringResource(R.string.search)) },
                        leadingIcon = {
                            if (expanded || text.isNotBlank()) IconButton(
                                onClick = {
                                    text = ""
                                    onSearchClick?.invoke("")
                                    expanded = false
                                }
                            ) {
                                OnSVIcon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack
                                )
                            }
                            else OnSVIcon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(android.R.string.search_go)
                            )
                        },
                        trailingIcon = {
                            if (expanded && text.isNotBlank())
                                IconButton(
                                    onClick = {
                                        text = ""
                                        onSuggestion?.invoke("")
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null
                                    )
                                }
                        },
                    )
                },
                expanded = expanded,
                onExpandedChange = { expanded = it },
                colors = SearchBarDefaults.colors(
                    containerColor = Color.Transparent,
                )
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp)
                ) {
                    items(suggestions) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                                .clickable {
                                text = it.suggestion
                                onSearchClick?.invoke(it.suggestion)
                                expanded = false
                            }
                        ) {
                            Row (
                                modifier = Modifier
                            ) {
                                if (it.saved) OnSVIcon(
                                    imageVector = Icons.Default.Refresh
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = it.suggestion,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            IconButton(
                                onClick = {
                                    onRemoveSuggestion?.invoke(it.suggestion)
                                }
                            ) {
                                OnSVIcon(
                                    imageVector = Icons.Default.Close
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Text(stringResource(R.string.add))
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_new_password)
                    )
                },
                onClick = { onAddFabClick?.invoke(null) },
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                expanded = extendedFab,
                modifier = Modifier.systemBarsPadding()
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal,
                    ),
                ),
        ) {
            when (value) {
                ViewPasswordUIAction.ShowEmptyListPrompt -> EmptyText(
                    textId = R.string.empty_password_entries
                )

                ViewPasswordUIAction.ShowEmptySearchResults -> EmptyText(
                    textId = R.string.no_search_results
                )

                is ViewPasswordUIAction.SubmitData -> LazyColumn(
                    state = listState
                ) {
                    val newList = List(value.filteredQueries.size * 10) {
                        value.filteredQueries[it % value.filteredQueries.size]
                    }
                    items(newList) { passwordModel ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AvatarImageFromFile(
                                    filePath = passwordModel.favIconUrl,
                                    title = passwordModel.title
                                )
                                Spacer(
                                    modifier = Modifier.width(14.dp)
                                )
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.SpaceAround
                                ) {
                                    Text(
                                        text = passwordModel.title,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = passwordModel.account,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        onAddFabClick?.invoke(passwordModel.entryId)
                                    }
                                ) {
                                    OnSVIcon(
                                        imageVector = Icons.Rounded.Edit,
                                        contentDescription = stringResource(R.string.edit)
                                    )
                                }
                            }
                        }
                    }
                }

                null -> Unit
            }

        }
    }
}

@Composable
fun AvatarImageFromFile(
    filePath: String?,
    title: String,
    sizeDp: Int = 40
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Load image off the main thread
    LaunchedEffect(filePath) {
        bitmap = withContext(Dispatchers.IO) {
            BitmapFactory.decodeFile(filePath)
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(sizeDp.dp)
                .clip(CircleShape)
        )
    } else {
        // Placeholder: initials with random background
        val initials = remember(title) {
            title.firstOrNull()?.uppercase() ?: "T"
        }

        val bgColor = remember(title) {
            // Random color based on username hash (so it's stable for the same name)
            val random = Random(title.hashCode())
            Color(
                red = random.nextInt(100, 256),
                green = random.nextInt(100, 256),
                blue = random.nextInt(100, 256)
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(sizeDp.dp)
                .clip(CircleShape)
                .background(bgColor)
        ) {
            Text(
                text = initials,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EmptyText(modifier: Modifier = Modifier, textId: Int) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(textId),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}