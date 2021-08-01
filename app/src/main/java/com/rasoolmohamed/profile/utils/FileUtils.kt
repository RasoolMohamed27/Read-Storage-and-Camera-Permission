package com.rasoolmohamed.profile.utils

import android.os.Build
import android.annotation.TargetApi
import android.provider.DocumentsContract
import android.os.Environment
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import java.io.*
import java.lang.Exception

/**
 * Created by rasoolmohamed on 22 May 2018
 */
object FileUtils {
    val LOG_TAG = FileUtils::class.java.simpleName
    fun getPathReal(context: Context, uri: Uri): String? {
        return getPathRealOnKitkatAboveVersion(context, uri)
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun getPathRealOnKitkatAboveVersion(context: Context, uri: Uri): String? {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    //return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/" + split[1]
                }
                // TODO handle non-primary volumes

                // DownloadsProvider
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), id.toLong()
                )
                return getDataColumn(context, contentUri, null, null)

                // MediaProvider
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }

            // MediaStore (and general)
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) {
                uri.lastPathSegment
            } else getDataColumn(context, uri, null, null)

            // File
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    /**
     * This Method for getting File Mime Type
     *
     * @param filePath Path of the file
     * @return String Mime Type
     */
    fun getMimeType(filePath: String?): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(filePath)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    fun getMimeType(file: File): String {
        var type: String? = null
        val url = file.toString()
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase())
        }
        if (type == null) {
            type = "image/*" // fallback type. You might set it to */*
        }
        return type
    }

    /**
     * This Method for writing InputStream into File.
     *
     * @param in   InputStream
     * @param file File
     */
    fun writeInputStreamDataToFile(`in`: InputStream, file: File?) {
        try {
            val out: OutputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            while (`in`.read(buf).also { len = it } > 0) {
                out.write(buf, 0, len)
            }
            out.close()
            `in`.close()
        } catch (e: Exception) {
            Log.d(LOG_TAG, e.localizedMessage)
        }
    }

    fun saveBitmap(bitmap: Bitmap, file: File?) {
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        } catch (e: Exception) {
            Log.d(LOG_TAG, e.localizedMessage)
        } finally {
            try {
                out?.close()
            } catch (e: IOException) {
                Log.d(LOG_TAG, e.localizedMessage)
            }
        }
    }

    fun createFile(context: Context, directoryName: String?, fileName: String?): File {
        /*val directory =
            File(Environment.getExternalStorageDirectory(), directoryName)*/
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (!directory!!.exists()) {
            directory.mkdirs()
        }
        return File(directory.path, fileName)
    }

    fun saveImageToInternalStorage(file: File, bitmapImage: Bitmap): String {
        try {
            val fos = FileOutputStream(file)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file.absolutePath
    }
}