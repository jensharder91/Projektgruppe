package cima;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;

import Gui.Gui;

public class MessageData {

	private int lamdaValue;
	private CIMAVertice sender;
	private CIMAVertice receiver;
	private MessageData calcMax1;
	private MessageData calcMax2;
	
	//animation / graphic

	private double angleSender;
	private double angleReceiver;
	private int radius;
	private int mittelpunktKreisX;
	private int mittelpunktKreisY;
	private int ovalWidth = 16;
	private int segmentMitteX;
	private int segmentMitteY;
	
	//testing
	int X_segMitteGewollt;
	int Y_segMitteGewollt;
	int X_mitte_segMitteGewollt_sender;
	int Y_mitte_segMitteGewollt_sender;
	int X_möglMittelpunkt;
	int Y_möglMittelpunkt;
	int tatsächlicherKreisMittelpunkt_X;
	int tatsächlicherKreisMittelpunkt_Y;

	private double animationAngle;
	private boolean activeAnimation = false;
	public static boolean animationInProgress = false;
	public static boolean clearGui = false;
	private boolean animationFinished = true;
	private Color defaultOvalColor = new Color(240, 240, 240);
	private Color ovalColor = defaultOvalColor;


	public MessageData(int lamdaValue, CIMAVertice sender, CIMAVertice receiver, MessageData calcMax1, MessageData calcMax2){
		this.lamdaValue = lamdaValue;
		this.sender = sender;
		this.receiver = receiver;
		this.calcMax1 = calcMax1;
		this.calcMax2 = calcMax2;

		if(getSender() != null && getReceiver() != null){
			calcArc();
		}
	}

