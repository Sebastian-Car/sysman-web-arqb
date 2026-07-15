package com.sysman.mantenimientoactivos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.mantenimientoactivos.enums.HerramientasControladorEnum;
import com.sysman.mantenimientoactivos.enums.HerramientasControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author ngomez
 * @version 1, 16/09/2015
 * @author jcrodriguez, Refactoring y depuracion
 * @version 2, 15/08/2017
 */
@ManagedBean
@ViewScoped

public class HerramientasControlador extends BeanBaseContinuoAcmeImpl
{

    private final String compania;
    private RegistroDataModelImpl listaCodigo;
    private RegistroDataModelImpl listaCodigoE;
    private String auxiliar;
    private String registroauxNombre;
    private String registroauxSerie;

    /**
     * Creates a new instance of HerramientasControlador
     */
    public HerramientasControlador()
    {

        super();
        compania = SessionUtil.getCompania();

        try
        {

            numFormulario = GeneralCodigoFormaEnum.HERRAMIENTAS_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(HerramientasControlador.class
                            .getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.HERRAMIENTAS;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListaCodigo();
        cargarListaCodigoE();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);

    }

    public void cargarListaCodigo()
    {
        try
        {
            UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(HerramientasControladorUrlEnum.URL4075.getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaCodigo = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                            true,
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANDSUNIST,
                                            GenericUrlEnum.DEVOLUTIVO
                                                            .getTable()));
        }
        catch (SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaCodigoE()
    {
        listaCodigoE = listaCodigo;
    }

    public void cambiarCodigoC(int rowNum)
    {
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(HerramientasControladorEnum.NOMBRELEMENTO.getValue(), registroauxNombre);
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(GeneralParameterEnum.SERIE.getName(),
                        registroauxSerie);
    }

    public void seleccionarFilaCodigo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(HerramientasControladorEnum.CODIGOELEMENTO.getValue(),
                        registroAux.getCampos().get(GeneralParameterEnum.ELEMENTO.getName()));
        registro.getCampos().put(HerramientasControladorEnum.NOMBRELEMENTO.getValue(),
                        registroAux.getCampos().get(HerramientasControladorEnum.NOMBRECORTO.getValue()));
        registro.getCampos().put(GeneralParameterEnum.SERIE.getName(), registroAux.getCampos().get(GeneralParameterEnum.SERIE.getName()));
    }

    public void seleccionarFilaCodigoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = validarCadena(registroAux.getCampos(), GeneralParameterEnum.ELEMENTO.getName());
        registroauxNombre = validarCadena(registroAux.getCampos(), HerramientasControladorEnum.NOMBRECORTO.getValue());
        registroauxSerie = validarCadena(registroAux.getCampos(), GeneralParameterEnum.SERIE.getName());
    }

    /**
     * metodo que valida si un parametro campo cadena se encuentra
     * vacio
     * 
     * @param campos
     * @param var
     * @return
     */
    private String validarCadena(Map<String, Object> campos, String var)
    {
        return SysmanFunciones.validarCampoVacio(campos, var) ? "" : campos.get(var).toString();
    }

    @Override
    public void abrirFormulario()
    {
        // heredad del bean base
    }

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        getListaInicial().load();
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
        // <CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
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
        // NO SE IMPLEMENTA
    }

    @Override
    public void asignarValoresRegistro()
    {
        // NO SE IMPLEMENTA
    }

    public RegistroDataModelImpl getListaCodigo()
    {
        return listaCodigo;
    }

    public void setListaCodigo(RegistroDataModelImpl listaCodigo)
    {
        this.listaCodigo = listaCodigo;
    }

    public RegistroDataModelImpl getListaCodigoE()
    {
        return listaCodigoE;
    }

    public void setListaCodigoE(RegistroDataModelImpl listaCodigoE)
    {
        this.listaCodigoE = listaCodigoE;
    }

    public String getAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    public String getRegistroauxNombre()
    {
        return registroauxNombre;
    }

    public void setRegistroauxNombre(String registroauxNombre)
    {
        this.registroauxNombre = registroauxNombre;
    }

    public String getRegistroauxSerie()
    {
        return registroauxSerie;
    }

    public void setRegistroauxSerie(String registroauxSerie)
    {
        this.registroauxSerie = registroauxSerie;
    }

}
