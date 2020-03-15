package ru.nstu.grin.concatenation.model.axis

import javafx.scene.paint.Color
import ru.nstu.grin.concatenation.model.Direction

data class TopAxis(
    private val zeroPoint: Double,
    private val minDelta: Double,
    private val deltaMarks: List<Double>,
    private val backGroundColor: Color,
    private val delimiterColor: Color
) : AbstractAxis(
    zeroPoint, minDelta, deltaMarks, backGroundColor, delimiterColor
) {
    override fun getDirection(): Direction {
        return Direction.TOP
    }
}