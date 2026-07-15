package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.general.enums.FrdetallecomprobantespresupuestalesControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 08/01/2016
 * @version 2, 05/04/2017 jcrodriguez
 * Se adicionaron los servicos para el formulario continuo
 * depuracion del controlador 
 * 
 * @author asana 
 * @version 5, 12/06/2017
 * Redireccion de formulario.
 * */
@ManagedBean
@ViewScoped
public class FrdetallecomprobantespresupuestalesControlador  extends BeanBaseContinuoAcmeImpl {
    /**
     * variable que alamcena la compañia
     */
    private final String compania;
    /**
     * variable que almacena el numero ppto
     */
    private String numeroPPTO;
    /**
     * variable que alamcena el tipo ppto
     */
    private String tipoPPTO;
    /**
     * variable que almacena la clase orden
     */
    private String claseOrden;
    /**
     * variable que alamcena el numero de orden
     */
    private String numeroOrden;
    /**
     * vairable que almacena el reporte en formato pdf
     */
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of
     * FrdetallecomprobantespresupuestalesControlador
     */
    public FrdetallecomprobantespresupuestalesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRDETALLECOMPROBANTESPRESUPUESTALES_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(
                            FrdetallecomprobantespresupuestalesControlador.class
                            .getName())
            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }
    /**
     * metodo que inicializa el formulario
     */
    @PostConstruct
    public void inicializar() {
        enumBase=GenericUrlEnum.COMPROBANTE_PPTAL;
        buscarLlave();
        tipoPPTO = JsfUtil.getParametros().get("tipoPPTO");
        numeroPPTO = JsfUtil.getParametros().get("numeroPPTO");
        numeroOrden = JsfUtil.getParametros().get("numeroOrden");
        claseOrden = JsfUtil.getParametros().get("claseOrden");

        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        abrirFormulario();
    }
    /**
     * metodo que asigna la informacion a la grilla del formulario
     */
    @Override
    public void reasignarOrigen() {
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(FrdetallecomprobantespresupuestalesControladorUrlEnum.URL2472.getValue()); 
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        parametrosListado.put("TIPOPPTO",tipoPPTO);
        parametrosListado.put("NUMEROPPTO",numeroPPTO);
    }
    /**
     * metodo que es llamado cuando se oprime el boton presentar
     */
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("numeroOrden", numeroOrden);
            reemplazar.put("claseOrden", claseOrden);
            // MANEJO DE PARAMETROS DE REEMPLAZO
            HashMap<String, Object> reemplazarSub = new HashMap<>();

            reemplazarSub.put("tipoPpto", tipoPPTO);
            reemplazarSub.put("numeroPpto", numeroPPTO);

            Map<String, Object> parametros = new HashMap<>();

            String reporte = "000460FRGDETALLE";
            String subreporte = "000461SUBFRG";
            // MANEJO DE PARAMETROS DEL REPORTE
            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);
            String strSqlSubreporte = Reporteador.resuelveConsulta(subreporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazarSub);
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_STRSQL_SUB_FRG", strSqlSubreporte);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
           
                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, ReportesBean.FORMATOS.PDF);
            }
            catch (JRException | IOException | SysmanException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
       
       
    }

    @Override
    public void abrirFormulario() {
        //heredado del bean base
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes() {       
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);        
        return true;
    }

    @Override
    public boolean insertarDespues() {
        //heredado del bean base
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        //heredado del bean base
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        //heredado del bean base
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        //heredado del bean base
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        //heredado del bean base
        return true;
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void removerCombos() {
        //heredado del bean base

    }

    @Override
    public void asignarValoresRegistro() {
        // Metodo heredado de la clase BeanBase
    }
    /**
     * metodos get y set
     * @return
     */
    public String getNumeroPPTO() {
        return numeroPPTO;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public void setNumeroPPTO(String numeroPPTO) {
        this.numeroPPTO = numeroPPTO;
    }

    public String getTipoPPTO() {
        return tipoPPTO;
    }

    public void setTipoPPTO(String tipoPPTO) {
        this.tipoPPTO = tipoPPTO;
    }

    public String getClaseOrden() {
        return claseOrden;
    }

    public void setClaseOrden(String claseOrden) {
        this.claseOrden = claseOrden;
    }

    public String getNumeroOrden() {
        return numeroOrden;
    }

    public void setNumeroOrden(String numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

}
