package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.contratos.enums.ParametroAdicionesControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.context.RequestContext;

/**
 *
 * @author dsuesca
 * @version 1, 05/01/2016
 *
 * @author spina
 * @version 2, 06/09/2017 - se refactoriza para dss, depuracion y ejbs
 */
@ManagedBean
@ViewScoped
public class ParametroAdicionesControlador extends BeanBaseModal
{

    private final String compania;
    private String tipoContrato;
    private String tipoAfectado;
    private String nombreContrato;
    private String anio;
    private List<Registro> listaTipoContrato;
    private List<Registro> listaTipoafectado;
    private List<Registro> listaanio;
    
    private Map<String,Object> parametroswf;
    /**
     * Creates a new instance of ParametroAdicionesControlador
     */
    public ParametroAdicionesControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.PARAMETRO_ADICIONES_CONTROLADOR
                            .getCodigo();
            parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
            if(parametroswf != null) {
            	SessionUtil.setSessionVar("modulo", "9");
            }
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(ParametroAdicionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        } finally {
        	try {
				SessionUtil.removeSessionVarContainer("parametroswf");
			} catch(NamingException e) {
				e.printStackTrace();
			}
        }
    }

    @PostConstruct
    public void inicializar()
    {
        cargarListaTipoContrato();
        cargarListaTipoafectado();
        cargarListaanio();
        abrirFormulario();

    }

    public void cargarListaTipoContrato()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), "M");
        try
        {
            listaTipoContrato = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ParametroAdicionesControladorUrlEnum.URL4820
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaTipoafectado()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaTipoafectado = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ParametroAdicionesControladorUrlEnum.URL4821
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaanio()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaanio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ParametroAdicionesControladorUrlEnum.URL4822
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirCancelar()
    {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirAceptar()
    {
        // <CODIGO_DESARROLLADO>
        nombreContrato = service.buscarEnLista(tipoAfectado,
                        GeneralParameterEnum.CODIGO.getName(),
                        GeneralParameterEnum.NOMBRE.getName(),
                        listaTipoafectado);

        Direccionador direccionar = new Direccionador();
        direccionar.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.ADICIONESPCONTRATOS_CONTROLADOR
                                        .getCodigo()));
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("anio", anio);
        parametros.put("tipoContrato", tipoContrato);
        parametros.put("tipoAfectado", tipoAfectado);
        parametros.put("nombreContrato", nombreContrato);
        parametros.put("vigencia", anio);
        direccionar.setParametros(parametros);

        if(parametroswf != null) {
        	try {
        		parametros.put("parametroswf",parametroswf);
				SessionUtil.setSessionVarContainer("parametros",parametros);
			} catch(NamingException e) {
				e.printStackTrace();
			}
        	JsfUtil.ejecutarJavaScript("window.location.href='/sysmanContratos/adicionespcontrato.sysman';");
        } else {
        	RequestContext.getCurrentInstance().closeDialog(direccionar);
        }
        // </CODIGO_DESARROLLADO>
    }

    public String getTipoContrato()
    {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato)
    {
        this.tipoContrato = tipoContrato;
    }

    public String getTipoAfectado()
    {
        return tipoAfectado;
    }

    public void setTipoAfectado(String tipoAfectado)
    {
        this.tipoAfectado = tipoAfectado;
    }

    public String getAnio()
    {
        return anio;
    }

    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    public List<Registro> getListaTipoContrato()
    {
        return listaTipoContrato;
    }

    public void setListaTipoContrato(List<Registro> listaTipoContrato)
    {
        this.listaTipoContrato = listaTipoContrato;
    }

    public List<Registro> getListaTipoafectado()
    {
        return listaTipoafectado;
    }

    public void setListaTipoafectado(List<Registro> listaTipoafectado)
    {
        this.listaTipoafectado = listaTipoafectado;
    }

    public List<Registro> getListaanio()
    {
        return listaanio;
    }

    public void setListaanio(List<Registro> listaanio)
    {
        this.listaanio = listaanio;
    }

    public String getNombreContrato()
    {
        return nombreContrato;
    }

    public void setNombreContrato(String nombreContrato)
    {
        this.nombreContrato = nombreContrato;
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

}
