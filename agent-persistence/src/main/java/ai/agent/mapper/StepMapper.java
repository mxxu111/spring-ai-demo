package ai.agent.mapper;

import ai.agent.entity.StepEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * 步骤 Mapper
 * 步骤数据访问接口
 */
@Mapper
public interface StepMapper {



    @Insert("""
        insert into agent_step
        (
          task_id,
          step_no,
          description,
          thought,
          action,
          observation,
          status,
          create_time
        )
        values
        (
          #{taskId},
          #{stepNo},
          #{description},
          #{thought},
          #{action},
          #{observation},
          #{status},
          now()
        )
    """)
    int insert(
            StepEntity entity
    );


}