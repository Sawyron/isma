import tornadofx.*
import views.IsmaErrorListTable
import views.IsmaMenuBar
import views.IsmaToolBar
import views.SimulationProcessBar
import views.editors.tabpane.IsmaEditorTabPane
import views.simulation.settings.SettingsPanelView

class MainView : View() {
    private val ismaMenuBar: IsmaMenuBar by di()
    private val ismaToolBar: IsmaToolBar by inject()
    private val ismaErrorListTable: IsmaErrorListTable by inject()
    private val ismaEditorTabPane: IsmaEditorTabPane by inject()
    private val simulationProcess: SimulationProcessBar by inject()
    private val settingsPanel: SettingsPanelView by inject()

    init {
        title = "ISMA Next"
    }

    override val root = borderpane {
        minHeight = 480.0
        minWidth = 640.0

        top {
            vbox {
                add(ismaMenuBar)
                add(ismaToolBar)
            }
        }

        center {
            add(ismaEditorTabPane)
        }

        bottom {
            borderpane {
                top = drawer {
                    item("Error list") {
                        add(ismaErrorListTable)
                    }
                }
                bottom = vbox {
                    add(simulationProcess)
                }
            }
        }

        right {
            add(settingsPanel)
        }
    }
}