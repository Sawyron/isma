package ru.nstu.isma.next.core.sim.controller.services.runners

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.nstu.isma.intg.api.IntgMetricData
import ru.nstu.isma.intg.api.providers.AsyncFilePointProvider
import ru.nstu.isma.intg.api.utilities.IntegrationResultPointFileHelpers
import ru.nstu.isma.next.core.sim.controller.models.HybridSystemIntegrationResult
import ru.nstu.isma.next.core.sim.controller.models.HybridSystemSimulatorParameters
import ru.nstu.isma.next.core.sim.controller.models.SimulationParameters
import ru.nstu.isma.next.core.sim.controller.services.simulators.IHybridSystemSimulator
import java.io.File

class InFileSimulationRunner(
    private val hybridSystemSimulator: IHybridSystemSimulator,
) : ISimulationRunner {
    override suspend fun run(context: SimulationParameters): HybridSystemIntegrationResult = coroutineScope {
        val tempFile = withContext(Dispatchers.IO) {
            File.createTempFile("ismaSolverTempFile_", ".txt")
        }

        var result = IntgMetricData()

        var isFirst = true

        tempFile.bufferedWriter().use { writer ->
            channelFlow {
                val simulatorParameters = HybridSystemSimulatorParameters(
                    context.compilationResult,
                    context.simulationInitials,
                    stepChangeHandlers = context.stepChangeHandlers,
                    resultPointHandlers = {
                        launch {
                            send(it)
                        }
                    }
                )

                result = hybridSystemSimulator.runAsync(simulatorParameters)
            }.flowOn(Dispatchers.IO).collect {
                if (isFirst) {
                    writer.append(IntegrationResultPointFileHelpers.buildCsvHeader(it))
                    isFirst = false
                }
                writer.append(IntegrationResultPointFileHelpers.buildCsvString(it))
            }
        }

        val resultReader = AsyncFilePointProvider(tempFile)

        return@coroutineScope HybridSystemIntegrationResult(
            metricData = result,
            resultPointProvider = resultReader,
            equationIndexProvider = context.compilationResult.indexProvider,
        )
    }
}