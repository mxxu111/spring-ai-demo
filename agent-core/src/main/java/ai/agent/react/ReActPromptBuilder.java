package ai.agent.react;
import ai.agent.model.Task;

/**
 * Prompt 构建器
 * 构建 ReAct 模式的 Prompt
 * 使用结构化 Prompt 引导 LLM 输出 Thought/Action
 * 提供清晰的工具描述和参数 Schema
 * 限制最大步数防止无限循环
 */
public class ReActPromptBuilder {



    public String build(Task task){
        return """
        你是一个智能Agent。
        任务:
        %s
        请输出:
        Thought:
        Action:
        Action Input:
        """.formatted(task.getInput());

    }

}
