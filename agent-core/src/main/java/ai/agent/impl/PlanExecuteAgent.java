package ai.agent.impl;
import ai.agent.Agent;
import ai.agent.model.AgentContext;
import ai.agent.model.AgentResult;
import ai.agent.model.Task;
import ai.agent.model.*;
/**
 * Plan-Execute Agent 实现
 * 先规划再执行的两阶段智能代理
 */

public class PlanExecuteAgent implements Agent {



    @Override
    public AgentResult execute(Task task, AgentContext context){


        /*
         * 1. Planner生成Plan
         */

        Plan plan = createPlan(task);

        /*
         * 2. 顺序执行Step
         */

        for(AgentStep agentStep : plan.getAgentSteps()){
            executeStep(agentStep);
        }



        return AgentResult.builder()
                .success(true)
                .build();


    }



    private Plan createPlan(Task task){

        return new Plan();

    }



    private void executeStep(AgentStep agentStep){
    }



    @Override
    public String name(){
        return "plan-agent";
    }

}
