package ru.ifmo.rain.contacts

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ContactSearchUtility {
    private val nameBor = SearchBor()
    private val phoneBor = SearchBor()

    fun addContactsForSearch(contacts: List<Contact>) {
        nameBor.create(contacts) { it.name }
        phoneBor.create(contacts) { it.phoneNumber }
    }

    fun search(query: String?): List<Contact> =
        if (query != null) {
            val names = nameBor.findAllByPrefix(query)
            val phone = phoneBor.findAllByPrefix(query)
            (names.asSequence() + phone.asSequence()).distinct()
                .sortedWith(compareBy({ it.name }, { it.phoneNumber })).toList()
        } else {
            emptyList()
        }
}

class SearchBor {
    private inner class Node {
        var isTerm: Boolean = false
        var edges: MutableMap<Char, Node> = HashMap()
        var terminalContact: Contact? = null
    }

    private var root: Node = Node()

    fun create(contacts: List<Contact>, selector: (Contact) -> String) {
        for (contact in contacts) {
            add(contact, selector)
        }
    }

    fun add(contact: Contact, selector: (Contact) -> String) {
        var curNode = root
        for (symbol in selector(contact).toLowerCase(Locale.getDefault())) {
            if (!curNode.edges.containsKey(symbol)) {
                curNode.edges[symbol] = Node()
            }
            curNode = curNode.edges[symbol]!!
        }
        curNode.isTerm = true
        curNode.terminalContact = contact
    }

    fun findAllByPrefix(prefix: String): List<Contact> {
        var curNode = root
        for (symbol in prefix.toLowerCase(Locale.getDefault())) {
            if (!curNode.edges.containsKey(symbol)) {
                return emptyList()
            }
            curNode = curNode.edges[symbol]!!
        }
        return dfs(curNode)
    }

    private fun dfs(node: Node, resultPrefix: MutableList<Contact> = ArrayList()): List<Contact> {
        if (node.isTerm) {
            resultPrefix.add(node.terminalContact!!)
        }
        for (edge in node.edges.values) {
            dfs(edge, resultPrefix)
        }
        return resultPrefix
    }
}