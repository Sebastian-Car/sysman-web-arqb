package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.serviciospublicos.enums.ClaseproblemasControladorEnum;
import com.sysman.serviciospublicos.enums.ClaseproblemasControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author jguerrero
 * @version 1, 02/08/2016
 * 
 * @author eamaya
 * @version 2, 17/05/2017 Proceso de Refactoring, Correcciones
 * SonarLint y Manejo de EJBs
 * 
 */
@ManagedBean
@ViewScoped

public class ClaseproblemasControlador extends BeanBaseDatosAcmeImpl {
    private final String compania;
    private final String claseProblema;
    private final String activaHabitado;
    private final String activaDesHabitado;
    
    // <DECLARAR_ATRIBUTOS>

    private boolean cargaDeshabitado;
    private boolean cargaHabitado;
    private boolean tipoFormulario;
    private boolean cargarCodEquivSuiPyre;
    private boolean cargarSuiEquivReclamaciones;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaSERVICIOEQUIVALENTE;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    private List<Registro> listaSubproblema;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    private Registro registroSub;

    @EJB
    private EjbSysmanUtilRemote ejbParametro;

    // </DECLARAR_ADICIONALES>
    public ClaseproblemasControlador() {
        super();
        compania = SessionUtil.getCompania();
        claseProblema = "CLASEPROBLEMA";
        activaHabitado = "ACTIVA_HABITADO";
        activaDesHabitado = "ACTIVA_DESHABITADO";
        try {
            numFormulario = GeneralCodigoFormaEnum.CLASEPROBLEMAS_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSub = new Registro(new HashMap<String, Object>());
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(ClaseproblemasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaSERVICIOEQUIVALENTE();
        // </CARGAR_LISTA>
    }

    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaSubproblema();
        // </CARGAR_LISTAS_SUBFORM>
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaSubproblema = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    @PostConstruct
    public void init() {
        enumBase = GenericUrlEnum.SP_CLASEPROBLEMA;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    public void cargarListaSubproblema() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            param.put(ClaseproblemasControladorEnum.PARAM0.getValue(),
                            registro.getCampos().get(claseProblema)
                                            .toString());

            listaSubproblema = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ClaseproblemasControladorUrlEnum.URL3762
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            "SP_PROBLEMA"));

        }
  
        catch (SysmanException | SystemException e) {      
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaSERVICIOEQUIVALENTE() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaSERVICIOEQUIVALENTE = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ClaseproblemasControladorUrlEnum.URL4574
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    public void cambiarACTIVADESHABITADO() {

        // <CODIGO_DESARROLLADO>

        if ((boolean) registroSub.getCampos().get(activaDesHabitado)
            && (boolean) registroSub.getCampos().get(activaHabitado)) {
            registroSub.getCampos().put(activaDesHabitado, false);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1052"));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarACTIVAHABITADO() {
        // <CODIGO_DESARROLLADO>

        if ((boolean) registroSub.getCampos().get(activaHabitado)
            && (boolean) registroSub.getCampos().get(activaDesHabitado)) {
            registroSub.getCampos().put(activaHabitado, false);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1053"));
        }
        // </CODIGO_DESARROLLADO>
    }
    public void agregarRegistroSubSubproblema() {
        try {

            if ((tipoFormulario)
                && !validacionCodigo(registroSub.getCampos()
                                .get("COD_EQUIVALENTE_SUI_PYRE").toString())) {

                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1032"));
                return;
            }

            registroSub.getCampos().put("COMPANIA", compania);
            registroSub.getCampos().put(claseProblema,
                            registro.getCampos().get(claseProblema));

            registroSub.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            registroSub.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_PROBLEMA
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());

            cargarListaSubproblema();

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
        }
        catch (SystemException ex) {
            Logger.getLogger(ClaseproblemasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    public void editarRegSubSubproblema(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {

            validacionProblemas(reg);

            if (!validarVacios(reg)) {
                return;
            }

            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_PROBLEMA
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));

        }
        catch (SystemException ex) {
            Logger.getLogger(ClaseproblemasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaSubproblema();
        }
    }

    public void cancelarEdicionSubproblema() {
        cargarListaSubproblema();
    }

    private boolean validarVacios(Registro reg) {
        if ((tipoFormulario)
            && !validacionCodigo(reg.getCampos()
                            .get("COD_EQUIVALENTE_SUI_PYRE").toString())) {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1032"));
            return false;
        }

        if ((reg.getCampos().get(GeneralParameterEnum.CODIGO.getName()) == null)
            || "".equals(reg.getCampos()
                            .get(GeneralParameterEnum.CODIGO.getName()))) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
            return false;
        }
        return true;
    }

    private void validacionProblemas(Registro reg) {
        if ((boolean) reg.getCampos().get(activaDesHabitado)
            && (boolean) reg.getCampos().get(activaHabitado)) {
            reg.getCampos().put(activaDesHabitado, false);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1052"));
        }

        if ((boolean) reg.getCampos().get(activaHabitado)
            && (boolean) reg.getCampos().get(activaDesHabitado)) {
            reg.getCampos().put(activaHabitado, false);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1053"));
        }

    }

    public void eliminarRegSubSubproblema(Registro reg) {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            param.put(ClaseproblemasControladorEnum.PARAM0.getValue(),
                            registro.getCampos().get(claseProblema)
                                            .toString());

            param.put(GeneralParameterEnum.CODIGO.getName(),
                            reg.getCampos().get(GeneralParameterEnum.CODIGO
                                            .getName()).toString());

            Registro eliminarRegistro = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ClaseproblemasControladorUrlEnum.URL6610
                                                                            .getValue())
                                            .getUrl(), param));

