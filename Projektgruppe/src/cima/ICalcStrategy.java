package cima;

import java.util.List;

public abstract class ICalcStrategy {
	
	public abstract MessageData calcMessageData(CIMAVertice senderNode, CIMAVertice receiverNode, int potential);
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
		private int bestMuResult;
		private CIMAEdgeWeight potentialEdge;
		private PotentialData potentialDataResult;
		
		public MuCalcResult(int muResult, PotentialData potentialDataresult) {
			this(muResult, muResult, null, potentialDataresult);
		}
		public MuCalcResult(int muResult, int bestMuResult, CIMAEdgeWeight potentialEdge){
			this(muResult, bestMuResult, potentialEdge, null);
		}
		public MuCalcResult(int muResult, int bestMuResult, CIMAEdgeWeight potentialEdge, PotentialData potentialDataResult){
			this.muResult = muResult;
			this.bestMuResult = bestMuResult;
			this.potentialEdge = potentialEdge;
			this.potentialDataResult = potentialDataResult;
		}
		
		public int getMuResult(){
			return muResult;
		}
		public int getBestMuResult(){
			return bestMuResult;
		}
		public CIMAEdgeWeight getPotentialEdge(){
			return potentialEdge;
		}
		public PotentialData getPotentialDataResult(){
			return potentialDataResult;
		}
	}

}
