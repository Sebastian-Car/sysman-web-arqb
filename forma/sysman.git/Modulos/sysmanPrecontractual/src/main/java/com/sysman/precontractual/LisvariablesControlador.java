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
import com.sysman.precontractual.enums.LisvariablesControladorEnum;
import com.sysman.precontractual.enums.LisvariablesControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
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
 * @author ybecerra
 * @version 1, 14/12/2015
 * 
 * @author jcrodriguez, Refactoring y Depuracion
 * @version 2, 31/08/2017
 */
@ManagedBean
@ViewScoped

public class LisvariablesControlador extends BeanBaseModal
{
    private final String compania;
    private final String modulo;
    private String tipoContrato;
    private String tipoContratoNombre;
    private String variableInicial;
    private String variableInicialNombre;
    private String variableFinal;
    private String variableFinalNombre;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaTipoContrato;
    private RegistroDataModelImpl listaVariableIni;
    private RegistroDataModelImpl listaVariableFin;

    /**
     * Creates a new instance of LisvariablesControlador
     */
    public LisvariablesControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.LISVARIABLES_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(LisvariablesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar()
    {
        cargarListaTipoContrato();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // heredado del bean base
    }

    public void cargarListaTipoContrato()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LisvariablesControladorUrlEnum.URL2742.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoContrato = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, LisvariablesControladorEnum.TIPOCONTRATO.getValue());
    }

    public void cargarListaVariableIni()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LisvariablesControladorUrlEnum.URL3300.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LisvariablesControladorEnum.TIPOCONTRATO.getValue(), tipoContrato);
        listaVariableIni = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaVariableFin()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LisvariablesControladorUrlEnum.URL4216.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LisvariablesControladorEnum.TIPOCONTRATO.getValue(), tipoContrato);
        param.put(LisvariablesControladorEnum.VARIABLE_INICIAL.getValue(), variableInicial);
        listaVariableFin = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    private String validarParametroCadena(Map<String, Object> campos, String var)
    {
        return SysmanFunciones.validarCampoVacio(campos, var) ? "" : campos.get(var).toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoContrato
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoContrato(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        tipoContrato = validarParametroCadena(registroAux.getCampos(), LisvariablesControladorEnum.TIPOCONTRATO.getValue());
        tipoContratoNombre = validarParametroCadena(registroAux.getCampos(), GeneralParameterEnum.DESCRIPCION.getName());
        variableInicial = null;
        cargarListaVariableIni();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaVariableIni
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaVariableIni(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        variableInicial = validarParametroCadena(registroAux.getCampos(), GeneralParameterEnum.CODIGO.getName());
        variableInicialNombre = validarParametroCadena(registroAux.getCampos(), GeneralParameterEnum.DESCRIPCION.getName());
        variableFinal = null;
        cargarListaVariableFin();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaVariableFin
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaVariableFin(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        variableFinal = validarParametroCadena(registroAux.getCampos(), GeneralParameterEnum.CODIGO.getName());
        variableFinalNombre = validarParametroCadena(registroAux.getCampos(), GeneralParameterEnum.DESCRIPCION.getName());
    }

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

    public void generarInforme(ReportesBean.FORMATOS formato)
    {
        String parReporte = "000432LisVariables";
        try
        {

            HashMap<String, Object> remplazar = new HashMap<>();

            remplazar.put("tipoContrato", "'" + tipoContrato + "'");
            remplazar.put("variableInicial", "'" + variableInicial + "'");
            remplazar.put("variableFinal", "'" + variableFinal + "'");

            String strsql = Reporteador.resuelveConsulta(parReporte,
                            Integer.parseInt(modulo), remplazar);

            Map<String, Object> parametros = new HashMap<>();

            parametros.put("PR_STRSQL", strsql);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            "Nit." + SessionUtil.getCompaniaIngreso().getNit());

            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString("MSM_INFORME_NO_EXISTE"), " ", ex.getMessage(), " ", parReporte));
            Logger.getLogger(LisvariablesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

        catch (JRException | IOException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(LisvariablesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }
        catch (SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public int getNumFormulario()
    {
        return numFormulario;
    }

    public String getTipoContrato()
    {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato)
    {
        this.tipoContrato = tipoContrato;
    }

    public String getVariableInicial()
    {
        return variableInicial;
    }

    public void setVariableInicial(String variableInicial)
    {
        this.variableInicial = variableInicial;
    }

    public String getVariableFinal()
    {
        return variableFinal;
    }

    public void setVariableFinal(String variableFinal)
    {
        this.variableFinal = variableFinal;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaTipoContrato()
    {
        return listaTipoContrato;
    }

    public void setListaTipoContrato(RegistroDataModelImpl listaTipoContrato)
    {
        this.listaTipoContrato = listaTipoContrato;
    }

    public RegistroDataModelImpl getListaVariableIni()
    {
        return listaVariableIni;
    }

    public void setListaVariableIni(RegistroDataModelImpl listaVariableIni)
    {
        this.listaVariableIni = listaVariableIni;
    }

    public RegistroDataModelImpl getListaVariableFin()
    {
        return listaVariableFin;
    }

    public void setListaVariableFin(RegistroDataModelImpl listaVariableFin)
    {
        this.listaVariableFin = listaVariableFin;
    }

    public String getTipoContratoNombre()
    {
        return tipoContratoNombre;
    }

    public void setTipoContratoNombre(String tipoContratoNombre)
    {
        this.tipoContratoNombre = tipoContratoNombre;
    }

    public String getVariableInicialNombre()
    {
        return variableInicialNombre;
    }

    public void setVariableInicialNombre(String variableInicialNombre)
    {
        this.variableInicialNombre = variableInicialNombre;
    }

    public String getVariableFinalNombre()
    {
        return variableFinalNombre;
    }

    public void setVariableFinalNombre(String variableFinalNombre)
    {
        this.variableFinalNombre = variableFinalNombre;
    }

}
