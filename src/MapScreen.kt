import java.awt.*
import java.awt.event.*
import java.io.File
import java.util.*
import javax.swing.ImageIcon
import javax.swing.JPanel
import javax.swing.Timer
import kotlin.collections.ArrayList
import kotlin.system.exitProcess


//The map screen of the program
//Allows the user to draw lines onto a map and stores the information into a MutableList
class MapScreen : JPanel(), ActionListener {

    //Width of the panel
    private val panelWidth = 1000
    private val panelHeight = 500

    //Offsets for scrolling
    private var xOffset = 0
    private var yOffset = 0

    //Booleans for scrolling
    private val scrollSpeed = 5
    private var scrollUp = false
    private var scrollDown = false
    private var scrollLeft = false
    private var scrollRight = false

    //Delay between frames
    private val delay = 10

    //Boolean to keep program looping.
    private var running = true

    //Timer for looping
    private var timer: Timer? = null

    //Map image to edit
    private var map1: Image? = null

    //Point storage
    private val tempPoint: Point = Point(-10, -10)
    private val nonPoint: Point = Point(-10, -10)
    private val pointStack: Stack<Point> = Stack()
    private var typeCheck: Boolean = false

    //Line array
    private val lineInfoArray: MutableList<LineInfo> = ArrayList()

    //Needed to change the value of keyChar to an Int
    private val keyDifference = 49


    //Initialize
    init {
        addKeyListener(KAdapter())
        addMouseListener(MAdapter())
        background = Color.white
        isFocusable = true

        preferredSize = Dimension(panelWidth, panelHeight)
        loadMap()
        //Push a nonPoint onto the stack to start
        pointStack.push(nonPoint)
        initTimer()
    }

    //Load Map
    private fun loadMap() {
        val iid = ImageIcon("src/resources/map1.png")
        map1 = iid.image
    }

    //Start timer
    private fun initTimer() {
        timer = Timer(delay, this)
        timer!!.start()
    }

