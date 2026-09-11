package ai.agent.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 执行上下文
 */
@Data
public class AgentContext {

    /**
     * 用户原始问题
     */
    private String userInput;

    /**
     * 当前步骤
     */
    private int step;

    /**
     * 最大执行次数
     */
    private int maxSteps = 10;

    /**
     * 历史执行过程
     */
    private List<AgentStep> steps = new ArrayList<>();

    /**
     * 最终答案
     */
    private String finalAnswer;

    /**
     * 是否结束
     */
    private boolean finished;

}