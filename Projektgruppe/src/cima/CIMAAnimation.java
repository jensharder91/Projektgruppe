package cima;

import java.util.List;

import Tree.Vertice;
import Tree.Vertice.AnimationTimer;

public class CIMAAnimation {
	
	private static CIMAAnimation animation = null;
	private boolean breakThread = false;
	
	public static CIMAAnimation getCIMAAnimation(){
		
		if(animation == null){
			animation = new CIMAAnimation();
		}
		return animation;
	}
	
	private CIMAAnimation(){
		
	}
	
	public void startAnimation(List<AgentWayData> agentsWayList){
		
		AnimationLoop animationLoop = new AnimationLoop(agentsWayList);
		animationLoop.start();
		
	}
	
	public void stopAnimation(){
		breakThread = true;
	}
	
	
	public class AnimationLoop extends Thread{

		private List<AgentWayData> agentsWayList;
		
		public AnimationLoop(List<AgentWayData> agentsWayList) {
			this.agentsWayList = agentsWayList;
		}
		 
		@Override
		public void run() {
			
			Vertice.activeAnimation = true;
			breakThread = false;
			
			//bis size - 1 weil der letzte schritt die animation null -> homebase ist und übersprungen werden muss
			for(int i = 0; i < agentsWayList.size() -1; i++){
				
				//breche bei bedarf die animation ab!
				if(breakThread){
					break;
				}
				
				System.out.println("make animation from "+agentsWayList.get(i).getSender().getName()+" to "+agentsWayList.get(i).getReceiver().getName());
				
				agentsWayList.get(i).getSender().changeCurrentAgents(- agentsWayList.get(i).getAgentNumber());
				
				AnimationTimer timer = agentsWayList.get(i).getSender().animation(agentsWayList.get(i).getReceiver(), agentsWayList.get(i).getAgentNumber());
				
//		        synchronized(timer){
					try {
						timer.join();
//						timer.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//		        }
				agentsWayList.get(i).getReceiver().changeCurrentAgents(agentsWayList.get(i).getAgentNumber());
				
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			
			Vertice.activeAnimation = false;
		}
	}

}
