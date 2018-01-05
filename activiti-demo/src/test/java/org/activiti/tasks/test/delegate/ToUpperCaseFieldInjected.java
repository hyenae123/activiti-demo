package org.activiti.tasks.test.delegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToUpperCaseFieldInjected implements JavaDelegate {

    public static int countOfInstance = 0;
    
    private static final Logger log = LoggerFactory.getLogger(ToUpperCaseFieldInjected.class);
    
    private Expression text;
    
    public ToUpperCaseFieldInjected() {
        countOfInstance++;
    }
    
    @Override
    public void execute(DelegateExecution execution) {
        log.info("Count of instance: {}", countOfInstance);
        execution.setVariable("var", ((String)text.getValue(execution)).toUpperCase());
    }
}
