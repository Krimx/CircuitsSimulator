import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

//TODO: Find solution to crashing when connection a node to a node that connects back to it
	//I did a fix but im not sure if it gives desirable behavior. it involves telling the circuit what nodes were ran and not letting them run again for the tick
//TODO: Custom nodes by grouping nodes and clicking a button or something to create a custom node. Save the node in the project file (idk figure it out)

class ControlsPanel extends JPanel {
	public void paintComponent(Graphics g) {
		g.setColor(new Color(55, 55, 65));
		g.fillRect(0, 0, 1000, 1000);
		
		//Control and use guide
		g.setFont(Main.nodeFont);
		g.setColor(Color.black);
		g.drawString("[1]: ", Main.exampleSwitch.x - 75, Main.exampleSwitch.y + 6);
		g.drawString("[2]: ", Main.exampleSwitch.x - 75, Main.exampleLight.y + 6);
		g.drawString("[3]: ", Main.exampleSwitch.x - 75, Main.exampleAnd.y + 6);
		g.drawString("[4]: ", Main.exampleSwitch.x - 75, Main.exampleOr.y + 6);
		g.drawString("[5]: ", Main.exampleSwitch.x - 75, Main.exampleNot.y + 6);
		g.drawString("[6]: ", Main.exampleSwitch.x - 75, Main.exampleXor.y + 6);
		g.drawString("[7]: ", Main.exampleSwitch.x - 75, Main.exampleNand.y + 6);
		g.drawString("[8]: ", Main.exampleSwitch.x - 75, Main.exampleNor.y + 6);
		g.drawString("[9]: ", Main.exampleSwitch.x - 75, Main.exampleXnor.y + 6);

		Main.exampleSwitch.render(g, Main.pointFont, Main.nodeFont);
		Main.exampleLight.render(g, Main.pointFont, Main.nodeFont);
		Main.exampleAnd.render(g, Main.pointFont, Main.nodeFont);
		Main.exampleOr.render(g, Main.pointFont, Main.nodeFont);
		Main.exampleNot.render(g, Main.pointFont, Main.nodeFont);
		Main.exampleXor.render(g, Main.pointFont, Main.nodeFont);
		Main.exampleNand.render(g, Main.pointFont, Main.nodeFont);
		Main.exampleNor.render(g, Main.pointFont, Main.nodeFont);
		Main.exampleXnor.render(g, Main.pointFont, Main.nodeFont);
		
		g.setFont(Main.nodeFont);
		g.setColor(Color.black);
		g.drawString("Shift + Left Click: Delete Node", Main.exampleSwitch.x + 75, Main.exampleSwitch.y + 6);
		g.drawString("Right Click + Drag: Sever Link", Main.exampleLight.x + 75, Main.exampleLight.y + 6);
		g.drawString("Middle Mouse + Drag: Look Around", Main.exampleAnd.x + 75, Main.exampleAnd.y + 6);
	}
}

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
	public static ArrayList<Node> displayNodes = new ArrayList<>();
	public static Font pointFont = new Font("Helvetica", Font.PLAIN, 16);
	public static Font nodeFont = new Font("Helvetica", Font.PLAIN, 20);
	
	public static boolean startedRightClick = false, startedLeftClick = false;
	public static String grabbedUUID = "", grabbedNode = "";
	
	//A line drawn by the cursor when holding right-click, used to break connections between inputs and outputs
	public static Line severLine = new Line(-1,-1,-1,-1);
	
	public static JFileChooser fileChooser = new JFileChooser();
	public static String chosenFilePath = "";
	public static String saveFilePath = "";
	
	//Two windows, one for "canvas" and one for controls guide. consider merging into one for dragging from selection menu
	public static JFrame controlsFrame = new JFrame();
	public static ControlsPanel controlsPanel = new ControlsPanel();
	
	//Visual user guide (prone to being changed for visual and ui overhual)
	public static int yOffset = -40;
	public static Node exampleSwitch = new Node(100,100 + yOffset,"switch", null, null, nodeFont);
	public static Node exampleLight = new Node(100,180 + yOffset,"light", null, null, nodeFont);
	public static Node exampleAnd = new Node(100,260 + yOffset,"and", null, null, nodeFont);
	public static Node exampleOr = new Node(100,340 + yOffset,"or", null, null, nodeFont);
	public static Node exampleNot = new Node(100,420 + yOffset,"not", null, null, nodeFont);
	public static Node exampleXor = new Node(100,500 + yOffset,"xor", null, null, nodeFont);
	public static Node exampleNand = new Node(100,580 + yOffset,"nand", null, null, nodeFont);
	public static Node exampleNor = new Node(100,660 + yOffset,"nor", null, null, nodeFont);
	public static Node exampleXnor = new Node(100,740 + yOffset,"xnor", null, null, nodeFont);
	
	public static ArrayList<String> ranUUIDs = new ArrayList<>();
	
	public static int transCount = 0;
	
	public static ArrayList<DisplayNode> menuNodes = new ArrayList<>();
	
	public static BufferedImage trashCan;
	
	public static int[] selectBox = {-9999,-9999,-9999,-9999};
	public static ArrayList<String> selectedNodes = new ArrayList<>();
	
	public static void main(String[] args) {
		try {
			trashCan = ImageIO.read(new FileInputStream("recs/trashy.png"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Load a file chooser upon program launch
		fileChooser.setDialogTitle("Choose Circuit File (Close for default)");
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
		engine.initializeJFrame(800, 800, false, false, 60);
		
		//Second jframe initialization
		controlsFrame.setSize(500,800);
		controlsFrame.setLocationRelativeTo(engine.frame);
		controlsFrame.setLocation(engine.frame.getLocation().x + engine.scrWidth, engine.frame.getLocation().x);
		controlsFrame.getContentPane().add(controlsPanel);
		controlsFrame.setVisible(true);
		engine.frame.setVisible(true);

		addDisplayNode("switch", 50);
		addDisplayNode("light", 130);
		addDisplayNode("and", 200);
		addDisplayNode("or", 260);
		addDisplayNode("not", 320);
		addDisplayNode("xor", 390);
		addDisplayNode("nand", 470);
		addDisplayNode("nor", 550);
		addDisplayNode("xnor", 620);
		
		try {
			engine.run();
		}
		catch(Exception e) {}
	}
	
	public static void addDisplayNode(String id, int x) {
		menuNodes.add(new DisplayNode(x, engine.scrHeight - 50, id, nodeFont));
		//menuNodes.get(menuNodes.size() - 1).x += menuNodes.get(menuNodes.size() - 1).w / 2;
	}
	
	public static void paintNodes(Graphics g) {
		//Iterate over every node instance and render
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).render(g, engine, pointFont, nodeFont, engine.camera, nodes, grabbedUUID);
			if (engine.keys.LSHIFT() && nodes.get(i).mouseIsHovering(engine)) {
				g.drawImage(trashCan, nodes.get(i).x + (nodes.get(i).w / 2) - 5, nodes.get(i).y - (nodes.get(i).h / 2) - 15, 30, 30, null);
			}
		}
		
		//If holding middle mouse button, drag camera around with mouse movement
		if (engine.mouse.MIDDLE()) {
			int[] delta = engine.mouse.getDelta();
			engine.camera.addX(-delta[0]);
			engine.camera.addY(-delta[1]);
			
			for (int i = 0; i < nodes.size(); i++) {
				nodes.get(i).redrawConnections(nodes, engine);
			}
		}
		
		//If holding right click, draw sever line
		if (engine.mouse.RIGHT()) {
			g.setColor(new Color(255,255,255,100));
			g.drawLine(severLine.x1, severLine.y1, severLine.x2, severLine.y2);
		}
		
		//If holding an input/output, draw a line from cursor to held
		if (!grabbedUUID.equals("")) {
			g.setColor(new Color(100,100,100,100));
			Node.Output output = searchByUUID(grabbedUUID);
			g.drawLine(output.trueX, output.trueY, engine.mouse.getX(), engine.mouse.getY());
		}
		
		if (engine.mouse.LEFT()) {
			g.setColor(new Color(100,100,100,100));
			g.fillRect(Math.min(selectBox[0], selectBox[2]), Math.min(selectBox[1], selectBox[3]), Math.max(selectBox[0], selectBox[2]) - Math.min(selectBox[0], selectBox[2]), Math.max(selectBox[1], selectBox[3]) - Math.min(selectBox[1], selectBox[3]));
		}
	}
	
	public static void paintDisplayNodes(Graphics g) {
		for (int i = 0; i < menuNodes.size(); i++) {
			menuNodes.get(i).render(g, pointFont, nodeFont);
		}
	}

	public static void paintToFrame(Graphics g) {
		engine.setBackground(g, new Color(65, 65, 75));
		paintNodes(g);
		paintDisplayNodes(g);
		
		g.setFont(nodeFont);
		g.setColor(Color.black);
		g.drawString("Transistors: " + String.valueOf(getAmountOfTransistors()), 10, 50);
	}

	public static void mainLoop() {
		//Based on pressed key, summon a node at cursor
		if (engine.keys.K1TYPED()) nodes.add(new Node(engine.mouse.getX(), engine.mouse.getY(), "switch", null, null, nodeFont));
		if (engine.keys.K2TYPED()) nodes.add(new Node(engine.mouse.getX(), engine.mouse.getY(), "light", null, null, nodeFont));
		if (engine.keys.K3TYPED()) nodes.add(new Node(engine.mouse.getX(), engine.mouse.getY(), "and", null, null, nodeFont));
		if (engine.keys.K4TYPED()) nodes.add(new Node(engine.mouse.getX(), engine.mouse.getY(), "or", null, null, nodeFont));
		if (engine.keys.K5TYPED()) nodes.add(new Node(engine.mouse.getX(), engine.mouse.getY(), "not", null, null, nodeFont));
		if (engine.keys.K6TYPED()) nodes.add(new Node(engine.mouse.getX(), engine.mouse.getY(), "xor", null, null, nodeFont));
		if (engine.keys.K7TYPED()) nodes.add(new Node(engine.mouse.getX(), engine.mouse.getY(), "nand", null, null, nodeFont));
		if (engine.keys.K8TYPED()) nodes.add(new Node(engine.mouse.getX(), engine.mouse.getY(), "nor", null, null, nodeFont));
		if (engine.keys.K9TYPED()) nodes.add(new Node(engine.mouse.getX(), engine.mouse.getY(), "xnor", null, null, nodeFont));
		
		//Iterate over nodes and update (deals with connections)
		//Keeps track of what nodes are ran using an arraylist of uuids in order to avoid infinite looping
		for (Node node : nodes) {
			node.update(engine, nodes, ranUUIDs);
		}
		ranUUIDs.clear();
		
		//If holding right click, create sever line
		if (engine.mouse.RIGHT()) {
			if (!startedRightClick) {
				severLine.x1 = engine.mouse.getX();
				severLine.y1 = engine.mouse.getY();
				startedRightClick = true;
			}
			severLine.x2 = engine.mouse.getX();
			severLine.y2 = engine.mouse.getY();
		}
		else {
			//Process to know where to start the line so it can be properly drawn
			if (startedRightClick) {
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
			else {
				severLine = new Line(-1,-1,-1,-1);
			}
		}
		
		//If holding shift and click on node, remove it and break all connections that it has
		if (engine.keys.LSHIFT()) {
			for (int i = 0; i < nodes.size(); i++) {
				if (nodes.get(i).mouseIsHovering(engine) && engine.mouse.LEFTCLICKED()) {
					nodes.get(i).prepareForRemoval(nodes);
				}
			}
		}
		
		if (engine.keys.K_CONTROL()) {
			if (engine.mouse.LEFT()) {
				if (grabbedUUID.equals("") && grabbedNode.equals("")) {
					for (Node node : nodes) {
						if (node.mouseIsHovering(engine)) {
							nodes.add(new Node(engine.mouse.getX(), engine.mouse.getY(), node.id, null, null, nodeFont));
							grabbedNode = nodes.get(nodes.size() - 1).uuid;
							break;
						}
					}
				}
			}
		}
		
		//If holding left click and not shift, grab node
		if (engine.mouse.LEFT()) {
			for (Node node : nodes) {
				for (Node.Output output : node.outputs) {
					if (node.inRad(output.trueX, output.trueY, node.pointRad, engine.mouse.getX(), engine.mouse.getY())) {
						grabbedUUID = output.uuid;
					}
				}
				if (grabbedUUID.equals("") && grabbedNode.equals("")) {
					if (node.mouseIsHovering(engine)) grabbedNode = node.uuid;
				}
			}
			for (DisplayNode node : menuNodes) {
				if (node.mouseIsHovering(engine) && grabbedNode.equals("")) {
					nodes.add(new Node(engine.mouse.getX(), engine.mouse.getY(), node.id, null, null, nodeFont));
					grabbedNode = nodes.get(nodes.size() - 1).uuid;
					break;
				}
			}
			if (!grabbedNode.equals("")) { //If already grabbed node, can't grab more (also filter for grabbing input/output)
				Node grabbed = searchNodesByUUID(grabbedNode);
				grabbed.x = engine.mouse.getX() + engine.camera.getX();
				grabbed.y = engine.mouse.getY() + engine.camera.getY();
				grabbed.redrawConnections(nodes, engine);
			}
		}
		else {
			if (!grabbedUUID.equals("")) {
				for (Node node : nodes) {
					for (Node.Input input : node.inputs) {
						if (node.inRad(input.trueX, input.trueY, node.pointRad, engine.mouse.getX(), engine.mouse.getY())) {
							if (!input.uuid.equals(grabbedUUID) && !grabbedUUID.equals("")) {
								searchByUUID(grabbedUUID).createConnection(input.uuid, engine, nodes, ranUUIDs);
							}
						}
					}
				}
			}
			grabbedUUID = "";
			grabbedNode = "";
		}
		
		//Pressing [H] allows saving of a current state to a file to be loaded another time or shared
		if (engine.keys.HTYPED()) {
			fileChooser.setDialogTitle("Save Project File");
			fileChooser.setFileFilter(new FileNameExtensionFilter("Circuit File", "circ"));
			int dialog = fileChooser.showSaveDialog(null);
			if (dialog == JFileChooser.APPROVE_OPTION)
				 
	        {
	            // set the label to the path of the selected file
	            chosenFilePath = fileChooser.getSelectedFile().getAbsolutePath();
	        }
			saveToFile(chosenFilePath);
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
	
	public static Node.Output searchByUUID(String uuid) {
		Node.Output output = null;
		for (int i = 0; i < nodes.size(); i++) {
			for (int j = 0; j < nodes.get(i).outputs.length; j++) {
				if (nodes.get(i).outputs[j].uuid.equals(uuid)) output = nodes.get(i).outputs[j];
			}
		}
		return output;
	}
	
	public static Node searchNodesByUUID(String toCheck) {
		Node toOut = null;
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).uuid.equals(toCheck)) toOut = nodes.get(i);
		}
		return toOut;
	}
	
	public static int getAmountOfTransistors() {
		int toOut = 0;
		
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).inputs.length > 0 && nodes.get(i).outputs.length > 0) toOut += (2 * nodes.get(i).inputs.length) + 1;
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
		
		String id = "", uuid = "";
		int x = 0, y = 0;
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
				}
				
				if (inputOutputCheck.equals("output:")) {
					Scanner line = new Scanner(reader.nextLine());
					String inID = line.next();
					String inUUID = line.next();
					
					Node.Output toOutput = new Node.Output(inID, uuid);
					toOutput.uuid = inUUID;
					
					outputs.add(toOutput);
				}
				
				inputOutputCheck = reader.nextLine();
				if (inputOutputCheck.equals("output:")) {
					Scanner line = new Scanner(reader.nextLine());
					String inID = line.next();
					String inUUID = line.next();
					
					Node.Output toOutput = new Node.Output(inID, uuid);
					toOutput.uuid = inUUID;
					
					outputs.add(toOutput);
					reader.nextLine();
				}
				
				Node toMake = new Node(x,y,id,null,null, nodeFont);
				toMake.uuid = uuid;
				
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
				Node.Output find = searchByUUID(outputUUID);
				find.createConnection(inputUUID, engine, nodes, ranUUIDs);
			}
		}
	}
	
	public static void saveToFile(String filename) {
		try {
			System.out.println(filename.substring(filename.length() - 5, filename.length()));
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
	
	public static String saveNode(Node node) {
		String toOut = "";
		toOut += "{\n";
		toOut += node.id + "\n";
		toOut += node.x + "\n";
		toOut += node.y + "\n";
		toOut += node.uuid + "\n";
		if (node.inputs.length > 0) toOut += "input:\n";
		for (int i = 0; i < node.inputs.length; i++) {
			String toAdd = node.inputs[i].id + " " + node.inputs[i].uuid;
			if (i == node.inputs.length - 1) toAdd += " done";
			toAdd += "\n";
			toOut += toAdd;
		}
		if (node.outputs.length > 0) {
			toOut += "output:\n";
			String toAdd = node.outputs[0].id + " " + node.outputs[0].uuid + "\n";
			toOut += toAdd;
		}
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

	
}
