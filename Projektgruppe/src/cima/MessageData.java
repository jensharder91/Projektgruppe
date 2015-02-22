package cima;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;

import cima.Gui;

public class MessageData {

	private int lamdaValue;
	private CIMAVertice sender;
	private CIMAVertice receiver;
	private MessageData calcMax1;
	private MessageData calcMax2;
	private int specialVerticeWeight;
	private boolean edgeWeightUsed;
	
	//animation / graphic

	private int xMiddleAnimationEndPosition;
	private int yMiddleAnimationEndPosition;
	private static int animationSpeed = 3;
	private static MessageData displayCalcInfos;
	
	private String max1String = "";
	private String max2String = "";
	
	private double angleSender;
	private double angleReceiver;
	private int radius;
	private int MiddlepunktKreisX;
	private int MiddlepunktKreisY;
	private int messageDataRadius = 8;
	private int segmentMitteX;
	private int segmentMitteY;
	
	//testing
	int X_segMitteGewollt;
	int Y_segMitteGewollt;
	int X_mitte_segMitteGewollt_sender;
	int Y_mitte_segMitteGewollt_sender;
	int X_möglMiddlepunkt;
	int Y_möglMiddlepunkt;
	int tatsächlicherKreisMiddlepunkt_X;
	int tatsächlicherKreisMiddlepunkt_Y;

	private double animationAngle;
	private boolean activeAnimation = false;
	public static boolean animationInProgress = false;
	public static boolean clearGui = false;
	private boolean animationFinished = true;
	private Color defaultOvalColor = new Color(240, 240, 240);
	private Color ovalColor = defaultOvalColor;


	public MessageData(int lamdaValue, CIMAVertice sender, CIMAVertice receiver, MessageData calcMax1, MessageData calcMax2, int specialVerticeWeight, boolean edgeWeightUsed){
		this.lamdaValue = lamdaValue;
		this.sender = sender;
		this.receiver = receiver;
		this.calcMax1 = calcMax1;
		this.calcMax2 = calcMax2;
		this.specialVerticeWeight = specialVerticeWeight;
		this.edgeWeightUsed = edgeWeightUsed;

		if(getSender() != null && getReceiver() != null){
			calcArc();
		}
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
		
		//////TODO
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
    
     
    
        tatsächlicherKreisMiddlepunkt_X = (int) (((x3-x4)*(x1*y2-y1*x2)-(x1-x2)*(x3*y4-y3*x4))/d);
    
        tatsächlicherKreisMiddlepunkt_Y = (int) (((y3-y4)*(x1*y2-y1*x2)-(y1-y2)*(x3*y4-y3*x4))/d);
        
        MiddlepunktKreisX = tatsächlicherKreisMiddlepunkt_X;
        MiddlepunktKreisY = tatsächlicherKreisMiddlepunkt_Y;

		
		//

//		g.drawLine(MiddlepunktX, MiddlepunktY, startOrthVektorX, startOrthVektorY);

		int vektorKreisMitteSenderX = getSender().getMiddleX() - MiddlepunktKreisX;
		int vektorKreisMitteSenderY = getSender().getMiddleY() - MiddlepunktKreisY;
//		int vektorKreisMitteReceiverX = getReceiver().getMiddleX() - MiddlepunktKreisX;
//		int vektorKreisMitteReiciverY = getReceiver().getMiddleY() - MiddlepunktKreisY;
		int vektorKreisMitteReceiverX = xMiddleAnimationEndPosition - MiddlepunktKreisX;
		int vektorKreisMitteReiciverY = yMiddleAnimationEndPosition - MiddlepunktKreisY;

		radius = (int) Math.sqrt((vektorKreisMitteSenderX) * (vektorKreisMitteSenderX)
										+ (vektorKreisMitteSenderY) * (vektorKreisMitteSenderY));


		angleSender = getAngle(vektorKreisMitteSenderX, vektorKreisMitteSenderY);
		angleReceiver = getAngle(vektorKreisMitteReceiverX, vektorKreisMitteReiciverY);

		if(angleReceiver < angleSender){
			angleReceiver += 2*Math.PI;
		}


		int vektorKreisMitteMiddleinieX = MiddlepunktX - MiddlepunktKreisX;
		int vektorKreisMitteMiddleinieY = MiddlepunktY - MiddlepunktKreisY;
		double längeVektorKreisMitteMiddleinie = Math.sqrt(vektorKreisMitteMiddleinieX*vektorKreisMitteMiddleinieX + vektorKreisMitteMiddleinieY*vektorKreisMitteMiddleinieY);
		int vektorKreisMitteSegmentMitteX = (int) (vektorKreisMitteMiddleinieX/längeVektorKreisMitteMiddleinie * radius);
		int vektorKreisMitteSegmentMitteY = (int) (vektorKreisMitteMiddleinieY/längeVektorKreisMitteMiddleinie * radius);
		segmentMitteX = MiddlepunktKreisX + vektorKreisMitteSegmentMitteX;
		segmentMitteY = MiddlepunktKreisY + vektorKreisMitteSegmentMitteY;
	}
	
