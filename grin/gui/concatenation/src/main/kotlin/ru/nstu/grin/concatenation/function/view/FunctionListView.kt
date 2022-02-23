package ru.nstu.grin.concatenation.function.view

import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.shape.Rectangle
import ru.nstu.grin.concatenation.function.controller.FunctionListViewController
import ru.nstu.grin.concatenation.function.model.ConcatenationFunction
import ru.nstu.grin.concatenation.function.model.FunctionListViewModel
import tornadofx.Fragment

class FunctionListView : Fragment() {
    private val model: FunctionListViewModel by inject()

    private val controller: FunctionListViewController = find { }

    override val root: Parent = ListView(model.functions).apply {
        setCellFactory {
            object : ListCell<ConcatenationFunction>() {
                override fun updateItem(item: ConcatenationFunction?, empty: Boolean) {
                    super.updateItem(item, empty)

                    if (item == null) {
                        graphic = null
                    } else {
                        graphic = BorderPane().apply {
                            left = HBox(
                                Rectangle(15.0, item.lineSize, item.functionColor),
                                Label(item.name)
                            ).apply {
                                spacing = 10.0
                            }

                            right = HBox(
                                Button(null, ImageView(Image("copy.png")).apply {
                                    fitHeight = 20.0
                                    fitWidth = 20.0
                                }).apply {
                                    tooltip = Tooltip("Скопировать")
                                    setOnAction {
                                        controller.openCopyModal(item.id)
                                    }
                                },
                                Button(null, ImageView(Image("edit-tool.png")).apply {
                                    fitHeight = 20.0
                                    fitWidth = 20.0
                                }).apply {
                                    tooltip = Tooltip("Отредактировать")
                                    setOnAction {
                                        controller.openChangeModal(item.id, currentWindow)
                                    }
                                },
                                Button(null, ImageView(Image("send-to-trash.png")).apply {
                                    fitHeight = 20.0
                                    fitWidth = 20.0
                                }).apply {
                                    tooltip = Tooltip("Удалить")
                                    setOnAction {
                                        controller.deleteFunction(item.id)
                                    }
                                },
                            ).apply {
                                spacing = 5.0
                            }
                        }
                    }
                }
            }
        }
    }
}