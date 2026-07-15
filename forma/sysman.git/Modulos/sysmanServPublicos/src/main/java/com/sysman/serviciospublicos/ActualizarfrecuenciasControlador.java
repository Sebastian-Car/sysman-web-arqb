package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCeroRemote;
import com.sysman.serviciospublicos.enums.ActualizarfrecuenciasControladorEnum;
import com.sysman.serviciospublicos.enums.ActualizarfrecuenciasControladorUrlEnum;
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

import org.primefaces.event.SelectEvent;

/**
 *
 * @author jguerrero
 * @version 1, 23/08/2016
 * 
 * @version 2.0, 15/05/2017, pespitia:<br>
 * Refactoring.<br>
 * Manejo de EJBs.
 * 
 */
@ManagedBean
@ViewScoped
public class ActualizarfrecuenciasControlador extends BeanBaseModal {
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del usuario que
     * abre el formulario.
     */
    private final String usuario;

    /**
     * Constante a nivel de clase que aloja el nombre del enumerado
     * <code>GeneralParameterEnum.CODIGORUTA</code>
     */
    private final String cod;
    // <DECLARAR_ATRIBUTOS>
    private int ciclo;
    private String actividad;
    private String frecuencia;
    private String codigoInicial;
    private String codigoFinal;
    private boolean dialogoVisible;
    private String textDialog;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaCiclo;
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigofinal;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    // <DECLARAR EJBs>
    @EJB
    private EjbServiciosPublicosCeroRemote ejbServiciosPublicosCero;
    // <DECLARAR EJBs>

    /**
     * Creates a new instance of ActualizarfrecuenciasControlador
     */
    public ActualizarfrecuenciasControlador() {
        super();

        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser().getCodigo();

        cod = GeneralParameterEnum.CODIGORUTA.getName();

        try {
            numFormulario = GeneralCodigoFormaEnum.ACTUALIZARFRECUENCIAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(ActualizarfrecuenciasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaCiclo();
        cargarListaCodigoInicial();
        cargarListaCodigofinal();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCiclo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ActualizarfrecuenciasControladorUrlEnum.URL3003
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActualizarfrecuenciasControladorUrlEnum.URL3303
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cod);
    }

    public void cargarListaCodigofinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActualizarfrecuenciasControladorUrlEnum.URL3937
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(ActualizarfrecuenciasControladorEnum.CODIGOINICIAL.getValue(),
                        codigoInicial);

        listaCodigofinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cod);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        dialogoVisible = true;
        textDialog = SysmanFunciones.concatenar(idioma.getString("TB_TB1303"),
                        " ", frecuencia, "<br> ",
                        idioma.getString("TB_TB1304"));
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarCiclo() {
        // <CODIGO_DESARROLLADO>
        codigoInicial = null;
        codigoFinal = null;

        cargarListaCodigoInicial();
        cargarListaCodigofinal();
        // </CODIGO_DESARROLLADO>
    }

    public void aceptarconfirmarActualizarFrecuencias() {
        // <CODIGO_DESARROLLADO>
        actualizar();

        dialogoVisible = false;
        ciclo = 0;
        codigoInicial = null;
        codigoFinal = null;

        actividad = null;
        frecuencia = null;

        listaCodigofinal = listaCodigoInicial = null;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cod), "").toString();

        codigoFinal = null;

        cargarListaCodigofinal();
    }

    public void seleccionarFilaCodigofinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones.nvl(registroAux.getCampos().get(cod), "")
                        .toString();
    }

    // <SET_GET_ATRIBUTOS>
    public int getCiclo() {
        return ciclo;
    }

    public void setCiclo(int ciclo) {
        this.ciclo = ciclo;
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }

    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    public RegistroDataModelImpl getListaCodigofinal() {
        return listaCodigofinal;
    }

    public void setListaCodigofinal(RegistroDataModelImpl listaCodigofinal) {
        this.listaCodigofinal = listaCodigofinal;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public String getCodigoInicial() {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal() {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    public boolean isDialogoVisible() {
        return dialogoVisible;
    }

    public void setDialogoVisible(boolean dialogoVisible) {
        this.dialogoVisible = dialogoVisible;
    }

    public String getTextDialog() {
        return textDialog;
    }

    public void setTextDialog(String textDialog) {
        this.textDialog = textDialog;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public void actualizar() {
        try {
            ejbServiciosPublicosCero.actualizarFrecuencia(compania, ciclo,
                            codigoInicial, codigoFinal, actividad,
                            new BigDecimal(frecuencia),
                            usuario);

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB837"));

        }
        catch (SystemException e) {
            Logger.getLogger(ActualizarfrecuenciasControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }
}
