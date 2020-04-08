package com.griffinbholt.familymapclient.controller.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.griffinbholt.familymapclient.R
import com.griffinbholt.familymapclient.controller.fragment.LoginFragment
import com.griffinbholt.familymapclient.controller.fragment.MapFragment
import com.griffinbholt.familymapclient.model.data.DataCache
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.FontAwesomeModule

/**
 * A [AppCompatActivity] subclass that hosts either a [LoginFragment] or a [MapFragment]. It serves as the parent
 * activity for all other activities in the application.
 * Use the [MainActivity.newIntent] method to create an [Intent] for this activity.
 */
class MainActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_fragment_container)
		Iconify.with(FontAwesomeModule())
	}

	override fun onStart() {
		super.onStart()

		val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
		if (fragment == null || notLoggedIn()) {
			loadFragment()
		}
	}

	private fun loadFragment() {
		val fragment: Fragment = getAppropriateFragment()
		supportFragmentManager
				.beginTransaction()
				.replace(R.id.fragment_container, fragment)
				.commit()
	}

	private fun getAppropriateFragment(): Fragment {
		return if (notLoggedIn()) {
			LoginFragment.newInstance()
		} else {
			MapFragment.newInstance(null, true)
		}
	}

	private fun notLoggedIn(): Boolean {
		return (DataCache.authToken == null)
	}

	companion object {
		/**
		 * Generates an [Intent] for the MainActivity to be started
		 *
		 * @param packageContext The [Context] of the MainActivity
		 * @return An [Intent] for the MainActivity
		 */
		fun newIntent(packageContext: Context?): Intent = Intent(packageContext, MainActivity::class.java)
	}
}
