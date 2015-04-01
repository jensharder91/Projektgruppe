package cima;

public interface ICalcStrategy {
	
	public MessageData calcMessageData(CIMAVertice senderNode, CIMAVertice receiverNode);

}
