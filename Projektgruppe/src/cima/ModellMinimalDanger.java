package cima;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 
 * algorithmus - logic for potential == 1
 *
 */
public class ModellMinimalDanger extends ICalcStrategy{
	
	private static int bestPossibleLamdaValue;
	private static ArrayList<CIMAEdgeWeight> potentialEdges = new ArrayList<CIMAEdgeWeight>();

	@Override
	public MessageData calcMessageData(CIMAVertice senderNode, CIMAVertice receiverNode, int potential) {
		
		CIMAEdgeWeight edgeToReceiverNode = null;
		if(receiverNode == senderNode.getParent()){
			edgeToReceiverNode = senderNode.getEdgeWeightToParent();
		}else{
			edgeToReceiverNode = receiverNode.getEdgeWeightToParent();
		}
		
		Collections.sort(senderNode.getLamdas(), new MessageDataComparator());
		List<MessageData_simplePotential> maximums = new ArrayList<MessageData_simplePotential>();
		for(MessageData data : senderNode.getLamdas()){
			if(data.getSender() != receiverNode){
				if(data instanceof MessageData_simplePotential){
					maximums.add((MessageData_simplePotential) data);
				}
			}
			if(maximums.size() == 2){
				break;
			}
		}
		
		MessageData_simplePotential biggestMsgData = new MessageData_simplePotential();
		MessageData_simplePotential secBiggestMsgData = new MessageData_simplePotential();
		if(maximums.size() >= 1){
			biggestMsgData = maximums.get(0);
		}
		if(maximums.size() >= 2){
			secBiggestMsgData = maximums.get(1);
		}
		
		CIMAEdgeWeight max1 = calcSortedEdgeWeightList(senderNode, receiverNode).get(0);
		CIMAEdgeWeight max2 = calcSortedEdgeWeightList(senderNode, receiverNode).get(1);
		CIMAEdgeWeight max3 = calcSortedEdgeWeightList(senderNode, receiverNode).get(2);
		
	
		
		Map<String, IMarkable> mapMarkable = new HashMap<String, IMarkable>();
		mapMarkable.put("max1", max1);
		mapMarkable.put("max2", max2);
		mapMarkable.put("edge", edgeToReceiverNode);
		mapMarkable.put("msgData", biggestMsgData);
		
		
		/*
		 * calc messageData and potentialDta
		 */
		
		MessageData_simplePotential calculatedMessageData;
		PotentialData calculatedPotentialData = null;

		//default case max1 + max2
		if(max1.getValue() == max3.getValue()){
			calculatedPotentialData = new PotentialData(true);
		}else if(max2.getValue() == 0 || max2.getValue() == max3.getValue()){
			calculatedPotentialData = new PotentialData(max1);
		}else{
			calculatedPotentialData = new PotentialData(max1, max2);
		}
		calculatedMessageData = new MessageData_simplePotential(senderNode, receiverNode, max1.getValue() + max2.getValue(), mapMarkable, calculatedPotentialData);
		
		//make sure it is not smaller then the edge
		if(calculatedMessageData.getValue() <= edgeToReceiverNode.getValue()){
	
			if(calculatedMessageData.getValue() == edgeToReceiverNode.getValue()){

				if(calculatedMessageData.getPotentialData().getNumberOfpotentialEdgeWeights() == 1){
					
					if(calculatedMessageData.getPotentialData().getPotentialEdgeWeights().get(0).equals(edgeToReceiverNode)){
						//potential data shouldnt change
					}else{
						calculatedMessageData = new MessageData_simplePotential(senderNode, receiverNode, edgeToReceiverNode.getValue(), mapMarkable, new PotentialData(true));
					}
				}				
				
			}else{
				//check if edgeToReceiver is > 1,  if not.. set flag
				if(edgeToReceiverNode.getValue() > 1){
					calculatedMessageData = new MessageData_simplePotential(senderNode, receiverNode, edgeToReceiverNode.getValue(), mapMarkable, new PotentialData(edgeToReceiverNode));								
				}else{
					calculatedMessageData = new MessageData_simplePotential(senderNode, receiverNode, edgeToReceiverNode.getValue(), mapMarkable, new PotentialData(true));
				}
			}
		}
		
		//make sure it is not smaller then the biggest messageData
		if(calculatedMessageData.getValue() <= biggestMsgData.getValue()){
			
			if(calculatedMessageData.getValue() == biggestMsgData.getValue()){
				
				//check if both potentialData has same number of edges
				if(calculatedMessageData.getPotentialData().getNumberOfpotentialEdgeWeights() == biggestMsgData.getPotentialData().getNumberOfpotentialEdgeWeights()){
					for(CIMAEdgeWeight edge : biggestMsgData.getPotentialData().getPotentialEdgeWeights()){
						if(calculatedMessageData.getPotentialData().hasSameEdge(edge)){
							//potential data shouldnt change
						}else{
							calculatedMessageData = new MessageData_simplePotential(senderNode, receiverNode, biggestMsgData.getValue(), mapMarkable, new PotentialData(true));
						}
					}
				}else{
					calculatedMessageData = new MessageData_simplePotential(senderNode, receiverNode, biggestMsgData.getValue(), mapMarkable, new PotentialData(true));
				}
				
				
			}else{
				calculatedMessageData = new MessageData_simplePotential(senderNode, receiverNode, biggestMsgData.getValue(), mapMarkable, biggestMsgData.getPotentialData().getPotentialDataCopy());								
			}
		}
		
		return calculatedMessageData;
	}
	
