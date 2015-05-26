package cima;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ModelStandardPaper extends ICalcStrategy{

	@Override
	public MessageData calcMessageData(CIMAVertice senderNode,
			CIMAVertice receiverNode, int potential) {
		// TODO Auto-generated method stub
		
		/****************************************************/
		
		List<MessageData> lamdas = senderNode.getLamdas();
		Collections.sort(lamdas, new MessageDataComparator());
 		List<MessageData> maximums = new ArrayList<MessageData>();
 		for(MessageData data : lamdas){
 			if(data.getSender() != receiverNode){
 				maximums.add(data);
 			}
 			if(maximums.size() == 2){
 				break;
 			}
 		}
		MessageData max1 = new MessageData();
		MessageData max2 = new MessageData();
		
 		if(maximums.size() > 0){
			max1 = maximums.get(0);
		}
		if(maximums.size() > 1){
			max2 = maximums.get(1);
 		}
		
//		CIMAEdgeWeight max1 = calcSortedEdgeWeightList(senderNode, receiverNode).get(0);
//		CIMAEdgeWeight max2 = calcSortedEdgeWeightList(senderNode, receiverNode).get(1);
//		
//		System.out.println("vertex name: "+name+"   max1 : "+max1.getEdgeWeightValue() + " / max2 : "+max2.getEdgeWeightValue());
 
 		MessageData calcMessageData;
 		int verticeWeight = calcGeneralVerticeWeight(senderNode);
 		if(max1.getLamdaValue() >= max2.getLamdaValue() + verticeWeight){
 			calcMessageData = new MessageData(max1.getLamdaValue(), senderNode, receiverNode, senderNode.getEdgeWeightToParent(), max1, max2, verticeWeight, null, null, new PotentialData(true));
 		}else{
 			calcMessageData = new MessageData(max2.getLamdaValue() + verticeWeight, senderNode, receiverNode, senderNode.getEdgeWeightToParent(), max1, max2, verticeWeight, null, null, new PotentialData(true));
 		}
 	
 
 		return calcMessageData;
		
		
		
		/****************************************************/
	}

	@Override
	public int calcGeneralVerticeWeight(CIMAVertice vertice) {
 		//calc the verticeWeight
		return calcSortedEdgeWeightList(vertice, null).get(0).getLamdaValue();
	}

	@Override
	public int calcSpecialVerticeWeight(CIMAVertice vertice, CIMAVertice exeptVertice) {

		// with this model there is no difference between general and special vertice weight
		return calcGeneralVerticeWeight(vertice);
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
	public MuCalcResult calcMu(CIMAVertice vertice) {
		List<MessageData> lamdas = vertice.getLamdas();
 		Collections.sort(lamdas, new MessageDataComparator());
 		MessageData max1 = new MessageData();
 		MessageData max2 = new MessageData();
 		if(lamdas.size() >= 1){
 			max1 = lamdas.get(0);
 		}
 		if(lamdas.size() >= 2){
 			max2 = lamdas.get(1);
 		}
 
 		int verticeWeight = calcGeneralVerticeWeight(vertice);
 		int mu = Math.max(max1.getLamdaValue(), max2.getLamdaValue() + verticeWeight);
 		
 		return new MuCalcResult(mu, new PotentialData(true));
	}
	
	
	@Override
	public String toString() {
		return "\"Paper Modell\"";
	}

}
