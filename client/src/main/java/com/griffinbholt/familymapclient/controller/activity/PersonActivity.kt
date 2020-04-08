package com.griffinbholt.familymapclient.controller.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.griffinbholt.familymapclient.controller.fragment.PersonFragment
import com.griffinbholt.familymapclient.model.data.item.ClientPerson

/**
 * A [SingleFragmentActivity] subclass that hosts the [PersonFragment] to display [ClientPerson] data to the user.
 * Use the [PersonActivity.newIntent] method to create an [Intent] for this activity.
 */
class PersonActivity : SingleFragmentActivity() {

	override fun createFragment(): Fragment {
		val focusPerson: ClientPerson = extractFocusPerson()
		return PersonFragment.newInstance(focusPerson)
	}

	private fun extractFocusPerson(): ClientPerson {
		return intent.getSerializableExtra(EXTRA_FOCUS_PERSON) as ClientPerson
	}

	companion object {
		// The activity initialization parameter for the displayed person
		private const val EXTRA_FOCUS_PERSON = "com.griffinbholt.personActivity.focusPerson"

		/**
		 * Generates an [Intent] for a new PersonActivity to be started
		 *
		 * @param packageContext The [Context] of the new PersonActivity
		 * @param focusPerson The [ClientPerson] whose information will be displayed by the new PersonActivity
		 * @return An [Intent] bundled with the input [ClientPerson]
		 */
		fun newIntent(packageContext: Context, focusPerson: ClientPerson): Intent {
			val intent = Intent(packageContext, PersonActivity::class.java)
			intent.putExtra(EXTRA_FOCUS_PERSON, focusPerson)
			return intent
		}
	}
}
