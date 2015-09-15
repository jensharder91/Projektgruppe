package cima;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * algorithmus - logic for paper algorithmus
 *
 */
public class ModelStandardPaper extends ICalcStrategy{

	@Override
	public MessageData calcMessageData(CIMAVertice senderNode,
		CIMAVertice receiverNode, int potential) {
		
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
		MessageData max1 = new MessageData_normal();
		MessageData max2 = new MessageData_normal();
		
 		if(maximums.size() > 0){
			max1 = maximums.get(0);
		}
		if(maximums.size() > 1){
			max2 = maximums.get(1);
 		}
		
		Map<String, IMarkable> mapMarkable = new HashMap<String, IMarkable>();
		mapMarkable.put("max1", max1);
		mapMarkable.put("max2", max2);
		mapMarkable.put("vertice", senderNode);
 
 		MessageData calcMessageData;
 		int verticeWeight = calcGeneralVerticeWeight(senderNode);
 		if(max1.getValue() >= max2.getValue() + verticeWeight){
 			calcMessageData = new MessageData_normal(senderNode, receiverNode, mapMarkable, max1.getValue());
 		}else{
 			calcMessageData = new MessageData_normal(senderNode, receiverNode, mapMarkable, max2.getValue() + verticeWeight);
 		}
 	
 
 		return calcMessageData;
	}

	@Override
	public int calcGeneralVerticeWeight(CIMAVertice vertice) {
 		//calc the verticeWeight
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
	public int calcMu(CIMAVertice vertice, int potential) {
		List<MessageData> lamdas = vertice.getLamdas();
 		Collections.sort(lamdas, new MessageDataComparator());
 		MessageData max1 = new MessageData_normal();
 		MessageData max2 = new MessageData_normal();
 		if(lamdas.size() >= 1){
 			max1 = lamdas.get(0);
 		}
 		if(lamdas.size() >= 2){
 			max2 = lamdas.get(1);
 		}
 
 		int verticeWeight = calcGeneralVerticeWeight(vertice);
 		int mu = Math.max(max1.getValue(), max2.getValue() + verticeWeight);
 		
 		return mu;
	}
	
	
	@Override
	public String toString() {
		return "\"Paper Modell\"";
	}

	@Override
	public void displayResult(CIMAVertice vertice, Graphics2D g2) {
		
	}

}
