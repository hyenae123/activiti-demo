package org.activiti.events.test;

import static org.junit.Assert.*;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ExecutionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntermediateThrowingEventTest {
    private static final Logger log = LoggerFactory.getLogger(IntermediateThrowingEventTest.class);
    
    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    @Deployment(resources = "activiti/events/intermediate_throwing/noneEvent.bpmn20.xml")
    public void testNoneEvent() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();

        runtimeService.startProcessInstanceByKey("myProcess");
        ProcessInstance processInstance = query.processDefinitionKey("myProcess").singleResult();
    }
    
    @Test
    @Deployment(resources = "activiti/events/intermediate_catching/timerEvent.bpmn20.xml")
    public void testTimerEvent() throws InterruptedException {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();

        runtimeService.startProcessInstanceByKey("myProcess");
        ProcessInstance processInstance = query.processDefinitionKey("myProcess").singleResult();
        assertNotNull(processInstance);
        
        Thread.sleep(5000);
        
        processInstance = query.processDefinitionKey("myProcess").singleResult();
        assertNull(processInstance);
    }
    
    @Test
    @Deployment(resources = "activiti/events/intermediate_catching/signalEvent.bpmn20.xml")
    public void testSignalEvent() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();

        runtimeService.startProcessInstanceByKey("myProcess");
        ProcessInstance processInstance = query.processDefinitionKey("myProcess").singleResult();
        assertNotNull(processInstance);
        
        runtimeService.signalEventReceived("signal");
        
        processInstance = query.processDefinitionKey("myProcess").singleResult();
        assertNull(processInstance);
    }
    
    @Test
    @Deployment(resources = "activiti/events/intermediate_catching/messageEvent.bpmn20.xml")
    public void testMessageEvent() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();

        runtimeService.startProcessInstanceByKey("myProcess");
        ProcessInstance processInstance = query.processDefinitionKey("myProcess").singleResult();
        assertNotNull(processInstance);
        
        Execution execution = runtimeService.createExecutionQuery()
                .messageEventSubscriptionName("message")
                .singleResult();
        runtimeService.messageEventReceived("message", execution.getId());
        
        processInstance = query.processDefinitionKey("myProcess").singleResult();
        assertNull(processInstance);
    }
}
