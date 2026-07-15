package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilidad.enums.InversionrfsControladorEnum;
import com.sysman.contabilidad.enums.InversionrfsControladorUrlEnum;
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
import com.sysman.services.RegistroDataModelImpl;

import java.util.HashMap;
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
 * @author otorres
 * @version 1, 12/05/2016
 *
 * @author ybecerra
 * @version 2, 10/04/2017 Revision Sonar y Refactoring
 * 
 * @author jreina
 * @version 3, 13/06/2017 Cambio c�digo formulario y actualizaci�n
 * de ConnectorPool
 */
@ManagedBean
@ViewScoped

public class InversionrfsControlador extends BeanBaseDatosAcmeImpl {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String nombreR;
    private boolean bloqueadoCodigo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    private List<Registro> listatipo;
    private List<Registro> listamoneda;

    private List<Registro> listabanco;

    private List<Registro> listaentidadIntermediaria;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaOrigenRecursos;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    public InversionrfsControlador() {
        super();
        compania = SessionUtil.getCompania();

        try {
            numFormulario = GeneralCodigoFormaEnum.INVERSIONRFS_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                rid = (HashMap<String, Object>) parametrosEntrada.get("rid");
                SessionUtil.removeSessionVar("rid");
            }
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(InversionrfsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaOrigenRecursos();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>

        cargarListatipo();
        cargarListamoneda();
        cargarListabanco();
        cargarListaentidadIntermediaria();
        // </CARGAR_LISTA>
    }

    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.INVERSIONRF;
        buscarLlave();
        asignarOrigenDatos();

    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaOrigenRecursos() {

        try {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            InversionrfsControladorUrlEnum.URL3905
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaOrigenRecursos = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            "FUENTE_RECURSOS"));
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListatipo() {
        try {
            listatipo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InversionrfsControladorUrlEnum.URL4688
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListamoneda() {
        try {
            listamoneda = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InversionrfsControladorUrlEnum.URL4926
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
     * Carga la lista listabanco
     *
     */
    public void cargarListabanco() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listabanco = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InversionrfsControladorUrlEnum.URL5207
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
     * Carga la lista listaentidadIntermediaria
     *
     */
    public void cargarListaentidadIntermediaria() {
        try {
            listaentidadIntermediaria = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InversionrfsControladorUrlEnum.URL226
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaOrigenRecursos(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("ORIGENRECURSOS",
                        registroAux.getCampos().get("CODIGO"));
        registro.getCampos().put("ANO", registroAux.getCampos().get("ANO"));
        nombreR = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()) == null ? ""
                            : registroAux.getCampos().get("NOMBRE").toString();

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    public void oprimirEtiqueta34() {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", css);
        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(parametros);
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FUENTERECURSOS_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        
        
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR700-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore formularioAbrir 1, Me.Name End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {

        // <CODIGO_DESARROLLADO>
        if (css != null) {
            Map<String, Object> fields = new TreeMap<>();
            fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            fields.put(GeneralParameterEnum.ANO.getName(), registro.getCampos()
                            .get(GeneralParameterEnum.ANO.getName()));
            fields.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos()
                                            .get(InversionrfsControladorEnum.PARAM0
                                                            .getValue()));
            try {
                nombreR = listaOrigenRecursos.getRegistroUnico(fields)
                                .getCampos().get("NOMBRE").toString();
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            bloqueadoCodigo = true;

        }
        else {

            nombreR = " ";

            registro.getCampos().put("VALORNOMINAL", "0");
            registro.getCampos().put("PLAZO", "0");
            registro.getCampos().put("TASA", "0");
            registro.getCampos().put("ANTICIPADO", "0");
            registro.getCampos().put("VENCIDO", "0");
            registro.getCampos().put("RENDIMIENTOSFINANCIEROS", "0");
            registro.getCampos().put("TASA_COMPRA", "0");
            registro.getCampos().put("SALDOFINAL", "0");
            registro.getCampos().put("VALOR_VENTA", "0");
            registro.getCampos().put("TASA_VENTA", "0");
            bloqueadoCodigo = false;

        }

        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().remove("NOMBRERECURSO");
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
        if (accion.equals(ACCION_MODIFICAR)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
            registro.getCampos().remove(
                            InversionrfsControladorEnum.PARAM1.getValue());
            registro.getCampos().remove(
                            InversionrfsControladorEnum.PARAM2.getValue());

        }
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
    public String getNombreR() {
        return nombreR;
    }

    public void setNombreR(String nombreR) {
        this.nombreR = nombreR;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>

    public boolean isBloqueadoCodigo() {
        return bloqueadoCodigo;
    }

    public void setBloqueadoCodigo(boolean bloqueadoCodigo) {
        this.bloqueadoCodigo = bloqueadoCodigo;
    }

    /**
     * Retorna la lista listabanco
     *
     * @return listabanco
     */
    public List<Registro> getListabanco() {
        return listabanco;
    }

    /**
     * Asigna la lista listabanco
     *
     * @param listabanco
     * Variable a asignar en listabanco
     */
    public void setListabanco(List<Registro> listabanco) {
        this.listabanco = listabanco;
    }

    /**
     * Retorna la lista listaentidadIntermediaria
     *
     * @return listaentidadIntermediaria
     */
    public List<Registro> getListaentidadIntermediaria() {
        return listaentidadIntermediaria;
    }

    /**
     * Asigna la lista listaentidadIntermediaria
     *
     * @param listaentidadIntermediaria
     * Variable a asignar en listaentidadIntermediaria
     */
    public void setListaentidadIntermediaria(
        List<Registro> listaentidadIntermediaria) {
        this.listaentidadIntermediaria = listaentidadIntermediaria;
    }

    public List<Registro> getListatipo() {
        return listatipo;
    }

    public void setListatipo(List<Registro> listatipo) {
        this.listatipo = listatipo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public List<Registro> getListamoneda() {
        return listamoneda;
    }

    public void setListamoneda(List<Registro> listamoneda) {
        this.listamoneda = listamoneda;
    }

    public RegistroDataModelImpl getListaOrigenRecursos() {
        return listaOrigenRecursos;
    }

    public void setListaOrigenRecursos(
        RegistroDataModelImpl listaOrigenRecursos) {
        this.listaOrigenRecursos = listaOrigenRecursos;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
