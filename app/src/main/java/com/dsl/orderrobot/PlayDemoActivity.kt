package com.dsl.orderrobot

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_play_demo.*

/**
 * @author dsl-abben
 * on 2020/09/28.
 */
class PlayDemoActivity: AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_demo)
        val mMediaController = MediaController(this)

//        //获得播放源访问入口
//        val afd = resources.openRawResourceFd(R.raw.demo)
////给MediaPlayer设置播放源
//        mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength())

        val path = "android.resource://" + packageName + "/" + R.raw.demo
        video_view.setVideoURI(Uri.parse(path))

//        video_view.setVideoPath(mMP4Path)
        video_view.setMediaController(mMediaController);
        video_view.seekTo(0)
        video_view.requestFocus()
        video_view.start()
//            pathTv.setText(mMP4Path);

    }

    override fun onClick(p0: View?) {

    }
}