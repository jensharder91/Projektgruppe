package cima;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Tree.Vertice;

public class CIMAVertice extends Vertice{
	
	private int edgeWeightToParent;
	private int verticeWeight;
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
		if(children.size() == 0 && state == states.READY && parent != null){
			//got a ready leaf -> send message
			((CIMAVertice) parent).receive(new MessageData(edgeWeightToParent, this));
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
		
		System.out.println("received msg : "+data);
		
		if(lamdas.size() == numberOfNeighbors() -1){
			System.out.println("YEAH");
			state = states.ACTIVE;
		}else if(lamdas.size() == numberOfNeighbors()){
			
			state = states.DONE;
		}
	}
	
	
	public void setEdgeWeightToParent(int edgeWeight){
		this.edgeWeightToParent = edgeWeight;
	}
	public int getEdgeWeightToParent(){
		return edgeWeightToParent;
	}

}
