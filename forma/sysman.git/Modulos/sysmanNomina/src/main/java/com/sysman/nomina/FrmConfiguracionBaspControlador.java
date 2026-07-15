/*-
 * FrmConfiguracionBaspControlador.java
 *
 * 1.0
 * 
 * 31/03/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.nomina.enums.FrmConfiguracionBaspControladorUrlEnum;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 31/03/2026
 * @author jmillan
 */
@ManagedBean
@ViewScoped
public class FrmConfiguracionBaspControlador extends BeanBaseContinuoAcmeImpl{

    private final String compania ; 
    private Registro registroOriginal;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Crea una nueva instancia de FrmConfiguracionBaspControlador
     */
	public FrmConfiguracionBaspControlador() {
		super();
	    	compania = SessionUtil.getCompania();
 try {
			//2574;
			
			numFormulario = GeneralCodigoFormaEnum.FRM_CONFIGURACIONBASP_CONTROLADOR
                    .getCodigo();
  validarPermisos();
//<INI_ADICIONAL>
//</INI_ADICIONAL>
		 } catch (Exception ex) {
logger.error(ex.getMessage(),ex);
            SessionUtil.redireccionarMenuPermisos();
        } finally {
        }
    }

    @PostConstruct
    public void inicializar(){

    	enumBase = GenericUrlEnum.CONFIG_PORC_BASP;
        reasignarOrigen();
        registro = new Registro();
        abrirFormulario();
	}

    @Override
    public void reasignarOrigen() {

        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }
	  @Override
public void abrirFormulario(){
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }

@Override
    public boolean insertarAntes(){
         //<CODIGO_DESARROLLADO>	 
	if (!validarFechas()) return false;

    registro.getCampos().put("COMPANIA", compania);
    
    String anoStr =  registro.getCampos().get("ANO").toString();
    java.util.Date fechaInicio = (java.util.Date) registro.getCampos().get("INICIO_VIGENCIA");
    java.util.Date fechaFin = (java.util.Date) registro.getCampos().get("FINAL_VIGENCIA");
    
	Map<String, Object> paramr = new TreeMap<>();
	paramr.put("UN_COMPANIA", compania);
	paramr.put("UN_ANO", anoStr);
	paramr.put("INICIO_VIGENCIA", fechaInicio);
	paramr.put("FINAL_VIGENCIA", fechaFin);
	
	try {
		Registro rs1 = RegistroConverter
				.toRegistro(
						requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(FrmConfiguracionBaspControladorUrlEnum.URL003.getValue())
										.getUrl(),
								paramr));

		if (Integer.parseInt(SysmanFunciones.nvl(rs1.getCampos().get("EXISTE"), "0").toString()) > 0) {
			JsfUtil.agregarMensajeError("Ya existe un rango de fechas para la vigencia");
			return false;

		}
		
		 return true;
		 
	} catch (Exception e) {
    	JsfUtil.agregarMensajeError(e.toString());
        return false;
    }
		 
        //</CODIGO_DESARROLLADO>
    }

private boolean validarFechas() {
    try {
    	 	String anoStr =  registro.getCampos().get("ANO").toString();
	        java.util.Date fechaInicio = (java.util.Date) registro.getCampos().get("INICIO_VIGENCIA");
	        java.util.Date fechaFin = (java.util.Date) registro.getCampos().get("FINAL_VIGENCIA");

	        if (anoStr == null || fechaInicio == null || fechaFin == null) {
	            JsfUtil.agregarMensajeError("Todos los campos de fecha y año son obligatorios.");
	            return false;
	        }

	        int anoVal = Integer.parseInt(anoStr);
	        java.util.Calendar cal = java.util.Calendar.getInstance();

	        if (fechaInicio.after(fechaFin)) {
	            JsfUtil.agregarMensajeError("La fecha inicial no puede ser mayor a la fecha final.");
	            return false;
	        }

	        cal.setTime(fechaInicio);
	        int anoInicio = cal.get(java.util.Calendar.YEAR);
	        
	        cal.setTime(fechaFin);
	        int anoFin = cal.get(java.util.Calendar.YEAR);

	        if (anoInicio != anoVal || anoFin != anoVal) {
	            JsfUtil.agregarMensajeError("Las fechas de vigencia deben pertenecer al año " + anoVal);
	            return false;
	        }

        return true;
    } catch (Exception e) {
    	JsfUtil.agregarMensajeError(e.toString());
        return false;
    }
}

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean insertarDespues(){
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
		return true;
    }
    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
@Override
    public boolean actualizarAntes(){
	    if (!validarFechas()) {
	        return false;
	    }
	    if (registroOriginal != null) {
	        registro.getCampos().put("KEY_COMPANIA", compania);
	        registro.getCampos().put("KEY_ANO", registroOriginal.getCampos().get("ANO"));
	        registro.getCampos().put("KEY_INICIO_VIGENCIA", registroOriginal.getCampos().get("INICIO_VIGENCIA"));
	        registro.getCampos().put("KEY_FINAL_VIGENCIA", registroOriginal.getCampos().get("FINAL_VIGENCIA"));
	    }
	    return true;
    }
    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion
     * del registro
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean actualizarDespues(){
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
		return true;
    }
    /**
     * Metodo ejecutado antes de realizar la eliminacion del
     * registro
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean eliminarAntes(){
    	registro.getCampos().put("COMPANIA", compania);
    	registro.getCampos().put("KEY_COMPANIA", compania);
        registro.getCampos().put("KEY_ANO", registro.getCampos().get("ANO"));
        registro.getCampos().put("KEY_INICIO_VIGENCIA", registro.getCampos().get("INICIO_VIGENCIA"));
        registro.getCampos().put("KEY_FINAL_VIGENCIA", registro.getCampos().get("FINAL_VIGENCIA"));
        
        registro.getLlave().put("KEY_COMPANIA", compania);
        registro.getLlave().put("KEY_ANO", registro.getCampos().get("ANO"));
        registro.getLlave().put("KEY_INICIO_VIGENCIA", registro.getCampos().get("INICIO_VIGENCIA"));
        registro.getLlave().put("KEY_FINAL_VIGENCIA", registro.getCampos().get("FINAL_VIGENCIA"));
        return true;
    }
    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean eliminarDespues(){
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
		return true;
    }
    
    @Override
    public void editar(RowEditEvent event) {
        this.registro = (Registro) event.getObject();
        super.editar(event); 
    }
    
    public void onRowEditInit(RowEditEvent event) {
        this.registroOriginal = new Registro();
        this.registroOriginal.setCampos(new HashMap<>(((Registro) event.getObject()).getCampos()));
    }

    
	@Override
	public void asignarValoresRegistro() {
		// TODO Auto-generated method stub
		if (registro != null && registro.getCampos() != null) {
	        registro.getCampos().put("COMPANIA", compania);
	    }
		
	}
	@Override
	public void removerCombos() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void cancelarEdicion(RowEditEvent event) {
		// TODO Auto-generated method stub
		
	}

}
