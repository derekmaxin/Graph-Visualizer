import javafx.application.Application
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Arc
import javafx.scene.shape.ArcType
import javafx.scene.text.Font
import javafx.stage.Stage
import java.lang.Math.sqrt
import kotlin.random.Random


val root = BorderPane()

object Model {
    var activeView = "LINE"

    // the data structure used for the graphs and their data is a map,
    // where each key is a unique string name identifier and the value
    // is an array of graph values as doubles
    private var dataSets = mutableMapOf<String, DoubleArray>("quadratic" to doubleArrayOf(0.1, 1.0, 4.0, 9.0, 16.0),
        "negative quadratic" to doubleArrayOf(-0.1, -1.0, -4.0, -9.0, -16.0),
        "alternating" to doubleArrayOf(-1.0, 3.0, -1.0, 3.0, -1.0, 3.0),
        "random" to (List(20) { Random.nextDouble(-100.0, 100.0)}.toDoubleArray()),
        "inflation ‘90-‘22" to doubleArrayOf(4.8, 5.6, 1.5, 1.9, 0.2, 2.1, 1.6, 1.6, 1.0, 1.7, 2.7, 2.5, 2.3, 2.8, 1.9, 2.2, 2.0, 2.1, 2.4, 0.3, 1.8, 2.9, 1.5, 0.9, 1.9, 1.1, 1.4, 1.6, 2.3, 1.9, 0.7, 3.4, 6.8)
    )

    var curActiveDataSet = "quadratic"

    fun getAllDataSets() : Map<String, DoubleArray> {
        return dataSets
    }

    fun setActiveDataSet(newActiveDataSetName: String){
        curActiveDataSet = newActiveDataSetName;
        refresh(activeView)
    }

    fun getActiveDataSetName() : String {
        return curActiveDataSet
    }

    fun getActiveDataSetValues() : DoubleArray {
        return dataSets[curActiveDataSet]!! //!! non null assertion
    }

    //adds a new value of 0.0 to the current active dataset
    fun addNewEntry(){
        dataSets[curActiveDataSet] = dataSets.get(curActiveDataSet)!!.plus(0.0)
        refresh(activeView)
        //print(dataSets[curActiveDataSet]!!.size) //for testing
    }

    //updates the data entry at the given index of the current active data set
    //if not a valid entry (ie not a number, ignore instead)
    fun updateDataEntry(index : Int, updatedValue: String) {
        if (updatedValue.toDoubleOrNull() != null){
            dataSets[curActiveDataSet]!![index] = updatedValue.toDouble()
            refresh(activeView)
        }
    }

    //removed the data entry at the given index of the current active data set
    fun removeDataEntry(index : Int){

        var myList = dataSets[curActiveDataSet]!!.toMutableList()
        myList.removeAt(index)
        dataSets[curActiveDataSet] = myList.toDoubleArray()

        //print(dataSets[curActiveDataSet]!!.size) //for testing
        refresh(activeView)
    }

    //create new data set and make it the active data set
    fun createNewDataSet(newDataSetName : String){
        dataSets = dataSets.plus(newDataSetName to doubleArrayOf(0.0)) as MutableMap<String, DoubleArray>
        setActiveDataSet(newDataSetName)
        //print(dataSets.size) //for testing
        refresh(activeView)
    }

    fun getNumActiveDataSetValues(): Int {
        return dataSets[curActiveDataSet]!!.size
    }

    fun setNewActiveView(newActiveView : String) {
        activeView = newActiveView
        refresh(activeView)
    }

    fun getActiveViewName() : String{
        return activeView
    }

    fun checkActiveDataSetHasNeg() : Boolean{
        dataSets[curActiveDataSet]!!.forEachIndexed { _, element ->
            if (element < 0.0) {
                return true;
            }
        }
        return false
    }
}