	public int calcGeneralVerticeWeight(CIMAVertice vertice){
		return calcSortedEdgeWeightList(vertice, null).get(0).getValue();
	}
	
	public List<CIMAEdgeWeight> calcSortedEdgeWeightList(CIMAVertice vertice, CIMAVertice exeptVertice){

		List<CIMAEdgeWeight> edgeWeightList = new ArrayList<CIMAEdgeWeight>();
		List<Vertice> allNeighbors = vertice.getNeighbors();

		for(Vertice verticeNeighbor : allNeighbors){
			if((CIMAVertice)verticeNeighbor != exeptVertice){
				if(((CIMAVertice) verticeNeighbor).equals((CIMAVertice)vertice.getParent())){
					edgeWeightList.add(vertice.getEdgeWeightToParent());
				}else{
					edgeWeightList.add(((CIMAVertice) verticeNeighbor).getEdgeWeightToParent());
				}
			}
		}
		
		//make sure there are 3 or more edges (for all calculations..)
		while(edgeWeightList.size() < 3){
			edgeWeightList.add(new CIMAEdgeWeight(vertice));
		}

		Collections.sort(edgeWeightList);
		return edgeWeightList;
	}
	
	public int calcMu(CIMAVertice vertice, int potential){
		int mu;
		List<MessageData> lamdas = vertice.getLamdas();
		Collections.sort(lamdas, new MessageDataComparator());
		MessageData_simplePotential biggestMsgData = new MessageData_simplePotential();
		MessageData_simplePotential biggest2MsgData = new MessageData_simplePotential();
		if(lamdas.size() >= 1){
			if(lamdas.get(0) instanceof MessageData_simplePotential){
				biggestMsgData = (MessageData_simplePotential)lamdas.get(0);
			}
		}
		if(lamdas.size() >= 2){
			if(lamdas.get(0) instanceof MessageData_simplePotential){
				biggest2MsgData = (MessageData_simplePotential)lamdas.get(1);
			}
		}
		
		List<CIMAEdgeWeight> edgeWeightList = calcSortedEdgeWeightList(vertice, null);
		CIMAEdgeWeight max1 = edgeWeightList.get(0);
		CIMAEdgeWeight max2 = edgeWeightList.get(1);
		CIMAEdgeWeight max3 = edgeWeightList.get(2);
		
		PotentialData potentialData;

		//default case max1 + max2
		if(max1.getValue() == max3.getValue()){
			potentialData = new PotentialData(vertice, true);
		}else if(max2.getValue() == 0 || max2.getValue() == max3.getValue()){
			potentialData = new PotentialData(vertice, max1);
		}else{
			potentialData = new PotentialData(vertice, max1, max2);
		}
		mu = max1.getValue() + max2.getValue();

		//make sure mu is not less the biggest msgData
		if(mu <=  biggestMsgData.getValue()){
			
			if(mu == biggestMsgData.getValue()){
				
				//check if both potentialData has same number of edges
				if(potentialData.getNumberOfpotentialEdgeWeights() == biggestMsgData.getPotentialData().getNumberOfpotentialEdgeWeights()){
					for(CIMAEdgeWeight edge : biggestMsgData.getPotentialData().getPotentialEdgeWeights()){
						if(potentialData.hasSameEdge(edge)){
							//potential data shouldnt change
						}else{
							potentialData = new PotentialData(vertice, true);
						}
					}
				}else{
					potentialData = new PotentialData(vertice, true);
				}
				
				
			}
			else{
				potentialData = biggestMsgData.getPotentialData().getPotentialDataCopy(vertice);
			}
						
			mu = biggestMsgData.getValue();
		}
		
		
		//return...
		if(!potentialData.getFlag()){
			vertice.setBestMu(mu -1);
			vertice.setPotentialEdges(potentialData.getPotentialEdgeWeights());
		}
		
		return mu;
	}

	
	@Override
	public String toString() {
		return "\"Modifikation\" (Potential = 1)";
	}


