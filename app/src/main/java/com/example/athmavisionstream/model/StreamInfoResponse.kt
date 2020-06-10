package com.example.athmavisionstream.model

class StreamInfoResponse {

    var type: String? = null
    var data: ArrayList<StreamData>?=null

    class StreamData{

        var title:String?=null
        var song:String?=null
        var track:TrackDetails?=null

        class TrackDetails{
            var artist:String?=null
            var title:String?=null
            var album:String?=null
            var imageurl:String?=null
        }

        var tuneinurl:String?=null
        var proxytuneinurl:String?=null
    }

}
