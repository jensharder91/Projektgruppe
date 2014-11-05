package tdti;

import java.awt.Graphics;
import java.util.*;

import Tree.Vertice;

public class TDTIVertice extends Vertice{

	public TDTIVertice(String name, TDTIVertice parent) {
		super(name, parent);
	}

	public enum states{
		READY, COMPUTING, DONE
	}

	private states state = states.READY;
	private int psi = 0;
	private List<MessageData> dataReceived = new ArrayList<MessageData>();


	public void algorithmus(){
		reset();
		init();
	}

	private void reset(){
		this.state = states.READY;
		this.dataReceived = new ArrayList<MessageData>();
		for(Vertice child : this.children){
			if(checkVerticeType(child)){
				((TDTIVertice) child).reset();
			}
		}
	}

	private void init(){
		if((this.state == states.READY) && (this.children.size() == 0)){
			// this is a ready leaf, we should send (1,1)
			MessageData data = new MessageData(1, 1, this);
			if(this.parent != null){
				// this is a leaf, only neighbor we can send to is the parent
				if(checkVerticeType(this.parent)){
					((TDTIVertice) this.parent).receive(data);
				}
				this.state = states.COMPUTING;
				// should not wait for data, if they are already received
				checkDataAlreadyReceived();
			} else {
				// this is the only vertice in the tree
				this.psi = 1;
				this.state = states.DONE; // we’re done with everything
			}
		} else {
			for(Vertice child : this.children){
				if(checkVerticeType(child)){
					((TDTIVertice) child).init();
				}
			}
		}
	}

	private void checkDataAlreadyReceived(){
		int neighborCount = this.numberOfNeighbors();
		int dataCount = this.dataReceived.size();

		if(dataCount == neighborCount){
			// sort dataReceived to get the maximum values:
			Collections.sort(dataReceived, new MessageDataComparator());
			MessageData max1 = dataReceived.get(0);
			MessageData max2 = new MessageData(0,0,null);
			if(dataReceived.size() >= 2){
				max2 = dataReceived.get(1);
			}

			// having data from all neighbors, send data to all neighbors except last sender
			System.out.println("- All data received, computing and sending data");
			if((max1.getA() == max2.getA()) && max2.getC() > TDTI.IMMUNITY_TIME/2){
				this.psi = max1.getA()+1;
			} else {
				this.psi = max1.getA();
			}
			this.state = states.DONE;
		}
	}

	private void sendToRemainingNeighbor(MessageData data){
		// find out which neighbor didn’t send any data yet
		for(Vertice neighbor : this.children){
			if(checkVerticeType(neighbor) && !didSendData((TDTIVertice) neighbor)){
				// this neighbor didn’t send any data
				System.out.println("-- Sending to "+neighbor);
				((TDTIVertice) neighbor).receive(data);
				return;
			}
		}
		if(this.parent != null && checkVerticeType(this.parent) && !didSendData((TDTIVertice) this.parent)){
			System.out.println("-- Sending to "+this.parent);
			((TDTIVertice) this.parent).receive(data);
		}
	}

	private boolean didSendData(TDTIVertice neighbor){
		for(MessageData data : this.dataReceived){
			if(data.getSender() == neighbor){
				return true;
			}
		}
		return false;
	}

	private void receive(MessageData data){
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

	private void redirectReceivedDataExceptTo(TDTIVertice exceptTo){
		List<Vertice> neighbors = new ArrayList<Vertice>();
		for(Vertice child : this.children){
			neighbors.add( child );
		}
		if(this.parent instanceof TDTIVertice){
			neighbors.add( this.parent );
		}

		for(Vertice neighbor : neighbors){
			if(neighbor != exceptTo){
				// send this neighbor the data computed from the other neighbors
				if(checkVerticeType(neighbor)){
					((TDTIVertice) neighbor).receive(this.computeDataExceptFromNeighbor((TDTIVertice) neighbor));
				}
			}
		}
	}

	private MessageData computeDataExceptFromNeighbor(TDTIVertice exceptTo){
		// sort dataReceived to get the maximum values:
		Collections.sort(this.dataReceived, new MessageDataComparator());
		List<MessageData> maximums = new ArrayList<MessageData>();
		for(MessageData data : this.dataReceived){
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

	protected void drawAllVertice(Graphics g){
		
		super.drawAllVertice(g);

		String string = String.valueOf(psi);
		int stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(string,g).getWidth());
		g.drawString(string, xMittel - stringWidth/2, yMittel+height/4);

	}
	
	private boolean checkVerticeType(Vertice vertice) {
		if(!(vertice instanceof TDTIVertice)){
			System.out.println("wrong VerticeType!!! FATAL ERROR");
			return false;
		}
		return true;
	}

	@Override
	public String toString(){
		return "Vertice ("+this.name+") ("+this.children.size()+" children) ("+this.state+") ("+this.psi+" minAgents)";
	}

	public int getPsi(){
		return psi;
	}
}
