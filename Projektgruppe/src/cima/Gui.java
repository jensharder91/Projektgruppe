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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class Gui extends JPanel{

	protected static final long serialVersionUID = 1L;

	public static Vertice rootVertice = null;
	protected Vertice homeBase = null;
	protected Vertice currentVertice = null;
//	protected boolean hideHomebase = false;

	protected JPanel buttonBarSouth = new JPanel();
	protected JPanel buttonBarNorth = new JPanel();
	
	private ICalcStrategy[] calcStrategies = {new ModellMinimalDanger(), new ModelMultPotential(), new ModelStandardPaper()};

	/** Buttons */
	protected JButton buttonCalculate = new JButton("Nachrichten sofort berechnen");
	protected JButton buttonCalculateAnimation = new JButton("Nachrichtenberechnung animieren");
	protected JButton buttonClear = new JButton("Clear");
	protected JButton buttonBack = new JButton("Zur�ck");
//	protected JButton buttonShowMu = new JButton("berechne minimale Agenten");
//	protected JToggleButton togglePause = new JToggleButton("\u25AE\u25AE");
	private JButton buttonNextAgentAnimationStep = new JButton("\u25BA");//RightArrow
	private JButton buttonNextCalculateAnimationStep = new JButton("\u25BA");
	protected JButton buttonCompleteAgentAnimation = new JButton("Baumdekontamination animieren");
	private JLabel lableAnimationSpeed = new JLabel("Animation speed:");
	private SpinnerNumberModel spinnerModel_speed = new SpinnerNumberModel(3, 0, 10, 1);
	private SpinnerNumberModel spinnerModel_potential = new SpinnerNumberModel(1, 1, 999, 1);
	private JSpinner spinnerAnimationSpeed = new JSpinner(spinnerModel_speed);
	private JSpinner spinnerPotential = new JSpinner(spinnerModel_potential);
	private JCheckBox checkboxShowMessageData = new JCheckBox("zeige die Berechnung an");
//	private JCheckBox checkboxEditor = new JCheckBox("editiere den Baum");
//	private JCheckBox checkboxDisableAllInfos = new JCheckBox("schalte alle Infos aus");
//	protected JButton buttonDrawAllPotentialEdges = new JButton("Färbe alle möglichen Kanten");
	protected JComboBox<ICalcStrategy> comboBoxCalcStrategy = new JComboBox<ICalcStrategy>(calcStrategies);
	
	
	/** bollean if gui element should be visible*/
	private boolean buttonCalculateBoolean = false;
	private boolean buttonCalculateAnimationBoolean = false;
	private boolean buttonClearBoolean = false;
	private boolean buttonBackBoolean = false;
	private boolean buttonShowMuBoolean = false;
	private boolean togglePauseBoolean = false;
	private boolean buttonNextAgentAnimationStepBoolean = false;
	private boolean buttonNextCalculateAnimationStepBoolean = false;
	private boolean buttonCompleteAgentAnimationBoolean = true;
	private boolean spinnerAnimationSpeedBoolean = false;
	private boolean spinnerPotentialBoolean = false;
	private boolean checkboxShowMessageDataBoolean = false;
	private boolean checkboxEditorBoolean = true;
	private boolean buttonDrawAllPotentialEdgesBoolean = true;
	
	private enum ButtonState{
		state1, //<2 knoten
		state2,	//"normal"
		state3,	//bei msgData animation
		state4, //nach msgData animation
		state5,	//bei baumdekonta. animation
		state6, //next msgData animation
		state7, //wait step msgData animation
		state8,	//next step agent animation
		state9; //wait step agent animation
	};
	private ButtonState buttonState = ButtonState.state2;
	
	
	protected boolean autoAlgo = false;
	protected boolean editorOn = true;

	protected Gui(){

		createGui();

		addGuiMouseListener();
	}

	protected void createGui(){

		setLayout(new BorderLayout());
		
		buttonBack.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				CIMAVertice.drawMu = false;
//				CIMAAnimation.afterMessageDataCalc = false;
//				for(MessageData_old msgData : CIMAVertice.messageDataList){
//					msgData.resetAllColors();
//				}
//				MessageData_old.resetDisplayCalcInfos();
				repaint();
			}
		});
