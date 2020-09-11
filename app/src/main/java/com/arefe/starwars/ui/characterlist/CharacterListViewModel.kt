package com.arefe.starwars.ui.characterlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arefe.starwars.api.ApiResponse
import com.arefe.starwars.api.ResourceConsumer
import com.arefe.starwars.api.model.BaseListResponse
import com.arefe.starwars.api.model.Character
import com.arefe.starwars.repositories.CharacterRepository
import com.arefe.starwars.utilities.API_CHARACTER_LIST
import com.arefe.starwars.utilities.DATA_LOADING_ERROR
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CharacterListViewModel @Inject constructor(private val characterRepository: CharacterRepository) :
    ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    val characterList = MutableLiveData<ApiResponse<ArrayList<Character>>>()
    private var nextPage: String? = null
    private var characterListUrl = API_CHARACTER_LIST
    private var currentSearchQuery: String = ""
    private var pageNumber = 0

    private val querySubject = BehaviorSubject.create<String>().apply {
        debounce(1, TimeUnit.SECONDS)
            .map { it.trim() }
            //.filter { it != currentSearchQuery }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                getFirstCharacterPage(it)
            }, {})
    }


    fun getCharacterListResponse(): MutableLiveData<ApiResponse<ArrayList<Character>>> = characterList

    fun onQueryChanged(searchQuery: String) {
        querySubject.onNext(searchQuery)
    }

    fun getFirstCharacterPage(searchQuery: String) {
        characterListUrl = API_CHARACTER_LIST
        currentSearchQuery = searchQuery
        pageNumber = 0
        getCharacterList()
    }

    fun getCharacterList() {
        compositeDisposable.add(characterRepository.getCharacters(
            characterListUrl,
            currentSearchQuery
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : ResourceConsumer<BaseListResponse<Character>>() {
                override fun onSuccess(data: BaseListResponse<Character>?) {
                    if (data == null || data.results.isEmpty()) {
                        characterList.value = ApiResponse.empty()
                    } else {
                        characterList.value = ApiResponse.success(data.results)
                        nextPage = data.next
                        if (!nextPage.isNullOrEmpty()) {
                            characterListUrl = nextPage!!
                            pageNumber++
                        }
                    }
                }

                override fun onLoading() {
                    characterList.value = ApiResponse.loading()
                }

                override fun onEmpty() {
                    characterList.value = ApiResponse.empty()
                }

                override fun onError(message: String?) {
                    characterList.value = ApiResponse.error(message ?: DATA_LOADING_ERROR)
                }

            })
        )

    }

    fun isLoadMore(): Boolean {
        return pageNumber > 0
    }

    fun hasMorePage(): Boolean {
        return !nextPage.isNullOrEmpty()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }


}