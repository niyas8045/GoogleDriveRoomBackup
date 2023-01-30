package com.example.samplegdrive.di

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.samplegdrive.persistance.NoteRepository
import com.example.samplegdrive.ui.screen.add_note.AddNoteViewModel
import com.example.samplegdrive.ui.screen.notes.NoteViewModel
import com.example.samplegdrive.ui.screen.restore.RestoreViewModel

/**
 * Factory for all ViewModels.
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val noteRepository: NoteRepository,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ) = with(modelClass) {
        when {
            isAssignableFrom(NoteViewModel::class.java) ->
                NoteViewModel(noteRepository)
            isAssignableFrom(AddNoteViewModel::class.java) ->
                AddNoteViewModel(noteRepository)
            isAssignableFrom(RestoreViewModel::class.java) ->
                RestoreViewModel(noteRepository)
            else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    } as T
}
