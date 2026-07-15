/*
* Generator
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.kernel.generators.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

import com.sysman.kernel.api.commons.util.ObjectUtility;
import com.sysman.kernel.api.commons.util.StringUtility;
import com.sysman.kernel.api.commons.util.enums.SignEnum;
import com.sysman.kernel.generators.beans.MatcherBean;
import com.sysman.kernel.generators.beans.SourceReceiver;
import com.sysman.kernel.generators.beans.TemplateService;
import com.sysman.kernel.generators.generators.WebServiceGeneration;
import com.sysman.kernel.templates.beans.DataPattern;
import com.sysman.kernel.templates.processors.StringProcessor;
import com.sysman.kernel.templates.util.cache.PropertyCache;
import com.sysman.kernel.templates.util.enums.PropertyEnum;
import com.sysman.kernel.templates.util.enums.StructureEnum;
import com.sysman.kernel.templates.util.enums.TemplateEnum;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Clase que realiza todo el tratamiento de patrones de busquedas,
 * coincidencias, extraccion y tratamiento de cadenas encontradas
 */
public class Generator {

	private static final Logger logger = Logger.getLogger(Generator.class);
	
	/* Permite extraer valores de acuerdo a patrones contenidos en el objeto MatcherBean y guarda las coincidencias encontras en el mismo objeto */
	private static void fetchValue(MatcherBean mb) {
		Matcher m ;
		String objectKey = mb.getObjectKey();
		String value = null;

		DataPattern dp = PropertyCache.getInstance().getPattern(PropertyEnum.GEN_PATTERNS.getValue(), objectKey);
		
		if (ObjectUtility.isObjecNotNullOrEmpty(dp, false)) { 
			Map<String, String> patternsMap = dp.getPatternsMap();
			if (ObjectUtility.isObjecNotNullOrEmpty(patternsMap, true)) {
				String str = mb.getContent();
				Set<String> keys = patternsMap.keySet();
				for (String key : keys) {
					m = StringProcessor.findPattern(patternsMap.get(key), str);
					if (m.find()) {
						str = m.group();
					} else {
						str = SignEnum.EMPTY.getValue(); 
					}
				}
				value = str;
			} else {
				m = StringProcessor.findPattern(dp.getReplace(), mb.getContent());
				if (m.find()) {
					value = m.group();
				}
			}
			
			if (!StringUtility.isNullOrEmpty(value) && value!=null) {
				mb.setValue(value.replace(SignEnum.TWO_POINTS.getValue(), SignEnum.EMPTY.getValue()).replace(SignEnum.SPACE.getValue(), SignEnum.EMPTY.getValue()).replace(SignEnum.SPECIAL_BACKSLASH_T.getValue(), SignEnum.EMPTY.getValue()).trim());
			}
		}
	}

	/* Basado en un contenido y un listado de patrones arma un arbol de objetos MatcherBean */
	private static List<MatcherBean> getMatch(String content, Collection<DataPattern> patterns) {
		List<MatcherBean> list = new ArrayList<>();
		for (DataPattern dp : patterns) {
			Matcher matcher = StringProcessor.findPattern(dp.getPattern(), content);
			while (matcher.find()) {
				MatcherBean mBean = new MatcherBean();
				mBean.setContent(matcher.group());
				mBean.setObjectKey(dp.getObjectKey());
				mBean.setPatternKey(dp.getPatternKey());
				mBean.setReplaceKey(dp.getReplaceKey());
				list.add(mBean);
			}
		}

		for (MatcherBean mb : list) {
			fetchValue(mb);
		}

		return list;
	}

	/**
	 * Permite construir todos los archivos de servicios web basados en la informacion 
	 * del objeto SourceReceiver y de configuracion misma.
	 */
	public static void build(SourceReceiver receiver) {

		/* Primer nivel de busqueda - Obtener los patrones principales y obtener las cadenas de coincidencia a traves de un objeto MatcherBean */
		Map<String, DataPattern> patterns = PropertyCache.getInstance().getPatterns(PropertyEnum.GEN_PATTERNS.getValue());
		List<MatcherBean> matcherBeanList = getMatch(receiver.getContent().toString(), patterns.values());

		/* Segundo nivel de busqueda - Obtener los patrones de parametros y con el resultado de la consulta anterior, obtener las cadenas de coincidencias de las subcadenas encontradas */
		patterns = PropertyCache.getInstance().getPatterns(PropertyEnum.GEN_PARAMS.getValue());
		logger.warn("================================== Parameters =============================");
		List<MatcherBean> matcherList = new ArrayList<>();
		for (MatcherBean mb : matcherBeanList) {
			List<MatcherBean> subList = getMatch(mb.getContent(), patterns.values());
			mb.setMatcherBeanList(subList);
			matcherList.addAll(subList);
		}
		/* Tercer nivel de busqueda - Obtener los patrones de parametros y con el resultado de la consulta anterior, obtener las cadenas de coincidencias de las subcadenas encontradas */
		logger.warn("================================== Match =============================");
		patterns = PropertyCache.getInstance().getPatterns(PropertyEnum.GEN_PARAM.getValue());
		for (MatcherBean mb : matcherList) {
			List<MatcherBean> subList = getMatch(mb.getContent(), patterns.values());
			mb.setMatcherBeanList(subList);
		}

		TemplateService ts = new TemplateService();
		ts.setTemplate(TemplateEnum.SERVICE.getValue());
		ts.setMatcherBeanList(matcherBeanList);
		WebServiceGeneration.generate(ts, receiver.getTargetPath());
	}
}
