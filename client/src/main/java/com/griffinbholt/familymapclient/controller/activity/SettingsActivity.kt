package com.griffinbholt.familymapclient.controller.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.griffinbholt.familymapclient.controller.fragment.SettingsFragment

/**
 * A [SingleFragmentActivity] subclass that hosts the [SettingsFragment] to display the settings to the user
 * Use the [SettingsActivity.newIntent] method to create an [Intent] for this activity.
 */
class SettingsActivity : SingleFragmentActivity() {

	override fun createFragment(): Fragment {
		return SettingsFragment.newInstance()
	}

	companion object {
		/**
		 * Generates an [Intent] for a new SettingsActivity to be started
		 *
		 * @param packageContext The [Context] of the new SettingsActivity
		 * @return An [Intent] for the new SettingsActivity
		 */
		fun newIntent(packageContext: Context?): Intent = Intent(packageContext, SettingsActivity::class.java)
	}
}