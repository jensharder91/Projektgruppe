package tdti;

public class MessageData {

	private int a;
	private int c;
	private TDTIVertice sender;

	public MessageData(int a, int c, TDTIVertice sender){
		this.a = a;
		this.c = c;
		this.sender = sender;
	}

	@Override
	public String toString() {
		return "("+getA()+"|"+getC()+") from "+this.sender.getName();
	}

	public String guiString() {
		return getA()+","+getC();
	}

	public int getA(){
		return a;
	}
	public int getC(){
		return c;
	}
	public TDTIVertice getSender(){
		return sender;
	}
}
