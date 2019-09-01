package com.yzs.activitymaster.controller.model;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.yzs.activitymaster.service.ModelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 流程模型Controller
 * <p>
 * 
 */
@Api(description = "编辑器控制类")
@RestController
@RequestMapping(value = "/model")
public class ModelController {

    @Autowired
    private ModelService modelService;
    @Autowired
    private RepositoryService repositoryService;

    private static final Logger log = LoggerFactory.getLogger(ModelController.class);
    /**
     * 创建流程模型
     * @param request
     * @param response
     * @throws Exception
     */
    @ApiOperation(value = "创建流程模型" ,  notes="创建流程模型")
    @GetMapping("/create")
    public void createModel(HttpServletRequest request, HttpServletResponse response) throws Exception{
        modelService.createModel(request,response);
    }
    @GetMapping("/test")
    public String test(@RequestParam(value = "key") String key, @RequestParam(value = "businessKey") String businessKey) throws Exception{
     log.info("feign进来了");
       return "测试feign";
    }

    /**
     * 编辑查看模型
     * @param modelId
     * @return
     * @throws Exception
     */
    @GetMapping(value="/{modelId}/json", produces = "application/json")
    public ObjectNode getEditorJson(@PathVariable String modelId) throws Exception {
        return modelService.getEditorJson(modelId);
    }

    /**
     * 获取流程模型,带分页
     * @param pageNum   当前页
     * @param pageSize
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "分页获取流程模型" ,  notes="分页获取流程模型")
    @GetMapping(value = "/selectModel/{pageNum}/{pageSize}")
    public List<Model> selectModel(@ApiParam(name="pageNum",value="当前页", required = true)@PathVariable int pageNum,
    		@ApiParam(name="pageSize",value="分页数量", required = true)@PathVariable int pageSize) throws Exception{
    	List<Model> list = modelService.selectModel(pageNum,pageSize);
    	return list;
    }

    /**
     * 保存流程模型
     * @param modelId
     * @param name
     * @param description
     * @param json_xml
     * @param svg_xml
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "保存流程模型" ,  notes="保存流程模型")
    @PutMapping(value="/{modelId}/save")
    @ResponseStatus(value = HttpStatus.OK)
    public int saveModel(@ApiParam(name="modelId",value="模型主键", required = true)@PathVariable String modelId, String name, String description, String json_xml, String svg_xml) throws Exception {
    	log.info(name);
    	return modelService.saveModel(modelId,name,description,json_xml,svg_xml);
    }

    /**
     * 部署流程模型
     * @param modelId
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "部署流程" ,  notes="部署流程")
    @GetMapping(value = "/deployModel")
    public int deployModel(@ApiParam(name="modelId",value="流程模型主键", required = true)@RequestParam String modelId) throws Exception {
        return modelService.deployModel(modelId);
    }
    //此删除会级联删除，建议只有超级管理员权限可以使用
    //ACT_RE_DEPLOYMENT流程表 id主键
    @ApiOperation(value = "删除流程" ,  notes="删除流程")
    @PostMapping("/delete")
    public String deleteModel(@ApiParam(name="modelId",value="流程主键", required = true)@RequestParam String modelId) {
    	 repositoryService.deleteDeployment(modelId,true);
    	return "删除成功！";
    }
    @ApiOperation(value = "启动流程" ,  notes="启动流程")
    @PostMapping("/startProcess")
    public String startProcess(@ApiParam(name="key",value="流程实例key", required = true)@RequestParam String key,
    		@ApiParam(name="businessKey",value="业务主键key", required = true)@RequestParam String businessKey,
    		@ApiParam(name="map",value="流程变量", required = true)@RequestBody Map<String, Object> map) {
    	String res=modelService.startProcess(key,businessKey,map);
    	return res;
    }
    /**
     * 查看视图模型列表
     * @param pageNum 当前页
     * @param pageSize 每页数量
     * @return
     */
    @ApiOperation(value = "查看视图模型列表" ,  notes="查看视图模型列表")
    @PostMapping("/modelAll")
    public String modelAll(@ApiParam(name="pageNum",value="当前页", required = true)@RequestParam int pageNum,
    		@ApiParam(name="pageSize",value="每页数量", required = true)@RequestParam int pageSize) {
    	String res = modelService.modelAll(pageNum, pageSize);
    	return res;
    }
    
}
