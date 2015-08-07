package cima;

import java.util.List;

public abstract class ICalcStrategy {
	
	public abstract MessageData calcMessageData(CIMAVertice senderNode, CIMAVertice receiverNode, int potential);
	public abstract int calcGeneralVerticeWeight(CIMAVertice vertice);
	public abstract List<CIMAEdgeWeight> calcSortedEdgeWeightList(CIMAVertice vertice, CIMAVertice exeptVertice);
	public abstract int calcMu(CIMAVertice vertice, int potential);
	
	//animation
	public abstract void displayResult(CIMAVertice vertice);
}
