package com.sysman.almacen;

import com.sysman.almacen.enums.IDosfinventariobodegaControladorUrlEnum;
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
import com.sysman.services.FormContinuoService;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
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
 * @author NGOMEZ
 * @version 1, 15/04/2016
 * @modifier amonroy
 * @version 2, 28/04/2017 Proceso de Refactoring
 */
@ManagedBean
@ViewScoped
public class IDosfinventariobodegaControlador extends BeanBaseModal {

    private final String codigoElemento;

    private String compania;
    private String conSaldoCero;
    private String ordenadoPor;
    private String presentadoPor;
    private String elementoDesde;
    private String elementoHasta;
    private String nombreDesde;
    private String nombreHasta;
    private String fechaInicial;
    private String fechaFinal;
    private String grupo;
    private String companiaSel;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listacmbElementoDesde;
    private RegistroDataModelImpl listacmbElementoHasta;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of I2finventariobodegaControlador
     */
    public IDosfinventariobodegaControlador() {

        super();
        codigoElemento = "CODIGOELEMENTO";
        try {
            numFormulario = GeneralCodigoFormaEnum.I_DOSFINVENTARIOBODEGA_CONTROLADOR.getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                fechaInicial = String
                                .valueOf(parametrosEntrada.get("fechaInicial"));
                fechaFinal = String
                                .valueOf(parametrosEntrada.get("fechaFinal"));
                companiaSel = String
                                .valueOf(parametrosEntrada.get("companiaSel"));
                grupo = String.valueOf(parametrosEntrada.get("grupo"));
            }
            compania = companiaSel;
        }
        catch (Exception ex) {
            Logger.getLogger(IDosfinventariobodegaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListacmbElementoDesde();
        cargarListacmbElementoHasta();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        conSaldoCero = "2";
        ordenadoPor = "1";
        presentadoPor = "1";
    }

    @Override
    public FormContinuoService getService() {
        return service;
    }

    @Override
    public void setService(FormContinuoService service) {
        this.service = service;
    }

    public void cargarListacmbElementoDesde() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        IDosfinventariobodegaControladorUrlEnum.URL3733
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoElemento);
    }

    public void cargarListacmbElementoHasta() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        IDosfinventariobodegaControladorUrlEnum.URL4474
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(), elementoDesde);

        listacmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoElemento);
    }

    public void oprimirPresentar() {
        genInforme(ReportesBean.FORMATOS.PDF);
    }

    public void oprimirExcel() {
        genInforme(ReportesBean.FORMATOS.EXCEL97);
    }

    public void seleccionarFilacmbElementoDesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoDesde = String
                        .valueOf(registroAux.getCampos().get(codigoElemento));
        nombreDesde = String
                        .valueOf(registroAux.getCampos().get("NOMBRELARGO"));
        elementoHasta = null;
        nombreHasta = null;
        cargarListacmbElementoHasta();
    }

    public void seleccionarFilacmbElementoHasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoHasta = String
                        .valueOf(registroAux.getCampos().get(codigoElemento));
        nombreHasta = String
                        .valueOf(registroAux.getCampos().get("NOMBRELARGO"));
    }

    public void genInforme(ReportesBean.FORMATOS formato) {
        String reporte = "000636I2INVENTARIOBODEGA";
        archivoDescarga = null;

        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            reemplazar.put("elementoDesde", elementoDesde);
            reemplazar.put("elementoHasta", elementoHasta);
            reemplazar.put("fechaInicial", fechaInicial);
            reemplazar.put("fechaFinal", fechaFinal);
            reemplazar.put("grupo", grupo);
            reemplazar.put("companiaSel", companiaSel);

            boolean aux = "1".equals(presentadoPor);
            String manejaPeps = ejbSysmanUtil.consultarParametro(compania,
                            "MANEJA PEPS EN CONSUMO ALMACEN",
                            SessionUtil.getModulo(), new Date(), true);
            parametros.put("AGRUPAMIENTO",
                            aux);
            if ("SI".equals(SysmanFunciones.nvl(manejaPeps, "NO"))) {
                reemplazar.put("conSaldoCero",
                                "2".equals(conSaldoCero)
                                    ? "AND NVL(I2_INVENTARIOBODEGA_PEPS_C.SALDO_PEPS,0)>0"
                                    : "");
                reemplazar.put("orden2",
                                "1".equals(ordenadoPor)
                                    ? "I2_INVENTARIOBODEGA_PEPS_C.ELEMENTO"
                                    : "INVENTARIO1.NOMBRELARGO");
                reemplazar.put("orden3",
                                "1".equals(ordenadoPor)
                                    ? "INVENTARIO1.NOMBRELARGO"
                                    : "I2_INVENTARIOBODEGA_PEPS_C.ELEMENTO");
                Reporteador.resuelveConsulta("000636I2INVENTARIOBODEGA1",
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar, parametros);
            }
            else {
                reemplazar.put("conSaldoCero",
                                "2".equals(conSaldoCero)
                                    ? "AND INVENTARIO.EXISTENCIA>0 AND INVENTARIO.VLRUNITARIOPROM>0"
                                    : "");
                reemplazar.put("orden2", "1".equals(ordenadoPor)
                    ? "INVENTARIO.CODIGOELEMENTO" : "INVENTARIO.NOMBRELARGO");
                reemplazar.put("orden3", "1".equals(ordenadoPor)
                    ? "INVENTARIO.NOMBRELARGO" : "INVENTARIO.CODIGOELEMENTO");
                Reporteador.resuelveConsulta("000636I2INVENTARIOBODEGA",
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar, parametros);
            }

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INFORME_NO_EXISTE")+" "+ex.getMessage()+" "+reporte);
            Logger.getLogger(IDosfinventariobodegaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        } 
        catch ( JRException | IOException
                        | SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA") + " "
                                + ex.getMessage());
            Logger.getLogger(IDosfinventariobodegaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException e) {    
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
       

    }

    public void cargarModal() {
        ///heredado del bean base
    }

    public String getConSaldoCero() {
        return conSaldoCero;
    }

    public void setConSaldoCero(String conSaldoCero) {
        this.conSaldoCero = conSaldoCero;
    }

    public String getOrdenadoPor() {
        return ordenadoPor;
    }

    public void setOrdenadoPor(String ordenadoPor) {
        this.ordenadoPor = ordenadoPor;
    }

    public String getPresentadoPor() {
        return presentadoPor;
    }

    public void setPresentadoPor(String presentadoPor) {
        this.presentadoPor = presentadoPor;
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

    public String getNombreDesde() {
        return nombreDesde;
    }

    public void setNombreDesde(String nombreDesde) {
        this.nombreDesde = nombreDesde;
    }

    public String getNombreHasta() {
        return nombreHasta;
    }

    public void setNombreHasta(String nombreHasta) {
        this.nombreHasta = nombreHasta;
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

    public String getCompania() {
        return compania;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(String fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public String getCompaniaSel() {
        return companiaSel;
    }

    public void setCompaniaSel(String companiaSel) {
        this.companiaSel = companiaSel;
    }

    public String getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(String fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public void setCompania(String compania) {
        this.compania = compania;
    }

}
