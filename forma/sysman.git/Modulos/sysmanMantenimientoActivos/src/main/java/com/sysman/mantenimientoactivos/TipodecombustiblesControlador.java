package com.sysman.mantenimientoactivos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.mantenimientoactivos.enums.TipodecombustiblesControladorUrlEnum;

import java.util.HashMap;
import java.util.List;
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
 * @version 2, 22/08/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno de los combos y en el origen de grilla.
 */

@ManagedBean
@ViewScoped
public class TipodecombustiblesControlador extends BeanBaseContinuoAcmeImpl
{

    private String rid;
    private String opcion;
    private List<Registro> listaCodigo;

    /**
     * Creates a new instance of TipodecombustiblesControlador
     */
    public TipodecombustiblesControlador()
    {
        super();
        numFormulario = GeneralCodigoFormaEnum.TIPODECOMBUSTIBLES_CONTROLADOR
                        .getCodigo();
        try
        {
            validarPermisos();
        }
        catch (SysmanException ex)
        {
            Logger.getLogger(TipodecombustiblesControlador.class
                            .getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.TIPO_DE_COMBUSTIBLE;
        buscarLlave();
        reasignarOrigen();

        registro = new Registro(new HashMap<String, Object>());
        cargarListaCodigo();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
    }

    public List<Registro> getListaCodigo()
    {
        return listaCodigo;
    }

    public void setListaCodigo(List<Registro> listaCodigo)
    {
        this.listaCodigo = listaCodigo;
    }

    public String getRid()
    {
        return rid;
    }

    public void setRid(String rid)
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

    public void cargarListaCodigo()
    {
        try
        {
            listaCodigo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TipodecombustiblesControladorUrlEnum.URL10542
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void removerCombos()
    {
        // Metodo heredado
    }

    @Override
    public void abrirFormulario()
    {
        // Metodo heredado
    }

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        // Metodo heredado
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
        listaInicial.load();
        return true;
    }

    @Override
    public void asignarValoresRegistro()
    {
        // Metodo heredado
    }
}
