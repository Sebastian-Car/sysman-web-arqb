/*
 * StringProcessor
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.kernel.templates.processors;

import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.commons.util.enums.SignEnum;
import com.sysman.kernel.templates.beans.DataPattern;
import com.sysman.kernel.templates.beans.Document;
import com.sysman.kernel.templates.beans.TemplateClass;
import com.sysman.kernel.templates.util.cache.PropertyCache;
import com.sysman.kernel.templates.util.constants.GeneralConstant;
import com.sysman.kernel.templates.util.enums.ClassEnum;
import com.sysman.kernel.templates.util.enums.PatternEnum;
import com.sysman.kernel.templates.util.enums.PropertyEnum;
import com.sysman.kernel.templates.util.enums.UrlClassEnum;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Clase encargada de procesar el contenido String para realizar el
 * refactoring.
 */
public class StringProcessor {

    /*
     * Metodo que permite encontrar con un patron de busqueda la
     * coincidencia en una cadena.
     */
    public static Matcher findPattern(String pattern, String content) {
        Pattern p = Pattern.compile(pattern);
        return p.matcher(content);
    }

    /*
     * Metodo que permite basado en un patron de busqueda de
     * parametros encontrar una coincidencia y crear un nuevo mapa de
     * parametros, reemplazando el codigo anterior.
     */
    private static void buildParams(String input, TemplateClass cBean) {
        StringBuffer sb = new StringBuffer(input);

        Map<String, DataPattern> params = PropertyCache.getInstance()
                        .getPatterns(PropertyEnum.PARAMS.getValue());
        StringBuffer sBuffer = new StringBuffer();
        Set<String> paramList = cBean.getParams();
        for (DataPattern dp : params.values()) {
            Matcher matcher = findPattern(dp.getPattern(), sb.toString());
            while (matcher.find()) {
                if (sBuffer.length() == 0) {
                    sBuffer.append(dp.getReplace());
                }
                String value = matcher.group()
                                .replaceAll(SignEnum.SPECIAL_ADD.getValue(),
                                                SignEnum.EMPTY.getValue())
                                .trim();
                String paramString = dp.getParam();
                String param = ClassEnum.PARAM_NAME.getValue().toUpperCase()
                                .concat(Integer.toString(cBean.getCounter()));
                paramList.add(param);
                MessageFormat messageFormat = new MessageFormat(paramString);
                Object[] args = { cBean.getClassName()
                                .concat(ClassEnum.DEFAULT_ENUM.getValue())
                                .concat(SignEnum.POINT.getValue()).concat(param)
                                .concat(SignEnum.POINT.getValue())
                                .concat(GeneralConstant.GET_VALUE), value };
                sBuffer.append(messageFormat.format(args));
                cBean.increment();
            }
        }
        cBean.setParams(paramList);
        cBean.setContent(sBuffer);
    }

    /* Permite obtener el patron de inicio de busqueda de la cadena */
    private static String getReplaceHome(String pattern, String content) {
        Matcher m = findPattern(pattern, content);
        if (m.find()) {
            return m.group();
        }
        return null;
    }

