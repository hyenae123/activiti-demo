package org.activiti.events.test;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntermediateNoneEventTest {
    public static class MyExecutionListener implements ExecutionListener {
        private static final long serialVersionUID = 1L;
        
        private static final Logger log = LoggerFactory.getLogger(MyExecutionListener.class);
        public void notify(DelegateExecution execution) {
            log.info("notify doing something...");
        }
    }
}
