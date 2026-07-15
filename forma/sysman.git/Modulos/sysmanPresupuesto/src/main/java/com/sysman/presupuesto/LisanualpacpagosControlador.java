/*-
 * LisanualpacpagosControlador.java
 *
 * 1.0
 * 
 * 19/12/2019
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.sysman.services.RegistroDataModelImpl;

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
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.AimregistroejecucgastoscxpsControladorEnum;
import com.sysman.presupuesto.enums.AimregistroejecucgastoscxpsControladorUrlEnum;
import com.sysman.presupuesto.enums.LisanualpacpagosControladorEnum;
import com.sysman.presupuesto.enums.LisanualpacpagosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.event.SelectEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 19/12/2019
 * @author jalfonsop
 */
@ManagedBean
@ViewScoped
public class  LisanualpacpagosControlador extends BeanBaseModal{
   
    private final String compania;
    private final String modulo;
    private String cuentaInicial;
    private String cuentaFinal;
    private int ano;
    private int digitos;
    private StreamedContent archivoDescarga;

    private List<Registro> listaAno;
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    
    @EJB

    private EjbSysmanUtilRemote ejbParametro;
    

    public LisanualpacpagosControlador() {
    super();
            compania = SessionUtil.getCompania();
            modulo = SessionUtil.getModulo();
            
            try {
            	//2126
            	  numFormulario = GeneralCodigoFormaEnum.LIS_ANUAL_PAC_PAGOS_CONTROLADOR
                          .getCodigo();

          validarPermisos();
          // <INI_ADICIONAL>
          // </INI_ADICIONAL>
      }
      catch (Exception ex) {
          Logger.getLogger(LisanualpacpagosControlador.class
                          .getName()).log(Level.SEVERE, null, ex);
          SessionUtil.redireccionarMenuPermisos();
      }
    }
    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar(){
    	 abrirFormulario();
		 cargarListaAno();
		 cargarListaCuentaInicial(); 
		 cargarListaCuentaFinal();
	
    }
  
  @Override
	public void abrirFormulario() {
        ano = SysmanFunciones.ano(new Date());
        digitos = 6;
            }
	
	
  	public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                            		LisanualpacpagosControladorUrlEnum.URL4395
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
  	
	  	
  	public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                		LisanualpacpagosControladorUrlEnum.URL5616
                                                .getValue());
Map<String, Object> param = new TreeMap<>();
param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
param.put(GeneralParameterEnum.ANO.getName(), ano);

listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                urlBean.getUrlConteo().getUrl(), param,
                true, LisanualpacpagosControladorEnum.CODIGO
                                .getValue());

}
  	
  	
  	
  	
public void cargarListaCuentaFinal()

{
    UrlBean urlBean = UrlServiceUtil.getInstance()
                    .getUrlServiceByUrlByEnumID(
                    		LisanualpacpagosControladorUrlEnum.URL6657
                                                    .getValue());
    Map<String, Object> param = new TreeMap<>();
    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    param.put(GeneralParameterEnum.ANO.getName(), ano);
    param.put(LisanualpacpagosControladorEnum.CUENTAINICIAL
                    .getValue(), cuentaInicial);
    listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                    urlBean.getUrlConteo().getUrl(), param,
                    true, LisanualpacpagosControladorEnum.CODIGO
                                    .getValue());
}

public void oprimirPresentar() throws com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException {
         //<CODIGO_DESARROLLADO>
         archivoDescarga=null;    
         generarInforme(FORMATOS.PDF);
        //</CODIGO_DESARROLLADO>
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel
     * en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     * @throws com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException 
     *
     */
public void oprimirExcel() throws com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException {
         //<CODIGO_DESARROLLADO>
         archivoDescarga=null; 
         generarInforme(FORMATOS.EXCEL97);
        //</CODIGO_DESARROLLADO>
    }

public void generarInforme(ReportesBean.FORMATOS formato) throws com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException {
	
	try
    {
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("digitos",digitos);
        reemplazar.put("ano",ano);
        reemplazar.put("cuentaInicial",cuentaInicial);
        reemplazar.put("cuentaFinal",cuentaFinal);
        
        
        // MANEJO DE PARAMETROS DE REEMPLAZO
        Map<String, Object> parametros = new HashMap<>();
        String reporte = "002069LisAnualPACPagos";
        // MANEJO DE PARAMETROS DEL REPORTE
        Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar, parametros);

        parametros.put("PR_CUENTAINICIAL", (cuentaInicial));
        parametros.put("PR_CUENTAFINAL",(cuentaFinal));
       
        
        archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                        ConectorPool.ESQUEMA_SYSMAN, formato);
    }
    catch (JRException | IOException ex)
    {
        JsfUtil.agregarMensajeError(
                        idioma.getString("MSM_TRANS_INTERRUMPIDA")
                            + ex.getMessage());
        Logger.getLogger(LisanualpacpagosControlador.class.getName())
                        .log(Level.SEVERE, null, ex);
    }

    
}

public void cambiarAno() {
	cuentaInicial = null;
    cuentaFinal = null;
    cargarListaCuentaInicial();
    }

public void seleccionarFilaCuentaInicial(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
    cuentaInicial = registroAux.getCampos()
                    .get(LisanualpacpagosControladorEnum.CODIGO
                                    .getValue()).toString();
    cuentaFinal = null;
    cargarListaCuentaFinal();
}
    
public void seleccionarFilaCuentaFinal(SelectEvent event) {

	Registro registroAux = (Registro) event.getObject();
    cuentaFinal = registroAux.getCampos()
                    .get(LisanualpacpagosControladorEnum.CODIGO
                                    .getValue()).toString();
}

public String getCuentaInicial() {
        return cuentaInicial;
    }
    
    public void setCuentaInicial(String cuentaInicial) {
        this.cuentaInicial = cuentaInicial;
    }
   
public String getCuentaFinal() {
        return cuentaFinal;
    }
   
    public void setCuentaFinal(String cuentaFinal) {
        this.cuentaFinal = cuentaFinal;
    }
    
public int getAno() {
        return ano;
    }
  
    public void setAno(int ano) {
        this.ano = ano;
    }
   
public int getDigitos() {
        return digitos;
    }
    
    public void setDigitos(int digitos) {
        this.digitos = digitos;
    }
   
public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

public List<Registro> getListaAno() {
        return listaAno;
    }
    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en  listaAno
     */
public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
    /**
     * Retorna la lista listaCuentaInicial
     * 
     * @return listaCuentaInicial
     */
    public RegistroDataModelImpl getListaCuentaInicial() {
        return listaCuentaInicial;
    }
    /**
     * Asigna la lista listaCuentaInicial
     * 
     * @param listaCuentaInicial
     * Variable a asignar en  listaCuentaInicial
     */
    public void setListaCuentaInicial(RegistroDataModelImpl listaCuentaInicial) {
        this.listaCuentaInicial = listaCuentaInicial;
    }
    /**
     * Retorna la lista listaCuentaFinal
     * 
     * @return listaCuentaFinal
     */
    public RegistroDataModelImpl getListaCuentaFinal() {
        return listaCuentaFinal;
    }
    /**
     * Asigna la lista listaCuentaFinal
     * 
     * @param listaCuentaFinal
     * Variable a asignar en  listaCuentaFinal
     */
    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
        this.listaCuentaFinal = listaCuentaFinal;
    }
//</SET_GET_LISTAS_COMBO_GRANDE>
}
