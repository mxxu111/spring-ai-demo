package ai.agent.model;

import lombok.Data;

/**
 * 观察模型
 */
@Data
public class Observation {


    /**
     * 动作
     */
    private Action action;


    /**
     * 返回结果
     */
    private String result;


    /**
     * 是否成功
     */
    private boolean success;


}