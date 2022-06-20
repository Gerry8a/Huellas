package com.cosmocolor.huellas

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cn.com.aratek.fp.Bione
import cn.com.aratek.fp.FingerprintImage
import cn.com.aratek.fp.FingerprintScanner
import cn.com.aratek.util.Result
import com.cosmocolor.huellas.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var mFingerprintScanner: FingerprintScanner
    lateinit var binding: ActivityMainBinding
    private var mExecutor: ThreadPoolExecutor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mFingerprintScanner = FingerprintScanner.getInstance(this)
        mFingerprintScanner.powerOn()


        binding.btnCheck.setOnClickListener {
            checkDevice()
        }

        binding.btnCapture.setOnClickListener {
            captureImage()
        }
    }

    private fun checkDevice() {
//        createNewExecutor()
//        lifecycleScope.launch(Dispatchers.IO){
//            mFingerprintScanner.powerOn()
//        }
        mFingerprintScanner.powerOn()
        val openError: Int = mFingerprintScanner.open()
        Log.d("ggg", openError.toString())
        if (openError == FingerprintScanner.RESULT_OK) {
            Toast.makeText(this, "Está abierto", Toast.LENGTH_SHORT).show()
            Log.d("ggg", "open device")
        } else {
            Toast.makeText(this, "Está cerrado", Toast.LENGTH_SHORT).show()
            Log.d("ggg", "close device")
        }

    }

    private fun captureImage() {

        var startTime: Long
        var captureTime: Long = -1
        val extractTime: Long = -1
        val generalizeTime: Long = -1
        val verifyTime: Long = -1
        var fi: FingerprintImage? = null
        val fpFeat: ByteArray? = null
        var fpTemp: ByteArray
        var result: Result? = null

        mFingerprintScanner.prepare()
        try {
            do {
                startTime = System.currentTimeMillis()
                result = mFingerprintScanner.capture()
                captureTime = System.currentTimeMillis() - startTime
                fi = result.data as FingerprintImage
                if (fi != null) {

                }
                if (result.error != FingerprintScanner.NO_FINGER ||
                    getExecutor().isShutdown
                ) {
                    break
                }
            } while (true)

            if (fi != null) {
                convertFingerprint(fi)
//            binding.imageView.setImageBitmap(fi)
            }
//            mFingerprintScanner.finish()
        } catch (e: Exception) {
        }
    }

    private fun convertFingerprint(fi: FingerprintImage) {
        val fpBmp: ByteArray
        val bitmap: Bitmap?
        fpBmp = fi.convert2Bmp()
        bitmap = BitmapFactory.decodeByteArray(fpBmp, 0, fpBmp.size)

        binding.imageView.setImageBitmap(bitmap)

        mFingerprintScanner.powerOff()
        mFingerprintScanner.close()
    }

    protected fun createNewExecutor() {
        mExecutor = ThreadPoolExecutor(
            1, 1, 3, TimeUnit.SECONDS,
            LinkedBlockingQueue()
        )
    }

    protected fun getExecutor(): ThreadPoolExecutor {
        return mExecutor!!
    }

//    private fun showFingerprintImage() {
//        getExecutor().execute(FingerprintScannerTask("show"))
//    }
}