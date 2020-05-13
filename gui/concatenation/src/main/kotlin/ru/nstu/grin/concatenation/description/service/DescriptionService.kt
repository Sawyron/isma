package ru.nstu.grin.concatenation.description.service

import ru.nstu.grin.concatenation.canvas.model.ConcatenationCanvasModelViewModel
import ru.nstu.grin.concatenation.description.events.*
import tornadofx.Controller

class DescriptionService : Controller() {
    private val model: ConcatenationCanvasModelViewModel by inject()

    fun add(event: AddDescriptionEvent) {
        model.descriptions.add(event.description)
    }

    fun update(event: UpdateDescriptionEvent) {
        val description = model.descriptions.first { it.id == event.id }
        description.text = event.text
        description.textSize = event.textSize
        description.color = event.color
        description.font = event.font
        getAll()
    }

    fun get(event: GetDescriptionQuery) {
        val description = model.descriptions.first { it.id == event.id }
        fire(GetDescriptionEvent(description = description))
    }

    fun getAll() {
        fire(GetAllDescriptionsEvent(descriptions = model.descriptions))
    }

    fun delete(event: DeleteDescriptionQuery) {
        model.descriptions.removeIf { it.id == event.id }
    }
}