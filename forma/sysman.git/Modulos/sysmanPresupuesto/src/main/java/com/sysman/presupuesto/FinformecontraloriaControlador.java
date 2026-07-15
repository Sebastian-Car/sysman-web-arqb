/*-
 * FinformecontraloriaControlador.java
 *
 * 1.0
 * 
 * 11/12/2019
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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.FinformeContraloriaControladorEnum;
import com.sysman.presupuesto.enums.FinformeContraloriaControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
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
 * @version 1.0, 11/12/2019
 * @author jalfonsop
 */
@ManagedBean
@ViewScoped
public class  FinformecontraloriaControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;
    private final String modulo;
    private boolean saldoanterior;
    private boolean especial;
    private String cuentaInicial;
    private String cuentaFinal;
    private String ano;
    private String mes;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
private StreamedContent archivoDescarga;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
private List<Registro> listaAno;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
private List<Registro> listaMes;
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
 private RegistroDataModelImpl listaCuentaInicial;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
 private RegistroDataModelImpl listaCuentaFinal;
 
  @EJB
 private EjbSysmanUtilRemote ejbSysmanUtil;
//</DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FinformecontraloriaControlador
     */
    public FinformecontraloriaControlador() {
  super();
            compania = SessionUtil.getCompania();
            modulo = SessionUtil.getModulo();
    
            
            try {
                numFormulario = GeneralCodigoFormaEnum.FRM_INFORME_CONTRALORIA
                                .getCodigo();
                validarPermisos();
                // <INI_ADICIONAL>
                // </INI_ADICIONAL>
            }
            catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                SessionUtil.redireccionarMenuPermisos();
            }
    }
    
    @PostConstruct
    public void inicializar(){
    	abrirFormulario();
		cargarListaAno();
		cargarListaMes();
		cargarListaCuentaInicial(); 
		cargarListaCuentaFinal();
//</CARGAR_LISTA_COMBO_GRANDE>
//<CREAR_ARBOLES>
//</CREAR_ARBOLES>
		
    }
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
	public void abrirFormulario(){
    ano = Integer.toString(SysmanFunciones.ano(new Date()));
    mes = Integer.toString(SysmanFunciones.mes(new Date()));
        //</CODIGO_DESARROLLADO>
    }

public void cargarListaAno(){
	
	Map<String, Object> param = new TreeMap<>();
    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

    try {
        listaAno = RegistroConverter.toListRegistro(
                        requestManager.getList(UrlServiceUtil.getInstance()
                                        .getUrlServiceByUrlByEnumID(
                                        		FinformeContraloriaControladorUrlEnum.URL5047
                                                                        .getValue())
                                        .getUrl(), param));
    }
    catch (SystemException e) {
        JsfUtil.agregarMensajeError(e.getMessage());
        logger.error(e.getMessage(), e);

    }
}
    
public void cargarListaMes(){
	
	Map<String, Object> param = new TreeMap<>();
    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    param.put(GeneralParameterEnum.ANO.getName(),
                    SysmanFunciones.ano(new Date()));

    try {
        listaMes = RegistroConverter.toListRegistro(
                        requestManager.getList(UrlServiceUtil.getInstance()
                                        .getUrlServiceByUrlByEnumID(
                                        		FinformeContraloriaControladorUrlEnum.URL5447
                                                                        .getValue())
                                        .getUrl(), param));
    }
    catch (SystemException e) {
        JsfUtil.agregarMensajeError(e.getMessage());
        logger.error(e.getMessage(), e);

    }

}
   
public void cargarListaCuentaInicial(){

	UrlBean urlBean = UrlServiceUtil.getInstance()
            .getUrlServiceByUrlByEnumID(
            		FinformeContraloriaControladorUrlEnum.URL6064
                                            .getValue());
Map<String, Object> param = new TreeMap<>();
param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
param.put(GeneralParameterEnum.ANO.getName(), ano);
listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
            urlBean.getUrlConteo().getUrl(), param,
            true, "ID");
}
   
