package com.sysman.almacen;

import com.sysman.almacen.enums.ProveedorControladorEnum;
import com.sysman.almacen.enums.ProveedorControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author ngomez
 * @version 1, 21/10/2015
 * 
 * @version 2, 05/05/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos, en el origen de grilla.
 */
@ManagedBean
@ViewScoped
public class ProveedorControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String cCodigoElemento;
    private String nombre;
    private String sucursal;
    private String tercero;
    private RegistroDataModelImpl listaCuadrocombinado10;
    private RegistroDataModelImpl listaCuadrocombinado10E;
    private RegistroDataModelImpl listaElemento;
    private RegistroDataModelImpl listaElementoE;
    private String auxiliar;

    /**
     * Creates a new instance of ProveedorControlador
     */
    public ProveedorControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.PROVEEDOR_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        cCodigoElemento = "CODIGOELEMENTO";
        try {
            validarPermisos();
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = ProveedorControladorEnum.TABLA.getValue();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaCuadrocombinado10();
        cargarListaCuadrocombinado10E();
        cargarListaElemento();
        cargarListaElementoE();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        ProveedorControladorUrlEnum.URL15084.getValue());
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.TERCERO.getName(), tercero);
        parametrosListado.put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);
    }

    public void cargarListaCuadrocombinado10() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ProveedorControladorUrlEnum.URL5313
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCuadrocombinado10 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaCuadrocombinado10E() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ProveedorControladorUrlEnum.URL5313
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCuadrocombinado10E = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaElemento() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ProveedorControladorUrlEnum.URL7309
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaElemento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoElemento);
    }

    public void cargarListaElementoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ProveedorControladorUrlEnum.URL7309
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaElementoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoElemento);
    }

    public void seleccionarFilaCuadrocombinado10(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tercero = registroAux.getCampos().get("NIT").toString();
        sucursal = registroAux.getCampos().get("SUCURSAL").toString();
        nombre = registroAux.getCampos().get("NOMBRE").toString();
        reasignarOrigen();
    }

    public void seleccionarFilaElementoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get("NIT").toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuadrocombinado10
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuadrocombinado10E(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get("NIT").toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaElemento
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaElemento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("ELEMENTO",
                        registroAux.getCampos().get(cCodigoElemento));
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                        tercero);
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        registro.getCampos().remove(ProveedorControladorEnum.NIT.getValue());
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

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getTercero() {
        return tercero;
    }

    public void setTercero(String tercero) {
        this.tercero = tercero;
    }

    public RegistroDataModelImpl getListaCuadrocombinado10() {
        return listaCuadrocombinado10;
    }

    public void setListaCuadrocombinado10(
        RegistroDataModelImpl listaCuadrocombinado10) {
        this.listaCuadrocombinado10 = listaCuadrocombinado10;
    }

    public RegistroDataModelImpl getListaCuadrocombinado10E() {
        return listaCuadrocombinado10E;
    }

    public void setListaCuadrocombinado10E(
        RegistroDataModelImpl listaCuadrocombinado10E) {
        this.listaCuadrocombinado10E = listaCuadrocombinado10E;
    }

    public RegistroDataModelImpl getListaElemento() {
        return listaElemento;
    }

    public void setListaElemento(RegistroDataModelImpl listaElemento) {
        this.listaElemento = listaElemento;
    }

    public RegistroDataModelImpl getListaElementoE() {
        return listaElementoE;
    }

    public void setListaElementoE(RegistroDataModelImpl listaElementoE) {
        this.listaElementoE = listaElementoE;
    }

}
