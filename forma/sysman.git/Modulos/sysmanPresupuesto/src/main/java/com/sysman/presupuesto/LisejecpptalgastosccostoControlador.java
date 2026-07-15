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
import com.sysman.presupuesto.enums.LisejecpptalgastosccostoControladorEnum;
import com.sysman.presupuesto.enums.LisejecpptalgastosccostoControladorUrlEnum;
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
 * @author dsuesca
 * @version 1, 29/06/2016
 * @modified jsforero
 * @version 2. 19/04/2017 Se realizo el refactory.
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el c�digo: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 */
@ManagedBean
@ViewScoped
public class LisejecpptalgastosccostoControlador extends BeanBaseModal {
    private final String compania;
    /**
     * Constante que identifica el nombre del campo CODIGO
     */
    private final String campoCodigo;
    /**
     * Constante que identifica el valor de la fecha actual SYSDATE
     */
    // <DECLARAR_ATRIBUTOS>
    private String cuentaInicial;
    private String cuentaFinal;
    private String centroCostoInicial;
    private String centroCostoFinal;
    private String anio;
    private String mes;
    private String nivel;
    private StreamedContent archivoDescarga;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    private String firmaEjecucion1;
    private String firmaEjecucion2;
    private String firmaEjecucion3;
    private String cargoEjecucion1;
    private String cargoEjecucion2;
    private String cargoEjecucion3;

    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;
    private List<Registro> listaMes;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    private RegistroDataModelImpl listaCentroCostoInicial;
    private RegistroDataModelImpl listaCentroCostoFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote sysmanUtil;

