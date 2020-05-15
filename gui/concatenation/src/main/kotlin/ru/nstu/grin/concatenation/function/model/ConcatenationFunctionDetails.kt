package ru.nstu.grin.concatenation.function.model

sealed class ConcatenationFunctionDetails

data class MirrorDetails(
    val isMirrorX: Boolean = false,
    val isMirrorY: Boolean = false
) : ConcatenationFunctionDetails()

data class DerivativeDetails(
    val degree: Int,
    val type: DerivativeType
) : ConcatenationFunctionDetails()