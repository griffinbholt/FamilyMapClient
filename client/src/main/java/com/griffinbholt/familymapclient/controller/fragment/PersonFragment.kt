package com.griffinbholt.familymapclient.controller.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.griffinbholt.familymapclient.R
import com.griffinbholt.familymapclient.controller.adapter.ItemListAdapter
import com.griffinbholt.familymapclient.model.data.item.ClientPerson
import kotlinx.android.synthetic.main.fragment_person.*
import shared.model.Gender

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_FOCUS_PERSON = "com.griffinbholt.person.focusPerson"

/**
 * A simple [Fragment] subclass.
 * Use the [PersonFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PersonFragment : Fragment() {
    private var focusPerson: ClientPerson? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            focusPerson = it.getSerializable(ARG_FOCUS_PERSON) as ClientPerson
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
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
        outState.putSerializable(ARG_FOCUS_PERSON, focusPerson)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param focusPerson The person object, the information of which will be displayed
         * @return A new instance of fragment PersonFragment.
         */
        @JvmStatic
        fun newInstance(focusPerson: ClientPerson) : PersonFragment {
            val arguments = Bundle()

            arguments.putSerializable(ARG_FOCUS_PERSON, focusPerson)

            val personFragment = PersonFragment()
            personFragment.arguments = arguments

            return personFragment
        }
    }
}
