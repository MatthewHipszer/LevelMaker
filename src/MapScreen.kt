import java.awt.*
import java.awt.event.*
import javax.swing.ImageIcon
import javax.swing.JPanel
import javax.swing.Timer
import kotlin.system.exitProcess


//TODO could change the last point current point stuff with undoing as some sort of stack like object
//would probably simplify a decent amount of stuff

class MapScreen : JPanel(), ActionListener {

    //Width of the panel
    private val panelWidth = 1000
    private val panelHeight = 500
    //Offsets for scrolling
    private var xOffset = 0
    private var yOffset = 0
    //Booleans for scrolling
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
    private val lastPoint: Point = Point(-10, -10)
    private val currentPoint: Point = Point(-10, -10)
    private var typeCheck: Boolean = false
    //Line array
    private val lineInfoArray: MutableList<LineInfo> = ArrayList()


    //Initialize
    init {
        addKeyListener(KAdapter())
        addMouseListener(MAdapter())
        background = Color.white
        isFocusable = true

        preferredSize = Dimension(panelWidth, panelHeight)
        loadMap()
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
            g.drawImage(map1, xOffset, yOffset, this)
            if (lineInfoArray.size > 0)
            {
                for (i in 0 until lineInfoArray.size)
                {
                    g.drawLine(lineInfoArray[i].x1 + xOffset, lineInfoArray[i].y1 + yOffset,
                        lineInfoArray[i].x2 + xOffset, lineInfoArray[i].y2 + yOffset)
                    //Label the type of line in the middle of the line
                    //It will say 0 if the type has not yet been assigned
                    g.drawString((lineInfoArray[i].type + 1).toString(),
                        (lineInfoArray[i].x1 + lineInfoArray[i].x2) / 2 + xOffset,
                        (lineInfoArray[i].y1 + lineInfoArray[i].y2) / 2 + yOffset)
                }
            }
            Toolkit.getDefaultToolkit().sync()
        } else {
            //Close the program if it is not running
            exitProcess(0)
        }
    }

    override fun actionPerformed(e: ActionEvent) {

        if (running) {
            if (scrollUp)
                yOffset++
            if (scrollDown)
                yOffset--
            if (scrollLeft)
                xOffset++
            if (scrollRight)
                xOffset--
        }
        repaint()
    }

    private inner class KAdapter : KeyAdapter() {

        override fun keyPressed(e: KeyEvent?) {

            when(e!!.keyCode) {
                KeyEvent.VK_W -> scrollUp = true
                KeyEvent.VK_A -> scrollLeft = true
                KeyEvent.VK_S -> scrollDown = true
                KeyEvent.VK_D -> scrollRight = true
                //Undo
                KeyEvent.VK_Z ->
                {
                    if (typeCheck)
                    {
                        typeCheck = false
                        currentPoint.x = lastPoint.x
                        currentPoint.y = lastPoint.y
                        lineInfoArray.removeLast()
                        println("did this undo?")
                    }
                    else
                    {

                        currentPoint.x = -10
                        if (lineInfoArray.isNotEmpty()) {
                            lineInfoArray.removeLast()
                            //lastPoint.x = lineInfoArray[lineInfoArray.size - 1].x2
                            //lastPoint.y = lineInfoArray[lineInfoArray.size - 1].y2
                        }
                        if (lineInfoArray.isNotEmpty()) {
                            //lineInfoArray.removeLast()
                            lastPoint.x = lineInfoArray[lineInfoArray.size - 1].x2
                            lastPoint.y = lineInfoArray[lineInfoArray.size - 1].y2
                        }
                        println("did this undo2?")
                    }
                }
                //Close program
                KeyEvent.VK_ESCAPE -> running = false
                //Enter a type
                KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4, KeyEvent.VK_5, KeyEvent.VK_6 ->
                {
                    if (typeCheck)
                    {
                        //-48 will give the correct value
                        lineInfoArray[lineInfoArray.size - 1].type = e.keyChar.toInt() - 49
                        typeCheck = false
                    }
                }
                KeyEvent.VK_7 ->
                {
                    if (typeCheck)
                    {
                        //typeCheck = false
                        lastPoint.x = -10
                        currentPoint.x = -10
                        println("-----line broken-----")
                        typePrompt()
                    }
                }
            }
        }

        override fun keyReleased(e: KeyEvent?) {

            when(e!!.keyCode) {
                KeyEvent.VK_W -> scrollUp = false
                KeyEvent.VK_A -> scrollLeft = false
                KeyEvent.VK_S -> scrollDown = false
                KeyEvent.VK_D -> scrollRight = false
            }
        }
    }

    private inner class MAdapter : MouseAdapter() {

        override fun mouseClicked(e: MouseEvent?) {
            if (e != null)
            {
                if ((lastPoint.x == -10) && (currentPoint.x == -10) && (!typeCheck))
                {
                    lastPoint.x = (e.x - xOffset)
                    lastPoint.y = (e.y - yOffset)
                }
                else if ((currentPoint.x == -10) && (!typeCheck))
                {
                    currentPoint.x = (e.x - xOffset)
                    currentPoint.y = (e.y - yOffset)
                    lineInfoArray.add(LineInfo(lastPoint.x, lastPoint.y, currentPoint.x,currentPoint.y,-1))
                    typeCheck = true
                    //Now you need a type and to add it to the array
                }
                else if (!typeCheck)
                {
                    lastPoint.x = currentPoint.x
                    lastPoint.y = currentPoint.y
                    currentPoint.x = (e.x - xOffset)
                    currentPoint.y = (e.y - yOffset)
                    lineInfoArray.add(LineInfo(lastPoint.x, lastPoint.y, currentPoint.x,currentPoint.y,-1))
                    typeCheck = true
                }
                if (typeCheck)
                {
                    typePrompt()
                }
            }
        }
    }

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