package cima;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;

import cima.Gui;

public class MessageData implements IMarkable{

	private int lamdaValue;
	private CIMAVertice sender;
	private CIMAVertice receiver;
	private IMarkable edgeMax1;
	private IMarkable edgeMax2;	
	private int specialVerticeWeight;
	private IMarkable maxMsgData;
	private IMarkable msgEdge;
	private CIMAEdgeWeight edge;
	
	//potential k == 1
	private PotentialData potentialData;
	
	//potantial k > 1
	private int bestPossibleLamdaValue;
	private CIMAEdgeWeight potentialEdge;
	
	
	//

	private InfoDisplayClass infoDisplayClass;
	
	private maxState maxState;
	private enum maxState { EDGEMAX, MAXMSGDATA, MSGEDGE};
	
	//animation / graphic

	private int xMiddleAnimationEndPosition;
	private int yMiddleAnimationEndPosition;
	private static int animationSpeed = 3;
	private static MessageData displayCalcInfos;
	private static boolean showMessageData = true;
	
	private String edgeMax1String = "";
	private String edgeMax2String = "";
	private String maxMsgDataString = "";
	private String mshEdgeString = "";
	
	private double angleSender;
	private double angleReceiver;
	private int radius;
	private int MiddlepunktKreisX;
	private int MiddlepunktKreisY;
	private int messageDataRadius = 8;

	//calc animation coords
	int X_segMitteGewollt;
	int Y_segMitteGewollt;
	int X_mitte_segMitteGewollt_sender;
	int Y_mitte_segMitteGewollt_sender;
	int X_möglMiddlepunkt;
	int Y_möglMiddlepunkt;
	int tatsÃ¤chlicherKreisMiddlepunkt_X;
	int tatsÃ¤chlicherKreisMiddlepunkt_Y;

	private double animationAngle;
	private boolean activeAnimation = false;
	public static boolean animationInProgress = false;
	public static boolean clearGui = false;
	private boolean animationFinished = true;
	private Color defaultOvalColor = new Color(240, 240, 240);
	private Color ovalColor = defaultOvalColor;


	public MessageData(){
		this(0, null, null, null, null, null, 0, null, null,  null);
	}
	public MessageData(int lamdaValue, CIMAVertice sender, CIMAVertice receiver, CIMAEdgeWeight edge, IMarkable edgeMax1, IMarkable edgeMax2, int specialVerticeWeight, IMarkable maxMsgData, IMarkable msgEdge, PotentialData potentialData){
		this.lamdaValue = lamdaValue;
		this.sender = sender;
		this.receiver = receiver;
		this.edgeMax1 = edgeMax1;
		this.edgeMax2 = edgeMax2;
		this.specialVerticeWeight = specialVerticeWeight;
		this.maxMsgData = maxMsgData;
		this.msgEdge = msgEdge;
		this.edge = edge;
		this.potentialData = potentialData;
		
		if(msgEdge != null){
			maxState = maxState.MSGEDGE;
		}else if(maxMsgData != null){
			maxState = maxState.MAXMSGDATA;
		}else{
			maxState = maxState.EDGEMAX;
		}

		if(getSender() != null && getReceiver() != null){
			calcArc();
		}
		
		infoDisplayClass = new InfoDisplayClass();
	}
	public MessageData(int lamdaValue, CIMAVertice sender, CIMAVertice receiver, int bestPossibleLamdaValue, CIMAEdgeWeight potentialEdge){
		this.lamdaValue = lamdaValue;
		this.sender = sender;
		this.receiver = receiver;
		this.bestPossibleLamdaValue = bestPossibleLamdaValue;
		this.potentialEdge = potentialEdge;
		
		System.out.println("new msgData created with:\n-----");
		System.out.println("sender "+sender.getName());
		System.out.println("receiver "+receiver.getName());
		System.out.println("lambda "+lamdaValue);
		System.out.println("bestlamda "+bestPossibleLamdaValue);
		System.out.println("-----");
	}

