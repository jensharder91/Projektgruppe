package cima;

import java.util.ArrayList;
import java.util.List;

import cima.AgentWayData.AgentAnimationTimer;
import cima.MessageData.SendMessageAnimationTimer;


/**
 * 
 * 
 * starts / stops all animations (MessageData Animation and Agent Animation)
 *
 */
public class CIMAAnimation {

	private static CIMAAnimation cimaAnimation = null;
	private static boolean breakThread = false;
	private static Gui gui;
	private static int index = 0;

	private boolean animationInProgress = false;
	public static boolean singeAnimationModus = false;

	/**Singleton*/
	public static CIMAAnimation getCIMAAnimation(){

		CIMAAnimation.gui = CIMAGui.getGui();

		if(cimaAnimation == null){
			cimaAnimation = new CIMAAnimation();
		}
		return cimaAnimation;
	}

	private CIMAAnimation(){

	}
	
	//getter
	public boolean animationIsInProgress(){
		return animationInProgress;
	}
	public boolean singleStepAnimationIsInProgress(){
		return animationInProgress || singeAnimationModus;
	}
	public boolean breakAnimation(){
		return breakThread;
	}
	
	
	//stop all animations
	public void stopAllAnimations(){
				
		if(animationInProgress){
			index = 0;

			breakThread = true;
		}
		index = 0;
		singeAnimationModus = false;
		CIMAVertice.activeAgentAnimation = false;
	}

	/*
	 *
	 *  Agent Animation....
	 *
	 */
	public void startAgentAnimation(List<AgentWayData> agentsWayList){

		if(animationInProgress){
			return;
		}

		singeAnimationModus = false;
		AnimationAgentLoop animationLoop = new AnimationAgentLoop(agentsWayList);
		animationLoop.start();
	}


	public void nextStepAgentAnimation(List<AgentWayData> agentsWayList){

		if(animationInProgress){
			return;
		}

		singeAnimationModus  = true;
		AnimationAgentLoop animationLoop = new AnimationAgentLoop(agentsWayList);
		animationLoop.start();
	}

	/*
	 *
	 * SendMessage Animation
	 *
	 */
	public void startSendMessageAnimation(List<MessageData> messageDataList){
			
		if(animationInProgress){
			return;
		}

		singeAnimationModus = false;
		AnimationSendMessageLoop animationLoop = new AnimationSendMessageLoop(messageDataList);
		animationLoop.start();

	}
	
	public void nextStepSendMessageAnimation(List<MessageData> messageDataList){

		if(animationInProgress){
			return;
		}

		singeAnimationModus  = true;
		AnimationSendMessageLoop animationLoop = new AnimationSendMessageLoop(messageDataList);
		animationLoop.start();
	}
	

	/*
	 *
	 * Animation AGENTS Loop Threads.....
	 *
	 */
	public class AnimationAgentLoop extends Thread{

		private List<AgentWayData> agentsWayList;

		public AnimationAgentLoop(List<AgentWayData> agentsWayList) {
			this.agentsWayList = agentsWayList;
		}

		@Override
		public void run() {

			CIMAVertice.activeAgentAnimation = true;
			animationInProgress = true;

			gui.repaint();

			if(singeAnimationModus){//step by step

				doAnimation(index);
				index++;

				if(index < 0){
					index = 0;
				}

				//bis size - 1 weil der letzte schritt die animation null -> homebase ist und übersprungen werden muss
				if(index >= agentsWayList.size() -1){
					CIMAVertice.activeAgentAnimation = false;
					index = 0;
				}

			}else{//complete animation

				breakThread = false;

				//bis size - 1 weil der letzte schritt die animation null -> homebase ist und übersprungen werden muss
				for(int i = index; i < agentsWayList.size() -1; i++){

					pauseAnimation();
					
					//breche bei bedarf die animation ab!
					if(breakThread){
						break;
					}

					doAnimation(i);

				}

				if(!breakThread){
					pauseAnimation();
				}
				CIMAVertice.activeAgentAnimation = false;
				index = 0;
			}

			animationInProgress = false;
			breakThread = false;
			gui.repaint();

		}

		private AgentAnimationTimer doAnimation(int i){

			agentsWayList.get(i).getSender().changeCurrentAgents(- agentsWayList.get(i).getAgentNumber());

			AgentAnimationTimer timer = agentsWayList.get(i).animation();

			try {
				timer.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(i < agentsWayList.size()){
				agentsWayList.get(i).getReceiver().changeCurrentAgents(agentsWayList.get(i).getAgentNumber());
			}

			gui.repaint();
			return timer;
		}

		private void pauseAnimation(){
			try {
				Thread.sleep(1000 - 75*AgentWayData.getAnimationSpeed());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/*
	 *
	 * Animation MsgData Loop Threads.....
	 *
	 */
	public class AnimationSendMessageLoop extends Thread{

		List<MessageData> messageDataList = new ArrayList<MessageData>();
		
		public AnimationSendMessageLoop(List<MessageData> messageDataList) {
			this.messageDataList = messageDataList;
		}

		@Override
		public void run() {
			
			CIMAVertice.drawMu = false;
			animationInProgress = true;

			if(singeAnimationModus){//step by step

				doAnimation(index);
				index++;

				if(index < 0){
					index = 0;
				}

				//bis size - 1 weil der letzte schritt die animation null -> homebase ist und übersprungen werden muss
				if(index > messageDataList.size() -1){
					index = 0;
					singeAnimationModus = false;
					if(!breakThread){
						CIMAVertice.drawMu = true;
					}
					
				}
				

			}else{//complete animation

				breakThread = false;
			
			
				for(int i = index; i < messageDataList.size(); i++){	
	
					doAnimation(i);
					
					if(breakThread){
						if(i < messageDataList.size()){
							messageDataList.get(i).clearExplainMessageData();
						}
						break;
					}
				}
				index = 0;
				singeAnimationModus = false;
				if(breakThread){
					for(MessageData msgData : messageDataList){
						msgData.clearExplainMessageData();
					}
					CIMAVertice.drawMu = false;
				}else{
					CIMAVertice.drawMu = true;
				}
			}
			breakThread = false;
			animationInProgress = false;
			gui.repaint();

		}
		
		
		private void doAnimation(int i){
			
			for(MessageData msgData : messageDataList){
				msgData.clearExplainMessageData();
			}
			SendMessageAnimationTimer timer = messageDataList.get(i).animation();
			try {
				timer.join();
				if(i == messageDataList.size()-1){
					messageDataList.get(i).resetCurrentmsgDataAnimation();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			gui.repaint();
		}

	}

}