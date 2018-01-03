package org.activiti.tasks.test;

import static org.junit.Assert.*;

import java.util.List;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.activiti.events.test.StartEventTest;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserTaskTest {
    private static final Logger log = LoggerFactory.getLogger(UserTaskTest.class);
    
    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    @Deployment(resources = "activiti/tasks/user/userTask.bpmn20.xml")
    public void testInitiator() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        IdentityService identityService = activitiRule.getIdentityService();

        try {
            identityService.setAuthenticatedUserId("bono");
            runtimeService.startProcessInstanceByKey("myProcess");
        } finally {
            identityService.setAuthenticatedUserId(null);
        }

        TaskService taskService = activitiRule.getTaskService();
        List<Task> tasks = taskService.createTaskQuery()
            .taskAssignee("bono")
            .list();
        
        assertEquals(1, tasks.size());
    }
}