	private void calcArc(){

		int vektorX = getReceiver().getMiddleX() - getSender().getMiddleX();
		int vektorY = getReceiver().getMiddleY() - getSender().getMiddleY();
		
		double vektorLength = Math.sqrt(vektorX*vektorX + vektorY*vektorY);
		xMiddleAnimationEndPosition = (int) (getReceiver().getMiddleX() - (vektorX/vektorLength)*(messageDataRadius + getReceiver().getDiameter()/2 + 3));
		yMiddleAnimationEndPosition = (int) (getReceiver().getMiddleY() - (vektorY/vektorLength)*(messageDataRadius + getReceiver().getDiameter()/2 + 3));
		

		int MiddlepunktX = Math.min(getSender().getMiddleX(), xMiddleAnimationEndPosition) + Math.abs(getSender().getMiddleX() - xMiddleAnimationEndPosition) / 2;
		int MiddlepunktY = Math.min(getSender().getMiddleY(), yMiddleAnimationEndPosition) + Math.abs(getSender().getMiddleY() - yMiddleAnimationEndPosition) / 2;
		
		
		int orthVektorX = vektorY;
		int orthVektorY = -vektorX;

		MiddlepunktKreisX = MiddlepunktX + orthVektorX;
		MiddlepunktKreisY = MiddlepunktY + orthVektorY;
		
	
		double orthLength = Math.sqrt(orthVektorX*orthVektorX + orthVektorY*orthVektorY);
		X_segMitteGewollt = (int) (MiddlepunktX - orthVektorX/orthLength * 15);
		Y_segMitteGewollt = (int) (MiddlepunktY - orthVektorY/orthLength * 15);
		
		X_mitte_segMitteGewollt_sender = Math.min(getSender().getMiddleX(), X_segMitteGewollt) + Math.abs(getSender().getMiddleX() - X_segMitteGewollt) / 2;
		Y_mitte_segMitteGewollt_sender = Math.min(getSender().getMiddleY(), Y_segMitteGewollt) + Math.abs(getSender().getMiddleY() - Y_segMitteGewollt) / 2;
		
		int vektor_sender_segMitteGewollt_X = X_segMitteGewollt - getSender().getMiddleX();
		int vektor_sender_segMitteGewollt_Y = Y_segMitteGewollt - getSender().getMiddleY();
		
		int orthVektor_sender_segMitteGewollt_X = vektor_sender_segMitteGewollt_Y;
		int orthVektor_sender_segMitteGewollt_Y = -vektor_sender_segMitteGewollt_X;
		
		X_möglMiddlepunkt = X_mitte_segMitteGewollt_sender + orthVektor_sender_segMitteGewollt_X;
		Y_möglMiddlepunkt = Y_mitte_segMitteGewollt_sender + orthVektor_sender_segMitteGewollt_Y;
		
		
		int x1 = MiddlepunktX;
		int x2 = MiddlepunktKreisX;
		int x3 = X_mitte_segMitteGewollt_sender;
		int x4 = X_möglMiddlepunkt;
		int y1 = MiddlepunktY;
		int y2 = MiddlepunktKreisY;
		int y3 = Y_mitte_segMitteGewollt_sender;
		int y4 = Y_möglMiddlepunkt;
		
	    double d = (x1-x2)*(y3-y4) - (y1-y2)*(x3-x4);
	    
        if (d == 0) System.out.println("problem");
    
     
    
        tatsÃ¤chlicherKreisMiddlepunkt_X = (int) (((x3-x4)*(x1*y2-y1*x2)-(x1-x2)*(x3*y4-y3*x4))/d);  
        tatsÃ¤chlicherKreisMiddlepunkt_Y = (int) (((y3-y4)*(x1*y2-y1*x2)-(y1-y2)*(x3*y4-y3*x4))/d);
        
        MiddlepunktKreisX = tatsÃ¤chlicherKreisMiddlepunkt_X;
        MiddlepunktKreisY = tatsÃ¤chlicherKreisMiddlepunkt_Y;


		int vektorKreisMitteSenderX = getSender().getMiddleX() - MiddlepunktKreisX;
		int vektorKreisMitteSenderY = getSender().getMiddleY() - MiddlepunktKreisY;
		int vektorKreisMitteReceiverX = xMiddleAnimationEndPosition - MiddlepunktKreisX;
		int vektorKreisMitteReiciverY = yMiddleAnimationEndPosition - MiddlepunktKreisY;

		radius = (int) Math.sqrt((vektorKreisMitteSenderX) * (vektorKreisMitteSenderX)
										+ (vektorKreisMitteSenderY) * (vektorKreisMitteSenderY));


		angleSender = getAngle(vektorKreisMitteSenderX, vektorKreisMitteSenderY);
		angleReceiver = getAngle(vektorKreisMitteReceiverX, vektorKreisMitteReiciverY);

		if(angleReceiver < angleSender){
			angleReceiver += 2*Math.PI;
		}
	}
	
