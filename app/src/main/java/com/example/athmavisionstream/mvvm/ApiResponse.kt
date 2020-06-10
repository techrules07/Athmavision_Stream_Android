package com.e60.mvvm

import io.reactivex.annotations.NonNull
import io.reactivex.annotations.Nullable

import com.e60.mvvm.Status.ERROR
import com.e60.mvvm.Status.FAILURE
import com.e60.mvvm.Status.LOADING
import com.e60.mvvm.Status.SUCCESS



class ApiResponse private constructor(val status: Status, @param:Nullable @field:Nullable
val data: Any?, @param:Nullable @field:Nullable
                                      val error: Throwable?, message: String?, var errorCode: Int) {

    var message: String? = null

    init {
        this.message = message
    }

    companion object {

        fun loading(): ApiResponse {
            return ApiResponse(LOADING, null, null, null, 0)
        }

        fun success(@NonNull data: Any, message: String?=null, errorCode: Int): ApiResponse {
            return if (errorCode == 1) {
                ApiResponse(SUCCESS, data, null, message, errorCode)
            } else {
                ApiResponse(FAILURE, data, null, message, errorCode)
            }
        }

        fun error(@NonNull error: Throwable): ApiResponse {
            return ApiResponse(ERROR, null, error, null, 0)
        }
    }

}
