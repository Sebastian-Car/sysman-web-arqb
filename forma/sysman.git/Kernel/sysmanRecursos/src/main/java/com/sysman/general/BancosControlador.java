package com.sysman.general;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.BancosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 *
 * @author cmanrique
 *
 * @author lcortes
 * @version 2, 18/08/2017. Refactorizacion de codigo para usar dss.
 * 
 */

@ManagedBean
@ViewScoped
public class BancosControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;

    private RegistroDataModelImpl listaBanco;
    private static final String CTEBANCO = GeneralParameterEnum.BANCO.getName();
    private boolean visibleHojas;
    private final String modulo;
    /**
     * Variable encargada de mostrar u ocultar los componentes de
     * bancos por medio de una opcion de menu
     */
    private boolean visibleNomina;
    /**
     * Variable encargada de almacenar temporalmente el titulo del
     * formulario.
     */
    private String tituloNoCuenta;
    /**
     * Variable encargada de mostrar u ocultar los componentes de
     * bancos por medio del utilitario
     */
    private boolean visibleSiif;

    /**
     * Variable encargada de almacenar los datos de respuesta de la
     * base de datos
     */
    private List<Registro> listatipoDocumento;

    public BancosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        numFormulario = GeneralCodigoFormaEnum.BANCOS_CONTROLADOR.getCodigo();
        try {
            validarPermisos();
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        registro = new Registro(new HashMap<String, Object>());

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.BANCOS_NOMINA;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    public RegistroDataModelImpl getListaBanco() {
        return listaBanco;
    }

    public void setListaBanco(RegistroDataModelImpl listaBanco) {
        this.listaBanco = listaBanco;
    }

    @Override
    public void iniciarListas() {
        cargarListaBanco();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListatipoDocumento();
    }

    public void cambiarBanco() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void cargarListatipoDocumento() {

        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listatipoDocumento = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BancosControladorUrlEnum.URL2362
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
     * Carga la lista listaBanco
     *
     */
    public void cargarListaBanco() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BancosControladorUrlEnum.URL2361
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaBanco = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CTEBANCO);
    }

    public void seleccionarFilaBanco(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(CTEBANCO,
                        registroAux.getCampos().get(CTEBANCO));
        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos().get("NOMBREBANCO"));
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        SessionUtil.redireccionarMenuFormulario("60506");

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        if ("21".equals(modulo)) {
            visibleHojas = false;
        }
        else {
            visibleHojas = true;
        }
        validarOpcionMenu();

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (ACCION_MODIFICAR.equals(accion)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
        }
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public void iniciarListasSubNulo() {
        // Metodo heredado
    }

    @Override
    public void iniciarListasSub() {
        // Metodo heredado
    }

    public boolean isVisibleHojas() {
        return visibleHojas;
    }

    public void setVisibleHojas(boolean visibleHojas) {
        this.visibleHojas = visibleHojas;
    }

    public boolean isVisibleNomina() {
        return visibleNomina;
    }

    public void setVisibleNomina(boolean visibleNomina) {
        this.visibleNomina = visibleNomina;
    }

    public String getTituloNoCuenta() {
        return tituloNoCuenta;
    }

    public void setTituloNoCuenta(String tituloNoCuenta) {
        this.tituloNoCuenta = tituloNoCuenta;
    }

    public List<Registro> getListatipoDocumento() {
        return listatipoDocumento;
    }

    public void setListatipoDocumento(List<Registro> listatipoDocumento) {
        this.listatipoDocumento = listatipoDocumento;
    }

    public boolean isVisibleNit() {
        return visibleSiif;
    }

    public void setVisibleNit(boolean visibleNit) {
        this.visibleSiif = visibleNit;
    }

    public boolean isVisibleSiif() {
        return visibleSiif;
    }

    public void setVisibleSiif(boolean visibleSiif) {
        this.visibleSiif = visibleSiif;
    }

    /**
     * Metodo encargado de validar la opcion de menu y mostrar u
     * ocultar los compomentes.
     */

    private void validarOpcionMenu() {
        if ("60112".equals(SessionUtil.getMenuActual())
            || "21010110".equals(SessionUtil.getMenuActual())) {
            visibleNomina = true;
            visibleSiif = false;
            tituloNoCuenta = idioma.getString("TB_TB3981");

        }
        else {
            visibleSiif = true;
            visibleNomina = false;
            tituloNoCuenta = idioma.getString("TG_TIPO_DOCUMENTO2");
        }
    }

}
