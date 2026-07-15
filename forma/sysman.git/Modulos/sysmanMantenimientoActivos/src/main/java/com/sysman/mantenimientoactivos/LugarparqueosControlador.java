package com.sysman.mantenimientoactivos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.mantenimientoactivos.enums.LugarparqueosControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * @author ngomez
 * @version 1, 16/09/2015
 *
 * @version 2, 16/08/2017, <strong>pespitia</strong>:<br>
 * Refactoring de sentencias SQL.<br>
 * Se reemplazo del numero del formulario por enumerado.<br>
 * Se ajusto el redireccionar a otro formulario.
 */
@ManagedBean
@ViewScoped
public class LugarparqueosControlador extends BeanBaseContinuoAcmeImpl
{

    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde el cual el usuario inicio sesion.
     */
    private final String modulo;

    /**
     * Constante a nivel de clase que aloja la cadena: <code>NOMBRE</code>
     */
    private final String cNombre;

    /**
     * Constante a nivel de clase que aloja la cadena: <code>DIRECCION</code>
     */
    private final String cDireccion;

    /**
     * Constante a nivel de clase que aloja la cadena: <code>SUCURSAL</code>
     */
    private final String cSucursal;

    /**
     * Constante a nivel de clase que aloja la cadena: <code>COMPANIA</code>
     */
    private final String cCompania;

    private RegistroDataModelImpl listaCodigo;
    private RegistroDataModelImpl listaCodigoE;
    private RegistroDataModelImpl listaResponsable;
    private RegistroDataModelImpl listaResponsableE;
    private String auxiliar;
    private String registroauxNombre;
    private String registroauxDireccion;
    private String registroauxTelefono;
    private String registroauxSucursalCod;
    private String registroauxSucursal;
    private Map<String, Object> rid;
    private String opcion;

    /**
     * Creates a new instance of LugarparqueosControlador
     */
    @SuppressWarnings("unchecked")
    public LugarparqueosControlador()
    {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cNombre = GeneralParameterEnum.NOMBRE.getName();
        cDireccion = GeneralParameterEnum.DIRECCION.getName();
        cSucursal = GeneralParameterEnum.SUCURSAL.getName();
        cCompania = GeneralParameterEnum.COMPANIA.getName();

        try
        {
            // 200
            numFormulario = GeneralCodigoFormaEnum.LUGARPARQUEOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null)
            {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
                opcion = parametrosEntrada.get("opcion").toString();
            }
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally
        {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.LUGAR_PARQUEO;

        buscarLlave();
        reasignarOrigen();

        registro = new Registro();

        cargarListaCodigo();
        cargarListaCodigoE();
        cargarListaResponsable();
        cargarListaResponsableE();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen()
    {
        buscarUrls();

        parametrosListado.put(cCompania, compania);
    }

    public String getAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    public Map<String, Object> getRid()
    {
        return rid;
    }

    public void setRid(Map<String, Object> rid)
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
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LugarparqueosControladorUrlEnum.URL4984
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaCodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "NIT");
    }

    public void cargarListaCodigoE()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LugarparqueosControladorUrlEnum.URL4984
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaCodigoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "NIT");
    }

    public void cargarListaResponsable()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LugarparqueosControladorUrlEnum.URL6459
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaResponsable = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "NIT");
    }

    public void cargarListaResponsableE()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LugarparqueosControladorUrlEnum.URL6459
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaResponsableE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "NIT");
    }

    public void cambiarCodigoC(int rowNum)
    {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(cNombre,
                        registroauxNombre);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cDireccion, registroauxDireccion);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("TELEFONO", registroauxTelefono);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("SUCURSAL_CODIGO", registroauxSucursalCod);
        // Para el cambio en una fila selecciona (PARA SUBFORMULARIOS)
        // se realiza como lo muestra la siguiente linea
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarResponsableC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(cSucursal,
                        registroauxSucursal);
        // Para el cambio en una fila selecciona (PARA SUBFORMULARIOS)
        // se realiza como lo muestra la siguiente linea
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCodigo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CODIGO", registroAux.getCampos().get("NIT"));

        registro.getCampos().put(cNombre, registroAux.getCampos().get(cNombre));

        registro.getCampos().put(cDireccion,
                        registroAux.getCampos().get(cDireccion));

        registro.getCampos().put("TELEFONO",
                        registroAux.getCampos().get("TELEFONOS"));

        registro.getCampos().put("SUCURSAL_CODIGO",
                        registroAux.getCampos().get(cSucursal));
    }

    public void seleccionarFilaCodigoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), "")
                        .toString();

        registroauxNombre = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNombre), "")
                        .toString();

        registroauxDireccion = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cDireccion), "")
                        .toString();

        registroauxTelefono = SysmanFunciones
                        .nvl(registroAux.getCampos().get("TELEFONOS"), "")
                        .toString();

        registroauxSucursalCod = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cSucursal), "")
                        .toString();
    }

    public void seleccionarFilaResponsable(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put("RESPONSABLE",
                        registroAux.getCampos().get("NIT"));

        registro.getCampos().put(cSucursal,
                        registroAux.getCampos().get(cSucursal));
    }

    public void seleccionarFilaResponsableE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), "")
                        .toString();

        registroauxSucursal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cSucursal), "")
                        .toString();
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
        registro.getCampos().put(cCompania, compania);
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

    public void ejecutarrcCerrar()
    {
        // <CODIGO_DESARROLLADO>
        if (rid == null)
        {
            SessionUtil.redireccionarMenu();
        }
        else
        {
            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.INVENTARIOPARQUEAUTOMOTOR_CONTROLADOR
                                            .getCodigo()));

            Map<String, Object> inPar = new HashMap<>();
            inPar.put("rid", rid);
            inPar.put("opcion", opcion);

            direccionador.setParametros(inPar);

            SessionUtil.redireccionarForma(direccionador, modulo);
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(cCompania);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
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

    public RegistroDataModelImpl getListaResponsable()
    {
        return listaResponsable;
    }

    public void setListaResponsable(RegistroDataModelImpl listaResponsable)
    {
        this.listaResponsable = listaResponsable;
    }

    public RegistroDataModelImpl getListaResponsableE()
    {
        return listaResponsableE;
    }

    public void setListaResponsableE(RegistroDataModelImpl listaResponsableE)
    {
        this.listaResponsableE = listaResponsableE;
    }
}
