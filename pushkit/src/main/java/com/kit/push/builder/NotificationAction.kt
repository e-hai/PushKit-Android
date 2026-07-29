package com.kit.push.builder

import android.app.PendingIntent

/**
 * 通知底部快捷操作按钮封装数据类。
 */
data class NotificationAction(
    val icon: Int = 0,
    val title: CharSequence,
    val pendingIntent: PendingIntent
)
