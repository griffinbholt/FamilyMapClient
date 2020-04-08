package com.griffinbholt.familymapclient.controller.fragment

import android.content.Intent
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.griffinbholt.familymapclient.R
import com.griffinbholt.familymapclient.controller.activity.MainActivity

/**
 * An abstract subclass of [Fragment] that manages the functionality of the up button.
 */
abstract class UpButtonFragment : Fragment() {

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == R.id.home) {
			returnToMainActivity()
		}

		return true
	}

	fun returnToMainActivity() {
		startActivity(getPopToMainActivityIntent())
	}

	fun getPopToMainActivityIntent(): Intent {
		val intent: Intent = MainActivity.newIntent(context)
		intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
		return intent
	}
}