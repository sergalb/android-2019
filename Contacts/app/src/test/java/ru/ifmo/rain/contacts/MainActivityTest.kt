package ru.ifmo.rain.contacts

import android.app.Activity
import android.content.ContentResolver
import android.provider.ContactsContract
import androidx.recyclerview.widget.RecyclerView
import androidx.test.rule.GrantPermissionRule
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.fakes.RoboCursor


@RunWith(RobolectricTestRunner::class)
class ContactsHelperTest {

    private lateinit var activity: Activity

    private lateinit var contactsRoboCursor: RoboCursor
    private lateinit var contentResolver: ContentResolver

    private val CONTACTS_COLUMNS = listOf(
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    )

    private lateinit var contacts: List<Contact>

    private val CONTACT_SERG = arrayOf("serg", "+7999888545")
    private val CONTACT_SERGALB = arrayOf("sergalb", "+79991234551")
    private val CONTACT_NOT_USED = arrayOf("not used", "8234323")
    private val CONTACT_PIKACHU = arrayOf("pikachu", "111111111")
    private val CONTACT_88005553535 = arrayOf("88005553535", "88005553535")
    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.READ_CONTACTS)
    @Before
    fun setUp() {
        contactsRoboCursor = RoboCursor()

        contactsRoboCursor.setColumnNames(CONTACTS_COLUMNS)
        contactsRoboCursor.setResults(
            arrayOf(
                CONTACT_SERG,
                CONTACT_SERGALB,
                CONTACT_NOT_USED,
                CONTACT_PIKACHU,
                CONTACT_88005553535
            )
        )
        val activityContoler = Robolectric.buildActivity(MainActivity::class.java)
        contentResolver = mock {
            on {
                query(
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull()
                )
            } doReturn contactsRoboCursor
        }

        contacts = listOf(
            Contact("serg", "+7999888545"),
            Contact("sergalb", "+79991234551"),
            Contact("not used", "8234323"),
            Contact("pikachu", "111111111"),
            Contact("88005553535", "88005553535")
        ).sortedWith(compareBy({ it.name }, { it.phoneNumber }))

        activity = activityContoler.get()
        val shadowContentResolver= shadowOf(activity.contentResolver)
        shadowContentResolver.setCursor(contactsRoboCursor)
        activity = activityContoler.create().get()

    }

    @Test
    fun validateContactsDisplayedOnSetupActivity() {
        //when
        val recyclerView = activity.findViewById<EmptyRecyclerView>(R.id.contact_recycler_view)

        assertNotNull("recyclerView is not seted", recyclerView)

    }

    @Test
    fun validateContactsContentOnSetupActivity() {
        //when
        val recyclerView = activity.findViewById<RecyclerView>(R.id.contact_recycler_view)

        recyclerView.measure(0, 0)
        recyclerView.layout(0, 0, 100, 10000)

        assertNotNull(
            "recyclerView has not view holders",
            recyclerView.findViewHolderForAdapterPosition(0)
        )

        for (i in contacts.indices) {
            val holder = recyclerView.findViewHolderForAdapterPosition(i) as ContactViewHolder
            assertEquals(contacts[i].name, holder.name.text)
            assertEquals(contacts[i].phoneNumber, holder.number.text)
        }

        /*assertNotNull("recyclerView has not adapter", recyclerView.adapter)

        val contactsAdapter= recyclerView.adapter as ContactAdapter

        assertEquals(contacts, contactsAdapter.contacts)*/

    }


}