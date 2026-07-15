/*
* FileUtility
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.kernel.templates.util.cache;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.sysman.kernel.api.commons.util.enums.SignEnum;
import com.sysman.kernel.templates.Path;
import com.sysman.kernel.templates.processors.Processor;
import com.sysman.kernel.templates.util.constants.ExtensionConstant;
import com.sysman.kernel.templates.util.constants.GeneralConstant;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Clase utilitaria para manejo y tratamiento de archivos
 */ 
public class FileUtility {
	
	private static final Logger logger = Logger.getLogger(FileUtility.class);
	
	/**
	 * Permite obtener el contenido de un archivo
	 * @param path
	 * @return
	 */
	private static StringBuffer readContentFile(String path) {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		String sCurrentLine = null;
		FileReader fw = null ;
		try {
			fw = new FileReader(path) ;
			br = new BufferedReader(fw);
			while ((sCurrentLine = br.readLine()) != null) {
				sb.append(sCurrentLine);
				sb.append(System.getProperty(GeneralConstant.LINE_SEPARATOR));
			}
		} catch (IOException e) {
			logger.error("[StringBuffer] Error :"+ e.getMessage());
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (fw != null) {
					fw.close();
				}
			} catch (IOException ex) {
				logger.error("[StringBuffer] Error :"+ ex.getMessage());
			}
		}
		return sb;
	}

	/**
	 * Permite obtener el contenido de un archivo a traves de la ruta dle mismo.
	 * @param content
	 * @param path
	 */
	private static void createNewFile(String content, String path) {
		adminFile(content,path);
	}
	
	/**
	 * Permite modificar un archivo basado en su contenido y la ubicacion del mismo.
	 * @param content
	 * @param pathFile
	 */
	public static void modifyFile(String content, String pathFile) {
		adminFile(content,pathFile);
	}
	
	/**
	 * Permite administrar un archivo basado en su contenido y la ubicacion del mismo
	 * @param content
	 * @param pathFile
	 */
	private static void adminFile(String content, String pathFile) {
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			File file = new File(Path.HOME.getPath()  + convertPackageToRoot(pathFile) + ExtensionConstant.JAVA_EXT);
			logger.warn("FILE: " +Path.HOME.getPath()  + convertPackageToRoot(pathFile) + ExtensionConstant.JAVA_EXT);
			if (!file.exists()) {
				file.createNewFile();
			}
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write(content);
		} catch (IOException e) {
			logger.error("[StringBuffer] Error :"+ e.getMessage());
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
				if (fw != null) {
					fw.close();
				}
			} catch (IOException ex) {
				logger.error("[StringBuffer] Error :"+ ex.getMessage());
			}
		}
	}
	
	/**
	 * Permite cambiar los caracteres de punto por separadores de slash
	 * @param pack
	 * @return
	 */
	private static String convertPackageToRoot(String pack) {
		return pack.replace(SignEnum.POINT.getValue(), File.separator);
	}
	
	/**
	 * Permite modificar el nombre a un archivo pasado como argumento.
	 * @param pathFile
	 * @param replaceClassName
	 */
	public static void renameFile(String pathFile, String replaceClassName) {
		File currentFile = new File(Path.HOME.getPath() + convertPackageToRoot(pathFile) + ExtensionConstant.JAVA_EXT);
		File newFile = new File(Path.HOME.getPath()  + convertPackageToRoot(replaceClassName) + ExtensionConstant.BACKUP_EXT);
		currentFile.renameTo(newFile);
	}

	/**
	 * Permite obtener el contenido de un archivo basado en la ubicacion y el nombre del mismo.
	 * @param pathFile
	 */
	public static StringBuffer readFile(String pathFile) {
		return readContentFile(Path.HOME.getPath()  + convertPackageToRoot(pathFile) + ExtensionConstant.JAVA_EXT);
	}
	
	/**
	 * Permite crear un archivo basado en su contenido y la ubicacion del mismo.
	 * @param content
	 * @param path
	 */
	public static void createFile(String content, String path) {
		createNewFile(content, path);
	}
	
	/**
	 * Permite modificar un archivo basado en su contenido y la ubicacion del mismo.
	 * @param content
	 * @param pathFile
	 */
	public static void createDirectory(String pathDirectory) {
		File file = new File(Path.HOME.getPath()  + convertPackageToRoot(pathDirectory));
		file.mkdirs();
	}
	

}
