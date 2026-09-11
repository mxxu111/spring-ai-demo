package ai.agent.enums;

import lombok.Getter;

/**
 * 任务状态枚举
 */
@Getter
public enum TaskStatus {

    /**
     * 待执行
     */
    PENDING("待执行", "任务已创建，等待开始执行"),

    /**
     * 规划中
     */
    PLANNING("规划中", "Agent 正在制定执行计划"),

    /**
     * 执行中
     */
    EXECUTING("执行中", "正在执行任务步骤"),

    /**
     * 已完成
     */
    COMPLETED("已完成", "任务执行成功"),

    /**
     * 已失败
     */
    FAILED("已失败", "任务执行失败"),

    /**
     * 已取消
     */
    CANCELLED("已取消", "任务被用户取消");

    private final String name;
    private final String description;

    TaskStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * 判断是否为终态
     */
    public boolean isFinal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }
}
