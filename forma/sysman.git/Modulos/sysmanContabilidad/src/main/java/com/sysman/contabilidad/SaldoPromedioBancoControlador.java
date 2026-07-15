package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.SaldoPromedioBancoControladorEnum;
import com.sysman.contabilidad.enums.SaldoPromedioBancoControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 26/05/2016 17:18:56 -- Modificado por jrodriguezr
 * 
 * @version 2, 12/04/2017, pespitia :<br>
 * Se aplicaron las recomendaciones de SonarLint.<br>
 * Se aplico Refactoring.
 * 
 * @author jreina
 * @version 3, 13/06/2017 Cambio c�digo formulario y actualizaci�n
 * de ConnectorPool
 */
@ManagedBean
@ViewScoped
public class SaldoPromedioBancoControlador extends BeanBaseModal {
    private final String compania;

    /**
     * Constante a nivel de clase que aloja la cadena
     * <code>GeneralParameterEnum.CODIGO</code>
     */
    private final String cCodigo;

    /**
     * Constante a nivel de clase que aloja el valor del enumerado
     * <code>SaldoPromedioBancoControladorEnum.CUENTAINICIAL</code>
     */
    private final String cCuentaIni;

    // <DECLARAR_ATRIBUTOS>
    private String cuentaInicial;
    private String cuentaFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private String anio;

    /**
     * Creates a new instance of SaldoPromedioBancoControlador
     */
    public SaldoPromedioBancoControlador() {
        super();
        compania = SessionUtil.getCompania();

        cCodigo = GeneralParameterEnum.CODIGO.getName();
        cCuentaIni = SaldoPromedioBancoControladorEnum.CUENTAINICIAL.getValue();
        try {
            numFormulario = GeneralCodigoFormaEnum.SALDO_PROMEDIO_BANCO_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(SaldoPromedioBancoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        fechaInicial = fechaFinal = new Date();
        anio = String.valueOf(SysmanFunciones.ano(fechaInicial));
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR790-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 1, Me.Name DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SaldoPromedioBancoControladorUrlEnum.URL4583
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(SaldoPromedioBancoControladorEnum.CLASECONTABLE.getValue(),
                        "B");

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);

    }

    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SaldoPromedioBancoControladorUrlEnum.URL3688
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(cCuentaIni, cuentaInicial);
        param.put(SaldoPromedioBancoControladorEnum.CLASECONTABLE.getValue(),
                        "B");

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Valida el valor de la fecha, si es nulo muestra un mensaje
     * informativo.
     * 
     * @param fecha
     * @param mensaje
     * Mensaje
     * @return <code>true</code> si la fecha es nula
     */
    private boolean validarNuloFecha(Date fecha, String mensaje) {
        if (fecha == null) {
            JsfUtil.agregarMensajeInformativo(idioma.getString(mensaje));
            return true;
        }

        return false;
    }

    /**
     * Valida el valor de un campo, si es <code>null</code> o esta
     * vaio muestra un mensaje informativo.
     * 
     * @param campo
     * @param mensaje
     * @return <code>true</code> si el campo tiene valor nulo o vacio.
     */
    private boolean validarCampo(String campo, String mensaje) {
        if (SysmanFunciones.validarVariableVacio(campo)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString(mensaje));
            return true;
        }

        return false;
    }

    private void generaReporte(FORMATOS formato) {
        try {
            if (validarNuloFecha(fechaInicial, "TB_TB725")
                            || validarNuloFecha(fechaFinal, "TB_TB726")
                            || validarCampo(cuentaInicial, "TB_TB727")
                            || validarCampo(cuentaFinal, "TB_TB728")) {
                return;
            }

            HashMap<String, Object> reemplazar = new HashMap<>();
            String reporte = "000819RptSaldoPromBancoCorpBoy";

            reemplazar.put("es_id", "0");
            reemplazar.put("id_codigo", cCodigo);
            reemplazar.put("id_cuenta", "CUENTA");
            reemplazar.put("filtrosCentro", "");
            reemplazar.put("mesAnterior", String
                            .valueOf(SysmanFunciones.mes(fechaInicial) - 1));
            reemplazar.put("anio", anio);
            reemplazar.put("tipoInicial", "");
            reemplazar.put("tipoFinal", "");
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            reemplazar.put("filtrosTercero", "");
            String baseAuxiliar = Reporteador.resuelveConsulta(
                            "800044BaseAuxiliares",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);
            reemplazar.put("baseAuxiliar", baseAuxiliar);

            // MANEJO DE PARAMETROS DEL REPORTE
            Map<String, Object> parametros = new HashMap<>();
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar,
                            parametros);
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial, "dd/MM/yyyy"));
            parametros.put("PR_FECHAFINAL", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal, "dd/MM/yyyy"));
            parametros.put("PR_CODIGOINICIAL", cuentaInicial);
            parametros.put("PR_CODIGOFINAL", cuentaFinal);


            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarFechainicial() {
        // <CODIGO_DESARROLLADO>
        anio = String.valueOf(SysmanFunciones.ano(fechaInicial));
        cuentaInicial = cuentaFinal = null;
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void onRowSelectCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos().get(cCodigo).toString();
        cargarListaCodigoFinal();
        cuentaFinal = null;
    }

    public void onRowSelectCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos().get(cCodigo).toString();
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

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
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
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
