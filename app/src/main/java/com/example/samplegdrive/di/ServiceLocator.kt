package com.example.samplegdrive.di

import android.content.Context
import com.example.samplegdrive.google.DriveApi
import com.example.samplegdrive.google.GoogleDriveDataSource
import com.example.samplegdrive.persistance.NoteRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn

object ServiceLocator {

    @Volatile
    private var noteRepository: NoteRepository? = null
    private val lock = Any()

    fun provideNoteRepository(context: Context): NoteRepository {
        synchronized(lock) {
            return noteRepository ?: createNoteRepository(context)
        }
    }

    private fun createNoteRepository(context: Context): NoteRepository {
        val lastUser = GoogleSignIn.getLastSignedInAccount(context)
        val driveApi = DriveApi.getInstance(context.applicationContext, lastUser!!)
        val googleDataSource = GoogleDriveDataSource(driveApi)
        val newRepo = NoteRepository(context.applicationContext, googleDataSource)
        noteRepository = newRepo
        return newRepo
    }
}