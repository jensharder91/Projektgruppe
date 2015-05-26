package cima;

import cima.ICalcStrategy.MuCalcResult;

public class ModelMultPotential /*extends ModellMinimalDanger*/{
	
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
	
	
	public static  MessageData calcBestMsgDataValueWithPotential(CIMAVertice sender, CIMAVertice receiver, CIMAEdgeWeight max1, CIMAEdgeWeight max2, CIMAEdgeWeight max3, MessageData maxMsgData, MessageData max2MsgData, CIMAEdgeWeight edgeValue, int potential){

		
		System.out.println("case a: Sender: "+sender.getName()+" --> Receiver: "+receiver.getName());
		
		//normal value without potential
		int normalMsgDataValue = calcMsgDataValue(max1.getEdgeWeightValue(), max2.getEdgeWeightValue(), maxMsgData.getLamdaValue(), edgeValue.getEdgeWeightValue());
		int bestMsgDataValue = normalMsgDataValue;
		MessageData newMessageData = new MessageData(normalMsgDataValue,sender, receiver, bestMsgDataValue, edgeValue);
		
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
		if(possibleMsgData < bestMsgDataValue){
			System.out.println("case b_2");
			bestMsgDataValue = possibleMsgData;
			newMessageData = new MessageData(normalMsgDataValue, sender, receiver, bestMsgDataValue, edgeValue);
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
		if(possibleMsgData < bestMsgDataValue){
			System.out.println("case c_3");
			bestMsgDataValue = possibleMsgData;
			newMessageData = new MessageData(normalMsgDataValue, sender, receiver, bestMsgDataValue, max1);
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
		if(possibleMsgData < bestMsgDataValue){
			System.out.println("case d_3");
			bestMsgDataValue = possibleMsgData;
			newMessageData = new MessageData(normalMsgDataValue, sender, receiver, bestMsgDataValue, max2);
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
		if(maxMsgData.getPotentialEdge() != null){
			System.out.println("case e_2");
			if(maxMsgData.getPotentialEdge().equals(max1)){
				System.out.println("case e_3");
				possibleMsgData = calcMsgDataValue(reducedValue, max2.getEdgeWeightValue(), reducedValue, edgeValue.getEdgeWeightValue());
			}else if(maxMsgData.getPotentialEdge().equals(max2)){
				System.out.println("case e_4");
				possibleMsgData = calcMsgDataValue(max1.getEdgeWeightValue(), reducedValue, reducedValue, edgeValue.getEdgeWeightValue());
			}else{
				System.out.println("case e_5");
				possibleMsgData = calcMsgDataValue(max1.getEdgeWeightValue(), max2.getEdgeWeightValue(), reducedValue, edgeValue.getEdgeWeightValue());
			}
			if(possibleMsgData < bestMsgDataValue){
				System.out.println("case e_6");
				bestMsgDataValue = possibleMsgData;
				newMessageData = new MessageData(normalMsgDataValue, sender, receiver, bestMsgDataValue, maxMsgData.getPotentialEdge());
			}
		}
		
		//return the bestMsgDataValue
		
		System.out.println("case return: message from "+newMessageData.getSender().getName()+" to "+newMessageData.getReceiver().getName() + " //  data: "+newMessageData.getLamdaValue()+" / optimal data: "+newMessageData.getBestPossiblelamdaValue());
		
		return newMessageData;
	}

	
/*	
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
		if(maximums.size() >= 1){
			biggestMsgData = maximums.get(0);
		}
		
		CIMAEdgeWeight max1 = calcSortedEdgeWeightList(senderNode, receiverNode).get(0);
		CIMAEdgeWeight max2 = calcSortedEdgeWeightList(senderNode, receiverNode).get(1);
		CIMAEdgeWeight max3 = calcSortedEdgeWeightList(senderNode, receiverNode).get(2);


		return calcBestMsgDataValueWithPotential(senderNode, receiverNode, max1, max2, max3, biggestMsgData, edgeToReceiverNode, potential);
	}
	*/

	public static MuCalcResult calcMu(ICalcStrategy strategy, CIMAVertice vertice, CIMAEdgeWeight max1, CIMAEdgeWeight max2, CIMAEdgeWeight max3, MessageData maxMsgData, MessageData max2MsgData, int potential) {
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
		if(maxMsgData.getPotentialEdge() != null){
			System.out.println("case e_2");
			if(maxMsgData.getPotentialEdge().equals(max1)){
				System.out.println("case e_3");
				possibleMu = calcBestMuValue(reducedValue, max2.getEdgeWeightValue(), reducedValue);
			}else if(maxMsgData.getPotentialEdge().equals(max2)){
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
		
		return strategy.new MuCalcResult(mu,  bestMuResult, potentialEdge);
	}
	
	@Override
	public String toString() {
		return "\"Minimale Bedrohung\" (potantial >= 1)";
	}

}
