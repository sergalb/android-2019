package ru.ifmo.rain.balahin.navigation

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.navigation.R
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

const val EXTRA_CURRENT_FRAGMENT_TAG = "current_fragment_tag"
const val EXTRA_SEQUENCE = "sequence"

class MainActivity : AppCompatActivity() {

    private var currentFragmentTag: String? = null
    private var fragmentState: Deque<Int> = ArrayDeque()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        if (savedInstanceState == null) {
            navigateToFragment(R.id.navigation_home)
        } else {
            currentFragmentTag = savedInstanceState.getString(EXTRA_CURRENT_FRAGMENT_TAG)
            fragmentState = ArrayDeque(savedInstanceState.getIntegerArrayList(EXTRA_SEQUENCE)!!)
        }

        val orientation = resources.configuration.orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            main_navigation_view?.setNavigationItemSelectedListener {
                navigateToFragment(it.itemId)
                true
            }
        } else {
            main_bottom_navigation_view?.setOnNavigationItemSelectedListener {
                navigateToFragment(it.itemId)
                true
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EXTRA_CURRENT_FRAGMENT_TAG, currentFragmentTag)
        outState.putIntegerArrayList(EXTRA_SEQUENCE, ArrayList(fragmentState))
    }

    private fun navigateToFragment(itemId: Int) {
        val fragment = supportFragmentManager.findFragmentByTag("$itemId")
            ?: TabFragment()

        val transaction = supportFragmentManager.beginTransaction()

        val currentFragment = supportFragmentManager.findFragmentByTag(currentFragmentTag)
        if (currentFragment != null) {
            transaction.hide(currentFragment)
        }

        if (!fragment.isAdded) {
            transaction.add(R.id.main_fragment_container, fragment, "$itemId")
            fragmentState.addLast(itemId)
        } else {
            transaction.show(fragment)
        }

        currentFragmentTag = fragment.tag
        transaction.commit()
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentByTag(currentFragmentTag)
            ?: error("can't find current fragment")
        if (fragment.childFragmentManager.backStackEntryCount == 0) {
            if (fragmentState.isNotEmpty()) {
                val last = fragmentState.pop()
                main_bottom_navigation_view?.selectedItemId = last
                navigateToFragment(last)
            } else {
                finish()
            }
        } else {
            fragment.childFragmentManager.popBackStack()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
