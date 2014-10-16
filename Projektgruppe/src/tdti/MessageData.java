package tdti;

public class MessageData{

	private int a;
	private int c;
	private Vertice sender;

	public MessageData(int a, int c, Vertice sender){
		this.a = a;
		this.c = c;
		this.sender = sender;
	}

	@Override
	public String toString() {
		return "("+getA()+"|"+getC()+") from "+this.sender.getName();
	}

	public int getA(){
		return a;
	}
	public int getC(){
		return c;
	}
	public Vertice getSender(){
		return sender;
	}
}
