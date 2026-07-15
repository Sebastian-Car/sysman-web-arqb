package com.sysman.contratos;

import static com.sysman.jsfutil.ReportesBean.FACTOR;
import static com.sysman.jsfutil.ReportesBean.TAMANIO_MAXIMO;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.InfDependenciasClaseContratoControladorUrlEnum;
import com.sysman.contratos.reports.InfDependenciasClaseContratoReporteador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.enums.ConstanteArchivo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
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

import net.sf.dynamicreports.report.base.datatype.AbstractDataType;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 30/09/2015
 * 
 * @author eamaya
 * @version 2.0, 08/08/2017 Proceso de Refactoring DSS cambio de
 * Sysdate por new Date()
 * 
 * 
 */
@ManagedBean
@ViewScoped

public class InfDependenciasClaseContratoControlador extends BeanBaseModal {

    private final String compania;
    private final String cNumber;

    private String claseContrato;
    private String dependencia;
    private String anioInicial;
    private String anioFinal;
    private List<Registro> listaCmbClaseContrato;
    private List<Registro> listaCmbAnoInicial;
    private List<Registro> listaCmbAnoFinal;
    private RegistroDataModelImpl listaCmbDependencia;
    private String nombreDependencia;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    private InfDependenciasClaseContratoReporteador infDependenciasClaseContratoReporteador;

