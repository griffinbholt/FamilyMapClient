package com.griffinbholt.familymapclient.model.connection.exception

import java.lang.Exception

class BadResponseException : Exception(DEFAULT_MESSAGE) {
    companion object {
        private const val DEFAULT_MESSAGE = "Bad HTTP response."
    }
}