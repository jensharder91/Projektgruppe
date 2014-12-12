package cima;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;

import Gui.Gui;
import Tree.Vertice;

public class CIMAGui extends Gui{

	private static final long serialVersionUID = 1L;
	private static CIMAGui gui;

	private int verticeCoutner = 0;


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
			homeBase = ((CIMAVertice) rootVertice).findHomeBase();
			((CIMAVertice) homeBase).moveAgents(null, 0);
			
//			if(rootVertice.getChildren().size() > 0){
//				rootVertice.animation(rootVertice.getChildren().get(0));
//			}
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

				if(SwingUtilities.isRightMouseButton(e)){
					if(rootVertice != null){
						//check if its a vertice  -> LÖSCHEN
						Vertice selectedVertice = rootVertice.pointExists(e.getX(), e.getY());
						if(selectedVertice != null){
							if(selectedVertice == rootVertice){
								rootVertice = null;
							}else{
								selectedVertice.delete();
							}
						}
					}
					if(rootVertice != null){
						//check if its a edgeWeightOval ->edgeWeight -1
						Vertice edgeWeigthOwner = ((CIMAVertice) rootVertice).edgeWeightOvalExists(e.getX(), e.getY());
						if(edgeWeigthOwner != null){
							((CIMAVertice) edgeWeigthOwner).edgeWeightDepress();
						}
					}
				//linksklick
				}else{

					// kein root ?  -> neues Kind
					if(rootVertice == null){
						verticeCoutner  = 1;
						rootVertice = new CIMAVertice(""+verticeCoutner, null, gui);
						verticeCoutner++;
					//add child
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
					
					}else {
						//check if its a edgeWeightOval ->edgeWeight +1
						Vertice edgeWeigthOwner = ((CIMAVertice) rootVertice).edgeWeightOvalExists(e.getX(), e.getY());
						if(edgeWeigthOwner != null){
							((CIMAVertice) edgeWeigthOwner).edgeWeightIncrease();;
						}
					}
				}

				repaint();
			}
		});
	}

}
