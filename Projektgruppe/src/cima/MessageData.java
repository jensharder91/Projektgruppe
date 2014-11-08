package cima;

public class MessageData {
	
	private int lamdaValue;
	private CIMAVertice sender;

	public MessageData(int lamdaValue, CIMAVertice sender){
		this.lamdaValue = lamdaValue;
		this.sender = sender;
	}
	
	@Override
	public String toString() {
		return "Sender : "+sender.getName()+"  edgeWeighttoParent: "+lamdaValue;
	}
	
	public int getLamdaValue(){
		return lamdaValue;
	}
	public CIMAVertice getSender(){
		return sender;
	}
}
