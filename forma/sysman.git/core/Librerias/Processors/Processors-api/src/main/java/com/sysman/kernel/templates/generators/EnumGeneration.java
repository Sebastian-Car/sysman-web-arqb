/*
* EnumGeneration
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.kernel.templates.generators;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.sysman.kernel.templates.beans.TemplateClass;
import com.sysman.kernel.templates.util.files.FileUtility;
import com.sysman.kernel.templates.util.constants.ExtensionConstant;
import com.sysman.kernel.templates.util.enums.ClassEnum;
import com.sysman.kernel.templates.util.enums.TemplateConfigEnum;
import com.sysman.kernel.templates.util.enums.TemplateTypeEnum;
import com.sysman.kernel.templates.util.enums.UrlClassEnum;

import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.commons.util.enums.SignEnum;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Clase que funciona como plantilla para construir los archivos enum.
 */
public class EnumGeneration {
	
	/**
	 * Recibe informacion de una plantilla y unos datos para generar un archivo enum u otro tipo 
	 */
	public static void generate(TemplateClass classBean) {
	
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty(TemplateConfigEnum.RESOURCE_LOADER.getKey(), TemplateConfigEnum.RESOURCE_LOADER.getValue());
		ve.setProperty(TemplateConfigEnum.CLASS_RESOURCE_LOADER.getKey(), TemplateConfigEnum.CLASS_RESOURCE_LOADER.getValue());
		ve.init();

		Template t = ve.getTemplate(classBean.getTemplate().concat(ExtensionConstant.VELOCITY_EXT));

		VelocityContext context = new VelocityContext();
		String className = null;
		if (TemplateTypeEnum.PARAMETER.getValue().equalsIgnoreCase(classBean.getType())) {
			// Parametros 
			ArrayList<String> paramList = new ArrayList<>();
			for (String param : classBean.getParams()) {
	        	paramList.add(param.toUpperCase());
	        }
			className = classBean.getClassName().concat(ClassEnum.DEFAULT_ENUM.getValue());
			context.put(ClassEnum.CLASS_NAME.getValue(), className);
			context.put(ClassEnum.PARAM_LIST.getValue(), paramList);
		}
		   
		if (TemplateTypeEnum.URL.getValue().equalsIgnoreCase(classBean.getType()) ) {
			// Url 
			ArrayList<UrlBean> paramList = new ArrayList<>();
			for (UrlBean cBean : classBean.getUlrs()) { 
	        	paramList.add(cBean);
	        }
			className = classBean.getClassName().concat(UrlClassEnum.DEFAULT_URL_ENUM.getValue());
			context.put(UrlClassEnum.BASE_CLASS_NAME.getValue(), classBean.getClassName());
			context.put(UrlClassEnum.CLASS_NAME.getValue(), className);
			context.put(UrlClassEnum.PARAM_LIST.getValue(), paramList);
		}        
		context.put(ClassEnum.PACKAGE.getValue(), classBean.getPack());
		Writer writer = new StringWriter();
		
		t.merge(context, writer);		
		FileUtility.createDirectory(classBean.getPack());		
		FileUtility.createFile(writer.toString(), classBean.getPack().concat(SignEnum.POINT.getValue()).concat(className));
	}
	
 }
