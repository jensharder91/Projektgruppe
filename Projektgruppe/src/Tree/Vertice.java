package Tree;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import Gui.Gui;

public class Vertice {

	//Gui
	protected int xMittel;
	protected int yMittel;
	protected int width = 20;
	protected int height = width;
	
    //animation
    protected Gui gui;
    protected double xMittelAnimation;
    protected double yMittelAnimation;
    protected boolean activeAnimation = false;


	protected Vertice parent;
	protected List<Vertice> children = new ArrayList<Vertice>();
	protected String name;

	public Vertice(String name, Vertice parent, Gui gui){
		this.name = name;
		if(parent instanceof Vertice){
			this.parent = parent;
			this.parent.addChild(this);
		}
		this.gui = gui;


		System.out.println("new Vertice created : "+name);
	}

	public String getName(){
		return this.name;
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
		int count = this.children.size();
		if(this.parent instanceof Vertice){
			count++;
		}
		return count;
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

	public void drawTree(Graphics g, int areaX, int areaY, int areaWidth, int areaHeight){
		int levelHeight = (areaHeight-areaY) / (this.getSubtreeHeight()+1);
		levelHeight = Math.min(100,levelHeight); // maximum Level Height: 100
		calcPoints(areaX, areaY, areaWidth, levelHeight);
		drawAllTreeLines(g);
		drawAllVertice(g);
		
//		System.out.println(activeAnimation);
		
		if(activeAnimation){
			drawAnimation(g);
		}
	}

	protected void calcPoints(int areaX, int areaY, int areaWidth, int levelHeight){
		int pointX = areaX + areaWidth/2;
		this.xMittel = pointX + width/2;
		this.yMittel = areaY + height/2;

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
	protected void drawAllTreeLines(Graphics g){
		if(parent != null){
			g.setColor(new Color(0x33,0x44,0x55));
			g.drawLine(xMittel, yMittel, parent.getMittelX(), parent.getMittelY());
		}
		for(Vertice child : children){
			child.drawAllTreeLines(g);
		}
	}

	protected void drawAllVertice(Graphics g){

		g.setColor(Color.white);
		g.fillOval(xMittel - width/2, yMittel - height/2, width, height);
		g.setColor(new Color(0x33,0x44,0x55));
		g.drawOval(xMittel - width/2, yMittel - height/2, width, height);
		
		g.drawString(name, xMittel, yMittel);

		for(Vertice child : children){
			child.drawAllVertice(g);;
		}
	}
	
    protected void drawAnimation(Graphics g) {
//    	System.out.println("drawing.....");
        g.setColor(Color.darkGray);
        g.fillOval((int)(xMittelAnimation - width/2), (int)(yMittelAnimation - height/2), width, height);
    }


	public boolean isSamePoint(int x, int y){
		System.out.println("Comparing to "+this.xMittel+","+this.yMittel);
		if((Math.abs(this.xMittel - x) <= width/2) && (Math.abs(this.yMittel - y) < height/2)){
			return true;
		}
		return false;
	}

	public Vertice pointExists(int x, int y){

		if(isSamePoint(x, y)){
			return this;
		}
		for(Vertice child : children){
			Vertice vertice = child.pointExists(x, y);
			if(vertice != null){
				return vertice;
			}
		}
		return null;
	}

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

	public Vertice getParent(){
		return parent;
	}
	public int getMittelX(){
		return xMittel;
	}
	public int getMittelY(){
		return yMittel;
	}
	public int getX(){
		return xMittel - width/2;
	}
	public int getY(){
		return yMittel - height/2;
	}
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
	public List<Vertice> getChildren(){
		return children;
	}
	
	public AnimationTimer animation(Vertice destVertice){
		AnimationTimer timer = new AnimationTimer(destVertice);
		timer.start();
		return timer;
	}
	
	public class AnimationTimer extends Thread{
		
		Vertice destVertice;
		int animationSpeed;
		
		public AnimationTimer(Vertice destVertice) {
			this.destVertice = destVertice;
		}
		 
		@Override
		public void run() {
			
			xMittelAnimation = xMittel;
			yMittelAnimation = yMittel;
			
			animationSpeed = 2;
			
			System.out.println(activeAnimation);
			activeAnimation = true;
			System.out.println(activeAnimation);
			
			while(isInterrupted() == false){
				
				double vektorX = destVertice.getMittelX() - xMittelAnimation;
				double vektorY = destVertice.getMittelY() - yMittelAnimation;
				
				double vektorLength = Math.sqrt(vektorX*vektorX + vektorY*vektorY);
				
				xMittelAnimation += animationSpeed * vektorX/vektorLength;
				yMittelAnimation += animationSpeed * vektorY/vektorLength;
				
//				System.out.println(xMittelAnimation +" / "+yMittelAnimation);
				
				gui.repaint();
								
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
				
				if(vektorLength < 2*animationSpeed){
					this.interrupt();
				}
			}
			
			System.out.println(activeAnimation);
			activeAnimation = false;
			System.out.println(activeAnimation);
			System.out.println("~~~~~~~~~~~");
			gui.repaint();
		}
	}

}
