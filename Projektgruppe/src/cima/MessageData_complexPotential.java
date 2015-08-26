package cima;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessageData_complexPotential extends MessageData{
	
	protected int bestPossibleLamdaValue;
	protected ArrayList<CIMAEdgeWeight> potentialEdges = new ArrayList<CIMAEdgeWeight>();
	
	protected Map<String, IMarkable> mapMarkable = new HashMap<String, IMarkable>();

	
	public MessageData_complexPotential(){
		
	}
	
	public MessageData_complexPotential(CIMAVertice sender, CIMAVertice receiver, int lamdaValue, int bestPossibleLamdaValue, Map<String, IMarkable> mapMarkable, CIMAEdgeWeight... potentialEdge){
		this.sender = sender;
		this.receiver = receiver;
		this.lamdaValue = lamdaValue;
		this.bestPossibleLamdaValue = bestPossibleLamdaValue;
		
		for(int i = 0; potentialEdge.length < i; i++){
			potentialEdges.add(potentialEdge[i]);
		}
		
		this.mapMarkable = mapMarkable;
	}
	
	public MessageData_complexPotential updateMessageData(int bestPossibleLamdaValue, CIMAEdgeWeight... potentialEdge){
		
		ArrayList<CIMAEdgeWeight> newPotentialEdges = new ArrayList<CIMAEdgeWeight>();
		for(int i = 0; i < potentialEdge.length; i++){
			newPotentialEdges.add(potentialEdge[i]);
		}
		
		return updateMessageData(bestPossibleLamdaValue, newPotentialEdges);
	}
		
	public MessageData_complexPotential updateMessageData(int bestPossibleLamdaValue, ArrayList<CIMAEdgeWeight> newPotentialEdges){
		
	
		//there is no improvement -> potetial cant get used
		if(bestPossibleLamdaValue >= lamdaValue){
			potentialEdges.clear();
			
			
		}else {//potential can be used
					
			if(this.bestPossibleLamdaValue > bestPossibleLamdaValue){
				this.bestPossibleLamdaValue = bestPossibleLamdaValue;
				potentialEdges.clear();
			}
			for(CIMAEdgeWeight edge : newPotentialEdges){
				if(!potentialEdges.contains(edge)){
					potentialEdges.add(edge);
				}
			}
//			potentialEdges.addAll(newPotentialEdges);
		}
//		System.out.println("~update compleyMsgData :: "+bestPossibleLamdaValue + " / "+potentialEdges.toString());
		return this;
	}
	
	@Override
	protected void explainMessageData(Graphics2D g) {

		if(ICalcStrategy.getShowPotential()){
			displayPotentialCalculation(g);
		}else{
			displayValueCalculation(g);
		}
	}
	
	private void displayPotentialCalculation(Graphics2D g){
		String[] explainStrings = new String[3]; 
		explainStrings[0] = "normale Nachricht: "+lamdaValue;
		explainStrings[1] = "bestmögliche Nachricht: "+bestPossibleLamdaValue;
		explainStrings[2] = potentialEdges.size()+" Kanten protokolliert";
		
		
		InfoDisplayClass.getInfoDisplayClass().displayInUpperLeftCorner(g, explainStrings[0], 1, Color.BLACK, ovalColor);
		InfoDisplayClass.getInfoDisplayClass().displayInUpperLeftCorner(g, explainStrings[1], 2, Color.BLACK, null);
		if(potentialEdges.size() > 0){
			InfoDisplayClass.getInfoDisplayClass().displayInUpperLeftCorner(g, explainStrings[2], 3, Color.BLACK, markColor);
		}else{
			InfoDisplayClass.getInfoDisplayClass().displayInUpperLeftCorner(g, explainStrings[2], 3, Color.BLACK, null);
		}
		
		
		for(CIMAEdgeWeight edge : potentialEdges){
			edge.setOvalColor(markColor, Color.BLACK);
//			System.out.println("color "+edge.toString());
		}
	}
	
	private void displayValueCalculation(Graphics2D g){
		
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
//			maxColor = Color.GREEN;
			maxColor = markColor;
			explainStrings[0] = "Neue Nachricht = max1 + max2:";
		}else if(edge.getValue() == lamdaValue){
			edge.setOvalColor(markColor, Color.BLACK);
//			edgeColor = Color.GREEN;
			edgeColor = markColor;
			explainStrings[0] = "Neue Nachricht = Nachrichtenkante";
		}else if(msgData.getValue() == lamdaValue){
			msgData.setOvalColor(markColor, Color.BLACK);
//			msgDataColor = Color.GREEN;
			msgDataColor = markColor;
			explainStrings[0] = "Neue Nachricht = größte Nachricht";
		}else{
			System.out.println("######################################################################\n"
					+ "etwas ist kaputt....... (Messagedata_complexPotential\n"
					+ "######################################################################");
		}
		
		InfoDisplayClass.getInfoDisplayClass().displayInUpperLeftCorner(g, explainStrings[0], 1, Color.BLACK, markColor);
		InfoDisplayClass.getInfoDisplayClass().displayInUpperLeftCorner(g, explainStrings[1], 2, Color.BLACK, maxColor);
		InfoDisplayClass.getInfoDisplayClass().displayInUpperLeftCorner(g, explainStrings[2], 3, Color.BLACK, edgeColor);
		InfoDisplayClass.getInfoDisplayClass().displayInUpperLeftCorner(g, explainStrings[3], 4, Color.BLACK, msgDataColor);
		
	}
	

	@Override
	protected void clearExplainMessageData() {
		for(CIMAEdgeWeight edge : potentialEdges){
//			System.out.println("clear "+edge.toString());
			edge.resetColor();
		}
		
		IMarkable max1 = mapMarkable.get("max1");
		IMarkable max2 = mapMarkable.get("max2");
		IMarkable edge = mapMarkable.get("edge");
		IMarkable msgData = mapMarkable.get("msgData");
		
		max1.resetColor();
		max2.resetColor();
		edge.resetColor();
		msgData.resetColor();
	}
	
	@Override
	public String toString() {
		if(sender == null || receiver == null){
			return "NULL string";
		}
		return "Sender : "+sender.getName()+"  EmpfÃ¤nger : "+receiver.getName()+"  LamdaValue: "+lamdaValue+ " bestPossibleLamdaValue: "+bestPossibleLamdaValue+" potentialEdges: "+potentialEdges;
	}
	
	public int getBestPossiblelamdaValue(){
		return bestPossibleLamdaValue;
	}
	public ArrayList<CIMAEdgeWeight> getPotentialEdges(){
		return potentialEdges;
	}
}
