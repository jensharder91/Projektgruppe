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
	
	private int angleSender;
	private int angleReceiver;
	private int radius;
	private int mittelpunktKreisX;
	private int mittelpunktKreisY;
	
	private int animationAngle;
	private boolean activeAnimation = false;
	public static boolean animationInProgress = false;
	private boolean animationFinished = true;
	

	public MessageData(int lamdaValue, CIMAVertice sender, CIMAVertice receiver){
		this.lamdaValue = lamdaValue;
		this.sender = sender;
		this.receiver = receiver;
		
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
			angleReceiver += 360;
		}
	}

	public void draw(Graphics g){
		
//		System.out.println("##### which animation:");
		
		if(activeAnimation){
			drawAnimation(g);
		}else{
			if((animationInProgress && animationFinished) || !animationInProgress){
//				System.out.println("##### normal draw (MessageData)");
				g.setColor(Color.orange);
				g.drawArc(mittelpunktKreisX - radius, mittelpunktKreisY - radius, 2*radius, 2*radius, angleSender, angleReceiver - angleSender);
			}
		}
		
	}
	
	public void drawAnimation(Graphics g){
		
//		System.out.println("##### animation (MessageData)");
		
		g.setColor(Color.orange);
		g.drawArc(mittelpunktKreisX - radius, mittelpunktKreisY - radius, 2*radius, 2*radius, angleSender, animationAngle);
	}
	
	private int getAngle(int x, int y){
		
		double calc = (Math.acos(x / Math.sqrt(x * x + y * y)));
		if(y > 0){
			return (int) (360 - (calc * 180/Math.PI));
		}
		
		return (int) (calc * 180/Math.PI);
		
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
	
		
	public SendMessageAnimationTimer animation(Gui gui, Graphics g){
		SendMessageAnimationTimer timer = new SendMessageAnimationTimer(gui, g);
		timer.start();
		return timer;
	}
	
	public class SendMessageAnimationTimer extends Thread{
		
		Gui gui;
		Graphics g;
		int animationSpeed;
		
		public SendMessageAnimationTimer(Gui gui, Graphics g) {
			this.gui = gui;
			this.g = g;
		}
		 
		@Override
		public void run() {
			
			animationSpeed = 3;
			
//			System.out.println(activeAgent);
			activeAnimation = true;
//			System.out.println(activeAgent);
			animationAngle = 0;
			
			while(isInterrupted() == false){

				animationAngle += 1;
				
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
			
//			System.out.println(activeAgent);
			activeAnimation = false;
//			System.out.println(activeAgent);
//			System.out.println("~~~~~~~~~~~");
			gui.repaint();
		}
	}
	
}
