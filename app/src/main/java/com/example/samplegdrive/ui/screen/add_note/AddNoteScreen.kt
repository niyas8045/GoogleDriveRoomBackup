package com.example.samplegdrive.ui.screen.add_note

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.samplegdrive.util.getViewModelFactory
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddNoteScreen(
    navController: NavHostController,
    viewModel: AddNoteViewModel = viewModel(factory = getViewModelFactory()),
) {

    LaunchedEffect(key1 = true, block = {
        viewModel.eventFlow.collectLatest {
            when (it) {
                AddNoteViewModel.AddNoteEvents.Added -> {
                    navController.popBackStack()
                }
            }
        }
    })
    AddScreenContent(navController, viewModel)
}

@Composable
fun AddScreenContent(navController: NavHostController?, viewModel: AddNoteViewModel? = null) {
    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "Add note") }, navigationIcon = {
            IconButton(onClick = { navController?.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = null)
            }
        })
    }) {
        Column(modifier = Modifier.padding(it).fillMaxSize()) {
            var note by rememberSaveable {
                mutableStateOf("")
            }

            Spacer(modifier = Modifier.height(12.dp))
            AddNoteUI(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                note = note,
                onNoteChange = { enteredString ->
                    note = enteredString
                }
            ) {
                viewModel?.addNote(note)
            }
        }
    }
}

@Composable
fun AddNoteUI(
    modifier: Modifier,
    note: String,
    onNoteChange: (String) -> Unit,
    onAddNote: () -> Unit
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        TextField(modifier = Modifier.fillMaxWidth(), value = note, onValueChange = onNoteChange)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onAddNote) {
            Text(text = "Add")
        }
    }
}

@Preview
@Composable
fun AddNotePreview() {
    AddScreenContent(navController = null, viewModel = null)
}