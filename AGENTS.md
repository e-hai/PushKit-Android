# AGENTS.md - Developer & Agent Guide for PushKit (Android)

本文档面向维护、重构与扩展 `PushKit-Android` 代码库的开发者与 AI Agent，明确架构边界、编码规范与发版步骤。

---

## 📌 项目概述

`PushKit-Android` 是一套面向 Android 生态的纯 Kotlin 跨厂商通知与推送基础库，提供高可用的通知渠道管理、Android 13+ 动态权限、极简 Kotlin DSL 与解耦的推送 Provider。

---

## 📂 模块划分

采用多模块 Gradle 结构组织：

1. **`:pushkit`**（核心库模块，`com.kit.push.library`）：
   - `PushKitManager.kt`: 库唯一门面单例，统筹全局调度。
   - `channel/`: 通知渠道与渠道组配置及管理 (`NotificationChannelConfig`, `NotificationChannelManager`)。
   - `permission/`: 运行时权限检测与设置跳转 (`NotificationPermissionHelper`)。
   - `builder/`: 链式与 DSL 构建器 (`PushNotificationBuilder`, `NotificationDsl`, `NotificationStyle`)。
   - `provider/`: 厂商推送统一接口协议与跨端数据模型 (`PushProvider`, `PushMessage`, `PushTokenListener`)。
   - `provider/fcm/`: Google 官方 FCM 接入实现 (`FcmPushProvider`, `PushKitFirebaseMessagingService`)。

2. **`:sample`**（Demo 演示应用，`com.kit.push`）：
   - 基于 Jetpack Compose 打造的完整体验界面，提供权限请求、各种样式通知演示与清除功能。

---

## 📐 代码规范与设计准则

1. **中文文档完整性**：
   - 公开暴露的类、扩展函数、配置属性**一律使用详细中文 KDoc 注释**。
2. **系统版本兼容规范**：
   - `minSdk = 24`, `compileSdk = 36`, `targetSdk = 36`。
   - 渠道逻辑受 `Build.VERSION.SDK_INT >= Build.VERSION_CODES.O` (API 26) 保护。
   - PendingIntent 标志位受 `Build.VERSION.SDK_INT >= Build.VERSION_CODES.S` (API 31) 保护。
   - 动态权限受 `Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU` (API 33) 保护。
3. **架构解耦原则**：
   - 任何第三方/手机厂商推送通道必须实现 `PushProvider` 接口，不得向外部泄漏厂商特有数据类型，所有消息必须转换为 `PushMessage`。

---

## 🛠️ 编译、发布与发版流程

```bash
# 1. 编译验证
./gradlew :pushkit:assembleRelease :sample:assembleDebug

# 2. 本地 Maven 发布测试
./gradlew :pushkit:publishToMavenLocal

# 3. 发布新版本 Tag (以 1.0.0 为例)
git tag 1.0.0
git push origin 1.0.0
```
