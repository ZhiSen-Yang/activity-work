package com.yzs.activitymaster.controller;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ProcessEventListener implements ActivitiEventListener  {
	 private static final Logger log = LoggerFactory.getLogger(ProcessEventListener.class);
	@Override
	public void onEvent(ActivitiEvent event) {
		ActivitiEventType eventType = event.getType();
        if (ActivitiEventType.PROCESS_STARTED.equals(eventType)){
        	log.info("流程启动 {} \t {}",eventType,event.getProcessInstanceId());
        }else if (ActivitiEventType.PROCESS_COMPLETED.equals(eventType)){
        	log.info("流程结束 {} \t {}",eventType,event.getProcessInstanceId());
        }
	}

	@Override
	public boolean isFailOnException() {
		// TODO Auto-generated method stub
		return false;
	}

}
