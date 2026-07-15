package com.sysman.almacen;

import com.sysman.almacen.enums.InventarioPorGrupoControladorEnum;
import com.sysman.almacen.enums.InventarioPorGrupoControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author OTORRES
 * @version 1, 28/01/2016
 * 
 * @author eamaya
 * @version 2, 02/05/2017 Proceso de Refactoring y Manejo de EJBs
 * 
 */
@ManagedBean
@ViewScoped

public class InventarioPorGrupoControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private final String codigoElemento;
    private String elementoDesde;
    private String elementoHasta;
    private String nombreElementoDesde;
    private String nombreElementoHasta;
    private Date fecha;
    private RegistroDataModelImpl listacmbElementoDesde;
    private RegistroDataModelImpl listacmbElementoHasta;
    private String parametroDigitos;
    private StreamedContent archivoDescarga;

    @EJB
    private EjbSysmanUtilRemote ejbParametro;

    /**
     * Creates a new instance of InventarioPorGrupoControlador
     */
    public InventarioPorGrupoControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.INVENTARIO_POR_GRUPO_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        codigoElemento = "CODIGOELEMENTO";
        try {
            validarPermisos();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        abrirFormulario();
        cargarListacmbElementoDesde();
    }

    public void cargarListacmbElementoDesde() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InventarioPorGrupoControladorUrlEnum.URL3103
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(InventarioPorGrupoControladorEnum.PARAM1.getValue(),
                        parametroDigitos);

        listacmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoElemento);
    }

    public void cargarListacmbElementoHasta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InventarioPorGrupoControladorUrlEnum.URL3948
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(InventarioPorGrupoControladorEnum.PARAM3.getValue(),
                        elementoDesde);
        param.put(InventarioPorGrupoControladorEnum.PARAM1.getValue(),
                        parametroDigitos);

        listacmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoElemento);
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void generaInforme(ReportesBean.FORMATOS formato) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            String strSQL;
            reemplazar.put("fecha", SysmanFunciones.formatearFecha(fecha));
            reemplazar.put("elementoDesde", elementoDesde);
            reemplazar.put("elementoHasta", elementoHasta);
            reemplazar.put("parametroDigitos", parametroDigitos);
            reemplazar.put("compania", compania);
            strSQL = Reporteador.resuelveConsulta("000485InventarioPorGrupo",
                            Integer.parseInt(modulo), reemplazar);
            parametros.put("PR_STRSQL", strSQL);
            parametros.put("PR_FECHA_CORTE", SysmanFunciones
                            .convertirAFechaCadena(fecha, "dd/MM/yyyy"));
            archivoDescarga = JsfUtil.exportarStreamed(
                            "000485InventarioPorGrupo", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void seleccionarFilacmbElementoDesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoDesde = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoElemento), "")
                        .toString();

        nombreElementoDesde = SysmanFunciones.nvl(registroAux.getCampos()
                        .get("NOMBRELARGO"), "").toString();

        elementoHasta = null;
        nombreElementoHasta = null;
        cargarListacmbElementoHasta();
    }

    public void seleccionarFilacmbElementoHasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoHasta = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoElemento), "")
                        .toString();

        nombreElementoHasta = SysmanFunciones.nvl(registroAux.getCampos()
                        .get("NOMBRELARGO"), "").toString();
    }

    @Override
    public void abrirFormulario() {
        try {
            parametroDigitos = ejbParametro.consultarParametro(compania,
                            "DIGITOS AGRUPACION INVENTARIO", modulo, new Date(),
                            false);

            fecha = new Date();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
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

    public String getNombreElementoDesde() {
        return nombreElementoDesde;
    }

    public void setNombreElementoDesde(String nombreElementoDesde) {
        this.nombreElementoDesde = nombreElementoDesde;
    }

    public String getNombreElementoHasta() {
        return nombreElementoHasta;
    }

    public void setNombreElementoHasta(String nombreElementoHasta) {
        this.nombreElementoHasta = nombreElementoHasta;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
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

    /**
     * @return the archivoDescarga
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * @param archivoDescarga
     * the archivoDescarga to set
     */
    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

}
