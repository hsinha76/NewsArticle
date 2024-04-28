package com.hsdroid.newsarticle.utils

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.hsdroid.newsarticle.data.models.Article
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class Helper {
    companion object {
        const val BASE_URL =
            "https://candidate-test-data-moengage.s3.amazonaws.com/Android/news-api-feed/staticResponse.json"

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        fun isPostNotificationPermissionGranted(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun formatDateTime(input: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(input)
            return outputFormat.format(date)
        }

        fun copyToClipboard(context: Context, link: String) {
            val clipboardManager =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("article-link", link)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(context, "Link copied to clipboard", Toast.LENGTH_LONG).show()
        }

        fun setViewsVisibility(visibility: Int, vararg views: View) {
            views.forEach { it.visibility = visibility }
        }

        fun sortList(key: String): Comparator<Article> =
            when (key) {
                "old-to-new" -> compareBy { it.publishedAt }
                "new-to-old" -> compareByDescending { it.publishedAt }
                else -> throw IllegalArgumentException("Invalid key")
            }
    }
}