//		buttonShowMu.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				CIMAVertice.drawMu = true;
////				CIMAAnimation.afterMessageDataCalc = false;
////				for(MessageData_old msgData : CIMAVertice.messageDataList){
////					msgData.resetAllColors();
////				}
////				MessageData_old.resetDisplayCalcInfos();
//				repaint();
//			}
//		});

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
				
				CIMAAnimation cimaAnimation = CIMAAnimation.getCIMAAnimation();
				
				if(cimaAnimation.animationIsInProgress()){
					cimaAnimation.stopAllAnimations();
					buttonCalculateAnimation.setText("Nachrichtenberechnung animieren");
				}else{
					homeBase = ((CIMAVertice) rootVertice).findHomeBase();
//					calcAlgorithmus(true);
					((CIMAVertice) rootVertice).animateMsgData();
					buttonCalculateAnimation.setText("Nachrichtenanimation anhalten");
				}
			}
		});
		
		buttonCompleteAgentAnimation.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				CIMAAnimation cimaAnimation = CIMAAnimation.getCIMAAnimation();
				
				if(cimaAnimation.animationIsInProgress()){
					cimaAnimation.stopAllAnimations();
					buttonCompleteAgentAnimation.setText("Baumdekontamination animieren");
				}else{
					((CIMAVertice) rootVertice).animateAgents();
					buttonCompleteAgentAnimation.setText("Dekontaminationsanimation anhalten");
				}
				
			}
		});
				
		buttonNextAgentAnimationStep.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				((CIMAVertice) homeBase).animateAgents(true);
				
			}
		});
		
		buttonNextCalculateAnimationStep.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
//				calcAlgorithmus(true);
				homeBase = ((CIMAVertice) rootVertice).findHomeBase();
				((CIMAVertice) rootVertice).animateMsgData(true);
				
			}
		});
		
		spinnerAnimationSpeed.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				AgentWayData.setAnimationSpeed((int) spinnerAnimationSpeed.getValue());
				MessageData.setAnimationSpeed((int) spinnerAnimationSpeed.getValue());
			}
		});
		
	spinnerPotential.addChangeListener(new ChangeListener() {
		
		@Override
		public void stateChanged(ChangeEvent e) {
			CIMAVertice.setPotential((int) spinnerPotential.getValue());
			if((int) spinnerPotential.getValue() <= 1){
				comboBoxCalcStrategy.setSelectedIndex(0);
				CIMAVertice.setStrategy((ICalcStrategy) comboBoxCalcStrategy.getSelectedItem());
			}else{
				comboBoxCalcStrategy.setSelectedIndex(1);
				CIMAVertice.setStrategy((ICalcStrategy) comboBoxCalcStrategy.getSelectedItem());
			}
		}
	});
//	spinnerPotential.setValue(1);

//		togglePause.addItemListener(new ItemListener() {
//
//			@Override
//			public void itemStateChanged(ItemEvent e) {
//				if(e.getStateChange() == ItemEvent.SELECTED){
//					CIMAVertice.setAnimationSpeed(0);
//					MessageData_old.setAnimationSpeed(0);
//				}else if(e.getStateChange() == ItemEvent.DESELECTED){
//					CIMAVertice.setAnimationSpeed((int) spinnerAnimationSpeed.getValue());
//					MessageData_old.setAnimationSpeed((int) spinnerAnimationSpeed.getValue());
//				}
//			}
//		});
		
		checkboxShowMessageData.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
//				MessageData_old.setShowMessageData(checkboxShowMessageData.isSelected());
				repaint();
			}
		});
		
//		checkboxDisableAllInfos.addChangeListener(new ChangeListener() {
//			
//			@Override
//			public void stateChanged(ChangeEvent e) {
////				MessageData.setShowMessageData(!checkboxDisableAllInfos.isSelected());
//				InfoDisplayClass.setDisableInfo(checkboxDisableAllInfos.isSelected());
//				hideHomebase = !checkboxDisableAllInfos.isSelected();
//				repaint();
//			}
//		});
		
//		checkboxEditor.addChangeListener(new ChangeListener() {
//			
//			@Override
//			public void stateChanged(ChangeEvent e) {
//				editorOn = checkboxEditor.isSelected();
//			}
//		});
//		checkboxEditor.setSelected(true);
		
//		buttonDrawAllPotentialEdges.addActionListener(new ActionListener(){
//			@Override
//			public void actionPerformed(ActionEvent e){
//				
//				if(rootVertice == null){
//					return;
//				}
//
//				((CIMAVertice) rootVertice).algorithmus();
////				((CIMAVertice) rootVertice).drawAllPotentialEdges();
//				
//				repaint();
//
//			}
//		});
		
		comboBoxCalcStrategy.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				CIMAVertice.setStrategy((ICalcStrategy) comboBoxCalcStrategy.getSelectedItem());
				
				
			}
		});
		//init
		comboBoxCalcStrategy.setSelectedIndex(0);
		CIMAVertice.setStrategy((ICalcStrategy) comboBoxCalcStrategy.getSelectedItem());
		
		

	
