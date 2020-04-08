package com.griffinbholt.familymapclient.model

/*
 * TODO: (Project)
 *  - Make settings & authToken persistent
 *  - Make all views be able to rotate
 *  - Handle multiple markers at the same location
 */

/**
 * A singleton object to track application preferences set by the user
 * in the [SettingsFragment][com.griffinbholt.familymapclient.controller.fragment.SettingsFragment] settings menu
 *
 * Changing the filters below changes which [events][com.griffinbholt.familymapclient.model.data.item.ClientEvent]
 * are drawn on the map in the [MapFragment][com.griffinbholt.familymapclient.controller.fragment.MapFragment],
 * shown in search results in the [SearchFragment][com.griffinbholt.familymapclient.controller.fragment.SearchFragment],
 * and shown in the [person's][com.griffinbholt.familymapclient.model.data.item.ClientPerson] details in the
 * [PersonFragment][com.griffinbholt.familymapclient.controller.fragment.PersonFragment].
 *
 * [People][com.griffinbholt.familymapclient.model.data.item.ClientPerson] are only filtered in the search results.
 * The filters have no effect on which [people][com.griffinbholt.familymapclient.model.data.item.ClientPerson]
 * are visible on the map or in a person's details.
 */
object Settings {

	/**
	 * Enables life story lines to be drawn on the map
	 * in the [MapFragment][com.griffinbholt.familymapclient.controller.fragment.MapFragment]
	 */
	var showLifeStoryLines: Boolean = true

	/**
	 * Enables family tree lines to be drawn on the map
	 * in the [MapFragment][com.griffinbholt.familymapclient.controller.fragment.MapFragment]
	 */
	var showFamilyTreeLines: Boolean = true

	/**
	 * Enables spouse lines to be drawn on the map
	 * in the [MapFragment][com.griffinbholt.familymapclient.controller.fragment.MapFragment]
	 */
	var showSpouseLines: Boolean = true

	/**
	 * Enables [people][com.griffinbholt.familymapclient.model.data.item.ClientPerson] and
	 * [events][com.griffinbholt.familymapclient.model.data.item.ClientEvent] that are connected to the user's
	 * mother's side of the family.
	 */
	var motherSideFilter: Boolean = true

	/**
	 * Enables [people][com.griffinbholt.familymapclient.model.data.item.ClientPerson] and
	 * [events][com.griffinbholt.familymapclient.model.data.item.ClientEvent] that are connected to the user's
	 * father's side of the family.
	 */
	var fatherSideFilter: Boolean = true

	/**
	 * Enables [events][com.griffinbholt.familymapclient.model.data.item.ClientEvent] that are connected to females
	 */
	var femaleEventFilter: Boolean = true

	/**
	 * Enables [events][com.griffinbholt.familymapclient.model.data.item.ClientEvent] that are connected to males
	 */
	var maleEventFilter: Boolean = true
}