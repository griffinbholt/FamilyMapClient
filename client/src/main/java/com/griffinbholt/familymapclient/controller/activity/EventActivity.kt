package com.griffinbholt.familymapclient.controller.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.griffinbholt.familymapclient.controller.fragment.MapFragment
import com.griffinbholt.familymapclient.model.data.item.ClientEvent

class EventActivity : SingleFragmentActivity() {
	override fun createFragment(): Fragment {
		val focusEvent: ClientEvent = extractFocusEvent()
		return MapFragment.newInstance(focusEvent, false)
	}

	private fun extractFocusEvent(): ClientEvent {
		return intent.getSerializableExtra(EXTRA_FOCUS_EVENT) as ClientEvent
	}

	companion object {
		private const val EXTRA_FOCUS_EVENT = "com.griffinbholt.eventActivity.focusEvent"

		fun newIntent(packageContext: Context, focusEvent: ClientEvent): Intent {
			val intent = Intent(packageContext, EventActivity::class.java)
			intent.putExtra(EXTRA_FOCUS_EVENT, focusEvent)
			return intent
		}
	}
}
