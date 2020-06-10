package com.e60.mvvm


import com.example.athmavisionstream.model.StreamInfoResponse

import io.reactivex.Observable

class Repository(private val apiCallInterface: ApiCallInterface) {

    /*
     * method to call login api
     * */
    fun getStreamInfo(): Observable<StreamInfoResponse> {
        return apiCallInterface.getStreamInfo()
    }

}
