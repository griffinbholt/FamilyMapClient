package com.griffinbholt.familymapclient.controller.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.griffinbholt.familymapclient.R
import com.griffinbholt.familymapclient.controller.activity.PersonActivity
import com.griffinbholt.familymapclient.controller.activity.SearchActivity
import com.griffinbholt.familymapclient.controller.activity.SettingsActivity
import com.griffinbholt.familymapclient.controller.utils.IconGenerator
import com.griffinbholt.familymapclient.model.Settings
import com.griffinbholt.familymapclient.model.data.DataCache
import com.griffinbholt.familymapclient.model.data.item.ClientEvent
import com.griffinbholt.familymapclient.model.data.item.ClientPerson
import com.joanzapata.iconify.IconDrawable
import kotlinx.android.synthetic.main.fragment_map.*

/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : UpButtonFragment(),
		OnMapReadyCallback,
		GoogleMap.OnMapLoadedCallback,
		GoogleMap.OnMarkerClickListener {

	private var focusEvent: ClientEvent? = null
	private var displayMenu: Boolean = true

	private var enabledEvents: Set<ClientEvent>? = null

	private val polyLines: MutableList<Polyline> = ArrayList()

	private var map: GoogleMap? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		extractSavedVariables()
		setHasOptionsMenu(displayMenu)
	}

	private fun extractSavedVariables() {
		arguments?.let {
			displayMenu = it.getBoolean(ARG_DISPLAY_MENU)
			focusEvent = it.getSerializable(ARG_FOCUS_EVENT) as ClientEvent?
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view: View = inflater.inflate(R.layout.fragment_map, container, false)
		loadChildMapFragment()
		return view
	}

	override fun onStart() {
		super.onStart()
		updateEnabledEvents()
	}

	private fun updateEnabledEvents() {
		enabledEvents = HashSet(DataCache.enabledEvents())
	}

	private fun loadChildMapFragment() {
		val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
		mapFragment.getMapAsync(this)
	}

	override fun onMapReady(googleMap: GoogleMap?) {
		googleMap!!.setOnMapLoadedCallback(this)
		this.map = googleMap
		loadInitialMap()
	}

	private fun loadInitialMap() {
		drawEnabledEventMarkers()
		updateMap()
		setOnClickListeners()
	}

	private fun setOnClickListeners() {
		map!!.setOnMarkerClickListener(this)

		map_info_display.setOnClickListener {
			focusEvent?.let {
				startPersonActivity(focusEvent!!.person!!)
			}
		}
	}

	private fun startPersonActivity(person: ClientPerson) {
		val intent: Intent = PersonActivity.newIntent(context!!, person)
		startActivity(intent)
	}

	override fun onMarkerClick(selectedMarker: Marker?): Boolean {
		selectedMarker?.let {
			focusEvent = it.tag as ClientEvent
			updateMap()
		}

		return true
	}

	private fun drawEnabledEventMarkers() {
		for (event in enabledEvents!!) {
			val marker: Marker = map!!.addMarker(generateMarker(event))
			marker.tag = event
		}
	}

	private fun generateMarker(event: ClientEvent): MarkerOptions {
		val markerHue: Float = IconGenerator.getMapMarkerHue(event.eventType)

		return MarkerOptions()
				.position(event.latLng())
				.icon(BitmapDescriptorFactory.defaultMarker(markerHue))
				.title(event.description())
	}

	private fun updateMap() {
		updateInfoDisplay()

		focusEvent?.let {
			clearPolyLines()
			centerCameraOnFocusEvent()
			drawEnabledLines(it)
		}
	}

	private fun updateInfoDisplay() {
		updateInfoDisplayIcon()
		updateInfoDisplayText()
	}

	private fun clearPolyLines() {
		for (line in polyLines) {
			line.remove()
		}

		polyLines.clear()
	}

	private fun updateInfoDisplayIcon() {
		val icon: IconDrawable = getInfoDisplayIcon()
		map_display_icon.setImageDrawable(icon)
	}

	private fun getInfoDisplayIcon(): IconDrawable {
		return if (focusEvent == null) {
			IconGenerator.getAndroidIcon(context)
		} else {
			IconGenerator.getGenderIcon(context, focusEvent!!.person!!.gender)
		}
	}

	private fun updateInfoDisplayText() {
		map_display_text.text = getInfoDisplayText()
	}

	private fun getInfoDisplayText(): String {
		return if (focusEvent == null) getString(R.string.defaultDisplayText) else focusEvent!!.description()
	}

	private fun centerCameraOnFocusEvent() {
		val coordinates = focusEvent!!.latLng()
		map?.animateCamera(CameraUpdateFactory.newLatLng(coordinates))
	}

	private fun drawEnabledLines(selectedEvent: ClientEvent) {
		if (Settings.showSpouseLines) {
			drawSpouseLines(selectedEvent)
		}

		if (Settings.showFamilyTreeLines) {
			drawFamilyTreeLines(selectedEvent)
		}

		if (Settings.showLifeStoryLines) {
			drawLifeStoryLine(selectedEvent)
		}
	}

	private fun drawSpouseLines(selectedEvent: ClientEvent) {
		val selectedPerson: ClientPerson = selectedEvent.person!!

		if (spouseExists(selectedPerson) && spouseHasEarliestEvent(selectedPerson)) {
			val spouseEvent: ClientEvent = selectedPerson.spouse!!.events.first()

			if (eventIsEnabled(spouseEvent)) {
				addLineToMap(generateSpouseLine(selectedEvent, spouseEvent))
			}
		}
	}

	private fun addLineToMap(line: PolylineOptions) {
		val polyline: Polyline = map!!.addPolyline(line)
		polyLines.add(polyline)
	}

	private fun spouseExists(person: ClientPerson): Boolean {
		return person.spouse != null
	}

	private fun spouseHasEarliestEvent(person: ClientPerson): Boolean {
		return person.spouse!!.events.isNotEmpty()
	}

	private fun eventIsEnabled(event: ClientEvent?): Boolean {
		return enabledEvents!!.contains(event)
	}

	private fun drawFamilyTreeLines(selectedEvent: ClientEvent) {
		val familyTreeLines: List<PolylineOptions> = generateFamilyTreeLines(selectedEvent)

		for (line in familyTreeLines) {
			addLineToMap(line)
		}
	}

	private fun drawLifeStoryLine(selectedEvent: ClientEvent) {
		addLineToMap(generateLifeStoryLine(selectedEvent))
	}

	private fun generateSpouseLine(selectedEvent: ClientEvent, spouseEvent: ClientEvent): PolylineOptions {
		assert(eventIsEnabled(spouseEvent))
		return generateLine(selectedEvent, spouseEvent, BASE_LINE_WIDTH, SPOUSE_LINE_COLOR)
	}

	private fun generateLine(
			firstEvent: ClientEvent,
			secondEvent: ClientEvent,
			lineWidth: Float,
			lineColor: Int
	): PolylineOptions {
		return PolylineOptions().add(firstEvent.latLng(), secondEvent.latLng()).width(lineWidth).color(lineColor)
	}

	private fun generateLifeStoryLine(selectedEvent: ClientEvent): PolylineOptions {
		val line = PolylineOptions()

		addLifeEvents(selectedEvent.person!!, line)

		return line.width(BASE_LINE_WIDTH).color(LIFE_STORY_COLOR)
	}

	private fun addLifeEvents(selectedPerson: ClientPerson, line: PolylineOptions) {
		val lifeEvents: List<ClientEvent> = selectedPerson.events.sorted()

		for (event in lifeEvents) {
			assert(eventIsEnabled(event))
			line.add(event.latLng())
		}
	}

	private fun generateFamilyTreeLines(selectedEvent: ClientEvent): List<PolylineOptions> {
		val familyTreeLines: MutableList<PolylineOptions> = ArrayList()

		recurseAddAncestorLines(selectedEvent, BASE_LINE_WIDTH, familyTreeLines)

		return familyTreeLines
	}

	private fun recurseAddAncestorLines(
			selectedEvent: ClientEvent,
			lineWidth: Float,
			familyTreeLines: MutableList<PolylineOptions>
	) {
		val selectedPerson: ClientPerson = selectedEvent.person!!

		// Base Case #1 - The parent doesn't exist
		selectedPerson.mother?.let { addParentLines(selectedEvent, it, lineWidth, familyTreeLines) }

		// Base Case #1 - The parent doesn't exist
		selectedPerson.father?.let { addParentLines(selectedEvent, it, lineWidth, familyTreeLines) }
	}

	private fun addParentLines(
			selectedEvent: ClientEvent,
			parent: ClientPerson,
			lineWidth: Float,
			familyTreeLines: MutableList<PolylineOptions>
	) {
		val parentEarliestEvent: ClientEvent? = parent.events.min()

		// Base Case #2 - The event is not enabled
		if (!eventIsEnabled(parentEarliestEvent)) {
			return
		}

		// Recursive Case
		val line: PolylineOptions = generateLine(selectedEvent, parentEarliestEvent!!, lineWidth, FAMILY_TREE_COLOR)

		familyTreeLines.add(line)

		recurseAddAncestorLines(parentEarliestEvent, lineWidth - LINE_WIDTH_DECREMENT, familyTreeLines)
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.map_menu, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		val intent: Intent = when (item.itemId) {
			R.id.search_menu_item -> SearchActivity.newIntent(context)
			R.id.settings_menu_item -> SettingsActivity.newIntent(context)
			R.id.home -> getPopToMainActivityIntent()
			else -> return super.onOptionsItemSelected(item)
		}

		startActivity(intent)

		return true
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		saveInstanceVariables(focusEvent, displayMenu, outState)
	}

	override fun onMapLoaded() {}

	companion object {
		private const val BASE_LINE_WIDTH = 12.0F
		private const val LINE_WIDTH_DECREMENT = 3.0F

		private const val SPOUSE_LINE_COLOR: Int = Color.RED
		private const val FAMILY_TREE_COLOR: Int = Color.GREEN
		private const val LIFE_STORY_COLOR: Int = Color.BLUE

		// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
		private const val ARG_DISPLAY_MENU = "com.griffinbholt.map.displayMenu"
		private const val ARG_FOCUS_EVENT = "com.griffinbholt.map.focusEvent"

		/**
		 * Use this factory method to create a new instance of
		 * this fragment using the provided parameters.
		 *
		 * @param focusEvent The event on which the map will center its focus
		 * @param displayMenu true, of the menu will be displayed; false, otherwise
		 * @return A new instance of fragment MapFragment.
		 */
		@JvmStatic
		fun newInstance(focusEvent: ClientEvent?, displayMenu: Boolean): MapFragment {
			val arguments = Bundle()
			saveInstanceVariables(focusEvent, displayMenu, arguments)

			val mapFragment = MapFragment()
			mapFragment.arguments = arguments

			return mapFragment
		}

		private fun saveInstanceVariables(focusEvent: ClientEvent?, displayMenu: Boolean, bundle: Bundle) {
			bundle.putSerializable(ARG_FOCUS_EVENT, focusEvent)
			bundle.putSerializable(ARG_DISPLAY_MENU, displayMenu)
		}
	}
}
