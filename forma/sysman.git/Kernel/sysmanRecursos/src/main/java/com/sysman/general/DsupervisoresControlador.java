package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.general.enums.DsupervisoresControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.FormContinuoService;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

/**
 *
 * @author jrodriguezr
 * @version 1, 02/12/2015
 * @modified jguerrero
 * @version 2. 05/04/2017 Se realizo el refactory. Ademas, se hicieron
 * las respectivas Correcciones del sonar.
 *
 * @modified sdaza
 * @version 3. 02/06/2017 Se modifica la asigancion para vobo con el
 * fin de asegurar los permisos de creacion, edicion o eliminación
 * segun de la aplicacion de invocacion del formulario
 */
@ManagedBean
@ViewScoped

public class DsupervisoresControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private String numero;
    private String fecha;
    private RegistroDataModelImpl listaCedula;
    private RegistroDataModelImpl listaCedulaE;
    private String auxiliar;
    private JSONObject llaves;
    /**
     * Indicador que gestiona el bloqueo de los controles en la forma
     */
    private boolean vobo;
    private String nombreAux;
    private String cargoAux;
    private String profesionAux;
    private static final String CAMPO_NOMBRE = "NOMBRE";
    private static final String CAMPO_CARGO = "CARGO";
    private static final String CAMPO_PROFESION = "PROFESION";

    private String claseOrdenGetFlash;
    private String numeroContratoGetFlash;
    private String numeroEstudioGetFlash;

    private final String numeroEstudioCons;
    private final String claseOrdenCons;
    private final String numeroContrato;
	private Map<String, Object> parametroswf;
	private String modulo;

    /**
     * Creates a new instance of DsupervisoresControlador
     */
    public DsupervisoresControlador() {
        super();
        numeroEstudioCons = "NUMERO_ESTUDIO";
        claseOrdenCons = "CLASEORDEN";
        numeroContrato = "NUMEROCONTRATO";
        numFormulario = GeneralCodigoFormaEnum.DSUPERVISORES_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        try {
        parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
    	if(parametroswf != null) {
    		SessionUtil.setSessionVar("modulo", "9");
    	}
    	modulo = SessionUtil.getModulo();
        
            cargarFlash();
            validarPermisos();
        }
        catch (SysmanException | NamingException ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    private void cargarFlash() {
        Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
        if (parametrosEntrada != null) {
            tabla = parametrosEntrada.get("tabla").toString();
            try {
                this.llaves = new JSONObject(
                                parametrosEntrada.get("llaves").toString());
            }
            catch (JSONException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

            numero = SysmanFunciones.nvl(parametrosEntrada.get("numero"), "")
                            .toString();

            /*
             * Antes estaba llamando al parametro: vobo ¡Este
             * parametro no se envia desde Pcontratros!
             */

            if (parametrosEntrada.get("vobo") != null) {
                vobo = Boolean.parseBoolean(
                                parametrosEntrada.get("vobo").toString());
            }
        }
    }

    @PostConstruct
    public void inicializar() {
        try {
            fecha = SysmanFunciones.convertirAFechaCadena(
                            new Date(),
                            "dd/MM/yyyy");
        }
        catch (ParseException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                + ex.getMessage());
            Logger.getLogger(DsupervisoresControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

        if ("19".equals(SessionUtil.getModulo())) {
            enumBase = GenericUrlEnum.ES_SUPERVISORES;

        }
        else {
            enumBase = GenericUrlEnum.SUPERVISORES;

        }

        reasignarOrigen();
        buscarLlave();
        registro = new Registro(new HashMap<String, Object>());
        cargarlistaCedula();
        cargarlistaCedulaE();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        try {

            if ("19".equals(SessionUtil.getModulo())) {
                numeroEstudioGetFlash = llaves.get(numeroEstudioCons)
                                .toString();
                numeroEstudioGetFlash = numeroEstudioGetFlash.replace("'", "");
                buscarUrls();
                parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                parametrosListado.put("NUMEROESTUDIO", numeroEstudioGetFlash);
            }
            else {

                claseOrdenGetFlash = llaves.get(claseOrdenCons).toString();

                numeroContratoGetFlash = llaves.get(numeroContrato)
                                .toString();

                claseOrdenGetFlash = claseOrdenGetFlash.replace("'", "");
                numeroContratoGetFlash = numeroContratoGetFlash.replace("'",
                                "");
                buscarUrls();
                parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                parametrosListado.put(claseOrdenCons, claseOrdenGetFlash);
                parametrosListado.put(numeroContrato, numeroContratoGetFlash);

            }

        }
        catch (JSONException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public FormContinuoService getService() {
        return service;
    }

    @Override
    public void setService(FormContinuoService service) {
        this.service = service;
    }

    public String getNumeroOrden() {
        return numero;
    }

    public void setNumeroOrden(String numeroOrden) {
        this.numero = numeroOrden;
    }

    public RegistroDataModelImpl getlistaCedula() {
        return listaCedula;
    }

    public void setlistaCedula(RegistroDataModelImpl listaCedula) {
        this.listaCedula = listaCedula;
    }

    public RegistroDataModelImpl getlistaCedulaE() {
        return listaCedulaE;
    }

    public void setlistaCedulaE(RegistroDataModelImpl listaCedulaE) {
        this.listaCedulaE = listaCedulaE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public JSONObject getLlaves() {
        return llaves;
    }

    public void setLlaves(JSONObject llaves) {
        this.llaves = llaves;
    }

    public void cargarlistaCedula() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DsupervisoresControladorUrlEnum.URL7544
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCedula = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");

    }

    public void cargarlistaCedulaE() {
        listaCedulaE = listaCedula;
    }

    public void cambiarCedulaC(int rowNum) {
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        CAMPO_NOMBRE,
                        nombreAux);
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        CAMPO_CARGO,
                        cargoAux);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(CAMPO_PROFESION, profesionAux);
    }

    public void seleccionarFilaCedula(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put("CEDULA", registroAux.getCampos().get("NIT"));
        registro.getCampos().put(CAMPO_NOMBRE,
                        registroAux.getCampos().get(CAMPO_NOMBRE));
        registro.getCampos().put("SUCURSAL",
                        registroAux.getCampos().get("SUCURSAL"));
        registro.getCampos().put(CAMPO_CARGO,
                        registroAux.getCampos().get(CAMPO_CARGO));
        registro.getCampos().put(CAMPO_PROFESION,
                        registroAux.getCampos().get(CAMPO_PROFESION));
    }

    public void seleccionarFilaCedulaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), "")
                        .toString();
        nombreAux = SysmanFunciones
                        .nvl(registroAux.getCampos().get(CAMPO_NOMBRE), "")
                        .toString();
        cargoAux = SysmanFunciones
                        .nvl(registroAux.getCampos().get(CAMPO_CARGO), "")
                        .toString();
        profesionAux = SysmanFunciones
                        .nvl(registroAux.getCampos().get(CAMPO_PROFESION), "")
                        .toString();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // <CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        if (!"19".equals(SessionUtil.getModulo())) {
            registro.getCampos().put(claseOrdenCons, claseOrdenGetFlash);
            registro.getCampos().put(numeroContrato, numeroContratoGetFlash);

        }
        else {

            registro.getCampos().put(numeroEstudioCons, numeroEstudioGetFlash);
        }

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
        registro.getCampos().remove(CAMPO_CARGO);
        registro.getCampos().remove(CAMPO_PROFESION);
        registro.getCampos().remove(CAMPO_NOMBRE);

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

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>

        if ("19".equals(SessionUtil.getModulo())) {
            registro.getCampos().remove(numeroEstudioCons);
            registro.getCampos()
                            .remove(GeneralParameterEnum.SUCURSAL.getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
        }
        else {
            registro.getCampos().remove("CARGO");
            registro.getCampos().remove("PROFESION");
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());

            registro.getCampos().remove(numeroContrato);

            registro.getCampos()
                            .remove(GeneralParameterEnum.CLASEORDEN.getName());

        }

        // <CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // <CODIGO_DESARROLLADO>
    }

    public boolean isVobo() {
        return vobo;
    }

    public void setVobo(boolean vobo) {
        this.vobo = vobo;
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        // <CODIGO_DESARROLLADO>
        // <CODIGO_DESARROLLADO>
    }
}
