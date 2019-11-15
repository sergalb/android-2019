package ru.ifmo.rain.contacts

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class SearchTest {
    private val contacts = listOf(
        Contact("serg", "+7999888545"),
        Contact("sergalb", "+79991234551"),
        Contact("not used", "8234323"),
        Contact("pikachu", "111111111"),
        Contact("88005553535", "88005553535")
    )

    private val contactSearchUtility = ContactSearchUtility()

    @Before
    fun setUp() {
        contactSearchUtility.addContactsForSearch(contacts)
    }

    @Test
    fun successSearchByPartName() {
        //given: contacts

        //when
        val searchRes = contactSearchUtility.search("se")

        //then
        assertEquals(
            setOf(
                Contact("serg", "+7999888545"),
                Contact("sergalb", "+79991234551")
            ),
            searchRes.asSequence().toSet()
        )
    }


    @Test
    fun successSearchByPartPhone() {
        //given: contacts

        //when
        val searchRes = contactSearchUtility.search("+7999")

        //then
        assertEquals(
            setOf(
                Contact("serg", "+7999888545"),
                Contact("sergalb", "+79991234551")
            ),
            searchRes.asSequence().toSet()
        )
    }

    @Test
    fun successSearchByFullPhone() {
        //given: contacts

        val searchRes = contactSearchUtility.search("111111111")

        assertEquals(
            setOf(
                Contact("pikachu", "111111111")
            ),
            searchRes.asSequence().toSet()
        )
    }

    @Test
    fun successSearchByFullName() {

        val searchRes = contactSearchUtility.search("pikachu")

        assertEquals(
            setOf(
                Contact("pikachu", "111111111")
            ),
            searchRes.asSequence().toSet()
        )
    }

    @Test
    fun successSearchByEqualsPhoneAndName() {
        //given: contacts

        val searchRes = contactSearchUtility.search("88005553535")

        assertEquals(
            listOf(
                Contact("88005553535", "88005553535")
            ),
            searchRes
        )
    }

    @Test
    fun failedSearchByName() {
        //given: contacts

        //when
        val searchRes = contactSearchUtility.search("sergnealb")


        //then
        assertEquals(emptyList<Contact>(), searchRes)
    }

    @Test
    fun failedSearchByPhone() {
        //given: contacts

        //when
        val searchRes = contactSearchUtility.search("880056")

        //then
        assertEquals(emptyList<Contact>(), searchRes)
    }


}

