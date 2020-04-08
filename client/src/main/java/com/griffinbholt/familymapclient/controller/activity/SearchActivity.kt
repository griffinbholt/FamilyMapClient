package com.griffinbholt.familymapclient.controller.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.griffinbholt.familymapclient.controller.fragment.SearchFragment

/**
 * A [SingleFragmentActivity] subclass that hosts the [SearchFragment] to display the search menu to the user
 * Use the [SearchActivity.newIntent] method to create an [Intent] for this activity.
 */
class SearchActivity : SingleFragmentActivity() {

	override fun createFragment(): Fragment {
		return SearchFragment.newInstance()
	}

	companion object {
		/**
		 * Generates an [Intent] for a new SearchActivity to be started
		 *
		 * @param packageContext The [Context] of the new SearchActivity
		 * @return An [Intent] for the new SearchActivity
		 */
		fun newIntent(packageContext: Context?): Intent = Intent(packageContext, SearchActivity::class.java)
	}
}
