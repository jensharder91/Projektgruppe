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
		System.out.println("case a: Sender: "+sender.getName()+" --> Receiver: "+receiver.getName());
		
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
			System.out.println("case b_1");
			reducedValue = 1;
		}
		possibleMsgData = calcMsgDataValue(max1.getValue(), max2.getValue(), maxMsgData.getValue(), reducedValue);
		if(possibleMsgData <= bestMsgDataValue){
			System.out.println("case b_2");
//			System.out.println(possibleMsgData +" / "+bestMsgDataValue +" / "+maxMsgData.getLamdaValue());
			bestMsgDataValue = possibleMsgData;
			newMessageData.updateMessageData(bestMsgDataValue, edgeValue);
		}
		
		//case2: reduce the max1
		reducedValue = max1.getValue() - potential;
		if(reducedValue < 1){
			System.out.println("case c_1");
			reducedValue = 1;
		}
		if(reducedValue < max3.getValue()){
			reducedValue = max3.getValue();
			System.out.println("case c_2");
		}
		possibleMsgData = calcMsgDataValue(reducedValue, max2.getValue(), maxMsgData.getValue(), edgeValue.getValue());
		if(possibleMsgData <= bestMsgDataValue){
//			System.out.println(possibleMsgData +" / "+bestMsgDataValue +" / "+maxMsgData.getLamdaValue());
			System.out.println("case c_3");
			bestMsgDataValue = possibleMsgData;
			newMessageData.updateMessageData(bestMsgDataValue, max1);
		}
		
		//case3: reduce the max2
		reducedValue = max2.getValue() - potential;
		if(reducedValue < 1){
			System.out.println("case d_1");
			reducedValue = 1;
		}
		if(reducedValue < max3.getValue()){
			reducedValue = max3.getValue();
			System.out.println("case d_2");
		}
		possibleMsgData = calcMsgDataValue(max1.getValue(), reducedValue, maxMsgData.getValue(), edgeValue.getValue());
		if(possibleMsgData <= bestMsgDataValue){
			System.out.println("case d_3");
			bestMsgDataValue = possibleMsgData;
			newMessageData.updateMessageData(bestMsgDataValue, max2);
		}
		
		//case4: reduce the maxMsgData
		System.out.println("case e");
		reducedValue = maxMsgData.getBestPossiblelamdaValue();
		if(reducedValue < 1){
			System.out.println("case e_1");
			reducedValue = 1;
		}
		
		
		if(reducedValue < max2MsgData.getValue()){
			System.out.println("case e_1_b");
			int reducedValueFromMax2MsgData = max2MsgData.getValue();
			
			
			if(reducedValue < max3.getValue()){
				reducedValue = max3.getValue();
				System.out.println("case e_1_b2");
			}
			
			if(maxMsgData.getPotentialEdges() != null){
				System.out.println("case e_2b");
				if(maxMsgData.getPotentialEdges().size() == 1 && maxMsgData.getPotentialEdges().get(0).equals(max1)){
					System.out.println("case e_3b");
					possibleMsgData = calcMsgDataValue(reducedValue, max2.getValue(), reducedValueFromMax2MsgData, edgeValue.getValue());					
				}else if(maxMsgData.getPotentialEdges().size() == 1 && maxMsgData.getPotentialEdges().get(0).equals(max2)){
					System.out.println("case e_4b");
					possibleMsgData = calcMsgDataValue(max1.getValue(), reducedValue, reducedValueFromMax2MsgData, edgeValue.getValue());
				}else{
					System.out.println("case e_5b");
					possibleMsgData = calcMsgDataValue(max1.getValue(), max2.getValue(), reducedValueFromMax2MsgData, edgeValue.getValue());
				}
				if(possibleMsgData <= bestMsgDataValue){
					System.out.println("case e_6b");
					bestMsgDataValue = possibleMsgData;
					newMessageData.updateMessageData(bestMsgDataValue, maxMsgData.getPotentialEdges());
				}
				
			}
			
			
			
		}else if(maxMsgData.getPotentialEdges() != null){
			System.out.println("case e_2c");
			if(maxMsgData.getPotentialEdges().size() == 1 && maxMsgData.getPotentialEdges().get(0).equals(max1)){
				System.out.println("case e_3c");
				possibleMsgData = calcMsgDataValue(reducedValue, max2.getValue(), reducedValue, edgeValue.getValue());
			}else if(maxMsgData.getPotentialEdges().size() == 1 && maxMsgData.getPotentialEdges().get(0).equals(max2)){
				System.out.println("case e_4c");
				possibleMsgData = calcMsgDataValue(max1.getValue(), reducedValue, reducedValue, edgeValue.getValue());
			}else{
				System.out.println("case e_5c");
				possibleMsgData = calcMsgDataValue(max1.getValue(), max2.getValue(), reducedValue, edgeValue.getValue());
			}
			if(possibleMsgData <= bestMsgDataValue){
				System.out.println("case e_6c");
				bestMsgDataValue = possibleMsgData;
				newMessageData.updateMessageData(bestMsgDataValue, maxMsgData.getPotentialEdges());
			}
		}
		
		//return the bestMsgDataValue
		
		System.out.println("case return: message from "+newMessageData.getSender().getName()+" to "+newMessageData.getReceiver().getName() + " //  data: "+newMessageData.getValue()+" / optimal data: "+newMessageData.getBestPossiblelamdaValue() + " / edges: "+newMessageData.getPotentialEdges());
		
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
//		Collections.sort(edgeWeightList);
		CIMAEdgeWeight max1 = edgeWeightList.get(0);
		CIMAEdgeWeight max2 = edgeWeightList.get(1);
		CIMAEdgeWeight max3 = edgeWeightList.get(2);
		