class LineGraphView() : Canvas() {
    init {

        var myList = Model.getActiveDataSetValues().toMutableList()
        val maxVal = (myList.maxOrNull() ?: 0.0) + 1
        val minVal = (myList.minOrNull() ?: 0.0) - 1
        //println("minVal: " + minVal) //for testing

        //constants
        val canvasWidth = 620.0
        val canvasHeight = 520.0

        val numDataPoints = Model.getNumActiveDataSetValues()
        val xDist = (canvasWidth - 20)/numDataPoints //evenly space the x values (with 10 padding each side)

        var scalingRatio = 0.0
        if (maxVal - minVal != 0.0){
            scalingRatio = canvasHeight/(maxVal - minVal)
        }
        //println("scalingRatio: " + scalingRatio) //for testing

        //LINE GRAPH
        this.apply{
            width = canvasWidth
            height = canvasHeight
            graphicsContext2D.apply{
                Model.getActiveDataSetValues().forEachIndexed { index, element ->
                    if (index < Model.getNumActiveDataSetValues() - 1) {
                        stroke = Color.BLACK
                        lineWidth = 2.0
                        strokeLine(
                            (index) * xDist + 10,
                            (canvasHeight - (scalingRatio * (element - minVal))),
                            (index + 1) * xDist + 10,
                            canvasHeight - (scalingRatio * (Model.getActiveDataSetValues()[index + 1] - minVal))
                        )
                    }
                    stroke = Color.RED
                    strokeOval((index) * xDist  + 10,  (canvasHeight - (scalingRatio * (element - minVal))),2.0,2.0)
                }
            }
        }
        //END OF LINE GRAPH
    }
}

class BarGraphView() : Canvas() {
    init {

        var myList = Model.getActiveDataSetValues().toMutableList()
        val maxVal = (myList.maxOrNull() ?: 0.0) + 1
        val minVal = (myList.minOrNull() ?: 0.0) - 1
        //println("minVal: " + minVal) //for testing

        //constants
        val canvasWidth = 620.0
        val canvasHeight = 520.0

        val numDataPoints = Model.getNumActiveDataSetValues()
        val xDist = (canvasWidth - 20)/numDataPoints //evenly space the x values (with 10 padding each side)

        var scalingRatio = 0.0
        if (maxVal - minVal != 0.0){
            scalingRatio = canvasHeight/(maxVal - minVal)
        }
        //println("scalingRatio: " + scalingRatio) //for testing


        //BAR GRAPH

        this.apply{
            width = canvasWidth
            height = canvasHeight
            graphicsContext2D.apply{
                Model.getActiveDataSetValues().forEachIndexed { index, element ->
                    if (index % 2 == 0) stroke = Color.RED else stroke = Color.ORANGE
                    lineWidth = xDist/3
                    if (Model.getActiveDataSetValues()[index] != 0.0) {
                        if (Model.getActiveDataSetValues()[index] > 0.0) {
                            strokeLine(
                                (index) * xDist + 10 + (xDist / 3) / 2,
                                canvasHeight - (scalingRatio * (0 - minVal)) - (xDist / 3) / 2,
                                (index) * xDist + 10 + (xDist / 3) / 2,
                                canvasHeight - (scalingRatio * (element - minVal)) - (xDist / 3) / 2
                            )
                        } else {
                            strokeLine(
                                (index) * xDist + 10 + (xDist / 3) / 2,
                                canvasHeight - (scalingRatio * (0 - minVal)) + (xDist / 3) / 2,
                                (index) * xDist + 10 + (xDist / 3) / 2,
                                canvasHeight - (scalingRatio * (element - minVal)) + (xDist / 3) / 2
                            )
                        }
                    }
                }
                stroke = Color.BLACK
                lineWidth = 2.0
                //0.0 line
                strokeLine(
                    10.0,
                    canvasHeight - (scalingRatio * (0 - minVal)),
                    canvasWidth - 10.0,
                    canvasHeight - (scalingRatio * (0 - minVal))
                )
            }
        }
        //END OF BAR GRAPH
    }
}

