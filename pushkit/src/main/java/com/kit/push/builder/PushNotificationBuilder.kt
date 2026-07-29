package com.kit.push.builder

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat

/**
 * 通知构建器包装类，支持属性配置与链式构建。
 */
class PushNotificationBuilder(
    private val context: Context,
    val channelId: String
) {
    /** 小图标资源 ID */
    @DrawableRes var smallIcon: Int = android.R.drawable.ic_dialog_info

    /** 通知标题 */
    var title: CharSequence? = null

    /** 通知正文内容 */
    var content: CharSequence? = null

    /** 通知副文本 */
    var subText: CharSequence? = null

    /** 大图标 Bitmap */
    var largeIcon: Bitmap? = null

    /** 点击通知触发的 PendingIntent */
    var contentIntent: PendingIntent? = null

    /** 滑动删除通知触发的 PendingIntent */
    var deleteIntent: PendingIntent? = null

    /** 点击后是否自动清除 */
    var autoCancel: Boolean = true

    /** 是否为常驻通知（无法手动划走） */
    var ongoing: Boolean = false

    /** 通知优先级 (NotificationCompat.PRIORITY_*) */
    var priority: Int = NotificationCompat.PRIORITY_DEFAULT

    /** 通知分类 (NotificationCompat.CATEGORY_*) */
    var category: String? = null

    /** 锁屏可见性 (NotificationCompat.VISIBILITY_*) */
    var visibility: Int = NotificationCompat.VISIBILITY_PUBLIC

    /** 自定义铃声 URI */
    var soundUri: Uri? = null

    /** 自定义震动模式 */
    var vibrationPattern: LongArray? = null

    /** 默认提示音/震动/灯光配置标志 */
    var defaults: Int = 0

    /** 分组 Group Key */
    var groupKey: String? = null

    /** 是否作为分组的 Summary 通知 */
    var isGroupSummary: Boolean = false

    /** 通知展示样式 */
    var style: NotificationStyle = NotificationStyle.Normal

    /** 底部操作按钮列表 */
    val actions: MutableList<NotificationAction> = mutableListOf()

    /**
     * 添加底部快捷操作按钮。
     */
    fun addAction(title: CharSequence, pendingIntent: PendingIntent, @DrawableRes icon: Int = 0) {
        actions.add(NotificationAction(icon, title, pendingIntent))
    }

    /**
     * 构建生成系统原生 [Notification] 对象。
     */
    fun build(): Notification {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setContentText(content)
            .setSubText(subText)
            .setLargeIcon(largeIcon)
            .setContentIntent(contentIntent)
            .setDeleteIntent(deleteIntent)
            .setAutoCancel(autoCancel)
            .setOngoing(ongoing)
            .setPriority(priority)
            .setVisibility(visibility)

        category?.let { builder.setCategory(it) }
        soundUri?.let { builder.setSound(it) }
        vibrationPattern?.let { builder.setVibrate(it) }
        if (defaults != 0) builder.setDefaults(defaults)

        groupKey?.let { builder.setGroup(it) }
        if (isGroupSummary) builder.setGroupSummary(true)

        // 添加操作按钮
        actions.forEach { action ->
            builder.addAction(action.icon, action.title, action.pendingIntent)
        }

        // 应用特定通知样式
        when (val currentStyle = style) {
            is NotificationStyle.Normal -> { /* 默认标准样式 */ }
            is NotificationStyle.BigText -> {
                builder.setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(currentStyle.bigText)
                        .setSummaryText(currentStyle.summaryText)
                        .setBigContentTitle(currentStyle.bigContentTitle ?: title)
                )
            }
            is NotificationStyle.BigPicture -> {
                builder.setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(currentStyle.picture)
                        .bigLargeIcon(currentStyle.largeIcon)
                        .setSummaryText(currentStyle.summaryText)
                        .setBigContentTitle(currentStyle.bigContentTitle ?: title)
                )
            }
            is NotificationStyle.Inbox -> {
                val inboxStyle = NotificationCompat.InboxStyle()
                    .setSummaryText(currentStyle.summaryText)
                    .setBigContentTitle(currentStyle.bigContentTitle ?: title)
                currentStyle.lines.forEach { inboxStyle.addLine(it) }
                builder.setStyle(inboxStyle)
            }
            is NotificationStyle.Progress -> {
                builder.setProgress(
                    currentStyle.max,
                    currentStyle.progress,
                    currentStyle.indeterminate
                )
            }
        }

        return builder.build()
    }
}
