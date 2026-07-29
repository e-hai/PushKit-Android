package com.kit.push.provider.fcm

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import com.kit.push.provider.PushMessageListener
import com.kit.push.provider.PushProvider
import com.kit.push.provider.PushTokenListener

/**
 * FCM (Firebase Cloud Messaging) 推送通道实现类。
 */
class FcmPushProvider : PushProvider {

    override val providerName: String = PROVIDER_NAME

    companion object {
        const val PROVIDER_NAME = "FCM"
        internal var tokenListener: PushTokenListener? = null
        internal var messageListener: PushMessageListener? = null
    }

    override fun register(context: Context, tokenListener: PushTokenListener?) {
        FcmPushProvider.tokenListener = tokenListener
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.let { token ->
                        tokenListener?.onTokenReceived(token, PROVIDER_NAME)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun unregister(context: Context) {
        try {
            FirebaseMessaging.getInstance().deleteToken()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getToken(context: Context): String? {
        return try {
            FirebaseMessaging.getInstance().token.result
        } catch (e: Exception) {
            null
        }
    }

    override fun setMessageListener(listener: PushMessageListener?) {
        FcmPushProvider.messageListener = listener
    }
}
