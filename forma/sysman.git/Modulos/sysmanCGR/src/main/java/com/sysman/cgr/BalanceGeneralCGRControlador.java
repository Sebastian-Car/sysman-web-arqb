/*-
 * BalanceGeneralCGRControlador.java
 *
 * 1.0
 * 
 * 3/06/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.cgr;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.cgr.enums.BalanceGeneralCGRControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
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
 * Formulario que imprime los balances generales CGR
 *
 * @version 1.0, 03/06/2021
 * @author eamaya
 */
@ManagedBean
@ViewScoped

public class BalanceGeneralCGRControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que almacena el codigo inicial seleccionado
     */
    private String codigoInicial;
    /**
     * Variable que almacena el codigo final seleccionado
     */
    private String codigoFinal;
    /**
     * Variable que almacena el anio seleccionado
     */
    private String anio;
    /**
     * Variable que almacena el mes seleccionado
     */
    private String mes;
    /**
     * VAriable que almacena los digitos
     */
    private String digitos;
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
     * Lista que carga los anios
     */
    private List<Registro> listaAnoTrabajo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que carga los codigos iniciales
     */
    private RegistroDataModelImpl listaCodigoInicial;
    /**
     * Lista que carga los codigos finales
     */
    private RegistroDataModelImpl listaCodigoFinal;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de BalanceGeneralCGRControlador
     */
    public BalanceGeneralCGRControlador() {
        super();
        compania = SessionUtil.getCompania();
        anio = Integer.toString(SysmanFunciones.ano(new Date()));
        mes = Integer.toString(SysmanFunciones.mes(new Date()) + 1);
        codigoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        codigoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        digitos = "6";
        try {
            // 2283
            numFormulario = GeneralCodigoFormaEnum.BALANCE_GENERALCGR_CONTROLADOR
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
        cargarListaAnoTrabajo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoInicial();
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

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAnoTrabajo
     *
     * 
     */
    public void cargarListaAnoTrabajo() {
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BalanceGeneralCGRControladorUrlEnum.URL4559
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
     * 
     */
    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceGeneralCGRControladorUrlEnum.URL5121
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCodigoFinal
     *
     * 
     */
    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceGeneralCGRControladorUrlEnum.URL6283
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put("CODIGOINICIAL",
                        codigoInicial);
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     *
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.CSV);
        // </CODIGO_DESARROLLADO>
    }

    public void generaInforme(ReportesBean.FORMATOS formato) {
        String informe = "";
        String modulo = "1";
        String titulo = "BALANCE GENERAL DEL MES DE "
            + (SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                            .parseInt(mes)])
                                            .toUpperCase()
            + " DE " + anio
            + "";

        Map<String, Object> parametros = new HashMap<>();
        HashMap<String, Object> reemplazar = new HashMap<>();
        try {
            reemplazar.put("mestrabajo", mes);
            reemplazar.put("anio", anio);
            reemplazar.put("codigoInicial", "'" + codigoInicial + "'");
            reemplazar.put("codigoFinal", "'" + cod() + "'");
            reemplazar.put("codigoFin", "'" + codigoFinal + "'");
            reemplazar.put("digitos", digitos);
            reemplazar.put("digitos1", digitos);
            reemplazos(reemplazar);
            reemplazar.put("manTer", "0");
            reemplazar.put("manAux", "0");
            reemplazar.put("manCen", "0");
            reemplazar.put("manRef", "0");
            reemplazar.put("manFue", "0");
            reemplazar.put("baseBalance", Reporteador.resuelveConsulta(
                            "800046BaseBalances",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar));
            informe = "000570BalanceGeneral";

            Reporteador.resuelveConsulta(informe, Integer.parseInt(modulo),
                            reemplazar, parametros);

            String firmaCont1 = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA CONTABLE 1", modulo, new Date(), true);

            String cargoCont1 = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO CONTABLE 1", modulo, new Date(), true);

            String documentoCont1 = ejbSysmanUtil.consultarParametro(
                            compania, "DOCUMENTO CONTABLE 1", modulo,
                            new Date(), true);

            String firmaCont2 = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA CONTABLE 2", modulo, new Date(), true);

            String cargoCont2 = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO CONTABLE 2", modulo, new Date(), true);

            String documentoCont2 = ejbSysmanUtil.consultarParametro(
                            compania, "DOCUMENTO CONTABLE 2", modulo,
                            new Date(), true);

            String firmaCont3 = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA CONTABLE 3", modulo, new Date(), true);

            String cargoCont3 = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO CONTABLE 3", modulo, new Date(), true);

            String documentoCont3 = ejbSysmanUtil.consultarParametro(
                            compania, "DOCUMENTO CONTABLE 3", modulo,
                            new Date(), true);

            parametros.put("PR_TITULO", titulo);
            parametros.put("PR_FIRMA_CONTABLE_1", firmaCont1);
            parametros.put("PR_FIRMA_CONTABLE_2", firmaCont2);
            parametros.put("PR_CARGO_CONTABLE_1", cargoCont1);
            parametros.put("PR_CARGO_CONTABLE_2", cargoCont2);
            parametros.put("PR_DOCUMENTO_CONTABLE_1", documentoCont1);
            parametros.put("PR_DOCUMENTO_CONTABLE_2", documentoCont2);

            String firmaTres = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(
                                            compania, "GENERA FIRMA CONTABLE 3",
                                            modulo,
                                            new Date(), true), "NO");

            if ("SI".equals(firmaTres)) {
                parametros.put("PR_FIRMA_CONTABLE_3", firmaCont3);
                parametros.put("PR_CARGO_CONTABLE_3", cargoCont3);
                parametros.put("PR_DOCUMENTO_CONTABLE_3", documentoCont3);
            }

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            archivoDescarga = JsfUtil.exportarStreamed(informe, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * se llama en el metodo generarInforme
     *
     * @param reemplazar
     */
    private void reemplazos(HashMap<String, Object> reemplazar) {

        reemplazar.put("saldoCero", " AND PLAN_CONTABLE.SALDO"
            + mes + "<> 0");
        reemplazar.put("saldoCeroExt",
                        " AND SALDO" + mes + "<> 0");

    }

    private String cod() {
        String cod = "";
        cod = codigoFinal + "9";

        return cod;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAnoTrabajo() {
        // <CODIGO_DESARROLLADO>
        codigoInicial = "";
        codigoFinal = "";
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
     * Retorna la variable anio
     * 
     * @return anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }

    /**
     * Retorna la variable mes
     * 
     * @return mes
     */
    public String getMes() {
        return mes;
    }

    /**
     * Asigna la variable mes
     * 
     * @param mes
     * Variable a asignar en mes
     */
    public void setMes(String mes) {
        this.mes = mes;
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
     * Retorna la lista listaAnoTrabajo
     * 
     * @return listaAnoTrabajo
     */
    public List<Registro> getListaAnoTrabajo() {
        return listaAnoTrabajo;
    }

    /**
     * Asigna la lista listaAnoTrabajo
     * 
     * @param listaAnoTrabajo
     * Variable a asignar en listaAnoTrabajo
     */
    public void setListaAnoTrabajo(List<Registro> listaAnoTrabajo) {
        this.listaAnoTrabajo = listaAnoTrabajo;
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
