# PhotoKit Library

## How to use:

- Add it in your root build.gradle at the end of repositories:
```gragle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
- Add the dependency for support library
```gradle
dependencies {
    implementation 'com.github.prongbang:photolib:1.0.0'
}
```

- Add the dependency for androidX
```gradle
dependencies {
    implementation 'com.github.prongbang:photolib:1.1.0'
}
```

- Select Image from Gallery
```kotlin
PhotoKit.create(this@MainActivity, BuildConfig.APPLICATION_ID)
    .addOnPhotoListener(object : PhotoKit.OnPhotoListener {
        override fun onResult(data: Uri?) {
            ivPreview.setImageURI(data)
        }
    })
    .gallery()
```

- Select Image by options

```kotlin
PhotoKit.create(this@MainActivity, BuildConfig.APPLICATION_ID)
    .addOnPhotoListener(object : PhotoKit.OnPhotoListener {
        override fun onResult(data: Uri?) {
            ivPreview.setImageURI(data)
        }
    })
    .addOnCameraListener(object : PhotoKit.OnCameraListener {
        override fun onResult(bitmap: Bitmap?) {
            ivPreview.setImageBitmap(bitmap)
        }
    })
    .selectImage()
```

- Take a Photo

```kotlin
PhotoKit.create(this@MainActivity, BuildConfig.APPLICATION_ID)
    .addOnCameraListener(object : PhotoKit.OnCameraListener {
        override fun onResult(bitmap: Bitmap?) {
            ivPreview.setImageBitmap(bitmap)
        }
    })
    .takePhoto()
```

- On Activity Result

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    PhotoKit.onActivityResult(requestCode, resultCode, data)
}

```
