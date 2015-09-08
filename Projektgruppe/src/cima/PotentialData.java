package cima;

import java.util.ArrayList;
import java.util.List;

public class PotentialData {
	
	private boolean flag = false;
	private ArrayList<CIMAEdgeWeight> potentialEdgeWeights = new ArrayList<CIMAEdgeWeight>();
	private CIMAVertice vertice = null;
	
	
	//constructors
	
	public PotentialData(boolean flag){
		setFlag(flag);
	}
	public PotentialData(CIMAEdgeWeight potentialEdgeWeight1){
		setPotentialEdgeWeight(potentialEdgeWeight1);
	}
	public PotentialData(CIMAEdgeWeight potentialEdgeWeight1, CIMAEdgeWeight potentialEdgeWeight2){
		setPotentialEdgeWeight(potentialEdgeWeight1, potentialEdgeWeight2);
	}
	
	public PotentialData(CIMAVertice vertice, boolean flag){
		this(flag);
		this.vertice = vertice;
	}
	public PotentialData(CIMAVertice vertice, CIMAEdgeWeight potentialEdgeWeight1){
		this(potentialEdgeWeight1);
		this.vertice = vertice;
//		if(vertice != null){
//			potentialEdgeWeight1.addToPotentialList(vertice);
//		}
	}
	public PotentialData(CIMAVertice vertice, CIMAEdgeWeight potentialEdgeWeight1, CIMAEdgeWeight potentialEdgeWeight2){
		this(potentialEdgeWeight1, potentialEdgeWeight2);
		this.vertice = vertice;
//		if(vertice != null){
//			potentialEdgeWeight1.addToPotentialList(vertice);
//			potentialEdgeWeight2.addToPotentialList(vertice);
//		}
	}
	
	
	//draw
	
//	public void prepareDraw(){
//		System.out.println("in draw");
//		if(flag){
//			System.out.println("flag");
//			vertice.markColor(Color.RED);
//		}
//		else{
//			for(CIMAEdgeWeight edge : potentialEdgeWeights){
//				System.out.println("edge");
//				if(edge.getEdgeWeightValue() > 1){
//					edge.markColor(Color.GREEN);
//				}else{
//					edge.markColor(Color.YELLOW);
//				}
//			}
//			if(potentialEdgeWeights.size() > 0){
//				vertice.markColor(Color.GREEN);
//			}
//		}
//	}
	
//	public void resetDraw(){
//		for(CIMAEdgeWeight edge : potentialEdgeWeights){
//			edge.resetColor();
//		}
//	}

	
	
	//setter
	
	public void setFlag(){
		setFlag(true);
	}
	private void setFlag(boolean flag){
		this.flag = flag;
		potentialEdgeWeights.clear();
	}
	
	public void setPotentialEdgeWeight(CIMAEdgeWeight potentialEdgeWeight1){
		setPotentialEdgeWeight(potentialEdgeWeight1, null);
	}
	public void setPotentialEdgeWeight(CIMAEdgeWeight potentialEdgeWeight1, CIMAEdgeWeight potentialEdgeWeight2){
		flag = false;
		potentialEdgeWeights.clear();
		if(potentialEdgeWeight1.getValue() > 1){
			potentialEdgeWeights.add(potentialEdgeWeight1);
		}
		if(potentialEdgeWeight2 != null){
			if(potentialEdgeWeight2.getValue() > 1){
				potentialEdgeWeights.add(potentialEdgeWeight2);
			}
		}
		
		//maybe no edge was added, because of edgevalue 1?
		if(potentialEdgeWeights.size() == 0){
			setFlag();
		}
	}
	
	//register
	
//	public void registerPotentialVertice(CIMAVertice vertice){
//		if(flag){
//			//dont register if flag is set
//			return;
//		}
//		for(CIMAEdgeWeight edgeWeight : potentialEdgeWeights){
//			edgeWeight.addToPotentialList(vertice);
//		}
//	}
	
	//check
	
	public boolean hasSameEdge(CIMAEdgeWeight newEdgeWeight){
		
		
		for(CIMAEdgeWeight edgeWeight : potentialEdgeWeights){
			if(edgeWeight.equals(newEdgeWeight)){
				return true;
			}
		}
		
		return false;
	}
	
	
	//getter
	
	public boolean getFlag(){
		return flag;
	}
	
	public ArrayList<CIMAEdgeWeight> getPotentialEdgeWeights(){
		return potentialEdgeWeights;
	}
	
	public int getNumberOfpotentialEdgeWeights(){
		return potentialEdgeWeights.size();
	}
	
	
	//copy
	
	public PotentialData getPotentialDataCopy(){
		return getPotentialDataCopy(null);
	}
	public PotentialData getPotentialDataCopy(CIMAVertice vertice){
		if(flag){
			return new PotentialData(vertice, flag);
		}else{
			if(potentialEdgeWeights.size() == 1){
				return new PotentialData(vertice, potentialEdgeWeights.get(0));
			}else{
				return new PotentialData(vertice, potentialEdgeWeights.get(0), potentialEdgeWeights.get(1));
			}
		}
	}
		
	
	@Override
	public String toString() {

		if(flag){
			return "flag -> true";
		}else{
			if(potentialEdgeWeights.size() == 1){
				return potentialEdgeWeights.get(0).getEdgeName();
			}else{
				return potentialEdgeWeights.get(0).getEdgeName() + ", "+potentialEdgeWeights.get(1).getEdgeName();
			}
		}
	}

}
