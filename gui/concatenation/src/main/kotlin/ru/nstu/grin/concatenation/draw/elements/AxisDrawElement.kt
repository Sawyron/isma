package ru.nstu.grin.concatenation.draw.elements

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import ru.nstu.grin.common.common.SettingsProvider
import ru.nstu.grin.common.view.ChainDrawElement
import ru.nstu.grin.concatenation.marks.MarksProvider
import ru.nstu.grin.concatenation.model.CanvasSettings
import ru.nstu.grin.concatenation.model.CartesianSpace
import ru.nstu.grin.concatenation.model.Direction
import ru.nstu.grin.concatenation.model.axis.ConcatenationAxis

class AxisDrawElement(
    private val xAxis: ConcatenationAxis,
    private val yAxis: ConcatenationAxis,
    private val canvasSettings: CanvasSettings,
    private val cartesianSpaces: List<CartesianSpace>
) : ChainDrawElement {

    override fun draw(context: GraphicsContext) {
        drawBackground(context, xAxis.order, xAxis.direction, xAxis.backGroundColor)
        drawBackground(context, yAxis.order, yAxis.direction, yAxis.backGroundColor)
        drawAxisMarks(context, xAxis.order, xAxis.zeroPoint, xAxis.direction, xAxis.marksProvider, xAxis.delimiterColor)
        drawAxisMarks(context, yAxis.order, yAxis.zeroPoint, yAxis.direction, yAxis.marksProvider, yAxis.delimiterColor)
    }

    private fun drawAxisMarks(
        context: GraphicsContext,
        order: Int,
        zeroPoint: Double,
        direction: Direction,
        marksProvider: MarksProvider,
        color: Color
    ) {
        context.stroke = color
        val startPoint = order * SettingsProvider.getAxisWidth()
        val marksCoordinate = startPoint + MARKS_MARGIN

        when (direction) {
            Direction.LEFT -> {
                var drawStepY = "0.0"
                var currentStepY = 0.0
                var currentY = zeroPoint
                val minY = getTopAxisSize() * SettingsProvider.getAxisWidth()
                while (currentY > minY) {
                    context.strokeText(
                        drawStepY,
                        marksCoordinate,
                        currentY
                    )

                    currentY -= SettingsProvider.getMarksInterval()
                    drawStepY = marksProvider.getInvertNextMark(currentY, zeroPoint, currentStepY, canvasSettings.step)
                    currentStepY += canvasSettings.step
                }

                drawStepY = "0.0"
                currentStepY = 0.0
                currentY = zeroPoint
                val maxY = SettingsProvider.getCanvasHeight() - getBottomAxisSize() * SettingsProvider.getAxisWidth()
                while (currentY < maxY) {
                    if (currentStepY != 0.0)
                        context.strokeText(
                            drawStepY,
                            marksCoordinate - 5,
                            currentY
                        )

                    currentY += SettingsProvider.getMarksInterval()
                    drawStepY = marksProvider.getInvertNextMark(currentY, zeroPoint, currentStepY, canvasSettings.step)
                    currentStepY -= canvasSettings.step
                }
            }
            Direction.RIGHT -> {
//                var currentStepY = 0.0
//                var currentY = getTopAxisSize() * SettingsProvider.getAxisWidth()
//                val maxY = SettingsProvider.getCanvasHeight() - getBottomAxisSize() * SettingsProvider.getAxisWidth()
//                while (currentY < maxY) {
//                    context.strokeText(
//                        marksProvider.getNextMark(, currentStepY, canvasSettings.step),
//                        SettingsProvider.getCanvasWidth() - marksCoordinate,
//                        currentY
//                    )
//                    currentStepY += canvasSettings.step
//                    currentY += SettingsProvider.getMarksInterval()
//                }
            }
            Direction.TOP -> {
//                var currentStepX = 0.0
//                var currentX = getLeftAxisSize() * SettingsProvider.getAxisWidth()
//                val maxX = SettingsProvider.getCanvasWidth() - getRightAxisSize() * SettingsProvider.getAxisWidth()
//                while (currentX < maxX) {
//                    context.strokeText(
//                        marksProvider.getNextMark(, currentStepX, canvasSettings.step),
//                        currentX,
//                        marksCoordinate
//                    )
//                    currentStepX += canvasSettings.step
//                    currentX += SettingsProvider.getMarksInterval()
//                }
            }
            Direction.BOTTOM -> {
                var drawStepX = "0.0"
                var currentStepX = 0.0
                var currentX = zeroPoint
                val minX = getLeftAxisSize() * SettingsProvider.getAxisWidth()
                while (currentX > minX) {
                    context.strokeText(
                        drawStepX,
                        currentX,
                        SettingsProvider.getCanvasHeight() - marksCoordinate
                    )

                    currentX -= SettingsProvider.getMarksInterval()
                    drawStepX = marksProvider.getNextMark(currentX, zeroPoint, currentStepX, canvasSettings.step)
                    currentStepX -= canvasSettings.step
                }

                drawStepX = "0.0"
                currentStepX = 0.0
                currentX = zeroPoint
                val maxX = SettingsProvider.getCanvasWidth() - getRightAxisSize() * SettingsProvider.getAxisWidth()
                while (currentX < maxX) {
                    context.strokeText(
                        drawStepX,
                        currentX,
                        SettingsProvider.getCanvasHeight() - marksCoordinate
                    )

                    currentX += SettingsProvider.getMarksInterval()
                    drawStepX = marksProvider.getNextMark(currentX, zeroPoint, currentStepX, canvasSettings.step)
                    currentStepX += canvasSettings.step
                }
            }
        }
    }

    private fun drawBackground(context: GraphicsContext, order: Int, direction: Direction, color: Color) {
        context.fill = color
        val startPoint = order * SettingsProvider.getAxisWidth()
        when (direction) {
            Direction.LEFT -> {
                context.fillRect(startPoint, 0.0, SettingsProvider.getAxisWidth(), SettingsProvider.getCanvasHeight())
            }
            Direction.RIGHT -> {
                context.fillRect(
                    SettingsProvider.getCanvasWidth() - startPoint,
                    0.0,
                    SettingsProvider.getAxisWidth(),
                    SettingsProvider.getCanvasHeight()
                )
            }
            Direction.TOP -> {
                context.fillRect(
                    0.0,
                    startPoint,
                    SettingsProvider.getCanvasWidth(),
                    SettingsProvider.getAxisWidth()
                )
            }
            Direction.BOTTOM -> {
                context.fillRect(
                    0.0,
                    SettingsProvider.getCanvasHeight() - startPoint - SettingsProvider.getAxisWidth(),
                    SettingsProvider.getCanvasWidth(),
                    SettingsProvider.getAxisWidth()
                )
            }
        }
    }

    private fun getTopAxisSize(): Int {
        return cartesianSpaces.filter { it.xAxis.direction == Direction.TOP || it.yAxis.direction == Direction.TOP }
            .size
    }

    private fun getLeftAxisSize(): Int {
        return cartesianSpaces.filter { it.xAxis.direction == Direction.LEFT || it.yAxis.direction == Direction.LEFT }
            .size
    }

    private fun getRightAxisSize(): Int {
        return cartesianSpaces.filter { it.xAxis.direction == Direction.RIGHT || it.yAxis.direction == Direction.RIGHT }
            .size
    }

    private fun getBottomAxisSize(): Int {
        return cartesianSpaces.filter {
            it.xAxis.direction == Direction.BOTTOM
                || it.yAxis.direction == Direction.BOTTOM
        }
            .size
    }

    private companion object {
        const val MARKS_MARGIN = 20.0
    }
}