	public void drawLine(Graphics2D g){
		//after calc dont draw
		if(CIMAVertice.drawMu && CIMAVertice.activeAnimation){
			return;
		}

		if(activeAnimation){
			clearGui = false;
			markAsAnimationColor();
			drawAnimationLine(g);
//			resetColor();//TODO
		}else{
			if(clearGui || CIMAAnimation.breakThread){
				return;
			}
			if((animationInProgress && animationFinished) || !animationInProgress){
				g.setColor(ovalColor);
				g.draw(new Arc2D.Double(MiddlepunktKreisX - radius, MiddlepunktKreisY - radius, 2*radius, 2*radius, Math.toDegrees(angleSender), Math.toDegrees(angleReceiver - angleSender), Arc2D.OPEN));
//				g.drawArc(MiddlepunktKreisX - radius, MiddlepunktKreisY - radius, 2*radius, 2*radius, (int)Math.toDegrees(angleSender), (int)Math.toDegrees(angleReceiver - angleSender));
			}
		}
	}

	public void drawMessageData(Graphics2D g){

//		g.drawOval(segmentMitteX -10, MiddlepunktKreisY + segmentMitteY - 10, 20, 20);
		
		/////TODO
//		g.setColor(Color.RED);
//		g.drawOval(X_segMitteGewollt-5, Y_segMitteGewollt-5, 10, 10);
//		g.setColor(Color.BLUE);
//		g.drawOval(X_mitte_segMitteGewollt_sender-5, Y_mitte_segMitteGewollt_sender-5, 10, 10);
//		g.drawLine(X_mitte_segMitteGewollt_sender, Y_mitte_segMitteGewollt_sender, X_möglMiddlepunkt, Y_möglMiddlepunkt);
//		g.setColor(Color.MAGENTA);
//		g.drawOval(xMiddleAnimationEndPosition-5, yMiddleAnimationEndPosition-5, 10, 10);
		
		
		//after calc dont draw
		if(CIMAVertice.drawMu && CIMAVertice.activeAnimation){
			return;
		}

		if(activeAnimation){
			clearGui = false;
			markAsAnimationColor();
			drawAnimation(g);
//			resetColor();//TODO
		}else{
			if(clearGui || CIMAAnimation.breakThread){
				return;
			}
			if((animationInProgress && animationFinished) || !animationInProgress){
//				g.setColor(ovalColor);
//				g.setColor(Color.YELLOW);
//				g.setColor(ovalColor);
//				g.drawArc(MiddlepunktKreisX - radius, MiddlepunktKreisY - radius, 2*radius, 2*radius, (int)Math.toDegrees(angleSender), (int)Math.toDegrees(angleReceiver - angleSender));
//				drawArrow(g);
//				drawMessageInfo(g, segmentMitteX, segmentMitteY);
				
				double angle = getStopMessageDataAngle();
				
				int ovalMitteX = (int) (MiddlepunktKreisX + Math.cos(angle) * radius);
				int ovalMitteY = (int) (MiddlepunktKreisY - Math.sin(angle) * radius);
				
				drawMessageInfo(g, ovalMitteX, ovalMitteY);
				//
			}
		}

	}

