package ru.nstu.grin.common.common

object SettingsProvider {
    private const val CANVAS_WIDTH = 800.0
    private const val CANVAS_HEIGHT = 600.0
    private const val CONCATENATION_CANVAS_WIDTH = 60.0

    fun getCanvasWidth(): Double {
        return CANVAS_WIDTH
    }

    fun getCanvasHeight(): Double {
        return CANVAS_HEIGHT
    }

    fun getAxisWidth(): Double {
        return CONCATENATION_CANVAS_WIDTH
    }
}