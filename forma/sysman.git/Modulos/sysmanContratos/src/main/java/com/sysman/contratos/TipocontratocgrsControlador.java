package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
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
 * @author dcastro
 * @version 1, 16/09/2015
 * 
 * @author jcrodriguez, Refactoring y depuracion.
 * @version 2, 14/08/2017
 */
@ManagedBean
@ViewScoped

public class TipocontratocgrsControlador extends BeanBaseContinuoAcmeImpl
{

    private final String compania;

    /**
     * Creates a new instance of TipocontratocgrsControlador
     */
    public TipocontratocgrsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.TIPOCONTRATOCGRS_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex)
        {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(TipocontratocgrsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.TIPOCONTRATO_CGR;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        abrirFormulario();
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
    public void removerCombos()
    {
        // Metodo heredado
    }

    @Override
    public boolean insertarAntes()
    {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
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
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);

    }

    @Override
    public void asignarValoresRegistro()
    {
        // Metodo heredado
    }
}
