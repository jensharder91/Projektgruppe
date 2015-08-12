package cima;

import java.awt.Color;
import java.awt.Graphics2D;

public class MessageData_simplePotential extends MessageData{
	
	protected PotentialData potentialData;
	
	public MessageData_simplePotential() {
		
	}
	public MessageData_simplePotential(CIMAVertice sender, CIMAVertice receiver, int lamdaValue, PotentialData potentialData) {
		this.sender = sender;
		this.receiver = receiver;
		this.lamdaValue = lamdaValue;
		this.potentialData = potentialData;
	}
	
	
	public PotentialData getPotentialData(){
		return potentialData;
	}
	
	@Override
	protected void explainMessageData(Graphics2D g) {
		String[] explainStrings = {"test1", "test2", "hier erkl‰r ich die msgData"};
		InfoDisplayClass.getInfoDisplayClass().displayInLowerRightCorner(g, Color.RED, null, explainStrings);
	}
	
	@Override
	protected void clearExplainMessageData() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String toString() {
		if(sender == null || receiver == null){
			return "NULL string";
		}
		return "Sender : "+sender.getName()+"  Empf√§nger : "+receiver.getName()+"  LamdaValue: "+lamdaValue+ " potentialData: "+potentialData;
	}
}
