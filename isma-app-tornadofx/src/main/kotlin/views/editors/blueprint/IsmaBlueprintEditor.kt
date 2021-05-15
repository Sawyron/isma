package views.editors.blueprint

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import tornadofx.*
import views.editors.blueprint.constants.INIT_STATE
import views.editors.blueprint.constants.MAIN_STATE
import views.editors.blueprint.controls.StateBox
import views.editors.blueprint.controls.StateTransactionArrow
import views.editors.blueprint.models.*
import views.editors.text.IsmaTextEditor

class IsmaBlueprintEditor: Fragment() {
    private val isRemoveStateModeProperty = SimpleBooleanProperty(false)
    private val isRemoveTransactionModeProperty = SimpleBooleanProperty(false)
    private val isAddTransactionModeProperty = SimpleBooleanProperty(false)
    private val addTransactionStateCounterProperty = SimpleIntegerProperty(0)

    private var activeStateBox: StateBox? = null
    private var statesToLink = arrayOf<StateBox?>(null, null)

    private var xOffset = 0.0;
    private var yOffset = 0.0;

    private fun isRemoveStateModeProperty() = isRemoveStateModeProperty
    private fun isRemoveTransactionModeProperty() = isRemoveTransactionModeProperty
    private fun isAddTransactionModeProperty() = isAddTransactionModeProperty
    private fun addTransactionStateCounterProperty() = addTransactionStateCounterProperty

    private var isRemoveStateMode by isRemoveStateModeProperty
    private var isRemoveTransactionMode by isRemoveTransactionModeProperty
    private var isAddTransactionMode by isAddTransactionModeProperty
    private var addTransactionStateCounter by addTransactionStateCounterProperty

    private val transactions = ArrayList<BlueprintEditorTransactionModel>()
    private val stateBoxes = ArrayList<StateBox>()
    private val mainStateBox: StateBox = createMainStateBox()
    private val initStateBox: StateBox = createInitStateBox()

    private val canvas = pane {
        addMainStateBox()
        addInitStateBox()

        addEventHandler(MouseEvent.MOUSE_DRAGGED) {
            if(it.isPrimaryButtonDown && activeStateBox != null && !isRemoveStateMode) {
                moveStateBox(activeStateBox!!, it.x + xOffset, it.y + yOffset)
            }
        }
    }

    private val tabs = tabpane {
        tab ("Blueprint") {
            add(canvas)
        }
    }

    override val root = borderpane {
        center = tabs
        bottom = toolbar {
            button {
                action {
                    resetEditorMode()
                    addStateBox()
                }

                text = "New state"

                disableClose()
            }
            button {
                action {
                    if(isAddTransactionMode) {
                        resetEditorMode()
                    } else {
                        resetEditorMode()
                        isAddTransactionMode = true
                        addTransactionStateCounter = 0
                    }
                }

                text = "New transition"

                isAddTransactionModeProperty().onChange {
                    text = if(it) {
                        "Stop adding transaction"
                    } else {
                        "New transition"
                    }
                }
            }
            separator()
            button {
                action {
                    if (isRemoveStateMode) {
                        resetEditorMode()
                    } else {
                        resetEditorMode()
                        isRemoveStateMode = true
                    }
                }

                text = "Remove state"

                isRemoveStateModeProperty().onChange {
                    text = if(it){
                        "Stop remove state"
                    } else {
                        "Remove state"
                    }
                }
            }
            button {
                action {
                    if (isRemoveTransactionMode) {
                        resetEditorMode()
                    } else {
                        resetEditorMode()
                        isRemoveTransactionMode = true
                    }
                }
                text = "Remove transition"

                isRemoveTransactionModeProperty().onChange {
                    text = if(it){
                        "Stop remove transition"
                    } else {
                        "Remove transition"
                    }
                }
            }
        }
    }

    public fun getBlueprintModel() : BlueprintModel {
        val main = mainStateBox.toBlueprintState()
        val init = initStateBox.toBlueprintState()
        val states = stateBoxes.map { it.toBlueprintState() }.toTypedArray()

        val blueprintTransactions = transactions
            .map {
                BlueprintTransactionModel(
                    startStateName = it.startStateBox.name,
                    endStateName = it.endStateBox.name,
                    predicate = it.transactionArrow.text
                )
            }
            .toTypedArray()

        return BlueprintModel(main, init, states, blueprintTransactions)
    }

    public fun setBlueprintModel(model: BlueprintModel) {
        stateBoxes.toTypedArray().forEach { it.removeFromEditor() }

        mainStateBox.applyBlueprintState(model.main)
        initStateBox.applyBlueprintState(model.init)

        val stateBoxes = model.states.associateBy({ it.name }, {instantiateStateBoxFromBlueprintState(it)})

        model.transactions.forEach {
            addTransactionArrow(stateBoxes[it.startStateName]!!, stateBoxes[it.endStateName]!!, it.predicate)
        }
    }

    private fun StateBox.toBlueprintState() : BlueprintStateModel {
        return BlueprintStateModel(
            this.translateXProperty().value,
            this.translateYProperty().value,
            this.name,
            this.text
        )
    }

    private fun StateBox.applyBlueprintState(blueprintState: BlueprintStateModel){
        this.apply {
            translateXProperty().value = blueprintState.canvasPositionX
            translateYProperty().value = blueprintState.canvasPositionY
            name = blueprintState.name
            text = blueprintState.text
        }
    }

