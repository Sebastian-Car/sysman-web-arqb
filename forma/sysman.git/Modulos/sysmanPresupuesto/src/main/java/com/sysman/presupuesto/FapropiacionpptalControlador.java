package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.FapropiacionpptalControladorEnum;
import com.sysman.presupuesto.enums.FapropiacionpptalControladorUrlEnum;
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
 *
 * @author acaceres
 * @version 1, 30/06/2016
 * @version 2, 18/04/2017 jrodriguezr Se refactoriza el codigo SQL de
 * las listas para utilizar dss.
 * @version 3, 24/04/2017 jrodriguezr Se refactoriza el codigo
 * ajustando los llamados a funciones, procedimientos y metodos de la
 * clase Acciones a llamados a EJB.
 */
@ManagedBean
@ViewScoped
public class FapropiacionpptalControlador extends BeanBaseModal {
    private final String compania;

    /**
     * Constante que almacenara la cadena "SYSDATE"
     */
    private final String modulo;

    /**
     * Constante que almacenara la cadena "9999999999999999"
     */
    private final String codigoCuentaFC;

    /**
     * Constante que almacenara la cadena "MSM_TRANS_INTERRUMPIDA"
     */
    private final String msjTranInterC;
    // <DECLARAR_ATRIBUTOS>
    private String cuentaInicial;
    private String cuentaInicialFinal;
    private String cuentaFinal;
    private String cuentaFinalFinal;
    private int mes;
    private int ano;
    private String nombreCuentaIni;
    private String nombreCuentaFin;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaMes;
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of FapropiacionpptalControlador
     */
    public FapropiacionpptalControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigoCuentaFC = "9999999999999999";
        msjTranInterC = "MSM_TRANS_INTERRUMPIDA";
        modulo = SessionUtil.getModulo();
        ano = SysmanFunciones
                        .ano(new Date());
        try {
            numFormulario = GeneralCodigoFormaEnum.FAPROPIACIONPPTAL_CONTROLADOR
                            .getCodigo();
            cuentaInicialFinal = "0";
            cuentaFinalFinal = codigoCuentaFC;
            validarPermisos();
            // <INI_ADICIONAL>
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
        cargarListaMes();
        cargarListaAno();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        ano = SysmanFunciones
                        .ano(new Date());
        mes = SysmanFunciones
                        .mes(new Date());
        cuentaInicial = "0";
        cuentaFinal = codigoCuentaFC;
        cuentaInicialFinal = "0";
        cuentaFinalFinal = codigoCuentaFC;
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaMes() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FapropiacionpptalControladorUrlEnum.URL4768
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAno() {
        //
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FapropiacionpptalControladorUrlEnum.URL5264
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FapropiacionpptalControladorUrlEnum.URL5780
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FapropiacionpptalControladorUrlEnum.URL6546
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(FapropiacionpptalControladorEnum.CUENTAINICIAL.getValue(),
                        cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    public boolean validarParametros(Map<String, Object> parametros)
                    throws SystemException {
        String firmaUnoPresuesto = ejbSysmanUtil.consultarParametro(compania,
                        "FIRMA 1 EN PRESUPUESTO", modulo,
                        new Date(), true);

        if (firmaUnoPresuesto == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB184"));
            return false;
        }

        String firmaDosPresupuesto = ejbSysmanUtil.consultarParametro(
                        compania,
                        "FIRMA 2 EN PRESUPUESTO", modulo,
                        new Date(), true);
        if (firmaDosPresupuesto == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB185"));
            return false;
        }
        String cargoUnoPresupuesto = ejbSysmanUtil.consultarParametro(
                        compania,
                        "CARGO 1 EN PRESUPUESTO", modulo,
                        new Date(), true);
        if (cargoUnoPresupuesto == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB182"));
            return false;
        }
        String cargoDosPresupuesto = ejbSysmanUtil.consultarParametro(
                        compania,
                        "CARGO 2 EN PRESUPUESTO", modulo,
                        new Date(), true);
        if (cargoDosPresupuesto == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB183"));
            return false;
        }
        // MANEJO DE PARAMETROS DEL REPORTE
        parametros.put("PR_FIRMA_1_EN_PRESUPUESTO", firmaUnoPresuesto);
        parametros.put("PR_FIRMA_2_EN_PRESUPUESTO", firmaDosPresupuesto);
        parametros.put("PR_CARGO_1_EN_PRESUPUESTO", cargoUnoPresupuesto);
        parametros.put("PR_CARGO_2_EN_PRESUPUESTO", cargoDosPresupuesto);
        return true;
    }

    public void obtenerReporte(FORMATOS formatos) {
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("cuentaInicial", cuentaInicialFinal);
            reemplazar.put("cuentaFinal", cuentaFinalFinal);
            reemplazar.put("ano", ano);
            reemplazar.put("mes", mes);

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            if (!validarParametros(parametros)) {
                return;
            }

            String enfecha = idioma.getString("TB_TB630") + " "
                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes].toUpperCase()
                + " DE "
                + ano
                + "";

            parametros.put("PR_ENFECHA", enfecha);
            Reporteador.resuelveConsulta("000961FApropiacionPptal",
                            Integer.valueOf(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(
                            "000961FApropiacionPptal", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(SysmanFunciones
                            .concatenar(msjTranInterC, "<br>", e.getMessage()));
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        cuentaInicial = "";
        cuentaFinal = "";
        nombreCuentaIni = "";
        nombreCuentaFin = "";
        cargarListaCuentaInicial();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID"), "").toString();
        cuentaInicialFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID"), "").toString();
        nombreCuentaIni = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();
        cuentaFinal = "";
        nombreCuentaFin = "";
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID"), "").toString();
        cuentaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID"), "").toString();
        cuentaFinalFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID"), "").toString();
        nombreCuentaFin = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()),
                                        "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getCuentaInicial() {
        return cuentaInicial;
    }

    public void setCuentaInicial(String cuentaInicial) {
        this.cuentaInicial = cuentaInicial;
    }

    public String getCuentaFinal() {
        return cuentaFinal;
    }

    public void setCuentaFinal(String cuentaFinal) {
        this.cuentaFinal = cuentaFinal;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getNombreCuentaIni() {
        return nombreCuentaIni;
    }

    public void setNombreCuentaIni(String nombreCuentaIni) {
        this.nombreCuentaIni = nombreCuentaIni;
    }

    public String getNombreCuentaFin() {
        return nombreCuentaFin;
    }

    public void setNombreCuentaFin(String nombreCuentaFin) {
        this.nombreCuentaFin = nombreCuentaFin;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaMes() {
        return listaMes;
    }

    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCuentaInicial() {
        return listaCuentaInicial;
    }

    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial) {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    public RegistroDataModelImpl getListaCuentaFinal() {
        return listaCuentaFinal;
    }

    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
        this.listaCuentaFinal = listaCuentaFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
