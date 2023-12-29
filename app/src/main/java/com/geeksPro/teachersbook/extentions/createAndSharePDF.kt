package com.geeksPro.teachersbook.extentions

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import java.io.File
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Objects

private const val TAG = "Aaviskar"

fun Fragment.createAndSharePDF(container: ViewGroup, group: String, maxSize: Int) {

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentResolver: ContentResolver = requireActivity().contentResolver
            val contentValues = ContentValues()

            val dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss")
            val now = LocalDateTime.now()
            val dateSuffix = "_${dtf.format(now)}"
            Log.d(TAG, dtf.format(now))

            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "$group$dateSuffix.pdf")
            contentValues.put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOCUMENTS + File.separator + "AaviskarFolder"
            )
            val pdfUri =
                contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

            val outst = pdfUri?.let { contentResolver.openOutputStream(it) }!!
            Objects.requireNonNull(outst)

            createPDF(outst, container, maxSize)

            val share = Intent(Intent.ACTION_SEND)
            share.type = "application/pdf"
            share.putExtra(Intent.EXTRA_STREAM, pdfUri)
            startActivity(Intent.createChooser(share, "Share PDF"))
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun createPDF(refOutst: OutputStream, container: ViewGroup, maxSize: Int) {
    val document = PdfDocument()

    val pdfPageWidth = 4000 // Set a higher width resolution
    val pdfPageHeight = 4500 // Set a higher height resolution

    val pageInfo = PdfDocument.PageInfo.Builder(pdfPageWidth, pdfPageHeight, 1).create()
    val page = document.startPage(pageInfo)

    val canvas = page.canvas
    canvas.drawColor(Color.WHITE)

    val paint = Paint().apply {
        color = Color.BLUE
        textSize = 20f
    }

    val resizedImage = getResizedBitmap(captureScreenShot(container), maxSize)

    val cordXImage = (pdfPageWidth - resizedImage.width) / 2f
    val cordYImage = (pdfPageHeight - resizedImage.height) / 2f

    canvas.drawBitmap(resizedImage, cordXImage, cordYImage, paint)

    document.finishPage(page)

    try {
        document.writeTo(refOutst)
        document.close()

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun captureScreenShot(container: ViewGroup): Bitmap {
    val returnBitmap = Bitmap.createBitmap(
        container.width,  // Use original width
        container.height, // Use original height
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(returnBitmap)
    container.background?.draw(canvas) ?: canvas.drawColor(Color.WHITE)
    container.draw(canvas)
    return returnBitmap
}

private fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
    val width = image.width
    val height = image.height
    val scale = maxSize.toFloat() / Math.max(width, height)

    val newWidth = (width * scale).toInt()
    val newHeight = (height * scale).toInt()

    return Bitmap.createScaledBitmap(image, newWidth, newHeight, true)
}

