package cima;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class CIMAEdgeWeight implements Comparable<CIMAEdgeWeight>{
	
	private CIMAVertice vertice;
	private CIMAVertice parent;
	private int edgeWeightValue;
	
	
    private Color defaultOvalColor = /*new Color(240, 240, 240)*/new Color(0x33,0x44,0x55);
    private Color ovalColor = defaultOvalColor;
    private Color defaultTextColor = Color.WHITE;
    private Color textColor = defaultTextColor;

	
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


	public CIMAVertice getParent(){
		return parent;
	}
	public CIMAVertice getVertice(){ 
		return vertice;
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
	public String getEdgeName(){
		String verticeName = "null";
		String parentName = "null";
		if(vertice != null){
			verticeName = vertice.getName();
		}
		if(parent != null){
			parentName = parent.getName();
		}
		return "Edge: ("+verticeName + " - " + parentName+")";
	}
	
	@Override
	public String toString() {
		return getEdgeName();
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
			
			if(((CIMAEdgeWeight) obj).getParent() != null && parent != null)
			
			
			if(((CIMAEdgeWeight) obj).getParent().equals(parent)){
				if(((CIMAEdgeWeight) obj).getVertice().equals(vertice)){
					return true;
				}
			}
		}

		return false;
	}


}
