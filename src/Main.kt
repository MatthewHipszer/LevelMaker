import java.awt.EventQueue
import javax.swing.JFrame

class Main : JFrame() {

    init {
        initUI()
    }

    //Initialize UI
    private fun initUI() {

        //Add MapScreen which implements JPanel and ActionListener
        add(MapScreen())

        title = "LineTest"
        isResizable = false
        pack()

        setLocationRelativeTo(null)
        defaultCloseOperation = EXIT_ON_CLOSE
    }
}

fun main() {
    EventQueue.invokeLater {
        val ex = Main()
        ex.isVisible = true
    }
}