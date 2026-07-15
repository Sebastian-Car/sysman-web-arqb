/*-
 * SeguimientoReciprocasControlador.java
 *
 * 1.0
 * 
 * 09/10/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.chipfut;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.chipfut.enums.SeguimientoReciprocasControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 09/10/2018
 * @author jrojas
 */
@ManagedBean
@ViewScoped
public class SeguimientoReciprocasControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>

    private int vigencia;
    private String trimestre;
    private String codigoPlancontable;
    private String nombre;
    private String auxiliar;
    private String observaciones;
    private String trimestreGrilla;
    private String valorCorriente;
    private String valorNoCorriente;
    private String fechaobservaciones;
    private String ano;
    private String mes;
    private boolean consolidar;
    private String reporte;
    private String consulta;
    /**
     * Este atributo se usa como auxiliar del componente referencia de
     * archivos archivoPlano y funciona como contenedor del archivo
     * que se desea cargar
     */
    private UploadedFile archivoCargaarchivoPlano;

    private int trimestreNumero;
    private int trimestral; // guarda el número del trimestre al que
                            // pertenece para la grilla
                            // TRIMESTRENUMERO 3 = trimestral 3, 6 =6.
    private int numeroTrimestreinsercion; // lleva el trimestre en
                                          // número 1,2,3,4 para la
                                          // inserción
    private StreamedContent archivoDescarga;
    private long consecutivo;
    private List<Registro> listaVigencia;
    private List<Registro> listatrimestre;
    private RegistroDataModelImpl listacodigo;
    private RegistroDataModelImpl listacodigoE;
    private List<Registro> listaAno;
    private List<Registro> listames;
    private RegistroDataModelImpl listacodigoEntidadReciproca;
    private RegistroDataModelImpl listacodigoEntidadReciprocaE;

    @EJB
    private EjbSysmanUtil ejbSysmanUtil;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de SeguimientoReciprocasControlador
     */
    public SeguimientoReciprocasControlador() {
        super();
        modulo = SessionUtil.getModulo();
        compania = SessionUtil.getCompania();
        vigencia = SysmanFunciones.ano(new Date());

        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_SEGUIMIENTORECIPROCAS_CONTROLADOR
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
        enumBase = GenericUrlEnum.SEGUIMIENTO_RECIPROCAS;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        abrirFormulario();
        cargarListaAno();
        // <CARGAR_LISTA>

        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>

    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override

    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), vigencia);
        parametrosListado.put("TRIMESTRE", trimestreNumero);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaVigencia
     *
     */
    public void cargarListaVigencia() {
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaVigencia = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SeguimientoReciprocasControladorUrlEnum.URL1742001
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listatrimestre
     *
     */

    /**
     * 
     * Carga la lista listacodigo
     *
     */
    public void cargarListacodigo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SeguimientoReciprocasControladorUrlEnum.URL16164
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()));

        listacodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listacodigo
     *
     */
    public void cargarListacodigoE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SeguimientoReciprocasControladorUrlEnum.URL16164
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), vigencia);

        listacodigoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListacodigoEntidadReciproca() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SeguimientoReciprocasControladorUrlEnum.URL14178
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacodigoEntidadReciproca = new RegistroDataModelImpl(
                        urlBean.getUrl(), urlBean.getUrlConteo().getUrl(),
                        param, true, "CODIGOEQUIVALENTE");
    }

    public void cargarListacodigoEntidadReciprocaE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SeguimientoReciprocasControladorUrlEnum.URL14178
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacodigoEntidadReciprocaE = new RegistroDataModelImpl(
                        urlBean.getUrl(), urlBean.getUrlConteo().getUrl(),
                        param, true, "CODIGOEQUIVALENTE");
    }

    public void oprimirimprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirimprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CargarArchivo en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
    public void oprimirCargarArchivo() {
        // <CODIGO_DESARROLLADO>

        String[] campos = { "opcion", "consecutivo" };

        Object[] valores = { SessionUtil.getMenuActual(), consecutivo };

        SessionUtil.cargarModalDatosFlash(
                        String.valueOf(1813),
                        SessionUtil.getModulo(), campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al cargar un archivo desde el control Excel
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     *
     */

    private void generarReporte(ReportesBean.FORMATOS formato) {
        // Creacion arreglos
        HashMap<String, Object> reemplazar = new HashMap<>();
        HashMap<String, Object> parametros = new HashMap<>();

        // Codigo del reporte

        reporte = "001952Seguimientoreciprocas";

        // <REEMPLAZAR VARIABLES EN CONSULTA>
        reemplazar.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        reemplazar.put("trimestre", trimestreNumero);
        reemplazar.put("ano", vigencia);
        reemplazar.put("condicion", consolidar ? 1 : 0);

        // </REEMPLAZAR VARIABLES EN CONSULTA
        try {
            // <ENVIAR PARAMETROS AL REPORTE>
            parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso()
                            .getNombre().toUpperCase());

            // </ENVIAR PARAMETROS AL REPORTE>
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo),
                            reemplazar, parametros);
            /*-aqui reporte hace referencia al nombre del reporte*/
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }

        catch (JRException | IOException
                        | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaano
     *
     */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SeguimientoReciprocasControladorUrlEnum.URL4001
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            Logger.getLogger(SeguimientoReciprocasControladorUrlEnum.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listames
     *
     */
    public void cargarListames() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("TRIMESTRE", trimestral);

        try {
            listames = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SeguimientoReciprocasControladorUrlEnum.URL7044
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            Logger.getLogger(SeguimientoReciprocasControladorUrlEnum.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("VALOR_CORRIENTE", 0);
        registro.getCampos().put("VALOR_NO_CORRIENTE", 0);
        cargarListacodigo();
        // </CODIGO_DESARROLLADO>

    }

    public void cambiartrimestreGrilla() {
        // <CODIGO_DESARROLLADO>
        trimestreGrilla = registro.getCampos().get("TRIMESTRE").toString();
        if ("Primero".equals(trimestreGrilla)) {
            trimestral = 3;
            numeroTrimestreinsercion = 1;

        }
        else if ("Segundo".equals(trimestreGrilla)) {
            trimestral = 6;
            numeroTrimestreinsercion = 2;
        }
        else if ("Tercero".equals(trimestreGrilla)) {
            trimestral = 9;
            numeroTrimestreinsercion = 3;
        }
        else if ("Cuarto".equals(trimestreGrilla)) {
            trimestral = 12;
            numeroTrimestreinsercion = 4;
        }
        cargarListames();
        // </CODIGO_DESARROLLADO>

    }

    public void cambiarcodigoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        GeneralParameterEnum.NOMBRE.getName(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcodigoEntidadReciprocaC(int rowNum) {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza
        // como lo muestra la siguiente linea
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        "ENTIDAD_RECIPROCA",
                        registro.getCampos().get("ENTIDAD_RECIPROCA"));
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        "CODIGO_ENTIDAD_RECIPROCA",
                        registro.getCampos().get("CODIGO_ENTIDAD_RECIPROCA"));

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarVigencia() {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>

    }

    /**
     * Metodo ejecutado al cambiar el control trimestre
     * 
     * 
     */
    public void cambiartrimestre() {
        // <CODIGO_DESARROLLADO>

        if ("Primero".equals(trimestre)) {
            trimestreNumero = 1;

        }
        else if ("Segundo".equals(trimestre)) {
            trimestreNumero = 2;
        }
        else if ("Tercero".equals(trimestre)) {
            trimestreNumero = 3;
        }
        else if ("Cuarto".equals(trimestre)) {
            trimestreNumero = 4;

        }
        trimestreGrilla = trimestre;

        reasignarOrigen();

        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilacodigo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacodigo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacodigoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get("CODIGO").toString();
        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos().get("NOMBRE"));
    }

    public void seleccionarFilacodigoEntidadReciproca(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CODIGO_ENTIDAD_RECIPROCA",
                        registroAux.getCampos().get("CODIGOEQUIVALENTE"));
        registro.getCampos().put("ENTIDAD_RECIPROCA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));

    }

    public void seleccionarFilacodigoEntidadReciprocaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get("CODIGOEQUIVALENTE").toString();
        registro.getCampos().put("ENTIDAD_RECIPROCA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
        registro.getCampos().put("CODIGO_ENTIDAD_RECIPROCA",
                        registroAux.getCampos().get("CODIGOEQUIVALENTE"));
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        trimestre = "Primero";
        cambiartrimestre();
        reasignarOrigen();
        cargarListaVigencia();
        cargarListacodigoE();
        cargarListacodigoEntidadReciproca();
        cargarListacodigoEntidadReciprocaE();

        // </CODIGO_DESARROLLADO>

    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * DOCUMENTACION ADICIONAL
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        generaConsecutivo());
        registro.getCampos().put("TRIMESTRE", numeroTrimestreinsercion);

        // </CODIGO_DESARROLLADO>

        return true;
    }

    public long generaConsecutivo() {
        try {

            String condicion = SysmanFunciones.concatenar("COMPANIA = ''",
                            compania, "'' AND ANO = ",
                            registro.getCampos()
                                            .get(GeneralParameterEnum.ANO
                                                            .getName())
                                            .toString(),
                            " AND MES = ",
                            registro.getCampos()
                                            .get(GeneralParameterEnum.MES
                                                            .getName())
                                            .toString());
            consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            "SEGUIMIENTO_RECIPROCAS", condicion,
                            "CONSECUTIVO", "1");

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return consecutivo;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * DOCUMENTACION ADICIONAL
     * 
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
     * 
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
     * 
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
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override

    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove(GeneralParameterEnum.MES.getName());
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
        registro.getCampos().remove(GeneralParameterEnum.CONSECUTIVO.getName());
        registro.getCampos().remove("TRIMESTRE");
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable vigencia
     * 
     * @return vigencia
     */
    public int getVigencia() {
        return vigencia;
    }

    /**
     * Asigna la variable vigencia
     * 
     * @param vigencia
     * Variable a asignar en vigencia
     */
    public void setVigencia(int vigencia) {
        this.vigencia = vigencia;
    }

    /**
     * Retorna la variable trimestre
     * 
     * @return trimestre
     */
    public String getTrimestre() {
        return trimestre;
    }

    /**
     * Asigna la variable trimestre
     * 
     * @param trimestre
     * Variable a asignar en trimestre
     */
    public void setTrimestre(String trimestre) {
        this.trimestre = trimestre;
    }

    /**
     * Retorna la variable codigoPlancontable
     * 
     * @return codigoPlancontable
     */
    public String getCodigoPlancontable() {
        return codigoPlancontable;
    }

    /**
     * Asigna la variable codigoPlancontable
     * 
     * @param codigoPlancontable
     * Variable a asignar en codigoPlancontable
     */
    public void setCodigoPlancontable(String codigoPlancontable) {
        this.codigoPlancontable = codigoPlancontable;
    }

    /**
     * Retorna la variable nombre
     * 
     * @return nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asigna la variable nombre
     * 
     * @param nombre
     * Variable a asignar en nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Retorna la variable observaciones
     * 
     * @return observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * Asigna la variable observaciones
     * 
     * @param observaciones
     * Variable a asignar en observaciones
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * Retorna la variable valorCorriente
     * 
     * @return valorCorriente
     */
    public String getValorCorriente() {
        return valorCorriente;
    }

    /**
     * Asigna la variable valorCorriente
     * 
     * @param valorCorriente
     * Variable a asignar en valorCorriente
     */
    public void setValorCorriente(String valorCorriente) {
        this.valorCorriente = valorCorriente;
    }

    /**
     * Retorna la variable valorNoCorriente
     * 
     * @return valorNoCorriente
     */
    public String getValorNoCorriente() {
        return valorNoCorriente;
    }

    /**
     * Asigna la variable valorNoCorriente
     * 
     * @param valorNoCorriente
     * Variable a asignar en valorNoCorriente
     */
    public void setValorNoCorriente(String valorNoCorriente) {
        this.valorNoCorriente = valorNoCorriente;
    }

    /**
     * Retorna la variable fechaobservaciones
     * 
     * @return fechaobservaciones
     */
    public String getFechaobservaciones() {
        return fechaobservaciones;
    }

    /**
     * Asigna la variable fechaobservaciones
     * 
     * @param fechaobservaciones
     * Variable a asignar en fechaobservaciones
     */
    public void setFechaobservaciones(String fechaobservaciones) {
        this.fechaobservaciones = fechaobservaciones;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaVigencia
     * 
     * @return listaVigencia
     */
    public List<Registro> getListaVigencia() {
        return listaVigencia;
    }

    /**
     * Asigna la lista listaVigencia
     * 
     * @param listaVigencia
     * Variable a asignar en listaVigencia
     */
    public void setListaVigencia(List<Registro> listaVigencia) {
        this.listaVigencia = listaVigencia;
    }

    /**
     * Retorna la lista listatrimestre
     * 
     * @return listatrimestre
     */
    public List<Registro> getListatrimestre() {
        return listatrimestre;
    }

    /**
     * Asigna la lista listatrimestre
     * 
     * @param listatrimestre
     * Variable a asignar en listatrimestre
     */
    public void setListatrimestre(List<Registro> listatrimestre) {
        this.listatrimestre = listatrimestre;
    }

    /**
     * Retorna la lista listacodigo
     * 
     * @return listacodigo
     */

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public String getano() {
        return ano;
    }

    public void setano(String ano) {
        this.ano = ano;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public List<Registro> getListames() {
        return listames;
    }

    public void setListames(List<Registro> listames) {
        this.listames = listames;
    }

    public RegistroDataModelImpl getListacodigoE() {
        return listacodigoE;
    }

    public void setListacodigoE(RegistroDataModelImpl listacodigoE) {
        this.listacodigoE = listacodigoE;
    }

    public RegistroDataModelImpl getListacodigo() {
        return listacodigo;
    }

    public void setListacodigo(RegistroDataModelImpl listacodigo) {
        this.listacodigo = listacodigo;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public int getTrimestreNumero() {
        return trimestreNumero;
    }

    public void setTrimestreNumero(int trimestreNumero) {
        this.trimestreNumero = trimestreNumero;
    }

    public int getTrimestral() {
        return trimestral;
    }

    public void setTrimestral(int trimestral) {
        this.trimestral = trimestral;
    }

    public String getTrimestreGrilla() {
        return trimestreGrilla;
    }

    public void setTrimestreGrilla(String trimestreGrilla) {
        this.trimestreGrilla = trimestreGrilla;
    }

    public RegistroDataModelImpl getListacodigoEntidadReciproca() {
        return listacodigoEntidadReciproca;
    }

    public void setListacodigoEntidadReciproca(
        RegistroDataModelImpl listacodigoEntidadReciproca) {
        this.listacodigoEntidadReciproca = listacodigoEntidadReciproca;
    }

    public RegistroDataModelImpl getListacodigoEntidadReciprocaE() {
        return listacodigoEntidadReciprocaE;
    }

    public void setListacodigoEntidadReciprocaE(
        RegistroDataModelImpl listacodigoEntidadReciprocaE) {
        this.listacodigoEntidadReciprocaE = listacodigoEntidadReciprocaE;
    }

    public int getNumeroTrimestreinsercion() {
        return numeroTrimestreinsercion;
    }

    public void setNumeroTrimestreinsercion(int numeroTrimestreinsercion) {
        this.numeroTrimestreinsercion = numeroTrimestreinsercion;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public long getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(long consecutivo) {
        this.consecutivo = consecutivo;
    }

    public EjbSysmanUtil getEjbSysmanUtil() {
        return ejbSysmanUtil;
    }

    public void setEjbSysmanUtil(EjbSysmanUtil ejbSysmanUtil) {
        this.ejbSysmanUtil = ejbSysmanUtil;
    }

    public UploadedFile getArchivoCargaarchivoPlano() {
        return archivoCargaarchivoPlano;
    }

    public void setArchivoCargaarchivoPlano(
        UploadedFile archivoCargaarchivoPlano) {
        this.archivoCargaarchivoPlano = archivoCargaarchivoPlano;
    }

    /**
     * @return the consolidar
     */
    public boolean isConsolidar() {
        return consolidar;
    }

    /**
     * @param consolidar
     * the consolidar to set
     */
    public void setConsolidar(boolean consolidar) {
        this.consolidar = consolidar;
    }

    /**
     * @return the reporte
     */
    public String getReporte() {
        return reporte;
    }

    /**
     * @param reporte
     * the reporte to set
     */
    public void setReporte(String reporte) {
        this.reporte = reporte;
    }

    /**
     * @return the consulta
     */
    public String getConsulta() {
        return consulta;
    }

    /**
     * @param consulta
     * the consulta to set
     */
    public void setConsulta(String consulta) {
        this.consulta = consulta;
    }

}
