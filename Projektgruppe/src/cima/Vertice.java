package cima;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class Vertice{

	//Gui
	protected int xMiddle;
	protected int yMiddle;
	protected int diameter = 20;

	protected Vertice parent;
	protected List<Vertice> children = new ArrayList<Vertice>();
	protected String name;

	protected Color lineColor = new Color(0x33,0x44,0x55);

	public Vertice(String name, Vertice parent){
		this.name = name;
		if(parent instanceof Vertice){
			this.parent = parent;
			this.parent.addChild(this);
		}
	}

	protected int getSubtreeHeight(){
		if(this.children.size() == 0){
			return 0;
		}

		int maxSubtreeHeight = 0;
		for(Vertice child : this.children){
			maxSubtreeHeight = Math.max(maxSubtreeHeight,child.getSubtreeHeight());
		}
		return maxSubtreeHeight+1;
	}

	/**
	 * Deletes a child from the list of children
	 * @param child child to be removed
	 */
	protected void deleteChild(Vertice child){
		children.remove(child);
	}

	/**
	 * Appends a child to the list of children
	 * @param child child to append
	 */
	protected void addChild(Vertice child){
		this.children.add(child);
	}

	/**
	 * Calculates the number of neighbors
	 * @return int
	 */
	protected int numberOfNeighbors(){
		return this.getNeighbors().size();
	}

	/**
	 * Returns a list of all neighbors
	 * @return List<Vertice>
	 */
	protected List<Vertice> getNeighbors(){
		List<Vertice> neighbors = new ArrayList<Vertice>(this.children);
		if(parent != null){
			neighbors.add(this.parent);
		}
		return neighbors;
	}

	/**
	 * Calculates the number of all vertices in tree
	 * @return int
	 */
	public int numberOfVertices(){
		return this.getRoot().numberOfVerticesInSubtree();
	}

	/**
	 * Calculates the number of descendants + the vertice itself
	 * @return int
	 */
	private int numberOfVerticesInSubtree(){
		int number = 0;
		for(Vertice child : children){
			number += child.numberOfVerticesInSubtree();
		}
		return number+1;
	}

	/**
	 * Logs the subtree rooted at the current Vertice to System.out recursively.
	 */
	public void logSubtree(){
		System.out.println(this);
		for(Vertice child : children){
			child.logSubtree();
		}
	}

	/**
	 * Draws the complete tree
	 */
	public void drawTree(Graphics2D g, int areaX, int areaY, int areaWidth, int areaHeight){
		this.getRoot().drawSubtree(g,areaX,areaY,areaWidth,areaHeight);
	}

	/**
	 * Draws the current subtree
	 */
	protected void drawSubtree(Graphics2D g, int areaX, int areaY, int areaWidth, int areaHeight){
		int levelHeight = (areaHeight-areaY) / (this.getSubtreeHeight()+1);
		levelHeight = Math.min(100,levelHeight); // maximum Level Height: 100
		calcPoints(areaX, areaY, areaWidth, levelHeight);
		drawAllTreeLines(g);
		drawAllVertice(g);
	}

	/**
	 * Draws all tree lines
	 */
	protected void drawAllTreeLines(Graphics g){
		if(parent != null){
			g.setColor(lineColor);
			g.drawLine(xMiddle, yMiddle, parent.getMiddleX(), parent.getMiddleY());
		}
		for(Vertice child : children){
			child.drawAllTreeLines(g);
		}
	}

	/**
	 * Draws all tree vertices with white color
	 */
	protected void drawAllVertice(Graphics g){
		drawAllVertice(g,Color.white);
	}

	/**
	 * Draws all tree vertices
	 */
	protected void drawAllVertice(Graphics g, Color fillColor){
		g.setColor(fillColor);
		g.fillOval(xMiddle - diameter/2, yMiddle - diameter/2, diameter, diameter);
		g.setColor(lineColor);
		g.drawOval(xMiddle - diameter/2, yMiddle - diameter/2, diameter, diameter);

		for(Vertice child : children){
			child.drawAllVertice(g);
		}
	}

	/**
	 * Calculates positions for all vertices
	 */
	protected void calcPoints(int areaX, int areaY, int areaWidth, int levelHeight){
		int pointX = areaX + areaWidth/2;
		this.xMiddle = pointX + diameter/2;
		this.yMiddle = areaY + diameter/2;

		int numberOfChildren = this.children.size();
		if(numberOfChildren == 0){ numberOfChildren = 1; }
		int subtreeAreaWidth = areaWidth / numberOfChildren;
		int subtreeAreaX = areaX;
		int subtreeAreaY = areaY + levelHeight;
		int childrenCounter = 0;

		for(Vertice child : this.children){
			subtreeAreaX = areaX + childrenCounter * subtreeAreaWidth;
			child.calcPoints(subtreeAreaX,subtreeAreaY,subtreeAreaWidth,levelHeight);
			childrenCounter++;
		}
	}

	/**
	 * Returns true if this vertex overlaps the given coordinates
	 * @return boolean
	 */
	public boolean isSamePoint(int x, int y){
		return ((Math.abs(this.xMiddle - x) <= diameter/2) && (Math.abs(this.yMiddle - y) < diameter/2));
	}

	/**
	 * Return the root vertice of this tree
	 * @return Vertice
	 */
	public Vertice getRoot(){
		if(parent != null){
			return parent.getRoot();
		}
		return this;
	}

	/**
	 * Returns the vertex at the given coordinates or null
	 * @return Vertice
	 */
	public Vertice getPoint(int x, int y){
		return this.getRoot().getPointOfSubtree(x,y);
	}

	/**
	 * Returns the vertex at the given coordinates in this subtree or null
	 * @return Vertice
	 */
	private Vertice getPointOfSubtree(int x, int y){
		if(isSamePoint(x, y)){
			return this;
		}
		for(Vertice child : children){
			Vertice vertice = child.getPointOfSubtree(x, y);
			if(vertice != null){
				return vertice;
			}
		}
		return null;
	}

	/**
	 * Deletes this vertice
	 */
	public void delete(){
		if(parent == null){
			return;
		}
		parent.deleteChild(this);
	}

	@Override
	public String toString(){
		return "Vertice ("+this.name+") ("+this.children.size()+" children)";
	}

	public String getName(){
		return this.name;
	}
	public Vertice getParent(){
		return parent;
	}
	public int getMiddleX(){
		return xMiddle;
	}
	public int getMiddleY(){
		return yMiddle;
	}
	public int getX(){
		return xMiddle - diameter/2;
	}
	public int getY(){
		return yMiddle - diameter/2;
	}
	public int getDiameter(){
		return diameter;
	}
	public List<Vertice> getChildren(){
		return children;
	}
	public void setLineColor(Color color){
		this.lineColor = color;
	}
}
