package ru.isma.next.app.services.simualtion

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import ru.isma.next.app.services.project.LismaPdeService
import ru.isma.next.app.services.project.ProjectService
import ru.isma.next.common.services.lisma.models.FailedTranslation
import ru.isma.next.common.services.lisma.models.SuccessTranslation
import ru.nstu.isma.intg.api.calcmodel.cauchy.CauchyInitials
import ru.nstu.isma.next.core.sim.controller.contracts.ISimulationCoreController
import ru.nstu.isma.next.core.sim.controller.models.IntegratorApiParameters
import ru.nstu.isma.next.core.sim.controller.parameters.EventDetectionParameters
import tornadofx.getValue
import tornadofx.setValue
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.math.max
import kotlin.math.min

class SimulationService(
    private val projectService: ProjectService,
    private val lismaPdeService: LismaPdeService,
    private val simulationParametersService: SimulationParametersService,
    private val simulationResult: SimulationResultService,
    private val simulationController: ISimulationCoreController,
) {
    private val progressProperty = SimpleDoubleProperty()
    fun progressProperty() = progressProperty
    var progress by progressProperty

    private val isSimulationInProgressProperty = SimpleBooleanProperty()
    fun isSimulationInProgressProperty() = isSimulationInProgressProperty
    var isSimulationInProgress by isSimulationInProgressProperty

    private var currentSimulationJob: Job? = null

    fun simulate() {
        currentSimulationJob = SimulationScope.launch {
            try {
                simulateAsyncInternal()
            }
            finally {
                resetState()
            }
        }
    }

    private suspend fun simulateAsyncInternal() = coroutineScope {
        val sourceCode = projectService.activeProject?.snapshot() ?: return@coroutineScope

        when (val translationResult = lismaPdeService.translateLisma(sourceCode)) {
            is FailedTranslation -> { }
            is SuccessTranslation -> {
                val initials = createCauchyInitials()

                translationResult.hsm.initTimeEquation(initials.start)

                val context = IntegratorApiParameters(
                    hsm = translationResult.hsm,
                    initials = createCauchyInitials(),
                    eventDetectionParameters = createEventDetectionParameters(),
                    stepChangeHandlers = arrayListOf(
                        {
                            withContext(Dispatchers.JavaFx) {
                                progress = normalizeProgress(initials.start, initials.end, it)
                            }
                        }
                    )
                )

                withContext(Dispatchers.JavaFx) {
                    isSimulationInProgress = true
                }

                val result = simulationController.simulateAsync(context)

                withContext(Dispatchers.JavaFx) {
                    simulationResult.simulationResult = result
                }
            }
        }
    }

    fun stopCurrentSimulation() {
        currentSimulationJob?.cancel()
    }

    private fun resetState(){
        currentSimulationJob = null
        isSimulationInProgress = false
        progress = 0.0
    }

    private fun createCauchyInitials(): CauchyInitials {
        return CauchyInitials().apply {
            start = simulationParametersService.cauchyInitials.startTime
            end = simulationParametersService.cauchyInitials.endTime
            stepSize = simulationParametersService.cauchyInitials.step
        }
    }

    private fun createEventDetectionParameters(): EventDetectionParameters? {
        val eventDetectionParams = simulationParametersService.eventDetection

        return if (eventDetectionParams.isEventDetectionInUse) {
            val stepLowerBound = if (eventDetectionParams.isStepLimitInUse) eventDetectionParams.lowBorder else 0.0

            EventDetectionParameters(eventDetectionParams.gamma, stepLowerBound)
        } else {
            null
        }
    }

    companion object {
        private val SimulationSupervisorJob = SupervisorJob()

        val SimulationScope = CoroutineScope(EmptyCoroutineContext + SimulationSupervisorJob)

        private fun normalizeProgress(start: Double, end: Double, current: Double): Double{
            return max(0.0, min(1.0, (current - start) / (end-start)))
        }
    }
}