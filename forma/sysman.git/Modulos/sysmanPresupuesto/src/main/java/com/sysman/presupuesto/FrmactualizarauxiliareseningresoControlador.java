/*-
 * FrmactualizarauxiliareseningresoControlador.java
 *
 * 1.0
 * 
 * 07/06/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.event.SelectEvent;

import com.sysman.presupuesto.ejb.EjbPresupuestoCuatroRemote;
import com.sysman.presupuesto.enums.FrmactualizarauxiliareseningresoControladorUrlEnum;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import java.math.BigInteger;



//import com.sysman.presupuesto.ejb.EjbPresupuestoCuatroLocal;
/**
 * @version 1.0, 07/06/2023
 * @author jmillan
 */

@ManagedBean(name="frmactualizarauxiliareseningresoControlador")
@ViewScoped
public class  FrmactualizarauxiliareseningresoControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
//<DECLARAR_ATRIBUTOS>
private String anio;
private String RUBRO;
private String FUENTEINICIAL;
private String FUENTEFINAL;
private String AUXILIARINICIAL;
private String AUXILIARFINAL;
private String CENTROCOSTOINICIAL;
private String CENTROCOSTOFINAL;
private String REFERENCIAINICIAL;
private String REFERENCIAFINAL;


private String fuenteIniPR;
private String fuenteFinPR;
private String auxIniPr;
private String auxFinPr;
private String centroCosIniPR;
private String CentroCosFinPR;
private String RefeIniPR;
private String RefeFinPR;

private String manAux;
private String manRef;
private String manCto;
private String manFue;

private boolean manAuxFlag = true;
private boolean manRefFlag = true;
private boolean manCtoFlag = true;
private boolean manFueFlag = true;

private boolean manAuxFlagF = true;
private boolean manRefFlagF = true;
private boolean manCtoFlagF = true;
private boolean manFueFlagF = true;

private boolean auxFinalFlag = false;
private boolean refFinalFlag = false;
private boolean ctoFinalFlagF = false;
private boolean fueFinalFlagF = false;

private StreamedContent archivoDescarga;

private List<Registro> listaAno;

 private RegistroDataModelImpl listaRubro;

 private RegistroDataModelImpl listaFuenteInicial;

 private RegistroDataModelImpl listaFuenteFinal;

 private RegistroDataModelImpl listaAuxiliarInicial;

 private RegistroDataModelImpl listaAuxiliarFinal;

 private RegistroDataModelImpl listaCentroCtoInicial;

 private RegistroDataModelImpl listaCentroCtoFinal;

 private RegistroDataModelImpl listaReferenciaIni;

 private RegistroDataModelImpl listaReferenciaFin;
 
	@EJB
	private EjbPresupuestoCuatroRemote ejbPresupuestoCuatro;

    /**
     * Crea una nueva instancia de FrmactualizarauxiliareseningresoControlador
     */
    public FrmactualizarauxiliareseningresoControlador() {
  super();
            compania = SessionUtil.getCompania();
        try {
        	numFormulario= GeneralCodigoFormaEnum.FRM_ACT_AUX_EN_INGRESO_CONTROLADOR
            .getCodigo();
            //validarPermisos();

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            SessionUtil.redireccionarMenuPermisos();
        }finally{
        }
    }

    @PostConstruct
    public void inicializar(){
//<CARGAR_LISTA>
    	
    	anio = Integer.toString(SysmanFunciones.ano(new Date()));
    	cargarListaAno();

		cargarListaRubro();
		cargarListaFuenteFinal();
		cargarListaCentroCtoFinal();
		cargarListaAuxiliarFinal();
		cargarListaReferenciaFin();


		abrirFormulario();
    }

	public void abrirFormulario(){
        //<CODIGO_DESARROLLADO>
	  anio = String.valueOf(SysmanFunciones.getParteFecha(
              new Date(),
              Calendar.YEAR));
	  
        //</CODIGO_DESARROLLADO>
    }

