package com.example.athmavisionstream

import android.content.*
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.e60.mvvm.ApiResponse
import com.e60.mvvm.MainViewModel
import com.e60.mvvm.Status
import com.e60.mvvm.ViewModelFactory
import com.example.athmavisionstream.interfaces.ActionEventsAndData
import com.example.athmavisionstream.model.StreamInfoResponse
import com.example.athmavisionstream.services.AudioService
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_radio_player.*
import javax.inject.Inject


class RadioPlayerActivity : AppCompatActivity(){

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    internal lateinit var viewModel: MainViewModel

    private lateinit var simpleExoPlayer: SimpleExoPlayer
    private lateinit var mediaSource: MediaSource
    private lateinit var dataSourceFactory: DefaultDataSourceFactory
    var audioManager: AudioManager? = null

    private var audioService: AudioService? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder) {
            val binder = service as AudioService.LocalBinder
            audioService = binder.getService()
            Log.e("connection","true")
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.e("connection","false")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_radio_player)

        (application as StreamApplication).getAppComponent().doInjection(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)

        viewModel.apiResponse.observe(this, Observer {
            if (it != null) {
                consumeResponse(it)
            }
        })

//        viewModel.getStreamInfo()

        val intent = Intent(this, AudioService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)

//        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this)
//
//        dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "exoPlayerSample"))
//
//        mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse("http://janus.cdnstream.com:5680//stream"))

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager?

//        audioManager!!.setRingerMode(AudioManager.RINGER_MODE_SILENT)
        val maxVolume = audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val curVolume = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
        sbVolume.setMax(maxVolume);
        sbVolume.setProgress(curVolume);


        sbVolume.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(arg0: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStartTrackingTouch(arg0: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onProgressChanged(
                arg0: SeekBar,
                arg1: Int,
                arg2: Boolean
            ) {
                // TODO Auto-generated method stub
                audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, arg1, 0)

                img_volume_mute.visibility = View.GONE
                img_volume_unmute.visibility = View.VISIBLE
            }
        })

        linear_plus.setOnClickListener {
            sbVolume.setProgress(sbVolume.progress+2)

            img_volume_mute.visibility = View.GONE
            img_volume_unmute.visibility = View.VISIBLE

        }

        linear_minus.setOnClickListener {
            sbVolume.setProgress(sbVolume.progress-2)

            img_volume_mute.visibility = View.GONE
            img_volume_unmute.visibility = View.VISIBLE
        }

        img_volume_unmute.setOnClickListener {

            audioManager!!.setStreamMute(AudioManager.STREAM_MUSIC,true)
            img_volume_mute.visibility = View.VISIBLE
            img_volume_unmute.visibility = View.GONE

        }

        img_volume_mute.setOnClickListener {

            audioManager!!.setStreamMute(AudioManager.STREAM_MUSIC,false)
            img_volume_mute.visibility = View.GONE
            img_volume_unmute.visibility = View.VISIBLE

        }

        txt_url.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("http://www.athmavision.org/"))
                startActivity(browserIntent)
        }

        img_play.setOnClickListener {
            img_play.visibility = View.GONE
            img_pause.visibility = View.VISIBLE
            audioService!!.initiatePlayPause("play")
        }

        img_pause.setOnClickListener {
            img_play.visibility = View.VISIBLE
            img_pause.visibility = View.GONE
            audioService!!.initiatePlayPause("pause")
        }


//        val h = Handler()
//        h.postDelayed(object : Runnable {
//            override fun run() {
//                viewModel.getStreamInfo()
//                h.postDelayed(this, 120000)
//            }
//        }, 120000)

    }

    override fun onDestroy() {
//        simpleExoPlayer.playWhenReady = false
        unbindService(connection)
        super.onDestroy()
    }

//    companion object {
//        const val RADIO_URL = "http://janus.cdnstream.com:5680//stream"
//    }

    /*
 * method to handle response
 * */
    private fun consumeResponse(apiResponse: ApiResponse) {
        when (apiResponse.status) {

//            Status.LOADING -> progressDialog.show()
            Status.LOADING -> Log.e("loading","loading")

            Status.SUCCESS -> {
//                progressDialog.dismiss()
//                Log.e("loading","success")
                renderSuccessResponse(apiResponse.data)
            }

            Status.FAILURE -> {
//                Toast.makeText(this, apiResponse.message, Toast.LENGTH_SHORT).show()
            }

            Status.ERROR -> {
//                progressDialog.dismiss()
//                Toast.makeText(this, "Try again", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /*
   * method to handle success response
   * */
    private fun renderSuccessResponse(response: Any?) {

        if (response != null) {
            if (response is StreamInfoResponse) {
//                txt_title.text = response.data!!.get(0)!!.song
                txt_title.text = response.data!!.get(0)!!.track!!.title
                txt_artist.text = response.data!!.get(0)!!.track!!.artist
            }
        }
    }


    override fun onStart() {
        super.onStart()

        val intentFilter = IntentFilter()
        intentFilter.addAction("MY_ACTION")
        registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }


    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
//            val datapassed = intent.getIntExtra("pAction", 0)
            val s = intent.action.toString()
            if(s.equals("MY_ACTION")){
                val s1 = intent.getStringExtra("pAction")
                val title = intent.getStringExtra("title")
                val artist = intent.getStringExtra("artist")


                if(s1.toString().length>0 && s1.equals("play")){
                    Log.e("s1",s1.toString())
//                    img_play.visibility = View.GONE
//                    img_pause.visibility = View.VISIBLE
                }
                else{
                    Log.e("s2",s1.toString())
//                    img_play.visibility = View.VISIBLE
//                    img_pause.visibility = View.GONE
                }

                if(title.length>0 && artist.length>0){
                    txt_artist.text = artist
                    txt_title.text = title
                }

            }

        }
    }

}