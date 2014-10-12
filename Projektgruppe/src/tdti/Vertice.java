package tdti;

import java.util.*;

public class Vertice {
	private String name;
	private Vertice parent;
	private int state = 0; // 0=ready, 1=computing, 2=done
	private int psi = 0;
	private List<Vertice> children = new ArrayList<Vertice>();
	private List<int[]> dataReceived = new ArrayList<int[]>();

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

	public int numberOfNeighbors(){
		int count = this.children.size();
		if(this.parent instanceof Vertice){
			count++;
		}
		return count;
	}

	public void logSubtree(){
		System.out.println(this);
		for(int i=0; i<this.children.size(); i++){
			this.children.get(i).logSubtree();
		}
	}

	public void computeMinTeamSize(){
		if((this.state == 0) && (this.children.size() == 0)){
			// it’s a ready leaf, we should send (1,1)
			int[] data = {1,1};
			if(this.parent instanceof Vertice){
				// this is a leaf, only neighbor we can send to is the parent
				this.parent.receive(data);
				this.state = 1;
			} else {
				// this is the only vertice in the tree
				this.psi = 1;
				this.state = 2; // we’re done with everything
			}
		}
	}

	public void receive(int[] data){
		this.dataReceived.add(data);

		if((this.state == 0) && (this.children.size() > 0)){
			// it’s a ready non-leaf
			int neighborCount = this.numberOfNeighbors();
			int dataCount = this.dataReceived.size();

			if(dataCount == neighborCount-1){
				// received data from all neighbors but one
				// TODO: sort dataReceived to get the maximum values
				// TODO: check which case it is
				// TODO: send data to missing neighbor (currently not known which one it is)
				// TODO: become computing (this.state = 1)
			} else {
				// waiting for more data
			}
		}

		if(this.state == 1){
			// check if we have all data
			int neighborCount = this.numberOfNeighbors();
			int dataCount = this.dataReceived.size();

			if(dataCount == neighborCount){
				// state is computing
				// TODO: sort dataReceived to get the maximum values
				// TODO: check which case it is
				// TODO: send data to N(x)\l
				// TODO: set this.psi (minimum number of agents)
				// TODO: become done (this.state = 2)
			}
		}
	}

	@Override
	public String toString(){
		return "Vertice ("+this.name+") ("+this.children.size()+" children) ("+this.psi+" minAgents)";
	}
}
