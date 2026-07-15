package com.sysman.almacen;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author ngomez
 * @version 1, 19/10/2015
 * 
 * @version 2, 26/04/2017
 * @author jreina se realizaron los cambios de refactoring en el
 * reasignar origen
 * 
 * @author ybecerra
 * @version 3, 12/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 */
@ManagedBean
@ViewScoped
public class ActdepreciablesControlador extends BeanBaseContinuoAcmeImpl
{

    /**
     * Creates a new instance of ActdepreciablesControlador
     */
    public ActdepreciablesControlador()
    {
        super();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.ACTDEPRECIABLES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex)
        {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(ActdepreciablesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.ACTDEPRECIABLE;
        buscarLlave();
        reasignarOrigen();

        registro = new Registro();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
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
        actualizarAntes();
        // </CODIGO_DESARROLLADO>
        return true;
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
        // </CODIGO_DESARROLLADO>
        return true;
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
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
}
