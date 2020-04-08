package com.griffinbholt.familymapclient.model.connection.exception

class BadResponseException : Exception(DEFAULT_MESSAGE) {
	companion object {
		private const val DEFAULT_MESSAGE = "Bad HTTP response."
	}
}