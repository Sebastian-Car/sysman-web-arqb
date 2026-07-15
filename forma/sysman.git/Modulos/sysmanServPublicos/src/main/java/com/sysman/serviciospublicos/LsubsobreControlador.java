/*-
 * LsubsobreControlador.java
 *
 * 1.0
 *
 * 06/10/2016
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
import com.sysman.jsfutil.Constantes;
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
import com.sysman.serviciospublicos.enums.LsubsobreControladorEnum;
import com.sysman.serviciospublicos.enums.LsubsobreControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
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
 * Controlador del formulario lsinmedicion para generar reportes de
 * subsidios y sobreprecios
 *
 * @version 1.0, 06/10/2016
 * @author NGOMEZ
 *
 * @version 2, 08/06/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 * @version 3 13/07/2017
 * @author asana, Se modifica cargue de codigos al momento de
 * seleccionar ciclos.
 */

@ManagedBean
@ViewScoped
public class LsubsobreControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que especifica el modulo en que se encuentra
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private String verUsuarios;
    private String verEstratos;
    private String verEa;
    private String cicloInicialUno;
    private String cicloFinalUno;
    private String codigoInicialUno;
    private String codigoFinalUno;
    private String cris;
    private String cicloInicialDos;
    private String cicloFinalDos;
    private String servicio;
    private String codigoInicialDos;
    private String codigoFinalDos;
    private StreamedContent archivoDescarga;
    private boolean discriVisible;
    private String nombrePeriodoUno;
    private String nombrePeriodoDos;
    private boolean cuadroVisible;
    private boolean pdf;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCicloInicialUno;
    private RegistroDataModelImpl listaCicloFinalUno;
    private RegistroDataModelImpl listaCicloInicialDos;
    private RegistroDataModelImpl listaCicloFinalDos;
    private RegistroDataModelImpl listaCodigoInicialDos;
    private RegistroDataModelImpl listaCodigoFinalDos;

    private final String numeroCons;
    private final String codigoRutaCons;
    private final String codigoInicialCons;
    private final String codigoFinalCons;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de LsubsobreControlador
     */
    public LsubsobreControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        numeroCons = "NUMERO";
        codigoRutaCons = "CODIGORUTA";
        codigoInicialCons = "CODIGOINICIAL";
        codigoFinalCons = "CODIGOFINAL";

        try {
            numFormulario = GeneralCodigoFormaEnum.LSUBSOBRE_CONTROLADOR
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
        cargarListaCicloInicialUno();
        cargarListaCicloFinalUno();
        cargarListaCicloInicialDos();
        cargarListaCicloFinalDos();
        cargarListaCodigoInicialDos();
        cargarListaCodigoFinalDos();
        // </CARGAR_LISTA_COMBO_GRANDE>
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
    public void cargarListaCicloInicialUno() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LsubsobreControladorUrlEnum.URL5717
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCicloInicialUno = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, numeroCons);
    }

    public void cargarListaCicloFinalUno() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LsubsobreControladorUrlEnum.URL5718
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LsubsobreControladorEnum.PARAM7.getValue(), cicloInicialUno);

        listaCicloFinalUno = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, numeroCons);
    }

    public void cargarListaCicloInicialDos() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LsubsobreControladorUrlEnum.URL7330
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCicloInicialDos = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, numeroCons);
    }

    public void cargarListaCicloFinalDos() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LsubsobreControladorUrlEnum.URL9355
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LsubsobreControladorEnum.PARAM1.getValue(), cicloInicialDos);

        listaCicloFinalDos = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, numeroCons);
    }

    public void cargarListaCodigoInicialDos() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LsubsobreControladorUrlEnum.URL8587
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LsubsobreControladorEnum.PARAM1.getValue(), cicloInicialDos);
        param.put(LsubsobreControladorEnum.PARAM2.getValue(), cicloFinalDos);

        listaCodigoInicialDos = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaCons);
    }

    public void cargarListaCodigoFinalDos() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LsubsobreControladorUrlEnum.URL8588
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LsubsobreControladorEnum.PARAM1.getValue(), cicloInicialDos);
        param.put(LsubsobreControladorEnum.PARAM2.getValue(), cicloFinalDos);
        param.put(LsubsobreControladorEnum.PARAM3.getValue(), codigoInicialDos);

        listaCodigoFinalDos = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaCons);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPDFUno() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInformeUno(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcelUno() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInformeUno(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPDFDos() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        validarInconsistencias(true);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcelDos() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        validarInconsistencias(false);
        // </CODIGO_DESARROLLADO>
    }

    public void validarInconsistencias(boolean formato) {
        Registro regAux = null;

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), cicloFinalDos);

        try {
            if ("01".equals(servicio)) {
                regAux = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                LsubsobreControladorUrlEnum.URL7971
                                                                                .getValue())
                                                .getUrl(), param));

            }
            else if ("02".equals(servicio)) {
                regAux = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                LsubsobreControladorUrlEnum.URL6537
                                                                                .getValue())
                                                .getUrl(), param));
            }
            else if ("03".equals(servicio)) {

                if ("true".equals(cris)) {
                    regAux = RegistroConverter.toRegistro(
                                    requestManager.get(
                                                    UrlServiceUtil.getInstance()
                                                                    .getUrlServiceByUrlByEnumID(
                                                                                    LsubsobreControladorUrlEnum.URL9304
                                                                                                    .getValue())
                                                                    .getUrl(),
                                                    param));

                }
                else {
                    regAux = RegistroConverter.toRegistro(
                                    requestManager.get(
                                                    UrlServiceUtil.getInstance()
                                                                    .getUrlServiceByUrlByEnumID(
                                                                                    LsubsobreControladorUrlEnum.URL9354
                                                                                                    .getValue())
                                                                    .getUrl(),
                                                    param));
                }
            }

            if ((regAux != null)
                && !"0".equals(regAux.getCampos().get("CUENTA"))) {
                pdf = formato;
                cuadroVisible = true;
            }
            else {
                genInformeDos(formato ? FORMATOS.PDF : FORMATOS.EXCEL);
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarServicio() {
        // <CODIGO_DESARROLLADO>
        discriVisible = "03".equals(servicio);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCicloInicialUno(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        if ((registroAux.getCampos().get(numeroCons) != null)
            && !"".equals(registroAux.getCampos().get(numeroCons))) {
            cicloInicialUno = new BigDecimal(registroAux.getCampos()
                            .get(numeroCons).toString()).toString();
            cicloFinalUno = cicloInicialUno;

            if (registroAux.getCampos().get(codigoInicialCons) != null) {
                codigoInicialUno = registroAux.getCampos()
                                .get(codigoInicialCons)
                                .toString();
            }
            else {
                codigoInicialUno = null;
            }

            if (registroAux.getCampos().get(codigoFinalCons) != null) {
                codigoFinalUno = registroAux.getCampos().get(codigoFinalCons)
                                .toString();
            }
            else {
                codigoFinalUno = null;
            }
            nombrePeriodoUno = registroAux.getCampos().get("NOMBREPERIODO")
                            .toString();
        }
        else {
            cicloInicialUno = null;
        }
        cargarListaCicloFinalUno();
    }

    public void seleccionarFilaCicloFinalUno(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        if ((registroAux.getCampos().get(numeroCons) != null)
            && !"".equals(registroAux.getCampos().get(numeroCons))) {
            cicloFinalUno = new BigDecimal(registroAux.getCampos()
                            .get(numeroCons).toString()).toString();
            if (registroAux.getCampos().get(codigoFinalCons) != null) {
                codigoFinalUno = registroAux.getCampos().get(codigoFinalCons)
                                .toString();
            }
            else {
                codigoFinalUno = null;
            }
        }
        else {
            cicloFinalUno = null;
        }
        cargarListaCicloInicialDos();
        cargarListaCodigoInicialDos();
    }

    public void seleccionarFilaCicloInicialDos(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        if ((registroAux.getCampos().get(numeroCons) != null)
            && !"".equals(registroAux.getCampos().get(numeroCons))) {
            cicloInicialDos = new BigDecimal(
                            registroAux.getCampos().get(numeroCons).toString())
                                            .toString();
            cargarListaCicloFinalDos();
            cargarListaCodigoInicialDos();
            cargarListaCodigoFinalDos();

            if (registroAux.getCampos().get(codigoInicialCons) != null) {
                codigoInicialDos = registroAux.getCampos()
                                .get(codigoInicialCons)
                                .toString();
            }
            else {
                codigoInicialDos = null;
            }

            if (registroAux.getCampos().get(codigoFinalCons) != null) {
                codigoFinalDos = registroAux.getCampos().get(codigoFinalCons)
                                .toString();
            }
            else {
                codigoFinalDos = null;
            }

        }
        else {
            cicloInicialDos = null;
        }

    }

    public void seleccionarFilaCicloFinalDos(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        if ((registroAux.getCampos().get(numeroCons) != null)
            && !"".equals(registroAux.getCampos().get(numeroCons))) {
            cicloFinalDos = new BigDecimal(registroAux.getCampos()
                            .get(numeroCons).toString()).toString();

            cargarListaCodigoFinalDos();

            if (registroAux.getCampos().get(codigoFinalCons) != null) {
                codigoFinalDos = registroAux.getCampos().get(codigoFinalCons)
                                .toString();
            }
            else {
                codigoFinalDos = null;
            }
        }
        else {
            cicloFinalDos = null;
        }
        cargarListaCodigoInicialDos();
    }

    public void seleccionarFilaCodigoInicialDos(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicialDos = registroAux.getCampos().get(codigoRutaCons)
                        .toString();
        cargarListaCodigoFinalDos();
    }

    public void seleccionarFilaCodigoFinalDos(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinalDos = registroAux.getCampos().get(codigoRutaCons).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <SET_GET_ATRIBUTOS>
    public String getVerUsuarios() {
        return verUsuarios;
    }

    public void setVerUsuarios(String verUsuarios) {
        this.verUsuarios = verUsuarios;
    }

    public String getVerEstratos() {
        return verEstratos;
    }

    public void setVerEstratos(String verEstratos) {
        this.verEstratos = verEstratos;
    }

    public String getVerEa() {
        return verEa;
    }

    public void setVerEa(String verEa) {
        this.verEa = verEa;
    }

    public String getCicloInicialUno() {
        return cicloInicialUno;
    }

    public void setCicloInicialUno(String cicloInicialUno) {
        this.cicloInicialUno = cicloInicialUno;
    }

    public String getCicloFinalUno() {
        return cicloFinalUno;
    }

    public void setCicloFinalUno(String cicloFinalUno) {
        this.cicloFinalUno = cicloFinalUno;
    }

    public String getCodigoInicialUno() {
        return codigoInicialUno;
    }

    public void setCodigoInicialUno(String codigoInicialUno) {
        this.codigoInicialUno = codigoInicialUno;
    }

    public String getCodigoFinalUno() {
        return codigoFinalUno;
    }

    public void setCodigoFinalUno(String codigoFinalUno) {
        this.codigoFinalUno = codigoFinalUno;
    }

    public String getCris() {
        return cris;
    }

    public void setCris(String cris) {
        this.cris = cris;
    }

    public String getCicloInicialDos() {
        return cicloInicialDos;
    }

    public void setCicloInicialDos(String cicloInicialDos) {
        this.cicloInicialDos = cicloInicialDos;
    }

    public String getCicloFinalDos() {
        return cicloFinalDos;
    }

    public void setCicloFinalDos(String cicloFinalDos) {
        this.cicloFinalDos = cicloFinalDos;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public String getCodigoInicialDos() {
        return codigoInicialDos;
    }

    public void setCodigoInicialDos(String codigoInicialDos) {
        this.codigoInicialDos = codigoInicialDos;
    }

    public String getCodigoFinalDos() {
        return codigoFinalDos;
    }

    public void setCodigoFinalDos(String codigoFinalDos) {
        this.codigoFinalDos = codigoFinalDos;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean isDiscriVisible() {
        return discriVisible;
    }

    public void setDiscriVisible(boolean discriVisible) {
        this.discriVisible = discriVisible;
    }

    public String getNombrePeriodoUno() {
        return nombrePeriodoUno;
    }

    public void setNombrePeriodoUno(String nombrePeriodoUno) {
        this.nombrePeriodoUno = nombrePeriodoUno;
    }

    public String getNombrePeriodoDos() {
        return nombrePeriodoDos;
    }

    public void setNombrePeriodoDos(String nombrePeriodoDos) {
        this.nombrePeriodoDos = nombrePeriodoDos;
    }

    public boolean isCuadroVisible() {
        return cuadroVisible;
    }

    public void setCuadroVisible(boolean cuadroVisible) {
        this.cuadroVisible = cuadroVisible;
    }

    public boolean isPdf() {
        return pdf;
    }

    public void setPdf(boolean pdf) {
        this.pdf = pdf;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListaCicloInicialUno() {
        return listaCicloInicialUno;
    }

    public void setListaCicloInicialUno(
        RegistroDataModelImpl listaCicloInicialUno) {
        this.listaCicloInicialUno = listaCicloInicialUno;
    }

    public RegistroDataModelImpl getListaCicloFinalUno() {
        return listaCicloFinalUno;
    }

    public void setListaCicloFinalUno(
        RegistroDataModelImpl listaCicloFinalUno) {
        this.listaCicloFinalUno = listaCicloFinalUno;
    }

    public RegistroDataModelImpl getListaCicloInicialDos() {
        return listaCicloInicialDos;
    }

    public void setListaCicloInicialDos(
        RegistroDataModelImpl listaCicloInicialDos) {
        this.listaCicloInicialDos = listaCicloInicialDos;
    }

    public RegistroDataModelImpl getListaCicloFinalDos() {
        return listaCicloFinalDos;
    }

    public void setListaCicloFinalDos(
        RegistroDataModelImpl listaCicloFinalDos) {
        this.listaCicloFinalDos = listaCicloFinalDos;
    }

    public RegistroDataModelImpl getListaCodigoInicialDos() {
        return listaCodigoInicialDos;
    }

    public void setListaCodigoInicialDos(
        RegistroDataModelImpl listaCodigoInicialDos) {
        this.listaCodigoInicialDos = listaCodigoInicialDos;
    }

    public RegistroDataModelImpl getListaCodigoFinalDos() {
        return listaCodigoFinalDos;
    }

    public void setListaCodigoFinalDos(
        RegistroDataModelImpl listaCodigoFinalDos) {
        this.listaCodigoFinalDos = listaCodigoFinalDos;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Proceso en que se genera el reporte de la pestania uno
     *
     * @param formato
     * Formato en que se desea generar el reporte
     *
     */
    public void genInformeUno(ReportesBean.FORMATOS formato) {
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("cicloInicial", cicloInicialUno);
            reemplazar.put("cicloFinal", cicloFinalUno);
            reemplazar.put("codigoInicial", codigoInicialUno);
            reemplazar.put("codigoFinal", codigoFinalUno);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBREPERIODO", nombrePeriodoUno);
            parametros.put("PR_FORMS_L_SUBSOBRE_CICLO", cicloInicialUno);
            parametros.put("PR_FORMS_L_SUBSOBRE_CMCICLOF", cicloFinalUno);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            parametros.put("PR_NOMBRE_GERENTE", SysmanFunciones.nvl(
                            ejbSysmanUtilRemote.consultarParametro(compania,
                                            "NOMBRE GERENTE", modulo,
                                            new Date(), false),
                            ""));
            parametros.put("PR_NOMBRE_DIRECTOR_COMERCIAL", SysmanFunciones
                            .nvl(ejbSysmanUtilRemote.consultarParametro(
                                            compania,
                                            "NOMBRE DIRECTOR COMERCIAL",
                                            modulo, new Date(), false),
                                            ""));
            parametros.put("PR_CARGO_DIRECTOR_COMERCIAL", SysmanFunciones
                            .nvl(ejbSysmanUtilRemote.consultarParametro(
                                            compania,
                                            "CARGO DIRECTOR COMERCIAL",
                                            modulo, new Date(), false),
                                            ""));
            parametros.put("PR_INCLUIR_FIRMA_INFORME_ SUBSIDIOS",
                            "SI".equals(SysmanFunciones.nvl(
                                            ejbSysmanUtilRemote
                                                            .consultarParametro(
                                                                            compania,
                                                                            "INCLUIR FIRMA INFORME SUBSIDIOS",
                                                                            modulo,
                                                                            new Date(),
                                                                            false),
                                            "NO")));

            parametros.put("PR_ESTRATOS", Boolean.parseBoolean(verEstratos));
            parametros.put("PR_USUARIOS", Boolean.parseBoolean(verUsuarios));
            parametros.put("PR_EA", Boolean.parseBoolean(verEa));
            Reporteador.resuelveConsulta("001134rptSubSobre",
                            Integer.parseInt(modulo),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed("001134rptSubSobre",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(Constantes.MSM_INFORME_NO_EXISTE),
                            "<br>", e.getMessage()));
        }
    }

    /**
     * Es llamado en el metodo genInformeDos
     *
     * @param excedente
     * @return El nombre del reporte y la consulta
     */
    public String report(boolean excedente) {
        String reporte;
        if ("true".equals(cris)) {
            reporte = "001153INFTarifasPlenasAseo#001153INFTarifasPlenasAseo";
        }
        else {
            String consultaReporte = excedente
                ? "001144INFTARIFASPLENASASEO"
                : "001144INFTARIFASPLENASASEONoTramo";
            reporte = "001144INFTARIFASPLENASASEO#" + consultaReporte + "";

        }
        return reporte;
    }

    /**
     * Proceso en que se genera el reporte de la pestania dos
     *
     * @param formato
     * Formato en que se desea generar el reporte
     *
     */
    public void genInformeDos(ReportesBean.FORMATOS formato) {
        try {
            String reporte;
            String consultaReporte;
            boolean tramoExcedente;
            tramoExcedente = "SI".equals(SysmanFunciones.nvl(
                            ejbSysmanUtilRemote.consultarParametro(compania,
                                            "MANEJA TRAMO EXCEDENTE", modulo,
                                            new Date(), true),
                            "NO"));

            if ("01".equals(servicio)) {
                reporte = "001143InfTarifasplenas";
                consultaReporte = "001143InfTarifasplenasAcueducto";
            }
            else if ("02".equals(servicio)) {
                reporte = "001143InfTarifasplenas";
                consultaReporte = "001143InfTarifasplenasAlcantarillado";
            }
            else {

                reporte = report(tramoExcedente).split("#")[0];
                consultaReporte = report(tramoExcedente).split("#")[1];

            }
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("cicloInicial", cicloInicialDos);
            reemplazar.put("cicloFinal", cicloFinalDos);
            reemplazar.put("codigoInicial", codigoInicialDos);
            reemplazar.put("codigoFinal", codigoFinalDos);
            reemplazar.put("servicio", servicio);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FORMS_L_SUBSOBRE_CMB_CICLO1", cicloInicialDos);
            parametros.put("PR_FORMS_L_SUBSOBRE_CMB_CICLO2", cicloFinalDos);
            parametros.put("PR_FORMS_L_SUBSOBRE_TXT_COD1", codigoInicialDos);
            parametros.put("PR_FORMS_L_SUBSOBRE_TXT_COD2", codigoFinalDos);
            parametros.put("PR_FORMS_L_SUBSOBRE_SERVICIO",
                            "01".equals(servicio) ? "Acueducto"
                                : "02".equals(servicio) ? "Alcantarillado"
                                    : "Aseo");
            Reporteador.resuelveConsulta(consultaReporte,
                            Integer.parseInt(modulo),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(Constantes.MSM_INFORME_NO_EXISTE),
                            "<br>", e.getMessage()));
        }
    }

    public void aceptarCuadroInconsisitencias() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInformeDos(pdf ? FORMATOS.PDF : FORMATOS.EXCEL);
        cuadroVisible = false;
        // </CODIGO_DESARROLLADO>
    }

    public void cancelarCuadroInconsisitencias() {
        // <CODIGO_DESARROLLADO>
        cuadroVisible = false;
        // </CODIGO_DESARROLLADO>
    }
}
