package com.griffinbholt.familymapclient.controller.utils

import android.content.Context
import android.graphics.Color
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.griffinbholt.familymapclient.R
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.fonts.FontAwesomeIcons
import shared.model.EventType
import shared.model.Gender

/**
 * A singleton object that is responsible for generating icons needed for the application.
 *
 * @author griffinbholt
 */
object IconGenerator {

	// These are the default saturation and value values for the Google HSV color system
	private const val SATURATION: Float = 77.0F
	private const val VALUE: Float = 92.0F

	// Designed size of every icon
	private const val ICON_SIZE_DP = 40

	// A list of all default hues made available by Google for their BitmapDescriptorFactory-produced icons
	private val GOOGLE_HUES: List<Float> =
			listOf(BitmapDescriptorFactory.HUE_GREEN,
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

	/**
	 * Given an input list of possible [EventType]s, this function maps each [EventType]
	 * to a [BitmapDescriptorFactory] default hue and saves this map for later use.
	 *
	 * @param eventTypes A [List] of all non-repeated possible [EventType]s which the application will encounter.
	 */
	fun setPossibleEventTypes(eventTypes: List<EventType>) {
		val numPossibleEventTypes: Int = eventTypes.size
		val numPossibleHues: Int = GOOGLE_HUES.size

		for (i in 0 until numPossibleEventTypes) {
			val eventType = eventTypes[i]

			eventTypeHueMap[eventType] = GOOGLE_HUES[i % numPossibleHues]
		}
	}

	/**
	 * Returns the Android logo icon.
	 *
	 * @param context The [Context] in which the icon will be used (required for the FontAwesome icon generator)
	 * @return The Android logo icon, in the form of an [IconDrawable]
	 */
	fun getAndroidIcon(context: Context?): IconDrawable {
		return IconDrawable(context, FontAwesomeIcons.fa_android).colorRes(R.color.colorPrimary).sizeDp(ICON_SIZE_DP)
	}

	/**
	 * Given a [Gender], returns the respective gender icon from the FontAwesome library.
	 *
	 * @param context The [Context] in which the icon will be used (required for the FontAwesome icon generator)
	 * @return The gender icon, in the form of an [IconDrawable]
	 */
	@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
	fun getGenderIcon(context: Context?, gender: Gender): IconDrawable {
		val genderIcon: IconDrawable = when (gender) {
			Gender.FEMALE -> IconDrawable(context, FontAwesomeIcons.fa_female).colorRes(R.color.femaleIcon)
			Gender.MALE -> IconDrawable(context, FontAwesomeIcons.fa_male).colorRes(R.color.maleIcon)
		}

		genderIcon.sizeDp(ICON_SIZE_DP)

		return genderIcon
	}

	/**
	 * Returns the [BitmapDescriptorFactory] default hue to which the input [EventType] is mapped.
	 *
	 * @param eventType An input [EventType]
	 * @return The [BitmapDescriptorFactory] default hue, in the form of a [Float],
	 * 		   to which the input [EventType] is mapped.
	 */
	fun getMapMarkerHue(eventType: EventType): Float {
		return eventTypeHueMap[eventType]!!
	}

	/**
	 * Returns a map marker icon, from the FontAwesome library, with the [BitmapDescriptorFactory] default hue to which
	 * the input [EventType] is mapped.
	 *
	 * @param context The [Context] in which the icon will be used (required for the FontAwesome icon generator)
	 * @return The map marker icon, in the form of an [IconDrawable]
	 */
	fun getMapMarkerIcon(context: Context?, eventType: EventType): IconDrawable {
		val eventColor: Int = getColorFromHue(eventTypeHueMap[eventType]!!)
		return IconDrawable(context, FontAwesomeIcons.fa_map_marker).color(eventColor).sizeDp(ICON_SIZE_DP)
	}

	private fun getColorFromHue(hue: Float): Int {
		val hsv = floatArrayOf(hue, SATURATION, VALUE)
		return Color.HSVToColor(hsv)
	}

	/**
	 * Clears the [EventType]-to-[Hue][BitmapDescriptorFactory] map.
	 */
	fun clear() {
		eventTypeHueMap.clear()
	}
}