package com.sysman.planeacion;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

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
import com.sysman.planeacion.enums.FultimopreciodecomprasControladorEnum;
import com.sysman.planeacion.enums.FultimopreciodecomprasControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 29/12/2015
 * 
 * @author eamaya
 * @version 2.0, 07/09/2017, Proceso de Refactoring DSS, cambio de
 * SYSDATE por new Date(),cambio nombre formulario por enum, cambio
 * metodos onRow y correcciones SonarLint
 * 
 */
@ManagedBean
@ViewScoped

public class FultimopreciodecomprasControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo = SessionUtil.getModulo();

    private String elementoInicial;
    private String elementoFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private String nombreInicial;
    private String nombreFinal;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listacmbElementoDesde;
    private RegistroDataModelImpl listacmbElementoHasta;

    /**
     * Creates a new instance of FultimopreciodecomprasControlador
     */
    public FultimopreciodecomprasControlador() {
        super();

        numFormulario = GeneralCodigoFormaEnum.FULTIMOPRECIODECOMPRAS_CONTROLADOR
                        .getCodigo();
        // 439
        compania = SessionUtil.getCompania();
        try {
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(FultimopreciodecomprasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init() {

        fechaInicial = new Date();
        fechaFinal = new Date();
        cargarListacmbElementoDesde();

        abrirFormulario();
    }

    public void cargarListacmbElementoDesde() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FultimopreciodecomprasControladorUrlEnum.URL3221
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listacmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.ELEMENTO.getName());
    }

    public void cargarListacmbElementoHasta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FultimopreciodecomprasControladorUrlEnum.URL4666
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(FultimopreciodecomprasControladorEnum.ELEMENTOINICIAL
                        .getValue(),
                        elementoInicial);

        listacmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.ELEMENTO.getName());
    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato) {

        archivoDescarga = null;
        try {

            String parReporte = "000453InfUltimopreciocompra";

            Map<String, Object> remplazar = new TreeMap<>();
            remplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            remplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            remplazar.put("elementoInicial", elementoInicial);
            remplazar.put("elementoFinal", elementoFinal);

            String strsql = Reporteador.resuelveConsulta(parReporte,
                            Integer.parseInt(modulo), remplazar);

            Map<String, Object> parametros = new TreeMap<>();

            String desdeAux = SysmanFunciones
                            .convertirAFechaCadena(fechaInicial);
            String hastaAux = SysmanFunciones.convertirAFechaCadena(fechaFinal);
            parametros.put("PR_STRSQL", strsql);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre() + " - "
                                + SessionUtil.getCompaniaIngreso().getSigla());
            parametros.put("PR_FECHAS", SysmanFunciones.concatenar(desdeAux,
                            " a ", hastaAux));

            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (JRException | ParseException
                        | IOException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(FultimopreciodecomprasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void seleccionarFilacmbElementoDesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.ELEMENTO.getName()), "")
                        .toString();
        nombreInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
                        .toString();
        cargarListacmbElementoHasta();
    }

    public void seleccionarFilacmbElementoHasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.ELEMENTO.getName()), "")
                        .toString();
        nombreFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
                        .toString();
    }

    public String getElementoInicial() {
        return elementoInicial;
    }

    public void setElementoInicial(String elementoInicial) {
        this.elementoInicial = elementoInicial;
    }

    public String getElementoFinal() {
        return elementoFinal;
    }

    public void setElementoFinal(String elementoFinal) {
        this.elementoFinal = elementoFinal;
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

    public String getNombreInicial() {
        return nombreInicial;
    }

    public void setNombreInicial(String nombreInicial) {
        this.nombreInicial = nombreInicial;
    }

    public String getNombreFinal() {
        return nombreFinal;
    }

    public void setNombreFinal(String nombreFinal) {
        this.nombreFinal = nombreFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListacmbElementoDesde() {
        return listacmbElementoDesde;
    }

    public void setListacmbElementoDesde(
        RegistroDataModelImpl listacmbElementoDesde) {
        this.listacmbElementoDesde = listacmbElementoDesde;
    }

    public RegistroDataModelImpl getListacmbElementoHasta() {
        return listacmbElementoHasta;
    }

    public void setListacmbElementoHasta(
        RegistroDataModelImpl listacmbElementoHasta) {
        this.listacmbElementoHasta = listacmbElementoHasta;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

}
