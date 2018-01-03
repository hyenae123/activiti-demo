package org.activiti.gateways.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
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

public class GatewayTest {
private static final Logger log = LoggerFactory.getLogger(GatewayTest.class);
    
    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();
    
    @Test
    @Deployment(resources = "activiti/gateways/exclusiveGateway.bpmn20.xml")
    public void testExclusiveGateway() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        TaskService taskService = activitiRule.getTaskService();
        TaskQuery query = taskService.createTaskQuery();
        
        Map<String, Object> args = new HashMap<>();
        args.put("input", 1);
        runtimeService.startProcessInstanceByKey("myProcess", args);
        
        Task task = query.singleResult();
        assertEquals("My Task1", task.getName());
        taskService.complete(task.getId());
        
        args.put("input", 2);
        runtimeService.startProcessInstanceByKey("myProcess", args);
        
        task = query.singleResult();
        assertEquals("My Task2", task.getName());
        taskService.complete(task.getId());
        
        args.put("input", 3);
        runtimeService.startProcessInstanceByKey("myProcess", args);
        
        task = query.singleResult();
        assertEquals("My Task3", task.getName());
        taskService.complete(task.getId());
    }
    
    @Test
    @Deployment(resources = "activiti/gateways/parallelGateway.bpmn20.xml")
    public void testParallelGateway() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        TaskService taskService = activitiRule.getTaskService();
        TaskQuery taskQuery = taskService.createTaskQuery();
        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
        
        runtimeService.startProcessInstanceByKey("myProcess");
        
        List<Task> tasks = taskQuery.list();
        assertEquals(2, tasks.size());
        
        // complete one task
        taskService.complete(tasks.get(0).getId());
        
        ProcessInstance processInstance = processInstanceQuery.singleResult();
        assertNotNull(processInstance);
        
        // only left one task
        assertEquals(1, taskQuery.list().size());
        
        // complete another task
        taskService.complete(tasks.get(1).getId());
        
        processInstance = processInstanceQuery.singleResult();
        assertNull(processInstance);
    }
    
    @Test
    @Deployment(resources = "activiti/gateways/inclusiveGateway.bpmn20.xml")
    public void testInclusiveGateway() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        TaskService taskService = activitiRule.getTaskService();
        TaskQuery taskQuery = taskService.createTaskQuery();
        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
        
        // an instance with task 1
        Map<String, Object> args = new HashMap<>();
        args.put("flag1", true);
        args.put("flag2", false);
        runtimeService.startProcessInstanceByKey("myProcess", args);
        
        Task task = taskQuery.singleResult();
        assertEquals("My Task1", task.getName());
        taskService.complete(task.getId());
        
        assertNull(processInstanceQuery.singleResult());
        
        // an instance with task 2
        args.put("flag1", false);
        args.put("flag2", true);
        runtimeService.startProcessInstanceByKey("myProcess", args);
        
        task = taskQuery.singleResult();
        assertEquals("My Task2", task.getName());
        taskService.complete(task.getId());
        
        assertNull(processInstanceQuery.singleResult());
        
        // an instance with both task
        args.put("flag1", true);
        args.put("flag2", true);
        runtimeService.startProcessInstanceByKey("myProcess", args);
        
        assertEquals(2, taskQuery.list().size());
    }
    
    @Test
    @Deployment(resources = "activiti/gateways/eventBasedGateway.bpmn20.xml")
    public void testEventBasedGatewayWithTimeout() throws InterruptedException {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        TaskService taskService = activitiRule.getTaskService();
        TaskQuery taskQuery = taskService.createTaskQuery();
        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
        
        runtimeService.startProcessInstanceByKey("myProcess");
        
        // if receive signal, will wait for task1
//        runtimeService.signalEventReceived("alert");
//        
//        Task task = taskQuery.singleResult();
//        assertEquals("My Task1", task.getName());
//        
//        assertNotNull(processInstanceQuery.singleResult());
        
        Thread.sleep(5000);
        
        assertNull(processInstanceQuery.singleResult());
    }
    
    @Test
    @Deployment(resources = "activiti/gateways/eventBasedGateway.bpmn20.xml")
    public void testEventBasedGatewayWithSignal() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        TaskService taskService = activitiRule.getTaskService();
        TaskQuery taskQuery = taskService.createTaskQuery();
        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
        
        runtimeService.startProcessInstanceByKey("myProcess");
        
        // receive signal and execute task1
        runtimeService.signalEventReceived("alert");
        
        Task task = taskQuery.singleResult();
        taskService.complete(task.getId());
        
        assertNull(processInstanceQuery.singleResult());
    }
}
