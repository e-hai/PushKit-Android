package com.kit.push.channel

import android.app.NotificationManager
import android.net.Uri

/**
 * 通知渠道 (Notification Channel) 配置数据类。
 *
 * @property id 渠道的唯一标识符 ID（例如 "channel_general"）
 * @property name 渠道展示名称，显示在系统设置的通知配置界面中
 * @property description 渠道功能描述说明（可选），帮助用户了解该渠道的用途
 * @property importance 渠道重要性/优先级，默认值为 [NotificationManager.IMPORTANCE_DEFAULT] (3)
 * @property groupId 渠道所属的渠道组 ID（可选）
 * @property soundUri 自定义渠道提示音 URI（可选），为 null 时使用系统默认提示音
 * @property enableVibration 是否开启通知震动，默认 true
 * @property vibrationPattern 自定义震动节奏模式数组（可选），例如 longArrayOf(0, 250, 250, 250)
 * @property enableLights 是否开启通知指示灯/呼吸灯，默认 true
 * @property lightColor 通知指示灯/呼吸灯颜色（可选，例如 Color.RED，默认 0 不特别指定）
 * @property lockScreenVisibility 锁屏可见性等级，默认 [android.app.Notification.VISIBILITY_PUBLIC]
 * @property showBadge 是否在桌面应用图标上显示通知角标，默认 true
 */
data class NotificationChannelConfig(
    /** 渠道的唯一标识符 ID */
    val id: String,

    /** 渠道展示名称 */
    val name: CharSequence,

    /** 渠道功能描述说明 */
    val description: String? = null,

    /** 渠道重要性/优先级等级 */
    val importance: Int = NotificationManager.IMPORTANCE_DEFAULT,

    /** 渠道所属的渠道组 ID */
    val groupId: String? = null,

    /** 自定义渠道提示音 URI */
    val soundUri: Uri? = null,

    /** 是否开启通知震动 */
    val enableVibration: Boolean = true,

    /** 自定义震动节奏模式数组 */
    val vibrationPattern: LongArray? = null,

    /** 是否开启通知指示灯/呼吸灯 */
    val enableLights: Boolean = true,

    /** 通知指示灯/呼吸灯颜色 */
    val lightColor: Int = 0,

    /** 锁屏可见性等级 */
    val lockScreenVisibility: Int = android.app.Notification.VISIBILITY_PUBLIC,

    /** 是否显示桌面图标通知角标 */
    val showBadge: Boolean = true
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NotificationChannelConfig

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (importance != other.importance) return false
        if (groupId != other.groupId) return false
        if (soundUri != other.soundUri) return false
        if (enableVibration != other.enableVibration) return false
        if (vibrationPattern != null) {
            if (other.vibrationPattern == null) return false
            if (!vibrationPattern.contentEquals(other.vibrationPattern)) return false
        } else if (other.vibrationPattern != null) return false
        if (enableLights != other.enableLights) return false
        if (lightColor != other.lightColor) return false
        if (lockScreenVisibility != other.lockScreenVisibility) return false
        if (showBadge != other.showBadge) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + importance
        result = 31 * result + (groupId?.hashCode() ?: 0)
        result = 31 * result + (soundUri?.hashCode() ?: 0)
        result = 31 * result + enableVibration.hashCode()
        result = 31 * result + (vibrationPattern?.contentHashCode() ?: 0)
        result = 31 * result + enableLights.hashCode()
        result = 31 * result + lightColor
        result = 31 * result + lockScreenVisibility
        result = 31 * result + showBadge.hashCode()
        return result
    }
}

/**
 * 通知渠道组 (Notification Channel Group) 配置数据类。
 *
 * @property id 渠道组的唯一标识符 ID
 * @property name 渠道组展示名称
 * @property description 渠道组功能描述说明（可选）
 */
data class NotificationChannelGroupConfig(
    /** 渠道组的唯一标识符 ID */
    val id: String,

    /** 渠道组展示名称 */
    val name: CharSequence,

    /** 渠道组功能描述说明 */
    val description: String? = null
)
