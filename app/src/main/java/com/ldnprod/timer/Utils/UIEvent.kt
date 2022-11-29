package com.ldnprod.timer.Utils

sealed class UIEvent {
    data class ItemInserted(val position: Int): UIEvent()
    object CloseActivity: UIEvent()
}
