package cima;

public class AgentWayData {
	
	private CIMAVertice senderVertice;
	private CIMAVertice receiverVertice;
	private int agentNumber;
	
	public AgentWayData(CIMAVertice sender, CIMAVertice receiver, int number){
		this.senderVertice  = sender;
		this.receiverVertice = receiver;
		this.agentNumber = number;
	}

	public CIMAVertice getSender(){
		return senderVertice;
	}
	public CIMAVertice getReceiver(){
		return receiverVertice;
	}
	public int getAgentNumber(){
		return agentNumber;
	}
}