//		buttonBarNorth.add(checkboxEditor);
//		buttonBarNorth.add(buttonDrawAllPotentialEdges);
		buttonBarNorth.add(comboBoxCalcStrategy);
		buttonBarNorth.add(spinnerPotential);
//		buttonBarNorth.add(checkboxDisableAllInfos);
		this.add(buttonBarNorth, "North");
		
//		buttonBarSouth.add(checkboxEditor);
//		buttonBarSouth.add(buttonDrawAllPotentialEdges);
	
//		buttonBarSouth.add(togglePause);
		buttonBarSouth.add(lableAnimationSpeed);
		buttonBarSouth.add(spinnerAnimationSpeed);
//		buttonBarSouth.add(spinnerPotential);
		buttonBarSouth.add(buttonBack);
//		buttonBarSouth.add(buttonShowMu);
		buttonBarSouth.add(buttonCalculate);
		buttonBarSouth.add(buttonCalculateAnimation);
		buttonBarSouth.add(buttonCompleteAgentAnimation);
		buttonBarSouth.add(buttonNextAgentAnimationStep);
		buttonBarSouth.add(buttonNextCalculateAnimationStep);
		buttonBarSouth.add(buttonClear);
		buttonBarSouth.add(checkboxShowMessageData);
		this.add(buttonBarSouth, "South");
		
		
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
//		for(MessageData_old msgData : CIMAVertice.messageDataList){
//			msgData.drawWriteCalcInfos(g2);
//		}
		
		//messageDataLine
		for(MessageData msgData : CIMAVertice.messageDataList){
			msgData.drawAnimationLine(g2);
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
//			if(homeBase != null && hideHomebase){
			if(homeBase != null){
				g2.drawRect(homeBase.getX(), homeBase.getY(), homeBase.getDiameter(), homeBase.getDiameter());
			}
		}

		//draw all vertices recursively
		if(rootVertice != null){
			((CIMAVertice) rootVertice).drawTree(g2,10,10 + buttonBarNorth.getHeight(),getWidth()-20,getHeight()-50);
		}
		
		//messageData
//		for(MessageData_old msgData : CIMAVertice.messageDataList){
//			msgData.drawMessageData(g2);
//		}
		for(MessageData msgData : CIMAVertice.messageDataList){
			msgData.drawAnimation(g2);
		}
		
//		if(rootVertice != null){
//			((CIMAVertice) rootVertice).drawAnimation(g2);
//		}
		for(AgentWayData agentData : CIMAVertice.agentWayList){
			agentData.drawAnimation(g2);
		}
		
