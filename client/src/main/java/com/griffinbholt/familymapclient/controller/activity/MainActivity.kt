package com.griffinbholt.familymapclient.controller.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.griffinbholt.familymapclient.R
import com.griffinbholt.familymapclient.controller.fragment.LoginFragment
import com.griffinbholt.familymapclient.controller.fragment.MapFragment
import com.griffinbholt.familymapclient.model.data.DataCache
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.FontAwesomeModule

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_container)

        Iconify.with(FontAwesomeModule())

        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            loadInitialFragment()
        }
    }

    private fun loadInitialFragment() {
        val fragment = if (DataCache.authToken == null) {
            LoginFragment.newInstance()
        } else {
            MapFragment.newInstance(null, true)
        }

        supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
    }
}
