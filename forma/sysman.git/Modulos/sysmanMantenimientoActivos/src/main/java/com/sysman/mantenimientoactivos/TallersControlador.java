package com.sysman.mantenimientoactivos;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.mantenimientoactivos.enums.TallersControladorEnum;
import com.sysman.mantenimientoactivos.enums.TallersControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

/**
 *
 * @author ngomez
 * @version 1, 18/09/2015
 * 
 * @author eamaya
 * @version 2.0,17/08/2017, Proceso de Refactoring DSS,cambio de
 * acciones por DSS y cambio de numero de formulario por enum
 * 
 */
@ManagedBean
@ViewScoped

public class TallersControlador extends BeanBaseDatosAcmeImpl {
    private final String compania;

    private Registro registroSub;
    private List<Registro> listaSubformularioactivotareap;
    private RegistroDataModelImpl listaNIT;
    private RegistroDataModelImpl listaNITE;
    private String auxiliar;
    private RegistroDataModelImpl listaCodigoActivo;
    private RegistroDataModelImpl listaPROPIETARIO;
    private String nombreCompania;
    private String registroauxNombre;
    private String registroauxSucursal;
    private String nombrePropietario;

    private final String sucursalCons;
    private final String nombreCons;

    public TallersControlador() {
        super();
        compania = SessionUtil.getCompania();
        sucursalCons = "SUCURSAL";
        nombreCons = "NOMBRE";
        try {
            nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
            numFormulario = GeneralCodigoFormaEnum.TALLERS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            registroSub = new Registro(new HashMap<String, Object>());
        }
        catch (Exception ex) {
            Logger.getLogger(TallersControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.TALLER;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    @Override
    public void iniciarListas() {
        cargarListaNIT();
        cargarListaNITE();
        cargarListaCodigoActivo();
        cargarListaPROPIETARIO();
    }

    @Override
    public void iniciarListasSub() {
        nombrePropietario = (String) registro.getCampos()
                        .get("NOMBREPROPIETARIO");
        cargarListaSubformularioactivotareap();
    }

    @Override
    public void iniciarListasSubNulo() {
        nombrePropietario = "";
        listaSubformularioactivotareap = null;
    }

    public void cargarListaSubformularioactivotareap() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(TallersControladorEnum.TALLER.getValue(),
                            registro.getCampos().get("NIT"));

            param.put(GeneralParameterEnum.SUCURSAL.getName(),
                            registro.getCampos().get(sucursalCons));

            listaSubformularioactivotareap = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TallersControladorUrlEnum.URL4042
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANDSUNIST,
                                            "RESPONSABLES_TALLERES"));
        }
        catch (SysmanException | SystemException e) {
            Logger.getLogger(TallersControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaNIT() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TallersControladorUrlEnum.URL5279
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaNIT = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaNITE() {
        listaNITE = listaNIT;

    }

    public void cargarListaCodigoActivo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TallersControladorUrlEnum.URL6573
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCodigoActivo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaPROPIETARIO() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TallersControladorUrlEnum.URL7385
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaPROPIETARIO = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void agregarRegistroSubSubformularioactivotareap() {
        try {
            registroSub.getCampos().put("COMPANIA", compania);
            registroSub.getCampos().put("TALLER",
                            registro.getCampos().get("NIT"));
            registroSub.getCampos().put("SUCURSAL_TALLER",
                            registro.getCampos().get(sucursalCons));

            registroSub.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            registroSub.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            TallersControladorUrlEnum.URL7441
                                                            .getValue());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());

            cargarListaSubformularioactivotareap();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
        }
        catch (SystemException ex) {
            Logger.getLogger(TallersControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    public void editarRegSubSubformularioactivotareap(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {

            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            TallersControladorUrlEnum.URL8482
                                                            .getValue());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException ex) {
            Logger.getLogger(TallersControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaSubformularioactivotareap();
        }
    }

    public void eliminarRegSubSubformularioactivotareap(Registro reg) {
        try {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            TallersControladorUrlEnum.URL10832
                                                            .getValue());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaSubformularioactivotareap();
        }
        catch (SystemException ex) {
            Logger.getLogger(TallersControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cancelarEdicionSubformularioactivotareap() {
        cargarListaSubformularioactivotareap();
    }

    public void cambiarNITC(int rowNum) {

        listaSubformularioactivotareap.get(rowNum).getCampos().put(nombreCons,
                        registroauxNombre);
        listaSubformularioactivotareap.get(rowNum).getCampos().put(sucursalCons,
                        registroauxSucursal);
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaNIT(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("NIT", registroAux.getCampos().get("NIT"));
        registroSub.getCampos().put(nombreCons,
                        registroAux.getCampos().get(nombreCons));
        registroSub.getCampos().put(sucursalCons,
                        registroAux.getCampos().get(sucursalCons));
    }

    public void seleccionarFilaNITE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), "")
                        .toString();
        registroauxNombre = SysmanFunciones
                        .nvl(registroAux.getCampos().get(nombreCons), "")
                        .toString();
        registroauxSucursal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(sucursalCons), "")
                        .toString();
    }

    public void seleccionarFilaCodigoActivo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NIT", registroAux.getCampos().get("NIT"));
        registro.getCampos().put(nombreCons,
                        registroAux.getCampos().get(nombreCons));
        registro.getCampos().put(sucursalCons,
                        registroAux.getCampos().get(sucursalCons));
        registro.getCampos().put("DIRECCION",
                        registroAux.getCampos().get("DIRECCION"));
        registro.getCampos().put("FAX", registroAux.getCampos().get("FAX"));
        registro.getCampos().put("TELEFONO",
                        registroAux.getCampos().get("TELEFONOS"));
    }

    public void seleccionarFilaPROPIETARIO(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("PROPIETARIO",
                        registroAux.getCampos().get("NIT"));
        registro.getCampos().put("SUCURSAL_PROPIETARIO",
                        registroAux.getCampos().get(sucursalCons));
        nombrePropietario = SysmanFunciones
                        .nvl(registroAux.getCampos().get(nombreCons), "")
                        .toString();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        // </CODIGO_DESARROLLADO>
    }

    public List<Registro> getListaSubformularioactivotareap() {
        return listaSubformularioactivotareap;
    }

    public void setListaSubformularioactivotareap(
        List<Registro> listaSubformularioactivotareap) {
        this.listaSubformularioactivotareap = listaSubformularioactivotareap;
    }

    public RegistroDataModelImpl getListaNIT() {
        return listaNIT;
    }

    public void setListaNIT(RegistroDataModelImpl listaNIT) {
        this.listaNIT = listaNIT;
    }

    public RegistroDataModelImpl getListaNITE() {
        return listaNITE;
    }

    public void setListaNITE(RegistroDataModelImpl listaNITE) {
        this.listaNITE = listaNITE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public RegistroDataModelImpl getListaCodigoActivo() {
        return listaCodigoActivo;
    }

    public void setListaCodigoActivo(RegistroDataModelImpl listaCodigoActivo) {
        this.listaCodigoActivo = listaCodigoActivo;
    }

    public RegistroDataModelImpl getListaPROPIETARIO() {
        return listaPROPIETARIO;
    }

    public void setListaPROPIETARIO(RegistroDataModelImpl listaPROPIETARIO) {
        this.listaPROPIETARIO = listaPROPIETARIO;
    }

    public Registro getRegistroSub() {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    public String getNombreCompania() {
        return nombreCompania;
    }

    public void setNombreCompania(String nombreCompania) {
        this.nombreCompania = nombreCompania;
    }

    public String getRegistroauxNombre() {
        return registroauxNombre;
    }

    public void setRegistroauxNombre(String registroauxNombre) {
        this.registroauxNombre = registroauxNombre;
    }

    public String getRegistroauxSucursal() {
        return registroauxSucursal;
    }

    public void setRegistroauxSucursal(String registroauxSucursal) {
        this.registroauxSucursal = registroauxSucursal;
    }

    public String getNombrePropietario() {
        return nombrePropietario;
    }

    public void setNombrePropietario(String nombrePropietario) {
        this.nombrePropietario = nombrePropietario;
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put("COMPANIA", compania);
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        registro.getCampos().remove("NOMBREPROPIETARIO");

        if (accion.equals(ACCION_MODIFICAR)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
        }
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

}
