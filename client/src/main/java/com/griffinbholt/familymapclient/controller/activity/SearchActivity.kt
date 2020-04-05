package com.griffinbholt.familymapclient.controller.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.griffinbholt.familymapclient.controller.fragment.SearchFragment

class SearchActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return SearchFragment.newInstance()
    }

    companion object {
        fun newIntent(packageContext: Context?) : Intent {
            return Intent(packageContext, SearchActivity::class.java)
        }
    }
}
