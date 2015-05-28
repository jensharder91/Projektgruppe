package cima;

public class MessageData{

	protected int lamdaValue = 0;
	protected CIMAVertice sender = null;
	protected CIMAVertice receiver = null;
	
	public MessageData(){
		
	}

	public MessageData(CIMAVertice sender, CIMAVertice receiver, int lamdaValue){
		this.sender = sender;
		this.receiver = receiver;
		this.lamdaValue = lamdaValue;
	}


	@Override
	public String toString() {
		if(sender == null || receiver == null){
			return "NULL string";
		}
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
	
	public CIMAEdgeWeight getEdge(){
		if(sender.getParent() == receiver){
			return sender.getEdgeWeightToParent();
		}
		return receiver.getEdgeWeightToParent();
	}


}
