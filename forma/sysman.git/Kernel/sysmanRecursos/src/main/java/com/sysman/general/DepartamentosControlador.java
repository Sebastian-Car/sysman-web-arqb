package com.sysman.general;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.DepartamentosControladorEnum;
import com.sysman.general.enums.DepartamentosControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;

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
import javax.faces.event.ActionEvent;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author dsuesca
 * @version 1, 20/10/2015
 *
 * Revision Sonar
 * @author ybecerra
 * @version 2, 03/04/2017
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Cambio código formulario y actualización
 * de ConnectorPool
 */
@ManagedBean
@ViewScoped

public class DepartamentosControlador extends BeanBaseDatosAcmeImpl {

    private final String modulo;
    /**
     * Constante definida que almacena el valor
     * GeneralParameterEnum.CODIGO.getName()
     */
    private final String cCodigo;
    // <DECLARAR_ATRIBUTOS>
    private Registro registroSub;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaPais;
    private List<Registro> listaregion;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    private RegistroDataModelImpl listaCiudad;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>

    public DepartamentosControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.DEPARTAMENTOS_CONTROLADOR
                        .getCodigo();

        modulo = SessionUtil.getModulo();
        cCodigo = GeneralParameterEnum.CODIGO.getName();
        try {
            registroSub = new Registro(new HashMap<String, Object>());
            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(DepartamentosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.DEPARTAMENTO;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
    }

    @Override
    public void iniciarListas() {
        cargarListaPais();
        cargarListaregion();
    }

    @Override
    public void iniciarListasSub() {
        cargarListaCiudad();
    }

    @Override
    public void iniciarListasSubNulo() {
        listaCiudad = null;
    }

    /**
     *
     * Carga la lista listaCiudad
     *
     */
    public void cargarListaCiudad() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GenericUrlEnum.CIUDAD.getGridKey());
        Map<String, Object> param = new TreeMap<>();
        param.put(DepartamentosControladorEnum.PARAM0.getValue(),
                        registro.getCampos().get("PAIS"));
        param.put(DepartamentosControladorEnum.PARAM1.getValue(),
                        registro.getCampos().get(cCodigo));

        try {
            listaCiudad = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            "CIUDAD"));
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaPais() {

        try {
            listaPais = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DepartamentosControladorUrlEnum.URL3642
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     *
     * Carga la lista listaregion
     *
     */
    public void cargarListaregion() {
        try {
            listaregion = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DepartamentosControladorUrlEnum.URL138
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo de insercion del formulario Ciudad
     *
     */
    public void agregarRegistroSubCiudad() {
        try {
            if (validarVaciosCiudad()) {
                registroSub.getCampos().put("PAIS",
                                registro.getCampos().get("PAIS"));
                registroSub.getCampos().put("DEPARTAMENTO",
                                registro.getCampos().get(cCodigo));
                registroSub.getCampos().put("CREATED_BY",
                                SessionUtil.getUser().getCodigo());
                registroSub.getCampos().put("DATE_CREATED", new Date());

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.CIUDAD
                                                                .getCreateKey());
                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                                registroSub.getCampos());

                listaCiudad.load();
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_INGRESADO"));
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    /**
     * Metodo de edicion del formulario Ciudad
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubCiudad(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            if (validarVaciosCiudadE(reg)) {

                reg.getCampos().put("MODIFIED_BY",
                                SessionUtil.getUser().getCodigo());
                reg.getCampos().put("DATE_MODIFIED", new Date());
                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.CIUDAD
                                                                .getUpdateKey());
                requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                                reg.getCampos(), reg.getLlave());

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_MODIFICADO"));
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            listaCiudad.load();
        }
    }

    public boolean validarVaciosCiudadE(Registro reg) {
        if (reg.getCampos().isEmpty()) {
            return true;
        }
        else {
            for (Map.Entry<String, Object> entrySet : reg.getCampos()
                            .entrySet()) {
                String key = entrySet.getKey();
                Object value = entrySet.getValue();
                if (!validarCampos(key, value)) {
                    return false;
                }

            }
        }
        return true;
    }

    private boolean validarCampos(String key, Object value) {
        if (key.equals(cCodigo) && validarVac(value, "TB_TB15")) {
            return false;

        }
        else {
            if (validarVac(value, "TB_TB18")) {
                return false;
            }

        }
        return true;
    }

    private boolean validarVac(Object value, String msj) {

        if ((value == null) || ("").equals(value)) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(msj));
            return true;
        }
        return false;

    }

    public boolean validarVaciosCiudad() {
        // <CODIGO_DESARROLLADO>
        actualizarAntes();
        String codigo = registroSub.getCampos().get(cCodigo) == null ? ""
            : registroSub.getCampos().get(cCodigo).toString();
        String nombre = registroSub.getCampos().get("NOMBRE") == null ? ""
            : registroSub.getCampos().get("NOMBRE").toString();
        // </CODIGO_DESARROLLADO>
        if (codigo.isEmpty()) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB15"));
            return false;
        }

        if (nombre.isEmpty()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB18"));
            return false;
        }
        return true;
    }

    /**
     * Metodo de eliminacion del formulario Ciudad
     *
     *
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubCiudad(Registro reg) {
        try {

            reg.getCampos().remove("RNUM");
            reg.getCampos().remove("RID");

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(GenericUrlEnum.CIUDAD
                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(),
                            reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            listaCiudad.load();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Ciudad
     *
     */
    public void cancelarEdicionCiudad() {
        cargarListaCiudad();
    }

    public void oprimirPais(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        SessionUtil.cargarModalDatos(Integer.toString(
                        GeneralCodigoFormaEnum.PAISES_CONTROLADOR.getCodigo()),
                        modulo);

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTexto11() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void retornarFormularioPais(SelectEvent event) {
        // <CODIGO_DESARROLLADO>
        cargarListaPais();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR71-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore formularioAbrir 1, Me.Name log
         * "Ingres� a Datos Basicos, Division Politica, Departamentos"
         * End Sub
         */
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
        // </CODIGO_DESARROLLADO>
        return true;
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

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>

    public List<Registro> getListaPais() {
        return listaPais;
    }

    public void setListaPais(List<Registro> listaPais) {
        this.listaPais = listaPais;
    }

    /**
     * Retorna la lista listaregion
     *
     * @return listaregion
     */
    public List<Registro> getListaregion() {
        return listaregion;
    }

    /**
     * Asigna la lista listaregion
     *
     * @param listaregion
     * Variable a asignar en listaregion
     */
    public void setListaregion(List<Registro> listaregion) {
        this.listaregion = listaregion;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaCiudad
     *
     * @return listaCiudad
     */
    public RegistroDataModelImpl getListaCiudad() {
        return listaCiudad;
    }

    /**
     * Asigna la lista listaCiudad
     *
     * @param listaCiudad
     * Variable a asignar en listaCiudad
     */
    public void setListaCiudad(RegistroDataModelImpl listaCiudad) {
        this.listaCiudad = listaCiudad;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSub
     *
     * @return registroSub
     */
    public Registro getRegistroSub() {
        return registroSub;
    }

    /**
     * Asigna el objeto registroSub
     *
     * @param registroSub
     * Variable a asignar en registroSub
     */
    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

}
