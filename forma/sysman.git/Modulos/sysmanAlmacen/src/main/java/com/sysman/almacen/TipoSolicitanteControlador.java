package com.sysman.almacen;

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
 * @author OTORRES
 * @version 1, 03/02/2016
 * @version 2, 10/05/2017
 * Descripcion:*depuracion del controlador
 *             *creacion de dss
 *             
 *@version 3.0, 13/06/2017, <strong>pespitia</strong>:<br>Reemplazar numero del formulario por enumerado.
 */
@ManagedBean
@ViewScoped
public class TipoSolicitanteControlador extends BeanBaseContinuoAcmeImpl
{
    /**
     * variable que almacena la compa�ia
     */
    private final String compania;

    /**
     * Creates a new instance of TipoSolicitanteControlador
     */
    public TipoSolicitanteControlador()
    {
        super();
        
        compania = SessionUtil.getCompania();
        
        try
        {
            // 499
            numFormulario = GeneralCodigoFormaEnum.TIPO_SOLICITANTE_CONTROLADOR.getCodigo();
            
            validarPermisos();
        }
        catch (SysmanException ex)
        {
            Logger.getLogger(TipoSolicitanteControlador.class.getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();

        }

    }
    /**
     * metodo que se llama al iniciar el formulario
     */
    @PostConstruct
    public void init()
    {
       enumBase=GenericUrlEnum.TIPO_SOLICITANTE;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        abrirFormulario();
    }
    /**
     * metoodo que se llama al para asignar datos a la grilla
     * se asignan las url de los dss creados
     */
    @Override   
    public void reasignarOrigen()
    {
       buscarUrls();
       parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),compania);
    }
    /**
     * metodo que se llama al abrir el formulario
     */
    @Override
    public void abrirFormulario()
    {
        // Se llama desde la forma
    }
    /**
     * metodo que se llama al cancela la edicion
     */
    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        listaInicial.load();
    }
    /**
     * metodo heredado del bean padre
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * metodo heredado del bean padre
     */
    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * metodo heredado del bean padre
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * metodo heredado del bean padre
     */
    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * metodo heredado del bean padre
     */
    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * metodo heredado del bean padre
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * metodo heredado del bean padre
     */
    @Override
    public void asignarValoresRegistro()
    {
        // Se llama desde la forma
    }
    /**
     * metodo que se llama antes de antualizar antes
     */
    @Override
    public void removerCombos()
    {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.DATE_CREATED.getName());
        registro.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
    }
}
