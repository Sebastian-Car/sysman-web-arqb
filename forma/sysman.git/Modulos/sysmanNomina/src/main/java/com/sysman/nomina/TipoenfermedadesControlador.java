package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author cmanrique
 * 
 * @author jcrodriguez Refactoring y depuracion
 * @version 2, 30/10/2017
 */
@ManagedBean
@ViewScoped

public class TipoenfermedadesControlador extends BeanBaseContinuoAcmeImpl
{

    /**
     * Creates a new instance of TipoenfermedadesControlador
     */
    public TipoenfermedadesControlador()
    {
        super();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.TIPOENFERMEDADES_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex)
        {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(TipoenfermedadesControlador.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @PostConstruct
    public void init()
    {
        enumBase = GenericUrlEnum.TIPO_ENFERMEDADES;
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

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        listaInicial.load();
    }

    @Override
    public void abrirFormulario()
    {
        // heredado del bean base
    }

    @Override
    public boolean insertarAntes()
    {
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {

        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        return true;
    }

    @Override
    public boolean eliminarDespues()
    {

        return true;
    }

    @Override
    public void removerCombos()
    {
        // heredado del bean base
    }

    @Override
    public void asignarValoresRegistro()
    {
        // heredado del bean base
    }

}
