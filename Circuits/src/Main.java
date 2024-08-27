import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Scanner;

//TODO: Add more gates (xor, nand, nor, etc)
//TODO: Saving projects and whatnot (inside of the .jar file so do that stuff i think the engine already has the stuff for it and the lwjgl tutorial has a bit on it too)
//TODO: Printwriter

public class Main {
	public static Engine engine = new Engine(false);
	public static ArrayList<Node> nodes = new ArrayList<>();
	public static Font pointFont = new Font("Helvetica", Font.PLAIN, 16);
	public static Font nodeFont = new Font("Helvetica", Font.PLAIN, 20);
	
	public static boolean startedRightClick = false;
	public static String grabbedUUID = "", grabbedNode = "";
	
	public static Line severLine = new Line(-1,-1,-1,-1);

	public static void main(String[] args) {
		
		loadSaveFile("save1.circ");

		/*
		nodes.add(new Node(100, 100, "switch", null, null, nodeFont));
		nodes.add(new Node(500, 150, "light", null, null, nodeFont));
		nodes.get(0).outputs[0].createConnection(nodes.get(1).inputs[0].uuid, engine, nodes);
		*/
		
		
		
		engine.initializeJFrame(800, 800, false, false, 60);
		engine.mouse.setXOffset(-7);
		engine.mouse.setYOffset(-30);
		
		try {
			engine.run();
		}
		catch(Exception e) {}
	}

	public static void paintToFrame(Graphics g) {
		engine.setBackground(g, new Color(65, 65, 75));
		
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).render(g, engine, pointFont, nodeFont, engine.camera, nodes, grabbedUUID);
		}
		
		if (engine.mouse.MIDDLE()) {
			int[] delta = engine.mouse.getDelta();
			engine.camera.addX(-delta[0]);
			engine.camera.addY(-delta[1]);
			
			for (int i = 0; i < nodes.size(); i++) {
				nodes.get(i).redrawConnections(nodes, engine);
			}
		}
		
		if (engine.mouse.RIGHT()) {
			g.setColor(new Color(255,255,255,100));
			g.drawLine(severLine.x1, severLine.y1, severLine.x2, severLine.y2);
		}
		
		
		if (!grabbedUUID.equals("")) {
			g.setColor(new Color(100,100,100,100));
			Node.Output output = searchByUUID(grabbedUUID);
			g.drawLine(output.trueX, output.trueY, engine.mouse.getX(), engine.mouse.getY());
		}
	}

	public static void mainLoop() {
		if (engine.keys.K1TYPED()) nodes.add(new Node(engine.mouse.getX(), engine.mouse.getY(), "switch", null, null, nodeFont));
		if (engine.keys.K2TYPED()) nodes.add(new Node(engine.mouse.getX(), engine.mouse.getY(), "and", null, null, nodeFont));
		if (engine.keys.K3TYPED()) nodes.add(new Node(engine.mouse.getX(), engine.mouse.getY(), "or", null, null, nodeFont));
		if (engine.keys.K4TYPED()) nodes.add(new Node(engine.mouse.getX(), engine.mouse.getY(), "not", null, null, nodeFont));
		if (engine.keys.K5TYPED()) nodes.add(new Node(engine.mouse.getX(), engine.mouse.getY(), "light", null, null, nodeFont));
		
		for (Node node : nodes) {
			node.update(engine, nodes);
		}
		
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
		
		if (engine.keys.LSHIFT()) {
			for (int i = 0; i < nodes.size(); i++) {
				if (nodes.get(i).mouseIsHovering(engine) && engine.mouse.LEFTCLICKED()) {
					nodes.get(i).prepareForRemoval(nodes);
				}
			}
		}
		
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
			if (!grabbedNode.equals("")) {
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
								searchByUUID(grabbedUUID).createConnection(input.uuid, engine, nodes);
							}
						}
					}
				}
			}
			grabbedUUID = "";
			grabbedNode = "";
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
	
	public static void loadSaveFile(String saveFile) {
		Scanner reader = engine.loadScannerFromSourceFolder("save1.circ");
		boolean readingNode = false;
		
		String id = "", uuid = "";
		int x = 0, y = 0;
		ArrayList<Node.Input> inputs = new ArrayList<>();
		ArrayList<Node.Output> outputs = new ArrayList<>();
		
		while (reader.hasNextLine()) {
			String readLine = reader.nextLine();
			if (readLine.equals("{")) {
				id = reader.nextLine();
				x = Integer.valueOf(reader.nextLine());
				y = Integer.valueOf(reader.nextLine());
				uuid = reader.nextLine();
				
				String inputOutputCheck = reader.nextLine();
				
				if (inputOutputCheck.equals("input:")) {
					Scanner line = new Scanner(reader.nextLine());
					String inID = line.next();
					String inUUID = line.next();
					
					Node.Input toInput = new Node.Input(inID, uuid);
					toInput.uuid = inUUID;
					
					inputs.add(toInput);
				}
				
				if (inputOutputCheck.equals("output:")) {
					Scanner line = new Scanner(reader.nextLine());
					String inID = line.next();
					String inUUID = line.next();
					
					Node.Output toOutput = new Node.Output(inID, uuid);
					toOutput.uuid = inUUID;
					
					outputs.add(toOutput);
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
				
				reader.nextLine();
			}
			Scanner line = new Scanner(readLine);
			if (line.next().equals("con:")) {
				String outputUUID = line.next();
				String inputUUID = line.next();
				Node.Output find = searchByUUID(outputUUID);
				find.createConnection(inputUUID, engine, nodes);
			}
		}
	}
	
	public static void saveToFile(String filename) {
		
	}
}
