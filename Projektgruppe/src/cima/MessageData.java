package cima;

import java.awt.Color;
import java.awt.Graphics;

public class MessageData {
	
	private int lamdaValue;
	private CIMAVertice sender;
	private CIMAVertice receiver;

	public MessageData(int lamdaValue, CIMAVertice sender, CIMAVertice receiver){
		this.lamdaValue = lamdaValue;
		this.sender = sender;
		this.receiver = receiver;
	}
	
	public void draw(Graphics g){
		
		
		g.setColor(Color.orange);
//		g.drawLine(getSender().getMittelX(), getSender().getMittelY(), getReceiver().getMittelX(), getReceiver().getMittelY());
		
		int mittelpunktX = Math.min(getSender().getMittelX(), getReceiver().getMittelX()) + Math.abs(getSender().getMittelX() - getReceiver().getMittelX()) / 2;
		int mittelpunktY = Math.min(getSender().getMittelY(), getReceiver().getMittelY()) + Math.abs(getSender().getMittelY() - getReceiver().getMittelY()) / 2;
				
		int vektorX = getReceiver().getMittelX() - getSender().getMittelX();
		int vektorY = getReceiver().getMittelY() - getSender().getMittelY();
		
		int orthVektorX = vektorY;
		int orthVektorY = -vektorX;
		
		int mittelpunktKreisX = mittelpunktX + orthVektorX;
		int mittelpunktKreisY = mittelpunktY + orthVektorY;
		
//		g.drawLine(mittelpunktX, mittelpunktY, startOrthVektorX, startOrthVektorY);
		
		int vektorKreisMitteSenderX = getSender().getMittelX() - mittelpunktKreisX;
		int vektorKreisMitteSenderY = getSender().getMittelY() - mittelpunktKreisY;
		int vektorKreisMitteReceiverX = getReceiver().getMittelX() - mittelpunktKreisX;
		int vektorKreisMitteReiciverY = getReceiver().getMittelY() - mittelpunktKreisY;
		
		int radius = (int) Math.sqrt((vektorKreisMitteSenderX) * (vektorKreisMitteSenderX)
										+ (vektorKreisMitteSenderY) * (vektorKreisMitteSenderY));
		
		
		int angleSender = getAngle(vektorKreisMitteSenderX, vektorKreisMitteSenderY);
		int angleReceiver = getAngle(vektorKreisMitteReceiverX, vektorKreisMitteReiciverY);
		
		if(angleReceiver < angleSender){
			angleReceiver += 360;
		}
		
		g.drawArc(mittelpunktKreisX - radius, mittelpunktKreisY - radius, 2*radius, 2*radius, angleSender, angleReceiver - angleSender);
		
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
	
	
}
