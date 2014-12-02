package cima;

import java.util.Comparator;

public class MessageDataComparator implements Comparator<MessageData>{

	@Override
	public int compare(MessageData o1, MessageData o2) {

		return (o2.getLamdaValue() - o1.getLamdaValue());
	}

}