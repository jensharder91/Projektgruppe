package cima;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cima.Gui;
import cima.Vertice;

public class CIMAVertice extends Vertice implements IMarkable{

	private CIMAEdgeWeight edgeWeightToParent;
	private int verticeWeight;
	public static boolean drawMu = false;
	private Color defaultStringColor = Color.BLACK;
	private Color stringColor = Color.black;
	private int mu;
	private List<MessageData> lamdas = new ArrayList<MessageData>();
	private static int minimalMu;
	private static List<CIMAVertice> minimalMuVertices = new ArrayList<CIMAVertice>();
	private static ICalcStrategy calcStrategy = new ModellMinimalDanger();
	
	private static boolean showDisplayInfo = false;
	
	//pot >= 1
	private static int potential = 0;
	private int bestMu;
	private ArrayList<CIMAEdgeWeight> potentialEdges;

	
	//animation
    protected Gui gui = CIMAGui.getGui();
    protected boolean activeAgent = false;
    public static boolean activeAgentAnimation = false;
    private boolean marked = false;
    private static String displayedInfoString ="";
    private boolean drawPotentialData = false;
    
    protected int currentAgents = 0;
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
	protected void drawSubtree(Graphics2D g, int areaX, int areaY, int areaWidth,
			int areaHeight) {
		super.drawSubtree(g, areaX, areaY, areaWidth, areaHeight);
	}
	
	public static void drawDisplayInformation(CIMAVertice root, Graphics2D g2){
		
		InfoDisplayClass.getInfoDisplayClass().displayInUpperRightCorner(g2, displayedInfoString, 1, Color.BLACK, null);


		if(showDisplayInfo && drawMu){
			calcStrategy.displayResult(root, g2);
		}
	}

