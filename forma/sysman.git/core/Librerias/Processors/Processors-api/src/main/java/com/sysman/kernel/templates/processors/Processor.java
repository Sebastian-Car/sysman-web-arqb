/*
}* Generation
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.kernel.templates.processors;

import static com.sysman.kernel.templates.util.constants.GeneralConstant.REFACTORING;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import com.sysman.kernel.api.commons.util.enums.SignEnum;
import com.sysman.kernel.templates.annotations.Refactoring;
import com.sysman.kernel.templates.beans.DataPattern;
import com.sysman.kernel.templates.beans.Document;
import com.sysman.kernel.templates.beans.TemplateClass;
import com.sysman.kernel.templates.generators.EnumGeneration;
import com.sysman.kernel.templates.util.files.FileUtility;
import com.sysman.kernel.templates.util.cache.PropertyCache;
import com.sysman.kernel.templates.util.constants.GeneralConstant;
import com.sysman.kernel.templates.util.enums.PropertyEnum;
import com.sysman.kernel.templates.util.enums.TemplateTypeEnum;

import javax.tools.JavaFileObject;

import org.apache.log4j.Logger;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Clase encargada de recibir cada una de las peticiones de refactoring 
 * cuando es identificada una clase con un patrón de la misma.
 */
@SupportedAnnotationTypes(REFACTORING)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class Processor extends AbstractProcessor {
	
	private static final Logger logger = Logger.getLogger(Processor.class);
	
	/**
	 * Permite ser llamdado de forma automatica cuando la annotation de la
	 * clase REFACTORING es identificada en una clase Java. 
	 */
	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
		for (final Element element : roundEnv.getElementsAnnotatedWith(Refactoring.class)) {
			if (element instanceof TypeElement) {
				final TypeElement typeElement = (TypeElement) element;
				final PackageElement packageElement = (PackageElement) typeElement.getEnclosingElement();
				Document document = new Document();
				String path = packageElement.getQualifiedName() + "." + typeElement.getSimpleName();
				StringBuffer content = FileUtility.readFile(path);
				document.setFileContent(content);
				
				try {
					final JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(
							packageElement.getQualifiedName() + "." + typeElement.getSimpleName());

					try (Writer writter = fileObject.openWriter()) {
						StringProcessor.applyRecfactoring(content, typeElement.getSimpleName().toString(),
								typeElement.getSimpleName().toString());
						
						Map<String,DataPattern> patterns = PropertyCache.getInstance().getPatterns(PropertyEnum.PATTERNS.getValue());
						Collection<DataPattern> values = patterns.values();
						
						for (DataPattern pb : values) {
							document = StringProcessor.applyRecfactoring(packageElement.getQualifiedName().toString(), typeElement.getSimpleName().toString(), document, pb);
						}
						
						
					    if (!document.getParameter().getParams().isEmpty()) {
					    	TemplateClass cBean  = document.getParameter(); 
							cBean.setPack(cBean.getPack().concat(SignEnum.POINT.getValue()).concat(GeneralConstant.PACK_ENUMS));
							cBean.setType(TemplateTypeEnum.PARAMETER.getValue());
							EnumGeneration.generate(cBean);
						}	
					
						if (!document.getUrl().getUlrs().isEmpty()) {
							TemplateClass urlClassBean  = document.getUrl(); 
							urlClassBean.setPack(urlClassBean.getPack().concat(SignEnum.POINT.getValue()).concat(GeneralConstant.PACK_ENUMS));
							urlClassBean.setType(TemplateTypeEnum.URL.getValue());
							EnumGeneration.generate(urlClassBean);
						}	
						
						
						writter.append(document.getFileContent());
					}
				} catch (final IOException ex) {
					logger.error(ex);
					processingEnv.getMessager().printMessage(Kind.ERROR, ex.getMessage());
				}
			}
		}

		return true;
	}
}
