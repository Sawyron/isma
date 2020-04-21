package ru.nstu.grin.concatenation.draw.elements

import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.ContextMenu
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import ru.nstu.grin.common.model.DrawSize
import ru.nstu.grin.common.view.ChainDrawElement
import ru.nstu.grin.concatenation.controller.ConcatenationCanvasController
import ru.nstu.grin.concatenation.model.ContextMenuType
import ru.nstu.grin.concatenation.model.view.ConcatenationCanvasModelViewModel
import tornadofx.action

class ContextMenuDrawElement(
    private val contextMenu: ContextMenu,
    private val model: ConcatenationCanvasModelViewModel,
    private val controller: ConcatenationCanvasController
) : ChainDrawElement {
    override fun draw(context: GraphicsContext) {
        contextMenu.items.clear()

        val settings = model.contextMenuSettings
        val stage = model.primaryStage
        when (model.contextMenuSettings.type) {
            ContextMenuType.AXIS -> {
                val axises = model.cartesianSpaces.map {
                    listOf(Pair(it, it.xAxis), Pair(it, it.yAxis))
                }.flatten()
                val cartesianSpace = axises.firstOrNull {
                    it.second.isLocated(settings.xGraphic, settings.yGraphic)
                }?.first ?: return

                val menu = Menu("Логарифмический масштаб")
                val xMenuItem = MenuItem("Включить по x")
                xMenuItem.action {
                    cartesianSpace.settings.isXLogarithmic = !cartesianSpace.settings.isXLogarithmic
                }
                menu.items.add(xMenuItem)

                val yMenuItem = MenuItem("Включить по y")
                yMenuItem.action {
                    cartesianSpace.settings.isYLogarithmic = !cartesianSpace.settings.isYLogarithmic
                }
                menu.items.add(yMenuItem)

                contextMenu.items.add(menu)
                contextMenu.show(context.canvas, stage.x + settings.xGraphic, stage.y + settings.yGraphic)
            }
            ContextMenuType.MAIN -> {
                val functionItem = MenuItem("Добавить функцию")
                functionItem.action {
                    val drawSize = DrawSize(
                        minX = 0.0,
                        maxX = context.canvas.width,
                        minY = 0.0,
                        maxY = context.canvas.height
                    )
                    controller.openFunctionModal(drawSize, listOf(), listOf())
                }
                val arrowItem = MenuItem("Добавить указатель")
                arrowItem.action {
                    controller.openArrowModal(stage.x + settings.xGraphic, stage.y + settings.yGraphic)
                }
                val descriptionItem = MenuItem("Добавить описание")
                descriptionItem.action {
                    controller.openDescriptionModal(stage.x + settings.xGraphic, stage.y + settings.yGraphic)
                }
                contextMenu.items.addAll(functionItem, arrowItem, descriptionItem)
                contextMenu.show(context.canvas, stage.x + settings.xGraphic, stage.y + settings.yGraphic)
            }
            ContextMenuType.NONE -> {
                contextMenu.hide()
            }
        }
    }
}