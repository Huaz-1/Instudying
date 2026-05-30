package com.test.easyget.ui.note

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.TextDecrease
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val presetColors = listOf(
    Color.Black, Color(0xFFE53935), Color(0xFF43A047),
    Color(0xFF1E88E5), Color(0xFF8E24AA), Color(0xFFFF6F00),
    Color.White
)

@Composable
fun NoteScreen(
    noteId: Long? = null,
    viewModel: NoteViewModel = viewModel(),
    onSaved: () -> Unit = {}
) {
    val content by viewModel.content.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState()
    val fontColor by viewModel.fontColor.collectAsState()
    val isBold by viewModel.isBold.collectAsState()
    val isItalic by viewModel.isItalic.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showNewDialog by remember { mutableStateOf(false) }

    LaunchedEffect(noteId) {
        if (noteId != null && noteId > 0) {
            viewModel.loadNote(noteId)
        }
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            snackbarHostState.showSnackbar("已保存")
            viewModel.resetSaveSuccess()
        }
    }

    if (showNewDialog) {
        AlertDialog(
            onDismissRequest = { showNewDialog = false },
            title = { Text("新建便签") },
            text = { Text("当前内容尚未保存，是否保存后再新建？") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.saveAndReset()
                    showNewDialog = false
                }) { Text("保存") }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.resetToNew()
                    showNewDialog = false
                }) { Text("不保存") }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 紧凑自定义工具栏
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(12.dp))
                Text(
                    if (noteId != null) "编辑便签" else "新建便签",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    if (content.isNotEmpty()) showNewDialog = true
                    else viewModel.resetToNew()
                }) {
                    Icon(Icons.Default.NoteAdd, contentDescription = "新建", tint = Color(0xFF616161))
                }
                IconButton(onClick = { viewModel.saveNote(); onSaved() }) {
                    Icon(Icons.Default.Check, contentDescription = "保存", tint = Color(0xFF43A047))
                }
            }

            FontToolbar(
                fontSize = fontSize,
                isBold = isBold,
                isItalic = isItalic,
                onIncreaseFont = viewModel::increaseFontSize,
                onDecreaseFont = viewModel::decreaseFontSize,
                onToggleBold = viewModel::toggleBold,
                onToggleItalic = viewModel::toggleItalic,
                onColorSelected = viewModel::setFontColor,
                selectedColor = fontColor
            )

            BasicTextField(
                value = content,
                onValueChange = viewModel::updateContent,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                textStyle = TextStyle(
                    fontSize = fontSize.sp,
                    color = fontColor,
                    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                    fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal
                ),
                cursorBrush = SolidColor(fontColor),
                decorationBox = { innerTextField ->
                    if (content.isEmpty()) {
                        Text(
                            "在此输入便签内容...",
                            color = Color.Gray.copy(alpha = 0.5f),
                            fontSize = fontSize.sp
                        )
                    }
                    innerTextField()
                }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun FontToolbar(
    fontSize: Int,
    isBold: Boolean,
    isItalic: Boolean,
    onIncreaseFont: () -> Unit,
    onDecreaseFont: () -> Unit,
    onToggleBold: () -> Unit,
    onToggleItalic: () -> Unit,
    onColorSelected: (Color) -> Unit,
    selectedColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDecreaseFont, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.TextDecrease, contentDescription = "缩小", modifier = Modifier.size(20.dp))
            }
            Text("${fontSize}sp", fontSize = 12.sp, modifier = Modifier.width(36.dp))
            IconButton(onClick = onIncreaseFont, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.TextIncrease, contentDescription = "放大", modifier = Modifier.size(20.dp))
            }

            Spacer(Modifier.width(16.dp))

            IconButton(onClick = onToggleBold, modifier = Modifier.size(36.dp)) {
                Icon(
                    Icons.Default.FormatBold, contentDescription = "加粗",
                    modifier = Modifier.size(20.dp),
                    tint = if (isBold) Color(0xFF1E88E5) else Color.Gray
                )
            }
            IconButton(onClick = onToggleItalic, modifier = Modifier.size(36.dp)) {
                Icon(
                    Icons.Default.FormatItalic, contentDescription = "斜体",
                    modifier = Modifier.size(20.dp),
                    tint = if (isItalic) Color(0xFF1E88E5) else Color.Gray
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            presetColors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(color)
                        .then(
                            if (color == selectedColor) Modifier.border(2.dp, Color.DarkGray, CircleShape)
                            else Modifier.border(1.dp, Color.LightGray, CircleShape)
                        )
                        .clickable { onColorSelected(color) }
                )
                Spacer(Modifier.width(8.dp))
            }
        }
    }
}