    //Paint
    public override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        g.color = Color.red
        if (running) {
            //Draw the map you are making
            g.drawImage(map1, xOffset, yOffset, this)
            if (lineInfoArray.size > 0) {
                for (i in 0 until lineInfoArray.size) {
                    //Draw all the actual collision lines
                    g.drawLine(
                        lineInfoArray[i].x1 + xOffset, lineInfoArray[i].y1 + yOffset,
                        lineInfoArray[i].x2 + xOffset, lineInfoArray[i].y2 + yOffset
                    )
                    //Label the type of line in the middle of the line
                    //It will say 0 if the type has not yet been assigned
                    //Currently contains the + 1 to type to make all the numbers look nice
                    // (1, 2, 3, 4, ect) instead of (0, 1, 2, 3, ect)
                    g.drawString(
                        (lineInfoArray[i].type + 1).toString(),
                        (lineInfoArray[i].x1 + lineInfoArray[i].x2) / 2 + xOffset,
                        (lineInfoArray[i].y1 + lineInfoArray[i].y2) / 2 + yOffset
                    )
                }
            }
            Toolkit.getDefaultToolkit().sync()
        } else {
            //Close the program if it is not running
            exitProcess(0)
        }
    }

    override fun actionPerformed(e: ActionEvent) {

        //Simple scrolling code
        if (running) {
            if (scrollUp)
                yOffset += scrollSpeed
            if (scrollDown)
                yOffset -= scrollSpeed
            if (scrollLeft)
                xOffset += scrollSpeed
            if (scrollRight)
                xOffset -= scrollSpeed
        }
        repaint()
    }

    private inner class KAdapter : KeyAdapter() {

        override fun keyPressed(e: KeyEvent?) {

            when (e!!.keyCode) {
                //Scrolling code
                KeyEvent.VK_W -> scrollUp = true
                KeyEvent.VK_A -> scrollLeft = true
                KeyEvent.VK_S -> scrollDown = true
                KeyEvent.VK_D -> scrollRight = true
                //Output code
                KeyEvent.VK_O -> {
                    outputData()
                }
                //Undo code
                KeyEvent.VK_Z -> {
                    //If there is more than one point on the pointStack
                    if (pointStack.size > 1) {
                        //Leave typeCheck mode if you are in it
                        typeCheck = false
                        //Set the tempPoint to whatever is on the stack
                        tempPoint.x = pointStack.peek().x
                        //Remove the top of the stack
                        pointStack.pop()
                    }

                    //If there is at least one line and the current point is not a nonPoint
                    if ((lineInfoArray.isNotEmpty()) && (pointStack.peek().x != nonPoint.x)) {
                        //Remove the most recent line
                        lineInfoArray.removeLast()
                    }

                    //If the last point removed was a nonPoint
                    if (tempPoint.x == nonPoint.x) {
                        //Remove the next point
                        pointStack.pop()
                    }
                }
                //Close program
                KeyEvent.VK_ESCAPE -> running = false
                //Enter a type
                KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4, KeyEvent.VK_5, KeyEvent.VK_6 -> {
                    if (typeCheck) {
                        //Translate the keyPress into the correct type based on keyEvent
                        lineInfoArray[lineInfoArray.size - 1].type = e.keyChar.toInt() - keyDifference
                        typeCheck = false
                    }
                }
                KeyEvent.VK_7 -> {
                    if (typeCheck) {
                        //Add a nonPoint to mark that a new series of lines has begun.
                        pointStack.push(nonPoint)
                        println("-----line broken-----")
                        //Ask for input
                        typePrompt()
                    }
                }
            }
        }

        override fun keyReleased(e: KeyEvent?) {

            when (e!!.keyCode) {
                //Scrolling code
                KeyEvent.VK_W -> scrollUp = false
                KeyEvent.VK_A -> scrollLeft = false
                KeyEvent.VK_S -> scrollDown = false
                KeyEvent.VK_D -> scrollRight = false
            }
        }
    }

    //Outputs the data stored in lineInfoArray into a text file for future use
    private fun outputData() {
        File("map1.txt").printWriter().use { out ->
            lineInfoArray.forEach {
                //println("${it.x1},${it.y1},${it.x2},${it.y2},${it.type}")
                out.println("${it.x1},${it.y1},${it.x2},${it.y2},${it.type}")
            }
        }
    }

    private inner class MAdapter : MouseAdapter() {

        override fun mouseClicked(e: MouseEvent?) {
            if (e != null) {
                println("clicked")
                //If the top of the stack is a nonPoint and it is not in typeCheck mode
                if ((pointStack.peek().x == nonPoint.x) && (!typeCheck)) {
                    //Push the point onto the stack
                    pointStack.push(Point(e.x - xOffset, e.y - yOffset))
                } else if (!typeCheck) {
                    //Make the tempPoint equal to the top of the stack
                    tempPoint.x = pointStack.peek().x
                    tempPoint.y = pointStack.peek().y
                    //Add the new point to the stack
                    pointStack.push(Point(e.x - xOffset, e.y - yOffset))
                    //Create a line using the tempPoint and the new Point
                    lineInfoArray.add(LineInfo(tempPoint.x, tempPoint.y, pointStack.peek().x, pointStack.peek().y, -1))
                    //Set the program to typeCheck mode
                    typeCheck = true
                }

                //If you are in typeCheck mode
                if (typeCheck) {
                    //Ask the user for the type
                    typePrompt()
                }
            }
        }
    }

    //Simply asks the user what type of collision line this is
    private fun typePrompt() {
        println("Enter a type from 1 - 6")
        println("1. Wall")
        println("2. Floor")
        println("3. SemiSolid Floor")
        println("4. Downward Slope")
        println("5. Upward Slope")
        println("6. Ceiling")
        println("7. Break line")
    }
}