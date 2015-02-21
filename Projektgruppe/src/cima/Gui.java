package cima;


import java.awt.BorderLayout;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class Gui extends JPanel{

	protected static final long serialVersionUID = 1L;

//	protected Vertice rootVertice = null;
	public static Vertice rootVertice = null;
	protected Vertice homeBase = null;
	protected Vertice currentVertice = null;

	protected JPanel buttonBar = new JPanel();

	/** Buttons */
	protected JButton buttonCalculate = new JButton("Sofort berechnen");
	protected JButton buttonCalculateAnimation = new JButton("Berechnung animieren");
//	protected JButton buttonAnimation = new JButton("Animation berechnen");
	protected JButton buttonClear = new JButton("Clear");
	protected JButton buttonBack = new JButton("Zurück");
//	protected JToggleButton toggleAutoAlgo = new JToggleButton("AutoCalc");
	private JButton buttonNextAgentAnimationStep = new JButton("\u25BA");//RightArrow
	private JButton buttonNextCalculateAnimationStep = new JButton("\u25BA");
	protected JButton buttonCompleteAgentAnimation = new JButton("Baum dekontaminieren");
	private SpinnerNumberModel spinnerModel = new SpinnerNumberModel(3, 0, 10, 1);
	private JSpinner spinnerAnimationSpeed = new JSpinner(spinnerModel);
	
	protected boolean autoAlgo = false;
//	public static boolean calcAgentMovesReady = false;

	protected Gui(){
//		super(true);

		createGui();

		addGuiMouseListener();
	}
	
//	public void makeStrate(){
//		this.createBufferStrategy(2);
//		strat = getBufferStrategy();
//	}

	protected void createGui(){

		setLayout(new BorderLayout());
		
		buttonBack.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				CIMAVertice.drawMu = false;
				repaint();
			}
		});

		buttonClear.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){

				clearGui();

			}
		});
		
		buttonCalculate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				calcAlgorithmus(true);

			}
		});
		

		buttonCalculateAnimation.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				homeBase = ((CIMAVertice) rootVertice).findHomeBase();
				((CIMAVertice) rootVertice).doCompleteSendMessageAnimation();

			}
		});
		
//		buttonAnimation.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//
////				homeBase = ((CIMAVertice) rootVertice).findHomeBase();
//				((CIMAVertice) homeBase).calcAgentsMove();
//				
//				if(calcAgentMovesReady){
//					calcAgentMovesReady = false;
//				}else{
//					calcAgentMovesReady = true;
//				}
//				repaint();
//
//			}
//		});
		
		buttonCompleteAgentAnimation.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				((CIMAVertice) homeBase).doCompleteAgentAnimation();
				
			}
		});
		
		buttonNextAgentAnimationStep.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				((CIMAVertice) homeBase).doStepAgentAnimation();
				
			}
		});
		
		buttonNextCalculateAnimationStep.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				homeBase = ((CIMAVertice) rootVertice).findHomeBase();
				((CIMAVertice) rootVertice).doStepSendMessageAnimation();
				
			}
		});
		
		spinnerAnimationSpeed.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				CIMAVertice.setAnimationSpeed((int) spinnerAnimationSpeed.getValue());
				MessageData.setAnimationSpeed((int) spinnerAnimationSpeed.getValue());
			}
		});

//		toggleAutoAlgo.addItemListener(new ItemListener() {
//
//			@Override
//			public void itemStateChanged(ItemEvent e) {
//				if(e.getStateChange() == ItemEvent.SELECTED){
//					autoAlgo = true;
//					calcAlgorithmus(true);
//				}else if(e.getStateChange() == ItemEvent.DESELECTED){
//					autoAlgo = false;
//				}
//			}
//		});

		buttonBar.add(spinnerAnimationSpeed);
		buttonBar.add(buttonBack);
		buttonBar.add(buttonCalculate);
		buttonBar.add(buttonCalculateAnimation);
//		buttonBar.add(buttonAnimation);
		buttonBar.add(buttonCompleteAgentAnimation);
		buttonBar.add(buttonNextAgentAnimationStep);
		buttonBar.add(buttonNextCalculateAnimationStep);
		buttonBar.add(buttonClear);
