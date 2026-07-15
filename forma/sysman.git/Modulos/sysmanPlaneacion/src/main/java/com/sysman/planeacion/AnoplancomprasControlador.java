package com.sysman.planeacion;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.planeacion.enums.AnoplancomprasControladorEnum;
import com.sysman.planeacion.enums.AnoplancomprasControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author dmaldonado
 * @version 1, 17/02/2016
 * 
 * @author jcrodriguez,Refactoring, Depuracion, se cambia el campo ano
 * por un combo sencillo, se agrega el metodo cargar lista ano y se
 * crea el dss
 * @version 2, 07/09/2017
 */
@ManagedBean
@ViewScoped
public class AnoplancomprasControlador extends BeanBaseContinuoAcmeImpl
{

    private final String compania;

    /**
     * Creates a new instance of AnoplancomprasControlador
     */
    public AnoplancomprasControlador()
    {
        super();
        compania = SessionUtil.getCompania();

        numFormulario = GeneralCodigoFormaEnum.ANOPLANCOMPRAS_CONTROLADOR.getCodigo();
        try
        {
            validarPermisos();
        }
        catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @PostConstruct
    public void init()
    {
        enumBase = GenericUrlEnum.ANO;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        abrirFormulario();

    }

    @Override
    public void reasignarOrigen()
    {
        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AnoplancomprasControladorUrlEnum.URL2124
                                                        .getValue());
        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AnoplancomprasControladorUrlEnum.URL2126
                                                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AnoplancomprasControladorUrlEnum.URL2127
                                                        .getValue());
    }

    @Override
    public void abrirFormulario()
    {
        // heredado del bean base
        registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(), SysmanFunciones.getParteFecha(new Date(),
                        Calendar.YEAR));
    }

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
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
        JsfUtil.ejecutarJavaScript("cerrarModalContinuoModulo()");
    }

    @Override
    public void removerCombos()
    {
        registro.getCampos().remove(AnoplancomprasControladorEnum.VLRFONDOSOL_PENSIONAL.getValue());
        registro.getCampos().remove(AnoplancomprasControladorEnum.PORCENTAJESALUD.getValue());
        registro.getCampos().remove(AnoplancomprasControladorEnum.PORCENTAJEPENSION.getValue());
        registro.getCampos().remove(AnoplancomprasControladorEnum.VALORUVT.getValue());
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
    }

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        // heredado del bean base
    }

    @Override
    public void asignarValoresRegistro()
    {
        // heredado del bean base
    }

}
