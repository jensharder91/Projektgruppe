package cima;

import java.util.List;

public abstract class ICalcStrategy {
	
	public abstract MessageData calcMessageData(CIMAVertice senderNode, CIMAVertice receiverNode);
	public abstract int calcGeneralVerticeWeight(CIMAVertice vertice);
	public abstract int calcSpecialVerticeWeight(CIMAVertice vertice, CIMAVertice exeptVertice);	
	public abstract List<CIMAEdgeWeight> calcSortedEdgeWeightList(CIMAVertice vertice, CIMAVertice exeptVertice);
	
	/**
	 * calcMu will calculate the mu value with the choosen strategy
	 * 
	 * @param vertice  from which the result should be calculated
	 * @return MuCalcResult, included the mu value and the PotentialData
	 */
	public abstract MuCalcResult calcMu(CIMAVertice vertice);
	
	
	
	public class MuCalcResult{
		
		private int muResult;
		private PotentialData potentialDataResult;
		
		public MuCalcResult(int muResult, PotentialData potentialDataresult) {
			this.muResult = muResult;
			this.potentialDataResult = potentialDataresult;
		}
		
		public int getMuResult(){
			return muResult;
		}
		public PotentialData getPotentialDataResult(){
			return potentialDataResult;
		}
	}

}
