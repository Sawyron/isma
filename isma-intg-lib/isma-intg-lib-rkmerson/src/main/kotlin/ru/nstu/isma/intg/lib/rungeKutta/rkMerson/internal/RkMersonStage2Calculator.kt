package ru.nstu.isma.intg.lib.rungeKutta.rkMerson.internal

import ru.nstu.isma.intg.api.methods.StageCalculator

/**
 * @author Mariya Nasyrova
 * @since 04.10.14
 */
class RkMersonStage2Calculator : StageCalculator() {
    override fun yk(step: Double, y: Double, f: Double, stages: DoubleArray): Double {
        return y + 1.0 / 3.0 * stages[0]
    }

    override fun k(step: Double, y: Double, f: Double, stages: DoubleArray, stagesY: Double, stagesF: Double): Double {
        return step * stagesF
    }
}
