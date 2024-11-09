import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileNameExtensionFilter;

//TODO: Find solution to crashing when connection a node to a node that connects back to it
	//I did a fix but im not sure if it gives desirable behavior. it involves telling the circuit what nodes were ran and not letting them run again for the tick
//TODO: Custom nodes by grouping nodes and clicking a button or something to create a custom node. Save the node in the project file (idk figure it out)

//Something needed for file loading and saving. Still not entirely sure how it works
class FileTypeFilter implements FileFilter {
    private String extension;
    private String description;
 
    public FileTypeFilter(String extension, String description) {
        this.extension = extension;
        this.description = description;
    }
 
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }
        return file.getName().endsWith(extension);
    }
 
    public String getDescription() {
        return description + String.format(" (*%s)", extension);
    }
}

public class Main {
	public static Engine engine = new Engine(false);
	public static ArrayList<Node> nodes = new ArrayList<>();
	public static Font pointFont = new Font("Helvetica", Font.PLAIN, 16);
	public static Font nodeFont = new Font("Helvetica", Font.PLAIN, 20);
	
	public static boolean startedRightClick = false, startedLeftClick = false;
	public static String grabbedUUID = "", grabbedNode = "";
	public static String hoveringID = "";
	
	//A line drawn by the cursor when holding right-click, used to break connections between inputs and outputs
	public static Line severLine = new Line(-1,-1,-1,-1);
	
	public static JFileChooser fileChooser = new JFileChooser();
	public static String chosenFilePath = "";
	public static String saveFilePath = "";

	public static Color bgColor = new Color(100,94,101);
	public static Color drawLineColor = new Color(255,255,255);
	public static Color offLineColor = new Color(105,0,0);
	public static Color onLineColor = new Color(240,0,0);
	public static Color severLineColor = new Color(0,0,0);
	public static Color menuNodeAreaColor = new Color(90,84,91);
	public static Color menuNodeColor = new Color(110,104,111);
	public static Color menuNodeHoveringColor = new Color(120,114,121);
	public static int nodeOutlineWidth = 3;
	public static int nodeCornerArc = 3;
	public static int connectionLineWidth = 3;
	public static int grabXOffset = 0, grabYOffset = 0;
	public static int menuAreaHeight = 50, menuNodeMargin = 5;
	
	public static ArrayList<String> ranUUIDs = new ArrayList<>();
	
	public static int transCount = 0, critPathDelay = 0, currentPathDelay = 0;
	public static ArrayList<Integer> pathDelays = new ArrayList<>();
	public static boolean startedLeft = false;
	
	public static ArrayList<MenuNode> menuNodes = new ArrayList<>();
	
	public static BufferedImage trashCan;
	
	public static int[] selectBox = {-9999,-9999,-9999,-9999};
	public static ArrayList<String> selectedNodes = new ArrayList<>();
	
	public static int displayScroll = 0, panAmount = 5;
	public static double scrollMultiplier = 3.5, panMultiplier = 1;
	public static int cameraVX = 0, cameraVY = 0;
	
	public static JMenuBar menuBar = new JMenuBar();
	public static JPopupMenu rightClickMenu = new JPopupMenu();
	
	public static String rightClickedUUID = "";
	
