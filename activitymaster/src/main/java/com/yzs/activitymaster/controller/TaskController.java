package com.yzs.activitymaster.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzs.activitymaster.config.listener.Jump2TargetFlowNodeCommand;
import com.yzs.activitymaster.service.FlowTaskService;
import com.yzs.activitymaster.util.DateUtils;
import com.yzs.activitymaster.utils.ActivitiUtils;
import com.yzs.activitymaster.utils.DeleteTaskCmd;
import com.yzs.activitymaster.utils.SetFLowNodeAndGoCmd;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti5.engine.impl.identity.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程任务Controller
 * <p>
 * 
 */
@Api(description = "任务控制类")
@RestController
@RequestMapping(value = "/task")
public class TaskController {

	/// task/getDemandByUserId/李四/1/10     /task/ui?taskId=117501
	@Autowired
	private TaskService taskService;
	@Autowired
	private HistoryService historyService;

	@Autowired
	private FlowTaskService flowTaskService;
	@Autowired
	private RuntimeService runtimeService;
	// 流程任务Service
	@Autowired
	private ActivitiUtils activitiUtils;
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private ManagementService managementService;

	/**
	 * 根据分组ID获取任务列表，带分页
	 *
	 * @param groupId  分组ID
	 * @param pageNum  当前页数
	 * @param pageSize 显示数量
	 * @return
	 */
	@GetMapping(value = "/getDemandByGroupId/{groupId}/{pageNum}/{pageSize}")
	public String getDemandByGroupId(@PathVariable String groupId, @PathVariable int pageNum,
                                     @PathVariable int pageSize) {
		pageNum = (pageNum - 1) * pageSize;
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		// 根据分组ID获取任务列表，不包含流程变量
//        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup(groupId).listPage(pageNum,pageSize);
		// 根据分组ID获取任务列表，包含流程变量
		List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup(groupId).includeProcessVariables()
				.listPage(pageNum, pageSize);
		if (tasks != null && tasks.size() > 0) {
			for (Task task : tasks) {
				Map<String, Object> resultMap = new HashMap<String, Object>();
				resultMap.put("任务名称", task.getName());
				resultMap.put("任务ID", task.getId());
				resultMap.put("组织ID", task.getProcessVariables().get("zzid"));
				resultMap.put("创建人", task.getProcessVariables().get("createName"));
				resultMap.put("需求描述", task.getProcessVariables().get("demandReviews"));
				resultList.add(resultMap);
			}
			JSONArray jsonArray = new JSONArray();
			jsonArray.add(resultList);
			return jsonArray.toJSONString();
		} else {
			return "获取成功，该组织下任务数: 0 ";
		}
	}

	/**
	 * 根据当前用户Id获取任务列表,带分页
	 *
	 * @param userId   用户ID
	 * @param pageNum  当前页数
	 * @param pageSize 显示数量
	 * @return
	 */
	@ApiOperation(value = "根据当前用户名称获取任务列表,带分页", notes = "根据当前用户名称获取任务列表,带分页")
	@PostMapping(value = "/getDemandByUserId")
	public String getDemandByUserId(
			@ApiParam(name = "userId", value = "当前用户", required = true) @RequestParam String userId,
			@ApiParam(name = "pageNum", value = "当前页数", required = true) @RequestParam int pageNum,
			@ApiParam(name = "pageSize", value = "显示数量", required = true) @RequestParam int pageSize) {
		pageNum = (pageNum - 1) * pageSize;

		// 创建一个任务查询对象
		TaskQuery taskQuery = taskService.createTaskQuery();
		// 办理人的任务列表
		List<Task> list = taskQuery.taskAssignee(userId).includeProcessVariables().listPage(pageNum, pageSize);

		// 遍历任务列表
		JSONArray res = new JSONArray();
		if (list != null && list.size() > 0) {
			for (Task task : list) {
				// 查询流程实例信息
				List<HistoricProcessInstance> list2 = historyService.createHistoricProcessInstanceQuery()
						.processInstanceId(task.getProcessInstanceId()).list();
				JSONObject json = new JSONObject();
				json.put("流程实例Id", list2.get(0).getId());
				json.put("任务标题：", list2.get(0).getStartUserId() + "在" + DateUtils.timeDay(list2.get(0).getStartTime())
						+ "发起" + task.getProcessVariables().get("processName"));
				json.put("所属人：", list2.get(0).getStartUserId());
				json.put("任务的办理人：", task.getAssignee());
				json.put("任务的id：", task.getId());
				json.put("任务的名称：", task.getName());
				json.put("业务主键Id", list2.get(0).getBusinessKey());
				json.put("创建时间", DateUtils.times(list2.get(0).getStartTime()));
				res.add(json);
			}
		}

		return res.toJSONString();
	}

