package com.kit.push

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.kit.push.channel.NotificationChannelConfig
import com.kit.push.channel.NotificationChannelGroupConfig
import com.kit.push.channel.NotificationChannelManager
import com.kit.push.permission.NotificationPermissionHelper
import com.kit.push.provider.PushProvider

/**
 * PushKit 核心管理门面单例类，提供通知发送、渠道配置、权限判断及多厂商 SDK 注册功能。
 */
class PushKitManager private constructor(context: Context) {

    private val appContext: Context = context.applicationContext
    val channelManager: NotificationChannelManager by lazy {
        NotificationChannelManager(appContext)
    }

    private val notificationManagerCompat: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(appContext)
    }

    private val providers = mutableMapOf<String, PushProvider>()

    companion object {
        @Volatile
        private var INSTANCE: PushKitManager? = null

        /**
         * 获取 PushKitManager 单例实例。
         */
        fun getInstance(context: Context): PushKitManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PushKitManager(context).also { INSTANCE = it }
            }
        }

        /**
         * 初始化 PushKitManager 辅助入口。
         */
        fun init(context: Context): PushKitManager {
            return getInstance(context)
        }
    }

    // --- 通知渠道管理 ---

    /**
     * 创建通知渠道。
     */
    fun createChannel(config: NotificationChannelConfig): PushKitManager {
        channelManager.createChannel(config)
        return this
    }

    /**
     * 创建通知渠道组。
     */
    fun createChannelGroup(groupConfig: NotificationChannelGroupConfig): PushKitManager {
        channelManager.createChannelGroup(groupConfig)
        return this
    }

    /**
     * 删除通知渠道。
     */
    fun deleteChannel(channelId: String): PushKitManager {
        channelManager.deleteChannel(channelId)
        return this
    }

    // --- 权限与设置 ---

    /**
     * 检查当前应用是否有通知发送权限（兼顾 Android 13+ 运行时权限与全局开关）。
     */
    fun hasNotificationPermission(): Boolean {
        return NotificationPermissionHelper.hasNotificationPermission(appContext)
    }

    /**
     * 检查当前应用通知全局开关状态。
     */
    fun areNotificationsEnabled(): Boolean {
        return NotificationPermissionHelper.areNotificationsEnabled(appContext)
    }

    /**
     * 打开应用系统通知设置页面。
     */
    fun openNotificationSettings() {
        NotificationPermissionHelper.openNotificationSettings(appContext)
    }

    /**
     * 打开指定 Channel 的系统通知设置页面。
     */
    fun openChannelSettings(channelId: String) {
        NotificationPermissionHelper.openChannelSettings(appContext, channelId)
    }

    // --- 通知发送与取消 ---

    /**
     * 发送系统通知。
     */
    fun post(id: Int, tag: String? = null, notification: Notification) {
        try {
            notificationManagerCompat.notify(tag, id, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    /**
     * 根据 ID 和 Tag 取消通知。
     */
    fun cancel(id: Int, tag: String? = null) {
        notificationManagerCompat.cancel(tag, id)
    }

    /**
     * 清除当前应用发出的所有通知。
     */
    fun cancelAll() {
        notificationManagerCompat.cancelAll()
    }

    // --- 厂商推送 SDK 扩展 ---

    /**
     * 注册厂商推送 SDK 实现。
     */
    fun registerProvider(provider: PushProvider): PushKitManager {
        providers[provider.providerName] = provider
        provider.register(appContext)
        return this
    }

    /**
     * 根据 Provider 名称获取绑定的推送实例。
     */
    fun getProvider(providerName: String): PushProvider? {
        return providers[providerName]
    }
}
