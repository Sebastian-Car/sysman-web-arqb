/*-
 * BalanceAperturaNiifSIsControlador.java
 *
 * 1.0
 * 
 * 01/06/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.chipfut;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.chipfut.ejb.EjbChipFutCeroRemote;
import com.sysman.chipfut.enums.BalanceAperturaNiifSisControladorEnum;
import com.sysman.chipfut.enums.BalanceAperturaNiifSisControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase migrada para la generacion del archivo plano saldos por
 * convergencia TAR Nº1000083960
 *
 * @version 1.0, 01/06/2018
 * @author ybecerra
 */
@ManagedBean
@ViewScoped
public class BalanceAperturaNiifSIsControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el valor del indicador "En miles de
     * Pesos"
     */
    private boolean miles;
    /**
     * Atributo que almacena el valor del indicador "Sin Centavos: "
     */
    private boolean centavos;

    /**
     * Atributo que almacena el valor del registro seleccionado del
     * combo "Codigo Inicial"
     */
    private String codigoInicial;
    /**
     * Atributo que almacena el valor del registro seleccionado del
     * combo "Codigo Final"
     */
    private String codigoFinal;
    /**
     * Atributo que almacena el valor del registro seleccionado del
     * combo "Ano"
     */
    private String anoTrabajo;
    /**
     * Atributo que almacena el valor del campo "Digitos"
     */
    private String digitos;
    /**
     * Atributo que almacena el valor del campo "Codigo Entidad"
     */
    private String codigoEntidad;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    @EJB
    private EjbChipFutCeroRemote ejbChipFutCero;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registros de la tabla ano
     */
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de registros de las cuentas contables
     */
    private RegistroDataModelImpl listaCodigoInicial;
    /**
     * Lista de registros de las cuentas contables
     */
    private RegistroDataModelImpl listaCodigoFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de BalanceAperturaNiifSIsControlador
     */
    public BalanceAperturaNiifSIsControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigoEntidad = SessionUtil.getCompaniaIngreso().getCodigoContaduria();
        try {
            // 1810
            numFormulario = GeneralCodigoFormaEnum.BALANCE_APERTURA_NIIF_CONTROLADOR
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
        cargarListaAno();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>

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
        anoTrabajo = String.valueOf(SysmanFunciones.ano(new Date()));
        cargarListaCodigoInicial();

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BalanceAperturaNiifSisControladorUrlEnum.URL181
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
     * Carga la lista listaCodigoInicial
     *
     */
    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceAperturaNiifSisControladorUrlEnum.URL208
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaCodigoFinal
     *
     */
    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceAperturaNiifSisControladorUrlEnum.URL208
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);
        param.put(BalanceAperturaNiifSisControladorEnum.CODIGO_INICIAL
                        .getValue(), codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton EnviarExcel en la vista
     *
     *
     */
    public void oprimirEnviarExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        String archivoPlano = generarPlano(true, miles, centavos);
        String separadorRegistros = System.getProperty("line.separator");
        String separadorColumnas = "\t";
        String nombreHoja = "ESTADO DE SITUACION FINANCIERA";
        String nombreDocumento = "ESTADO DE SITUACION FINANCIERA SI";
        archivoDescarga = JsfUtil.armarExcel(archivoPlano, separadorRegistros,
                        separadorColumnas, nombreHoja, nombreDocumento);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton GenerarPlano en la vista
     *
     *
     */
    public void oprimirGenerarPlano() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        try {
            archivoDescarga = JsfUtil
                            .getArchivoDescarga(JsfUtil.serializarPlano(
                                            generarPlano(false, miles,
                                                            centavos)),
                                            SysmanFunciones.concatenar("CGN",
                                                            anoTrabajo,
                                                            compania,
                                                            "_SI_CONVERGENCIA.txt"));
        }
        catch (NumberFormatException | JRException | IOException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Genera archivo plano de saldos y movimientos contables
     * 
     * @param exportaExcel
     * Indica si se va a exportar a MS Excel.
     * @return archivo plano
     */
    private String generarPlano(boolean exportaExcel, boolean miles,
        boolean centavos) {
        try {
            return ejbChipFutCero.generarPlanoBalance(compania,
                            Integer.valueOf(anoTrabajo), codigoEntidad,
                            codigoInicial, codigoFinal,
                            Integer.valueOf(digitos), exportaExcel, miles,
                            centavos);
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return null;
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ano
     * 
     * 
     */
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        cargarListaCodigoInicial();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
        codigoFinal = null;
        cargarListaCodigoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable miles
     * 
     * @return miles
     */
    public boolean getMiles() {
        return miles;
    }

    /**
     * Asigna la variable miles
     * 
     * @param miles
     * Variable a asignar en miles
     */
    public void setMiles(boolean miles) {
        this.miles = miles;
    }

    /**
     * Retorna la variable centavos
     * 
     * @return centavos
     */
    public boolean getCentavos() {
        return centavos;
    }

    /**
     * Asigna la variable centavos
     * 
     * @param centavos
     * Variable a asignar en centavos
     */
    public void setCentavos(boolean centavos) {
        this.centavos = centavos;
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
     * Retorna la variable anoTrabajo
     * 
     * @return anoTrabajo
     */
    public String getAnoTrabajo() {
        return anoTrabajo;
    }

    /**
     * Asigna la variable anoTrabajo
     * 
     * @param anoTrabajo
     * Variable a asignar en anoTrabajo
     */
    public void setAnoTrabajo(String anoTrabajo) {
        this.anoTrabajo = anoTrabajo;
    }

    /**
     * Retorna la variable digitos
     * 
     * @return digitos
     */
    public String getDigitos() {
        return digitos;
    }

    /**
     * Asigna la variable digitos
     * 
     * @param digitos
     * Variable a asignar en digitos
     */
    public void setDigitos(String digitos) {
        this.digitos = digitos;
    }

    /**
     * Retorna la variable codigoEntidad
     * 
     * @return codigoEntidad
     */
    public String getCodigoEntidad() {
        return codigoEntidad;
    }

    /**
     * Asigna la variable codigoEntidad
     * 
     * @param codigoEntidad
     * Variable a asignar en codigoEntidad
     */
    public void setCodigoEntidad(String codigoEntidad) {
        this.codigoEntidad = codigoEntidad;
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
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

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
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
