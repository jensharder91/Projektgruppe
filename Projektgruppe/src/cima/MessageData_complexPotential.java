package cima;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class MessageData_complexPotential extends MessageData{
	
	protected int bestPossibleLamdaValue;
	protected ArrayList<CIMAEdgeWeight> potentialEdges = new ArrayList<CIMAEdgeWeight>();
	
	public MessageData_complexPotential(){
		
	}
	
	public MessageData_complexPotential(CIMAVertice sender, CIMAVertice receiver, int lamdaValue, int bestPossibleLamdaValue, CIMAEdgeWeight... potentialEdge){
		this.sender = sender;
		this.receiver = receiver;
		this.lamdaValue = lamdaValue;
		this.bestPossibleLamdaValue = bestPossibleLamdaValue;
		
		for(int i = 0; potentialEdge.length < i; i++){
			potentialEdges.add(potentialEdge[i]);
		}
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
			potentialEdges.addAll(newPotentialEdges);
		}
//		System.out.println("~update compleyMsgData :: "+bestPossibleLamdaValue + " / "+potentialEdges.toString());
		return this;
	}
	
	@Override
	protected void explainMessageData(Graphics2D g) {
		String[] explainStrings = 
			{"Nachricht", 
				"normale Nachricht: "+lamdaValue, 
				"bestmögliche Nachricht: "+bestPossibleLamdaValue};
		InfoDisplayClass.getInfoDisplayClass().displayInLowerRightCorner(g, Color.BLUE, null, explainStrings);
		
		
		for(CIMAEdgeWeight edge : potentialEdges){
			edge.setOvalColor(Color.GREEN);
			System.out.println("color "+edge.toString());
		}
	}
	

	@Override
	protected void clearExplainMessageData() {
		for(CIMAEdgeWeight edge : potentialEdges){
			System.out.println("clear "+edge.toString());
			edge.resetColor();
		}		
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