//		if(rootVertice != null){
//			((CIMAVertice) rootVertice).drawPotentialData(g2);
//		}
		
		if(rootVertice != null){
			CIMAVertice.drawDisplayInformation(((CIMAVertice) rootVertice), g2);
		}
		

		
		//***  BUTTON logic   **/
		
		buttonState = ButtonState.state2; //default
		
		if(CIMAAnimation.getCIMAAnimation().singleStepAnimationIsInProgress() && !CIMAVertice.drawMu){
			buttonState = ButtonState.state7;
		}
		
		if(rootVertice == null || rootVertice.getChildren().size() < 1){
			buttonState = ButtonState.state1;
		}
		
		if(CIMAAnimation.getCIMAAnimation().animationIsInProgress() && !CIMAVertice.activeAgentAnimation){
			buttonState = ButtonState.state3;
			
			if(CIMAAnimation.getCIMAAnimation().singeAnimationModus){
				buttonState = ButtonState.state6;
			}
		}
		
		if(!CIMAAnimation.getCIMAAnimation().animationIsInProgress() && CIMAVertice.drawMu){
			buttonState = ButtonState.state4;
			
			if(CIMAAnimation.getCIMAAnimation().singleStepAnimationIsInProgress()){
				buttonState = ButtonState.state9;
			}
		}
		
		if(CIMAAnimation.getCIMAAnimation().animationIsInProgress() && CIMAVertice.activeAgentAnimation){
			buttonState = ButtonState.state5;
			
			if(CIMAAnimation.getCIMAAnimation().singeAnimationModus){
				buttonState = ButtonState.state8;
			}
		}
		
		
		
		//enabe / disable the buttons
		
		//default disable all buttons
		buttonCalculateBoolean = false;
		buttonCalculateAnimationBoolean = false;
		buttonClearBoolean = false;
		buttonBackBoolean = false;
		buttonShowMuBoolean = false;
		togglePauseBoolean = false;
		buttonNextAgentAnimationStepBoolean = false;
		buttonNextCalculateAnimationStepBoolean = false;
		buttonCompleteAgentAnimationBoolean = false;
		spinnerAnimationSpeedBoolean = false;
		spinnerPotentialBoolean = false;
		checkboxShowMessageDataBoolean = false;
		checkboxEditorBoolean = false;
		buttonDrawAllPotentialEdgesBoolean = false;
		buttonNextAgentAnimationStepBoolean = false;
		buttonNextCalculateAnimationStepBoolean = false;
		
		
		if(buttonState == ButtonState.state1){//<2 knoten
			buttonClearBoolean = true;
		}
		if(buttonState == ButtonState.state2){//"normal"
			buttonClearBoolean = true;
			buttonCalculateBoolean = true;
			buttonCalculateAnimationBoolean = true;
			buttonNextCalculateAnimationStepBoolean  = true;
		}
		if(buttonState == ButtonState.state3){//bei msgData animation
			buttonCalculateAnimationBoolean = true;
			spinnerAnimationSpeedBoolean = true;
		}
		if(buttonState == ButtonState.state4){//nach msgData animation
			buttonBackBoolean = true;
			buttonCompleteAgentAnimationBoolean = true;
			buttonClearBoolean = true;
			buttonNextAgentAnimationStepBoolean = true;
		}
		if(buttonState == ButtonState.state5){//bei baumdekonta. animation
			buttonCompleteAgentAnimationBoolean = true;
			spinnerAnimationSpeedBoolean = true;
		}
		
		//next steps...
		if(buttonState == ButtonState.state6){//next step msgData animation
			buttonNextCalculateAnimationStepBoolean = true;
//			buttonCalculateAnimationBoolean = true;
			spinnerAnimationSpeedBoolean = true;
		}
		
		if(buttonState == ButtonState.state7){//wait step msgData animation
			buttonNextCalculateAnimationStepBoolean = true;
			buttonCalculateAnimationBoolean = true;
		}
		
		if(buttonState == ButtonState.state8){//next step agent animation
			buttonNextAgentAnimationStepBoolean = true;
//			buttonCompleteAgentAnimationBoolean = true;
			spinnerAnimationSpeedBoolean = true;
		}
		
		if(buttonState == ButtonState.state9){//wait step agent animation
			buttonNextAgentAnimationStepBoolean = true;
			buttonCompleteAgentAnimationBoolean = true;
		}
		
		

		
		

