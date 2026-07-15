
package com.sysman.contratos;

import static com.sysman.jsfutil.ReportesBean.FACTOR;
import static com.sysman.jsfutil.ReportesBean.TAMANIO_MAXIMO;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.InfDependTipoGastoModSeleccControladorUrlEnum;
import com.sysman.contratos.reports.InfDependTipoGastoModSeleccReporteador;
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
import com.sysman.persistencia.Acciones;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
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
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.event.SelectEvent;

import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;

/**
 *
 * @author jrodriguezr
 * @version 1, 01/10/2015
 * 
 * @version 2, 09/08/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 */

@ManagedBean
@ViewScoped
public class InfDependTipoGastoModSeleccControlador extends BeanBaseModal {

    private final String compania;
    private final String tipoGastoCons;
    private final String modalidadCons;
    private final String dependenciaCons;
    private final String companiaCons;
    private final String anioInicialCons;
    private final String anioFinalCons;
    private final String destinoCons;
    private String tipoGasto;
    private String modalidad;
    private String dependencia;
    private String anioInicial;
    private String anioFinal;
    private List<Registro> listaCmbmodalidad;
    private List<Registro> listaCmbAnoInicial;
    private List<Registro> listaCmbAnoFinal;
    private RegistroDataModelImpl listaCmbdependencia;
    private String nombreDependencia;
    
    private InfDependTipoGastoModSeleccReporteador infDependTipoGastoModSeleccReporteador;

    /**
     * Creates a new instance of
     * InfDependTipoGastoModSeleccControlador
     */
    public InfDependTipoGastoModSeleccControlador() {
        super();
        compania = SessionUtil.getCompania();
        tipoGastoCons = "tipoGasto";
        modalidadCons = "modalidad";
        dependenciaCons = "dependencia";
        companiaCons = "compania";
        anioInicialCons = "anioInicial";
        anioFinalCons = "anioFinal";
        destinoCons = "destino";
        try {
            numFormulario = GeneralCodigoFormaEnum.INF_DEPEND_TIPO_GASTO_MOD_SELECC_CONTROLADOR.getCodigo();
            anioInicial = String.valueOf(
                            SysmanFunciones.getParteFecha(
                                            new Date(),
                                            Calendar.YEAR));
            anioFinal = String.valueOf(
                            SysmanFunciones.getParteFecha(
                                            new Date(),
                                            Calendar.YEAR)
                                + 1);
            infDependTipoGastoModSeleccReporteador= new InfDependTipoGastoModSeleccReporteador();
            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(InfDependTipoGastoModSeleccControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListacmbmodalidad();
        cargarListacmbAnoInicial();
        cargarListacmbAnoFinal();
        cargarListacmbdependencia();
        abrirFormulario();
    }

    public void cargarListacmbmodalidad() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

            listaCmbmodalidad = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InfDependTipoGastoModSeleccControladorUrlEnum.URL5597
                                                                            .getValue())
                                            .getUrl(), param));
        }catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListacmbAnoInicial() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            
            listaCmbAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InfDependTipoGastoModSeleccControladorUrlEnum.URL6139
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListacmbAnoFinal() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.ANO.getName(),anioInicial);
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            
            listaCmbAnoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InfDependTipoGastoModSeleccControladorUrlEnum.URL6538
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListacmbdependencia() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(InfDependTipoGastoModSeleccControladorUrlEnum.URL7041.getValue());    
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        listaCmbdependencia = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "CODIGO");
    }

    public void oprimirComando85() {
        // <CODIGO_DESARROLLADO>
        generaExcel();
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirComando16() {
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
            String destino = "";
            if ("I".equals(tipoGasto)) {
                destino = "'INVERSION'";
            }
            else if ("F".equals(tipoGasto)) {
                destino = "'FUNCIONAMIENTO'";
            }

            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put(companiaCons, compania);
            reemplazar.put(dependenciaCons, dependencia);
            reemplazar.put(tipoGastoCons, tipoGasto);
            reemplazar.put(modalidadCons, modalidad);
            reemplazar.put(anioInicialCons, anioInicial);
            reemplazar.put(anioFinalCons, anioFinal);
            reemplazar.put(destinoCons, destino);
            String strSql = Reporteador.resuelveConsulta(
                            "800017DependTipoGastoMod",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);

            exportarHojaDatos(strSql,
                            ReportesBean.FORMATOS.EXCEL97);

   
    }

    public void exportarHojaDatos(String sql,
        ReportesBean.FORMATOS formato) {
        try {
            HttpServletResponse response = (HttpServletResponse) FacesContext
                            .getCurrentInstance().getExternalContext()
                            .getResponse();
            ServletOutputStream stream = null;
            if (formato == ReportesBean.FORMATOS.EXCEL97) {
                response.addHeader("Content-disposition",
                                "attachment; filename=hojaDatos.xls");
                response.setContentType("application/vnd.ms-excel");
                stream = response.getOutputStream();
                boolean rta = infDependTipoGastoModSeleccReporteador
                                .exportarHojaDatosExcel(sql, stream, tipoGasto,
                                                compania, dependencia,
                                                modalidad, anioInicial,
                                                anioFinal, nombreDependencia);
                if (!rta) {
                    stream = null;
                    response.reset();
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2065"));
                }
            }
            if (stream != null) {
                stream.flush();
                stream.close();
                FacesContext.getCurrentInstance().responseComplete();
            }
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
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
            tamanioValor(factorAux, valor, i, tamanios, arr, rs);
        }
        catch (SQLException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void tamanioValor(int factorAux, String valor, int i, int[] tamanios,
        TextColumnBuilder[] arr, ResultSet rs) {
        if (valor != null && tamanios[i - 1] < valor.length()
            * (FACTOR + factorAux)) {
            if (valor.length()
                * (FACTOR + factorAux) > TAMANIO_MAXIMO) {
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
        dependencia = registroAux.getCampos().get("CODIGO").toString();
        nombreDependencia = registroAux.getCampos().get("NOMBRE").toString();
    }

    public String getTipoGasto() {
        return tipoGasto;
    }

    public void setTipoGasto(String tipoGasto) {
        this.tipoGasto = tipoGasto;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
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

    public List<Registro> getListaCmbmodalidad() {
        return listaCmbmodalidad;
    }

    public void setListaCmbmodalidad(List<Registro> listacmbmodalidad) {
        this.listaCmbmodalidad = listacmbmodalidad;
    }

    public List<Registro> getlistaCmbAnoInicial() {
        return listaCmbAnoInicial;
    }

    public void setlistaCmbAnoInicial(List<Registro> listaCmbAnoInicial) {
        this.listaCmbAnoInicial = listaCmbAnoInicial;
    }

    public List<Registro> getlistaCmbAnoFinal() {
        return listaCmbAnoFinal;
    }

    public void setlistaCmbAnoFinal(List<Registro> listaCmbAnoFinal) {
        this.listaCmbAnoFinal = listaCmbAnoFinal;
    }

    public RegistroDataModelImpl getlistaCmbdependencia() {
        return listaCmbdependencia;
    }

    public void setlistaCmbdependencia(RegistroDataModelImpl listaCmbdependencia) {
        this.listaCmbdependencia = listaCmbdependencia;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }
}