package cima;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModelMultPotential extends ICalcStrategy{
	
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
	
	
	public static  MessageData calcBestMsgDataValueWithPotential(CIMAVertice sender, CIMAVertice receiver, CIMAEdgeWeight max1, CIMAEdgeWeight max2, CIMAEdgeWeight max3, MessageData maxMsgData_notTested, MessageData max2MsgData_notTested, CIMAEdgeWeight edgeValue, int potential){

		MessageData_complexPotential maxMsgData = new MessageData_complexPotential();
		MessageData_complexPotential max2MsgData = new MessageData_complexPotential();
		
		if(maxMsgData_notTested instanceof MessageData_complexPotential){
			maxMsgData = (MessageData_complexPotential) maxMsgData_notTested;
		}
		if(max2MsgData_notTested instanceof MessageData_complexPotential){
			max2MsgData = (MessageData_complexPotential) max2MsgData_notTested;
		}
		System.out.println("case a: Sender: "+sender.getName()+" --> Receiver: "+receiver.getName());
		
		//normal value without potential
		int normalMsgDataValue = calcMsgDataValue(max1.getEdgeWeightValue(), max2.getEdgeWeightValue(), maxMsgData.getLamdaValue(), edgeValue.getEdgeWeightValue());
		int bestMsgDataValue = normalMsgDataValue;
		MessageData_complexPotential newMessageData = new MessageData_complexPotential(sender, receiver, normalMsgDataValue, -1);
		
		//check if you can use the potential to minimize the msgDataValue
		int reducedValue = 1;
		int possibleMsgData = 1;
		
		//case1: reduce the edgeValue
		reducedValue = edgeValue.getEdgeWeightValue() - potential;
		if(reducedValue < 1){
			System.out.println("case b_1");
			reducedValue = 1;
		}
		possibleMsgData = calcMsgDataValue(max1.getEdgeWeightValue(), max2.getEdgeWeightValue(), maxMsgData.getLamdaValue(), reducedValue);
		if(possibleMsgData <= bestMsgDataValue){
			System.out.println("case b_2");
			bestMsgDataValue = possibleMsgData;
			newMessageData.updateMessageData(bestMsgDataValue, edgeValue);
		}
		
		//case2: reduce the max1
		reducedValue = max1.getEdgeWeightValue() - potential;
		if(reducedValue < 1){
			System.out.println("case c_1");
			reducedValue = 1;
		}
		if(reducedValue < max3.getEdgeWeightValue()){
			reducedValue = max3.getEdgeWeightValue();
			System.out.println("case c_2");
		}
		possibleMsgData = calcMsgDataValue(reducedValue, max2.getEdgeWeightValue(), maxMsgData.getLamdaValue(), edgeValue.getEdgeWeightValue());
		if(possibleMsgData <= bestMsgDataValue){
			System.out.println("case c_3");
			bestMsgDataValue = possibleMsgData;
			newMessageData.updateMessageData(bestMsgDataValue, max1);
		}
		
		//case3: reduce the max2
		reducedValue = max2.getEdgeWeightValue() - potential;
		if(reducedValue < 1){
			System.out.println("case d_1");
			reducedValue = 1;
		}
		if(reducedValue < max3.getEdgeWeightValue()){
			reducedValue = max3.getEdgeWeightValue();
			System.out.println("case d_2");
		}
		possibleMsgData = calcMsgDataValue(max1.getEdgeWeightValue(), reducedValue, maxMsgData.getLamdaValue(), edgeValue.getEdgeWeightValue());
		if(possibleMsgData <= bestMsgDataValue){
			System.out.println("case d_3");
			bestMsgDataValue = possibleMsgData;
			newMessageData.updateMessageData(bestMsgDataValue, max2);
		}
		
		//case4: reduce the maxMsgData
		reducedValue = maxMsgData.getBestPossiblelamdaValue();
		if(reducedValue < 1){
			System.out.println("case e_1");
			reducedValue = 1;
		}
		if(reducedValue < max2MsgData.getLamdaValue()){
			System.out.println("case e_1_b");
			reducedValue = max2MsgData.getLamdaValue();
		}
		if(maxMsgData.getPotentialEdges() != null && maxMsgData.getPotentialEdges().size() == 1){
			System.out.println("case e_2");
			if(maxMsgData.getPotentialEdges().get(0).equals(max1)){
				System.out.println("case e_3");
				possibleMsgData = calcMsgDataValue(reducedValue, max2.getEdgeWeightValue(), reducedValue, edgeValue.getEdgeWeightValue());
			}else if(maxMsgData.getPotentialEdges().get(0).equals(max2)){
				System.out.println("case e_4");
				possibleMsgData = calcMsgDataValue(max1.getEdgeWeightValue(), reducedValue, reducedValue, edgeValue.getEdgeWeightValue());
			}else{
				System.out.println("case e_5");
				possibleMsgData = calcMsgDataValue(max1.getEdgeWeightValue(), max2.getEdgeWeightValue(), reducedValue, edgeValue.getEdgeWeightValue());
			}
			if(possibleMsgData <= bestMsgDataValue){
				System.out.println("case e_6");
				bestMsgDataValue = possibleMsgData;
				newMessageData.updateMessageData(bestMsgDataValue, maxMsgData.getPotentialEdges().get(0));
			}
		}
		
		//return the bestMsgDataValue
		
		System.out.println("case return: message from "+newMessageData.getSender().getName()+" to "+newMessageData.getReceiver().getName() + " //  data: "+newMessageData.getLamdaValue()+" / optimal data: "+newMessageData.getBestPossiblelamdaValue());
		
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
		
		MessageData biggestMsgData = new MessageData();
		MessageData secBiggestMsgData = new MessageData();
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
	

	public static int calcMu(ICalcStrategy strategy, CIMAVertice vertice, CIMAEdgeWeight max1, CIMAEdgeWeight max2, CIMAEdgeWeight max3, MessageData maxMsgData_notTested, MessageData max2MsgData_notTested, int potential) {
		
		MessageData_complexPotential maxMsgData = new MessageData_complexPotential();
		MessageData_complexPotential max2MsgData = new MessageData_complexPotential();
		
		if(maxMsgData_notTested instanceof MessageData_complexPotential){
			maxMsgData = (MessageData_complexPotential) maxMsgData_notTested;
		}
		if(max2MsgData_notTested instanceof MessageData_complexPotential){
			max2MsgData = (MessageData_complexPotential) max2MsgData_notTested;
		}
		
		
		int mu;
		int bestMuResult;
		CIMAEdgeWeight potentialEdge = null;
		
		//calc normal MU		
		mu = calcBestMuValue(max1.getEdgeWeightValue(), max2.getEdgeWeightValue(), maxMsgData.getLamdaValue());
		bestMuResult = mu;
		
		
		//get the bestMuResult (with using the potential)
		//check if you can use the potential to minimize the msgDataValue
		int reducedValue = 1;
		int possibleMu = 1;
		

		//case2: reduce the max1
		reducedValue = max1.getEdgeWeightValue() - potential;
		if(reducedValue < 1){
			System.out.println("case c_1");
			reducedValue = 1;
		}
		if(reducedValue < max3.getEdgeWeightValue()){
			reducedValue = max3.getEdgeWeightValue();
			System.out.println("case c_2");
		}
		possibleMu = calcBestMuValue(reducedValue, max2.getEdgeWeightValue(), maxMsgData.getLamdaValue());
		if(possibleMu < bestMuResult){
			System.out.println("case c_3");
			bestMuResult = possibleMu;
			potentialEdge = max1;
		}
		
		//case3: reduce the max2
		reducedValue = max2.getEdgeWeightValue() - potential;
		if(reducedValue < 1){
			System.out.println("case d_1");
			reducedValue = 1;
		}
		if(reducedValue < max3.getEdgeWeightValue()){
			reducedValue = max3.getEdgeWeightValue();
			System.out.println("case d_2");
		}
		possibleMu = calcBestMuValue(max1.getEdgeWeightValue(), reducedValue, maxMsgData.getLamdaValue());
		if(possibleMu < bestMuResult){
			System.out.println("case d_3");
			bestMuResult = possibleMu;
			potentialEdge = max2;
		}
		
		//case4: reduce the maxMsgData
		reducedValue = maxMsgData.getBestPossiblelamdaValue();
		if(reducedValue < 1){
			System.out.println("case e_1");
			reducedValue = 1;
		}
		if(reducedValue < max2MsgData.getLamdaValue()){
			System.out.println("case e_1_b");
			reducedValue = max2MsgData.getLamdaValue();
		}
		if(maxMsgData.getPotentialEdges() != null && maxMsgData.getPotentialEdges().size() == 1){
			System.out.println("case e_2");
			if(maxMsgData.getPotentialEdges().get(0).equals(max1)){
				System.out.println("case e_3");
				possibleMu = calcBestMuValue(reducedValue, max2.getEdgeWeightValue(), reducedValue);
			}else if(maxMsgData.getPotentialEdges().get(0).equals(max2)){
				System.out.println("case e_4");
				possibleMu = calcBestMuValue(max1.getEdgeWeightValue(), reducedValue, reducedValue);
			}else{
				System.out.println("case e_5");
				possibleMu = calcBestMuValue(max1.getEdgeWeightValue(), max2.getEdgeWeightValue(), reducedValue);
			}
			if(possibleMu < bestMuResult){
				System.out.println("case e_6");
				bestMuResult = possibleMu;
			}
		}
		
		return mu;
	}
	
	@Override
	public String toString() {
		return "\"Minimale Bedrohung\" (potantial >= 1)";
	}

	@Override
	public int calcGeneralVerticeWeight(CIMAVertice vertice){
		return calcSortedEdgeWeightList(vertice, null).get(0).getEdgeWeightValue();
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
//		Collections.sort(edgeWeightList);
		CIMAEdgeWeight max1 = edgeWeightList.get(0);
		CIMAEdgeWeight max2 = edgeWeightList.get(1);
		CIMAEdgeWeight max3 = edgeWeightList.get(2);
		
//		CIMAEdgeWeight max1 = calcMaxOrSecmaxEdge(null, true);
//		CIMAEdgeWeight max2 = calcMaxOrSecmaxEdge(null, false);
		
		System.out.println("Vertice : "+vertice.getName());
		
		System.out.println("max1: "+ max1.getEdgeWeightValue());
		System.out.println("max2: "+ max2.getEdgeWeightValue());
		System.out.println("max3: "+ max3.getEdgeWeightValue());
	

//		int verticeWeight = calcGeneralVerticeWeight(vertice);
		PotentialData potentialData;

		//default case max1 + max2
		if(max1.getEdgeWeightValue() == max3.getEdgeWeightValue()){
			potentialData = new PotentialData(vertice, true);
			System.out.println("case a");
		}else if(max2.getEdgeWeightValue() == 0 || max2.getEdgeWeightValue() == max3.getEdgeWeightValue()){
			potentialData = new PotentialData(vertice, max1);
			System.out.println("case b");
		}else{
			potentialData = new PotentialData(vertice, max1, max2);
			System.out.println("case c");
		}
		mu = max1.getEdgeWeightValue() + max2.getEdgeWeightValue();

		//make sure mu is not less the biggest msgData
		if(mu <=  biggestMsgData.getLamdaValue()){
			
			if(mu == biggestMsgData.getLamdaValue()){
				
				System.out.println("show edges:");
				System.out.println("potentialData: "+ potentialData.getPotentialEdgeWeights());
				System.out.println("biggest.potential: "+biggestMsgData.getPotentialData().getPotentialEdgeWeights());
				
				//check if both potentialData has same number of edges
				if(potentialData.getNumberOfpotentialEdgeWeights() == biggestMsgData.getPotentialData().getNumberOfpotentialEdgeWeights()){
					for(CIMAEdgeWeight edge : biggestMsgData.getPotentialData().getPotentialEdgeWeights()){
						if(potentialData.hasSameEdge(edge)){
							//potential data shouldnt change
							System.out.println("case d_1");
						}else{
							potentialData = new PotentialData(vertice, true);
							System.out.println("case d_2");
						}
					}
				}else{
					potentialData = new PotentialData(vertice, true);
					System.out.println("case d_3");
				}
				
				
			}
			else{
				potentialData = biggestMsgData.getPotentialData().getPotentialDataCopy(vertice);
				System.out.println("case e");
				System.out.println(biggestMsgData.getPotentialData());
				System.out.println(potentialData);
			}
						
			mu = biggestMsgData.getLamdaValue();
		}
		
		return mu;
	}

}
