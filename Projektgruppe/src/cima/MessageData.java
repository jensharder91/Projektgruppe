package cima;

public class MessageData {
	
	private int edgeWeightToParent;
	private CIMAVertice sender;

	public MessageData(int edgeWeightToParent, CIMAVertice sender){
		this.edgeWeightToParent = edgeWeightToParent;
		this.sender = sender;
	}
	
	@Override
	public String toString() {
		return "Sender : "+sender+"  edgeWeighttoParent: "+edgeWeightToParent;
	}
	
	public int getEdgeWeightToParent(){
		return edgeWeightToParent;
	}
	public CIMAVertice getSender(){
		return sender;
	}
}