public void cargarListaAno(){
	
    Map<String, Object> param = new TreeMap<>();
    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

    try
    {
    	listaAno = RegistroConverter.toListRegistro(
                        requestManager.getList(UrlServiceUtil.getInstance()
                                        .getUrlServiceByUrlByEnumID(
                                        		FrmactualizarauxiliareseningresoControladorUrlEnum.URL2656
                                                                        .getValue())
                                        .getUrl(), param));
    }
    catch (SystemException e)
    {
        logger.error(e.getMessage(), e);
        JsfUtil.agregarMensajeError(e.getMessage());
    } 
    

}



public void cargarListaRubro(){
	
	   UrlBean urlBean = UrlServiceUtil.getInstance()
               .getUrlServiceByUrlByEnumID(
            		   FrmactualizarauxiliareseningresoControladorUrlEnum.URL3725
                                               .getValue());

Map<String, Object> param = new TreeMap<>();
param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
param.put(GeneralParameterEnum.ANO.getName(), anio);
param.put(GeneralParameterEnum.NATURALEZA.getName(), "C");

listaRubro = new RegistroDataModelImpl(urlBean.getUrl(),
               urlBean.getUrlConteo().getUrl(), param,
               true, GeneralParameterEnum.CODIGO.getName());


}

public void cargarListaFuenteInicial(){
	
	
	UrlBean urlBean = UrlServiceUtil.getInstance()
            .getUrlServiceByUrlByEnumID(
            		FrmactualizarauxiliareseningresoControladorUrlEnum.URL34043
                                            .getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.RUBRO.getName(), RUBRO);
		
		listaFuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
		            urlBean.getUrlConteo().getUrl(), param,
		            true, GeneralParameterEnum.CODIGO.getName());
	

}

public void cargarListaFuenteFinal(){

	UrlBean urlBean = UrlServiceUtil.getInstance()
            .getUrlServiceByUrlByEnumID(
            		FrmactualizarauxiliareseningresoControladorUrlEnum.URL34045
                                            .getValue());
	
	Map<String, Object> param = new TreeMap<>();
	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	param.put(GeneralParameterEnum.ANO.getName(), anio);
	
	listaFuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
	            urlBean.getUrlConteo().getUrl(), param,
	            true, GeneralParameterEnum.CODIGO.getName());

             
}

public void cargarListaAuxiliarInicial(){
	
	UrlBean urlBean = UrlServiceUtil.getInstance()
            .getUrlServiceByUrlByEnumID(
            		FrmactualizarauxiliareseningresoControladorUrlEnum.URL5449
                                            .getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.RUBRO.getName(), RUBRO);
		
		listaAuxiliarInicial = new RegistroDataModelImpl(urlBean.getUrl(),
	            urlBean.getUrlConteo().getUrl(), param,
	            true, GeneralParameterEnum.CODIGO.getName());

}

public void cargarListaAuxiliarFinal(){
	
	UrlBean urlBean = UrlServiceUtil.getInstance()
            .getUrlServiceByUrlByEnumID(
            		FrmactualizarauxiliareseningresoControladorUrlEnum.URL5450
                                            .getValue());
	
	Map<String, Object> param = new TreeMap<>();
	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	param.put(GeneralParameterEnum.ANO.getName(), anio);
	
	listaAuxiliarFinal = new RegistroDataModelImpl(urlBean.getUrl(),
	            urlBean.getUrlConteo().getUrl(), param,
	            true, GeneralParameterEnum.CODIGO.getName());

}

public void cargarListaCentroCtoInicial(){
	
    	UrlBean urlBean = UrlServiceUtil.getInstance()
            .getUrlServiceByUrlByEnumID(
            		FrmactualizarauxiliareseningresoControladorUrlEnum.URL8835
                                            .getValue());
    
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
		            compania);
		param.put(GeneralParameterEnum.ANO.getName(),
		            anio);
		param.put(GeneralParameterEnum.RUBRO.getName(), RUBRO);
		
		listaCentroCtoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
		            urlBean.getUrlConteo().getUrl(), param,
		            true, GeneralParameterEnum.CODIGO.getName()); 


}