class SemGraphView() : Canvas() {
    init {

        var myList = Model.getActiveDataSetValues().toMutableList()
        val maxVal = (myList.maxOrNull() ?: 0.0) + 1
        val minVal = (myList.minOrNull() ?: 0.0) - 1
        //println("minVal: " + minVal) //for testing

        //constants
        val canvasWidth = 620.0
        val canvasHeight = 520.0

        val numDataPoints = Model.getNumActiveDataSetValues()
        val xDist = (canvasWidth - 20)/numDataPoints //evenly space the x values (with 10 padding each side)

        var scalingRatio = 0.0
        if (maxVal - minVal != 0.0){
            scalingRatio = canvasHeight/(maxVal - minVal)
        }
        //println("scalingRatio: " + scalingRatio) //for testing

        //SEM GRAPH

        val mean = Model.getActiveDataSetValues().average()

        var semCalc = 0.0
        Model.getActiveDataSetValues().forEachIndexed { index, element ->
            semCalc += (element - mean) * (element - mean) //for each number: subtract the Mean and square the result.
        }
        semCalc /= numDataPoints // Then work out the mean of those squared differences.
        semCalc = sqrt(semCalc) //Take the square root of that and we are done!

        this.apply{
            width = canvasWidth
            height = canvasHeight
            graphicsContext2D.apply{
                Model.getActiveDataSetValues().forEachIndexed { index, element ->
                    if (index % 2 == 0) stroke = Color.RED else stroke = Color.ORANGE
                    lineWidth = xDist/3
                    if (Model.getActiveDataSetValues()[index] != 0.0) {
                        if (Model.getActiveDataSetValues()[index] > 0.0) {
                            strokeLine(
                                (index) * xDist + 10 + (xDist / 3) / 2,
                                canvasHeight - (scalingRatio * (0 - minVal)) - (xDist / 3) / 2,
                                (index) * xDist + 10 + (xDist / 3) / 2,
                                canvasHeight - (scalingRatio * (element - minVal)) - (xDist / 3) / 2
                            )
                        } else {
                            strokeLine(
                                (index) * xDist + 10 + (xDist / 3) / 2,
                                canvasHeight - (scalingRatio * (0 - minVal)) + (xDist / 3) / 2,
                                (index) * xDist + 10 + (xDist / 3) / 2,
                                canvasHeight - (scalingRatio * (element - minVal)) + (xDist / 3) / 2
                            )
                        }
                    }
                }
                //0.0 line
                stroke = Color.BLACK
                lineWidth = 2.0
                strokeLine(
                    10.0,
                    canvasHeight - (scalingRatio * (0 - minVal)),
                    canvasWidth - 10.0,
                    canvasHeight - (scalingRatio * (0 - minVal))
                )
                //mean line
                stroke = Color.GREY
                lineWidth = 5.0
                strokeLine(
                    10.0,
                    canvasHeight - (scalingRatio * (mean - minVal)) - (xDist / 3) / 2,
                    canvasWidth - 10.0,
                    canvasHeight - (scalingRatio * (mean - minVal)) - (xDist / 3) / 2
                )
                //upper SEM line
                setLineDashes(7.0, 15.0)
                strokeLine(
                    10.0,
                    canvasHeight - (scalingRatio * (mean + semCalc - minVal)) - (xDist / 3) / 2,
                    canvasWidth - 10.0,
                    canvasHeight - (scalingRatio * (mean + semCalc - minVal)) - (xDist / 3) / 2
                )
                //lower SEM line
                strokeLine(
                    10.0,
                    canvasHeight - (scalingRatio * (mean - semCalc - minVal)) - (xDist / 3) / 2,
                    canvasWidth - 10.0,
                    canvasHeight - (scalingRatio * (mean - semCalc - minVal)) - (xDist / 3) / 2
                )
                setLineDashes(0.0, 0.0)
                stroke = Color.BLACK
                lineWidth = 1.5
                font = Font.font("Arial", 12.0)
                fillText("mean: " + mean + "\n" + "SEM: " + semCalc,  10.0, 10.0, 1000.0)
            }
        }

        //END OF SEM GRAPH
    }
}

