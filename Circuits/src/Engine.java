import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JPanel;

class Screen extends JPanel {
	private static final long serialVersionUID = 1L;
	public int scale = 600;
	
	private boolean checkedForRunOutput = false;
	
	public double maxFPS = 60;
	public long lastTimeMillis = 0, thisTimeMillis = 0;
	public float timeBetweenFrames;
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		try {
			Thread.sleep((int) (1000.0 / maxFPS));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Main.paintToFrame(g);
		
		if (!Engine.calledRun) {
			if (!checkedForRunOutput) System.out.println("Nothing happening? Try calling run() from Engine instance.");
			checkedForRunOutput = true;
		}
		
		repaint();
	}
}

class SetVisibleThread extends Thread {
	public void run() {
		Engine.frame.setVisible(true);
	}
}

public class Engine {
	public static Keyboard keys = new Keyboard();
	public static Mouse mouse = new Mouse();
	public static Camera camera = new Camera(0,0);
	
	public static JFrame frame = new JFrame();
	public static Container cont = frame.getContentPane();
	public static Screen scr = new Screen();
	
	public static int tps;
	public static boolean running;
	public static int scrWidth, scrHeight;
	
	public static boolean initialized = false, calledRun = false, iKnowWhatImDoing = false;
	
	public Engine(boolean pleaseTellMeWhatToDo) {
		if (pleaseTellMeWhatToDo) {
			System.out.println("Make sure that the class which is running your game is named \"Main\"");
			System.out.println("Make sure it includes the methods mainLoop() and paintToFrame()");
			System.out.println("These methods are run on separate threads so keep that in mind.");
			System.out.println("To start, run initializeJFrame() from your Engine instance and fill in the right parameters. Afterwards, call run() from the same instance.");
			System.out.println();
		}
	}
	
	public void i_know_what_im_doing() {iKnowWhatImDoing = true;}
	
