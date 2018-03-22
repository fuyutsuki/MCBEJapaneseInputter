package tokyo.pmmp.mcbeji

/**
 * mcbeji (Itsuさん作)からコードを引用して直したものです
 * とりあえず動けばいいと思っているので許してください
 */

import java.awt.Color
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.Robot
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.image.BufferedImage
import java.util.logging.LogManager

import javax.imageio.ImageIO
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.JTextField
import javax.swing.UIManager

import org.jnativehook.GlobalScreen
import org.jnativehook.NativeHookException
import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.keyboard.NativeKeyListener
import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    val main = Main()
    if (!GlobalScreen.isNativeHookRegistered()) {
        try {
            GlobalScreen.registerNativeHook()
        } catch (e: NativeHookException) {
            e.printStackTrace()
            System.exit(-1)
        }
    }

    LogManager.getLogManager().reset()
    GlobalScreen.addNativeKeyListener(main)
}

class Main: NativeKeyListener {

    private lateinit var frame: JFrame
    private lateinit var button: JButton
    private lateinit var field: JTextField

    private lateinit var menu: PopupMenu
    private lateinit var item1: MenuItem
    private lateinit var item: MenuItem

    private lateinit var robot: Robot

    private var isInputting: Boolean = false

    init {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

            robot = Robot()
            robot.delay(500)

            frame = JFrame()
            frame.setBounds(0, 0, 600, 70)
            frame.isResizable = false
            frame.setLocationRelativeTo(null)
            frame.title = "MCBEJapaneseInputter"
            frame.isAlwaysOnTop = true
            frame.contentPane.layout = null
            frame.contentPane.background = Color.LIGHT_GRAY
            frame.iconImage = ImageIO.read(this.javaClass.classLoader.getResourceAsStream("Icon.png"))
            frame.addWindowListener(object: WindowListener {
                override fun windowIconified(e: WindowEvent) { }
                override fun windowDeiconified(e: WindowEvent) { }
                override fun windowActivated(e: WindowEvent) { }
                override fun windowDeactivated(e: WindowEvent) { isInputting = false }
                override fun windowOpened(e: WindowEvent) { }
                override fun windowClosing(e: WindowEvent) { }
                override fun windowClosed(e: WindowEvent) { }
            })

            button = JButton("終了")
            button.setBounds(490, 10, 100, 20)
            button.addActionListener { System.exit(0) }

            frame.contentPane.add(button)

            field = JTextField("")
            field.setBounds(10, 10, 470, 20)

            field.addKeyListener(object : KeyListener {
                override fun keyPressed(e: KeyEvent) {
                    if (e.keyCode == KeyEvent.VK_ENTER) {
                        val text = field.text
                        val selection = StringSelection(text)
                        Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, null)

                        frame.isVisible = false
                        robot.mousePress(0)

                        keyboardClick(robot, KeyEvent.VK_CONTROL, KeyEvent.VK_V)
                        keyboardClick(robot, KeyEvent.VK_CONTROL, KeyEvent.VK_ENTER)
                        isInputting = false
                    }
                }
                override fun keyReleased(e: KeyEvent) {}
                override fun keyTyped(e: KeyEvent) {}
            })

            frame.contentPane.add(field)

            menu = PopupMenu()

            item1 = MenuItem("開く                           ")
            item1.addActionListener { frame.isVisible = true }

            item = MenuItem("終了                           ")
            item.addActionListener { System.exit(0) }

            menu.add(item1)
            menu.add(item)

            val iconImage = ImageIO.read(this.javaClass.classLoader.getResourceAsStream("Icon.png"))
            val image = BufferedImage(16, 16, iconImage.type)
            val g = image.createGraphics()
            g.drawImage(iconImage, 0, 0, 16, 16, null)
            g.dispose()

            val icon = TrayIcon(image)
            icon.popupMenu = menu
            SystemTray.getSystemTray().add(icon)

            Toolkit.getDefaultToolkit().beep()
            JOptionPane.showMessageDialog(JFrame(), "起動しました。\n「t」「Enter」「/」のうち、いずれかのキーを押す、もしくはタスクバーのアイコンを右クリックして「開く」を押すとウィンドウが開きます。\nなお、使用時は必ずMCBEのウィンドウ上にこのソフトのウィンドウを置いてください。\n終了するときはウィンドウ上の「終了」ボタン、もしくはタスクバーのアイコンを右クリックして「終了」を選んで終了してください。", "MCBEJapaneseInputter", JOptionPane.INFORMATION_MESSAGE)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun keyboardClick(robot: Robot, vararg args: Int) {
        for (key in args) {
            robot.keyPress(key)
        }
        for (i in args.indices.reversed()) {
            robot.keyRelease(args[i])
        }
        robot.delay(200)
    }

    override fun nativeKeyPressed(e: NativeKeyEvent) {
        if (!isInputting) {
            if (e.keyCode == NativeKeyEvent.VC_T || e.keyCode == NativeKeyEvent.VC_ENTER || e.keyCode == NativeKeyEvent.VC_SLASH) {
                robot.delay(100)
                field.text = ""
                field.isFocusable = true
                frame.isVisible = true
                isInputting = true
            }
        }
    }

    override fun nativeKeyReleased(e: NativeKeyEvent) {}
    override fun nativeKeyTyped(e: NativeKeyEvent) {}
}
