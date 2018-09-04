package com.prongbang.photolib

import android.Manifest
import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.prongbang.photokit.PhotoKit
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnGallery.setOnClickListener { _ ->
            selectGallery()
        }

        btnOption.setOnClickListener {
            selectImage()
        }

        btnTakePhoto.setOnClickListener {
            takePhoto()
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun takePhoto() {
        permisionRequest(object : OnPermissionGranted {
            override fun onGranted() {
                PhotoKit.create(this@MainActivity, BuildConfig.APPLICATION_ID)
                        .takePhoto()
                        .addOnCameraListener(object : PhotoKit.OnCameraListener {
                            override fun onResult(bitmap: Bitmap?) {
                                ivPreview.setImageBitmap(bitmap)
                            }
                        })
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun selectImage() {
        permisionRequest(object : OnPermissionGranted {
            override fun onGranted() {
                PhotoKit.create(this@MainActivity, BuildConfig.APPLICATION_ID)
                        .addOnPhotoListener(object : PhotoKit.OnPhotoListener {
                            override fun onResult(data: Uri?) {
                                ivPreview.setImageURI(data)
                            }
                        })
                        .selectImage()

            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun selectGallery() {
        permisionRequest(object : OnPermissionGranted {
            override fun onGranted() {
                PhotoKit.create(this@MainActivity, BuildConfig.APPLICATION_ID)
                        .gallery()
                        .addOnPhotoListener(object : PhotoKit.OnPhotoListener {
                            override fun onResult(data: Uri?) {
                                ivPreview.setImageURI(data)
                            }
                        })

            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun permisionRequest(onPermissionGranted: OnPermissionGranted) {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        onPermissionGranted.onGranted()
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }
                }).check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        PhotoKit.onActivityResult(requestCode, resultCode, data)
    }

    interface OnPermissionGranted {
        fun onGranted()
    }
}
