package ru.nstu.grin.concatenation.canvas.model.project

import javafx.scene.paint.Color
import kotlinx.serialization.Serializable
import ru.nstu.grin.common.model.Point
import ru.nstu.grin.common.model.WaveletDirection
import ru.nstu.grin.common.model.WaveletTransformFun
import ru.nstu.grin.concatenation.axis.model.AxisMarkType
import ru.nstu.grin.concatenation.axis.model.AxisSettings
import ru.nstu.grin.concatenation.axis.model.ConcatenationAxis
import ru.nstu.grin.concatenation.axis.model.Direction
import ru.nstu.grin.concatenation.cartesian.model.CartesianSpace
import ru.nstu.grin.concatenation.function.model.*
import java.util.*

@Serializable
data class ProjectSnapshot(
    val spaces: List<CartesianSpaceSnapshot>
)

@Serializable
data class CartesianSpaceSnapshot(
    val id: String,
    val name: String,
    val functions: List<ConcatenationFunctionSnapshot>,
    val xAxis: ConcatenationAxisSnapshot,
    val yAxis: ConcatenationAxisSnapshot,
    val isShowGrid: Boolean,
)

@Serializable
data class ConcatenationFunctionSnapshot(
    val id: String,
    val name: String,
    val points: List<PointSnapshot>,
    val isHide: Boolean,
    val isSelected: Boolean,

    val functionColor: ColorSnapshot,

    val lineSize: Double,
    val lineType: LineType,
    val details: List<ConcatenationFunctionDetailsSnapshot>
)

@Serializable
data class PointSnapshot(
    val x: Double,
    val y: Double,
)

@Serializable
data class ColorSnapshot(
    val red: Double,
    val green: Double,
    val blue: Double,
)

@Serializable
sealed class ConcatenationFunctionDetailsSnapshot

@Serializable
data class MirrorDetailsSnapshot(
    val isMirrorX: Boolean,
    val isMirrorY: Boolean,
) : ConcatenationFunctionDetailsSnapshot()

@Serializable
data class DerivativeDetailsSnapshot(
    val degree: Int,
    val type: DerivativeType
) : ConcatenationFunctionDetailsSnapshot()

@Serializable
data class WaveletDetailsSnapshot(
    val waveletTransformFun: WaveletTransformFun,
    val waveletDirection: WaveletDirection
) : ConcatenationFunctionDetailsSnapshot()

@Serializable
data class ConcatenationAxisSnapshot(
    val id: String,
    val name: String,
    val order: Int,
    val direction: Direction,
    val backGroundColor: ColorSnapshot,
    val fontColor: ColorSnapshot,
    val distanceBetweenMarks: Double,
    val textSize: Double,
    val font: String,
    val isHide: Boolean,
    val axisMarkType: AxisMarkType,
    val settings: AxisSettingsSnapshot,
)

@Serializable
data class AxisSettingsSnapshot(
    val isOnlyIntegerPow: Boolean,
    val integerStep: Int,

    val isLogarithmic: Boolean,
    val logarithmBase: Double,

    val min: Double,
    val max: Double,
)

fun AxisSettingsSnapshot.toModel() =
    AxisSettings(
        isOnlyIntegerPow = isOnlyIntegerPow,
        integerStep = integerStep,
        isLogarithmic = isLogarithmic,
        logarithmBase = logarithmBase,
        min = min,
        max = max,
    )

fun AxisSettings.toSnapshot() =
    AxisSettingsSnapshot(
        isOnlyIntegerPow = isOnlyIntegerPow,
        integerStep = integerStep,
        isLogarithmic = isLogarithmic,
        logarithmBase = logarithmBase,
        min = min,
        max = max,
    )

fun ColorSnapshot.toModel() =
    Color.color(red, green, blue)

fun Color.toSnapshot() =
    ColorSnapshot(red, green, blue)

fun ConcatenationAxisSnapshot.toModel() =
    ConcatenationAxis(
        id = UUID.fromString(id),
        name = name,
        order = order,
        direction = direction,
        backGroundColor = backGroundColor.toModel(),
        fontColor = fontColor.toModel(),
        distanceBetweenMarks = distanceBetweenMarks,
        textSize = textSize,
        font = font,
        isHide = isHide,
        axisMarkType = axisMarkType,
        settings = settings.toModel()
    )

fun ConcatenationAxis.toSnapshot() =
    ConcatenationAxisSnapshot(
        id = id.toString(),
        name = name,
        order = order,
        direction = direction,
        backGroundColor = backGroundColor.toSnapshot(),
        fontColor = fontColor.toSnapshot(),
        distanceBetweenMarks = distanceBetweenMarks,
        textSize = textSize,
        font = font,
        isHide = isHide,
        axisMarkType = axisMarkType,
        settings = settings.toSnapshot()
    )

fun PointSnapshot.toModel() = Point(x, y)

fun Point.toSnapshot() = PointSnapshot(x, y)

fun ConcatenationFunctionDetailsSnapshot.toModel() =
    when(this) {
        is DerivativeDetailsSnapshot -> DerivativeDetails(degree, type)
        is MirrorDetailsSnapshot -> MirrorDetails(isMirrorX, isMirrorY)
        is WaveletDetailsSnapshot -> WaveletDetails(waveletTransformFun, waveletDirection)
    }

fun ConcatenationFunctionDetails.toSnapshot() =
    when(this) {
        is DerivativeDetails -> DerivativeDetailsSnapshot(degree, type)
        is MirrorDetails -> MirrorDetailsSnapshot(isMirrorX, isMirrorY)
        is WaveletDetails -> WaveletDetailsSnapshot(waveletTransformFun, waveletDirection)
    }

fun ConcatenationFunctionSnapshot.toModel() =
    ConcatenationFunction(
        id = UUID.fromString(id),
        name = name,
        points = points.map { it.toModel() },
        isHide = isHide,
        isSelected = isSelected,
        functionColor = functionColor.toModel(),
        lineSize = lineSize,
        lineType = lineType,
        details = details.map { it.toModel() }.toMutableList()
    )

fun ConcatenationFunction.toSnapshot() =
    ConcatenationFunctionSnapshot(
        id = id.toString(),
        name = name,
        points = points.map { it.toSnapshot() },
        isHide = isHide,
        isSelected = isSelected,
        functionColor = functionColor.toSnapshot(),
        lineSize = lineSize,
        lineType = lineType,
        details = details.map { it.toSnapshot() }
    )

fun CartesianSpaceSnapshot.toModel() =
    CartesianSpace(
        id = UUID.fromString(id),
        name = name,
        functions = functions.map { it.toModel() }.toMutableList(),
        xAxis = xAxis.toModel(),
        yAxis = yAxis.toModel(),
        isShowGrid = isShowGrid,
    )

fun CartesianSpace.toSnapshot() =
    CartesianSpaceSnapshot(
        id = id.toString(),
        name = name,
        functions = functions.map { it.toSnapshot() },
        xAxis = xAxis.toSnapshot(),
        yAxis = yAxis.toSnapshot(),
        isShowGrid = isShowGrid,
    )
