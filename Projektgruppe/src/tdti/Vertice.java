package tdti;

import java.util.*;

public class Vertice {
	private Vertice parent;
	private List<Vertice> children = new ArrayList<Vertice>();
	
	public Vertice(){
		this(null);
	}
	public Vertice(Vertice parent){
		this.parent = parent;
		parent.addChild(this);
	}
	
	public void addChild(Vertice child){
		this.children.add(child);
	}
}
