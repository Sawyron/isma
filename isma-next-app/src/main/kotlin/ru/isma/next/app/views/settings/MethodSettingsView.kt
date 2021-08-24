package ru.isma.next.app.views.settings

import javafx.scene.control.ScrollPane
import ru.isma.next.app.services.simualtion.SimulationParametersService
import ru.isma.next.app.views.controls.PropertiesGrid
import tornadofx.View

class MethodSettingsView(
    private val parametersService: SimulationParametersService
): View("Method") {
    override val root =
        ScrollPane(
            PropertiesGrid().apply {
                addComboBox(
                    "Method",
                    parametersService.integrationMethods,
                    parametersService.integrationMethod.selectedMethodProperty
                )
                addNode("Accurate", parametersService.integrationMethod.isAccuracyInUseProperty)
                addNode("Accuracy", parametersService.integrationMethod.accuracyProperty).apply {
                    disableProperty().bind(parametersService.integrationMethod.isAccuracyInUseProperty.not())
                }
                addNode("Stable", parametersService.integrationMethod.isStableInUseProperty)
                addNode("Parallel", parametersService.integrationMethod.isParallelInUseProperty)
                addNode("Server", parametersService.integrationMethod.serverProperty).apply {
                    disableProperty().bind(parametersService.integrationMethod.isParallelInUseProperty.not())
                }
                addNode("Port", parametersService.integrationMethod.portProperty).apply {
                    disableProperty().bind(parametersService.integrationMethod.isParallelInUseProperty.not())
                }
            }
        )
}