package cima;

import java.awt.Color;
import java.awt.Graphics;

import Gui.Gui;
import Tree.Vertice;
import Tree.Vertice.AgentAnimationTimer;

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
	private int ovalWidth = 17;
	private int segmentMitteX;
	private int segmentMitteY;

	private double animationAngle;
	private boolean activeAnimation = false;
	public static boolean animationInProgress = false;
	public static boolean clearGui = false;
	private boolean animationFinished = true;
	private Color ovalColor = Color.orange;


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

	public void draw(Graphics g){

//		g.drawOval(segmentMitteX -10, mittelpunktKreisY + segmentMitteY - 10, 20, 20);
//		System.out.println("##### which animation:");

		if(activeAnimation){
			clearGui = false;
			drawAnimation(g);
		}else{
			if(clearGui){
				return;
			}
			if((animationInProgress && animationFinished) || !animationInProgress){
//				System.out.println("##### normal draw (MessageData)");
//				g.setColor(ovalColor);
				g.setColor(Color.YELLOW);
				g.drawArc(mittelpunktKreisX - radius, mittelpunktKreisY - radius, 2*radius, 2*radius, (int)Math.toDegrees(angleSender), (int)Math.toDegrees(angleReceiver - angleSender));
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

	public void drawAnimation(Graphics g){

//		System.out.println("##### animation (MessageData)");

		g.setColor(ovalColor);
		g.drawArc(mittelpunktKreisX - radius, mittelpunktKreisY - radius, 2*radius, 2*radius, (int)Math.toDegrees(angleSender), (int)Math.toDegrees(animationAngle));

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

	private void drawMessageInfo(Graphics g, int ovalMitteX, int ovalMitteY){
		
		
		g.setColor(ovalColor);
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
		
		int abstand = (int) ((ovalWidth + 20) / 1.5); //20 hardcoded verticewidth
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
	
	public void markAsMax(){
		ovalColor = new Color(22, 16, 232);
	}
	public void markAsSecMax(){
		ovalColor = new Color(16, 222, 255);
	}
	public void resetColor(){
		ovalColor = Color.orange;
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

				animationAngle += Math.toRadians(1);
//				System.out.println("animation "+animationAngle + " bis... >= " + (angleReceiver - angleSender));




//				System.out.println(xMittelAnimation +" / "+yMittelAnimation);

				gui.repaint();

				try {
					Thread.sleep(50);
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
