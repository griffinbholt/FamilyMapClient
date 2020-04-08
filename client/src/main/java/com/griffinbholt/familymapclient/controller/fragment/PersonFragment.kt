package com.griffinbholt.familymapclient.controller.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.griffinbholt.familymapclient.R
import com.griffinbholt.familymapclient.controller.adapter.ItemListAdapter
import com.griffinbholt.familymapclient.model.data.item.ClientPerson
import kotlinx.android.synthetic.main.fragment_person.*
import shared.model.Gender

/**
 * An [UpButtonFragment] subclass to display a [ClientPerson] information.
 * Use the [PersonFragment.newInstance] factory method to create an instance of this fragment.
 */
class PersonFragment : UpButtonFragment() {

	private var focusPerson: ClientPerson? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		extractFocusPerson()
	}

	private fun extractFocusPerson() {
		arguments?.let { focusPerson = it.getSerializable(ARG_FOCUS_PERSON) as ClientPerson }
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_person, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		setFirstNameTextField()
		setLastNameTextField()
		setGenderTextField()
		setExpandableListView()
	}

	private fun setFirstNameTextField() {
		first_name_person_field.text = focusPerson!!.firstName
	}

	private fun setLastNameTextField() {
		last_name_person_field.text = focusPerson!!.lastName
	}

	@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
	private fun setGenderTextField() {
		gender_person_field.text = when (focusPerson!!.gender) {
			Gender.FEMALE -> Gender.FEMALE.toFullString()
			Gender.MALE -> Gender.MALE.toFullString()
		}
	}

	private fun setExpandableListView() {
		person_expandable_list_view.setAdapter(ItemListAdapter(this, focusPerson!!))
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		saveInstanceVariables(focusPerson, outState)
	}

	companion object {
		// The fragment initialization parameter for the displayed person
		private const val ARG_FOCUS_PERSON = "com.griffinbholt.person.focusPerson"

		/**
		 * Use this factory method to create a new instance of
		 * this fragment using the provided parameters.
		 *
		 * @param focusPerson The person object, the information of which will be displayed
		 * @return A new instance of fragment PersonFragment.
		 */
		@JvmStatic
		fun newInstance(focusPerson: ClientPerson): PersonFragment {
			val arguments = Bundle()

			saveInstanceVariables(focusPerson, arguments)

			val personFragment = PersonFragment()
			personFragment.arguments = arguments

			return personFragment
		}

		private fun saveInstanceVariables(focusPerson: ClientPerson?, bundle: Bundle) {
			bundle.putSerializable(ARG_FOCUS_PERSON, focusPerson)
		}
	}
}
