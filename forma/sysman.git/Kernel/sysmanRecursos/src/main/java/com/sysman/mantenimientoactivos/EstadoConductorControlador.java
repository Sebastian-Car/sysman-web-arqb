package com.sysman.mantenimientoactivos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

/**
 *
 * @author ngomez
 * @version 1, 21/09/2015
 * @author spina - refactorizo conexiones
 * @version 2, 12/06/2017
 * @author jcrodriguez - Refactoring
 * @version 3, 06/09/2017
 */
@ManagedBean
@ViewScoped
public class EstadoConductorControlador extends BeanBaseModal
{

    private String opcion;

    /**
     * Creates a new instance of EstadoConductorControlador
     */
    public EstadoConductorControlador()
    {
        super();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.ESTADO_CONDUCTOR_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(EstadoConductorControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        opcion = "1";
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirAceptar()
    {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("opcion", opcion);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(GeneralCodigoFormaEnum.RECURSOHUMANOS_CONTROLADOR.getCodigo()));
        direccionador.setParametros(parametros);
        RequestContext.getCurrentInstance().closeDialog(direccionador);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCancelar()
    {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    public String getOpcion()
    {
        return opcion;
    }

    public void setOpcion(String opcion)
    {
        this.opcion = opcion;
    }

}
