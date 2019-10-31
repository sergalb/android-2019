package ru.ifmo.rain.contacts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import android.R.id
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.telephony.PhoneNumberUtils


class MainActivity : AppCompatActivity() {
    private val PERMISSIONS_REQUEST_READ_CONTACTS = 478

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissionAndSetRecyclerView()
    }

    private fun setRecyclerView() {
        val viewManager = LinearLayoutManager(this)
        val contactAdapter = ContactAdapter(
            fetchAllContacts()
        ) { contact ->
            val uri = Uri.fromParts("tel", contact.phoneNumber, null)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        contact_recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = contactAdapter
        }
    }

    private fun checkPermissionAndSetRecyclerView() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_CONTACTS
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(this, getString(R.string.explanation_permission), Toast.LENGTH_LONG)
                    .show()
            }

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSIONS_REQUEST_READ_CONTACTS
            )

        } else {
            setRecyclerView()
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    setRecyclerView()
                } else {
                    Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_LONG)
                        .show()

                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

}
