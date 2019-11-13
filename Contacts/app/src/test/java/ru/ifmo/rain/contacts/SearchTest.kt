package ru.ifmo.rain.contacts

import android.app.Activity
import android.content.ContentProviderOperation
import android.os.Build
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.StructuredName
import android.provider.ContactsContract.Data
import androidx.test.rule.GrantPermissionRule
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
class SearchTest {
    val sampleContact = Contact("Test contact name", "+122344")

    var activity: Activity? = null

    /*@get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.WRITE_CONTACTS)

    */

    @Test
    fun kiss(){
        assertEquals(4, 2+2)
    }
/*

    @Before
    fun addTestedContacts(){
        activity = Robolectric.buildActivity(MainActivity::class.java).create().get()
        addContact(sampleContact)
        println(activity.fetchAllContacts().isNotEmpty())
    }

    @After
    fun deleteTestedContacts(){
        deleteContact(sampleContact.name)
    }



    private fun getRawContactIdByName(name: String): Long {
        val contentResolver = activity.contentResolver

        // Query raw_contacts table by display name field ( given_name family_name ) to get raw contact id.

        // Create query column array.
        val queryColumnArr = arrayOf(ContactsContract.RawContacts._ID)

        // Create where condition clause.
        val whereClause =
            ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY + " = '" + name + "'"

        // Query raw contact id through RawContacts uri.
        val rawContactUri = ContactsContract.RawContacts.CONTENT_URI

        // Return the query cursor.
        val cursor = contentResolver.query(rawContactUri, queryColumnArr, whereClause, null, null)

        var rawContactId: Long = -1

        if (cursor != null) {
            // Get contact count that has same display name, generally it should be one.
            val queryResultCount = cursor.count
            // This check is used to avoid cursor index out of bounds exception. android.database.CursorIndexOutOfBoundsException
            if (queryResultCount > 0) {
                // Move to the first row in the result cursor.
                cursor.moveToFirst()
                // Get raw_contact_id.
                rawContactId =
                    cursor.getLong(cursor.getColumnIndex(ContactsContract.RawContacts._ID))
            }
        }

        return rawContactId
    }

    private fun deleteContact(name: String){
        // First select raw contact id by given name and family name.
        val rawContactId = getRawContactIdByName(name)

        val contentResolver = activity.contentResolver

        //                    delete data table related data ****************************************
        // Data table content process uri.
        val dataContentUri = Data.CONTENT_URI

        // Create data table where clause.
        val dataWhereClauseBuf = StringBuffer()
        dataWhereClauseBuf.append(Data.RAW_CONTACT_ID)
        dataWhereClauseBuf.append(" = ")
        dataWhereClauseBuf.append(rawContactId)

        // Delete all this contact related data in data table.
        contentResolver.delete(dataContentUri, dataWhereClauseBuf.toString(), null)


        //         delete raw_contacts table related data ***************************************
        // raw_contacts table content process uri.
        val rawContactUri = ContactsContract.RawContacts.CONTENT_URI

        // Create raw_contacts table where clause.
        val rawContactWhereClause = StringBuffer()
        rawContactWhereClause.append(ContactsContract.RawContacts._ID)
        rawContactWhereClause.append(" = ")
        rawContactWhereClause.append(rawContactId)

        // Delete raw_contacts table related data.
        contentResolver.delete(rawContactUri, rawContactWhereClause.toString(), null)

        //          delete contacts table related data ***************************************
        // contacts table content process uri.
        val contactUri = ContactsContract.Contacts.CONTENT_URI

        // Create contacts table where clause.
        val contactWhereClause = StringBuffer()
        contactWhereClause.append(ContactsContract.Contacts._ID)
        contactWhereClause.append(" = ")
        contactWhereClause.append(rawContactId)

        // Delete raw_contacts table related data.
        contentResolver.delete(contactUri, contactWhereClause.toString(), null)
    }

    private fun addContact(contact: Contact) {
        val operationList = ArrayList<ContentProviderOperation>()
        operationList.add(
            ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build()
        )


        operationList.add(
            ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                .withValue(StructuredName.DISPLAY_NAME, contact.name)
                .build()
        )

        operationList.add(
            ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                .withValue(
                    Data.MIMETYPE,
                    Phone.CONTENT_ITEM_TYPE
                )
                .withValue(Phone.NUMBER, contact.phoneNumber)
                .withValue(Phone.TYPE, Phone.TYPE_HOME)
                .build()
        )

        try {
            activity.contentResolver.applyBatch(ContactsContract.AUTHORITY, operationList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun successSearchByPartName(){
        //given: activity

        //when
        val searchRes = activity.fetchContactsByPhoneOrName(sampleContact.name.substring(1, 5))

        //then
        assertEquals(listOf(sampleContact), searchRes)
    }

    @Test
    fun successSearchByPartPhone() {
        //given: activity

        //when
        val searchRes = activity.fetchContactsByPhoneOrName(sampleContact.phoneNumber.substring(2, 6))

        //then
        assertEquals(listOf(sampleContact), searchRes)
    }

    @Test
    fun successSearchByFullPhone() {
        //given: activity

        //when
        val searchRes = activity.fetchContactsByPhoneOrName(sampleContact.phoneNumber)

        //then
        assertEquals(listOf(sampleContact), searchRes)
    }
    @Test
    fun successSearchByFullName() {
        //given: activity

        //when
        val searchRes = activity.fetchContactsByPhoneOrName(sampleContact.name)

        //then
        assertEquals(listOf(sampleContact), searchRes)
    }

    @Test
    fun successSearchByPhoneInDifferentFormat() {
        //given: activity

        val phone = "${sampleContact.phoneNumber.subSequence(0,2)} " +
                "${sampleContact.phoneNumber.substring(2,4)}-" +
                sampleContact.phoneNumber.substring(4)

        //when
        val searchRes = activity.fetchContactsByPhoneOrName(phone)

        //then
        assertEquals(listOf(sampleContact), searchRes)
    }

    @Test
    fun failedSearchByName(){
        //given: activity

        //when
        val searchRes = activity.fetchContactsByPhoneOrName("wrong")

        //then
        assertEquals(emptyList<Contact>(), searchRes)
    }

    @Test
    fun failedSearchByPhone(){
        //given: activity

        //when
        val searchRes = activity.fetchContactsByPhoneOrName("+1321")

        //then
        assertEquals(emptyList<Contact>(), searchRes)
    }

*/
}

