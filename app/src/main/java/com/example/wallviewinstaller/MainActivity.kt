package com.example.wallviewinstaller

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.github.javiersantos.appupdater.AppUpdater
import com.github.javiersantos.appupdater.enums.UpdateFrom
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var button: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.button)

        button.setOnClickListener{
            handleInAppUpdate()
        }
    }


    private fun handleInAppUpdate() {
        var allow = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            allow = this.packageManager.canRequestPackageInstalls()
        } else {
            try {
                allow = Settings.Secure.getInt(
                    contentResolver,
                    Settings.Secure.INSTALL_NON_MARKET_APPS
                ) == 1
            } catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
            }
        }

        if (!allow) {
            dialogYesOrNo(
                this,
                "Unknown Source Permission",
                "Allow permission to install WallView app."
            ) { _, _ ->
                // do whatever you need to do when user presses "Yes"

                Toast.makeText(
                    applicationContext,
                    "Please re-launch the WallView application after giving unknown source permission.",
                    Toast.LENGTH_LONG
                ).show()

                Handler(Looper.getMainLooper()).postDelayed({
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startActivity(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES))

                    } else {
                        startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
                    }
                    finish()
                    exitProcess(0)
                }, 800)

            }
        }

        if (allow) {
            Log.d(Companion.TAG, "canRequestPackageInstalls: ${packageManager.canRequestPackageInstalls()}")
            AppUpdater(this)
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON("https://dev.api.medias.pictplay.com/api/uploads/media/download?key=wv/live/tv/eec3f160-a92b-11ed-917f-e588b1892ac9")
                .setDisplay(com.github.javiersantos.appupdater.enums.Display.DIALOG)
                .setUpdateAvailableListener {
                }
                .showAppUpdated(true)
                .start()
            Log.d(
                Companion.TAG,
                "update json url:  https://dev.api.medias.pictplay.com/api/uploads/media/download?key=wv/live/tv/eec3f160-a92b-11ed-917f-e588b1892ac9"
            )
        }
    }

    private fun dialogYesOrNo(
        activity: Activity,
        title: String,
        message: String,
        listener: DialogInterface.OnClickListener
    ) {
        val builder = AlertDialog.Builder(activity)
        builder.setPositiveButton("Yes") { dialog, id ->
            dialog.dismiss()
            listener.onClick(dialog, id)
        }
        builder.setNegativeButton("No", null)
        val alert = builder.create()
        alert.setTitle(title)
        alert.setMessage(message)
        alert.show()
    }

    companion object {
        private const val TAG: String = "MainActivity"
    }

}