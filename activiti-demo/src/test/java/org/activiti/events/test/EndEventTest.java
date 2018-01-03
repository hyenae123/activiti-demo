package org.activiti.events.test;

import static org.junit.Assert.*;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndEventTest {
    private static final Logger log = LoggerFactory.getLogger(EndEventTest.class);
    
    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();
    
    /**
     *  TODO a sub-process can be an 
     *  embedded sub-process, 
     *  call activity, 
     *  event sub-process 
     *  or transaction sub-process.
     *  
     *  This rule applies in general: 
     *  when for example there is a multi-instance call activity or embedded subprocess, only that instance will be ended, the other instances and the process instance are not affected.
     */
    @Test
    @Deployment(resources = "activiti/events/end/terminateEndEvent.bpmn20.xml")
    public void testTerminateEndEvent() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        IdentityService identityService = activitiRule.getIdentityService();
        
        //TODO
    }
}
