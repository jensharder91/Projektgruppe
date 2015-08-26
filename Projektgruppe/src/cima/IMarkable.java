package cima;

import java.awt.Color;

public interface IMarkable {
	
	public void setOvalColor(Color color);
	public void setOvalColor(Color color, Color textcolor);
	public void resetColor();
	public int getValue();

}
