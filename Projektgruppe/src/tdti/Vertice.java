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
		if(parent != null){
			this.parent = parent;
			this.parent.addChild(this);
		}
	}
	
	public void addChild(Vertice child){
		this.children.add(child);
	}
	
	@Override
	public String toString(){
		return "Vertice \""+this.name+"\" mit "+this.children.size()+" Kindern";
	}
}
