package ai.agent.model;

import lombok.Data;

/**
 * 思考模型
 */
@Data
public class Thought {


    /**
     * 思考内容
     */
    private String reasoning;


    /**
     * 下一步动作
     */
    private Action action;

}