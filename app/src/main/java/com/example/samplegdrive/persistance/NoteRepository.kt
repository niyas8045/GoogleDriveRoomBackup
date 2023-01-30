package com.example.samplegdrive.persistance

import android.content.Context
import com.example.samplegdrive.google.GoogleDriveDataSource
import com.google.api.services.drive.model.FileList
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*

const val GOOGLE_DRIVE_DB_LOCATION = "sample_drive_db"
private const val ALPHABET = "abcdefghijklmnopqrstuvwxyz"

class NoteRepository(
    context: Context, private val googleDriveDataSource: GoogleDriveDataSource
) {

    private var appContext: Context = context
    private val mutex = Mutex()
    private var db: NoteDataBase? = null

    suspend fun insertNote(note: Note) {
        mutex.withLock {
            db().noteDao().insertNote(note)
        }
    }

    fun getAllNotes() = db().noteDao().getAllNotes()

    suspend fun export() {
        mutex.withLock {
            db?.close()
            db = null

            googleDriveDataSource.uploadFile(
                appContext.getDatabasePath(NoteDataBase.DATABASE_NAME), GOOGLE_DRIVE_DB_LOCATION
            )
        }
    }

    suspend fun import(fileId: String) {
        mutex.withLock {
            db?.close()
            db = null

            val fileToPopulate = appContext.getDatabasePath(NoteDataBase.DATABASE_NAME)
            googleDriveDataSource.readFile(
                fileToPopulate, fileId
            )
        }
    }

    private fun db(): NoteDataBase {
        if (db == null) {
            db = NoteDataBase.newInstance(appContext)
        }

        return db!!
    }

    suspend fun getBackupFiles(): FileList {
        return googleDriveDataSource.queryFiles()
    }

    suspend fun deleteFile(id: String) {
        googleDriveDataSource.deleteFile(id)
    }

    suspend fun addDummyData() {
        mutex.withLock {
            repeat(100) {
                db().noteDao().insertNote(Note(content = getRandomWord()))
            }
        }
    }

    private fun getRandomWord(): String {
        val random = Random()
        val sb = StringBuilder()
        for (i in 0..4) {
            val index: Int = random.nextInt(ALPHABET.length)
            sb.append(ALPHABET[index])
        }
        return sb.toString()
    }

    suspend fun clearDB() {
        mutex.withLock {
            db?.close()
            appContext.getDatabasePath(NoteDataBase.DATABASE_NAME).delete()
            db = null
        }
    }

}