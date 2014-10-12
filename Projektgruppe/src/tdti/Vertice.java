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
	 * Sends MessageData to all its neighbors except one
	 * @param data The MessageData object to send
	 * @param exceptTo The neighbor which should be skipped
	 */
	public void sendToAllNeighbors (MessageData data, Vertice exceptTo){
		for(Vertice vertice : children){
			if(vertice != exceptTo){
				vertice.receive(data);
			}
		}
		if(parent instanceof Vertice && parent != exceptTo){
			parent.receive(data);
		}
	}

	/**
	 * Sends a MessageData object to all neighbors that didn’t yet send any data
	 * @param data The MessageData object to send
	 */
	public void sendToMissingNeighbor(MessageData data){
		boolean missingNeighborFound = false;
		boolean result;
		for(Vertice vertice : children){
			//result and if/else is just for checking that only one is found
			result = checkVertice(vertice, data);
			if(result && missingNeighborFound){
				//both are true
				System.out.println("ERROR Vertice.sentToMissingNeighbor\n multiple found");
			}else{
				missingNeighborFound = result;
			}
		}
		if(parent instanceof Vertice){
			//result and if/else is just for checking that only one is found
			result = checkVertice(parent, data);
			if(result && missingNeighborFound){
				//both are true
				System.out.println("ERROR Vertice.sentToMissingNeighbor\n multiple found");
			}else{
				missingNeighborFound = result;
			}
		}
	}

	/**
	 * Sends a MessageData object to all neighbors that didn’t yet send any data
	 * @param vertice The vertice which should be checked
	 * @param data The MessageData object to send
	 */
	private boolean checkVertice(Vertice vertice, MessageData data){
		boolean found = false;
		for(MessageData msgData : dataReceived){
			if(vertice.equals(msgData.getSender())){
				found = true;
			}
		}
		if(!found){
			vertice.receive(data);
			return true;
		}
		return false;
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

	public void computeMinTeamSize(){
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
		}
	}

	public void receive(MessageData data){
		this.dataReceived.add(data);

		// sort dataReceived to get the maximum values:
		Collections.sort(dataReceived, new MessageDataComparator());
		MessageData max1 = dataReceived.get(0);
		MessageData max2 = dataReceived.get(1);
		boolean case1Boolaen = (max1.getA() == max2.getA()) && max2.getC() > TDTI.IMMUNITY_TIME/2;
		//		boolean case2Boolean = (((max1.getA() == max2.getA()) && (max2.getC() <= TDTI.IMMUNITY_TIME/2)) || (max1.getA() != max2.getA()));

		if((this.state == states.READY) && (this.children.size() > 0)){
			// it’s a ready non-leaf
			int neighborCount = this.numberOfNeighbors();
			int dataCount = this.dataReceived.size();

			if(dataCount == neighborCount-1){
				// received data from all neighbors but one
				if(case1Boolaen){
					//case1
					sendToMissingNeighbor(new MessageData(max1.getA() + 1, 1, this));
				}
				else{
					//case2
					sendToMissingNeighbor(new MessageData(max1.getA(), Math.max(max1.getC(), max2.getC()) + 1, this));
				}
				this.state = states.COMPUTING;
			} else {
				// waiting for more data
			}
		}

		if(this.state == states.COMPUTING){
			// check if we have all data
			int neighborCount = this.numberOfNeighbors();
			int dataCount = this.dataReceived.size();

			if(dataCount == neighborCount){
				if(case1Boolaen){
					//case 1
					this.psi = max1.getA() +1;
					sendToAllNeighbors(new MessageData(max1.getA() + 1, 1, this), data.getSender());//send data to N(x)\l
				}else{
					//case 2
					this.psi = max1.getA();
					sendToAllNeighbors(new MessageData(max1.getA(), Math.max(max1.getC(), max2.getC()) + 1, this), data.getSender());//send data to N(x)\l
				}
				this.state = states.DONE;
			}
		}
	}

	@Override
	public String toString(){
		return "Vertice ("+this.name+") ("+this.children.size()+" children) ("+this.psi+" minAgents)";
	}
}
