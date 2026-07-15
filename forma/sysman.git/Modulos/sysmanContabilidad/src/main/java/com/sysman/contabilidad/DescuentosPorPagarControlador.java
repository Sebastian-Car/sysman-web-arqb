package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.DescuentosPorPagarControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
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
import java.util.List;
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
 * @version 2, 16/05/2016 17:53:09 -- Modificado por jrodriguezr
 *
 * @modified jguerrero
 * @version 2. 10/04/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Adem�s se ajustaron los errores del sonar
 *
 * -- Modificado por lcortes 12/06/2017. Se reemplaza el valor del
 * atributo numFormulario por el enumerado correspondiente y se
 * suprimen las conexiones a la base de datos.
 */
@ManagedBean
@ViewScoped

public class DescuentosPorPagarControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String tipoInicial;
    private String tipoFinal;
    private String cuentaInicial;
    private String cuentaFinal;
    private StreamedContent archivoDescarga;
    private Date fechaInicial;
    private Date fechaFinal;
    private String formato;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaTipoInicial;
    private RegistroDataModelImpl listaTipoFinal;
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private String anio;
    private final String codigoCons;
    private final String mensajeAlertCons;
    private final String tipoCteAfeCons;

    /**
     * Creates a new instance of DescuentosPorPagarControlador
     */
    public DescuentosPorPagarControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigoCons = "CODIGO";
        mensajeAlertCons = "TB_TB553";
        tipoCteAfeCons = "TIPO_CPTE_AFECT";
        try {
            numFormulario = GeneralCodigoFormaEnum.DESCUENTOS_POR_PAGAR_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(DescuentosPorPagarControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        fechaInicial = new Date();
        fechaFinal = new Date();
        anio = String.valueOf(SysmanFunciones.ano(fechaInicial));
        cargarListaTipoInicial();
        cargarListaTipoFinal();
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaTipoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DescuentosPorPagarControladorUrlEnum.URL3764
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void cargarListaTipoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DescuentosPorPagarControladorUrlEnum.URL4440
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("TIPOINICIAL", tipoInicial);

        listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DescuentosPorPagarControladorUrlEnum.URL5170
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DescuentosPorPagarControladorUrlEnum.URL6132
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CUENTA.getName(), cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>

        if (!validarFechas()) {
            return;
        }

        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirVERIFICAREGRYCOM() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (fechaInicial == null) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(mensajeAlertCons));
            return;
        }
        if (fechaFinal == null) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB554"));
            return;
        }
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("anio",
                            String.valueOf(SysmanFunciones.ano(fechaInicial)));
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            String strSQL = Reporteador.resuelveConsulta(
                            "800048EGR_Y_COM_AFECTADOS",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);

            List<Registro> rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                            strSQL);

            StringBuilder textoTxt = new StringBuilder();
            textoTxt.append(SysmanFunciones.padl("EGR Y COM AFECTADOS", 60, " ")
                + "\r\n");

            textoTxt.append(" " + "\r\n");
            textoTxt.append(SysmanFunciones.padr("FECHA", 20, " ")
                + SysmanFunciones.padr("TIPO_CPTE", 20, " ")
                + SysmanFunciones.padr("COMPROBANTE", 20, " ")
                + SysmanFunciones.padr(tipoCteAfeCons, 20, " ")
                + "CMPTE_AFECTADO");
            textoTxt.append("\r\n");
            if (!rs.isEmpty()) {

                for (int j = 0; j < rs.size(); j++) {

                    textoTxt.append(SysmanFunciones.padr(
                                    SysmanFunciones.convertirAFechaCadena(
                                                    (Date) rs.get(j).getCampos()
                                                                    .get("FECHA"),
                                                    "dd/MM/yyyy"),
                                    20, " "));

                    if (!SysmanFunciones.validarCampoVacio(
                                    rs.get(j).getCampos(), "TIPO_CPTE")) {
                        textoTxt.append(SysmanFunciones
                                        .padr(rs.get(j).getCampos()
                                                        .get("TIPO_CPTE")
                                                        .toString(), 20, " "));
                    }

                    if (!SysmanFunciones.validarCampoVacio(
                                    rs.get(j).getCampos(), "COMPROBANTE")) {
                        textoTxt.append(SysmanFunciones
                                        .padr(rs.get(j).getCampos()
                                                        .get("COMPROBANTE")
                                                        .toString(), 20, " "));
                    }

                    if (!SysmanFunciones.validarCampoVacio(
                                    rs.get(j).getCampos(), tipoCteAfeCons)) {
                        textoTxt.append(SysmanFunciones
                                        .padr(rs.get(j).getCampos()
                                                        .get(tipoCteAfeCons)
                                                        .toString(),
                                                        20, " "));
                    }

                    if (!SysmanFunciones.validarCampoVacio(
                                    rs.get(j).getCampos(), "CMPTE_AFECTADO")) {
                        textoTxt.append(rs.get(j).getCampos().get(
                                        "CMPTE_AFECTADO").toString()
                            + "\r\n");
                    }

                }
            }

            archivoDescarga = JsfUtil.getArchivoDescarga(
                            JsfUtil.serializarPlano(textoTxt.toString()),
                            "EGR_Y_COM_AFECTADOS");

        }
        catch (ParseException | JRException | IOException e) {
            Logger.getLogger(DescuentosPorPagarControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        if (!validarFechas()) {
            return;
        }

        archivoDescarga = null;
        generarReporte(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * SE GENERAN LOS REPORTES 776 Y 777 SE DEBE REVISAR EL PROCESO DE
     * ACTUALIZACI�N QUE SE LLEVA A CABO ANTES DE GENERAR EL REPORTE
     *
     * @param formato
     * FORMATO DEL REPORTE PUEDE SER EXCEL O PDF.
     */
    private void generarReporte(ReportesBean.FORMATOS formato) {
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            String reporte = "1".equals(this.formato)
                ? "000776LisDescuentosPorPagar1"
                : "000777LisDescuentosPorPagar2";
            String fechaIni = SysmanFunciones
                            .convertirAFechaCadena(fechaInicial);
            String fechaFin = SysmanFunciones.convertirAFechaCadena(fechaFinal);

            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("tipoInicial", tipoInicial);
            reemplazar.put("tipoFinal", tipoFinal);
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);

            // MANEJO DE PARAMETROS DEL REPORTE
            Map<String, Object> parametros = new HashMap<>();
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            parametros.put("PR_FECHAINICIAL", fechaIni);
            parametros.put("PR_FECHAFINAL", fechaFin);
            parametros.put("PR_CUENTAINICIAL", cuentaInicial);
            parametros.put("PR_CUENTAFINAL", cuentaFinal);

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
    public void cambiarFechaInicial() {
        // <CODIGO_DESARROLLADO>
        anio = String.valueOf(SysmanFunciones.ano(fechaInicial));
        cuentaInicial = null;
        cargarListaCuentaInicial();
        cuentaFinal = null;
        cargarListaCuentaFinal();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaTipoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoInicial = registroAux.getCampos().get(codigoCons).toString();
        cargarListaTipoFinal();
        tipoFinal = null;
    }

    public void seleccionarFilaTipoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoFinal = registroAux.getCampos().get(codigoCons).toString();
    }

    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos().get(codigoCons).toString();
        cargarListaCuentaFinal();
        cuentaFinal = null;
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos().get(codigoCons).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getTipoInicial() {
        return tipoInicial;
    }

    public void setTipoInicial(String tipoInicial) {
        this.tipoInicial = tipoInicial;
    }

    public String getTipoFinal() {
        return tipoFinal;
    }

    public void setTipoFinal(String tipoFinal) {
        this.tipoFinal = tipoFinal;
    }

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

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
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

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public String getAnio() {
        return anio;
    }

    public RegistroDataModelImpl getListaTipoInicial() {
        return listaTipoInicial;
    }

    public void setListaTipoInicial(RegistroDataModelImpl listaTipoInicial) {
        this.listaTipoInicial = listaTipoInicial;
    }

    public RegistroDataModelImpl getListaTipoFinal() {
        return listaTipoFinal;
    }

    public void setListaTipoFinal(RegistroDataModelImpl listaTipoFinal) {
        this.listaTipoFinal = listaTipoFinal;
    }

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

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    private boolean validarFechas() {
        boolean rta = true;
        if (fechaFinal.before(fechaInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB528"));
            rta = false;
        }
        return rta;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
