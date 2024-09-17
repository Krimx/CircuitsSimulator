import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.UUID;

//TODO: Figure out setting width to id string size

public class Node {
	public int x,y,w,h;
	public String id, uuid;
	public Input[] inputs;
	public Output[] outputs;
	public Color color;
	public int yOffset = 10, yGap = 20;
	public int grabX, grabY;
	public boolean pointHovering;
	public int pointRad;
	public boolean selected;
	public int groupXOffset;
	public int groupYOffset;
	
	public String[] ids = {
			"and",
			"or",
			"not",
			"xor",
			"nand",
			"nor",
			"xnor",
			"switch",
			"light",
			"custom"};
	public Color[] cols = {
			new Color(53,205,159),
			new Color(53,124,205),
			new Color(220,129,196),
			new Color(188,91,168),
			new Color(161,232,218),
			new Color(255,193,100),
			new Color(205,249,130),
			null,
			null,
			new Color(205,249,130)};
	
	public Node(int x, int y, String id, String[] inputs, String[] outputs, Font nodeFont) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.uuid = UUID.randomUUID().toString();
		this.pointHovering = false;
		this.pointRad = 4;
		this.selected = false;
		this.groupXOffset = 0;
		this.groupYOffset = 0;

		if (this.id.equals("and")) {
			this.inputs = new Input[2];
			this.outputs = new Output[1];

			this.inputs[0] = new Input("input", this.uuid);
			this.inputs[1] = new Input("input", this.uuid);

			this.outputs[0] = new Output("output", this.uuid);
		}
		else if (this.id.equals("or")) {
			this.inputs = new Input[2];
			this.outputs = new Output[1];

			this.inputs[0] = new Input("input", this.uuid);
			this.inputs[1] = new Input("input", this.uuid);

			this.outputs[0] = new Output("output", this.uuid);
		}
		else if (this.id.equals("not")) {
			this.inputs = new Input[1];
			this.outputs = new Output[1];

			this.inputs[0] = new Input("input", this.uuid);

			this.outputs[0] = new Output("output", this.uuid);
		}
		else if (this.id.equals("switch")) {
			this.inputs = new Input[0];
			this.outputs = new Output[1];

			this.outputs[0] = new Output("output", this.uuid);
		}
		else if (this.id.equals("light")) {
			this.inputs = new Input[1];
			this.outputs = new Output[0];

			this.inputs[0] = new Input("input", this.uuid);
		}
		else if (this.id.equals("xor")) {
			this.inputs = new Input[2];
			this.outputs = new Output[1];

			this.inputs[0] = new Input("input", this.uuid);
			this.inputs[1] = new Input("input", this.uuid);
			
			this.outputs[0] = new Output("output", this.uuid);
		}
		else if (this.id.equals("nand")) {
			this.inputs = new Input[2];
			this.outputs = new Output[1];

			this.inputs[0] = new Input("input", this.uuid);
			this.inputs[1] = new Input("input", this.uuid);
			
			this.outputs[0] = new Output("output", this.uuid);
		}
		else if (this.id.equals("nor")) {
			this.inputs = new Input[2];
			this.outputs = new Output[1];

			this.inputs[0] = new Input("input", this.uuid);
			this.inputs[1] = new Input("input", this.uuid);
			
			this.outputs[0] = new Output("output", this.uuid);
		}
		else if (this.id.equals("xnor")) {
			this.inputs = new Input[2];
			this.outputs = new Output[1];

			this.inputs[0] = new Input("input", this.uuid);
			this.inputs[1] = new Input("input", this.uuid);
			
			this.outputs[0] = new Output("output", this.uuid);
		}
		else if (this.id.equals("custom")) {
			this.inputs = new Input[1];
			this.outputs = new Output[1];

			this.inputs[0] = new Input("input", this.uuid);
			this.outputs[0] = new Output("output", this.uuid);
		}
		else {
			this.inputs = new Input[inputs.length];
			this.outputs = new Output[outputs.length];
			
			for (int i = 0; i < inputs.length; i++) {
				this.inputs[i] = new Input(inputs[i], this.uuid);
			}

			for (int i = 0; i < outputs.length; i++) {
				this.outputs[i] = new Output(outputs[i], this.uuid);
			}
		}
		
