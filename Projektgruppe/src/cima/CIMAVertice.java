package cima;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sound.sampled.ReverbType;

import cima.Gui;
import cima.Vertice;

public class CIMAVertice extends Vertice{

	private CIMAEdgeWeight edgeWeightToParent;
	private int verticeWeight;
	private int specialVerticeWeight;
	public static boolean drawMu = false;
	private Color stringColor = Color.black;
	private int mu;
	private List<MessageData> lamdas = new ArrayList<MessageData>();
	private PotentialData potentialData;
	private static int minimalMu;
	private static List<CIMAEdgeWeight> potentialEdges= new ArrayList<CIMAEdgeWeight>();
	private static InfoDisplayClass infoDisplayClass = new InfoDisplayClass();

	
	//animation
    protected Gui gui = CIMAGui.getGui();
    protected double xMiddleAnimation;
    protected double yMiddleAnimation;
    protected boolean activeAgent = false;
    public static boolean activeAnimation = false;
    private boolean marked = false;
    private static int animationSpeed = 3;
    private static String displayedInfoString ="";
    private boolean drawPotentialData = false;
    
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
			this.edgeWeightToParent = new CIMAEdgeWeight(edgeWeightToParent, this, (CIMAVertice)parent);
		}else{
			this.edgeWeightToParent = new CIMAEdgeWeight(0, this, null);
		}
	
	}
	
	@Override
	protected void drawSubtree(Graphics g, int areaX, int areaY, int areaWidth,
			int areaHeight) {
		super.drawSubtree(g, areaX, areaY, areaWidth, areaHeight);
	}

	@Override
	protected void drawAllVertice(Graphics g){

		//check if color should be chosen
		if(drawPotentialData){
//			drawPotentialMessage(g);//TODO
		}else{
			//chose color
			if(CIMAVertice.drawMu){
				if(activeAnimation){
					if(decontaminated){
						verticeColor = Color.GREEN;
					}else{
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
		}
		super.drawAllVertice(g, verticeColor);

		String stringVertice;
		displayedInfoString = "";
		if(activeAnimation){
			g.setColor(Color.black);
			stringVertice = String.valueOf(currentAgents);
			displayedInfoString = "aktuelle Agentenanzahl";
			int stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(stringVertice,g).getWidth());
			Font defaultFont = g.getFont();
			g.setFont(CIMAConstants.getTextFont());
			g.drawString(stringVertice, xMiddle - stringWidth/2, yMiddle+diameter/4);
			g.setFont(defaultFont);
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
			Font defaultFont = g.getFont();
			g.setFont(CIMAConstants.getTextFont());
			g.drawString(stringVertice, xMiddle - stringWidth/2, yMiddle+diameter/4);
			g.setFont(defaultFont);
		}		

	}

	public static void drawDisplayInformation(Graphics g){
		//draw the displayedInfo - string in upper right corner
//		Font defaulFont = g.getFont();
//		g.setFont(CIMAConstants.getTextFont());
//		int stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(displayedInfoString,g).getWidth());
//		g.drawString(displayedInfoString, CIMAGui.getGui().getWidth() - 5 - stringWidth, 12);
//		g.setFont(defaulFont);
		
		infoDisplayClass.displayInUpperRightCorner(g, displayedInfoString, 1, Color.BLACK, null);
	}
	
	
	@Override
	protected void drawAllTreeLines(Graphics g) {
		super.drawAllTreeLines(g);

		if(parent != null){
			edgeWeightToParent.draw(g);
		}
	}
	
//	protected void drawPotentialData(Graphics g){
//		
//		System.out.println("name : "+name);
//		 
//		if(drawPotentialData){
//			System.out.println("draw potential??");
//			potentialData.draw(g);
//		}
//		
//		for(Vertice child : children){
//			((CIMAVertice)child).drawPotentialData(g);
//		}
//	}
	
    public void drawAnimation(Graphics g) {
    	
    	if(activeAgent){
    		g.setColor(Color.green);
	        g.fillOval((int)(xMiddleAnimation - diameter/2), (int)(yMiddleAnimation - diameter/2), diameter, diameter);
			g.setColor(new Color(0x33,0x44,0x55));
			g.drawOval((int)(xMiddleAnimation - diameter/2), (int)(yMiddleAnimation - diameter/2), diameter, diameter);
			g.setColor(Color.black);
			String string = String.valueOf(moveAgentCounter);
			int stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(string,g).getWidth());
			Font defaultFont = g.getFont();
			g.setFont(CIMAConstants.getTextFont());
			g.drawString(string, (int)(xMiddleAnimation - stringWidth/2), (int)(yMiddleAnimation + diameter/4));
			g.setFont(defaultFont);
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

		minimalMu = Integer.MAX_VALUE;
		calcMu();
		logSubtree();

		MessageData.clearGui = false;
		drawMu = true;
	}

	public void reset(){
		lamdas.clear();
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
			specialVerticeWeight = calcSpecialVerticeWeight((CIMAVertice)parent);
				((CIMAVertice) parent).receive(new MessageData(specialVerticeWeight, this, (CIMAVertice) parent, edgeWeightToParent, null, null, specialVerticeWeight, null, null, new PotentialData(edgeWeightToParent)));
		}else{
			for(Vertice child : children){
				if(!(child instanceof CIMAVertice)){
					return;
				}
				((CIMAVertice) child).startAlgo();
			}
		}
	}

	private void receive(MessageData data){
		this.lamdas.add(data);
		messageDataList.add(data);

		if(lamdas.size() == numberOfNeighbors() -1){
			computeLamdasAndSendTo(getMissingNeightbour());
		}else if(lamdas.size() == numberOfNeighbors()){
			if(lamdas.size() == 1 && children.size() == 0){
				
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
		
//		CIMAEdgeWeight edgeToReceiverNode = null;
//		if(receiverNode == parent){
//			edgeToReceiverNode = edgeWeightToParent;
//		}else{
//			edgeToReceiverNode = receiverNode.getEdgeWeightToParent();
//		}
//		
//		Collections.sort(lamdas, new MessageDataComparator());
//		List<MessageData> maximums = new ArrayList<MessageData>();
//		for(MessageData data : lamdas){
//			if(data.getSender() != receiverNode){
//				maximums.add(data);
//			}
//			if(maximums.size() == 2){
//				break;
//			}
//		}
//		
//		MessageData biggestMsgData = new MessageData();
//		if(maximums.size() >= 1){
//			biggestMsgData = maximums.get(0);
//		}
//		
//		CIMAEdgeWeight max1 = calcMaxOrSecmaxEdge(receiverNode, true);
//		CIMAEdgeWeight max2 = calcMaxOrSecmaxEdge(receiverNode, false);
//		
//		MessageData calcMessageData;
//		specialVerticeWeight = calcSpecialVerticeWeight(receiverNode);
//		if(max1.getLamdaValue() >= max2.getLamdaValue() + specialVerticeWeight){
//			calcMessageData = new MessageData(max1.getLamdaValue(), this, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, null, null);
//		}else{
//			calcMessageData = new MessageData(max2.getLamdaValue() + specialVerticeWeight, this, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, null, null);
//		}
//		
//		//make sure the lamdaValue is not less the biggest msgData
//		if(biggestMsgData.getLamdaValue() > calcMessageData.getLamdaValue()){
////			calcMessageData = biggestMsgData;
//			calcMessageData = new MessageData(biggestMsgData.getLamdaValue(), this, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, biggestMsgData, null);
//		}
//		
//		//make sure the lamdaValue is not less then the edgeWeight
//		if(receiverNode == parent){
//			if(edgeWeightToParent.getEdgeWeightValue() > calcMessageData.getLamdaValue()){
//				calcMessageData = new MessageData(edgeWeightToParent.getEdgeWeightValue(), this, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, null, edgeWeightToParent);
//			}
//		}else{
//			if(receiverNode.getEdgeWeightToParent().getEdgeWeightValue() > calcMessageData.getLamdaValue()){
//				calcMessageData = new MessageData(receiverNode.getEdgeWeightToParent().getEdgeWeightValue(), this, receiverNode, edgeToReceiverNode, max1, max2, specialVerticeWeight, null, receiverNode.getEdgeWeightToParent());
//			}
//		}
//
//		receiverNode.receive(calcMessageData);
		
		receiverNode.receive(new ModellMinimalDanger().calcMessageData(this, receiverNode));
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
		verticeWeight = edgeWeightToParent.getEdgeWeightValue();
		for(Vertice child : children){
			if(!(child instanceof CIMAVertice)){
				return;
			}

			if(((CIMAVertice) child).getEdgeWeightToParent().getEdgeWeightValue() > verticeWeight){
				verticeWeight = ((CIMAVertice) child).getEdgeWeightToParent().getEdgeWeightValue();
			}
		}
	}
	
	public int calcSpecialVerticeWeight(CIMAVertice exeptVertice){
		if(getNeighbors().size() == 1){
			return verticeWeight;
		}
		return calcMaxOrSecmaxEdge(exeptVertice, true).get(0).getLamdaValue();	
	}
	
//	public CIMAEdgeWeight calcMaxOrSecmaxEdge(CIMAVertice exeptVertice, boolean maxEdge){
	public List<CIMAEdgeWeight> calcMaxOrSecmaxEdge(CIMAVertice exeptVertice, boolean maxEdge){
		CIMAEdgeWeight maxEdgeWeight = new CIMAEdgeWeight(this);
		CIMAEdgeWeight secMaxEdgeWeight = new CIMAEdgeWeight(this);
		CIMAEdgeWeight thirdMaxEdgeWeight = new CIMAEdgeWeight(this);
		
		List<Vertice> allNeighbors = getNeighbors();
		
//		if(allNeighbors.size() == 1){
////			maxEdgeWeight.setEdgeWeightToParent(verticeWeight);
//		}
//		else{
			for(Vertice vertice : allNeighbors){
				if((CIMAVertice)vertice != exeptVertice){
					if(vertice == parent){
						if(maxEdgeWeight.getEdgeWeightValue() < edgeWeightToParent.getEdgeWeightValue()){
							thirdMaxEdgeWeight = secMaxEdgeWeight;
							secMaxEdgeWeight = maxEdgeWeight;
							maxEdgeWeight = edgeWeightToParent;
						}else{
							if(secMaxEdgeWeight.getEdgeWeightValue() < edgeWeightToParent.getEdgeWeightValue()){
								thirdMaxEdgeWeight = secMaxEdgeWeight;
								secMaxEdgeWeight = edgeWeightToParent;
							}else{
								if(thirdMaxEdgeWeight.getEdgeWeightValue() < edgeWeightToParent.getEdgeWeightValue()){
									thirdMaxEdgeWeight = edgeWeightToParent;
								}
							}
						}
					}else{
						if(maxEdgeWeight.getEdgeWeightValue() < ((CIMAVertice) vertice).getEdgeWeightToParent().getEdgeWeightValue()){
							thirdMaxEdgeWeight = secMaxEdgeWeight;
							secMaxEdgeWeight = maxEdgeWeight;
							maxEdgeWeight = ((CIMAVertice) vertice).getEdgeWeightToParent();
						}else{
							if(secMaxEdgeWeight.getEdgeWeightValue() < ((CIMAVertice) vertice).getEdgeWeightToParent().getEdgeWeightValue()){
								thirdMaxEdgeWeight = secMaxEdgeWeight;
								secMaxEdgeWeight = ((CIMAVertice) vertice).getEdgeWeightToParent();
							}else{
								if(thirdMaxEdgeWeight.getEdgeWeightValue() < ((CIMAVertice) vertice).getEdgeWeightToParent().getEdgeWeightValue()){
									thirdMaxEdgeWeight = ((CIMAVertice) vertice).getEdgeWeightToParent();
								}
							}
						}
					}
				}
			}
//		}		
	
//		if(maxEdge){
//			return maxEdgeWeight;
//		}else{
//			return secMaxEdgeWeight;
//		}
		List<CIMAEdgeWeight> edgeWeightList = new ArrayList<CIMAEdgeWeight>();
		edgeWeightList.add(maxEdgeWeight);
		edgeWeightList.add(secMaxEdgeWeight);
		edgeWeightList.add(thirdMaxEdgeWeight);
		Collections.sort(edgeWeightList);
		
		return edgeWeightList;
	}
	

	private void calcMu(){		

		Collections.sort(lamdas, new MessageDataComparator());
		MessageData biggestMsgData = new MessageData();
		if(lamdas.size() >= 1){
			biggestMsgData = lamdas.get(0);
		}
		
		List<CIMAEdgeWeight> edgeWeightList = calcMaxOrSecmaxEdge(null, true);
//		Collections.sort(edgeWeightList);
		CIMAEdgeWeight max1 = edgeWeightList.get(0);
		CIMAEdgeWeight max2 = edgeWeightList.get(1);
		CIMAEdgeWeight max3 = edgeWeightList.get(2);
		
//		CIMAEdgeWeight max1 = calcMaxOrSecmaxEdge(null, true);
//		CIMAEdgeWeight max2 = calcMaxOrSecmaxEdge(null, false);
		
		System.out.println("Vertice : "+name);
		
		System.out.println("max1: "+ max1.getEdgeWeightValue());
		System.out.println("max2: "+ max2.getEdgeWeightValue());
		System.out.println("max3: "+ max3.getEdgeWeightValue());
		

		calcGeneralVerticeWeight();

		//default case max1 + max2
		if(max1.getEdgeWeightValue() == max3.getEdgeWeightValue()){
			potentialData = new PotentialData(this, true);
			System.out.println("case a");
		}else if(max2.getEdgeWeightValue() == 0){
			potentialData = new PotentialData(this, max1);
			System.out.println("case b");
		}else{
			potentialData = new PotentialData(this, max1, max2);
			System.out.println("case c");
		}
		mu = max1.getLamdaValue() + max2.getLamdaValue();

		//make sure mu is not less the biggest msgData
		if(mu <=  biggestMsgData.getLamdaValue()){
			
			if(mu == biggestMsgData.getLamdaValue()){
				
				System.out.println("show edges:");
				System.out.println("potentialData: "+ potentialData.getPotentialEdgeWeights());
				System.out.println("biggest.potential: "+biggestMsgData.getPotentialData().getPotentialEdgeWeights());
				
				//check if both potentialData has same number of edges
				if(potentialData.getNumberOfpotentialEdgeWeights() == biggestMsgData.getPotentialData().getNumberOfpotentialEdgeWeights()){
					for(CIMAEdgeWeight edge : biggestMsgData.getPotentialData().getPotentialEdgeWeights()){
						if(potentialData.hasSameEdge(edge)){
							//potential data shouldnt change
							System.out.println("case d_1");
						}else{
							potentialData = new PotentialData(this, true);
							System.out.println("case d_2");
						}
					}
				}else{
					potentialData = new PotentialData(this, true);
					System.out.println("case d_3");
				}
				
				
			}
			else{
				potentialData = biggestMsgData.getPotentialData().getPotentialDataCopy(this);
				System.out.println("case e");
				System.out.println(biggestMsgData.getPotentialData());
				System.out.println(potentialData);
			}
						
			mu = biggestMsgData.getLamdaValue();
		}
		
		potentialData.registerPotentialVertice(this);
		
		if(mu < minimalMu){//save the minimal MU
			minimalMu = mu;
			potentialEdges.clear();
		}
		
		//save all edges which decrement the minimal mu:
		if(mu == minimalMu){
			for(CIMAEdgeWeight edge : potentialData.getPotentialEdgeWeights()){
				if(!potentialEdges.contains(edge)){
					potentialEdges.add(edge);
				}
			}
		}

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

		//animation läuft schon.... breche neue animation ab
		if(activeAnimation){
			animation.stopAgentAnimation();
			return;
		}

		agentWayList.clear();
		
		CIMAVertice homeBase = findHomeBase();
		homeBase.resetAllVerticeAnimation();
		homeBase.changeCurrentAgents(homeBase.getMu());
		homeBase.moveAgents(null, 0);

	}
	
	public void doCompleteSendMessageAnimation(){
		
		if(CIMAAnimation.breakThread){
			return;
		}
		
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
		
		if(CIMAAnimation.breakThread){
			return;
		}

		CIMAAnimation animation = CIMAAnimation.getCIMAAnimation();
		
		if(!MessageData.animationInProgress){
			algorithmus();
		}
		animation.nextStepSendMessageAnimation(messageDataList);

	}

	public void doCompleteAgentAnimation(){
		
		if(CIMAAnimation.breakThread){
			return;
		}

		CIMAAnimation animation = CIMAAnimation.getCIMAAnimation();

		//animation läuft schon.... breche neue animation ab
		if(activeAnimation && !CIMAAnimation.singeAnimationModus){
			animation.stopAgentAnimation();
			return;
		}
		if(!activeAnimation){
			calcAgentsMove();
		}

		animation.startAgentAnimation(agentWayList);
	}

	public void doStepAgentAnimation(){
		
		if(CIMAAnimation.breakThread){
			return;
		}

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
		return "##Vertice "+this.name+"; "+this.children.size()+" children; mu: "+this.mu+"; currentAgents: "+currentAgents+"  potentialEdges: "+this.potentialData + "\n"
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


	public Vertice edgeWeightOvalExists(int x, int y){

		if(edgeWeightToParent != null){
			if(edgeWeightToParent.onEdgeWeightClick(x, y)){
				return this;
			}
		}
		for(Vertice child : children){
			Vertice vertice = ((CIMAVertice) child).edgeWeightOvalExists(x, y);
			if(vertice != null){
				return vertice;
			}
		}
		return null;
	}

	public CIMAEdgeWeight getEdgeWeightToParent(){
		return edgeWeightToParent;
	}
	public int getMu(){
		return mu;
	}
	public int getVerticeWeight(){
		return verticeWeight;
	}
	public List<MessageData> getLamdas(){
		return lamdas;
	}
	public void drawPotentialDataForThisNode(boolean drawPotentialData){
		
		resetDrawPotentialData();
		
		this.drawPotentialData = drawPotentialData;
		potentialData.prepareDraw();
		System.out.println("draw potential : "+drawPotentialData);
	}
	public void markColor(Color color){
		verticeColor = color;
	}
	public void markAsMax(int specialVerticeWeight){
		this.specialVerticeWeight = specialVerticeWeight;
		verticeColor = CIMAConstants.getMarkAsMaxColor();
		marked = true;
	}
	public void markAsSecMax(int specialVerticeWeight){
		this.specialVerticeWeight = specialVerticeWeight;
		verticeColor = CIMAConstants.getMarkAsSecMaxColor();
		marked = true;
	}
	public void resetColor(){
		verticeColor = Color.white;
		marked = false;
	}
	public void drawAllPotentialEdges(){
		resetDrawPotentialData();
		
		drawPotentialData = true;
		
		for(CIMAEdgeWeight edge : potentialEdges){
			if(edge.getEdgeWeightValue() > 1){
				edge.markColor(Color.ORANGE);
			}else{
				edge.markColor(Color.YELLOW);
			}
		}
	}
	
	public void drawPotentialMessage(Graphics g){
		if(potentialEdges.size() == 0){
			Font defaulFont = g.getFont();
			int stringHeight = (int) Math.floor(g.getFontMetrics().getStringBounds("stringheight",g).getHeight());
			g.setFont(CIMAConstants.getTextFont());
			g.setColor(CIMAConstants.getMarkAsMaxColor());

			String string = "Agentenanzahl kann nicht optimiert werden";
			int stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(string,g).getWidth());
			g.fillRect(3, 12 - stringHeight +1, stringWidth, stringHeight);
			g.setColor(Color.ORANGE);
			g.drawString(string, 3, 12);
			g.setFont(defaulFont);
		}
	}
	public void resetDrawPotentialData(){
		
		resetColor();
		if(edgeWeightToParent != null){
			edgeWeightToParent.resetColor();
		}
		potentialData.resetDraw();
		drawPotentialData = false;
		
		for(Vertice child : children){
			((CIMAVertice)child).resetDrawPotentialData();
		}
	}
	public boolean isDecontaminated(){
		return decontaminated;
	}
	public static void setAnimationSpeed(int animationSpeed){
		if(animationSpeed >= 0 && animationSpeed <= 10){
			CIMAVertice.animationSpeed = animationSpeed;
		}
	}
	public void setDrawPotentialData(boolean bool){
		drawPotentialData = bool;
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
