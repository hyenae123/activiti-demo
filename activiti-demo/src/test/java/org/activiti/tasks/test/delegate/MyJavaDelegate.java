package org.activiti.tasks.test.delegate;

import java.util.Arrays;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyJavaDelegate implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(MyJavaDelegate.class);
    
    @Override
    public void execute(DelegateExecution execution) {
        int[] inputArray = (int[])execution.getVariable("inputArray");
        log.info("execute MyJavaDelegate with var: {}", Arrays.toString(inputArray));
    }

}
