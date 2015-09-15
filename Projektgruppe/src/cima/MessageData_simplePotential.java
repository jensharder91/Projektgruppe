package cima;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 
 * messageData for potential == 1
 *
 */
public class MessageData_simplePotential extends MessageData{
	
	protected PotentialData potentialData;
	
	protected Map<String, IMarkable> mapMarkable = new HashMap<String, IMarkable>();

	
	//constructers
	public MessageData_simplePotential() {
		
	}
	public MessageData_simplePotential(CIMAVertice sender, CIMAVertice receiver, int lamdaValue, Map<String,IMarkable> mapMarkable, PotentialData potentialData) {
		this.sender = sender;
		this.receiver = receiver;
		this.lamdaValue = lamdaValue;
		this.potentialData = potentialData;
		
		
		this.mapMarkable = mapMarkable;
	}
	
	
	public PotentialData getPotentialData(){
		return potentialData;
	}
	

	//explant the msgData (color tree-emenets, write in InfoDisplayClass)
	@Override
	protected void explainMessageData(Graphics2D g) {

		if(ICalcStrategy.getShowPotential()){
			displayPotentialCalculation(g);
		}else{
			displayValueCalculation(g);
		}
	}
	
	private void displayPotentialCalculation(Graphics2D g){
		
		
		List<CIMAEdgeWeight> potentialEdgeWeights = potentialData.getPotentialEdgeWeights();
		
		
		String[] explainStrings = new String[2]; 
		explainStrings[0] = "normale Nachricht: "+lamdaValue;
		explainStrings[1] = potentialEdgeWeights.size()+" Kanten protokolliert";
		
		
		InfoDisplayClass.getInfoDisplayClass().displayInUpperLeftCorner(g, explainStrings[0], 1, Color.BLACK, ovalColor);
		if(potentialEdgeWeights.size() > 0){
			InfoDisplayClass.getInfoDisplayClass().displayInUpperLeftCorner(g, explainStrings[1], 2, Color.BLACK, markColor);
		}else{
			InfoDisplayClass.getInfoDisplayClass().displayInUpperLeftCorner(g, explainStrings[1], 2, Color.BLACK, null);
		}
		
		
		for(CIMAEdgeWeight edge : potentialEdgeWeights){
			edge.setOvalColor(markColor, Color.BLACK);
		}
	}
	
	private void displayValueCalculation(Graphics2D g){
		
		List<CIMAEdgeWeight> potentialEdgeWeights = potentialData.getPotentialEdgeWeights();
		for(CIMAEdgeWeight edge : potentialEdgeWeights){
			edge.resetColor();
		}
		
		IMarkable max1 = mapMarkable.get("max1");
		IMarkable max2 = mapMarkable.get("max2");
		IMarkable edge = mapMarkable.get("edge");
		IMarkable msgData = mapMarkable.get("msgData");
		
		if(max1 == null){
			System.out.println("mark1 == null");
		}
		if(max2 == null){
			System.out.println("mark2 == null");
		}
		if(edge == null){
			System.out.println("edge == null");
		}
		if(msgData == null){
			System.out.println("msgData == null");
		}
		
		//default coloring
		max1.setOvalColor(defaultColorMax, Color.BLACK);
		max2.setOvalColor(defaultColorMax, Color.BLACK);
		edge.setOvalColor(defaultColorEdge, Color.BLACK);
		msgData.setOvalColor(defaultColorMsgData, Color.BLACK);
		
		
		String[] explainStrings = new String[4];
		explainStrings[1] = "max1 ("+max1.getValue()+") + max2 ("+max2.getValue() +") = "+(max1.getValue()+max2.getValue());
		explainStrings[2] = "Nachrichtenkante = "+edge.getValue();
		explainStrings[3] = "Größte Nachricht = "+msgData.getValue();
		
		Color maxColor = defaultColorMax;
		Color edgeColor = defaultColorEdge;
		Color msgDataColor = defaultColorMsgData;
			

		if((max1.getValue() + max2.getValue()) == lamdaValue){
			max1.setOvalColor(markColor, Color.BLACK);
			max2.setOvalColor(markColor, Color.BLACK);
			maxColor = Color.GREEN;
			explainStrings[0] = "Neue Nachricht = max1 + max2:";
		}else if(edge.getValue() == lamdaValue){
			edge.setOvalColor(markColor, Color.BLACK);
			edgeColor = Color.GREEN;
			explainStrings[0] = "Neue Nachricht = Nachrichtenkante";
		}else if(msgData.getValue() == lamdaValue){
			msgData.setOvalColor(markColor, Color.BLACK);
			msgDataColor = Color.GREEN;
			explainStrings[0] = "Neue Nachricht = größte Nachricht";
		}else{
			System.out.println("######################################################################\n"
					+ "etwas ist kaputt....... (Messagedata_complexPotential\n"
					+ "######################################################################");
		}
		
		InfoDisplayClass.getInfoDisplayClass().displayInUpperLeftCorner(g, explainStrings[0], 1, Color.BLACK, Color.GREEN);
		InfoDisplayClass.getInfoDisplayClass().displayInUpperLeftCorner(g, explainStrings[1], 2, Color.BLACK, maxColor);
		InfoDisplayClass.getInfoDisplayClass().displayInUpperLeftCorner(g, explainStrings[2], 3, Color.BLACK, edgeColor);
		InfoDisplayClass.getInfoDisplayClass().displayInUpperLeftCorner(g, explainStrings[3], 4, Color.BLACK, msgDataColor);
		
	}
	

	@Override
	protected void clearExplainMessageData() {
		
		IMarkable max1 = mapMarkable.get("max1");
		IMarkable max2 = mapMarkable.get("max2");
		IMarkable edge = mapMarkable.get("edge");
		IMarkable msgData = mapMarkable.get("msgData");
		
		max1.resetColor();
		max2.resetColor();
		edge.resetColor();
		msgData.resetColor();
	}
	
	
	//getter
	public boolean getFlag(){
		return potentialData.getFlag();
	}
	
	public ArrayList<CIMAEdgeWeight> getPotentialEdgeWeights(){
		return potentialData.getPotentialEdgeWeights();
	}
	
	@Override
	public String toString() {
		if(sender == null || receiver == null){
			return "NULL string";
		}
		return "Sender : "+sender.getName()+"  EmpfÃ¤nger : "+receiver.getName()+"  LamdaValue: "+lamdaValue+ " potentialData: "+potentialData;
	}
}
