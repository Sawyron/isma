package ru.isma.next.editor.blueprint.controls

import javafx.beans.binding.DoubleBinding
import javafx.beans.property.*
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import tornadofx.*

class StateBox : Fragment() {
    private val mousePressedListeners = arrayListOf<(StateBox, MouseEvent) -> Unit>()
    private val mouseReleasedListeners = arrayListOf<(StateBox, MouseEvent) -> Unit>()
    private val mouseClickedListeners = arrayListOf<(StateBox, MouseEvent) -> Unit>()
    private val editActionListeners = arrayListOf<() -> Unit>()

    private val isEditModeEnabledProperty = SimpleBooleanProperty(false)
    private val isEditableProperty = SimpleBooleanProperty(true)
    private val isEditButtonVisibleProperty = SimpleBooleanProperty(true)
    private val nameProperty = SimpleStringProperty("")
    private val textProperty = SimpleStringProperty("")
    private val squareWidthProperty = SimpleDoubleProperty(150.0)
    private val squareHeightProperty = SimpleDoubleProperty(80.0)
    private val colorProperty = SimpleObjectProperty<Paint>(Color.WHITE)

    private var isEditModeEnabled by isEditModeEnabledProperty
    private var isDragged = false

    var isEditable by isEditableProperty
    var isEditButtonVisible by isEditButtonVisibleProperty
    var name: String by nameProperty
    var text: String by textProperty
    var squareWidth by squareWidthProperty
    var squareHeight by squareHeightProperty
    var color: Paint by colorProperty

    fun isEditModeEnabledProperty() = isEditModeEnabledProperty
    fun isEditableProperty() = isEditableProperty
    fun isEditButtonVisible() = isEditButtonVisibleProperty
    fun nameProperty() = nameProperty
    fun textProperty() = textProperty
    fun squareWidthProperty() = squareWidthProperty
    fun squareHeightProperty() = squareHeightProperty
    fun colorProperty() = colorProperty

    fun translateXProperty(): DoubleProperty = root.layoutXProperty()
    fun translateYProperty(): DoubleProperty = root.layoutYProperty()

    fun centerXProperty(): DoubleBinding = root.layoutXProperty() + squareWidth / 2
    fun centerYProperty(): DoubleBinding = root.layoutYProperty() + squareHeight / 2

    override val root = group {
        rectangle {
            heightProperty().bind(squareHeightProperty())
            widthProperty().bind(squareWidthProperty())
            fillProperty().bind(colorProperty())
            viewOrder = 3.0
            arcWidth = 20.0
            arcHeight = 20.0
        }

        val nameTextArea = TextArea().apply {
            visibleWhen(isEditModeEnabledProperty())
            managedWhen(isEditModeEnabledProperty())
            focusedProperty().onChange {
                if(it){
                    text = name
                } else {
                    name = text
                    isEditModeEnabled = false
                }
            }
        }

        val boxLabel = Label().label {
            font = Font("Arial", 22.0)
            textProperty().bind(nameProperty())
            visibleWhen(!isEditModeEnabledProperty())
            managedWhen(!isEditModeEnabledProperty())
        }

        hbox {
            prefHeightProperty().bind(squareHeightProperty() - 20.0)
            prefWidthProperty().bind(squareWidthProperty() - 20.0)
            translateX += 10.0
            translateY += 10.0
            alignment = Pos.CENTER
            add(boxLabel)
            add(nameTextArea)
        }

        button("Edit") {
            val isVisibleBinding = isEditModeEnabledProperty().not().and(isEditButtonVisible())

            action { executeEditActionListeners() }
            visibleWhen(isVisibleBinding)
            managedWhen(isVisibleBinding)
        }

        addEventHandler(MouseEvent.MOUSE_CLICKED) {
            if (!isDragged && isEditable) {
                isEditModeEnabled = true
                nameTextArea.requestFocus()
            }
            executeMouseClickedListeners(it)
        }

        addEventHandler(MouseEvent.MOUSE_PRESSED) {
            isDragged = false
            executeMousePressedListener(it)
        }
        addEventHandler(MouseEvent.MOUSE_RELEASED) {
            executeMouseReleasedListeners(it)
        }
        addEventHandler(MouseEvent.MOUSE_DRAGGED) {
            isDragged = true
        }
    }

    fun addMousePressedListener(handler: (StateBox, MouseEvent) -> Unit){
        mousePressedListeners.add(handler)
    }

    fun removeMousePressedListener(handler: (StateBox, MouseEvent) -> Unit){
        mousePressedListeners.remove(handler)
    }

    private fun executeMousePressedListener(event: MouseEvent){
        if(event.isPrimaryButtonDown){
            for(i in mousePressedListeners.indices){
                mousePressedListeners[i](this, event)
            }
        }
    }

    fun addMouseReleasedListener(handler: (StateBox, MouseEvent) -> Unit){
        mouseReleasedListeners.add(handler)
    }

    fun removeMouseReleasedListeners(handler: (StateBox, MouseEvent) -> Unit){
        mouseReleasedListeners.remove(handler)
    }

    private fun executeMouseReleasedListeners(event: MouseEvent) {
        for (i in mouseReleasedListeners.indices) {
            mouseReleasedListeners[i](this, event)
        }
    }

    fun addMouseClickedListeners(handler: (StateBox, MouseEvent) -> Unit){
        mouseClickedListeners.add(handler)
    }

    fun removeMouseClickedListeners(handler: (StateBox, MouseEvent) -> Unit){
        mouseClickedListeners.remove(handler)
    }

    private fun executeMouseClickedListeners(event: MouseEvent){
        for(i in mouseClickedListeners.indices){
            mouseClickedListeners[i](this, event)
        }
    }

    fun addEditActionListener(op: () -> Unit){
        editActionListeners.add(op)
    }

    fun removeEditActionListener(op: () -> Unit){
        editActionListeners.remove(op)
    }

    private fun executeEditActionListeners(){
        for(i in editActionListeners.indices){
            editActionListeners[i]()
        }
    }
}