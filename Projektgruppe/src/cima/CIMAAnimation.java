package cima;

import java.util.ArrayList;
import java.util.List;

import cima.AgentWayData.AgentAnimationTimer;
import cima.MessageData.SendMessageAnimationTimer;

public class CIMAAnimation {

	private static CIMAAnimation cimaAnimation = null;
	private static boolean breakThread = false;
	private static Gui gui;
	private static int index = 0;

	private boolean animationInProgress = false;
//	private boolean activeAgent = false;
	public static boolean singeAnimationModus = false;
//	public static boolean afterMessageDataCalc = false;

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
	
	public boolean animationIsInProgress(){
		return animationInProgress;
	}
	public boolean singleStepAnimationIsInProgress(){
		return animationInProgress || singeAnimationModus;
	}
	
	public void stopAllAnimations(){
		
//		if(CIMAVertice.activeAnimation || MessageData.animationInProgress){
		if(animationInProgress){
			index = 0;

			breakThread = true;

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
		
//		if(activeAgent || breakThread){
//			return;
//		}
		if(animationInProgress){
			return;
		}

		singeAnimationModus = false;
		AnimationAgentLoop animationLoop = new AnimationAgentLoop(agentsWayList);
		animationLoop.start();

	}

	private void stopAgentAnimation(){
//		if(activeAgent){
//		if(animationInProgress){
//			breakThread = true;
//		}
//		CIMAVertice.activeAnimation = false;
//		activeAgent = false;
//		gui.repaint();
	}

	public void nextStepAgentAnimation(List<AgentWayData> agentsWayList){

//		if(activeAgent || breakThread){
//			return;
//		}
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
			
//		if(activeAgent || breakThread){
//			return;
//		}
		if(animationInProgress){
			return;
		}

		singeAnimationModus = false;
		AnimationSendMessageLoop animationLoop = new AnimationSendMessageLoop(messageDataList);
		animationLoop.start();

	}
	
	public void nextStepSendMessageAnimation(List<MessageData> messageDataList){

//		if(activeAgent || breakThread){
//			return;
//		}
		if(animationInProgress){
			return;
		}

//		if(!MessageData.animationInProgress){
//			index = 0;
//		}
		singeAnimationModus  = true;
		AnimationSendMessageLoop animationLoop = new AnimationSendMessageLoop(messageDataList);
		animationLoop.start();
	}
	
	private void stopSendMessageAnimation(){
//		if(activeAgent){
//		if(animationInProgress){
//			breakThread = true;
//		}
//		MessageData.resetDisplayCalcInfos();
//		index = 0;	
//		MessageData.animationInProgress = false;
//		activeA{gent = false;
//		gui.repaint();
	}

	/*
	 *
	 * Animation AGENTS Loop Threads.....
	 *
	 */

	public class AnimationAgentLoop extends Thread{

		private List<AgentWayData> agentsWayList;
		private boolean singleStepAnimation = false;

		public AnimationAgentLoop(List<AgentWayData> agentsWayList) {
			this.agentsWayList = agentsWayList;
		}

		@Override
		public void run() {

			CIMAVertice.activeAgentAnimation = true;
//			activeAgent = true;
			animationInProgress = true;

			gui.repaint();

			if(singleStepAnimation){

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

			}else{

				breakThread = false;

				//bis size - 1 weil der letzte schritt die animation null -> homebase ist und übersprungen werden muss
				for(int i = index; i < agentsWayList.size() -1; i++){

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
				CIMAVertice.activeAgentAnimation = false;
				index = 0;
			}

			animationInProgress = false;
//			CIMAVertice.activeAgentAnimation = false;
			breakThread = false;
			gui.repaint();
//			activeAgent = false;

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
			
//			MessageData.animationInProgress = true;
			CIMAVertice.drawMu = false;
//			breakThread  = false;
//			activeAgent = true;
			animationInProgress = true;
//
//			for(int j  = 0; j < messageDataList.size(); j++){
//				messageDataList.get(j).prepareForAnimation();
//			}

			if(singeAnimationModus){

				doAnimation(index);
				index++;

				if(index < 0){
					index = 0;
//					MessageData.resetDisplayCalcInfos();
				}

				//bis size - 1 weil der letzte schritt die animation null -> homebase ist und übersprungen werden muss
				if(index > messageDataList.size() -1){
//					MessageData.animationInProgress = false;
//					if(breakThread){
//						MessageData.clearGui = true;
//					}else{
//						CIMAAnimation.afterMessageDataCalc = true;
//					}
					index = 0;
					singeAnimationModus = false;
					CIMAVertice.drawMu = true;
				}
				

			}else{

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
//				MessageData.animationInProgress = false;
				if(breakThread){
//					MessageData.clearGui = true;
					for(MessageData msgData : messageDataList){
						msgData.clearExplainMessageData();
					}
					CIMAVertice.drawMu = false;
				}else{
//					CIMAAnimation.afterMessageDataCalc = true;
					CIMAVertice.drawMu = true;
				}
			}
			breakThread = false;
//			activeAgent = false;
			animationInProgress = false;
			gui.repaint();

		}
		
		
		private void doAnimation(int i){
//			for(int j  = 0; j < i; j++){
//				messageDataList.get(j).animationFinished();
//			}
			
			for(MessageData msgData : messageDataList){
				msgData.clearExplainMessageData();
			}
//			messageDataList.get(i).markAllColors();
			SendMessageAnimationTimer timer = messageDataList.get(i).animation();
			try {
				timer.join();
				System.out.println("join.... i = "+i+"  / msgDataList.size() = "+messageDataList.size());
				if(i == messageDataList.size()-1){
					System.out.println("ende");
					messageDataList.get(i).resetCurrentmsgDataAnimation();
//					messageDataList.get(i).animationFinished();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			gui.repaint();
		}

	}

}