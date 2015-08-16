package cima;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModellMinimalDanger extends ICalcStrategy{

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
		
		System.out.println("start calc mesgData...."+senderNode.getName()+" > "+receiverNode.getName());
		
	
		
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
//			calculatedMessageData = new MessageData(edgeToReceiverNode.getLamdaValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, max2.getLamdaValue(), null, edgeToReceiverNode, calculatedPotentialData);
			
			//
			
			if(calculatedMessageData.getValue() == edgeToReceiverNode.getValue()){
				
				//
				if(calculatedMessageData.getPotentialData().getNumberOfpotentialEdgeWeights() == 1){
					
					if(calculatedMessageData.getPotentialData().getPotentialEdgeWeights().get(0).equals(edgeToReceiverNode)){
						//potential data shouldnt change
//						calcMessageData = new MessageData(senderNode.getEdgeWeightToParent().getEdgeWeightValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, null, senderNode.getEdgeWeightToParent(), new PotentialData(senderNode.getEdgeWeightToParent()));
					}else{
						calculatedMessageData = new MessageData_simplePotential(senderNode, receiverNode, edgeToReceiverNode.getValue(), mapMarkable, new PotentialData(true));
					}
					
				}
				//
				
				
			}else{
				calculatedMessageData = new MessageData_simplePotential(senderNode, receiverNode, edgeToReceiverNode.getValue(), mapMarkable, new PotentialData(edgeToReceiverNode));								
			}
			
			//
		}
		
		//make sure it is not smaller then the biggest messageData
		if(calculatedMessageData.getValue() <= biggestMsgData.getValue()){
//			calculatedMessageData = new MessageData(biggestMsgData.getLamdaValue(), senderNode, receiverNode, edgeToReceiverNode, max1, max2, max2.getLamdaValue(), biggestMsgData, null, calculatedPotentialData);
			
			//
			
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
			
			//
			

		}
	
		
		System.out.println("end: "+calculatedMessageData.getPotentialData().toString());
		
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
//		Collections.sort(edgeWeightList);
		CIMAEdgeWeight max1 = edgeWeightList.get(0);
		CIMAEdgeWeight max2 = edgeWeightList.get(1);
		CIMAEdgeWeight max3 = edgeWeightList.get(2);
		
//		CIMAEdgeWeight max1 = calcMaxOrSecmaxEdge(null, true);
//		CIMAEdgeWeight max2 = calcMaxOrSecmaxEdge(null, false);
		
		System.out.println("Vertice : "+vertice.getName());
		
		System.out.println("max1: "+ max1.getValue());
		System.out.println("max2: "+ max2.getValue());
		System.out.println("max3: "+ max3.getValue());
	

//		int verticeWeight = calcGeneralVerticeWeight(vertice);
		PotentialData potentialData;

		//default case max1 + max2
		if(max1.getValue() == max3.getValue()){
			potentialData = new PotentialData(vertice, true);
			System.out.println("case a");
		}else if(max2.getValue() == 0 || max2.getValue() == max3.getValue()){
			potentialData = new PotentialData(vertice, max1);
			System.out.println("case b");
		}else{
			potentialData = new PotentialData(vertice, max1, max2);
			System.out.println("case c");
		}
		mu = max1.getValue() + max2.getValue();

		//make sure mu is not less the biggest msgData
		if(mu <=  biggestMsgData.getValue()){
			
			if(mu == biggestMsgData.getValue()){
				
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
						
			mu = biggestMsgData.getValue();
		}
		
		return mu;
	}

	
	@Override
	public String toString() {
		return "\"Modifikation\" (Potential = 1)";
	}


	@Override
	public void displayResult(CIMAVertice vertice, Graphics2D g2) {
		// TODO Auto-generated method stub
		
	}

}
