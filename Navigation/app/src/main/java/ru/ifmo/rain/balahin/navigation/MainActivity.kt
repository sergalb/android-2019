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

class MainActivity : AppCompatActivity() {

    private var currentFragmentTag: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        if (savedInstanceState == null) {
            navigateToFragment(R.id.navigation_home.toString())
        } else {
            currentFragmentTag = savedInstanceState.getString(EXTRA_CURRENT_FRAGMENT_TAG)
        }

        val orientation = resources.configuration.orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            main_navigation_view?.setNavigationItemSelectedListener {
                navigateToFragment(it.itemId.toString())
                true
            }
        } else {
            main_bottom_navigation_view?.setOnNavigationItemSelectedListener {
                navigateToFragment(it.itemId.toString())
                true
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EXTRA_CURRENT_FRAGMENT_TAG, currentFragmentTag)
    }

    private fun navigateToFragment(tag: String) {
        val fragment = supportFragmentManager.findFragmentByTag(tag)
            ?: TabFragment()

        val transaction = supportFragmentManager.beginTransaction()

        val currentFragment = supportFragmentManager.findFragmentByTag(currentFragmentTag)
        if (currentFragment != null) {
            transaction.hide(currentFragment)
        }

        if (!fragment.isAdded) {
            transaction.add(R.id.main_fragment_container, fragment, tag)
        } else {
            transaction.show(fragment)
        }

        currentFragmentTag = fragment.tag
        transaction.commit()
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentByTag(currentFragmentTag)
            ?: error("can't find current fragment")

        if (currentFragment.childFragmentManager.backStackEntryCount == 0) {
            val fragment = supportFragmentManager.fragments.last()
            if (supportFragmentManager.fragments.size > 1 ){
                main_bottom_navigation_view?.selectedItemId = fragment.id
                navigateToFragment(fragment.tag!!)
            } else {
                finish()
            }
        } else {
            currentFragment.childFragmentManager.popBackStack()
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
