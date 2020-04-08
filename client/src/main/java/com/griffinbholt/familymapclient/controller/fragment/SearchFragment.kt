package com.griffinbholt.familymapclient.controller.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.griffinbholt.familymapclient.R
import com.griffinbholt.familymapclient.controller.activity.EventActivity
import com.griffinbholt.familymapclient.controller.activity.PersonActivity
import com.griffinbholt.familymapclient.controller.utils.IconGenerator
import com.griffinbholt.familymapclient.model.data.DataCache
import com.griffinbholt.familymapclient.model.data.item.ClientEvent
import com.griffinbholt.familymapclient.model.data.item.ClientPerson
import com.joanzapata.iconify.IconDrawable
import kotlinx.android.synthetic.main.fragment_search.*
import org.jetbrains.anko.find

/**
 * An [UpButtonFragment] subclass to display a search menu.
 * Use the [SearchFragment.newInstance] factory method to create an instance of this fragment.
 *
 * @author griffinbholt
 */
class SearchFragment : UpButtonFragment() {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_search, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setSearchBar()
		setRecyclerView()
	}

	private fun setSearchBar() {
		search_bar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
			override fun onQueryTextSubmit(query: String?): Boolean {
				if (!query.isNullOrEmpty()) {
					search_recycler_view.adapter = runSearchQuery(query)
				}

				return true
			}

			override fun onQueryTextChange(newText: String?): Boolean {
				return true
			}
		})
	}

	private fun runSearchQuery(textQuery: String): SearchItemAdapter {
		DataCache.setTextQuery(textQuery)

		val peopleFound: List<ClientPerson> = DataCache.searchEnabledPeople()
		val eventsFound: List<ClientEvent> = DataCache.searchEnabledEvents()

		return SearchItemAdapter(peopleFound, eventsFound)
	}

	private fun setRecyclerView() {
		search_recycler_view.layoutManager = LinearLayoutManager(activity)
	}

	/** A private inner RecyclerView.Adapter class for the RecyclerView
	 * to display search results (ClientPerson and ClientEvent objects)
	 *
	 * @param peopleFound The [List] of [ClientPerson] objects to display in the [RecyclerView]
	 * @param eventsFound The [List] of [ClientEvent] objects to display in the [RecyclerView]
	 */
	private inner class SearchItemAdapter(
			private val peopleFound: List<ClientPerson>,
			private val eventsFound: List<ClientEvent>
	) : RecyclerView.Adapter<SearchItemHolder>() {

		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemHolder {
			val view: View = layoutInflater.inflate(R.layout.family_item, parent, false)
			return SearchItemHolder(view, viewType)
		}

		override fun getItemCount(): Int {
			return peopleFound.size + eventsFound.size
		}

		override fun getItemViewType(position: Int): Int {
			return if (position < peopleFound.size) PERSON_ITEM_VIEW_TYPE else EVENT_ITEM_VIEW_TYPE
		}

		override fun onBindViewHolder(holder: SearchItemHolder, position: Int) {
			if (position < peopleFound.size) {
				holder.bind(peopleFound[position])
			} else {
				holder.bind(eventsFound[position - peopleFound.size])
			}
		}
	}

	/**
	 * A private inner [RecyclerView.ViewHolder] subclass for the [SearchFragment]'s [RecyclerView]
	 * to display search results ([ClientPerson] and [ClientEvent] objects).
	 * Implements [View.OnClickListener].
	 *
	 * @param itemView The [View] for the item in the [RecyclerView] list
	 * @param viewType The viewType of the bound item
	 */
	private inner class SearchItemHolder(
			itemView: View,
			private val viewType: Int
	) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

		private val firstTextField: TextView
		private val secondTextField: TextView
		private val icon: ImageView

		private lateinit var person: ClientPerson
		private lateinit var event: ClientEvent

		init {
			itemView.setOnClickListener(this)
			firstTextField = itemView.find(R.id.item_first_text_field)
			secondTextField = itemView.find(R.id.item_second_text_field)
			icon = itemView.find(R.id.item_icon)
		}

		/**
		 * Binds the input [ClientPerson] object to the [itemView][View] in the [RecyclerView]
		 *
		 * @param person A [ClientPerson] item to be bound to the [itemView][View]
		 */
		fun bind(person: ClientPerson) {
			this.person = person
			bind(person.fullName(), null, IconGenerator.getGenderIcon(context, person.gender))
		}

		/**
		 * Binds the input [ClientEvent] object to the [itemView][View] in the [RecyclerView]
		 *
		 * @param event A [ClientEvent] item to be bound to the [itemView][View]
		 */
		fun bind(event: ClientEvent) {
			this.event = event
			val icon: IconDrawable = IconGenerator.getMapMarkerIcon(context, event.eventType)
			bind(event.description(), event.person!!.fullName(), icon)
		}

		private fun bind(firstText: String, secondText: String?, icon: IconDrawable) {
			setFirstTextField(firstText)
			setSecondTextField(secondText)
			setIcon(icon)
		}

		private fun setFirstTextField(text: String) {
			firstTextField.text = text
		}

		private fun setSecondTextField(text: String?) {
			secondTextField.text = text
		}

		private fun setIcon(iconDrawable: IconDrawable) {
			icon.setImageDrawable(iconDrawable)
		}

		/**
		 * Starts a [PersonActivity] or a [EventActivity], respectively, when a [ClientPerson] or [ClientEvent] item
		 * is clicked in the [RecyclerView] list.
		 *
		 * @param v The item view for the [ClientPerson] or [ClientEvent] item
		 */
		override fun onClick(v: View?) {
			val intent: Intent? = when (viewType) {
				PERSON_ITEM_VIEW_TYPE -> context?.let { PersonActivity.newIntent(it, this.person) }
				EVENT_ITEM_VIEW_TYPE -> context?.let { EventActivity.newIntent(it, this.event) }
				else -> throw IllegalArgumentException(invalidViewTypeMessage(viewType))
			}

			startActivity(intent)
		}

		private fun invalidViewTypeMessage(viewType: Int) = "Unrecognized view type: $viewType"
	}

	companion object {
		// The two view types for the RecyclerView used in this fragment
		private const val PERSON_ITEM_VIEW_TYPE = 0
		private const val EVENT_ITEM_VIEW_TYPE = 1

		/**
		 * Use this factory method to create a new instance of
		 * this fragment using the provided parameters.
		 *
		 * @return A new instance of fragment SearchFragment.
		 */
		@JvmStatic
		fun newInstance() = SearchFragment()
	}
}
