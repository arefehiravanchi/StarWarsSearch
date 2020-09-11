package com.arefe.starwars.api

class CharacterListRequest(private val searchQuery: String? = "", private val page: Int) {
    val params: HashMap<String, String>
        get() {
            val params = HashMap<String, String>()
            if (!searchQuery.isNullOrEmpty()){
                params["search"] = searchQuery
            }
            if (page>0){
                params["page"] = page.toString()
            }
            return params
        }

}