package cima;

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

}
