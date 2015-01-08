package cima;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Gui.Gui;
import Tree.Vertice;

public class CIMAVertice extends Vertice{

	private int edgeWeightToParent;
	private int verticeWeight;
	private boolean drawMu = false;
	private Color stringColor = Color.black;
	private int mu;
	private List<MessageData> lamdas = new ArrayList<MessageData>();

	//weightField
	int ovalWidth = 17;
	int ovalMittelX = -1;
	int ovalMittelY = -1;

	//agentWaayList
	public static List<AgentWayData> agentWayList = new ArrayList<AgentWayData>();
	public static List<MessageData> messageDataList = new ArrayList<MessageData>();


	public CIMAVertice(String name, Vertice parent, Gui gui){
		this(name, parent, 1, gui);
	}

	public CIMAVertice(String name, Vertice parent, int edgeWeightToParent, Gui gui) {
		super(name, parent, gui);
		if(parent != null){
			this.edgeWeightToParent = edgeWeightToParent;
		}else{
			this.edgeWeightToParent = 0;
		}
	}

	@Override
	protected void drawAllVertice(Graphics g){

		super.drawAllVertice(g);

		if(activeAnimation){
//			g.setColor(Color.white);
			g.setColor(Color.black);
			String string = String.valueOf(currentAgents);
			int stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(string,g).getWidth());
			g.drawString(string, xMittel - stringWidth/2, yMittel+height/4);
		}else{
			String string;
			if(drawMu){
				string = String.valueOf(mu);
			}else{
				string = String.valueOf(verticeWeight);
			}
			g.setColor(stringColor);
			int stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(string,g).getWidth());
			g.drawString(string, xMittel - stringWidth/2, yMittel+height/4);
		}

	}

	@Override
	protected void drawAllTreeLines(Graphics g) {
		super.drawAllTreeLines(g);

		if(parent != null){
			g.setColor(Color.lightGray);
			ovalMittelX = Math.min(xMittel, parent.getMittelX()) + Math.abs(xMittel - parent.getMittelX()) / 2;
			ovalMittelY = Math.min(yMittel, parent.getMittelY()) + Math.abs(yMittel - parent.getMittelY()) / 2;
			g.fillOval(ovalMittelX - ovalWidth/2, ovalMittelY - ovalWidth/2, ovalWidth, ovalWidth);

			g.setColor(Color.black);
			String string = String.valueOf(edgeWeightToParent);
			int stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(string,g).getWidth());
			g.drawString(string, ovalMittelX - stringWidth/2, ovalMittelY+height/4);


//			g.drawString(""+edgeWeightToParent, Math.min(xMittel, parent.getMittelX()) + Math.abs(xMittel - parent.getMittelX()) / 2, Math.min(yMittel, parent.getMittelY()) + Math.abs(yMittel - parent.getMittelY()) / 2);
		}
	}


	/************************************************************************************/
	/**********************************ALGORITHMUS***************************************/
	/************************************************************************************/

	public void algorithmus(){
		System.out.println("starting algo....");
		reset();
		startAlgo();

		System.out.println("##################################\n###############################\n######################");

		calcMu();
		logSubtree();

		System.out.println("////////////////////////////////////////////");
		for(MessageData msgData : messageDataList){
			System.out.println("  "+msgData.toString());
		}
		System.out.println("////////////////////////////////////////////");

		CIMAAnimation.getCIMAAnimation(gui).startSendMessageAnimation(messageDataList);
	}

	private void reset(){
		lamdas.clear();
		messageDataList.clear();
		resetCurrentAgents();

		//calc the verticeWeight
		verticeWeight = edgeWeightToParent;
		for(Vertice child : children){
			if(!(child instanceof CIMAVertice)){
				System.out.println("ERROR... wrong Verticetype!!");
				return;
			}

			if(((CIMAVertice) child).getEdgeWeightToParent() > verticeWeight){
				verticeWeight = ((CIMAVertice) child).getEdgeWeightToParent();
			}
		}

		//call reset() recursive to all children
		for(Vertice child : children){
			if(!(child instanceof CIMAVertice)){
				System.out.println("ERROR... wrong Verticetype!!");
				return;
			}
			((CIMAVertice) child).reset();
		}
	}

	private void startAlgo(){
		if(children.size() == 0 && parent != null){
			//got a ready leaf -> send message
			((CIMAVertice) parent).receive(new MessageData(verticeWeight, this, (CIMAVertice) parent, null, null));
		}else{
			for(Vertice child : children){
				if(!(child instanceof CIMAVertice)){
					System.out.println("ERROR... wrong Verticetype!!");
					return;
				}
				((CIMAVertice) child).startAlgo();
			}
		}

//		if(children.size() == 0 && parent != null){
//			//got a ready leaf -> send message
//			((CIMAVertice) parent).receive(new MessageData(verticeWeight, this));
//		}else{
//			((CIMAVertice) children.get(0)).startAlgo();
//		}
	}

	private void receive(MessageData data){
		this.lamdas.add(data);
		messageDataList.add(data);

		System.out.println("++++++++ received msg in: "+this.getName()+ "msg: "+data);

		if(lamdas.size() == numberOfNeighbors() -1){
			//TODO *
			computeLamdasAndSendTo(getMissingNeightbour());
		}else if(lamdas.size() == numberOfNeighbors()){
			//TODO **
			if(lamdas.size() == 1 && children.size() == 0){
				//leaf, dont send aganin
//				computeLamdasAndSendTo(data.getSender());
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
				System.out.println("ERROR... wrong Verticetype!!");
				return;
			}
			if(((CIMAVertice) child) != exeptVertice){
				computeLamdasAndSendTo((CIMAVertice) child);
			}
		}
		if(parent != null){
			if(!(parent instanceof CIMAVertice)){
				System.out.println("ERROR... wrong Verticetype!!");
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
		MessageData max1 = new MessageData(0, null, null, null, null);
		MessageData max2 = new MessageData(0, null, null, null, null);
//		int otherVerticeWeight = 0;
		if(maximums.size() >= 1){
			max1 = maximums.get(0);
		}
		if(maximums.size() >= 2){
			max2 = maximums.get(1);
//			otherVerticeWeight = maximums.get(1).getSender().getVerticeWeight();
		}

//		System.out.println("otherVerticeWeight: "+otherVerticeWeight);

		//int maxValue = Math.max(max1.getLamdaValue(), max2.getLamdaValue() + verticeWeight);//TODO get verticeWeight from other vertice
		MessageData calcMessageData;
		if(max1.getLamdaValue() >= max2.getLamdaValue() + verticeWeight){
			calcMessageData = new MessageData(max1.getLamdaValue(), this, receiverNode, max1, max2);
		}else{
			calcMessageData = new MessageData(max2.getLamdaValue() + verticeWeight, this, receiverNode, max1, max2);
		}
		

//		System.out.println("++++ sende VON "+ this.name+" nach "+ receiverNode.getName() + " value: "+maxValue +"           // verticeWeight : "+verticeWeight);
//		System.out.println("+++++++++++++++++++++  max1.getLamdaValue() : "+max1.getLamdaValue()+" max2.getLamdaValue(): "+max2.getLamdaValue());

		receiverNode.receive(calcMessageData);
	}

	private CIMAVertice getMissingNeightbour(){
		for(Vertice neighbor : children){
			if(!(neighbor instanceof CIMAVertice)){
				System.out.println("ERROR... wrong Verticetype!!");
				return null;
			}
			if(! didSendData((CIMAVertice) neighbor)){
				return (CIMAVertice) neighbor;
			}
		}
		if(parent != null){
			if(!(parent instanceof CIMAVertice)){
				System.out.println("ERROR... wrong Verticetype!!");
				return null;
			}
			if(!didSendData((CIMAVertice) parent)){
				return (CIMAVertice) parent;
			}
		}
		System.out.println("ERROR : no missing neighbor?");
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
		Collections.sort(lamdas, new MessageDataComparator());
		MessageData max1 = new MessageData(0, null, null, null, null);
		MessageData max2 = new MessageData(0, null, null, null, null);
		if(lamdas.size() >= 1){
			max1 = lamdas.get(0);
		}
		if(lamdas.size() >= 2){
			max2 = lamdas.get(1);
		}

		mu = Math.max(max1.getLamdaValue(), max2.getLamdaValue() + verticeWeight);

//		System.out.println("++++ bin in "+ this.name+"   value (mu) : "+mu +"           // verticeWeight : "+verticeWeight);
//		System.out.println("+++++++++++++++++++++  max1.getLamdaValue() : "+max1.getLamdaValue()+" max2.getLamdaValue(): "+max2.getLamdaValue());


		for(Vertice child : children){
			if(!(child instanceof CIMAVertice)){
				System.out.println("ERROR... wrong Verticetype!!");
				return;
			}
			((CIMAVertice) child).calcMu();
		}
	}

	/////////////////////////////////

	public void calcAgentsMove(){

		CIMAAnimation animation = CIMAAnimation.getCIMAAnimation(gui);

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

	public void doCompleteAnimation(){
		//make sure the algo is calced //TODO
//		algorithmus();

		CIMAAnimation animation = CIMAAnimation.getCIMAAnimation(gui);

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

	public void doStepAnimation(boolean nextStep){

		CIMAAnimation animation = CIMAAnimation.getCIMAAnimation(gui);

		if(!activeAnimation){
			calcAgentsMove();
		}

		if(nextStep){
			//nextStep
			animation.nextStepAgentAnimation(agentWayList, nextStep);
		}else{
			//prevStep
//			animation.nextStepAnimation(agentWayList, nextStep);
		}
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

		if(sender == null){
			System.out.println("###################");
			System.out.println("sender := null -> start?");
		}else{
			System.out.println("sende >"+agentNumber +"< agent from "+sender.getName()+" zu "+this.getName());
		}

		Collections.sort(lamdas, new MessageDataComparator());
		for(int i = lamdas.size() - 1; i >= 0; i--){

//			System.out.println("##### lamdaMessage from _ "+lamdas.get(i).getSender().getName());

			if(sender == null  || !(lamdas.get(i).getSender().equals(sender))){
				System.out.println("call " + this.getName() +" -> "+lamdas.get(i).getSender().getName());
				agentWayList.add(new AgentWayData(this, lamdas.get(i).getSender(), lamdas.get(i).getLamdaValue()));
				lamdas.get(i).getSender().moveAgents(this, lamdas.get(i).getLamdaValue());
			}
		}


		if(sender == null){
			System.out.println("## fertig?");
			System.out.println("###################");
		}else{
			System.out.println("## sende >"+agentNumber +"< agent #ZURÜCK# von "+this.getName()+" zu "+sender.getName());
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
		System.out.println("Check EdgeWeight : "+this.ovalMittelX+","+this.ovalMittelY);
		if((Math.abs(this.ovalMittelX - x) <= ovalWidth/2) && (Math.abs(this.ovalMittelY - y) < ovalWidth/2)){
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
	public void markAsMax(){
		stringColor = new Color(22, 16, 232);
	}
	public void markAsSecMax(){
		stringColor = new Color(16, 222, 255);
	}
	public void resetColor(){
		stringColor = Color.black;
	}

}
