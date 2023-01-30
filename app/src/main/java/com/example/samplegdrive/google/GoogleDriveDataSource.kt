package com.example.samplegdrive.google

import com.google.api.client.http.ByteArrayContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.FileList
import kotlinx.coroutines.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class GoogleDriveDataSource(
    private val mDriveService: Drive,
    private val dispatcher: CoroutineDispatcher = Executors.newSingleThreadExecutor()
        .asCoroutineDispatcher(),
    private val appScope: CoroutineScope = CoroutineScope(SupervisorJob())
) {

    companion object {
        private const val FILE_MIME_TYPE = "application/x-sqlite3"
        private const val APP_DATA_FOLDER_SPACE = "appDataFolder"
    }

    suspend fun uploadFile(dbFile: File, fileName: String) {
        createFile(fileName, dbFile)
    }

    suspend fun readFile(
        file: File, fileId: String
    ) = withContext(dispatcher + appScope.coroutineContext) {
        mDriveService.files()[fileId].executeMediaAsInputStream()?.use {
            it.copyTo(file.outputStream())
        }
        null
    }

    suspend fun queryFiles(): FileList = withContext(dispatcher) {
        mDriveService.files().list().setSpaces(APP_DATA_FOLDER_SPACE).execute()
    }

    private suspend fun createFile(fileName: String, dbFile: File) =
        withContext(dispatcher + appScope.coroutineContext) {
            val metadata = getMetaData(fileName)
            metadata.parents = listOf(APP_DATA_FOLDER_SPACE)
            val bytes = dbFile.inputStream().readBytes()
            val fileContent = ByteArrayContent(FILE_MIME_TYPE, bytes)
            val file = mDriveService.files().create(metadata, fileContent).execute()
            queryFiles().files.forEach {
                if (file.id != it.id) deleteFile(it.id)
            }
        }

    suspend fun deleteFile(fileId: String) = withContext(dispatcher + appScope.coroutineContext) {
        mDriveService.files().delete(fileId).execute()
        fileId
    }

    private fun getMetaData(fileName: String): com.google.api.services.drive.model.File {
        return com.google.api.services.drive.model.File().setMimeType(FILE_MIME_TYPE)
            .setName(fileName)
    }

}