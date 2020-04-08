package com.griffinbholt.familymapclient.model.connection.exception

/**
 * An [Exception] subclass for when the FamilyMap server returns a bad HTTP response.
 *
 * @author griffinbholt
 */
class BadResponseException : Exception(DEFAULT_MESSAGE) {

	companion object {
		private const val DEFAULT_MESSAGE = "Bad HTTP response."
	}
}