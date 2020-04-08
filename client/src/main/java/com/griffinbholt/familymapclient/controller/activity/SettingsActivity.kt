package com.griffinbholt.familymapclient.controller.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.griffinbholt.familymapclient.controller.fragment.SettingsFragment

class SettingsActivity : SingleFragmentActivity() {
	override fun createFragment(): Fragment {
		return SettingsFragment.newInstance()
	}

	companion object {
		fun newIntent(packageContext: Context?): Intent {
			return Intent(packageContext, SettingsActivity::class.java)
		}
	}
}