	private void calcArc(){
		int mittelpunktX = Math.min(getSender().getMittelX(), getReceiver().getMittelX()) + Math.abs(getSender().getMittelX() - getReceiver().getMittelX()) / 2;
		int mittelpunktY = Math.min(getSender().getMittelY(), getReceiver().getMittelY()) + Math.abs(getSender().getMittelY() - getReceiver().getMittelY()) / 2;

		int vektorX = getReceiver().getMittelX() - getSender().getMittelX();
		int vektorY = getReceiver().getMittelY() - getSender().getMittelY();

		int orthVektorX = vektorY;
		int orthVektorY = -vektorX;

		mittelpunktKreisX = mittelpunktX + orthVektorX;
		mittelpunktKreisY = mittelpunktY + orthVektorY;
		
		//////TODO
		double orthLength = Math.sqrt(orthVektorX*orthVektorX + orthVektorY*orthVektorY);
		X_segMitteGewollt = (int) (mittelpunktX - orthVektorX/orthLength * 15);
		Y_segMitteGewollt = (int) (mittelpunktY - orthVektorY/orthLength * 15);
		
		X_mitte_segMitteGewollt_sender = Math.min(getSender().getMittelX(), X_segMitteGewollt) + Math.abs(getSender().getMittelX() - X_segMitteGewollt) / 2;
		Y_mitte_segMitteGewollt_sender = Math.min(getSender().getMittelY(), Y_segMitteGewollt) + Math.abs(getSender().getMittelY() - Y_segMitteGewollt) / 2;
		
		int vektor_sender_segMitteGewollt_X = X_segMitteGewollt - getSender().getMittelX();
		int vektor_sender_segMitteGewollt_Y = Y_segMitteGewollt - getSender().getMittelY();
		
		int orthVektor_sender_segMitteGewollt_X = vektor_sender_segMitteGewollt_Y;
		int orthVektor_sender_segMitteGewollt_Y = -vektor_sender_segMitteGewollt_X;
		
		X_möglMittelpunkt = X_mitte_segMitteGewollt_sender + orthVektor_sender_segMitteGewollt_X;
		Y_möglMittelpunkt = Y_mitte_segMitteGewollt_sender + orthVektor_sender_segMitteGewollt_Y;
		
		
		int x1 = mittelpunktX;
		int x2 = mittelpunktKreisX;
		int x3 = X_mitte_segMitteGewollt_sender;
		int x4 = X_möglMittelpunkt;
		int y1 = mittelpunktY;
		int y2 = mittelpunktKreisY;
		int y3 = Y_mitte_segMitteGewollt_sender;
		int y4 = Y_möglMittelpunkt;
		
	    double d = (x1-x2)*(y3-y4) - (y1-y2)*(x3-x4);
	    
        if (d == 0) System.out.println("problem");
    
     
    
        tatsächlicherKreisMittelpunkt_X = (int) (((x3-x4)*(x1*y2-y1*x2)-(x1-x2)*(x3*y4-y3*x4))/d);
    
        tatsächlicherKreisMittelpunkt_Y = (int) (((y3-y4)*(x1*y2-y1*x2)-(y1-y2)*(x3*y4-y3*x4))/d);
        
        mittelpunktKreisX = tatsächlicherKreisMittelpunkt_X;
        mittelpunktKreisY = tatsächlicherKreisMittelpunkt_Y;

		
		//

//		g.drawLine(mittelpunktX, mittelpunktY, startOrthVektorX, startOrthVektorY);

		int vektorKreisMitteSenderX = getSender().getMittelX() - mittelpunktKreisX;
		int vektorKreisMitteSenderY = getSender().getMittelY() - mittelpunktKreisY;
		int vektorKreisMitteReceiverX = getReceiver().getMittelX() - mittelpunktKreisX;
		int vektorKreisMitteReiciverY = getReceiver().getMittelY() - mittelpunktKreisY;

		radius = (int) Math.sqrt((vektorKreisMitteSenderX) * (vektorKreisMitteSenderX)
										+ (vektorKreisMitteSenderY) * (vektorKreisMitteSenderY));


		angleSender = getAngle(vektorKreisMitteSenderX, vektorKreisMitteSenderY);
		angleReceiver = getAngle(vektorKreisMitteReceiverX, vektorKreisMitteReiciverY);

		if(angleReceiver < angleSender){
			angleReceiver += 2*Math.PI;
		}


		int vektorKreisMitteMitteLinieX = mittelpunktX - mittelpunktKreisX;
		int vektorKreisMitteMitteLinieY = mittelpunktY - mittelpunktKreisY;
		double längeVektorKreisMitteMitteLinie = Math.sqrt(vektorKreisMitteMitteLinieX*vektorKreisMitteMitteLinieX + vektorKreisMitteMitteLinieY*vektorKreisMitteMitteLinieY);
		int vektorKreisMitteSegmentMitteX = (int) (vektorKreisMitteMitteLinieX/längeVektorKreisMitteMitteLinie * radius);
		int vektorKreisMitteSegmentMitteY = (int) (vektorKreisMitteMitteLinieY/längeVektorKreisMitteMitteLinie * radius);
		segmentMitteX = mittelpunktKreisX + vektorKreisMitteSegmentMitteX;
		segmentMitteY = mittelpunktKreisY + vektorKreisMitteSegmentMitteY;
	}
	
	public void drawLine(Graphics2D g){
		//after calc dont draw
		if(CIMAVertice.drawMu){
			return;
		}

		if(activeAnimation){
			clearGui = false;
			markAsAnimationColor();
			drawAnimationLine(g);
			resetColor();
		}else{
			if(clearGui || CIMAAnimation.breakThread){
				return;
			}
			if((animationInProgress && animationFinished) || !animationInProgress){
				g.setColor(ovalColor);
				g.drawArc(mittelpunktKreisX - radius, mittelpunktKreisY - radius, 2*radius, 2*radius, (int)Math.toDegrees(angleSender), (int)Math.toDegrees(angleReceiver - angleSender));
			}
		}
	}

