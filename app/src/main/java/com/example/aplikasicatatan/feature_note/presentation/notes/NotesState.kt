package com.example.aplikasicatatan.feature_note.presentation.notes

import com.example.aplikasicatatan.feature_note.domain.model.Note
import com.example.aplikasicatatan.feature_note.domain.utils.NoteOrder
import com.example.aplikasicatatan.feature_note.domain.utils.OrderType

data class NotesState(
    val notes: List<Note> = emptyList(),
    val noteOrder: NoteOrder = NoteOrder.Date(OrderType.Descending),
    val isOrderSectionVisible: Boolean = false
)
