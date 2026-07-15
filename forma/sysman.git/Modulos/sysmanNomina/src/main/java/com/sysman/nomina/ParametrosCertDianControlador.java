package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.ParametrosCertDianControladorEnum;
import com.sysman.nomina.enums.ParametrosCertDianControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 * 
 * @version 1.0, 02/11/2017
 * @author asana
 */
@ManagedBean
@ViewScoped
public class ParametrosCertDianControlador extends BeanBaseContinuoAcmeImpl
{
    private String compania;
    private List<Registro> listaTipo;
    private String tipoP;
    private String valor;
    private String valorN;
    private String tipoParametro;
    private String valorMascara;

    /**
     * Crea una nueva instancia de ParametrosCertDian
     */
    public ParametrosCertDianControlador()
    {
        super();
        try
        {
            compania = SessionUtil.getCompania();
            numFormulario = GeneralCodigoFormaEnum.PARAMETROSCERTDIAN_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar()
    {
        tabla = GenericUrlEnum.PARAMETROS.getTable();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarlistaTipo();
        abrirFormulario();
        tipoP = "";
        valorN = "VALOR";
        tipoParametro = "TIPOPARAMETRO";
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("NOMBRETIPO");
        registro.getCampos().remove("MODULO");
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove("FECHACREACION");
        registro.getCampos().remove("FECHAINICIAL");
        registro.getCampos().remove("DESCRIPCION");
        registro.getCampos().remove("NOMPARAMETRO");

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        // <CODIGO_DESARROLLADO>
        listaInicial.load();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void reasignarOrigen()
    {
        // <CODIGO_DESARROLLADO>
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(ParametrosCertDianControladorEnum.PARAM9.getValue(), SessionUtil.getModulo());
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(ParametrosCertDianControladorUrlEnum.URL2288.getValue());
        urlActualizacion = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(ParametrosCertDianControladorUrlEnum.URL2290.getValue());
        // </CODIGO_DESARROLLADO>
    }

    private void cargarlistaTipo()
    {
        // <CODIGO_DESARROLLADO>

        try
        {
            listaTipo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ParametrosCertDianControladorUrlEnum.URL2289
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void cambiarTipo()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTipoC(int rowNum)
    {
        String valorTipoP;
        valorTipoP = listaInicial.getDatasource().get(rowNum % 10).getCampos().get(tipoParametro).toString();
        if ("4".equals(valorTipoP))
        {
            valorMascara = "99/99/9999";
        }
        else if ("6".equals(valorTipoP))
        {
            valorMascara = "99:99:99";
        }
        else
            valorMascara = "";
    }

    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        if ((registro.getCampos().get(tipoParametro) != null) || (registro.getCampos().get(valorN) != null))
        {
            return validarParametro();

        }
        else
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1781"));
            return false;
        }

        // </CODIGO_DESARROLLADO>

    }

    public boolean validarParametro()
    {
        boolean valida;

        tipoP = registro.getCampos().get(tipoParametro).toString();
        valor = registro.getCampos().get(valorN).toString();
        switch (tipoP)
        {
        case "2":
            valida = validarNumerico((valor.replace(",", ".")).trim());
            valorMascara = "";
            break;
        case "3":
            valida = validarNumericoEntero(valor);
            valorMascara = "";
            break;
        case "4":
            valida = validarFecha(valor.trim());
            valorMascara = "99/99/9999";
            break;
        case "5":
            valida = validarBoolean((valor.toLowerCase()).trim());
            valorMascara = "";
            break;
        case "6":
            valida = validarHora();
            valorMascara = "99:99:99";
            break;
        default:
            valida = true;
            break;
        }

        return valida;
    }

    public boolean validarNumerico(String numero)
    {
        try
        {
            registro.getCampos().put(valorN, Double.parseDouble(numero));
            return true;
        }
        catch (NumberFormatException ex)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB545"));
            return false;
        }

    }

    public boolean validarNumericoEntero(String numeroEntero)
    {
        try
        {
            Integer.parseInt(numeroEntero);
            return true;
        }
        catch (NumberFormatException e)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB545"));
            return false;
        }

    }

    public boolean validarFecha(String fecha)
    {
        String fechavalor = fecha.replace("-", "/");
        if (!SysmanFunciones.validarFecha(fechavalor))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB547"));
            return false;
        }
        else
        {
            registro.getCampos().put(valorN, fechavalor);
            return true;
        }
    }

    public boolean validarBoolean(String valor)
    {
        if (("SI".equals(valor)) || ("NO".equals(valor)))
        {
            return true;
        }
        else
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB549"));
            return false;
        }

    }

    public boolean validarHora()
    {
        Pattern pat = Pattern
                        .compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
        Matcher mat = pat.matcher(valor);
        if (mat.matches())
        {
            return true;
        }
        else
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB550"));
        }
        return false;
    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public List<Registro> getListaTipo()
    {
        return listaTipo;
    }

    public void setListaTipo(List<Registro> listaTipo)
    {
        this.listaTipo = listaTipo;
    }

    public String getValorMascara()
    {
        return valorMascara;
    }

    public void setValorMascara(String valorMascara)
    {
        this.valorMascara = valorMascara;
    }

}