    /**
     * Creates a new instance of LisejecpptalgastosccostoControlador
     */
    public LisejecpptalgastosccostoControlador() {
        super();
        compania = SessionUtil.getCompania();
        campoCodigo = GeneralParameterEnum.CODIGO.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.LISEJECPPTALGASTOSCCOSTO_CONTROLADOR
                            .getCodigo();
            validarPermisos();

            // <INI_ADICIONAL>
            anio = String.valueOf(SysmanFunciones.ano(new Date()));
            mes = String.valueOf(SysmanFunciones.mes(new Date()));
            cuentaInicial = centroCostoInicial = "0";
            cuentaFinal = "9999999999999999";
            centroCostoFinal = "99999999999999999999";
            nivel = "16";

            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(
                            LisejecpptalgastosccostoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    public void cargarParametros() {
        try {
            firmaEjecucion1 = SysmanFunciones
                            .nvl(sysmanUtil.consultarParametro(compania,
                                            "FIRMA EJECUCION 1",
                                            SessionUtil.getModulo(),
                                            new Date(), true),
                                            "")
                            .toString();
            firmaEjecucion2 = SysmanFunciones
                            .nvl(sysmanUtil.consultarParametro(compania,
                                            "FIRMA EJECUCION 2",
                                            SessionUtil.getModulo(),
                                            new Date(), true),
                                            "")
                            .toString();
            firmaEjecucion3 = SysmanFunciones
                            .nvl(sysmanUtil.consultarParametro(compania,
                                            "FIRMA EJECUCION 3",
                                            SessionUtil.getModulo(),
                                            new Date(), true),
                                            "")
                            .toString();
            cargoEjecucion1 = SysmanFunciones
                            .nvl(sysmanUtil.consultarParametro(compania,
                                            "CARGO EJECUCION 1",
                                            SessionUtil.getModulo(),
                                            new Date(), true),
                                            "")
                            .toString();
            cargoEjecucion2 = SysmanFunciones
                            .nvl(sysmanUtil.consultarParametro(compania,
                                            "CARGO EJECUCION 2",
                                            SessionUtil.getModulo(),
                                            new Date(), true),
                                            "")
                            .toString();
            cargoEjecucion3 = SysmanFunciones
                            .nvl(sysmanUtil.consultarParametro(compania,
                                            "CARGO EJECUCION 3",
                                            SessionUtil.getModulo(),
                                            new Date(), true),
                                            "")
                            .toString();
        }
        catch (SystemException e) {
            Logger.getLogger(
                            LisejecpptalgastosccostoControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarParametros();
        cargarListaAno();
        cargarListaMes();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        cargarListaCentroCostoInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);

        try {
            UrlBean urlListAno = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            LisejecpptalgastosccostoControladorUrlEnum.URL8140
                                                            .getValue());
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(urlListAno.getUrl(), param));
        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(LisejecpptalgastosccostoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cargarListaMes() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), anio);
        UrlBean urlListM = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisejecpptalgastosccostoControladorUrlEnum.URL8510
                                                        .getValue());
        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(urlListM.getUrl(), param));
        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(LisejecpptalgastosccostoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisejecpptalgastosccostoControladorUrlEnum.URL8934
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), anio);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisejecpptalgastosccostoControladorUrlEnum.URL9915
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), anio);
        param.put(LisejecpptalgastosccostoControladorEnum.CUENTAINICIAL
                        .getValue(), cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    public void cargarListaCentroCostoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisejecpptalgastosccostoControladorUrlEnum.URL11026
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), anio);

        listaCentroCostoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, campoCodigo);
    }

    public void cargarListaCentroCostoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisejecpptalgastosccostoControladorUrlEnum.URL11696
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), anio);
        param.put(GeneralParameterEnum.CENTRO_COSTO.name(), centroCostoInicial);

        listaCentroCostoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, campoCodigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirEXCEL() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPDF() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    private void getInforme(FORMATOS formato) {
        String reporte = "000960LisEjecPptalGastosCCosto";
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("anio", anio);
        reemplazar.put("centroCostoInicial", centroCostoInicial);
        reemplazar.put("centroCostoFinal", centroCostoFinal);
        reemplazar.put("cuentaIncial", cuentaInicial);
        reemplazar.put("cuentaFinal", cuentaFinal);
        reemplazar.put("mes", mes);
        reemplazar.put("nivel", nivel);

        // MANEJO DE PARAMETROS DE REEMPLAZO
        Map<String, Object> parametros = new HashMap<>();
        // MANEJO DE PARAMETROS DEL REPORTE
        Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar, parametros);

        parametros.put("PR_NOMBREDEMES",
                        SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                        .parseInt(mes)].toUpperCase());
        parametros.put("PR_ANO", anio);
        parametros.put("PR_CARGO_EJECUCION_3", cargoEjecucion3);
        parametros.put("PR_CARGO_EJECUCION_1", cargoEjecucion1);
        parametros.put("PR_FIRMA_EJECUCION_2", firmaEjecucion2);
        parametros.put("PR_CARGO_EJECUCION_2", cargoEjecucion2);
        parametros.put("PR_FIRMA_EJECUCION_3", firmaEjecucion3);
        parametros.put("PR_FIRMA_EJECUCION_1", firmaEjecucion1);
        try {
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        centroCostoInicial = centroCostoFinal = cuentaInicial = cuentaFinal = null;
        cargarListaMes();
        cargarListaCentroCostoInicial();
        cargarListaCentroCostoFinal();
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID"), "").toString();
        cuentaFinal = null;
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones.nvl(registroAux.getCampos().get("ID"), "")
                        .toString();
    }

    public void seleccionarFilaCentroCostoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroCostoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(campoCodigo), "")
                        .toString();
        centroCostoFinal = null;
        cargarListaCentroCostoFinal();
    }

    public void seleccionarFilaCentroCostoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroCostoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(campoCodigo), "")
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

    public String getCentroCostoInicial() {
        return centroCostoInicial;
    }

    public void setCentroCostoInicial(String centroCostoInicial) {
        this.centroCostoInicial = centroCostoInicial;
    }

    public String getCentroCostoFinal() {
        return centroCostoFinal;
    }

    public void setCentroCostoFinal(String centroCostoFinal) {
        this.centroCostoFinal = centroCostoFinal;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public List<Registro> getListaMes() {
        return listaMes;
    }

    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
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

    public RegistroDataModelImpl getListaCentroCostoInicial() {
        return listaCentroCostoInicial;
    }

    public void setListaCentroCostoInicial(
        RegistroDataModelImpl listaCentroCostoInicial) {
        this.listaCentroCostoInicial = listaCentroCostoInicial;
    }

    public RegistroDataModelImpl getListaCentroCostoFinal() {
        return listaCentroCostoFinal;
    }

    public void setListaCentroCostoFinal(
        RegistroDataModelImpl listaCentroCostoFinal) {
        this.listaCentroCostoFinal = listaCentroCostoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