//		CIMAEdgeWeight max1 = calcMaxOrSecmaxEdge(null, true);
//		CIMAEdgeWeight max2 = calcMaxOrSecmaxEdge(null, false);
		
//		System.out.println("Vertice : "+vertice.getName());
//		
//		System.out.println("max1: "+ max1.getEdgeWeightValue());
//		System.out.println("max2: "+ max2.getEdgeWeightValue());
//		System.out.println("max3: "+ max3.getEdgeWeightValue());
//		System.out.println("maxMsgData: "+ maxMsgData.getLamdaValue());
//		System.out.println("max2MsgData: "+ max2MsgData.getLamdaValue());

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
			System.out.println("case c_1");
			reducedValue = 1;
		}
		if(reducedValue < max3.getValue()){
			reducedValue = max3.getValue();
			System.out.println("case c_2");
		}
		possibleMu = calcBestMuValue(reducedValue, max2.getValue(), maxMsgData.getValue());
		if(possibleMu <= bestMuResult){
			if(possibleMu < bestMuResult){
				potentialEdges.clear();
			}
			System.out.println("case c_3");
			bestMuResult = possibleMu;
			potentialEdges.add(max1);
		}
		
		//case3: reduce the max2
		reducedValue = max2.getValue() - potential;
		if(reducedValue < 1){
			System.out.println("case d_1");
			reducedValue = 1;
		}
		if(reducedValue < max3.getValue()){
			reducedValue = max3.getValue();
			System.out.println("case d_2");
		}
		possibleMu = calcBestMuValue(max1.getValue(), reducedValue, maxMsgData.getValue());
		if(possibleMu <= bestMuResult){
			if(possibleMu < bestMuResult){
				potentialEdges.clear();
			}
			System.out.println("case d_3");
			bestMuResult = possibleMu;
			potentialEdges.add(max2);
		}
		
		//case4: reduce the maxMsgData
		boolean edge1Case = true;
		reducedValue = maxMsgData.getBestPossiblelamdaValue();
		if(reducedValue < 1){
			System.out.println("case e_1");
			reducedValue = 1;
		}
		if(reducedValue < max2MsgData.getValue()){
			System.out.println("case e_1_b");
			reducedValue = max2MsgData.getValue();
			edge1Case = false;
		}
		possibleMu = calcBestMuValue(max1.getValue(), max2.getValue(), reducedValue);
		if(possibleMu < bestMuResult){
			if(possibleMu < bestMuResult){
				potentialEdges.clear();
			}
			System.out.println("case e_2");
			bestMuResult = possibleMu;
//			if(edge1Case){//TODO
//				potentialEdges.addAll(maxMsgData.getPotentialEdges());
//			}else{
//				potentialEdges.addAll(max2MsgData.getPotentialEdges());
//			}
			for(CIMAEdgeWeight edge : maxMsgData.getPotentialEdges()){
				if(!potentialEdges.contains(edge)){
					potentialEdges.add(edge);
				}
			}
			
		}
		
		
		
		
		
		if(reducedValue < max2MsgData.getValue()){
			System.out.println("case e_1_b");
			int reducedValueFromMax2MsgData = max2MsgData.getValue();
			
			
			if(maxMsgData.getPotentialEdges() != null){
				System.out.println("case e_2b");
				if(maxMsgData.getPotentialEdges().size() == 1 && maxMsgData.getPotentialEdges().get(0).equals(max1)){
					System.out.println("case e_3b");
					possibleMu = calcBestMuValue(reducedValue, max2.getValue(), reducedValueFromMax2MsgData);					
				}else if(maxMsgData.getPotentialEdges().size() == 1 && maxMsgData.getPotentialEdges().get(0).equals(max2)){
					System.out.println("case e_4b");
					possibleMu = calcBestMuValue(max1.getValue(), reducedValue, reducedValueFromMax2MsgData);
				}else{
					System.out.println("case e_5b");
					possibleMu = calcBestMuValue(max1.getValue(), max2.getValue(), reducedValueFromMax2MsgData);
				}
				if(possibleMu <= bestMuResult){
					System.out.println("case e_6b");
					if(possibleMu < bestMuResult){
						potentialEdges.clear();
					}
					bestMuResult = possibleMu;
					for(CIMAEdgeWeight edge : maxMsgData.getPotentialEdges()){
						if(!potentialEdges.contains(edge)){
							potentialEdges.add(edge);
						}
					}
//					potentialEdges.addAll(maxMsgData.getPotentialEdges());
				}
				
			}
			
			
			
		}else if(maxMsgData.getPotentialEdges() != null){
			System.out.println("case e_2c");
			if(maxMsgData.getPotentialEdges().size() == 1 && maxMsgData.getPotentialEdges().get(0).equals(max1)){
				System.out.println("case e_3c");
				possibleMu = calcBestMuValue(reducedValue, max2.getValue(), reducedValue);
			}else if(maxMsgData.getPotentialEdges().size() == 1 && maxMsgData.getPotentialEdges().get(0).equals(max2)){
				System.out.println("case e_4c");
				possibleMu = calcBestMuValue(max1.getValue(), reducedValue, reducedValue);
			}else{
				System.out.println("case e_5c");
				possibleMu = calcBestMuValue(max1.getValue(), max2.getValue(), reducedValue);
			}
			if(possibleMu <= bestMuResult){
				System.out.println("case e_6c");
				if(possibleMu < bestMuResult){
					potentialEdges.clear();
				}
				bestMuResult = possibleMu;
				for(CIMAEdgeWeight edge : maxMsgData.getPotentialEdges()){
					if(!potentialEdges.contains(edge)){
						potentialEdges.add(edge);
					}
				}
//				potentialEdges.addAll(maxMsgData.getPotentialEdges());
			}
		}
		
		
		
