package cima;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Gui.Gui;
import Tree.Vertice;

public class CIMAVertice extends Vertice{

	private int edgeWeightToParent;
	private int verticeWeight;
	private int specialVerticeWeight;
	public static boolean drawMu = false;
	private Color stringColor = Color.black;
//	private boolean verticeStarted = false;
	private int mu;
	private List<MessageData> lamdas = new ArrayList<MessageData>();

	//weightField
	int ovalWidth = 17;
	int ovalMiddleX = -1;
	int ovalMiddleY = -1;
	
	//animation
    protected Gui gui = CIMAGui.getGui();
    protected double xMiddleAnimation;
    protected double yMiddleAnimation;
    protected boolean activeAgent = false;
    public static boolean activeAnimation = false;
    private boolean marked = false;
    private static int animationSpeed = 3;
    
    protected int currentAgents = 0;
    protected int moveAgentCounter = 0;
    protected boolean decontaminated = false;
    protected Color verticeColor = Color.white;

	//agentWaayList
	public static List<AgentWayData> agentWayList = new ArrayList<AgentWayData>();
	public static List<MessageData> messageDataList = new ArrayList<MessageData>();


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
	protected void drawSubtree(Graphics g, int areaX, int areaY, int areaWidth,
			int areaHeight) {
		super.drawSubtree(g, areaX, areaY, areaWidth, areaHeight);
		drawAnimation(g);
	}

	@Override
	protected void drawAllVertice(Graphics g){

		//chose color
		if(CIMAVertice.drawMu){
			if(activeAnimation){
				if(decontaminated){
					verticeColor = Color.GREEN;
				}else{
	//				verticeColor = Color.RED;
					verticeColor = new Color(255, 50, 0);
				}
				
			}else{
				verticeColor = Color.white;
			}
		}else{
			if(!MessageData.animationInProgress){
				verticeColor = Color.white;
			}
		}
		super.drawAllVertice(g, verticeColor);

		String stringVertice;
		String displayedInfoString = "";
		if(activeAnimation){
//			g.setColor(Color.white);
			g.setColor(Color.black);
			stringVertice = String.valueOf(currentAgents);
			displayedInfoString = "aktuelle Agentenanzahl";
			int stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(stringVertice,g).getWidth());
			g.drawString(stringVertice, xMiddle - stringWidth/2, yMiddle+diameter/4);
		}else{
			if(drawMu){
				stringVertice = String.valueOf(mu);
				displayedInfoString = "minimale Agenten als Homebase";
			}else{
				calcGeneralVerticeWeight();
				stringVertice = String.valueOf(verticeWeight);
				displayedInfoString = "Knotengewichte";
				if(MessageData.animationInProgress && marked){
					stringVertice = String.valueOf(specialVerticeWeight);
				}
			}
			g.setColor(stringColor);
			int stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(stringVertice,g).getWidth());
			g.drawString(stringVertice, xMiddle - stringWidth/2, yMiddle+diameter/4);
		}
		
		//draw the displayedInfo - string in upper right corner
		Font defaulFont = g.getFont();
		g.setFont(new Font("Arial", Font.BOLD, 12));
		int stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(displayedInfoString,g).getWidth());
