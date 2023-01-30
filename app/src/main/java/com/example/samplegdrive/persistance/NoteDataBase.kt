package com.example.samplegdrive.persistance

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

@Database(entities = [Note::class], version = 1, exportSchema = true)
abstract class NoteDataBase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        const val DATABASE_NAME = "app_db"
        fun newInstance(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            NoteDataBase::class.java,
            DATABASE_NAME
        ).build()
    }
}
