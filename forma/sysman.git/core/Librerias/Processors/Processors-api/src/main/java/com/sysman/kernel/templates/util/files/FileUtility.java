/*
* FileUtility
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.kernel.templates.util.files;

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
	
	/* Permite obtener el contenido de un archivo */
	private static StringBuffer readContentFile(String path) {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		String sCurrentLine = null;
		try {
			br = new BufferedReader(new FileReader(path));
			while ((sCurrentLine = br.readLine()) != null) {
				sb.append(sCurrentLine);
				sb.append(System.getProperty(GeneralConstant.LINE_SEPARATOR));
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException ex) {
				logger.error(ex.getMessage());
			}
		}
		return sb;
	}

	/*
	 * Permite obtener el contenido de un archivo a traves de la ruta del mismo.
	 */
	private static void createNewFile(String content, String path, boolean applicationContext) {
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			File file = null;
			if (applicationContext) {
				file = new File(Path.HOME.getPath() + convertPackageToRoot(path) + ExtensionConstant.JAVA_EXT);
				logger.warn("Context Application FILE: " + Path.HOME.getPath() + convertPackageToRoot(path) + ExtensionConstant.JAVA_EXT);
			} else {
				file = new File(path);
				logger.warn("Physical Path FILE: " +path);
			}
			
			if (!file.exists()) {
				file.createNewFile();
			}

			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write(content);

		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
				if (fw != null) {
					fw.close();
				}
			} catch (IOException ex) {
				logger.error(ex.getMessage());
			}
		}
	}

	/* Permite cambiar los caracteres de punto por separadores de slash */
	private static String convertPackageToRoot(String pack) {
		return pack.replace(SignEnum.POINT.getValue(), File.separator);
	}

	/**
	 * Permite modificar el nombre a un archivo pasado como argumento.
	 */
	public static void renameFile(String pathFile, String replaceClassName) {
		File currentFile = new File(Path.HOME.getPath() + convertPackageToRoot(pathFile) + ExtensionConstant.JAVA_EXT);
		File newFile = new File(
				Path.HOME.getPath() + convertPackageToRoot(replaceClassName) + ExtensionConstant.BACKUP_EXT);
		currentFile.renameTo(newFile);
	}

	/**
	 * Permite obtener el contenido de un archivo Java basado en el contexto de la aplicacion	 
	 * @param filePath
	 */
	public static StringBuffer readFile(String filePath) {
		return readContentFile(Path.HOME.getPath() + convertPackageToRoot(filePath) + ExtensionConstant.JAVA_EXT);
	}
	
	/**
	 * Permite obtener el contenido de un archivo basado en el path suministrado	 
	 * @param filePath
	 */
	public static StringBuffer readFileContent(String filePath) {
		return readContentFile(filePath);
	}

	/**
	 * Permite crear un archivo basado en su contenido y la ubicacion del mismo.	 
	 * @param content
	 * @param path
	 */
	public static void createFile(String content, String path) {
		createNewFile(content, path, true);
	}
	
	/**
	 * Permite crear un archivo basado en su contenido y la ubicacion especificada.	 
	 * @param content
	 * @param path
	 */
	public static void createPCFile(String content, String path) {
		createNewFile(content, path, false);
	}

	/**
	 * Permite modificar un archivo basado en su contenido y la ubicacion del
	 * mismo.
	 * 
	 * @param content
	 * @param pathFile
	 */
	public static void createDirectory(String pathDirectory) {
		File file = new File(Path.HOME.getPath() + convertPackageToRoot(pathDirectory));
		file.mkdirs();
	}

	/**
	 * Permite modificar un archivo basado en su contenido y la ubicacion del
	 * mismo.
	 * 
	 * @param content
	 * @param pathFile
	 */
	public static void modifyFile(String content, String pathFile) {
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			File file = new File(Path.HOME.getPath() + convertPackageToRoot(pathFile) + ExtensionConstant.JAVA_EXT);
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write(content);
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
				if (fw != null) {
					fw.close();
				}
			} catch (IOException ex) {
				logger.error(ex.getMessage());
			}
		}
	}

	/**
	 * Permite obtener los nombres de cada uno de los archivos de un directorio
	 */
	public static String[] getFilesNameDirectory(String directory) {
		File f = null;
		String[] paths = null;

		try {			
			// create new file
			f = new File(directory);
			// array of files and directory
			paths = f.list();
		} catch (Exception e) {			
			// if any error occurs
			logger.error(e.getMessage());
		}
		return paths;
	}
}
