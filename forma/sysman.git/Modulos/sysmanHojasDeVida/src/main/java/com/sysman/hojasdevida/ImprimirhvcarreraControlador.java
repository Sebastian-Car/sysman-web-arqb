/*-
 * ImprimirhvcarreraControlador.java
 *
 * 1.0
 *
 * 13/12/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.hojasdevida.enums.ImprimirhvcarreraControladorEnum;
import com.sysman.hojasdevida.enums.ImprimirhvcarreraControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite generar el informe de hojas de vida de
 * persona natural referentes a carrera administrativa. Se accede
 * desde la ruta Panel Principal\Hojas de vida\Hojas de
 * vida\Informes\Carrera administrativa hojas de vida.
 *
 * @version 1.0, 13/12/2017
 * @author lcortes
 */
@ManagedBean
@ViewScoped
public class ImprimirhvcarreraControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que almacena el codigo de la carpeta inicial
     * seleccionada.
     */
    private String carpetaInicial;
    /**
     * Variable que almacena el codigo de la carpeta final
     * seleccionada.
     */
    private String carpetaFinal;
    /**
     * Variable que almacena el nombre de la carpeta inicial
     * seleccionada.
     */
    private String nombreCarpetaIni;
    /**
     * Variable que almacena el nombre de la carpeta final
     * seleccionada.
     */
    private String nombreCarpetaFin;
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
     * Listado de carpetas
     */
    private RegistroDataModelImpl listaCarpetaInicial;
    /**
     * Listado de carpetas a partir de la carpeta inicial
     * seleccionada.
     */
    private RegistroDataModelImpl listaCarpetaFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ImprimirhvcarreraControlador
     */
    public ImprimirhvcarreraControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.HOJAS_DE_VIDA_CARRERA_CONTROLADOR
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
        cargarListaCarpetaInicial();
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
        /*
         * FR1508-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCarpetaInicial
     *
     */
    public void cargarListaCarpetaInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ImprimirhvcarreraControladorUrlEnum.URL001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaCarpetaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, ImprimirhvcarreraControladorEnum.NUMEROCARPETA
                                        .getValue());
    }

    /**
     *
     * Carga la lista listaCarpetaFinal
     *
     */
    public void cargarListaCarpetaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ImprimirhvcarreraControladorUrlEnum.URL002
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ImprimirhvcarreraControladorEnum.EMPLEADOINICIAL.getValue(),
                        carpetaInicial);
        listaCarpetaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, ImprimirhvcarreraControladorEnum.NUMEROCARPETA
                                        .getValue());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton CmdImprimir en la vista
     *
     *
     */
    public void oprimirCmdImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton CmdExcel en la vista
     *
     *
     */
    public void oprimirCmdExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que permite generar el reporte en el formato
     * seleccionado
     *
     * @param formato
     */
    public void generarReporte(FORMATOS formato) {
        Map<String, Object> reemplazos = new TreeMap<>();
        reemplazos.put("carpetaInicial", carpetaInicial);
        reemplazos.put("carpetaFinal", carpetaFinal);
        Map<String, Object> parametros = new TreeMap<>();

        Reporteador.resuelveConsulta("001549HojasDeVidaCarrera",
                        Integer.valueOf(SessionUtil.getModulo()),
                        reemplazos, parametros);

        try {
            archivoDescarga = JsfUtil.exportarStreamed(
                            "001549HojasDeVidaCarrera", parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCarpetaInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCarpetaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        carpetaInicial = registroAux.getCampos()
                        .get(ImprimirhvcarreraControladorEnum.NUMEROCARPETA
                                        .getValue())
                        .toString();
        nombreCarpetaIni = registroAux.getCampos()
                        .get(ImprimirhvcarreraControladorEnum.NOMBRECOMPLETO
                                        .getValue())
                        .toString();
        cargarListaCarpetaFinal();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCarpetaFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCarpetaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        carpetaFinal = registroAux.getCampos()
                        .get(ImprimirhvcarreraControladorEnum.NUMEROCARPETA
                                        .getValue())
                        .toString();
        nombreCarpetaFin = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRES.getName())
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable carpetaInicial
     *
     * @return carpetaInicial
     */
    public String getCarpetaInicial() {
        return carpetaInicial;
    }

    /**
     * Asigna la variable carpetaInicial
     *
     * @param carpetaInicial
     * Variable a asignar en carpetaInicial
     */
    public void setCarpetaInicial(String carpetaInicial) {
        this.carpetaInicial = carpetaInicial;
    }

    /**
     * Retorna la variable carpetaFinal
     *
     * @return carpetaFinal
     */
    public String getCarpetaFinal() {
        return carpetaFinal;
    }

    /**
     * Asigna la variable carpetaFinal
     *
     * @param carpetaFinal
     * Variable a asignar en carpetaFinal
     */
    public void setCarpetaFinal(String carpetaFinal) {
        this.carpetaFinal = carpetaFinal;
    }

    /**
     * Retorna la variable nombreCarpetaIni
     *
     * @return nombreCarpetaIni
     */
    public String getNombreCarpetaIni() {
        return nombreCarpetaIni;
    }

    /**
     * Asigna la variable nombreCarpetaIni
     *
     * @param nombreCarpetaIni
     * Variable a asignar en nombreCarpetaIni
     */
    public void setNombreCarpetaIni(String nombreCarpetaIni) {
        this.nombreCarpetaIni = nombreCarpetaIni;
    }

    /**
     * Retorna la variable nombreCarpetaFin
     *
     * @return nombreCarpetaFin
     */
    public String getNombreCarpetaFin() {
        return nombreCarpetaFin;
    }

    /**
     * Asigna la variable nombreCarpetaFin
     *
     * @param nombreCarpetaFin
     * Variable a asignar en nombreCarpetaFin
     */
    public void setNombreCarpetaFin(String nombreCarpetaFin) {
        this.nombreCarpetaFin = nombreCarpetaFin;
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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCarpetaInicial
     *
     * @return listaCarpetaInicial
     */
    public RegistroDataModelImpl getListaCarpetaInicial() {
        return listaCarpetaInicial;
    }

    /**
     * Asigna la lista listaCarpetaInicial
     *
     * @param listaCarpetaInicial
     * Variable a asignar en listaCarpetaInicial
     */
    public void setListaCarpetaInicial(
        RegistroDataModelImpl listaCarpetaInicial) {
        this.listaCarpetaInicial = listaCarpetaInicial;
    }

    /**
     * Retorna la lista listaCarpetaFinal
     *
     * @return listaCarpetaFinal
     */
    public RegistroDataModelImpl getListaCarpetaFinal() {
        return listaCarpetaFinal;
    }

    /**
     * Asigna la lista listaCarpetaFinal
     *
     * @param listaCarpetaFinal
     * Variable a asignar en listaCarpetaFinal
     */
    public void setListaCarpetaFinal(RegistroDataModelImpl listaCarpetaFinal) {
        this.listaCarpetaFinal = listaCarpetaFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
