
package com.sysman.contratos;

import static com.sysman.jsfutil.ReportesBean.FACTOR;
import static com.sysman.jsfutil.ReportesBean.TAMANIO_MAXIMO;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.InfDependenciasModalidadSeleccionControladorEnum;
import com.sysman.contratos.enums.InfDependenciasModalidadSeleccionControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
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
import net.sf.jasperreports.engine.JRException;

import com.sysman.contratos.reports.InfDependenciasModalidadSeleccionReporteador;

/**
 *
 * @author jrodriguezr
 * @version 1, 30/09/2015
 *
 * @author eamaya
 * @version 2.0, 09/08/2017 Proceso de Refactoring DSS cambio de
 * Sysdate por new Date()
 *
 */
@ManagedBean
@ViewScoped

public class InfDependenciasModalidadSeleccionControlador
                extends BeanBaseModal {

    private final String compania;
    private final String msgNoRegistrosGraficas;
    private String dependencia;
    private String modalidad;
    private String anioInicial;
    private String anioFinal;
    private List<Registro> listaCmbmodalidad;
    private List<Registro> listaCmbAnoInicial;
    private List<Registro> listaCmbAnoFinal;
    private RegistroDataModelImpl listaCmbdependencia;
    private String nombreDependencia;
    private InfDependenciasModalidadSeleccionReporteador infDependenciasModalidadSeleccionReporteador;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of
     * InfDependenciasModalidadSeleccionControlador
     */
    public InfDependenciasModalidadSeleccionControlador() {
        super();
        compania = SessionUtil.getCompania();
        msgNoRegistrosGraficas = "TB_TB2065";
        numFormulario = GeneralCodigoFormaEnum.INF_DEPENDENCIAS_MODALIDAD_SELECCION_CONTROLADOR
                        .getCodigo();
        try {
            infDependenciasModalidadSeleccionReporteador = new InfDependenciasModalidadSeleccionReporteador();
            validarPermisos();

        }
        catch (Exception ex) {
            Logger.getLogger(InfDependenciasModalidadSeleccionControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        abrirFormulario();
        cargarListacmbmodalidad();
        cargarListacmbAnoInicial();
        cargarListacmbAnoFinal();
        cargarListacmbdependencia();

    }

    @Override
    public void abrirFormulario() {
        anioInicial = Integer.toString(SysmanFunciones.ano(new Date()));
        anioFinal = Integer.toString(SysmanFunciones.ano(new Date()) + 1);
    }

    public void cargarListacmbmodalidad() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCmbmodalidad = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InfDependenciasModalidadSeleccionControladorUrlEnum.URL4814
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
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCmbAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InfDependenciasModalidadSeleccionControladorUrlEnum.URL5358
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
                                                            InfDependenciasModalidadSeleccionControladorUrlEnum.URL5760
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListacmbdependencia() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfDependenciasModalidadSeleccionControladorUrlEnum.URL6256
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCmbdependencia = new RegistroDataModelImpl(urlBean.getUrl(),
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

    private void generaExcel() {
        try {

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(InfDependenciasModalidadSeleccionControladorEnum.MODALIDAD
                            .getValue(),
                            modalidad);

            param.put(InfDependenciasModalidadSeleccionControladorEnum.ANIOINICIAL
                            .getValue(), anioInicial);
            param.put(InfDependenciasModalidadSeleccionControladorEnum.ANIOFINAL
                            .getValue(), anioFinal);

            param.put(GeneralParameterEnum.DEPENDENCIA.getName(), dependencia);

            Registro descripcion = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InfDependenciasModalidadSeleccionControladorUrlEnum.URL5555
                                                                            .getValue())
                                            .getUrl(), param));
            if ((descripcion == null)
                || (descripcion.getCampos().get("DESCRIPCION") == null)) {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString(msgNoRegistrosGraficas));
                return;
            }
            else {
                String pivot = "'"
                    + (descripcion.getCampos().get("DESCRIPCION")).toString()
                    + "'";

                HashMap<String, Object> reemplazar = new HashMap<>();
                reemplazar.put("modalidad", modalidad);
                reemplazar.put("compania", compania);
                reemplazar.put("anioInicial", anioInicial);
                reemplazar.put("anioFinal", anioFinal);
                reemplazar.put("dependencia", dependencia);
                reemplazar.put("pivot", pivot);
                String strSql = Reporteador.resuelveConsulta(
                                "800010DependenciasMod",
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar);

                exportarHojaDatos(strSql);
            }

        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                + ex.getMessage());
            Logger.getLogger(InfDependenciasModalidadSeleccionControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void exportarHojaDatos(String sql) {
        archivoDescarga = null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            boolean rta = infDependenciasModalidadSeleccionReporteador
                            .exportarHojaDatosExcel(sql, stream, compania,
                                            modalidad, anioInicial, anioFinal,
                                            dependencia, nombreDependencia , requestManager);
            if (rta) {

                archivoDescarga = JsfUtil.getArchivoDescarga(
                                new ByteArrayInputStream(stream.toByteArray()),
                                "informe.xls",
                                ConstanteArchivo.EXCEL97.getContentType());

            }
            else {

                JsfUtil.agregarMensajeError(idioma.getString(
                                "msgNoRegistrosGraficas"));
            }

        }
        catch (JRException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private Collection getDataSource(ResultSet rs, int[] tamanios,
        TextColumnBuilder[] arr)
                        throws SQLException, IOException {
        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
            AbstractDataType tipo;
            if (("NUMBER").equals(rs.getMetaData().getColumnTypeName(i))) {
                if ((rs.getMetaData().getScale(i) == 0)
                    && (rs.getMetaData().getPrecision(i) > 0)) {
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
            HashMap map = new HashMap<String, Object>();
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                factorValor(rs, i, tamanios, map, arr);
            }
            dummyCollection.add(map);
        }
        return dummyCollection;
    }

    public void factorValor(ResultSet rs, int i, int[] tamanios,
        Map<String, Object> map, TextColumnBuilder[] arr) {
        int factorAux = 0;
        String valor;

        try {
            valor = ("CLOB").equals(rs.getMetaData().getColumnTypeName(i))
                || ("BLOB").equals(rs.getMetaData().getColumnTypeName(i))
                    ? Acciones.clobToString(rs.getClob(i))
                    : rs.getString(i);
            if (("NUMBER").equals(rs.getMetaData().getColumnTypeName(i))) {
                if ((rs.getMetaData().getScale(i) == 0)
                    && (rs.getMetaData().getPrecision(i) > 0)) {
                    map.put(rs.getMetaData().getColumnName(i),
                                    rs.getInt(i));
                }
                else {
                    map.put(rs.getMetaData().getColumnName(i),
                                    rs.getBigDecimal(i));
                    factorAux = valor != null ? 3 + (valor.length() / 3) : 0;
                }
            }
            else {
                map.put(rs.getMetaData().getColumnName(i), valor);
            }
            tamanioValor(factorAux, valor, i, tamanios, arr, rs);
        }
        catch (SQLException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void tamanioValor(int factorAux, String valor, int i, int[] tamanios,
        TextColumnBuilder[] arr, ResultSet rs) {
        if ((valor != null) && (tamanios[i - 1] < (valor.length()
            * (FACTOR + factorAux)))) {
            if ((valor.length()
                * (FACTOR + factorAux)) > TAMANIO_MAXIMO) {
                tamanios[i - 1] = TAMANIO_MAXIMO;
            }
            else {
                try {
                    tamanios[i - 1] = (int) (rs.getString(i).length()
                        * (FACTOR + factorAux));
                }
                catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }
            }
            arr[i - 1].setWidth(tamanios[i - 1]);
        }
    }

    public void seleccionarFilaCmbdependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependencia = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
        nombreDependencia = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
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

    public List<Registro> getListaCmbmodalidad() {
        return listaCmbmodalidad;
    }

    public void setListaCmbmodalidad(List<Registro> listaCmbmodalidad) {
        this.listaCmbmodalidad = listaCmbmodalidad;
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

    public void setListaCmbAnoFinal(List<Registro> listaCmbAnoFinal) {
        this.listaCmbAnoFinal = listaCmbAnoFinal;
    }

    public RegistroDataModelImpl getListaCmbdependencia() {
        return listaCmbdependencia;
    }

    public void setListaCmbdependencia(
        RegistroDataModelImpl listaCmbdependencia) {
        this.listaCmbdependencia = listaCmbdependencia;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

}