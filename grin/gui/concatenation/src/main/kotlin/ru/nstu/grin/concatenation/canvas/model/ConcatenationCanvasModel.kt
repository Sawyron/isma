package ru.nstu.grin.concatenation.canvas.model

import javafx.collections.FXCollections.observableArrayList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import ru.nstu.grin.common.model.Arrow
import ru.nstu.grin.common.model.Description
import ru.nstu.grin.concatenation.axis.model.ConcatenationAxis
import ru.nstu.grin.concatenation.canvas.view.ConcatenationCanvas
import ru.nstu.grin.concatenation.cartesian.model.CartesianSpace
import ru.nstu.grin.concatenation.function.model.ConcatenationFunction
import ru.nstu.grin.concatenation.points.model.PointToolTipsSettings
import tornadofx.ItemViewModel
import kotlin.math.max
import kotlin.math.min

class ConcatenationCanvasModel : ItemViewModel<ConcatenationCanvas>(), Cloneable {
    val cartesianSpaces = observableArrayList<CartesianSpace>()!!

    val arrows = observableArrayList<Arrow>()!!

    val descriptions = observableArrayList<Description>()!!

    val pointToolTipSettings = PointToolTipsSettings(false, mutableSetOf())

    val contextMenuSettings =
        ContextMenuSettings(ContextMenuType.NONE, 0.0, 0.0)

    val selectionSettings = SelectionSettings()

    var traceSettings: TraceSettings? = null

    var moveSettings: MoveSettings? = null

    private val functionsListUpdatedEventInternal = MutableSharedFlow<List<ConcatenationFunction>>()
    val functionsListUpdatedEvent = functionsListUpdatedEventInternal.asSharedFlow()

    fun getAllFunctions() =
        cartesianSpaces.map { it.functions }.flatten()

    suspend fun reportFunctionsListUpdate() =
        functionsListUpdatedEventInternal.emit(getAllFunctions())

    private val axesListUpdatedEventInternal = MutableSharedFlow<List<ConcatenationAxis>>()
    val axesListUpdatedEvent = axesListUpdatedEventInternal.asSharedFlow()

    fun getAllAxes() =
        cartesianSpaces.map { listOf(it.xAxis, it.yAxis) }.flatten()

    suspend fun reportAxesListUpdate() =
        axesListUpdatedEventInternal.emit(getAllAxes())

    fun unselectAll() {
        for (cartesianSpace in cartesianSpaces) {
            for (function in cartesianSpace.functions) {
                function.isSelected = false
            }
        }
        for (description in descriptions) {
            description.isSelected = false
        }
    }

    fun getSelectedFunction(): ConcatenationFunction? {
        return cartesianSpaces.map { it.functions }.flatten().firstOrNull { it.isSelected }
    }

    fun getSelectedDescription(): Description? {
        return descriptions.firstOrNull { it.isSelected }
    }

    fun normalizeSpaces(){
        cartesianSpaces.forEach { space ->
            var minX = Double.POSITIVE_INFINITY
            var maxX = Double.NEGATIVE_INFINITY

            var minY = Double.POSITIVE_INFINITY
            var maxY = Double.NEGATIVE_INFINITY

            space.functions.forEach{ function ->
                function.points.forEach { point ->
                    minX = min(point.x, minX)
                    maxX = max(point.x, maxX)

                    minY = min(point.y, minY)
                    maxY = max(point.y, maxY)
                }
            }

            val indentX = (maxX - minX) * DEFAULT_INDENT
            val indentY = (maxY - minY) * DEFAULT_INDENT

            space.xAxis.settings.min = minX - indentX
            space.xAxis.settings.max = maxX + indentX

            space.yAxis.settings.min = minY - indentY
            space.yAxis.settings.max = maxY + indentY
        }
    }

    private companion object {
        const val DEFAULT_INDENT = 0.01
    }
}