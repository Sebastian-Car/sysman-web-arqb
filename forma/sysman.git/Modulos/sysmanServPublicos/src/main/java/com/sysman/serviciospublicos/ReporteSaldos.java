/*-
 * ReporteSaldos.java
 *
 * 1.0
 *
 * 11/11/2016
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
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.ReporteSaldosEnum;
import com.sysman.serviciospublicos.enums.ReporteSaldosUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador que permite generar el reporte de saldos credito
 *
 * @version 1.0, 11/11/2016
 * @author jlozano
 * 
 * @version 2.0, 13/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Reemplazo de texto quemado por etiquetas de properties.<br>
 * Reemplazar los llamados a ConectorPool por el esquema
 * <code>ConectorPool.ESQUEMA_SYSMAN</code>.<br>
 * Refactoring.<br>
 * Manejo de EJBs.
 */
@ManagedBean
@ViewScoped
public class ReporteSaldos extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que almacena el codigo del modulo en
     * el cual inicio sesion el usuario
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el valor seleccionado para el combo Ciclo
     */
    private String ciclo;
    /**
     * Atributo que almacena el valor seleccionado para el combo
     * Codigo inicial
     */
    private String codigoInicial;
    /**
     * Atributo que almacena el valor seleccionado para el combo
     * Codigo final
     */
    private String codigoFinal;
    /**
     * Atributo que almacena el valor seleccionado para el combo Banco
     * inicial
     */
    private String bancoInicial;
    /**
     * Atributo que almacena el valor seleccionado para el combo Banco
     * final
     */
    private String bancoFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Atributo que controla la visibilidad de los combos Banco
     * inicial y Banco final
     */
    private boolean visibleBanco;
    /**
     * Constante para literal "CODIGO"
     */
    private static final String CODIGO = "CODIGO";

    /**
     * Constante para literal "CODIGORUTA"
     */
    private static final String CODIGO_RUTA = "CODIGORUTA";
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que se muestra en el combo Ciclo
     */
    private List<Registro> listaCiclo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que se muestra en el combo Codigo inicial
     */
    private RegistroDataModelImpl listacmbCodigoInicial;
    /**
     * Lista que se muestra en el combo Codigo final
     */
    private RegistroDataModelImpl listacmbCodigoFinal;
    /**
     * Lista que se muestra en el combo Banco inicial
     */
    private RegistroDataModelImpl listacmbBancoInicial;
    /**
     * Lista que se muestra en el combo Banco final
     */
    private RegistroDataModelImpl listacmbBancoFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    // < DECLARAR EJBs>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </ DECLARAR EJBs>

    /**
     * Crea una nueva instancia de ReporteSaldos
     */
    public ReporteSaldos() {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        ciclo = "T";
        codigoInicial = "0";
        codigoFinal = "9999";

        try {
            // 1193
            numFormulario = GeneralCodigoFormaEnum.REPORTE_SALDOS.getCodigo();

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
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacmbCodigoInicial();
        cargarListacmbCodigoFinal();
        cargarListacmbBancoInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario. Verifica el valor del parametro
     * "MOSTRAR FILTROS ENTRE BANCOS SALDOS CREDITOS" y segun este
     * asigna la visibilidad de los combos Banco inicial y Banco final
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            /*-Controla la visibilidad del combo banco inicial y final*/
            visibleBanco = "SI".equals(SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "MOSTRAR FILTROS ENTRE BANCOS SALDOS CREDITOS",
                                            modulo, new Date(), true), "NO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Metodo que carga los elementos de la lista Ciclo.
     */
    public void cargarListaCiclo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCiclo = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ReporteSaldosUrlEnum.URL7067
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
     * Metodo que carga los elementos de la lista Codigo inicial
     */
    public void cargarListacmbCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(ReporteSaldosUrlEnum.URL7712
                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listacmbCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO_RUTA);
    }

    /**
     *
     * Metodo que carga los elementos de la lista Codigo final
     */
    public void cargarListacmbCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(ReporteSaldosUrlEnum.URL8902
                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ReporteSaldosEnum.CODIGOINICIAL.getValue(), codigoInicial);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listacmbCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO_RUTA);
    }

    /**
     *
     * Metodo que carga los elementos de la lista Banco inicial
     */
    public void cargarListacmbBancoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(ReporteSaldosUrlEnum.URL9982
                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbBancoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);
    }

    /**
     *
     * Metodo que carga los elementos de la lista Banco final
     */
    public void cargarListacmbBancoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ReporteSaldosUrlEnum.URL10658
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ReporteSaldosEnum.BANCOINICIAL.getValue(), bancoInicial);

        listacmbBancoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Impresora en la vista.
     * Genera el reporte "Informe de saldos credito" en formato PDF
     *
     */
    public void oprimirImpresora() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        generaInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     * Genera el reporte "Informe de saldos crédito" en formato EXCEL
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ciclo
     *
     * Recarga los valores de los combos Codigo inicial y Codigo final
     * filtrando por el ciclo seleccionado
     *
     */
    public void cambiarCiclo() {
        // <CODIGO_DESARROLLADO>
        codigoInicial = codigoFinal = "";

        if (ciclo == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3236"));

            ciclo = "T";
            listacmbCodigoInicial = listacmbCodigoFinal = null;
        }

        cargarListacmbCodigoInicial();

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbCodigoInicial
     *
     * Asigna al atributo codigoInicial el valor seleccionado
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        codigoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(CODIGO_RUTA), "")
                        .toString();

        codigoFinal = "";
        cargarListacmbCodigoFinal();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbCodigoFinal
     *
     * Asigna al atributo codigoFinal el valor seleccionado
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        codigoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(CODIGO_RUTA), "")
                        .toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbBancoInicial
     *
     * Asigna al atributo bancoInicial el valor seleccionado
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbBancoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        bancoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(CODIGO), "")
                        .toString();

        bancoFinal = "";
        cargarListacmbBancoFinal();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbBancoFinal
     *
     * Asigna al atributo bancoFinal el valor seleccionado
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbBancoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        bancoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(CODIGO), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    public void generaInforme(FORMATOS formato) {
        if (visibleBanco && validarCBBancos()) {
            return;
        }

        if (validarCampos()) {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            String reporte = "001244rptSaldosCredito";

            try {
                // <ENVIAR PARAMETROS AL REPORTE>
                parametros.put("PR_CICLO", "T".equals(ciclo) ? "Todos" : ciclo);
                // </ENVIAR PARAMETROS AL REPORTE>

                // <REEMPLAZAR VARIABLES EN CONSULTA>
                reemplazar.put("codIni", codigoInicial);
                reemplazar.put("codFin", codigoFinal);
                reemplazar.put("condicionBanco", condBancoperproceso());

                reemplazar.put("condicionCiclo", "T".equals(ciclo) ? " "
                    : "AND SP_FACTURADO.CICLO = " + ciclo);

                // </REEMPLAZAR VARIABLES EN CONSULTA>

                Reporteador.resuelveConsulta(reporte, Integer.valueOf(modulo),
                                reemplazar, parametros);

                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }
            catch (JRException | IOException | SysmanException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB165"));
        }
    }

    private boolean validarCBBancos() {
        if (bancoInicial.isEmpty() || bancoFinal.isEmpty()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3237"));
            return true;
        }

        return false;
    }

    /**
     * Define la condicion para filtrar por el campo
     * <code>SP_USUARIO.BANCOPERPROCESO</code>.
     * 
     * @return La condicion para filtrar por el bancoperproceso.
     */
    private String condBancoperproceso() {
        return visibleBanco ? " AND SP_USUARIO.BANCOPERPROCESO BETWEEN '"
            + bancoInicial + "' AND '" + bancoFinal + "'" : " ";
    }

    private boolean validarCampos() {
        return !"".equals(SysmanFunciones.nvl(codigoInicial, ""))
            || !"".equals(SysmanFunciones.nvl(codigoFinal, ""));
    }

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
     * Retorna la variable bancoInicial
     *
     * @return bancoInicial
     */
    public String getBancoInicial() {
        return bancoInicial;
    }

    /**
     * Asigna la variable bancoInicial
     *
     * @param bancoInicial
     * Variable a asignar en bancoInicial
     */
    public void setBancoInicial(String bancoInicial) {
        this.bancoInicial = bancoInicial;
    }

    /**
     * Retorna la variable bancoFinal
     *
     * @return bancoFinal
     */
    public String getBancoFinal() {
        return bancoFinal;
    }

    /**
     * Asigna la variable bancoFinal
     *
     * @param bancoFinal
     * Variable a asignar en bancoFinal
     */
    public void setBancoFinal(String bancoFinal) {
        this.bancoFinal = bancoFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean isVisibleBanco() {
        return visibleBanco;
    }

    public void setVisibleBanco(boolean visibleBanco) {
        this.visibleBanco = visibleBanco;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCiclo
     *
     * @return listaCiclo
     */
    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     *
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacmbCodigoInicial
     *
     * @return listacmbCodigoInicial
     */
    public RegistroDataModelImpl getListacmbCodigoInicial() {
        return listacmbCodigoInicial;
    }

    /**
     * Asigna la lista listacmbCodigoInicial
     *
     * @param listacmbCodigoInicial
     * Variable a asignar en listacmbCodigoInicial
     */
    public void setListacmbCodigoInicial(
        RegistroDataModelImpl listacmbCodigoInicial) {
        this.listacmbCodigoInicial = listacmbCodigoInicial;
    }

    /**
     * Retorna la lista listacmbCodigoFinal
     *
     * @return listacmbCodigoFinal
     */
    public RegistroDataModelImpl getListacmbCodigoFinal() {
        return listacmbCodigoFinal;
    }

    /**
     * Asigna la lista listacmbCodigoFinal
     *
     * @param listacmbCodigoFinal
     * Variable a asignar en listacmbCodigoFinal
     */
    public void setListacmbCodigoFinal(
        RegistroDataModelImpl listacmbCodigoFinal) {
        this.listacmbCodigoFinal = listacmbCodigoFinal;
    }

    /**
     * Retorna la lista listacmbBancoInicial
     *
     * @return listacmbBancoInicial
     */
    public RegistroDataModelImpl getListacmbBancoInicial() {
        return listacmbBancoInicial;
    }

    /**
     * Asigna la lista listacmbBancoInicial
     *
     * @param listacmbBancoInicial
     * Variable a asignar en listacmbBancoInicial
     */
    public void setListacmbBancoInicial(
        RegistroDataModelImpl listacmbBancoInicial) {
        this.listacmbBancoInicial = listacmbBancoInicial;
    }

    /**
     * Retorna la lista listacmbBancoFinal
     *
     * @return listacmbBancoFinal
     */
    public RegistroDataModelImpl getListacmbBancoFinal() {
        return listacmbBancoFinal;
    }

    /**
     * Asigna la lista listacmbBancoFinal
     *
     * @param listacmbBancoFinal
     * Variable a asignar en listacmbBancoFinal
     */
    public void setListacmbBancoFinal(
        RegistroDataModelImpl listacmbBancoFinal) {
        this.listacmbBancoFinal = listacmbBancoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