	public static BufferedImage loadBufferedImage(String fullFilePath) {
		BufferedImage toOut = null;
		try {
			toOut = ImageIO.read(Engine.class.getResourceAsStream(fullFilePath)); //Needs to be the class that this is i hate java sometimes im going to punch someone
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		
		return toOut;
	}
	
	public static Scanner loadScannerFromSourceFolder(String filename) {
		Scanner toOut = null;
		
		try {
			toOut = new Scanner(Engine.class.getResourceAsStream(filename));
		}
		catch(Exception e) {
			
		}
		
		return toOut;
	}
	
	public static PrintWriter loadPrintWriterFromSourceFolder(String filename) {
		PrintWriter toOut = null;
		
		try {
			
		}
		catch(Exception e) {
			
		}
		
		return toOut;
	}
	
	public static class SoundClip {
		private URL sfx;
		private Clip clip;
		public SoundClip(String fullFilePath) {
			try {
				URL url = this.getClass().getClassLoader().getResource(fullFilePath);
				AudioInputStream audio = AudioSystem.getAudioInputStream(url);
				clip = AudioSystem.getClip();
				clip.open(audio);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void playClip() {
			clip.setFramePosition(0);
			clip.start();
		}
	}
	
	public void initializeJFrame(int screen_width, int screen_height, boolean fullscreen, boolean undecorated, int ticks_per_second) {
		cont.add(scr);
		frame.addKeyListener(keys);
		scr.addMouseListener(mouse);
		scr.addMouseMotionListener(mouse);
		scr.addMouseWheelListener(mouse);

		
		scrWidth = screen_width;
		scrHeight = screen_height;
		if (fullscreen) {
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			//frame.setPreferredSize(new Dimension((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()));
			//frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
			scrWidth = cont.getWidth();
			scrHeight = cont.getHeight();
		}
		else {
			frame.setSize(scrWidth, scrHeight);
			
		}
		
		if (undecorated) {
			frame.setUndecorated(true);
		}
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		running = true;
		tps = ticks_per_second;
		
		frame.setVisible(true);
		
		//SetVisibleThread runit = new SetVisibleThread();
		//runit.start();
		
		initialized = true;
	}
	
	public void run() throws Exception {
		if (!initialized) {
			throw new Exception("Error: JFrame not initialized. Please call method initializeJFrame() from Engine instance");
		}
		else {
			calledRun = true;
			long lastTime = System.nanoTime(); //Init time var
		    final double ns = 1000000000.0 / tps; //Control ticks per second
		    double delta = 0;
		    while(running) { //Game loop boolean
		        long now = System.nanoTime(); //Far for now time
		        delta += (now - lastTime) / ns; //While in running loop, wait until two time vars are equal
		        lastTime = now; //Reset
		        while(delta >= 1) { //Wait until time is less than one
		        	updateSystem();
		        	Main.mainLoop();
		            delta--; //Remove
	            }
	        } 
		}

	    System.exit(0); //Once done with game loop, shut everything down
	}
	
	public void updateSystem() {
		scrWidth = cont.getWidth();
		scrHeight = cont.getHeight();
	}
	
	public void setScale(int scale) {scr.scale = scale;}
	
	public static class Keyboard implements KeyListener {
		
		public boolean K_ESCAPE = false, K_LEFT = false, K_RIGHT = false, K_UP = false, K_DOWN = false, K_SPACE = false, K_LSHIFT = false, K_RSHIFT = false, K_TAB = false, K_DELETE = false, K_ENTER;
		public boolean K_w = false, K_a = false, K_s = false, K_d = false, K_e = false, K_q = false, K_r = false, K_t = false, K_y = false, K_u = false, K_i = false, K_o = false, K_p = false, K_f = false, K_g = false, K_h = false, K_j = false, K_k = false, K_l = false, K_z = false, K_x = false, K_c = false, K_v = false, K_b = false, K_n = false, K_m = false;
		public boolean K_1 = false, K_2 = false, K_3 = false, K_4 = false, K_5 = false, K_6 = false, K_7 = false, K_8 = false, K_9 = false, K_0 = false;

		public boolean canSpace = true, canEscape = true, canLeft = true, canRight = true, canUp = true, canDown = true, canTab = true, canLShift = true, canDelete = true, canEnter = true;
		public boolean canq = true, canw = true, cane = true, canr = true, cant = true, cany = true, canu = true, cani = true, cano = true, canp = true, cana = true, cans = true, cand = true, canf = true, cang = true, canh = true, canj = true, cank = true, canl = true, canz = true, canx = true, canc = true, canv = true, canb = true, cann = true, canm = true;
		public boolean can1 = true, can2 = true, can3 = true, can4 = true, can5 = true, can6 = true, can7 = true, can8 = true, can9 = true, can0 = true;
		
		public boolean K_CONTROL = false;
		@Override
		public void keyTyped(KeyEvent e) {
			//int key = e.getKeyCode();
		}

		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			
			if (key == KeyEvent.VK_ESCAPE) {K_ESCAPE = true;}
			if (key == KeyEvent.VK_LEFT) {K_LEFT = true;}
			if (key == KeyEvent.VK_RIGHT) {K_RIGHT = true;}
			if (key == KeyEvent.VK_UP) {K_UP = true;}
			if (key == KeyEvent.VK_DOWN) {K_DOWN = true;}
			if (key == KeyEvent.VK_SPACE) {K_SPACE = true;}
			if (key == KeyEvent.VK_SHIFT) {K_LSHIFT = true;}
			if (key == KeyEvent.VK_TAB) {K_TAB = true;}
			if (key == KeyEvent.VK_BACK_SPACE) {K_DELETE = true;}
			if (key == KeyEvent.VK_ENTER) {K_ENTER = true;}
			
			if (key == KeyEvent.VK_W) {K_w = true;}
			if (key == KeyEvent.VK_A) {K_a = true;}
			if (key == KeyEvent.VK_S) {K_s = true;}
			if (key == KeyEvent.VK_D) {K_d = true;}
			if (key == KeyEvent.VK_Q) {K_q = true;}
			if (key == KeyEvent.VK_E) {K_e = true;}
			if (key == KeyEvent.VK_R) {K_r = true;}
			if (key == KeyEvent.VK_T) {K_t = true;}
			if (key == KeyEvent.VK_Y) {K_y = true;}
			if (key == KeyEvent.VK_U) {K_u = true;}
			if (key == KeyEvent.VK_I) {K_i = true;}
			if (key == KeyEvent.VK_O) {K_o = true;}
			if (key == KeyEvent.VK_P) {K_p = true;}
			if (key == KeyEvent.VK_F) {K_f = true;}
			if (key == KeyEvent.VK_G) {K_g = true;}
			if (key == KeyEvent.VK_H) {K_h = true;}
			if (key == KeyEvent.VK_J) {K_j = true;}
			if (key == KeyEvent.VK_K) {K_k = true;}
			if (key == KeyEvent.VK_L) {K_l = true;}
			if (key == KeyEvent.VK_Z) {K_z = true;}
			if (key == KeyEvent.VK_X) {K_x = true;}
			if (key == KeyEvent.VK_C) {K_c = true;}
			if (key == KeyEvent.VK_V) {K_v = true;}
			if (key == KeyEvent.VK_B) {K_b = true;}
			if (key == KeyEvent.VK_N) {K_n = true;}
			if (key == KeyEvent.VK_M) {K_m = true;}
			
			if (key == KeyEvent.VK_1) {K_1 = true;}
			if (key == KeyEvent.VK_2) {K_2 = true;}
			if (key == KeyEvent.VK_3) {K_3 = true;}
			if (key == KeyEvent.VK_4) {K_4 = true;}
			if (key == KeyEvent.VK_5) {K_5 = true;}
			if (key == KeyEvent.VK_6) {K_6 = true;}
			if (key == KeyEvent.VK_7) {K_7 = true;}
			if (key == KeyEvent.VK_8) {K_8 = true;}
			if (key == KeyEvent.VK_9) {K_9 = true;}
			if (key == KeyEvent.VK_0) {K_0 = true;}
			
			if (e.isControlDown()) K_CONTROL = true;
			else K_CONTROL = false;
			
		}

		@Override
		public void keyReleased(KeyEvent e) {
			int key = e.getKeyCode();
			
			if (key == KeyEvent.VK_ESCAPE) {
				K_ESCAPE = false;
				canEscape = true;
			}
			if (key == KeyEvent.VK_LEFT) {
				K_LEFT = false;
				canLeft = true;
			}
			if (key == KeyEvent.VK_RIGHT) {
				K_RIGHT = false;
				canRight = true;
			}
			if (key == KeyEvent.VK_UP) {
				K_UP = false;
				canUp = true;
			}
			if (key == KeyEvent.VK_DOWN) {
				K_DOWN = false;
				canDown = true;
			}
			if (key == KeyEvent.VK_SPACE) {
				K_SPACE = false;
				canSpace = true;
			}
			if (key == KeyEvent.VK_SHIFT) {
				K_LSHIFT = false;
				canLShift = true;
			}
			if (key == KeyEvent.VK_TAB) {
				K_TAB = false;
				canTab = true;
			}
			if (key == KeyEvent.VK_BACK_SPACE) {
				K_DELETE = false;
				canDelete = true;
			}
			if (key == KeyEvent.VK_ENTER) {
				K_ENTER = false;
				canEnter = true;
			}
			
			if (key == KeyEvent.VK_W) {
				K_w = false;
				canw = true;
			}
			if (key == KeyEvent.VK_A) {
				K_a = false;
				cana = true;
			}
			if (key == KeyEvent.VK_S) {
				K_s = false;
				cans = true;
			}
			if (key == KeyEvent.VK_D) {
				K_d = false;
				cand = true;
			}
			if (key == KeyEvent.VK_E) {
				K_e = false;
				cane = true;
			}
			if (key == KeyEvent.VK_Q) {
				K_q = false;
				canq = true;
			}
			if (key == KeyEvent.VK_R) {
				K_r = false;
				canr = true;
			}
			if (key == KeyEvent.VK_T) {
				K_t = false;
				cant = true;
			}
			if (key == KeyEvent.VK_Y) {
				K_y = false;
				cany = true;
			}
			if (key == KeyEvent.VK_U) {
				K_u = false;
				canu = true;
			}
			if (key == KeyEvent.VK_I) {
				K_i = false;
				cani = true;
			}
			if (key == KeyEvent.VK_O) {
				K_o = false;
				cano = true;
			}
			if (key == KeyEvent.VK_P) {
				K_p = false;
				canp = true;
			}
			if (key == KeyEvent.VK_F) {
				K_f = false;
				canf = true;
			}
			if (key == KeyEvent.VK_G) {
				K_g = false;
				cang = true;
			}
			if (key == KeyEvent.VK_H) {
				K_h = false;
				canh = true;
			}
			if (key == KeyEvent.VK_J) {
				K_j = false;
				canj = true;
			}
			if (key == KeyEvent.VK_K) {
				K_k = false;
				cank = true;
			}
			if (key == KeyEvent.VK_L) {
				K_l = false;
				canl = true;
			}
			if (key == KeyEvent.VK_Z) {
				K_z = false;
				canz = true;
			}
			if (key == KeyEvent.VK_X) {
				K_x = false;
				canx = true;
			}
			if (key == KeyEvent.VK_C) {
				K_c = false;
				canc = true;
			}
			if (key == KeyEvent.VK_V) {
				K_v = false;
				canv = true;
			}
			if (key == KeyEvent.VK_B) {
				K_b = false;
				canb = true;
			}
			if (key == KeyEvent.VK_N) {
				K_n = false;
				cann = true;
			}
			if (key == KeyEvent.VK_M) {
				K_m = false;
				canm = true;
			}
			
			if (key == KeyEvent.VK_1) {
				K_1 = false;
				can1 = true;
			}
			if (key == KeyEvent.VK_2) {
				K_2 = false;
				can2 = true;
			}
			if (key == KeyEvent.VK_3) {
				K_3 = false;
				can3 = true;
			}
			if (key == KeyEvent.VK_4) {
				K_4 = false;
				can4 = true;
			}
			if (key == KeyEvent.VK_5) {
				K_5 = false;
				can5 = true;
			}
			if (key == KeyEvent.VK_6) {
				K_6 = false;
				can6 = true;
			}
			if (key == KeyEvent.VK_7) {
				K_7 = false;
				can7 = true;
			}
			if (key == KeyEvent.VK_8) {
				K_8 = false;
				can8 = true;
			}
			if (key == KeyEvent.VK_9) {
				K_9 = false;
				can9 = true;
			}
			if (key == KeyEvent.VK_0) {
				K_0 = false;
				can0 = true;
			}
			
			if (!e.isControlDown()) K_CONTROL = false;
		}
		
		//Normal Accessor methods
		public  boolean ESCAPE() {return K_ESCAPE;}
		public  boolean LEFT() {return K_LEFT;}
		public  boolean RIGHT() {return K_RIGHT;}
		public  boolean UP() {return K_UP;}
		public  boolean DOWN() {return K_DOWN;}
		public  boolean SPACE() {return K_SPACE;}
		public  boolean LSHIFT() {return K_LSHIFT;}
		public  boolean TAB() {return K_TAB;}
		public  boolean DELETE() {return K_DELETE;}
		public  boolean ENTER() {return K_ENTER;}
		
		public  boolean W() {return K_w;}
		public  boolean A() {return K_a;}
		public  boolean S() {return K_s;}
		public  boolean D() {return K_d;}
		public  boolean E() {return K_e;}
		public  boolean Q() {return K_q;}
		public  boolean R() {return K_r;}
		public  boolean T() {return K_t;}
		public  boolean Y() {return K_y;}
		public  boolean U() {return K_u;}
		public  boolean I() {return K_i;}
		public  boolean O() {return K_o;}
		public  boolean P() {return K_p;}
		public  boolean F() {return K_f;}
		public  boolean G() {return K_g;}
		public  boolean H() {return K_h;}
		public  boolean J() {return K_j;}
		public  boolean K() {return K_k;}
		public  boolean L() {return K_l;}
		public  boolean Z() {return K_z;}
		public  boolean X() {return K_x;}
		public  boolean C() {return K_c;}
		public  boolean V() {return K_v;}
		public  boolean B() {return K_b;}
		public  boolean N() {return K_n;}
		public  boolean M() {return K_m;}
		
		public  boolean K1() {return K_1;}
		public  boolean K2() {return K_2;}
		public  boolean K3() {return K_3;}
		public  boolean K4() {return K_4;}
		public  boolean K5() {return K_5;}
		public  boolean K6() {return K_6;}
		public  boolean K7() {return K_7;}
		public  boolean K8() {return K_8;}
		public  boolean K9() {return K_9;}
		public  boolean K0() {return K_0;}
		
		public boolean K_CONTROL() {return K_CONTROL;}
		
		//Typed Accessor methods
		public  boolean ESCAPETYPED() {
			if (K_ESCAPE && canEscape) {
				canEscape = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean LEFTTYPED() {
			if (K_LEFT && canLeft) {
				canLeft = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean RIGHTTYPED() {
			if (K_RIGHT && canRight) {
				canRight = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean UPTYPED() {
			if (K_UP && canUp) {
				canUp = false;
				return true;
			}
			else {
				return false;
			}
		}
		public boolean DOWNTYPED() {
			if (K_DOWN && canDown) {
				canDown = false;
				return true;
			}
			else {
				return false;
			}
		}
		public boolean SPACETYPED() {
			if (K_SPACE && canSpace) {
				canSpace = false;
				return true;
			}
			else {
				return false;
			}
		}
		public boolean LSHIFTTYPED() {
			if (K_LSHIFT && canLShift) {
				canLShift = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean TABTYPED() {
			if (K_TAB && canTab) {
				canTab = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean DELETETYPED() {
			if (K_DELETE && canDelete) {
				canDelete = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean ENTERTYPED() {
			if (K_ENTER && canEnter) {
				canEnter = false;
				return true;
			}
			else {
				return false;
			}
		}
		
		public  boolean WTYPED() {
			if (K_w && canw) {
				canw = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean ATYPED() {
			if (K_a && cana) {
				cana = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean STYPED() {
			if (K_s && cans) {
				cans = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean DTYPED() {
			if (K_d && cand) {
				cand = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean ETYPED() {
			if (K_e && cane) {
				cane = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean QTYPED() {
			if (K_q && canq) {
				canq = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean RTYPED() {
			if (K_r && canr) {
				canr = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean TTYPED() {
			if (K_t && cant) {
				cant = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean YTYPED() {
			if (K_y && cany) {
				cany = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean UTYPED() {
			if (K_u && canu) {
				canu = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean ITYPED() {
			if (K_i && cani) {
				cani = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean OTYPED() {
			if (K_o && cano) {
				cano = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean PTYPED() {
			if (K_p && canp) {
				canp = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean FTYPED() {
			if (K_f && canf) {
				canf = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean GTYPED() {
			if (K_g && cang) {
				cang = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean HTYPED() {
			if (K_h && canh) {
				canh = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean JTYPED() {
			if (K_j && canj) {
				canj = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean KTYPED() {
			if (K_k && cank) {
				cank = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean LTYPED() {
			if (K_l && canl) {
				canl = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean ZTYPED() {
			if (K_z && canz) {
				canz = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean XTYPED() {
			if (K_x && canx) {
				canx = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean CTYPED() {
			if (K_c && canc) {
				canc = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean VTYPED() {
			if (K_v && canv) {
				canv = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean BTYPED() {
			if (K_b && canb) {
				canb = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean NTYPED() {
			if (K_n && cann) {
				cann = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean MTYPED() {
			if (K_m && canm) {
				canm = false;
				return true;
			}
			else {
				return false;
			}
		}

		public  boolean K1TYPED() {
			if (K_1 && can1) {
				can1 = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean K2TYPED() {
			if (K_2 && can2) {
				can2 = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean K3TYPED() {
			if (K_3 && can3) {
				can3 = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean K4TYPED() {
			if (K_4 && can4) {
				can4 = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean K5TYPED() {
			if (K_5 && can5) {
				can5 = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean K6TYPED() {
			if (K_6 && can6) {
				can6 = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean K7TYPED() {
			if (K_7 && can7) {
				can7 = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean K8TYPED() {
			if (K_8 && can8) {
				can8 = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean K9TYPED() {
			if (K_9 && can9) {
				can9 = false;
				return true;
			}
			else {
				return false;
			}
		}
		public  boolean K0TYPED() {
			if (K_0 && can0) {
				can0 = false;
				return true;
			}
			else {
				return false;
			}
		}
		
		//Axis methods (similar to Unity's system)
		public boolean isUp() {
			if (K_UP || K_w) return true;
			else return false;
		}
		public boolean isDown() {
			if (K_DOWN || K_s) return true;
			else return false;
		}
		public boolean isLeft() {
			if (K_LEFT || K_a) return true;
			else return false;
		}
		public boolean isRight() {
			if (K_RIGHT || K_d) return true;
			else return false;
		}
		
		//Mutator methods
		public void setEscape(boolean in) {K_ESCAPE = in;}
		public void setSpace(boolean in) {K_SPACE = in;}
		public void setA(boolean in) {K_a = in;}
		public void setD(boolean in) {K_d = in;}
		
		public void reInitEnter() {canEnter = true;}
		
	}
	
	public static class Mouse implements MouseListener, MouseMotionListener, MouseWheelListener {
		private boolean left, right, middle;
		private boolean canLeft = true, canRight = true, canMiddle = true;
		private int x,y, lastX = 0, lastY = 0;
		private int scroll, scrollDifference = 0;
		private int dX = 0, dY = 0;
		private Robot bot;
		private int xOffset = 0, yOffset = 0;
		
		public Mouse() {
			this.xOffset = 0;
			this.yOffset = 0;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			int key = e.getButton();

			if (key == MouseEvent.BUTTON1) {
				left = true;
			
			}
			else left = false;
			
			if (key == MouseEvent.BUTTON2) {
				middle = true;
			}
			
			if (key == MouseEvent.BUTTON3) {
				right = true;
			}
			
			x = e.getX();
			y = e.getY();
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			int key = e.getButton();

			if (key == MouseEvent.BUTTON1) {
				left = false;
				canLeft = true;
			}
			if (key == MouseEvent.BUTTON2) {
				middle = false;
				canMiddle = true;
			}
			if (key == MouseEvent.BUTTON3) {
				right = false;
				canRight = true;
			}
			
			x = e.getX();
			y = e.getY();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			x = e.getX();
			y = e.getY();
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			x = e.getX();
			y = e.getY();
			
		}
		
		public void mouseDragged(MouseEvent e) {
			x = e.getX();
			y = e.getY();
			
			dX = e.getX() - lastX;
			dY = e.getY() - lastY;
			
			lastX = e.getX();
			lastY = e.getY();
		}

	    public void mouseMoved(MouseEvent e) {
	    	x = e.getX();
			y = e.getY();
			
			dX = e.getX() - lastX;
			dY = e.getY() - lastY;
			
			lastX = e.getX();
			lastY = e.getY();
	    }
	    
	    @Override
		public void mouseWheelMoved(MouseWheelEvent e) {
	    	scroll -= e.getWheelRotation();
	    	scrollDifference = -e.getWheelRotation();
		}
		
		public int getX() {return x + xOffset;}
		public int getY() {return y + yOffset;}

		public int[] getDelta() {
			int[] toOut = {dX,dY};
			dX = 0;
			dY = 0;
			return toOut;
		}

		public boolean LEFT() {return left;}
		public boolean RIGHT() {return right;}
		public boolean MIDDLE() {return middle;}
		
		public boolean LEFTCLICKED() {
			if (left && canLeft) {
				canLeft = false;
				return true;
			}
			else {
				return false;
			}
		}
		public boolean RIGHTCLICKED() {
			if (right && canRight) {
				canRight = false;
				return true;
			}
			else {
				return false;
			}
		}
		public boolean MIDDLECLICKED() {
			if (middle && canMiddle) {
				canMiddle = false;
				return true;
			}
			else {
				return false;
			}
		}
		public int getScrollAmount() {
			return scroll;
		}
		public int getScrollDifference() {
			int toOut = scrollDifference;
			scrollDifference = 0;
			return toOut;
		}
		


		public void setXOffset(int offset) {xOffset = offset;}
		public void setYOffset(int offset) {yOffset = offset;}
	}
	
	public static class Hitbox {
		private double x,y,w,h,l,r,u,d;
		private boolean colRight, colLeft, colUp, colDown;
		private boolean tag;
		
		public Hitbox() {
			this(0,0,1,1);
		}
		public Hitbox(double xIn, double yIn) {
			this(xIn, yIn, 1,1);
		}
		public Hitbox (double xIn, double yIn, double wIn, double hIn) {
			x = xIn;
			y = yIn;
			w = wIn;
			h = hIn;
			
			l = x - (w / 2);
			r = x + (w / 2);
			u = y - (h / 2);
			d = y + (h / 2);
			
			colRight = false;
			colLeft = false;
			colUp = false;
			colDown = false;
			
			this.tag = false;
		}
		
		public void update (double xIn, double yIn, double wIn, double hIn) {
			x = xIn;
			y = yIn;
			w = wIn;
			h = hIn;
			
			l = x - (w / 2);
			r = x + (w / 2);
			u = y - (h / 2);
			d = y + (h / 2);
		}
		
		public void resetDirs() {
			colRight = false;
			colLeft = false;
			colDown = false;
			colUp = false;
		}
		
		public boolean isColliding (Hitbox b) {
			if (b == null) return false;
			else {
				if (l < b.getRight() && r > b.getLeft() && u < b.getBottom() && d > b.getTop()) {
					//Checking collision direction
					if (r > b.getRight()) colLeft = true;

					if (l < b.getLeft()) colRight = true;

					if (d > b.getBottom()) colUp = true;

					if (u < b.getTop()) colDown = true;
					
					return true;
				}
				
				else {
					return false;
				}
			}
		}

		public boolean isColliding (Hitbox a, Hitbox b) {
			if (b == null) return false;
			else {
				if (a.getLeft() < b.getRight() && a.getRight() > b.getLeft() && a.getTop() < b.getBottom() && a.getBottom() > b.getTop()) {
					
					return true;
				}
				
				else {
					return false;
				}
			}
		}

		public int dirCollision(Hitbox b) {
			int dir = -1;
			
			if (b != null) {
				if (checkLengthToDistance(b)) {
					if (l < b.getRight() && r > b.getLeft() && u < b.getBottom() && d > b.getTop()) {
						//Checking collision direction
						double dLeft = 0;
						double dRight = 0;
						double dUp = 0;
						double dDown = 0;

						while (true) {
							Hitbox temp;
							
							temp = new Hitbox(this.x, this.y + dUp, this.w, this.h);
							if (!isColliding(temp, b)) {
								dir = 0;
								break;
							}
							else dUp -= 0.1;
							dUp = round(dUp);
							
							temp = new Hitbox(this.x + dRight, this.y, this.w, this.h);
							if (!isColliding(temp, b)) {
								dir = 1;
								break;
							}
							else dRight += 0.1;
							dRight = round(dRight);

							temp = new Hitbox(this.x, this.y + dDown, this.w, this.h);
							if (!isColliding(temp, b)) {
								dir = 2;
								break;
							}
							else dDown += 0.1;
							dDown = round(dDown);

							temp = new Hitbox(this.x + dLeft, this.y, this.w, this.h);
							if (!isColliding(temp, b)) {
								dir = 3;
								break;
							}
							else dLeft -= 0.1;
							dLeft = round(dLeft);
							
						}
					}
					
					else {
						dir = -1;
					}
				}
			}

			return dir;
		}
		
		public double dirCollisionDist(Hitbox b) {
			double dist = 0;

			if (b != null) {
				if (checkLengthToDistance(b)) {
					if (isColliding(b)) {
						//Checking collision direction
						double dLeft = 0;
						double dRight = 0;
						double dUp = 0;
						double dDown = 0;

						while (true) {
							Hitbox temp;
							
							temp = new Hitbox(this.x, this.y + dUp, this.w, this.h);
							if (!isColliding(temp, b)) {
								dist = dUp;
								break;
							}
							else dUp -= 0.1;
							dUp = round(dUp);
							
							temp = new Hitbox(this.x + dRight, this.y, this.w, this.h);
							if (!isColliding(temp, b)) {
								dist = dRight;
								break;
							}
							else dRight += 0.1;
							dRight = round(dRight);

							temp = new Hitbox(this.x, this.y + dDown, this.w, this.h);
							if (!isColliding(temp, b)) {
								dist = dDown;
								break;
							}
							else dDown += 0.1;
							dDown = round(dDown);

							temp = new Hitbox(this.x + dLeft, this.y, this.w, this.h);
							if (!isColliding(temp, b)) {
								dist = dLeft;
								break;
							}
							else dLeft -= 0.1;
							dLeft = round(dLeft);
							
						}
					}
					
					else {
						dist = 0;
					}
				}
			}

			return dist;
		}
		
		public void tag(boolean tag) {this.tag = tag;}
		public boolean getTag() {return this.tag;}
		
		public boolean checkLengthToDistance(Hitbox box) {
			double thisLongest = Math.max(this.w,  this.h);
			double boxLongest = Math.max(box.getW(), box.getH());
			double longLength = thisLongest + boxLongest;
			
			double distance = Math.sqrt(Math.pow(box.getX() - this.x, 2) + Math.pow(box.getY() - this.y, 2));
			
			box.tag(longLength >= distance);
			
			return longLength >= distance;
		}
		
		public void render(Graphics g, int scrW, int scrH, double upscale) {
			int rX = (int)(((x * upscale) - (w / 2 * upscale)) + (scrW / 2));
			int rY = (int)(((y * upscale) - (h / 2 * upscale)) + (scrH / 2));
			
			g.setColor(Color.red);
			g.drawRect((int)(rX), (int)(rY), (int)(w * upscale), (int)(h * upscale));
		}
		
		public void render(Graphics g, int scrWidth, int scrHeight, Camera camera) {
			
			int xOffset = scrWidth / 2;
			int yOffset = scrHeight / 2;
			
			int renderX = (int) ((x + xOffset) - camera.getX() - ((double) w / 2.0));
			int renderY = (int) ((y + yOffset) - camera.getY() - ((double) h / 2.0));

			g.setColor(Color.red);
			
			g.drawRect(renderX, renderY, (int) w, (int) h);
		}
		
		public void render(Graphics g, int scrWidth, int scrHeight, int mult, Camera camera) {
			double scale = (double) (((double) (scrWidth) / (double) (mult)) * 10);
			
			int xOffset = scrWidth / 2;
			int yOffset = scrHeight / 2;
			
			int renderW = (int) (w * scale);
			int renderH = (int) (h * scale);
			
			int renderX = (int) ((x * scale) + xOffset - camera.getX() - ((double) renderW / 2.0));
			int renderY = (int) ((y * scale) + yOffset - camera.getY() - ((double) renderH / 2.0));

			g.setColor(Color.red);
			
			g.drawRect(renderX, renderY, renderW, renderH);
		}
		
		public void render(Graphics g) {
			int[] renderPos = Engine.prepareRenderVals(x, y, w, h);
			g.setColor(Color.red);
			g.drawRect(renderPos[0], renderPos[1], renderPos[2], renderPos[3]);
		}
		
		public void renderPos(Graphics g, int scrWidth, int scrHeight, int mult, Camera camera) {
			double scale = (double) (((double) (scrWidth) / (double) (mult)) * 10);
			
			int xOffset = scrWidth / 2;
			int yOffset = scrHeight / 2;
			
			int renderW = (int) (w * scale);
			int renderH = (int) (h * scale);
			
			int renderX = (int) ((x * scale) + xOffset - camera.getX());
			int renderY = (int) ((y * scale) + yOffset - camera.getY());

			g.setColor(Color.red);
			
			g.drawRect(renderX, renderY, 10, 10);
		}

		public double round(double in) {
			return Math.round(in*100)/100.0;
		}

		public double getLeft() {return l;}
		public double getRight() {return r;}
		public double getTop() {return u;}
		public double getBottom() {return d;}
		public double getW() {return this.w;}
		public double getH() {return this.h;}
		public double getX() {return this.x;}
		public double getY() {return this.y;}

		public boolean getColRight() {return colRight;}
		public boolean getColLeft() {return colLeft;}
		public boolean getColUp() {return colUp;}
		public boolean getColDown() {return colDown;}
	}

	public static class Camera {
		private int x,y, bound;
		private int xOffset, yOffset;
		private int shakeTime;
		
		public Camera(int x, int y) {
			this.x = x;
			this.y = y;
			bound = 100;
			this.xOffset = 0;
			this.yOffset = 0;
			this.shakeTime = 0;
		}
		
		public void setX(int x) {this.x = x;}
		public void setY(int y) {this.y = y;}
		public void setPos(int x, int y) {
			this.x = x;
			this.y = y;
		}
	
		public void addX(int toAdd) {this.x += toAdd;}
		public void addY(int toAdd) {this.y += toAdd;}
		
		public int getX() {return this.x + this.xOffset;}
		public int getY() {return this.y + this.xOffset;}
		public int getBound() {return this.bound;}
		
		/********************************************************************************
		* shake()                                                                       *
		* modifies offsets based of dt and whatnot to give impression of shaking        *
		*                                                                               *
		* Parameters:                                                                   *
		* int time                                                                      *
		* int intensity                                                                 *
		*                                                                               *
		* Return Type: none                                                             *
		********************************************************************************/
		public void shake(int time, int intensity) {
			if (this.shakeTime < time) {
				this.shakeTime++;
				int divider = intensity / 2;
				
				int delta = this.shakeTime % intensity + 1;
				
				if (delta <= divider) {
					this.xOffset = delta;
					this.yOffset = delta;
				}
				else {
					this.xOffset = -delta + divider;
					this.yOffset = -delta + divider;
				}
			}
		}
		public void resetShake() {this.shakeTime = 0;}
	}

	public static int round(double in) {
		return (int) Math.rint(in);
	}
	public static int roundUp(double in) {
		return (int) Math.rint(in + 0.5);
	}
	
	public static int[] prepareRenderVals(double x, double y, double w, double h) {
		double scale = (double) (((double) (scrWidth) / (double) (getScale())) * 10);
		
		int xOffset = scrWidth / 2;
		int yOffset = scrHeight / 2;
		
		int renderW = roundUp(w * scale);
		int renderH = roundUp(h * scale);
		int renderX = roundUp((x * scale) + xOffset - camera.getX() - ((double) renderW / 2.0));
		int renderY = roundUp((y * scale) + yOffset - camera.getY() - ((double) renderH / 2.0));
		int[] toOut = {renderX, renderY, renderW, renderH};
		return toOut;
	}
	
	public static void setMaxFPS(int fps) {
		if (fps > 999) System.out.println("Max fps cannot be 1000 or higher");
		else if (fps == 0) System.out.println("Max fps cannot be 0");
		else if (fps < 0) System.out.println("Max fps cannot be negative");
		else scr.maxFPS = fps;
	}
	
	public static int getScale() {return scr.scale;}
	
	public static void setBackground(Graphics g, Color color) {
		g.setColor(color);
		g.fillRect(0, 0, scrWidth, scrHeight);
	}
}
