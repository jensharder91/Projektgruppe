package tdti;

import java.util.*;
import java.awt.Graphics;

public class TDTIAgentCoordinator {

  static TDTIAgentCoordinator coordinator;
  private TDTIVertice base;
  private int numberOfAgents;
  private List<TDTIAgent> agents = new ArrayList<TDTIAgent>();

  private TDTIAgentCoordinator(){
    super();
  }

  public static TDTIAgentCoordinator getCoordinator(){
    if(coordinator == null){
      coordinator = new TDTIAgentCoordinator();
    }
    return coordinator;
  }

  public void setBase(TDTIVertice base){
    this.base = base;
    if(base != null){
      this.numberOfAgents = base.getPsi();
    } else {
      this.numberOfAgents = 0;
    }
    this.agents.clear();
    for(int a=0; a<numberOfAgents; a++){
      this.agents.add(new TDTIAgent(base,base,a,numberOfAgents));
    }
    // TODO: Remove this, was just for testing
    if(this.agents.size() >= 2){
      this.agents.get(1).setVertice(base.getNeighborThatSentSmallestA());
    }
    // END
  }

  public void drawAgents(Graphics g){
    for(TDTIAgent agent : this.agents){
      agent.draw(g);
    }
  }

  public void clear(){
    agents.clear();
  }

}
