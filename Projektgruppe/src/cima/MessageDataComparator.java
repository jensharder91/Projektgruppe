package cima;

import java.util.Comparator;

public class MessageDataComparator implements Comparator<MessageData>{

	@Override
	public int compare(MessageData o1, MessageData o2) {
		
		if(o2.getValue() != o1.getValue() || o1.getEdge() == null || o2.getEdge() == null){
			return (o2.getValue() - o1.getValue());
		}

		return (o1.getEdge().getValue() - o2.getEdge().getValue());
		
	}

}