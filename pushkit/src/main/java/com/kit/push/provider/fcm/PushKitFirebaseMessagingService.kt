package com.kit.push.provider.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kit.push.provider.PushMessage

/**
 * PushKit 封装的 Firebase 消息与 Token 接收服务。
 */
class PushKitFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        FcmPushProvider.tokenListener?.onTokenReceived(token, FcmPushProvider.PROVIDER_NAME)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val notification = remoteMessage.notification
        val pushMessage = PushMessage(
            id = remoteMessage.messageId,
            title = notification?.title ?: remoteMessage.data["title"],
            content = notification?.body ?: remoteMessage.data["content"] ?: remoteMessage.data["body"],
            payload = remoteMessage.data,
            extra = emptyMap()
        )

        FcmPushProvider.messageListener?.onMessageReceived(pushMessage, FcmPushProvider.PROVIDER_NAME)
    }
}
