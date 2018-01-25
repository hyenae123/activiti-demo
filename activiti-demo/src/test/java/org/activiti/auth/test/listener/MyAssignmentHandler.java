package org.activiti.auth.test.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class MyAssignmentHandler implements TaskListener {

    private static final long serialVersionUID = 1L;

    public void notify(DelegateTask delegateTask) {
//        delegateTask.setAssignee("kermit");
        delegateTask.addCandidateGroup("liuxy");
//        delegateTask.addCandidateGroup("management");
    }
}
