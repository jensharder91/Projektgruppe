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
		
		System.out.println("start calc mesgData...."+senderNode.getName()+" > "+receiverNode.getName());
		
		
		//
	
		
		
		/*
		 * calc messageData and potentialDta
		 */
		
		MessageData calculatedMessageData;
		PotentialData calculatedPotentialData = null;

		//default case max1 + max2
		if(max1.getEdgeWeightValue() == max3.getEdgeWeightValue()){
			calculatedPotentialData = new PotentialData(true);
		}else if(max2.getEdgeWeightValue() == 0){
			calculatedPotentialData = new PotentialData(max1);
		}else{
			calculatedPotentialData = new PotentialData(max1, max2);
		}
		calculatedMessageData = new MessageData(max1.getLamdaValue() + max2.getLamdaValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, max2.getLamdaValue(), null, null, calculatedPotentialData);
		
		//make sure it is not smaller then the edge
		if(calculatedMessageData.getLamdaValue() <= edgeToReceiverNode.getLamdaValue()){
//			calculatedMessageData = new MessageData(edgeToReceiverNode.getLamdaValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, max2.getLamdaValue(), null, edgeToReceiverNode, calculatedPotentialData);
			
			//
			
			if(calculatedMessageData.getLamdaValue() == edgeToReceiverNode.getLamdaValue()){
				
				//
				if(calculatedMessageData.getPotentialData().getNumberOfpotentialEdgeWeights() == 1){
					
					if(calculatedMessageData.getPotentialData().getPotentialEdgeWeights().get(0).equals(edgeToReceiverNode)){
						//potential data shouldnt change
//						calcMessageData = new MessageData(senderNode.getEdgeWeightToParent().getEdgeWeightValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, null, senderNode.getEdgeWeightToParent(), new PotentialData(senderNode.getEdgeWeightToParent()));
					}else{
						calculatedMessageData = new MessageData(edgeToReceiverNode.getEdgeWeightValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, max2.getLamdaValue(), null, edgeToReceiverNode, new PotentialData(true));
					}
					
				}
				//
				
				
			}else{
				calculatedMessageData = new MessageData(edgeToReceiverNode.getEdgeWeightValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, max2.getLamdaValue(), null, edgeToReceiverNode, new PotentialData(edgeToReceiverNode));								
			}
			
			//
		}
		
		//make sure it is not smaller then the biggest messageData
		if(calculatedMessageData.getLamdaValue() <= biggestMsgData.getLamdaValue()){
//			calculatedMessageData = new MessageData(biggestMsgData.getLamdaValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, max2.getLamdaValue(), biggestMsgData, null, calculatedPotentialData);
			
			//
			
			if(calculatedMessageData.getLamdaValue() == biggestMsgData.getLamdaValue()){
				
				//check if both potentialData has same number of edges
				if(calculatedMessageData.getPotentialData().getNumberOfpotentialEdgeWeights() == biggestMsgData.getPotentialData().getNumberOfpotentialEdgeWeights()){
					for(CIMAEdgeWeight edge : biggestMsgData.getPotentialData().getPotentialEdgeWeights()){
						if(calculatedMessageData.getPotentialData().hasSameEdge(edge)){
							//potential data shouldnt change
						}else{
							calculatedMessageData = new MessageData(biggestMsgData.getLamdaValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, max2.getLamdaValue(), biggestMsgData, null, new PotentialData(true));
						}
					}
				}else{
					calculatedMessageData = new MessageData(biggestMsgData.getLamdaValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, max2.getLamdaValue(), biggestMsgData, null, new PotentialData(true));
				}
				
				
			}else{
				calculatedMessageData = new MessageData(biggestMsgData.getLamdaValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, max2.getLamdaValue(), biggestMsgData, null, biggestMsgData.getPotentialData().getPotentialDataCopy());								
			}
			
			//
			

		}
		
		//
		
		
