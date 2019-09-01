package com.yzs.activitymaster.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.yzs.activitymaster.service.ModelService;
import com.yzs.activitymaster.util.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.task.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 
 * 流程实例历史
 *
 */
@Api(description = "流程历史控制类")
@RestController
@RequestMapping("/history")
public class HistoryController {
	@Autowired
	private HistoryService historyService;
	 @Autowired
	    private ModelService modelService;
	 @Autowired
	    private RepositoryService repositoryService;
	 @Autowired
	 private TaskService taskService;
	/**
	 * 分页查询历史流程实例
	 * 
	 * @param pageNum  当前页
	 * @param pageSize 每页数量
	 * @return
	 */
	@ApiOperation(value = "分页查询历史流程实例", notes = "分页查询历史流程实例")
	@PostMapping(value = "/procinst")
	public String procinst(@ApiParam(name = "pageNum", value = "当前页数", required = true) @RequestParam int pageNum,
			@ApiParam(name = "pageSize", value = "显示数量", required = true) @RequestParam int pageSize) {
		JSONArray res = new JSONArray();
		// 查询所有得流程实例历史记录，带实例流程变量
		List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery()
				.orderByProcessInstanceStartTime().desc().includeProcessVariables().listPage(pageNum, pageSize);
		long count = historyService.createHistoricProcessInstanceQuery()
		.orderByProcessInstanceStartTime().desc().includeProcessVariables().count();

		for (HistoricProcessInstance h : list) {
			JSONObject data = new JSONObject();
			data.put("流程Id", h.getId());
			data.put("实例标题", h.getStartUserId() + "在" + DateUtils.timeDay(h.getStartTime()) + "发起"
					+ h.getProcessVariables().get("processName"));
			data.put("流程名称", h.getProcessVariables().get("processName"));
			data.put("状态", h.getEndTime() == null ? "正运行" : "结束");
			data.put("实例创建时间", DateUtils.times(h.getStartTime()));
			data.put("实例结束时间", h.getEndTime() == null ? null : DateUtils.times(h.getEndTime()));
			data.put("持续时间", h.getEndTime() == null ? null : DateUtils.timeShort(h.getStartTime(), h.getEndTime()));
			
			res.add(data);
		}
		JSONObject datas = new JSONObject();
		datas.fluentPut("totalPage", count);
		return res.toJSONString();
	}
	
	/**
	 * 查看流程实例得详细信息
	 * @param processId
	 * @return
	 */
	@ApiOperation(value = "查看流程实例得详细信息", notes = "查看流程实例得详细信息")
	@PostMapping(value = "/procinstTask")
	public String procinstTask(@ApiParam(name = "processId", value = "流程实例Id", required = true) @RequestParam String processId) {
		//根据流程实例ID查询某个实例得详细信息
		HistoricProcessInstance h = historyService.createHistoricProcessInstanceQuery()
															.includeProcessVariables()
															.processInstanceId(processId)
															.singleResult();
		JSONObject data = new JSONObject();
		data.put("流程Id", h.getId());
		data.put("流程实例Id",h.getId());
		data.put("流程定义Id",h.getProcessDefinitionId());
		data.put("流程定义key",h.getProcessDefinitionKey());
		data.put("关联数据得业务主键", h.getBusinessKey());
		data.put("实例标题", h.getStartUserId() + "在" + DateUtils.timeDay(h.getStartTime()) + "发起"
				+ h.getProcessDefinitionName());
		data.put("流程名称", h.getProcessVariables().get("processName"));
		data.put("创建人", h.getStartUserId());
		data.put("创建时间", DateUtils.times(h.getStartTime()));
		data.put("结束时间", h.getEndTime() == null ? null : DateUtils.times(h.getEndTime()));
		data.put("审批用时", h.getEndTime() == null ? null : DateUtils.timeShort(h.getStartTime(), h.getEndTime()));
		return data.toJSONString();
	}
	
	/**
	 * 查看当前流程实例审批记录
	 * @return
	 */
	@ApiOperation(value = "查看当前流程实例审批记录", notes = "查看当前流程实例审批记录")
	@PostMapping("/taskHistory")
	public String taskHistory(@ApiParam(name = "processId", value = "流程实例Id", required = true) @RequestParam String processId) {
		//查看当前流程实例得审批记录
		List<HistoricActivityInstance> list = historyService
				.createHistoricActivityInstanceQuery()
				.processInstanceId(processId)
				.orderByHistoricActivityInstanceStartTime()
				.asc()
				.list();
		
		//查看当前流程实例记录信息
		HistoricProcessInstance result = historyService
				.createHistoricProcessInstanceQuery()
				.processInstanceId(processId)
				.includeProcessVariables()
				.singleResult();	
		JSONArray res = new JSONArray();
		// 任务变量--意见
		List<Comment> list2 = taskService.getProcessInstanceComments(result.getId());
		JSONObject comments = new JSONObject();
		for (Comment comment : list2) {
			comments.put(comment.getTaskId(), comment);
		}
		for (HistoricActivityInstance h : list) {
			//获取任务人的审批意见
			JSONObject data = new JSONObject();
			if(h.getActivityType().equals("startEvent")) {
				data.put("发起人：", result.getStartUserId());
				data.put("发起时间：",DateUtils.times(result.getStartTime()));
				data.put("审批意见：", "发起流程");
				data.put("状态", "提交");
			}
			if(h.getActivityType().equals("userTask")) {
				//任务正常执行状态
				if(comments.get(h.getTaskId())==null) {
					data.put("待执行人", h.getAssignee());
					data.put("执行人：", null);
					data.put("审批时间：",null);
					data.put("审批意见：", null);
					data.put("状态", "待处理");
				}else {
					Comment taskComment=((Comment)comments.get(h.getTaskId()));
					data.put("执行人：", h.getAssignee());
					data.put("审批时间：", DateUtils.times(taskComment.getTime()));
					data.put("审批意见：", taskComment.getFullMessage());
					data.put("状态", taskComment.getType());
					}
				
			}
			
			if(h.getActivityType().equals("endEvent")) {
				continue;
			}
			data.put("任务创建时间/更新时间", DateUtils.times(h.getEndTime()==null?h.getStartTime():h.getEndTime()));
			res.add(data);
		}
		return res.toJSONString();
	}
	
	@ApiOperation(value = "删除流程实例", notes = "删除流程实例")
	@PostMapping("/delHisProcess")
	public void delHisProcess(@ApiParam(name = "id", value = "流程实例Id", required = true)@RequestParam String id) {
		System.out.println(id);
		historyService.deleteHistoricProcessInstance(id);
	}
}
