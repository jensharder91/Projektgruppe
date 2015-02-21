package tdti;

import java.awt.Color;
import java.awt.Graphics;
import java.util.*;

import Tree.Vertice;

public class TDTIVertice extends Vertice{

	private TDTIGui gui = TDTIGui.getGui();
	protected Color computingColor = new Color(250,220,100);
	protected Color doneColor = new Color(140,240,100);
	protected Color decontaminatedColor = new Color(255,255,255);
	private int immunityTime = 0;
	private boolean contaminated = true;
	private TDTIAgentCoordinator coordinator = TDTIAgentCoordinator.getCoordinator();

	public enum states {
		READY, COMPUTING, DONE
	}

	private states state = states.READY;
	private int psi = 0;
	private List<MessageData> dataReceived = new ArrayList<MessageData>();


	public TDTIVertice(String name, TDTIVertice parent){
		super(name, parent);
		this.gui = gui;
	}

	public void algorithmus(){
		reset();
		init();
	}

	private void reset(){
		this.state = states.READY;
		this.dataReceived = new ArrayList<MessageData>();
		this.psi = 0;

		for(Vertice child : this.children){
			if(checkVerticeType(child)){
				((TDTIVertice) child).reset();
			}
		}
	}

	public void recursiveReset(){
		contaminated = true;
		immunityTime = 0;
		for(Vertice child : children){
			if(checkVerticeType(child)){
				((TDTIVertice) child).recursiveReset();
			}
		}
	}

