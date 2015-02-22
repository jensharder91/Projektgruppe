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
	private static boolean showMessageData = false;
	
	private String max1String = "";
	private String max2String = "";
	
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


		int vektorKreisMitteMiddleinieX = MiddlepunktX - MiddlepunktKreisX;
		int vektorKreisMitteMiddleinieY = MiddlepunktY - MiddlepunktKreisY;
		double längeVektorKreisMitteMiddleinie = Math.sqrt(vektorKreisMitteMiddleinieX*vektorKreisMitteMiddleinieX + vektorKreisMitteMiddleinieY*vektorKreisMitteMiddleinieY);
		int vektorKreisMitteSegmentMitteX = (int) (vektorKreisMitteMiddleinieX/längeVektorKreisMitteMiddleinie * radius);
		int vektorKreisMitteSegmentMitteY = (int) (vektorKreisMitteMiddleinieY/längeVektorKreisMitteMiddleinie * radius);
	}
	
	public void drawLine(Graphics2D g){
		//after calc dont draw
		if(CIMAVertice.drawMu && CIMAVertice.activeAnimation && !showMessageData){
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
		if(CIMAVertice.drawMu && CIMAVertice.activeAnimation && !showMessageData){
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
			String kantengewichtString = lamdaValue+" Kantengewicht";
			stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(kantengewichtString,g).getWidth());
			g.fillRect(3, (int) (12 + 2.4*stringHeight) - stringHeight +1, stringWidth, stringHeight);
			g.setColor(Color.BLACK);
			g.drawString(kantengewichtString, 3, (int) (12 + 2.4*stringHeight));
		}
		g.setFont(defaulFont);
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
	public static void setShowMessageData(boolean showMessageData){
		MessageData.showMessageData = showMessageData;
	}
	
	public void markAllColors(){
		if(calcMax1 != null){
			calcMax1.markAsMax();
			if(edgeWeightUsed){
				calcMax1.markAsSecMax();
			}
			max1String = calcMax1.getLamdaValue()+" max1";
		}
		if(calcMax2 != null){
			calcMax2.markAsMax();
			if(edgeWeightUsed){
				calcMax2.markAsSecMax();
			}
			sender.markAsMax(specialVerticeWeight);
			max1String = (calcMax2.getLamdaValue() + specialVerticeWeight) + " = "+calcMax2.getLamdaValue()+" + "+specialVerticeWeight +"  max2 + Knotengewicht";
		}
		if(calcMax1 != null && calcMax2 != null){
			if(calcMax1.getLamdaValue() >= calcMax2.getLamdaValue() + sender.getVerticeWeight()){
				calcMax1.markAsMax();
				if(edgeWeightUsed){
					calcMax1.markAsSecMax();
				}
				calcMax2.markAsSecMax();
				sender.markAsSecMax(specialVerticeWeight);
				max1String = calcMax1.getLamdaValue()+" max1";
				max2String = (calcMax2.getLamdaValue() + specialVerticeWeight) + " = "+calcMax2.getLamdaValue()+" + "+specialVerticeWeight +"  max2 + Knotengewicht";
			}else{
				calcMax1.markAsSecMax();
				calcMax2.markAsMax();
				sender.markAsMax(specialVerticeWeight);
				if(edgeWeightUsed){
					calcMax2.markAsSecMax();
					sender.markAsSecMax(specialVerticeWeight);
				}
				max1String = (calcMax2.getLamdaValue() + specialVerticeWeight) + " = "+calcMax2.getLamdaValue()+" + "+specialVerticeWeight +"  max2 + Knotengewicht";
				max2String = calcMax1.getLamdaValue()+" max1";
			}
		}
		if(calcMax1 == null && calcMax2 == null){
			//has to be a leaf
			sender.markAsMax(specialVerticeWeight);
			if(edgeWeightUsed){
				sender.markAsSecMax(specialVerticeWeight);
			}
			max1String = specialVerticeWeight+" Knotengewicht (Blatt)";
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
