package cima;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModellMinimalDanger implements ICalcStrategy{

	@Override
	public MessageData calcMessageData(CIMAVertice senderNode, CIMAVertice receiverNode) {
		
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
		
		CIMAEdgeWeight max1 = senderNode.calcMaxOrSecmaxEdge(receiverNode, true).get(0);
		CIMAEdgeWeight max2 = senderNode.calcMaxOrSecmaxEdge(receiverNode, false).get(1);
		CIMAEdgeWeight max3 = senderNode.calcMaxOrSecmaxEdge(receiverNode, false).get(2);
		
		MessageData calcMessageData;
		int specialVerticeWeight = senderNode.calcSpecialVerticeWeight(receiverNode);
		
		PotentialData potentialData;
		if(max1.getEdgeWeightValue() == max3.getEdgeWeightValue()){
			potentialData = new PotentialData(true);
		}else if(max1.getEdgeWeightValue() == max2.getEdgeWeightValue()){
			potentialData = new PotentialData(max1, max2);
		}else{
			potentialData = new PotentialData(max1);
		}
		
		if(max1.getLamdaValue() >= max2.getLamdaValue() + specialVerticeWeight){
			calcMessageData = new MessageData(max1.getLamdaValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, null, null, potentialData);
		}else{
			calcMessageData = new MessageData(max2.getLamdaValue() + specialVerticeWeight, senderNode, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, null, null, potentialData);
		}
		
		//make sure the lamdaValue is not less the biggest msgData
		if(biggestMsgData.getLamdaValue() >= calcMessageData.getLamdaValue()){
//			calcMessageData = biggestMsgData;
			
			if(biggestMsgData.getLamdaValue() == calcMessageData.getLamdaValue()){
				calcMessageData = new MessageData(biggestMsgData.getLamdaValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, biggestMsgData, null, new PotentialData(true));
			}else{
				calcMessageData = new MessageData(biggestMsgData.getLamdaValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, biggestMsgData, null, biggestMsgData.getPotentialData().getPotentialDataCopy());
			}
		}
		
		//make sure the lamdaValue is not less then the edgeWeight
		if(receiverNode == senderNode.getParent()){
			if(senderNode.getEdgeWeightToParent().getEdgeWeightValue() >= calcMessageData.getLamdaValue()){
				
				if(senderNode.getEdgeWeightToParent().getEdgeWeightValue() == calcMessageData.getLamdaValue()){
					calcMessageData = new MessageData(senderNode.getEdgeWeightToParent().getEdgeWeightValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, null, senderNode.getEdgeWeightToParent(), new PotentialData(true));
				}else{
					calcMessageData = new MessageData(senderNode.getEdgeWeightToParent().getEdgeWeightValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, null, senderNode.getEdgeWeightToParent(), new PotentialData(senderNode.getEdgeWeightToParent()));
				}
			}
		}else{
			if(receiverNode.getEdgeWeightToParent().getEdgeWeightValue() >= calcMessageData.getLamdaValue()){
								
				if(receiverNode.getEdgeWeightToParent().getEdgeWeightValue() == calcMessageData.getLamdaValue()){
					calcMessageData = new MessageData(receiverNode.getEdgeWeightToParent().getEdgeWeightValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, null, receiverNode.getEdgeWeightToParent(), new PotentialData(true));
				}else{
					calcMessageData = new MessageData(receiverNode.getEdgeWeightToParent().getEdgeWeightValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, null, receiverNode.getEdgeWeightToParent(), new PotentialData(receiverNode.getEdgeWeightToParent()));
				}
			}
		}
		
		return calcMessageData;
	}
	
	

}
