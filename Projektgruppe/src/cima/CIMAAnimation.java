package cima;

import java.util.List;

import Gui.Gui;
import Tree.Vertice;
import Tree.Vertice.AnimationTimer;

public class CIMAAnimation {
	
	private static CIMAAnimation animation = null;
	private boolean breakThread = false;
	private static Gui gui;
	private static int index = 0;
	
	private boolean activeAgent = false;
	public static boolean singeAnimationModus = false;
	
	/**Singleton*/
	public static CIMAAnimation getCIMAAnimation(Gui gui){
		
		CIMAAnimation.gui = gui;
		if(animation == null){
			animation = new CIMAAnimation();
		}
		return animation;
	}
	
	private CIMAAnimation(){
		
	}
	
	public void startAnimation(List<AgentWayData> agentsWayList){
		
		singeAnimationModus = false;
		AnimationLoop animationLoop = new AnimationLoop(agentsWayList);
		animationLoop.start();
		
	}
	
	public void stopAnimation(){
		breakThread = true;
		Vertice.activeAnimation = false;
		gui.repaint();
	}
	
	public void nextStepAnimation(List<AgentWayData> agentsWayList, boolean nextStep){
		
		if(activeAgent){
			return;
		}
		
		singeAnimationModus  = true;
		AnimationLoop animationLoop = new AnimationLoop(agentsWayList, singeAnimationModus, nextStep);
		animationLoop.start();
	}
	
	
	public class AnimationLoop extends Thread{

		private List<AgentWayData> agentsWayList;
		private boolean singleStepAnimation = false;
		private boolean nextStep = false;
		
		public AnimationLoop(List<AgentWayData> agentsWayList) {
			this(agentsWayList, false, false);
		}
		public AnimationLoop(List<AgentWayData> agentsWayList, boolean singeStepAnimation, boolean nextStep) {
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
		
		private AnimationTimer doAnimation(int i){
			
			System.out.println("make animation from "+agentsWayList.get(i).getSender().getName()+" to "+agentsWayList.get(i).getReceiver().getName());
			
			agentsWayList.get(i).getSender().changeCurrentAgents(- agentsWayList.get(i).getAgentNumber());
			
			AnimationTimer timer = agentsWayList.get(i).getSender().animation(agentsWayList.get(i).getReceiver(), agentsWayList.get(i).getAgentNumber());
			
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

}
