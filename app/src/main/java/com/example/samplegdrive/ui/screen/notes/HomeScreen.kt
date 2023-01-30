package com.example.samplegdrive.ui.screen.notes

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.samplegdrive.R
import com.example.samplegdrive.ui.screen.auth.AuthViewModel
import com.example.samplegdrive.util.Screen
import com.example.samplegdrive.util.getViewModelFactory
import kotlinx.coroutines.flow.collectLatest

private const val TAG = "HomeScreen"

@Composable
fun HomeScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    viewModel: NoteViewModel = viewModel(factory = getViewModelFactory()),
) {

    LaunchedEffect(key1 = true) {
        authViewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AuthViewModel.AuthState.LogOut -> {
                    navController.navigate(Screen.AuthScreen.route) {
                        popUpTo(Screen.HomeScreen.route) {
                            inclusive = true
                        }
                    }
                }
                else -> {}
            }
        }
    }

    HomeScreenContent(navController, authViewModel, viewModel)
}

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun HomeScreenContent(
    navController: NavHostController?, authViewModel: AuthViewModel?, viewModel: NoteViewModel?
) {

    var showUploadDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel?.eventFlow?.collectLatest { event ->
            when (event) {
                NoteViewModel.NoteEvents.FileUploadStarted -> {
                    showUploadDialog = true
                }
                NoteViewModel.NoteEvents.FileUploaded -> {
                    showUploadDialog = false
                    Toast.makeText(context, "Upload success", Toast.LENGTH_LONG).show()
                }
                NoteViewModel.NoteEvents.UploadFailed -> {
                    showUploadDialog = false
                    Toast.makeText(context, "Upload failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    var isMenuExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) }, actions = {
            Box(
                modifier = Modifier
                    .wrapContentSize(Alignment.TopStart)
            ) {
                IconButton(onClick = { isMenuExpanded = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = null)
                }
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false }) {
                    DropdownMenuItem(onClick = {
                        isMenuExpanded = false
                        navController?.navigate(Screen.RestoreDataScreen.route)
                    }) {
                        Text(
                            text = "Restore"
                        )
                    }
                    Divider()
                    DropdownMenuItem(
                        onClick = {
                            isMenuExpanded = false
                            viewModel?.uploadDB()
                        }
                    ) { Text(text = "Backup") }
                    Divider()
                    DropdownMenuItem(
                        onClick = {
                            isMenuExpanded = false
                            viewModel?.addDummyData()
                        }
                    ) { Text(text = "Add 100 dummy data") }
                    Divider()
                    DropdownMenuItem(
                        onClick = {
                            isMenuExpanded = false
                            viewModel?.clearDatabase()
                        }
                    ) { Text(text = "Clear Database") }
                    Divider()
                    DropdownMenuItem(
                        onClick = {
                            isMenuExpanded = false
                            authViewModel?.signOut()
                        }
                    ) {
                        Text(text = "Logout")
                    }
                }
            }
        })
    }, floatingActionButton = {
        FloatingActionButton(onClick = {
            navController?.navigate(Screen.AddNoteScreen.route)
        }) {
            Icon(Icons.Filled.Add, contentDescription = null)
        }
    }) {
        Column(modifier = Modifier.padding(it)) {
            val noteData =
                viewModel?.getAllNotes()?.collectAsStateWithLifecycle(initialValue = listOf())
            val noteList = noteData?.value
            when (viewModel?.uiState?.value?.screenState) {
                NoteViewModel.NoteScreenState.Loaded, NoteViewModel.NoteScreenState.ReLoaded -> {
                    if (noteList.isNullOrEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "Add some notes", style = MaterialTheme.typography.h6)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Or")
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { navController?.navigate(Screen.RestoreDataScreen.route) }) {
                                Text(text = "Restore from drive")
                            }
                        }
                    } else {
                        LazyColumn {
                            items(noteList) { note ->
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text(
                                        text = note.content,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp, horizontal = 8.dp)
                                    )
                                    Divider()
                                }
                            }
                        }
                    }
                }
                NoteViewModel.NoteScreenState.Loading -> {}
                null -> {}
            }
        }
    }

    if (showUploadDialog) {
        AlertDialog(onDismissRequest = { }, title = { Text("Backing Data") }, buttons = {}, text = {
            Text(
                text = "This function uploads data from local db to google drive"
            )
        })
    }
}

@Preview
@Composable
fun HomePreview() {
    HomeScreenContent(navController = null, authViewModel = null, viewModel = null)
}