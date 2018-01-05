package org.activiti.versions.test;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionTest {
    private static final Logger log = LoggerFactory.getLogger(VersionTest.class);
        
    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();
    
    @Test
    public void testExclusiveGateway() {
        RepositoryService repositoryService = activitiRule.getRepositoryService();
        DeploymentBuilder builder = repositoryService.createDeployment();
        builder.addClasspathResource("activiti/versions/myProcess1.bpmn20.xml").deploy();
        
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
        ProcessDefinition process = query.processDefinitionKey("myProcess").singleResult();
        log.info("Process v1: {}", process);
        
        builder.addClasspathResource("activiti/versions/myProcess2.bpmn20.xml").deploy();
        process = query.processDefinitionKey("myProcess").singleResult();
        log.info("Process v2: {}", process);
    }
}
