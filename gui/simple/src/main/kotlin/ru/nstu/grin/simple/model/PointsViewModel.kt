package ru.nstu.grin.simple.model

import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import ru.nstu.grin.common.model.Point
import tornadofx.*
import java.io.File

class PointsViewModel : ViewModel() {
    val pointsProperty: SimpleListProperty<Point> = SimpleListProperty(FXCollections.observableArrayList())
    var points by pointsProperty

    val file: File by param()
}