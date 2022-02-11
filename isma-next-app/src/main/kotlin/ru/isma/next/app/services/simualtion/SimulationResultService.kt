package ru.isma.next.app.services.simualtion

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.FXCollections
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.stage.FileChooser
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.javafx.JavaFx
import ru.isma.next.app.models.simulation.CompletedSimulationModel
import ru.nstu.grin.integration.FunctionModel
import ru.nstu.grin.integration.GrinIntegrationFacade
import ru.nstu.grin.integration.PointModel
import ru.nstu.isma.intg.api.calcmodel.DaeSystem
import ru.nstu.isma.intg.api.models.IntgResultPoint
import tornadofx.FileChooserMode
import tornadofx.chooseFile
import java.io.File
import java.io.Writer

class SimulationResultService(private val grinIntegrationController: GrinIntegrationFacade) {
    val trackingTasksResults = FXCollections.observableArrayList<CompletedSimulationModel>()!!

    class ColumnPickerItem(val isSelected: BooleanProperty, val columnName: String)

    private val fileFilers = arrayOf(
        FileChooser.ExtensionFilter("Comma separate file", "*.csv")
    )

    suspend fun commitResult(result: CompletedSimulationModel) =
        withContext(Dispatchers.JavaFx) {
            trackingTasksResults.add(result)
        }

    suspend fun removeResult(result: CompletedSimulationModel) =
        withContext(Dispatchers.JavaFx) {
            trackingTasksResults.remove(result)
        }