public void cargarListaCentroCtoFinal(){
	
	
	  	UrlBean urlBean = UrlServiceUtil.getInstance()
              .getUrlServiceByUrlByEnumID(FrmactualizarauxiliareseningresoControladorUrlEnum.URL9520
                      .getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
		              compania);
		param.put(GeneralParameterEnum.ANO.getName(),
		              anio);
		
		listaCentroCtoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
		              urlBean.getUrlConteo().getUrl(), param,
		              true, GeneralParameterEnum.CODIGO.getName()); 


}

public void cargarListaReferenciaIni(){
	
    UrlBean urlBean = UrlServiceUtil.getInstance()
            .getUrlServiceByUrlByEnumID(
                            FrmactualizarauxiliareseningresoControladorUrlEnum.URL5584
                                            .getValue());
Map<String, Object> param = new TreeMap<>();
param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
param.put(GeneralParameterEnum.ANO.getName(), anio);
param.put(GeneralParameterEnum.RUBRO.getName(), RUBRO);

listaReferenciaIni = new RegistroDataModelImpl(urlBean.getUrl(),
            urlBean.getUrlConteo().getUrl(), param,
            true, GeneralParameterEnum.CODIGO.getName());

}

public void cargarListaReferenciaFin(){

	
    UrlBean urlBean = UrlServiceUtil.getInstance()
	            .getUrlServiceByUrlByEnumID(
	                            FrmactualizarauxiliareseningresoControladorUrlEnum.URL5585
	                                            .getValue());
	Map<String, Object> param = new TreeMap<>();
	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	param.put(GeneralParameterEnum.ANO.getName(), anio);
	
	listaReferenciaFin = new RegistroDataModelImpl(urlBean.getUrl(),
	            urlBean.getUrlConteo().getUrl(), param,
	            true, GeneralParameterEnum.CODIGO.getName());
}

public void cambiarAno() {

	RUBRO = FUENTEINICIAL = FUENTEFINAL = AUXILIARINICIAL= "";
	AUXILIARFINAL = CENTROCOSTOINICIAL = CENTROCOSTOFINAL= "";
	REFERENCIAINICIAL = REFERENCIAFINAL = "";
	
	cargarListaRubro();
	cargarListaFuenteFinal();
	cargarListaCentroCtoFinal();
	cargarListaAuxiliarFinal();
	cargarListaReferenciaFin();

}

public void oprimircmdIniciar() {
	
	
	
	if(validarAuxiliares()) {
		
		 try {
				
				fuenteIniPR = fuenteFinPR = auxIniPr= auxFinPr = "N/A";
				centroCosIniPR =CentroCosFinPR = RefeIniPR = RefeFinPR = "N/A";
				
				  if(!(manAuxFlag) && !(AUXILIARINICIAL.equalsIgnoreCase(""))) { 
					  
					  if(!(AUXILIARFINAL.equalsIgnoreCase("")) && auxFinalFlag) {
						  auxIniPr = AUXILIARINICIAL;
						  auxFinPr = AUXILIARFINAL;
					  }
			    	
			    }
			    if(!(manCtoFlag) && !(CENTROCOSTOINICIAL.equalsIgnoreCase(""))) { 
			  	  
			  	  if(!(CENTROCOSTOFINAL.equalsIgnoreCase("")) && ctoFinalFlagF) {
			  		  		centroCosIniPR = CENTROCOSTOINICIAL;
			  				CentroCosFinPR = CENTROCOSTOFINAL;
					  }
			    	
			    }
			    if(!(manFueFlag) && !(FUENTEINICIAL.equalsIgnoreCase(""))) { 
			    		
			  	  if(!(FUENTEFINAL.equalsIgnoreCase("")) && fueFinalFlagF) {
			  		  
			  		  	fuenteIniPR = FUENTEINICIAL;
			  			fuenteFinPR = FUENTEFINAL;
					  }
				  }
			    if(!(manRefFlag)&& !(REFERENCIAINICIAL.equalsIgnoreCase(""))) { 
			    	
			  	  if(!(REFERENCIAFINAL.equalsIgnoreCase("")) && refFinalFlag) {
			  		  		RefeIniPR = REFERENCIAINICIAL;
			  				RefeFinPR = REFERENCIAFINAL;
					  }
				  }
				

				
				
				ejbPresupuestoCuatro.actualizarAuxiliaresIngreso(
						 compania, 
						 anio, 
						 RUBRO,
						 centroCosIniPR, 
						 CentroCosFinPR, 
						 auxIniPr,
						 auxFinPr,
						 RefeIniPR,
						 RefeFinPR,
						 fuenteIniPR, 
						 fuenteFinPR);
		
		
				  JsfUtil.agregarMensajeInformativo(
	                        idioma.getString("TB_TB4440"));
			}
			catch (SystemException e) {
				Logger.getLogger(RevisarafectacionesControlador.class.getName())
				.log(Level.SEVERE, null, e);
				JsfUtil.agregarMensajeError(e.getMessage());
		
	    } 
		
		
		
		}else {
		JsfUtil.agregarMensajeError(idioma.getString("TB_TB4441"));
		
	}
	
	
		 
}