	public void drawAnimationLine(Graphics2D g){
		g.setColor(ovalColor);
//		g.setColor(CIMAConstants.getMarkAsMaxColor());
		g.draw(new Arc2D.Double(MiddlepunktKreisX - radius, MiddlepunktKreisY - radius, 2*radius, 2*radius, Math.toDegrees(angleSender), Math.toDegrees(animationAngle), Arc2D.OPEN));
//		g.drawArc(MiddlepunktKreisX - radius, MiddlepunktKreisY - radius, 2*radius, 2*radius, (int)Math.toDegrees(angleSender), (int)Math.toDegrees(animationAngle));

	}
	public void drawAnimation(Graphics2D g){		

//		g.setColor(ovalColor);
////		g.setColor(CIMAConstants.getMarkAsMaxColor());
//		g.draw(new Arc2D.Double(MiddlepunktKreisX - radius, MiddlepunktKreisY - radius, 2*radius, 2*radius, Math.toDegrees(angleSender), Math.toDegrees(animationAngle), Arc2D.OPEN));
////		g.drawArc(MiddlepunktKreisX - radius, MiddlepunktKreisY - radius, 2*radius, 2*radius, (int)Math.toDegrees(angleSender), (int)Math.toDegrees(animationAngle));

		//teste: messageInfo wird im kreissegment verschoben TODO 
		double kreisSegmentEndeAngle = angleSender + animationAngle;
		
		if(kreisSegmentEndeAngle >= getStopMessageDataAngle()){
			kreisSegmentEndeAngle = getStopMessageDataAngle();
		}
		
		if(kreisSegmentEndeAngle >= 2*Math.PI){
			kreisSegmentEndeAngle %= 2*Math.PI;
		}
		
		int kreisSegmentEndeX = (int) (MiddlepunktKreisX + Math.cos(kreisSegmentEndeAngle) * radius);
		int kreisSegmentEndeY = (int) (MiddlepunktKreisY - Math.sin(kreisSegmentEndeAngle) * radius);
		
//		drawMessageInfo(g, 100, kreisSegmentEndeY);
		drawMessageInfo(g, kreisSegmentEndeX, kreisSegmentEndeY);
		
//		//falls schon über die hälfte des kreissegment gezeichnet wurde:
//		if(animationAngle >= (angleReceiver - angleSender)/2){
//			g.setColor(ovalColor);
//			drawArrow(g);
//			drawMessageInfo(g, animationOvalMitteX, animationOvalMitteY);
//		}
	}
	
