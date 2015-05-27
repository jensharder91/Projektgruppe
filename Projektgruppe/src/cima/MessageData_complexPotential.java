package cima;

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
		if(this.bestPossibleLamdaValue > bestPossibleLamdaValue){
			this.bestPossibleLamdaValue = bestPossibleLamdaValue;
			potentialEdges.clear();
			for(int i = 0; potentialEdge.length < i; i++){
				potentialEdges.add(potentialEdge[i]);
			}
		}else{
			for(int i = 0; potentialEdge.length < i; i++){
				potentialEdges.add(potentialEdge[i]);
			}
		}
		return this;
	}
	
	
	public int getBestPossiblelamdaValue(){
		return bestPossibleLamdaValue;
	}
	public ArrayList<CIMAEdgeWeight> getPotentialEdges(){
		return potentialEdges;
	}

}
