package com.yzs.activitymaster.utils;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

public class ProcessCompleteListener implements ExecutionListener {
	@Override
	public void notify(DelegateExecution execution) {
		String name = execution.getEventName();
		if("".equals(name)) {
			System.out.println("全局时间结束了。。。"+execution);
		}else if("".equals(name)) {
			System.out.println("全局时间开始了 。。。。。"+execution);
		}
		// TODO Auto-generated method stub
	}

}
