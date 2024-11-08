import java.awt.Font;
import java.awt.FontMetrics;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

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
			if (nodes.get(i).uuid.equals(toCheck)) {
				toOut = nodes.get(i);
				break;
			}
		}
		return toOut;
	}
	
	public static Node.Output searchOutputsByUUID(ArrayList<Node> nodes, String toCheck) {
		Node.Output toOut = null;
		
		for (int i = 0; i < nodes.size(); i++) {
			for (int j = 0; j < nodes.get(i).outputs.length; j++) {
				if (nodes.get(i).outputs[j].uuid.equals(toCheck)) {
					toOut = nodes.get(i).outputs[j];
					break;
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
					break;
				}
			}
		}
		
		return toOut;
	}
	
	public static Node searchNodesByUUID(ArrayList<Node> nodes, String toCheck) {
		Node toOut = null;
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).uuid.equals(toCheck)) {
				toOut = nodes.get(i);
				break;
			}
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
			if (toUUID.equals(node.outputs[i].uuid)) {
				index = i;
				break;
			}
		}
		
		return index;
	}
	
	public static int searchInputIndexByUUID(Node node, String toUUID) {
		int index = -1;
		
		for (int i = 0; i < node.inputs.length; i++) {
			if (toUUID.equals(node.inputs[i].uuid)) {
				index = i;
				break;
			}
		}
		
		return index;
	}
	
	public static int maxInArrayList(ArrayList<Integer> list) {
		int max = 0;
		
		for (int i = 0; i < list.size(); i++) {
			if (i == 0) max = list.get(i);
			else {
				if (list.get(i) > max) max = list.get(i);
			}
		}
		
		return max;
	}
	
	public static int minInArrayList(ArrayList<Integer> list) {
		int min = 0;
		
		for (int i = 0; i < list.size(); i++) {
			if (i == 0) min = list.get(i);
			else {
				if (list.get(i) < min) min = list.get(i);
			}
		}
		
		return min;
	}
	
	public static void loadCustomNodeFromFile(ArrayList<Node> nodes, Font nodeFont, Engine engine) {
		JFileChooser fileChooser = new JFileChooser();
		String chosenFilePath = "";
		fileChooser.setDialogTitle("Choose Node file");
		fileChooser.setFileFilter(new FileNameExtensionFilter("Node Files", "node"));
		int dialog = fileChooser.showOpenDialog(null);
		if (dialog == JFileChooser.APPROVE_OPTION)
			 
        {
            // set the label to the path of the selected file
            chosenFilePath = fileChooser.getSelectedFile().getAbsolutePath();
        }
		if (!chosenFilePath.equals("")) {
			try {
				Scanner reader = new Scanner(new File(chosenFilePath));
				String toID = reader.nextLine();
				int inputCount = Integer.valueOf(reader.nextLine());
				int outputCount = Integer.valueOf(reader.nextLine());
				String[] toInputs = new String[inputCount];
				String[] toOutputs = new String[outputCount];
				Node toMake = new Node(engine.scrWidth / 2, engine.scrWidth / 2, "custom", toInputs, toOutputs, nodeFont, true);
				toMake.id = toID;
				toMake.addInputsAndOutputs(inputCount, outputCount);
				toMake.customBehavior = reader.nextLine();
				FontMetrics metrics = engine.scr.getFontMetrics(nodeFont);
				toMake.w = metrics.stringWidth(toMake.id) + 20;
				toMake.parseCustomActions();

				nodes.add(toMake);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static boolean isInteger(String str) {
	    try {
	        Integer.parseInt(str);
	        return true; // The string is a valid integer
	    } catch (NumberFormatException e) {
	        return false; // The string is not a valid integer
	    }
	}
	
	public static boolean[] fullAdd(boolean A, boolean B, boolean carryIn) {
		boolean[] toOut = {false, false};
		
		toOut[0] = (A ^ B) ^ carryIn;
		toOut[1] = (A && B) || ((A ^ B) && carryIn);
		
		return toOut;
	}
	
	public static boolean[] rippleCarryAddSubtract(boolean[] A, boolean[] B, boolean subtract) {
		boolean[] toOut = {false, false, false, false, false};

		boolean[] add1 = fullAdd(A[3], B[3] ^ subtract, subtract);
		boolean[] add2 = fullAdd(A[2], B[2] ^ subtract, add1[1]);
		boolean[] add3 = fullAdd(A[1], B[1] ^ subtract, add2[1]);
		boolean[] add4 = fullAdd(A[0], B[0] ^ subtract, add3[1]);

		toOut[3] = add1[0];
		toOut[2] = add2[0];
		toOut[1] = add3[0];
		toOut[0] = add4[0];
		toOut[4] = add4[1];
		
		return toOut;
	}
}
