package com.example.samplegdrive

import android.app.Application
import com.example.samplegdrive.di.ServiceLocator
import com.example.samplegdrive.persistance.NoteRepository

class MyApp : Application() {

    val noteRepository: NoteRepository
        get() = ServiceLocator.provideNoteRepository(this)
}