package com.prongbang.photokit

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PhotoKit(private var mActivity: Activity?, private var applicationId: String) {

	private var addPhoto: String = ""
	private var takeAPhoto: String = ""
	private var chooseFromLibrary: String = ""
	private var selectFile: String = ""

	companion object {

		private const val TAG = "PhotoKit"

		private var currentPhotoPath = ""
		private var directoryName = "images"
		private var imageUri: Uri? = null
		private var resize: Int? = null

		private var mOnCameraListener: ((Bitmap) -> Unit)? = null
		private var mOnPhotoListener: ((Uri?) -> Unit)? = null

		private const val MEDIA_TYPE_IMAGE = 1
		private const val REQUEST_IMAGE_CAPTURE: Int = 1000
		private const val REQUEST_IMAGE_PICK: Int = 2000

		fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
			if (resultCode == Activity.RESULT_OK) {
				when (requestCode) {
					REQUEST_IMAGE_CAPTURE -> {

						// bimatp factory
						val options = BitmapFactory.Options()

						// Downsizing image as it throws OutOfMemory Exception for larger images
						options.inSampleSize = 9

						val bitmap = if (SDKUtil.isNougatAndHigher()) {
							val imageUri = Uri.parse(currentPhotoPath)
							val file = File(imageUri.path)
							try {
								val ims = FileInputStream(file)
								if (resize != null) {
									BitmapUtil.getScaledDownBitmap(BitmapFactory.decodeStream(ims),
											1024, false)
								} else {
									BitmapFactory.decodeStream(ims)
								}
							} catch (e: FileNotFoundException) {
								e.printStackTrace()
								return
							}
						} else {
							BitmapFactory.decodeFile(imageUri?.path, options)
						}

						mOnCameraListener?.invoke(bitmap)
					}
					REQUEST_IMAGE_PICK -> {
						mOnPhotoListener?.invoke(data?.data)
					}
				}
			}
		}
	}

	class Builder(private val activity: Activity, private val applicationId: String) {

		private var addPhotoText: String = "Add Photo"
		private var takeAPhotoText: String = "Take a Photo"
		private var chooseFromLibraryText: String = "Choose from Library"
		private var selectFileText: String = "Select File"
		private var resizeInt: Int? = null

		private var onCameraListener: ((Bitmap) -> Unit)? = null
		private var onPhotoListener: ((Uri?) -> Unit)? = null

		fun setTitle(title: String): Builder {
			addPhotoText = title
			return this
		}

		fun setTakePhoto(takePhoto: String): Builder {
			takeAPhotoText = takePhoto
			return this
		}

		fun setChooseFromLibrary(s: String): Builder {
			chooseFromLibraryText = s
			return this
		}

		fun setSelectFile(s: String): Builder {
			selectFileText = s
			return this
		}

		fun setResize(size: Int): Builder {
			resizeInt = size
			return this
		}

		fun addOnCameraListener(listener: ((Bitmap) -> Unit)?): Builder {
			onCameraListener = listener
			return this
		}

		fun addOnPhotoListener(listener: ((Uri?) -> Unit)?): Builder {
			onPhotoListener = listener
			return this
		}

		fun build(): PhotoKit {
			return PhotoKit(activity, applicationId).apply {
				addPhoto = addPhotoText
				takeAPhoto = takeAPhotoText
				chooseFromLibrary = chooseFromLibraryText
				selectFile = selectFileText
				resize = resizeInt
				mOnCameraListener = onCameraListener
				mOnPhotoListener = onPhotoListener
			}
		}
	}

	fun selectImage() {
		if (mActivity == null) return

		val items = arrayOf<CharSequence>(takeAPhoto, chooseFromLibrary)
		val builder = AlertDialog.Builder(mActivity!!)
		builder.setTitle(addPhoto)
		builder.setItems(items) { _, item ->
			when (item) {
				0 -> takePhoto()
				else -> gallery()
			}
		}
		builder.show()
	}

	fun takePhoto(): PhotoKit {
		val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
		imageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE)
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

		// start the image capture Intent
		mActivity?.startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
		return this
	}

	fun gallery(): PhotoKit {
		val intent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
		intent.type = "image/*"
		mActivity?.startActivityForResult(Intent.createChooser(intent, selectFile),
				REQUEST_IMAGE_PICK)
		return this
	}

	private fun getOutputMediaFileUri(type: Int): Uri? {
		return if (SDKUtil.isNougatAndHigher()) {
			if (mActivity == null) return null

			FileProvider.getUriForFile(mActivity!!.applicationContext,
					"$applicationId.fileprovider", getOutputMediaFile(type))
		} else Uri.fromFile(getOutputMediaFile(type))
	}

	private fun getOutputMediaFile(type: Int): File {

		// External sdcard location
		val mediaStorageDir = File(
				Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				directoryName
		)

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.e(TAG, "Oops! Failed create $directoryName directory")
				return File(directoryName)
			}
		}

		// Create a media file name
		val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
		var mediaFile: File
		val imageFileName = "IMG_$timeStamp.jpg"
		if (type == MEDIA_TYPE_IMAGE) {

			mediaFile = File(mediaStorageDir.path + File.separator + imageFileName)
			if (SDKUtil.isNougatAndHigher()) {
				try {
					mediaFile = File.createTempFile(imageFileName, ".jpg",
							File(mediaStorageDir.path))
					currentPhotoPath = mediaFile.absolutePath
				} catch (e: IOException) {
					e.printStackTrace()
				}
			}
		} else {
			return File(directoryName)
		}

		return mediaFile
	}
}