package com.sysman.planeacion;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.planeacion.enums.InventarioprecioControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
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

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 17/12/2015
 * 
 * @version 2, 07/09/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 */

@ManagedBean
@ViewScoped
public class InventarioprecioControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private final String consCodigo;
    private String cmbElementoDesde;
    private String cmbElementoHasta;
    private String cmbAno;
    private String cpElementoDesde;
    private String cpElementoHasta;
    private String cpAgrupacion;
    private String opcion;
    private String orderBy;
    private boolean especial;
    private List<Registro> listacmbANO;
    private RegistroDataModelImpl listacmbElementoDesde;
    private RegistroDataModelImpl listacmbElementoHasta;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of InventarioprecioControlador
     */
    public InventarioprecioControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.INVENTARIOPRECIO_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        consCodigo = "CODIGOELEMENTO";
        try {
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(InventarioprecioControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        opcion = "2";
        cargarListacmbANO();
        cargarListacmbElementoDesde();
        abrirFormulario();
    }

    public void cargarListacmbANO() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listacmbANO = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InventarioprecioControladorUrlEnum.URL2634
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListacmbElementoDesde() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InventarioprecioControladorUrlEnum.URL2991
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        consCodigo);
    }

    public void cargarListacmbElementoHasta() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InventarioprecioControladorUrlEnum.URL3592
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ELEMENTO.getName(), cmbElementoDesde);

        listacmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        consCodigo);
    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga=null;
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga= null;
        generarReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void generarReporte(FORMATOS formatos) {
        if ("1".equals(opcion)) {
            orderBy = SysmanFunciones.concatenar("ORDER BY  \n",
                            "    INVENTARIO.CODIGOELEMENTO ");
        }
        else {
            orderBy = SysmanFunciones.concatenar("ORDER BY \n",
                            "    INVENTARIO.NOMBRELARGO ");
        }
        String reporte;
        if (!especial) {
            reporte = "000440InventarioPrecio";
        }
        else {
            reporte = "000441InventarioPrecioEspecial";
        }
        try {
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("cmbAno", cmbAno);
            reemplazar.put("cmbElementoDesde", cmbElementoDesde);
            reemplazar.put("cmbElementoHasta", cmbElementoHasta);
            reemplazar.put("orderBy", orderBy);
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar);

            parametros.put("PR_STRSQL", strSql);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);

        }
        catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeInformativo(e.getMessage());
        }
        catch (SysmanException | JRException | IOException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA)
                                + ex.getMessage());
        }

    }

    public void cambiarcmbANO() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilacmbElementoDesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cmbElementoDesde = registroAux.getCampos().get(consCodigo).toString();
        cpElementoDesde = registroAux.getCampos().get("NOMBRELARGO").toString();
        cargarListacmbElementoHasta();
    }

    public void seleccionarFilacmbElementoHasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cmbElementoHasta = registroAux.getCampos().get(consCodigo).toString();
        cpElementoHasta = registroAux.getCampos().get("NOMBRELARGO").toString();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isEspecial() {
        return especial;
    }

    public void setEspecial(boolean especial) {
        this.especial = especial;
    }

    public String getCmbElementoDesde() {
        return cmbElementoDesde;
    }

    public void setCmbElementoDesde(String cmbElementoDesde) {
        this.cmbElementoDesde = cmbElementoDesde;
    }

    public String getCmbElementoHasta() {
        return cmbElementoHasta;
    }

    public void setCmbElementoHasta(String cmbElementoHasta) {
        this.cmbElementoHasta = cmbElementoHasta;
    }

    public String getCmbAno() {
        return cmbAno;
    }

    public void setCmbAno(String cmbAno) {
        this.cmbAno = cmbAno;
    }

    public String getCpElementoDesde() {
        return cpElementoDesde;
    }

    public void setCpElementoDesde(String cpElementoDesde) {
        this.cpElementoDesde = cpElementoDesde;
    }

    public String getCpElementoHasta() {
        return cpElementoHasta;
    }

    public void setCpElementoHasta(String cpElementoHasta) {
        this.cpElementoHasta = cpElementoHasta;
    }

    public String getCpAgrupacion() {
        return cpAgrupacion;
    }

    public void setCpAgrupacion(String cpAgrupacion) {
        this.cpAgrupacion = cpAgrupacion;
    }

    public List<Registro> getListacmbANO() {
        return listacmbANO;
    }

    public void setListacmbANO(List<Registro> listacmbANO) {
        this.listacmbANO = listacmbANO;
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
}
