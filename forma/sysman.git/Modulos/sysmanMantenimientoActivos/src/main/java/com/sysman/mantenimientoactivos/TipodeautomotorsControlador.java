package com.sysman.mantenimientoactivos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author ngomez
 * @version 1, 16/09/2015
 *
 * @modifier amonroy
 * @version 2, 18/08/2017 Se realiza el Proceso de Refactoring para la ejecucion de las operaciones CRUD en el formulario
 */
@ManagedBean
@ViewScoped
public class TipodeautomotorsControlador extends BeanBaseContinuoAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Atributo que almacena la llave proveniente del formulario "inventarioparqueautomotorControlador"
     */
    private String rid;
    /**
     * Atributo necesario para enviar como parámetro para redireccionar al formulario "inventarioparqueautomotorControlador"
     */
    private String opcion;
    /**
     * Estructura que almacena los valores enviados por Flash desde el formulario "inventarioparqueautomotorControlador"
     */
    private Map<String, Object> parametrosEntrada;

    /**
     * Crea una nueva instancia de RestriccionespasesControlador
     */
    public TipodeautomotorsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.TIPODEAUTOMOTORS_CONTROLADOR
                            .getCodigo();
            validarPermisos();

            parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                rid = parametrosEntrada.get("rid").toString();
                opcion = parametrosEntrada.get("opcion").toString();
            }

        }
        catch (SysmanException ex)
        {
            Logger.getLogger(TipodeautomotorsControlador.class
                            .getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.TIPO_DE_AUTOMOTOR;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        listaInicial.load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     */
    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     *
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     *
     */
    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     *
     */
    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del registro
     *
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        listaInicial.load();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se pueden remover valores auxiliares que no se desee o se deban enviar en el registro
     */
    @Override
    public void removerCombos()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     *
     */
    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el formulario
     *
     * Se realiza la validacion del rid debido a que este formulario puede ser accedido mediante una opcion de menu o redireccionado desde el formulario "inventarioparqueautomotorControlador"
     *
     */
    public void ejecutarrcCerrar()
    {
        // <CODIGO_DESARROLLADO>
        if (rid != null)
        {
            Map<String, Object> parametros = parametrosEntrada;
            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.INVENTARIOPARQUEAUTOMOTOR_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);
            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());
        }
        else
        {
            SessionUtil.redireccionarMenu();
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y edicion del registro se usa cuando se desean agregar valores al registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public String getCompania()
    {
        return compania;
    }

    /**
     * Retorna el valor del atributo rid
     *
     * @return Valor del atributo rid
     */
    public String getRid()
    {
        return rid;
    }

    /**
     * Asigna un valor al atributo rd
     *
     * @param rid
     */
    public void setRid(String rid)
    {
        this.rid = rid;
    }

    /**
     * Retorna el valor del atributo opcion
     *
     * @return Valor del atributo opcion
     */
    public String getOpcion()
    {
        return opcion;
    }

    /**
     * Asigna un valor al atributo opcion
     *
     * @param opcion
     */
    public void setOpcion(String opcion)
    {
        this.opcion = opcion;
    }
}
