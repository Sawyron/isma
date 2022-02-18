package ru.nstu.grin.concatenation.file

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.nstu.grin.concatenation.canvas.model.ConcatenationCanvasModel
import ru.nstu.grin.concatenation.canvas.model.project.ProjectSnapshot
import ru.nstu.grin.concatenation.canvas.model.project.toModel
import ru.nstu.grin.concatenation.canvas.model.project.toSnapshot
import tornadofx.Controller
import tornadofx.Scope
import java.io.File

//TODO: implement descriptions and arrows saving
class CanvasProjectLoader(override val scope: Scope) : Controller() {
    private val model: ConcatenationCanvasModel by inject()

    fun save(path: File) {
        val project = ProjectSnapshot(
            spaces = model.cartesianSpaces.map { it.toSnapshot() }
        )

        path.bufferedWriter(Charsets.UTF_8).use {
            it.write(Json.encodeToString(project))
        }
    }

    fun load(path: File) {
        val json = path.readText(Charsets.UTF_8)
        val project = Json.decodeFromString<ProjectSnapshot>(json)

        model.cartesianSpaces.clear()
        model.cartesianSpaces.setAll(project.spaces.map { it.toModel() })
        model.descriptions.clear()
        model.arrows.clear()
    }
}