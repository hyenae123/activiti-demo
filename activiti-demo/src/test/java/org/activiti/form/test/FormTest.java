package org.activiti.form.test;

import org.activiti.auth.test.AuthTest;
import org.activiti.engine.FormService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormTest {
    private static final Logger log = LoggerFactory.getLogger(AuthTest.class);
    
    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    @Deployment(resources = "activiti/form/build-in.bpmn20.xml")
    public void testBuildIn() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        runtimeService.startProcessInstanceByKey("process");
        
        TaskService taskService = activitiRule.getTaskService();
        Task task = taskService.createTaskQuery().singleResult();
        
        FormService formService = activitiRule.getFormService();
        Object taskForm = formService.getRenderedTaskForm(task.getId());
        
        System.out.println(taskForm);
    }
}
