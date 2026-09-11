package ai.agent.service;

import ai.agent.entity.StepEntity;
import ai.agent.entity.TaskEntity;
import ai.agent.mapper.StepMapper;
import ai.agent.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 任务持久化服务
 * 负责任务和步骤的持久化操作
 */
@Service
@RequiredArgsConstructor
public class TaskPersistenceService {



    private final TaskMapper taskMapper;


    private final StepMapper stepMapper;



    /**
     * 创建任务
     */
    public void createTask(TaskEntity task){

        taskMapper.insert(task);

    }



    /**
     * 保存执行步骤
     */
    public void saveStep(StepEntity step){

        stepMapper.insert(step);

    }



    /**
     * 完成任务
     */
    public void finish(String taskId, String result){

        TaskEntity entity = new TaskEntity();
        entity.setTaskId(taskId);
        entity.setStatus("SUCCESS");
        entity.setResult(result);
        taskMapper.update(entity);

    }


}