package ru.nstu.grin.concatenation.file.utilities

import ru.nstu.grin.concatenation.function.model.FileType
import java.io.File

fun File.getFileType() =
    when (extension) {
        "xls" -> {
            FileType.XLS
        }
        "xlsx" -> {
            FileType.XLSX
        }
        "csv" -> {
            FileType.CSV
        }
        else -> {
            tornadofx.error("Неправильный формат файла")
            null
        }
    }