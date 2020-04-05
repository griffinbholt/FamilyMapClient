package com.griffinbholt.familymapclient.controller.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.griffinbholt.familymapclient.R
import com.griffinbholt.familymapclient.model.data.DataCache
import com.griffinbholt.familymapclient.model.connection.ServerProxy
import kotlinx.android.synthetic.main.fragment_login.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import shared.model.Gender
import shared.request.LoginRequest
import shared.request.RegisterRequest
import shared.result.AllEventsResult
import shared.result.FamilyMembersResult
import shared.result.RegisterResult

// TODO - More detailed logging

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_SERVER_HOST = "com.griffinbholt.login.serverHost"
private const val ARG_SERVER_PORT = "com.griffinbholt.login.serverPort"
private const val ARG_USER_NAME = "com.griffinbholt.login.userName"
private const val ARG_PASSWORD = "com.griffinbholt.login.password"
private const val ARG_FIRST_NAME = "com.griffinbholt.login.firstName"
private const val ARG_LAST_NAME = "com.griffinbholt.login.lastName"
private const val ARG_EMAIL = "com.griffinbholt.login.email"
private const val ARG_GENDER = "com.griffinbholt.login.gender"
private const val ARG_LOGIN_ENABLED = "com.griffinbholt.login.loginEnabled"
private const val ARG_REGISTER_ENABLED = "com.griffinbholt.login.registerEnabled"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    private enum class TaskType {
        Login, Register;
    }

    private var mServerHost: String? = "10.0.2.2"
    private var mServerPort: Int? = 8080
    private var mUserName: String? = null
    private var mPassword: String? = null
    private var mFirstName: String? = null
    private var mLastName: String? = null
    private var mEmail: String? = null
    private var mGender: Gender? = null

    private var mLoginEnabled: Boolean = false
    private var mRegisterEnabled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mServerHost = it.getString(ARG_SERVER_HOST)
            mServerPort = it.getInt(ARG_SERVER_PORT)
            mUserName = it.getString(ARG_USER_NAME)
            mPassword = it.getString(ARG_PASSWORD)
            mFirstName = it.getString(ARG_FIRST_NAME)
            mLastName = it.getString(ARG_LAST_NAME)
            mEmail = it.getString(ARG_EMAIL)
            mGender = it.getSerializable(ARG_GENDER) as Gender?
            mLoginEnabled = it.getBoolean(ARG_LOGIN_ENABLED)
            mRegisterEnabled = it.getBoolean(ARG_REGISTER_ENABLED)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInputFields()
        setButtonListeners()
    }

    private fun setInputFields() {
        setServerHostField()
        setServerPortField()
        setUserNameField()
        setPasswordField()
        setFirstNameField()
        setLastNameField()
        setEmailField()
        setGenderListener()
    }

    private fun setServerHostField() {
        server_host_field.setText(mServerHost)

        server_host_field.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mServerHost = s.toString()
                toggleBothButtonsEnable()
            }
        })
    }

    private fun setServerPortField() {
        mServerPort?.let { server_port_field.setText(it.toString()) }

        server_port_field.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrBlank()) {
                    mServerPort = Integer.parseInt(s.toString())
                }

                toggleBothButtonsEnable()
            }
        })
    }

    private fun setUserNameField() {
        user_name_field.setText(mUserName)

        user_name_field.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mUserName = s.toString()
                toggleBothButtonsEnable()
            }
        })
    }

    private fun setPasswordField() {
        password_field.setText(mPassword)

        password_field.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mPassword = s.toString()
                toggleBothButtonsEnable()
            }
        })
    }

    private fun setFirstNameField() {
        first_name_login_field.setText(mFirstName)

        first_name_login_field.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mFirstName = s.toString()
                toggleRegisterButtonEnable()
            }
        })
    }

    private fun setLastNameField() {
        last_name_login_field.setText(mLastName)

        last_name_login_field.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mLastName = s.toString()
                toggleRegisterButtonEnable()
            }
        })
    }

    private fun setEmailField() {
        email_field.setText(mEmail)

        email_field.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mEmail = s.toString()
                toggleRegisterButtonEnable()
            }
        })
    }

    private fun setGenderListener() {
        when (mGender) {
            Gender.MALE -> gender_radio_group.check(male_radio_button.id)
            Gender.FEMALE -> gender_radio_group.check(female_radio_button.id)
        }

        gender_radio_group.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                male_radio_button.id -> mGender = Gender.MALE
                female_radio_button.id -> mGender = Gender.FEMALE
            }

            toggleRegisterButtonEnable()
        }
    }

    private fun toggleBothButtonsEnable() {
        toggleLoginButtonEnable()
        toggleRegisterButtonEnable()
    }

    private fun toggleLoginButtonEnable() {
        mLoginEnabled = (!mServerHost.isNullOrBlank()) && (mServerPort != null) &&
                        (!mUserName.isNullOrBlank()) && (!mPassword.isNullOrBlank())

        setSignInButtonEnable()
    }

    private fun setSignInButtonEnable() {
        sign_in_button.isEnabled = mLoginEnabled
    }

    private fun toggleRegisterButtonEnable() {
        mRegisterEnabled = mLoginEnabled && (!mFirstName.isNullOrBlank()) &&
                                            (!mLastName.isNullOrBlank()) &&
                                            (!mEmail.isNullOrBlank()) &&
                                            (mGender != null)

        setRegisterButtonEnable()
    }

    private fun setRegisterButtonEnable() {
        register_button.isEnabled = mRegisterEnabled
    }

    private fun setButtonListeners() {
        setLoginButtonListener()
        setRegisterButtonListener()
    }

    private fun setLoginButtonListener() {
        setSignInButtonEnable()
        sign_in_button.setOnClickListener {
            setServerAddress()

            val request = getLoginRequest()

            runLoginTask(request)
        }
    }

    private fun setServerAddress() {
        ServerProxy.mServerHost = mServerHost
        ServerProxy.mServerPort = mServerPort
    }

    private fun getLoginRequest() : LoginRequest {
        return LoginRequest(mUserName, mPassword)
    }

    private fun runLoginTask(request: LoginRequest) {
        doAsync {
            val result = ServerProxy.login(request)

            uiThread {
                handleInfoResult(TaskType.Login, result)
            }
        }
    }

    private fun handleInfoResult(taskType: TaskType, result: shared.result.InfoResult) {
        if (result.isSuccess) {
            DataCache.personID = result.personID
            DataCache.authToken = result.authToken
            runFetchDataTask(taskType)
        } else {
            displayFailureToast(taskType, result.message)
        }
    }

    private fun displayFailureToast(failureType : TaskType, failureMessage : String) {
        val toastMessage : String = when (failureType) {
            TaskType.Login -> getLoginFailureMessage(failureMessage)
            TaskType.Register -> getRegisterFailureMessage(failureMessage)
        }

        activity!!.toast(toastMessage)
    }

    private fun getLoginFailureMessage(failureMessage: String) =
            getFailureMessage(R.string.signInFailed, failureMessage)

    private fun getRegisterFailureMessage(failureMessage: String) =
            getFailureMessage(R.string.registerFailed, failureMessage)

    private fun getFailureMessage(stringValue: Int, failureMessage: String) =
            getString(stringValue, failureMessage)

    private fun runFetchDataTask(taskType: TaskType) {
        doAsync {
            val familyMembersResult = ServerProxy.requestFamilyMembers(DataCache.authToken!!)
            val familyEventsResult = ServerProxy.requestFamilyEvents(DataCache.authToken!!)

            uiThread {
                handleFetchDataResult(taskType, familyMembersResult, familyEventsResult)
            }
        }
    }

    private fun handleFetchDataResult(taskType: TaskType, familyMembersResult: FamilyMembersResult,
                                      familyEventsResult: AllEventsResult) {
        if (familyMembersResult.isSuccess && familyEventsResult.isSuccess) {
            DataCache.loadFamilyMembers(familyMembersResult.data)
            DataCache.loadFamilyEvents(familyEventsResult.data)

            displayFetchDataSuccess(taskType, DataCache.firstName!!, DataCache.lastName!!)

            startMapFragment()
        } else {
            displayFetchDataFailure(familyMembersResult, familyEventsResult, taskType)
        }
    }

    private fun displayFetchDataSuccess(taskType: TaskType, firstName: String, lastName: String) {
        val toastMessage : String = when (taskType) {
            TaskType.Login -> getLoginSuccessMessage(firstName, lastName)
            TaskType.Register -> getRegisterSuccessMessage(firstName, lastName)
        }

        activity!!.toast(toastMessage)
    }

    private fun getLoginSuccessMessage(firstName: String, lastName: String) =
            getSuccessMessage(R.string.signInSucceeded, firstName, lastName)

    private fun getRegisterSuccessMessage(firstName: String, lastName: String) =
            getSuccessMessage(R.string.registerSucceeded, firstName, lastName)

    private fun getSuccessMessage(stringValue: Int, firstName: String, lastName: String) =
            getString(stringValue, firstName, lastName)

    private fun startMapFragment() {
        fragmentManager!!.beginTransaction()
                .replace(R.id.fragment_container, MapFragment.newInstance(null, true))
                .commit()
    }

    private fun displayFetchDataFailure(familyMembersResult: FamilyMembersResult,
                                        familyEventsResult: AllEventsResult, taskType: TaskType) {
        val failureMessage = if (!familyMembersResult.isSuccess) familyMembersResult.message else familyEventsResult.message
        displayFailureToast(taskType, failureMessage)
    }

    private fun setRegisterButtonListener() {
        setRegisterButtonEnable()
        register_button.setOnClickListener {
            setServerAddress()

            val request = getRegisterRequest()

            runRegisterTask(request)
        }
    }

    private fun getRegisterRequest() : RegisterRequest {
        val gender : String = mGender!!.toAbbreviation()
        return RegisterRequest(mUserName, mPassword, mEmail, mFirstName, mLastName, gender)
    }

    private fun runRegisterTask(request: RegisterRequest) {
        doAsync {
            val result : RegisterResult = ServerProxy.register(request)

            uiThread {
                handleInfoResult(TaskType.Register, result)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(ARG_SERVER_HOST, mServerHost)
        mServerPort?.let { outState.putInt(ARG_SERVER_PORT, it) }
        outState.putString(ARG_USER_NAME, mUserName)
        outState.putString(ARG_PASSWORD, mPassword)
        outState.putString(ARG_FIRST_NAME, mFirstName)
        outState.putString(ARG_LAST_NAME, mLastName)
        outState.putString(ARG_EMAIL, mEmail)
        outState.putSerializable(ARG_GENDER, mGender)
        outState.putBoolean(ARG_LOGIN_ENABLED, mLoginEnabled)
        outState.putBoolean(ARG_REGISTER_ENABLED, mRegisterEnabled)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment LoginFragment.
         */
        @JvmStatic
        fun newInstance() = LoginFragment()
    }
}