public void cargarListaCuentaFinal(){
	
	UrlBean urlBean = UrlServiceUtil.getInstance()
            .getUrlServiceByUrlByEnumID(
            		FinformeContraloriaControladorUrlEnum.URL7124
                                            .getValue());
Map<String, Object> param = new TreeMap<>();
param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
param.put(GeneralParameterEnum.ANO.getName(), ano);
param.put(FinformeContraloriaControladorEnum.CUENTAINICIAL
            .getValue(), cuentaInicial);

listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
            urlBean.getUrlConteo().getUrl(), param,
            true, "ID");


}
//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
   
public void oprimirImprimir() throws SystemException {
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
     * @throws SystemException 
     *
     */
public void oprimirExcel() throws SystemException {
         //<CODIGO_DESARROLLADO>
         archivoDescarga=null;
         generarInforme(FORMATOS.EXCEL);
        //</CODIGO_DESARROLLADO>
    }
//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ano
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     */
public void cambiarAno() {
	
	cuentaInicial = null;
    cuentaFinal = null;
    cargarListaCuentaInicial();
    cargarListaCuentaFinal();
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }


//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaInicial
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaCuentaInicial(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
	cuentaInicial = SysmanFunciones
	.nvl(registroAux.getCampos().get("ID"), "").toString();
	cuentaFinal = null;
	cargarListaCuentaFinal();
}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaFinal
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaCuentaFinal(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
	cuentaFinal = SysmanFunciones.nvl(registroAux.getCampos().get("ID"), "")
				.toString();
}

/**
 * Metodo ejecutado al darle clic en el boton pdf o excel
 * 
 * @param formato
 * @throws SystemException 
 */
public void generarInforme(ReportesBean.FORMATOS formato) throws SystemException {
    String reporte = "";
    
     
    if(especial){
    	reporte = "002080FInformeContraloria";
    	}
    else if (saldoanterior) {
    	reporte = "002081FInformeContraloriaSalAnt";
    }
    else reporte = "002083FInformeContraloriaAZ";
 
    Map<String, Object> reemplazos = new HashMap<>();

    
    
    reemplazos.put("especial", especial == true?-1:0);
    reemplazos.put("mes", mes);
    reemplazos.put("compania", compania);
    reemplazos.put("ano", ano);
    reemplazos.put("cuentaInicial", cuentaInicial);
    reemplazos.put("cuentaFinal", cuentaFinal);
    
    String nombreMes = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mes)];
    
    String firmaUno = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA 1 EN PRESUPUESTO", modulo,new Date(), true), "");
    
    String cargoUno = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
            "CARGO 1 EN PRESUPUESTO", modulo,new Date(), true), "");
    
    String documentoUno = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
            "DOCUMENTO 1 EN PRESUPUESTO", modulo,new Date(), true), "");
    
    String firmaDos = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
            "FIRMA 2 EN PRESUPUESTO", modulo,new Date(), true), "");
    
    String cargoDos = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
            "CARGO 2 EN PRESUPUESTO", modulo,new Date(), true), "");
    
    String documentoDos = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
            "DOCUMENTO 2 EN PRESUPUESTO", modulo,new Date(), true), "");
    
    String firmaTres = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
            "FIRMA 3 EN PRESUPUESTO", modulo,new Date(), true), "");
    
    String cargoTres = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
            "CARGO 3 EN PRESUPUESTO", modulo,new Date(), true), "");
    
    String documentoTres = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
            "DOCUMENTO 3 EN PRESUPUESTO", modulo,new Date(), true), "");
    
    String firmaCuatro = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
            "FIRMA 4 EN PRESUPUESTO", modulo,new Date(), true), "");
    
    String cargoCuatro = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
            "CARGO 4 EN PRESUPUESTO", modulo,new Date(), true), "");
    
    String documentoCuatro = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
            "DOCUMENTO 4 EN PRESUPUESTO", modulo,new Date(), true), "");
    
    Map<String, Object> parametros = new HashMap<>();
    parametros.put("PR_FIRMA_1_EN_PRESUPUESTO", firmaUno);
    parametros.put("PR_CARGO_1_EN_PRESUPUESTO", cargoUno);
    parametros.put("PR_DOCUMENTO_1_EN_PRESUPUESTO", documentoUno);
    parametros.put("PR_FIRMA_2_EN_PRESUPUESTO", firmaDos);
    parametros.put("PR_CARGO_2_EN_PRESUPUESTO", cargoDos);
    parametros.put("PR_DOCUMENTO_2_EN_PRESUPUESTO", documentoDos);
    parametros.put("PR_FIRMA_3_EN_PRESUPUESTO", firmaTres);
    parametros.put("PR_CARGO_3_EN_PRESUPUESTO", cargoTres);
    parametros.put("PR_DOCUMENTO_3_EN_PRESUPUESTO", documentoTres);
    parametros.put("PR_FIRMA_4_EN_PRESUPUESTO", firmaCuatro);
    parametros.put("PR_CARGO_4_EN_PRESUPUESTO", cargoCuatro);
    parametros.put("PR_DOCUMENTO_4_EN_PRESUPUESTO", documentoCuatro);
    parametros.put("PR_MES", nombreMes); 
    parametros.put("PR_ANO", ano); 
    
    

    Reporteador.resuelveConsulta(reporte,
                    Integer.parseInt(modulo), reemplazos,
                    parametros);

    try {
		archivoDescarga = JsfUtil.exportarStreamed(reporte,
		                parametros, ConectorPool.ESQUEMA_SYSMAN,
		                formato);
	} catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   
}

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable saldoanterior
     * 
     * @return  saldoanterior
     */

