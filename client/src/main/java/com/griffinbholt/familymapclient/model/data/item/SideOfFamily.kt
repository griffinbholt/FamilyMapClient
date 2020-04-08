package com.griffinbholt.familymapclient.model.data.item

/**
 * A enumerated class representing the side of the family to which a [ClientPerson] belongs.
 * Valid types: IMMEDIATE (representing the user and his/her spouse);
 * 				MOTHER (representing the user's mother's side of the family);
 * 			 	FATHER (representing the user's father's side of the family)
 *
 * 	@author griffinbholt
 */
enum class SideOfFamily { IMMEDIATE, MOTHER, FATHER; }