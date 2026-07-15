/*-
 * FuncionarioControlador.java
 *
 * 1.0
 * 
 * 18/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.viaticos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.viaticos.enums.FuncionarioControladorEnum;
import com.sysman.viaticos.enums.FuncionarioControladorUrlEnum;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador de la forma <code>funcionario</code>. Fue migrado del
 * formulario <code>Funcionario</code> de Access.
 *
 * @version 1.0, 18/01/2018
 * @author pespitia
 */
@ManagedBean
@ViewScoped
public class FuncionarioControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual el usuario inicio sesion.
     */
    private final String modulo = SessionUtil.getModulo();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>.
     */
    private final String cCompania = GeneralParameterEnum.COMPANIA.getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>CODIGOINICIAL</code>.
     */
    private final String cCodigoInicial = FuncionarioControladorEnum.CODIGOINICIAL
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>ID_DE_EMPLEADO</code>.
     */
    private final String cNumeroDcto = GeneralParameterEnum.NUMERO_DCTO
                    .getName();

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que contiene el codigo del empleado del item
     * seleccionado en el combo funcionario inicial (CB5429).
     */
    private String funcionarioIni;

    /**
     * Atributo que contiene el codigo del empleado del item
     * seleccionado en el combo funcionario final (CB5430).
     */
    private String funcionarioFin;

    /**
     * Atributo que contiene la fecha inicial ingresada en el campo
     * fecha inicial (CP50580).
     */
    private Date fechaInicial;

    /**
     * Atributo que contiene la fecha final ingresada en el campo
     * fecha final (CP50581).
     */
    private Date fechaFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que contiene los items del combo funcionario inicial
     * (CB5429).
     */
    private RegistroDataModelImpl listaCbFuncionarioIni;

    /**
     * Lista que contiene los items del combo funcionario final
     * (CB5430).
     */
    private RegistroDataModelImpl listaCbFuncionarioFin;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FuncionarioControlador
     */
    public FuncionarioControlador() {
        super();

        compania = SessionUtil.getCompania();

        try {
            // 1615
            numFormulario = GeneralCodigoFormaEnum.FUNCIONARIO_CONTROLADOR
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
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCbFuncionarioIni();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        fechaInicial = fechaFinal = new Date();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista: <code>listaCbFuncionarioIni</code>, asociada al
     * combo funcionario inicial (CB5429).
     */
    public void cargarListaCbFuncionarioIni() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FuncionarioControladorUrlEnum.URL5197
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaCbFuncionarioIni = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNumeroDcto);
    }

    /**
     * Carga la lista: <code>listaCbFuncionarioFin</code>, asociada al
     * combo funcionario final (CB5430).
     */
    public void cargarListaCbFuncionarioFin() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FuncionarioControladorUrlEnum.URL6653
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cCodigoInicial, funcionarioIni);

        listaCbFuncionarioFin = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNumeroDcto);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton PDF (BT2868) en la vista.
     */
    public void oprimirBtPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton EXCEL (BT2869) en la
     * vista.
     */
    public void oprimirBtExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        generarReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila del combo funcionario
     * inicial (CB5429).
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbFuncionarioIni(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        funcionarioIni = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNumeroDcto), "")
                        .toString();

        cargarListaCbFuncionarioFin();
    }

    /**
     * Metodo ejecutado al seleccionar una fila del combo funcionario
     * final (CB5430).
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbFuncionarioFin(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        funcionarioFin = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNumeroDcto), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    /**
     * Genera un reporte con un determinado formato.
     * 
     * @param formato
     * Tipo de documento a generar.
     */
    private void generarReporte(FORMATOS formato) {
        Map<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        String reporte = "001652InfFuncionarioH";

        // <REEMPLAZAR VARIABLES EN CONSULTA>
        reemplazar.put("fechaInicial",
                        SysmanFunciones.formatearFecha(fechaInicial));

        reemplazar.put("fechaFinal",
                        SysmanFunciones.formatearFecha(fechaFinal));

        reemplazar.put("funcionarioIni",
                        SysmanFunciones.concatenar("'", funcionarioIni, "'"));

        reemplazar.put("funcionarioFin",
                        SysmanFunciones.concatenar("'", funcionarioFin, "'"));

        // </REEMPLAZAR VARIABLES EN CONSULTA>

        // <ENVIAR PARAMETROS AL REPORTE>
        // </ENVIAR PARAMETROS AL REPORTE>

        Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                        reemplazar, parametros);

        try {
            /*-aqui reporte hace referencia al nombre del reporte*/
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable funcionarioIni
     * 
     * @return funcionarioIni
     */
    public String getFuncionarioIni() {
        return funcionarioIni;
    }

    /**
     * Asigna la variable funcionarioIni
     * 
     * @param funcionarioIni
     * Variable a asignar en funcionarioIni
     */
    public void setFuncionarioIni(String funcionarioIni) {
        this.funcionarioIni = funcionarioIni;
    }

    /**
     * Retorna la variable funcionarioFin
     * 
     * @return funcionarioFin
     */
    public String getFuncionarioFin() {
        return funcionarioFin;
    }

    /**
     * Asigna la variable funcionarioFin
     * 
     * @param funcionarioFin
     * Variable a asignar en funcionarioFin
     */
    public void setFuncionarioFin(String funcionarioFin) {
        this.funcionarioFin = funcionarioFin;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCbFuncionarioIni() {
        return listaCbFuncionarioIni;
    }

    public void setListaCbFuncionarioIni(
        RegistroDataModelImpl listaCbFuncionarioIni) {
        this.listaCbFuncionarioIni = listaCbFuncionarioIni;
    }

    public RegistroDataModelImpl getListaCbFuncionarioFin() {
        return listaCbFuncionarioFin;
    }

    public void setListaCbFuncionarioFin(
        RegistroDataModelImpl listaCbFuncionarioFin) {
        this.listaCbFuncionarioFin = listaCbFuncionarioFin;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
