package com.griffinbholt.familymapclient.model.connection

import com.griffinbholt.familymapclient.model.connection.exception.BadResponseException
import com.griffinbholt.familymapclient.model.data.DataCache
import shared.http.FamilyMapUrl
import shared.json.JsonInterpreter
import shared.model.AuthToken
import shared.request.LoginRequest
import shared.request.RegisterRequest
import shared.result.*
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

object ServerProxy {
    private const val POST : String = "POST"
    private const val GET : String = "GET"
    private const val AUTHORIZATION : String = "Authorization"
    private const val READ_BUFFER_LENGTH = 1024

    var mServerHost : String? = null
    var mServerPort : Int? = null

    private fun baseUrl() : String? = "http://$mServerHost:$mServerPort"

    private fun loginUrl() : String? = baseUrl() + FamilyMapUrl.LOGIN

    private fun registerUrl() : String? = baseUrl() + FamilyMapUrl.REGISTER

    private fun personUrl() : String? = baseUrl() + FamilyMapUrl.PERSON

    private fun eventUrl() : String? = baseUrl() + FamilyMapUrl.EVENT

    private val jsonInterpreter = JsonInterpreter()

    fun login(request: LoginRequest) : LoginResult {
        DataCache.clear()

        val connection = prepareConnection(loginUrl()!!)
        val jsonRequest = preparePostRequest(request, connection)

        return try {
            connection.connect()
            writeStringToOutputStream(jsonRequest, connection.outputStream)
            readLoginResponse(connection)
        } catch (e: Exception) {
            LoginResult.newFailure(e.localizedMessage)
        }
    }

    fun register(request: RegisterRequest) : RegisterResult {
        DataCache.clear()

        val connection = prepareConnection(registerUrl()!!)
        val jsonRequest = preparePostRequest(request, connection)

        return try {
            connection.connect()
            writeStringToOutputStream(jsonRequest, connection.outputStream)
            readRegisterResponse(connection)
        } catch (e: Exception) {
            RegisterResult.newFailure(e.localizedMessage)
        }
    }

    private fun prepareConnection(urlString: String) : HttpURLConnection {
        val connection = openConnection(urlString)
        connection.readTimeout = 5000
        return connection
    }

    private fun openConnection(urlString: String) : HttpURLConnection {
        return URL(urlString).openConnection() as HttpURLConnection
    }

    private fun preparePostRequest(request: Any, connection: HttpURLConnection): String {
        val jsonRequest = jsonInterpreter.generateJsonString(request)
        connection.requestMethod = POST
        connection.doOutput = true
        return jsonRequest
    }

    private fun readLoginResponse(connection: HttpURLConnection) : LoginResult {
        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            val jsonResponse : String? = readStringFromInputStream(connection.inputStream)
            return jsonInterpreter.parseJson(jsonResponse, LoginResult::class.java) as LoginResult
        }

        throw BadResponseException()
    }

    private fun readRegisterResponse(connection: HttpURLConnection) : RegisterResult {
        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            val jsonResponse : String? = readStringFromInputStream(connection.inputStream)
            return jsonInterpreter.parseJson(jsonResponse, RegisterResult::class.java) as RegisterResult
        }

        throw BadResponseException()
    }

    @Throws(IOException::class)
    private fun readStringFromInputStream(inputStream: InputStream): String? {
        val stringBuilder = StringBuilder()
        val streamReader = InputStreamReader(inputStream)
        val buf = CharArray(READ_BUFFER_LENGTH)
        var len: Int

        while (0 < streamReader.read(buf).also { len = it }) {
            stringBuilder.append(buf, 0, len)
        }

        inputStream.close()
        return stringBuilder.toString()
    }

    @Throws(IOException::class)
    private fun writeStringToOutputStream(str: String, outputStream: OutputStream) {
        val streamWriter = OutputStreamWriter(outputStream)
        val bufferedWriter = BufferedWriter(streamWriter)
        bufferedWriter.write(str)
        bufferedWriter.flush()
    }

    fun requestFamilyMembers(authToken: AuthToken) : FamilyMembersResult {
        val connection : HttpURLConnection = sendGetRequest(personUrl()!!, authToken)

        return try {
            readFamilyMembersResponse(connection)
        } catch (e: Exception) {
            FamilyMembersResult.newFailure(e.localizedMessage)
        }
    }

    private fun sendGetRequest(urlString: String, authToken: AuthToken) : HttpURLConnection {
        val connection: HttpURLConnection = prepareConnection(urlString)

        connection.requestMethod = GET
        connection.addRequestProperty(AUTHORIZATION, authToken.toString())
        connection.connect()

        return connection
    }

    private fun readFamilyMembersResponse(connection: HttpURLConnection) : FamilyMembersResult {
        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            val jsonResponse : String? = readStringFromInputStream(connection.inputStream)
            return jsonInterpreter.parseJson(jsonResponse, FamilyMembersResult::class.java) as FamilyMembersResult
        }

        throw BadResponseException()
    }

    fun requestFamilyEvents(authToken: AuthToken) : AllEventsResult {
        val connection : HttpURLConnection = sendGetRequest(eventUrl()!!, authToken)

        return try {
            readFamilyEventsResponse(connection)
        } catch (e: IOException) {
            AllEventsResult.newFailure(e.localizedMessage)
        } catch (e: BadResponseException) {
            AllEventsResult.newFailure(e.localizedMessage)
        }
    }

    private fun readFamilyEventsResponse(connection: HttpURLConnection) : AllEventsResult {
        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            val jsonResponse : String? = readStringFromInputStream(connection.inputStream)
            return jsonInterpreter.parseJson(jsonResponse, AllEventsResult::class.java) as AllEventsResult
        }

        throw BadResponseException()
    }
}