package cima;

import java.awt.Color;
import java.awt.Graphics;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Tree.Vertice;

public class CIMAVertice extends Vertice{
	
	private int edgeWeightToParent;
	private int verticeWeight;
	private int mu;
	private states state;
	private List<MessageData> lamdas = new ArrayList<MessageData>();
	
	private enum states{
		READY,
		ACTIVE,
		DONE
	};
	
	public CIMAVertice(String name, Vertice parent){
		this(name, parent, 1);
	}
	
	public CIMAVertice(String name, Vertice parent, int edgeWeightToParent) {
		super(name, parent);
		if(parent != null){
			this.edgeWeightToParent = edgeWeightToParent;
		}else{
			this.edgeWeightToParent = 0;
		}
	}
	
	@Override
	protected void drawAllTreeLines(Graphics g) {
		super.drawAllTreeLines(g);
		
		if(parent != null){
			g.setColor(Color.red);
			g.drawString(""+edgeWeightToParent, Math.min(xMittel, parent.getMittelX()) + Math.abs(xMittel - parent.getMittelX()) / 2, Math.min(yMittel, parent.getMittelY()) + Math.abs(yMittel - parent.getMittelY()) / 2);
		}
	}
	
	public void algorithmus(){
		System.out.println("starting algo....");
		reset();
		startAlgo();
		calcMu();
		logSubtree();
	}
	
	private void reset(){
		state = states.READY;
		lamdas.clear(); 
		
		//calc the verticeWeight
		verticeWeight = edgeWeightToParent;
		for(Vertice child : children){
			if(!(child instanceof CIMAVertice)){
				System.out.println("ERROR... wrong Verticetype!!");
				return;
			}
			
			if(((CIMAVertice) child).getEdgeWeightToParent() > verticeWeight){
				verticeWeight = ((CIMAVertice) child).getEdgeWeightToParent();
			}
		}
		
		//call reset() recursive to all children
		for(Vertice child : children){
			if(!(child instanceof CIMAVertice)){
				System.out.println("ERROR... wrong Verticetype!!");
				return;
			}
			((CIMAVertice) child).reset();
		}
	}
	
	private void startAlgo(){
		if(children.size() == 0 && parent != null){
			//got a ready leaf -> send message
			((CIMAVertice) parent).receive(new MessageData(verticeWeight, this));
			state = states.ACTIVE;
		}else{
			for(Vertice child : children){
				if(!(child instanceof CIMAVertice)){
					System.out.println("ERROR... wrong Verticetype!!");
					return;
				}
				((CIMAVertice) child).startAlgo();
			}
		}
	}
	
	private void receive(MessageData data){
		this.lamdas.add(data);
		
//		System.out.println("received msg : "+data);
		
		if(lamdas.size() == numberOfNeighbors() -1){
			//TODO *
			computeLamdasAndSendTo(getMissingNeightbour());
			state = states.ACTIVE;
		}else if(lamdas.size() == numberOfNeighbors()){
			//TODO **
			computeAllLamdasExeptFor(data.getSender());
			state = states.DONE;
		}
	}
	
	private void computeAllLamdasExeptFor(CIMAVertice exeptVertice){
		for(Vertice child : children){
			if(!(child instanceof CIMAVertice)){
				System.out.println("ERROR... wrong Verticetype!!");
				return;
			}
			if(((CIMAVertice) child) != exeptVertice){
				computeLamdasAndSendTo((CIMAVertice) child);
			}
		}
		if(parent != null){
			if(!(parent instanceof CIMAVertice)){
				System.out.println("ERROR... wrong Verticetype!!");
				return;
			}
			if(((CIMAVertice)parent) != exeptVertice){
				computeLamdasAndSendTo((CIMAVertice) parent);
			}
		}
	}
	
	private void computeLamdasAndSendTo(CIMAVertice receiverNode){
		
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
		MessageData max1 = maximums.get(0);
		MessageData max2 = new MessageData(0, null);
		if(maximums.size() >= 2){
			max2 = maximums.get(1);
		}
		
		int maxValue = Math.max(max1.getLamdaValue(), max2.getLamdaValue() + verticeWeight);
		
		receiverNode.receive(new MessageData(maxValue, this));
	}
	
	private CIMAVertice getMissingNeightbour(){
		for(Vertice neighbor : children){
			if(!(neighbor instanceof CIMAVertice)){
				System.out.println("ERROR... wrong Verticetype!!");
				return null;
			}
			if(! didSendData((CIMAVertice) neighbor)){
				return (CIMAVertice) neighbor;
			}
		}
		if(parent != null){
			if(!(parent instanceof CIMAVertice)){
				System.out.println("ERROR... wrong Verticetype!!");
				return null;
			}
			if(!didSendData((CIMAVertice) parent)){
				return (CIMAVertice) parent;
			}
		}
		System.out.println("ERROR : no missing neighbor?");
		return null;
	}

	private boolean didSendData(CIMAVertice neighbor){
		for(MessageData data : lamdas){
			if(data.getSender() == neighbor){
				return true;
			}
		}
		return false;
	}
	
	private void calcMu(){
		Collections.sort(lamdas, new MessageDataComparator());
		MessageData max1 = lamdas.get(0);
		MessageData max2 = new MessageData(0, null);
		if(lamdas.size() >= 2){
			max2 = lamdas.get(1);
		}
		
		mu = Math.max(max1.getLamdaValue(), max2.getLamdaValue() + verticeWeight);
		
		for(Vertice child : children){
			if(!(child instanceof CIMAVertice)){
				System.out.println("ERROR... wrong Verticetype!!");
				return;
			}
			((CIMAVertice) child).calcMu();
		}
	}
	
	@Override
	public String toString(){
		return "##Vertice ("+this.name+") ("+this.children.size()+" children) ("+this.mu+")\n"
				+ "		all lamda: "+getAlllamda();
	}
	
	private String getAlllamda(){
		
		String alllamda = "   ";
		for(MessageData msgData : lamdas){
			alllamda += msgData.toString() + "  /  ";
		}
		
		return alllamda;
	}
	
	
	public void setEdgeWeightToParent(int edgeWeight){
		this.edgeWeightToParent = edgeWeight;
	}
	public int getEdgeWeightToParent(){
		return edgeWeightToParent;
	}

}
