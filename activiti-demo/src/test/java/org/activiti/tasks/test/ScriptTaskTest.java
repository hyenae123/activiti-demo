package org.activiti.tasks.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptTaskTest {
    private static final Logger log = LoggerFactory.getLogger(ScriptTaskTest.class);
    
    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    @Deployment(resources = "activiti/tasks/script/autoStoreVariables.bpmn20.xml")
    public void testAutoStoreVariables() throws InterruptedException {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        HistoryService historyService = activitiRule.getHistoryService();

        Map<String, Object> args = new HashMap<>();
        args.put("inputArray", new int[]{1, 2, 3});
        
        runtimeService.startProcessInstanceByKey("myProcess", args);
        
        assertNull(runtimeService.createProcessInstanceQuery().singleResult());
        
        List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery().list();
        assertEquals(3, list.size());
        
        log.info("Historic Variables: {}", list);
    }
    
    @Test
    @Deployment(resources = "activiti/tasks/script/setVariable.bpmn20.xml")
    public void testSetVariable() throws InterruptedException {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        HistoryService historyService = activitiRule.getHistoryService();

        Map<String, Object> args = new HashMap<>();
        args.put("inputArray", new int[]{1, 2, 3});
        
        runtimeService.startProcessInstanceByKey("myProcess", args);
        
        assertNull(runtimeService.createProcessInstanceQuery().singleResult());
        
        assertEquals(2, historyService.createHistoricVariableInstanceQuery().list().size());
        
        HistoricVariableInstance instance = historyService.createHistoricVariableInstanceQuery()
                .variableName("result")
                .singleResult();
        assertNotNull(instance);
        
        log.info("Variable: {}", instance);
    }
    
    @Test
    @Deployment(resources = "activiti/tasks/script/resultVariable.bpmn20.xml")
    public void testResultVariable() throws InterruptedException {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        HistoryService historyService = activitiRule.getHistoryService();

        Map<String, Object> args = new HashMap<>();
        args.put("inputArray", new int[]{1, 2, 3});
        
        runtimeService.startProcessInstanceByKey("myProcess", args);
        
        assertNull(runtimeService.createProcessInstanceQuery().singleResult());
        
        HistoricVariableInstance instance = historyService.createHistoricVariableInstanceQuery()
                .variableName("result")
                .singleResult();
        assertNotNull(instance);
        
        log.info("Variable: {}", instance);
    }
}
