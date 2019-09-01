package com.yzs.activitymaster.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Configuration
public class FeignConfig implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate requestTemplate) {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if(attributes != null) {
	        HttpServletRequest request = attributes.getRequest();
	        Enumeration<String> headerNames = request.getHeaderNames();
	        if (headerNames != null) {
	        	boolean b = false;
	            while (headerNames.hasMoreElements()) {
	                String name = headerNames.nextElement();
	                if("token".equals(name)) {
	                	b = true;
	                }
	                String values = request.getHeader(name);
	                requestTemplate.header(name, values);
	            }
	            if(!b) {
	                requestTemplate.header("token", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6NDYxMTY4NzU3MDcxMDU3NiwiY3JlYXRlZCI6MTU1MjI4MzE4ODExOSwiYXV0aG9yaXRpZXMiOlt7ImF1dGhvcml0eSI6InN5czp1c2VyOnZpZXcifSx7ImF1dGhvcml0eSI6InN5czptZW51OmRlbGV0ZSJ9LHsiYXV0aG9yaXR5Ijoic3lzOmRlcHQ6ZWRpdCJ9LHsiYXV0aG9yaXR5IjoiZGljdDpjb21wYW55OmFkZCJ9LHsiYXV0aG9yaXR5Ijoic3lzOmRpY3Q6ZWRpdCJ9LHsiYXV0aG9yaXR5Ijoic3lzOmRpY3Q6ZGVsZXRlIn0seyJhdXRob3JpdHkiOiJzeXM6bWVudTphZGQifSx7ImF1dGhvcml0eSI6InN5czp1c2VyOmFkZCJ9LHsiYXV0aG9yaXR5IjoiZGljdDpjb21wYW55OnZpZXcifSx7ImF1dGhvcml0eSI6InN5czpsb2c6dmlldyJ9LHsiYXV0aG9yaXR5Ijoic3lzOmRlcHQ6ZGVsZXRlIn0seyJhdXRob3JpdHkiOiJzeXM6cm9sZTplZGl0In0seyJhdXRob3JpdHkiOiJzeXM6cm9sZTp2aWV3In0seyJhdXRob3JpdHkiOiJkaWN0OmNvbXBhbnk6ZWRpdCJ9LHsiYXV0aG9yaXR5Ijoic3lzOmRpY3Q6dmlldyJ9LHsiYXV0aG9yaXR5Ijoic3lzOnVzZXI6ZWRpdCJ9LHsiYXV0aG9yaXR5Ijoic3lzOnVzZXI6ZGVsZXRlIn0seyJhdXRob3JpdHkiOiJzeXM6ZGVwdDp2aWV3In0seyJhdXRob3JpdHkiOiJzeXM6ZGVwdDphZGQifSx7ImF1dGhvcml0eSI6InN5czpyb2xlOmRlbGV0ZSJ9LHsiYXV0aG9yaXR5Ijoic3lzOm1lbnU6dmlldyJ9LHsiYXV0aG9yaXR5Ijoic3lzOm1lbnU6ZWRpdCJ9LHsiYXV0aG9yaXR5Ijoic3lzOmRpY3Q6YWRkIn0seyJhdXRob3JpdHkiOiJzeXM6cm9sZTphZGQifV19.zqeykmLqQRADkfKhQSqltd_q0llDoRfNH4FUYwcdFkWk5KbuWT9e90wZQjYMZldBDtC_XtzyvPHbsHpPJfQHSg");
	            }
	        }
		}else {
			requestTemplate.header("token", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6NDYxMTY4NzU3MDcxMDU3NiwiY3JlYXRlZCI6MTU1MjI4MzE4ODExOSwiYXV0aG9yaXRpZXMiOlt7ImF1dGhvcml0eSI6InN5czp1c2VyOnZpZXcifSx7ImF1dGhvcml0eSI6InN5czptZW51OmRlbGV0ZSJ9LHsiYXV0aG9yaXR5Ijoic3lzOmRlcHQ6ZWRpdCJ9LHsiYXV0aG9yaXR5IjoiZGljdDpjb21wYW55OmFkZCJ9LHsiYXV0aG9yaXR5Ijoic3lzOmRpY3Q6ZWRpdCJ9LHsiYXV0aG9yaXR5Ijoic3lzOmRpY3Q6ZGVsZXRlIn0seyJhdXRob3JpdHkiOiJzeXM6bWVudTphZGQifSx7ImF1dGhvcml0eSI6InN5czp1c2VyOmFkZCJ9LHsiYXV0aG9yaXR5IjoiZGljdDpjb21wYW55OnZpZXcifSx7ImF1dGhvcml0eSI6InN5czpsb2c6dmlldyJ9LHsiYXV0aG9yaXR5Ijoic3lzOmRlcHQ6ZGVsZXRlIn0seyJhdXRob3JpdHkiOiJzeXM6cm9sZTplZGl0In0seyJhdXRob3JpdHkiOiJzeXM6cm9sZTp2aWV3In0seyJhdXRob3JpdHkiOiJkaWN0OmNvbXBhbnk6ZWRpdCJ9LHsiYXV0aG9yaXR5Ijoic3lzOmRpY3Q6dmlldyJ9LHsiYXV0aG9yaXR5Ijoic3lzOnVzZXI6ZWRpdCJ9LHsiYXV0aG9yaXR5Ijoic3lzOnVzZXI6ZGVsZXRlIn0seyJhdXRob3JpdHkiOiJzeXM6ZGVwdDp2aWV3In0seyJhdXRob3JpdHkiOiJzeXM6ZGVwdDphZGQifSx7ImF1dGhvcml0eSI6InN5czpyb2xlOmRlbGV0ZSJ9LHsiYXV0aG9yaXR5Ijoic3lzOm1lbnU6dmlldyJ9LHsiYXV0aG9yaXR5Ijoic3lzOm1lbnU6ZWRpdCJ9LHsiYXV0aG9yaXR5Ijoic3lzOmRpY3Q6YWRkIn0seyJhdXRob3JpdHkiOiJzeXM6cm9sZTphZGQifV19.zqeykmLqQRADkfKhQSqltd_q0llDoRfNH4FUYwcdFkWk5KbuWT9e90wZQjYMZldBDtC_XtzyvPHbsHpPJfQHSg");
		}
	}

}
