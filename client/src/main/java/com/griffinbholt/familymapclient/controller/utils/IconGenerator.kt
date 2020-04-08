package com.griffinbholt.familymapclient.controller.utils

import android.content.Context
import android.graphics.Color
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.griffinbholt.familymapclient.R
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.fonts.FontAwesomeIcons
import shared.model.EventType
import shared.model.Gender

object IconGenerator {
	// These are the default saturation and value values for the Google HSV color system
	private const val SATURATION: Float = 77.0F
	private const val VALUE: Float = 92.0F

	private const val ICON_SIZE_DP = 40

	private val GOOGLE_HUES: List<Float> = listOf(BitmapDescriptorFactory.HUE_GREEN,
			BitmapDescriptorFactory.HUE_VIOLET,
			BitmapDescriptorFactory.HUE_YELLOW,
			BitmapDescriptorFactory.HUE_RED,
			BitmapDescriptorFactory.HUE_AZURE,
			BitmapDescriptorFactory.HUE_MAGENTA,
			BitmapDescriptorFactory.HUE_CYAN,
			BitmapDescriptorFactory.HUE_ORANGE,
			BitmapDescriptorFactory.HUE_BLUE,
			BitmapDescriptorFactory.HUE_ROSE)

	private var eventTypeHueMap: MutableMap<EventType, Float> = HashMap()

	fun setPossibleEventTypes(eventTypes: List<EventType>) {
		val numPossibleEvenTypes: Int = eventTypes.size

		for (i in 0 until numPossibleEvenTypes) {
			val eventType = eventTypes[i]

			eventTypeHueMap[eventType] = GOOGLE_HUES[i]
		}
	}

	fun getAndroidIcon(context: Context?): IconDrawable {
		return IconDrawable(context, FontAwesomeIcons.fa_android).colorRes(R.color.colorPrimary).sizeDp(ICON_SIZE_DP)
	}

	@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
	fun getGenderIcon(context: Context?, gender: Gender): IconDrawable {
		val genderIcon: IconDrawable = when (gender) {
			Gender.FEMALE -> IconDrawable(context, FontAwesomeIcons.fa_female).colorRes(R.color.femaleIcon)
			Gender.MALE -> IconDrawable(context, FontAwesomeIcons.fa_male).colorRes(R.color.maleIcon)
		}

		genderIcon.sizeDp(ICON_SIZE_DP)

		return genderIcon
	}

	fun getMapMarkerHue(eventType: EventType): Float {
		return eventTypeHueMap[eventType]!!
	}

	fun getMapMarkerIcon(context: Context?, eventType: EventType): IconDrawable {
		val eventColor: Int = getColorFromHue(eventTypeHueMap[eventType]!!)
		return IconDrawable(context, FontAwesomeIcons.fa_map_marker).color(eventColor).sizeDp(ICON_SIZE_DP)
	}

	private fun getColorFromHue(hue: Float): Int {
		val hsv = floatArrayOf(hue, SATURATION, VALUE)
		return Color.HSVToColor(hsv)
	}

	fun clear() {
		eventTypeHueMap.clear()
	}
}