    /**
     * Permite aplicar patron de refactoring en el contenido al nombre
     * del archivo
     */
    public static StringBuffer applyRecfactoring(StringBuffer content,
        String pattern, String replace) {
        Matcher m = findPattern(pattern, content.toString());
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, replace);
        }
        m.appendTail(sb);
        return sb;
    }

    /**
     * Permite aplciar patrones de refactoring al contenido del
     * archivos y a las cainicdencias encontradas para encontrar
     * parámetros de filtros que serán reemplazados por mapas de
     * objetos
     * 
     * @param pack
     * @param className
     * @param content
     * @param patternBean
     * @return
     */
    public static Document applyRecfactoring(String pack, String className,
        Document docummentRefactory, DataPattern patternBean) {
        Matcher m = findPattern(patternBean.getPattern(),
                        docummentRefactory.getFileContent().toString());
        TemplateClass cBean = new TemplateClass();
        cBean.setPack(pack);
        cBean.setClassName(className);
        cBean.setTemplate(ClassEnum.TEMPLATE.getValue());
        cBean.setParams(new HashSet<String>());
        TemplateClass urlClassBean = new TemplateClass();
        urlClassBean.setPack(pack);
        urlClassBean.setClassName(className);
        urlClassBean.setTemplate(UrlClassEnum.TEMPLATE.getValue());
        urlClassBean.setUlrs(new HashSet<UrlBean>());

        if (docummentRefactory.getParameter() == null) {
            docummentRefactory.setParameter(cBean);
        }

        if (docummentRefactory.getUrl() == null) {
            docummentRefactory.setUrl(urlClassBean);
        }

        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String home = null;// patternBean.getReplaceHome();
            if (patternBean.getPatternHome() != null) {
                String patternHome = patternBean.getPatternHome();
                home = getReplaceHome(patternHome, m.group());
            }
            MessageFormat messageFormat = null;
            buildParams(m.group(), cBean);
            messageFormat = new MessageFormat(patternBean.getReplace());
            String urlCode = UrlClassEnum.URL_ID.getValue()
                            .concat(Integer.toString(m.start())).toUpperCase();
            String identity = cBean.getClassName()
                            .concat(UrlClassEnum.DEFAULT_URL_ENUM.getValue())
                            .concat(".").concat(urlCode).concat(".")
                            .concat(GeneralConstant.GET_VALUE);
            Object[] args = { cBean.getContent().toString(), home, identity,
                              !cBean.getParams().isEmpty()
                                  ? ClassEnum.PARAM_NAME.getValue() : null };
            String value = messageFormat.format(args);
            m.appendReplacement(sb, value);
            String urlString = m.group()
                            .replaceAll(SignEnum.DOUBLE_QUOTES
                                            .getValue(),
                            SignEnum.SPECIAL_BACKSLASH.getValue().concat(
                                            SignEnum.DOUBLE_QUOTES.getValue()));
            urlString = urlString.replaceAll(
                            SignEnum.SPECIAL_BACKSLASH_N.getValue(),
                            SignEnum.EMPTY.getValue());
            urlString = urlString.replaceAll(
                            SignEnum.SPECIAL_BACKSLASH_T.getValue(),
                            SignEnum.EMPTY.getValue());
            urlString = urlString.replaceAll(
                            SignEnum.SPECIAL_BACKSLASH_R.getValue(),
                            SignEnum.EMPTY.getValue());
            urlString = urlString.replaceAll(
                            SignEnum.SPACE.getValue().concat("+"),
                            SignEnum.SPACE.getValue());
            if (findPattern(PatternEnum.REPLACEMENT.getUrl(),
                            patternBean.getPatternKey()).find()) {
                urlClassBean.getUlrs().add(
                                new UrlBean(urlCode, urlString, null, null));
            }
        }
        m.appendTail(sb);

        docummentRefactory.setFileContent(sb);

        if (!cBean.getParams().isEmpty()) {
            if (docummentRefactory.getParameter().getParams() != null) {
                docummentRefactory.getParameter().getParams()
                                .addAll(cBean.getParams());
            }
            else {
                Set<String> params = new HashSet<String>();
                docummentRefactory.getParameter().setParams(params);
                docummentRefactory.getParameter().getParams()
                                .addAll(cBean.getParams());
            }
        }

        if (!urlClassBean.getUlrs().isEmpty()) {
            if (docummentRefactory.getUrl().getUlrs() != null) {
                docummentRefactory.getUrl().getUlrs()
                                .addAll(urlClassBean.getUlrs());
            }
            else {
                Set<UrlBean> urls = new HashSet<UrlBean>();
                docummentRefactory.getParameter().setUlrs(urls);
                docummentRefactory.getUrl().getUlrs()
                                .addAll(urlClassBean.getUlrs());
            }
        }

        return docummentRefactory;
    }
}
