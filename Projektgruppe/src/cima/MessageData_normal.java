package cima;

import java.awt.Color;
import java.awt.Graphics2D;

public class MessageData_normal extends MessageData{
	
	public MessageData_normal(){
		
	}

	public MessageData_normal(CIMAVertice sender, CIMAVertice receiver, int lamdaValue){
		this.sender = sender;
		this.receiver = receiver;
		this.lamdaValue = lamdaValue;
		
		calcArc();
	}
	
	@Override
	protected void explainMessageData(Graphics2D g) {
		String[] explainStrings = {"test1", "test2", "hier erklär ich die msgData"};
		InfoDisplayClass.getInfoDisplayClass().displayInLowerRightCorner(g, Color.RED, null, explainStrings);
	}

	@Override
	protected void clearExplainMessageData() {
		// TODO Auto-generated method stub
		
	}

}
