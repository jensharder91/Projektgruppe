package cima;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import cima.MessageData.SendMessageAnimationTimer;
import Gui.Gui;
import Tree.Vertice;
import Tree.Vertice.AgentAnimationTimer;

public class CIMAAnimation {
	
	private static CIMAAnimation animation = null;
	private boolean breakThread = false;
	private static Gui gui;
	private static Graphics g;
	private static int index = 0;
	
	private boolean activeAgent = false;
	public static boolean singeAnimationModus = false;
	
	/**Singleton*/
	public static CIMAAnimation getCIMAAnimation(Gui gui, Graphics g){
		
		CIMAAnimation.gui = gui;
		CIMAAnimation.g = g;
		
		if(animation == null){
			animation = new CIMAAnimation();
		}
		return animation;
	}
	
	private CIMAAnimation(){
		
	}
	
	/*
	 * 
	 *  Agent Animation....
	 *  
	 */
	
	public void startAgentAnimation(List<AgentWayData> agentsWayList){
		
		singeAnimationModus = false;
		AnimationAgentLoop animationLoop = new AnimationAgentLoop(agentsWayList);
		animationLoop.start();
		
	}
	
	public void stopAgentAnimation(){
		breakThread = true;
		Vertice.activeAnimation = false;
		gui.repaint();
	}
	
	public void nextStepAgentAnimation(List<AgentWayData> agentsWayList, boolean nextStep){
		
		if(activeAgent){
			return;
		}
		
		singeAnimationModus  = true;
		AnimationAgentLoop animationLoop = new AnimationAgentLoop(agentsWayList, singeAnimationModus, nextStep);
		animationLoop.start();
	}
	
	/*
	 * 
	 * SendMessage Animation
	 *
	 */
	
	public void startSendMessageAnimation(List<MessageData> messageDataList){
		
		System.out.println("in start sendMessage ANIMATION");
		
		singeAnimationModus = false;
		AnimationSendMessageLoop animationLoop = new AnimationSendMessageLoop(messageDataList);
		animationLoop.start();
		
	}
	
	/*
	 * 
	 * Animation Loop Threads.....
	 *
	 */
	
	public class AnimationAgentLoop extends Thread{

		private List<AgentWayData> agentsWayList;
		private boolean singleStepAnimation = false;
		private boolean nextStep = false;
		
		public AnimationAgentLoop(List<AgentWayData> agentsWayList) {
			this(agentsWayList, false, false);
		}
		public AnimationAgentLoop(List<AgentWayData> agentsWayList, boolean singeStepAnimation, boolean nextStep) {
			this.agentsWayList = agentsWayList;
			this.singleStepAnimation = singeStepAnimation;
			this.nextStep = nextStep;
		}
		 
		@Override
		public void run() {
			
			Vertice.activeAnimation = true;
			activeAgent = true;
			
			System.out.println("##### start animation #####");
			
			gui.repaint();
			
			if(singleStepAnimation){
				
				doAnimation(index);
				if(nextStep){
					index++;
				}else{
					index--;
				}
				
				if(index < 0){
					index = 0;
				}
				
				//bis size - 1 weil der letzte schritt die animation null -> homebase ist und übersprungen werden muss
				if(index >= agentsWayList.size() -1){
					Vertice.activeAnimation = false;
//					Gui.calcAgentMovesReady = false;
					index = 0;
				}
				
			}else{
				
				breakThread = false;
			
				//bis size - 1 weil der letzte schritt die animation null -> homebase ist und übersprungen werden muss
				for(int i = index; i < agentsWayList.size() -1; i++){
					
	//				Gui.rootVertice.logSubtree();
					
					//breche bei bedarf die animation ab!
					if(breakThread){
						break;
					}
					
					pauseAnimation();
									
					doAnimation(i);
	
				}
				
				if(!breakThread){
					pauseAnimation();
				}
				Vertice.activeAnimation = false;
				index = 0;
			}
			
			gui.repaint();
			activeAgent = false;
			
		}
		
		private AgentAnimationTimer doAnimation(int i){
			
			System.out.println("make animation from "+agentsWayList.get(i).getSender().getName()+" to "+agentsWayList.get(i).getReceiver().getName());
			
			agentsWayList.get(i).getSender().changeCurrentAgents(- agentsWayList.get(i).getAgentNumber());
			
			AgentAnimationTimer timer = agentsWayList.get(i).getSender().animation(agentsWayList.get(i).getReceiver(), agentsWayList.get(i).getAgentNumber());
			
//	        synchronized(timer){
				try {
					timer.join();
//					timer.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//	        }
			agentsWayList.get(i).getReceiver().changeCurrentAgents(agentsWayList.get(i).getAgentNumber());
			
			gui.repaint();
			return timer;
		}
		
		private void pauseAnimation(){
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public class AnimationSendMessageLoop extends Thread{
		
		List<MessageData> messageDataList = new ArrayList<MessageData>();
		
		public AnimationSendMessageLoop(List<MessageData> messageDataList) {
			this.messageDataList = messageDataList;
		}
		
		@Override
		public void run() {
			
			System.out.println("start animation :)");
			MessageData.animationInProgress = true;
			
			for(int j  = 0; j < messageDataList.size(); j++){
				System.out.println("for loop.... j:"+j);
				messageDataList.get(j).prepareForAnimation();
			}
			
			for(int i = 0; i < messageDataList.size(); i++){
				System.out.println("for loop.... i:"+i);
				
//				gui.repaint();
				
				for(int j  = 0; j < i; j++){
					System.out.println("for loop.... j:"+j);
					messageDataList.get(j).animationFinished();
				}
				
				SendMessageAnimationTimer timer = messageDataList.get(i).animation(gui, g);
				try {
					timer.join();
//					timer.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			MessageData.animationInProgress = false;

		}
		
	}

}
