package com.example.samplegdrive.ui.screen.restore

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samplegdrive.persistance.NoteRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class RestoreViewModel(private val noteRepository: NoteRepository) : ViewModel() {

    private val _state = mutableStateOf(RestoreScreenUIState())
    val state: State<RestoreScreenUIState> = _state

    private val _events = MutableSharedFlow<RestoreScreenEvents>()
    val events = _events.asSharedFlow()

    init {
        getBackupFileList()
    }

    private fun getBackupFileList() {
        _state.value = _state.value.copy(fetchState = RestoreScreenState.FetchingList)
        viewModelScope.launch {
            try {
                val files = noteRepository.getBackupFiles()
                _state.value = _state.value.copy(fileId = files.files.firstOrNull()?.id ?: "")
            } catch (throwable: Throwable) {
                _state.value = _state.value.copy(fileId = "")
            }
            _state.value = _state.value.copy(fetchState = RestoreScreenState.ListFetched)
        }
    }

    fun restoreFromDrive(fileID: String) {
        viewModelScope.launch {
            noteRepository.import(fileID)
            _events.emit(RestoreScreenEvents.CreateDbFromFile)
        }
    }


    fun delete(fileID: String) {
        viewModelScope.launch {
            noteRepository.deleteFile(fileID)
            getBackupFileList()
            _events.emit(RestoreScreenEvents.BackupDeleted)
        }
    }

    data class RestoreScreenUIState(
        var fetchState: RestoreScreenState = RestoreScreenState.FetchingList,
        var fileId: String = ""
    )

    sealed class RestoreScreenEvents {
        object CreateDbFromFile : RestoreScreenEvents()
        object BackupDeleted : RestoreScreenEvents()
    }

    sealed class RestoreScreenState {
        object FetchingList : RestoreScreenState()
        object ListFetched : RestoreScreenState()
    }
}