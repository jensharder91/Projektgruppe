package cima;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;


public class AgentWayData {
	
	private CIMAVertice senderVertice;
	private CIMAVertice receiverVertice;
	private int agentNumber;
	
	
	//animation
    protected Gui gui = CIMAGui.getGui();
    protected double xMiddleAnimation;
    protected double yMiddleAnimation;
//    protected boolean activeAgent = false;
//    public static boolean activeAnimation = false;
//    private boolean marked = false;
    private static int animationSpeed = 3;
    protected int diameter = 20;
//    private static String displayedInfoString ="";
//    private boolean drawPotentialData = false;
    
//    protected int currentAgents = 0;
//    protected int moveAgentCounter = 0;
//    protected boolean decontaminated = false;
//    protected Color verticeColor = Color.white;
    protected static AgentWayData currentAgentAnimation = null;
	
	
	
	
	public AgentWayData(CIMAVertice sender, CIMAVertice receiver, int number){
		this.senderVertice  = sender;
		this.receiverVertice = receiver;
		this.agentNumber = number;
	}

	public CIMAVertice getSender(){
		return senderVertice;
	}
	public CIMAVertice getReceiver(){
		return receiverVertice;
	}
	public int getAgentNumber(){
		return agentNumber;
	}
	
	public static int getAnimationSpeed(){
		return animationSpeed;
	}
	public static void setAnimationSpeed(int speed){
		animationSpeed = speed;
		if(animationSpeed < 0){
			animationSpeed = 0;
		}
	}
	
	
	//animation
    public void drawAnimation(Graphics2D g) {
    	
    	if(currentAgentAnimation == this){
    		g.setColor(Color.green);
	        g.fillOval((int)(xMiddleAnimation - diameter/2), (int)(yMiddleAnimation - diameter/2), diameter, diameter);
			g.setColor(new Color(0x33,0x44,0x55));
			g.drawOval((int)(xMiddleAnimation - diameter/2), (int)(yMiddleAnimation - diameter/2), diameter, diameter);
			g.setColor(Color.black);
			String string = String.valueOf(agentNumber);
			int stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(string,g).getWidth());
			Font defaultFont = g.getFont();
			g.setFont(CIMAConstants.getTextFont());
			g.drawString(string, (int)(xMiddleAnimation - stringWidth/2), (int)(yMiddleAnimation + diameter/4));
			g.setFont(defaultFont);
    	}
	        
    }
	
	
	public AgentAnimationTimer animation(){
		currentAgentAnimation = this;
		AgentAnimationTimer timer = new AgentAnimationTimer();
		timer.start();
		return timer;
	}
	
	public class AgentAnimationTimer extends Thread{
				
		public AgentAnimationTimer() {
			
		}
		 
		@Override
		public void run() {
			
			xMiddleAnimation = senderVertice.getMiddleX();
			yMiddleAnimation = senderVertice.getMiddleY();
			
//			activeAgent = true;
			
			while(isInterrupted() == false){
				
				double vektorX = receiverVertice.getMiddleX() - xMiddleAnimation;
				double vektorY = receiverVertice.getMiddleY() - yMiddleAnimation;
				
				double vektorLength = Math.sqrt(vektorX*vektorX + vektorY*vektorY);
				
				xMiddleAnimation += animationSpeed * vektorX/vektorLength;
				yMiddleAnimation += animationSpeed * vektorY/vektorLength;
				
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
			
//			activeAgent = false;
			currentAgentAnimation = null;
			gui.repaint();
		}
	}
}