    /**
     * Creates a new instance of
     * InfDependenciasClaseContratoControlador
     */
    public InfDependenciasClaseContratoControlador() {
        super();
        compania = SessionUtil.getCompania();
        cNumber = "NUMBER";

        try {
            numFormulario = GeneralCodigoFormaEnum.INF_DEPENDENCIAS_CLASE_CONTRATO_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            infDependenciasClaseContratoReporteador = new InfDependenciasClaseContratoReporteador();
        }
        catch (Exception ex) {
            Logger.getLogger(InfDependenciasClaseContratoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        abrirFormulario();
        cargarListacmbClaseContrato();
        cargarListacmbAnoInicial();
        cargarListacmbAnoFinal();
        cargarListacmbDependencia();

    }

    @Override
    public void abrirFormulario() {
        anioInicial = Integer.toString(SysmanFunciones.ano(new Date()));
        anioFinal = Integer.toString(SysmanFunciones.ano(new Date()) + 1);
    }

    public void cargarListacmbClaseContrato() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaCmbClaseContrato = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InfDependenciasClaseContratoControladorUrlEnum.URL5312
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListacmbAnoInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaCmbAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InfDependenciasClaseContratoControladorUrlEnum.URL5868
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListacmbAnoFinal() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anioInicial);

        try {
            listaCmbAnoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InfDependenciasClaseContratoControladorUrlEnum.URL6287
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListacmbDependencia() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfDependenciasClaseContratoControladorUrlEnum.URL6858
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCmbDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    public void oprimirComando85() {
        // <CODIGO_DESARROLLADO>
        generaExcel();
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirComando14() {
        // <CODIGO_DESARROLLADO>
        generaExcel();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCmbAnoInicial() {
        // <CODIGO_DESARROLLADO>
        anioFinal = null;
        cargarListacmbAnoFinal();
        // </CODIGO_DESARROLLADO>
    }

    public boolean validarVacios() {

        if (claseContrato == null || compania == null || anioInicial == null) {
            return false;
        }
        if (anioFinal == null || dependencia == null) {
            return false;
        }

        return true;
    }

    private void generaExcel() {

        if (!validarVacios()) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2120"));
            return;
        }
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("compania", compania);
        reemplazar.put("claseContrato", claseContrato);
        reemplazar.put("anioInicial", anioInicial);
        reemplazar.put("anioFinal", anioFinal);
        reemplazar.put("dependencia", dependencia);
        String strSql = Reporteador.resuelveConsulta(
                        "800008DependenciasClase",
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar);
        exportarHojaDatos(strSql);

    }

    public void exportarHojaDatos(String sql) {

        try {

            archivoDescarga = null;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            boolean respuesta = infDependenciasClaseContratoReporteador
                            .exportarHojaDatosExcel(sql,
                                            stream, compania, anioInicial,
                                            anioFinal,
                                            dependencia, nombreDependencia,
                                            claseContrato);

            if (respuesta) {

                archivoDescarga = JsfUtil.getArchivoDescarga(
                                new ByteArrayInputStream(stream.toByteArray()),
                                "informe.xls",
                                ConstanteArchivo.EXCEL97.getContentType());
            }
            else {

                JsfUtil.agregarMensajeError(idioma.getString(
                                "TB_TB2065"));
            }
        }
        catch (SQLException | IOException | DRException | SysmanException
                        | JRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private Collection getDataSource(ResultSet rs, int[] tamanios,
        TextColumnBuilder<?>[] arr)
                        throws SQLException, IOException {
        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
            AbstractDataType tipo;
            if (rs.getMetaData().getColumnTypeName(i).equals(cNumber)) {
                if (rs.getMetaData().getScale(i) == 0
                    && rs.getMetaData().getPrecision(i) > 0) {
                    tipo = type.integerType();
                }
                else {
                    tipo = type.bigDecimalType();
                }
            }
            else {
                tipo = type.stringType();
            }
            arr[i - 1] = col.column(rs.getMetaData().getColumnName(i),
                            rs.getMetaData().getColumnName(i), tipo);
            tamanios[i - 1] = (int) (rs.getMetaData().getColumnName(i).length()
                * FACTOR);
            arr[i - 1].setWidth(tamanios[i - 1]);
        }
        Collection dummyCollection = new ArrayList();

        while (rs.next()) {

            dummyCollection.add(crearMap(rs, tamanios, arr));
        }
        return dummyCollection;
    }

    public Map<String, Object> crearMap(ResultSet rs, int[] tamanios,
        TextColumnBuilder<?>[] arr) {
        HashMap<String, Object> map = new HashMap<>();
        String valor;
        int factorAux;
        try {
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                factorAux = 0;
                valor = "CLOB".equals(rs.getMetaData().getColumnTypeName(i))
                    || "BLOB".equals(rs.getMetaData().getColumnTypeName(i))
                        ? Acciones.clobToString(rs.getClob(i))
                        : rs.getString(i);
                if (rs.getMetaData().getColumnTypeName(i).equals(cNumber)) {
                    if (rs.getMetaData().getScale(i) == 0
                        && rs.getMetaData().getPrecision(i) > 0) {
                        map.put(rs.getMetaData().getColumnName(i),
                                        rs.getInt(i));
                    }
                    else {
                        map.put(rs.getMetaData().getColumnName(i),
                                        rs.getBigDecimal(i));
                        factorAux = valor != null ? 3 + valor.length() / 3 : 0;
                    }
                }
                else {
                    map.put(rs.getMetaData().getColumnName(i), valor);
                }
                if (valor != null && tamanios[i - 1] < valor.length()
                    * (FACTOR + factorAux)) {
                    if (valor.length()
                        * (FACTOR + factorAux) > TAMANIO_MAXIMO) {
                        tamanios[i - 1] = TAMANIO_MAXIMO;
                    }
                    else {
                        tamanios[i - 1] = (int) (rs.getString(i).length()
                            * (FACTOR + factorAux));
                    }
                    arr[i - 1].setWidth(tamanios[i - 1]);
                }
            }
        }
        catch (SQLException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return map;
    }

    public void seleccionarFilaCmbDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependencia = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), " ")
                        .toString();
        nombreDependencia = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), " ")
                        .toString();
    }

    public String getClaseContrato() {
        return claseContrato;
    }

    public void setClaseContrato(String claseContrato) {
        this.claseContrato = claseContrato;
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    public String getAnioInicial() {
        return anioInicial;
    }

    public void setAnioInicial(String anioInicial) {
        this.anioInicial = anioInicial;
    }

    public String getAnioFinal() {
        return anioFinal;
    }

    public void setAnioFinal(String anioFinal) {
        this.anioFinal = anioFinal;
    }

    public List<Registro> getListaCmbClaseContrato() {
        return listaCmbClaseContrato;
    }

    public void setListaCmbClaseContrato(List<Registro> listaCmbClaseContrato) {
        this.listaCmbClaseContrato = listaCmbClaseContrato;
    }

    public List<Registro> getListaCmbAnoInicial() {
        return listaCmbAnoInicial;
    }

    public void setListacmbAnoInicial(List<Registro> listaCmbAnoInicial) {
        this.listaCmbAnoInicial = listaCmbAnoInicial;
    }

    public List<Registro> getListaCmbAnoFinal() {
        return listaCmbAnoFinal;
    }

    public void setListacmbAnoFinal(List<Registro> listaCmbAnoFinal) {
        this.listaCmbAnoFinal = listaCmbAnoFinal;
    }

    public RegistroDataModelImpl getListaCmbDependencia() {
        return listaCmbDependencia;
    }

    public void setListacmbDependencia(
        RegistroDataModelImpl listaCmbDependencia) {
        this.listaCmbDependencia = listaCmbDependencia;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

}
