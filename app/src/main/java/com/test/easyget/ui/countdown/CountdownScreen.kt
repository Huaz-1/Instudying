package com.test.easyget.ui.countdown

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CountdownScreen(
    viewModel: CountdownViewModel = viewModel()
) {
    val title by viewModel.title.collectAsState()
    val targetDays by viewModel.targetDays.collectAsState()
    val allCountdowns by viewModel.allCountdowns.collectAsState()
    val activeCountdown by viewModel.activeCountdown.collectAsState()
    val activeRemainingDays by viewModel.activeRemainingDays.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // 工具栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("倒计时", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }

        // 新建倒计时
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("新建倒计时", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = viewModel::updateTitle,
                    label = { Text("标题（可选）") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = targetDays,
                        onValueChange = viewModel::updateTargetDays,
                        label = { Text("天数") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(Modifier.width(12.dp))
                    Button(onClick = { viewModel.startCountdown() }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("开始")
                    }
                }
            }
        }

        // 活跃倒计时卡片（标题 + 剩余天数）
        activeCountdown?.let { countdown ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = countdown.title.ifBlank { "${countdown.targetDays}天" },
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "剩余",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "$activeRemainingDays",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    )
                    Text(
                        text = "天",
                        fontSize = 18.sp,
                        color = Color(0xFF1565C0)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "到期日: ${formatDate(countdown.endTime)}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        // 历史倒计时列表
        if (allCountdowns.isNotEmpty()) {
            Text(
                "全部倒计时",
                modifier = Modifier.padding(start = 12.dp, top = 12.dp, bottom = 4.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp)
            ) {
                items(allCountdowns, key = { it.id }) { countdown ->
                    val remainDays =
                        ((countdown.endTime - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)).toInt()
                            .coerceAtLeast(0)
                    val isExpired = countdown.endTime <= System.currentTimeMillis()
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        colors = if (isExpired)
                            CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                        else
                            CardDefaults.cardColors()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    countdown.title.ifBlank { "${countdown.targetDays}天" },
                                    fontWeight = FontWeight.Medium,
                                    color = if (isExpired) Color.Gray else Color.Black
                                )
                                Text(
                                    if (isExpired) "已到期"
                                    else "目标: ${countdown.targetDays}天  |  剩余: $remainDays 天  |  ${formatDate(countdown.endTime)}",
                                    fontSize = 12.sp,
                                    color = if (isExpired) Color(0xFFE53935) else Color.Gray
                                )
                            }
                            IconButton(onClick = { viewModel.deleteCountdown(countdown) }) {
                                Icon(Icons.Default.Delete, contentDescription = "删除", tint = Color(0xFFE53935))
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date(millis))
}
