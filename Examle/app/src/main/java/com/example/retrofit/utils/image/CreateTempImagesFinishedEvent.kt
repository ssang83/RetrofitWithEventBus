package com.example.retrofit.utils.image

/**
 * Created by Kim Joonsung on 2019-08-13.
 */
class CreateTempImagesFinishedEvent {
    var bitmapPaths: MutableList<String> = mutableListOf()
    var originalFilePaths: MutableList<String> = mutableListOf()
    var exception: Exception? = null
}