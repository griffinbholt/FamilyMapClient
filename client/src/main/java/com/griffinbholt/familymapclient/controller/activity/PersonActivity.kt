package com.griffinbholt.familymapclient.controller.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.griffinbholt.familymapclient.controller.fragment.PersonFragment
import com.griffinbholt.familymapclient.model.data.item.ClientPerson

class PersonActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        val focusPerson: ClientPerson = extractFocusPerson()
        return PersonFragment.newInstance(focusPerson)
    }

    private fun extractFocusPerson(): ClientPerson {
        return intent.getSerializableExtra(EXTRA_FOCUS_PERSON) as ClientPerson
    }

    companion object {
        const val EXTRA_FOCUS_PERSON = "com.griffinbholt.personActivity.focusPerson"

        fun newIntent(packageContext: Context, focusPerson: ClientPerson) : Intent {
            val intent = Intent(packageContext, PersonActivity::class.java)
            intent.putExtra(EXTRA_FOCUS_PERSON, focusPerson)
            return intent
        }
    }
}
