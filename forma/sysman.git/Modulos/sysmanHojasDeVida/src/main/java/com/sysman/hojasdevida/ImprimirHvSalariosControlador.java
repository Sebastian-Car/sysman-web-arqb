/*-
 * ImprimirHvSalariosControlador.java
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
import com.sysman.hojasdevida.enums.ImprimirHvSalariosControladorEnum;
import com.sysman.hojasdevida.enums.ImprimirHvSalariosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Migracion del formulario ImprimirHVSalarios, migracion de reportes
 * 001551HojasDeVidaSalarios,001604INFOSALARIAL creacion de dss para
 * los combos de persona inicial y final
 * 
 * @version 1.0, 13/12/2017
 * @author jcrodriguez
 */
@ManagedBean
@ViewScoped
public class ImprimirHvSalariosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * variable cadena que almacena el numero de cedula de la persona
     * incial
     */
    private String personaInicial;
    /**
     * variable cadena que almacena el numero de cedula de la persona
     * final
     */
    private String personaFinal;
    /**
     * variable cadena que almacena el nombre de la persona inicial
     */
    private String carpetaInicial;
    /**
     * variable cadena que almacena el nombre de la persona final
     */
    private String carpetaFinal;
    /**
     * variable estado check listado
     */
    private boolean listado;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * variable que lista las personas inicial
     */
    private RegistroDataModelImpl listaPersonaInicial;
    /**
     * variable que lista las personas final
     */
    private RegistroDataModelImpl listaPersonaFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ImprimirHvSalariosControlador
     */
    public ImprimirHvSalariosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.IMPRIMIR_HV_SALARIOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
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
        cargarListaPersonaInicial();
        cargarListaPersonaFinal();
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // heredado del bean base
    }

    /**
     * 
     * Carga la lista listaPersonaInicial
     */
    public void cargarListaPersonaInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ImprimirHvSalariosControladorUrlEnum.URL3323
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaPersonaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        ImprimirHvSalariosControladorEnum.NUMERO_DCTO
                                        .getValue());

    }

    /**
     * 
     * Carga la lista listaPersonaFinal
     */
    public void cargarListaPersonaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ImprimirHvSalariosControladorUrlEnum.URL3325
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ImprimirHvSalariosControladorEnum.EMPLEADOINICIAL.getValue(),
                        personaInicial);

        listaPersonaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        ImprimirHvSalariosControladorEnum.NUMERO_DCTO
                                        .getValue());

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdImprimir en la vista
     * 
     */
    public void oprimirPdf() {
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdPrevia en la vista
     * 
     */
    public void oprimirExcel() {
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
    }

    /**
     * metodo que contiene la logica para imprimir los reportes en
     * formato pdf y excel
     * 
     * @param formato
     */
    private void generarInforme(FORMATOS formato) {

        Map<String, Object> reemplazos = new HashMap<>();
        reemplazos.put("numeroInicial", personaInicial);
        reemplazos.put("numeroFinal", personaFinal);

        String reporte = listado
            ? ImprimirHvSalariosControladorEnum.REPORTE001604.getValue()
            : ImprimirHvSalariosControladorEnum.REPORTE001551
                            .getValue();

        Map<String, Object> parametros = new HashMap<>();

        Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(SessionUtil.getModulo()), reemplazos,
                        parametros);
        try {
            archivoDescarga = JsfUtil
                            .exportarStreamed(reporte,
                                            parametros,
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString("MSM_INFORME_NO_EXISTE"), " ",
                            ex.getMessage(), " ",
                            reporte));
            Logger.getLogger(ImprimirHvSalariosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaPersonaInicial
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPersonaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        personaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(ImprimirHvSalariosControladorEnum.NUMERO_DCTO
                                                        .getValue()),
                                        "")
                        .toString();
        carpetaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();
        personaFinal = null;
        carpetaFinal = null;
        cargarListaPersonaFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaPersonaFinal
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPersonaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        personaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(ImprimirHvSalariosControladorEnum.NUMERO_DCTO
                                                        .getValue()),
                                        "")
                        .toString();
        carpetaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable personaInicial
     * 
     * @return personaInicial
     */
    public String getPersonaInicial() {
        return personaInicial;
    }

    /**
     * Asigna la variable personaInicial
     * 
     * @param personaInicial
     * Variable a asignar en personaInicial
     */
    public void setPersonaInicial(String personaInicial) {
        this.personaInicial = personaInicial;
    }

    /**
     * Retorna la variable personaFinal
     * 
     * @return personaFinal
     */
    public String getPersonaFinal() {
        return personaFinal;
    }

    /**
     * Asigna la variable personaFinal
     * 
     * @param personaFinal
     * Variable a asignar en personaFinal
     */
    public void setPersonaFinal(String personaFinal) {
        this.personaFinal = personaFinal;
    }

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
     * Retorna la lista listaPersonaInicial
     * 
     * @return listaPersonaInicial
     */
    public RegistroDataModelImpl getListaPersonaInicial() {
        return listaPersonaInicial;
    }

    /**
     * Asigna la lista listaPersonaInicial
     * 
     * @param listaPersonaInicial
     * Variable a asignar en listaPersonaInicial
     */
    public void setListaPersonaInicial(
        RegistroDataModelImpl listaPersonaInicial) {
        this.listaPersonaInicial = listaPersonaInicial;
    }

    /**
     * Retorna la lista listaPersonaFinal
     * 
     * @return listaPersonaFinal
     */
    public RegistroDataModelImpl getListaPersonaFinal() {
        return listaPersonaFinal;
    }

    /**
     * Asigna la lista listaPersonaFinal
     * 
     * @param listaPersonaFinal
     * Variable a asignar en listaPersonaFinal
     */
    public void setListaPersonaFinal(RegistroDataModelImpl listaPersonaFinal) {
        this.listaPersonaFinal = listaPersonaFinal;
    }

    public boolean isListado() {
        return listado;
    }

    public void setListado(boolean listado) {
        this.listado = listado;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
