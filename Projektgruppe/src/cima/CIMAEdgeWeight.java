package cima;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.naming.PartialResultException;

public class CIMAEdgeWeight implements IMarkable, Comparable<CIMAEdgeWeight>{
	
	private CIMAVertice vertice;
	private CIMAVertice parent;
	private int edgeWeightValue;
	private Color defaultOvalColor = /*new Color(240, 240, 240)*/new Color(0x33,0x44,0x55);
	private Color ovalColor = defaultOvalColor;
	private Color defaultTextColor = Color.WHITE;
	private Color textColor = defaultTextColor;
	private List<CIMAVertice> potentialVertices = new ArrayList<CIMAVertice>();
	
	//weightField
	int ovalWidth = 17;
	int ovalMiddleX = -1;
	int ovalMiddleY = -1;
	
	public CIMAEdgeWeight(CIMAVertice vertice){
		this(0, vertice, null);
	}
	public CIMAEdgeWeight(int edgeWeightValue, CIMAVertice vertice, CIMAVertice parent) {
		this.vertice = vertice;
		this.parent = parent;
		this.edgeWeightValue = edgeWeightValue;
	}
	
	public void draw(Graphics g){
		g.setColor(ovalColor);
		ovalMiddleX = Math.min(vertice.getMiddleX(), parent.getMiddleX()) + Math.abs(vertice.getMiddleX() - parent.getMiddleX()) / 2;
		ovalMiddleY = Math.min(vertice.getMiddleY(), parent.getMiddleY()) + Math.abs(vertice.getMiddleY() - parent.getMiddleY()) / 2;
		g.fillOval(ovalMiddleX - ovalWidth/2, ovalMiddleY - ovalWidth/2, ovalWidth, ovalWidth);

		g.setColor(textColor);
		String string = String.valueOf(edgeWeightValue);
		int stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(string,g).getWidth());
		Font defaultFont = g.getFont();
		g.setFont(CIMAConstants.getTextFont());
		g.drawString(string, ovalMiddleX - stringWidth/2, ovalMiddleY+vertice.diameter/4);
		g.setFont(defaultFont);
	}
	

	@Override
	public void markAsMax() {
		ovalColor = CIMAConstants.getMarkAsMaxColor();
		textColor = Color.BLACK;
	}
	
	public void setDrawPotentialData(boolean drawPotentialData){
		
		System.out.println("IN EDGE  ("+getEdgeName()+")");
				
		if(potentialVertices.size() > 0){
			System.out.println("edge case a");
			ovalColor = Color.GREEN;
			
			for(CIMAVertice vertice : potentialVertices){
				System.out.println("edge case b");
				System.out.println(vertice.getName());
				vertice.markColor(Color.GREEN);
				vertice.setDrawPotentialData(true);
				
			}
		}else{
			System.out.println("edge case c");
			ovalColor = Color.RED;
		}
	}

	@Override
	public void markAsSecMax() {
		ovalColor = CIMAConstants.getMarkAsSecMaxColor();
		textColor = Color.BLACK;
	}

	@Override
	public int getLamdaValue() {
		return edgeWeightValue;
	}
	public CIMAVertice getParent(){
		return parent;
	}
	public CIMAVertice getVertice(){ 
		return vertice;
	}

	@Override
	public void resetColor() {
		ovalColor = defaultOvalColor;
		textColor = defaultTextColor;
	}
	
	@Override
	public Color getColor() {
		return ovalColor;
	}
	
	@Override
	public void markColor(Color color) {
		ovalColor = color;
	}
	
	public boolean onEdgeWeightClick(int x, int y){
		if((Math.abs(this.ovalMiddleX - x) <= ovalWidth/2) && (Math.abs(this.ovalMiddleY - y) < ovalWidth/2)){
			return true;
		}
		return false;
	}

	
	
	public int getEdgeWeightValue(){
		return edgeWeightValue;
	}
	public void edgeWeightIncrease(){
		this.edgeWeightValue++;
	}
	public void edgeWeightDepress(){
		this.edgeWeightValue--;
		if(this.edgeWeightValue <= 0){
			this.edgeWeightValue = 1;
		}
	}
	public void setEdgeWeightToParent(int edgeWeight){
		this.edgeWeightValue = edgeWeight;
	}
	
	public void addToPotentialList(CIMAVertice newVertice){
		if(!potentialVertices.contains(newVertice)){
			potentialVertices.add(newVertice);
		}
	}
	public void cleanPotentialList(){
		potentialVertices.clear();
	}
	public String getEdgeName(){
		return vertice.getName() + " - " + parent.getName();
	}
	
	@Override
	/** 
	 *
	 *  order from HIGH to LOW (!)
	 * 
	 */	
	public int compareTo(CIMAEdgeWeight o) {
		return o.getEdgeWeightValue() - edgeWeightValue;
	}
	
	@Override
	public boolean equals(Object obj) {

		if(obj instanceof CIMAEdgeWeight){
			if(((CIMAEdgeWeight) obj).getParent().equals(parent)){
				if(((CIMAEdgeWeight) obj).getVertice().equals(vertice)){
					return true;
				}
			}
		}

		return false;
	}


}
