package ru.nstu.grin.concatenation.canvas.handlers

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import ru.nstu.grin.common.model.Point
import ru.nstu.grin.concatenation.canvas.view.ConcatenationChainDrawer
import ru.nstu.grin.concatenation.axis.model.ConcatenationAxis
import ru.nstu.grin.concatenation.canvas.model.DraggedSettings
import ru.nstu.grin.concatenation.canvas.model.ConcatenationCanvasModelViewModel
import ru.nstu.grin.concatenation.canvas.model.ConcatenationViewModel
import ru.nstu.grin.concatenation.canvas.model.EditMode
import tornadofx.Controller

class DraggedHandler : EventHandler<MouseEvent>, Controller() {
    private val model: ConcatenationCanvasModelViewModel by inject()
    private val chainDrawer: ConcatenationChainDrawer by inject()
    private val currentCanvasSettings: MutableMap<ConcatenationAxis, DraggedSettings> = mutableMapOf()
    private val concatenationViewModel: ConcatenationViewModel by inject()

    override fun handle(event: MouseEvent) {
        val editMode = concatenationViewModel.currentEditMode
        if (editMode == EditMode.SCALE) {
            if (event.isPrimaryButtonDown) {
                println("Primary button down dragged")
                model.selectionSettings.secondPoint = Point(event.x, event.y)
                chainDrawer.draw()
            }
            if (!event.isPrimaryButtonDown) {
                model.selectionSettings.isSelected = false
            }
        }

        if (model.pointToolTipSettings.isShow) return
        if (!event.isPrimaryButtonDown) {
            model.selectionSettings.isSelected = false
            return
        }

        val axises = model.cartesianSpaces.map {
            listOf(it.xAxis, it.yAxis)
        }.flatten()
        val axis = axises.firstOrNull {
            it.isLocated(event.x, event.y)
        } ?: return

        val draggedSettings = getDraggedSettings(axis)

        if (draggedSettings.lastX == -1.0) draggedSettings.lastX = event.x
        if (draggedSettings.lastY == -1.0) draggedSettings.lastY = event.y


        if (axis.isXAxis()) {
            when {
                event.x < draggedSettings.lastX -> {
                    axis.settings.correlation -= DELTA
                }
                event.x > draggedSettings.lastX -> {
                    axis.settings.correlation += DELTA
                }
                else -> {
                }
            }
        } else {
            when {
                event.y < draggedSettings.lastY -> {
                    axis.settings.correlation -= DELTA
                }
                event.y > draggedSettings.lastY -> {
                    axis.settings.correlation += DELTA
                }
                else -> {
                }
            }
        }
        draggedSettings.lastX = event.x
        draggedSettings.lastY = event.y
        currentCanvasSettings[axis] = draggedSettings
        chainDrawer.draw()
    }

    private fun getDraggedSettings(axis: ConcatenationAxis): DraggedSettings {
        return currentCanvasSettings[axis] ?: DraggedSettings()
    }

    private companion object {
        const val DELTA = 2.5
    }
}