package com.example.samplegdrive.ui.screen.notes

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samplegdrive.di.ServiceLocator
import com.example.samplegdrive.persistance.Note
import com.example.samplegdrive.persistance.NoteDataBase
import com.example.samplegdrive.persistance.NoteRepository
import com.example.samplegdrive.ui.screen.auth.AuthViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import java.io.File

class NoteViewModel(private val noteRepository: NoteRepository) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<NoteEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _uiState = mutableStateOf(MainScreenState())
    val uiState: State<MainScreenState> = _uiState

    var hasBeenTriggered = false

    init {
        getAllNotes()
    }

    fun getAllNotes() = noteRepository.getAllNotes().transform { note ->
        if (!hasBeenTriggered) {
            hasBeenTriggered = true
            _uiState.value = _uiState.value.copy(screenState = NoteScreenState.Loaded)
        }
        emit(note)
    }

    fun uploadDB() {
        viewModelScope.launch {
            _eventFlow.emit(NoteEvents.FileUploadStarted)
            try {
                noteRepository.export()
                _eventFlow.emit(NoteEvents.FileUploaded)
                Log.i(TAG, "Upload success")
            } catch (throwable: Throwable) {
                _eventFlow.emit(NoteEvents.UploadFailed)
                Log.e(TAG, "error upload file", throwable)
            }
        }
    }

    fun addDummyData() {
        viewModelScope.launch {
            noteRepository.addDummyData()
        }
    }

    fun clearDatabase() {
        viewModelScope.launch {
            noteRepository.clearDB()
            _uiState.value = _uiState.value.copy(
                screenState = if (_uiState.value.screenState == NoteScreenState.Loaded)
                    NoteScreenState.ReLoaded else NoteScreenState.Loaded
            )
        }
    }

    data class MainScreenState(
        val screenState: NoteScreenState = NoteScreenState.Loading,
        var noteList: List<Note> = listOf()
    )

    sealed class NoteScreenState {
        object Loading : NoteScreenState()
        object Loaded : NoteScreenState()
        object ReLoaded : NoteScreenState()
    }

    sealed class NoteEvents {
        object FileUploadStarted : NoteEvents()
        object FileUploaded : NoteEvents()
        object UploadFailed : NoteEvents()
    }

    companion object {
        val TAG = NoteViewModel::class.simpleName
    }
}