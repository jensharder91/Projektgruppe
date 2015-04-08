package cima;

import java.util.List;

import cima.ModellMinimalDanger.MuCalcResult;

public interface ICalcStrategy {
	
	public MessageData calcMessageData(CIMAVertice senderNode, CIMAVertice receiverNode);
	public int calcGeneralVerticeWeight(CIMAVertice vertice);
	public int calcSpecialVerticeWeight(CIMAVertice vertice, CIMAVertice exeptVertice);	
	public List<CIMAEdgeWeight> calcSortedEdgeWeightList(CIMAVertice vertice, CIMAVertice exeptVertice);
	
	/**
	 * calcMu will calculate the mu value with the choosen strategy
	 * 
	 * @param vertice  from which the result should be calculated
	 * @return MuCalcResult, included the mu value and the PotentialData
	 */
	public MuCalcResult calcMu(CIMAVertice vertice);

}