	public void drawWriteCalcInfos(Graphics2D g){
		
		if(CIMAVertice.drawMu && CIMAVertice.activeAnimation){
			return;
		}
		
		if(displayCalcInfos != this){
			return;
		}
		
		//schreibe oben in der Ecke welche Werte eine Rolle spielen
		Font defaulFont = g.getFont();
		int stringHeight = (int) Math.floor(g.getFontMetrics().getStringBounds("stringheight",g).getHeight());
		g.setFont(CIMAConstants.getTextFont());
		g.setColor(CIMAConstants.getMarkAsMaxColor());
		if(edgeWeightUsed){
			g.setColor(CIMAConstants.getMarkAsSecMaxColor());
		}
		int stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(max1String,g).getWidth());
		g.fillRect(3, 12 - stringHeight +1, stringWidth, stringHeight);
		g.setColor(Color.BLACK);
		g.drawString(max1String, 3, 12);
		g.setColor(CIMAConstants.getMarkAsSecMaxColor());
		stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(max2String,g).getWidth());
		g.fillRect(3, (int) (12 + 1.2*stringHeight) -stringHeight +1, stringWidth, stringHeight);
		g.setColor(Color.BLACK);
		g.drawString(max2String, 3, (int) (12 + 1.2*stringHeight));
		if(edgeWeightUsed){
			g.setColor(CIMAConstants.getMarkAsMaxColor());
			stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(lamdaValue+" aktuelles Kantengewichrt",g).getWidth());
			g.fillRect(3, (int) (12 + 2.4*stringHeight) - stringHeight +1, stringWidth, stringHeight);
			g.setColor(Color.BLACK);
			g.drawString(lamdaValue+" aktuelles Kantengewichrt", 3, (int) (12 + 2.4*stringHeight));
		}
		g.setFont(defaulFont);
	}

	private void drawMessageInfo(Graphics2D g, int ovalMitteX, int ovalMitteY){
		
		
		g.setColor(ovalColor);
//		g.setColor(CIMAConstants.getMarkAsMaxColor());
		g.fillOval(ovalMitteX - messageDataRadius, ovalMitteY - messageDataRadius, 2*messageDataRadius, 2*messageDataRadius);

		g.setColor(Color.black);
		String string = String.valueOf(lamdaValue);
		int stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(string,g).getWidth());
		Font defaultFont = g.getFont();
		g.setFont(CIMAConstants.getTextFont());
		g.drawString(string, ovalMitteX - stringWidth/2, ovalMitteY+messageDataRadius/2);
		g.setFont(defaultFont);
	}