	private void init(){
		if(gui.remainingSteps == 0){
			return;
		}

		if((this.state == states.READY) && (this.children.size() == 0)){
			// this is a ready leaf, we should send (1,1)
			MessageData data = new MessageData(1, 1, this);
			if(parent != null && checkVerticeType(parent)){
				// this is a leaf, only neighbor we can send to is the parent
				this.state = states.COMPUTING;
				((TDTIVertice) this.parent).receive(data);
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

	private void sendToRemainingNeighbor(MessageData data){
		// find out which neighbor didn’t send any data yet
		int counter = 0;
		for(Vertice neighbor : getNeighbors()){
			if(checkVerticeType(neighbor) && !didSendData((TDTIVertice) neighbor)){
				if(counter > 0){ System.out.println("# ERROR: sendToRemainingNeighbor found more than one neighbor"); return; }
				// this neighbor didn’t send any data
				((TDTIVertice) neighbor).receive(data);
				counter++;
			}
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

	private MessageData computeMessageDataFromMessages(List<MessageData> data){
		if(data.size() == 0){ System.out.println("# ERROR: computeMessageDataFromMessages got empty input list"); return null; }
		int a = 0;
		int c = 0;
		if(data.size() == 1){
			a = data.get(0).getA();
			c = data.get(0).getC()+1;
			return new MessageData(a,c,this);
		}
		// sort data to get the maximum values:
		Collections.sort(data, new MessageDataComparator());
		MessageData max1 = dataReceived.get(0);
		MessageData max2 = dataReceived.get(1);
		if((max1.getA() == max2.getA()) && (max2.getC() > gui.IMMUNITY_TIME/2)){
			a = max1.getA()+1;
			c = 1;
		} else {
			a = max1.getA();
			c = max1.getC()+1;
		}
		return new MessageData(a,c,this);
	}

	private int calculatePsiFromMessages(List<MessageData> data){
		if(data.size() == 0){ System.out.println("# ERROR: calculatePsiFromMessages got empty input list"); return -1; }
		return computeMessageDataFromMessages(data).getA();
	}

	private void receive(MessageData data){
		if(gui.remainingSteps == 0){
			return;
		}
		gui.remainingSteps--;

		System.out.println("Received data from "+data.getSender());

		this.dataReceived.add(data);
		int neighborCount = this.numberOfNeighbors();
		int dataCount = this.dataReceived.size();


		if(state == states.DONE){
			System.out.println("# ERROR: Received message in state DONE");
			return;
		}

		if(state == states.READY){
			if(dataCount == neighborCount){
				if(neighborCount != 1){ System.out.println("# ERROR: Received last message in state READY with neighborcount != 1"); return; }
				// receiving last message in state ready means this is a leaf
				// -> be DONE
				// -> calculate own Psi
				// -> send (1,1) to only neighbor
				state = states.DONE;
				psi = calculatePsiFromMessages(dataReceived);
				((TDTIVertice)getNeighbors().get(0)).receive(new MessageData(1,1,this));
			}
			if(dataCount == neighborCount-1){
				if(neighborCount <= 1){ System.out.println("# ERROR: Received second to last message in state READY with neighborcount <= 1"); return; }
				// received second to last message
				// -> be COMPUTING
				// -> compute data from all of those and send computed data to missing neighbor
				state = states.COMPUTING;
				MessageData newData = computeMessageDataFromMessages(dataReceived);
				sendToRemainingNeighbor(newData);
			}
			if(dataCount < neighborCount-1){
				// didn't receive enough messages yet
				// -> wait for more messages
			}
			return;
		}

		if(state == states.COMPUTING){
			if(dataCount != neighborCount){ System.out.println("# ERROR: Received message, that was not last one, in state COMPUTING"); return; }
			// Received last message in state computing
			// -> be DONE
			// -> calculate my Psi
			// -> compute and send data to all neighbors except the sender of this last message
			state = states.DONE;
			psi = calculatePsiFromMessages(dataReceived);
			TDTIVertice lastSender = data.getSender();
			redirectReceivedDataExceptTo(lastSender);
			return;
		}
	}

	private void redirectReceivedDataExceptTo(TDTIVertice exceptTo){
		List<Vertice> neighbors = getNeighbors();
		System.out.println("Redirect Data except to "+exceptTo);

		for(Vertice neighbor : neighbors){
			if(neighbor != exceptTo && checkVerticeType(neighbor)){
				// send this neighbor the data computed from the other neighbors
				//System.out.println("Redirecting to "+(TDTIVertice)neighbor);
				((TDTIVertice) neighbor).receive(this.computeDataExceptFromNeighbor((TDTIVertice) neighbor));
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

		if((max1.getA() == max2.getA()) && max2.getC() > gui.IMMUNITY_TIME/2){
			return new MessageData(max1.getA()+1,1,this);
		} else {
			return new MessageData(max1.getA(),max1.getC()+1,this);
		}
	}

	protected Color getColor(){
		if(this.contaminated == false){
			return decontaminatedColor;
		}
		if(this.state == states.COMPUTING){
			return computingColor;
		}
		if(this.state == states.DONE){
			return doneColor;
		}
		return Color.white;
	}

	protected void drawAllVertice(Graphics g){
		Color fillColor = getColor();

		if(immunityTime > 0){
			System.out.println("Drawing Immunity Timer "+immunityTime);
			g.setColor(new Color(0x00,0x00,0x00,0xaa));
			g.fillArc(xMiddle-diameter/2-3,yMiddle-diameter/2-3,diameter+6,diameter+6,90,360*immunityTime/gui.IMMUNITY_TIME);
		}

		super.drawAllVertice(g,fillColor);

		if(psi > 0){
			g.setColor(new Color(0x33,0x44,0x55));
			String string = String.valueOf(psi);
			int stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(string,g).getWidth());
			g.drawString(string, xMiddle - stringWidth/2, yMiddle+diameter/4);
		}

		drawMessages(g);
	}

	protected void drawMessages(Graphics g){
		for(MessageData data : dataReceived){
			TDTIVertice sender = data.getSender();
			String message = data.guiString();
			int messageWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(message,g).getWidth());
			int msgX = (sender.getMiddleX() + this.getMiddleX()*2)/3 - messageWidth/2;
			int msgY = (sender.getMiddleY() + this.getMiddleY()*2)/3;
			g.setColor(new Color(255,255,255,200));
			g.fillRect(msgX-2,msgY-14,messageWidth+4,18);
			g.setColor(new Color(0x33,0x44,0x55));
			g.drawString(message,msgX,msgY);
		}
	}

	private boolean checkVerticeType(Vertice vertice){
		if(!(vertice instanceof TDTIVertice)){
			System.out.println("wrong VerticeType!!! FATAL ERROR");
			return false;
		}
		return true;
	}

	public TDTIVertice getMinimumVerticeOfSubtree(){
		if(this.children.size() == 0){
			return this;
		}
		TDTIVertice min = this;
		for(Vertice childVertice : this.children){
			if(childVertice instanceof TDTIVertice){
				TDTIVertice child = (TDTIVertice) childVertice;
				TDTIVertice childMin = child.getMinimumVerticeOfSubtree();
				if(childMin.getPsi() < min.getPsi()){
					min = childMin;
				}
			}
		}
		return min;
	}

	@Override
	public String toString(){
		return "Vertice ("+this.name+") ("+this.children.size()+" children) ("+this.state+") ("+this.psi+" minAgents)";
	}

	public int getPsi(){
		return psi;
	}

	public TDTIVertice getContaminatedNeighborWithSmallestMessage(){
		MessageData msg = getSmallestContaminatedNeighborMessage();
		if(msg != null){
			return msg.getSender();
		}
		if(parent != null){
			return (TDTIVertice)parent;
		}
		return null;
	}

	public MessageData getSmallestContaminatedNeighborMessage(){
		Collections.sort(dataReceived, new MessageDataComparator());
		Collections.reverse(dataReceived);
		MessageData msg = null;
		for(MessageData data : dataReceived){
			if(data.getSender().isContaminated()){
				msg = data;
				break;
			}
		}
		return msg;
	}

	public boolean isContaminated(){
		return contaminated;
	}

	public void decontaminate(){
		immunityTime = gui.IMMUNITY_TIME;
		contaminated = false;
		System.out.println("Decontaminated "+this);
	}

	public void decreaseImmunityTimesOfSubtree(){
		if(coordinator.getAgentsAtVertice(this).size() == 0){
			immunityTime = Math.max(0,immunityTime-1);
		} else {
			// Some Agent is currently staying on this vertice
			// -> do not decrease immunity time
		}
		for(Vertice child : children){
			if(child instanceof TDTIVertice){
				((TDTIVertice)child).decreaseImmunityTimesOfSubtree();
			}
		}
	}

	public int numberOfContaminatedNeighbors(){
		int contaminatedNeighbors = 0;
		List<Vertice> neighbors = this.getNeighbors();
		for(Vertice neighbor : neighbors){
			if(neighbor instanceof TDTIVertice){
				TDTIVertice n = (TDTIVertice)neighbor;
				if(n.isContaminated()){
					contaminatedNeighbors++;
				}
			} else {
				System.out.println("# ERROR: Neighbor is not a TDTIVertice");
			}
		}
		return contaminatedNeighbors;
	}
}
