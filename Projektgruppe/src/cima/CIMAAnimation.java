package cima;

import java.util.List;

import Tree.Vertice;
import Tree.Vertice.AnimationTimer;

public class CIMAAnimation {
	
	private static CIMAAnimation animation = null;
	
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
	
	
	public class AnimationLoop extends Thread{

		private List<AgentWayData> agentsWayList;
		
		public AnimationLoop(List<AgentWayData> agentsWayList) {
			this.agentsWayList = agentsWayList;
		}
		 
		@Override
		public void run() {
			
			for(int i = 0; i < agentsWayList.size(); i++){
				
				System.out.println("make animation from "+agentsWayList.get(i).getSender().getName()+" to "+agentsWayList.get(i).getReceiver().getName());
				
				AnimationTimer timer = agentsWayList.get(i).getSender().animation(agentsWayList.get(i).getReceiver());
				
//		        synchronized(timer){
					try {
						timer.join();
//						timer.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//		        }

			}
		}
	}

}
