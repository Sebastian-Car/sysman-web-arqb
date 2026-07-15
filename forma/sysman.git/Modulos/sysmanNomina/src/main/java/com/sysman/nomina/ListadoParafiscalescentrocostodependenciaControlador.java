/*-
 * ListadoParafiscalescentrocostodependenciaControlador.java
 *
 * 1.0
 * 
 * 15/09/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;
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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.FrmFactoresvacacionesdefinitivasControladorUrlEnum;
import com.sysman.nomina.enums.ListadoParafiscalesControladorEnum;
import com.sysman.nomina.enums.ListadoParafiscalesControladorUrlEnum;
import com.sysman.nomina.enums.ListadoParafiscalescentrocostodependenciaControladorUrlEnum;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.model.StreamedContent;
/**
 * Formulario que se crea para la impresión del informe de centros de costo
 * y dependencia.
 *
 * @version 1.0, 15/09/2020
 * @author dcastiblanco
 */
@ManagedBean
@ViewScoped
public class  ListadoParafiscalescentrocostodependenciaControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;
//<DECLARAR_ATRIBUTOS>
    /**
     * Constante a nivel de clase que almacena el codigo del usuario
     * que inicio sesion
     */
private String periodo;
    /**
     * variable que almacena el periodo
     */
private String proceso;
    /**
     * variable que almacena el proceso
     */
private String anio;
    /**
     * variable que almacena el anio
     */
private String mes;
    /**
     * variable que almacena el mes
     */
private StreamedContent archivoDescarga;
    /**
     * variable de  descarga
     */
private List<Registro> listaPeriodo1;
    /**
     * variable que almacena la lista de periodos
     */
private List<Registro> listaProceso;
    /**
     * variable que almacena la lista de proceso
     */
private List<Registro> listaAno1;
    /**
     * variable que almacena la lista de anios
     */
private List<Registro> listaMes1;
    /*
     * variable que almacena la lista de mes
     */
private String nombreCompania;
    /*
     * Variable qe almacena la compania
     */
@EJB
private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Crea una nueva instancia de ListadoParafiscalescentrocostodependenciaControlador
     */
    public ListadoParafiscalescentrocostodependenciaControlador() {
  super();
          compania = SessionUtil.getCompania();
          mes = SessionUtil.getSessionVar("mesNomina").toString();
          anio = SessionUtil.getSessionVar("anioNomina").toString();
          periodo = SessionUtil.getSessionVar("periodoNomina").toString();
          nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
        try {
            //2189
            numFormulario = GeneralCodigoFormaEnum.LISTADO_PARAFISCALES_CENTROCOSTO_DEPENDENCIA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            
        }catch (Exception ex) {
                Logger.getLogger(ListadoParafiscalesControlador.class.getName())
                                .log(Level.SEVERE, null, ex);
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
        cargarListaPeriodo1();
        cargarListaProceso();
        cargarListaAno1();
        cargarListaMes1();
	abrirFormulario();
    }
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
  @Override
	public void abrirFormulario(){
      //Metodo heredado
      proceso = "1";
      cargarListaAno1();
      anio = Integer.toString(SysmanFunciones.ano(new Date()));
      cargarListaMes1();
      mes = Integer.toString(SysmanFunciones.mes(new Date()));
      cargarListaPeriodo1();
    }
//<METODOS_CARGAR_LISTA>
  /**
   * 
   * Carga la lista listaProceso
   *
   */
public void cargarListaProceso(){
  Map<String, Object> param = new TreeMap<>();
  param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
  try {
      listaProceso = RegistroConverter.toListRegistro(
                      requestManager.getList(UrlServiceUtil.getInstance()
                                      .getUrlServiceByUrlByEnumID(
                                                      ListadoParafiscalescentrocostodependenciaControladorUrlEnum.URL40082
                                                                      .getValue())
                                      .getUrl(), param));
  }
  catch (SystemException e) {
      logger.error(e.getMessage(), e);
      JsfUtil.agregarMensajeError(e.getMessage());
  }
}
/**
 * 
 * Carga la lista listaAno1
 *
 */
public void cargarListaAno1(){
Map<String, Object> param = new TreeMap<>();
param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
try {
    listaAno1 = RegistroConverter.toListRegistro(
                    requestManager.getList(UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    ListadoParafiscalescentrocostodependenciaControladorUrlEnum.URL40083
                                                                    .getValue())
                                    .getUrl(), param));
}
catch (SystemException e) {
    logger.error(e.getMessage(), e);
    JsfUtil.agregarMensajeError(e.getMessage());
  }
}
/**
 * 
 * Carga la lista listaMes1
 *
 */
