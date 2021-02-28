import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.ImageIcon
import javax.swing.JPanel
import javax.swing.Timer


class MapScreen : JPanel(), ActionListener {

    private val panelWidth = 1000
    private val panelHeight = 500
    private val delay = 140

    private var inGame = true

    private var timer: Timer? = null
    //private var ball: Image? = null

    init {

        addKeyListener(TAdapter())
        background = Color.black
        isFocusable = true

        preferredSize = Dimension(panelWidth, panelHeight)
        initTimer()
    }


    private fun initTimer() {
        timer = Timer(delay, this)
        timer!!.start()
    }

    public override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        println("in paint")
        if (inGame) {

            //g.drawImage(apple, appleX, appleY, this)
            Toolkit.getDefaultToolkit().sync()

        } else {

            gameOver(g)
        }
    }

    private fun gameOver(g: Graphics) {

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

        if (inGame) {
            repaint()
        }
        repaint()
    }

    private inner class TAdapter : KeyAdapter() {

        override fun keyPressed(e: KeyEvent?) {

            val key = e!!.keyCode

            if (key == KeyEvent.VK_LEFT) {
                inGame = false
            }
        }
    }
}