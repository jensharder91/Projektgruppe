package cima;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelMultPotential extends ICalcStrategy{
	
	
	private static int bestPossibleLamdaValue;
	private static ArrayList<CIMAEdgeWeight> potentialEdges = new ArrayList<CIMAEdgeWeight>();
	
	
	private static int calcMsgDataValue(int max1, int max2, int maxMsgDataValue, int edgeValue){
		
		int newMsgDataValue = max1 + max2;
		
		if(maxMsgDataValue > newMsgDataValue){
			newMsgDataValue = maxMsgDataValue;
		}
		
		if(edgeValue > newMsgDataValue){
			newMsgDataValue = edgeValue;
		}
		
		return newMsgDataValue;
	}
	
	private static int calcBestMuValue(int max1, int max2, int maxMsgDataValue){
		return calcMsgDataValue(max1, max2, maxMsgDataValue, 0);
	}
	
	
	private static  MessageData calcBestMsgDataValueWithPotential(CIMAVertice sender, CIMAVertice receiver, CIMAEdgeWeight max1, CIMAEdgeWeight max2, CIMAEdgeWeight max3, MessageData maxMsgData_notTested, MessageData max2MsgData_notTested, CIMAEdgeWeight edgeValue, int potential){

		MessageData_complexPotential maxMsgData = new MessageData_complexPotential();
		MessageData_complexPotential max2MsgData = new MessageData_complexPotential();
		
		if(maxMsgData_notTested instanceof MessageData_complexPotential){
			maxMsgData = (MessageData_complexPotential) maxMsgData_notTested;
		}
		if(max2MsgData_notTested instanceof MessageData_complexPotential){
			max2MsgData = (MessageData_complexPotential) max2MsgData_notTested;
		}
		
		Map<String, IMarkable> mapMarkable = new HashMap<String, IMarkable>();
		mapMarkable.put("max1", max1);
		mapMarkable.put("max2", max2);
		mapMarkable.put("edge", edgeValue);
		mapMarkable.put("msgData", maxMsgData);
		
		//normal value without potential
		int normalMsgDataValue = calcMsgDataValue(max1.getValue(), max2.getValue(), maxMsgData.getValue(), edgeValue.getValue());
		int bestMsgDataValue = normalMsgDataValue;
		MessageData_complexPotential newMessageData = new MessageData_complexPotential(sender, receiver, normalMsgDataValue, bestMsgDataValue, mapMarkable);
		
		//check if you can use the potential to minimize the msgDataValue
		int reducedValue = 1;
		int possibleMsgData = 1;
		
		//case1: reduce the edgeValue
		reducedValue = edgeValue.getValue() - potential;
		if(reducedValue < 1){
			reducedValue = 1;
		}
		possibleMsgData = calcMsgDataValue(max1.getValue(), max2.getValue(), maxMsgData.getValue(), reducedValue);
		if(possibleMsgData <= bestMsgDataValue){
			bestMsgDataValue = possibleMsgData;
			newMessageData.updateMessageData(bestMsgDataValue, edgeValue);
		}
		
		//case2: reduce the max1
		reducedValue = max1.getValue() - potential;
		if(reducedValue < 1){
			reducedValue = 1;
		}
		if(reducedValue < max3.getValue()){
			reducedValue = max3.getValue();
		}
		possibleMsgData = calcMsgDataValue(reducedValue, max2.getValue(), maxMsgData.getValue(), edgeValue.getValue());
		if(possibleMsgData <= bestMsgDataValue){
			bestMsgDataValue = possibleMsgData;
			newMessageData.updateMessageData(bestMsgDataValue, max1);
		}
		
		//case3: reduce the max2
		reducedValue = max2.getValue() - potential;
		if(reducedValue < 1){
			reducedValue = 1;
		}
		if(reducedValue < max3.getValue()){
			reducedValue = max3.getValue();
		}
		possibleMsgData = calcMsgDataValue(max1.getValue(), reducedValue, maxMsgData.getValue(), edgeValue.getValue());
		if(possibleMsgData <= bestMsgDataValue){
			bestMsgDataValue = possibleMsgData;
			newMessageData.updateMessageData(bestMsgDataValue, max2);
		}
		
		//case4: reduce the maxMsgData
		reducedValue = maxMsgData.getBestPossiblelamdaValue();
		if(reducedValue < 1){
			reducedValue = 1;
		}
		
		
		if(reducedValue < max2MsgData.getValue()){
			int reducedValueFromMax2MsgData = max2MsgData.getValue();
			
			
			if(reducedValue < max3.getValue()){
				reducedValue = max3.getValue();
			}
			
			if(maxMsgData.getPotentialEdges() != null){
				if(maxMsgData.getPotentialEdges().size() == 1 && maxMsgData.getPotentialEdges().get(0).equals(max1)){
					possibleMsgData = calcMsgDataValue(reducedValue, max2.getValue(), reducedValueFromMax2MsgData, edgeValue.getValue());					
				}else if(maxMsgData.getPotentialEdges().size() == 1 && maxMsgData.getPotentialEdges().get(0).equals(max2)){
					possibleMsgData = calcMsgDataValue(max1.getValue(), reducedValue, reducedValueFromMax2MsgData, edgeValue.getValue());
				}else{
					possibleMsgData = calcMsgDataValue(max1.getValue(), max2.getValue(), reducedValueFromMax2MsgData, edgeValue.getValue());
				}
				if(possibleMsgData <= bestMsgDataValue){
					bestMsgDataValue = possibleMsgData;
					newMessageData.updateMessageData(bestMsgDataValue, maxMsgData.getPotentialEdges());
				}
				
			}
			
			
			
		}else if(maxMsgData.getPotentialEdges() != null){
			if(maxMsgData.getPotentialEdges().size() == 1 && maxMsgData.getPotentialEdges().get(0).equals(max1)){
				possibleMsgData = calcMsgDataValue(reducedValue, max2.getValue(), reducedValue, edgeValue.getValue());
			}else if(maxMsgData.getPotentialEdges().size() == 1 && maxMsgData.getPotentialEdges().get(0).equals(max2)){
				possibleMsgData = calcMsgDataValue(max1.getValue(), reducedValue, reducedValue, edgeValue.getValue());
			}else{
				possibleMsgData = calcMsgDataValue(max1.getValue(), max2.getValue(), reducedValue, edgeValue.getValue());
			}
			if(possibleMsgData <= bestMsgDataValue){
				bestMsgDataValue = possibleMsgData;
				newMessageData.updateMessageData(bestMsgDataValue, maxMsgData.getPotentialEdges());
			}
		}
		
		//return the bestMsgDataValue	
		return newMessageData;
	}

	

	@Override
	public MessageData calcMessageData(CIMAVertice senderNode, CIMAVertice receiverNode, int potential) {
		
		CIMAEdgeWeight edgeToReceiverNode = null;
		if(receiverNode == senderNode.getParent()){
			edgeToReceiverNode = senderNode.getEdgeWeightToParent();
		}else{
			edgeToReceiverNode = receiverNode.getEdgeWeightToParent();
		}
		
		Collections.sort(senderNode.getLamdas(), new MessageDataComparator());
		List<MessageData> maximums = new ArrayList<MessageData>();
		for(MessageData data : senderNode.getLamdas()){
			if(data.getSender() != receiverNode){
				maximums.add(data);
			}
			if(maximums.size() == 2){
				break;
			}
		}
		
		MessageData biggestMsgData = new MessageData_normal();
		MessageData secBiggestMsgData = new MessageData_normal();
		if(maximums.size() >= 1){
			biggestMsgData = maximums.get(0);
		}
		if(maximums.size() >= 2){
			secBiggestMsgData = maximums.get(1);
		}
		
		CIMAEdgeWeight max1 = calcSortedEdgeWeightList(senderNode, receiverNode).get(0);
		CIMAEdgeWeight max2 = calcSortedEdgeWeightList(senderNode, receiverNode).get(1);
		CIMAEdgeWeight max3 = calcSortedEdgeWeightList(senderNode, receiverNode).get(2);


		return calcBestMsgDataValueWithPotential(senderNode, receiverNode, max1, max2, max3, biggestMsgData, secBiggestMsgData, edgeToReceiverNode, potential);
	}
	
	@Override
	public String toString() {
		return "\"Modifikation\" (Potential \u2265 1)";
	}

	@Override
	public int calcGeneralVerticeWeight(CIMAVertice vertice){
		return calcSortedEdgeWeightList(vertice, null).get(0).getValue();
	}
	
	@Override
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

	@Override
	public int calcMu(CIMAVertice vertice, int potential){
		int mu;
		List<MessageData> lamdas = vertice.getLamdas();
		Collections.sort(lamdas, new MessageDataComparator());
		MessageData_complexPotential maxMsgData = new MessageData_complexPotential();
		MessageData_complexPotential max2MsgData = new MessageData_complexPotential();
		if(lamdas.size() >= 1){
			if(lamdas.get(0) instanceof MessageData_complexPotential){
				maxMsgData = (MessageData_complexPotential)lamdas.get(0);
			}
		}
		if(lamdas.size() >= 2){
			if(lamdas.get(0) instanceof MessageData_complexPotential){
				max2MsgData = (MessageData_complexPotential)lamdas.get(1);
			}
		}
		
		List<CIMAEdgeWeight> edgeWeightList = calcSortedEdgeWeightList(vertice, null);
		CIMAEdgeWeight max1 = edgeWeightList.get(0);
		CIMAEdgeWeight max2 = edgeWeightList.get(1);
		CIMAEdgeWeight max3 = edgeWeightList.get(2);

		int bestMuResult;
		
		//calc normal MU		
		mu = calcBestMuValue(max1.getValue(), max2.getValue(), maxMsgData.getValue());
		bestMuResult = mu;
		
		
		//get the bestMuResult (with using the potential)
		//check if you can use the potential to minimize the msgDataValue
		int reducedValue = 1;
		int possibleMu = 1;
		ArrayList<CIMAEdgeWeight> potentialEdges = new ArrayList<CIMAEdgeWeight>();
		

		//case2: reduce the max1
		reducedValue = max1.getValue() - potential;
		if(reducedValue < 1){
			reducedValue = 1;
		}
		if(reducedValue < max3.getValue()){
			reducedValue = max3.getValue();
		}
		possibleMu = calcBestMuValue(reducedValue, max2.getValue(), maxMsgData.getValue());
		if(possibleMu <= bestMuResult){
			if(possibleMu < bestMuResult){
				potentialEdges.clear();
			}
			bestMuResult = possibleMu;
			potentialEdges.add(max1);
		}
		
		//case3: reduce the max2
		reducedValue = max2.getValue() - potential;
		if(reducedValue < 1){
			reducedValue = 1;
		}
		if(reducedValue < max3.getValue()){
			reducedValue = max3.getValue();
		}
		possibleMu = calcBestMuValue(max1.getValue(), reducedValue, maxMsgData.getValue());
		if(possibleMu <= bestMuResult){
			if(possibleMu < bestMuResult){
				potentialEdges.clear();
			}
			bestMuResult = possibleMu;
			potentialEdges.add(max2);
		}
		
		//case4: reduce the maxMsgData
		boolean edge1Case = true;
		reducedValue = maxMsgData.getBestPossiblelamdaValue();
		if(reducedValue < 1){
			reducedValue = 1;
		}
		if(reducedValue < max2MsgData.getValue()){
			reducedValue = max2MsgData.getValue();
			edge1Case = false;
		}
		possibleMu = calcBestMuValue(max1.getValue(), max2.getValue(), reducedValue);
		if(possibleMu < bestMuResult){
			if(possibleMu < bestMuResult){
				potentialEdges.clear();
			}
			bestMuResult = possibleMu;
			for(CIMAEdgeWeight edge : maxMsgData.getPotentialEdges()){
				if(!potentialEdges.contains(edge)){
					potentialEdges.add(edge);
				}
			}
			
		}
		
		
		
		
		
		if(reducedValue < max2MsgData.getValue()){
			int reducedValueFromMax2MsgData = max2MsgData.getValue();
			
			
			if(maxMsgData.getPotentialEdges() != null){
				if(maxMsgData.getPotentialEdges().size() == 1 && maxMsgData.getPotentialEdges().get(0).equals(max1)){
					possibleMu = calcBestMuValue(reducedValue, max2.getValue(), reducedValueFromMax2MsgData);					
				}else if(maxMsgData.getPotentialEdges().size() == 1 && maxMsgData.getPotentialEdges().get(0).equals(max2)){
					possibleMu = calcBestMuValue(max1.getValue(), reducedValue, reducedValueFromMax2MsgData);
				}else{
					possibleMu = calcBestMuValue(max1.getValue(), max2.getValue(), reducedValueFromMax2MsgData);
				}
				if(possibleMu <= bestMuResult){
					if(possibleMu < bestMuResult){
						potentialEdges.clear();
					}
					bestMuResult = possibleMu;
					for(CIMAEdgeWeight edge : maxMsgData.getPotentialEdges()){
						if(!potentialEdges.contains(edge)){
							potentialEdges.add(edge);
						}
					}
				}
				
			}
			
			
			
		}else if(maxMsgData.getPotentialEdges() != null){
			if(maxMsgData.getPotentialEdges().size() == 1 && maxMsgData.getPotentialEdges().get(0).equals(max1)){
				possibleMu = calcBestMuValue(reducedValue, max2.getValue(), reducedValue);
			}else if(maxMsgData.getPotentialEdges().size() == 1 && maxMsgData.getPotentialEdges().get(0).equals(max2)){
				possibleMu = calcBestMuValue(max1.getValue(), reducedValue, reducedValue);
			}else{
				possibleMu = calcBestMuValue(max1.getValue(), max2.getValue(), reducedValue);
			}
			if(possibleMu <= bestMuResult){
				if(possibleMu < bestMuResult){
					potentialEdges.clear();
				}
				bestMuResult = possibleMu;
				for(CIMAEdgeWeight edge : maxMsgData.getPotentialEdges()){
					if(!potentialEdges.contains(edge)){
						potentialEdges.add(edge);
					}
				}
			}
		}
		
		//no improvement... potential cant be used
		if(bestMuResult >= mu){
			potentialEdges.clear();
		}

		
		//more return values:
		vertice.setBestMu(bestMuResult);
		vertice.setPotentialEdges(potentialEdges);
		
		return mu;
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