public boolean validarAuxiliares() {
	
	boolean flag = false;
	
	
	  if(!(manAuxFlag) && !(AUXILIARINICIAL.equalsIgnoreCase(""))) { 
		  
		  if(!(AUXILIARFINAL.equalsIgnoreCase(""))) {
			  flag = true;
			  auxFinalFlag = true;
		  }else {
			  
			  auxFinalFlag = false;
		  }
    	
    }
    if(!(manCtoFlag) && !(CENTROCOSTOINICIAL.equalsIgnoreCase(""))) { 
  	  
  	  if(!(CENTROCOSTOFINAL.equalsIgnoreCase(""))) {
  		    flag = true;
  		    ctoFinalFlagF = true;
		  }else {
			ctoFinalFlagF = false;
		  }
    	
    }
    if(!(manFueFlag) && !(FUENTEINICIAL.equalsIgnoreCase(""))) { 
    		
  	  if(!(FUENTEFINAL.equalsIgnoreCase(""))) {
  		  	flag = true;
  		  	fueFinalFlagF = true;
		  }else {
			fueFinalFlagF = false;  
		  }
	  }
    if(!(manRefFlag)&& !(REFERENCIAINICIAL.equalsIgnoreCase(""))) { 
    	
  	  if(!(REFERENCIAFINAL.equalsIgnoreCase(""))) {
  		  	flag = true;
  		  	refFinalFlag = true;
		  }else {
			refFinalFlag = false;
		  }
	  }
    
    return   flag;
}

public void oprimircmdExcel() {
         archivoDescarga=null;   
         generaReporte();
    }

public void seleccionarFilaRubro(SelectEvent event) {
	 manFueFlag = false;
	 manAuxFlag = false;
	 manCtoFlag = false;
	 manRefFlag = false;
	 
	 manFueFlagF = false;
	 manAuxFlagF = false;
	 manCtoFlagF = false;
	 manRefFlagF = false;
	
    Registro registroAux = (Registro) event.getObject();
    RUBRO = SysmanFunciones
            .nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), " ")
            .toString();
    
     manAux = SysmanFunciones
             .nvl(registroAux.getCampos().get(GeneralParameterEnum.MAN_AUX_GEN.getName()), "false")
             .toString();
     manRef = SysmanFunciones
             .nvl(registroAux.getCampos().get(GeneralParameterEnum.MAN_AUX_REF.getName()), "false")
             .toString();
     manCto = SysmanFunciones
             .nvl(registroAux.getCampos().get(GeneralParameterEnum.MAN_CEN_CTO.getName()), "false")
             .toString();
     manFue = SysmanFunciones
             .nvl(registroAux.getCampos().get(GeneralParameterEnum.MAN_AUX_FUE.getName()), "false")
             .toString();
     
     if(manAux.equalsIgnoreCase("true")) {
    	 manAuxFlag = false;
    	 manAuxFlagF = false;
     }else {
    	 manAuxFlag = true;
    	 manAuxFlagF = true;
     }
     
     if(manRef.equalsIgnoreCase("true")) {
    	 
    	 manRefFlag = false;
    	 manRefFlagF = false;
     }else {
    	 manRefFlag = true;
    	 manRefFlagF = true;
     }
     
     
     if(manCto.equalsIgnoreCase("true")) {
    	 manCtoFlag = false;
    	 manCtoFlagF = false;
     }else {
    	 manCtoFlag = true;
    	 manCtoFlagF = true;
     }
     
     if(manFue.equalsIgnoreCase("true")) {
    	 manFueFlag = false;
    	 manFueFlagF = false;
     }else {
    	 manFueFlag = true;
    	 manFueFlagF = true;
     }
	    
     
   
    FUENTEINICIAL = FUENTEFINAL = AUXILIARINICIAL= "";
 	AUXILIARFINAL = CENTROCOSTOINICIAL = CENTROCOSTOFINAL= "";
 	REFERENCIAINICIAL = REFERENCIAFINAL = "";
    
    cargarListaFuenteInicial();
	cargarListaCentroCtoInicial();
	cargarListaAuxiliarInicial(); 
	cargarListaReferenciaIni(); 
}

