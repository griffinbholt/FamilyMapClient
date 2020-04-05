package com.griffinbholt.familymapclient.controller.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.griffinbholt.familymapclient.R
import com.griffinbholt.familymapclient.controller.activity.SearchActivity
import com.griffinbholt.familymapclient.controller.activity.SettingsActivity
import com.griffinbholt.familymapclient.model.data.item.ClientEvent

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_DISPLAY_MENU = "com.griffinbholt.map.displayMenu"
private const val ARG_FOCUS_EVENT = "com.griffinbholt.map.focusEvent"

/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private var focusEvent: ClientEvent? = null
    private var displayMenu: Boolean = true

    private var map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            displayMenu = it.getBoolean(ARG_DISPLAY_MENU)
            focusEvent = it.getSerializable(ARG_FOCUS_EVENT) as ClientEvent?
        }

        setHasOptionsMenu(displayMenu)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_map, container, false)
        loadChildMapFragment()
        return view
    }

    private fun loadChildMapFragment() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap!!.setOnMapLoadedCallback(this)
        this.map = googleMap
        centerCameraOnFocusEvent()
    }

    private fun centerCameraOnFocusEvent() {
        focusEvent?.let {
            val coordinates = LatLng(focusEvent!!.latitude, focusEvent!!.longitude)
            map?.animateCamera(CameraUpdateFactory.newLatLng(coordinates))
        }
    }

    override fun onMapLoaded() {}

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent: Intent = when (item.itemId) {
            R.id.search_menu_item -> SearchActivity.newIntent(context)
            R.id.settings_menu_item -> SettingsActivity.newIntent(context)
            else -> return super.onOptionsItemSelected(item)
        }

        startActivity(intent)

        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putSerializable(ARG_FOCUS_EVENT, focusEvent)
        outState.putBoolean(ARG_DISPLAY_MENU, displayMenu)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param focusEvent The event on which the map will center its focus
         * @param displayMenu true, of the menu will be displayed; false, otherwise
         * @return A new instance of fragment MapFragment.
         */
        @JvmStatic
        fun newInstance(focusEvent: ClientEvent?, displayMenu: Boolean) : MapFragment {
            val arguments = Bundle()

            arguments.putSerializable(ARG_FOCUS_EVENT, focusEvent)
            arguments.putSerializable(ARG_DISPLAY_MENU, displayMenu)

            val mapFragment = MapFragment()
            mapFragment.arguments = arguments

            return mapFragment
        }
    }
}
