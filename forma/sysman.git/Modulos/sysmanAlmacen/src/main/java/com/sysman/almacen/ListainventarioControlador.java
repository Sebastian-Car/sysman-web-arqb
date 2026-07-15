package com.sysman.almacen;

import com.sysman.almacen.enums.ListainventarioControladorEnum;
import com.sysman.almacen.enums.ListainventarioControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dcastro
 * @version 1, 17/10/2015
 * 
 * @author eamaya
 * @version 2, 04/05/2017 Proceso de Refactoring, Manejo de EJBs y
 * Correcciones SonarLint
 */
@ManagedBean
@ViewScoped

public class ListainventarioControlador extends BeanBaseModal {

    private final String compania;
    private String opciones;
    private String elementoDesde;
    private String elementoHasta;
    private String elementolabelDesde;
    private String elementolabelHasta;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaCmbElementoDesde;
    private RegistroDataModelImpl listaCmbElementoHasta;
    private final String modulo;
    private final String msgTransInterrumpida = idioma
                    .getString("MSM_TRANS_INTERRUMPIDA");

    @EJB
    private EjbSysmanUtilRemote ejbParametro;

    /**
     * Creates a new instance of ListainventarioControlador
     */
    public ListainventarioControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.LISTAINVENTARIO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaCmbElementoDesde();
        cargarListaCmbElementoHasta();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaCmbElementoDesde() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListainventarioControladorUrlEnum.URL2977
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());
    }

    public void cargarListaCmbElementoHasta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListainventarioControladorUrlEnum.URL3765
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(ListainventarioControladorEnum.PARAM0.getValue(),
                        elementoDesde);

        listaCmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());

    }

    public void generarReportes(FORMATOS formato) {
        String parametro = "";
        if (opciones == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1915"));
            return;
        }
        try {
            parametro = ejbParametro.consultarParametro(compania, "MANEJA CUBS",
                            SessionUtil.getModulo(), new Date(), false);
            if (parametro == null) {
                parametro = "NO";
            }
        }
        catch (SystemException ex) {
            Logger.getLogger(ListainventarioControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        if (elementoDesde != null && elementoHasta != null) {
            if ("SI".equals(parametro)) {
                generarReporteInventario(formato, "cubs");
            }
            else if ("2".equals(opciones)) {
                generarReporteInventario(formato, "");
            }
            else {
                generarReporteInventario(formato, "codigo");
            }
        }

    }

    public void oprimircmdPantalla() {
        generarReportes(ReportesBean.FORMATOS.PDF);
    }

    public void oprimircmbExcel() {
        generarReportes(ReportesBean.FORMATOS.EXCEL);
    }

    public void seleccionarFilaCmbElementoDesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoDesde = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGOELEMENTO
                                                        .getName()),
                                        "")
                        .toString();

        elementolabelDesde = SysmanFunciones.nvl(registroAux.getCampos()
                        .get("NOMBRELARGO"), "").toString();

        cargarListaCmbElementoHasta();

    }

    public void seleccionarFilaCmbElementoHasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoHasta = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGOELEMENTO
                                                        .getName()),
                                        "")
                        .toString();

        elementolabelHasta = SysmanFunciones.nvl(registroAux.getCampos()
                        .get("NOMBRELARGO"), "").toString();
    }

    private void generarReporteInventario(FORMATOS formatos, String conCubs) {
        archivoDescarga = null;
        String informe;
        switch (conCubs) {
        case "cubs":
            informe = "000314IInventarioCodCUBS";
            break;
        case "codigo":
            informe = "000313IInventarioCod";
            break;
        default:
            informe = "000312IInventario";
            break;
        }
        try {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            String strSQL;
            reemplazar.put("elementoInicial", elementoDesde);
            reemplazar.put("elementoFinal", elementoHasta);
            reemplazar.put("compania", compania);

            if ("cubs".equals(conCubs)) {
                reemplazar.put("ordenar",
                                "1".equals(opciones) ? "CODIGOELEMENTO"
                                    : "SUBSTR(CODIGOELEMENTO,0,3),NOMBRELARGO");
            }
            strSQL = Reporteador.resuelveConsulta(informe,
                            Integer.parseInt(modulo), reemplazar);
            parametros.put("PR_STRSQL", strSQL);
            archivoDescarga = JsfUtil.exportarStreamed(informe,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(msgTransInterrumpida + e.getMessage());
        }
    }

    public String getOpciones() {
        return opciones;
    }

    public void setOpciones(String opciones) {
        this.opciones = opciones;
    }

    public String getElementoDesde() {
        return elementoDesde;
    }

    public void setElementoDesde(String elementoDesde) {
        this.elementoDesde = elementoDesde;
    }

    public String getElementoHasta() {
        return elementoHasta;
    }

    public void setElementoHasta(String elementoHasta) {
        this.elementoHasta = elementoHasta;
    }

    public String getElementolabelDesde() {
        return elementolabelDesde;
    }

    public void setElementolabelDesde(String elementolabelDesde) {
        this.elementolabelDesde = elementolabelDesde;
    }

    public String getElementolabelHasta() {
        return elementolabelHasta;
    }

    public void setElementolabelHasta(String elementolabelHasta) {
        this.elementolabelHasta = elementolabelHasta;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaCmbElementoDesde() {
        return listaCmbElementoDesde;
    }

    public void setListaCmbElementoDesde(
        RegistroDataModelImpl listaCmbElementoDesde) {
        this.listaCmbElementoDesde = listaCmbElementoDesde;
    }

    public RegistroDataModelImpl getListaCmbElementoHasta() {
        return listaCmbElementoHasta;
    }

    public void setListacmbElementoHasta(
        RegistroDataModelImpl listaCmbElementoHasta) {
        this.listaCmbElementoHasta = listaCmbElementoHasta;
    }

}