	public void drawMessageData(Graphics2D g){

//		g.drawOval(segmentMitteX -10, mittelpunktKreisY + segmentMitteY - 10, 20, 20);
//		System.out.println("##### which animation:");
		
		/////TODO
//		g.setColor(Color.RED);
//		g.drawOval(X_segMitteGewollt-5, Y_segMitteGewollt-5, 10, 10);
//		g.setColor(Color.BLUE);
//		g.drawOval(X_mitte_segMitteGewollt_sender-5, Y_mitte_segMitteGewollt_sender-5, 10, 10);
//		g.drawLine(X_mitte_segMitteGewollt_sender, Y_mitte_segMitteGewollt_sender, X_möglMittelpunkt, Y_möglMittelpunkt);
//		g.setColor(Color.MAGENTA);
//		g.drawOval(tatsächlicherKreisMittelpunkt_X-5, tatsächlicherKreisMittelpunkt_Y-5, 10, 10);
		
		
		//after calc dont draw
		if(CIMAVertice.drawMu){
			return;
		}

		if(activeAnimation){
			clearGui = false;
			markAsAnimationColor();
			drawAnimation(g);
			resetColor();
		}else{
			if(clearGui || CIMAAnimation.breakThread){
				return;
			}
			if((animationInProgress && animationFinished) || !animationInProgress){
//				System.out.println("##### normal draw (MessageData)");
//				g.setColor(ovalColor);
//				g.setColor(Color.YELLOW);
//				g.setColor(ovalColor);
//				g.drawArc(mittelpunktKreisX - radius, mittelpunktKreisY - radius, 2*radius, 2*radius, (int)Math.toDegrees(angleSender), (int)Math.toDegrees(angleReceiver - angleSender));
//				drawArrow(g);
//				drawMessageInfo(g, segmentMitteX, segmentMitteY);
				
				double angle = getStopMessageDataAngle();
				
//				System.out.println(Math.toDegrees(angleReceiver%2*Math.PI) + " / "+Math.toDegrees(angle));
				
				int ovalMitteX = (int) (mittelpunktKreisX + Math.cos(angle) * radius);
				int ovalMitteY = (int) (mittelpunktKreisY - Math.sin(angle) * radius);
				
				drawMessageInfo(g, ovalMitteX, ovalMitteY);
				//
			}
		}

	}

	public void drawAnimationLine(Graphics2D g){
		g.setColor(ovalColor);
//		g.setColor(CIMAConstants.getMarkAsMaxColor());
		g.draw(new Arc2D.Double(mittelpunktKreisX - radius, mittelpunktKreisY - radius, 2*radius, 2*radius, Math.toDegrees(angleSender), Math.toDegrees(animationAngle), Arc2D.OPEN));
//		g.drawArc(mittelpunktKreisX - radius, mittelpunktKreisY - radius, 2*radius, 2*radius, (int)Math.toDegrees(angleSender), (int)Math.toDegrees(animationAngle));

	}
	public void drawAnimation(Graphics2D g){

//		System.out.println("##### animation (MessageData)");
		

//		g.setColor(ovalColor);
////		g.setColor(CIMAConstants.getMarkAsMaxColor());
//		g.draw(new Arc2D.Double(mittelpunktKreisX - radius, mittelpunktKreisY - radius, 2*radius, 2*radius, Math.toDegrees(angleSender), Math.toDegrees(animationAngle), Arc2D.OPEN));
////		g.drawArc(mittelpunktKreisX - radius, mittelpunktKreisY - radius, 2*radius, 2*radius, (int)Math.toDegrees(angleSender), (int)Math.toDegrees(animationAngle));

		//teste: messageInfo wird im kreissegment verschoben TODO 
		double kreisSegmentEndeAngle = angleSender + animationAngle;
		
		if(kreisSegmentEndeAngle >= getStopMessageDataAngle()){
			kreisSegmentEndeAngle = getStopMessageDataAngle();
		}
		
		if(kreisSegmentEndeAngle >= 2*Math.PI){
			kreisSegmentEndeAngle %= 2*Math.PI;
		}
		
		int kreisSegmentEndeX = (int) (mittelpunktKreisX + Math.cos(kreisSegmentEndeAngle) * radius);
		int kreisSegmentEndeY = (int) (mittelpunktKreisY - Math.sin(kreisSegmentEndeAngle) * radius);
		
//		System.out.println("Angle : "+Math.toDegrees(kreisSegmentEndeAngle)+"   y wert vom kreissegment: "+kreisSegmentEndeY);
		
//		drawMessageInfo(g, 100, kreisSegmentEndeY);
		drawMessageInfo(g, kreisSegmentEndeX, kreisSegmentEndeY);
		
//		//falls schon über die hälfte des kreissegment gezeichnet wurde:
//		if(animationAngle >= (angleReceiver - angleSender)/2){
//			g.setColor(ovalColor);
//			drawArrow(g);
//			drawMessageInfo(g, animationOvalMitteX, animationOvalMitteY);
//		}
	}

