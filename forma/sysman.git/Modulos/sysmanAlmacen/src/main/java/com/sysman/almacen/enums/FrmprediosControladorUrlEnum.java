/*
* FrmprediosControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum FrmprediosControladorUrlEnum {
   
           	URL12999("FRMPREDIOSCONTROLADORURL12999","177001"),  
             	URL13489("FRMPREDIOSCONTROLADORURL13489","170001"),  
             	URL28285("FRMPREDIOSCONTROLADORURL28285","Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN, serviciosPredio,"),  
             	URL17668("FRMPREDIOSCONTROLADORURL17668","173001"),  
             	URL14446("FRMPREDIOSCONTROLADORURL14446","5003"),  
             	URL25142("FRMPREDIOSCONTROLADORURL25142","Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN, usosPredio,"),  
             	URL15059("FRMPREDIOSCONTROLADORURL15059","164001"),  
             	URL17335("FRMPREDIOSCONTROLADORURL17335","181001"),  
             	URL21564("FRMPREDIOSCONTROLADORURL21564","Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, usosPredio,"),  
             	URL24920("FRMPREDIOSCONTROLADORURL24920","Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, serviciosPredio,"),  
             	URL17036("FRMPREDIOSCONTROLADORURL17036","1002"),  
             	URL15529("FRMPREDIOSCONTROLADORURL15529","172001"),  
             	URL11040("FRMPREDIOSCONTROLADORURL11040"," listaSubprediousos = service .getListado(ConectorPool.ESQUEMA_SYSMAN, \"SELECT \" + \" USOS_PREDIO.COMPANIA,\" + \" USOS_PREDIO.ID_PREDIO, \" + \" USOS_PREDIO.CODIGO_USO,\" + \" USOS.DESCRIPCION\" + \" FROM USOS_PREDIO INNER JOIN USOS ON\" + \" USOS_PREDIO.COMPANIA = USOS.COMPANIA\" + \" AND USOS_PREDIO.CODIGO_USO = USOS.CODIGO_USO\" + \" WHERE USOS_PREDIO.COMPANIA = '\" + compania + \"' AND USOS_PREDIO.ID_PREDIO='\" + registro.getCampos().get(idPredio) + \"'\","),  
             	URL23623("FRMPREDIOSCONTROLADORURL23623","Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN, serviciosPredio,"),  
             	URL37546("FRMPREDIOSCONTROLADORURL37546"," List<Registro> rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN, \"SELECT SERIE_PLACA FROM PREDIOS WHERE COMPANIA='\" + compania + \"' AND SERIE_PLACA=\" + strCodigo + \"\");"),  
             	URL18082("FRMPREDIOSCONTROLADORURL18082","141050"),  
             	URL13922("FRMPREDIOSCONTROLADORURL13922","2001"),  
             	URL38109("FRMPREDIOSCONTROLADORURL38109"," List<Registro> rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN, \"SELECT DEVOLUTIVO.SERIE, DEVOLUTIVO.ELEMENTO, INVENTARIO.NOMBRELARGO, DEVOLUTIVO.DESCRIPCION, DEVOLUTIVO.RESPONSABLE \" + \" FROM (DEVOLUTIVO LEFT JOIN INVENTARIO ON \" + \" DEVOLUTIVO.ELEMENTO = INVENTARIO.CODIGOELEMENTO \" + \" AND DEVOLUTIVO.COMPANIA = INVENTARIO.COMPANIA) LEFT JOIN TIPOMOVIMIENTO ON \" + \" DEVOLUTIVO.COMPANIA = TIPOMOVIMIENTO.COMPANIA \" + \" AND DEVOLUTIVO.TIPOMOVIMIENTOI = TIPOMOVIMIENTO.CODIGO \" + \" WHERE DEVOLUTIVO.COMPANIA='\" + compania + \"' AND TIPOMOVIMIENTO.TIPOELEMENTO='N' AND DEVOLUTIVO.SERIE=\" + strCodigo + \" \");"),  
             	URL20409("FRMPREDIOSCONTROLADORURL20409","Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN, usosPredio,"),  
             	URL16003("FRMPREDIOSCONTROLADORURL16003","141047"),  
             	URL27048("FRMPREDIOSCONTROLADORURL27048"," <CODIGO_DESARROLLADO> Registro reg = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN, \"SELECT NVL(MAX(ANO),"),  
             	URL12449("FRMPREDIOSCONTROLADORURL12449"," listaSubpredioservicios = service .getListado(ConectorPool.ESQUEMA_SYSMAN, \"SELECT \" + \" SERVICIOS_PREDIO.COMPANIA,\" + \" SERVICIOS_PREDIO.ID_PREDIO, \" + \" SERVICIOS_PREDIO.CODIGO_SERVICIO,\" + \" SERVICIOS_PUBLICOS.NOMBRE\" + \" FROM SERVICIOS_PREDIO INNER JOIN SERVICIOS_PUBLICOS ON\" + \" SERVICIOS_PREDIO.COMPANIA = SERVICIOS_PUBLICOS.COMPANIA\" + \" AND SERVICIOS_PREDIO.CODIGO_SERVICIO = SERVICIOS_PUBLICOS.CODIGO_SERVICIO\" + \" WHERE SERVICIOS_PREDIO.COMPANIA = '\" + compania + \"' AND SERVICIOS_PREDIO.ID_PREDIO='\" + registro.getCampos().get(idPredio) + \"'\","),
             	URL16045("FRMPREDIOSCONTROLADORURL16045","152001"),
             	URL16046("FRMPREDIOSCONTROLADORURL16046","137019"),
             	URL16047("FRMPREDIOSCONTROLADORURL16047","136020"),
             	URL16048("FRMPREDIOSCONTROLADORURL16048","141054"),
             	URL16049("FRMPREDIOSCONTROLADORURL16049","112055"),
             	URL16050("FRMPREDIOSCONTROLADORURL16050","141055");
        	
	private final String key;
	private final String value;
	
	private  FrmprediosControladorUrlEnum(String key, String value) {
	    this.key   = key; 
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
}
