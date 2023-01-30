package com.example.samplegdrive.google

import android.content.Context
import com.example.samplegdrive.R
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes

class DriveApi {

    companion object{
        private var instance: Drive? = null
        fun getInstance(context: Context, signInAccount: GoogleSignInAccount): Drive {

            if (instance != null)
                return instance!!

            val scopes: MutableList<String> = ArrayList()
            scopes.add(DriveScopes.DRIVE_APPDATA)

            val credential = GoogleAccountCredential.usingOAuth2(context, scopes)
            credential.selectedAccount = signInAccount.account
            val builder = Drive.Builder(
                NetHttpTransport(),
                GsonFactory(),
                credential
            )
            val appName: String = context.getString(R.string.app_name)
            val driveApi = builder
                .setApplicationName(appName)
                .build()
            instance = driveApi
            return instance!!
        }
    }
}
