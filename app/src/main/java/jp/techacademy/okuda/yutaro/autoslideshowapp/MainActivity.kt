package jp.techacademy.okuda.yutaro.autoslideshowapp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import android.view.View
import android.content.Intent
import android.database.Cursor
import android.os.Handler
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.logging.Logger.global

class MainActivity : AppCompatActivity(), View.OnClickListener {



    //    private val resolver = contentResolver
//    private val cursor1: resolver.query(
//    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
//    null, // 項目（null = 全項目）
//    null, // フィルタ条件（null = フィルタなし）
//    null, // フィルタ用パラメータ
//    null // ソート (nullソートなし）
//    )
    private val PERMISSIONS_REQUEST_CODE = 100
    private var cursor: Cursor? = null
//    private var cursor
    private var mHandler = Handler()
    private var mTimer: Timer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        forward_button.setOnClickListener(this)
        backward_button.setOnClickListener(this)
        play_pause_button.setOnClickListener(this)

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.forward_button -> forward()
            R.id.backward_button -> backward()
            R.id.play_pause_button -> play_pause()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("ANDROID", "許可された")
                } else {
                    forward_button.isEnabled = false
                    backward_button.isEnabled = false
                    play_pause_button.isEnabled = false

                    val view: View = findViewById(android.R.id.content)

                    val snackbar = Snackbar.make(view, "アクセスを許可してください", Snackbar.LENGTH_LONG)
                    snackbar.show()
                }
        }
    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            PERMISSIONS_REQUEST_CODE ->
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    getContentsInfo()
//                }
//        }
//    }
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray,
//        v: View
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            PERMISSIONS_REQUEST_CODE ->
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    getContentsInfo()
//                }
////                    forward_button.isEnabled = false
////                    backward_button.isEnabled = false
////                    play_pause_button.isEnabled = false
////                    Snackbar.make(v, "アクセスを許可してください", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show()
//        }
//    }

    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver
            cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目（null = 全項目）
            null, // フィルタ条件（null = フィルタなし）
            null, // フィルタ用パラメータ
            null // ソート (nullソートなし）
        )

        if (cursor!!.moveToFirst()) {
            // indexからIDを取得し、そのIDから画像のURIを取得する

            val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor!!.getLong(fieldIndex)
            val imageUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUri)
        }
//        cursor.close()
    }

    private fun forward() {

        if (cursor!!.moveToNext() == false) {
            cursor!!.moveToFirst()
        }

        val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
        val id = cursor!!.getLong(fieldIndex)
        val imageUri =
            ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

        imageView.setImageURI(imageUri)

    }

    private fun backward() {

        if (cursor!!.moveToPrevious()  == false) {
            cursor!!.moveToLast();
        }

        val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
        val id = cursor!!.getLong(fieldIndex)
        val imageUri =
            ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

        imageView.setImageURI(imageUri)

    }

    private fun play_pause() {
        if (mTimer == null) {
            forward_button.isEnabled = false
            backward_button.isEnabled = false
            play_pause_button.text = "停止"
            mTimer = Timer()
            mTimer!!.schedule(object : TimerTask() {
                override fun run() {
                    mHandler.post {
                        forward()
                    }
                }
            }, 2000, 2000)
        } else {
            if (mTimer != null) {
                mTimer!!.cancel()
                mTimer = null
            }
            forward_button.isEnabled = true
            backward_button.isEnabled = true
            play_pause_button.text = "再生"
        }
    }


}