class PieGraphView() : Pane() {
    init {
        background = Background(BackgroundFill(Color.WHITE, CornerRadii(0.0), null))
        //fill = Color.WHITE
        var total = 0.0
        Model.getActiveDataSetValues().forEachIndexed { index, element ->
            total += element
        }
        val lowerArc = Arc().apply {
            centerX = 100.0
            centerY = 100.0
            radiusX = 220.0
            radiusY = 220.0
            startAngle = 0.0
            length = 360.0
            fill = Color.WHITE
        }
        this.children.add(lowerArc)

        var curStartAngle = 0.0
        var curColor = 0.0
        Model.getActiveDataSetValues().forEachIndexed { index, element ->
            val arc = Arc().apply{
                centerX = 250.0
                centerY = 250.0
                radiusX = 200.0
                radiusY = 200.0
                startAngle = curStartAngle
                curStartAngle += 360.0 * element/total
                length = 360.0 * element/total
                //print(element/total)//for testing
                type = ArcType.ROUND
                fill = Color.color(index * curColor,0.0,0.0)
                curColor = (100.0/Model.getNumActiveDataSetValues())/100.0
                //println("curcolor:" + curColor + " ") //for testing
                //println("model:" + Model.getNumActiveDataSetValues() + " ") //for testing
            }
            this.children.add(arc)
        }

    }
}


class ToolBarController() : ToolBar() {
    init {
        //CHANGE ACTIVE DATASET COMBOBOX
        val dataSetsComboBox: ComboBox<String> = ComboBox()
        dataSetsComboBox.items.addAll(
            Model.getAllDataSets().keys
        )
        dataSetsComboBox.promptText = Model.getActiveDataSetName()
        this.items.add(dataSetsComboBox)

        dataSetsComboBox.onAction = EventHandler {
            Model.setActiveDataSet(dataSetsComboBox.value)
            if (Model.checkActiveDataSetHasNeg()) {
                if (Model.getActiveViewName() == "SEM" || Model.getActiveViewName() == "PIE") Model.setNewActiveView("LINE")
            }
        }

        //SEPERATOR
        val separator1 = Separator().apply{
            padding = Insets(0.0, 5.0, 0.0, 5.0)
        }
        this.items.add(separator1)

        //ADD NEW DATASET
        val createDataSetTextField = TextField().apply{
            promptText = "Data set name"
        }
        val createDataSetButton = Button("Create")

        createDataSetButton.onAction = EventHandler {
            Model.createNewDataSet(createDataSetTextField.text)
            createDataSetTextField.text = ""
        }

        this.items.add(createDataSetTextField)
        this.items.add(createDataSetButton)

        //SEPERATOR
        val separator2 = Separator().apply{
            padding = Insets(0.0, 5.0, 0.0, 5.0)
        }
        this.items.add(separator2)

        //GRAPH VIEW BUTTONS
        val graphViewButtonToggleGroup = ToggleGroup()
        val lineView = RadioButton("Line").apply{
            toggleGroup = graphViewButtonToggleGroup
            prefWidth = 70.0
            if (Model.getActiveViewName() == "LINE") isDisable = true
        }
        val barView = RadioButton("Bar").apply{
            toggleGroup = graphViewButtonToggleGroup
            prefWidth = 70.0
            if (Model.getActiveViewName() == "BAR") isDisable = true
        }
        val barSEMView = RadioButton("Bar (SEM)").apply{
            toggleGroup = graphViewButtonToggleGroup
            prefWidth = 70.0
            if (Model.getActiveViewName() == "SEM") isDisable = true
            if (Model.checkActiveDataSetHasNeg()) isDisable = true
        }
        val pieView = RadioButton("Pie").apply{
            toggleGroup = graphViewButtonToggleGroup
            prefWidth = 70.0
            if (Model.getActiveViewName() == "PIE") isDisable = true
            if (Model.checkActiveDataSetHasNeg()) isDisable = true
        }
        lineView.getStyleClass().remove("radio-button");
        lineView.getStyleClass().add("toggle-button");
        barView.getStyleClass().remove("radio-button");
        barView.getStyleClass().add("toggle-button");
        barSEMView.getStyleClass().remove("radio-button");
        barSEMView.getStyleClass().add("toggle-button");
        pieView.getStyleClass().remove("radio-button");
        pieView.getStyleClass().add("toggle-button");

        lineView.onAction = EventHandler { Model.setNewActiveView("LINE") }
        barView.onAction = EventHandler { Model.setNewActiveView("BAR") }
        barSEMView.onAction = EventHandler { Model.setNewActiveView("SEM") }
        pieView.onAction = EventHandler { Model.setNewActiveView("PIE") }

        val graphViewButtonHBox= HBox(lineView, barView, barSEMView, pieView).apply{ //
            padding = Insets(5.0)
            spacing = 5.0
        }
        this.items.add(graphViewButtonHBox)
    }

}

