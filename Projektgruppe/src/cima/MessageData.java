package cima;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;


/**
 * 
 * abstract messageData (for basic informations (sender, receiver) and animation
 *
 */
public abstract class MessageData implements IMarkable{

	protected int lamdaValue = 0;
	protected CIMAVertice sender = null;
	protected CIMAVertice receiver = null;
	
	
	
	//animation
	protected double angleSender;
	protected double angleReceiver;
	protected int radius;
	protected int MiddlepunktKreisX;
	protected int MiddlepunktKreisY;
	protected int messageDataRadius = 8;
	
	//calc animation coords
	protected int xMiddleAnimationEndPosition;
	protected int yMiddleAnimationEndPosition;
	protected int X_segMitteGewollt;
	protected int Y_segMitteGewollt;
	protected int X_mitte_segMitteGewollt_sender;
	protected int Y_mitte_segMitteGewollt_sender;
	protected int X_möglMiddlepunkt;
	protected int Y_möglMiddlepunkt;
	protected int tatsächlicherKreisMiddlepunkt_X;
	protected int tatsächlicherKreisMiddlepunkt_Y;
	
	protected double animationAngle;
	protected static int animationSpeed = 3;

	protected Color defaultStrongColor = new Color(140, 220, 230);
	protected Color defaultWeakColor = Color.LIGHT_GRAY;
	protected Color ovalColor = defaultStrongColor;
	protected Color defaultTextColor = Color.BLACK;
	protected Color textColor = Color.BLACK;
	
	protected Color markColor = new Color(58, 255, 0);
	protected Color defaultColorMax = new Color(255, 234, 13);
	protected Color defaultColorEdge = Color.PINK;
	protected Color defaultColorMsgData = new Color(255, 183, 138);
	
	protected static MessageData currentMsgDataAnimation = null;
	protected boolean readyAnimated = false;
	
	
	
	//constructers
	public MessageData(){
		
	}

	public MessageData(CIMAVertice sender, CIMAVertice receiver, int lamdaValue){
		this.sender = sender;
		this.receiver = receiver;
		this.lamdaValue = lamdaValue;
		
		calcArc();
	}


	@Override
	public String toString() {
		if(sender == null || receiver == null){
			return "NULL string";
		}
		return "Sender : "+sender.getName()+"  EmpfÃ¤nger : "+receiver.getName()+"  LamdaValue: "+lamdaValue;
	}

	//getter
	public int getValue(){
		return lamdaValue;
	}
	public CIMAVertice getSender(){
		return sender;
	}
	public CIMAVertice getReceiver(){
		return receiver;
	}
	
	public CIMAEdgeWeight getEdge(){
		if(sender.getParent() == receiver){
			return sender.getEdgeWeightToParent();
		}
		return receiver.getEdgeWeightToParent();
	}
	
	
	
	/***ANIMATION*/
	
	
	protected abstract void explainMessageData(Graphics2D g);
	protected abstract void clearExplainMessageData();
	
	
	
	
	protected void calcArc(){
	
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
		}
	
	protected double getAngle(int x, int y){
		
		double calc	= (Math.acos(x / Math.sqrt(x * x + y * y)));
			
		if(y > 0){
			return (2*Math.PI - calc);
		}
			
		return calc;
	
	}
	
