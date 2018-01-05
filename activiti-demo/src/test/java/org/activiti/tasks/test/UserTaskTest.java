package org.activiti.tasks.test;

import static org.junit.Assert.*;

import java.util.Date;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserTaskTest {
    private static final Logger log = LoggerFactory.getLogger(UserTaskTest.class);
    
    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    @Deployment(resources = "activiti/tasks/user/dueDate.bpmn20.xml")
    public void testDueDate() throws InterruptedException {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        TaskService taskService = activitiRule.getTaskService();

        runtimeService.startProcessInstanceByKey("myProcess");
        
        Task task = taskService.createTaskQuery()
                .taskDueBefore(new Date())
                .singleResult();
        
        assertNull(task);
        
        Thread.sleep(5000);
        
        task = taskService.createTaskQuery()
                .taskDueBefore(new Date())
                .singleResult();
        
        assertNotNull(task);
    }
    
    @Test
    @Deployment(resources = "activiti/tasks/user/userAssignment.bpmn20.xml")
    public void testUserAssignment() throws InterruptedException {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        IdentityService identityService = activitiRule.getIdentityService();
        TaskQuery taskQuery = activitiRule.getTaskService().createTaskQuery();

        runtimeService.startProcessInstanceByKey("eatDogFood");
        
        identityService.saveUser(identityService.newUser("dog"));
        identityService.saveUser(identityService.newUser("cat"));
        identityService.saveUser(identityService.newUser("me"));
        identityService.saveGroup(identityService.newGroup("human"));
        identityService.createMembership("me", "human");
        
        assertNotNull(taskQuery.taskAssignee("dog").singleResult());
        assertNull(taskQuery.taskAssignee("cat").singleResult());
        assertNull(taskQuery.taskAssignee("me").singleResult());
        
        assertNull(taskQuery.taskCandidateUser("dog").singleResult());
        
        // not pass, cause of assignee is set
        assertNotNull(taskQuery.taskCandidateUser("cat").singleResult());
        assertNotNull(taskQuery.taskCandidateUser("me").singleResult());
    }
}