	@Override
	public void displayResult(CIMAVertice vertice, Graphics2D g2) {
				
		bestPossibleLamdaValue = CIMAVertice.getMinimalMu();
		potentialEdges = new ArrayList<CIMAEdgeWeight>();
		
		//find root
		while (vertice.getParent() != null) {
			vertice = (CIMAVertice) vertice.getParent();
		}
		
		calcBestPossibleLamdaValue(vertice);
		
		//check if potential reduce the minimumMu
		if(bestPossibleLamdaValue >= CIMAVertice.getMinimalMu()){
			bestPossibleLamdaValue = CIMAVertice.getMinimalMu();
			potentialEdges.clear();
		}

		if(ICalcStrategy.showPotential){
			if(potentialEdges.size() > 0){
				InfoDisplayClass.getInfoDisplayClass().displayInUpperLeftCorner(g2, "Agentenzahl kann auf  >>"+bestPossibleLamdaValue+"<<  reduziert werde", 1, Color.black, null);
				for(CIMAEdgeWeight edge : potentialEdges){
					edge.setOvalColor(Color.RED);
					edge.draw(g2);
				}
			}else{
				InfoDisplayClass.getInfoDisplayClass().displayInUpperLeftCorner(g2, "Agentenzahl kann  >>nicht<<  reduziert werde", 1, Color.black, null);
			}
		}
		
	}
	
	private void calcBestPossibleLamdaValue(CIMAVertice vertice){
		
			
		if(vertice.getBestMu() < bestPossibleLamdaValue){
			bestPossibleLamdaValue = vertice.getBestMu();
			potentialEdges.clear();
			potentialEdges = vertice.getPotentialEdges();
		}else if(vertice.getBestMu() == bestPossibleLamdaValue){
			bestPossibleLamdaValue = vertice.getBestMu();
			
			if(vertice.getPotentialEdges() != null){
				for(CIMAEdgeWeight weight : vertice.getPotentialEdges()){
					if(!potentialEdges.contains(weight)){
						potentialEdges.add(weight);
					}
				}
			}
		}
		
		for(Vertice childVertice : vertice.getChildren()){
			calcBestPossibleLamdaValue(((CIMAVertice)childVertice));
		}
		
		if(potentialEdges == null){
			potentialEdges = new ArrayList<CIMAEdgeWeight>();
		}
	}

}
