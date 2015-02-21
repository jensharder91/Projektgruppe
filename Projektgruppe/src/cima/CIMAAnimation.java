package cima;

import java.util.ArrayList;
import java.util.List;

import cima.MessageData.SendMessageAnimationTimer;
import Gui.Gui;
import Tree.Vertice;
import cima.CIMAVertice.AgentAnimationTimer;

public class CIMAAnimation {

	private static CIMAAnimation animation = null;
	public static boolean breakThread = false;
	private static Gui gui;
	private static int index = 0;

	private boolean activeAgent = false;
	public static boolean singeAnimationModus = false;

	/**Singleton*/
	public static CIMAAnimation getCIMAAnimation(){

		CIMAAnimation.gui = CIMAGui.getGui();

		if(animation == null){
			animation = new CIMAAnimation();
		}
		return animation;
	}

	private CIMAAnimation(){

	}
	
	public void stopAllAnimations(){
		
		if(CIMAVertice.activeAnimation || MessageData.animationInProgress){
			stopAgentAnimation();
			stopSendMessageAnimation();
		}
	}

	/*
	 *
	 *  Agent Animation....
	 *
	 */

	public void startAgentAnimation(List<AgentWayData> agentsWayList){
		
		if(breakThread){
			return;
		}
		
		if(activeAgent){
			return;
		}

		singeAnimationModus = false;
		AnimationAgentLoop animationLoop = new AnimationAgentLoop(agentsWayList);
		animationLoop.start();

	}

	public void stopAgentAnimation(){
		breakThread = true;
		CIMAVertice.activeAnimation = false;
		activeAgent = false;
		gui.repaint();
	}

	public void nextStepAgentAnimation(List<AgentWayData> agentsWayList){
		
		if(breakThread){
			return;
		}

		if(activeAgent){
			return;
		}

		singeAnimationModus  = true;
		AnimationAgentLoop animationLoop = new AnimationAgentLoop(agentsWayList, singeAnimationModus);
		animationLoop.start();
	}

	/*
	 *
	 * SendMessage Animation
	 *
	 */

	public void startSendMessageAnimation(List<MessageData> messageDataList){
		
		if(breakThread){
			return;
		}
		
		if(activeAgent){
			return;
		}

		singeAnimationModus = false;
		AnimationSendMessageLoop animationLoop = new AnimationSendMessageLoop(messageDataList);
		animationLoop.start();

	}
	
	public void nextStepSendMessageAnimation(List<MessageData> messageDataList){
		
		if(breakThread){
			return;
		}

		if(activeAgent){
			return;
		}

		if(!MessageData.animationInProgress){
			index = 0;
		}
		singeAnimationModus  = true;
		AnimationSendMessageLoop animationLoop = new AnimationSendMessageLoop(messageDataList, singeAnimationModus);
		animationLoop.start();
	}
	
	public void stopSendMessageAnimation(){
		breakThread = true;
//		index = 0;	
		MessageData.animationInProgress = false;
		activeAgent = false;
		gui.repaint();
	}

	/*
	 *
	 * Animation Loop Threads.....
	 *
	 */

	public class AnimationAgentLoop extends Thread{

		private List<AgentWayData> agentsWayList;
		private boolean singleStepAnimation = false;

		public AnimationAgentLoop(List<AgentWayData> agentsWayList) {
			this(agentsWayList, false);
		}
		public AnimationAgentLoop(List<AgentWayData> agentsWayList, boolean singeStepAnimation) {
			this.agentsWayList = agentsWayList;
			this.singleStepAnimation = singeStepAnimation;
		}

		@Override
		public void run() {

			CIMAVertice.activeAnimation = true;
			activeAgent = true;

			gui.repaint();

			if(singleStepAnimation){

				doAnimation(index);
				index++;

				if(index < 0){
					index = 0;
				}

				//bis size - 1 weil der letzte schritt die animation null -> homebase ist und übersprungen werden muss
				if(index >= agentsWayList.size() -1){
					CIMAVertice.activeAnimation = false;
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
				CIMAVertice.activeAnimation = false;
				index = 0;
			}

			breakThread = false;
			gui.repaint();
			activeAgent = false;

		}

		private AgentAnimationTimer doAnimation(int i){

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
			if(i < agentsWayList.size()){
				agentsWayList.get(i).getReceiver().changeCurrentAgents(agentsWayList.get(i).getAgentNumber());
			}

			gui.repaint();
			return timer;
		}

		private void pauseAnimation(){
			try {
				Thread.sleep(1000 - 75*CIMAVertice.getAnimationSpeed());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public class AnimationSendMessageLoop extends Thread{

		List<MessageData> messageDataList = new ArrayList<MessageData>();
		boolean singleStepAnimation = false;

		public AnimationSendMessageLoop(List<MessageData> messageDataList) {
			this(messageDataList, false);
		}
		public AnimationSendMessageLoop(List<MessageData> messageDataList, boolean singleStepAnimation) {
			this.messageDataList = messageDataList;
			this.singleStepAnimation = singleStepAnimation;
		}

		@Override
		public void run() {
			
			MessageData.animationInProgress = true;
			CIMAVertice.drawMu = false;
			breakThread  = false;
			activeAgent = true;

			for(int j  = 0; j < messageDataList.size(); j++){
				messageDataList.get(j).prepareForAnimation();
			}

			if(singleStepAnimation){

				doAnimation(index);
				index++;

				if(index < 0){
					index = 0;
				}

				//bis size - 1 weil der letzte schritt die animation null -> homebase ist und übersprungen werden muss
				if(index > messageDataList.size() -1){
					MessageData.animationInProgress = false;
					if(breakThread){
						MessageData.clearGui = true;
					}else{
						CIMAVertice.drawMu = true;
					}
//					if(messageDataList.get(index-1) != null){
//						messageDataList.get(index-1).resetAllColors();
//					}
					for(MessageData msgData : messageDataList){
						msgData.resetAllColors();
					}
					index = 0;
				}
				

			}else{

				breakThread = false;
			
			
				for(int i = index; i < messageDataList.size(); i++){
	
	//				gui.repaint();
					
	
					doAnimation(i);
					
					if(breakThread){
						if(i < messageDataList.size()){
							messageDataList.get(i).resetAllColors();
						}
						break;
					}
				}
//				if(messageDataList.get(messageDataList.size() - 1) != null){
//					messageDataList.get(messageDataList.size() - 1).resetAllColors();
//				}
				for(MessageData msgData : messageDataList){
					msgData.resetAllColors();
				}
				index = 0;
				MessageData.animationInProgress = false;
				if(breakThread){
					MessageData.clearGui = true;
				}else{
					CIMAVertice.drawMu = true;
				}
			}
			gui.repaint();
			breakThread = false;
			activeAgent = false;

		}
		
		
		private void doAnimation(int i){
			for(int j  = 0; j < i; j++){
				messageDataList.get(j).animationFinished();
			}

//			if(i > 0){
//				if(messageDataList.get(i-1) != null){
//					messageDataList.get(i-1).resetAllColors();
//				}
//			}
			for(MessageData msgData : messageDataList){
				msgData.resetAllColors();
			}
			messageDataList.get(i).markAllColors();
			SendMessageAnimationTimer timer = messageDataList.get(i).animation();
			try {
				timer.join();
				if(i < messageDataList.size()){
					messageDataList.get(i).animationFinished();
				}
//				timer.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			gui.repaint();
		}

	}

}
