package ru.nstu.grin.concatenation.canvas.view

import javafx.scene.canvas.Canvas
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import ru.nstu.grin.common.common.SettingsProvider
import ru.nstu.grin.concatenation.canvas.handlers.DraggedHandler
import ru.nstu.grin.concatenation.canvas.handlers.PressedMouseHandler
import ru.nstu.grin.concatenation.canvas.handlers.ReleaseMouseHandler
import ru.nstu.grin.concatenation.canvas.handlers.ScalableScrollHandler
import ru.nstu.grin.concatenation.canvas.model.CanvasViewModel
import ru.nstu.grin.concatenation.canvas.model.ConcatenationCanvasModel

class ConcatenationCanvas(
    private val scalableScrollHandler: ScalableScrollHandler,
    private val draggedHandler: DraggedHandler,
    private val pressedMouseHandle: PressedMouseHandler,
    private val releaseMouseHandler: ReleaseMouseHandler,
    private val model: ConcatenationCanvasModel,
    private val canvasViewModel: CanvasViewModel,
    private val chainDrawer: ConcatenationChainDrawer,
): Pane() {
    private val fxCoroutineScope = CoroutineScope(Dispatchers.JavaFx)

    init {
        val c1 = Canvas(SettingsProvider.getCanvasWidth(), SettingsProvider.getCanvasHeight()).apply {
            VBox.setVgrow(this, Priority.ALWAYS)
            HBox.setHgrow(this, Priority.ALWAYS)

            canvasViewModel.functionsLayerContext = graphicsContext2D
        }

        val c2 = Canvas(SettingsProvider.getCanvasWidth(), SettingsProvider.getCanvasHeight()).apply {
            VBox.setVgrow(this, Priority.ALWAYS)
            HBox.setHgrow(this, Priority.ALWAYS)

            canvasViewModel.uiLayerContext = graphicsContext2D

            onScroll = scalableScrollHandler

            onMouseDragged = draggedHandler

            onMousePressed = pressedMouseHandle

            onMouseReleased = releaseMouseHandler
        }

        children.addAll(c1, c2)

        widthProperty().addListener { _ ->
            c1.width = width
            c2.width = width
            canvasViewModel.canvasWidth = width

            chainDrawer.draw()
        }

        heightProperty().addListener { _ ->
            c1.height = height
            c2.height = height
            canvasViewModel.canvasHeight = height

            chainDrawer.draw()
        }

        fxCoroutineScope.launch {
            merge(
                model.axesListUpdatedEvent,
                model.functionsListUpdatedEvent,
                model.descriptionsListUpdatedEvent,
                model.cartesianSpacesListUpdatedEvent,
                model.arrowsListUpdatedEvent
            ).collectLatest {
                chainDrawer.draw()
            }
        }

        chainDrawer.draw()
    }

    fun redraw() {
        chainDrawer.draw()
    }
}