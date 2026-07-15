package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.DiarioSaldosBancariosControladorEnum;
import com.sysman.contabilidad.enums.DiarioSaldosBancariosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
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
 * @author sdaza
 * @version 1, 16/05/2016 MODIFIED BY JRODRIGUEZR
 * @modified spina 07/04/2017 refactorizacion servicios DSS y
 * depuracion sonar
 * @version 3, 21/04/2017 jrodriguezr Se refactoriza el codigo
 * ajustando los llamados a funciones, procedimientos y metodos de la
 * clase Acciones a llamados a EJB. Se cierra coneccion del reporte.
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el c�digo: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 */
@ManagedBean
@ViewScoped
public class DiarioSaldosBancariosControlador extends BeanBaseModal {
    private final String compania;
    private String modulo;
    // <DECLARAR_ATRIBUTOS>
    private String cuentaInicial;
    private String cuentaFinal;
    private Date fechaInicial;
    private Date fechaFin;
    private int anoInicial;
    private String nombreCtaIni;
    private String nombreCtaFin;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaini;
    private RegistroDataModelImpl listacuentafin;
    private StreamedContent archivoDescarga;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of DiarioSaldosBancariosControlador
     */
    public DiarioSaldosBancariosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.DIARIO_SALDOS_BANCARIOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(DiarioSaldosBancariosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        fechaInicial = new Date();
        fechaFin = new Date();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        anoInicial = SysmanFunciones.ano(fechaInicial);
        cargarListaCuentaini();
        cargarListacuentafin();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR712-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 1, Me.Name DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCuentaini() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DiarioSaldosBancariosControladorUrlEnum.URL3746
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anoInicial);

        listaCuentaini = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListacuentafin() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DiarioSaldosBancariosControladorUrlEnum.URL4720
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anoInicial);
        param.put(DiarioSaldosBancariosControladorEnum.CUENTAINICIAL.getValue(),
                        cuentaInicial);

        listacuentafin = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        generaInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimircmdExcel() {
        // <CODIGO_DESARROLLADO>
        generaInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarFechaini() {
        // <CODIGO_DESARROLLADO>
        cuentaInicial = "";
        cuentaFinal = "";
        nombreCtaIni = "";
        nombreCtaFin = "";
        fechaFin = null;
        anoInicial = SysmanFunciones.ano(fechaInicial);
        cargarListaCuentaini();
        cargarListacuentafin();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarfechafin() {
        cuentaInicial = "";
        nombreCtaIni = "";
        cuentaFinal = "";
        nombreCtaFin = "";
        cargarListaCuentaini();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaini(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        nombreCtaIni = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
        cuentaFinal = "";
        nombreCtaFin = "";
        cargarListacuentafin();

    }

    public void seleccionarFilacuentafin(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        nombreCtaFin = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
    }

    public void generaInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        try {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("mesAnterior",
                            SysmanFunciones.mes(fechaInicial) - 1);
            reemplazar.put("anio", SysmanFunciones.ano(fechaInicial));
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal", SysmanFunciones
                            .convertirAFechaCadena(fechaFin));
            reemplazar.put("fechaInicialMes",
                            SysmanFunciones.primeroDeMesCadena(fechaInicial,
                                            "dd/MM/yyyy"));
            Reporteador.resuelveConsulta("000775DiarioSaldosBancos",
                            Integer.parseInt(modulo), reemplazar,
                            parametros);
            parametros.put("PR_TITULO_REPORTE", "DESDE EL "
                + SysmanFunciones.convertirAFechaCadena(fechaInicial)
                + " Y "
                + SysmanFunciones.convertirAFechaCadena(fechaFin));
            parametros.put("PR_COMPANIA", compania);
            parametros.put("PR_NOMBRE_COMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NIT_COMPANIA", "NIT: "
                + SessionUtil.getCompaniaIngreso().getNit());
            String cargoEncTesoreria = ejbSysmanUtil.consultarParametro(
                            compania,
                            "CARGO ENCARGADO DE TESORERIA", modulo,
                            new Date(), true);
            parametros.put("PR_CARGO_ENCARGADO_DE_TESORERIA",
                            cargoEncTesoreria);
            String nombreEncTesoreria = ejbSysmanUtil.consultarParametro(
                            compania,
                            "NOMBRE ENCARGADO DE TESORERIA", modulo,
                            new Date(), true);
            parametros.put("PR_NOMBRE_ENCARGADO_DE_TESORERIA",
                            nombreEncTesoreria);
            archivoDescarga = JsfUtil.exportarStreamed(
                            "000775DiarioSaldosBancos", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (OutOfMemoryError | JRException | IOException
                        | ParseException | SystemException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

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

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public int getAnoInicial() {
        return anoInicial;
    }

    public void setAnoInicial(int anoInicial) {
        this.anoInicial = anoInicial;
    }

    public String getNombreCtaIni() {
        return nombreCtaIni;
    }

    public void setNombreCtaIni(String nombreCtaIni) {
        this.nombreCtaIni = nombreCtaIni;
    }

    public String getNombreCtaFin() {
        return nombreCtaFin;
    }

    public void setNombreCtaFin(String nombreCtaFin) {
        this.nombreCtaFin = nombreCtaFin;
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

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>

}
