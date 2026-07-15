package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.contratos.enums.ParametroNovedadControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.context.RequestContext;

/**
 *
 * @author dsuesca
 * @version 1, 14/10/2015
 *
 * @version 2.0, 07/09/2017
 * @author jguerrero Se agrega refactoring a la clase, ajustando consultas y llamados a funciones.
 */

@ManagedBean
@ViewScoped
public class ParametroNovedadControlador extends BeanBaseModal
{

    private final String compania;

    private static final String CODIGO_NOVEDAD = "CODIGO";
    private static final String NOMBRE_NOVEDAD = "NOMBRE";

    private String tipoContrato;
    private String anio;
    private String titulo;
    private String claseNov;
    private List<Registro> listaTipoContrato;
    private List<Registro> listaTipoNovedad;
    private List<Registro> listaAno;
    private String modulo;

    /**
     * Constante definida para almacenar la cadena "NUMERO"
     */
    private final String codigoNovedad;

    private Map<String,Object> parametroswf;
    /**
     * Creates a new instance of ParametroNovedadControlador
     */
    @SuppressWarnings("unchecked")
	public ParametroNovedadControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        numFormulario = GeneralCodigoFormaEnum.PARAMETRO_NOVEDAD_CONTROLADOR
                        .getCodigo();
        codigoNovedad = CODIGO_NOVEDAD;
        try
        {
        	parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
        	if(parametroswf != null) {
        		SessionUtil.setSessionVar("modulo", "9");
        		 modulo = SessionUtil.getModulo();
        	}
            validarPermisos();
        }
        catch (NamingException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        } finally {
        }
    }

    @PostConstruct
    public void inicializar()
    {
        cargarListaTipoContrato();
        cargarListaAno();
        abrirFormulario();
        anio = String.valueOf(SysmanFunciones
                        .ano(new Date()));
    }

    public void cargarListaTipoContrato()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaTipoContrato = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(ParametroNovedadControladorUrlEnum.URL5748.getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (!listaTipoContrato.isEmpty())
        {
            tipoContrato = (String) listaTipoContrato.get(0).getCampos()
                            .get(GeneralParameterEnum.CODIGO.getName());
        }
    }

    public void cargarListaAno()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAno = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(ParametroNovedadControladorUrlEnum.URL5749.getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirComando2()
    {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirComando3()
    {
        // <CODIGO_DESARROLLADO>
        if (anio != null)
        {
            if (tipoContrato != null)
            {
                String strCaseNovedad;
                String strTipoContrato;
                String enviarTitulo = "";
                StringBuilder builder = new StringBuilder();

                try
                {
                    Map<String, Object> param = new TreeMap<>();
                    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

                    listaTipoNovedad = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(ParametroNovedadControladorUrlEnum.URL6565.getValue()).getUrl(),
                                    param));

                    strCaseNovedad = service.buscarEnLista(String.valueOf(claseNov), codigoNovedad,
                                    NOMBRE_NOVEDAD, listaTipoNovedad);

                    strTipoContrato = service.buscarEnLista(String.valueOf(tipoContrato),
                                    codigoNovedad, NOMBRE_NOVEDAD, listaTipoContrato);

                    builder.append(strCaseNovedad).append(" - ").append(strTipoContrato);

                    enviarTitulo = builder.toString();
                }
                catch (NullPointerException | SystemException ex)
                {
                    logger.error(ex.getMessage(), ex);
                    JsfUtil.agregarMensajeAlerta(idioma
                                    .getString("TB_TB2139")
                                    .replace("$#clasenov$#", claseNov));
                    return;
                }
               

                HashMap<String, Object> parametros = new HashMap<>();

                parametros.put("anio", anio);
                parametros.put("tipoContrato", tipoContrato);
                parametros.put("claseNov", claseNov);
                parametros.put("titulo", enviarTitulo);
               
                if(parametroswf != null) {

                	String[] campos = {	"anio", "tipoContrato", "claseNov", "titulo" };

                	Object[] valores = { anio, tipoContrato, claseNov, enviarTitulo };

                	SessionUtil.redireccionarFormularioModalFormulario(modulo, "282", campos, valores, true);
                } else {
                	Direccionador dir = new Direccionador();
                	dir.setNumForm("282");
                	dir.setParametros(parametros);
                	RequestContext.getCurrentInstance().closeDialog(dir);
                }
                // </CODIGO_DESARROLLADO>
            }
            else
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2138"));
            }
        }
        else
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB729"));
        }
    }

    public String getTipoContrato()
    {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato)
    {
        this.tipoContrato = tipoContrato;
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

    public List<Registro> getListaTipoNovedad()
    {
        return listaTipoNovedad;
    }

    public void setListaTipoNovedad(List<Registro> listaTipoNovedad)
    {
        this.listaTipoNovedad = listaTipoNovedad;
    }

    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }

    public String getTitulo()
    {
        return titulo;
    }

    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }

    public String getClaseNov()
    {
        return claseNov;
    }

    public void setClaseNov(String claseNov)
    {
        this.claseNov = claseNov;
    }

    @Override
    public void abrirFormulario()
    {
        String menu = "";
        if(parametroswf != null) {
        	menu = SysmanFunciones.nvl(parametroswf.get("menu"),"").toString();
        } else {
        	menu = SessionUtil.getMenuActual();
        }
        String strCaseNovedad;
        StringBuilder builder = new StringBuilder();

        switch (menu)
        {
        case "9020301":
            claseNov = "A";
            break;
        case "9020302":
            claseNov = "P";
            break;
        case "9020303":
            claseNov = "E";
            break;
        case "9020304":
            claseNov = "O";
            break;
        case "9020305":
            claseNov = "T";
            break;
        case "9020308":
            claseNov = "R";
            break;
        case "9020310":
            claseNov = "D";
            break;
        default:
            break;
        }

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaTipoNovedad = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(ParametroNovedadControladorUrlEnum.URL6565.getValue()).getUrl(),
                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        strCaseNovedad = service.buscarEnLista(String.valueOf(claseNov), codigoNovedad,
                        NOMBRE_NOVEDAD, listaTipoNovedad);

        builder.append(strCaseNovedad.toUpperCase()).append(" -  TRABAJAR CON:");

        titulo = builder.toString();

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
}
