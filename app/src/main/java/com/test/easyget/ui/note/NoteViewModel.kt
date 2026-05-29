package com.test.easyget.ui.note

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.test.easyget.data.db.AppDatabase
import com.test.easyget.data.model.Note
import com.test.easyget.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NoteRepository(
        AppDatabase.getInstance(application).noteDao()
    )

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()

    private val _fontSize = MutableStateFlow(16)
    val fontSize: StateFlow<Int> = _fontSize.asStateFlow()

    private val _fontColor = MutableStateFlow(Color.Black)
    val fontColor: StateFlow<Color> = _fontColor.asStateFlow()

    private val _isBold = MutableStateFlow(false)
    val isBold: StateFlow<Boolean> = _isBold.asStateFlow()

    private val _isItalic = MutableStateFlow(false)
    val isItalic: StateFlow<Boolean> = _isItalic.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    // 编辑模式：加载已有便签
    private var editingNoteId: Long? = null

    fun loadNote(noteId: Long) {
        editingNoteId = noteId
        viewModelScope.launch {
            repository.getById(noteId)?.let { note ->
                _content.value = note.content
                _fontSize.value = note.fontSize
                _fontColor.value = Color(note.fontColor)
                _isBold.value = note.isBold
                _isItalic.value = note.isItalic
            }
        }
    }

    fun updateContent(text: String) { _content.value = text }

    fun increaseFontSize() { if (_fontSize.value < 40) _fontSize.value += 2 }

    fun decreaseFontSize() { if (_fontSize.value > 8) _fontSize.value -= 2 }

    fun setFontColor(color: Color) { _fontColor.value = color }

    fun toggleBold() { _isBold.value = !_isBold.value }

    fun toggleItalic() { _isItalic.value = !_isItalic.value }

    fun saveNote() {
        viewModelScope.launch { saveNoteInternal() }
    }

    private suspend fun saveNoteInternal() {
        val note = Note(
            id = editingNoteId ?: 0,
            content = _content.value,
            fontSize = _fontSize.value,
            fontColor = _fontColor.value.toArgb(),
            isBold = _isBold.value,
            isItalic = _isItalic.value,
            updatedAt = System.currentTimeMillis()
        )
        if (editingNoteId != null) {
            repository.update(note)
        } else {
            val newId = repository.insert(note)
            editingNoteId = newId
        }
        _saveSuccess.value = true
    }

    fun resetSaveSuccess() { _saveSuccess.value = false }

    fun saveAndReset() {
        viewModelScope.launch {
            saveNoteInternal()
            resetToNew()
        }
    }

    fun resetToNew() {
        editingNoteId = null
        _content.value = ""
        _fontSize.value = 16
        _fontColor.value = Color.Black
        _isBold.value = false
        _isItalic.value = false
        _saveSuccess.value = false
    }
}
