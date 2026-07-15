package com.sysman.bancoproyectos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author lcortes
 * @version 1, 17/09/2015
 * 
 * @author jcrodriguez,Refactoring y depuracion del controlador
 * @version 1, 17/09/2015
 */
@ManagedBean
@ViewScoped

public class PreguntasControlador extends BeanBaseContinuoAcmeImpl
{

    private final String compania;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of PreguntasControlador
     */
    public PreguntasControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.PREGUNTAS_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex)
        {
            Logger.getLogger(PreguntasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.PREGUNTAS;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // heredado del bean base
    }

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes()
    {
        try
        {
            StringBuilder criterio = new StringBuilder();
            criterio.append("COMPANIA = ''");
            criterio.append(compania);
            criterio.append("''");
            registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                            ejbSysmanUtil.generarConsecutivoConValorInicial(GenericUrlEnum.PREGUNTAS.getTable(),
                                            criterio.toString(), GeneralParameterEnum.CODIGO.getName(), "001"));
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);

        }
        catch (SystemException ex)
        {
            Logger.getLogger(PreguntasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        // herado del bean base
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        // herado del bean base
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
        // herado del bean base
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        // herado del bean base
        return true;
    }

    @Override
    public boolean eliminarDespues()
    {
        // herado del bean base
        return true;
    }

    @Override
    public void removerCombos()
    {
        // herado del bean base
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
        // heredado del bean base

    }
}