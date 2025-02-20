package ru.nstu.grin.concatenation.axis.view

import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import ru.nstu.grin.concatenation.axis.controller.AxisListViewController
import ru.nstu.grin.concatenation.axis.model.AxisListViewModel
import ru.nstu.grin.concatenation.axis.model.ConcatenationAxis

class AxisListView(
    viewModel: AxisListViewModel,
    private val controller: AxisListViewController
) : ListView<ConcatenationAxis>(viewModel.axes) {

    init {
        setCellFactory {
            object : ListCell<ConcatenationAxis>() {
                override fun updateItem(item: ConcatenationAxis?, empty: Boolean) {
                    super.updateItem(item, empty)

                    graphic = if (item == null) null else createItem(item)
                }
            }
        }
    }

    private fun createItem(item: ConcatenationAxis): BorderPane {
        return BorderPane().apply {
            left = HBox(
                Label(item.name).apply {
                    textFill = item.styleProperties.marksColor
                    font = item.styleProperties.marksFont
                    background = Background(
                        BackgroundFill(
                            item.styleProperties.backgroundColor,
                            CornerRadii(0.0),
                            Insets(0.0)
                        )
                    )
                    padding = Insets(5.0)
                },
            ).apply {
                spacing = 5.0
            }

            right = Button(null, ImageView(Image("edit-tool.png")).apply {
                fitHeight = 20.0
                fitWidth = 20.0
            }).apply {
                tooltip = Tooltip("Отредактировать")
                setOnAction {
                    controller.editAxis(item, scene.window)
                }
            }
        }
    }
}