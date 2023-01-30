package com.example.samplegdrive.ui.screen.add_note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samplegdrive.persistance.Note
import com.example.samplegdrive.persistance.NoteRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AddNoteViewModel(private val noteRepository: NoteRepository) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<AddNoteEvents>()
    val eventFlow = _eventFlow.asSharedFlow()
    fun addNote(note: String) {
        viewModelScope.launch {
            noteRepository.insertNote(Note(content = note))
            _eventFlow.emit(AddNoteEvents.Added)
        }
    }


    sealed class AddNoteEvents {
        object Added : AddNoteEvents()
    }
}