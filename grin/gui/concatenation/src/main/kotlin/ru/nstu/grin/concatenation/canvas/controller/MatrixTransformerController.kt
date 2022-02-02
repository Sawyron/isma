package ru.nstu.grin.concatenation.canvas.controller

import ru.nstu.grin.common.common.SettingsProvider
import ru.nstu.grin.concatenation.axis.model.AxisSettings
import ru.nstu.grin.concatenation.axis.model.Direction
import ru.nstu.grin.concatenation.canvas.model.ConcatenationCanvasModel
import tornadofx.Controller
import kotlin.math.abs

class MatrixTransformerController : Controller() {
    private val model: ConcatenationCanvasModel by inject()

    fun transformPixelToUnits(number: Double, axisSettings: AxisSettings, direction: Direction): Double {
        val min = axisSettings.min
        val max = axisSettings.max
        val sumUnits = abs(max - min)

        val (minPixel, maxPixel) = getMinMaxPixel(direction)
        val sumPixel = maxPixel - minPixel
        val unitPrice = sumPixel / sumUnits

        return when (direction) {
            Direction.LEFT, Direction.RIGHT -> {
                min + (maxPixel - number) / unitPrice
            }
            Direction.TOP, Direction.BOTTOM -> {
                min + (number - minPixel) / unitPrice
            }
        }
    }

    fun transformUnitsToPixel(number: Double, axisSettings: AxisSettings, direction: Direction): Double {
        val min = axisSettings.min
        val max = axisSettings.max
        val sumUnits = abs(max - min)

        val (minPixel, maxPixel) = getMinMaxPixel(direction)
        val sumPixel = maxPixel - minPixel
        val unitPrice = sumPixel / sumUnits

        return when (direction) {
            Direction.LEFT, Direction.RIGHT -> {
                maxPixel - (number - min) * unitPrice
            }
            Direction.TOP, Direction.BOTTOM -> {
                minPixel + (number - min) * unitPrice
            }
        }
    }

    /**
     * Узнаём ширину окна за вычетом осей
     */
    fun getMinMaxPixel(direction: Direction): Pair<Double, Double> {
        return when (direction) {
            Direction.LEFT, Direction.RIGHT -> {
                Pair(
                    getTopAxisSize() * SettingsProvider.getAxisWidth(),
                    SettingsProvider.getCanvasHeight() - getBottomAxisSize() * SettingsProvider.getAxisWidth()
                )
            }
            Direction.TOP, Direction.BOTTOM -> {
                Pair(
                    getLeftAxisSize() * SettingsProvider.getAxisWidth(),
                    SettingsProvider.getCanvasWidth() - getRightAxisSize() * SettingsProvider.getAxisWidth()
                )
            }
        }
    }

    private fun getLeftAxisSize(): Int {
        return model.cartesianSpaces
            .filter { it.xAxis.direction == Direction.LEFT || it.yAxis.direction == Direction.LEFT }
            .size
    }

    private fun getRightAxisSize(): Int {
        return model.cartesianSpaces
            .filter { it.xAxis.direction == Direction.RIGHT || it.yAxis.direction == Direction.RIGHT }
            .size
    }

    private fun getBottomAxisSize(): Int {
        return model.cartesianSpaces
            .filter { it.xAxis.direction == Direction.BOTTOM || it.yAxis.direction == Direction.BOTTOM }
            .size
    }

    private fun getTopAxisSize(): Int {
        return model.cartesianSpaces
            .filter { it.xAxis.direction == Direction.TOP || it.yAxis.direction == Direction.TOP }
            .size
    }
}