package com.sysman.almacen;

import com.sysman.almacen.enums.MovimientosPorDependenciaControladorEnum;
import com.sysman.almacen.enums.MovimientosPorDependenciaControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
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
 * @author dmaldonado
 * @version 1, 28/01/2016
 * @modifier amonroy
 * @version 2, 04/05/2017 Proceso de Refactoring
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Cambio c�digo formulario y actualizaci�n
 * de ConnectorPool
 */
@ManagedBean
@ViewScoped
public class MovimientosPorDependenciaControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private final String codigo;
    private String dependencia;
    private String dependenciaFinal;
    private Date desde;
    private Date hasta;
    private String nombreDep;
    private String nombreDepFinal;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaDependencia;
    private RegistroDataModelImpl listaDependenciaFinal;

    /**
     * Creates a new instance of MovimientosPorDependenciaControlador
     */
    public MovimientosPorDependenciaControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        codigo = "CODIGO";
        try {
            numFormulario = GeneralCodigoFormaEnum.MOVIMIENTOS_POR_DEPENDENCIA_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(MovimientosPorDependenciaControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaDependencia();
        cargarListaDependenciaFinal();
        abrirFormulario();
    }

    public void cargarListaDependencia() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MovimientosPorDependenciaControladorUrlEnum.URL2764
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    public void cargarListaDependenciaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MovimientosPorDependenciaControladorUrlEnum.URL3412
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(MovimientosPorDependenciaControladorEnum.PARAM0.getValue(),
                        dependencia);

        listaDependenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void generaInforme(ReportesBean.FORMATOS formato) {

        archivoDescarga = null;
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            String strSql;
            reemplazar.put("desde", SysmanFunciones.formatearFecha(desde));
            reemplazar.put("hasta", SysmanFunciones.formatearFecha(hasta));
            reemplazar.put("dependencia", dependencia);
            reemplazar.put("dependenciaFinal", dependenciaFinal);
            strSql = Reporteador.resuelveConsulta(
                            "000484RelacionMovimientosPorDependencia",
                            Integer.parseInt(modulo), reemplazar);
            parametros.put("PR_ENTREFECHAS",
                            "Desde "
                                + SysmanFunciones.convertirAFechaCadena(desde,
                                                "dd/MM/yyyy")
                                + " hasta "
                                + SysmanFunciones.convertirAFechaCadena(hasta,
                                                "dd/MM/yyyy")
                                + ".");
            parametros.put("PR_DEPENDENCIA",
                            "Entre las dependencias " + nombreDep + "("
                                + dependencia + ")"
                                + " y " + nombreDepFinal + "("
                                + dependenciaFinal + ").");
            parametros.put("PR_STRSQL", strSql);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000484RelacionMovimientosPorDependencia",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarDesde() {
        // <CODIGO_DESARROLLADO>
        if ((desde == null) || (hasta == null)) {
            return;
        }
        if (desde.after(hasta)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1874"));
            hasta = null;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarHasta() {
        // <CODIGO_DESARROLLADO>
        if ((desde == null) || (hasta == null)) {
            return;
        }
        if (desde.after(hasta)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1874"));
            hasta = null;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependencia = registroAux.getCampos().get(codigo).toString();
        nombreDep = registroAux.getCampos().get("NOMBRE").toString();
        cargarListaDependenciaFinal();
        dependenciaFinal = null;
        nombreDepFinal = null;
    }

    public void seleccionarFilaDependenciaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependenciaFinal = registroAux.getCampos().get(codigo).toString();
        nombreDepFinal = registroAux.getCampos().get("NOMBRE").toString();
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    public String getDependenciaFinal() {
        return dependenciaFinal;
    }

    public String getNombreDepFinal() {
        return nombreDepFinal;
    }

    public void setNombreDepFinal(String nombreDepFinal) {
        this.nombreDepFinal = nombreDepFinal;
    }

    public void setDependenciaFinal(String dependenciaFinal) {
        this.dependenciaFinal = dependenciaFinal;
    }

    public Date getDesde() {
        return desde;
    }

    public void setDesde(Date desde) {
        this.desde = desde;
    }

    public Date getHasta() {
        return hasta;
    }

    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    public String getNombreDep() {
        return nombreDep;
    }

    public void setNombreDep(String nombreDep) {
        this.nombreDep = nombreDep;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaDependencia() {
        return listaDependencia;
    }

    public void setListaDependencia(RegistroDataModelImpl listaDependencia) {
        this.listaDependencia = listaDependencia;
    }

    public RegistroDataModelImpl getListaDependenciaFinal() {
        return listaDependenciaFinal;
    }

    public void setListaDependenciaFinal(
        RegistroDataModelImpl listaDependenciaFinal) {
        this.listaDependenciaFinal = listaDependenciaFinal;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }
}
