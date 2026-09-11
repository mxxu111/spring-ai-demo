package ai.agent.model;

import ai.agent.model.AgentStep;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Agent 执行结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentResult {

    /**
     * 是否执行成功
     */
    private boolean success;

    /**
     * 最终答案
     */
    private String answer;

    /**
     * Agent 执行步骤
     */
    private List<AgentStep> steps;

    /**
     * 错误信息
     */
    private String errorMessage;

    public static AgentResult success(String answer, List<AgentStep> steps) {

        return AgentResult.builder()
                .success(true)
                .answer(answer)
                .steps(steps)
                .build();
    }

    public static AgentResult failure(String errorMessage) {

        return AgentResult.builder()
                .success(false)
                .errorMessage(errorMessage)
                .steps(new ArrayList<>())
                .build();
    }
}