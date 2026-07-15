/*-
 * CobroPersuasivoControlador.java
 *
 * 1.0
 *
 * 17/03/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosDosRemote;
import com.sysman.serviciospublicos.enums.CobroPersuasivoControladorEnum;
import com.sysman.serviciospublicos.enums.CobroPersuasivoControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 * Clase que permite controlar y realizar los eventos para la
 * funcionalidad de cobro persuasivo.
 *
 * @version 1, 03/02/2017 12:56:56 -- Modificado por jrodriguezr
 * @author jrodriguezr
 *
 * @version 2, 17/05/2017 Proceso de refactoring.
 * @author jrodrigueza
 *
 * -- Modificado por lcortes 13/06/2017. Se reemplaza el valor del
 * atributo numFormulario por el enumerado correspondiente y se
 * reemplaza el numero de formulario enviado en el metodo
 * cargarModalDatosFlash por el enumerado correspondiente al
 * formulario a cargar.
 *
 */
@ManagedBean
@ViewScoped
public class CobroPersuasivoControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Cadena que representa la seleccion de todos los ciclos.
     */
    private static final String STR_TODOS = "TODOS";
    /**
     * C�digo que identifica el tipo de formato para Cobro Persuasivo.
     */
    private static final int TIPO_FORMATO_COBRO_PERSUASIVO = 27;
    /**
     * Valor del atributo correspondiente al indicador consecutivo
     * automatico
     */
    private boolean consecAuto;
    /**
     * Valor del atributo correspondiente al codigo Inicial para el
     * filtro de la consulta.
     */
    private String codigoInicial;
    /**
     * Valor del atributo correspondiente al codigo Final para el
     * filtro de la consulta.
     */
    private String codigoFinal;
    /**
     * Valor del atributo correspondiente al ciclo para el filtro de
     * la consulta.
     */
    private String ciclo;

    /**
     * Valor del atributo correspondiente al valor de la plantilla a
     * generar.
     */
    private String plantilla;
    /**
     * Valor del atributo correspondiente al valor de la fecha de la
     * plantilla a generar.
     */
    private String fechaPlantilla;
    /**
     * Valor del atributo correspondiente al valor de atraso Superior
     * para el filtro de la consulta.
     */
    private Date fechaEmision;
    /**
     * Valor del atributo correspondiente al valor de atraso para el
     * filtro de la consulta.
     */
    private String atraso;
    /**
     * Valor del atributo correspondiente al valor de deuda para el
     * filtro de la consulta.
     */
    private String deuda;
    /**
     * Valor del atributo correspondiente al valor de deuda Final para
     * el filtro de la consulta.
     */
    private String deudaFinal;
    /**
     * Valor del atributo correspondiente al valor de atraso Superior
     * para el filtro de la consulta.
     */
    private String atrasoSuperior;
    /**
     * Valor del atributo correspondiente al consecutivo inicial
     */
    private String consecutivo;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de objetos pertenecientes al combo Codigo Inicial
     */
    private RegistroDataModelImpl listaCodigoInicial;
    /**
     * Lista de objetos pertenecientes al combo Codigo Final
     */
    private RegistroDataModelImpl listaCodigoFinal;
    /**
     * Lista de objetos pertenecientes al combo Ciclo
     */
    private RegistroDataModelImpl listaCiclo;
    /**
     * Lista de objetos pertenecientes al combo formato
     */
    private RegistroDataModelImpl listaFormato;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    @EJB
    private EjbServiciosPublicosDosRemote ejbServiciosPublicosDos;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de CobroPersuasivoControlador
     */
    public CobroPersuasivoControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.COBRO_PERSUASIVO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            consecAuto = false;
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios.
     */
    @PostConstruct
    public void inicializar() {
        atraso = "1";
        atrasoSuperior = "999";
        deuda = "1";
        deudaFinal = "99999999";
        fechaEmision = new Date();
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        cargarListaCiclo();
        cargarListaFormato();
        registro = new Registro();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario.
     */
    @Override
    public void asignarOrigenDatos() {
        // No aplica para los formularios de datos sin grilla.
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCodigoInicial
     *
     */
    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CobroPersuasivoControladorUrlEnum.URL2559
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), traerNumeroCiclo());
        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGORUTA.getName());
    }

    /**
     * Carga la lista listaCodigoFinal
     */
    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CobroPersuasivoControladorUrlEnum.URL2779
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), traerNumeroCiclo());
        param.put(CobroPersuasivoControladorEnum.PARAM0.getValue(),
                        codigoInicial);
        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGORUTA.getName());
    }

    /**
     * Trae el numero del ciclo. Si se selecciono TODOS retorna -1 y
     * si es nulo retorna 0.
     *
     * @return numero de ciclo
     */
    private int traerNumeroCiclo() {
        int numeroCiclo = 0;
        if (ciclo != null) {
            numeroCiclo = STR_TODOS.equals(ciclo) ? -1
                : Integer.parseInt(ciclo);
        }
        return numeroCiclo;
    }

    /**
     * Carga la lista listaCiclo
     */
    public void cargarListaCiclo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CobroPersuasivoControladorUrlEnum.URL2999
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());
    }

    /**
     * Carga la lista listaFormato
     */
    public void cargarListaFormato() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CobroPersuasivoControladorUrlEnum.URL3149
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(CobroPersuasivoControladorEnum.PARAM1.getValue(),
                        TIPO_FORMATO_COBRO_PERSUASIVO);
        listaFormato = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "LLAVE");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control ConsecAuto
     */
    public void cambiarConsecAuto() {
        // <CODIGO_DESARROLLADO>
        if (consecAuto) {
            String criterio = " COMPANIA = ''" + compania + "'' ";
            String campo = "IDCOBRO";
            String inicial = "1";
            try {
                long numero = ejbSysmanUtil.generarConsecutivoConValorInicial(
                                "SP_COBROSPERSUASIVOS", criterio, campo,
                                inicial);
                numero++;
                consecutivo = String.valueOf(numero);
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGORUTA.getName())
                        .toString();
        cargarListaCodigoFinal();
        codigoFinal = null;
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGORUTA.getName())
                        .toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCiclo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ciclo = registroAux.getCampos().get("NUMERO").toString();
        cargarListaCodigoInicial();
        codigoInicial = codigoFinal = null;
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFormato
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFormato(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        plantilla = registroAux.getCampos().get("CODIGO").toString();
        fechaPlantilla = registroAux.getCampos().get("FECHA").toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getInforme();
        // </CODIGO_DESARROLLADO>
    }

    private void getInforme() {
        String consecutivos = registrarPersuasivo();
        if ((plantilla != null) && (consecutivos != null)) {
            String consecutivoInicial = consecutivos.split(",")[0];
            String consecutivoFinal = consecutivos.split(",")[1];
            String strNombreDocumento = "Cobro Persuasivo No."
                + consecutivoInicial + " hasta " + consecutivoFinal;
            String[] campos = new String[3];
            String[] valores = new String[3];
            campos[0] = "codigoPlantilla";
            campos[1] = "fechaPlantilla";
            campos[2] = "nombreDocDescarga";

            valores[0] = plantilla;
            try {
                valores[1] = SysmanFunciones.formatearFecha(SysmanFunciones
                                .convertirAFecha(fechaPlantilla));
            }
            catch (ParseException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            valores[2] = strNombreDocumento;

            HashMap<String, String> variablesConsultaW = new HashMap<>();
            variablesConsultaW.put("s$compania$s", "'" + compania + "'");
            variablesConsultaW.put("s$ciclo$s", STR_TODOS.equals(ciclo) ? ""
                : "      AND USUARIO.CICLO         = " + ciclo);
            variablesConsultaW.put("s$codigoInicial$s",
                            "'" + consecutivoInicial + "'");
            variablesConsultaW.put("s$codigoFinal$s",
                            "'" + consecutivoFinal + "'");

            // variables por parametro para documento word
            SessionUtil.setSessionVar("variablesConsultaWord",
                            variablesConsultaW);

            SessionUtil.cargarModalDatosFlash(
                            Integer.toString(
                                            GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                                            .getCodigo()),
                            SessionUtil.getModulo(), campos, valores);
        }
    }

    /**
     * Ejecuta la funcion registrarPersuasivo.
     *
     * @return persuasivo
     */
    private String registrarPersuasivo() {
        String persuasivo = null;
        String nit = SessionUtil.getCompaniaIngreso().getNit();
        String codigoConsecutivo = SysmanFunciones
                        .validarVariableVacio(consecutivo)
                            ? "0" : consecutivo;
        BigDecimal deudaInicial = new BigDecimal(deuda);
        BigDecimal deudaFin = new BigDecimal(deudaFinal);
        String usuario = SessionUtil.getUser().getCodigo();
        try {
            persuasivo = ejbServiciosPublicosDos.registrarPersuasivo(compania,
                            nit, codigoConsecutivo, ciclo, codigoInicial,
                            codigoFinal, atraso, atrasoSuperior, deudaInicial,
                            deudaFin, usuario);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return persuasivo;
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        atraso = "1";
        atrasoSuperior = "999";
        deuda = "1";
        deudaFinal = "99999999";
        consecAuto = false;
        fechaEmision = new Date();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     *
     * @return VARIABLE
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     * @return VARIABLE
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     *
     * @return VARIABLE
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     *
     * @return VARIABLE
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     *
     * @return VARIABLE
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     *
     * @return VARIABLE
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable consecAuto
     *
     * @return consecAuto
     */
    public boolean getConsecAuto() {
        return consecAuto;
    }

    /**
     * Asigna la variable consecAuto
     *
     * @param consecAuto
     * Variable a asignar en consecAuto
     */
    public void setConsecAuto(boolean consecAuto) {
        this.consecAuto = consecAuto;
    }

    /**
     * Retorna la variable codigoInicial
     *
     * @return codigoInicial
     */
    public String getCodigoInicial() {
        return codigoInicial;
    }

    /**
     * Asigna la variable codigoInicial
     *
     * @param codigoInicial
     * Variable a asignar en codigoInicial
     */
    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    /**
     * Retorna la variable codigoFinal
     *
     * @return codigoFinal
     */
    public String getCodigoFinal() {
        return codigoFinal;
    }

    /**
     * Asigna la variable codigoFinal
     *
     * @param codigoFinal
     * Variable a asignar en codigoFinal
     */
    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

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
     * Retorna la variable plantilla
     *
     * @return plantilla
     */
    public String getPlantilla() {
        return plantilla;
    }

    /**
     * Asigna la variable plantilla
     *
     * @param plantilla
     * Variable a asignar en plantilla
     */
    public void setPlantilla(String plantilla) {
        this.plantilla = plantilla;
    }

    /**
     * Retorna la variable fechaEmision
     *
     * @return fechaEmision
     */
    public Date getFechaEmision() {
        return fechaEmision;
    }

    /**
     * Asigna la variable fechaEmision
     *
     * @param fechaEmision
     * Variable a asignar en fechaEmision
     */
    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    /**
     * Retorna la variable atraso
     *
     * @return atraso
     */
    public String getAtraso() {
        return atraso;
    }

    /**
     * Asigna la variable atraso
     *
     * @param atraso
     * Variable a asignar en atraso
     */
    public void setAtraso(String atraso) {
        this.atraso = atraso;
    }

    /**
     * Retorna la variable deuda
     *
     * @return deuda
     */
    public String getDeuda() {
        return deuda;
    }

    /**
     * Asigna la variable deuda
     *
     * @param deuda
     * Variable a asignar en deuda
     */
    public void setDeuda(String deuda) {
        this.deuda = deuda;
    }

    /**
     * Retorna la variable deudaFinal
     *
     * @return deudaFinal
     */
    public String getDeudaFinal() {
        return deudaFinal;
    }

    /**
     * Asigna la variable deudaFinal
     *
     * @param deudaFinal
     * Variable a asignar en deudaFinal
     */
    public void setDeudaFinal(String deudaFinal) {
        this.deudaFinal = deudaFinal;
    }

    /**
     * Retorna la variable atrasoSuperior
     *
     * @return atrasoSuperior
     */
    public String getAtrasoSuperior() {
        return atrasoSuperior;
    }

    /**
     * Asigna la variable atrasoSuperior
     *
     * @param atrasoSuperior
     * Variable a asignar en atrasoSuperior
     */
    public void setAtrasoSuperior(String atrasoSuperior) {
        this.atrasoSuperior = atrasoSuperior;
    }

    /**
     * Retorna la variable consecutivo
     *
     * @return consecutivo
     */
    public String getConsecutivo() {
        return consecutivo;
    }

    /**
     * Asigna la variable consecutivo
     *
     * @param consecutivo
     * Variable a asignar en consecutivo
     */
    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodigoInicial
     *
     * @return listaCodigoInicial
     */
    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    /**
     * Asigna la lista listaCodigoInicial
     *
     * @param listaCodigoInicial
     * Variable a asignar en listaCodigoInicial
     */
    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    /**
     * Retorna la lista listaCodigoFinal
     *
     * @return listaCodigoFinal
     */
    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    /**
     * Asigna la lista listaCodigoFinal
     *
     * @param listaCodigoFinal
     * Variable a asignar en listaCodigoFinal
     */
    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }

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
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    /**
     * Retorna la lista listaFormato
     *
     * @return listaFormato
     */
    public RegistroDataModelImpl getListaFormato() {
        return listaFormato;
    }

    /**
     * Asigna la lista listaFormato
     *
     * @param listaFormato
     * Variable a asignar en listaFormato
     */
    public void setListaFormato(RegistroDataModelImpl listaFormato) {
        this.listaFormato = listaFormato;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
