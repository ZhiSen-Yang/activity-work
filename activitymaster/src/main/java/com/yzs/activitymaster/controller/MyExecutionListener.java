package com.yzs.activitymaster.controller;

import com.alibaba.druid.util.StringUtils;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.history.HistoricVariableInstanceQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


public class MyExecutionListener implements ExecutionListener,ActivitiEventListener{
	 private static final Logger log = LoggerFactory.getLogger(MyExecutionListener.class);
	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	
	@Override
	public void notify(DelegateExecution execution) {
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
		String eventName = execution.getEventName();
		
		// start
		 ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		if ("start".equals(eventName)) {
			TaskService taskService = engine.getTaskService();
			//ProcessInstanceBuilder
			//taskService.addComment("execution.getTenantId()", execution.getProcessInstanceId(), "");
			//taskService.addComment(execution.getTenantId(), processInstanceId, message)
			System.out.println("start=========");
		} else if ("end".equals(eventName)) {
			 HistoryService historyService = engine.getHistoryService();
			String id=execution.getProcessInstanceId();
			HistoricVariableInstanceQuery query = historyService.createHistoricVariableInstanceQuery();
			HistoricVariableInstanceQuery query2 = query.processInstanceId(id);
			List<HistoricVariableInstance> list = query2.list();
			String url=null;
			String businessKey= null;
			String end = null;
			String success = "true";
			for (HistoricVariableInstance h : list) {
				if("url".equals(h.getVariableName())) {
					url=(String)h.getValue();
				}
				
				if("businessKey".equals(h.getVariableName())) {
					businessKey=(String)h.getValue();
				}
				if("end".equals(h.getVariableName())) {
					end =(String)h.getValue();
				}
			}
			if(StringUtils.isEmpty(end)) {
				success = end;
			}
			url="127.0.0.1:8997/scm/scmReturnMoney/v1/cc";
			if(!StringUtils.isEmpty(url)) {
				try {
					response.sendRedirect("http://"+url+"?"+"businessKey="+businessKey+"&success="+success);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("end=========");
		} else if ("take".equals(eventName)) {
			System.out.println("take=========");
		}
	}

	@Override
	public void onEvent(ActivitiEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isFailOnException() {
		// TODO Auto-generated method stub
		return false;
	}


	
}
