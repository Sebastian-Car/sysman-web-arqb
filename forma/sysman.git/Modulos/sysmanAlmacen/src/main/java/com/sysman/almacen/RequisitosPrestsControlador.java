package com.sysman.almacen;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author OTORRES
 * @version 1, 08/02/2016
 * 
 * @author eamaya
 * @version 2, 08/05/2017 Proceso de Refactoring y Manejo de EJBs
 * 
 * @author ybecerra
 * @version 3, 13/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 */
@ManagedBean
@ViewScoped
public class RequisitosPrestsControlador extends BeanBaseContinuoAcmeImpl
{

    private final String compania;

    @EJB
    private EjbSysmanUtilRemote ejbUtil;

    /**
     * Creates a new instance of RequisitosPrestsControlador
     */
    public RequisitosPrestsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.REQUISITOS_PRESTS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex)
        {
            SessionUtil.redireccionarMenuPermisos();
            logger.error(ex.getMessage(), ex);
        }

    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.REQUISITOS_PRESTAMO;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
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
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        try
        {
            registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                            ejbUtil.generarConsecutivoConValorInicial(tabla,
                                            "COMPANIA = " + compania + "",
                                            "CODIGO", "1"));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            Logger.getLogger(RequisitosPrestsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
        }

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

    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void asignarValoresRegistro()
    {
        // heredado del bean base

    }

    @Override
    public void removerCombos()
    {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

    }

}
