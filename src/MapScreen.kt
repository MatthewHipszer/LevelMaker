import java.awt.*
import java.awt.event.*
import javax.swing.ImageIcon
import javax.swing.JPanel
import javax.swing.Timer
import kotlin.system.exitProcess


class MapScreen : JPanel(), ActionListener {

    //Width of the panel
    private val panelWidth = 1000
    private val panelHeight = 500
    //Offsets for scrolling
    private val xOffset = 0
    private val yOffset = 0
    //Delay between frames
    private val delay = 140
    //Boolean to keep program looping.
    private var running = true
    //Timer for looping
    private var timer: Timer? = null
    //Map image to edit
    private var map1: Image? = null

    //Initialize
    init {

        addKeyListener(KAdapter())
        addMouseListener(MAdapter())
        background = Color.black
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
    //TODO will need to incorporate offsets when scrolling is added
    public override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        if (running) {
            g.drawImage(map1,0, 0, this)
            Toolkit.getDefaultToolkit().sync()
        } else {
            //Close the program if it is not running
            exitProcess(0)
        }
    }

    //TODO turn this into a key for what all the buttons do
    //Might be easier to just make the key an image and place it in a corner though.
    //If going the image route just add it to loadImages and paintComponent
    private fun textTest(g: Graphics) {

        val textTest = "Text test"
        val small = Font("Helvetica", Font.BOLD, 14)
        val fontMetrics = getFontMetrics(small)

        val rh = RenderingHints(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON)

        rh[RenderingHints.KEY_RENDERING] = RenderingHints.VALUE_RENDER_QUALITY

        (g as Graphics2D).setRenderingHints(rh)

        g.color = Color.white
        g.font = small
        g.drawString(textTest, (panelWidth - fontMetrics.stringWidth(textTest)) / 2,
            panelHeight / 2)
    }

    override fun actionPerformed(e: ActionEvent) {

        if (running) {
            //Currently doesn't do anything
        }
        repaint()
    }

    private inner class KAdapter : KeyAdapter() {

        override fun keyPressed(e: KeyEvent?) {

            when(e!!.keyCode) {
                //Close program
                KeyEvent.VK_ESCAPE -> running = false
            }
        }
    }

    private inner class MAdapter : MouseAdapter() {

        override fun mouseClicked(e: MouseEvent?) {
            println("Click Test")
        }
    }
}