    fun showChart(simulationResult: CompletedSimulationModel) {
        val headers = createColumnNamesArray(simulationResult)
        val columnPickerItems = headers.mapIndexed {
                i, item -> ColumnPickerItem(SimpleBooleanProperty(), item)
        }

        Dialog<ButtonType>().apply {
            title = "Select variables"
            dialogPane.content = ScrollPane(
                ListView(FXCollections.observableArrayList(columnPickerItems)).apply {
                    setCellFactory {
                        object : ListCell<ColumnPickerItem>() {
                            override fun updateItem(item: ColumnPickerItem?, empty: Boolean) {
                                if (empty || item == null || graphic != null) {
                                    return
                                }

                                graphic = HBox(
                                    CheckBox().apply {
                                        selectedProperty().bindBidirectional(item.isSelected)
                                    },
                                    Label(item.columnName)
                                ).apply {
                                    spacing = 10.0
                                }
                            }
                        }
                    }
                })

            dialogPane.buttonTypes.addAll(
                ButtonType("Ok", ButtonBar.ButtonData.OK_DONE),
                ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE),
            )
            showAndWait().ifPresent {
                if(it.buttonData == ButtonBar.ButtonData.OK_DONE){
                    val selctedHeaders = headers.filterIndexed { i, _ ->
                        columnPickerItems[i].isSelected.value
                    }
                    val selectedColumns = createResultColumns(simulationResult, headers.size).filterIndexed { i, _ ->
                        columnPickerItems[i].isSelected.value
                    }
                    val functions = selctedHeaders.mapIndexed { i, name ->
                        FunctionModel(name, selectedColumns[i].asIterable())
                    }
                    grinIntegrationController.openSimpleChart(functions)
                }
            }
        }
    }

    fun exportToFile(simulationResult: CompletedSimulationModel){
        val selectedFiles = chooseFile (filters = fileFilers, mode = FileChooserMode.Save)

        val file = selectedFiles.firstOrNull() ?: return

        ResultServiceScope.launch {
            exportToFileAsync(simulationResult, file)
        }
    }

    suspend fun exportToFileAsync(simulationResult: CompletedSimulationModel, file: File) = coroutineScope {
        launch(Dispatchers.IO) {
            file.bufferedWriter().use { writer ->
                val header = buildHeader(simulationResult)
                writer.write(header)
                writePoints(simulationResult, writer)
            }
        }
    }

    private suspend fun writePoints(result: CompletedSimulationModel, writer: Writer) = coroutineScope {
        result.resultPointProvider.results.collect { value ->
            writer.appendLine(value.toCsvLine())
        }
    }

    private fun IntgResultPoint.toCsvLine() : String {
        val builder = StringBuilder()

        builder.append(x).append(COMMA_AND_SPACE)

        // Дифференциальные переменные
        for (yForDe in yForDe) {
            builder.append(yForDe).append(COMMA_AND_SPACE)
        }

        // Алгебраические переменные
        for (yForAe in rhs[DaeSystem.RHS_AE_PART_IDX]) {
            builder.append(yForAe).append(COMMA_AND_SPACE)
        }

        // Правая часть
        for (f in rhs[DaeSystem.RHS_DE_PART_IDX]) {
            builder.append(f).append(COMMA_AND_SPACE)
        }

        // Удаляем последний пробел и запятую и заменяем на перенос строки
        builder.delete(builder.length - 2, builder.length)

        return builder.toString()
    }

    private fun createColumnNamesArray(result: CompletedSimulationModel) : Array<String> {
        val equationIndexProvider = result.equationIndexProvider
        val deCount = equationIndexProvider.getDifferentialEquationCount()
        val aeCount: Int = equationIndexProvider.getAlgebraicEquationCount()
        val outputArray = Array(deCount*2 + aeCount) { "" }

        for (i in 0 until deCount) {
            outputArray[i] = equationIndexProvider.getDifferentialEquationCode(i) ?: ""
        }

        var offset = deCount

        for (i in 0 until aeCount) {
            outputArray[i + offset] = equationIndexProvider.getAlgebraicEquationCode(i) ?: ""
        }

        offset = aeCount + deCount

        for (i in 0 until deCount) {
            outputArray[i + offset] = "f${i}"
        }

        return outputArray
    }

    private fun createResultColumns(result: CompletedSimulationModel, columnsCount: Int) : Array<Array<PointModel>> = runBlocking {
        val it = result.resultPointProvider.results.toList()
        val rowsCount = it.size
        val tempArray = Array(columnsCount) { Array(rowsCount) { PointModel.ZERO } }
        for (i in it.indices) {
            val x = it[i].x

            for (j in it[i].yForDe.indices) {
                tempArray[j][i] = PointModel(x, it[i].yForDe[j])
            }

            var offset = it[i].yForDe.size

            for (j in it[i].rhs[DaeSystem.RHS_AE_PART_IDX].indices) {
                tempArray[j + offset][i] = PointModel(x, it[i].rhs[DaeSystem.RHS_AE_PART_IDX][j])
            }

            offset = it[i].yForDe.size + it[i].rhs[DaeSystem.RHS_AE_PART_IDX].size

            for (j in it[i].rhs[DaeSystem.RHS_DE_PART_IDX].indices) {
                tempArray[j + offset][i] = PointModel(x, it[i].rhs[DaeSystem.RHS_DE_PART_IDX][j])
            }
        }

        return@runBlocking tempArray
    }

    companion object {
        private const val COMMA_AND_SPACE = ", "

        private val ResultServiceScope = CoroutineScope(Dispatchers.Default)

        private fun buildHeader(result: CompletedSimulationModel): String {
            val header = StringBuilder()

            // x
            header.append("x").append(COMMA_AND_SPACE)
            val equationIndexProvider = result.equationIndexProvider

            // Дифференциальные переменные
            val deCount = equationIndexProvider.getDifferentialEquationCount()
            for (i in 0 until deCount) {
                header.append(equationIndexProvider.getDifferentialEquationCode(i)).append(COMMA_AND_SPACE)
            }

            // Алгебраические переменные
            val aeCount: Int = equationIndexProvider.getAlgebraicEquationCount()
            for (i in 0 until aeCount) {
                header.append(equationIndexProvider.getAlgebraicEquationCode(i)).append(COMMA_AND_SPACE)
            }

            // Правая часть
            for (i in 0 until deCount) {
                header.append("f").append(i.toString()).append(COMMA_AND_SPACE)
            }

            // Удаляем последний пробел и запятую и заменяем на перенос строки
            header.delete(header.length - 2, header.length).appendLine()
            return header.toString()
        }
    }
}