//		if(maxMsgData.getPotentialEdges() != null && maxMsgData.getPotentialEdges().size() == 1){
//			System.out.println("case e_2");
//			if(maxMsgData.getPotentialEdges().get(0).equals(max1)){
//				System.out.println("case e_3");
//				possibleMu = calcBestMuValue(reducedValue, max2.getEdgeWeightValue(), reducedValue);
//			}else if(maxMsgData.getPotentialEdges().get(0).equals(max2)){
//				System.out.println("case e_4");
//				possibleMu = calcBestMuValue(max1.getEdgeWeightValue(), reducedValue, reducedValue);
//			}else{
//				System.out.println("case e_5");
//				possibleMu = calcBestMuValue(max1.getEdgeWeightValue(), max2.getEdgeWeightValue(), reducedValue);
//			}
//			if(possibleMu < bestMuResult){
//				System.out.println("case e_6");
//				bestMuResult = possibleMu;
//			}
//		}
		
		//no improvement... potential cant be used
		if(bestMuResult >= mu){
			potentialEdges.clear();
		}
		
		
		System.out.println("##VERTICE: "+vertice.getName()+"  // mu: "+mu+ "  bestMu: "+bestMuResult+"  edges: "+potentialEdges.toString());
		
		
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
		
//		System.out.println("bestPossibleLamdaValue vom ganzen Baum: "+bestPossibleLamdaValue + "  potentialEdges:  "+potentialEdges.toString());
		
		if(ICalcStrategy.showPotential){
			if(potentialEdges.size() > 0){
				InfoDisplayClass.getInfoDisplayClass().displayInUpperRightCorner(g2, "Agentenzahl kann auf  >>"+bestPossibleLamdaValue+"<<  reduziert werde", 1, Color.black, null);
				for(CIMAEdgeWeight edge : potentialEdges){
					edge.setOvalColor(Color.RED);
					edge.draw(g2);
				}
			}else{
				InfoDisplayClass.getInfoDisplayClass().displayInUpperRightCorner(g2, "Agentenzahl kann  >>nicht<<  reduziert werde", 1, Color.black, null);
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
			
			for(CIMAEdgeWeight weight : vertice.getPotentialEdges()){
				if(!potentialEdges.contains(weight)){
					potentialEdges.add(weight);
				}
			}
//			potentialEdges.addAll(vertice.getPotentialEdges());
		}
		
		for(Vertice childVertice : vertice.getChildren()){
			calcBestPossibleLamdaValue(((CIMAVertice)childVertice));
		}
	}

}