//		buttonBackBoolean = false;
//		buttonShowMuBoolean = false;
//		buttonCalculateBoolean = true;
//		buttonCalculateAnimationBoolean = true;
////		if(!buttonCalculateAnimation.getText().equals("Berechnung animieren")){
////			buttonCalculateAnimation.setText("Berechnung animieren");
////		}
//		buttonClearBoolean = true;
//		buttonNextAgentAnimationStepBoolean = false;
//		buttonNextCalculateAnimationStepBoolean = true;
//		buttonCompleteAgentAnimationBoolean = true;
//		spinnerAnimationSpeedBoolean = false;
//		spinnerPotentialBoolean = false;
//		togglePauseBoolean = false;
//		checkboxShowMessageDataBoolean = false;
//		checkboxEditorBoolean = true;
//		buttonDrawAllPotentialEdgesBoolean = true;
//		
//		if(rootVertice == null || rootVertice.getChildren().size() < 1){
//			buttonBackBoolean = false;
//			buttonCalculateBoolean = false;
//			buttonCalculateAnimationBoolean  = false;
//			buttonClearBoolean = false;
//			buttonNextAgentAnimationStepBoolean = false;
//			buttonNextCalculateAnimationStepBoolean = false;
//			buttonCompleteAgentAnimationBoolean = true;
//			spinnerAnimationSpeedBoolean = false;
//			spinnerPotentialBoolean = false;
//			togglePauseBoolean = false;
//			checkboxEditorBoolean = false;
//			buttonDrawAllPotentialEdgesBoolean = false;
//		}
//
//		
//		if(MessageData_old.animationInProgress){
//			if(!buttonCalculateAnimation.getText().equals("Animation abbrechen")){
//				buttonCalculateAnimation.setText("Animation abbrechen");
//			}
//			buttonCalculateBoolean  = false;
//			buttonClearBoolean = false;
//			buttonNextCalculateAnimationStepBoolean = false;
//			spinnerAnimationSpeedBoolean = true;
//			togglePauseBoolean = true;
//			
//			if(CIMAAnimation.singeAnimationModus){
//				if(!buttonCalculateAnimation.getText().equals("komplette Animation")){
//					buttonCalculateAnimation.setText("komplette Animation");
//				}
//				buttonNextCalculateAnimationStepBoolean = true;
//				buttonCalculateAnimationBoolean = true;
//			}
//		}
//		
//		if(CIMAAnimation.afterMessageDataCalc){
//			buttonBackBoolean = true;
//			buttonShowMuBoolean = true;
//			
//			spinnerAnimationSpeedBoolean = false;
//			togglePauseBoolean = false;
//			buttonCalculateAnimationBoolean = false;
//			buttonNextCalculateAnimationStepBoolean  = false;
//			buttonClearBoolean = false;
//			buttonCalculateBoolean = false;
//		}
//		
//		if(CIMAVertice.drawMu == true){
//			if(!buttonCompleteAgentAnimation.getText().equals("Baum dekontaminieren")){
//				buttonCompleteAgentAnimation.setText("Baum dekontaminieren");
//			}
//			buttonBackBoolean = true;
//			buttonNextAgentAnimationStepBoolean = true;
//			buttonCompleteAgentAnimationBoolean = true;
//			buttonCalculateBoolean = false;
//			buttonCalculateAnimationBoolean = false;
//			buttonClearBoolean = false;
//			buttonNextCalculateAnimationStepBoolean = false;
//			spinnerAnimationSpeedBoolean = false;
//			togglePauseBoolean = false;
//			checkboxShowMessageDataBoolean = true;
//		}
//		
//		if(CIMAVertice.activeAnimation){
//			if(!buttonCompleteAgentAnimation.getText().equals("Animation abbrechen")){
//				buttonCompleteAgentAnimation.setText("Animation abbrechen");
//			}
//			buttonBackBoolean = false;
//			buttonCalculateBoolean = false;
//			buttonCalculateAnimationBoolean = false;
//			buttonClearBoolean = false;
//			buttonNextAgentAnimationStepBoolean = false;
//			buttonNextCalculateAnimationStepBoolean = false;
//			buttonCompleteAgentAnimationBoolean = true;
//			spinnerAnimationSpeedBoolean = true;
//			togglePauseBoolean = true;
//			checkboxShowMessageDataBoolean = true;
//			
//			if(CIMAAnimation.singeAnimationModus){
//				if(!buttonCompleteAgentAnimation.getText().equals("komplette Animation")){
//					buttonCompleteAgentAnimation.setText("komplette Animation");
//				}
//				
//				buttonNextAgentAnimationStepBoolean  = true;
//				buttonCompleteAgentAnimationBoolean = true;
//			}
//		}
//		
//		
		buttonSetEnabled(buttonCalculate, buttonCalculateBoolean);
		buttonSetEnabled(buttonCalculateAnimation, buttonCalculateAnimationBoolean);
		buttonSetEnabled(buttonClear, buttonClearBoolean);
		buttonSetEnabled(buttonBack, buttonBackBoolean);
//		buttonSetEnabled(buttonShowMu, buttonShowMuBoolean);
//		buttonSetEnabled(buttonNextAgentAnimationStep, buttonNextAgentAnimationStepBoolean);
//		buttonSetEnabled(buttonNextCalculateAnimationStep, buttonNextCalculateAnimationStepBoolean);
		buttonSetEnabled(buttonCompleteAgentAnimation, buttonCompleteAgentAnimationBoolean);
		buttonSetEnabled(buttonNextAgentAnimationStep, buttonNextAgentAnimationStepBoolean);
		buttonSetEnabled(buttonNextCalculateAnimationStep, buttonNextCalculateAnimationStepBoolean);
//		togglePause.setVisible(togglePauseBoolean);
		lableAnimationSpeed.setVisible(spinnerAnimationSpeedBoolean);
		spinnerAnimationSpeed.setVisible(spinnerAnimationSpeedBoolean);
		checkboxShowMessageData.setVisible(checkboxShowMessageDataBoolean);
//		checkboxEditor.setVisible(checkboxEditorBoolean);
//		buttonDrawAllPotentialEdges.setVisible(buttonDrawAllPotentialEdgesBoolean);
		
//		checkboxDisableAllInfos.setVisible(true);
		
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
//		MessageData_old.clearGui = true;
		repaint();
	}
	
	public JPanel getButtonBarNorth(){
		return buttonBarNorth;
	}
	public JPanel getButtonBarSouth(){
		return buttonBarSouth;
	}

	protected abstract void calcAlgorithmus(boolean repaintBool);
	protected abstract void addGuiMouseListener();

}

