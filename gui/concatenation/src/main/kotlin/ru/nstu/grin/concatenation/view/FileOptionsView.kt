package ru.nstu.grin.concatenation.view

import javafx.scene.Parent
import ru.nstu.grin.concatenation.controller.FileOptionsController
import ru.nstu.grin.concatenation.model.FileOptionsModel
import ru.nstu.grin.concatenation.model.FileReaderMode
import tornadofx.*

class FileOptionsView : View() {
    private val model: FileOptionsModel by inject(params = params)
    private val controller: FileOptionsController by inject(params = params)

    override val root: Parent = form {
        fieldset("Опции чтения") {
            field("Разделитель") {
                textfield().bind(model.delimiterProperty)
            }
            field("Вид файла") {
                combobox(model.readerModeProperty, FileReaderMode.values().toList()) {
                    cellFormat {
                        text = when (it) {
                            FileReaderMode.ONE_TO_MANY -> "Один ко многим"
                            FileReaderMode.SEQUENCE -> "Функции подряд"
                        }
                    }
                }
            }
        }
        button("ОК") {
            action {
                controller.openPointsWindow()
                close()
            }
        }
    }

}