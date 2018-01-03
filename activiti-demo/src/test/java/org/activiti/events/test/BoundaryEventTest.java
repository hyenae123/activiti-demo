package org.activiti.events.test;

import static org.junit.Assert.*;

import java.util.List;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ExecutionQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
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
    
    @Test
    @Deployment(resources = "activiti/events/boundary/errorBoundaryEvent.bpmn20.xml")
    public void testErrorBoundaryEvent() {
        activitiRule.getRuntimeService().startProcessInstanceByKey("myProcess");

        TaskService taskService = activitiRule.getTaskService();
        
        Task task1 = taskService.createTaskQuery()
                .processDefinitionKey("myProcess")
                .singleResult();
        
        log.info("Task ID is: {}", task1.getId());
        
        taskService.complete(task1.getId());
        
        Task task2 = taskService.createTaskQuery()
                .processDefinitionKey("myProcess")
                .singleResult();
        
        taskService.complete(task2.getId());
        
        Task task3 = taskService.createTaskQuery()
                .processDefinitionKey("myProcess")
                .singleResult();
        assertNull(task3);
    }
    
    @Test
    @Deployment(resources = "activiti/events/boundary/signalBoundaryEvent.bpmn20.xml")
    public void testSignalBoundaryEvent() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        TaskService taskService = activitiRule.getTaskService();
        
        // start process1 and do task1, then waiting for signal
        runtimeService.startProcessInstanceByKey("process1");
        
        Task task1 = taskService.createTaskQuery()
                .processDefinitionKey("process1")
                .singleResult();
        assertNotNull(task1);
        
        taskService.complete(task1.getId());
        
        Task task2 = taskService.createTaskQuery()
                .processDefinitionKey("process1")
                .singleResult();
        assertEquals("My Task2", task2.getName());
        
        // throw signal by api
        runtimeService.signalEventReceived("The Signal");
        
        task1 = taskService.createTaskQuery()
                .processDefinitionKey("process1")
                .singleResult();
        assertEquals("My Task1", task1.getName());
        
        taskService.complete(task1.getId());
        task2 = taskService.createTaskQuery()
                .processDefinitionKey("process1")
                .singleResult();
        assertEquals("My Task2", task2.getName());
        
        // throw signal by process
        runtimeService.startProcessInstanceByKey("process2");
        
        Task task3 = taskService.createTaskQuery()
                .processDefinitionKey("process2")
                .singleResult();
        assertEquals("My Task3", task3.getName());
        
        taskService.complete(task3.getId());
        
        task1 = taskService.createTaskQuery()
                .processDefinitionKey("process1")
                .singleResult();
        assertEquals("My Task1", task1.getName());
    }
    
    @Test
    @Deployment(resources = "activiti/events/boundary/messageBoundaryEvent.bpmn20.xml")
    public void testMessageBoundaryEvent() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        TaskService taskService = activitiRule.getTaskService();
        TaskQuery taskQuery = taskService.createTaskQuery();
        ExecutionQuery executionQuery = runtimeService.createExecutionQuery();
        
        runtimeService.startProcessInstanceByKey("myProcess");
        
        Task task = taskQuery.singleResult();
        assertEquals("My Task1", task.getName());
        
        taskService.complete(task.getId());
        task = taskQuery.singleResult();
        assertEquals("My Task2", task.getName());
        
        assertEquals(1, executionQuery.messageEventSubscriptionName("newMessage").list().size());
        Execution execution = executionQuery.messageEventSubscriptionName("newMessage").singleResult();
        runtimeService.messageEventReceived("newMessage", execution.getId());
        
        task = taskQuery.singleResult();
        assertEquals("My Task1", task.getName());
    }
}