	public void drawLine(Graphics2D g){
		//after calc dont draw
		if(CIMAVertice.drawMu && CIMAVertice.activeAnimation || !showMessageData){
			return;
		}

		if(activeAnimation){
			clearGui = false;
			markAsAnimationColor();
			drawAnimationLine(g);
		}else{
			if(clearGui || CIMAAnimation.breakThread){
				return;
			}
			if((animationInProgress && animationFinished) || !animationInProgress){
				g.setColor(ovalColor);
				g.draw(new Arc2D.Double(MiddlepunktKreisX - radius, MiddlepunktKreisY - radius, 2*radius, 2*radius, Math.toDegrees(angleSender), Math.toDegrees(angleReceiver - angleSender), Arc2D.OPEN));
			}
		}
	}

	public void drawMessageData(Graphics2D g){
				
		//after calc dont draw
		if(CIMAVertice.drawMu && CIMAVertice.activeAnimation || !showMessageData){
			return;
		}
		
		if(activeAnimation){
			clearGui = false;
			markAsAnimationColor();
			drawAnimation(g);
		}else{
			if(clearGui || CIMAAnimation.breakThread){
				return;
			}
			if((animationInProgress && animationFinished) || !animationInProgress){

				double angle = getStopMessageDataAngle();
				
				int ovalMitteX = (int) (MiddlepunktKreisX + Math.cos(angle) * radius);
				int ovalMitteY = (int) (MiddlepunktKreisY - Math.sin(angle) * radius);
				
				drawMessageInfo(g, ovalMitteX, ovalMitteY);
			}
		}

	}

	public void drawAnimationLine(Graphics2D g){
		g.setColor(ovalColor);
		g.draw(new Arc2D.Double(MiddlepunktKreisX - radius, MiddlepunktKreisY - radius, 2*radius, 2*radius, Math.toDegrees(angleSender), Math.toDegrees(animationAngle), Arc2D.OPEN));
	}
	public void drawAnimation(Graphics2D g){		

		double kreisSegmentEndeAngle = angleSender + animationAngle;
		
		if(kreisSegmentEndeAngle >= getStopMessageDataAngle()){
			kreisSegmentEndeAngle = getStopMessageDataAngle();
		}
		
		if(kreisSegmentEndeAngle >= 2*Math.PI){
			kreisSegmentEndeAngle %= 2*Math.PI;
		}
		
		int kreisSegmentEndeX = (int) (MiddlepunktKreisX + Math.cos(kreisSegmentEndeAngle) * radius);
		int kreisSegmentEndeY = (int) (MiddlepunktKreisY - Math.sin(kreisSegmentEndeAngle) * radius);
		
		drawMessageInfo(g, kreisSegmentEndeX, kreisSegmentEndeY);
	}
	
	public void drawWriteCalcInfos(Graphics2D g){
		
		if(CIMAVertice.drawMu && CIMAVertice.activeAnimation){
			return;
		}
		
		if(displayCalcInfos != this){
			return;
		}
		
		
		//schreibe oben in der Ecke welche Werte eine Rolle spielen
		Color colorBackground;
		colorBackground = CIMAConstants.getMarkAsMaxColor();
		if(edgeMax1 != null){
			colorBackground = edgeMax1.getColor();
		}
		infoDisplayClass.displayInUpperLeftCorner(g, edgeMax1String, 1, Color.BLACK, colorBackground);
		
		if(edgeMax2 != null){
			colorBackground = edgeMax2.getColor();
		}
		infoDisplayClass.displayInUpperLeftCorner(g, edgeMax2String, 2, Color.BLACK, colorBackground);
		
		if(maxState == maxState.MAXMSGDATA){
			colorBackground = maxMsgData.getColor();
			infoDisplayClass.displayInUpperLeftCorner(g, maxMsgDataString, 3, Color.BLACK, colorBackground);
		}
		if(maxState == maxState.MSGEDGE){
			colorBackground = msgEdge.getColor();
			infoDisplayClass.displayInUpperLeftCorner(g, mshEdgeString, 3, Color.BLACK, colorBackground);
		}
		
	}

