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
	
	@Override
	public String toString() {
		if(sender == null || receiver == null){
			return "NULL string";
		}
		return "Sender : "+sender.getName()+"  Empfänger : "+receiver.getName()+"  LamdaValue: "+lamdaValue+ " potentialData: "+potentialData;
	}

}