public void cargarListaMes1(){
Map<String, Object> param = new TreeMap<>();
param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
param.put(GeneralParameterEnum.ANO.getName(),anio);

try {
    listaMes1 = RegistroConverter.toListRegistro(
                    requestManager.getList(UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    ListadoParafiscalescentrocostodependenciaControladorUrlEnum.URL40084
                                                                    .getValue())
                                    .getUrl(), param));
}
catch (SystemException e) {
    logger.error(e.getMessage(), e);
    JsfUtil.agregarMensajeError(e.getMessage());
      }
}
    /**
     * 
     * Carga la lista listaPeriodo1
     *
     */
public void cargarListaPeriodo1(){
    Map<String, Object> param = new TreeMap<>();
    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
    param.put(GeneralParameterEnum.ANO.getName(), anio);
    param.put(GeneralParameterEnum.MES.getName(), mes);
    try {
        listaPeriodo1 = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ListadoParafiscalescentrocostodependenciaControladorUrlEnum.URL40081.getValue())
                                                            .getUrl(),
                                            param));
    } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
    }
}
//<METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Preliminar
     * en la vista
     *
     *
     */
public void oprimirPreliminar() {
         archivoDescarga=null;
         generarInforme(ReportesBean.FORMATOS.PDF);
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel
     * en la vista
     *
     *
     */
public void oprimirExcel() {
         archivoDescarga=null;
         generarInforme(ReportesBean.FORMATOS.EXCEL97);
    }
    /*
     * Metodo generarInforme
     */
public void generarInforme(ReportesBean.FORMATOS formato){
    try {
                    HashMap<String, Object> reemplaza = new HashMap<>();
                    Map<String, Object> parametros = new HashMap<>();
                    reemplaza.put("proceso", proceso);
                    reemplaza.put("anio", anio);
                    reemplaza.put("mes", mes);
                    reemplaza.put("periodo", periodo);
                    
                 // MANEJO DE PARAMETROS DEL REPORTE
                    Reporteador.resuelveConsulta("002132PARAFISCALESIDIPRON",
                                    Integer.parseInt(SessionUtil.getModulo()),
                                    reemplaza,
                                    parametros);
                    String jefeRH = ejbSysmanUtil.consultarParametro(compania,
                                    "NOMBRE JEFE DESARROLLO HUMANO",
                                    SessionUtil.getModulo(),
                                    new Date(), true);
                    String jefeNomina = ejbSysmanUtil.consultarParametro(compania,
                                    "NOMBRE JEFE NOMINA", SessionUtil.getModulo(),
                                    new Date(), true);
                    String cargoResponsableNomina = ejbSysmanUtil.consultarParametro(compania,
                                    "CARGO RESPONSABLE DE NOMINA", SessionUtil.getModulo(), 
                                    new Date(), false);
                    String cargoJefeDesarrolloHumano = ejbSysmanUtil.consultarParametro(compania,
                                    "CARGO JEFE DESARROLLO HUMANO", SessionUtil.getModulo(), 
                                    new Date(), false);
                    parametros.put("PR_NOMBRE_JEFE_DESARROLLO_HUMANO", jefeRH);
                    parametros.put("PR_NOMBRE_JEFE_NOMINA", jefeNomina);
                    parametros.put("PR_CARGO_RESPONSABLE_DE_NOMINA", cargoResponsableNomina);
                    parametros.put("PR_CARGO_JEFE_DESARROLLO_HUMANO", cargoJefeDesarrolloHumano);
                    parametros.put("PR_NOMBREEMPRESA", nombreCompania);
                    
        			parametros.put("PR_SIGLA", SessionUtil.getCompaniaIngreso().getSigla());

                    archivoDescarga = JsfUtil.exportarStreamed(
                                    "002132PARAFISCALESIDIPRON", parametros,
                                    ConectorPool.ESQUEMA_SYSMAN, formato);
                }
                catch (JRException | IOException | SystemException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }
}
    /**
     * Metodo ejecutado al cambiar el control Proceso
     * 
     * 
     */
public void cambiarProceso() {
    cargarListaMes1();
    mes = null;
    cargarListaAno1();
    anio = null;
    mes = null;
    periodo = null;
    listaMes1 = null;
    listaPeriodo1 = null;
    }
    /**
     * Metodo ejecutado al cambiar el control Ano1
     * 
     * 
     */
public void cambiarAno1() {
    cargarListaMes1();
    mes = null;
    periodo = null;
    listaPeriodo1 = null;
    }
public void cambiarMes1() {
    cargarListaPeriodo1();
    periodo = null;
}
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable periodo
     * 
     * @return  periodo
     */
public String getPeriodo() {
        return periodo;
    }
    /**
     * Asigna la variable  periodo
     * 
     * @param  periodo
     * Variable a asignar en  periodo
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }
    /**
     * Retorna la variable proceso
     * 
     * @return  proceso
     */
public String getProceso() {
        return proceso;
    }
    /**
     * Asigna la variable  proceso
     * 
     * @param  proceso
     * Variable a asignar en  proceso
     */
    public void setProceso(String proceso) {
        this.proceso = proceso;
    }
    /**
     * Retorna la variable ano1
     * 
     * @return  ano1
     */
public String getAno1() {
        return anio;
    }
    /**
     * Asigna la variable  ano1
     * 
     * @param  ano1
     * Variable a asignar en  ano1
     */
    public void setAno1(String ano1) {
        this.anio = ano1;
    }
    /**
     * Retorna la variable mes1
     * 
     * @return  mes1
     */
public String getMes1() {
        return mes;
    }
    /**
     * Asigna la variable  mes1
     * 
     * @param  mes1
     * Variable a asignar en  mes1
     */
    public void setMes1(String mes1) {
        this.mes = mes1;
    }
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
    /**
     * Retorna la lista listaPeriodo1
     * 
     * @return listaPeriodo1
     */
public List<Registro> getListaPeriodo1() {
        return listaPeriodo1;
    }
    /**
     * Asigna la lista listaPeriodo1
     * 
     * @param listaPeriodo1
     * Variable a asignar en  listaPeriodo1
     */
public void setListaPeriodo1(List<Registro> listaPeriodo1) {
        this.listaPeriodo1 = listaPeriodo1;
    }
    /**
     * Retorna la lista listaProceso
     * 
     * @return listaProceso
     */
public List<Registro> getListaProceso() {
        return listaProceso;
    }
    /**
     * Asigna la lista listaProceso
     * 
     * @param listaProceso
     * Variable a asignar en  listaProceso
     */
public void setListaProceso(List<Registro> listaProceso) {
        this.listaProceso = listaProceso;
    }
    /**
     * Retorna la lista listaAno1
     * 
     * @return listaAno1
     */
public List<Registro> getListaAno1() {
        return listaAno1;
    }
    /**
     * Asigna la lista listaAno1
     * 
     * @param listaAno1
     * Variable a asignar en  listaAno1
     */
public void setListaAno1(List<Registro> listaAno1) {
        this.listaAno1 = listaAno1;
    }
    /**
     * Retorna la lista listaMes1
     * 
     * @return listaMes1
     */
public List<Registro> getListaMes1() {
        return listaMes1;
    }
    /**
     * Asigna la lista listaMes1
     * 
     * @param listaMes1
     * Variable a asignar en  listaMes1
     */
public void setListaMes1(List<Registro> listaMes1) {
        this.listaMes1 = listaMes1;
        
    }
/**
 * Retorna la compania
 * 
 * @return NombreCompania
 */
public String getNombreCompania() {
    return nombreCompania;
    }
}
