package ru.ifmo.rain.contacts

import android.Manifest
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager


class MainActivity : AppCompatActivity() {
    private val PERMISSIONS_REQUEST_READ_CONTACTS = 478
    lateinit var contactAdapter: ContactAdapter
    private val contactSearchUtility = ContactSearchUtility()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissionAndSetRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater

        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.app_bar, menu)
        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search_bar).actionView as SearchView
        searchView.setSearchableInfo(
            searchManager.getSearchableInfo(componentName)
        )
        searchView.setOnQueryTextListener(searchListener)
        super.onCreateOptionsMenu(menu)
        return true
    }

    private fun setRecyclerView() {
        val viewManager = LinearLayoutManager(this)
        val contacts = fetchAllContacts()
        contactAdapter = ContactAdapter(
            contacts
        ) { contact ->
            val uri = Uri.fromParts("tel", contact.phoneNumber, null)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        contactSearchUtility.addContactsForSearch(contacts)

        val contactRecyclerView = findViewById<EmptyRecyclerView>(R.id.contact_recycler_view)
        contactRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = contactAdapter
        }
        contactRecyclerView.setEmptyView(findViewById(R.id.empty_view))
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

    private val searchListener: SearchView.OnQueryTextListener =
        object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                search(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                search(newText)
                return true
            }
        }

    private fun search(query: String?) {
        val contacts: List<Contact> = contactSearchUtility.search(query)
        contactAdapter.contacts = contacts
        contactAdapter.notifyDataSetChanged()
    }
}


