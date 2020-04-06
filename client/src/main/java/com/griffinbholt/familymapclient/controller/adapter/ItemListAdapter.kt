package com.griffinbholt.familymapclient.controller.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.griffinbholt.familymapclient.R
import com.griffinbholt.familymapclient.controller.activity.EventActivity
import com.griffinbholt.familymapclient.controller.activity.PersonActivity
import com.griffinbholt.familymapclient.controller.utils.IconGenerator
import com.griffinbholt.familymapclient.model.Settings
import com.griffinbholt.familymapclient.model.data.item.ClientEvent
import com.griffinbholt.familymapclient.model.data.item.ClientPerson
import com.joanzapata.iconify.IconDrawable
import org.jetbrains.anko.find
import shared.model.Gender

private const val LIFE_EVENTS_GROUP_POSITION = 0
private const val FAMILY_GROUP_POSITION = 1

private const val NUM_GROUPS = 2

@Suppress("PrivatePropertyName")
class ItemListAdapter(private val fragment: Fragment,
                      private val person: ClientPerson):
                      BaseExpandableListAdapter() {

    private val context: Context = fragment.context!!
    private val layoutInflater: LayoutInflater = this.
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private val LIFE_EVENTS_TITLE = getString(R.string.lifeEventsTitle)
    private val FAMILY_TITLE = getString(R.string.familyTitle)

    private val FATHER_TITLE = getString(R.string.fatherTitle)
    private val MOTHER_TITLE = getString(R.string.motherTitle)
    private val SPOUSE_TITLE = getString(R.string.spouseTitle)
    private val CHILD_TITLE = getString(R.string.childTitle)

    private fun getString(resId: Int) : String {
        return fragment.getString(resId)
    }

    private val lifeEvents: List<ClientEvent> = initializeLifeEvents()

    private fun initializeLifeEvents(): List<ClientEvent> {
        return if (genderIsEnabled(person.gender)) {
            person.events.sorted()
        } else {
            ArrayList()
        }
    }

    private fun genderIsEnabled(gender: Gender) : Boolean {
        return (((Settings.femaleEventFilter) && (gender == Gender.FEMALE)) ||
                ((Settings.maleEventFilter) && (gender == Gender.MALE)))
    }

    private val family: MutableList<FamilyMemberItem> = ArrayList()

    init { initializeFamily() }

    private fun initializeFamily() {
        person.father?.let { family.add(FamilyMemberItem(it, FATHER_TITLE)) }
        person.mother?.let { family.add(FamilyMemberItem(it, MOTHER_TITLE)) }
        person.spouse?.let { family.add(FamilyMemberItem(it, SPOUSE_TITLE)) }

        for (child in person.children) {
            family.add(FamilyMemberItem(child, CHILD_TITLE))
        }
    }

    override fun getGroup(groupPosition: Int): Any {
        return when (groupPosition) {
            LIFE_EVENTS_GROUP_POSITION -> LIFE_EVENTS_TITLE
            FAMILY_GROUP_POSITION -> FAMILY_TITLE
            else -> throw IllegalArgumentException(invalidGroupPositionMessage(groupPosition))
        }
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean { return true }

    override fun hasStableIds(): Boolean { return false }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?,
                              parent: ViewGroup?): View {
        var groupView: View? = convertView

        if (groupView == null) {
            groupView = layoutInflater.inflate(R.layout.family_item_group, parent, false)
        }

        setGroupTitle(groupPosition, groupView!!)

        return groupView
    }

    private fun setGroupTitle(groupPosition: Int, groupView: View) {
        val groupTitle: TextView = groupView.find(R.id.group_title)
        groupTitle.text = getGroup(groupPosition) as String
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return when (groupPosition) {
            LIFE_EVENTS_GROUP_POSITION -> lifeEvents.size
            FAMILY_GROUP_POSITION -> family.size
            else -> throw IllegalArgumentException(invalidGroupPositionMessage(groupPosition))
        }
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return when (groupPosition) {
            LIFE_EVENTS_GROUP_POSITION -> lifeEvents[childPosition]
            FAMILY_GROUP_POSITION -> family[childPosition]
            else -> throw IllegalArgumentException(invalidGroupPositionMessage(groupPosition))
        }
    }

    override fun getGroupId(groupPosition: Int): Long { return groupPosition.toLong() }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean,
                              convertView: View?, parent: ViewGroup?): View {
        var childView : View? = convertView

        if (childView == null) {
            childView = layoutInflater.inflate(R.layout.family_item, parent, false)
        }

        updateChildView(childView!!, groupPosition, childPosition)

        return childView
    }

    private fun updateChildView(itemView: View, groupPosition: Int, childPosition: Int) {
        when (groupPosition) {
            LIFE_EVENTS_GROUP_POSITION -> {
                val lifeEvent = getChild(groupPosition, childPosition) as ClientEvent
                initializeLifeEventView(itemView, lifeEvent)
            }
            FAMILY_GROUP_POSITION -> {
                val familyMember = getChild(groupPosition, childPosition) as FamilyMemberItem
                initializeFamilyMemberView(itemView, familyMember)
            }
            else -> throw IllegalArgumentException(invalidGroupPositionMessage(groupPosition))
        }
    }

    private fun initializeLifeEventView(itemView: View, lifeEvent: ClientEvent) {
        val icon: IconDrawable = IconGenerator.getMapMarkerIcon(context, lifeEvent.eventType)
        initializeItemView(itemView, lifeEvent.description(), lifeEvent.person!!.fullName(), icon)

        itemView.setOnClickListener {
            val intent: Intent = EventActivity.newIntent(context, lifeEvent)
            startActivity(intent)
        }
    }

    private fun initializeFamilyMemberView(itemView: View, familyMember: FamilyMemberItem) {
        val icon: IconDrawable = IconGenerator.getGenderIcon(context, familyMember.person.gender)
        initializeItemView(itemView, familyMember.person.fullName(), familyMember.title, icon)

        itemView.setOnClickListener {
            val intent: Intent = PersonActivity.newIntent(context, familyMember.person)
            startActivity(intent)
        }
    }

    private fun initializeItemView(itemView: View, firstTextFieldText: String,
                                   secondTextFieldText: String, icon: IconDrawable) {
        val firstTextField : TextView = itemView.find(R.id.item_first_text_field)
        firstTextField.text = firstTextFieldText

        val secondTextField : TextView = itemView.find(R.id.item_second_text_field)
        secondTextField.text = secondTextFieldText

        val iconField : ImageView = itemView.find(R.id.item_icon)
        iconField.setImageDrawable(icon)
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int { return NUM_GROUPS }

    private fun invalidGroupPositionMessage(groupPosition: Int) =
            "Unrecognized group position: $groupPosition"

    private class FamilyMemberItem(val person: ClientPerson, val title: String)

    private fun startActivity(intent: Intent) {
        fragment.startActivity(intent)
    }
}