	//draw animation
	public void drawAnimation(Graphics2D g){		
		
		//just draw if it is the animated msgData or if the animation is ready
		if(currentMsgDataAnimation != this && !readyAnimated){
			
			return;
		}
		if(!CIMAAnimation.getCIMAAnimation().animationIsInProgress() && !CIMAAnimation.getCIMAAnimation().singleStepAnimationIsInProgress()){
			return;
		}
		
		if(CIMAVertice.activeAgentAnimation){
			return;
		}		
		
		double animationAngle = this.animationAngle;
		
		
		//draw the msgData
		double kreisSegmentEndeAngle = angleSender + animationAngle;
		if(kreisSegmentEndeAngle >= angleReceiver){
			kreisSegmentEndeAngle = angleReceiver;
		}
		
		if(kreisSegmentEndeAngle >= 2*Math.PI){
			kreisSegmentEndeAngle %= 2*Math.PI;
		}
		
		int kreisSegmentEndeX = (int) (MiddlepunktKreisX + Math.cos(kreisSegmentEndeAngle) * radius);
		int kreisSegmentEndeY = (int) (MiddlepunktKreisY - Math.sin(kreisSegmentEndeAngle) * radius);
		
		g.setColor(ovalColor);
		int ovalMitteX = kreisSegmentEndeX;
		int ovalMitteY = kreisSegmentEndeY;
		g.fillOval(ovalMitteX - messageDataRadius, ovalMitteY - messageDataRadius, 2*messageDataRadius, 2*messageDataRadius);
		
		
		//draw the number inside the msgData
		g.setColor(textColor);
		String string = String.valueOf(lamdaValue);
		int stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(string,g).getWidth());
		Font defaultFont = g.getFont();
		g.setFont(CIMAConstants.getTextFont());
		g.drawString(string, ovalMitteX - stringWidth/2, ovalMitteY+messageDataRadius/2);
		g.setFont(defaultFont);
		
		
		//draw msgData-info in lower right corner only if this is the animated msgData
		if(currentMsgDataAnimation == this){
			explainMessageData(g);
		}
	}
	
	public void drawAnimationLine(Graphics2D g){
		
		
		//just draw if it is the animated msgData or if the animation is ready
		if(currentMsgDataAnimation != this && !readyAnimated){
			return;
		}

		if(!CIMAAnimation.getCIMAAnimation().animationIsInProgress() && !CIMAAnimation.getCIMAAnimation().singleStepAnimationIsInProgress()){
			return;
		}
		
		if(CIMAVertice.activeAgentAnimation){
			return;
		}
		
		
		double animationAngle = this.animationAngle;
		

		
		//draw the msgData LINE
		g.setColor(ovalColor);
		g.draw(new Arc2D.Double(MiddlepunktKreisX - radius, MiddlepunktKreisY - radius, 2*radius, 2*radius, Math.toDegrees(angleSender), Math.toDegrees(animationAngle), Arc2D.OPEN));

	}
	
	public void setOvalColor(Color color){
		setOvalColor(color, defaultTextColor);
	}
	public void setOvalColor(Color color, Color textcolor){
		this.ovalColor = color;
		this.textColor = textcolor;
	}
	public void resetColor(){
		this.ovalColor = this.defaultWeakColor;
		this.textColor = defaultTextColor;
	}
	
	
	//setter
	public void setReadyAnimated(){
		readyAnimated = true;
	}
	public void resetReadyAnimated(){
		readyAnimated = false;
	}
	public static int getAnimationSpeed(){
		return animationSpeed;
	}
	public static void setAnimationSpeed(int speed){
		animationSpeed = speed;
		if(animationSpeed < 0){
			animationSpeed = 0;
		}
	}
	
	public void resetCurrentmsgDataAnimation(){
		if(currentMsgDataAnimation != null){
			currentMsgDataAnimation.resetColor();
			currentMsgDataAnimation.clearExplainMessageData();
			currentMsgDataAnimation.setReadyAnimated();
		}
	}
	
	//start animation
	public SendMessageAnimationTimer animation(){
		SendMessageAnimationTimer timer = new SendMessageAnimationTimer();
		timer.start();
		resetCurrentmsgDataAnimation();
		currentMsgDataAnimation = this;
		currentMsgDataAnimation.setOvalColor(defaultStrongColor);
		return timer;
	}

	public class SendMessageAnimationTimer extends Thread{

		Gui gui;

		public SendMessageAnimationTimer() {
			this.gui = CIMAGui.getGui();
		}

		@Override
		public void run() {

			animationAngle = 0;
			
			calcArc();//TODO ?

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
				if(CIMAAnimation.getCIMAAnimation().breakAnimation()){
					this.interrupt();
					break;
				}
			}
			
			gui.repaint();
		}
	}

}
