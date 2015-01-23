package Tree;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import cima.CIMAVertice;
import Gui.Gui;

public class Vertice {

	//Gui
	protected int xMittel;
	protected int yMittel;
	public static int RADIUS = 10;
	protected int width = RADIUS*2;
	protected int height = RADIUS*2;
	
    //animation
    protected Gui gui;
    protected double xMittelAnimation;
    protected double yMittelAnimation;
    protected boolean activeAgent = false;
    public static boolean activeAnimation = false;
    
    protected int currentAgents = 0;
    protected int moveAgentCounter = 0;
    protected boolean decontaminated = false;
    protected Color verticeColor = Color.white;


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
		drawAnimation(g);
		
//		System.out.println(activeAnimation);
		
	
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
		
		//chose color
		if(CIMAVertice.drawMu){
			if(activeAnimation){
				if(decontaminated){
					verticeColor = Color.GREEN;
				}else{
	//				verticeColor = Color.RED;
					verticeColor = new Color(255, 50, 0);
				}
				
			}else{
				verticeColor = Color.white;
			}
		}
		
		//draw vertice
		g.setColor(verticeColor);
		g.fillOval(xMittel - width/2, yMittel - height/2, width, height);
		g.setColor(new Color(0x33,0x44,0x55));
		g.drawOval(xMittel - width/2, yMittel - height/2, width, height);

//		if(activeAnimation){
////			g.setColor(Color.black);
//			if(decontaminated){
//				g.setColor(Color.GREEN);
//			}else{
//				g.setColor(Color.RED);
//			}
//			g.fillOval(xMittel - width/2, yMittel - height/2, width, height);
//		}else{
//			g.setColor(Color.WHITE);
//			g.fillOval(xMittel - width/2, yMittel - height/2, width, height);
//			g.setColor(new Color(0x33,0x44,0x55));
//			g.drawOval(xMittel - width/2, yMittel - height/2, width, height);
//		}
		
//		g.drawString(name, xMittel, yMittel);

		for(Vertice child : children){
			child.drawAllVertice(g);;
		}
	}
	
    protected void drawAnimation(Graphics g) {
//    	System.out.println("drawing.....");
    	
    	if(activeAgent){
//    		g.setColor(Color.black);
    		g.setColor(Color.green);
	        g.fillOval((int)(xMittelAnimation - width/2), (int)(yMittelAnimation - height/2), width, height);
			g.setColor(new Color(0x33,0x44,0x55));
			g.drawOval((int)(xMittelAnimation - width/2), (int)(yMittelAnimation - height/2), width, height);
			g.setColor(Color.black);
			String string = String.valueOf(moveAgentCounter);
			int stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(string,g).getWidth());
			g.drawString(string, (int)(xMittelAnimation - stringWidth/2), (int)(yMittelAnimation + height/4));
			
//			System.out.println("~~~~~~~~~~~~ moveAgentCoutner: "+moveAgentCounter);
    	}
	        
		for(Vertice child : children){
			child.drawAnimation(g);;
		}
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
	
	public void resetAllVerticeAnimation(){
		resetCurrentAgents();
		
		for(Vertice vertice : children){
			if(vertice.isDecontaminated()){
				vertice.resetAllVerticeAnimation();
			}
		}
		if(parent != null){
			if(parent.isDecontaminated()){
				parent.resetAllVerticeAnimation();
			}
		}
	}
	
	public void resetCurrentAgents(){
		this.currentAgents = 0;
		decontaminated = false;
	}
	public void changeCurrentAgents(int number){
		this.currentAgents += number;
		decontaminated = true;
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
	public boolean isDecontaminated(){
		return decontaminated;
	}

	
	public AgentAnimationTimer animation(Vertice destVertice, int moveAgentCounter){
		this.moveAgentCounter = moveAgentCounter;
		AgentAnimationTimer timer = new AgentAnimationTimer(destVertice);
		timer.start();
		return timer;
	}
	
	public class AgentAnimationTimer extends Thread{
		
		Vertice destVertice;
		int animationSpeed;
		
		public AgentAnimationTimer(Vertice destVertice) {
			this.destVertice = destVertice;
		}
		 
		@Override
		public void run() {
			
			xMittelAnimation = xMittel;
			yMittelAnimation = yMittel;
			
			animationSpeed = 3;
			
//			System.out.println(activeAgent);
			activeAgent = true;
//			System.out.println(activeAgent);
			
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
				}
				
				if(vektorLength < 2*animationSpeed){
					this.interrupt();
				}
			}
			
//			System.out.println(activeAgent);
			activeAgent = false;
//			System.out.println(activeAgent);
//			System.out.println("~~~~~~~~~~~");
			gui.repaint();
		}
	}

}