    private fun instantiateStateBoxFromBlueprintState(blueprintState: BlueprintStateModel) : StateBox {
        return instantiateStateBox(
            positionX = blueprintState.canvasPositionX,
            positionY = blueprintState.canvasPositionY,
            stateName = blueprintState.name,
            stateText = blueprintState.text,
        )
    }

    private fun instantiateStateBox(
        positionX: Double = 0.0,
        positionY: Double = 0.0,
        stateName: String = "",
        stateText: String = "",
    ) : StateBox {
        val stateBox = find<StateBox> {
            color = Color.CORAL

            addEditActionListener {
                openStateTextEditorTab(this)
            }

            initMouseMovingEvents()
            initMouseRemoveStateEvents()
            initMouseLinkTransactionEvents()

            translateXProperty().value = positionX
            translateYProperty().value = positionY
            name = stateName
            text = stateText

            isEditableProperty().bind((isRemoveStateModeProperty).or(isAddTransactionModeProperty).not())
        }

        stateBoxes.add(stateBox)
        canvas.add(stateBox)

        return stateBox
    }

    private fun addStateBox() {
        instantiateStateBox(10.0, 200.0)
    }

    private fun addTransactionArrow(startStateBox: StateBox, endStateBox: StateBox, predicate: String = "") {
        if(transactions.any { it.startStateBox == startStateBox && it.endStateBox == endStateBox }) {
            return
        }

        val transactionArrow = find<StateTransactionArrow> {
            startXProperty().bind(startStateBox.centerXProperty())
            startYProperty().bind(startStateBox.centerYProperty())
            endXProperty().bind(endStateBox.centerXProperty())
            endYProperty().bind(endStateBox.centerYProperty())

            text = predicate

            initMouseRemoveTransactionEvents()
        }

        canvas.add(transactionArrow)

        transactions.add(BlueprintEditorTransactionModel(startStateBox, endStateBox, transactionArrow))
    }

    private fun StateBox.removeFromEditor() {
        transactions
            .asSequence()
            .filter { it.startStateBox == this || it.endStateBox == this }
            .toList()
            .forEach { removeTransaction(it) }

        this.removeFromParent()
    }

    private fun StateTransactionArrow.removeFromEditor() {
        transactions
            .filter { it.transactionArrow == this }
            .toList()
            .forEach { removeTransaction(it) }

        this.removeFromParent()
    }

    private fun removeTransaction(transaction: BlueprintEditorTransactionModel) {
        transactions.remove(transaction)
        transaction.transactionArrow.removeFromParent()
    }

    private fun createMainStateBox() : StateBox {
        return find {
            color = Color.LIGHTGREEN
            isEditable = false
            squareHeight = 60.0
            name = MAIN_STATE
            translateXProperty() += 10
            translateYProperty() += 10

            addEditActionListener { openMainTextEditorTab() }

            initMouseMovingEvents()
        }
    }

    private fun createInitStateBox() : StateBox {
        return find {
            color = Color.LIGHTBLUE
            isEditButtonVisible = false
            isEditable = false
            squareHeight = 60.0
            name = INIT_STATE
            translateXProperty() += 10
            translateYProperty() += 100

            initMouseMovingEvents()
            initMouseLinkTransactionEvents()
        }
    }

    private fun Pane.addMainStateBox() {
        add(mainStateBox)
    }

    private fun Pane.addInitStateBox() {
        add(initStateBox)
    }

    private fun moveStateBox(stateBox: StateBox, positionX: Double, positionY: Double) {
        stateBox.translateXProperty().value = positionX
        stateBox.translateYProperty().value = positionY
    }

    private fun resetEditorMode() {
        isRemoveStateMode = false
        isRemoveTransactionMode = false
        isAddTransactionMode = false
    }

    private fun openStateTextEditorTab(state: StateBox) {
        tabs.tab(state.name) {
            add<IsmaTextEditor> {
                replaceText(state.text)
                state.textProperty().bind(textProperty())
            }
            textProperty().bind(state.nameProperty())
        }
    }

    private fun openMainTextEditorTab() {
        tabs.tab("Main") {
            add<IsmaTextEditor> {
                replaceText(mainStateBox.text)
                mainStateBox.textProperty().bind(textProperty())
            }
        }
    }

    private fun StateBox.initMouseRemoveStateEvents() {
        addMousePressedListener { it, _ ->
            if(!isRemoveStateMode){
                return@addMousePressedListener
            }
            it.removeFromEditor()
        }
    }

    private fun StateTransactionArrow.initMouseRemoveTransactionEvents() {
        addMousePressedListener { it, _ ->
            if(!isRemoveTransactionMode){
                return@addMousePressedListener
            }
            it.removeFromEditor()
        }
    }

    private fun StateBox.initMouseMovingEvents() {
        addMousePressedListener { it, event ->
            if(isRemoveStateMode || isAddTransactionMode){
                return@addMousePressedListener
            }

            xOffset = -event.x
            yOffset = -event.y
            activeStateBox = it
        }
        addMouseReleasedListener { _, _ ->
            activeStateBox = null
        }
    }

    private fun StateBox.initMouseLinkTransactionEvents() {
        addMouseClickedListeners { it, _ ->
            if (!isAddTransactionMode || isRemoveStateMode) {
                return@addMouseClickedListeners
            }

            statesToLink[addTransactionStateCounter++] = it

            if (addTransactionStateCounter > 1) {
                addTransactionArrow(statesToLink[0]!!, statesToLink[1]!!)
                isAddTransactionMode = false
            }

            return@addMouseClickedListeners
        }
    }
}