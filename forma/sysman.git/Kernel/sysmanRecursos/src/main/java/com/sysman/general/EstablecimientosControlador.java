package com.sysman.general;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.EstablecimientosControladorEnum;
import com.sysman.general.enums.EstablecimientosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 *
 * @author cmanrique
 *
 * Revision Sonar
 *
 * -- Modificado por ybecerra 16/03/2017
 * 
 * @author eamaya
 * @version 2.0,04/10/2017, Proceso de Refactoring DSS, Manejo de EJBs y cambio de numero de formulario por enum
 * 
 */
@ManagedBean
@ViewScoped

public class EstablecimientosControlador extends BeanBaseDatosAcmeImpl
{

    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para almacenar la cadena "DEPARTAMENTO"
     */
    private final String cDepartamento;
    /**
     * Constante definida para almacenar la cadena "SUCURSAL"
     */
    private final String cSucursal;
    // <DECLARAR_ATRIBUTOS>
    private String pais;
    private String departamento;
    private String nombreTercero;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registros de la tabla CGR_DEPENDENCIAS
     */
    private List<Registro> listaDependenciasCgr;
    private List<Registro> listaPais;
    private List<Registro> listaDepartamento;
    private List<Registro> listaCiudad;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaNit;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    public EstablecimientosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        cDepartamento = "DEPARTAMENTO";
        cSucursal = "SUCURSAL";
        try {
            numFormulario = GeneralCodigoFormaEnum.ESTABLECIMIENTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(EstablecimientosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.ESTABLECIMIENTOS_DOCENTES;
        buscarLlave();
        asignarOrigenDatos();

    }

    @Override
    public void asignarOrigenDatos()
    {

        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    public void cargarListaNit()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EstablecimientosControladorUrlEnum.URL8311
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaNit = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaPais()
    {

        try {
            listaPais = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EstablecimientosControladorUrlEnum.URL8770
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaDepartamento()
    {

        Map<String, Object> param = new TreeMap<>();

        param.put(EstablecimientosControladorEnum.PAIS.getValue(), pais);

        try {
            listaDepartamento = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EstablecimientosControladorUrlEnum.URL3656
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCiudad()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(EstablecimientosControladorEnum.PAIS.getValue(),
                        pais);
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(),
                        departamento);

        try {
            listaCiudad = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EstablecimientosControladorUrlEnum.URL4587
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaDependenciasCgr
     *
     */
    public void cargarListaDependenciasCgr()
    {
        Map<String, Object> param = new TreeMap<>();
        try {
            listaDependenciasCgr = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EstablecimientosControladorUrlEnum.URL198
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
          
        }
    }

    public void cambiarPais()
    {
        // <CODIGO_DESARROLLADO>
        pais = SysmanFunciones.nvl(registro.getCampos().get("PAIS"), "")
                        .toString();

        cargarListaDepartamento();

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDepartamento()
    {
        // <CODIGO_DESARROLLADO>
        departamento = SysmanFunciones
                        .nvl(registro.getCampos().get(cDepartamento), "")
                        .toString();
        cargarListaCiudad();
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaNit(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NIT", registroAux.getCampos().get("NIT"));
        nombreTercero = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
        registro.getCampos().put(cSucursal,
                        registroAux.getCampos().get(cSucursal));
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        if (accion.equals(ACCION_INSERTAR)) {
            departamento = null;
            listaDepartamento = null;
            listaCiudad = null;
            nombreTercero = null;
            registro.getCampos().put(cDepartamento, null);
            registro.getCampos().put("CIUDAD", null);
            listaCiudad = null;
        }else {
            pais = SysmanFunciones.nvl(registro.getCampos().get("PAIS"), "")
                            .toString();
            departamento = SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.DEPARTAMENTO.getName()), "")
                            .toString();
            cargarListaDepartamento();
            cargarListaCiudad();
        }
        // </CODIGO_DESARROLLADO>
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

        if (accion.equals(ACCION_MODIFICAR)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
        }
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
    public void iniciarListasSubNulo()
    {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    @Override
    public void iniciarListasSub()
    {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>

    }

    @Override
    public void iniciarListas()
    {
        cargarListaNit();
        cargarListaPais();
        cargarListaDependenciasCgr();

    }

    public List<Registro> getListaPais()
    {
        return listaPais;
    }

    public void setListaPais(List<Registro> listaPais)
    {
        this.listaPais = listaPais;
    }

    public List<Registro> getListaDepartamento()
    {
        return listaDepartamento;
    }

    public void setListaDepartamento(List<Registro> listaDepartamento)
    {
        this.listaDepartamento = listaDepartamento;
    }

    public List<Registro> getListaCiudad()
    {
        return listaCiudad;
    }

    public void setListaCiudad(List<Registro> listaCiudad)
    {
        this.listaCiudad = listaCiudad;
    }

    public RegistroDataModelImpl getListaNit()
    {
        return listaNit;
    }

    public void setListaNit(RegistroDataModelImpl listaNit)
    {
        this.listaNit = listaNit;
    }

    public String getNombreTercero()
    {
        return nombreTercero;
    }

    public void setNombreTercero(String nombreTercero)
    {
        this.nombreTercero = nombreTercero;
    }

    /**
     * Retorna la lista listaDependenciasCgr
     * 
     * @return listaDependenciasCgr
     */
    public List<Registro> getListaDependenciasCgr()
    {
        return listaDependenciasCgr;
    }

    /**
     * Asigna la lista listaDependenciasCgr
     * 
     * @param listaDependenciasCgr
     * Variable a asignar en listaDependenciasCgr
     */
    public void setListaDependenciasCgr(List<Registro> listaDependenciasCgr)
    {
        this.listaDependenciasCgr = listaDependenciasCgr;
    }

}
