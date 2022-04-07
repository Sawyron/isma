package ru.nstu.grin.concatenation.axis.view

import javafx.geometry.VPos
import javafx.scene.canvas.GraphicsContext
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment
import ru.nstu.grin.concatenation.axis.controller.NumberFormatter
import ru.nstu.grin.concatenation.axis.model.ConcatenationAxis
import ru.nstu.grin.concatenation.axis.utilities.createStringValue
import ru.nstu.grin.concatenation.axis.utilities.estimateTextSize
import ru.nstu.grin.concatenation.canvas.controller.MatrixTransformer
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class VerticalAxisDrawStrategy(
    private val matrixTransformer: MatrixTransformer
) : AxisMarksDrawStrategy {

    override fun drawMarks(
        context: GraphicsContext,
        axis: ConcatenationAxis,
        marksCoordinate: Double,
    ) {
        context.save()

        context.stroke = axis.fontColor
        context.fill = axis.fontColor
        context.textAlign = TextAlignment.CENTER
        context.textBaseline = VPos.CENTER
        context.font = Font.font(axis.font, axis.textSize)


        // TODO: drawOnlyIntegerMark is temporary unavailable
        /*if (axis.settings.isOnlyIntegerPow) {
            println("IsOnlyIntger")
            drawOnlyIntegerMark(context, axis, marksCoordinate, zeroPixel)
        } else {
            drawDoubleMarks(context, axis, marksCoordinate, zeroPixel)
        }*/

        val (marksWidth, _) = drawDoubleMarks(context, axis, marksCoordinate)
        val (_, labelHeight) = estimateTextSize(axis.name, context.font)

        drawAxisLabel(context, axis, marksCoordinate - marksWidth / 2 - DISTANCE_TO_LABEL - labelHeight / 2)

        context.restore()
    }

    private fun drawDoubleMarks(
        context: GraphicsContext,
        axis: ConcatenationAxis,
        marksCoordinate: Double,
    ): Pair<Double, Double> {
        val (minPixel, maxPixel) = matrixTransformer
            .getMinMaxPixel(axis.direction)

        val maxDrawingPixel = maxPixel - TEXT_VERTICAL_BORDERS_OFFSET
        val minDrawingPixel = minPixel + TEXT_VERTICAL_BORDERS_OFFSET

        val zeroPixel = matrixTransformer.transformUnitsToPixel(0.0, axis.settings, axis.direction)

        var maxWidth = 0.0

        val zeroPixelOffset = if(zeroPixel < maxDrawingPixel && zeroPixel > minDrawingPixel){
            val text = createStringValue(0.0, axis)
            val (width, height) = estimateTextSize(text, context.font)

            maxWidth = max(maxWidth, width)

            if(zeroPixel + height / 2 < maxDrawingPixel && zeroPixel - height / 2 > minDrawingPixel){
                context.fillText(text, marksCoordinate, zeroPixel)
                height / 2
            } else{
                0.0
            }
        } else {
            0.0
        }

        var nextMarkPixel = max(zeroPixel + axis.distanceBetweenMarks, minDrawingPixel)
        var filledPosition = zeroPixel + zeroPixelOffset

        while (nextMarkPixel < maxDrawingPixel) {
            val currentValue = matrixTransformer
                .transformPixelToUnits(nextMarkPixel, axis.settings, axis.direction)

            val text = createStringValue(currentValue, axis)
            val (width, height) = estimateTextSize(text, context.font)

            maxWidth = max(maxWidth, width)

            if(nextMarkPixel - height / 2 - MIN_SPACE_BETWEEN_MARKS > filledPosition
                && nextMarkPixel + height / 2 < maxDrawingPixel
                && nextMarkPixel - height / 2 > minDrawingPixel
            ){
                filledPosition = nextMarkPixel + height / 2
                context.fillText(text, marksCoordinate, nextMarkPixel)
            }

            nextMarkPixel += axis.distanceBetweenMarks
        }

        nextMarkPixel = min(zeroPixel - zeroPixelOffset - axis.distanceBetweenMarks, maxDrawingPixel)
        filledPosition = zeroPixel - zeroPixelOffset

        while (nextMarkPixel > minDrawingPixel) {
            val currentValue = matrixTransformer
                .transformPixelToUnits(nextMarkPixel, axis.settings, axis.direction)

            val text = createStringValue(currentValue, axis)
            val (width, height) = estimateTextSize(text, context.font)

            maxWidth = max(maxWidth, width)

            if(nextMarkPixel + height / 2 + MIN_SPACE_BETWEEN_MARKS < filledPosition
                && nextMarkPixel + height / 2 < maxDrawingPixel
                && nextMarkPixel - height / 2 > minDrawingPixel
            ) {
                filledPosition = nextMarkPixel - height / 2
                context.fillText(text, marksCoordinate, nextMarkPixel)
            }

            nextMarkPixel -= axis.distanceBetweenMarks
        }

        return Pair(maxWidth, maxDrawingPixel - minDrawingPixel)
    }

    private fun drawAxisLabel(
        context: GraphicsContext,
        axis: ConcatenationAxis,
        labelCoordinate: Double,
    ){
        context.save()

        val (minPixel, maxPixel) = matrixTransformer.getMinMaxPixel(axis.direction)

        context.translate(labelCoordinate, minPixel + (maxPixel - minPixel) / 2)
        context.rotate(-90.0)
        context.fillText(axis.name, 0.0, 0.0)

        context.restore()
    }

    private fun drawOnlyIntegerMark(
        context: GraphicsContext,
        axis: ConcatenationAxis,
        marksCoordinate: Double,
        zeroPixel: Double,
    ) {
        var currentY = axis.settings.max.roundToInt().toDouble()
        val min = axis.settings.min
        while (currentY > min) {
            val stepY = matrixTransformer
                .transformUnitsToPixel(currentY, axis.settings, axis.direction)

            if (axis.settings.max > 0 && axis.settings.min < 0) {
                if ((stepY - zeroPixel).absoluteValue < (axis.distanceBetweenMarks / 2)) {
                    println("Handled")
                    currentY -= axis.settings.integerStep
                    continue
                }
            }
            val text = if (axis.isLogarithmic()) {
                NumberFormatter.formatLogarithmic(currentY, axis.settings.logarithmBase)
            } else {
                NumberFormatter.format(currentY)
            }

            context.fillText(
                text,
                marksCoordinate - 15.0,
                stepY
            )

            currentY -= axis.settings.integerStep
        }
    }

    private companion object {
        const val TEXT_VERTICAL_BORDERS_OFFSET = 5.0
        const val MIN_SPACE_BETWEEN_MARKS = 5.0
        const val DISTANCE_TO_LABEL = 2.0
    }
}