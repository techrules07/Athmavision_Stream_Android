package com.e60.mvvm

import com.example.athmavisionstream.model.StreamInfoResponse

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by ${Saquib} on 03-05-2018.
 */


interface ApiCallInterface {

    @GET("athmavis/streaminfo.get")
    fun getStreamInfo(): Observable<StreamInfoResponse>

}
