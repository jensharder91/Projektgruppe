package tdti;

import java.util.*;
import java.awt.Graphics;

public class TDTIAgentCoordinator {

  static TDTIAgentCoordinator coordinator;
  private TDTIVertice base;
  private int numberOfAgents;
  private List<TDTIAgent> agents = new ArrayList<TDTIAgent>();

  private List<TDTIAgent> agentsOscillating = new ArrayList<TDTIAgent>();

  private TDTIAgentCoordinator(){
    super();
  }

  public static TDTIAgentCoordinator getCoordinator(){
    if(coordinator == null){
      coordinator = new TDTIAgentCoordinator();
    }
    return coordinator;
  }

  public void setBase(TDTIVertice newBase){
    if(base == newBase){
      return;
    }
    this.base = newBase;
    if(base != null){
      this.numberOfAgents = base.getPsi();
      base.decontaminate();
    } else {
      this.numberOfAgents = 0;
    }
    this.agents.clear();
    for(int a=0; a<numberOfAgents; a++){
      this.agents.add(new TDTIAgent(base,base,a,numberOfAgents));
    }
  }

  public void moveAgents(){
    TDTIAgent agent = this.agents.get(0);
    TDTIVertice currentVertice = agent.getVertice();
    TDTIVertice nextVertice = currentVertice.getContaminatedNeighborWithSmallestMessage();
    MessageData msg = agent.getVertice().getSmallestContaminatedNeighborMessage();
    /*
    for(TDTIAgent oscAgent : agentsOscillating){
      oscAgent.move();
    }
    */
    if(nextVertice != null){
      if(msg != null){
        // check if we need to come back
        if(currentVertice.numberOfContaminatedNeighbors() > 1){
          // somebody needs to come back after IMMUNITY_TIME to keep this vertice decontaminated
          /*
          TDTIGui gui = TDTIGui.getGui();
          if(agents.size() > 1 && gui.IMMUNITY_TIME == 2){
            agentsOscillating.add(agents.get(1));
            agents.get(1).setNextVertice(currentVertice);
          }
          */
        }
        for(int a=0; a<msg.getA(); a++){
          agents.get(a).setVertice(nextVertice);
        }
      } else {
        // everything decontaminated here,
        // -> move up in the tree
        if(currentVertice.numberOfContaminatedNeighbors() <= 1){
          List<TDTIAgent> affectedAgents = getAgentsAtVertice(currentVertice);
          for(TDTIAgent a : affectedAgents){
            a.setVertice(nextVertice);
          }
        } else {
          agents.get(0).setVertice(nextVertice);
        }
      }
    } else {
      System.out.println("Done");
    }
  }

  public void drawAgents(Graphics g){
    for(TDTIAgent agent : this.agents){
      agent.draw(g);
    }
  }

  public void clear(){
    agents.clear();
    base = null;
  }

  public TDTIVertice getBase(){
    return this.base;
  }

  public List<TDTIAgent> getAgentsAtVertice(TDTIVertice vertice){
    List<TDTIAgent> selectedAgents = new ArrayList<TDTIAgent>();
    for(TDTIAgent agent : agents){
      if(agent.getVertice() == vertice){
        selectedAgents.add(agent);
      }
    }
    return selectedAgents;
  }

}