	/**
	 * 完成流程任务
	 *
	 * @param taskId    任务ID
	 * @param variables 流程变量
	 * @return 返回下一个任务的ID
	 */
	@ApiOperation(value = "完成流程任务", notes = "完成流程任务")
	@PostMapping(value = "/fulfil")
	public String fulfilTask(@ApiParam(name = "taskId", value = "任务ID", required = true) @RequestParam String taskId,
			@ApiParam(name = "variables", value = "审批意见以及备注", required = true) @RequestBody Map<String, Object> variables)
			throws Exception {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		Authentication.setAuthenticatedUserId(task.getAssignee());
		taskService.addComment(taskId, task.getProcessInstanceId(), "通过", (String)variables.get("cause"));
		return flowTaskService.fulfilTask(taskId, variables);
	}

	@ApiOperation(value = "终止流程任务", notes = "终止流程任务")
	@PostMapping(value = "/endTask")
	public String endTask(@ApiParam(name = "taskId", value = "当前任务Id", required = true) @RequestParam String taskId,
			@ApiParam(name = "variables", value = "审批意见以及备注", required = true) @RequestBody Map<String, Object> variables)
			throws Exception {
		// 获取当前任务
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		// 获取流程定义
		Process process = repositoryService.getBpmnModel(task.getProcessDefinitionId()).getMainProcess();
		runtimeService.setVariables(task.getExecutionId(), variables);
		//记录
		
		taskService.addComment(taskId, task.getProcessInstanceId(), "终止", (String)variables.get("cause"));
		// 获取流程节点
		FlowNode element = (FlowNode) process.getFlowElement("EndEvent");
		// 删除当前运行任务

		String executionEntityId = managementService.executeCommand(new DeleteTaskCmd(task.getId()));
		// 流程执行到来源节点

		managementService.executeCommand(new SetFLowNodeAndGoCmd(element, executionEntityId));
		
		return "操作成功！";
	}

	/**
	 * 创建流程并获取第一个任务
	 *
	 * @param userId    创建人ID
	 * @param key       流程ID
	 * @param variables 流程变量
	 * @return 返回当前任务的ID
	 */
	@ApiOperation(value = "创建流程并获取第一个任务", notes = "创建流程并获取第一个任务")
	@PostMapping(value = "/createFlow/{userId}/{key}")
	public String createFlow(@ApiParam(name = "userId", value = "流程发起人ID", required = true) @PathVariable String userId,
			@ApiParam(name = "key", value = "流程Id", required = true) @PathVariable String key,
			@ApiParam(name = "variables", value = "流程变量", required = true) @RequestBody Map<String, Object> variables)
			throws Exception {
		// 获取当前流程实例ID
		// String processInstanceId = activitiUtils.getProcessInstance(userId,
		// key).getId();
		ProcessInstance pi = runtimeService.startProcessInstanceByKey(key, userId);
		// 查询第一个任务
		Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
		// 设置流程任务变量
		
		taskService.setVariables(task.getId(), variables);
		return task.getId();
	}

