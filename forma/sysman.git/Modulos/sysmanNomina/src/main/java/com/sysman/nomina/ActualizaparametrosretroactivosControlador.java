package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.ActualizaparametrosretroactivosControladorEnum;
import com.sysman.nomina.enums.ActualizaparametrosretroactivosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author jgomez
 * @version 1, 05/08/2015
 * 
 * @version 2, 18/08/2017, <strong>pespitia</strong>:<br>
 * Se reemplazo el numero del formulario por enumerado.<br>
 * Refactoring de sentencias SQL.<br>
 * Se aplicaron las recomendaciones de SonarLint.
 */
@ManagedBean
@ViewScoped
public class ActualizaparametrosretroactivosControlador
                extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String moduloNomina;

    /**
     * Constante a nivel de clase que aloja la cadena
     * <code>VALOR</code>.
     */
    private final String cValor;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>MODULO</code>
     */
    private final String cModulo;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>NOMBRE</code>
     */
    private final String cNombre;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>NOMBRETIPOPARAMETRO</code>
     */
    private final String cNombreTipoParametro;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>TIPOPARAMETRO</code>.
     */
    private final String cTipoParametro;

    // <DECLARAR_LISTAS>
    /**
     * Lista que contiene los items del combo:
     * <code>Tipo Parametro</code>.
     */
    private List<Registro> listaTipoParametro;
    // </DECLARAR_LISTAS>

    /**
     * Creates a new instance of
     * ActualizaparametrosretroactivosControlador
     */
    public ActualizaparametrosretroactivosControlador() {
        super();

        compania = SessionUtil.getCompania();
        moduloNomina = SessionUtil.getModulo();

        cValor = GeneralParameterEnum.VALOR.getName();
        cCompania = GeneralParameterEnum.COMPANIA.getName();
        cNombre = GeneralParameterEnum.NOMBRE.getName();

        cNombreTipoParametro = ActualizaparametrosretroactivosControladorEnum.NOMBRETIPOPARAMETRO
                        .getValue();

        cModulo = ActualizaparametrosretroactivosControladorEnum.MODULO
                        .getValue();

        cTipoParametro = ActualizaparametrosretroactivosControladorEnum.TIPOPARAMETRO
                        .getValue();

        try {
            // 105
            numFormulario = GeneralCodigoFormaEnum.ACTUALIZAPARAMETROSRETROACTIVOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(ActualizaparametrosretroactivosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = GenericUrlEnum.PARAMETROS.getTable();
        registro = new Registro(new HashMap<String, Object>());

        buscarLlave();
        // <CARGAR_LISTA>
        cargarListaTipoParametro();
        // </CARGAR_LISTA>
        reasignarOrigen();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        urlListado = UrlServiceUtil
                        .getUrlBeanById(ActualizaparametrosretroactivosControladorUrlEnum.URL0001
                                        .getValue());

        urlActualizacion = UrlServiceUtil
                        .getUrlBeanById(ActualizaparametrosretroactivosControladorUrlEnum.URL0002
                                        .getValue());

        parametrosListado.put(cCompania, compania);
        parametrosListado.put(cModulo, moduloNomina);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista {@code listaTipoParametro} asociada al combo
     * Tipo Parametro.
     */
    public void cargarListaTipoParametro() {
        Map<String, Object> param = new TreeMap<>();

        try {
            listaTipoParametro = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ActualizaparametrosretroactivosControladorUrlEnum.URL0003
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    // </METODOS_CARGAR_LISTA>

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        boolean key = SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        cValor) ? true : verificarFormatoValor();

        registro.getCampos().remove(cCompania);
        registro.getCampos().remove(cModulo);
        registro.getCampos().remove(cNombre);
        registro.getCampos().remove(cTipoParametro);
        registro.getCampos().remove(cNombreTipoParametro);

        return key;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Valida que el valor ingresado en el parametro {@code valor} sea
     * {@code SI} o {@code NO}
     * 
     * @param valor
     * @return
     */
    private boolean validarSINO(String valor) {
        switch (valor) {
        case "SI":
        case "NO":
            registro.getCampos().put(cValor, valor.trim());
            break;
        default:
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3631"));
            return false;
        }
        return true;
    }

    /**
     * Valida que el valor ingresado en el parametro {@code valor} sea
     * un numero entero.
     * 
     * @param valor
     * @return
     */
    private boolean validarValorInt(String valor) {
        try {
            Integer.parseInt(valor);
        }
        catch (NumberFormatException nfe) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3630"));
            return false;
        }

        return true;
    }

    public boolean verificarFormatoValor() {
        String valor = (String) registro.getCampos().get(cValor);
        boolean verificar = true;

        try {
            switch (registro.getCampos().get(cTipoParametro).toString()) {
            case "3":
                verificar = validarValorInt(valor);
                break;
            case "5":
                verificar = validarSINO(valor.trim().toUpperCase());
                break;
            default:
                break;
            }
        }
        catch (NullPointerException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3632"));
            return false;
        }

        return verificar;
    }

    public List<Registro> getListaTipoParametro() {
        return listaTipoParametro;
    }

    public void setListaTipoParametro(List<Registro> listaTipoParametro) {
        this.listaTipoParametro = listaTipoParametro;
    }
}
