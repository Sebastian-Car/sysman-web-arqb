/*-
 * NovedadVuelveControlador.java
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
import com.sysman.serviciospublicos.enums.NovedadVuelveControladorEnum;
import com.sysman.serviciospublicos.enums.NovedadVuelveControladorUrlEnum;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Este controlador es el encargado de generar una hoja de Excel la
 * cual exporta las novedades externas en el servicios publicos.
 *
 * @version 1.0, 27/07/2017
 * @author jeguerrero Se hace refactory del controlador
 */
@ManagedBean
@ViewScoped

public class NovedadVuelveControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable encargada de almancenar temporalmente lo seleccionado
     * por el Combo ciclo en el formulario
     */
    private String ciclo;
    /**
     * Variable encargada de almancenar temporalmente lo seleccionado
     * por el Combo Empresa en el formulario
     */
    private String empresa;
    /**
     * Variable encargada de almancenar temporalmente lo seleccionado
     * por el Combo ano en el formulario
     */
    private String ano;
    /**
     * Variable encargada de almancenar temporalmente lo seleccionado
     * por el Combo periodo en el formulario
     */
    private String periodo;
    /**
     * Variable encargada de almacenar temporlamente lo seleccionado
     * en el campo fecha final
     */
    private Date fechaFinal;
    /**
     * Variable encargada de almacenar temporlamente lo seleccionado
     * en el campo fecha Inicial
     */
    private Date fechaIncial;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * en el combo ciclo del formulario.
     */
    private RegistroDataModelImpl listaCiclo;

    private boolean visibleFechas;
    private String entreFechas;
    /**
     * Lista encargada de almacenar temporalmente el resultado de la
     * llamada de la base de datos en la tabla Empresa_terceriza
     */

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    private List<Registro> listaEmpresa;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de NovedadVuelveControlador
     */
    public NovedadVuelveControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.NOVEDADVUELVE_CONTROLADOR
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

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaCiclo();
        cargarListaEmpresa();

        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
        validarEntreFechas();

    }

    private void validarEntreFechas() {
        try {

            entreFechas = ejbSysmanUtil.consultarParametro(
                            compania,
                            NovedadVuelveControladorEnum.PARAM0.getValue(),
                            SessionUtil.getModulo(),
                            new Date(), false);

            if ("SI".equals(entreFechas)) {
                visibleFechas = true;
            }
            else {
                visibleFechas = false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCiclo
     *
     * Metodo encargado de hacer la llamda a la base de datos y
     * almacenar la respuesta en la lista LIstaCiclo.
     */
    public void cargarListaCiclo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NovedadVuelveControladorUrlEnum.URL5197
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());
    }
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    /**
     * 
     * Carga la lista listaEmpresa Metodo encargado de hacer la llamda
     * a la base de datos y recibir la respuesta en la listaEmpresa
     */
    public void cargarListaEmpresa() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaEmpresa = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            NovedadVuelveControladorUrlEnum.URL6029
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton aceptar en la vista
     * Además, es el encargado de invocar el metodo que genera el
     * excel-
     *
     */
    public void oprimiraceptar() {
        // <CODIGO_DESARROLLADO>

        if (!validarFechas()) {
            return;
        }

        archivoDescarga = null;
        generarExcel();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCiclo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ciclo = retornarString(registroAux,
                        GeneralParameterEnum.NUMERO.getName());
        periodo = retornarString(registroAux,
                        GeneralParameterEnum.PERIODO.getName());
        ano = retornarString(registroAux, GeneralParameterEnum.ANO.getName());
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ciclo
     * 
     * @return ciclo
     */
    public String getCiclo() {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     * 
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    /**
     * Retorna la variable empresa
     * 
     * @return empresa
     */
    public String getEmpresa() {
        return empresa;
    }

    /**
     * Asigna la variable empresa
     * 
     * @param empresa
     * Variable a asignar en empresa
     */
    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    /**
     * Retorna la variable ano
     * 
     * @return ano
     */
    public String getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(String ano) {
        this.ano = ano;
    }

    /**
     * Retorna la variable periodo
     * 
     * @return periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * Asigna la variable periodo
     * 
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    /**
     * Retorna la variable fechaFinal
     * 
     * @return fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * Asigna la variable fechaFinal
     * 
     * @param fechaFinal
     * Variable a asignar en fechaFinal
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Retorna la variable fechaIncial
     * 
     * @return fechaIncial
     */
    public Date getFechaIncial() {
        return fechaIncial;
    }

    /**
     * Asigna la variable fechaIncial
     * 
     * @param fechaIncial
     * Variable a asignar en fechaIncial
     */
    public void setFechaIncial(Date fechaIncial) {
        this.fechaIncial = fechaIncial;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>

    /**
     * Retorna la lista listaEmpresa
     * 
     * @return listaEmpresa
     */
    public List<Registro> getListaEmpresa() {
        return listaEmpresa;
    }

    public RegistroDataModelImpl getListaCiclo() {
        return listaCiclo;
    }

    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    /**
     * Asigna la lista listaEmpresa
     * 
     * @param listaEmpresa
     * Variable a asignar en listaEmpresa
     */
    public void setListaEmpresa(List<Registro> listaEmpresa) {
        this.listaEmpresa = listaEmpresa;
    }

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();

    }

    public boolean isVisibleFechas() {
        return visibleFechas;
    }

    public void setVisibleFechas(boolean visibleFechas) {
        this.visibleFechas = visibleFechas;
    }

    private void generarExcel() {

        try {
            archivoDescarga = null;
            String fechaIniAux = SysmanFunciones.formatearFecha(fechaIncial);
            String fechaFinAux = SysmanFunciones.formatearFecha(fechaIncial);

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("empresa", empresa);
            reemplazar.put("ciclo", ciclo);
            reemplazar.put("ano", ano);
            reemplazar.put("periodo", periodo);
            reemplazar.put("fechaInicial",
                            fechaIniAux == null ? "NULL" : fechaIniAux);
            reemplazar.put("fechaFinal",
                            fechaFinAux == null ? "NULL" : fechaFinAux);

            String strSql = Reporteador
                            .resuelveConsulta(
                                            NovedadVuelveControladorEnum.PARAM2
                                                            .getValue(),
                                            Integer.parseInt(
                                                            SessionUtil.getModulo()),
                                            reemplazar);

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL97,
                            NovedadVuelveControladorEnum.PARAM1.getValue());
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private boolean validarFechas() {

        if ("SI".equals(entreFechas) && fechaIncial.after(fechaFinal)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB147"));
            return false;
        }

        return true;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
