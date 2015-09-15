package cima;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.plaf.basic.BasicLookAndFeel;

/**
 * 
 * messageData for the paper-algorithmus (no potential)
 *
 */
public class MessageData_normal extends MessageData{
	
	
	Map<String, IMarkable> mapMarkable = new HashMap<String, IMarkable>();

	
	//constructers
	public MessageData_normal(){
		
	}

	public MessageData_normal(CIMAVertice sender, CIMAVertice receiver, Map<String, IMarkable> mapMarkable, int lamdaValue){
		this.sender = sender;
		this.receiver = receiver;
		this.lamdaValue = lamdaValue;
		
		this.mapMarkable = mapMarkable;
		
		calcArc();
	}
	
	
	//explant the msgData (color tree-emenets, write in InfoDisplayClass)
	@Override
	protected void explainMessageData(Graphics2D g) {

		displayValueCalculation(g);

	}
		
	private void displayValueCalculation(Graphics2D g){
		
		IMarkable max1 = mapMarkable.get("max1");
		IMarkable max2 = mapMarkable.get("max2");
		IMarkable vertice = mapMarkable.get("vertice");
		
		if(max1 == null){
			System.out.println("mark1 == null");
		}
		if(max2 == null){
			System.out.println("mark2 == null");
		}
		if(vertice == null){
			System.out.println("edge == null");
		}
		
		//default coloring
		max1.setOvalColor(defaultColorMax, Color.BLACK);
		max2.setOvalColor(defaultColorMax, Color.BLACK);
		vertice.setOvalColor(defaultColorMax, Color.BLACK);
		
		
		String[] explainStrings = new String[4];
		explainStrings[1] = "max1 = "+max1.getValue();
		explainStrings[2] = "Knotengewicht ("+vertice.getValue()+") + max2 ("+max2.getValue()+") = "+(vertice.getValue()+max2.getValue());
		
		Color maxColor = defaultColorMax;
		Color edgeColor = defaultColorMax;
			

		if((max1.getValue()) == lamdaValue){
			max1.setOvalColor(markColor, Color.BLACK);
			maxColor = Color.GREEN;
			explainStrings[0] = "Neue Nachricht = max1";
		}else if(vertice.getValue() + max2.getValue() == lamdaValue){
			vertice.setOvalColor(markColor, Color.BLACK);
			max2.setOvalColor(markColor, Color.BLACK);
			edgeColor = Color.GREEN;
			explainStrings[0] = "Neue Nachricht = Knotengewicht + max2";
		}else{
			System.out.println("######################################################################\n"
					+ "etwas ist kaputt....... (Messagedata_complexPotential\n"
					+ "######################################################################");
		}
		
		InfoDisplayClass.getInfoDisplayClass().displayInUpperLeftCorner(g, explainStrings[0], 1, Color.BLACK, Color.GREEN);
		InfoDisplayClass.getInfoDisplayClass().displayInUpperLeftCorner(g, explainStrings[1], 2, Color.BLACK, maxColor);
		InfoDisplayClass.getInfoDisplayClass().displayInUpperLeftCorner(g, explainStrings[2], 3, Color.BLACK, edgeColor);
		
	}
	

	@Override
	protected void clearExplainMessageData() {
		
		IMarkable max1 = mapMarkable.get("max1");
		IMarkable max2 = mapMarkable.get("max2");
		IMarkable vertice = mapMarkable.get("vertice");
		
		max1.resetColor();
		max2.resetColor();
		vertice.resetColor();
	}

}
