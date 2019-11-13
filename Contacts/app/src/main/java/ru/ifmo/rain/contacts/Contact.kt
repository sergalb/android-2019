package ru.ifmo.rain.contacts

import android.content.Context
import android.provider.ContactsContract

data class Contact(val name: String, val phoneNumber: String)

fun Context.fetchAllContacts(): List<Contact> = fetchContacts()


//can't test without context
fun Context.fetchContactsByPhoneOrName(searchText: String): List<Contact> {
    val mProjection = arrayOf(
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.LOOKUP_KEY,
        ContactsContract.Contacts.HAS_PHONE_NUMBER,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    )

    val selection = "(${ContactsContract.Contacts.DISPLAY_NAME} LIKE ?)" +
            " OR (${ContactsContract.CommonDataKinds.Phone.NUMBER} LIKE ?)" +
            " OR (${ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER} LIKE ?)"
    val selectionArgs = arrayOf("%$searchText%", "%$searchText%", "%$searchText%")
    ContactsContract.CommonDataKinds.Phone.NUMBER
    return fetchContacts(mProjection, selection, selectionArgs)

}

private fun Context.fetchContacts(projection: Array<String>? = null, selection: String? = null, selectionArgs: Array<String>? = null): List<Contact> {

    contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, selectionArgs, null)
        .use { cursor ->
            if (cursor == null) return emptyList()
            val builder = ArrayList<Contact>()
            while (cursor.moveToNext()) {
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)) ?: "N/A"
                val phoneNumber =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)) ?: "N/A"

                builder.add(Contact(name, phoneNumber))
            }
            return builder.sortedWith(compareBy({it.name}, {it.phoneNumber}))
        }
}