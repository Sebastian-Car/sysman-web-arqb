package com.sysman.precontractual;

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
import com.sysman.precontractual.enums.LisprecontratosControladorEnum;
import com.sysman.precontractual.enums.LisprecontratosControladorUrlEnum;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 03/12/2015
 *
 * @version2, 02/06/2017 sdaza - incluir ruta para validar reporte
 * faltante
 * 
 * @author jcrodriguez, Refactoring y Depuracion
 * @version 3, 31/08/2017
 * 
 */
@ManagedBean
@ViewScoped

public class LisprecontratosControlador extends BeanBaseModal
{

    private final String compania;
    private final String modulo;
    private String modalidadInicial;
    private String modalidadFinal;
    private String modalidadInicialNombre;
    private String modalidadFinalNombre;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaModalidadInicial;
    private RegistroDataModelImpl listaModalidadFinal;

    /**
     * Creates a new instance of LisprecontratosControlador
     */
    public LisprecontratosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try
        {
            numFormulario = GeneralCodigoFormaEnum.LISPRECONTRATOS_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(LisprecontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        fechaInicial = new Date();
        fechaFinal = new Date();
        cargarListaModalidadInicial();
        abrirFormulario();
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga los items del combo Modalidad de Seleccion Inicial en la
     * lista
     */
    public void cargarListaModalidadInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LisprecontratosControladorUrlEnum.URL3383.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaModalidadInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, LisprecontratosControladorEnum.COD_T_CONTRATO.getValue());
    }

    /**
     * Carga los items del combo Modalidad de Selecci�n Final en la
     * lista
     */
    public void cargarListaModalidadFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LisprecontratosControladorUrlEnum.URL3985.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LisprecontratosControladorEnum.CONTRATO.getValue(), modalidadInicial);

        listaModalidadFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, LisprecontratosControladorEnum.COD_T_CONTRATO.getValue());

    }

    // </METODOS_CARGAR_LISTA>

    public void oprimirPresentar()
    {
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF);

    }

    public void oprimirExcel()
    {
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.EXCEL97);

    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaModalidadInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaModalidadInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        modalidadInicial = validarParametroCadena(registroAux.getCampos(), LisprecontratosControladorEnum.COD_T_CONTRATO.getValue());
        modalidadInicialNombre = validarParametroCadena(registroAux.getCampos(), GeneralParameterEnum.NOMBRE.getName());
        modalidadFinal = null;
        cargarListaModalidadFinal();
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaModalidadFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaModalidadFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        modalidadFinal = validarParametroCadena(registroAux.getCampos(), LisprecontratosControladorEnum.COD_T_CONTRATO.getValue());
        modalidadFinalNombre = validarParametroCadena(registroAux.getCampos(), GeneralParameterEnum.NOMBRE.getName());
    }

    private String validarParametroCadena(Map<String, Object> campos, String var)
    {
        return SysmanFunciones.validarCampoVacio(campos, var) ? "" : campos.get(var).toString();
    }

    public void generarInforme(ReportesBean.FORMATOS formato)
    {

        try
        {

            String parReporte = "000412GRALPRECONTRATOS";

            HashMap<String, Object> remplazar = new HashMap<>();

            remplazar.put("modalidadInicial", "'" + modalidadInicial + "'");
            remplazar.put("modalidadFinal", "'" + modalidadFinal + "'");
            remplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            remplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));

            String strsql = Reporteador.resuelveConsulta(parReporte,
                            Integer.parseInt(modulo), remplazar);

            Map<String, Object> parametros = new HashMap<>();

            parametros.put("PR_STRSQL", strsql);

            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (JRException | IOException | SysmanException ex)
        {

            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(idioma.getString("TB_TB2119"), ex.getMessage()));
            Logger.getLogger(LisprecontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void abrirFormulario()
    {
        // heredado del bean base
    }

    public String getModalidadInicial()
    {
        return modalidadInicial;
    }

    public void setModalidadInicial(String modalidadInicial)
    {
        this.modalidadInicial = modalidadInicial;
    }

    public String getModalidadFinal()
    {
        return modalidadFinal;
    }

    public void setModalidadFinal(String modalidadFinal)
    {
        this.modalidadFinal = modalidadFinal;
    }

    public Date getFechaInicial()
    {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial)
    {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal()
    {
        return fechaFinal;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public void setFechaFinal(Date fechaFinal)
    {
        this.fechaFinal = fechaFinal;
    }

    public RegistroDataModelImpl getListaModalidadInicial()
    {
        return listaModalidadInicial;
    }

    public void setListaModalidadInicial(
        RegistroDataModelImpl listaModalidadInicial)
    {
        this.listaModalidadInicial = listaModalidadInicial;
    }

    public RegistroDataModelImpl getListaModalidadFinal()
    {
        return listaModalidadFinal;
    }

    public void setListaModalidadFinal(RegistroDataModelImpl listaModalidadFinal)
    {
        this.listaModalidadFinal = listaModalidadFinal;
    }

    public String getModalidadInicialNombre()
    {
        return modalidadInicialNombre;
    }

    public void setModalidadInicialNombre(String modalidadInicialNombre)
    {
        this.modalidadInicialNombre = modalidadInicialNombre;
    }

    public String getModalidadFinalNombre()
    {
        return modalidadFinalNombre;
    }

    public void setModalidadFinalNombre(String modalidadFinalNombre)
    {
        this.modalidadFinalNombre = modalidadFinalNombre;
    }

}
