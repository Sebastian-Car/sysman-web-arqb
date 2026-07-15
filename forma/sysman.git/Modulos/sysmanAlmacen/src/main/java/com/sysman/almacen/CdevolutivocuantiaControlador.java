package com.sysman.almacen;

import com.sysman.almacen.enums.CdevolutivocuantiaControladorEnum;
import com.sysman.almacen.enums.CdevolutivocuantiaControladorUrlEnum;
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
 * @author NGOMEZ
 * @version 1, 01/02/2016
 * 
 * @author jlramirez
 * @version 2, 26/04/2017, Se realizo refactoring
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Cambio c�digo formulario y actualizaci�n
 * de ConnectorPool
 */
@ManagedBean
@ViewScoped
public class CdevolutivocuantiaControlador extends BeanBaseModal {

    private final String compania;
    private String elementoInicial;
    private String elementoFinal;
    private String elementoInicialNombre;
    private String elementoFinalNombre;
    private String desde;
    private String hasta;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaElementoInicial;
    private RegistroDataModelImpl listaElementoFinal;

    /**
     * Creates a new instance of CdevolutivocuantiaControlador
     */
    public CdevolutivocuantiaControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.CDEVOLUTIVOCUANTIA_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(CdevolutivocuantiaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaElementoInicial();
        cargarListaElementoFinal();
        abrirFormulario();
    }

    public void cargarListaElementoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CdevolutivocuantiaControladorUrlEnum.URL2489
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaElementoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());
    }

    public void cargarListaElementoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CdevolutivocuantiaControladorUrlEnum.URL3360
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(CdevolutivocuantiaControladorEnum.PARAM0.getValue(),
                        elementoInicial);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaElementoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());
    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        genInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        genInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaElementoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGOELEMENTO.getName()), "")
                        .toString();
        elementoInicialNombre = SysmanFunciones.nvl(registroAux.getCampos()
                        .get("NOMBRELARGO"), "").toString();
        elementoFinal = null;
        elementoFinalNombre = null;
        cargarListaElementoFinal();
    }

    public void seleccionarFilaElementoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGOELEMENTO.getName()), "")
                        .toString();
        elementoFinalNombre = SysmanFunciones.nvl(registroAux.getCampos()
                        .get("NOMBRELARGO"), "").toString();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void genInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;

        String reporte = "000503CDevolutivoCuantia";
        try {

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("elementoInicial", elementoInicial);
            reemplazar.put("elementoFinal", elementoFinal);
            reemplazar.put("desde", desde);
            reemplazar.put("hasta", hasta);
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_FORMS_C_DEVOLUTIVOCUANTIA_DESDE",
                            Double.parseDouble(desde));
            parametros.put("PR_FORMS_C_DEVOLUTIVOCUANTIA_HASTA",
                            Double.parseDouble(hasta));
            parametros.put("PR_STRSQL", strSql);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

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

    public String getElementoInicialNombre() {
        return elementoInicialNombre;
    }

    public void setElementoInicialNombre(String elementoInicialNombre) {
        this.elementoInicialNombre = elementoInicialNombre;
    }

    public String getElementoFinalNombre() {
        return elementoFinalNombre;
    }

    public void setElementoFinalNombre(String elementoFinalNombre) {
        this.elementoFinalNombre = elementoFinalNombre;
    }

    public String getDesde() {
        return desde;
    }

    public void setDesde(String desde) {
        this.desde = desde;
    }

    public String getHasta() {
        return hasta;
    }

    public void setHasta(String hasta) {
        this.hasta = hasta;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaElementoInicial() {
        return listaElementoInicial;
    }

    public void setListaElementoInicial(
        RegistroDataModelImpl listaElementoInicial) {
        this.listaElementoInicial = listaElementoInicial;
    }

    public RegistroDataModelImpl getListaElementoFinal() {
        return listaElementoFinal;
    }

    public void setListaElementoFinal(
        RegistroDataModelImpl listaElementoFinal) {
        this.listaElementoFinal = listaElementoFinal;
    }
}
