/*-
 * LTomaLecturasControlador.java
 *
 * 1.0
 * 
 * 04/10/2016
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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.LTomaLecturasControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador del formulario LTomaLecturas.
 *
 * @version 1.0, 04/10/2016
 * @author Pablo Espitia Cuca
 * @modified jguerrero
 * @version 2. 07/06/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Adem�s se ajustaron los errores del sonar
 * 
 * @author eamaya
 * @version 3.0, 13/06/2017 Se cambi� el llamado del c�digo del
 * formulario y actualizaci�n de ConnectorPool
 */
@ManagedBean
@ViewScoped

public class LTomaLecturasControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    /** Contiene el tipo de informe seleccionado. */
    private String opcionReporte;

    /** Verifica la seleccion de la casilla <Excluir Usuario> */
    private boolean ckExcluir;

    /** Contiene el ciclo seleccionado. */
    private String opcionCiclo;

    /** Contiene el codigo inicial del ciclo. */
    private String codigoInicial;

    /** Contiene el codigo final del ciclo. */
    private String codigoFinal;

    /** Periodo a mostrar en el reporte. */
    private String periodo;

    /** Contiene la seleccion de la casilla <Primer Digito>. */
    private boolean ckPrimerDigito;

    /** Controla si la casilla Excluir Usuarios es visible. */
    private boolean visibleExcluir;

    /**
     */
    private RegistroDataModelImpl listaCodigoInicial;
    /**
     */
    private RegistroDataModelImpl listaCodigoFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCiclo;
    private boolean parametro;

    private static final String CODIGORUTACONS = GeneralParameterEnum.CODIGORUTA
                    .getName();

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /** Creates a new instance of LTomaLecturasControlador */
    public LTomaLecturasControlador() {
        super();
        compania = SessionUtil.getCompania();

        try {
            numFormulario = GeneralCodigoFormaEnum.L_TOMA_LECTURAS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            /* Por omision seleccione el reporte 1. */
            opcionReporte = "1";

            /* Mostrar el CHECK si el parametro esta en SI. */

            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCiclo();
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            parametro = "SI"
                            .equals(SysmanFunciones.nvl(ejbSysmanUtil
                                            .consultarParametro(compania,
                                                            "CARGAR USUARIOS NUEVOS PERIODO",
                                                            SessionUtil.getModulo(),
                                                            new Date(),
                                                            true),
                                            "NO"));

            if ("1".equals(opcionReporte) && parametro) {
                visibleExcluir = true;
            }
            else {
                visibleExcluir = false;
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCiclo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LTomaLecturasControladorUrlEnum.URL4823
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO");

        // 214060
    }

    /**
     * 
     * Carga la lista listaCodigoInicial
     *
     */
    public void cargarListaCodigoInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.CICLO.getName(), opcionCiclo);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LTomaLecturasControladorUrlEnum.URL6416
                                                        .getValue());
        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CODIGORUTACONS);
    }

    /**
     * 
     * Carga la lista listaCodigoFinal
     *
     */
    public void cargarListaCodigoFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.CICLO.getName(), opcionCiclo);
        param.put("CODIGOINICIAL", codigoInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LTomaLecturasControladorUrlEnum.URL6829
                                                        .getValue());
        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CODIGORUTACONS);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Genera un reporte.
     * 
     * @param formato
     * Tipo de documento a generar.
     */
    private void generarReporte(FORMATOS formato) {
        try {
            /* Verifica si el parametro FORMATO CALIDAD esta en SI. */
            boolean key = "SI".equals(
                            SysmanFunciones.nvlStr(ejbSysmanUtil
                                            .consultarParametro(compania,
                                                            "FORMATO CALIDAD",
                                                            SessionUtil.getModulo(),
                                                            new Date(), true),
                                            "NO"));

            /** Recupera la consulta del reporte a generar. */
            String reporte = seleccionarReporte(key);

            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            /* Valores a reemplazar en la consulta del reporte. */
            reemplazar.put("codInicial", "'" + codigoInicial + "'");
            reemplazar.put("codFinal", "'" + codigoFinal + "'");
            reemplazar.put("ciclo", opcionCiclo);

            /* Check <Primer Digito> marcado, entonces muestre 1 */
            reemplazar.put("primerDigito", ckPrimerDigito ? "1" : "0");

            /* Parametros a reemplazar en el reporte. */
            parametros.put("PR_PERIODO", periodo);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "001119LTomaLecturaExcluir".equals(reporte)
                                ? "001119LTomaLectura" : reporte,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Selecciona un tipo de reporte.
     * 
     * @return Nombre del reporte a generar.
     */
    public String seleccionarReporte(boolean key) {
        String reporte;

        switch (opcionReporte) {
        case "1":
            reporte = key ? "001108LTomaLecturaCOS"
                : seleccionarInformeNormal();
            validarParametro();

            break;
        case "2":
            reporte = "001117LTomaLecturaOrd";
            break;
        case "3":
            reporte = key ? "001111LTomaLectura1COS"
                : "001120LTomaLectura1";
            break;
        case "4":
            reporte = key ? "001112LTomaLectura2COS"
                : "001121LTomaLectura2";
            break;
        case "5":
            reporte = "001114LTomaLectura3Hor";
            break;
        case "6":
            reporte = "001115LTomaLecturaCupon";
            break;
        default:
            reporte = "001116LtomaLecturaHorSer";
            break;
        }

        return reporte;
    }

    private void validarParametro() {
        try {
            visibleExcluir = "SI"
                            .equals(SysmanFunciones.nvl(ejbSysmanUtil
                                            .consultarParametro(compania,
                                                            "CARGAR USUARIOS NUEVOS PERIODO",
                                                            SessionUtil.getModulo(),
                                                            new Date(),
                                                            true),
                                            "NO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Verifica si la casilla <Excluir Usuarios> esta marcada.
     * 
     * @return La consulta del tipo de informe <Normal>.
     */
    public String seleccionarInformeNormal() {
        return ckExcluir ? "001119LTomaLecturaExcluir" : "001119LTomaLectura";
    }

    /**
     * Metodo ejecutado al cambiar el control verNuevo
     * 
     * 
     */
    public void cambiarverNuevo() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control OpReporte
     * 
     * 
     */
    public void cambiarOpReporte() {
        // <CODIGO_DESARROLLADO>
        if ("1".equals(opcionReporte) && parametro) {
            visibleExcluir = true;
        }
        else {
            visibleExcluir = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        opcionCiclo = validarString(registroAux,
                        GeneralParameterEnum.NUMERO.getName());
        codigoInicial = null;
        codigoFinal = null;
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();

        /* Obtiene el codigo Inicial del ciclo. */
        codigoInicial = validarString(registroAux, "CODIGOINICIAL");

        /* Obtiene el codigo Final del ciclo */
        codigoFinal = validarString(registroAux, "CODIGOFINAL");
        /* Obtiene el periodo del ciclo. */
        setPeriodo(registroAux.getCampos().get("NOMBREPERIODO").toString());

    }

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
        codigoInicial = validarString(registroAux, CODIGORUTACONS);
        codigoFinal = null;
        cargarListaCodigoFinal();
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
        codigoFinal = validarString(registroAux, CODIGORUTACONS);
    }

    // </METODOS_COMBOS_GRANDES>
    // <SET_GET_ATRIBUTOS>
    public String getOpcionReporte() {
        return opcionReporte;
    }

    public void setOpcionReporte(String opcionReporte) {
        this.opcionReporte = opcionReporte;
    }

    public String getOpcionCiclo() {
        return opcionCiclo;
    }

    public void setOpcionCiclo(String opcionCiclo) {
        this.opcionCiclo = opcionCiclo;
    }

    public String getCodigoInicial() {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal() {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean isVisibleExcluir() {
        return visibleExcluir;
    }

    public void setVisibleExcluir(boolean visibleExcluir) {
        this.visibleExcluir = visibleExcluir;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public boolean isCkPrimerDigito() {
        return ckPrimerDigito;
    }

    public void setCkPrimerDigito(boolean ckPrimerDigito) {
        this.ckPrimerDigito = ckPrimerDigito;
    }

    public boolean isCkExcluir() {
        return ckExcluir;
    }

    public void setCkExcluir(boolean ckExcluir) {
        this.ckExcluir = ckExcluir;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCiclo() {
        return listaCiclo;
    }

    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }

    private String validarString(Registro registro, String campo) {
        return SysmanFunciones.validarCampoVacio(registro.getCampos(), campo)
            ? "" : registro.getCampos().get(campo).toString();
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
