# AGENTS.md - Developer & Agent Guide for PushKit

本文档为 AI Agent 及开发者维护和扩展 `PushKit` 代码库提供项目架构说明、设计规范及开发指南。

---

## 📌 项目概述

`PushKit` 是一个纯 Kotlin 开发的 Android 通知与推送封装库，目标是为 Android 应用提供简洁、高可扩展的通知与多厂商推送处理机制。

---

## 📂 模块划分

项目采用多模块 Gradle 结构：

1. **`:pushkit`**（库模块，`com.kit.push.library`）：
   - `PushKitManager.kt`: 单例门面，暴露核心 API。
   - `channel/`: 通知渠道与渠道组配置 (`NotificationChannelConfig`, `NotificationChannelManager`)。
   - `permission/`: 运行时权限与系统设置辅助 (`NotificationPermissionHelper`)。
   - `builder/`: 链式与 DSL 构建器 (`PushNotificationBuilder`, `NotificationDsl`, `NotificationStyle`)。
   - `provider/`: 厂商推送统一接口 (`PushProvider`, `PushMessage`, `PushTokenListener`)。

2. **`:sample`**（示例应用，`com.kit.push`）：
   - Jetpack Compose 实现的 UI 示例，展示权限请求、通道创建、各类型通知发送与清除功能。

---

## 📐 代码规范与设计原则

1. **语言与注释**：
   - 全面使用 Kotlin 语言与 Kotlin Idiomatic 写法。
   - 代码中的类、方法、数据结构定义**一律使用中文注释**。
2. **兼容性与版本**：
   - `minSdk = 24`，`compileSdk = 36`，`targetSdk = 36`。
   - 适配 Android 8.0+ (API 26) `NotificationChannel`。
   - 适配 Android 12+ (API 31) `PendingIntent` 标志位 (`FLAG_IMMUTABLE`)。
   - 适配 Android 13+ (API 33) `POST_NOTIFICATIONS` 动态权限。
3. **API 设计**：
   - 优先提供简洁的 Kotlin DSL (例如 `Context.notifyPush { ... }`)。
   - 核心管理器使用单例模式，内部做线程安全控制与 Application Context 绑定。
4. **扩展性**：
   - 保持 `PushProvider` 抽象接口独立，后续新增 FCM 或国内厂商推送 SDK 时不破坏已有 API。

---

## 🛠️ 常用构建与验证命令

```bash
# 1. 编译 pushkit 库模块 Release 包
./gradlew :pushkit:assembleRelease

# 2. 编译 sample 示例应用 Debug 包
./gradlew :sample:assembleDebug

# 3. 完整构建与单元测试
./gradlew check assemble
```

---

## ⚠️ 注意事项

- 修改 `:pushkit` 模块的公开 API 时，需同步更新 `:sample` 中的示例代码及 `README.md`。
- 修改代码注释时，请始终保持中文格式规范。
