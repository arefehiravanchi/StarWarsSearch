package com.arefe.starwars.ui.characterlist

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arefe.starwars.R
import com.arefe.starwars.api.ResourceObserver
import com.arefe.starwars.api.model.Character
import com.arefe.starwars.di.Injectable
import com.arefe.starwars.ui.characterdetail.CharacterDetailActivity
import com.arefe.starwars.utilities.DATA_LOADING_ERROR
import com.arefe.starwars.utilities.EndlessRecyclerViewOnScrollListener
import com.arefe.starwars.utilities.toGone
import com.arefe.starwars.utilities.toVisible
import kotlinx.android.synthetic.main.activity_character_list.*
import javax.inject.Inject

class CharacterListActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var characterListViewModel: CharacterListViewModel? = null

    private var characterAdapter:CharacterAdapter? = null
    private val characterList = arrayListOf<Character>()
    private var onScrollListener: EndlessRecyclerViewOnScrollListener? = null


    companion object {
        const val EXT_SHOW_CHARACTER_LIST = "ext_show_character_list"
        fun start(activity: AppCompatActivity,showCharacterList: Boolean){
            val intent = Intent(activity,CharacterListActivity::class.java)
            intent.putExtra(EXT_SHOW_CHARACTER_LIST,showCharacterList)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_list)

        characterListViewModel = ViewModelProvider(this,viewModelFactory)[CharacterListViewModel::class.java]
        initViews()
        fetchData()
        observe()

    }

    private fun initViews() {
        if (intent.getBooleanExtra(EXT_SHOW_CHARACTER_LIST,false)){
            activity_characterList_txtTitle.toVisible()
            activity_characterList_lytSearch.toGone()
        } else {
            activity_characterList_txtTitle.toGone()
            activity_characterList_lytSearch.toVisible()
        }
        characterAdapter = CharacterAdapter(this,characterList){
            CharacterDetailActivity.start(this,it)
        }
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        val divider = DividerItemDecoration(this@CharacterListActivity, RecyclerView.VERTICAL)
        divider.setDrawable(ColorDrawable(ContextCompat.getColor(this,R.color.dark_gray_transparent)))
        onScrollListener = EndlessRecyclerViewOnScrollListener(layoutManager) {
            if (true == characterListViewModel?.hasMorePage()){
                characterListViewModel?.getCharacterList()
            }
        }

        activity_characterList_rcCharacters.apply {
            adapter = characterAdapter
            addItemDecoration(divider)
            setLayoutManager(layoutManager)
            addOnScrollListener(onScrollListener!!)

        }

        activity_characterList_edtSearch.apply {
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    characterListViewModel?.onQueryChanged(s.toString())

                }

            })
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard()
                    search()
                }
                false
            }
        }

        activity_characterList_btnRetry.setOnClickListener {
           fetchData()
        }
    }

    private fun fetchData(){
        if (intent.getBooleanExtra(EXT_SHOW_CHARACTER_LIST,false)) {
            characterListViewModel?.getFirstCharacterPage("")
        } else {
            search()
        }
    }

    private fun observe() {
        observeCharacterList()
    }

    private fun search() {
        if (activity_characterList_edtSearch.text.toString().isNotEmpty()) {
            hideKeyboard()
            characterListViewModel?.onQueryChanged(activity_characterList_edtSearch.text.toString())
        }
    }

    private fun observeCharacterList() {
        characterListViewModel?.getCharacterListResponse()?.observe(this,object : ResourceObserver<ArrayList<Character>>(){
            override fun onSuccess(data: ArrayList<Character>?) {
                handleShowingContent()
                data?.let {
                    if (false == characterListViewModel?.isLoadMore()) {
                        characterList.clear()
                        characterAdapter?.notifyDataSetChanged()
                    }
                    characterList.addAll(data)
                    characterAdapter?.notifyItemRangeChanged(characterAdapter?.itemCount ?: 0 , data.size)
                    onScrollListener?.onLoadFinished()
                }
            }

            override fun onLoading() {
                handleLoadingView()
            }

            override fun onEmpty() {
                handleEmptyView()
            }

            override fun onError(message: String?) {
                handleErrorView(message ?: DATA_LOADING_ERROR)
            }

        })
    }

    private fun handleLoadingView() {
        activity_characterList_lytError.toGone()
        if (true == characterListViewModel?.isLoadMore()) {
            activity_characterList_pbLoadMore.toVisible()
            activity_characterList_pbLoading.toGone()
        } else {
            activity_characterList_pbLoading.toVisible()
            activity_characterList_lytContentList.toGone()
        }
    }

    private fun handleErrorView(message: String) {
        activity_characterList_pbLoading.toGone()
        activity_characterList_pbLoadMore.toGone()
        if (false == characterListViewModel?.isLoadMore()) {
            activity_characterList_txtError.text = message
            activity_characterList_lytError.toVisible()
        }
    }

    private fun handleEmptyView(){
        activity_characterList_pbLoading.toGone()
        activity_characterList_pbLoadMore.toGone()
        if (false == characterListViewModel?.isLoadMore()) {
            activity_characterList_txtError.text = getString(R.string.empty_character_list,activity_characterList_edtSearch.text.toString())
            activity_characterList_lytError.toVisible()
        }
    }

    private fun handleShowingContent() {
        activity_characterList_lytError.toGone()
        activity_characterList_pbLoadMore.toGone()
        activity_characterList_pbLoading.toGone()
        activity_characterList_lytContentList.toVisible()
    }

    private fun hideKeyboard() {
        if (currentFocus != null) {
            val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

}
