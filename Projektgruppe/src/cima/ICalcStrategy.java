package cima;

import java.awt.Graphics2D;
import java.util.List;

/**
 * 
 * abstract class: for all strategys (paper-algorithmus, modifikation, potential, ...)
 *
 */
public abstract class ICalcStrategy {	
	
	protected static boolean showPotential = false;
	
	public abstract MessageData calcMessageData(CIMAVertice senderNode, CIMAVertice receiverNode, int potential);
	public abstract int calcGeneralVerticeWeight(CIMAVertice vertice);
	public abstract List<CIMAEdgeWeight> calcSortedEdgeWeightList(CIMAVertice vertice, CIMAVertice exeptVertice);
	public abstract int calcMu(CIMAVertice vertice, int potential);
	
	//animation
	public abstract void displayResult(CIMAVertice vertice, Graphics2D g2);
	
	//potential on-off
	public static void setShowPotential(boolean showPotential){
		ICalcStrategy.showPotential = showPotential;
	}
	public static boolean getShowPotential(){
		return showPotential;
	}
}
