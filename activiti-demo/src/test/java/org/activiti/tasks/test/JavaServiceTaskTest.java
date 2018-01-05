package org.activiti.tasks.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.activiti.tasks.test.delegate.Counter;
import org.activiti.tasks.test.delegate.MyBean;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext.xml")
public class JavaServiceTaskTest {
    private static final Logger log = LoggerFactory.getLogger(JavaServiceTaskTest.class);
    
    @Autowired
    @Rule
    public ActivitiRule activitiRule;
    
    /**
     * org.activiti.engine.impl.pvm.delegate.ActivityBehavior in org.arctiviti.engine.impl is not stable
     * ref: https://www.activiti.org/userguide/index.html#internal
     * @throws InterruptedException
     */
    @Test
    @Deployment(resources = "activiti/tasks/java_service/javaDelegate.bpmn20.xml")
    public void testJavaDelegate() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        Map<String, Object> args = new HashMap<>();
        args.put("inputArray", new int[]{1, 2, 3});
        
        runtimeService.startProcessInstanceByKey("myProcess", args);
        
        assertNull(runtimeService.createProcessInstanceQuery().singleResult());
    }
    
    @Test
    @Deployment(resources = "activiti/tasks/java_service/delegateExpression.bpmn20.xml")
    public void testDelegateExpression() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        Map<String, Object> args = new HashMap<>();
        args.put("inputArray", new int[]{3, 4, 5});
        
        runtimeService.startProcessInstanceByKey("myProcess", args);
        
        assertNull(runtimeService.createProcessInstanceQuery().singleResult());
    }
    
    @Test
    @Deployment(resources = "activiti/tasks/java_service/methodExpression1.bpmn20.xml")
    public void testMethodExpression1() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        Map<String, Object> args = new HashMap<>();
        args.put("inputArray", new int[]{3, 4});
        
        runtimeService.startProcessInstanceByKey("myProcess", args);
        
        assertNull(runtimeService.createProcessInstanceQuery().singleResult());
    }
    
    @Test
    @Deployment(resources = "activiti/tasks/java_service/methodExpression2.bpmn20.xml")
    public void testMethodExpression2() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        Map<String, Object> args = new HashMap<>();
        args.put("inputArray", new int[]{7, 8});
        args.put("delegate", new MyBean());
        
        runtimeService.startProcessInstanceByKey("myProcess", args);
        
        assertNull(runtimeService.createProcessInstanceQuery().singleResult());
    }
    
    @Test
    @Deployment(resources = "activiti/tasks/java_service/valueExpression.bpmn20.xml")
    public void testValueExpression() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        Map<String, Object> args = new HashMap<>();
        args.put("inputArray", new int[]{7, 8});
        
        MyBean delegate = new MyBean();
        delegate.setUser("user1");
        args.put("delegate", delegate);
        
        runtimeService.startProcessInstanceByKey("myProcess", args);
        
        assertNull(runtimeService.createProcessInstanceQuery().singleResult());
    }
    
    @Test
    @Deployment(resources = "activiti/tasks/java_service/oneInstance.bpmn20.xml")
    public void testOneInstance() {
        assertEquals(0, Counter.countOfInstance);
        
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        runtimeService.startProcessInstanceByKey("myProcess");
        assertEquals(1, Counter.countOfInstance);
        
        runtimeService.startProcessInstanceByKey("myProcess");
        assertEquals(1, Counter.countOfInstance);
    }
    
    @Test
    @Deployment(resources = "activiti/tasks/java_service/fieldInjection.bpmn20.xml")
    public void testFieldInjection() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        
        Map<String, Object> args = new HashMap<>();
        args.put("input", "Hello World");
        
        runtimeService.startProcessInstanceByKey("myProcess", args);
        
        HistoricVariableInstance instance = activitiRule.getHistoryService().createHistoricVariableInstanceQuery()
                .variableName("var")
                .singleResult();
        assertNotNull(instance);
        assertEquals("HELLO WORLD", instance.getValue());
        
        // test another input
        args.put("input", "Hello What");
        
        String processInstanceId = runtimeService.startProcessInstanceByKey("myProcess", args).getId();
        
        instance = activitiRule.getHistoryService().createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .variableName("var")
                .singleResult();
        assertNotNull(instance);
        assertEquals("HELLO WHAT", instance.getValue());
    }
}
