
package com.sysman.bancoproyectos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
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
 * @author dmaldonado
 * @version 1, 12/08/2015
 * 
 * @author asana
 * @version 2, 22/09/2017 Se realiza proceso de refactoring.
 * @author asana
 * @version 3, 18/108/2017 Dado que se estaba direccionando a la tabla
 * Unidad, por indicaciones de Jose, se direcciona a tabla
 * UnidadProyectos dado que esta tabla esx propia de este módulo
 */
@ManagedBean
@ViewScoped
public class FrmunidadproyectosControlador extends BeanBaseContinuoAcmeImpl
{

    private String compania;

    /**
     * Creates a new instance of FrmunidadproyectosControlador
     */
    public FrmunidadproyectosControlador()
    {
        super();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMUNIDADPROYECTOS_CONTROLADOR
                            .getCodigo();
            compania = SessionUtil.getCompania();
            validarPermisos();
        }
        catch (SysmanException ex)
        {
            Logger.getLogger(FrmunidadproyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.UNIDADPROYECTOS;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO DESARROLLADO>
        // </CODIGO DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        listaInicial.load();
    }

    @Override
    public void removerCombos()
    {
        // NO ESTA IMPLEMENTADO

        registro.getCampos().remove("CREATED_BY");
        registro.getCampos().remove("DATE_CREATED");
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
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
        // < CODIGO DESARROLADO>

        return true;
        // </CODIGO DESARROLLADO>
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
        // < CODIGO DESARROLADO>

        // </CODIGO DESARROLLADO>
    }

}