//		
//		MessageData calcMessageData;
//		int specialVerticeWeight = senderNode.calcSpecialVerticeWeight(receiverNode);
//		
//		PotentialData potentialData;
//		if(max1.getEdgeWeightValue() == max3.getEdgeWeightValue()){
//			System.out.println("case 1");
//			potentialData = new PotentialData(true);
//		}else if(max1.getEdgeWeightValue() == max2.getEdgeWeightValue()){
//			System.out.println("case 2");
//			potentialData = new PotentialData(max1, max2);
//		}else{
//			System.out.println("case 3");
//			potentialData = new PotentialData(max1);
//		}
//		
//		if(max1.getLamdaValue() > max2.getLamdaValue() + specialVerticeWeight){
//			System.out.println("case 123 a");
//			calcMessageData = new MessageData(max1.getLamdaValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, null, null, potentialData);
//		}else{
//			System.out.println("case 123 b");
//			calcMessageData = new MessageData(max2.getLamdaValue() + specialVerticeWeight, senderNode, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, null, null, new PotentialData(max1, max2));
//		}
//		
//		//make sure the lamdaValue is not less the biggest msgData
//		if(biggestMsgData.getLamdaValue() >= calcMessageData.getLamdaValue()){
//			System.out.println("case 4");
////			calcMessageData = biggestMsgData;
//			
//			if(biggestMsgData.getLamdaValue() == calcMessageData.getLamdaValue()){
//				System.out.println("case 4a");
////				calcMessageData = new MessageData(biggestMsgData.getLamdaValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, biggestMsgData, null, new PotentialData(true));
//				
//					///
//					
//					//check if both potentialData has same number of edges
//					if(calcMessageData.getPotentialData().getNumberOfpotentialEdgeWeights() == biggestMsgData.getPotentialData().getNumberOfpotentialEdgeWeights()){
//						for(CIMAEdgeWeight edge : biggestMsgData.getPotentialData().getPotentialEdgeWeights()){
//							if(calcMessageData.getPotentialData().hasSameEdge(edge)){
//								//potential data shouldnt change
//								System.out.println("case d_1");
//							}else{
//								calcMessageData = new MessageData(biggestMsgData.getLamdaValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, biggestMsgData, null, new PotentialData(true));
//								System.out.println("case d_2");
//							}
//						}
//					}else{
//						calcMessageData = new MessageData(biggestMsgData.getLamdaValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, biggestMsgData, null, new PotentialData(true));
//						System.out.println("case d_3");
//					}
//					
//					///
//				
//			}else{
//				System.out.println("case 4b");
//				calcMessageData = new MessageData(biggestMsgData.getLamdaValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, biggestMsgData, null, biggestMsgData.getPotentialData().getPotentialDataCopy());
//			}
//		}
//		
//		//make sure the lamdaValue is not less then the edgeWeight
//		if(receiverNode == senderNode.getParent()){
//			System.out.println("case 5");
//			if(senderNode.getEdgeWeightToParent().getEdgeWeightValue() >= calcMessageData.getLamdaValue()){
//				System.out.println("case 6");
//				
//				if(senderNode.getEdgeWeightToParent().getEdgeWeightValue() == calcMessageData.getLamdaValue()){
////					calcMessageData = new MessageData(senderNode.getEdgeWeightToParent().getEdgeWeightValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, null, senderNode.getEdgeWeightToParent(), new PotentialData(true));
//					System.out.println("case 7");
//					
//					//
//					if(calcMessageData.getPotentialData().getNumberOfpotentialEdgeWeights() == 1){
//						System.out.println("case 8");
//						
//						if(calcMessageData.getPotentialData().getPotentialEdgeWeights().get(0).equals(senderNode.getEdgeWeightToParent())){
//							//potential data shouldnt change
//							System.out.println("case 9");
//							calcMessageData = new MessageData(senderNode.getEdgeWeightToParent().getEdgeWeightValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, null, senderNode.getEdgeWeightToParent(), new PotentialData(senderNode.getEdgeWeightToParent()));
//						}else{
//							System.out.println("case 10");
//							calcMessageData = new MessageData(senderNode.getEdgeWeightToParent().getEdgeWeightValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, null, senderNode.getEdgeWeightToParent(), new PotentialData(true));
//						}
//						
//					}
//					//
//					
//					
//				}else{
//					System.out.println("case 11");
//					calcMessageData = new MessageData(senderNode.getEdgeWeightToParent().getEdgeWeightValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, null, senderNode.getEdgeWeightToParent(), new PotentialData(senderNode.getEdgeWeightToParent()));
//				}
//			}
//		}else{
//			System.out.println("case 12");
//			if(receiverNode.getEdgeWeightToParent().getEdgeWeightValue() >= calcMessageData.getLamdaValue()){
//				System.out.println("case 13");
//				if(receiverNode.getEdgeWeightToParent().getEdgeWeightValue() == calcMessageData.getLamdaValue()){
////					calcMessageData = new MessageData(receiverNode.getEdgeWeightToParent().getEdgeWeightValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, null, receiverNode.getEdgeWeightToParent(), new PotentialData(true));
//					System.out.println("case 14");
//					//
//					if(calcMessageData.getPotentialData().getNumberOfpotentialEdgeWeights() == 1){
//						System.out.println("case 15");
//						if(calcMessageData.getPotentialData().getPotentialEdgeWeights().get(0).equals(receiverNode.getEdgeWeightToParent())){
//							//potential data shouldnt change
//							System.out.println("case 16");
//							calcMessageData = new MessageData(receiverNode.getEdgeWeightToParent().getEdgeWeightValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, null, receiverNode.getEdgeWeightToParent(), new PotentialData(receiverNode.getEdgeWeightToParent()));
//						}else{
//							System.out.println("case 17");
//							calcMessageData = new MessageData(receiverNode.getEdgeWeightToParent().getEdgeWeightValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, null, receiverNode.getEdgeWeightToParent(), new PotentialData(true));
//						}
//						
//					}
//					//
//				}else{
//					System.out.println("case 18");
//					calcMessageData = new MessageData(receiverNode.getEdgeWeightToParent().getEdgeWeightValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, null, receiverNode.getEdgeWeightToParent(), new PotentialData(receiverNode.getEdgeWeightToParent()));
//				}
//			}
//		}
		
		System.out.println("end: "+calculatedMessageData.getPotentialData().toString());
		
		return calculatedMessageData;
	}
	
	

}
