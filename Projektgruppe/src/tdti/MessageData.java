package tdti;

public class MessageData{
	
	private int a;
	private int c;
	
	public MessageData(int a, int c){
		this.a = a;
		this.c = c;
	}
	
	@Override
	public String toString() {
		return "MessageData: a: "+getA()+"  c: "+getC();
	}
	
	public int getA(){
		return a;
	}
	public int getC(){
		return c;
	}
}
