package ai.agent.mapper;

import ai.agent.entity.TaskEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 任务 Mapper
 * 任务数据访问接口
 */
@Mapper
public interface TaskMapper {


    /**
     * 保存任务
     */
    @Insert("""
        insert into agent_task
        (
          task_id,
          input,
          goal,
          agent_type,
          status,
          create_time
        )
        values
        (
          #{taskId},
          #{input},
          #{goal},
          #{agentType},
          #{status},
          now()
        )
    """)
    int insert(TaskEntity entity);



    /**
     * 查询任务
     */
    @Select("""
        select *
        from agent_task
        where task_id=#{taskId}
    """)
    TaskEntity findByTaskId(
            String taskId
    );



    /**
     * 更新状态
     */
    @Update("""
        update agent_task
        set status=#{status},
            result=#{result},
            update_time=now()
        where task_id=#{taskId}
    """)
    int update(TaskEntity entity);


}