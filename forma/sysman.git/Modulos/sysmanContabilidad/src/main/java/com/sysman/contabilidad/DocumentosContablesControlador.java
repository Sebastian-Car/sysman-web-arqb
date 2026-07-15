package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.DocumentosContablesControladorEnum;
import com.sysman.contabilidad.enums.DocumentosContablesControladorUrlEnum;
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
import java.text.SimpleDateFormat;
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
 * @author otorres
 * @version 1, 19/04/2016
 * 
 * @author eamaya
 * @version 2, 10/04/2017 Proceso de Refactoring y Correciones
 * SonarLint
 */
@ManagedBean
@ViewScoped

public class DocumentosContablesControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    private String anoInicial;
    private String anoFinal;
    private String tipoInicial;
    private String tipoFinal;
    private String comprobanteInicial;
    private String comprobanteFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private boolean edadComprobante;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaTipoInicial;
    private RegistroDataModelImpl listaTipoFinal;
    private RegistroDataModelImpl listaComprobanteInicial;
    private RegistroDataModelImpl listaComprobanteFinal;
    private String controlDocumental;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    /**
     * Creates a new instance of DocumentosContablesControlador
     */
    public DocumentosContablesControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.DOCUMENTOS_CONTABLES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(DocumentosContablesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        fechaInicial = new Date();
        fechaFinal = fechaInicial;
        cargarListaTipoInicial();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaTipoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DocumentosContablesControladorUrlEnum.URL3036
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaTipoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DocumentosContablesControladorUrlEnum.URL3856
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(DocumentosContablesControladorEnum.PARAM2.getValue(),
                        tipoInicial);

        listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }
    
    /**
     * 
     * Carga la lista listaComprobanteInicial
     *
     */
    public void cargarListaComprobanteInicial() {

        UrlBean urlBean;
        SimpleDateFormat getAnio = new SimpleDateFormat("yyyy");

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ANOINICIAL", getAnio.format(fechaInicial));
        param.put("ANOFINAL", getAnio.format(fechaFinal));
        param.put("TIPOINICIAL", tipoInicial);
        param.put("TIPOFINAL", tipoFinal);

        urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                            		DocumentosContablesControladorUrlEnum.URL15070
                                                            .getValue());
        

        listaComprobanteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());

    }

    /**
     * 
     * Carga la lista listaComprobanteFinal
     *
     */
    public void cargarListaComprobanteFinal() {

        UrlBean urlBean;
        SimpleDateFormat getAnio = new SimpleDateFormat("yyyy");

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ANOINICIAL", getAnio.format(fechaInicial));
        param.put("ANOFINAL", getAnio.format(fechaFinal));
        param.put("TIPOINICIAL", tipoInicial);
        param.put("TIPOFINAL", tipoFinal);
        param.put("NUMEROINICIAL", comprobanteInicial);

        urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                            		DocumentosContablesControladorUrlEnum.URL15072
                                                            .getValue());
        

        listaComprobanteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());
    }
    
    public void cambiarFechaInicial() {
        comprobanteInicial = null;
        comprobanteFinal = null;
        cargarListaComprobanteInicial();
    }

    public void cambiarFechaFinal() {        
    	comprobanteInicial = null;
        comprobanteFinal = null;
    	cargarListaComprobanteInicial();
    }

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void generaInforme(ReportesBean.FORMATOS formato) {

        try {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put("tipoInicial", tipoInicial);
            reemplazar.put("tipoFinal", tipoFinal);
            reemplazar.put("comprobanteInicial", comprobanteInicial);
            reemplazar.put("comprobanteFinal", comprobanteFinal);
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            parametros.put("PR_DESCRIPCION_FECHAS",
                            "LISTADO DE DOCUMENTOS ENTRE FECHAS "
                                + SysmanFunciones.convertirAFechaCadena(
                                                fechaInicial)
                                + " Y "
                                + SysmanFunciones.convertirAFechaCadena(
                                                fechaFinal));

            cargarParamtero();

            String reporte = isEdadComprobante()
                && controlDocumental.equals("SI")
                    ? "002018LisCompContable"
                    : "000638I2LisCompContable";

            parametros.put("PR_EDAD_INFORME", edadComprobante);
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void cargarParamtero() {

        try {
            controlDocumental = ejbSysmanUtilRemote.consultarParametro(compania,
                            "PERMITE CONTROL DOCUMENTAL", modulo, new Date(),
                            true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void seleccionarFilaTipoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), " ")
                        .toString();
        tipoFinal = null;
        comprobanteInicial = null;
        comprobanteFinal = null;
        cargarListaTipoFinal();
        cargarListaComprobanteInicial();
    }

    public void seleccionarFilaTipoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), " ")
                        .toString();
        comprobanteInicial = null;
        comprobanteFinal = null;
        cargarListaComprobanteInicial();
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaComprobanteInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaComprobanteInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        comprobanteInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NUMERO"), "")
                        .toString();
        cargarListaComprobanteFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaComprobanteFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaComprobanteFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        comprobanteFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NUMERO"), "")
                        .toString();
    }

    public String getTipoInicial() {
        return tipoInicial;
    }

    public void setTipoInicial(String tipoInicial) {
        this.tipoInicial = tipoInicial;
    }

    public String getTipoFinal() {
        return tipoFinal;
    }

    public void setTipoFinal(String tipoFinal) {
        this.tipoFinal = tipoFinal;
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

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaTipoInicial() {
        return listaTipoInicial;
    }

    public void setListaTipoInicial(RegistroDataModelImpl listaTipoInicial) {
        this.listaTipoInicial = listaTipoInicial;
    }

    public RegistroDataModelImpl getListaTipoFinal() {
        return listaTipoFinal;
    }

    public boolean isEdadComprobante() {
        return edadComprobante;
    }

    public void setEdadComprobante(boolean edadComprobante) {
        this.edadComprobante = edadComprobante;
    }

    public void setListaTipoFinal(RegistroDataModelImpl listaTipoFinal) {
        this.listaTipoFinal = listaTipoFinal;
    }

	/**
	 * @return the comprobanteInicial
	 */
	public String getComprobanteInicial() {
		return comprobanteInicial;
	}

	/**
	 * @param comprobanteInicial the comprobanteInicial to set
	 */
	public void setComprobanteInicial(String comprobanteInicial) {
		this.comprobanteInicial = comprobanteInicial;
	}

	/**
	 * @return the comprobanteFinal
	 */
	public String getComprobanteFinal() {
		return comprobanteFinal;
	}

	/**
	 * @param comprobanteFinal the comprobanteFinal to set
	 */
	public void setComprobanteFinal(String comprobanteFinal) {
		this.comprobanteFinal = comprobanteFinal;
	}

	/**
	 * @return the listaComprobanteInicial
	 */
	public RegistroDataModelImpl getListaComprobanteInicial() {
		return listaComprobanteInicial;
	}

	/**
	 * @param listaComprobanteInicial the listaComprobanteInicial to set
	 */
	public void setListaComprobanteInicial(RegistroDataModelImpl listaComprobanteInicial) {
		this.listaComprobanteInicial = listaComprobanteInicial;
	}

	/**
	 * @return the listaComprobanteFinal
	 */
	public RegistroDataModelImpl getListaComprobanteFinal() {
		return listaComprobanteFinal;
	}

	/**
	 * @param listaComprobanteFinal the listaComprobanteFinal to set
	 */
	public void setListaComprobanteFinal(RegistroDataModelImpl listaComprobanteFinal) {
		this.listaComprobanteFinal = listaComprobanteFinal;
	}

	/**
	 * @return the anoInicial
	 */
	public String getAnoInicial() {
		return anoInicial;
	}

	/**
	 * @param anoInicial the anoInicial to set
	 */
	public void setAnoInicial(String anoInicial) {
		this.anoInicial = anoInicial;
	}

	/**
	 * @return the anoFinal
	 */
	public String getAnoFinal() {
		return anoFinal;
	}

	/**
	 * @param anoFinal the anoFinal to set
	 */
	public void setAnoFinal(String anoFinal) {
		this.anoFinal = anoFinal;
	}

}
