package ru.nstu.grin.concatenation.converters.model

import ru.nstu.grin.common.converters.Converter
import ru.nstu.grin.concatenation.dto.ConcatenationFunctionDTO
import ru.nstu.grin.concatenation.model.ConcatenationFunction

object ConcatenationFunctionConverter : Converter<ConcatenationFunctionDTO, ConcatenationFunction> {
    override fun convert(source: ConcatenationFunctionDTO): ConcatenationFunction {
        return ConcatenationFunction(
            name = source.name,
            points = source.points,
            functionColor = source.functionColor
        )
    }
}