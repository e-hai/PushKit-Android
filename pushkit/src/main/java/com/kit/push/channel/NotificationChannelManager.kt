package com.kit.push.channel

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.os.Build

/**
 * 通知渠道及渠道组管理者类。
 */
class NotificationChannelManager(private val context: Context) {

    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    /**
     * 创建单条通知渠道 (适用于 Android 8.0+ / API 26+)。
     */
    fun createChannel(config: NotificationChannelConfig) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(config.id, config.name, config.importance).apply {
            config.description?.let { description = it }
            config.groupId?.let { group = it }
            setShowBadge(config.showBadge)
            lockscreenVisibility = config.lockScreenVisibility

            if (config.soundUri != null) {
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
                setSound(config.soundUri, audioAttributes)
            }

            enableVibration(config.enableVibration)
            config.vibrationPattern?.let { vibrationPattern = it }

            enableLights(config.enableLights)
            if (config.lightColor != 0) {
                lightColor = config.lightColor
            }
        }

        notificationManager.createNotificationChannel(channel)
    }

    /**
     * 批量创建通知渠道。
     */
    fun createChannels(configs: List<NotificationChannelConfig>) {
        configs.forEach { createChannel(it) }
    }

    /**
     * 创建通知渠道组 (适用于 Android 8.0+ / API 26+)。
     */
    fun createChannelGroup(groupConfig: NotificationChannelGroupConfig) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val group = NotificationChannelGroup(groupConfig.id, groupConfig.name)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            groupConfig.description?.let { group.description = it }
        }
        notificationManager.createNotificationChannelGroup(group)
    }

    /**
     * 根据 Channel ID 删除通知渠道。
     */
    fun deleteChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.deleteNotificationChannel(channelId)
        }
    }

    /**
     * 根据 Group ID 删除通知渠道组。
     */
    fun deleteChannelGroup(groupId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.deleteNotificationChannelGroup(groupId)
        }
    }

    /**
     * 获取指定 ID 的通知渠道对象。
     */
    fun getChannel(channelId: String): NotificationChannel? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.getNotificationChannel(channelId)
        } else {
            null
        }
    }

    /**
     * 检查指定通知渠道是否存在且已被用户启用。
     */
    fun isChannelEnabled(channelId: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return true
        val channel = getChannel(channelId) ?: return false
        return channel.importance != NotificationManager.IMPORTANCE_NONE
    }
}
