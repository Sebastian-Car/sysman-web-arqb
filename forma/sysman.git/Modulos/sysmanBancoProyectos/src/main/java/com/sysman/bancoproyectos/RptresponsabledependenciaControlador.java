package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.RptresponsabledependenciaControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
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
 * @author dcastro
 * @version 1, 15/10/2015
 * 
 * @version 2, 27/09/2017, <strong>pespitia</strong>:
 * <li>Se reemplazo el numero del formulario por enumerado.
 * <li>Refactoring de sentencias SQL.
 * <li>Se aplicaron las recomendaciones de SonarLint.
 */
@ManagedBean
@ViewScoped
public class RptresponsabledependenciaControlador extends BeanBaseModal {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual el usuario inicio sesion
     */
    private final String modulo;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>CODIGO</code>
     */
    private final String cCodigo = GeneralParameterEnum.CODIGO.getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania = GeneralParameterEnum.COMPANIA.getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>DEPENDENCIA</code>
     */
    private final String cDependencia = GeneralParameterEnum.DEPENDENCIA
                    .getName();

    private String dependenciaInicial;
    private String dependenciaFinal;
    private StreamedContent archivoDescarga;

    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /** Lista que contiene los items del combo dependencia inicial. */
    private RegistroDataModelImpl listaCmbDependenciaInicial;

    /** Lista que contiene los items del combo dependencia final. */
    private RegistroDataModelImpl listaCmbDependenciaFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of RptresponsabledependenciaControlador
     */
    public RptresponsabledependenciaControlador() {
        super();

        // 283
        numFormulario = GeneralCodigoFormaEnum.RPTRESPONSABLEDEPENDENCIA_CONTROLADOR
                        .getCodigo();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try {
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(RptresponsabledependenciaControlador.class
                            .getName()).log(Level.SEVERE, null, ex);

            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListacmbDependenciaInicial();
        abrirFormulario();
    }

    public void cargarListacmbDependenciaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RptresponsabledependenciaControladorUrlEnum.URL0001
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaCmbDependenciaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListacmbDependenciaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RptresponsabledependenciaControladorUrlEnum.URL0002
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cDependencia, dependenciaInicial);

        listaCmbDependenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void oprimircmbPdf() {
        generarReporte(FORMATOS.PDF);
    }

    public void oprimircmbExcel() {
        generarReporte(FORMATOS.EXCEL97);
    }

    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista:
     * <code>listaCmbDependenciaInicial</code>, asociada al combo
     * dependencia inicial.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbDependenciaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        dependenciaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();

        dependenciaFinal = "";

        cargarListacmbDependenciaFinal();
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista:
     * <code>listaCmbDependenciaFinal</code>, asociada al combo
     * dependencia final.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbDependenciaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        dependenciaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
    }
    // </METODOS_COMBOS_GRANDES>

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Genera un reporte con un determinado formato.
     * 
     * @param formato
     * Tipo de documento a generar.
     */
    private void generarReporte(FORMATOS formato) {
        archivoDescarga = null;
        Map<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        String reporte = "000287ResponsableDependencia";

        // <REEMPLAZAR VARIABLES EN CONSULTA>
        reemplazar.put("dependenciaInicial", SysmanFunciones.concatenar("'",
                        dependenciaInicial, "'"));

        reemplazar.put("dependenciaFinal",
                        SysmanFunciones.concatenar("'", dependenciaFinal, "'"));

        // </REEMPLAZAR VARIABLES EN CONSULTA>

        // <ENVIAR PARAMETROS AL REPORTE>
        parametros.put("PR_DEPENDENCIAINI", dependenciaInicial);
        parametros.put("PR_DEPENDENCIAFIN", dependenciaFinal);

        // </ENVIAR PARAMETROS AL REPORTE>

        Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(modulo),
                        reemplazar, parametros);

        /*-aqui reporte hace referencia al nombre del reporte*/
        try {
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public String getDependenciaInicial() {
        return dependenciaInicial;
    }

    public void setDependenciaInicial(String dependenciaInicial) {
        this.dependenciaInicial = dependenciaInicial;
    }

    public String getDependenciaFinal() {
        return dependenciaFinal;
    }

    public void setDependenciaFinal(String dependenciaFinal) {
        this.dependenciaFinal = dependenciaFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaCmbDependenciaInicial() {
        return listaCmbDependenciaInicial;
    }

    public void setListaCmbDependenciaInicial(
        RegistroDataModelImpl listaCmbDependenciaInicial) {
        this.listaCmbDependenciaInicial = listaCmbDependenciaInicial;
    }

    public RegistroDataModelImpl getListaCmbDependenciaFinal() {
        return listaCmbDependenciaFinal;
    }

    public void setListaCmbDependenciaFinal(
        RegistroDataModelImpl listaCmbDependenciaFinal) {
        this.listaCmbDependenciaFinal = listaCmbDependenciaFinal;
    }

}