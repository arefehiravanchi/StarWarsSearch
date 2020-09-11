package com.arefe.starwars.utilities

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EndlessRecyclerViewOnScrollListener (private val layoutManager: LinearLayoutManager,
                                           private val onLoadMore: () -> Unit) :  RecyclerView.OnScrollListener() {

    private var loading = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val totalItemCount = layoutManager.itemCount
        val visibleThreshold = 1
        val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
        if (!loading && totalItemCount <= lastVisibleItem + visibleThreshold) {
            onLoadMore.invoke()
            loading = true
        }

    }

    fun onLoadFinished() {
        loading = false
    }
}