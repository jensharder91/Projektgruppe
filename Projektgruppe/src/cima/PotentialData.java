package cima;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * potentialdata for potential == 1
 *
 */
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
	}
	public PotentialData(CIMAVertice vertice, CIMAEdgeWeight potentialEdgeWeight1, CIMAEdgeWeight potentialEdgeWeight2){
		this(potentialEdgeWeight1, potentialEdgeWeight2);
		this.vertice = vertice;
	}
	
	
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
