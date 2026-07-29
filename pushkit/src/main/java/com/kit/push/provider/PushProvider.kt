package com.kit.push.provider

import android.content.Context

/**
 * 厂商推送通道（FCM、华为、小米、OPPO、vivo、荣耀等）统一抽象接口。
 */
interface PushProvider {
    /**
     * 通道唯一标识名称（例如 "FCM", "HUAWEI", "XIAOMI"）。
     */
    val providerName: String

    /**
     * 注册或初始化推送通道 SDK。
     */
    fun register(context: Context, tokenListener: PushTokenListener? = null)

    /**
     * 注销推送通道 SDK。
     */
    fun unregister(context: Context)

    /**
     * 获取当前推送通道的 Token。
     */
    fun getToken(context: Context): String?

    /**
     * 设置推送消息监听器。
     */
    fun setMessageListener(listener: PushMessageListener?)
}
