package ru.nstu.grin.common.extensions

import javafx.scene.paint.Color
import java.io.DataInputStream
import java.io.InputStream
import java.io.ObjectInputStream
import java.nio.ByteBuffer

fun Color.toByteArray(): ByteArray {
    val byteBuffer = ByteBuffer.allocate(24)
    byteBuffer.putDouble(red)
    byteBuffer.putDouble(green)
    byteBuffer.putDouble(blue)
    return byteBuffer.array()
}

fun readColor(input : InputStream): Color {
    val dataInput = DataInputStream(input)
    val red = dataInput.readDouble()
    val green = dataInput.readDouble()
    val blue = dataInput.readDouble()
    return Color.color(red, green, blue)
}