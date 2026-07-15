package com.sysman.mantenimientoactivos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GenericUrlEnum;

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
 * @author eamaya
 * @version 2.0, 16/08/2017, Proceso de Refactoring DSS, cambio de numero de formulario por enum
 *
 */
@ManagedBean
@ViewScoped
public class MarcaControlador extends BeanBaseContinuoAcmeImpl
{

    private Map<String, Object> rid;
    private String opcion;

    /**
     * Creates a new instance of MarcaControlador
     */
    public MarcaControlador()
    {
        super();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.MARCA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(MarcaControlador.class.getName()).log(Level.SEVERE,
                            null, ex);
        }
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.MARCA;
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

    public Map<String, Object> getRid()
    {
        return rid;
    }

    public void setRid(Map<String, Object> rid)
    {
        this.rid = rid;
    }

    public String getOpcion()
    {
        return opcion;
    }

    public void setOpcion(String opcion)
    {
        this.opcion = opcion;
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        listaInicial.load();
        return true;
        // </CODIGO_DESARROLLADO>
    }

    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void removerCombos()
    {
        // METODO_NO_IMPLEMENTADO
    }

    @Override
    public void asignarValoresRegistro()
    {
        // METODO_NO_IMPLEMENTADO
    }

}