//		g.drawString(displayedInfoString, CIMAGui.getGui().getWidth() - stringWidth, CIMAGui.getGui().getHeight());
		g.drawString(displayedInfoString, CIMAGui.getGui().getWidth() - 5 - stringWidth, 12);
		g.setFont(defaulFont);
		

	}

	@Override
	protected void drawAllTreeLines(Graphics g) {
		super.drawAllTreeLines(g);

		if(parent != null){
			g.setColor(Color.lightGray);
			ovalMiddleX = Math.min(xMiddle, parent.getMiddleX()) + Math.abs(xMiddle - parent.getMiddleX()) / 2;
			ovalMiddleY = Math.min(yMiddle, parent.getMiddleY()) + Math.abs(yMiddle - parent.getMiddleY()) / 2;
			g.fillOval(ovalMiddleX - ovalWidth/2, ovalMiddleY - ovalWidth/2, ovalWidth, ovalWidth);

			g.setColor(Color.black);
			String string = String.valueOf(edgeWeightToParent);
			int stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(string,g).getWidth());
			g.drawString(string, ovalMiddleX - stringWidth/2, ovalMiddleY+diameter/4);


//			g.drawString(""+edgeWeightToParent, Math.min(xMiddle, parent.getMiddleX()) + Math.abs(xMiddle - parent.getMiddleX()) / 2, Math.min(yMiddle, parent.getMiddleY()) + Math.abs(yMiddle - parent.getMiddleY()) / 2);
		}
	}
	
    protected void drawAnimation(Graphics g) {
    	
    	//no active Animation, probl animation canceled
//    	if(!activeAnimation){
//    		return;
//    	}
    	
    	if(activeAgent){
//    		g.setColor(Color.black);
    		g.setColor(Color.green);
	        g.fillOval((int)(xMiddleAnimation - diameter/2), (int)(yMiddleAnimation - diameter/2), diameter, diameter);
			g.setColor(new Color(0x33,0x44,0x55));
			g.drawOval((int)(xMiddleAnimation - diameter/2), (int)(yMiddleAnimation - diameter/2), diameter, diameter);
			g.setColor(Color.black);
			String string = String.valueOf(moveAgentCounter);
			int stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(string,g).getWidth());
			g.drawString(string, (int)(xMiddleAnimation - stringWidth/2), (int)(yMiddleAnimation + diameter/4));
    	}
	        
		for(Vertice child : children){
			((CIMAVertice)child).drawAnimation(g);;
		}
    }
    
	public void resetAllVerticeAnimation(){
		resetCurrentAgents();
		
		for(Vertice vertice : children){
			if(((CIMAVertice)vertice).isDecontaminated()){
				((CIMAVertice)vertice).resetAllVerticeAnimation();
			}
		}
		if(parent != null){
			if(((CIMAVertice)parent).isDecontaminated()){
				((CIMAVertice)parent).resetAllVerticeAnimation();
			}
		}
	}
	
	public void resetCurrentAgents(){
		this.currentAgents = 0;
		decontaminated = false;
	}
	public void changeCurrentAgents(int number){
		this.currentAgents += number;
		decontaminated = true;
	}


	/************************************************************************************/
	/**********************************ALGORITHMUS***************************************/
	/************************************************************************************/

	public void algorithmus(){

		reset();
		startAlgo();

				calcMu();
		logSubtree();

		drawMu = true;
	}

	public void reset(){
		lamdas.clear();
//		verticeStarted = false;
		for(MessageData msgData : messageDataList){
			msgData.resetAllColors();
		}
		messageDataList.clear();
		agentWayList.clear();
		resetCurrentAgents();

		calcGeneralVerticeWeight();

		//call reset() recursive to all children
		for(Vertice child : children){
			if(!(child instanceof CIMAVertice)){
				return;
			}
			((CIMAVertice) child).reset();
		}
	}

	private void startAlgo(){
		if(children.size() == 0 && parent != null){
			//got a ready leaf -> send message
//			if(verticeStarted == false){
//				verticeStarted = true;
			specialVerticeWeight = calcSpecialVerticeWeight((CIMAVertice)parent);
				((CIMAVertice) parent).receive(new MessageData(specialVerticeWeight, this, (CIMAVertice) parent, null, null, specialVerticeWeight));
//			}
		}else{
			for(Vertice child : children){
				if(!(child instanceof CIMAVertice)){
					return;
				}
				((CIMAVertice) child).startAlgo();
			}
		}

//		if(children.size() == 0 && parent != null){
//got a ready leaf -> send message
//			((CIMAVertice) parent).receive(new MessageData(verticeWeight, this));
//		}else{
//			((CIMAVertice) children.get(0)).startAlgo();
//		}
	}

	private void receive(MessageData data){
		this.lamdas.add(data);
		messageDataList.add(data);

		if(lamdas.size() == numberOfNeighbors() -1){
			//TODO *
			computeLamdasAndSendTo(getMissingNeightbour());
		}else if(lamdas.size() == numberOfNeighbors()){
			//TODO **
			if(lamdas.size() == 1 && children.size() == 0){
				//leaf, dont send aganin ? (-> check if it has started)
//				if(verticeStarted == false){
//					verticeStarted = true;
//					computeLamdasAndSendTo(data.getSender());
//				}
			}else if(lamdas.size() == 1 && children.size() > 0){
				//root with just one child
				computeLamdasAndSendTo(data.getSender());
			}else{
				computeAllLamdasExeptFor(data.getSender());
			}
		}
	}

	private void computeAllLamdasExeptFor(CIMAVertice exeptVertice){
		for(Vertice child : children){
			if(!(child instanceof CIMAVertice)){
				return;
			}
			if(((CIMAVertice) child) != exeptVertice){
				computeLamdasAndSendTo((CIMAVertice) child);
			}
		}
		if(parent != null){
			if(!(parent instanceof CIMAVertice)){
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
		MessageData max1 = new MessageData(0, null, null, null, null, 0);
		MessageData max2 = new MessageData(0, null, null, null, null, 0);
//		int otherVerticeWeight = 0;
		if(maximums.size() >= 1){
			max1 = maximums.get(0);
		}
		if(maximums.size() >= 2){
			max2 = maximums.get(1);
//			otherVerticeWeight = maximums.get(1).getSender().getVerticeWeight();
		}

		//int maxValue = Math.max(max1.getLamdaValue(), max2.getLamdaValue() + verticeWeight);//TODO get verticeWeight from other vertice
		MessageData calcMessageData;
		specialVerticeWeight = calcSpecialVerticeWeight(receiverNode);
		if(max1.getLamdaValue() >= max2.getLamdaValue() + specialVerticeWeight){
			calcMessageData = new MessageData(max1.getLamdaValue(), this, receiverNode, max1, max2, specialVerticeWeight);
		}else{
			calcMessageData = new MessageData(max2.getLamdaValue() + specialVerticeWeight, this, receiverNode, max1, max2, specialVerticeWeight);
		}
		
		//make sure the lamdaValue is not sless then the edgeWeight
		if(receiverNode == parent){
			if(edgeWeightToParent > calcMessageData.getLamdaValue()){
				calcMessageData = new MessageData(edgeWeightToParent, this, receiverNode, max1, max2, specialVerticeWeight);
			}
		}else{
			if(receiverNode.getEdgeWeightToParent() > calcMessageData.getLamdaValue()){
				calcMessageData = new MessageData(receiverNode.getEdgeWeightToParent(), this, receiverNode, max1, max2, specialVerticeWeight);
			}
		}

		receiverNode.receive(calcMessageData);
	}

	private CIMAVertice getMissingNeightbour(){
		for(Vertice neighbor : children){
			if(!(neighbor instanceof CIMAVertice)){
				return null;
			}
			if(! didSendData((CIMAVertice) neighbor)){
				return (CIMAVertice) neighbor;
			}
		}
		if(parent != null){
			if(!(parent instanceof CIMAVertice)){
				return null;
			}
			if(!didSendData((CIMAVertice) parent)){
				return (CIMAVertice) parent;
			}
		}
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
	
	private void calcGeneralVerticeWeight(){
		//calc the verticeWeight
		verticeWeight = edgeWeightToParent;
		for(Vertice child : children){
			if(!(child instanceof CIMAVertice)){
				return;
			}

			if(((CIMAVertice) child).getEdgeWeightToParent() > verticeWeight){
				verticeWeight = ((CIMAVertice) child).getEdgeWeightToParent();
			}
		}
	}
	
	private int calcSpecialVerticeWeight(CIMAVertice exeptVertice){
		
		int specialVerticeWeight = 0;
		
		List<Vertice> allNeighbors = getNeighbors();
		
		if(allNeighbors.size() == 1){
			specialVerticeWeight = verticeWeight;
		}
		else{
			for(Vertice vertice : allNeighbors){
				if((CIMAVertice)vertice != exeptVertice){
					if(vertice == parent){
						if(specialVerticeWeight < edgeWeightToParent){
							specialVerticeWeight = edgeWeightToParent;
						}
					}else{
						if(specialVerticeWeight < ((CIMAVertice) vertice).getEdgeWeightToParent()){
							specialVerticeWeight = ((CIMAVertice) vertice).getEdgeWeightToParent();
						}
					}
				}
			}
		}		
	
		return specialVerticeWeight;
	}
	

	private void calcMu(){
		Collections.sort(lamdas, new MessageDataComparator());
		MessageData max1 = new MessageData(0, null, null, null, null, 0);
		MessageData max2 = new MessageData(0, null, null, null, null, 0);
		if(lamdas.size() >= 1){
			max1 = lamdas.get(0);
		}
		if(lamdas.size() >= 2){
			max2 = lamdas.get(1);
		}

		calcGeneralVerticeWeight();
		mu = Math.max(max1.getLamdaValue(), max2.getLamdaValue() + verticeWeight);


		for(Vertice child : children){
			if(!(child instanceof CIMAVertice)){
				return;
			}
			((CIMAVertice) child).calcMu();
		}
	}

	/////////////////

	private void calcAgentsMove(){

		CIMAAnimation animation = CIMAAnimation.getCIMAAnimation();
		
//		MessageData.animationInProgress = false;

		//animation läuft schon.... breche neue animation ab
		if(activeAnimation){
			animation.stopAgentAnimation();
			return;
		}

		agentWayList.clear();
		messageDataList.clear();
		
		CIMAVertice homeBase = findHomeBase();
		homeBase.resetAllVerticeAnimation();
		homeBase.changeCurrentAgents(homeBase.getMu());
		homeBase.moveAgents(null, 0);

	}
	
	public void doCompleteSendMessageAnimation(){
		
		//animation läuft schon.... breche die animation ab
		if(MessageData.animationInProgress && !CIMAAnimation.singeAnimationModus){
			CIMAAnimation.getCIMAAnimation().stopSendMessageAnimation();
			return;
		}

		if(!MessageData.animationInProgress){
			algorithmus();
		}
		
		
		CIMAAnimation.getCIMAAnimation().startSendMessageAnimation(messageDataList);
	}
	
	public void doStepSendMessageAnimation(){

		CIMAAnimation animation = CIMAAnimation.getCIMAAnimation();
//		if(!activeAnimation){
//			calcAgentsMove();
//		}
		
		if(!MessageData.animationInProgress){
			algorithmus();
		}
		animation.nextStepSendMessageAnimation(messageDataList);

	}

	public void doCompleteAgentAnimation(){
		//make sure the algo is calced //TODO
//		algorithmus();

		CIMAAnimation animation = CIMAAnimation.getCIMAAnimation();

		//animation läuft schon.... breche neue animation ab
		if(activeAnimation && !CIMAAnimation.singeAnimationModus){
			animation.stopAgentAnimation();
			return;
		}
		if(!activeAnimation){
			calcAgentsMove();
		}

//		calcAgentsMove();
		animation.startAgentAnimation(agentWayList);
	}

	public void doStepAgentAnimation(){

		CIMAAnimation animation = CIMAAnimation.getCIMAAnimation();

		if(!activeAnimation){
			calcAgentsMove();
		}


		animation.nextStepAgentAnimation(agentWayList);

	}

	public CIMAVertice findHomeBase(){
		CIMAVertice currentHomeBase = this;
		for(Vertice child : children){
			CIMAVertice testVertice = ((CIMAVertice) child).findHomeBase();
			if(testVertice.getMu() < currentHomeBase.getMu()){
				currentHomeBase = testVertice;
			}
		}
		return currentHomeBase;
	}

	private int moveAgents(CIMAVertice sender, int agentNumber){

		Collections.sort(lamdas, new MessageDataComparator());
		for(int i = lamdas.size() - 1; i >= 0; i--){

			if(sender == null  || !(lamdas.get(i).getSender().equals(sender))){
				agentWayList.add(new AgentWayData(this, lamdas.get(i).getSender(), lamdas.get(i).getLamdaValue()));
				lamdas.get(i).getSender().moveAgents(this, lamdas.get(i).getLamdaValue());
			}
		}

		agentWayList.add(new AgentWayData(this, sender, agentNumber));
		return agentNumber;
	}



	@Override
	public String toString(){
		return "##Vertice "+this.name+"; "+this.children.size()+" children; mu: "+this.mu+"; currentAgents: "+currentAgents+"\n"
				+ "		all lamda: "+getAlllamda();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof CIMAVertice){
			if(this.getName().equals(((CIMAVertice) obj).getName())){
				return true;
			}
		}
		return false;
	}

	private String getAlllamda(){

		String alllamda = "   ";
		for(MessageData msgData : lamdas){
			alllamda += msgData.toString() + "  /  ";
		}

		return alllamda;
	}

	public boolean onEdgeWeightClick(int x, int y){
		if((Math.abs(this.ovalMiddleX - x) <= ovalWidth/2) && (Math.abs(this.ovalMiddleY - y) < ovalWidth/2)){
			return true;
		}
		return false;
	}
	public Vertice edgeWeightOvalExists(int x, int y){

		if(onEdgeWeightClick(x, y)){
			return this;
		}
		for(Vertice child : children){
			Vertice vertice = ((CIMAVertice) child).edgeWeightOvalExists(x, y);
			if(vertice != null){
				return vertice;
			}
		}
		return null;
	}

	public void edgeWeightIncrease(){
		this.edgeWeightToParent++;
	}
	public void edgeWeightDepress(){
		this.edgeWeightToParent--;
		if(this.edgeWeightToParent <= 0){
			this.edgeWeightToParent = 1;
		}
	}
	public void setEdgeWeightToParent(int edgeWeight){
		this.edgeWeightToParent = edgeWeight;
	}
	public int getEdgeWeightToParent(){
		return edgeWeightToParent;
	}
	public int getMu(){
		return mu;
	}
	public int getVerticeWeight(){
		return verticeWeight;
	}
	public void markAsMax(int specialVerticeWeight){
		this.specialVerticeWeight = specialVerticeWeight;
//		stringColor = CIMAConstants.getMarkAsMaxColor();
		verticeColor = CIMAConstants.getMarkAsMaxColor();
		marked = true;
	}
	public void markAsSecMax(int specialVerticeWeight){
		this.specialVerticeWeight = specialVerticeWeight;
//		stringColor = CIMAConstants.getMarkAsSecMaxColor();
		verticeColor = CIMAConstants.getMarkAsSecMaxColor();
		marked = true;
	}
	public void resetColor(){
//		stringColor = Color.black;
		verticeColor = Color.white;
		marked = false;
	}
	public boolean isDecontaminated(){
		return decontaminated;
	}
	public static void setAnimationSpeed(int animationSpeed){
		if(animationSpeed >= 0 && animationSpeed <= 10){
			CIMAVertice.animationSpeed = animationSpeed;
		}
	}
	public static int getAnimationSpeed(){
		return animationSpeed;
	}
	
	
	
	
	public AgentAnimationTimer animation(Vertice destVertice, int moveAgentCounter){
		this.moveAgentCounter = moveAgentCounter;
		AgentAnimationTimer timer = new AgentAnimationTimer(destVertice);
		timer.start();
		return timer;
	}
	
	public class AgentAnimationTimer extends Thread{
		
		Vertice destVertice;
		
		public AgentAnimationTimer(Vertice destVertice) {
			this.destVertice = destVertice;
		}
		 
		@Override
		public void run() {
			
			xMiddleAnimation = xMiddle;
			yMiddleAnimation = yMiddle;
			
			
			activeAgent = true;
			
			while(isInterrupted() == false){
				
				double vektorX = destVertice.getMiddleX() - xMiddleAnimation;
				double vektorY = destVertice.getMiddleY() - yMiddleAnimation;
				
				double vektorLength = Math.sqrt(vektorX*vektorX + vektorY*vektorY);
				
				xMiddleAnimation += animationSpeed * vektorX/vektorLength;
				yMiddleAnimation += animationSpeed * vektorY/vektorLength;
				
				gui.repaint();
								
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if(vektorLength < 2*animationSpeed){
					this.interrupt();
				}
			}
			
			activeAgent = false;
			gui.repaint();
		}
	}

}
