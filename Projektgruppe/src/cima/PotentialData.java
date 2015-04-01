package cima;

import java.util.ArrayList;
import java.util.List;

public class PotentialData {
	
	private boolean flag = false;
	private List<CIMAEdgeWeight> potentialEdgeWeights = new ArrayList<CIMAEdgeWeight>();
	private CIMAVertice vertice;
	
	
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
		potentialEdgeWeight1.addToPotentialList(vertice);
	}
	public PotentialData(CIMAVertice vertice, CIMAEdgeWeight potentialEdgeWeight1, CIMAEdgeWeight potentialEdgeWeight2){
		this(potentialEdgeWeight1, potentialEdgeWeight2);
		potentialEdgeWeight1.addToPotentialList(vertice);
		potentialEdgeWeight2.addToPotentialList(vertice);
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
		potentialEdgeWeights.add(potentialEdgeWeight1);
		if(potentialEdgeWeight2 != null){
			potentialEdgeWeights.add(potentialEdgeWeight2);
		}
	}
	
	
	//getter
	
	public boolean getFlag(){
		return flag;
	}
	
	public List<CIMAEdgeWeight> getPotentialEdgeWeights(){
		return potentialEdgeWeights;
	}
	
	public int getNumberOfpotentialEdgeWeights(){
		return potentialEdgeWeights.size();
	}
	
	
	//copy
	
	public PotentialData getPotentialDataCopy(){
		if(flag){
			return new PotentialData(flag);
		}else{
			if(potentialEdgeWeights.size() == 1){
				return new PotentialData(potentialEdgeWeights.get(0));
			}else{
				return new PotentialData(potentialEdgeWeights.get(0), potentialEdgeWeights.get(1));
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
