package com.kit.push.builder

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.kit.push.PushKitManager

/**
 * Kotlin DSL 扩展函数：构建 Notification 对象。
 */
fun Context.buildNotification(
    channelId: String,
    block: PushNotificationBuilder.() -> Unit
): Notification {
    val builder = PushNotificationBuilder(this, channelId)
    builder.block()
    return builder.build()
}

/**
 * Kotlin DSL 扩展函数：构建并直接发送通知。
 */
fun Context.notifyPush(
    id: Int,
    channelId: String,
    tag: String? = null,
    block: PushNotificationBuilder.() -> Unit
) {
    val notification = buildNotification(channelId, block)
    PushKitManager.getInstance(this).post(id, tag, notification)
}

/**
 * 构造 Activity PendingIntent 的辅助函数，自动兼容 Android 12+ (API 31+) FLAG_IMMUTABLE 标志位。
 */
fun Context.createPendingActivityIntent(
    requestCode: Int,
    intent: Intent,
    flags: Int = PendingIntent.FLAG_UPDATE_CURRENT,
    isMutable: Boolean = false
): PendingIntent {
    val mutableFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (isMutable) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_IMMUTABLE
    } else 0

    return PendingIntent.getActivity(
        this,
        requestCode,
        intent,
        flags or mutableFlag
    )
}

/**
 * 构造 Broadcast PendingIntent 的辅助函数，自动兼容 Android 12+ (API 31+) FLAG_IMMUTABLE 标志位。
 */
fun Context.createPendingBroadcastIntent(
    requestCode: Int,
    intent: Intent,
    flags: Int = PendingIntent.FLAG_UPDATE_CURRENT,
    isMutable: Boolean = false
): PendingIntent {
    val mutableFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (isMutable) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_IMMUTABLE
    } else 0

    return PendingIntent.getBroadcast(
        this,
        requestCode,
        intent,
        flags or mutableFlag
    )
}
