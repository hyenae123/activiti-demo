package org.activiti.variable.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.activiti.auth.test.AuthTest;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VariableTest {
    private static final Logger log = LoggerFactory.getLogger(AuthTest.class);
    
    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    @Deployment(resources = "activiti/variable/variable.bpmn20.xml")
    public void testTransientVariable() {
        TaskService taskService = activitiRule.getTaskService();
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        
        runtimeService.startProcessInstanceByKey("process");
        Task task = taskService.createTaskQuery().active().singleResult();
        
        Map<String, Object> args = new HashMap<>();
        args.put("var1", "test");
        taskService.complete(task.getId(), null, args);
        
        task = taskService.createTaskQuery().active().singleResult();
        assertEquals("test", task.getAssignee());
        
        args = taskService.getVariables(task.getId());
        assertNull(args.get("var1"));
        
        taskService.complete(task.getId());
        
        task = taskService.createTaskQuery().active().singleResult();
        assertEquals("test", task.getAssignee());
    }
}
