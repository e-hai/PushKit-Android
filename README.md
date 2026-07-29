# PushKit - Android 通知与推送封装库

`PushKit` 是一套轻量、易用且易扩展的 Android 通知与推送封装库，采用纯 Kotlin 开发。它为 Android 应用提供了简洁的 **Kotlin DSL** 通知构建器、**Android 8.0+ 通知渠道管理**、**Android 13+ 动态通知权限辅助** 以及 **多厂商推送（FCM / 华为 / 小米等）统一抽象接口**。

---

## 🌟 核心特性

- 🚀 **Kotlin DSL 封装**：通过 `context.notifyPush { ... }` 极简语法创建和发送通知。
- 📢 **通知渠道管理 (`NotificationChannelManager`)**：一键创建、更新、删除通知渠道及渠道组，灵活配置优先级、声音、震动、灯光及锁屏可见性。
- 🛡️ **Android 13+ 权限适配 (`NotificationPermissionHelper`)**：轻松检测 `POST_NOTIFICATIONS` 运行时权限与全局通知开关，提供跳转系统设置引导。
- 🔒 **Android 12+ 安全适配**：内部自动处理 `PendingIntent` 的 `FLAG_IMMUTABLE` / `FLAG_MUTABLE` 标志位。
- 🎨 **丰富样式支持**：
  - 普通通知 (`Normal`)
  - 长文本样式 (`BigText`)
  - 大图样式 (`BigPicture`)
  - 列表/收件箱样式 (`Inbox`)
  - 确定/不确定进度条通知 (`Progress`)
- 🔌 **多厂商推送抽象 (`PushProvider`)**：定义统一接入接口，方便无缝扩展 FCM、华为、小米、OPPO、vivo、荣耀等厂商 Push SDK。
- 📱 **完整 Compose 示例**：包含基于 Jetpack Compose 打造的示例应用 (`:sample`)，便于交互测试。

---

## 🛠️ 项目结构

```
PushKit/
├── pushkit/                    # PushKit 核心封装库模块
│   └── src/main/java/com/kit/push/
│       ├── PushKitManager.kt             # 核心单例门面类
│       ├── channel/                      # 通知渠道与渠道组配置及管理者
│       ├── permission/                   # 通知权限与系统设置辅助
│       ├── builder/                      # Kotlin DSL、NotificationBuilder及样式
│       └── provider/                     # 厂商推送 SDK 统一抽象接口
├── sample/                     # Jetpack Compose 演示应用模块
└── build.gradle.kts
```

---

## 快速上手

### 1. 依赖配置 (JitPack)

在根目录 `settings.gradle.kts` 的 `dependencyResolutionManagement` 中添加 JitPack 仓库：

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

在主模块 `build.gradle.kts` 中引入依赖（替换 `Tag` 为发布版本，例如 `1.0.0`）：

```kotlin
dependencies {
    implementation("com.github.username:PushKit:Tag")
}
```

> **本地源码依赖说明**：若作为本地子模块引入，使用 `implementation(project(":pushkit"))` 即可。

---

### 2. 初始化与通知渠道配置

建议在 `Application` 或 `MainActivity` 中初始化 notification channels：

```kotlin
import com.kit.push.PushKitManager
import com.kit.push.channel.NotificationChannelConfig

val pushKit = PushKitManager.getInstance(context)

// 创建通用通知渠道
pushKit.createChannel(
    NotificationChannelConfig(
        id = "channel_general",
        name = "普通消息通知",
        description = "用于接收日常应用更新与消息提醒"
    )
)
```

---

### 3. 发送通知 (使用 Kotlin DSL)

#### 发送普通通知
```kotlin
import com.kit.push.builder.notifyPush
import com.kit.push.builder.createPendingActivityIntent

val intent = Intent(context, MainActivity::class.java)
val pendingIntent = context.createPendingActivityIntent(requestCode = 101, intent = intent)

context.notifyPush(
    id = 1001,
    channelId = "channel_general"
) {
    title = "新消息提醒"
    content = "您收到一条新的系统提醒消息"
    contentIntent = pendingIntent
    autoCancel = true
}
```

#### 发送大文本通知
```kotlin
context.notifyPush(
    id = 1002,
    channelId = "channel_general"
) {
    title = "长文本通知"
    content = "展开查看详细内容..."
    style = NotificationStyle.BigText(
        bigText = "这是非常详细的长文本内容，展开通知栏即可完整查看。",
        summaryText = "系统通知"
    )
}
```

#### 发送大图通知
```kotlin
context.notifyPush(
    id = 1003,
    channelId = "channel_general"
) {
    title = "图片消息"
    content = "点击或下拉展开图片"
    style = NotificationStyle.BigPicture(
        picture = bitmap,
        summaryText = "大图预览"
    )
}
```

#### 发送进度条通知
```kotlin
context.notifyPush(
    id = 1004,
    channelId = "channel_general"
) {
    title = "文件下载中"
    content = "已下载 65%"
    style = NotificationStyle.Progress(
        max = 100,
        progress = 65,
        indeterminate = false
    )
    ongoing = true // 设置为常驻通知
}
```

---

### 4. 权限与系统设置管理

```kotlin
// 检查是否有发送通知权限（兼容 Android 13+）
val hasPermission = PushKitManager.getInstance(context).hasNotificationPermission()

// 一键跳转到应用的系统通知设置页
PushKitManager.getInstance(context).openNotificationSettings()

// 清除所有通知
PushKitManager.getInstance(context).cancelAll()
```

---

### 5. 注册 FCM 推送通道

```kotlin
import com.kit.push.provider.fcm.FcmPushProvider

// 1. 创建 FCM 通道并绑定 Token 与 消息回调
val fcmProvider = FcmPushProvider()

fcmProvider.setMessageListener { message, providerName ->
    Log.d("PushKit", "收到来自 $providerName 的消息: ${message.title} - ${message.content}")
}

// 2. 注册通道并获取 Token
PushKitManager.getInstance(context).registerProvider(fcmProvider)
```

---

### 6. 💡 厂商推送通知展示机制说明

厂商推送（FCM / 华为 / 小米 / OPPO / vivo 等）消息能否自动弹出通知，取决于消息类型：

1. **通知消息 (Notification Message / 系统展示消息)**：
   - 消息体中带有系统标准的 `title` 和 `body` 字段。
   - **由厂商系统直接弹出通知**，即使 App 进程被杀死也能弹出。点击后自动拉起 App。

2. **透传消息 (Data Message / 静默消息)**：
   - 消息体中仅有 `data` 自定义数据负载，**系统不会自动弹通知**。
   - 消息会触发 `onMessageReceived` 回调，由开发者手动调用 `context.notifyPush { ... }` 动态创建并弹出通知：

```kotlin
fcmProvider.setMessageListener { message, providerName ->
    // 当收到透传数据消息时，使用 PushKit 动态弹出系统通知
    context.notifyPush(id = 1005, channelId = "channel_general") {
        title = message.title ?: "透传通知"
        content = message.content ?: "收到一条后台数据推送到达"
        autoCancel = true
    }
}
```

---

## ⚙️ 编译与验证

使用系统预置的 Gradle Wrapper 运行编译：

```bash
# 编译 PushKit 库模块与 Sample 应用
./gradlew :pushkit:assembleRelease :sample:assembleDebug
```

---

## 📄 License

本项目采用 MIT 许可证，详情参见 LICENSE 文件。
