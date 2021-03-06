package org.activiti.spring.test;

import static org.junit.Assert.*;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.db.DbSchemaCreate;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.h2.command.ddl.CreateSchema;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext.xml")
public class SpringIntegrationTest {

    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    @Rule
    public ActivitiRule activitiSpringRule;
    
    @Test
    public void simplestProcessTest() {
        runtimeService.startProcessInstanceByKey("simplestProcess");
        assertEquals(0, runtimeService.createExecutionQuery().count());
    }
    
    @Test
    public void simpleProcessTest() {
        runtimeService.startProcessInstanceByKey("simpleProcess");
        Task task = taskService.createTaskQuery().singleResult();
        assertEquals("My Task", task.getName());
        
        taskService.complete(task.getId());
        assertEquals(0, runtimeService.createExecutionQuery().count());
    }
}
