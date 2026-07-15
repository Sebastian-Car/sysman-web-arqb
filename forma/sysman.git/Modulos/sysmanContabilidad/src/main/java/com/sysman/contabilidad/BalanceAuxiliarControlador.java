package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.BalanceAuxiliarControladorEnum;
import com.sysman.contabilidad.enums.BalanceAuxiliarControladorUrlEnum;
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
 * @version 1, 25/04/2016
 * @author lcortes
 * @version 2, 18/11/2016 15:42:48 -- Modificado por lcortes
 * @version 3, 10/04/2017 10:56:00 -- Modificado por jcrodriguez
 * descripcion:--se adicionaron los servicios para las consultas
 * quemadas --depuracion del controlador
 */
@ManagedBean
@ViewScoped
public class BalanceAuxiliarControlador extends BeanBaseModal {
    /**
     * variable que alamcena la compa�ia
     */
    private final String compania;
    /***
     * variable que alamcena el modulo
     */
    private final String modulo;
    /**
     * variable que alamcena el saldo
     */
    private boolean saldoCero;
    /**
     * varible que alamcena el formato
     */
    private boolean formato;
    /**
     * variable que alamcena el centro de costo
     */
    private boolean porCentroCosto;
    /**
     * variable que alamcena el nivel
     */
    private String mostrarNivel;
    /**
     * variable que almacena el centro de costo
     */
    private String mostrarCentroCosto;
    /**
     * variable que almacena el estado para el reporte
     */
    private String mostrarExcel;
    /**
     * variable que alamcena el formato
     */
    private String mostrarFormato;
    /**
     * variable auxiliar
     */
    private String arribaCentroCosto;
    /**
     * variable auxiliar
     */
    private String arribaNivel;
    /**
     * variable auxiliar
     */
    private String arribaFormato;
    /**
     * variable auxiliar
     */
    private String arribaBotones;
    /**
     * variable que alamcena el codigo inicial
     */
    private String codigoInicial;
    /**
     * variable que alamcena el codigo final
     */
    private String codigoFinal;
    /**
     * variable auxiliar
     */
    private String auxiliarInicial;
    /**
     * variable auxiliar
     */
    private String auxiliarFinal;
    /**
     * variable que alamcena el centro de costo
     */
    private String centroCosto;
    /**
     * variable que almacena el a�o
     */
    private String anio;
    /**
     * variable que almacena el mes
     */
    private int mes;
    /**
     * variable que almacena el nivel
     */
    private int nivelCC;
    /**
     * variable que alaceman el reporte en formato excel y pdf
     */
    private StreamedContent archivoDescarga;
    /**
     * variable que alamcena el listado de a�os
     */
    private List<Registro> listaAnoTrabajo;
    /**
     * variable que lista los codigos inicial
     */
    private RegistroDataModelImpl listaCodigoInicial;
    /**
     * variable que lista los codigos final
     */
    private RegistroDataModelImpl listaCodigoFinal;
    /**
     * variable que lista los codigos auxiliar inicial
     */
    private RegistroDataModelImpl listaAuxiliarInicial;
    /**
     * variable que lista los codigos auxiliar final
     */
    private RegistroDataModelImpl listaAuxiliarFinal;
    /**
     * variable que lista el centro de costo
     */
    private RegistroDataModelImpl listaCentroCosto;
    /**
     * variable que alamcena el nombre del centro de costo
     */
    private String nombreCentroCosto;
    /**
     * variable ejb
     */
    @EJB
    EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of BalanceAuxiliarControlador
     */
    public BalanceAuxiliarControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.BALANCE_AUXILIAR_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        } catch (Exception ex) {
            Logger.getLogger(BalanceAuxiliarControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * metodo que es llamado al iniciar el formulario
     */
    @PostConstruct
    public void inicializar() {
        nivelCC = 2;
        mes = SysmanFunciones.mes(new Date());
        anio = String.valueOf(SysmanFunciones.ano(new Date()));
        arribaBotones = BalanceAuxiliarControladorEnum.PARAM2.getValue();
        arribaFormato = BalanceAuxiliarControladorEnum.PARAM0.getValue();
        arribaCentroCosto = BalanceAuxiliarControladorEnum.PARAM3.getValue();
        arribaNivel = BalanceAuxiliarControladorEnum.PARAM4.getValue();
        mostrarNivel = BalanceAuxiliarControladorEnum.PARAM5.getValue();
        mostrarCentroCosto = BalanceAuxiliarControladorEnum.PARAM5.getValue();
        mostrarExcel = BalanceAuxiliarControladorEnum.PARAM1.getValue();
        mostrarFormato = BalanceAuxiliarControladorEnum.PARAM1.getValue();
        cargarListaAnoTrabajo();
        cargarListaCodigoInicial();
        cargarListaAuxiliarInicial();
        abrirFormulario();
    }

    /**
     * metodo que se llama cuando se abre el formulario
     */
    @Override
    public void abrirFormulario() {
        // heredado del bean base
    }

    /**
     * metodo que carga la lista de a�os
     */
    public void cargarListaAnoTrabajo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnoTrabajo = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            BalanceAuxiliarControladorUrlEnum.URL4224
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * metodo que carga la lista de codigo inicial
     */
    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceAuxiliarControladorUrlEnum.URL4758
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * metodo que carga la lista de codigos final
     */
    public void cargarListaCodigoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceAuxiliarControladorUrlEnum.URL5715
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(BalanceAuxiliarControladorEnum.CODIGOINICIAL.getValue(),
                        codigoInicial);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * metodo que carga la lista auxiliar inicial
     */
    public void cargarListaAuxiliarInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceAuxiliarControladorUrlEnum.URL6839
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(BalanceAuxiliarControladorEnum.ANIO.getValue(),
                        Integer.parseInt(anio));
        listaAuxiliarInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * metodo que carga la lista auxiliar final
     */
    public void cargarListaAuxiliarFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceAuxiliarControladorUrlEnum.URL7567
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(BalanceAuxiliarControladorEnum.CODIGOINICIAL.getValue(), auxiliarInicial);
        param.put(BalanceAuxiliarControladorEnum.ANIO.getValue(),
                        Integer.parseInt(anio));

        listaAuxiliarFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * metodo que carga la lista de centro de costo
     */
    public void cargarListaCentroCosto() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceAuxiliarControladorUrlEnum.URL8425
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(BalanceAuxiliarControladorEnum.NIVEL.getValue(), nivelCC);

        listaCentroCosto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * metodo que se llama cuando se oprime el boton pdf
     */
    public void oprimirImprimir() {
        archivoDescarga = null;
        if (validarNivel()) {
            generaInforme(ReportesBean.FORMATOS.PDF);
        }

    }

    private boolean validarNivel() {
        if (porCentroCosto && nivelCC > 20) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3250"));
            return false;
        }
        if (porCentroCosto
            && SysmanFunciones.validarVariableVacio(centroCosto)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3251"));
            return false;
        }
        return true;
    }

    /**
     * metodo que se llama cuando se oprime el boton excel
     */
    public void oprimirEnviarExcel() {
        archivoDescarga = null;
        if (validarNivel()) {
            generaInforme(ReportesBean.FORMATOS.EXCEL);
        }
    }

    /**
     * metodo que contiene toda la logia para generar el informe en
     * formato pdf o excel
     * 
     * @param formato
     */
    public void generaInforme(ReportesBean.FORMATOS formato) {
        String informe = null;
        try {
            StringBuilder titulo = new StringBuilder("");

            String condicionSaldoCero;
            String condicionReportes = " ";
            String condicionNivel = " ";
            String condicionReporte;
            String pcentroCosto = " ";
            String firmaContable1;
            String firmaContable2;
            String firmaContable3;
            String cargoContable1;
            String cargoContable2;
            String cargoContable3;
            String documentContb1;
            String documentContb2;
            String documentContb3;

            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            if (this.formato) {
                informe = "000696BalanceGeneralPorAuxiliar";
                titulo.append(idioma
                                .getString("TG_BALANCE_POR_AUXILIAR_GENERAL")
                                .toUpperCase());
                titulo.append(" ");
                titulo.append(SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes]);
                titulo.append(" DE ");
                titulo.append(anio);
                titulo.append(" ");
            } else if (!"".equalsIgnoreCase(centroCosto)) {
                informe = "000678BalancePorAuxiliarCC";
                pcentroCosto = " CENTRO DE COSTO: " + centroCosto + " - "
                    + nombreCentroCosto;
                condicionNivel = " SUBSTR(CENTRO_COSTO,1, "
                    + nivelCC + ") = '" + centroCosto + "'";
                condicionReportes = " AUXILIAR BETWEEN '"
                    + auxiliarInicial + "' AND '" + auxiliarFinal + "' "
                    + "   AND TERCERO IS NULL ";

            } else {
                informe = "000678BalancePorAuxiliarCC";
                condicionReportes = " (AUXILIAR BETWEEN '"
                    + auxiliarInicial + "' AND '" + auxiliarFinal + "') ";

                condicionReportes = condicionReportes
                    + (" OR( TERCERO IS NULL" +
                        " AND CENTRO_COSTO IS NULL AND " +
                        "  AUXILIAR IS NULL) ");
            }

            titulo.append(idioma.getString("TB_TB520"));
            titulo.append(" ");
            titulo.append(SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes]);
            titulo.append(" DE ");
            titulo.append(anio);
            condicionSaldoCero = saldoCero ? " "
                : ""
                    + " AND (SALDO" + mes + "        NOT IN(0)  \n"
                    + "   OR DEBITO" + mes + "       NOT IN(0)  \n"
                    + "   OR CREDITO" + mes + "      NOT IN(0)  \n"
                    + "   OR SALDO" + (mes - 1) + "  NOT IN(0)) ";

            firmaContable1 = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA CONTABLE 1", modulo, new Date(), false);
            firmaContable2 = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA CONTABLE 2", modulo, new Date(), false);
            firmaContable3 = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA CONTABLE 3", modulo, new Date(), false);
            cargoContable1 = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO CONTABLE 1", modulo, new Date(), false);
            cargoContable2 = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO CONTABLE 2", modulo, new Date(), false);
            cargoContable3 = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO CONTABLE 3", modulo, new Date(), false);
            documentContb1 = ejbSysmanUtil.consultarParametro(compania,
                            "DOCUMENTO CONTABLE 1", modulo, new Date(), false);
            documentContb2 = ejbSysmanUtil.consultarParametro(compania,
                            "DOCUMENTO CONTABLE 2", modulo, new Date(), false);
            documentContb3 = ejbSysmanUtil.consultarParametro(compania,
                            "DOCUMENTO CONTABLE 3", modulo, new Date(), false);
            condicionNivel = (" ").equals(condicionNivel) ? " "
                : " WHERE " + condicionNivel;
            condicionReportes = (" ").equals(condicionReportes) ? " "
                : (" ").equals(condicionNivel) ? " WHERE" + condicionReportes
                    : " AND " + condicionReportes;
            condicionReporte = condicionNivel + condicionReportes;
            reemplazar.put("mes", mes);
            reemplazar.put("mesActual", mes);
            reemplazar.put("mesAnterior", mes - 1);
            reemplazar.put("anio", anio);
            reemplazar.put("codigoInicial", "'" + codigoInicial + "'");
            reemplazar.put("codigoFinal", "'" + codigoFinal + "'");
            reemplazar.put("auxiliarInicial", auxiliarInicial);
            reemplazar.put("auxiliarFinal", auxiliarFinal);
            reemplazar.put("condicionSaldoCero", condicionSaldoCero);
            reemplazar.put("condicionReporte", condicionReporte);
            reemplazar.put("manTer", "0");
            reemplazar.put("manAux", "1");
            reemplazar.put("manCen", porCentroCosto ? "1" : "0");
            reemplazar.put("manRef", "0");
            reemplazar.put("manFue", "0");
            reemplazar.put("consultaBase", Reporteador.resuelveConsulta(
                            "800046BaseBalances",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar));

            parametros.put("PR_TITULO_INFORME",
                            titulo.toString().toUpperCase());
            parametros.put("PR_CENTROS_COSTO", pcentroCosto);
            parametros.put("PR_FIRMA_CONTABLE1", firmaContable1);
            parametros.put("PR_FIRMA_CONTABLE2", firmaContable2);
            parametros.put("PR_FIRMA_CONTABLE3", firmaContable3);
            parametros.put("PR_CARGO_CONTABLE1", cargoContable1);
            parametros.put("PR_CARGO_CONTABLE2", cargoContable2);
            parametros.put("PR_CARGO_CONTABLE3", cargoContable3);
            parametros.put("PR_DOCUMENTO_CONTABLE1", documentContb1);
            parametros.put("PR_DOCUMENTO_CONTABLE2", documentContb2);
            parametros.put("PR_DOCUMENTO_CONTABLE3", documentContb3);
            Reporteador.resuelveConsulta(informe, Integer.parseInt(modulo),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(informe, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        } catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage() + " " + informe);
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que se ejecuta cuando se cambia un a�o de trabajo
     */
    public void cambiarAnoTrabajo() {

        codigoInicial = null;
        codigoFinal = null;
        cargarListaAnoTrabajo();
        cargarListaCodigoInicial();
        cargarListaAuxiliarInicial();

    }

    /**
     * metodo que actua cuando se cambia de nivel
     */
    public void cambiarnivelCC() {

        centroCosto = null;
        cargarListaCentroCosto();

    }

    /**
     * metodo que actua cuando cambia el centro de costo
     */
    public void cambiarporCentroCosto() {
        nivelCC = 2;
        centroCosto = null;
        if (porCentroCosto) {
            arribaCentroCosto = "260px";
            arribaNivel = BalanceAuxiliarControladorEnum.PARAM0.getValue();
            arribaBotones = "290px";
            mostrarNivel = BalanceAuxiliarControladorEnum.PARAM1.getValue();
            mostrarCentroCosto = BalanceAuxiliarControladorEnum.PARAM1
                            .getValue();
            mostrarExcel = BalanceAuxiliarControladorEnum.PARAM5.getValue();
            mostrarFormato = BalanceAuxiliarControladorEnum.PARAM5.getValue();
            formato = false;
        } else {
            arribaBotones = BalanceAuxiliarControladorEnum.PARAM2.getValue();
            arribaFormato = BalanceAuxiliarControladorEnum.PARAM0.getValue();
            mostrarNivel = BalanceAuxiliarControladorEnum.PARAM5.getValue();
            mostrarCentroCosto = BalanceAuxiliarControladorEnum.PARAM5
                            .getValue();
            mostrarExcel = BalanceAuxiliarControladorEnum.PARAM1.getValue();
            mostrarFormato = BalanceAuxiliarControladorEnum.PARAM1.getValue();
        }
        cargarListaCentroCosto();

    }

    /**
     * metodo que se llama cuando cambia el saldo
     */
    public void cambiarSaldoCero() {

        if (formato || saldoCero || porCentroCosto) {
            mostrarExcel = BalanceAuxiliarControladorEnum.PARAM5.getValue();
        } else {
            arribaBotones = BalanceAuxiliarControladorEnum.PARAM6.getValue();
            mostrarExcel = BalanceAuxiliarControladorEnum.PARAM1.getValue();
        }

    }

    /**
     * metodo que es llamado cuando cambia el formato
     */
    public void cambiarFormato() {

        if (formato || saldoCero || porCentroCosto) {
            mostrarExcel = BalanceAuxiliarControladorEnum.PARAM5.getValue();
        } else {
            arribaBotones = BalanceAuxiliarControladorEnum.PARAM6.getValue();
            mostrarExcel = BalanceAuxiliarControladorEnum.PARAM1.getValue();
        }

    }

    /**
     * metodo que valida el casteo a toString
     * 
     * @param campos
     * @param var
     * @return
     */
    private String cadenaVacia(Registro campos, String var) {
        return SysmanFunciones.validarCampoVacio(campos.getCampos(), var) ? null
            : campos.getCampos().get(var).toString();
    }

    /**
     * metodo que se llama cuando se selecciona una fila o registro
     * 
     * @param event
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = cadenaVacia(registroAux,
                        GeneralParameterEnum.CODIGO.getName());
        codigoFinal = null;
        cargarListaCodigoFinal();
    }

    /**
     * metodo que se llama cuando se selecciona una fila o registro
     * 
     * @param event
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = cadenaVacia(registroAux,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * metodo que se llama cuando se selecciona una fila o registro
     * 
     * @param event
     */
    public void seleccionarFilaAuxiliarInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliarInicial = cadenaVacia(registroAux,
                        GeneralParameterEnum.CODIGO.getName());
        auxiliarFinal = null;
        cargarListaAuxiliarFinal();
    }

    /**
     * metodo que se llama cuando se selecciona una fila o registro
     * 
     * @param event
     */
    public void seleccionarFilaAuxiliarFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliarFinal = cadenaVacia(registroAux,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * metodo que se llama cuando se selecciona una fila o registro
     * 
     * @param event
     */
    public void seleccionarFilaCentroCosto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroCosto = cadenaVacia(registroAux,
                        GeneralParameterEnum.CODIGO.getName());
        nombreCentroCosto = cadenaVacia(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());
    }

    /**
     * set y get
     * 
     * @return
     */
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

    public boolean getPorCentroCosto() {
        return porCentroCosto;
    }

    public void setPorCentroCosto(boolean porCentroCosto) {
        this.porCentroCosto = porCentroCosto;
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

    public String getAuxiliarInicial() {
        return auxiliarInicial;
    }

    public void setAuxiliarInicial(String auxiliarInicial) {
        this.auxiliarInicial = auxiliarInicial;
    }

    public String getAuxiliarFinal() {
        return auxiliarFinal;
    }

    public void setAuxiliarFinal(String auxiliarFinal) {
        this.auxiliarFinal = auxiliarFinal;
    }

    public String getCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(String centroCosto) {
        this.centroCosto = centroCosto;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getNivelCC() {
        return nivelCC;
    }

    public void setNivelCC(int nivelCC) {
        this.nivelCC = nivelCC;
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

    public RegistroDataModelImpl getListaAuxiliarInicial() {
        return listaAuxiliarInicial;
    }

    public void setListaAuxiliarInicial(
        RegistroDataModelImpl listaAuxiliarInicial) {
        this.listaAuxiliarInicial = listaAuxiliarInicial;
    }

    public RegistroDataModelImpl getListaAuxiliarFinal() {
        return listaAuxiliarFinal;
    }

    public void setListaAuxiliarFinal(
        RegistroDataModelImpl listaAuxiliarFinal) {
        this.listaAuxiliarFinal = listaAuxiliarFinal;
    }

    public RegistroDataModelImpl getListaCentroCosto() {
        return listaCentroCosto;
    }

    public void setListaCentroCosto(RegistroDataModelImpl listaCentroCosto) {
        this.listaCentroCosto = listaCentroCosto;
    }

    /**
     * @return the mostrarNivel
     */
    public String getMostrarNivel() {
        return mostrarNivel;
    }

    /**
     * @param mostrarNivel
     * the mostrarNivel to set
     */
    public void setMostrarNivel(String mostrarNivel) {
        this.mostrarNivel = mostrarNivel;
    }

    /**
     * @return the mostrarCentroCosto
     */
    public String getMostrarCentroCosto() {
        return mostrarCentroCosto;
    }

    /**
     * @param mostrarCentroCosto
     * the mostrarCentroCosto to set
     */
    public void setMostrarCentroCosto(String mostrarCentroCosto) {
        this.mostrarCentroCosto = mostrarCentroCosto;
    }

    /**
     * @return the mostrarExcel
     */
    public String getMostrarExcel() {
        return mostrarExcel;
    }

    /**
     * @param mostrarExcel
     * the mostrarExcel to set
     */
    public void setMostrarExcel(String mostrarExcel) {
        this.mostrarExcel = mostrarExcel;
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
     * @return the arribaCentroCosto
     */
    public String getArribaCentroCosto() {
        return arribaCentroCosto;
    }

    /**
     * @param arribaCentroCosto
     * the arribaCentroCosto to set
     */
    public void setArribaCentroCosto(String arribaCentroCosto) {
        this.arribaCentroCosto = arribaCentroCosto;
    }

    /**
     * @return the arribaNivel
     */
    public String getArribaNivel() {
        return arribaNivel;
    }

    /**
     * @param arribaNivel
     * the arribaNivel to set
     */
    public void setArribaNivel(String arribaNivel) {

        this.arribaNivel = arribaNivel;
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
}
