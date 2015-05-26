package cima;

import java.util.Comparator;

public class MessageDataComparator implements Comparator<MessageData>{

	@Override
	public int compare(MessageData o1, MessageData o2) {
		
		if(o2.getLamdaValue() != o1.getLamdaValue() || o1.getEdge() == null || o2.getEdge() == null){
			return (o2.getLamdaValue() - o1.getLamdaValue());
		}

		return (o1.getEdge().getEdgeWeightValue() - o2.getEdge().getEdgeWeightValue());
		
	}

}