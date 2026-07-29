package com.kit.push

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kit.push.builder.NotificationStyle
import com.kit.push.builder.createPendingActivityIntent
import com.kit.push.builder.notifyPush
import com.kit.push.channel.NotificationChannelConfig
import com.kit.push.ui.theme.PushKitTheme

class MainActivity : ComponentActivity() {

    companion object {
        const val CHANNEL_GENERAL = "channel_general"
        const val CHANNEL_HIGH_IMPORTANCE = "channel_high_importance"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 初始化 PushKit 通知渠道
        initChannels()

        setContent {
            PushKitTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PushKitDemoScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    /**
     * 初始化配置应用的通知渠道。
     */
    private fun initChannels() {
        val pushKit = PushKitManager.getInstance(this)

        // 普通通知渠道
        pushKit.createChannel(
            NotificationChannelConfig(
                id = CHANNEL_GENERAL,
                name = "普通消息通知",
                description = "用于接收日常应用更新与消息提醒"
            )
        )

        // 高优先级通知渠道
        pushKit.createChannel(
            NotificationChannelConfig(
                id = CHANNEL_HIGH_IMPORTANCE,
                name = "重要紧急提醒",
                description = "用于接收高优先级的紧急消息通知",
                importance = android.app.NotificationManager.IMPORTANCE_HIGH
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PushKitDemoScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val pushKit = remember { PushKitManager.getInstance(context) }

    var hasPermission by remember { mutableStateOf(pushKit.hasNotificationPermission()) }

    // Android 13+ 运行时权限请求启动器
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasPermission = isGranted
            val msg = if (isGranted) "通知权限已授予！" else "通知权限被拒绝！"
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "PushKit 通知封装库演示",
            style = MaterialTheme.typography.headlineMedium
        )

        // 权限状态卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (hasPermission) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "通知权限状态",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (hasPermission) "通知正常启用" else "需申请权限 / 全局通知已关闭",
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Button(
                            onClick = {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        ) {
                            Text("申请通知权限")
                        }
                    }
                    OutlinedButton(
                        onClick = {
                            pushKit.openNotificationSettings()
                        }
                    ) {
                        Text("打开应用通知设置")
                    }
                }
            }
        }

        // 通知发送功能示范
        Text(
            text = "发送测试通知",
            style = MaterialTheme.typography.titleLarge
        )

        // 1. 发送普通通知
        Button(
            onClick = {
                val intent = Intent(context, MainActivity::class.java)
                val pendingIntent = context.createPendingActivityIntent(101, intent)

                context.notifyPush(
                    id = 1001,
                    channelId = MainActivity.CHANNEL_GENERAL
                ) {
                    title = "基础通知"
                    content = "这是一条通过 PushKit Kotlin DSL 发送的基础通知。"
                    contentIntent = pendingIntent
                    autoCancel = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("发送基础通知")
        }

        // 2. 发送大文本通知
        Button(
            onClick = {
                val intent = Intent(context, MainActivity::class.java)
                val pendingIntent = context.createPendingActivityIntent(102, intent)

                context.notifyPush(
                    id = 1002,
                    channelId = MainActivity.CHANNEL_HIGH_IMPORTANCE
                ) {
                    title = "大文本样式通知"
                    content = "展开下拉查看详细内容..."
                    contentIntent = pendingIntent
                    style = NotificationStyle.BigText(
                        bigText = "PushKit 封装库通过简洁的 Kotlin DSL 极大地简化了 Android 通知、Channel 渠道配置及 Android 13+ 动态权限处理。您可以轻松定制大文本、大图及进度条等丰富样式！",
                        summaryText = "PushKit 消息更新"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("发送大文本样式通知")
        }

        // 3. 发送大图通知
        Button(
            onClick = {
                val sampleBitmap = createSampleBitmap()
                context.notifyPush(
                    id = 1003,
                    channelId = MainActivity.CHANNEL_GENERAL
                ) {
                    title = "大图样式通知"
                    content = "点击或展开查看示例大图！"
                    style = NotificationStyle.BigPicture(
                        picture = sampleBitmap,
                        summaryText = "图片预览"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("发送大图样式通知")
        }

        // 4. 发送进度条通知
        Button(
            onClick = {
                context.notifyPush(
                    id = 1004,
                    channelId = MainActivity.CHANNEL_GENERAL
                ) {
                    title = "文件正在下载中"
                    content = "正在下载更新包..."
                    style = NotificationStyle.Progress(
                        max = 100,
                        progress = 65,
                        indeterminate = false
                    )
                    ongoing = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("发送进度条通知 (65%)")
        }

        // 5. 清除所有通知
        OutlinedButton(
            onClick = {
                pushKit.cancelAll()
                Toast.makeText(context, "已清除所有通知！", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("清除所有通知")
        }
    }
}

/**
 * 辅助函数：生成演示使用的大图 Bitmap。
 */
private fun createSampleBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(400, 200, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawColor(Color.rgb(33, 150, 243)) // 蓝色背景

    val paint = Paint().apply {
        color = Color.WHITE
        textSize = 36f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    canvas.drawText("PushKit 示例大图", 200f, 110f, paint)
    return bitmap
}