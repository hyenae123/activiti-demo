package org.activiti.tasks.test.delegate;

import java.io.Serializable;
import java.util.Arrays;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(MyBean.class);
    
    private String user;
    
    public void execute(DelegateExecution execution) {
        int[] inputArray = (int[])execution.getVariable("inputArray");
        log.info("execute MyJavaDelegate with var: {}", Arrays.toString(inputArray));
    }

    public String getUser() {
        log.info("getUser: {}", user);
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
}
