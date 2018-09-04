package com.prongbang.photokit

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PhotoKit(private var mActivity: Activity?, private var applicationId: String) {

    companion object {

        private const val TAG = "PhotoKit"

        private var currentPhotoPath = ""
        private var directoryName = "images"
        private var imageUri: Uri? = null

        private const val MEDIA_TYPE_IMAGE = 1
        private const val REQUEST_IMAGE_CAPTURE: Int = 1000
        private const val REQUEST_IMAGE_PICK: Int = 2000

        private var mOnCameraListener: OnCameraListener? = null
        private var mOnPhotoListener: OnPhotoListener? = null

        private var addPhoto: String = "Add Photo"
        private var takeAPhoto: String = "Take a Photo"
        private var chooseFromLibrary: String = "Choose from Library"
        private var selectFile: String = "Select File"

        fun create(activity: Activity, applicationId: String): PhotoKit {
            return PhotoKit(activity, applicationId)
        }

        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (resultCode == Activity.RESULT_OK) {
                when (requestCode) {
                    REQUEST_IMAGE_CAPTURE -> {

                        // bimatp factory
                        val options = BitmapFactory.Options()

                        // downsizing image as it throws OutOfMemory Exception for larger
                        // images
                        options.inSampleSize = 9

                        val bitmap = if (SDKUtil.isNougatAndHigher()) {
                            val imageUri = Uri.parse(currentPhotoPath)
                            val file = File(imageUri.path)
                            try {
                                val ims = FileInputStream(file)
                                BitmapUtil.getScaledDownBitmap(BitmapFactory.decodeStream(ims), 950, false)
                            } catch (e: FileNotFoundException) {
                                e.printStackTrace()
                                return
                            }
                        } else {
                            BitmapFactory.decodeFile(imageUri?.path, options)
                        }

                        mOnCameraListener?.onResult(bitmap)
                    }
                    REQUEST_IMAGE_PICK -> {
                        mOnPhotoListener?.onResult(data?.data)
                    }
                }
            }
        }
    }

    fun setTitle(title: String): PhotoKit {
        addPhoto = title
        return this
    }

    fun setTakePhoto(takePhoto: String): PhotoKit {
        takeAPhoto = takePhoto
        return this
    }

    fun setChooseFromLibrary(s: String): PhotoKit {
        chooseFromLibrary = s
        return this
    }

    fun setSelectFile(s: String): PhotoKit {
        selectFile = s
        return this
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
        mActivity?.startActivityForResult(Intent.createChooser(intent, selectFile), REQUEST_IMAGE_PICK)
        return this
    }

    fun addOnCameraListener(onCameraListener: OnCameraListener): PhotoKit {
        mOnCameraListener = onCameraListener
        return this
    }

    fun addOnPhotoListener(onPhotoListener: OnPhotoListener): PhotoKit {
        mOnPhotoListener = onPhotoListener
        return this
    }

    private fun getOutputMediaFileUri(type: Int): Uri? {
        return if (SDKUtil.isNougatAndHigher()) {
            if (mActivity == null) return null

            FileProvider.getUriForFile(
                    mActivity!!.applicationContext,
                    "$applicationId.fileprovider",
                    getOutputMediaFile(type)
            )
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
                    mediaFile = File.createTempFile(imageFileName, ".jpg", File(mediaStorageDir.path))
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

    interface OnCameraListener {
        fun onResult(bitmap: Bitmap?)
    }

    interface OnPhotoListener {
        fun onResult(data: Uri?)
    }
}