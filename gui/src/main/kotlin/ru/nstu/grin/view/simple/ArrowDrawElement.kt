package ru.nstu.grin.view.simple

import javafx.scene.canvas.GraphicsContext
import ru.nstu.grin.model.drawable.Arrow
import ru.nstu.grin.model.view.SimpleCanvasViewModel
import ru.nstu.grin.view.ChainDrawElement

class ArrowDrawElement(
    private val model: SimpleCanvasViewModel
) : ChainDrawElement {
    override fun draw(context: GraphicsContext) {
        for (arrow in model.arrows) {
            val x = arrow.x
            val y = arrow.y
            context.strokeLine(x, y, x + DEFAULT_LENGTH, y + DEFAULT_LENGTH)
            context.strokeLine(
                x + DEFAULT_LENGTH,
                y + DEFAULT_LENGTH,
                x + DEFAULT_LENGTH / 2,
                y + DEFAULT_LENGTH
            )
            context.strokeLine(
                y + DEFAULT_LENGTH,
                y + DEFAULT_LENGTH,
                x + DEFAULT_LENGTH,
                y + DEFAULT_LENGTH / 2
            )
        }
    }

    private companion object {
        const val DEFAULT_LENGTH = 20
    }
}