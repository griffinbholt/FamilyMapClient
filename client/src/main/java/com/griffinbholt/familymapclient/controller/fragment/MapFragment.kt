package com.griffinbholt.familymapclient.controller.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
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
 * An [UpButtonFragment] subclass to display a [GoogleMap].
 * Implements [OnMapReadyCallback], [GoogleMap.OnMapLoadedCallback], and [GoogleMap.OnMarkerClickListener].
 * Use the [MapFragment.newInstance] factory method to create an instance of this fragment.
 */
class MapFragment : UpButtonFragment(),
		OnMapReadyCallback,
		GoogleMap.OnMapLoadedCallback,
		GoogleMap.OnMarkerClickListener {

	private var mFocusEvent: ClientEvent? = null
	private var mDisplayMenu: Boolean = true

	private var mEnabledEvents: Set<ClientEvent>? = null

	private val mPolyLines: MutableList<Polyline> = ArrayList()

	private var mGoogleMap: GoogleMap? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		extractSavedVariables()
		setHasOptionsMenu(mDisplayMenu)
	}

	private fun extractSavedVariables() {
		arguments?.let {
			mDisplayMenu = it.getBoolean(ARG_DISPLAY_MENU)
			mFocusEvent = it.getSerializable(ARG_FOCUS_EVENT) as ClientEvent?
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
		mEnabledEvents = HashSet(DataCache.enabledEvents())
	}

	private fun loadChildMapFragment() {
		val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
		mapFragment.getMapAsync(this)
	}

	override fun onMapReady(googleMap: GoogleMap?) {
		googleMap!!.setOnMapLoadedCallback(this)
		this.mGoogleMap = googleMap
		loadInitialMap()
	}

	private fun loadInitialMap() {
		drawEnabledEventMarkers()
		updateMap()
		setOnClickListeners()
	}

	private fun setOnClickListeners() {
		mGoogleMap!!.setOnMarkerClickListener(this)

		map_info_display.setOnClickListener {
			mFocusEvent?.let {
				startPersonActivity(mFocusEvent!!.person!!)
			}
		}
	}

	private fun startPersonActivity(person: ClientPerson) {
		val intent: Intent = PersonActivity.newIntent(context!!, person)
		startActivity(intent)
	}

	/**
	 * Updates the map by making the [Marker]'s related [ClientEvent] the focus of the [map][GoogleMap].
	 *
	 * @param selectedMarker The [Marker] that was clicked on the [map][GoogleMap]
	 * @return [Boolean] always true
	 */
	override fun onMarkerClick(selectedMarker: Marker?): Boolean {
		selectedMarker?.let {
			mFocusEvent = it.tag as ClientEvent
			updateMap()
		}

		return true
	}

	private fun drawEnabledEventMarkers() {
		for (event in mEnabledEvents!!) {
			val marker: Marker = mGoogleMap!!.addMarker(generateMarker(event))
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

		mFocusEvent?.let {
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
		for (line in mPolyLines) {
			line.remove()
		}

		mPolyLines.clear()
	}

	private fun updateInfoDisplayIcon() {
		val icon: IconDrawable = getInfoDisplayIcon()
		map_display_icon.setImageDrawable(icon)
	}

	private fun getInfoDisplayIcon(): IconDrawable {
		return if (mFocusEvent == null) {
			IconGenerator.getAndroidIcon(context)
		} else {
			IconGenerator.getGenderIcon(context, mFocusEvent!!.person!!.gender)
		}
	}

	private fun updateInfoDisplayText() {
		map_display_text.text = getInfoDisplayText()
	}

	private fun getInfoDisplayText(): String {
		return if (mFocusEvent == null) getString(R.string.defaultDisplayText) else mFocusEvent!!.description()
	}

	private fun centerCameraOnFocusEvent() {
		val coordinates = mFocusEvent!!.latLng()
		mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLng(coordinates))
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
		val polyline: Polyline = mGoogleMap!!.addPolyline(line)
		mPolyLines.add(polyline)
	}

	private fun spouseExists(person: ClientPerson): Boolean {
		return person.spouse != null
	}

	private fun spouseHasEarliestEvent(person: ClientPerson): Boolean {
		return person.spouse!!.events.isNotEmpty()
	}

	private fun eventIsEnabled(event: ClientEvent?): Boolean {
		return mEnabledEvents!!.contains(event)
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
		saveInstanceVariables(mFocusEvent, mDisplayMenu, outState)
	}

	override fun onMapLoaded() {}

	companion object {
		/* The designated base line width for spouse lines, life story lines, and the first generation
		   of family tree lines */
		private const val BASE_LINE_WIDTH = 12.0F

		// The amount by which the line width is decremented with each generation for the family tree lines
		private const val LINE_WIDTH_DECREMENT = 3.0F

		// The default line colors
		private const val SPOUSE_LINE_COLOR: Int = Color.RED
		private const val FAMILY_TREE_COLOR: Int = Color.GREEN
		private const val LIFE_STORY_COLOR: Int = Color.BLUE

		// The fragment initialization parameters, e.g. ARG_ITEM_NUMBER
		private const val ARG_DISPLAY_MENU = "com.griffinbholt.map.displayMenu"
		private const val ARG_FOCUS_EVENT = "com.griffinbholt.map.focusEvent"

		/**
		 * Use this factory method to create a new instance of this fragment using the provided parameters.
		 *
		 * @param focusEvent The event on which the map will center its focus
		 * @param displayMenu true, the menu will be displayed; false, otherwise
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
