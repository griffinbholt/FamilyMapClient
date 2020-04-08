package com.griffinbholt.familymapclient.controller.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import com.griffinbholt.familymapclient.R
import com.griffinbholt.familymapclient.controller.utils.IconGenerator
import com.griffinbholt.familymapclient.model.Settings
import com.griffinbholt.familymapclient.model.data.DataCache
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 * An [UpButtonFragment] subclass to display a the settings menu.
 * Use the [SearchFragment.newInstance] factory method to create an instance of this fragment.
 *
 * @author griffinbholt
 */
class SettingsFragment : UpButtonFragment() {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_settings, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		setLifeStorySwitch()
		setFamilyTreeSwitch()
		setSpouseSwitch()
		setMotherSideSwitch()
		setFatherSideSwitch()
		setFemaleEventSwitch()
		setMaleEventSwitch()
		setLogoutButton()
	}

	private fun setLifeStorySwitch() {
		life_story_switch.isChecked = Settings.showLifeStoryLines

		life_story_switch.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
			Settings.showLifeStoryLines = b
		}
	}

	private fun setFamilyTreeSwitch() {
		family_tree_switch.isChecked = Settings.showFamilyTreeLines

		family_tree_switch.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
			Settings.showFamilyTreeLines = b
		}
	}

	private fun setSpouseSwitch() {
		spouse_switch.isChecked = Settings.showSpouseLines

		spouse_switch.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
			Settings.showSpouseLines = b
		}
	}

	private fun setFatherSideSwitch() {
		father_side_switch.isChecked = Settings.fatherSideFilter

		father_side_switch.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
			Settings.fatherSideFilter = b
		}
	}

	private fun setMotherSideSwitch() {
		mother_side_switch.isChecked = Settings.motherSideFilter

		mother_side_switch.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
			Settings.motherSideFilter = b
		}
	}

	private fun setMaleEventSwitch() {
		male_event_switch.isChecked = Settings.maleEventFilter

		male_event_switch.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
			Settings.maleEventFilter = b
		}
	}

	private fun setFemaleEventSwitch() {
		female_event_switch.isChecked = Settings.femaleEventFilter

		female_event_switch.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
			Settings.femaleEventFilter = b
		}
	}

	private fun setLogoutButton() {
		logout_button.setOnClickListener {
			DataCache.clear()
			IconGenerator.clear()
			returnToMainActivity()
		}
	}

	companion object {
		/**
		 * Use this factory method to create a new instance of
		 * this fragment using the provided parameters.
		 *
		 * @return A new instance of fragment SettingsFragment.
		 */
		@JvmStatic
		fun newInstance() = SettingsFragment()
	}
}
