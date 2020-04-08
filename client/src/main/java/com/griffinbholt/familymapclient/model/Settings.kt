package com.griffinbholt.familymapclient.model

/*
 * TODO:
 *  - Make settings & authToken persistent
 *  - Make all views be able to rotate
 *  - Handle multiple markers at the same location
 */

object Settings {
	var showLifeStoryLines: Boolean = true
	var showFamilyTreeLines: Boolean = true
	var showSpouseLines: Boolean = true

	var fatherSideFilter: Boolean = true
	var motherSideFilter: Boolean = true

	var maleEventFilter: Boolean = true
	var femaleEventFilter: Boolean = true
}