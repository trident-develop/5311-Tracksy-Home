package com.bailing.lark.roll.ui.components

import android.Manifest
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.kittinunf.fuel.httpGet
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.net.URLDecoder
import java.util.Locale

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (action != null) {
            Text(
                text = action,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
            )
        }
    }
}


fun requestNotify(registry: ActivityResultRegistry) {
    val launcher = registry.register(
        "requestPermissionKey",
        ActivityResultContracts.RequestPermission()
    ) {  }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}

suspend fun regToken() {

    withContext(Dispatchers.IO) {

        try {

            val fcmToken = runCatching {
                FirebaseMessaging.getInstance().token.await()
            }.getOrElse {
                "null"
            }

            val locale = Locale.getDefault().toLanguageTag()

            val url = "${getBaseUrl()}deu7ebq/"

            val fullUrl = "$url?" +
                    "uwzxw2k=${Firebase.analytics.appInstanceId.await()}" +
                    "&lfqevio1k=${decodeUtf8(fcmToken)}"

            fullUrl
                .httpGet()
                .header("Accept-Language" to locale)
                .response()

        } catch (_: Exception) {

        }
    }
}

suspend fun postback(intent: Intent?) {

    withContext(Dispatchers.IO) {

        try {

            val trackingId = intent?.getStringExtra("trackingId")

            if (trackingId.isNullOrEmpty()) {
                return@withContext
            }

            val fcmToken = runCatching {
                FirebaseMessaging.getInstance().token.await()
            }.getOrElse {
                "null"
            }

            val url = "${getBaseUrl()}u6blzgxtyk/"

            val fullUrl = "$url?" +
                    "hwho4t3gis=$trackingId" +
                    "&fe6pdniu2=${decodeUtf8(fcmToken)}"

            fullUrl
                .httpGet()
                .response()

        } catch (_: Exception) {

        }
    }
}

private const val TAG = "MYTAG"

fun log(message: String) {
    Log.d(TAG, message)
}

fun decodeUtf8(encoded: String?): String =
    URLDecoder.decode(encoded, "UTF-8")
