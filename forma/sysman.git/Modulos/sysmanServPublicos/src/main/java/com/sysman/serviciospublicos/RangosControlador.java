package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author amonroy
 * @version 1, 02/08/2016
 *
 * @author jreina
 * @version 2, 13/06/2017 Cambio código formulario y actualización de ConnectorPool
 *
 * @author jguerrero implementacion de refactoring al controlador.
 * @version 3, 15/06/2017
 */
@ManagedBean
@ViewScoped
public class RangosControlador extends BeanBaseContinuoAcmeImpl
{
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String limiteInferior;
    private String limiteSuperior;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    private boolean cambiarNombreServicioAcueducto;
    private String unidadParaAcueducto;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejSysmanUtil;

    private static final String TG_LIMITE_SUPERIOR = "TG_LIMITE_SUPERIOR2";
    private static final String TG_LIMITE_INFERIOR = "TG_LIMITE_INFERIOR";
    private static final String TI_MS_ERROR_VALIDACION = "TI_MS_ERROR_VALIDACION";
    private static final String LIMITE_INFERIOR = "LIMITEINFERIOR";
    private static final String LIMITE_SUPERIOR = "LIMITESUPERIOR";

    /**
     * Creates a new instance of RangosControlador
     */
    public RangosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.RANGOS_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>

            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(RangosControlador.class.getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init()
    {
        try
        {
            cambiarNombreServicioAcueducto = "SI".equalsIgnoreCase(SysmanFunciones.nvl(ejSysmanUtil.consultarParametro(compania,
                            "CAMBIAR NOMBRE SERVICIO ACUEDUCTO", SessionUtil.getModulo(), new Date(), true), "NO").toString());
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        enumBase = GenericUrlEnum.SP_RANGO;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            if (cambiarNombreServicioAcueducto)
            {

                unidadParaAcueducto = SysmanFunciones.nvl(ejSysmanUtil.consultarParametro(compania, "UNIDAD PARA ACUEDUCTO",
                                SessionUtil.getModulo(), new Date(), true), " ").toString();
                limiteInferior = idioma.getString(TG_LIMITE_INFERIOR) + " " + unidadParaAcueducto;
                limiteSuperior = idioma.getString(TG_LIMITE_SUPERIOR) + " " + unidadParaAcueducto;
            }
            else
            {
                limiteInferior = idioma.getString(TG_LIMITE_INFERIOR) + " M3";
                limiteSuperior = idioma.getString(TG_LIMITE_SUPERIOR) + " M3";
            }

        }
        catch (SystemException e)
        {
            Logger.getLogger(RangosControlador.class.getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes()
    {
        StringBuilder builder = new StringBuilder();
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        // </CODIGO_DESARROLLADO>
        if (Integer.parseInt(registro.getCampos().get(LIMITE_INFERIOR).toString()) >= Integer
                        .parseInt(registro.getCampos().get(LIMITE_SUPERIOR).toString()))
        {
            builder.append(idioma.getString("TB_TB3334"));
            JsfUtil.agregarMensajeAlerta(builder.toString());
            return false;

        }
        else
        {
            return true;
        }

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
        StringBuilder builder = new StringBuilder();
        boolean rta = true;
        if ((("").equals(registro.getCampos().get("CODIGO"))) && (registro.getCampos().get("LIMITEINFERIOR") == null)
            && (registro.getCampos().get(LIMITE_INFERIOR) == null))
        {
            builder.append(idioma.getString(TI_MS_ERROR_VALIDACION)).append(": ").append(idioma.getString("TG_CODIGO"))
                            .append(idioma.getString(TG_LIMITE_INFERIOR)).append(", ").append(idioma.getString(TG_LIMITE_SUPERIOR));

            JsfUtil.agregarMensajeAlerta(builder.toString());

            rta = false;
        }
        else if (("").equals(registro.getCampos().get("CODIGO")))
        {
            builder.append(idioma.getString(TI_MS_ERROR_VALIDACION)).append(": ").append(idioma.getString("TG_CODIGO"));
            JsfUtil.agregarMensajeAlerta(builder.toString());
            rta = false;
        }
        else if (registro.getCampos().get(LIMITE_INFERIOR) == null)
        {
            builder.append(idioma.getString(TI_MS_ERROR_VALIDACION)).append(": ").append(idioma.getString(TG_LIMITE_INFERIOR));
            JsfUtil.agregarMensajeAlerta(builder.toString());
            rta = false;
        }
        else if (registro.getCampos().get(LIMITE_SUPERIOR) == null)
        {
            builder.append(idioma.getString(TI_MS_ERROR_VALIDACION)).append(": ").append(idioma.getString(TG_LIMITE_SUPERIOR));
            JsfUtil.agregarMensajeAlerta(builder.toString());
            rta = false;

        }
        else if (Integer.parseInt(registro.getCampos().get(LIMITE_INFERIOR).toString()) >= Integer
                        .parseInt(registro.getCampos().get(LIMITE_SUPERIOR).toString()))
        {
            builder.append(idioma.getString("TB_TB3334"));
            JsfUtil.agregarMensajeAlerta(builder.toString());
            rta = false;

        }
        // </CODIGO_DESARROLLADO>
        return rta;
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

    @Override
    public void removerCombos()
    {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

    }

    public void cerrarFormulario()
    {
        JsfUtil.ejecutarJavaScript("cerrarModalDefault()");
    }

    @Override
    public void asignarValoresRegistro()
    {
        // METODO NO IMPLEMENTADO
    }

    // <SET_GET_ATRIBUTOS>
    public String getLimiteInferior()
    {
        return limiteInferior;
    }

    public void setLimiteInferior(String limiteInferior)
    {
        this.limiteInferior = limiteInferior;
    }

    public String getLimiteSuperior()
    {
        return limiteSuperior;
    }

    public void setLimiteSuperior(String limiteSuperior)
    {
        this.limiteSuperior = limiteSuperior;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>

    public boolean isCambiarNombreServicioAcueducto()
    {
        return cambiarNombreServicioAcueducto;
    }

    public void setCambiarNombreServicioAcueducto(boolean cambiarNombreServicioAcueducto)
    {
        this.cambiarNombreServicioAcueducto = cambiarNombreServicioAcueducto;
    }

    public String getUnidadParaAcueducto()
    {
        return unidadParaAcueducto;
    }

    public void setUnidadParaAcueducto(String unidadParaAcueducto)
    {
        this.unidadParaAcueducto = unidadParaAcueducto;
    }

    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