//		buttonBar.add(toggleAutoAlgo);
		this.add(buttonBar, "South");
		
		
		repaint();
	}

	public Vertice getRoot(){
		return rootVertice;
	}

	@Override
	public void paintComponent(Graphics g) {
		

		if(rootVertice != null && autoAlgo){
			calcAlgorithmus(false);
		}

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.setStroke(new BasicStroke(2));
		super.paintComponent(g);
		
		//background
		g2.setColor(Color.white);
		g2.fillRect(0, 0, (int)getSize().getWidth(), (int)getSize().getHeight());
		
		//draw calculation infos
		for(MessageData msgData : CIMAVertice.messageDataList){
			msgData.drawWriteCalcInfos(g2);
		}
		
		//messageDataLine
		for(MessageData msgData : CIMAVertice.messageDataList){
			msgData.drawLine(g2);
		}

		//mark current point
		g2.setColor(Color.blue);
		if(currentVertice != null){
			g2.drawRect(currentVertice.getX(), currentVertice.getY(), currentVertice.getDiameter(), currentVertice.getDiameter());
		}
		
		//mark homebase
		if(CIMAVertice.drawMu){
			g2.setColor(Color.RED);
			if(rootVertice != null){
				homeBase = ((CIMAVertice) rootVertice).findHomeBase();
			}
			if(homeBase != null){
				g2.drawRect(homeBase.getX(), homeBase.getY(), homeBase.getDiameter(), homeBase.getDiameter());
			}
		}

		//draw all vertices recursively
		if(rootVertice != null){
			((CIMAVertice) rootVertice).drawTree(g2,10,10,getWidth()-20,getHeight()-50);
		}
		
		//messageData
		for(MessageData msgData : CIMAVertice.messageDataList){
			msgData.drawMessageData(g2); //TODO
		}
		
		CIMAVertice.drawDisplayInformation(g2);
		

		
		//disable / enable buttons
//		buttonBack.setVisible(false);
		buttonSetEnabled(buttonBack, false);
//		buttonCalculate.setVisible(true);
		buttonSetEnabled(buttonCalculate, true);
//		buttonCalculateAnimation.setVisible(true);
		buttonSetEnabled(buttonCalculateAnimation, true);
		buttonCalculateAnimation.setText("Berechnung animieren");
//		buttonClear.setVisible(true);
		buttonSetEnabled(buttonClear, true);
//		toggleAutoAlgo.setVisible(true);
//		buttonNextAgentAnimationStep.setVisible(false);
		buttonSetEnabled(buttonNextAgentAnimationStep, false);
//		buttonNextCalculateAnimationStep.setVisible(true);
		buttonSetEnabled(buttonNextCalculateAnimationStep, true);
//		buttonCompleteAgentAnimation.setVisible(false);
		buttonSetEnabled(buttonCompleteAgentAnimation, false);
//		buttonAnimation.setText("Animation berechnen");
		spinnerAnimationSpeed.setVisible(false);
		
		if(rootVertice == null || rootVertice.getChildren().size() < 1){
//			buttonBack.setVisible(false);
//			buttonCalculate.setVisible(false);
//			buttonCalculateAnimation.setVisible(false);
//			buttonClear.setVisible(false);
//			buttonNextAgentAnimationStep.setVisible(false);
//			buttonNextCalculateAnimationStep.setVisible(false);
//			buttonCompleteAgentAnimation.setVisible(false);
			buttonSetEnabled(buttonBack, false);
			buttonSetEnabled(buttonCalculate, false);
			buttonSetEnabled(buttonCalculateAnimation, false);
			buttonSetEnabled(buttonClear, false);
			buttonSetEnabled(buttonNextAgentAnimationStep, false);
			buttonSetEnabled(buttonNextCalculateAnimationStep, false);
			buttonSetEnabled(buttonCompleteAgentAnimation, false);
			spinnerAnimationSpeed.setVisible(false);
		}

		
		if(MessageData.animationInProgress){
//			buttonCalculate.setVisible(false);
			buttonCalculateAnimation.setText("Animation abbrechen");
//			buttonClear.setVisible(false);
//			buttonNextCalculateAnimationStep.setVisible(false);
			buttonSetEnabled(buttonCalculate, false);
			buttonSetEnabled(buttonClear, false);
			buttonSetEnabled(buttonNextCalculateAnimationStep, false);
			spinnerAnimationSpeed.setVisible(true);
//			spinnerAnimationSpeed.setVisible(false);
			
			if(CIMAAnimation.singeAnimationModus){
//				buttonNextCalculateAnimationStep.setVisible(true);
//				buttonCalculateAnimation.setVisible(true);//TODO let the complete animation finish the step by step modus
				buttonCalculateAnimation.setText("komplette Animation");
				buttonSetEnabled(buttonNextCalculateAnimationStep, true);
				buttonSetEnabled(buttonCalculateAnimation, true);
			}
		}
		
//		if(calcAgentMovesReady){
		if(CIMAVertice.drawMu == true){
//			buttonBack.setVisible(true);
//			buttonNextAgentAnimationStep.setVisible(true);
//			buttonCompleteAgentAnimation.setVisible(true);
			buttonCompleteAgentAnimation.setText("Baum dekontaminieren");
//			buttonCalculate.setVisible(false);
//			buttonCalculateAnimation.setVisible(false);
//			buttonClear.setVisible(false);
//			buttonNextCalculateAnimationStep.setVisible(false);
//			toggleAutoAlgo.setVisible(false);
//			buttonAnimation.setText("Animation abbrechen");
			
			buttonSetEnabled(buttonBack, true);
			buttonSetEnabled(buttonNextAgentAnimationStep, true);
			buttonSetEnabled(buttonCompleteAgentAnimation, true);
			buttonSetEnabled(buttonCalculate, false);
			buttonSetEnabled(buttonCalculateAnimation, false);
			buttonSetEnabled(buttonClear, false);
			buttonSetEnabled(buttonNextCalculateAnimationStep, false);
			spinnerAnimationSpeed.setVisible(false);
		}
		
		if(CIMAVertice.activeAnimation){
//			buttonBack.setVisible(false);
//			buttonCalculate.setVisible(false);
//			buttonCalculateAnimation.setVisible(false);
//			buttonClear.setVisible(false);
//			toggleAutoAlgo.setVisible(false);
//			buttonNextAgentAnimationStep.setVisible(false);
//			buttonNextCalculateAnimationStep.setVisible(false);
//			buttonCompleteAgentAnimation.setVisible(true);
			buttonCompleteAgentAnimation.setText("Animation abbrechen");
//			buttonAnimation.setText("Animation abbrechen");
			
			buttonSetEnabled(buttonBack, false);
			buttonSetEnabled(buttonCalculate, false);
			buttonSetEnabled(buttonCalculateAnimation, false);
			buttonSetEnabled(buttonClear, false);
			buttonSetEnabled(buttonNextAgentAnimationStep, false);
			buttonSetEnabled(buttonNextCalculateAnimationStep, false);
			buttonSetEnabled(buttonCompleteAgentAnimation, true);
			spinnerAnimationSpeed.setVisible(true);
			
			if(CIMAAnimation.singeAnimationModus){
//				buttonNextAgentAnimationStep.setVisible(true);
//				buttonCompleteAgentAnimation.setVisible(true);//TODO let the complete animation finish the step by step modus
				buttonCompleteAgentAnimation.setText("komplette Animation");
				
				buttonSetEnabled(buttonNextAgentAnimationStep, true);
				buttonSetEnabled(buttonCompleteAgentAnimation, true);
			}
		}
	}
	
	private void buttonSetEnabled(JButton button, boolean shouldBeVisible){
		if(shouldBeVisible && !button.isEnabled()){
			button.setEnabled(true);
			button.setVisible(true);
		}
		
		if(!shouldBeVisible && button.isEnabled()){
			button.setEnabled(false);
			button.setVisible(false);
		}
	}

	protected void clearGui(){
		rootVertice = null;
		currentVertice = null;
		homeBase = null;
		MessageData.clearGui = true;
		repaint();
	}

	protected abstract void calcAlgorithmus(boolean repaintBool);
	protected abstract void addGuiMouseListener();

}