public void seleccionarFilaFuenteInicial(SelectEvent event) {
    Registro registroAux = (Registro) event.getObject();
    FUENTEINICIAL = SysmanFunciones
            .nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), " ")
            .toString();

}

public void seleccionarFilaFuenteFinal(SelectEvent event) {
	
    Registro registroAux = (Registro) event.getObject();
    FUENTEFINAL = SysmanFunciones
            .nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), " ")
            .toString();
    
}

public void seleccionarFilaAuxiliarInicial(SelectEvent event) {
   
	Registro registroAux = (Registro) event.getObject();
	AUXILIARINICIAL = SysmanFunciones
            .nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), " ")
            .toString();
}

public void seleccionarFilaAuxiliarFinal(SelectEvent event) {
	
    Registro registroAux = (Registro) event.getObject();
  
    AUXILIARFINAL = SysmanFunciones
            .nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), " ")
            .toString();
}

public void seleccionarFilaCentroCtoInicial(SelectEvent event) {
	   Registro registroAux = (Registro) event.getObject();
	   CENTROCOSTOINICIAL = SysmanFunciones
                       .nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), " ")
                       .toString();


}

public void seleccionarFilaCentroCtoFinal(SelectEvent event) {
	
	 Registro registroAux = (Registro) event.getObject();
	   CENTROCOSTOFINAL = SysmanFunciones
                     .nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), " ")
                     .toString();
	
	
}

public void seleccionarFilaReferenciaIni(SelectEvent event) {

	  Registro registroAux = (Registro) event.getObject();
	    REFERENCIAINICIAL = SysmanFunciones
                .nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), " ")
                .toString();
	

}

public void seleccionarFilaReferenciaFin(SelectEvent event) {
	  Registro registroAux = (Registro) event.getObject();
	    REFERENCIAFINAL = SysmanFunciones
                .nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), " ")
                .toString();
}

