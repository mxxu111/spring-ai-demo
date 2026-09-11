package ai.agent.model;

import lombok.Data;

import java.util.Map;

/**
 * 动作模型
 */
@Data
public class Action {


    /**
     * 动作名称
     */
    private String name;


    /**
     * 工具名称
     */
    private String tool;


    /**
     * 参数
     */
    private Map<String,Object> arguments;


}