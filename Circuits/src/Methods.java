import java.util.ArrayList;

public class Methods {
	public static int binaryToInt(String in) {
		return Integer.parseInt(in, 2);
	}
	
	public static int bits2ToInt(boolean a, boolean b) {
		String binary = "";
		binary += (a) ? "1" : "0";
		binary += (b) ? "1" : "0";
		
		return binaryToInt(binary);
	}
	
	public static int bits3ToInt(boolean a, boolean b, boolean c) {
		String binary = "";
		binary += (a) ? "1" : "0";
		binary += (b) ? "1" : "0";
		binary += (c) ? "1" : "0";
		
		return binaryToInt(binary);
	}
	
	public static int bits4ToInt(boolean a, boolean b, boolean c, boolean d) {
		String binary = "";
		binary += (a) ? "1" : "0";
		binary += (b) ? "1" : "0";
		binary += (c) ? "1" : "0";
		binary += (d) ? "1" : "0";
		
		return binaryToInt(binary);
	}
	
	public static boolean[] fullAdd(boolean a, boolean b, boolean c) {
		boolean[] toOut = {false, false};
		
		toOut[0] = (!a && !b && c) || (!a && b && !c) || (a && !b && !c) || (a && b && c);
		toOut[1] = (!a && b && c) || (a && !b && c) || (a && b && !c) || (a && b && c);
		
		return toOut;
	}
	
	public static String intToBinary (int n, int numOfBits) {
	   String binary = "";
	   for(int i = 0; i < numOfBits; ++i, n/=2) {
	      switch (n % 2) {
	         case 0:
	            binary = "0" + binary;
	         break;
	         case 1:
	            binary = "1" + binary;
	         break;
	      }
	   }

	   return binary;
	}
	
	public static String add_Binary(String x, String y) {
 
        int num1 = Integer.parseInt(x, 2);
        // converting binary string into integer(decimal
        // number)
 
        int num2 = Integer.parseInt(y, 2);
        // converting binary string into integer(decimal
        // number)
 
        int sum = num1 + num2;
        // Adding those two decimal numbers and storing in
        // sum
 
        String result = Integer.toBinaryString(sum);
        // Converting that resultant decimal into binary
        // string
 
        return result;
    }
	
	public static boolean inProx(int x, int y, int rad, int checkX, int checkY, int prox) {
		boolean in = false;
		
		if (checkX >= x - rad - prox && checkX <= x + rad + prox && checkY >= y - rad - prox && checkY <= y + rad + prox) in = true;
		
		return in;
	}
	
	public static boolean inRad(int x, int y, int rad, int checkX, int checkY) {
		boolean in = false;
		
		if (checkX >= x - rad && checkX <= x + rad && checkY >= y - rad && checkY <= y + rad) in = true;
		
		return in;
	}
	
	public static Node searchByUUID(ArrayList<Node> nodes, String toCheck) {
		Node toOut = null;
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).uuid.equals(toCheck)) toOut = nodes.get(i);
		}
		return toOut;
	}
	
	public static Node.Output searchOutputsByUUID(ArrayList<Node> nodes, String toCheck) {
		Node.Output toOut = null;
		
		for (int i = 0; i < nodes.size(); i++) {
			for (int j = 0; j < nodes.get(i).outputs.length; j++) {
				if (nodes.get(i).outputs[j].uuid.equals(toCheck)) {
					toOut = nodes.get(i).outputs[j];
				}
			}
		}
		
		return toOut;
	}
	
	public static Node.Input searchInputsByUUID(ArrayList<Node> nodes, String toCheck) {
		Node.Input toOut = null;
		
		for (int i = 0; i < nodes.size(); i++) {
			for (int j = 0; j < nodes.get(i).inputs.length; j++) {
				if (nodes.get(i).inputs[j].uuid.equals(toCheck)) {
					toOut = nodes.get(i).inputs[j];
				}
			}
		}
		
		return toOut;
	}
	
	public static Node searchNodesByUUID(ArrayList<Node> nodes, String toCheck) {
		Node toOut = null;
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).uuid.equals(toCheck)) toOut = nodes.get(i);
		}
		return toOut;
	}
	
	public static int indexOf(String val, String[] array) {
		int index = -1;
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(val)) {
				index = i;
				break;
			}
		}
		return index;
	}
	
	public static int searchOutputIndexByUUID(Node node, String toUUID) {
		int index = -1;
		
		for (int i = 0; i < node.outputs.length; i++) {
			if (toUUID.equals(node.outputs[i].uuid)) index = i;
		}
		
		return index;
	}
	
	public static int searchInputIndexByUUID(Node node, String toUUID) {
		int index = -1;
		
		for (int i = 0; i < node.inputs.length; i++) {
			if (toUUID.equals(node.inputs[i].uuid)) index = i;
		}
		
		return index;
	}
}
