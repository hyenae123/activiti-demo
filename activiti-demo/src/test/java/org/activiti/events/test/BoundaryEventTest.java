package org.activiti.events.test;

import static org.junit.Assert.*;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoundaryEventTest {
    private static final Logger log = LoggerFactory.getLogger(BoundaryEventTest.class);
    
    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    @Deployment(resources = "activiti/events/boundary/timerBoundaryEvent.bpmn20.xml")
    public void testTimerBoundaryEventCanceled() throws InterruptedException {
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        runtimeService.startProcessInstanceByKey("myProcess");

        Thread.sleep(10000);
        
        TaskService taskService = activitiRule.getTaskService();
        Task task1 = taskService.createTaskQuery()
                .processDefinitionKey("myProcess")
                .taskDefinitionKey("theTask1")
                .singleResult();
        assertNull(task1);
        
        Task task2 = taskService.createTaskQuery()
                .processDefinitionKey("myProcess")
                .taskDefinitionKey("theTask2")
                .singleResult();
        
        taskService.complete(task2.getId());
    }
    
    @Test
    @Deployment(resources = "activiti/events/boundary/timerBoundaryEvent.bpmn20.xml")
    public void testTimerBoundaryEventNotCanceled() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        runtimeService.startProcessInstanceByKey("myProcess");

        TaskService taskService = activitiRule.getTaskService();
        Task task1 = taskService.createTaskQuery()
                .processDefinitionKey("myProcess")
                .taskDefinitionKey("theTask1")
                .singleResult();
        assertNotNull(task1);
        
        taskService.complete(task1.getId());
        
        Task task2 = taskService.createTaskQuery()
                .processDefinitionKey("myProcess")
                .taskDefinitionKey("theTask2")
                .singleResult();
        
        assertNull(task2);
    }
}
