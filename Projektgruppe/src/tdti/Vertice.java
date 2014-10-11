package tdti;

import java.util.*;

public class Vertice {
	private String name;
	private Vertice parent;
	private List<Vertice> children = new ArrayList<Vertice>();

	public Vertice(){
		this("");
	}
	public Vertice(String name){
		this(name,null);
	}
	public Vertice(Vertice parent){
		this("",parent);
	}
	public Vertice(String name, Vertice parent){
		this.name = name;
		if(parent instanceof Vertice){
			this.parent = parent;
			this.parent.addChild(this);
		}
	}

	public void addChild(Vertice child){
		this.children.add(child);
	}

	public void logSubtree(){
		System.out.println(this);
		for(int i=0; i<this.children.size(); i++){
			this.children.get(i).logSubtree();
		}
	}

	@Override
	public String toString(){
		return "Vertice ("+this.name+") ("+this.children.size()+" children)";
	}
}