		this.h = (Math.max(this.inputs.length, this.outputs.length) * yGap) + yOffset;
		//this.w = Graphics.getFontMetrics().stringWidth(this.id) + 20;
		if (this.id.equals("and")) this.w = 55;
		else if (this.id.equals("or")) this.w = 35;
		else if (this.id.equals("not")) this.w = 45;
		else if (this.id.equals("switch")) this.w = 75;
		else if (this.id.equals("light")) this.w = 55;
		else if (this.id.equals("xor")) this.w = 50;
		else if (this.id.equals("nand")) this.w = 65;
		else if (this.id.equals("nor")) this.w = 50;
		else if (this.id.equals("xnor")) this.w = 55;
		else if (this.id.equals("custom")) this.w = 90;
		else this.w = 100;
		
		this.color = cols[indexOf(this.id, ids)];
	}

	public static class Input {
		public String id, uuid;
		public String connectedUUID, parentUUID;
		public int trueX = 0, trueY = 0;
		public boolean state;
		
		public Input(String id, String parentUUID) {
			this.id = id;
			this.connectedUUID = "";
			this.uuid = UUID.randomUUID().toString();
			this.parentUUID = parentUUID;
			this.state = false;
		}
	}

	public static class Output {
		public String id, uuid;
		public String parentUUID;
		public ArrayList<String> connectedUUIDs = new ArrayList<>();
		public ArrayList<Line> connections = new ArrayList<>();
		public int trueX = 0, trueY = 0;
		public boolean state;
		
		public Output(String id, String parentUUID) {
			this.id = id;
			this.uuid = UUID.randomUUID().toString();
			this.parentUUID = parentUUID;
			this.state = false;
		}
		
		public Node searchNodesByUUID(ArrayList<Node> nodes, String toCheck) {
			Node toOut = null;
			for (int i = 0; i < nodes.size(); i++) {
				if (nodes.get(i).uuid.equals(toCheck)) toOut = nodes.get(i);
			}
			return toOut;
		}
		
		public int searchOutputIndexByUUID(Node node, String toUUID) {
			int index = -1;
			
			for (int i = 0; i < node.outputs.length; i++) {
				if (toUUID.equals(node.outputs[i].uuid)) index = i;
			}
			
			return index;
		}
		
		public int searchInputIndexByUUID(Node node, String toUUID) {
			int index = -1;
			
			for (int i = 0; i < node.inputs.length; i++) {
				if (toUUID.equals(node.inputs[i].uuid)) index = i;
			}
			
			return index;
		}
		
		public void redrawConnectionLines(ArrayList<Node> nodes, Engine engine) {
			Node parent = searchNodesByUUID(nodes, this.parentUUID);
			
			int toY = parent.y + (parent.yOffset + (parent.yOffset / 2)) + (searchOutputIndexByUUID(parent, this.uuid) * parent.yGap) - (parent.h / 2) - engine.camera.getY();
			int toX = parent.x + (parent.w / 2) - engine.camera.getX();
			
			try {
				for (int l = 0; l < this.connectedUUIDs.size(); l++) {
					for (int j = 0; j < nodes.size(); j++) { //Iterate over nodes
						for (int k = 0; k < nodes.get(j).inputs.length; k++) { //Iterate over inputs in nodes
							if (nodes.get(j).inputs[k].uuid.equals(this.connectedUUIDs.get(l))) { //Check for matching uuid and connectedUUID
								
								
								int endX = nodes.get(j).x - (nodes.get(j).w / 2) - engine.camera.getX();
								int endY = nodes.get(j).y + (nodes.get(j).yOffset + (nodes.get(j).yOffset / 2)) + (k * nodes.get(j).yGap) - (nodes.get(j).h / 2) - engine.camera.getY();
								
								this.connections.set(l, new Line(toX, toY, endX, endY));
								
								//Get out of the loops and move on with life
								break;
							}
						}
					}
				}
				
			}
			catch(Exception e) {}
		}
		
		public void createConnection(String toUUID, Engine engine, ArrayList<Node> nodes, ArrayList<String> ranUUIDs) {
			Node parent = searchNodesByUUID(nodes, this.parentUUID);
			
			int toY = parent.y + (parent.yOffset + (parent.yOffset / 2)) + (searchOutputIndexByUUID(parent, this.uuid) * parent.yGap) - (parent.h / 2) - engine.camera.getY();
			int toX = parent.x + (parent.w / 2) - engine.camera.getX();
			
			try {
				for (int j = 0; j < nodes.size(); j++) { //Iterate over nodes
					for (int k = 0; k < nodes.get(j).inputs.length; k++) { //Iterate over inputs in nodes
						if (nodes.get(j).inputs[k].uuid.equals(toUUID)) { //Check for matching uuid and connectedUUID
							Node.Output connectedOutput = parent.searchOutputsByUUID(nodes, nodes.get(j).inputs[k].connectedUUID);
							
							if (connectedOutput != null) {
								connectedOutput.severConnection(nodes, nodes.get(j).inputs[k].uuid);
							}
							
							
							int endX = nodes.get(j).x - (nodes.get(j).w / 2) - engine.camera.getX();
							int endY = nodes.get(j).y + (nodes.get(j).yOffset + (nodes.get(j).yOffset / 2)) + (k * nodes.get(j).yGap) - (nodes.get(j).h / 2) - engine.camera.getY();
							
							this.connections.add(new Line(toX, toY, endX, endY));
							this.connectedUUIDs.add(toUUID);
							nodes.get(j).inputs[k].connectedUUID = this.uuid;
							
							//Get out of the loops and move on with life
							break;
						}
					}
				}
			}
			catch(Exception e) {}
			
			if (!ranUUIDs.contains(parent.uuid)) {
				ranUUIDs.add(parent.uuid);
				parent.logic(nodes, ranUUIDs);
			}
		}
		
		public void severConnection(ArrayList<Node> nodes, String uuid) {
			int connectionIndex = this.connectedUUIDs.indexOf(uuid);
			nodes.get(0).searchInputsByUUID(nodes, connectedUUIDs.get(connectionIndex)).connectedUUID = "";
			this.connectedUUIDs.remove(connectionIndex);
			this.connections.remove(connectionIndex);
			
		}
		
		public void severConnection(ArrayList<Node> nodes, int connectionIndex) {
			nodes.get(0).searchInputsByUUID(nodes, connectedUUIDs.get(connectionIndex)).connectedUUID = "";
			this.connectedUUIDs.remove(connectionIndex);
			this.connections.remove(connectionIndex);
		}
		
		public void severAllConnections(ArrayList<Node> nodes) {
			for (int i = 0; i < this.connectedUUIDs.size(); i++) {
				nodes.get(0).searchInputsByUUID(nodes, this.connectedUUIDs.get(i)).connectedUUID = "";
			}
			this.connectedUUIDs.clear();
			this.connections.clear();
		}
	}
	
	public String[] getInputUUIDs() {
		String[] toOut = new String[this.inputs.length];
		
		for (int i = 0; i < this.inputs.length; i++) {
			toOut[i] = this.inputs[i].uuid;
		}
		
		return toOut;
	}
	
	public Output searchOutputsByUUID(ArrayList<Node> nodes, String toCheck) {
		Output toOut = null;
		
		for (int i = 0; i < nodes.size(); i++) {
			for (int j = 0; j < nodes.get(i).outputs.length; j++) {
				if (nodes.get(i).outputs[j].uuid.equals(toCheck)) {
					toOut = nodes.get(i).outputs[j];
				}
			}
		}
		
		return toOut;
	}
	
	public Input searchInputsByUUID(ArrayList<Node> nodes, String toCheck) {
		Input toOut = null;
		
		for (int i = 0; i < nodes.size(); i++) {
			for (int j = 0; j < nodes.get(i).inputs.length; j++) {
				if (nodes.get(i).inputs[j].uuid.equals(toCheck)) {
					toOut = nodes.get(i).inputs[j];
				}
			}
		}
		
		return toOut;
	}
	
	public Node searchNodesByUUID(ArrayList<Node> nodes, String toCheck) {
		Node toOut = null;
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).uuid.equals(toCheck)) toOut = nodes.get(i);
		}
		return toOut;
	}
	
	public int indexOf(String val, String[] array) {
		int index = -1;
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(val)) {
				index = i;
				break;
			}
		}
		return index;
	}
	
	public void recreateConnections(ArrayList<Node> nodes, Engine engine, ArrayList<String> ranUUIDs) {
		for (int i = 0; i < nodes.size(); i++) {
			for (int j = 0; j < nodes.get(i).outputs.length; j++) {
				if (nodes.get(i).outputs[j].connectedUUIDs.size() > 0) {
					ArrayList<String> uuids = new ArrayList<>(nodes.get(i).outputs[j].connectedUUIDs);
					nodes.get(i).outputs[j].connectedUUIDs.clear();
					nodes.get(i).outputs[j].connections.clear();
					for (int k = 0; k < uuids.size(); k++) {
						nodes.get(i).outputs[j].createConnection(uuids.get(k), engine, nodes, ranUUIDs);
					}
					
				}
			}
		}
	}
	
	public void redrawConnections(ArrayList<Node> nodes, Engine engine) {
		for (int i = 0; i < nodes.size(); i++) {
			for (int j = 0; j < nodes.get(i).outputs.length; j++) {
				nodes.get(i).outputs[j].redrawConnectionLines(nodes, engine);
			}
		}
	}
	
	public void render(Graphics g, Font pointFont, Font nodeFont) {
		g.setColor(this.color);
		if (this.id.equals("switch")) {
			if (this.outputs[0].state) g.setColor(new Color(49,250,52));
			else g.setColor(new Color(6,63,16));
		}
		if (this.id.equals("light")) {
			if (this.inputs[0].state) g.setColor(new Color(45,225,245));
			else g.setColor(new Color(16,34,68));
		}
		g.fillRect(x - (this.w / 2), y - (this.h / 2), w, h);
		g.setColor(Color.black);
		if (this.selected) g.setColor(Color.yellow);
		g.drawRect(x - (this.w / 2), y - (this.h / 2), w, h);
		
		g.setColor(Color.black);
		g.setFont(nodeFont);
		g.drawString(this.id, this.x - (this.w / 2) + 10, this.y + 8);
		
		for (int i = 0; i < this.inputs.length; i++) {
			//Gathering render positions
			int rad = this.pointRad;
			int toY = this.y + (this.yOffset + (this.yOffset / 2)) + (i * this.yGap) - (this.h / 2);
			int toX = this.x - (this.w / 2);
			this.inputs[i].trueX = toX;
			this.inputs[i].trueY = toY;
			
			g.setColor(new Color(243, 240, 226));
			
			g.fillOval(toX - rad, toY - rad, rad * 2, rad * 2);
			
			g.setColor(Color.black);
			g.drawOval(toX - rad, toY - rad, rad * 2, rad * 2);
		}
		
		for (int i = 0; i < this.outputs.length; i++) {
			int rad = this.pointRad;
			int toY = this.y + (this.yOffset + (this.yOffset / 2)) + (i * this.yGap) - (this.h / 2);
			int toX = this.x + (this.w / 2);
			this.outputs[i].trueX = toX;
			this.outputs[i].trueY = toY;
			
			g.setColor(new Color(243, 240, 226));
			
			g.fillOval(toX - rad, toY - rad, rad * 2, rad * 2);
			
			g.setColor(Color.black);
			g.drawOval(toX - rad, toY - rad, rad * 2, rad * 2);
		}
	}
	
	public void render(Graphics g, Engine engine, Font pointFont, Font nodeFont, Engine.Camera camera, ArrayList<Node> nodes, String grabbedUUID) {
		
		g.setColor(this.color);
		if (this.id.equals("switch")) {
			if (this.outputs[0].state) g.setColor(new Color(49,250,52));
			else g.setColor(new Color(6,63,16));
		}
		if (this.id.equals("light")) {
			if (this.inputs[0].state) g.setColor(new Color(45,225,245));
			else g.setColor(new Color(16,34,68));
		}
		g.fillRect(x - (this.w / 2) - camera.getX(), y - (this.h / 2) - camera.getY(), w, h);
		g.setColor(Color.black);
		if (this.selected) g.setColor(Color.yellow);
		g.drawRect(x - (this.w / 2) - camera.getX(), y - (this.h / 2) - camera.getY(), w, h);
		
		g.setColor(Color.black);
		g.setFont(nodeFont);
		g.drawString(this.id, this.x - (this.w / 2) + 10 - camera.getX(), this.y + 8 - camera.getY());
		
		this.pointHovering = false;

		for (int i = 0; i < this.inputs.length; i++) {
			//Gathering render positions
			int rad = this.pointRad;
			int toY = this.y + (this.yOffset + (this.yOffset / 2)) + (i * this.yGap) - (this.h / 2) - camera.getY();
			int toX = this.x - (this.w / 2) - camera.getX();
			this.inputs[i].trueX = toX;
			this.inputs[i].trueY = toY;
			
			boolean mouseHover = inRad(toX, toY, rad, engine.mouse.getX(), engine.mouse.getY());
			
			if (mouseHover) {
				g.setColor(new Color(203, 200, 183));
				this.pointHovering = true;
			}
			else {
				g.setColor(new Color(243, 240, 226));
			}
			
			g.fillOval(toX - rad, toY - rad, rad * 2, rad * 2);
			
			g.setColor(Color.black);
			g.drawOval(toX - rad, toY - rad, rad * 2, rad * 2);
			
			//Rendering the id if the mouse is hovering
			if (mouseHover) {
				g.setFont(pointFont);
				g.setColor(Color.white);
				int strWidth = g.getFontMetrics().stringWidth(this.inputs[i].id);
				g.drawString(this.inputs[i].id, toX - strWidth - 5, toY - 8);
			}
		}
		
		for (int i = 0; i < this.outputs.length; i++) {
			int rad = this.pointRad;
			int toY = this.y + (this.yOffset + (this.yOffset / 2)) + (i * this.yGap) - (this.h / 2) - camera.getY();
			int toX = this.x + (this.w / 2) - camera.getX();
			this.outputs[i].trueX = toX;
			this.outputs[i].trueY = toY;
			
			//Drawing lines between connected inputs and outputs
			for (int j = 0; j < this.outputs[i].connections.size(); j++) {
				Line con = this.outputs[i].connections.get(j);
				if (con != null) {
					g.setColor(Color.red);
					g.drawLine(con.x1, con.y1, con.x2, con.y2);
				}
			}
			
			boolean mouseHover = inRad(toX, toY, rad, engine.mouse.getX(), engine.mouse.getY());
			
			if (mouseHover) {
				g.setColor(new Color(203, 200, 183));
				this.pointHovering = true;
			}
			else {
				g.setColor(new Color(243, 240, 226));
			}
			
			g.fillOval(toX - rad, toY - rad, rad * 2, rad * 2);
			
			g.setColor(Color.black);
			g.drawOval(toX - rad, toY - rad, rad * 2, rad * 2);
			
			if (mouseHover) {
				g.setFont(pointFont);
				g.setColor(Color.white);
				g.drawString(this.outputs[i].id, toX + 5, toY - 8);
			}
		}
	}
	
	public void update(Engine engine, ArrayList<Node> nodes, ArrayList<String> ranUUIDs) {
		if (this.id.equals("switch")) {
			if (mouseIsHovering(engine) && engine.keys.SPACETYPED()) {
				this.outputs[0].state = !this.outputs[0].state;
				logic(nodes, ranUUIDs);
			}
		}
		
	}
	
	public boolean mouseIsHovering(Engine engine) {
		if (engine.mouse.getX() >= this.x - (this.w / 2) - engine.camera.getX() &&
			engine.mouse.getX() <= this.x + (this.w / 2) - engine.camera.getX()  &&
			engine.mouse.getY() >= this.y - (this.h / 2) - engine.camera.getY()  &&
			engine.mouse.getY() <= this.y + (this.h / 2) - engine.camera.getY() )
		{
			return true;
		}
		return false;
	}
	
	public boolean inRad(int x, int y, int rad, int checkX, int checkY) {
		boolean in = false;
		
		if (checkX >= x - rad && checkX <= x + rad && checkY >= y - rad && checkY <= y + rad) in = true;
		
		return in;
	}
	
	public Node searchByUUID(ArrayList<Node> nodes, String toCheck) {
		Node toOut = null;
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).uuid.equals(toCheck)) toOut = nodes.get(i);
		}
		return toOut;
	}
	
	public void prepareForRemoval(ArrayList<Node> nodes) {
		for (int i = 0; i < this.inputs.length; i++) {
			Output toCut = searchOutputsByUUID(nodes, this.inputs[i].connectedUUID);
			if (toCut != null) toCut.severConnection(nodes, inputs[i].uuid);
		}
		
		for (int i = 0; i < this.outputs.length; i++) {
			this.outputs[i].severAllConnections(nodes);
		}
		
		int toPop = -1;
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).uuid.equals(this.uuid)) {
				toPop = i;
				break;
			}
		}
		
		nodes.remove(toPop);
	}

	public void logic(ArrayList<Node> nodes, ArrayList<String> ranUUIDs) {
		if (!ranUUIDs.contains(this.uuid)) {
			if (this.id.equals("and")) {
				this.outputs[0].state = (this.inputs[0].state && this.inputs[1].state);
			}
			if (this.id.equals("or")) {
				this.outputs[0].state = (this.inputs[0].state || this.inputs[1].state);
			}
			if (this.id.equals("not")) {
				this.outputs[0].state = !this.inputs[0].state;
			}
			if (this.id.equals("xor")) {
				this.outputs[0].state = (this.inputs[0].state || this.inputs[1].state) && !(this.inputs[0].state && this.inputs[1].state);
			}
			if (this.id.equals("nand")) {
				this.outputs[0].state = !(this.inputs[0].state && this.inputs[1].state);
			}
			if (this.id.equals("nor")) {
				this.outputs[0].state = !(this.inputs[0].state || this.inputs[1].state);
			}
			if (this.id.equals("xnor")) {
				this.outputs[0].state = !((this.inputs[0].state || this.inputs[1].state) && !(this.inputs[0].state && this.inputs[1].state));
			}
			
			sendLogic(nodes, ranUUIDs);
		}
	}
	
	public void sendLogic(ArrayList<Node> nodes, ArrayList<String> ranUUIDs) {
		if (this.outputs.length > 0 && !ranUUIDs.contains(this.uuid)) {
			for (int i = 0; i < this.outputs[0].connectedUUIDs.size(); i++) {
				if (this.outputs.length > 0) searchInputsByUUID(nodes, this.outputs[0].connectedUUIDs.get(i)).state = this.outputs[0].state;
				ranUUIDs.add(this.uuid);
				searchNodesByUUID(nodes, searchInputsByUUID(nodes, this.outputs[0].connectedUUIDs.get(i)).parentUUID).logic(nodes, ranUUIDs);
			}
		}
		
	}
}
