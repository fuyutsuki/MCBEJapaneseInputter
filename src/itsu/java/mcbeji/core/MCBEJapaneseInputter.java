package itsu.java.mcbeji.core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.logging.LogManager;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class MCBEJapaneseInputter implements NativeKeyListener {
	
	private JFrame frame;
	private JButton button;
	private JTextField field;
	
	private PopupMenu menu;
	private MenuItem item1;
	private MenuItem item;
	
	private Robot robot;
    
    public MCBEJapaneseInputter() {
    	try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				
	    	robot = new Robot();
	    	robot.delay(500);
	    	
	        frame = new JFrame();
	        frame.setBounds(0, 0, 600, 70);
	        frame.setResizable(false);
	        frame.setLocationRelativeTo(null);
	        frame.setTitle("MCBEJapaneseInputter");
	        frame.setAlwaysOnTop(true);
	        frame.getContentPane().setLayout(null);
	        frame.getContentPane().setBackground(Color.WHITE);
	        frame.setIconImage(ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("Icon.png")));
	        
	        button = new JButton("終了");
	        button.setBounds(490, 10, 100, 20);
	        button.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                System.exit(0);
	            }
	        });
	        
	        frame.getContentPane().add(button);
	        
	        field = new JTextField();
	        field.setBounds(10, 10, 470, 20);
	        
	        field.addKeyListener(new KeyListener() {
				@Override
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						String text = field.getText();
						StringSelection selection = new StringSelection(text);
						Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
						
						frame.setVisible(false);
						
						robot.delay(500);
						robot.mousePress(0);
						
						keyboardClick(robot, KeyEvent.VK_CONTROL, KeyEvent.VK_V);
						keyboardClick(robot, KeyEvent.VK_CONTROL, KeyEvent.VK_ENTER);
					}
				}
				@Override public void keyReleased(KeyEvent e) {}
				@Override public void keyTyped(KeyEvent e) {}
	        });
	        
	        frame.getContentPane().add(field);
	        
	        menu = new PopupMenu();
	        
	        item1 = new MenuItem("開く                           ");
	        item1.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                frame.setVisible(true);
	            }
	        });
	        
	        item = new MenuItem("終了                           ");
	        item.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                System.exit(0);
	            }
	        });
	        
	        menu.add(item1);
	        menu.add(item);
	        
	        BufferedImage iconImage = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("Icon.png"));
	        BufferedImage image = new BufferedImage(16, 16, iconImage.getType());
            Graphics2D g = image.createGraphics();
            g.drawImage(iconImage, 0, 0, 16, 16, null);
            g.dispose();
            
	        TrayIcon icon = new TrayIcon(image);
	        icon.setPopupMenu(menu);
	        SystemTray.getSystemTray().add(icon);
	        
	        Toolkit.getDefaultToolkit().beep();
	        JOptionPane.showMessageDialog(new JFrame(), "起動しました。\n「t」「Enter」「/」のうち、いずれかのキーを押す、もしくはタスクバーのアイコンを右クリックして「開く」を押すとウィンドウが開きます。\nなお、使用時は必ずMCBEのウィンドウ上にこのソフトのウィンドウを置いてください。\n終了するときはウィンドウ上の「終了」ボタン、もしくはタスクバーのアイコンを右クリックして「終了」を選んで終了してください。", "MCBEJapaneseInputter", JOptionPane.INFORMATION_MESSAGE);
	        
    	} catch (Exception e) {
			e.printStackTrace();
		}
        
    }

    public static void main(String[] args) {
        if (!GlobalScreen.isNativeHookRegistered()) {
            try {
                GlobalScreen.registerNativeHook();
            } catch (NativeHookException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }

    	LogManager.getLogManager().reset();
        GlobalScreen.addNativeKeyListener(new MCBEJapaneseInputter());
    }
    
	public void keyboardClick(Robot robot, int... args) {
		for (int key : args) {
			robot.keyPress(key);
		}
		for (int i=args.length-1; i>=0; i--) {
			robot.keyRelease(args[i]);
		}
		robot.delay(500);
	}
    
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
    	if(e.getKeyCode() == NativeKeyEvent.VC_T || e.getKeyCode() == NativeKeyEvent.VC_ENTER || e.getKeyCode() == NativeKeyEvent.VC_SLASH) {
    		field.setText("");
    		field.setFocusable(true);
        	frame.setVisible(true);
    	}
    }
    
    @Override public void nativeKeyReleased(NativeKeyEvent e) {}
    @Override public void nativeKeyTyped(NativeKeyEvent e) {}
    

}
