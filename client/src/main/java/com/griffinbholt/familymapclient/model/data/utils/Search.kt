package com.griffinbholt.familymapclient.model.data.utils

import com.griffinbholt.familymapclient.model.data.item.ClientEvent
import com.griffinbholt.familymapclient.model.data.item.ClientPerson

interface Search {
    fun setTextQuery(textQuery: String)
    fun searchPeople(): List<ClientPerson>
    fun searchEvents(): List<ClientEvent>
}