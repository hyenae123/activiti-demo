package org.activiti.events.test;

import static org.junit.Assert.*;

import java.util.List;

import org.activiti.engine.FormService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartEventTest {
    private static final Logger log = LoggerFactory.getLogger(StartEventTest.class);
    
    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    @Deployment(resources = "activiti/events/initiator.bpmn20.xml")
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
    
    /**
     * Note: a subprocess always has a none start event.
     */
    @Test
    @Deployment(resources = "activiti/events/noneStartEvent.bpmn20.xml")
    public void testNoneStartEvent() {
        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("myProcess");
        
        //The id property is set to {processDefinitionKey}:{processDefinitionVersion}:{generated-id}, 
        //where generated-id is a unique number added to guarantee uniqueness of the process id for the process definition caches in a clustered environment.
        
        String procDefId = processInstance.getProcessDefinitionId();
        log.info("Process ID: {}", procDefId);
        
        FormService formService = activitiRule.getFormService();
        
        List<FormProperty> formProperties = formService.getStartFormData(procDefId).getFormProperties();
        log.info("Form properties size: {}", formProperties.size());
        for (FormProperty formProperty : formProperties) {
            log.info("Form property names: {}, with type: {}", formProperty.getName(), formProperty.getType());
        }
        log.info("Form key: {}", formService.getStartFormData(procDefId).getFormKey());
    }
    
    /**
     * Note: timers are only fired when the job or async executor is enabled 
     * (i.e. jobExecutorActivate or asyncExecutorActivate needs to be set to true in the activiti.cfg.xml, since the job and async executor are disabled by default).
     * @throws InterruptedException
     */
    @Test
    @Deployment(resources = "activiti/events/timerStartEvent.bpmn20.xml")
    public void testTimerStartEvent() throws InterruptedException {
        ProcessEngineConfiguration procEngineCfg = activitiRule.getProcessEngine().getProcessEngineConfiguration();
        assertTrue(procEngineCfg.isAsyncExecutorActivate());
        
        assertEquals(1, activitiRule.getRepositoryService().createDeploymentQuery().list().size());
        
        log.info("Task size: {}", activitiRule.getTaskService().createTaskQuery().list().size());
        
        Thread.sleep(10000);
        
        log.info("Task size: {}", activitiRule.getTaskService().createTaskQuery().list().size());
        log.info("Process Instance size: {}", activitiRule.getRuntimeService().createProcessInstanceQuery().list().size());
    }
    
    @Test
    @Deployment(resources ="activiti/events/messageStartEvent.bpmn20.xml")
    public void testMessageStartEvent() {
        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByMessage("newInvoiceMessage");
        assertNotNull(processInstance);
        
        log.info("Process Instance: {}", processInstance.getProcessDefinitionName());
    }
    
    /**
     * The signal can be fired from within a process instance using the intermediary signal throw event 
     * or through the API (runtimeService.signalEventReceivedXXX methods)
     */
    @Test
    @Deployment(resources = "activiti/events/signalStartEvent.bpmn20.xml")
    public void testSignalStartEvent() {
        //TODO what is the execution
        List<Execution> executions = activitiRule.getRuntimeService().createExecutionQuery()
                .signalEventSubscriptionName("The Signal")
                .list();
        for (Execution e : executions) {
            System.out.println(e);
        }
        
        activitiRule.getRuntimeService().signalEventReceived("The Signal");
        
        ProcessInstance processInstance = activitiRule.getRuntimeService().createProcessInstanceQuery().singleResult();
        
        log.info("Process Instance: {}", processInstance.getProcessDefinitionName());
    }
    
    @Test
    @Deployment(resources = "activiti/events/errorStartEvent.bpmn20.xml")
    public void testErrorStartEvent() {
        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("myProcess");
        log.info("Process Instance: {}", processInstance.getProcessDefinitionName());
        
        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();
        log.info("Taks: {}", task.getName());
        
        activitiRule.getTaskService().complete(task.getId());
        
        task = activitiRule.getTaskService().createTaskQuery().singleResult();
        log.info("Taks: {}", task.getName());
    }
}
