package ru.nstu.grin

import javafx.stage.Stage
import ru.nstu.grin.view.ConstuctorView
import tornadofx.App
import tornadofx.UIComponent
import tornadofx.launch
import tornadofx.reloadStylesheetsOnFocus
import kotlin.reflect.KClass

class MyApp : App() {
    override val primaryView: KClass<out UIComponent> = ConstuctorView::class

    init {
        reloadStylesheetsOnFocus()
    }

    override fun start(stage: Stage) {
        stage.isResizable = false
        super.start(stage)
    }
}

fun main(args: Array<String>) {
    launch<MyApp>(args = args)
}