class DataController: VBox() {
    init{
        this.minWidth = 300.0
        this.prefWidth = 300.0 //doesn't work
        padding = Insets(2.5)
        this.children.add(Label("Dataset name: " + Model.getActiveDataSetName()).apply{padding = Insets(5.0)})
        Model.getActiveDataSetValues().forEachIndexed { index, element ->

            var dataValueText = TextField("$element").apply{ minWidth = 200.0}
            dataValueText.onAction = EventHandler {
                Model.updateDataEntry(index, dataValueText.text)
                if (Model.checkActiveDataSetHasNeg()) {
                    if (Model.getActiveViewName() == "SEM" || Model.getActiveViewName() == "PIE") Model.setNewActiveView("LINE")
                }
            }

            val XButton = Button(" X ").apply{
                padding = Insets(5.0)
                spacing = 5.0
                isDisable = (Model.getNumActiveDataSetValues() == 1)
            }
            XButton.onAction = EventHandler { Model.removeDataEntry(index) }

            this.children.add(HBox(Label("Entry #$index"), dataValueText, XButton).apply{
                padding = Insets(5.0)
                spacing = 5.0
            })
        }
        val AddEntryButton = Button("Add Entry").apply{
            HBox.setHgrow(this, Priority.ALWAYS)
            prefWidth = 290.0
            padding = Insets(5.0)
        }
        AddEntryButton.onAction = EventHandler{
            Model.addNewEntry()
        }
        this.children.add(AddEntryButton)
    }
}

fun refresh(curView : String){
    var graphView : Node
    //print(curView)//for testing
    if (curView == "LINE"){
        graphView = LineGraphView()
    } else if (curView == "BAR"){
        graphView = BarGraphView()
    } else if (curView == "SEM") {
        graphView = SemGraphView()
    } else {
        graphView = PieGraphView()
    }
    root.top = ToolBarController()
    root.center = SplitPane(ScrollPane(DataController()).apply{
        minWidth = 300.0
        maxWidth = 300.0
    }, graphView).apply{
        setDividerPositions(0.3, 0.7) //play around with
    }
}


class Main : Application()  {
    override fun start(stage: Stage) {
        val graphView = LineGraphView()
        val dataCTRL = DataController()
        val toolBarCTRL = ToolBarController()

        //initial render
        root.top = toolBarCTRL
        root.center = SplitPane(dataCTRL,graphView).apply{
            setDividerPositions(0.3, 0.7) //play around with
        }

        stage.apply {
            title = "CS349 - A2 Graphs - dmaxin"
            scene = Scene(root , 800.0, 600.0)
            minWidth = 640.0
            minHeight = 480.0
        }.show ()
    }
}
