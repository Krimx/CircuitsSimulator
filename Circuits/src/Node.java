import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Scanner;
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
	public int transistors;
	public int pointProx;
	public String binary4BitValue;
	public int value;
	public boolean pausePanning;
	public int decoderAmount;
	public boolean encoderHigh;
	public String customBehavior;
	public boolean custom;
	public int multipleSelectMouseOffsetX, multipleSelectMouseOffsetY;
	public ArrayList<String> customActions = new ArrayList<>();
	
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
			"4BitNumber",
			"4BitAdder",
			"4BitDisplay",
			"decoder",
			"encoder",
			"mux",
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
			new Color(235,235,235),
			new Color(251,255,202),
			new Color(200,200,200),
			new Color(251,255,104),
			new Color(195,249,204),
			new Color(230,255,0),
			new Color(205,249,130)};
	
	public Node() {
		this(0,0,"no_name",null,null,null, true);
	}
	
	public Node(int x, int y, String id, String[] inputs, String[] outputs, Font nodeFont, boolean custom) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.uuid = UUID.randomUUID().toString();
		this.pointHovering = false;
		this.pointRad = 5;
		this.selected = false;
		this.groupXOffset = 0;
		this.groupYOffset = 0;
		this.transistors = 0;
		this.pointProx = 6;
		this.decoderAmount = 2;
		this.encoderHigh = false;
		
		this.binary4BitValue = "0000";
		this.value = 0;
		this.pausePanning = false;
		
		this.custom = custom;
		this.customBehavior = "";

		multipleSelectMouseOffsetX = 0;
		multipleSelectMouseOffsetY = 0;

		if (this.id.equals("and")) addInputsAndOutputs(2,1);
		else if (this.id.equals("or")) addInputsAndOutputs(2,1);
		else if (this.id.equals("not")) addInputsAndOutputs(1,1);
		else if (this.id.equals("switch")) addInputsAndOutputs(0,1);
		else if (this.id.equals("light")) addInputsAndOutputs(1,0);
		else if (this.id.equals("xor")) addInputsAndOutputs(2,1);
		else if (this.id.equals("nand")) addInputsAndOutputs(2,1);
		else if (this.id.equals("nor")) addInputsAndOutputs(2,1);
		else if (this.id.equals("xnor")) addInputsAndOutputs(2,1);
		else if (this.id.equals("4BitNumber")) {
			this.inputs = new Input[0];
			this.outputs = new Output[4];
			this.outputs[0] = new Output("A1", this.uuid);
			this.outputs[1] = new Output("A2", this.uuid);
			this.outputs[2] = new Output("A3", this.uuid);
			this.outputs[3] = new Output("A4", this.uuid);
			
			this.pausePanning = true;
		}
		else if (this.id.equals("4BitAdder")) {
			this.inputs = new Input[9];
			this.outputs = new Output[5];

			this.inputs[0] = new Input("A1", this.uuid);
			this.inputs[1] = new Input("A2", this.uuid);
			this.inputs[2] = new Input("A3", this.uuid);
			this.inputs[3] = new Input("A4", this.uuid);
			this.inputs[4] = new Input("B1", this.uuid);
			this.inputs[5] = new Input("B2", this.uuid);
			this.inputs[6] = new Input("B3", this.uuid);
			this.inputs[7] = new Input("B4", this.uuid);
			this.inputs[8] = new Input("subtract", this.uuid);

			this.outputs[0] = new Output("sum1", this.uuid);
			this.outputs[1] = new Output("sum2", this.uuid);
			this.outputs[2] = new Output("sum3", this.uuid);
			this.outputs[3] = new Output("sum4", this.uuid);
			this.outputs[4] = new Output("carry_out", this.uuid);
		}
		else if (this.id.equals("4BitDisplay")) {
			this.inputs = new Input[4];
			this.outputs = new Output[0];

			this.inputs[0] = new Input("A1", this.uuid);
			this.inputs[1] = new Input("A2", this.uuid);
			this.inputs[2] = new Input("A3", this.uuid);
			this.inputs[3] = new Input("A4", this.uuid);
		}
		else if (this.id.equals("decoder")) {
			this.inputs = new Input[2];
			this.outputs = new Output[4];

			this.inputs[0] = new Input("A0", this.uuid);
			this.inputs[1] = new Input("A1", this.uuid);

			this.outputs[0] = new Output("B0", this.uuid);
			this.outputs[1] = new Output("B1", this.uuid);
			this.outputs[2] = new Output("B2", this.uuid);
			this.outputs[3] = new Output("B3", this.uuid);
		}
		else if (this.id.equals("encoder")) {
			this.inputs = new Input[4];
			this.outputs = new Output[3];

			this.inputs[0] = new Input("A0", this.uuid);
			this.inputs[1] = new Input("A1", this.uuid);
			this.inputs[2] = new Input("A2", this.uuid);
			this.inputs[3] = new Input("A3", this.uuid);

			this.outputs[0] = new Output("B0", this.uuid);
			this.outputs[1] = new Output("B1", this.uuid);
			this.outputs[2] = new Output("B2", this.uuid);
		}
		else if (this.id.equals("mux")) {
			this.inputs = new Input[3];
			this.outputs = new Output[1];

			this.inputs[0] = new Input("A0", this.uuid);
			this.inputs[1] = new Input("A1", this.uuid);
			this.inputs[2] = new Input("Control_0", this.uuid);

			this.outputs[0] = new Output("B0", this.uuid);
		}
		else if (this.id.equals("custom")) {
			addInputsAndOutputs(inputs.length, outputs.length);
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
		else if (this.id.equals("4BitNumber")) this.w = 100;
		else if (this.id.equals("4BitAdder")) this.w = 115;
		else if (this.id.equals("4BitDisplay")) this.w = 115;
		else if (this.id.equals("decoder")) this.w = 135;
		else if (this.id.equals("encoder")) this.w = 135;
		else if (this.id.equals("mux")) this.w = 60;
		else if (this.id.equals("custom")) this.w = 90;
		else this.w = 100;
		
		if (!this.custom) {
			this.color = cols[Methods.indexOf(this.id, ids)];
		}
		else {
			this.color = cols[cols.length - 1];
		}
	}
	
	public void addInputsAndOutputs(int ins, int outs) {
		this.inputs = new Input[ins];
		this.outputs = new Output[outs];
		if (this.inputs.length > 0) {
			for (int i = 0; i < this.inputs.length; i++) {
				this.inputs[i] = new Input("input", this.uuid);
			}
		}
		if (this.outputs.length > 0) {
			for (int i = 0; i < this.outputs.length; i++) {
				this.outputs[i] = new Output("output", this.uuid);
			}
		}
		
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
		
		
		
		
		
		public void redrawConnectionLines(ArrayList<Node> nodes, Engine engine) {
			Node parent = Methods.searchNodesByUUID(nodes, this.parentUUID);
			
			int toY = parent.y + (parent.yOffset + (parent.yOffset / 2)) + (Methods.searchOutputIndexByUUID(parent, this.uuid) * parent.yGap) - (parent.h / 2) - engine.camera.getY();
			int toX = parent.x + (parent.w / 2) - engine.camera.getX();
			
			try {
				for (int l = 0; l < this.connectedUUIDs.size(); l++) {
					for (int j = 0; j < nodes.size(); j++) { //Iterate over nodes
						for (int k = 0; k < nodes.get(j).inputs.length; k++) { //Iterate over inputs in nodes
							if (nodes.get(j).inputs[k].uuid.equals(this.connectedUUIDs.get(l))) { //Check for matching uuid and connectedUUID
								
								
								int endX = nodes.get(j).x - (nodes.get(j).w / 2) - engine.camera.getX();
								int endY = nodes.get(j).y + (nodes.get(j).yOffset + (nodes.get(j).yOffset / 2)) + (k * nodes.get(j).yGap) - (nodes.get(j).h / 2) - engine.camera.getY();
								
								this.connections.set(l, new Line(toX + engine.camera.getX(), toY + engine.camera.getY(), endX + engine.camera.getX(), endY + engine.camera.getY()));
								
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
			Node parent = Methods.searchNodesByUUID(nodes, this.parentUUID);
			
			int toY = parent.y + (parent.yOffset + (parent.yOffset / 2)) + (Methods.searchOutputIndexByUUID(parent, this.uuid) * parent.yGap) - (parent.h / 2) - engine.camera.getY();
			int toX = parent.x + (parent.w / 2) - engine.camera.getX();
			
			try {
				for (int j = 0; j < nodes.size(); j++) { //Iterate over nodes
					for (int k = 0; k < nodes.get(j).inputs.length; k++) { //Iterate over inputs in nodes
						if (nodes.get(j).inputs[k].uuid.equals(toUUID)) { //Check for matching uuid and connectedUUID
							Node.Output connectedOutput = Methods.searchOutputsByUUID(nodes, nodes.get(j).inputs[k].connectedUUID);
							
							if (connectedOutput != null) {
								connectedOutput.severConnection(nodes, nodes.get(j).inputs[k].uuid);
							}
							
							
							int endX = nodes.get(j).x - (nodes.get(j).w / 2) - engine.camera.getX();
							int endY = nodes.get(j).y + (nodes.get(j).yOffset + (nodes.get(j).yOffset / 2)) + (k * nodes.get(j).yGap) - (nodes.get(j).h / 2) - engine.camera.getY();
							
							this.connections.add(new Line(toX + engine.camera.getX(), toY + engine.camera.getY(), endX + engine.camera.getX(), endY + engine.camera.getY()));
							this.connectedUUIDs.add(toUUID);
							nodes.get(j).inputs[k].connectedUUID = this.uuid;
							
							Methods.searchNodesByUUID(nodes, this.parentUUID).sendAllOutputs(nodes, ranUUIDs);
							
							//Get out of the loops and move on with life
							break;
						}
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
			if (!ranUUIDs.contains(parent.uuid)) {
				ranUUIDs.add(parent.uuid);
				parent.logic(nodes, ranUUIDs);
			}
		}
		
		public void severConnection(ArrayList<Node> nodes, String uuid) {
			int connectionIndex = this.connectedUUIDs.indexOf(uuid);
			Methods.searchInputsByUUID(nodes, connectedUUIDs.get(connectionIndex)).connectedUUID = ""; //Add a method to search within a single node instead of looking up every single node
			this.connectedUUIDs.remove(connectionIndex);
			this.connections.remove(connectionIndex);
			
		}
		
		public void severConnection(ArrayList<Node> nodes, int connectionIndex) {
			Methods.searchInputsByUUID(nodes, connectedUUIDs.get(connectionIndex)).connectedUUID = "";
			this.connectedUUIDs.remove(connectionIndex);
			this.connections.remove(connectionIndex);
		}
		
		public void severAllConnections(ArrayList<Node> nodes) {
			for (int i = 0; i < this.connectedUUIDs.size(); i++) {
				Methods.searchInputsByUUID(nodes, this.connectedUUIDs.get(i)).connectedUUID = "";
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
	
	public boolean render(Graphics g, Engine engine, Font pointFont, Font nodeFont, Engine.Camera camera, ArrayList<Node> nodes, String grabbedUUID, ArrayList<String> selectedNodes) {
		boolean inScreen = false;
		Graphics2D g2d = (Graphics2D) g;
		Stroke origStroke = g2d.getStroke();
		
		g2d.setColor(this.color);
		if (this.id.equals("switch")) {
			if (this.outputs[0].state) g2d.setColor(new Color(49,250,52));
			else g2d.setColor(new Color(6,63,16));
		}
		if (this.id.equals("light")) {
			if (this.inputs[0].state) g2d.setColor(new Color(45,225,245));
			else g2d.setColor(new Color(16,34,68));
		}
		
		//Decoders have special case where height needs to be updated in real time
		if (this.id.equals("decoder")) {
			this.h = (Math.max(this.inputs.length, this.outputs.length) * yGap) + yOffset;
		}
		
		int centerX = x - camera.getX();
		int centerY = y - camera.getY();
		
		//g2d.fillRect(x - (this.w / 2) - camera.getX(), y - (this.h / 2) - camera.getY(), w, h);
		g2d.fillRoundRect(centerX - (this.w / 2), centerY - (this.h / 2), w, h, Math.min(w, h) / Main.nodeCornerArc, Math.min(w, h) / Main.nodeCornerArc);
		g2d.setColor(Color.black);
		if (this.selected) g2d.setColor(Color.yellow);
		//g2d.drawRect(x - (this.w / 2) - camera.getX(), y - (this.h / 2) - camera.getY(), w, h);
		g2d.setStroke(new BasicStroke(Main.nodeOutlineWidth));
		if (selectedNodes.contains(this.uuid)) g2d.setColor(Color.yellow);
		g2d.drawRoundRect(centerX - (this.w / 2), centerY - (this.h / 2), w, h, Math.min(w, h) / Main.nodeCornerArc, Math.min(w, h) / Main.nodeCornerArc);
		g2d.setStroke(origStroke);
		
		
		
		if (this.id.equals("4BitNumber")) {
			g.setFont(nodeFont);
			g.setColor(Color.black);
			g.drawString(String.valueOf(this.value), this.x - (this.w / 2) + 10 - camera.getX(), this.y + 8 - camera.getY());
		}
		else if (this.id.equals("4BitDisplay")) {
			g.setFont(nodeFont);
			g.setColor(Color.black);
			g.drawString(String.valueOf(this.value), this.x - (this.w / 2) + 10 - camera.getX(), this.y + 8 - camera.getY());
		}
		else if (this.id.equals("decoder")) {
			g.setFont(nodeFont);
			g.setColor(Color.black);
			g.drawString(String.valueOf(this.inputs.length) + "-" + String.valueOf(this.outputs.length) + " decoder", this.x - (this.w / 2) + 10 - camera.getX(), this.y + 8 - camera.getY());
		}
		else {
			g2d.setColor(Color.black);
			g2d.setFont(nodeFont);
			g2d.drawString(this.id, this.x - (this.w / 2) + 10 - camera.getX(), this.y + 8 - camera.getY());
			if (this.id.equals("encoder")) {
				String highOrLow = (this.encoderHigh) ? "(High)" : "(Low)";
				g2d.drawString(highOrLow, this.x - (this.w / 2) + 10 - camera.getX(), this.y + 8 - camera.getY() + 20);
			}
		}
		
		this.pointHovering = false;

		for (int i = 0; i < this.inputs.length; i++) {
			//Gathering render positions
			int rad = this.pointRad;
			int toY = this.y + (this.yOffset + (this.yOffset / 2)) + (i * this.yGap) - (this.h / 2) - camera.getY();
			int toX = this.x - (this.w / 2) - camera.getX();
			this.inputs[i].trueX = toX;
			this.inputs[i].trueY = toY;
			
			boolean mouseHover = Methods.inProx(toX, toY, rad, engine.mouse.getX(), engine.mouse.getY(), this.pointProx);
			
			if (mouseHover) {
				g2d.setColor(new Color(203, 200, 183));
				this.pointHovering = true;
			}
			else {
				g2d.setColor(new Color(243, 240, 226));
			}
			
			g2d.fillOval(toX - rad, toY - rad, rad * 2, rad * 2);
			
			g2d.setColor(Color.black);
			g2d.drawOval(toX - rad, toY - rad, rad * 2, rad * 2);
			
			//Rendering the id if the mouse is hovering
			if (mouseHover) {
				g2d.setFont(pointFont);
				g2d.setColor(Color.white);
				int strWidth = g2d.getFontMetrics().stringWidth(this.inputs[i].id);
				g2d.drawString(this.inputs[i].id, toX - strWidth - 5, toY - 8);
			}
		}
		
		for (int i = 0; i < this.outputs.length; i++) {
			int rad = this.pointRad;
			int toY = this.y + (this.yOffset + (this.yOffset / 2)) + (i * this.yGap) - (this.h / 2) - camera.getY();
			int toX = this.x + (this.w / 2) - camera.getX();
			this.outputs[i].trueX = toX;
			this.outputs[i].trueY = toY;
			
			boolean mouseHover = Methods.inProx(toX, toY, rad, engine.mouse.getX(), engine.mouse.getY(), this.pointProx);
			
			if (mouseHover) {
				g2d.setColor(new Color(203, 200, 183));
				this.pointHovering = true;
			}
			else {
				g2d.setColor(new Color(243, 240, 226));
			}
			
			g2d.fillOval(toX - rad, toY - rad, rad * 2, rad * 2);
			
			g2d.setColor(Color.black);
			g2d.drawOval(toX - rad, toY - rad, rad * 2, rad * 2);
			
			if (mouseHover) {
				g2d.setFont(pointFont);
				g2d.setColor(Color.white);
				g2d.drawString(this.outputs[i].id, toX + 5, toY - 8);
			}
		}
		
		if (centerX >= 0 && centerX <= engine.scrWidth && centerY >= 0 && centerY <= engine.scrHeight) inScreen = true;
		
		return inScreen;
	}
	
	public void drawConnectionLines(Graphics g, Engine engine, Font pointFont, Font nodeFont, Engine.Camera camera, ArrayList<Node> nodes, String grabbedUUID) {
		Graphics2D g2d = (Graphics2D) g;
		Stroke origStroke = g2d.getStroke();
		int darkenFactor = 10;
		
		for (int i = 0; i < this.outputs.length; i++) {
			for (int j = 0; j < this.outputs[i].connections.size(); j++) {
				Line con = this.outputs[i].connections.get(j);
				
				if (con != null && this.color != null) {
					int darkenAmount = darkenFactor * (i + 10);
					
					if (this.outputs[i].state) g2d.setColor(this.color);
					else g2d.setColor(new Color(Math.max(this.color.getRed() - darkenAmount, 0), Math.max(this.color.getGreen() - darkenAmount, 0), Math.max(this.color.getBlue() - darkenAmount, 0)));
				}
				else {
					if (this.outputs[i].state) g2d.setColor(Main.onLineColor);
					else g2d.setColor(Main.offLineColor);
				}
				
				g2d.setStroke(new BasicStroke(Main.connectionLineWidth));
				g2d.drawLine(con.x1 - engine.camera.getX(), con.y1 - engine.camera.getY(), con.x2 - engine.camera.getX(), con.y2 - engine.camera.getY());
				g2d.setStroke(origStroke);
			}
		}
	}
	
	public void update(Engine engine, ArrayList<Node> nodes, ArrayList<String> ranUUIDs, String hoveringID) {
		if (this.id.equals("switch")) {
			if (mouseIsHovering(engine) && engine.keys.SPACETYPED()) {
				this.outputs[0].state = !this.outputs[0].state;
				logic(nodes, ranUUIDs);
			}
		}
		
		if (this.pausePanning && mouseIsHovering(engine)) {
			hoveringID = this.id;
		}
		
		
		if (this.id.equals("4BitNumber")) {
			if (mouseIsHovering(engine)) {
				String binaryString = "";
				if (engine.keys.LEFTTYPED() || engine.mouse.getScrollDifference() < 0) {
					if (this.value > 0) this.value--;
					else this.value = 15;
					
					binaryString = Methods.intToBinary(this.value, 4);
					
					
				}
				if (engine.keys.RIGHTTYPED() || engine.mouse.getScrollDifference() > 0) {
					if (this.value < 15) this.value++;
					else this.value = 0;
					
					binaryString = Methods.intToBinary(this.value, 4);
				}
				
				if (binaryString.length() == 4) {
					for (int i = 0; i < 4; i++) {
						this.outputs[i].state = (binaryString.charAt(i) == '1') ? true : false;
					}
				}
			}
			sendAllOutputs(nodes, ranUUIDs);
		}
		
		if (this.id.equals("decoder")) {
			if (mouseIsHovering(engine)) {
				//If press left or right arrow, increase or decrease internal value keeping track of amount of inputs
				if (engine.keys.LEFTTYPED()) {
					if (this.decoderAmount > 1) this.decoderAmount--;
				}
				if (engine.keys.RIGHTTYPED()) {
					if (this.decoderAmount < 4) this.decoderAmount++;
				}
			}
			
			//If amount of inputs does not match internal value, modify inputs and outputs array
			if (this.inputs.length < this.decoderAmount) {
				//Create new input array with proper length
				Input[] newInputs = new Input[this.decoderAmount];
				//Add elements from current input array
				for (int i = 0; i < this.inputs.length; i++) {
					newInputs[i] = this.inputs[i];
				}
				//Add new inputs
				for (int i = this.inputs.length; i < newInputs.length; i++) {
					newInputs[i] = new Input("Input", this.uuid);
				}
				//Assign new array to current array
				this.inputs = newInputs;
				
				//Create new outputs array using 2^decoderAmount for length
				Output[] newOutputs = new Output[(int) Math.pow(2, decoderAmount)];
				//Add elements from current output array
				for (int i = 0; i < this.outputs.length; i++) {
					newOutputs[i] = this.outputs[i];
				}
				//Add new outputs
				for (int i = this.outputs.length; i < newOutputs.length; i++) {
					newOutputs[i] = new Output("Output", this.uuid);
				}
				this.outputs = newOutputs;
			}
			
			//Case where decreasing amount of inputs and outputs is special because we need to sever any potential connections to other nodes
			if (this.inputs.length > this.decoderAmount) {
				//Create new input and output arrays using the needed lengths and copying over the needed inputs and outputs
				Input[] newInputs = new Input[decoderAmount];
				Output[] newOutputs = new Output[(int) Math.pow(2, decoderAmount)];
				
				for (int i = 0; i < newInputs.length; i++) {
					newInputs[i] = this.inputs[i];
				}
				for (int i = 0; i < newOutputs.length; i++) {
					newOutputs[i] = this.outputs[i];
				}
				
				//Before we assign our new arrays to the current arrays, we need to sever the connections
				//Im going to keep track of the uuids that are going to be removed in ArrayLists that we can look through
				//Then I'm going to iterate over the inputs and outputs to see if the connectedUUIDs are in the ArrayLists
				ArrayList<String> inputsToBeRemoved = new ArrayList<>();
				ArrayList<String> outputsToBeRemoved = new ArrayList<>();
				
				for (int i = newInputs.length; i < this.inputs.length; i++) {
					inputsToBeRemoved.add(this.inputs[i].uuid);
				}
				for (int i = newOutputs.length; i < this.outputs.length; i++) {
					outputsToBeRemoved.add(this.outputs[i].uuid);
				}
				
				for (int i = 0; i < nodes.size(); i++) {
					for (int ii = 0; ii < nodes.get(i).inputs.length; ii++) {
						if (outputsToBeRemoved.contains(nodes.get(i).inputs[ii].connectedUUID)) {
							nodes.get(i).inputs[ii].connectedUUID = "";
						}
					}
					
					for (int ii = 0; ii < nodes.get(i).outputs.length; ii++) {
						for (int iii = 0; iii < nodes.get(i).outputs[ii].connectedUUIDs.size(); iii++) {
							if (inputsToBeRemoved.contains(nodes.get(i).outputs[ii].connectedUUIDs.get(iii))) {
								nodes.get(i).outputs[ii].severConnection(nodes, iii);
							}
						}
					}
				}
				
				//Now we can assign the new input and output arrays
				this.inputs = newInputs;
				this.outputs = newOutputs;
			}
		}
		if (this.id.equals("encoder")) {
			if (mouseIsHovering(engine)) {
				if (engine.keys.UPTYPED()) {
					this.encoderHigh = true;
					logic(nodes, ranUUIDs);
				}
				if (engine.keys.DOWNTYPED()) {
					this.encoderHigh = false;
					logic(nodes, ranUUIDs);
				}
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
	
	public void prepareForRemoval(ArrayList<Node> nodes) {
		for (int i = 0; i < this.inputs.length; i++) {
			Output toCut = Methods.searchOutputsByUUID(nodes, this.inputs[i].connectedUUID);
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
		
		/*
		 * Revision to logic and propagation:
		 * 
		 * Start at switches
		 * 	These are always going to be the start of the circuit
		 * Do a "sendAllOutput" for the switches' outputs
		 * 	Do not reject uuids that were used later on
		 * 
		 * When a connection is made, have the output that leads to the input send a propagation
		 * Consider how looping connections should be handled
		 * 	Is it possible to handle every connection asyncronously?
		 * 		Consider: CompletableFuture (open a new "thread" for each send then close the current thread for the output and move on)
		 * 			May still have an issue with looping considering how we may end up with multiple threads doing the same thing
		 * 				Find a way to figure out what connections are already being handled by a new thread and avoid starting something new with them
		 */
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
				this.outputs[0].state = this.inputs[0].state ^ this.inputs[1].state;
			}
			if (this.id.equals("nand")) {
				this.outputs[0].state = !(this.inputs[0].state && this.inputs[1].state);
			}
			if (this.id.equals("nor")) {
				this.outputs[0].state = !(this.inputs[0].state || this.inputs[1].state);
			}
			if (this.id.equals("xnor")) {
				this.outputs[0].state = !(this.inputs[0].state ^ this.inputs[1].state);
			}
			if (this.id.equals("4BitAdder")) {
				boolean[] A = {inputs[0].state, inputs[1].state, inputs[2].state, inputs[3].state};
				boolean[] B = {inputs[4].state, inputs[5].state, inputs[6].state, inputs[7].state};
				
				boolean[] result = Methods.rippleCarryAddSubtract(A, B, inputs[8].state);

				this.outputs[0].state = result[0];
				this.outputs[1].state = result[1];
				this.outputs[2].state = result[2];
				this.outputs[3].state = result[3];
				this.outputs[4].state = result[4];
				/*
				int a = Methods.bits4ToInt(inputs[0].state, inputs[1].state, inputs[2].state, inputs[3].state);
				int b = Methods.bits4ToInt(inputs[4].state, inputs[5].state, inputs[6].state, inputs[7].state);
				int sum = 0;
				int carry = 0;
				
				sum = (inputs[8].state) ? a - b : a + b;
				
				if (sum > 15) {
					carry = 1;
					sum -= 15;
				}
				
				String sumBinary = Methods.intToBinary(sum, 4);
				
				for (int i = 0; i < 4; i++) {
					outputs[i].state = (sumBinary.charAt(i) == '1') ? true : false;
				}
				outputs[4].state = (carry == 0) ? false : true;
				*/
				
				
			}
			if (this.id.equals("4BitDisplay")) {
				String bits = "";
				for (int i = 0; i < 4; i++) {
					if (this.inputs[i].state) bits += "1";
					else bits += "0";
				}
				
				this.value = Methods.binaryToInt(bits);
			}
			if (this.id.equals("decoder")) {
				int input = 0;
				
				if (this.decoderAmount == 1) input = (this.inputs[0].state) ? 1 : 0;
				if (this.decoderAmount == 2) input = Methods.bits2ToInt(this.inputs[0].state, this.inputs[1].state);
				if (this.decoderAmount == 3) input = Methods.bits3ToInt(this.inputs[0].state, this.inputs[1].state, this.inputs[2].state);
				if (this.decoderAmount == 4) input = Methods.bits4ToInt(this.inputs[0].state, this.inputs[1].state, this.inputs[2].state, this.inputs[3].state);
				
				for (int i = 0; i < this.outputs.length; i++) {
					this.outputs[i].state = (i == input) ? true : false;
				}
			}
			if (this.id.equals("mux")) {
				this.outputs[0].state = this.inputs[this.inputs[2].state ? 1: 0].state;
			}
			if (this.id.equals("encoder")) {
				//Store all bits that are on into an array. Based on priority, select either highest or lowest bit index and output it
				ArrayList<Integer> onBits = new ArrayList<>();
				
				for (int i = 0; i < this.inputs.length; i++) {
					if (this.inputs[i].state) onBits.add(i);
				}
				
				int priorityBit = 0;
				
				if (onBits.size() > 0) {
					if (this.encoderHigh) priorityBit = Methods.maxInArrayList(onBits);
					else priorityBit = Methods.minInArrayList(onBits);
				}
				
				String binaryString = Methods.intToBinary(priorityBit, 3);
				
				if (binaryString.length() == 3) {
					for (int i = 0; i < 3; i++) {
						this.outputs[i].state = (binaryString.charAt(i) == '1') ? true : false;
					}
				}
			}
			if (this.custom) {
				//ArrayLists to store values and a list of actions to take sequentially
				ArrayList<String> vars = new ArrayList<>();
				ArrayList<Boolean> states = new ArrayList<>();
				
				try {
					for (String action : this.customActions) {
						Scanner reader = new Scanner(action);
						int iter = 0;
						int assignOutput = -1;
						String assignOutputString = "";
						String assignmentOperator = "";
						String operand1 = "", operand2 = "", operation = "";
						boolean output = false;
						int operand1Index = -1, operand2Index = -1;
						
						while (reader.hasNext()) {
							if (iter == 0) {
								String assignment = reader.next();
								iter++;
								
								if (Methods.isInteger(assignment)) {
									assignOutput = Integer.parseInt(assignment);
								}
								else {
									 if (!vars.contains(assignment)) {
										 assignOutputString = assignment;
										 vars.add(assignment);
										 states.add(false);
									 }
								}
							}
							else if (iter == 1) {
								assignmentOperator = reader.next();
								iter++;
							}
							else if (iter == 2) {
								operand1 = reader.next();
								iter++;
							}
							else if (iter == 3) {
								operation = reader.next();
								iter++;
							}
							else if (iter == 4) {
								operand2 = reader.next();
								iter++;
							}
						}

						if (Methods.isInteger(operand1)) operand1Index = Integer.parseInt(operand1);
						if (Methods.isInteger(operand2)) operand2Index = Integer.parseInt(operand2);
						
						if (iter > 3) {
							if (operand1Index >= 0 && operand2Index >= 0) {
								if (operation.equals("&&")) {
									output = this.inputs[operand1Index].state && this.inputs[operand2Index].state;
								}
								else if (operation.equals("||")) {
									output = this.inputs[operand1Index].state || this.inputs[operand2Index].state;
								}
								else if (operation.equals("^^")) {
									output = this.inputs[operand1Index].state ^ this.inputs[operand2Index].state;
								}
							}
							if (operand1Index >= 0 && operand2Index < 0) {
								if (operation.equals("&&")) {
									output = this.inputs[operand1Index].state && states.get(vars.indexOf(operand2));
								}
								else if (operation.equals("||")) {
									output = this.inputs[operand1Index].state || states.get(vars.indexOf(operand2));
								}
								else if (operation.equals("^^")) {
									output = this.inputs[operand1Index].state ^ states.get(vars.indexOf(operand2));
								}
							}
							if (operand1Index < 0 && operand2Index >= 0) {
								if (operation.equals("&&")) {
									output = states.get(vars.indexOf(operand1)) && this.inputs[operand2Index].state;
								}
								else if (operation.equals("||")) {
									output = states.get(vars.indexOf(operand1)) || this.inputs[operand2Index].state;
								}
								else if (operation.equals("^^")) {
									output = states.get(vars.indexOf(operand1)) ^ this.inputs[operand2Index].state;
								}
							}
							else if (operand1Index < 0 && operand2Index < 0){
								if (operation.equals("&&")) {
									output = states.get(vars.indexOf(operand1)) && states.get(vars.indexOf(operand2));
								}
								else if (operation.equals("||")) {
									output = states.get(vars.indexOf(operand1)) || states.get(vars.indexOf(operand2));
								}
								else if (operation.equals("^^")) {
									output = states.get(vars.indexOf(operand1)) ^ states.get(vars.indexOf(operand2));
								}
							}
						}
						else {
							if (operand1Index < 0) {
								output = states.get(vars.indexOf(operand1));
							}
							else {
								output = this.inputs[operand1Index].state;
							}
						}
						
						
						if (assignOutput < 0) {
							states.set(vars.indexOf(assignOutputString), assignmentOperator.equals("=!") ? !output: output);
						}
						else {
							this.outputs[assignOutput].state = assignmentOperator.equals("=!") ? !output: output;
						}
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
			sendAllOutputs(nodes, ranUUIDs);
		}
	}
	
	public void sendLogic(ArrayList<Node> nodes, ArrayList<String> ranUUIDs) {
		if (this.outputs.length > 0) {
			for (int i = 0; i < this.outputs[0].connectedUUIDs.size(); i++) {
				if (this.outputs.length > 0) Methods.searchInputsByUUID(nodes, this.outputs[0].connectedUUIDs.get(i)).state = this.outputs[0].state;
				ranUUIDs.add(this.uuid);
				Methods.searchNodesByUUID(nodes, Methods.searchInputsByUUID(nodes, this.outputs[0].connectedUUIDs.get(i)).parentUUID).logic(nodes, ranUUIDs);
			}
		}
	}
	
	public void sendAllOutputs(ArrayList<Node> nodes, ArrayList<String> ranUUIDs) {
		if (this.outputs.length > 0) {
			for (int i = 0; i < this.outputs.length; i++) {
				for (int ii = 0; ii < this.outputs[i].connectedUUIDs.size(); ii++) {
					Methods.searchInputsByUUID(nodes, this.outputs[i].connectedUUIDs.get(ii)).state = this.outputs[i].state;
					ranUUIDs.add(this.uuid);
					Methods.searchNodesByUUID(nodes, Methods.searchInputsByUUID(nodes, this.outputs[i].connectedUUIDs.get(ii)).parentUUID).logic(nodes, ranUUIDs);
				}
			}
		}
	}
	
	public void parseCustomActions() {
		//Iterate through behavior script to gather all actions (actions are defined between {})
		for (int i = 0; i < customBehavior.length(); i++) {
			//Find open bracket
			if (customBehavior.charAt(i) == '{') {
				//Find position of next closing bracket
				int endBracket = -1;
				for (int ii = i; ii < customBehavior.length(); ii++) {
					if (customBehavior.charAt(ii) == '}') {
						endBracket = ii;
						ii = customBehavior.length();
					}
				}
				//Add action between brackets to ArrayList of actions
				String action = customBehavior.substring(i + 1, endBracket);
				this.customActions.add(action);
			}
		}
	}
	
	
	
	
	
}
