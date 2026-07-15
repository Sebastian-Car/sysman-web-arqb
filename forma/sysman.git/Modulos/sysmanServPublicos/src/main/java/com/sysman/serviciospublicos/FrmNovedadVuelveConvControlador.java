/*-
 * FrmNovedadVuelveConvControlador.java
 *
 * 1.0
 * 
 * 27/07/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.FrmNovedadVuelveConvControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Clase que permite obtener un informe acerca de las novedades de los convenios a
 * partir de la seleccion de una serie de atributos
 *
 * @version 1.0, 27/07/2017
 * @author jreina
 */

@ManagedBean
@ViewScoped
public class  FrmNovedadVuelveConvControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;
    
    private final String modulo;
    //<DECLARAR_ATRIBUTOS>
    /**
     * Atributo que contiene el valor asignado al ciclo en la forma
     * del formulario.
     */
    private String ciclo;
    /**
     * Atributo que contiene el valor asignado al convenio en la forma
     * del formulario.
     */
    private String convenio;
    /**
     * Atributo que contiene el valor asignado al año en la forma
     * del formulario.
     */
    private String anio;
    /**
     * Atributo que contiene el valor asignado al periodo en la forma
     * del formulario.
     */
    private String periodo;
    /**
     * Atributo que contiene el valor asignado a la fecha inicial en la forma
     * del formulario.
     */
    private Date fechaInicial;
    /**
     * Atributo que contiene el valor asignado a la fecha final en la forma
     * del formulario.
     */
    private Date fechaFinal;
    
    /**
     * Atributo que contiene el valor asignado a la visibilidad de la fecha en la forma
     * del formulario.
     */
    private boolean visibleFecha;
    
    
    private boolean manejaFechas;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    
    //</DECLARAR_ATRIBUTOS>
    //<DECLARAR_PARAMETROS>
    //</DECLARAR_PARAMETROS>
    //<DECLARAR_LISTAS>
    private List<Registro> listaEMPRESA;
    //</DECLARAR_LISTAS>
    //<DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCiclo;
    //</DECLARAR_LISTAS_COMBO_GRANDE>
    
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;
    
    /**
     * Crea una nueva instancia de FrmNovedadVuelveConvControlador
     */
    public FrmNovedadVuelveConvControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo= SessionUtil.getModulo();
        try {
            numFormulario=GeneralCodigoFormaEnum.FORMULARIO_NOVEDADCONVENIO_CONTROLADOR.getCodigo();
            validarPermisos();
            //<INI_ADICIONAL>
            //</INI_ADICIONAL>
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
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
        //<CARGAR_LISTA>
        cargarListaEMPRESA();
        //</CARGAR_LISTA>
        //<CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCiclo();
        //</CARGAR_LISTA_COMBO_GRANDE>
        //<CREAR_ARBOLES>
        //</CREAR_ARBOLES>
        abrirFormulario();
    }
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario(){
        //<CODIGO_DESARROLLADO>
        
        try {
            String infNovedades = ejbSysmanUtilRemote.consultarParametro(
                            compania,
                            "GENERAR INFORME NOVEDADES EXTERNAS ENTRE FECHAS",
                            modulo, new Date(), true);
            
            manejaFechas = "SI".equals(infNovedades);
            
            if(manejaFechas){
                visibleFecha=true;
            }else{
                visibleFecha=false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        //</CODIGO_DESARROLLADO>
    }
    
    
    //<METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaEMPRESA
     *
     */
    public void cargarListaEMPRESA(){
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        try {
            listaEMPRESA = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmNovedadVuelveConvControladorUrlEnum.URL4996
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
     * Carga la lista listaCiclo
     *
     */
    public void cargarListaCiclo(){
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(FrmNovedadVuelveConvControladorUrlEnum.URL5582.getValue());   
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,true,"NUMERO");
    }
    //</METODOS_CARGAR_LISTA>
    //<METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdAceptar
     * en la vista
     *
     */
    public void oprimirCmdAceptar() {
        //<CODIGO_DESARROLLADO>
        archivoDescarga=null;
        generarReporte(FORMATOS.EXCEL);
        //</CODIGO_DESARROLLADO>
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdCancelar
     * en la vista
     *
     */
    public void oprimirCmdCancelar() {
        //<CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        //</CODIGO_DESARROLLADO>
    }
    
    
    private void generarReporte(FORMATOS formato) {
        try {
            Map<String, Object> reemplazos = new HashMap<>();

            String condicion = " AND H.FECHA_PAGO BETWEEN "
                + SysmanFunciones.formatearFecha(fechaInicial)
                + " AND " + SysmanFunciones.formatearFecha(fechaFinal)
                + " ";

            reemplazos.put("compania", "'" + compania + "'");
            reemplazos.put("ciclo", ciclo);
            reemplazos.put("anio", anio);
            reemplazos.put("periodo", "'" + periodo + "'");
            reemplazos.put("convenio", "'" + convenio + "'");
            reemplazos.put("condicion", manejaFechas ? condicion : " ");

            String strSql = Reporteador.resuelveConsulta(
                            "8000100NovedadesdeConvenios",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos);

            Long num = service.getConteoConsulta(strSql);
            if (num > 0) {
                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                                ConectorPool.ESQUEMA_SYSMAN,
                                formato);
            }
            else {
                JsfUtil.agregarMensajeError(idioma.getString(
                                "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
            }

        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    //</METODOS_BOTONES>
    //<METODOS_CAMBIAR>
    //</METODOS_CAMBIAR>
    //<METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCiclo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ciclo= registroAux.getCampos().get("NUMERO").toString();
        anio= registroAux.getCampos().get("ANO").toString();
        periodo= registroAux.getCampos().get("PERIODO").toString();
    }
    //</METODOS_COMBOS_GRANDES>
    //<METODOS_ARBOL>
    //</METODOS_ARBOL>
    //<SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ciclo
     * 
     * @return  ciclo
     */
    public String getCiclo() {
        return ciclo;
    }
    /**
     * Asigna la variable  ciclo
     * 
     * @param  ciclo
     * Variable a asignar en  ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }
    /**
     * Retorna la variable convenio
     * 
     * @return  convenio
     */
    public String getConvenio() {
        return convenio;
    }
    /**
     * Asigna la variable  convenio
     * 
     * @param  convenio
     * Variable a asignar en  convenio
     */
    public void setConvenio(String convenio) {
        this.convenio = convenio;
    }
    /**
     * Retorna la variable anio
     * 
     * @return  anio
     */
    public String getAnio() {
        return anio;
    }
    /**
     * Asigna la variable  anio
     * 
     * @param  anio
     * Variable a asignar en  anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }
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
     * Retorna la variable fechaInicial
     * 
     * @return  fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }
    /**
     * Asigna la variable  fechaInicial
     * 
     * @param  fechaInicial
     * Variable a asignar en  fechaInicial
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }
    /**
     * Retorna la variable fechaFinal
     * 
     * @return  fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }
    /**
     * Asigna la variable  fechaFinal
     * 
     * @param  fechaFinal
     * Variable a asignar en  fechaFinal
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }
    
    
    public boolean isVisibleFecha() {
        return visibleFecha;
    }
    
    
    public void setVisibleFecha(boolean visibleFecha) {
        this.visibleFecha = visibleFecha;
    }
    
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }
    //</SET_GET_ATRIBUTOS>
    //<SET_GET_PARAMETROS>
    //</SET_GET_PARAMETROS>
    //<SET_GET_LISTAS>
    /**
     * Retorna la lista listaEMPRESA
     * 
     * @return listaEMPRESA
     */
    public List<Registro> getListaEMPRESA() {
        return listaEMPRESA;
    }
    /**
     * Asigna la lista listaEMPRESA
     * 
     * @param listaEMPRESA
     * Variable a asignar en  listaEMPRESA
     */
    public void setListaEMPRESA(List<Registro> listaEMPRESA) {
        this.listaEMPRESA = listaEMPRESA;
    }
    //</SET_GET_LISTAS>
    //<SET_GET_LISTAS_COMBO_GRANDE>	
    /**
     * Retorna la lista listaCiclo
     * 
     * @return listaCiclo
     */
    public RegistroDataModelImpl getListaCiclo() {
        return listaCiclo;
    }
   

    /**
     * Asigna la lista listaCiclo
     * 
     * @param listaCiclo
     * Variable a asignar en  listaCiclo
     */
    
    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    
    
    //</SET_GET_LISTAS_COMBO_GRANDE>
}
