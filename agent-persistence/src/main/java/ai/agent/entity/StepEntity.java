package ai.agent.entity;
import lombok.Data;
import java.time.LocalDateTime;
/**
 * 步骤实体
 * 表示任务执行中的一个步骤
 */

@Data
public class StepEntity {


    private Long id;


    /**
     * 任务ID
     */
    private String taskId;


    /**
     * 步骤编号
     */
    private Integer stepNo;


    /**
     * 步骤描述
     */
    private String description;


    /**
     * Thought
     */
    private String thought;


    /**
     * Action
     */
    private String action;


    /**
     * Observation
     */
    private String observation;


    /**
     * 执行状态
     */
    private String status;


    private LocalDateTime createTime;


}