package com.sysman.contabilidad;

import static com.sysman.util.SysmanFunciones.nvl;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.DiariosaldosbancofuenteControladorEnum;
import com.sysman.contabilidad.enums.DiariosaldosbancofuenteControladorUrlEnum;
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
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
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
 * @version 1, 19/05/2016 17:17:03 Modificado por dsuesca
 *
 * @modifed jsforero
 * @version 2. 05/04/2017 Se realizo el refactory. Ademas se hicieron
 * las respectivas Correcciones del sonar.
 *
 * @author jlramirez
 * @version 3. 20/04/2017, Manejos de EJBs
 * 
 * @version 4.0, 12/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Remover y/o reemplazar los llamados a ConectorPool por el esquema.
 */
@ManagedBean
@ViewScoped
public class DiariosaldosbancofuenteControlador extends BeanBaseModal {
    private final String compania;
    private final String codigoConst;
    private final String bancoConst;
    // <DECLARAR_ATRIBUTOS>
    private String cuentaInicial;
    private String cuentaFinal;
    private String fuenteInicial;
    private String fuenteFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    @EJB
    private EjbSysmanUtilRemote sysmanUtil;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaini;
    private RegistroDataModelImpl listacuentafin;
    private RegistroDataModelImpl listaFuenteInicial;
    private RegistroDataModelImpl listaFuenteFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of DiariosaldosbancofuenteControlador
     */
    public DiariosaldosbancofuenteControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigoConst = "CODIGO";
        bancoConst = "BANCO";
        try {
            // 717
            numFormulario = GeneralCodigoFormaEnum.DIARIOSALDOSBANCOFUENTE_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            fechaInicial = new Date();
            fechaFinal = new Date();
            fuenteFinal = "ZZZZZZZZZZ";
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(DiariosaldosbancofuenteControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaini();
        cargarListacuentafin();
        cargarListaFuenteInicial();
        cargarListaFuenteFinal();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCuentaini() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DiariosaldosbancofuenteControladorUrlEnum.URL3967
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);

        listaCuentaini = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, bancoConst);
    }

    public void cargarListacuentafin() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DiariosaldosbancofuenteControladorUrlEnum.URL4655
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(DiariosaldosbancofuenteControladorEnum.CUENTAINICIAL
                        .getValue(), String.valueOf(cuentaInicial));

        listacuentafin = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, bancoConst);
    }

    public void cargarListaFuenteInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DiariosaldosbancofuenteControladorUrlEnum.URL5389
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(),
                        SysmanFunciones.ano(fechaInicial));
        listaFuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        codigoConst);
    }

    public void cargarListaFuenteFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DiariosaldosbancofuenteControladorUrlEnum.URL6313
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(),
                        SysmanFunciones.ano(fechaFinal));
        param.put(DiariosaldosbancofuenteControladorEnum.FUENTEINICIAL
                        .getValue(), fuenteInicial);
        listaFuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        codigoConst);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirImprimirexcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void getInforme(FORMATOS formato) {
        try {
            String nombreEncargado = nvl(sysmanUtil.consultarParametro(compania,
                            "NOMBRE ENCARGADO DE TESORERIA",
                            SessionUtil.getModulo(),
                            new Date(), true), "").toString();

            String cargoEncargado = nvl(sysmanUtil.consultarParametro(compania,
                            "CARGO ENCARGADO DE TESORERIA",
                            SessionUtil.getModulo(),
                            new Date(), true), "").toString();

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("bancoInicial", "'" + cuentaInicial + "'");
            reemplazar.put("bancoFinal", "'" + cuentaFinal + "'");
            reemplazar.put("fuenteInicial", "'" + fuenteInicial + "'");
            reemplazar.put("fuenteFinal", "'" + fuenteFinal + "'");

            reemplazar.put("mesAnterior",
                            SysmanFunciones.mes(fechaInicial) - 1);

            reemplazar.put("anio", SysmanFunciones.ano(fechaInicial));

            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));

            reemplazar.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));

            reemplazar.put("fechaInicialMes",
                            SysmanFunciones.primeroDeMesCadena(fechaInicial,
                                            "dd/MM/yyyy"));

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            String reporte = "000783DiarioSaldosBancosFuente";
            // MANEJO DE PARAMETROS DEL REPORTE
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar,
                            parametros);
            parametros.put("PR_CARGO_ENCARGADO_DE_TESORERIA", cargoEncargado);
            parametros.put("PR_NOMBRE_ENCARGADO_DE_TESORERIA", nombreEncargado);
            parametros.put("PR_STRNOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_STRNITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());

            parametros.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));

            parametros.put("PR_FECHAINCIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarFechaini() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaini(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(bancoConst), "")
                        .toString();
        cuentaFinal = null;
        cargarListacuentafin();
    }

    public void seleccionarFilacuentafin(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(bancoConst), "")
                        .toString();
    }

    public void seleccionarFilaFuenteInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoConst), "")
                        .toString();
        fuenteFinal = null;
        cargarListaFuenteFinal();
    }

    public void seleccionarFilaFuenteFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoConst), "")
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

    public String getFuenteInicial() {
        return fuenteInicial;
    }

    public void setFuenteInicial(String fuenteInicial) {
        this.fuenteInicial = fuenteInicial;
    }

    public String getFuenteFinal() {
        return fuenteFinal;
    }

    public void setFuenteFinal(String fuenteFinal) {
        this.fuenteFinal = fuenteFinal;
    }

    public Date getFechaIncial() {
        return fechaInicial;
    }

    public void setFechaIncial(Date fechaIncial) {
        this.fechaInicial = fechaIncial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCuentaini() {
        return listaCuentaini;
    }

    public void setListaCuentaini(RegistroDataModelImpl listaCuentaini) {
        this.listaCuentaini = listaCuentaini;
    }

    public RegistroDataModelImpl getListacuentafin() {
        return listacuentafin;
    }

    public void setListacuentafin(RegistroDataModelImpl listacuentafin) {
        this.listacuentafin = listacuentafin;
    }

    public RegistroDataModelImpl getListaFuenteInicial() {
        return listaFuenteInicial;
    }

    public void setListaFuenteInicial(
        RegistroDataModelImpl listaFuenteInicial) {
        this.listaFuenteInicial = listaFuenteInicial;
    }

    public RegistroDataModelImpl getListaFuenteFinal() {
        return listaFuenteFinal;
    }

    public void setListaFuenteFinal(RegistroDataModelImpl listaFuenteFinal) {
        this.listaFuenteFinal = listaFuenteFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