	private void drawMessageInfo(Graphics2D g, int ovalMitteX, int ovalMitteY){
		
		
		g.setColor(ovalColor);
//		g.setColor(CIMAConstants.getMarkAsMaxColor());
		g.fillOval(ovalMitteX - ovalWidth/2, ovalMitteY - ovalWidth/2, ovalWidth, ovalWidth);

		g.setColor(Color.black);
		String string = String.valueOf(lamdaValue);
		int stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(string,g).getWidth());
		g.drawString(string, ovalMitteX - stringWidth/2, ovalMitteY+ovalWidth/4);
	}

//	private void drawArrow(Graphics g){
//		//TODO pfeilspitze muss verbessert werden
//		int pfeillänge = 10;
//		double a = (int) (Math.PI/4 - Math.atan2((segmentMitteY - getSender().getMittelY()),(segmentMitteX - getSender().getMittelX())));
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
		
		int abstand = (int) ((ovalWidth + 20) / 1.8); //20 hardcoded verticewidth
//		double angle = Math.acos((abstand * abstand - radius * radius - radius * radius) / ( -2 * radius * radius));
		double angle = getAngle(radius, -abstand);
		
//		System.out.println("vorher : "+Math.toDegrees(angle));
		angle = angleReceiver - angle;
//		if(angle >= 2*Math.PI){
//			angle %= 2*Math.PI;
//		}
		
		return angle;
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


	public SendMessageAnimationTimer animation(Gui gui){
		SendMessageAnimationTimer timer = new SendMessageAnimationTimer(gui);
		timer.start();
		return timer;
	}

	public class SendMessageAnimationTimer extends Thread{

		Gui gui;
		int animationSpeed;

		public SendMessageAnimationTimer(Gui gui) {
			this.gui = gui;
		}

		@Override
		public void run() {

			animationSpeed = 3;

//			System.out.println(activeAgent);
			activeAnimation = true;
//			System.out.println(activeAgent);
			animationAngle = 0;
			
			System.out.println("calcmax1 == null ? : "+(calcMax1==null)+"  /  calcmax2 == null ? : "+(calcMax2==null));
			
			//mark the MessageData from the calc
			if(calcMax1 != null){
				calcMax1.markAsMax();
			}
			if(calcMax2 != null){
				calcMax2.markAsMax();
				sender.markAsMax();
			}
			if(calcMax1 != null && calcMax2 != null){
				if(calcMax1.getLamdaValue() >= calcMax2.getLamdaValue() + sender.getVerticeWeight()){
					calcMax1.markAsMax();
					calcMax2.markAsSecMax();
					sender.markAsSecMax();
				}else{
					calcMax1.markAsSecMax();
					calcMax2.markAsMax();
					sender.markAsMax();
				}
			}
			if(calcMax1 == null && calcMax2 == null){
				//has to be a leaf
				sender.markAsMax();
			}

			while(isInterrupted() == false){

//				animationAngle += Math.toRadians(1);
				
				animationAngle += 3f/radius;
//				animationAngle = Math.toRadians(Math.ceil(Math.toDegrees(animationAngle)));
				
				System.out.println("animationANgle "+animationAngle +"  /  animationAngle (degree) :"+Math.toDegrees(animationAngle));
//				System.out.println("animation "+animationAngle + " bis... >= " + (angleReceiver - angleSender));
//				System.out.println(xMittelAnimation +" / "+yMittelAnimation);

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

			//reset the MessageData from the calc
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
			
			
//			System.out.println(activeAgent);
			activeAnimation = false;
//			System.out.println(activeAgent);
//			System.out.println("~~~~~~~~~~~");
			gui.repaint();
		}
	}

}
