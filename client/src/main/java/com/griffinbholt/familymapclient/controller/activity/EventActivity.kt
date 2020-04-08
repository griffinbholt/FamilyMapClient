package com.griffinbholt.familymapclient.controller.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.griffinbholt.familymapclient.controller.fragment.MapFragment
import com.griffinbholt.familymapclient.model.data.item.ClientEvent

/**
 * A [SingleFragmentActivity] subclass that hosts the [MapFragment]
 * when displaying a specific [ClientEvent] to the user.
 * Use the [EventActivity.newIntent] method to create an [Intent] for this activity.
 *
 * @author griffinbholt
 */
class EventActivity : SingleFragmentActivity() {

	override fun createFragment(): Fragment {
		val focusEvent: ClientEvent = extractFocusEvent()
		return MapFragment.newInstance(focusEvent, false)
	}

	private fun extractFocusEvent(): ClientEvent {
		return intent.getSerializableExtra(EXTRA_FOCUS_EVENT) as ClientEvent
	}

	companion object {
		// The activity initialization parameter for the displayed event
		private const val EXTRA_FOCUS_EVENT = "com.griffinbholt.eventActivity.focusEvent"

		/**
		 * Generates an [Intent] for a new EventActivity to be started
		 *
		 * @param packageContext The [Context] of the new EventActivity
		 * @param focusEvent The [ClientEvent] on which the new MapFragment will be focused
		 * @return An [Intent] bundled with the input [ClientEvent]
		 */
		fun newIntent(packageContext: Context, focusEvent: ClientEvent): Intent {
			val intent = Intent(packageContext, EventActivity::class.java)
			intent.putExtra(EXTRA_FOCUS_EVENT, focusEvent)
			return intent
		}
	}
}
