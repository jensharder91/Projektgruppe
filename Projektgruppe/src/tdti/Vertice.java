package tdti;

import java.util.*;

public class Vertice {

	public enum states{
		READY, COMPUTING, DONE
	}

	private String name;
	private states state = states.READY;
	private int psi = 0;
	private Vertice parent;
	private List<Vertice> children = new ArrayList<Vertice>();
	private List<MessageData> dataReceived = new ArrayList<MessageData>();

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

	public String getName(){
		return this.name;
	}

	/**
	 * Appends a child to the list of children
	 * @param child child to append
	 */
	public void addChild(Vertice child){
		this.children.add(child);
	}

	/**
	 * Calculates the number of neighbors
	 * @return int
	 */
	public int numberOfNeighbors(){
		int count = this.children.size();
		if(this.parent instanceof Vertice){
			count++;
		}
		return count;
	}

	/**
	 * Logs the subtree rooted at the current Vertice to System.out recursively.
	 */
	public void logSubtree(){
		System.out.println(this);
		for(int i=0; i<this.children.size(); i++){
			this.children.get(i).logSubtree();
		}
	}

	public void init(){
		if((this.state == states.READY) && (this.children.size() == 0)){
			// this is a ready leaf, we should send (1,1)
			MessageData data = new MessageData(1, 1, this);
			if(this.parent instanceof Vertice){
				// this is a leaf, only neighbor we can send to is the parent
				this.parent.receive(data);
				this.state = states.COMPUTING;
			} else {
				// this is the only vertice in the tree
				this.psi = 1;
				this.state = states.DONE; // we’re done with everything
			}
		} else {
			for(int i=0; i<this.children.size(); i++){
				this.children.get(i).init();
			}
		}
	}

	public void sendToRemainingNeighbor(MessageData data){
		// find out which neighbor didn’t send any data yet
		for(int i=0; i<this.children.size(); i++){
			Vertice neighbor = this.children.get(i);
			if(!didSendData(neighbor)){
				// this neighbor didn’t send any data
				System.out.println("-- Sending to "+neighbor);
				neighbor.receive(data);
				return;
			}
		}
		if(this.parent instanceof Vertice && !didSendData(this.parent)){
			System.out.println("-- Sending to "+this.parent);
			this.parent.receive(data);
		}
	}

	public boolean didSendData(Vertice neighbor){
		for(int i=0; i<this.dataReceived.size(); i++){
			if(this.dataReceived.get(i).getSender() == neighbor){
				return true;
			}
		}
		return false;
	}

	public void receive(MessageData data){
		this.dataReceived.add(data);
		System.out.println("Vertice "+this.name+" received "+data+" (message "+this.dataReceived.size()+" of "+this.numberOfNeighbors()+")");

		// sort dataReceived to get the maximum values:
		Collections.sort(dataReceived, new MessageDataComparator());
		MessageData max1 = dataReceived.get(0);
		MessageData max2 = new MessageData(0,0,null);
		if(dataReceived.size() >= 2){
			max2 = dataReceived.get(1);
		}
		boolean case1Boolaen = (max1.getA() == max2.getA()) && max2.getC() > TDTI.IMMUNITY_TIME/2;

		if((this.state == states.READY) && (this.children.size() > 0)){
			// it’s a ready non-leaf
			int neighborCount = this.numberOfNeighbors();
			int dataCount = this.dataReceived.size();

			if(dataCount == neighborCount-1){
				// received data from all neighbors but one
				// compute the received data and send it to the neighbor, that didn’t send anything yet
				System.out.println("- Sending Data to remaining neighbor.");
				if(case1Boolaen){
					sendToRemainingNeighbor(new MessageData(max1.getA() + 1, 1, this));
				} else {
					sendToRemainingNeighbor(new MessageData(max1.getA(), Math.max(max1.getC(), max2.getC()) + 1, this));
				}
				this.state = states.COMPUTING;
			} else {
				// waiting for more data
				System.out.println("- Waiting for more data.");
			}
		}

		if(this.state == states.COMPUTING){
			// check if we have all data
			int neighborCount = this.numberOfNeighbors();
			int dataCount = this.dataReceived.size();

			if(dataCount == neighborCount){
				// having data from all neighbors, send data to all neighbors except last sender
				System.out.println("- All data received, computing and sending data");
				redirectReceivedDataExceptTo(data.getSender());
				if(case1Boolaen){
					this.psi = max1.getA()+1;
				} else {
					this.psi = max1.getA();
				}
				this.state = states.DONE;
			}
		}
	}

	public void redirectReceivedDataExceptTo(Vertice exceptTo){
		List<Vertice> neighbors = new ArrayList<Vertice>();
		for(int i=0; i<this.children.size(); i++){
			neighbors.add( this.children.get(i) );
		}
		if(this.parent instanceof Vertice){
			neighbors.add( this.parent );
		}

		for(int j=0; j<neighbors.size(); j++){
			Vertice neighbor = neighbors.get(j);
			if(neighbor != exceptTo){
				// send this neighbor the data computed from the other neighbors
				neighbor.receive(this.computeDataExceptFromNeighbor(neighbor));
			}
		}
	}

	public MessageData computeDataExceptFromNeighbor(Vertice exceptTo){
		// sort dataReceived to get the maximum values:
		Collections.sort(this.dataReceived, new MessageDataComparator());
		List<MessageData> maximums = new ArrayList<MessageData>();
		for(int i=0; i<this.dataReceived.size(); i++){
			MessageData data = this.dataReceived.get(i);
			if(data.getSender() != exceptTo){
				maximums.add(data);
			}
			if(maximums.size() == 2){
				break;
			}
		}
		MessageData max1 = maximums.get(0);
		MessageData max2 = new MessageData(0,0,null);
		if(maximums.size() >= 2){
			max2 = maximums.get(1);
		}

		if((max1.getA() == max2.getA()) && max2.getC() > TDTI.IMMUNITY_TIME/2){
			return new MessageData(max1.getA()+1,1,this);
		} else {
			return new MessageData(max1.getA(),max1.getC()+1,this);
		}
	}

	@Override
	public String toString(){
		return "Vertice ("+this.name+") ("+this.children.size()+" children) ("+this.psi+" minAgents)";
	}
}
