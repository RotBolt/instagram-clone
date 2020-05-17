package com.mindorks.bootcamp.instagram.utils.common

import android.content.Context
import java.io.File
import java.io.InputStream

interface FileHelper {

    fun makeFile(path: String): File?

    fun getDirectory(context: Context, dirName: String): File

    fun saveInputStreamToFile(
        input: InputStream,
        directory: File,
        imageName: String,
        height: Int
    ): File?

    fun getImageSize(file: File): Pair<Int, Int>?

}