package ru.nstu.grin.concatenation.function.controller

import javafx.scene.Scene
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.Window
import ru.nstu.grin.concatenation.function.events.DeleteFunctionQuery
import ru.nstu.grin.concatenation.function.events.GetAllFunctionsEvent
import ru.nstu.grin.concatenation.function.events.GetAllFunctionsQuery
import ru.nstu.grin.concatenation.function.model.FunctionListViewModel
import ru.nstu.grin.concatenation.function.view.ChangeFunctionFragment
import ru.nstu.grin.concatenation.function.view.CopyFunctionFragment
import tornadofx.Controller
import java.util.*

class FunctionListViewController : Controller() {
    private val model: FunctionListViewModel by inject()

    init {
        subscribe<GetAllFunctionsEvent> {
            if (model.functions != null) {
                model.functions.clear()
            }
            model.functionsProperty.setAll(it.functions)
        }
    }

    fun openCopyModal(id: UUID) {
        val view = find<CopyFunctionFragment>(
            mapOf(
                CopyFunctionFragment::functionId to id
            )
        )

        Stage().apply {
            scene = Scene(view.root)
            title = "Function parameters"
            initModality(Modality.WINDOW_MODAL)

            if (owner != null){
                initOwner(owner)
            }

            show()
        }
    }

    fun openChangeModal(id: UUID, owner: Window? = null) {
        val view = find<ChangeFunctionFragment>(
            mapOf(
                ChangeFunctionFragment::functionId to id
            )
        )

        Stage().apply {
            scene = Scene(view.root)
            title = "Function parameters"
            initModality(Modality.WINDOW_MODAL)

            if (owner != null){
                initOwner(owner)
            }

            show()
        }
    }

    fun getAllFunctions() {
        fire(GetAllFunctionsQuery())
    }

    fun deleteFunction(functionId: UUID) {
        val event = DeleteFunctionQuery(functionId)
        fire(event)
    }
}