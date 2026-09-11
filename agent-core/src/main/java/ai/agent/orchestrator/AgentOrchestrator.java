package ai.agent.orchestrator;
import ai.agent.Agent;
import java.util.List;
/**
 * Agent 编排器
 * 负责协调多个 Agent 的执行流程
 */

public class AgentOrchestrator {


    private final List<Agent> agents;



    public AgentOrchestrator(List<Agent> agents){

        this.agents=agents;

    }



    public Agent route(String input){

        for(Agent agent:agents){
            if(agent.name().contains("chat")){
                return agent;
            }
        }

        return agents.get(0);


    }


}
