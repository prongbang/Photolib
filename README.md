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
    implementation 'com.github.prongbang:photolib:1.2.0'
}
```

- Select Image from Gallery
```kotlin
PhotoKit.Builder(this, BuildConfig.APPLICATION_ID)
    .addOnPhotoListener { uri ->
        ivPreview.setImageURI(uri)
    }
    .build()
    .gallery()
```

- Select Image by options

```kotlin
PhotoKit.Builder(this, BuildConfig.APPLICATION_ID)
    .addOnPhotoListener { uri ->
        ivPreview.setImageURI(uri)
    }
    .addOnCameraListener { bitmap ->
        ivPreview.setImageBitmap(bitmap)
    }
    .build()
    .selectImage()
```

- Take a Photo

```kotlin
PhotoKit.Builder(this, BuildConfig.APPLICATION_ID)
    .addOnCameraListener { bitmap ->
        ivPreview.setImageBitmap(bitmap)
    }
    .build()
    .takePhoto()
```

- On Activity Result

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    PhotoKit.onActivityResult(requestCode, resultCode, data)
}

```