	@Override
	protected void drawAllVertice(Graphics g){
		
		//check if color should be chosen
		//chose color
		if(CIMAVertice.drawMu){
			if(activeAgentAnimation){
				if(decontaminated){
					verticeColor = Color.GREEN;
				}else{
					verticeColor = new Color(255, 50, 0);
				}
				
			}else{
				verticeColor = Color.white;
			}
		}
		
		super.drawAllVertice(g, verticeColor);

		String stringVertice;
		displayedInfoString = "";
		if(activeAgentAnimation){
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
				verticeWeight = calcStrategy.calcGeneralVerticeWeight(this);
				stringVertice = String.valueOf(verticeWeight);
				displayedInfoString = "Knotengewichte";
			}
			g.setColor(stringColor);
			int stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(stringVertice,g).getWidth());
			Font defaultFont = g.getFont();
			g.setFont(CIMAConstants.getTextFont());
			g.drawString(stringVertice, xMiddle - stringWidth/2, yMiddle+diameter/4);
			g.setFont(defaultFont);
		}		

	}
	
	
	@Override
	protected void drawAllTreeLines(Graphics g) {
		super.drawAllTreeLines(g);

		if(parent != null){
//			edgeWeightToParent.resetColor();
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
	
//    public void drawAnimation(Graphics g) {
//    	
//    	if(activeAgent){
//    		g.setColor(Color.green);
//	        g.fillOval((int)(xMiddleAnimation - diameter/2), (int)(yMiddleAnimation - diameter/2), diameter, diameter);
//			g.setColor(new Color(0x33,0x44,0x55));
//			g.drawOval((int)(xMiddleAnimation - diameter/2), (int)(yMiddleAnimation - diameter/2), diameter, diameter);
//			g.setColor(Color.black);
//			String string = String.valueOf(moveAgentCounter);
//			int stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(string,g).getWidth());
//			Font defaultFont = g.getFont();
//			g.setFont(CIMAConstants.getTextFont());
//			g.drawString(string, (int)(xMiddleAnimation - stringWidth/2), (int)(yMiddleAnimation + diameter/4));
//			g.setFont(defaultFont);
//    	}
//	        
//		for(Vertice child : children){
//			((CIMAVertice)child).drawAnimation(g);;
//		}
//    }
    
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
		
		showDisplayInfo = true;

		minimalMu = Integer.MAX_VALUE;
		calcMu();
		logSubtree();

		drawMu = true;
		
		
//		messageDataList.get(0).animation();

//		CIMAAnimation.getCIMAAnimation().startSendMessageAnimation(messageDataList);
		
	}

	public void reset(){
		
		showDisplayInfo = false;
		
		lamdas.clear();
		for(MessageData msgData : messageDataList){
			msgData.resetColor();
		}
		if(edgeWeightToParent != null){
			edgeWeightToParent.resetColor();
		}
		resetColor();
		messageDataList.clear();
		agentWayList.clear();
		resetCurrentAgents();

		verticeWeight = calcStrategy.calcGeneralVerticeWeight(this);

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
			
			((CIMAVertice) parent).receive(calcStrategy.calcMessageData(this, (CIMAVertice) getParent(), potential));
//			((CIMAVertice) parent).receive(new MessageData(specialVerticeWeight, this, (CIMAVertice) parent, edgeWeightToParent, null, null, specialVerticeWeight, null, null, new PotentialData(edgeWeightToParent)));
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
		
//		if(data.getSender() != null && data.getReceiver() != null){
//			System.out.println("+++++++++++++++++");
//			System.out.println("++");
//			System.out.println("++ message from "+data.getSender().getName()+" to "+data.getReceiver().getName() + " //  data: "+data.getLamdaValue());
//			System.out.println("++");
//			System.out.println("+++++++++++++++++");
//		}
		
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
		
		receiverNode.receive(calcStrategy.calcMessageData(this, receiverNode, potential));
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

	private void calcMu(){		

		mu = calcStrategy.calcMu(this, potential);
		
		if(mu < minimalMu){//save the minimal MU
			minimalMu = mu;
			minimalMuVertices.clear();
		}
		
		if(mu == minimalMu){
			minimalMuVertices.add(this);
		}

		for(Vertice child : children){
			if(!(child instanceof CIMAVertice)){
				return;
			}
			((CIMAVertice) child).calcMu();
		}
	}

	public CIMAVertice findHomeBase(){
		if(minimalMuVertices.size() > 0){
			return minimalMuVertices.get(0);//eig egal welcher
		}else{
			return null;
		}
	}
	

	@Override
	public String toString(){
		return "##Vertice "+this.name+"; "+this.children.size()+" children; mu: "+this.mu+"; currentAgents: "+currentAgents + "\n"
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
	@Override
	public void setOvalColor(Color color) {
		setOvalColor(color, defaultStringColor);
	}
	public void setOvalColor(Color color, Color textColor) {
		verticeColor = color;
		this.stringColor = textColor;
		marked = true;
	}
	@Override
	public int getValue() {
		return calcStrategy.calcGeneralVerticeWeight(this);
	}
	@Override
	public void resetColor(){
		verticeColor = Color.white;
		marked = false;
		this.stringColor = defaultStringColor;
	}
	
	public boolean isDecontaminated(){
		return decontaminated;
	}
	public void setDrawPotentialData(boolean bool){
		drawPotentialData = bool;
	}
	public static int getMinimalMu(){
		return minimalMu;
	}
	public static void setStrategy(ICalcStrategy strategy){
		CIMAVertice.calcStrategy = strategy;
	}
	public static void setPotential(int potential){
		CIMAVertice.potential = potential;
	}
	
	
	
	//potential k >= 1  und k == 1
	public void setBestMu(int bestMu){
		this.bestMu = bestMu;
	}
	public void setPotentialEdges(ArrayList<CIMAEdgeWeight> potentialEdges){
		this.potentialEdges = potentialEdges;
	}
	public int getBestMu(){
		return this.bestMu ;
	}
	public ArrayList<CIMAEdgeWeight> getPotentialEdges(){
		return this.potentialEdges;
	}
	
	
	/***ANIMATION*/
	
	public void animateMsgData(){
		animateMsgData(false);
	}
	public void animateMsgData(boolean singlestep){
		//falls noch keine animation (singel step) gestartet wurde... 
		if(!CIMAAnimation.getCIMAAnimation().singleStepAnimationIsInProgress()){
			algorithmus();
		}
		
		
		if(singlestep){
			CIMAAnimation.getCIMAAnimation().nextStepSendMessageAnimation(messageDataList);
		}else{
			CIMAAnimation.getCIMAAnimation().startSendMessageAnimation(messageDataList);
		}
	}
	
	public void animateAgents(){
		animateAgents(false);
	}
	public void animateAgents(boolean singlestep){
		
		System.out.println("####animateAgents!");
		
		//calc agents steps
		if(!activeAgentAnimation){
			calcAgentsMove();
		}
		
		if(singlestep){
			CIMAAnimation.getCIMAAnimation().nextStepAgentAnimation(agentWayList);
		}else{
			//reset the msgData animation
			for(MessageData msgData : messageDataList){
				msgData.resetReadyAnimated();
			}
			CIMAAnimation.getCIMAAnimation().startAgentAnimation(agentWayList);
		}
	
	}
	
	private void calcAgentsMove(){
		
		CIMAAnimation animation = CIMAAnimation.getCIMAAnimation();

		//animation läuft schon.... breche neue animation ab
//		if(activeAnimation){
//			animation.stopAgentAnimation();
//			return;
//		}

		agentWayList.clear();
		
		CIMAVertice homeBase = findHomeBase();
		homeBase.resetAllVerticeAnimation();
		homeBase.changeCurrentAgents(homeBase.getMu());
		homeBase.moveAgents(null, 0);

	}

	private int moveAgents(CIMAVertice sender, int agentNumber){


		Collections.sort(lamdas, new MessageDataComparator());
		for(int i = lamdas.size() - 1; i >= 0; i--){

			if(sender == null  || !(lamdas.get(i).getSender().equals(sender))){
				agentWayList.add(new AgentWayData(this, lamdas.get(i).getSender(), lamdas.get(i).getValue()));
				lamdas.get(i).getSender().moveAgents(this, lamdas.get(i).getValue());
			}
		}

		agentWayList.add(new AgentWayData(this, sender, agentNumber));
		return agentNumber;
	}	
}
