package com.prongbang.photolib

import android.Manifest
import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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

		btnGallery.setOnClickListener {
			permissionRequest { selectGallery() }
		}

		btnOption.setOnClickListener {
			permissionRequest { selectImage() }
		}

		btnTakePhoto.setOnClickListener {
			permissionRequest { takePhoto() }
		}
	}

	@RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
	private fun takePhoto() {
		PhotoKit.Builder(this, BuildConfig.APPLICATION_ID)
				.addOnCameraListener { bitmap ->
					ivPreview.setImageBitmap(bitmap)
				}
				.build()
				.takePhoto()
	}

	@RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
	private fun selectImage() {
		PhotoKit.Builder(this, BuildConfig.APPLICATION_ID)
				.addOnPhotoListener { uri ->
					ivPreview.setImageURI(uri)
				}
				.addOnCameraListener { bitmap ->
					ivPreview.setImageBitmap(bitmap)
				}
				.build()
				.selectImage()
	}

	@RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
	private fun selectGallery() {
		PhotoKit.Builder(this, BuildConfig.APPLICATION_ID)
				.addOnPhotoListener { uri ->
					ivPreview.setImageURI(uri)
				}
				.build()
				.gallery()
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)

		PhotoKit.onActivityResult(requestCode, resultCode, data)
	}

	@RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
	private fun permissionRequest(onPermissionGranted: () -> Unit) {
		Dexter.withActivity(this)
				.withPermissions(
						Manifest.permission.CAMERA,
						Manifest.permission.READ_EXTERNAL_STORAGE,
						Manifest.permission.WRITE_EXTERNAL_STORAGE
				)
				.withListener(object : MultiplePermissionsListener {
					override fun onPermissionsChecked(report: MultiplePermissionsReport) {
						onPermissionGranted.invoke()
					}

					override fun onPermissionRationaleShouldBeShown(
							permissions: List<PermissionRequest>, token: PermissionToken) {
						token.continuePermissionRequest()
					}
				})
				.check()
	}
}
