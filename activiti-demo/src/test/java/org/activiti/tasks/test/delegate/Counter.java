package org.activiti.tasks.test.delegate;

import java.io.Serializable;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Counter implements JavaDelegate, Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(Counter.class);
    
    public static int countOfInstance = 0;
    
    private int countOfExecution;
    
    public Counter() {
        countOfInstance++;
        countOfExecution = 0;
    }

    @Override
    public void execute(DelegateExecution execution) {
        countOfExecution++;
        log.info("Execute Delegate: countOfInstance {}, countOfExecution {}", countOfInstance, countOfExecution);
    }
}
