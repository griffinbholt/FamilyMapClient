package com.griffinbholt.familymapclient.controller.utils

import android.content.Context
import android.graphics.Color
import com.griffinbholt.familymapclient.R
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.fonts.FontAwesomeIcons
import shared.model.EventType
import shared.model.Gender

object IconGenerator {
    private const val ICON_SIZE_DP = 40

    private val ANDROID_COLORS =
            listOf(Color.GREEN, Color.BLACK, Color.YELLOW, Color.CYAN, Color.RED, Color.MAGENTA,
                    Color.BLUE, Color.DKGRAY, Color.GRAY, Color.LTGRAY)

    private var eventTypeColorMap: MutableMap<EventType, Int> = HashMap()

    fun setPossibleEventTypes(eventTypes: List<EventType>) {
        val numPossibleEvenTypes: Int = eventTypes.size

        for (i in 0 until numPossibleEvenTypes) {
            val eventType = eventTypes[i]

            eventTypeColorMap[eventType] = ANDROID_COLORS[i]
        }
    }

    @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
    fun getGenderIcon(context: Context?, gender: Gender): IconDrawable {
        val genderIcon : IconDrawable = when (gender) {
            Gender.FEMALE -> IconDrawable(context, FontAwesomeIcons.fa_female).colorRes(R.color.femaleIcon)
            Gender.MALE -> IconDrawable(context, FontAwesomeIcons.fa_male).colorRes(R.color.maleIcon)
        }

        genderIcon.sizeDp(ICON_SIZE_DP)

        return genderIcon
    }

    fun getMapMarkerIcon(context: Context?, eventType: EventType): IconDrawable {
        val eventColor = eventTypeColorMap[eventType]!!
        return IconDrawable(context, FontAwesomeIcons.fa_map_marker)
                                        .color(eventColor)
                                        .sizeDp(ICON_SIZE_DP)
    }

    fun clear() {
        eventTypeColorMap.clear()
    }
}