//	private void drawArrow(Graphics g){
//		//TODO pfeilspitze muss verbessert werden
//		int pfeillänge = 10;
//		double a = (int) (Math.PI/4 - Math.atan2((segmentMitteY - getSender().getMiddleY()),(segmentMitteX - getSender().getMiddleX())));
//		int c = (int) (Math.cos(a)*pfeillänge);
//		int s = (int) (Math.sin(a)*pfeillänge);
//		g.drawLine(segmentMitteX, segmentMitteY, segmentMitteX-s, segmentMitteY-c);
//		g.drawLine(segmentMitteX, segmentMitteY, segmentMitteX-c, segmentMitteY+s);
//	}

	private double getAngle(int x, int y){

		double calc = (Math.acos(x / Math.sqrt(x * x + y * y)));
		
        if(y > 0){
            return (2*Math.PI - calc);
        }

        return calc;

	}
	
	private double getStopMessageDataAngle(){
		
		return angleReceiver;
		
//		int abstand = (int) ((2*messageDataRadius + CIMAVertice.RADIUS) / 1.8); //20 hardcoded verticewidth
//		double angle = getAngle(radius, -abstand);
//		
//		angle = angleReceiver - angle;
//		
//		return angle;
	}

	@Override
	public String toString() {
		return "Sender : "+sender.getName()+"  Empfänger : "+receiver.getName()+"  LamdaValue: "+lamdaValue;
	}

	public int getLamdaValue(){
		return lamdaValue;
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
//		markAsMax();
		ovalColor = new Color(140, 220, 230);
	}
	public void markAsMax(){
		ovalColor = CIMAConstants.getMarkAsMaxColor();
	}
	public void markAsSecMax(){
		ovalColor = CIMAConstants.getMarkAsSecMaxColor();
	}
	public void resetColor(){
		ovalColor = defaultOvalColor;
	}
	
	public void markAllColors(){
		if(calcMax1 != null){
			calcMax1.markAsMax();
			if(edgeWeightUsed){
				calcMax1.markAsSecMax();
			}
			max1String = calcMax1.getLamdaValue()+" von der ersten Kante";
		}
		if(calcMax2 != null){
			calcMax2.markAsMax();
			if(edgeWeightUsed){
				calcMax2.markAsSecMax();
			}
			sender.markAsMax(specialVerticeWeight);
			max1String = calcMax2.getLamdaValue()+" + "+specialVerticeWeight +" = " + (calcMax2.getLamdaValue() + specialVerticeWeight) +"  von der zweiten Kante und vom Senderknoten";
		}
		if(calcMax1 != null && calcMax2 != null){
			if(calcMax1.getLamdaValue() >= calcMax2.getLamdaValue() + sender.getVerticeWeight()){
				calcMax1.markAsMax();
				if(edgeWeightUsed){
					calcMax1.markAsSecMax();
				}
				calcMax2.markAsSecMax();
				sender.markAsSecMax(specialVerticeWeight);
				max1String = calcMax1.getLamdaValue()+" von der ersten Kante";
				max2String = calcMax2.getLamdaValue()+" + "+specialVerticeWeight +" = " + (calcMax2.getLamdaValue() + specialVerticeWeight) +"  von der zweiten Kante und vom Senderknoten";
			}else{
				calcMax1.markAsSecMax();
				calcMax2.markAsMax();
				sender.markAsMax(specialVerticeWeight);
				if(edgeWeightUsed){
					calcMax2.markAsSecMax();
					sender.markAsSecMax(specialVerticeWeight);
				}
				max1String = calcMax2.getLamdaValue()+" + "+specialVerticeWeight +" = " + (calcMax2.getLamdaValue() + specialVerticeWeight) +"  von der zweiten Kante und vom Senderknoten";
				max2String = calcMax1.getLamdaValue()+" von der ersten Kante";
			}
		}
		if(calcMax1 == null && calcMax2 == null){
			//has to be a leaf
			sender.markAsMax(specialVerticeWeight);
			if(edgeWeightUsed){
				sender.markAsSecMax(specialVerticeWeight);
			}
			max1String = specialVerticeWeight+" vom Blattknoten";
		}
	}
	
	public void resetAllColors(){
		if(calcMax1 != null){
			calcMax1.resetColor();
		}
		if(calcMax2 != null){
			calcMax2.resetColor();
			sender.resetColor();
		}
		if(calcMax1 == null && calcMax2 == null){
			//has to be a leaf
			sender.resetColor();
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
			
			//mark the MessageData from the calc
//			if(calcMax1 != null){
//				calcMax1.markAsMax();
//			}
//			if(calcMax2 != null){
//				calcMax2.markAsMax();
//				sender.markAsMax();
//			}
//			if(calcMax1 != null && calcMax2 != null){
//				if(calcMax1.getLamdaValue() >= calcMax2.getLamdaValue() + sender.getVerticeWeight()){
//					calcMax1.markAsMax();
//					calcMax2.markAsSecMax();
//					sender.markAsSecMax();
//				}else{
//					calcMax1.markAsSecMax();
//					calcMax2.markAsMax();
//					sender.markAsMax();
//				}
//			}
//			if(calcMax1 == null && calcMax2 == null){
//				//has to be a leaf
//				sender.markAsMax();
//			}

			while(isInterrupted() == false){

//				animationAngle += Math.toRadians(1);
				
//				animationAngle += 2f/radius;
				
				double calcAngle = 2f/radius;
				animationAngle += animationSpeed * calcAngle / 4;
				
//				animationAngle = Math.toRadians(Math.ceil(Math.toDegrees(animationAngle)));

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

			//reset the MessageData from the calc (rest color)
//			if(calcMax1 != null){
//				calcMax1.resetColor();
//			}
//			if(calcMax2 != null){
//				calcMax2.resetColor();
//				sender.resetColor();
//			}
//			if(calcMax1 == null && calcMax2 == null){
//				//has to be a leaf
//				sender.resetColor();
//			}
			
			activeAnimation = false;
			gui.repaint();
		}
	}

}
