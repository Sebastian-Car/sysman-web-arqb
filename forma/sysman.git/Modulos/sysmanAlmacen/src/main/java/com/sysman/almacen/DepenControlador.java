package com.sysman.almacen;

import com.sysman.almacen.enums.DepenControladorEnum;
import com.sysman.almacen.enums.DepenControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
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
 * @version 1, 29/10/2015
 *
 * @version 2, 26/04/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el c�digo: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 */
@ManagedBean
@ViewScoped
public class DepenControlador extends BeanBaseModal {

    private final String compania;
    private final String moduloAlmacen;
    private String dependenciaFinal;
    private String dependenciaInicial;
    private String depenFinal;
    private String depenInicial;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaDepenFinal;
    private RegistroDataModelImpl listaDepenInicial;
    private final String codigoCons;

    /**
     * Creates a new instance of DepenControlador
     */
    public DepenControlador() {
        super();
        compania = SessionUtil.getCompania();
        moduloAlmacen = SessionUtil.getModulo();
        codigoCons = "CODIGO";
        try {
            numFormulario = GeneralCodigoFormaEnum.DEPEN_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(DepenControlador.class.getName()).log(Level.SEVERE,
                            null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaDepenFinal();
        cargarListaDepenInicial();
        abrirFormulario();
    }

    public void cargarListaDepenFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DepenControladorUrlEnum.URL2526
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(DepenControladorEnum.PARAM0.getValue(), dependenciaInicial);

        listaDepenFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void cargarListaDepenInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DepenControladorUrlEnum.URL3270
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDepenInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void oprimircmbPdf() {
        if ((dependenciaFinal != null) && (dependenciaInicial != null)) {
            generarReporteDepRespon(ReportesBean.FORMATOS.PDF);
        }
        else {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
        }
    }

    public void oprimircmbExcel() {
        if ((dependenciaFinal != null) && (dependenciaInicial != null)) {
            generarReporteDepRespon(ReportesBean.FORMATOS.EXCEL97);
        }
        else {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
        }
    }

    public void seleccionarFilaDepenFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependenciaFinal = registroAux.getCampos().get(codigoCons).toString();
        depenFinal = registroAux.getCampos().get("NOMBRE").toString();
    }

    public void seleccionarFilaDepenInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependenciaInicial = registroAux.getCampos().get(codigoCons).toString();
        depenInicial = registroAux.getCampos().get("NOMBRE").toString();
        cargarListaDepenFinal();
    }

    private void generarReporteDepRespon(FORMATOS formato) {
        archivoDescarga = null;
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("depenInicial", dependenciaInicial);
        reemplazar.put("depenFinal", dependenciaFinal);
        String strSql = Reporteador.resuelveConsulta("000358Depyrespon",
                        Integer.parseInt(moduloAlmacen), reemplazar);

        Map<String, Object> parametros = new HashMap<>();
        // MANEJO DE PARAMETROS DEL REPORTE
        parametros.put("PR_STRSQL", strSql);

        try {
            archivoDescarga = JsfUtil.exportarStreamed("000358Depyrespon",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public String getDependenciaFinal() {
        return dependenciaFinal;
    }

    public void setDependenciaFinal(String dependenciaFinal) {
        this.dependenciaFinal = dependenciaFinal;
    }

    public String getDependenciaInicial() {
        return dependenciaInicial;
    }

    public void setDependenciaInicial(String dependenciaInicial) {
        this.dependenciaInicial = dependenciaInicial;
    }

    public String getDepenFinal() {
        return depenFinal;
    }

    public void setDepenFinal(String depenFinal) {
        this.depenFinal = depenFinal;
    }

    public String getDepenInicial() {
        return depenInicial;
    }

    public void setDepenInicial(String depenInicial) {
        this.depenInicial = depenInicial;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaDepenFinal() {
        return listaDepenFinal;
    }

    public void setListaDepenFinal(RegistroDataModelImpl listaDepenFinal) {
        this.listaDepenFinal = listaDepenFinal;
    }

    public RegistroDataModelImpl getListaDepenInicial() {
        return listaDepenInicial;
    }

    public void setListaDepenInicial(RegistroDataModelImpl listaDepenInicial) {
        this.listaDepenInicial = listaDepenInicial;
    }

}
