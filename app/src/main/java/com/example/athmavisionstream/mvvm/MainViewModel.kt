package com.e60.mvvm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by ${Saquib} on 03-05-2018.
 */

class MainViewModel(private val repository: Repository) : ViewModel() {
    private val disposables = CompositeDisposable()
    val apiResponse = MutableLiveData<ApiResponse>()

    /*
     * method to call normal login api with $(mobileNumber + password)
     * */
    fun getStreamInfo() {
        disposables.add(repository.getStreamInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d -> apiResponse.setValue(ApiResponse.loading()) }
                .subscribe(
                        { result -> apiResponse.setValue(ApiResponse.success(result, result.type, 1)) },
                        { throwable -> apiResponse.setValue(ApiResponse.error(throwable)) }
                ))

    }


}
