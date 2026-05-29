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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val blueColor = Color(0xFF1565C0)

@Composable
fun CountdownScreen(
    viewModel: CountdownViewModel = viewModel()
) {
    val title by viewModel.title.collectAsState()
    val targetDays by viewModel.targetDays.collectAsState()
    val countdowns by viewModel.countdowns.collectAsState()
    val remainingMs by viewModel.remainingMs.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Color(0xFFF5F5F5))
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

        // 活跃倒计时显示（仅显示天数）
        if (remainingMs > 0) {
            val remainingDays = (remainingMs / (24 * 60 * 60 * 1000)).coerceAtLeast(0)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("剩余天数", fontSize = 14.sp, color = Color.Gray)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "$remainingDays",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = blueColor
                    )
                    Text("天", fontSize = 20.sp, color = blueColor)
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.stopTimer() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("停止")
                    }
                }
            }
        }

        // 历史倒计时列表
        if (countdowns.isNotEmpty()) {
            Text(
                "历史记录",
                modifier = Modifier.padding(12.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp)
            ) {
                items(countdowns, key = { it.id }) { countdown ->
                    val remainDays = ((countdown.endTime - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)).coerceAtLeast(0)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "目标: ${countdown.targetDays}天  |  剩余: ${remainDays}天",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            IconButton(onClick = { viewModel.startTimerForExisting(countdown.endTime) }) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "开始计时", tint = Color(0xFF43A047))
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
