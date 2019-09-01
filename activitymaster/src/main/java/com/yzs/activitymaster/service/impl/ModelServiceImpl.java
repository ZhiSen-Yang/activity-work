package com.yzs.activitymaster.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.yzs.activitymaster.service.ModelService;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.activiti.editor.constants.ModelDataJsonConstants.MODEL_DESCRIPTION;
import static org.activiti.editor.constants.ModelDataJsonConstants.MODEL_NAME;

/**
 * 流程模型Service实现类
 * <p>
 * 
 */
@Service
public class ModelServiceImpl implements ModelService {
	 private static final Logger log = LoggerFactory.getLogger(ModelServiceImpl.class);
	 private static final String SYMBOLS = "abcdefghijklmnopqrstuvwxyz"; 
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
	private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private HistoryService  historyService;
    @Override
    public void createModel(HttpServletRequest request, HttpServletResponse response) throws Exception{

        String modelName = "modelName";
        String modelKey = "modelKey";
        String description = "description";

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.put("stencilset", stencilSetNode);
        Model modelData = repositoryService.newModel();

        ObjectNode modelObjectNode = objectMapper.createObjectNode();
        modelObjectNode.put(MODEL_NAME, modelName);
        modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
        modelObjectNode.put(MODEL_DESCRIPTION, description);
        modelData.setMetaInfo(modelObjectNode.toString());
        modelData.setName(modelName);
        modelData.setKey(modelKey);
        //创建默认版本号0,保存后才是1
        modelData.setVersion(0);

        //保存模型
        repositoryService.saveModel(modelData);
        repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));
        response.sendRedirect(request.getContextPath() + "/modeler.html?modelId=" + modelData.getId());

    }

    /**
     * 获取流程模型,带分页
     * @param pageNum   当前页
     * @param pageSize  显示数量
     * @return
     * @throws Exception
     */
    @Override
    public List<Model> selectModel(int pageNum,int pageSize) throws Exception {
        pageNum = (pageNum - 1) * pageSize;
       // String selectModelSql = "SELECT * FROM act_re_model WHERE EDITOR_SOURCE_EXTRA_VALUE_ID_ !='' or EDITOR_SOURCE_EXTRA_VALUE_ID_ !=NULL";
        List<Model> modelList = repositoryService.createModelQuery().listPage(pageNum, pageSize);;
        if (modelList != null && modelList.size() > 0) {
            return modelList;
        }else {
            return null;
        }
    }

    /**
     * 保存流程模型
     *
     * @param modelId
     * @param name
     * @param description
     * @param json_xml
     * @param svg_xml
     * @return
     */
    @Override
    public int saveModel(String modelId, String name, String description, String json_xml, String svg_xml) throws Exception {
        // 获取流程模型
        Model model = repositoryService.getModel(modelId);

        ObjectNode modelJson = (ObjectNode) objectMapper.readTree(model.getMetaInfo());

        modelJson.put(MODEL_NAME, name);
        modelJson.put(MODEL_DESCRIPTION, description);
        model.setMetaInfo(modelJson.toString());
        model.setName(name);
        model.setVersion(model.getVersion()+1);

        repositoryService.saveModel(model);

        repositoryService.addModelEditorSource(model.getId(), json_xml.getBytes("utf-8"));

        InputStream svgStream = new ByteArrayInputStream(svg_xml.getBytes("utf-8"));
        TranscoderInput input = new TranscoderInput(svgStream);

        PNGTranscoder transcoder = new PNGTranscoder();
        // 设置输出
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        TranscoderOutput output = new TranscoderOutput(outStream);

        // 进行转换
        transcoder.transcode(input, output);
        final byte[] result = outStream.toByteArray();
        repositoryService.addModelEditorSourceExtra(model.getId(), result);
        outStream.close();
        return Integer.parseInt(modelId);
    }


    /**
     * 根据流程模型ID部署流程
     *
     * @param modelId
     * @throws Exception
     */
    @Override
    public int deployModel(String modelId) throws Exception {
        //数据库保存的是模型的元数据，不是XMl格式--需要将元数据转换为XML格式，再进行部署
        Model model = repositoryService.getModel(modelId);
        ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelId));
		
		 log.info(modelNode+"---modelNode---"); 
		 ObjectNode node =(ObjectNode)modelNode.get("properties");
		 node.put("process_id", findByName());
		 modelNode.put("properties", node); 
		 log.info(modelNode+"---modelNode---");
		 
        BpmnModel bpmnModel = new BpmnJsonConverter().convertToBpmnModel(modelNode);

        byte[] bytes = new BpmnXMLConverter().convertToXML(bpmnModel,"UTF-8");

        String processName = model.getName() + ".bpmn20.xml";
        log.info(processName+"-----processName---");
       
        //部署流程
        Deployment deployment = repositoryService.createDeployment().name(model.getName()).addString(
                processName, new String(bytes,"UTF-8")).deploy();
        //保存流程Id到model
        model.setDeploymentId(deployment.getId());
        repositoryService.saveModel(model);
        
        return 1;
    }
    //查询部署流程数据得key是否已经存在
    public String findByName() {
    	String key = getNonce_str();
    	List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().processDefinitionKey(key).list();
    	if(list.size() == 0) {
    		return key;
    	}else {
    		return findByName();
    	}
    }
    /**
     * 获取模型json数据
     *
     * @param modelId
     * @return
     * @throws Exception
     */
    @Override
    public ObjectNode getEditorJson(String modelId) throws Exception {
        ObjectNode modelNode = null;
        Model model = repositoryService.getModel(modelId);
        if (model != null) {
            if (StringUtils.isNotEmpty(model.getMetaInfo())) {
                modelNode = (ObjectNode) objectMapper.readTree(model.getMetaInfo());
            } else {
                modelNode = objectMapper.createObjectNode();
                modelNode.put("name", model.getName());
            }
            modelNode.put("modelId", model.getId());
            ObjectNode editorJsonNode = (ObjectNode) objectMapper.readTree(
                    new String(repositoryService.getModelEditorSource(model.getId()), "utf-8"));
            modelNode.put("model", editorJsonNode);

        }
        return modelNode;
    }
   

    	public static String getNonce_str() {
		char[] nonceChars = new char[7];
		for (int index = 0; index < nonceChars.length; ++index) {
			nonceChars[index] = SYMBOLS.charAt((int)(Math.random()*SYMBOLS.length()));
		}
		
		return new String(nonceChars);
	}
    	public static void main(String[] args) {
    		for (int i = 0; i < 10; i++) {
    			System.out.println(getNonce_str());
			}
			
		}

		@Override
		public String startProcess(String key,String businessKey ,Map<String, Object> map) {
	        map.put("businessKey", businessKey);
	        map.put("sex", "man");
	        map.put("url", "www.baidu.com");
	        map.put("createName", "八戒");
	        identityService.setAuthenticatedUserId("八戒");
	       // identityService.setUserInfo("悟空", key, "发起流程");
	       HistoricProcessInstance result = historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
	       if(result!=null) {
	    	   if(result.getEndTime() == null) {
	    		   return "当前业务流程还没有结束,不能重新发起！";
	    	   }
	    	  
	       }
	       ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery().processDefinitionKey(key);
			List<ProcessDefinition> list = query.list();
			String name = list.get(0).getName();
			
			 map.put("processName", name);
			 
	        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key,businessKey,map);
	        JSONObject data = new JSONObject();
			try {
				data.put("getBusinessKey", processInstance.getBusinessKey());
				data.put("id", processInstance.getId());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return data.toString();
		}

		@Override
		public String modelAll(int pageNum, int pageSize) {
			//List<Model> list = repositoryService.createModelQuery().list();
			List<Model> list = repositoryService.createModelQuery().listPage(pageNum-1, pageSize);
			 long count = repositoryService.createModelQuery().count();
			 JSONArray res = new JSONArray();
			for (Model model : list) {
				ProcessDefinition result=null;
				if(model.getDeploymentId()!= null) {
					result = repositoryService.createProcessDefinitionQuery().deploymentId(model.getDeploymentId()).singleResult();
				}
				com.alibaba.fastjson.JSONObject data = new com.alibaba.fastjson.JSONObject();
				data.put("视图模型主键：",model.getId());
				data.put("视图创建时间", model.getCreateTime());
				data.put("状态", model.getDeploymentId()==null?"未发布":"已发布");
				data.put("视图模型名称", model.getName());
				data.put("流程key", result==null?null:result.getKey());
				data.put("版本", model.getVersion());
				res.add(data);
			}
	       com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
	       json.put("totalPage", count);
	       res.add(json);
			return res.toJSONString();
		}

    
}
