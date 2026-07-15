package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.ejb.EjbPresupuestoCeroRemote;
import com.sysman.presupuesto.enums.ProyectarpptoControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author NGOMEZ
 * @version 1, 14/06/2016
 * 
 * @author eamaya
 * @version 2, 19/04/2017 Proceso de Rafactoring
 * 
 * @author ybecerra
 * @version 3, 13/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 */
@ManagedBean
@ViewScoped

public class ProyectarpptoControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String opcion;
    private String sinApropiaciones;
    private String regalias;
    private String soloRegalias;
    private String anioFuente;
    private String anioDestino;
    private String incremento;
    private boolean regaliasVisible;
    private boolean incrementoVisible;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAnoFuente;
    private List<Registro> listaAnoDestino;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB

    private EjbPresupuestoCeroRemote ejbFuncion;

    /**
     * Creates a new instance of ProyectarpptoControlador
     */
    public ProyectarpptoControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.PROYECTARPPTO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(ProyectarpptoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaAnoFuente();
        cargarListaAnoDestino();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        opcion = "1";
        incrementoVisible = true;
        regaliasVisible = false;
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAnoFuente() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaAnoFuente = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ProyectarpptoControladorUrlEnum.URL2925
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAnoDestino() {
        listaAnoDestino = listaAnoFuente;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    private boolean verificarRegistro() {
        Registro regAux;
        try {
            if ("false".equals(soloRegalias)) {

                Map<String, Object> param = new TreeMap<>();

                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.ANO.getName(), anioDestino);

                regAux = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                ProyectarpptoControladorUrlEnum.URL6969
                                                                                .getValue())
                                                .getUrl(), param));

                /*- El plan presupuesta ya esta definido para el anioDestino  */
                if (!"0".equals(regAux.getCampos()
                                .get(GeneralParameterEnum.CUENTA.getName())
                                .toString())) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB253"));
                    return false;
                }
            }

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anioFuente);

            regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ProyectarpptoControladorUrlEnum.URL6969
                                                                            .getValue())
                                            .getUrl(), param));

            /*- Si no se ha definido el plan presupuestal en el anioFuente */
            if ("0".equals(regAux.getCampos()
                            .get(GeneralParameterEnum.CUENTA.getName())
                            .toString())) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB258"));
                return false;
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;

    }

    public void oprimirIniciar() {
        // <CODIGO_DESARROLLADO>

        if ("1".equals(opcion)
            && (SysmanFunciones.validarVariableVacio(incremento))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB251"));
            return;
        }

        if (!verificarRegistro()) {
            return;
        }

        /* Si la fuente recurso no existe. */
        if (!verificarFuentes()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2718").replace(
                            "#ANIODESTINO#", anioDestino));
            return;
        }

        try {
            boolean funcion = ejbFuncion.proyectarPresupuestoSiguienteVigencia(
                            compania,
                            Integer.parseInt(anioFuente),
                            Integer.parseInt(anioDestino),
                            BigDecimal.valueOf(Double.parseDouble(incremento)),
                            "true".equals(sinApropiaciones) ? true : false,
                            "true".equals(regalias) ? true : false,
                            "true".equals(soloRegalias) ? true : false);

            if (funcion) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_PROCESO_EJECUTADO"));
            }
        }
        catch (NumberFormatException | SystemException e) {
            Logger.getLogger(ProyectarpptoControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Verifica la existencia de la fuente de recurso para el
     * anioDestino.
     * 
     * @return true si existe.
     */
    public boolean verificarFuentes() {
        Registro registro = null;
        try {
            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.ANO.getName(), anioDestino);

            registro = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ProyectarpptoControladorUrlEnum.URL1666
                                                                            .getValue())
                                            .getUrl(), param));

        }
        catch (

        SystemException e)

        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return registro == null ? false
            : Integer.valueOf(
                            registro.getCampos().get("CANT").toString()) > 0
                                ? true : false;

    }

    // </CODIGO_DESARROLLADO>

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarSinApropiaciones() {
        // <CODIGO_DESARROLLADO>
        regaliasVisible = "true".equals(sinApropiaciones);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarOpcion() {
        // <CODIGO_DESARROLLADO>
        incrementoVisible = "1".equals(opcion);
        incremento = "0.0";
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public String getSinApropiaciones() {
        return sinApropiaciones;
    }

    public void setSinApropiaciones(String sinApropiaciones) {
        this.sinApropiaciones = sinApropiaciones;
    }

    public String getRegalias() {
        return regalias;
    }

    public void setRegalias(String regalias) {
        this.regalias = regalias;
    }

    public String getSoloRegalias() {
        return soloRegalias;
    }

    public void setSoloRegalias(String soloRegalias) {
        this.soloRegalias = soloRegalias;
    }

    public String getAnioFuente() {
        return anioFuente;
    }

    public void setAnioFuente(String anioFuente) {
        this.anioFuente = anioFuente;
    }

    public String getAnioDestino() {
        return anioDestino;
    }

    public void setAnioDestino(String anioDestino) {
        this.anioDestino = anioDestino;
    }

    public String getIncremento() {
        return incremento;
    }

    public void setIncremento(String incremento) {
        this.incremento = incremento;
    }

    public boolean isRegaliasVisible() {
        return regaliasVisible;
    }

    public void setRegaliasVisible(boolean regaliasVisible) {
        this.regaliasVisible = regaliasVisible;
    }

    public boolean isIncrementoVisible() {
        return incrementoVisible;
    }

    public void setIncrementoVisible(boolean incrementoVisible) {
        this.incrementoVisible = incrementoVisible;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaAnoFuente() {
        return listaAnoFuente;
    }

    public void setListaAnoFuente(List<Registro> listaAnoFuente) {
        this.listaAnoFuente = listaAnoFuente;
    }

    public List<Registro> getListaAnoDestino() {
        return listaAnoDestino;
    }

    public void setListaAnoDestino(List<Registro> listaAnoDestino) {
        this.listaAnoDestino = listaAnoDestino;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
