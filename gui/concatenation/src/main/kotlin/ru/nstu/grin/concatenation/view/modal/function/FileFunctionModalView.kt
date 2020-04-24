package ru.nstu.grin.concatenation.view.modal.function

import javafx.scene.Parent
import javafx.scene.layout.Priority
import ru.nstu.grin.concatenation.controller.function.FileFunctionController
import ru.nstu.grin.concatenation.model.Direction
import ru.nstu.grin.concatenation.model.ExistDirection
import ru.nstu.grin.concatenation.model.function.FileFunctionViewModel
import tornadofx.*

class FileFunctionModalView : AbstractAddFunctionModal() {
    private val controller: FileFunctionController by inject()
    private val model: FileFunctionViewModel by inject()

    override val root: Parent = form {
        fieldset {
            field("Введите название группы функций") {
                textfield().bind(model.functionNameProperty)
            }
            field("Выберите файл") {
                button("Файл") {
                    action {
                        controller.chooseFile()
                    }
                }
            }
            field("Укажите шаг рисования") {
                textfield().bind(model.stepProperty)
            }
        }
        fieldset("Направления осей") {
            field("Ось x") {
                val default = Direction.values().map { ExistDirection(it, null) }
                val existDirections = xExistDirections
                combobox(model.xDirectionProperty, default + existDirections) {
                    cellFormat {
                        text = if (it.functionName != null) {
                            "Напрвление ${it.direction.name}, функция ${it.functionName}"
                        } else {
                            it.direction.name
                        }
                    }
                }
            }
            field("Ось y") {
                val default = Direction.values().map { ExistDirection(it, null) }
                val existDirections = yExistDirections
                combobox(model.yDirectionProperty, default + existDirections) {
                    cellFormat {
                        text = if (it.functionName != null) {
                            "Напрвление ${it.direction.name}, функция ${it.functionName}"
                        } else {
                            it.direction.name
                        }
                    }
                }
            }
        }
        fieldset("Цвета") {
            field("Цвет функций") {
                colorpicker().bind(model.functionColorProperty)
            }
            field("Цвет x оси") {
                colorpicker().bind(model.xAxisColorProperty)
            }
            field("Цвет дельт оси x") {
                colorpicker().bind(model.xDelimeterColorProperty)
            }
            field("Цвет y оси") {
                colorpicker().bind(model.yAxisColorProperty)
            }
            field("Цвте дельт оси y") {
                colorpicker().bind(model.yDelimiterColorProperty)
            }
        }
        button("OK") {
            enableWhen {
                model.valid
            }

            hgrow = Priority.ALWAYS
            vgrow = Priority.ALWAYS
            action {
                controller.loadFunctions(drawSize)
                close()
            }
        }
    }
}