/*
* EnumGeneration
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.kernel.generators.generators;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.sysman.kernel.templates.processors.StringProcessor;
import com.sysman.kernel.templates.util.constants.ExtensionConstant;
import com.sysman.kernel.templates.util.enums.ClassEnum;
import com.sysman.kernel.templates.util.enums.StructureEnum;
import com.sysman.kernel.templates.util.enums.TemplateConfigEnum;
import com.sysman.kernel.templates.util.files.FileUtility;
import com.sysman.kernel.api.commons.util.ObjectUtility;
import com.sysman.kernel.api.commons.util.StringUtility;
import com.sysman.kernel.generators.beans.MatcherBean;
import com.sysman.kernel.generators.beans.TemplateService;
import com.sysman.kernel.generators.impl.Generator;
import com.sysman.kernel.generators.util.enums.ParamServiceEnum;
import com.sysman.kernel.generators.util.enums.ServiceEnum;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Clase que funciona como plantilla para construir los archivos enum.
 */
public class WebServiceGeneration {
	
	private static final Logger logger = Logger.getLogger(WebServiceGeneration.class);
	
	/**
	 * Recibe informacion de una plantilla y unos datos para generar un archivo enum u otro tipo 
	 */
	public static void generate(TemplateService templateService, String targetPath) {
		
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty(TemplateConfigEnum.RESOURCE_LOADER.getKey(), TemplateConfigEnum.RESOURCE_LOADER.getValue());
		ve.setProperty(TemplateConfigEnum.CLASS_RESOURCE_LOADER.getKey(), TemplateConfigEnum.CLASS_RESOURCE_LOADER.getValue());
		ve.init();

		Template t = ve.getTemplate(templateService.getTemplate().concat(ExtensionConstant.VELOCITY_EXT));

		VelocityContext context = new VelocityContext();
		if (ObjectUtility.isObjecNotNullOrEmpty(templateService, false)) {
			List<String> paramGetList    = new ArrayList<>();
			List<String> paramPostList   = new ArrayList<>();
			List<String> paramPutList    = new ArrayList<>();
			List<String> paramDeleteList = new ArrayList<>();
			for (MatcherBean mb : templateService.getMatcherBeanList()) {
				String content   =  mb.getContent();
				String objectKey =  mb.getObjectKey();
				if (!StringUtility.isNullOrEmpty(mb.getValue())) {
					if (StringProcessor.findPattern(StructureEnum.PL_SQL.getPack(),objectKey).find()) {
						context.put(ParamServiceEnum.CLASS_NAME.getValue(), mb.getValue());
					} else if (StringProcessor.findPattern(StructureEnum.PL_SQL.getMethod(),objectKey).find()) {
						if (StringProcessor.findPattern(ServiceEnum.GET.getValue(),content).find()) {
							paramGetList.add(mb.getValue());
						} if (StringProcessor.findPattern(ServiceEnum.POST.getValue(),content).find()) {
							paramPostList.add(mb.getValue());
						} else if (StringProcessor.findPattern(ServiceEnum.PUT.getValue(),content).find()) {
							paramPutList.add(mb.getValue());
						} else if (StringProcessor.findPattern(ServiceEnum.DELETE.getValue(),content).find()) {
							paramDeleteList.add(mb.getValue());
						}
					} 
				}
	        }
			context.put(ParamServiceEnum.PARAM_GET_SERVICE.getValue(), paramGetList);
			context.put(ParamServiceEnum.PARAM_POST_SERVICE.getValue(), paramPostList);
			context.put(ParamServiceEnum.PARAM_PUT_SERVICE.getValue(), paramPutList);
			context.put(ParamServiceEnum.PARAM_DELETE_SERVICE.getValue(), paramDeleteList);
		}        
		context.put(ParamServiceEnum.PACKAGE_NAME.getValue(), templateService.getPack());
		Writer writer = new StringWriter();
		t.merge(context, writer);
		logger.warn("==================================================================================");
		logger.warn(writer);
		//FileUtility.createDirectory(classBean.getPack());
		FileUtility.createPCFile(writer.toString(), targetPath);
	}
}