public void cambiarEspecial(){

if(especial){
saldoanterior = false;

}}


public void cambiarConsaldosanteriores(){

if(saldoanterior){
especial = false;

}}
public boolean getSaldoanterior() {
        return saldoanterior;
    }
    /**
     * Asigna la variable  saldoanterior
     * 
     * @param  saldoanterior
     * Variable a asignar en  saldoanterior
     */
    public void setSaldoanterior(boolean saldoanterior) {
        this.saldoanterior = saldoanterior;
    }
    /**
     * Retorna la variable especial
     * 
     * @return  especial
     */
public boolean getEspecial() {
        return especial;
    }
    /**
     * Asigna la variable  especial
     * 
     * @param  especial
     * Variable a asignar en  especial
     */
    public void setEspecial(boolean especial) {
        this.especial = especial;
    }
    /**
     * Retorna la variable cuentaInicial
     * 
     * @return  cuentaInicial
     */
public String getCuentaInicial() {
        return cuentaInicial;
    }
    /**
     * Asigna la variable  cuentaInicial
     * 
     * @param  cuentaInicial
     * Variable a asignar en  cuentaInicial
     */
    public void setCuentaInicial(String cuentaInicial) {
        this.cuentaInicial = cuentaInicial;
    }
    /**
     * Retorna la variable cuentaFinal
     * 
     * @return  cuentaFinal
     */
public String getCuentaFinal() {
        return cuentaFinal;
    }
    /**
     * Asigna la variable  cuentaFinal
     * 
     * @param  cuentaFinal
     * Variable a asignar en  cuentaFinal
     */
    public void setCuentaFinal(String cuentaFinal) {
        this.cuentaFinal = cuentaFinal;
    }
    /**
     * Retorna la variable ano
     * 
     * @return  ano
     */
public String getAno() {
        return ano;
    }
    /**
     * Asigna la variable  ano
     * 
     * @param  ano
     * Variable a asignar en  ano
     */
    public void setAno(String ano) {
        this.ano = ano;
    }
    /**
     * Retorna la variable mes
     * 
     * @return  mes
     */
public String getMes() {
        return mes;
    }
    /**
     * Asigna la variable  mes
     * 
     * @param  mes
     * Variable a asignar en  mes
     */
    public void setMes(String mes) {
        this.mes = mes;
    }
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
    /**
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
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
    /**
     * Retorna la lista listaMes
     * 
     * @return listaMes
     */
public List<Registro> getListaMes() {
        return listaMes;
    }
    /**
     * Asigna la lista listaMes
     * 
     * @param listaMes
     * Variable a asignar en  listaMes
     */
public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
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
