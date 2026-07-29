package com.kit.push.provider

/**
 * 通用推送消息数据模型。
 */
data class PushMessage(
    val id: String? = null,
    val title: String? = null,
    val content: String? = null,
    val payload: Map<String, String> = emptyMap(),
    val extra: Map<String, Any> = emptyMap()
)

/**
 * 推送 Token 回调接口。
 */
fun interface PushTokenListener {
    fun onTokenReceived(token: String, providerName: String)
}

/**
 * 推送消息接收回调接口。
 */
fun interface PushMessageListener {
    fun onMessageReceived(message: PushMessage, providerName: String)
}
