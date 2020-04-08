package com.griffinbholt.familymapclient.controller.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.griffinbholt.familymapclient.R

abstract class SingleFragmentActivity : AppCompatActivity() {
	protected abstract fun createFragment(): Fragment

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_fragment_container)

		val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
		if (fragment == null) {
			loadFragment()
		}
	}

	private fun loadFragment() {
		val fragment = createFragment()
		supportFragmentManager.beginTransaction()
				.add(R.id.fragment_container, fragment)
				.commit()
	}
}