private void generaReporte() {
	
	
    String reporte = "800582LisAuxPptalIngreso";
    String strSql = "";
    String strSqlwhere = "WHERE DETALLE_COMPROBANTE_PPTAL.COMPANIA  = '"+compania+"' " + 
    		"AND DETALLE_COMPROBANTE_PPTAL.ANO = "+anio+" " + 
    		"AND DETALLE_COMPROBANTE_PPTAL.CUENTA = '"+RUBRO+"' "+
    		"AND TIPO_COMPROBPP.CLASE IN ('ING','AIN','DIN','MOI','ICA','AIC','DIC','MIC')";
    try {

      
        HashMap<String, Object> reemplazar = new HashMap<>();
        
        if(!(manAuxFlag) && !(AUXILIARINICIAL.equalsIgnoreCase(""))) { 
        	strSqlwhere = strSqlwhere+" AND DETALLE_COMPROBANTE_PPTAL.AUXILIAR = '"+AUXILIARINICIAL+"' ";
        }
        if(!(manCtoFlag) && !(CENTROCOSTOINICIAL.equalsIgnoreCase(""))) { 
        	strSqlwhere = strSqlwhere+" AND DETALLE_COMPROBANTE_PPTAL.CENTRO_COSTO = '"+CENTROCOSTOINICIAL+"' ";
        }
        if(!(manFueFlag) && !(FUENTEINICIAL.equalsIgnoreCase(""))) { 
        	strSqlwhere = strSqlwhere+" AND DETALLE_COMPROBANTE_PPTAL.FUENTE_RECURSO =  '"+FUENTEINICIAL+"' ";	
	    }
        if(!(manRefFlag)&& !(REFERENCIAINICIAL.equalsIgnoreCase(""))) { 
        	strSqlwhere = strSqlwhere+" AND DETALLE_COMPROBANTE_PPTAL.REFERENCIA = '"+REFERENCIAINICIAL+"' ";
	    }
        
        reemplazar.put("whereSql", strSqlwhere);
        

        strSql = Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar);

        archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                ConectorPool.ESQUEMA_SYSMAN,
                ReportesBean.FORMATOS.EXCEL);
    }
    catch (FileNotFoundException ex) {
        JsfUtil.agregarMensajeInformativo(
                        idioma.getString("MSM_INFORME_NO_EXISTE") + " "
                            + ex.getMessage() + " " + reporte);
        Logger.getLogger(AuxiliarPptalTercerosControlador.class.getName())
                        .log(Level.SEVERE, null, ex);
    }
    catch (JRException | IOException ex) {
        Logger.getLogger(AuxiliarPptalTercerosControlador.class.getName())
                        .log(Level.SEVERE, null, ex);
        JsfUtil.agregarMensajeError(ex.getMessage());
    }
    catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (DRException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

}

public boolean isManAuxFlagF() {
	return manAuxFlagF;
}

public void setManAuxFlagF(boolean manAuxFlagF) {
	this.manAuxFlagF = manAuxFlagF;
}

public boolean isManRefFlagF() {
	return manRefFlagF;
}

public void setManRefFlagF(boolean manRefFlagF) {
	this.manRefFlagF = manRefFlagF;
}

public boolean isManCtoFlagF() {
	return manCtoFlagF;
}

public void setManCtoFlagF(boolean manCtoFlagF) {
	this.manCtoFlagF = manCtoFlagF;
}

public boolean isManFueFlagF() {
	return manFueFlagF;
}

public void setManFueFlagF(boolean manFueFlagF) {
	this.manFueFlagF = manFueFlagF;
}


public String getManAux() {
	return manAux;
}

public void setManAux(String manAux) {
	this.manAux = manAux;
}

public String getManRef() {
	return manRef;
}

public void setManRef(String manRef) {
	this.manRef = manRef;
}

public String getManCto() {
	return manCto;
}

public void setManCto(String manCto) {
	this.manCto = manCto;
}

public String getManFue() {
	return manFue;
}

public void setManFue(String manFue) {
	this.manFue = manFue;
}

public boolean isManAuxFlag() {
	return manAuxFlag;
}

public void setManAuxFlag(boolean manAuxFlag) {
	this.manAuxFlag = manAuxFlag;
}

public boolean isManRefFlag() {
	return manRefFlag;
}

public void setManRefFlag(boolean manRefFlag) {
	this.manRefFlag = manRefFlag;
}

public boolean isManCtoFlag() {
	return manCtoFlag;
}

public void setManCtoFlag(boolean manCtoFlag) {
	this.manCtoFlag = manCtoFlag;
}

public boolean isManFueFlag() {
	return manFueFlag;
}

public void setManFueFlag(boolean manFueFlag) {
	this.manFueFlag = manFueFlag;
}


public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

public String getRUBRO() {
        return RUBRO;
    }

    public void setRUBRO(String RUBRO) {
        this.RUBRO = RUBRO;
    }

public String getFUENTEINICIAL() {
        return FUENTEINICIAL;
    }

    public void setFUENTEINICIAL(String FUENTEINICIAL) {
        this.FUENTEINICIAL = FUENTEINICIAL;
    }

public String getFUENTEFINAL() {
        return FUENTEFINAL;
    }

    public void setFUENTEFINAL(String FUENTEFINAL) {
        this.FUENTEFINAL = FUENTEFINAL;
    }

