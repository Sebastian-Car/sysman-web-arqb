package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.BalanceTerceroControladorEnum;
import com.sysman.contabilidad.enums.BalanceTerceroControladorUrlEnum;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author otorres
 * @version 1, 26/04/2016
 * @modifier amonroy
 * @version 2, 06/04/2017 Proceso de Refactoring y Revision de buenas
 * practicas SonarLint, 10/05/2017 Ajustes en los reemplazos que se
 * envian a las consultas que generan el reporte
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped
public class BalanceTerceroControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    /**
     * Constante definida por el numero de veces que se realiza la
     * definicion de "113px" en el formulario, almacena el texto 113px
     */
    private final String c113px;
    /**
     * Constante definida por el numero de veces que se realiza la
     * definicion de ""140px"" en el formulario, almacena el texto
     * "140px"
     */
    private final String c140px;
    /**
     * Constante definida por el numero de veces que se realiza la
     * definicion de "167px" en el formulario, almacena el texto 167px
     */
    private final String c167px;
    /**
     * Constante definida por el numero de veces que se realiza la
     * definicion de "193px" en el formulario, almacena el texto 193px
     */
    private final String c193px;
    /**
     * Constante definida por el numero de veces que se realiza la
     * definicion de "219px" en el formulario, almacena el texto 219px
     */
    private final String c219px;
    /**
     * Constante definida por el numero de veces que se realiza la
     * definicion de "244px" en el formulario, almacena el texto 244px
     */
    private final String c244px;
    /**
     * Constante definida por el numero de veces que se realiza la
     * definicion de "270px" en el formulario, almacena el texto 270px
     */
    private final String c270px;
    /**
     * Constante definida por el numero de veces que se realiza la
     * definicion de "295px" en el formulario, almacena el texto 295px
     */
    private final String c295px;
    /**
     * Constante definida por el numero de veces que se realiza la
     * definicion de "block" en el formulario, almacena el texto block
     */
    private final String cBlock;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al informe "000690BalancePorTercero" en el formulario,
     * almacena el texto 000690BalancePorTercero
     */
    private final String cBalancePorTercero;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo CODIGO en el formulario, almacena el texto
     * CODIGO el cual es un campo del registro
     */
    private final String cCodigo;

    private boolean saldoCero;
    private boolean formato;
    private boolean sinMayorizar;
    private boolean meses;
    private boolean tercero;
    private boolean formatoEspecial;
    private String codigoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String codigoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private String terceroInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String terceroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private String mostrarMesFinal;
    private String mostrarSaldoCero;
    private String mostrarFormato;
    private String mostrarSinMayorizar;
    private String mostrarAgrupado;
    private String arribaBotones;
    private String arribaCodigoInicial;
    private String arribaCodigoFinal;
    private String arribaTerceroInicial;
    private String arribaTerceroFinal;
    private String arribaSinMayorizar;
    private String arribaSaldo;
    private String arribaAgrupado;
    private String arribaMesFinal;
    private String arribaFormato;
    private String arribaFormatoEspecial;
    private String anio;
    private String titulo;
    private int mesFinal;
    private int mesInicial;
    private StreamedContent archivoDescarga;
    private List<Registro> listaAnoTrabajo;
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;
    private RegistroDataModelImpl listaTerceroInicial;
    private RegistroDataModelImpl listaTerceroFinal;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of BalanceTerceroControlador
     */
    public BalanceTerceroControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        c113px = "113px";
        c140px = "140px";
        c167px = "167px";
        c193px = "193px";
        c219px = "219px";
        c244px = "244px";
        c270px = "270px";
        c295px = "295px";
        cBlock = "block";
        cBalancePorTercero = "000690BalancePorTercero";
        cCodigo = "CODIGO";
        try {
            numFormulario = GeneralCodigoFormaEnum.BALANCE_TERCERO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(BalanceTerceroControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        arribaBotones = "265px";
        arribaCodigoInicial = "87px";
        arribaCodigoFinal = c113px;
        arribaTerceroInicial = c140px;
        arribaTerceroFinal = c167px;
        arribaSaldo = c193px;
        arribaFormato = c219px;
        arribaFormatoEspecial = "245px";
        arribaSinMayorizar = c244px;
        arribaAgrupado = c270px;
        mostrarMesFinal = "none";
        mostrarSaldoCero = cBlock;
        mostrarFormato = cBlock;
        mostrarSinMayorizar = cBlock;
        mostrarAgrupado = cBlock;
        mesInicial = SysmanFunciones.mes(new Date());
        mesFinal = SysmanFunciones.mes(new Date());
        anio = String.valueOf(SysmanFunciones.ano(new Date()));
        cargarListaAnoTrabajo();
        cargarListaCodigoInicial();
        cargarListaTerceroInicial();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaAnoTrabajo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BalanceTerceroControladorUrlEnum.URL6989
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceTerceroControladorUrlEnum.URL7549
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceTerceroControladorUrlEnum.URL8900
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(BalanceTerceroControladorEnum.PARAM0.getValue(),
                        codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaTerceroInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceTerceroControladorUrlEnum.URL10424
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, BalanceTerceroControladorEnum.PARAM2.getValue());
    }

    public void cargarListaTerceroFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceTerceroControladorUrlEnum.URL11242
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(BalanceTerceroControladorEnum.PARAM1.getValue(),
                        terceroInicial);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, BalanceTerceroControladorEnum.PARAM2.getValue());
    }

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAnoTrabajo() {
        // <CODIGO_DESARROLLADO>
        codigoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        codigoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        cargarListaCodigoInicial();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarmeses() {
        // <CODIGO_DESARROLLADO>
        if (meses) {
            arribaBotones = "245px";
            arribaMesFinal = "87px";
            arribaCodigoInicial = c113px;
            arribaCodigoFinal = c140px;
            arribaTerceroInicial = c167px;
            arribaTerceroFinal = c193px;
            arribaSaldo = c219px;
            arribaFormato = c244px;
            arribaFormatoEspecial = "219px";
            arribaSinMayorizar = c270px;
            arribaAgrupado = c295px;
            mostrarMesFinal = cBlock;
            mostrarSaldoCero = "none";
            mostrarFormato = "none";
            mostrarSinMayorizar = "none";
            mostrarAgrupado = "none";
            saldoCero = false;
            formato = false;
            sinMayorizar = false;
            tercero = false;
        }
        else {
            arribaBotones = "265px";
            arribaCodigoInicial = "87px";
            arribaCodigoFinal = c113px;
            arribaTerceroInicial = c140px;
            arribaTerceroFinal = c167px;
            arribaSaldo = c193px;
            arribaFormato = c219px;
            arribaFormatoEspecial = "245px";
            arribaSinMayorizar = c244px;
            arribaAgrupado = c270px;
            mostrarMesFinal = "none";
            mostrarSaldoCero = cBlock;
            mostrarFormato = cBlock;
            mostrarSinMayorizar = cBlock;
            mostrarAgrupado = cBlock;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarSinMayorizar() {
        // <CODIGO_DESARROLLADO>
        if (sinMayorizar) {
            formato = false;
            tercero = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Arma la condicion que se envia de reemplazo en la consulta del
     * informe a generar. Adicionalmente arma el título que se envia
     * como un parametro del informe
     *
     * @param bldMesesCredito
     * String builder que almacena los valores calcular el credito
     * entre meses del formulario
     * @param bldMesesDebito
     * String builder que almacena los valores calcular el debito
     * entre meses del formulario
     */
    private void generarInformeCondicionales(StringBuilder bldMesesCredito,
        StringBuilder bldMesesDebito) {
        String auxCondicion = sinMayorizar ? "SALDO_AUX_CONTABLE." : "";
        if (meses) {
            if (mesInicial < mesFinal) {
                for (int i = mesInicial; i <= mesFinal; i++) {
                    bldMesesCredito.append(auxCondicion +
                        "CREDITO" + i + "+");
                    bldMesesDebito.append(auxCondicion +
                        "DEBITO" + i + "+");
                }
                titulo = "BALANCE POR TERCERO DE LOS MESES DE "
                    + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesInicial]
                                    .toUpperCase()
                    + " A "
                    + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesFinal]
                                    .toUpperCase()
                    + " DE " + anio;

                bldMesesCredito.insert(0, "(");
                bldMesesCredito.replace(bldMesesCredito.length() - 1,
                                bldMesesCredito.length(), ")");

                bldMesesDebito.insert(0, "(");
                bldMesesDebito.replace(bldMesesDebito.length() - 1,
                                bldMesesDebito.length(), ")");

            }
            else {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB749"));
            }
        }
        else {
            titulo = "BALANCE POR TERCERO DEL MES DE "
                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesInicial]
                + " DE " + anio;
            bldMesesCredito.append(auxCondicion + " CREDITO" + mesInicial);
            bldMesesDebito.append(auxCondicion + " DEBITO" + mesInicial);
        }

    }

    /**
     * Arma la condicion a enviar en la consulta que genera el reporte
     *
     * @param condicionSaldoCero
     * valor previo de la variable condicionSaldoCero
     * @return valor a asignar en la variable condicionSaldoCero
     */
    private String definirCondicionSaldoCero(String condicionSaldoCero) {
        return !saldoCero ? condicionSaldoCero + " "
            + " AND (SALDO_AUX_CONTABLE.SALDO" + mesInicial
            + "       NOT IN(0)  \n"
            + "   OR SALDO_AUX_CONTABLE.DEBITO" + mesInicial
            + "       NOT IN(0)  \n"
            + "   OR SALDO_AUX_CONTABLE.CREDITO" + mesInicial
            + "      NOT IN(0)  \n"
            + "   OR SALDO_AUX_CONTABLE.SALDO" + (mesInicial - 1)
            + "  NOT IN(0)) "
            : " ";
    }

    /**
     * Define las acciones necesarias para generar el informe realiza
     * el reemplazo de valores en la consulta del informe y envía los
     * parámetros definidos
     *
     * @param formato
     * Formato seleccionado por el usuario para generar el informe
     */
    public void generaInforme(ReportesBean.FORMATOS formato) {
        String informe = null;
        String condicionSaldoCero = " ";
        String condicionReportes = " ";
        String firmaContable1 = " ";
        String firmaContable2 = " ";
        String firmaContable3 = " ";
        String cargoContable1 = " ";
        String cargoContable2 = " ";
        String cargoContable3 = " ";
        String documentContb1 = " ";
        String documentContb2 = " ";
        String documentContb3 = " ";
        String agregarMesesCredito = " ";
        String agregarMesesDebito = " ";
        StringBuilder bldMesesCredito = new StringBuilder();
        StringBuilder bldMesesDebito = new StringBuilder();

        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("mayorizar", sinMayorizar ? "0" : "1");
            reemplazar.put("mayoriza", sinMayorizar ? "0" : "1");
            condicionReportes = " AND SALDO_AUX_CONTABLE.TERCERO BETWEEN '"
                + terceroInicial + "' AND '" + terceroFinal + "' ";

            generarInformeCondicionales(bldMesesCredito, bldMesesDebito);

            if (sinMayorizar) {
                informe = "000697BalanceGeneralPorTercero";
            }
            else {
                informe = cBalancePorTercero;
                if (!meses) {
                    condicionReportes = condicionReportes
                        + "\n AND SALDO_AUX_CONTABLE.AUXILIAR = PCK_DATOS.FC_CONS_AUXILIAR";
                }
            }

            condicionSaldoCero = definirCondicionSaldoCero(condicionSaldoCero);

            agregarMesesCredito = bldMesesCredito.toString();
            agregarMesesDebito = bldMesesDebito.toString();

            firmaContable1 = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA CONTABLE 1", modulo, new Date(), true);
            firmaContable2 = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA CONTABLE 2", modulo, new Date(), true);
            firmaContable3 = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA CONTABLE 3", modulo, new Date(), true);
            cargoContable1 = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO CONTABLE 1", modulo, new Date(), true);
            cargoContable2 = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO CONTABLE 2", modulo, new Date(), true);
            cargoContable3 = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO CONTABLE 3", modulo, new Date(), true);
            documentContb1 = ejbSysmanUtil.consultarParametro(compania,
                            "DOCUMENTO CONTABLE 1", modulo, new Date(), true);
            documentContb2 = ejbSysmanUtil.consultarParametro(compania,
                            "DOCUMENTO CONTABLE 2", modulo, new Date(), true);
            documentContb3 = ejbSysmanUtil.consultarParametro(compania,
                            "DOCUMENTO CONTABLE 3", modulo, new Date(), true);

            // Reemplazos BaseBalancesAuxiliares
            reemplazar.put("mesIni", mesInicial);
            reemplazar.put("mesFin", meses ? mesFinal : mesInicial);
            reemplazar.put("mesFinal", mesFinal);
            reemplazar.put("mestrabajo", mesFinal);
            reemplazar.put("manTer", "1");
            reemplazar.put("manAux", "0");
            reemplazar.put("manCen", "0");
            reemplazar.put("manRef", "0");
            reemplazar.put("manFue", "0");
            reemplazar.put("anio", anio);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("auxiliarTercero",
                            condicionReportes + "\n" + condicionSaldoCero);
            reemplazar.put("auxiliarGeneral", "");
            reemplazar.put("auxiliarCentroCosto", "");
            reemplazar.put("auxiliarReferencia", "");
            reemplazar.put("auxiliarFteRecurso", "");
            reemplazar.put("terceroInicial", terceroInicial);
            reemplazar.put("terceroFinal", terceroFinal);
            reemplazar.put("consultaBase", Reporteador.resuelveConsulta(
                            "800046BaseBalancesAuxiliares",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar));
            reemplazar.put("mesActual", mesInicial);
            reemplazar.put("mesAnterior", mesInicial - 1);
            reemplazar.put("mesDebito", agregarMesesDebito);
            reemplazar.put("mesCredito", agregarMesesCredito);
            reemplazar.put("mesFinal", mesFinal);

            // Reemplazo para consulta 000691SubBalancePorTercero
            /*
             * reemplazar.put("baseTercero",
             * Reporteador.resuelveConsulta( sinMayorizar ?
             * "000697BalanceGeneralPorTercero" :
             * "000690BalancePorTercero",
             * Integer.parseInt(SessionUtil.getModulo()),
             * reemplazar));
             */
            reemplazar.put("baseTercero", Reporteador.resuelveConsulta(
                            "000690BalancePorTercero",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar));
            reemplazar.put("condicionAdicional",
                            condicionReportes + "\n" + condicionSaldoCero);

            Reporteador.resuelveConsulta(informe, Integer.parseInt(modulo),
                            reemplazar, parametros);

            parametros.put("PR_TITULO_INFORME", titulo.toUpperCase());
            parametros.put("PR_FIRMA_CONTABLE1", firmaContable1);
            parametros.put("PR_FIRMA_CONTABLE2", firmaContable2);
            parametros.put("PR_FIRMA_CONTABLE3", firmaContable3);
            parametros.put("PR_CARGO_CONTABLE1", cargoContable1);
            parametros.put("PR_CARGO_CONTABLE2", cargoContable2);
            parametros.put("PR_CARGO_CONTABLE3", cargoContable3);
            parametros.put("PR_DOCUMENTO_CONTABLE1", documentContb1);
            parametros.put("PR_DOCUMENTO_CONTABLE2", documentContb2);
            parametros.put("PR_DOCUMENTO_CONTABLE3", documentContb3);
            parametros.put("PR_FORMATO_ESPECIAL", formatoEspecial);

            archivoDescarga = JsfUtil.exportarStreamed(cBalancePorTercero,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SystemException
                        | SysmanException ex) {
            Logger.getLogger(BalanceTerceroControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        codigoInicial = !SysmanFunciones
                        .validarCampoVacio(registroAux.getCampos(), cCodigo)
                            ? registroAux.getCampos().get(cCodigo)
                                            .toString()
                            : "";
        codigoFinal = null;
        cargarListaCodigoFinal();
    }

    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = !SysmanFunciones
                        .validarCampoVacio(registroAux.getCampos(), cCodigo)
                            ? registroAux.getCampos().get(cCodigo)
                                            .toString()
                            : "";
    }

    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = !SysmanFunciones.validarCampoVacio(
                        registroAux.getCampos(),
                        BalanceTerceroControladorEnum.PARAM2.getValue())
                            ? registroAux.getCampos()
                                            .get(BalanceTerceroControladorEnum.PARAM2
                                                            .getValue())
                                            .toString()
                            : "";
        terceroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        cargarListaTerceroFinal();
    }

    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = !SysmanFunciones.validarCampoVacio(
                        registroAux.getCampos(),
                        BalanceTerceroControladorEnum.PARAM2.getValue())
                            ? registroAux.getCampos()
                                            .get(BalanceTerceroControladorEnum.PARAM2
                                                            .getValue())
                                            .toString()
                            : "";
    }

    public boolean getSaldoCero() {
        return saldoCero;
    }

    public void setSaldoCero(boolean saldoCero) {
        this.saldoCero = saldoCero;
    }

    public boolean getFormato() {
        return formato;
    }

    public void setFormato(boolean formato) {
        this.formato = formato;
    }

    public boolean getSinMayorizar() {
        return sinMayorizar;
    }

    public void setSinMayorizar(boolean sinMayorizar) {
        this.sinMayorizar = sinMayorizar;
    }

    public boolean getMeses() {
        return meses;
    }

    public void setMeses(boolean meses) {
        this.meses = meses;
    }

    public boolean getTercero() {
        return tercero;
    }

    public void setTercero(boolean tercero) {
        this.tercero = tercero;
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

    public String getTerceroInicial() {
        return terceroInicial;
    }

    public void setTerceroInicial(String terceroInicial) {
        this.terceroInicial = terceroInicial;
    }

    public String getTerceroFinal() {
        return terceroFinal;
    }

    public void setTerceroFinal(String terceroFinal) {
        this.terceroFinal = terceroFinal;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public int getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(int mesFinal) {
        this.mesFinal = mesFinal;
    }

    public int getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(int mesInicial) {
        this.mesInicial = mesInicial;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaAnoTrabajo() {
        return listaAnoTrabajo;
    }

    public void setListaAnoTrabajo(List<Registro> listaAnoTrabajo) {
        this.listaAnoTrabajo = listaAnoTrabajo;
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

    public RegistroDataModelImpl getListaTerceroInicial() {
        return listaTerceroInicial;
    }

    public void setListaTerceroInicial(
        RegistroDataModelImpl listaTerceroInicial) {
        this.listaTerceroInicial = listaTerceroInicial;
    }

    public RegistroDataModelImpl getListaTerceroFinal() {
        return listaTerceroFinal;
    }

    public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal) {
        this.listaTerceroFinal = listaTerceroFinal;
    }

    /**
     * @return the mostrarMesFinal
     */
    public String getMostrarMesFinal() {
        return mostrarMesFinal;
    }

    /**
     * @param mostrarMesFinal
     * the mostrarMesFinal to set
     */
    public void setMostrarMesFinal(String mostrarMesFinal) {
        this.mostrarMesFinal = mostrarMesFinal;
    }

    /**
     * @return the mostrarSaldoCero
     */
    public String getMostrarSaldoCero() {
        return mostrarSaldoCero;
    }

    /**
     * @param mostrarSaldoCero
     * the mostrarSaldoCero to set
     */
    public void setMostrarSaldoCero(String mostrarSaldoCero) {
        this.mostrarSaldoCero = mostrarSaldoCero;
    }

    /**
     * @return the mostrarFormato
     */
    public String getMostrarFormato() {
        return mostrarFormato;
    }

    /**
     * @param mostrarFormato
     * the mostrarFormato to set
     */
    public void setMostrarFormato(String mostrarFormato) {
        this.mostrarFormato = mostrarFormato;
    }

    /**
     * @return the mostrarSinMayorizar
     */
    public String getMostrarSinMayorizar() {
        return mostrarSinMayorizar;
    }

    /**
     * @param mostrarSinMayorizar
     * the mostrarSinMayorizar to set
     */
    public void setMostrarSinMayorizar(String mostrarSinMayorizar) {
        this.mostrarSinMayorizar = mostrarSinMayorizar;
    }

    /**
     * @return the mostrarAgrupado
     */
    public String getMostrarAgrupado() {
        return mostrarAgrupado;
    }

    /**
     * @param mostrarAgrupado
     * the mostrarAgrupado to set
     */
    public void setMostrarAgrupado(String mostrarAgrupado) {
        this.mostrarAgrupado = mostrarAgrupado;
    }

    /**
     * @return the arribaBotones
     */
    public String getArribaBotones() {
        return arribaBotones;
    }

    /**
     * @param arribaBotones
     * the arribaBotones to set
     */
    public void setArribaBotones(String arribaBotones) {
        this.arribaBotones = arribaBotones;
    }

    /**
     * @return the arribaCodigoInicial
     */
    public String getArribaCodigoInicial() {
        return arribaCodigoInicial;
    }

    /**
     * @param arribaCodigoInicial
     * the arribaCodigoInicial to set
     */
    public void setArribaCodigoInicial(String arribaCodigoInicial) {
        this.arribaCodigoInicial = arribaCodigoInicial;
    }

    /**
     * @return the arribaCodigoFinal
     */
    public String getArribaCodigoFinal() {
        return arribaCodigoFinal;
    }

    /**
     * @param arribaCodigoFinal
     * the arribaCodigoFinal to set
     */
    public void setArribaCodigoFinal(String arribaCodigoFinal) {
        this.arribaCodigoFinal = arribaCodigoFinal;
    }

    /**
     * @return the arribaTerceroInicial
     */
    public String getArribaTerceroInicial() {
        return arribaTerceroInicial;
    }

    /**
     * @param arribaTerceroInicial
     * the arribaTerceroInicial to set
     */
    public void setArribaTerceroInicial(String arribaTerceroInicial) {
        this.arribaTerceroInicial = arribaTerceroInicial;
    }

    /**
     * @return the arribaTereroFinal
     */
    public String getArribaTerceroFinal() {
        return arribaTerceroFinal;
    }

    /**
     * @param arribaTereroFinal
     * the arribaTereroFinal to set
     */
    public void setArribaTerceroFinal(String arribaTereroFinal) {
        this.arribaTerceroFinal = arribaTereroFinal;
    }

    /**
     * @return the arribaSinMayorizar
     */
    public String getArribaSinMayorizar() {
        return arribaSinMayorizar;
    }

    /**
     * @param arribaSinMayorizar
     * the arribaSinMayorizar to set
     */
    public void setArribaSinMayorizar(String arribaSinMayorizar) {
        this.arribaSinMayorizar = arribaSinMayorizar;
    }

    /**
     * @return the arribaAgrupado
     */
    public String getArribaAgrupado() {
        return arribaAgrupado;
    }

    /**
     * @param arribaAgrupado
     * the arribaAgrupado to set
     */
    public void setArribaAgrupado(String arribaAgrupado) {
        this.arribaAgrupado = arribaAgrupado;
    }

    /**
     * @return the arribaMesFinal
     */
    public String getArribaMesFinal() {
        return arribaMesFinal;
    }

    /**
     * @param arribaMesFinal
     * the arribaMesFinal to set
     */
    public void setArribaMesFinal(String arribaMesFinal) {
        this.arribaMesFinal = arribaMesFinal;
    }

    /**
     * @return the arribaSaldo
     */
    public String getArribaSaldo() {
        return arribaSaldo;
    }

    /**
     * @param arribaSaldo
     * the arribaSaldo to set
     */
    public void setArribaSaldo(String arribaSaldo) {
        this.arribaSaldo = arribaSaldo;
    }

    /**
     * @return the arribaFormato
     */
    public String getArribaFormato() {
        return arribaFormato;
    }

    /**
     * @param arribaFormato
     * the arribaFormato to set
     */
    public void setArribaFormato(String arribaFormato) {
        this.arribaFormato = arribaFormato;
    }

    public boolean isFormatoEspecial() {
        return formatoEspecial;
    }

    public void setFormatoEspecial(boolean formatoEspecial) {
        this.formatoEspecial = formatoEspecial;
    }

    public String getArribaFormatoEspecial() {
        return arribaFormatoEspecial;
    }

    public void setArribaFormatoEspecial(String arribaFormatoEspecial) {
        this.arribaFormatoEspecial = arribaFormatoEspecial;
    }

}
