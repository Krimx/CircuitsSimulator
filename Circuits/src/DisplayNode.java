import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.UUID;

public class DisplayNode {
	public int x,y,w,h;
	public String id, uuid;
	public Color color;
	public int yOffset = 10, yGap = 20;
	public int grabX, grabY;
	public boolean pointHovering;
	public int pointRad;
	
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
			new Color(49,250,52),
			new Color(45,225,245),
			new Color(205,249,130)};
	
	public DisplayNode(int x, int y, String id, Font nodeFont) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.uuid = UUID.randomUUID().toString();
		this.pointHovering = false;
		this.pointRad = 4;

		this.h = 20 + yOffset;
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
	
	public void render(Graphics g, Font pointFont, Font nodeFont) {
		g.setColor(this.color);
		
		g.fillRect(x - (this.w / 2), y - (this.h / 2), w, h);
		g.setColor(Color.black);
		g.drawRect(x - (this.w / 2), y - (this.h / 2), w, h);
		
		g.setColor(Color.black);
		g.setFont(nodeFont);
		g.drawString(this.id, this.x - (this.w / 2) + 10, this.y + 8);
	}
	
	public void render(Graphics g, Engine engine, Font pointFont, Font nodeFont, Engine.Camera camera, ArrayList<Node> nodes, String grabbedUUID) {
		g.setColor(this.color);
		
		g.fillRect(x - (this.w / 2) - camera.getX(), y - (this.h / 2) - camera.getY(), w, h);
		g.setColor(Color.black);
		g.drawRect(x - (this.w / 2) - camera.getX(), y - (this.h / 2) - camera.getY(), w, h);
		
		g.setColor(Color.black);
		g.setFont(nodeFont);
		g.drawString(this.id, this.x - (this.w / 2) + 10 - camera.getX(), this.y + 8 - camera.getY());
		
		this.pointHovering = false;
	}
	
	public void update(Engine engine, ArrayList<Node> nodes, ArrayList<String> ranUUIDs) {
		
		
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
}