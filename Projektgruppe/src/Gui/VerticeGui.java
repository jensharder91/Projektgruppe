package Gui;

import java.awt.Color;
import java.awt.Graphics;

import tdti.Vertice;

public class VerticeGui {

	private int xCoord;
	private int yCoord;
	private int xMittel;
	private int yMittel;
	private Vertice vertice;
	private int width = 12;
	private int height = width;

	public VerticeGui(int xCoord, int yCoord, Vertice vertice){
		this.xCoord = xCoord - width/2;
		this.yCoord = yCoord - height/2;
		this.xMittel = xCoord;
		this.yMittel = yCoord;

		this.vertice = vertice;
		vertice.setVerticeGui(this);
	}

	public void drawVertice(Graphics g){
		if(vertice.getParent() != null){
			g.setColor(Color.black);
			VerticeGui parentGui = vertice.getParent().getVerticeGui();
			g.drawLine(xMittel, yMittel, parentGui.getMittelX(), parentGui.getMittelY());
		}

		g.setColor(Color.red);
		g.fillOval(xCoord, yCoord, width, height);

		g.drawString("Psi: "+vertice.getPsi(), xCoord + width, yCoord + height);
	}

	public boolean isSamePoint(int x, int y){
		if((Math.abs(this.xMittel - x) <= width/2) && (Math.abs(this.yMittel - y) < height/2)){
			return true;
		}
		return false;
	}

	public Vertice getVertice(){
		return vertice;
	}
	public int getX(){
		return xCoord;
	}
	public int getY(){
		return yCoord;
	}
	public int getMittelX(){
		return xMittel;
	}
	public int getMittelY() {
		return yMittel;
	}
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}

}
