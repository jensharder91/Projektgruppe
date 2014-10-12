package tdti;

import java.util.Comparator;

public class MessageDataComparator implements Comparator<MessageData>{

	@Override
	public int compare(MessageData o1, MessageData o2) {

		//a1 and a2 are equal -> use c1 and c2 to sort
		if(o1.getA() == o2.getA()){
			return (o2.getC() - o1.getC());
		}
		return (o2.getA() - o1.getA());
	}

}
