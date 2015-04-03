package cima;

import java.awt.Color;

public interface IMarkable {

	public void markColor(Color color);
	public void markAsMax();
	public void markAsSecMax();
	public int getLamdaValue();
	public void resetColor();
	public Color getColor();
}
