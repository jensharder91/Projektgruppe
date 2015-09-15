package cima;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;


/**
 * 
 *   handels the animation animation
 *   agentNumber: from sender to receiver
 *
 */
public class AgentWayData {
	
	private CIMAVertice senderVertice;
	private CIMAVertice receiverVertice;
	private int agentNumber;
	
	
	//animation
    protected Gui gui = CIMAGui.getGui();
    protected double xMiddleAnimation;
    protected double yMiddleAnimation;
    private static int animationSpeed = 3;
    protected int diameter = 20;
    
    protected static AgentWayData currentAgentAnimation = null;
	
	
	
	// constrctor
	public AgentWayData(CIMAVertice sender, CIMAVertice receiver, int number){
		this.senderVertice  = sender;
		this.receiverVertice = receiver;
		this.agentNumber = number;
	}

	//getter
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
	
	//setter
	public static void setAnimationSpeed(int speed){
		animationSpeed = speed;
		if(animationSpeed < 0){
			animationSpeed = 0;
		}
	}
	
	
	//animation draw
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
	
	
    //start animation
	public AgentAnimationTimer animation(){
		currentAgentAnimation = this;
		AgentAnimationTimer timer = new AgentAnimationTimer();
		timer.start();
		return timer;
	}
	
	// animation thread
	public class AgentAnimationTimer extends Thread{
				
		public AgentAnimationTimer() {
			
		}
		 
		@Override
		public void run() {
			
			xMiddleAnimation = senderVertice.getMiddleX();
			yMiddleAnimation = senderVertice.getMiddleY();
						
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
				if(CIMAAnimation.getCIMAAnimation().breakAnimation()){
					this.interrupt();
				}
			}

			currentAgentAnimation = null;
			gui.repaint();
		}
	}
}
