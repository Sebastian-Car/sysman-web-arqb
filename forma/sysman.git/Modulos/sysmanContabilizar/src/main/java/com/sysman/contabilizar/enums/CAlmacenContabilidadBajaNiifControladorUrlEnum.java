package com.sysman.contabilizar.enums;

public enum CAlmacenContabilidadBajaNiifControladorUrlEnum {

		URL20888("CALMACENCONTABILIDADBAJACONTROLADORURL20888",
		            "183019"),
		
		URL14271("CALMACENCONTABILIDADBAJACONTROLADORURL14271",
		            "16118"),
		
		URL25587("CALMACENCONTABILIDADBAJACONTROLADORURL25587",
		            "183021"),
		
		URL11457("CALMACENCONTABILIDADBAJACONTROLADORURL11457",
		            "139019"),
		
		URL19121("CALMACENCONTABILIDADBAJACONTROLADORURL19121",
		            "183022"),
		
		URL28803("CALMACENCONTABILIDADBAJACONTROLADORURL28803",
		            "18300D")
		;
		
		private final String key;
		private final String value;
		
		private CAlmacenContabilidadBajaNiifControladorUrlEnum(String key,
		String value) {
		this.key = key;
		this.value = value;
		}
		
		public String getKey() {
		return key;
		}
		
		public String getValue() {
		return value;
}
	
}
