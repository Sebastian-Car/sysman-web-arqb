package com.sysman.kernel.api.clientwso2.connectors;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.sysman.kernel.api.commons.util.exceptions.SysmanException;

public class PropertiesConfigUtil {
	
	private Properties prop = new Properties();
	private InputStream input = null;
	private String valueProperties ;

	
	public String getValueFromConfigP(String key) throws SysmanException {
		
		valueProperties = Constans.EMPTYSTRG;
		try {
			
			String filename = "config.properties";
    		input = PropertiesConfigUtil.class.getClassLoader().getResourceAsStream(filename);    		

			// load a properties file
			prop.load(input);
			valueProperties = String.valueOf(prop.getProperty(key));
		} catch (IOException ex) {
			throw new SysmanException(ex.getMessage());
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					throw new SysmanException(e.getMessage());
				}
			}
		}
		
		return valueProperties ;
		
	}

}
