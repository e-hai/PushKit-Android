package com.kit.push.builder

import android.graphics.Bitmap

/**
 * 通知展示样式密封接口。
 */
sealed interface NotificationStyle {
    /**
     * 普通默认样式。
     */
    object Normal : NotificationStyle

    /**
     * 大文本样式 (BigTextStyle)。
     */
    data class BigText(
        val bigText: String,
        val summaryText: String? = null,
        val bigContentTitle: String? = null
    ) : NotificationStyle

    /**
     * 大图样式 (BigPictureStyle)。
     */
    data class BigPicture(
        val picture: Bitmap,
        val largeIcon: Bitmap? = null,
        val summaryText: String? = null,
        val bigContentTitle: String? = null
    ) : NotificationStyle

    /**
     * 收件箱/多行列表样式 (InboxStyle)。
     */
    data class Inbox(
        val lines: List<String>,
        val summaryText: String? = null,
        val bigContentTitle: String? = null
    ) : NotificationStyle

    /**
     * 进度条样式 (Progress)。
     */
    data class Progress(
        val max: Int,
        val progress: Int,
        val indeterminate: Boolean = false
    ) : NotificationStyle
}