	public static void main(String[] args) {
		try {
			trashCan = ImageIO.read(Engine.class.getResourceAsStream("/trashy.png"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Load a file chooser upon program launch
		fileChooser.setDialogTitle("Choose Circuit File ([ESCAPE] for blank project)");
		fileChooser.setFileFilter(new FileNameExtensionFilter("Circuit Files", "circ"));
		//fileChooser.showSaveDialog(null);
		//fileChooser.addChoosableFileFilter(null);
		int dialog = fileChooser.showOpenDialog(null);
		if (dialog == JFileChooser.APPROVE_OPTION)
			 
        {
            // set the label to the path of the selected file
            chosenFilePath = fileChooser.getSelectedFile().getAbsolutePath();
        }
		
		loadSaveFile("save1.circ");

		/*
		nodes.add(new Node(100, 100, "switch", null, null, nodeFont));
		nodes.add(new Node(500, 150, "light", null, null, nodeFont));
		nodes.get(0).outputs[0].createConnection(nodes.get(1).inputs[0].uuid, engine, nodes);
		*/
		
		//Do ingine initialization process
		engine.initializeJFrame(800, 800, true, false, 60);
		menuBar.setFont(nodeFont);
		
        JMenu barFileMenu = new JMenu("File");
        JMenuItem saveMenuItem = new JMenuItem("Save Project As");
        JMenuItem nodeMenuItem = new JMenuItem("Open Custom Node");
        
        saveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	openSaveDialogue("Save Project File", "Circuit File", "circ");
				saveToFile(chosenFilePath);
            }
        });
        
        nodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	Methods.loadCustomNodeFromFile(nodes, nodeFont, engine);
            }
        });

        barFileMenu.add(saveMenuItem);
        barFileMenu.add(nodeMenuItem);
        menuBar.add(barFileMenu);
        engine.frame.setJMenuBar(menuBar);

        JMenuItem cloneOption = new JMenuItem("Clone");
        JMenuItem deleteOption = new JMenuItem("Delete");
        
        
        cloneOption.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		if (!rightClickedUUID.equals("")) cloneNode(rightClickedUUID);
        		rightClickedUUID = "";
        		rightClickMenu.setVisible(false);
        	}
        });
        
        deleteOption.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		if (!rightClickedUUID.equals("")) deleteNode(rightClickedUUID);
        		rightClickedUUID = "";
        		rightClickMenu.setVisible(false);
        	}
        });
        

        rightClickMenu.add(cloneOption);
        rightClickMenu.add(deleteOption);
        
        
		
		engine.frame.setVisible(true);
		
		String[] menuNodesToAdd = {"switch", "light", "and", "or", "not", "xor", "nand", "nor", "xnor", "4BitNumber", "4BitDisplay", "4BitAdder", "decoder", "encoder", "mux", "register"};
		
		int nextX = 0;
		for (int i = 0; i < menuNodesToAdd.length; i++) {
			addMenuNode(menuNodesToAdd[i], nextX + menuNodeMargin);
			menuNodes.get(menuNodes.size() - 1).x += menuNodes.get(menuNodes.size() - 1).w / 2;
			nextX = menuNodes.get(menuNodes.size() - 1).x + (menuNodes.get(menuNodes.size() - 1).w / 2);
		}
		
		try {
			engine.run();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void addMenuNode(String id, int x) {
		menuNodes.add(new MenuNode(x, engine.scrHeight - 50, id, nodeFont));
		//menuNodes.get(menuNodes.size() - 1).x += menuNodes.get(menuNodes.size() - 1).w / 2;
	}
	
	public static void paintNodes(Graphics g) {
		//Iterate over every node instance and render
		boolean hovering = false;
		
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).drawConnectionLines(g, engine, pointFont, nodeFont, engine.camera, nodes, grabbedUUID);
		}
		
		boolean aNodeIsOnScreen = false;
		
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).render(g, engine, pointFont, nodeFont, nodes, grabbedUUID, selectedNodes)) aNodeIsOnScreen = true;
			if (nodes.get(i).mouseIsHovering(engine)) {
				hovering = true;
				if (engine.keys.K_CONTROL()) {
					g.drawImage(trashCan, nodes.get(i).x + (nodes.get(i).w / 2) - 5 - engine.camera.getX(), nodes.get(i).y - (nodes.get(i).h / 2) - 15 - engine.camera.getY(), 30, 30, null);
				}
			}
		}
		
		if (!aNodeIsOnScreen && nodes.size() != 0) {
			g.setColor(Color.white);
			int nodePointX = nodes.get(0).x - engine.camera.getX();
			int nodePointY = nodes.get(0).y - engine.camera.getY();
			
			g.drawLine(engine.mouse.getX(), engine.mouse.getY(), nodePointX, nodePointY);
		}
		
		boolean isHoveringOverPauser = false;
		
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).mouseIsHovering(engine) && nodes.get(i).pausePanning) isHoveringOverPauser = true;
		}
		
		
		if (!isHoveringOverPauser) {
			//If holding middle mouse button, drag camera around with mouse movement
			if (engine.mouse.MIDDLE()) {
				int[] delta = engine.mouse.getDelta();
				engine.camera.addX(-delta[0]);
				engine.camera.addY(-delta[1]);
				
				for (int i = 0; i < nodes.size(); i++) {
					nodes.get(i).redrawConnections(nodes, engine);
				}
			}
			
			//Trackpad panning support area
			int scrollDifference = (int) (engine.mouse.getScrollDifference() * scrollMultiplier);
			if (engine.mouse.getY() >= engine.scrHeight - menuAreaHeight) {
				displayScroll -= engine.mouse.getScrollDifference() * scrollMultiplier;
				displayScroll = Math.max(0, displayScroll);
				
				if (menuNodes.size() > 0) {
					if (menuNodes.get(menuNodes.size() - 1).x + (menuNodes.get(menuNodes.size() - 1).w / 2) > engine.scrWidth - menuNodeMargin) {
						displayScroll = Math.min(displayScroll, menuNodes.get(menuNodes.size() - 1).x + (menuNodes.get(menuNodes.size() - 1).w / 2) - (engine.scrWidth - menuNodeMargin));
					}
					else {
						displayScroll = Math.min(displayScroll, 0);
					}
				}
				
			}
			else {
				if (engine.mouse.getIsVerticalScroll()) engine.camera.addY(-scrollDifference);
				else engine.camera.addX(-scrollDifference);
			}
			
			/* Probably nt as good, kinda finicky. Wouldn't mind coming back to it to make it better
			//Better trackpad support???
			int delta = engine.mouse.getScrollDifference();
			if (engine.mouse.getScrollAmount() != 0) {
				if (engine.mouse.getIsVerticalScroll()) {
					if (Math.pow(cameraVY, delta) < 0) cameraVY = delta;
					else cameraVY += delta;
				}
				else {
					if (Math.pow(cameraVX, delta) < 0) cameraVX = delta;
					else cameraVX += delta;
				}
			}
			
			System.out.println(cameraVX + ", " + cameraVY);
			engine.camera.addY((int) (-cameraVY *  panMultiplier));
			engine.camera.addX((int) (-cameraVX *  panMultiplier));
			
			if (cameraVY < 0) cameraVY++;
			if (cameraVY > 0) cameraVY--;
			if (cameraVX < 0) cameraVX++;
			if (cameraVX > 0) cameraVX--;
			*/
		}
		
		if (engine.mouse.RIGHTCLICKED()) {
			boolean hoveringOverNode = false;
			for (Node node : nodes) {
				if (node.mouseIsHovering(engine)) {
					hoveringOverNode = true;
					rightClickedUUID = node.uuid;
				}
			}
			if (hoveringOverNode) {
				rightClickMenu.setLocation(new Point(engine.mouse.getX(), engine.mouse.getY() + 30));
				rightClickMenu.setVisible(true);
			}
			
		}
		
		//If holding right click, draw sever line
		if (engine.mouse.RIGHT()) {
			g.setColor(severLineColor);
			g.drawLine(severLine.x1, severLine.y1, severLine.x2, severLine.y2);
		}
		
		if (engine.mouse.LEFT()) rightClickMenu.setVisible(false);
		
		//If holding an input/output, draw a line from cursor to held
		if (!grabbedUUID.equals("")) {
			g.setColor(drawLineColor);
			Node.Output output = Methods.searchOutputsByUUID(nodes, grabbedUUID);
			g.drawLine(output.trueX, output.trueY, engine.mouse.getX(), engine.mouse.getY());
		}
		
		if (engine.mouse.LEFT()) {
			rightClickMenu.setVisible(false);
			g.setColor(new Color(100,100,100,100));
			g.fillRect(Math.min(selectBox[0], selectBox[2]), Math.min(selectBox[1], selectBox[3]), Math.max(selectBox[0], selectBox[2]) - Math.min(selectBox[0], selectBox[2]), Math.max(selectBox[1], selectBox[3]) - Math.min(selectBox[1], selectBox[3]));
		}
	}
	
	public static void paintMenuNodes(Graphics g) {
		for (int i = 0; i < menuNodes.size(); i++) {
			menuNodes.get(i).render(g, pointFont, nodeFont, displayScroll, engine);
		}
	}

	public static void paintToFrame(Graphics g) {
		engine.setBackground(g, bgColor);
		paintNodes(g);
		g.setColor(menuNodeAreaColor);
		g.fillRect(0, engine.scrHeight - menuAreaHeight, engine.scrWidth, menuAreaHeight);
		paintMenuNodes(g);
		
		/*
		g.setFont(nodeFont);
		g.setColor(Color.black);
		g.drawString("Transistors: " + String.valueOf(getAmountOfTransistors()), 10, 50);
		getCriticalPathDelay();
		g.drawString("Critical Path Delay: " + critPathDelay + "ns", 10, 100);
		*/
	}

	public static void mainLoop() {
		
		for (int i = 0; i < menuNodes.size(); i++) {
			menuNodes.get(i).y = engine.scrHeight - 25;
		}
			
		
		
		//Iterate over nodes and update (deals with connections)
		//Keeps track of what nodes are ran using an arraylist of uuids in order to avoid infinite looping
		for (Node node : nodes) {
			node.update(engine, nodes, ranUUIDs, hoveringID);
		}
		ranUUIDs.clear();
		
		//If holding right click, create sever line
		if (engine.mouse.RIGHT()) {
			makeSeverLine();
			
		}
		else {
			//Process to know where to start the line so it can be properly drawn
			if (startedRightClick) {
				severConnection();
			}
			else {
				severLine = new Line(-1,-1,-1,-1);
			}
		}
		
		//If holding ctrl and click on node, remove it and break all connections that it has
		if (engine.keys.K_CONTROL()) {
			deleteNode();
		}
		
		if (engine.keys.DELETETYPED()) {
			for (int i = 0; i < nodes.size(); i++) {
				if (selectedNodes.contains(nodes.get(i).uuid)) {
					nodes.get(i).prepareForRemoval(nodes);
					i--;
				}
			}
			selectedNodes.clear();
		}
		
		//If holding control and left click node, duplicate node and grab
		if (engine.keys.K_ALT() && engine.mouse.LEFT()) {
			if (grabbedUUID.equals("") && grabbedNode.equals("")) {
				cloneNode(); 
			}
		}
		
		if (engine.keys.LSHIFT() && engine.mouse.LEFTCLICKED()) {
			selectNode();
		}
		
		//If holding left click and not shift, grab node
		if (engine.mouse.LEFT() && !engine.keys.LSHIFT()) {
			grabNode();
		}
		else {
			grabInput();
		}
		
		if (engine.keys.K_CMD() || engine.keys.K_CONTROL()) {
			if (engine.keys.STYPED()) {
				openSaveDialogue("Save Project File", "Circuit File", "circ");
				saveToFile(chosenFilePath);
			}
			
			if (engine.keys.NTYPED()) {
				Methods.loadCustomNodeFromFile(nodes, nodeFont, engine);
			}
		}
	}
	
	
	
	public static boolean checkForNodeInSelection(Node node) {
		if (node.x - node.w / 2 <= Math.max(selectBox[0], selectBox[2]) &&
			node.x + node.w / 2 >= Math.min(selectBox[0], selectBox[2]) &&
			node.y - node.h / 2 <= Math.max(selectBox[1], selectBox[3]) &&
			node.y + node.h / 2 >= Math.min(selectBox[1], selectBox[3])) {
			return true;
		}
		else return false;
	}
	
	public static void clearSelectedNodes() {
		for (Node node : nodes) {
			node.selected = false;
		}
	}
	
	public static int getIndexByUUID(String uuid) {
		return -1;
	}
	
	public static int getAmountOfTransistors() {
		int toOut = 0;
		
		for (int i = 0; i < nodes.size(); i++) {
			toOut += nodes.get(i).transistors;
		}
		
		return toOut;
	}
	
	public static void loadSaveFile(String saveFile) {
		//Please dont ask me how this works, its pretty much just a clusterfuck of code that i barely understand why it works
		//If you decide to try and decode this yourself, good luck
		Scanner reader = null;
		try {
			if (chosenFilePath.equals("")) reader = engine.loadScannerFromSourceFolder("save1.circ");
			else reader = new Scanner(new File(chosenFilePath));
		}
		catch (Exception e) {}
		boolean readingNode = false;
		
		String id = "", uuid = "", customBehavior = "";
		int x = 0, y = 0, decoderAmount = 0, h = 0;
		ArrayList<Node.Input> inputs = new ArrayList<>();
		ArrayList<Node.Output> outputs = new ArrayList<>();
		boolean readingInputs = false, readingOutputs = false;
		
		while (reader.hasNextLine()) {
			String readLine = reader.nextLine();
			if (readLine.equals("{")) {
				readingInputs = false;
				readingOutputs = false;
				id = reader.nextLine();
				x = Integer.valueOf(reader.nextLine());
				y = Integer.valueOf(reader.nextLine());
				uuid = reader.nextLine();
				decoderAmount = Integer.valueOf(reader.nextLine());
				h = Integer.valueOf(reader.nextLine());
				customBehavior = reader.nextLine();
				
				String inputOutputCheck = reader.nextLine();
				
				if (inputOutputCheck.equals("input:")) {
					boolean readingInput = true;
					while (readingInput) {
						Scanner line = new Scanner(reader.nextLine());
						String inID = line.next();
						String inUUID = line.next();
						
						Node.Input toInput = new Node.Input(inID, uuid);
						toInput.uuid = inUUID;
						
						inputs.add(toInput);
						
						if (line.hasNext()) {
							String doneCheck = line.next();
							if (doneCheck.equals("done")) {
								readingInput = false;
							}
						}
					}
					inputOutputCheck = reader.nextLine();
				}
				
				if (inputOutputCheck.equals("output:")) {
					boolean readingOutput = true;
					while (readingOutput) {
						Scanner line = new Scanner(reader.nextLine());
						String inID = line.next();
						String inUUID = line.next();
						
						Node.Output toOutput = new Node.Output(inID, uuid);
						toOutput.uuid = inUUID;
						
						outputs.add(toOutput);
						
						if (line.hasNext()) {
							String doneCheck = line.next();
							if (doneCheck.equals("done")) {
								readingOutput = false;
							}
						}
					}
				}

				String[] toInputs = new String[0];
				String[] toOutputs = new String[0];
				Node toMake = new Node(x,y,id,toInputs, toOutputs, nodeFont, !customBehavior.equals(""));
				toMake.uuid = uuid;
				toMake.decoderAmount = decoderAmount;
				
				toMake.customBehavior = customBehavior;
				toMake.parseCustomActions();
				
				if (inputs.size() > 0) {
					Node.Input[] inputsArray = new Node.Input[inputs.size()];
					for (int i = 0; i < inputs.size(); i++) {
						inputsArray[i] = inputs.get(i);
					}
					toMake.inputs = inputsArray;
				}
				
				if (outputs.size() > 0) {
					Node.Output[] outputsArray = new Node.Output[outputs.size()];
					for (int i = 0; i < outputs.size(); i++) {
						outputsArray[i] = outputs.get(i);
					}
					toMake.outputs = outputsArray;
				}
				
				inputs.clear();
				outputs.clear();
				nodes.add(toMake);
			}
			Scanner line = new Scanner(readLine);
			if (line.next().equals("con:")) {
				String outputUUID = line.next();
				String inputUUID = line.next();
				Node.Output find = Methods.searchOutputsByUUID(nodes, outputUUID);
				find.createConnection(inputUUID, engine, nodes, ranUUIDs);
			}
		}
	}
	
	public static void openSaveDialogue(String title, String fileTypeName, String fileType) {
		fileChooser.setDialogTitle("Save Project File");
		fileChooser.setFileFilter(new FileNameExtensionFilter("Circuit File", "circ"));
		int dialog = fileChooser.showSaveDialog(null);
		if (dialog == JFileChooser.APPROVE_OPTION)
			 
        {
            // set the label to the path of the selected file
            chosenFilePath = fileChooser.getSelectedFile().getAbsolutePath();
        }
	}
	
	public static void saveToFile(String filename) {
		if (nodes.size() > 0) {
			try {
				if (!filename.substring(filename.length() - 5, filename.length()).equals(".circ")) filename += ".circ";
				
				FileOutputStream fout = new FileOutputStream(filename);
				
				String data = "";
				
				for (Node node : nodes) {
					data += saveNode(node);
				}
				data += collectConnections();

				data = data.substring(0, data.length() - 1);
				
				fout.write(data.getBytes());
				
				fout.flush();
				fout.close();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			System.out.println("No content in project, no file created.");
		}
		
	}
	
	public static String saveNode(Node node) {
		String toOut = "";
		toOut += "{\n";
		toOut += node.id + "\n";
		toOut += node.x + "\n";
		toOut += node.y + "\n";
		toOut += node.uuid + "\n";
		toOut += node.decoderAmount + "\n";
		toOut += node.h + "\n";
		toOut += node.customBehavior + "\n";
		if (node.inputs.length > 0) toOut += "input:\n";
		for (int i = 0; i < node.inputs.length; i++) {
			String toAdd = node.inputs[i].id + " " + node.inputs[i].uuid;
			if (i == node.inputs.length - 1) toAdd += " done";
			toAdd += "\n";
			toOut += toAdd;
		}
		if (node.outputs.length > 0) toOut += "output:\n";
		for (int i = 0; i < node.outputs.length; i++) {
			String toAdd = node.outputs[i].id + " " + node.outputs[i].uuid;
			if (i == node.outputs.length - 1) toAdd += " done";
			toAdd += "\n";
			toOut += toAdd;
		}
		/*
		if (node.outputs.length > 0) {
			toOut += "output:\n";
			String toAdd = node.outputs[0].id + " " + node.outputs[0].uuid + "\n";
			toOut += toAdd;
		}
		*/
		toOut += "}\n";
		return toOut;
	}
	
	public static String collectConnections() {
		String toOut = "";
		
		for (Node node : nodes) {
			for (Node.Output output : node.outputs) {
				for (String uuid : output.connectedUUIDs) {
					toOut += "con: " + output.uuid + " " + uuid + "\n";
				}
			}
		}
		
		return toOut;
	}

	public static void getCriticalPathDelay() {
		/*
		 * Idea: iterate over every switch, go through the output into the next input until reaching a light while gathering the total gate delay times
		 * Collect all total gate delay times and pick max as critical path delay
		 */
		critPathDelay = 0;
		pathDelays.clear();
		
		
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).id.equals("switch")) {
				currentPathDelay = 0;
				propogateNodes(nodes.get(i));
				pathDelays.add(currentPathDelay);
			}
		}
		
		if (pathDelays.size() > 0) {
			Collections.sort(pathDelays);
			critPathDelay = pathDelays.get(pathDelays.size() - 1);
		}
	}
	
	public static void propogateNodes(Node node) {
		if (node.id.equals("light")) return;
		else {
			for (int i = 0; i < node.outputs.length; i++) {
				for (int ii = 0; ii < node.outputs[i].connectedUUIDs.size(); ii++) {
					currentPathDelay += node.inputs.length;
					Node toProp = Methods.searchNodesByUUID(nodes, Methods.searchInputsByUUID(nodes, node.outputs[i].connectedUUIDs.get(ii)).parentUUID);
					propogateNodes(toProp);
				}
			}
		}
	}
	
	public static void cloneNode() {
		for (Node node : nodes) {
			if (node.mouseIsHovering(engine)) {
				nodes.add(new Node(engine.mouse.getX(), engine.mouse.getY(), node.id, null, null, nodeFont, false));
				grabbedNode = nodes.get(nodes.size() - 1).uuid;
				break;
			}
		}
	}
	public static void cloneNode(String uuid) {
		Node toClone = Methods.searchByUUID(nodes, uuid);
		nodes.add(new Node(engine.mouse.getX(), engine.mouse.getY(), toClone.id, null, null, nodeFont, false));
	}
	
	public static void makeSeverLine() {
		if (!startedRightClick) {
			severLine.x1 = engine.mouse.getX();
			severLine.y1 = engine.mouse.getY();
			startedRightClick = true;
		}
		severLine.x2 = engine.mouse.getX();
		severLine.y2 = engine.mouse.getY();
	}
	
	public static void severConnection() {
		for (int i = 0; i < nodes.size(); i++) {
			for (int j = 0; j < nodes.get(i).outputs.length; j++) {
				for (int k = 0; k < nodes.get(i).outputs[j].connections.size(); k = k) {
					if (severLine.doIntersect(nodes.get(i).outputs[j].connections.get(k))) {
						nodes.get(i).outputs[j].severConnection(nodes, k);
					}
					else k++;
				}
			}
		}
		startedRightClick = false;
	}
	
	public static void deleteNode() {
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).mouseIsHovering(engine) && engine.mouse.LEFTCLICKED()) {
				nodes.get(i).prepareForRemoval(nodes);
			}
		}
	}
	
	public static void deleteNode(String uuid) {
		Methods.searchByUUID(nodes, uuid).prepareForRemoval(nodes);
		if (selectedNodes.contains(uuid)) selectedNodes.remove(uuid);
		if (selectedNodes.size() > 0) {
			for (String selected : selectedNodes) {
				Methods.searchByUUID(nodes, selected).prepareForRemoval(nodes);
			}
		}
		selectedNodes.clear();
	}
	
	public static void selectNode() {
		for (Node node : nodes) {
			if (node.mouseIsHovering(engine)) {
				if (selectedNodes.contains(node.uuid)) selectedNodes.remove(node.uuid);
				else selectedNodes.add(node.uuid);
			}
		}
	}
	
	public static void grabNode() {
		if (!startedLeft) {
			boolean grabbedSomething = false;
			boolean hovering = false;
			for (Node node : nodes) {
				for (Node.Output output : node.outputs) {
					if (Methods.inProx(output.trueX, output.trueY, node.pointRad, engine.mouse.getX(), engine.mouse.getY(), node.pointProx)) {
						grabbedUUID = output.uuid;
						grabbedSomething = true;
					}
				}
				if (grabbedUUID.equals("") && grabbedNode.equals("")) {
					if (node.mouseIsHovering(engine)) {
						hovering = true;
						grabbedNode = node.uuid;
						grabXOffset = (node.x - engine.camera.getX()) - engine.mouse.getX();
						grabYOffset = (node.y - engine.camera.getY()) - engine.mouse.getY();
						
						if (!selectedNodes.contains(grabbedNode)) {
							selectedNodes.clear();
							
							for (Node nodeAgain : nodes) {
								nodeAgain.multipleSelectMouseOffsetX = 0;
								nodeAgain.multipleSelectMouseOffsetY = 0;
							}
						}
						else {
							for (String uuid : selectedNodes) {
								Node selected = Methods.searchByUUID(nodes, uuid);
								selected.multipleSelectMouseOffsetX = selected.x - engine.mouse.getX() - engine.camera.getX();
								selected.multipleSelectMouseOffsetY = selected.y - engine.mouse.getY() - engine.camera.getY();
							}
						}
					}
						
					grabbedSomething = true;
				}
			}
			
			if (!hovering) selectedNodes.clear();
			
			//Menu of nodes to grab from at the bottom, detect if leftclicking one and grab one
			for (MenuNode node : menuNodes) {
				if (node.mouseIsHovering(engine, displayScroll) && grabbedNode.equals("")) {
					nodes.add(new Node(engine.mouse.getX(), engine.mouse.getY(), node.id, null, null, nodeFont, false));
					grabbedNode = nodes.get(nodes.size() - 1).uuid;
					break;
				}
			}
		}
		
		if (!grabbedNode.equals("")) { //If already grabbed node, can't grab more (also filter for grabbing input/output)
			Node grabbed = Methods.searchNodesByUUID(nodes, grabbedNode);
			grabbed.x = engine.mouse.getX() + engine.camera.getX() + grabXOffset;
			grabbed.y = engine.mouse.getY() + engine.camera.getY() + grabYOffset;
			
			if (selectedNodes.contains(grabbedNode)) {
				for (String uuid : selectedNodes) {
					if (!uuid.equals(grabbedNode)) {
						Node toMove = Methods.searchByUUID(nodes, uuid);
						toMove.x = engine.mouse.getX() + engine.camera.getX() + toMove.multipleSelectMouseOffsetX;
						toMove.y = engine.mouse.getY() + engine.camera.getY() + toMove.multipleSelectMouseOffsetY;
					}
				}
			}
			grabbed.redrawConnections(nodes, engine);
		}
		startedLeft = true;
	}
	
	public static void grabInput() {
		if (!grabbedUUID.equals("")) {
			for (Node node : nodes) {
				for (Node.Input input : node.inputs) {
					if (Methods.inProx(input.trueX, input.trueY, node.pointRad, engine.mouse.getX(), engine.mouse.getY(), node.pointProx)) {
						if (!input.uuid.equals(grabbedUUID) && !grabbedUUID.equals("")) {
							Methods.searchOutputsByUUID(nodes, grabbedUUID).createConnection(input.uuid, engine, nodes, ranUUIDs);
						}
					}
				}
			}
		}
		startedLeft = false;
		grabbedUUID = "";
		grabbedNode = "";
	}
}
