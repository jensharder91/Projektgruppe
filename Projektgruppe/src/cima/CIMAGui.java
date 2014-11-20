package cima;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import Gui.Gui;
import Tree.Vertice;

public class CIMAGui extends Gui{

	private static final long serialVersionUID = 1L;
	private static CIMAGui gui;

	private int verticeCoutner = 0;
	private boolean editWeight = false;

	protected JToggleButton buttonAddWeight = new JToggleButton("Gewicht hinzufügen");
	protected JTextField tf_weight = new JTextField();

	private CIMAGui(){
		super();

		tf_weight.setText(1+"");
		tf_weight.setSize(25, 10);
		buttonBar.add(tf_weight);

		buttonAddWeight.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
					editWeight = true;
				}else if(e.getStateChange() == ItemEvent.DESELECTED){
					editWeight = false;
				}
			}
		});
		buttonBar.add(buttonAddWeight);


	}

	public static CIMAGui getGui(){
		if(gui == null){
			gui = new CIMAGui();
		}
		return gui;
	}



	@Override
	protected void calcAlgorithmus(boolean repaintBool) {

		if(rootVertice instanceof CIMAVertice){
			((CIMAVertice) rootVertice).algorithmus();
			if(rootVertice.getChildren().size() > 0){
				rootVertice.animation(rootVertice.getChildren().get(0));
			}
		}

		if(repaintBool){
			repaint();
		}
	}

	@Override
	protected void addGuiMouseListener() {
		addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println(e.getX() +" / "+e.getY());

				// füge ein Gewicht ein
				if(editWeight){
					CIMAVertice editVertice = (CIMAVertice) rootVertice.pointExists(e.getX(), e.getY());
					if(editVertice != null){
						if(editVertice.getParent() != null){
							editVertice.setEdgeWeightToParent(Integer.valueOf(tf_weight.getText()));
						}
					}
				//rechtsklick -> LÖSCHEN
				}else if(SwingUtilities.isRightMouseButton(e)){
					if(rootVertice != null){
						Vertice selectedVertice = rootVertice.pointExists(e.getX(), e.getY());
						if(selectedVertice != null){
							if(selectedVertice == rootVertice){
								rootVertice = null;
							}else{
								selectedVertice.delete();
							}
						}
					}
				//linksklick -> neues Kind
				}else{

					if(rootVertice == null){
						verticeCoutner  = 1;
						rootVertice = new CIMAVertice(""+verticeCoutner, null, gui);
						verticeCoutner++;
					}else if(rootVertice.pointExists(e.getX(), e.getY()) != null){
						// add a child to this point
						CIMAVertice parent = null;
						parent = (CIMAVertice) rootVertice.pointExists(e.getX(), e.getY());
						if(parent != null){
							System.out.println("Add new Child to "+parent);
						}
						if(parent instanceof CIMAVertice){
							new CIMAVertice(""+verticeCoutner, parent, gui);
							verticeCoutner++;
						}
					}
				}

				repaint();
			}
		});
	}

}