	private void drawMessageInfo(Graphics2D g, int ovalMitteX, int ovalMitteY){
		
		
		g.setColor(ovalColor);
		g.fillOval(ovalMitteX - messageDataRadius, ovalMitteY - messageDataRadius, 2*messageDataRadius, 2*messageDataRadius);

		g.setColor(Color.black);
		String string = String.valueOf(lamdaValue);
		int stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(string,g).getWidth());
		Font defaultFont = g.getFont();
		g.setFont(CIMAConstants.getTextFont());
		g.drawString(string, ovalMitteX - stringWidth/2, ovalMitteY+messageDataRadius/2);
		g.setFont(defaultFont);
	}

	private double getAngle(int x, int y){

		double calc = (Math.acos(x / Math.sqrt(x * x + y * y)));
		
        if(y > 0){
            return (2*Math.PI - calc);
        }

        return calc;

	}
	
	private double getStopMessageDataAngle(){
		
		return angleReceiver;
	}

	@Override
	public String toString() {
		//TODO fix the null problem
		if(sender == null || receiver == null){
			return "NULL string";
		}
		return "Sender : "+sender.getName()+"  EmpfÃ¤nger : "+receiver.getName()+"  LamdaValue: "+lamdaValue;
	}

	@Override
	public int getLamdaValue(){
		return lamdaValue;
	}
	public PotentialData getPotentialData(){
		return potentialData;
	}
	public CIMAVertice getSender(){
		return sender;
	}
	public CIMAVertice getReceiver(){
		return receiver;
	}
	public void animationFinished(){
		animationFinished = true;
	}
	public void prepareForAnimation(){
		animationFinished = false;
	}
	public static void setAnimationSpeed(int animationSpeed){
		if(animationSpeed >= 0 && animationSpeed <= 10){
			MessageData.animationSpeed = animationSpeed;
		}
	}
	public static void resetDisplayCalcInfos(){
		displayCalcInfos = null;
	}
	
	public void markAsAnimationColor(){
		ovalColor = new Color(140, 220, 230);
	}
	@Override
	public void markAsMax(){
		ovalColor = CIMAConstants.getMarkAsMaxColor();
	}
	@Override
	public void markAsSecMax(){
		ovalColor = CIMAConstants.getMarkAsSecMaxColor();
	}
	@Override
	public void resetColor(){
		ovalColor = defaultOvalColor;
	}
	@Override
	public Color getColor() {
		return ovalColor;
	}
	@Override
	public void markColor(Color color) {
		ovalColor = color;
	}
	public static void setShowMessageData(boolean showMessageData){
		MessageData.showMessageData = showMessageData;
	}
	public CIMAEdgeWeight getEdge(){
		return edge;
	}
	public int getBestPossiblelamdaValue(){
		return bestPossibleLamdaValue;
	}
	public CIMAEdgeWeight getPotentialEdge(){
		return potentialEdge;
	}
	
	public void markAllColors(){
		
		//only edges are relevant
		if(maxState == maxState.EDGEMAX){
			//leaf
			if(edgeMax1 == null){
				sender.markAsMax(specialVerticeWeight);
				edgeMax1String = specialVerticeWeight+" Knotengewicht (Blatt)";
				
			//just 1 relevant edge
			}else if(edgeMax2 == null){
				edgeMax1.markAsMax();
				if(specialVerticeWeight != 0){
					sender.markAsSecMax(specialVerticeWeight);
				}
				edgeMax1String = edgeMax1.getLamdaValue()+" max1";
				edgeMax2String = (edgeMax2.getLamdaValue() + specialVerticeWeight) + " = "+edgeMax2.getLamdaValue()+" + "+specialVerticeWeight +"  max2 + Knotengewicht";
				
			//both edges relevamt
			}else{
				if(specialVerticeWeight == 0){
					edgeMax1.markAsMax();
					edgeMax2.markAsMax();
					edgeMax1String = (edgeMax1.getLamdaValue() + edgeMax2.getLamdaValue()) + " = "+edgeMax1.getLamdaValue()+" + "+edgeMax2.getLamdaValue() +"  max1 + max2";
				}else if(edgeMax1.getLamdaValue() >= (edgeMax2.getLamdaValue() + specialVerticeWeight)){
					edgeMax1.markAsMax();
					edgeMax2.markAsSecMax();
					sender.markAsSecMax(specialVerticeWeight);
					edgeMax1String = edgeMax1.getLamdaValue()+" max1";
					edgeMax2String = (edgeMax2.getLamdaValue() + specialVerticeWeight) + " = "+edgeMax2.getLamdaValue()+" + "+specialVerticeWeight +"  max2 + Knotengewicht";
				}else{
					edgeMax1.markAsSecMax();
					edgeMax2.markAsMax();
					sender.markAsMax(specialVerticeWeight);
					edgeMax1String = edgeMax1.getLamdaValue()+" max1";
					edgeMax2String = (edgeMax2.getLamdaValue() + specialVerticeWeight) + " = "+edgeMax2.getLamdaValue()+" + "+specialVerticeWeight +"  max2 + Knotengewicht";
				}
			}
			
		//the two edges are not relevant -> mark as secMax, if edge != null
		}else{
			if(edgeMax1 != null){
				edgeMax1.markAsSecMax();
				edgeMax1String = edgeMax1.getLamdaValue()+" max1";
			}
			if(edgeMax2 != null){
				edgeMax2.markAsSecMax();
				if(specialVerticeWeight != 0){
					sender.markAsSecMax(specialVerticeWeight);
				}
				edgeMax2String = (edgeMax2.getLamdaValue() + specialVerticeWeight) + " = "+edgeMax2.getLamdaValue()+" + "+specialVerticeWeight +"  max2 + Knotengewicht";
			}
		}
		
		//the biggest msgData is relevamt
		if(maxState == maxState.MAXMSGDATA){
			maxMsgData.markAsMax();
			maxMsgDataString = maxMsgData.getLamdaValue()+" maximale Nachricht";
		}
		
		//the edge for the message is relevant
		if(maxState == maxState.MSGEDGE){
			msgEdge.markAsMax();
			mshEdgeString = msgEdge.getLamdaValue()+" aktelles Kantengewicht";
		}
		
		
		
		
		///////
//		if(calcMax1 != null){
//			calcMax1.markAsMax();
//			if(edgeWeightUsed){
//				calcMax1.markAsSecMax();
//			}
//			max1String = calcMax1.getLamdaValue()+" max1";
//		}
//		if(calcMax2 != null){
//			calcMax2.markAsMax();
//			if(edgeWeightUsed){
//				calcMax2.markAsSecMax();
//			}
//			sender.markAsMax(specialVerticeWeight);
//			max1String = (calcMax2.getLamdaValue() + specialVerticeWeight) + " = "+calcMax2.getLamdaValue()+" + "+specialVerticeWeight +"  max2 + Knotengewicht";
//		}
//		if(calcMax1 != null && calcMax2 != null){
//			if(calcMax1.getLamdaValue() >= calcMax2.getLamdaValue() + sender.getVerticeWeight()){
//				calcMax1.markAsMax();
//				if(edgeWeightUsed){
//					calcMax1.markAsSecMax();
//				}
//				calcMax2.markAsSecMax();
//				sender.markAsSecMax(specialVerticeWeight);
//				max1String = calcMax1.getLamdaValue()+" max1";
//				max2String = (calcMax2.getLamdaValue() + specialVerticeWeight) + " = "+calcMax2.getLamdaValue()+" + "+specialVerticeWeight +"  max2 + Knotengewicht";
//			}else{
//				calcMax1.markAsSecMax();
//				calcMax2.markAsMax();
//				sender.markAsMax(specialVerticeWeight);
//				if(edgeWeightUsed){
//					calcMax2.markAsSecMax();
//					sender.markAsSecMax(specialVerticeWeight);
//				}
//				max1String = (calcMax2.getLamdaValue() + specialVerticeWeight) + " = "+calcMax2.getLamdaValue()+" + "+specialVerticeWeight +"  max2 + Knotengewicht";
//				max2String = calcMax1.getLamdaValue()+" max1";
//			}
//		}
//		if(calcMax1 == null && calcMax2 == null){
//			//has to be a leaf
//			sender.markAsMax(specialVerticeWeight);
//			if(edgeWeightUsed){
//				sender.markAsSecMax(specialVerticeWeight);
//			}
//			max1String = specialVerticeWeight+" Knotengewicht (Blatt)";
//		}
	}
	
	public void resetAllColors(){
		if(edgeMax1 != null){
			edgeMax1.resetColor();
		}
		if(edgeMax2 != null){
			edgeMax2.resetColor();
			sender.resetColor();
		}
		if(edgeMax1 == null && edgeMax2 == null){
			//has to be a leaf
			sender.resetColor();
		}
		if(maxMsgData != null){
			maxMsgData.resetColor();
		}
		if(msgEdge != null){
			msgEdge.resetColor();
		}
		resetColor();
	}


	public SendMessageAnimationTimer animation(){
		SendMessageAnimationTimer timer = new SendMessageAnimationTimer();
		timer.start();
		displayCalcInfos = this;
		return timer;
	}

	public class SendMessageAnimationTimer extends Thread{

		Gui gui;

		public SendMessageAnimationTimer() {
			this.gui = CIMAGui.getGui();
		}

		@Override
		public void run() {

			activeAnimation = true;
			animationAngle = 0;

			while(isInterrupted() == false){
				
				double calcAngle = 2f/radius;
				animationAngle += animationSpeed * calcAngle / 4;

				gui.repaint();

				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}

				if(animationAngle >= angleReceiver - angleSender){
					this.interrupt();
					break;
				}
			}
			
			activeAnimation = false;
			gui.repaint();
		}
	}

}
