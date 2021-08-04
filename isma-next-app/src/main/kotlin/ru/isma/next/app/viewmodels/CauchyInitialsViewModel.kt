package ru.isma.next.app.viewmodels

import tornadofx.*
import javafx.beans.property.SimpleDoubleProperty
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import ru.isma.next.app.models.simulation.CauchyInitialsModel

class CauchyInitialsViewModel {
    private val startTimeProperty = SimpleDoubleProperty()
    private val endTimeProperty = SimpleDoubleProperty()
    private val stepProperty = SimpleDoubleProperty()

    var startTime by startTimeProperty
    var endTime by endTimeProperty
    var step by stepProperty

    fun startTimeProperty() = startTimeProperty
    fun endTimeProperty() = endTimeProperty
    fun stepProperty() = stepProperty

    fun commit(model: CauchyInitialsModel){
        startTime = model.startTime
        endTime = model.endTime
        step = model.initialStep
    }

    fun snapshot() = CauchyInitialsModel(startTime, endTime, step)
}