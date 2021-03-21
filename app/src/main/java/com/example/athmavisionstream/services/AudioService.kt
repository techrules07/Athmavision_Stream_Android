package com.example.athmavisionstream.services

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import androidx.annotation.Nullable
import com.example.athmavisionstream.R
import com.example.athmavisionstream.RadioPlayerActivity
import com.example.athmavisionstream.utils.AppConstants
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerNotificationManager.*
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception


public class AudioService : Service() {

    private lateinit var simpleExoPlayer: SimpleExoPlayer
    private lateinit var mediaSource: MediaSource
    private lateinit var dataSourceFactory: DefaultDataSourceFactory
    private val binder = LocalBinder()

    private var playerNotificationManager: PlayerNotificationManager? = null
    val MY_ACTION = "MY_ACTION"
    val client = OkHttpClient()

    var title = ""
    var artist = ""
    lateinit var h : Handler
    var r: Runnable? = null

    inner class LocalBinder : Binder() {
        fun getService(): AudioService = this@AudioService
    }

    override fun onCreate() {
        super.onCreate()
        val context: Context = this

        getStreamInfo(AppConstants.API_URL, context)

        h = Handler()
        r = object : Runnable {
            override fun run() {
                Log.e("runnable", "rrr")
                getStreamInfo(AppConstants.API_URL, context)
                h.postDelayed(r, 120000)
            }
        }
        h.postDelayed(r, 120000)

        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this)
        dataSourceFactory = DefaultDataSourceFactory(
            this, Util.getUserAgent(
                this,
                "exoPlayerSample"
            )
        )
        mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
            Uri.parse(
                AppConstants.RADIO_URL
            )
        )

        with(simpleExoPlayer) {
            prepare(mediaSource)

            playWhenReady = true

            showNotification(context)

        }

    }


    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }


    override fun onDestroy() {
        super.onDestroy()
        playerNotificationManager!!.setPlayer(null)
        simpleExoPlayer.playWhenReady = false
        h.removeCallbacks(r)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    fun initiatePlayPause(command: String){
        Log.e("command", command)
        if(command.equals("play")){
            simpleExoPlayer.playWhenReady = true
        }
        else if(command.equals("pause")){
            simpleExoPlayer.playWhenReady = false
        }

    }

    fun showNotification(context: Context){
        playerNotificationManager =
            createWithNotificationChannel(context,
                "playback_channel", R.string.app_name, 1,
                object : MediaDescriptionAdapter {
                    override fun getCurrentContentTitle(player: Player): String {
                        return title
                    }

                    override fun createCurrentContentIntent(player: Player): PendingIntent? {
                        val intent = Intent(context, RadioPlayerActivity::class.java)
                        return PendingIntent.getActivity(
                            context, 0,
                            intent, PendingIntent.FLAG_UPDATE_CURRENT
                        )
                    }

                    override fun getCurrentContentText(player: Player): String? {
                        return artist
                    }

                    override fun getCurrentLargeIcon(
                        player: Player,
                        callback: BitmapCallback
                    ): Bitmap? {
                        return null
                    }
                })
        playerNotificationManager!!.setNotificationListener(object :
            NotificationListener {
            override fun onNotificationStarted(
                notificationId: Int,
                notification: Notification
            ) {
                startForeground(notificationId, notification)
            }

            override fun onNotificationCancelled(notificationId: Int) {
                stopSelf()
            }
        })


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            playerNotificationManager!!.setColor(getColor(R.color.colorPrimaryDark))
        }
        playerNotificationManager!!.setSmallIcon(R.drawable.athma_logo)
        playerNotificationManager!!.setPlayer(simpleExoPlayer)
        playerNotificationManager!!.setRewindIncrementMs(0);
        playerNotificationManager!!.setFastForwardIncrementMs(0);
        playerNotificationManager!!.setStopAction(null)

        simpleExoPlayer.addListener(PlayerEventListener())

    }

    private inner class PlayerEventListener : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            if (playbackState == Player.STATE_READY) {
                if (simpleExoPlayer.playWhenReady) {
                    // In Playing state
                    Log.e("playWhenReady", "play")
                    val intent = Intent()
                    intent.action = MY_ACTION
                    intent.putExtra("pAction", "play")
                    intent.putExtra("title", "")
                    intent.putExtra("artist", "")
                    sendBroadcast(intent)
                } else {
                    // In Paused state
                    Log.e("playWhenReady", "pause")
                    val intent = Intent()
                    intent.action = MY_ACTION
                    intent.putExtra("pAction", "pause")
                    intent.putExtra("title", "")
                    intent.putExtra("artist", "")
                    sendBroadcast(intent)
                }
            } else if (playbackState == Player.STATE_ENDED) {
                // In Ended state
            }
        }
        override fun onPlayerError(error: ExoPlaybackException) {
            // On error
        }
    }

    fun getStreamInfo(url: String, context: Context) {
        Log.e("runnable", "stream")
//        progress.visibility = View.VISIBLE
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
//                progress.visibility = View.GONE
            }

            override fun onResponse(call: Call, response: Response) {
                var str_response = response.body()!!.string()
                //creating json object
                val json_contact: JSONObject = JSONObject(str_response)
                //creating json array
//                var jsonarray_info: JSONArray = json_contact.getJSONArray("data")
//                var i:Int = 0
//                var size:Int = jsonarray_info.length()

                var jsonarray_info: JSONArray = json_contact.getJSONArray("history")

//                Log.e("title",jsonarray_info.getJSONObject(0).getString("title"))

//                title = jsonarray_info.getJSONObject(0).getJSONObject("track").getString("title")
//                artist = jsonarray_info.getJSONObject(0).getJSONObject("track").getString("artist")

                try{
                    val titleArtist = json_contact.getJSONObject("current_track").getString("title")
                    val parts = titleArtist.split("-".toRegex()).toTypedArray()
                    title = parts[0]
                    artist = parts[1]
                }catch(e: Exception){
                    title = jsonarray_info.getJSONObject(0).getString("title")
                    artist = jsonarray_info.getJSONObject(1).getString("title")
                }

//                showNotification(context)

                val intent = Intent()
                intent.action = MY_ACTION
                intent.putExtra("pAction", "")
//                intent.putExtra("title",jsonarray_info.getJSONObject(0).getJSONObject("track").getString("title"))
//                intent.putExtra("artist",jsonarray_info.getJSONObject(0).getJSONObject("track").getString("artist"))
                intent.putExtra("title", title)
                intent.putExtra("artist", artist)
                sendBroadcast(intent)

            }
        })
    }

}