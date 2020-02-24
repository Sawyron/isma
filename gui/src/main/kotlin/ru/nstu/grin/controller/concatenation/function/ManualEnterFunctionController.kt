package ru.nstu.grin.controller.concatenation.function

import ru.nstu.grin.controller.DeltaMarksGenerator
import ru.nstu.grin.controller.DeltaSizeCalculator
import ru.nstu.grin.events.concatenation.ConcatenationFunctionEvent
import ru.nstu.grin.dto.concatenation.AxisDTO
import ru.nstu.grin.dto.concatenation.FunctionDTO
import ru.nstu.grin.model.DrawSize
import ru.nstu.grin.model.concatenation.function.ManualEnterFunctionViewModel
import ru.nstu.grin.model.Point
import tornadofx.Controller

/**
 * @author Konstantin Volivach
 */
class ManualEnterFunctionController : Controller() {
    private val model: ManualEnterFunctionViewModel by inject()

    private companion object {
        const val DELIMITER = ","
    }

    fun parsePoints(): List<Point> {
        val x = model.xPoints.split(DELIMITER)
        val y = model.yPoints.split(DELIMITER)
        return x.zip(y) { a, b ->
            Point(a.toDouble(), b.toDouble())
        }
    }

    fun addFunction(points: List<Point>, drawSize: DrawSize) {
        val delta = DeltaSizeCalculator().calculateDelta(drawSize)
        val deltaMarksGenerator = DeltaMarksGenerator()

        val functionDto = FunctionDTO(
            name = model.functionName,
            points = points,
            xAxis = AxisDTO(
                color = model.xAxisColor,
                delimeterColor = model.xDelimiterColor,
                direction = model.xDirection,
                deltaMarks = deltaMarksGenerator.getDeltaMarks(drawSize, delta, model.xDirection.direction)
            ),
            yAxis = AxisDTO(
                color = model.yAxisColor,
                delimeterColor = model.yDelimeterColor,
                direction = model.yDirection,
                deltaMarks = deltaMarksGenerator.getDeltaMarks(drawSize, delta, model.yDirection.direction)
            ),
            functionColor = model.functionColor
        )
        fire(
            ConcatenationFunctionEvent(
                function = functionDto,
                minAxisDelta = delta
            )
        )
    }
}