            if (!"0".equals(eliminarRegistro.getCampos().get("CANTIDAD")
                            .toString())) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1033"));
                return;
            }

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_PROBLEMA
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(),
                            reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaSubproblema();
        }
        catch (SystemException ex) {
            Logger.getLogger(ClaseproblemasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void onCancelSubproblema() {
        cargarListaSubproblema();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            if ("SI".equals(SysmanFunciones
                            .nvl(ejbParametro.consultarParametro(compania,
                                            "PQR DESPUES DE RES 20101300048765 DEL 14-12-2010",
                                            SessionUtil.getModulo(), new Date(),
                                            false), "NO"))) {
                tipoFormulario = true;
                cargarCodEquivSuiPyre = true;
                cargarSuiEquivReclamaciones = false;
            }
            else {
                tipoFormulario = false;
                cargarCodEquivSuiPyre = false;
                cargarSuiEquivReclamaciones = true;
            }
        }
        catch (SystemException e) {
            Logger.getLogger(ClaseproblemasControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        if (css != null) {
            try {
                if (("AFR".equals(registro.getCampos().get(claseProblema))
                    && "SI".equals(SysmanFunciones
                                    .nvl(ejbParametro.consultarParametro(
                                                    compania,
                                                    "ACTIVA DESHABITADOS DESDE PROBLEMAS AFORO",
                                                    SessionUtil.getModulo(),
                                                    new Date(), false), "NO")))
                    || "SI".equals(SysmanFunciones
                                    .nvl(ejbParametro.consultarParametro(
                                                    compania,
                                                    "FIMM - FACTURA ASEO EN SITIO",
                                                    SessionUtil.getModulo(),
                                                    new Date(),
                                                    false), "NO"))) {
                    cargaDeshabitado = true;
                }
                else {

                    cargaDeshabitado = false;

                }

                if (("AFR".equals(registro.getCampos().get(claseProblema))
                    && "SI".equals(SysmanFunciones
                                    .nvl(ejbParametro.consultarParametro(
                                                    compania,
                                                    "ACTIVA HABITADOS DESDE PROBLEMAS AFORO",
                                                    SessionUtil.getModulo(),
                                                    new Date(), false), "NO")))
                    || "SI".equals(SysmanFunciones
                                    .nvl(ejbParametro.consultarParametro(
                                                    compania,
                                                    "FIMM - FACTURA ASEO EN SITIO",
                                                    SessionUtil.getModulo(),
                                                    new Date(), false),
                                                    "NO"))) {
                    cargaHabitado = true;
                }
                else {
                    cargaHabitado = false;
                }

            }
            catch (SystemException e) {
                Logger.getLogger(ClaseproblemasControlador.class.getName())
                                .log(Level.SEVERE, null, e);

                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);

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

    public boolean isCargaDeshabitado() {
        return cargaDeshabitado;
    }

    public boolean isCargarSuiEquivReclamaciones() {
        return cargarSuiEquivReclamaciones;
    }

    public void setCargarSuiEquivReclamaciones(
        boolean cargarSuiEquivReclamaciones) {
        this.cargarSuiEquivReclamaciones = cargarSuiEquivReclamaciones;
    }

    public boolean isCargarCodEquivSuiPyre() {
        return cargarCodEquivSuiPyre;
    }

    public void setCargarCodEquivSuiPyre(boolean cargarCodEquivSuiPyre) {
        this.cargarCodEquivSuiPyre = cargarCodEquivSuiPyre;
    }

    public boolean isTipoFormulario() {
        return tipoFormulario;
    }

    public void setTipoFormulario(boolean tipoFormulario) {
        this.tipoFormulario = tipoFormulario;
    }

    public void setCargaDeshabitado(boolean cargaDeshabitado) {
        this.cargaDeshabitado = cargaDeshabitado;
    }

    public boolean isCargaHabitado() {
        return cargaHabitado;
    }

    public void setCargaHabitado(boolean cargaHabitado) {
        this.cargaHabitado = cargaHabitado;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaSERVICIOEQUIVALENTE() {
        return listaSERVICIOEQUIVALENTE;
    }

    public void setListaSERVICIOEQUIVALENTE(
        List<Registro> listaSERVICIOEQUIVALENTE) {
        this.listaSERVICIOEQUIVALENTE = listaSERVICIOEQUIVALENTE;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    public List<Registro> getListaSubproblema() {
        return listaSubproblema;
    }

    public void setListaSubproblema(List<Registro> listaSubproblema) {
        this.listaSubproblema = listaSubproblema;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    public Registro getRegistroSub() {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }
    // </SET_GET_ADICIONALES>

    public boolean validacionCodigo(String codigo) {

        int codigoInt = Integer.parseInt(codigo);
        if (((codigoInt > 101) && (codigoInt < 136))
            || ((codigoInt > 201) && (codigoInt < 217)) || (codigoInt == 999)) {
            return true;
        }

        return false;

    }
}