public String getAUXILIARINICIAL() {
        return AUXILIARINICIAL;
    }

    public void setAUXILIARINICIAL(String AUXILIARINICIAL) {
        this.AUXILIARINICIAL = AUXILIARINICIAL;
    }

public String getAUXILIARFINAL() {
        return AUXILIARFINAL;
    }

    public void setAUXILIARFINAL(String AUXILIARFINAL) {
        this.AUXILIARFINAL = AUXILIARFINAL;
    }

public String getCENTROCOSTOINICIAL() {
        return CENTROCOSTOINICIAL;
    }

    public void setCENTROCOSTOINICIAL(String CENTROCOSTOINICIAL) {
        this.CENTROCOSTOINICIAL = CENTROCOSTOINICIAL;
    }

public String getCENTROCOSTOFINAL() {
        return CENTROCOSTOFINAL;
    }

    public void setCENTROCOSTOFINAL(String CENTROCOSTOFINAL) {
        this.CENTROCOSTOFINAL = CENTROCOSTOFINAL;
    }

public String getREFERENCIAINICIAL() {
        return REFERENCIAINICIAL;
    }

    public void setREFERENCIAINICIAL(String REFERENCIAINICIAL) {
        this.REFERENCIAINICIAL = REFERENCIAINICIAL;
    }

public String getREFERENCIAFINAL() {
        return REFERENCIAFINAL;
    }

    public void setREFERENCIAFINAL(String REFERENCIAFINAL) {
        this.REFERENCIAFINAL = REFERENCIAFINAL;
    }

public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

public List<Registro> getListaAno() {
        return listaAno;
    }

public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public RegistroDataModelImpl getListaRubro() {
        return listaRubro;
    }
 
    public void setListaRubro(RegistroDataModelImpl listaRubro) {
        this.listaRubro = listaRubro;
    }

    public RegistroDataModelImpl getListaFuenteInicial() {
        return listaFuenteInicial;
    }

    public void setListaFuenteInicial(RegistroDataModelImpl listaFuenteInicial) {
        this.listaFuenteInicial = listaFuenteInicial;
    }

    public RegistroDataModelImpl getListaFuenteFinal() {
        return listaFuenteFinal;
    }

    public void setListaFuenteFinal(RegistroDataModelImpl listaFuenteFinal) {
        this.listaFuenteFinal = listaFuenteFinal;
    }
 
    public RegistroDataModelImpl getListaAuxiliarInicial() {
        return listaAuxiliarInicial;
    }

    public void setListaAuxiliarInicial(RegistroDataModelImpl listaAuxiliarInicial) {
        this.listaAuxiliarInicial = listaAuxiliarInicial;
    }

    public RegistroDataModelImpl getListaAuxiliarFinal() {
        return listaAuxiliarFinal;
    }

    public void setListaAuxiliarFinal(RegistroDataModelImpl listaAuxiliarFinal) {
        this.listaAuxiliarFinal = listaAuxiliarFinal;
    }

    public RegistroDataModelImpl getListaCentroCtoInicial() {
        return listaCentroCtoInicial;
    }

    public void setListaCentroCtoInicial(RegistroDataModelImpl listaCentroCtoInicial) {
        this.listaCentroCtoInicial = listaCentroCtoInicial;
    }
  
    public RegistroDataModelImpl getListaCentroCtoFinal() {
        return listaCentroCtoFinal;
    }

    public void setListaCentroCtoFinal(RegistroDataModelImpl listaCentroCtoFinal) {
        this.listaCentroCtoFinal = listaCentroCtoFinal;
    }

    public RegistroDataModelImpl getListaReferenciaIni() {
        return listaReferenciaIni;
    }
 
    public void setListaReferenciaIni(RegistroDataModelImpl listaReferenciaIni) {
        this.listaReferenciaIni = listaReferenciaIni;
    }
  
    public RegistroDataModelImpl getListaReferenciaFin() {
        return listaReferenciaFin;
    }

    public void setListaReferenciaFin(RegistroDataModelImpl listaReferenciaFin) {
        this.listaReferenciaFin = listaReferenciaFin;
    }

}
