package ai.agent.model;

import lombok.Data;

import java.util.List;

/**
 * 计划模型
 */
@Data
public class Plan {


    /**
     * 计划ID
     */
    private String planId;



    /**
     * 目标
     */
    private String goal;



    /**
     * 步骤
     */
    private List<AgentStep> agentSteps;


}
