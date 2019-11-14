package ru.ifmo.rain.contacts

import android.app.Activity
import android.content.ContentResolver
import android.provider.ContactsContract
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.rule.GrantPermissionRule
import com.nhaarman.mockitokotlin2.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController
import org.robolectric.fakes.RoboCursor


@RunWith(RobolectricTestRunner::class)
class MainActivityTest {

    @get:Rule
    val permissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.READ_CONTACTS)

    companion object {
        lateinit var activity: Activity
        lateinit var activityController: ActivityController<MainActivity>

        val contactsRoboCursor: RoboCursor = RoboCursor()
        private lateinit var contentResolver: ContentResolver

        private val CONTACTS_COLUMNS = listOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        lateinit var contacts: List<Contact>

        private val CONTACT_SERG = arrayOf("serg", "+7999888545")
        private val CONTACT_SERGALB = arrayOf("sergalb", "+79991234551")
        private val CONTACT_NOT_USED = arrayOf("not used", "8234323")
        private val CONTACT_PIKACHU = arrayOf("pikachu", "111111111")
        private val CONTACT_88005553535 = arrayOf("88005553535", "88005553535")

        @BeforeClass
        @JvmStatic
        fun setUpTestData() {
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
        }
    }

    @Before
    fun setupActivity() {
        activityController = Robolectric.buildActivity(MainActivity::class.java)
        activity = activityController.get()
        val shadowContentResolver = shadowOf(activity.contentResolver)
        shadowContentResolver.setCursor(contactsRoboCursor)
    }

    @Test
    fun validateContactsContentOnSetupActivity() {
        //when
        activity = activityController.create().get()

        //then
        checkRecyclerViewContent(contacts)

    }

    @Test
    fun contactsSetedOnQuery() {
        //when
        provideSearchQuery("sergalb")

        //then
        checkRecyclerViewContent(listOf(Contact("sergalb", "+79991234551")))
    }

    @Test
    fun emptyViewSeOnWrongQuery() {
        provideSearchQuery("wrong query")

        val emptyRecyclerView = activity.findViewById<TextView>(R.id.empty_view)
        assertNotNull("empty not found", emptyRecyclerView)

        assertEquals(View.VISIBLE, emptyRecyclerView.visibility)
        assertEquals(
            activity.resources.getString(R.string.no_contacts_found),
            emptyRecyclerView.text
        )
    }


    private fun provideSearchQuery(query: String) {
        activity = activityController.create().visible().get()
        val shadowActivity = shadowOf(activity)
        val menu = shadowActivity.optionsMenu
        assertNotNull("menu is null", menu)
        val searchItem = menu.findItem(R.id.search_bar)
        assertNotNull("search item is null", searchItem)
        assertTrue(searchItem.actionView is SearchView)
        val searchView = searchItem.actionView as SearchView

        //when
        searchView.setQuery(query, true)
    }

    private fun checkRecyclerViewContent(expected: List<Contact>) {
        val recyclerView = activity.findViewById<RecyclerView>(R.id.contact_recycler_view)

        assertNotNull("recyclerView is not seted", recyclerView)

        recyclerView.measure(0, 0)
        recyclerView.layout(0, 0, 100, 10000)
        assertEquals("unexpected contacts counts", recyclerView.adapter!!.itemCount, expected.size)
        for (i in expected.indices) {
            val holder = recyclerView.findViewHolderForAdapterPosition(i) as ContactViewHolder
            assertEquals(expected[i].name, holder.name.text)
            assertEquals(expected[i].phoneNumber, holder.number.text)
        }
    }


}