	/**
	 * 撤销流程
	 *
	 * @param taskId 流程任务ID
	 * @return
	 */
	@PostMapping(value = "/revocationTask/{taskId}")
	public String revocationTask(@PathVariable String taskId) throws Exception {
		int i = flowTaskService.revocationTask(taskId);
		if (i == 1) {
			return "撤销成功";
		} else {
			return "撤销失败";
		}
	}

	
	@ApiOperation(value = "任务进行时审批进度", notes = "任务进行时审批进度")
	@PostMapping("/selTaskVar")
	public String selTaskVar(@ApiParam(name = "proInstanId", value = "流程实例Id", required = true)@RequestParam String proInstanId) {
		// 查出已经办理和正在办理的任务人
		List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
				.processInstanceId(proInstanId).orderByHistoricActivityInstanceStartTime().desc().list();
		JSONArray res = new JSONArray();
		HistoricProcessInstance result = historyService.createHistoricProcessInstanceQuery()
				.processInstanceId(proInstanId).singleResult();
		// 任务变量--意见
		List<Comment> list2 = taskService.getProcessInstanceComments(result.getId());
		JSONObject comments = new JSONObject();
		for (Comment comment : list2) {
			comments.put(comment.getTaskId(), comment);
		}
		for (HistoricActivityInstance h : list) {
			JSONObject data = new JSONObject();
			if (h.getActivityType().equals("startEvent")) {
				data.put("发起人：", result.getStartUserId());
				data.put("发起时间：", DateUtils.times(result.getStartTime()));
				data.put("审批意见：", "发起流程");
				data.put("状态", "提交");
			}
			if (h.getActivityType().equals("userTask")) {
				if (comments.get(h.getTaskId())==null) {
					data.put("待执行人", h.getAssignee());
					data.put("执行人：", null);
					data.put("审批时间：", null);
					data.put("审批意见：", null);
					data.put("状态", "待处理");
				} else {
					
					Comment taskComment=((Comment)comments.get(h.getTaskId()));
					data.put("执行人：", h.getAssignee());
					data.put("审批时间：", DateUtils.times(taskComment.getTime()));
					data.put("审批意见：", taskComment.getFullMessage());
					data.put("状态", taskComment.getType());
				}
				
			}
			if (h.getActivityType().equals("endEvent")) {
				continue;
			}
			res.add(data);
		}
		return res.toJSONString();
	}
	
	/**
	 * 驳回请求返回上层所有节点人员信息
	 * @param taskId 任务Id
	 * @return
	 */
	@ApiOperation(value = "驳回请求返回上层所有节点人员信息", notes = "驳回请求返回上层所有节点人员信息")
	@PostMapping("/rejectedPlo")
	public String rejectedPlo(@ApiParam(name = "proinstanId", value = "流程实例Id", required = true)@RequestParam String proinstanId,
			@ApiParam(name = "userId", value = "当前用户Id", required = true)@RequestParam String userId) {
		//查询出当前任务的任务信息
	     
	       JSONArray result = new JSONArray();
	       List<IdentityLink> list = runtimeService.getIdentityLinksForProcessInstance(proinstanId);
	        for (IdentityLink identityLink : list) {
	        	  
	        	if(!identityLink.getUserId().equals(userId)) {
	        		JSONObject res = new JSONObject();
	        		res.put("流程实例Id", identityLink.getProcessInstanceId());
	        		res.put("节点人员类型", identityLink.getType());
	        		res.put("节点人员", identityLink.getUserId());
	        		result.add(res);
	        	}
	        	
			}
	        return result.toJSONString();
	}
	@ApiOperation(value = "驳回请求", notes = "驳回请求")
	@PostMapping("/rejectedNode")
	public String rejectedNode(@ApiParam(name = "userId", value = "驳回人员名称", required = true)@RequestParam String userId,
			@ApiParam(name = "cause", value = "驳货原因", required = true)@RequestParam String cause,
			@ApiParam(name = "taskId", value = "当前任务id'", required = true)@RequestParam String taskId) {
        //当前任务
        Task currentTask = taskService.createTaskQuery().taskId(taskId).singleResult();
        //获取流程定义
        Process process = repositoryService.getBpmnModel(currentTask.getProcessDefinitionId()).getMainProcess();
        //获取目标节点定义
        taskService.addComment(currentTask.getId(), currentTask.getProcessInstanceId(),"驳回", cause);
        
        List<HistoricActivityInstance> list = historyService
		 .createHistoricActivityInstanceQuery()
		 .taskAssignee(userId)
		 .processInstanceId(currentTask.getProcessInstanceId())
		.list();
      
        HistoricActivityInstance result = list.get(0);
        managementService.executeCommand(new Jump2TargetFlowNodeCommand(currentTask.getId(), result.getActivityId()));
	return null;
	}
}