package org.activiti.auth.test;

import static org.junit.Assert.*;

import java.util.List;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthTest {
private static final Logger log = LoggerFactory.getLogger(AuthTest.class);
    
    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    @Deployment(resources = "activiti/auth/auth.bpmn20.xml")
    public void testAuth() {
        RepositoryService repositoryService = activitiRule.getRepositoryService();
        IdentityService identityService = activitiRule.getIdentityService();
        
        User user = identityService.newUser("user1");
        Group group = identityService.newGroup("group1");
        
        identityService.saveUser(user);
        identityService.saveGroup(group);
        
        identityService.createMembership("user1", "group1");
        
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().startableByUser("user1").list();
        log.info("Process Definitions: {}", processDefinitions);
        assertEquals(2, processDefinitions.size());
        
        assertEquals(0, repositoryService.createProcessDefinitionQuery().startableByUser("user3"));
        
        // activiti not limit in fact
        identityService.setAuthenticatedUserId("user3");
        activitiRule.getRuntimeService().startProcessInstanceByKey("process1");
    }
}
