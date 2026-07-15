package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.general.enums.OrdendecompraauxiliaresControladorUrlEnum;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author jrodriguezr
 * @version 1, 07/01/2016
 *
 *
 * @author ybecerra
 * @version 2, 03/04/2017 Revision Sonar y refactorizacion
 * 
 * @version 3, 12/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Reemplazar el redireccionar por el redireccionarForma de
 * SessionUtil.
 */
@ManagedBean
@ViewScoped
public class OrdendecompraauxiliaresControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual el usuario inicio sesion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>

    private String claseOrden;
    private String numeroOrden;
    private String anio;
    private String auxiliarColumn1;
    private String fuenteRecursosColumn1;
    private String metaProdColumn1;
    private String actividadColumn8;
    private String auxiliar;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Crea una nueva instancia de OrdendecompraauxiliaresControlador
     */
    public OrdendecompraauxiliaresControlador() {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try {
            // 362
            numFormulario = GeneralCodigoFormaEnum.ORDENDECOMPRAAUXILIARES_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (SysmanException ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        tabla = "ORDENDECOMPRA_AUXILIAR";

        buscarLlave();
        Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

        if (parametrosEntrada != null) {
            claseOrden = parametrosEntrada.get("claseF").toString();
            numeroOrden = parametrosEntrada.get("numeroOrden").toString();
            anio = parametrosEntrada.get("vigencia").toString();
        }
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());

        abrirFormulario();

    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        numeroOrden);
        parametrosListado.put(GeneralParameterEnum.CLASEORDEN.getName(),
                        claseOrden);
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        OrdendecompraauxiliaresControladorUrlEnum.URL89
                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton SolicitudesORDENDECOMPRA
     * en la vista
     *
     *
     */
    public void oprimirSolicitudesORDENDECOMPRA() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "anio", "numeroOrden", "claseOrden" };
        String[] valores = { anio, numeroOrden, claseOrden };

        SessionUtil.cargarModalDatos(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.SOLICITUD_ORDEN_COMPRA_CONTROLADOR
                                                        .getCodigo()),
                        SessionUtil.getModulo(), campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }

    public void retornarFormularioSolicitudesORDENDECOMPRA() {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control SolicitudesORDENDECOMPRA
     *
     */
    public void retornarFormularioSolicitudesORDENDECOMPRA(SelectEvent event) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR362-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     * @return true
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     *
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     *
     * @return true
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR362-DESPUES_ACTUALIZAR Private Sub Form_AfterUpdate()
         * 'registrarmodificacion End Sub
         */
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     *
     * @return true
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     *
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public int getNumFormulario() {
        return numFormulario;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     */
    public void ejecutarrcCerrar() {
        int form = 0;

        switch (SessionUtil.getMenuActual()) {
        case "9020312":
            form = GeneralCodigoFormaEnum.ADICIONESPCONTRATOS_CONTROLADOR
                            .getCodigo();
            break;
        case "10020201":
        case "90202":
            form = GeneralCodigoFormaEnum.PCONTRATOS_CONTROLADOR.getCodigo();
            break;
        default:
            break;
        }

        Map<String, Object> parametros = SessionUtil.getFlash();

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer.toString(form));
        direccionador.setParametros(parametros);

        SessionUtil.redireccionarForma(direccionador, modulo);
    }

    // <SET_GET_ATRIBUTOS>
    public String getClaseOrden() {
        return claseOrden;
    }

    public void setClaseOrden(String claseOrden) {
        this.claseOrden = claseOrden;
    }

    public String getNumeroOrden() {
        return numeroOrden;
    }

    public void setNumeroOrden(String numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getAuxiliarColumn1() {
        return auxiliarColumn1;
    }

    public void setAuxiliarColumn1(String auxiliarColumn1) {
        this.auxiliarColumn1 = auxiliarColumn1;
    }

    public String getFuenteRecursosColumn1() {
        return fuenteRecursosColumn1;
    }

    public void setFuenteRecursosColumn1(String fuenteRecursosColumn1) {
        this.fuenteRecursosColumn1 = fuenteRecursosColumn1;
    }

    public String getMetaProdColumn1() {
        return metaProdColumn1;
    }

    public void setMetaProdColumn1(String metaProdColumn1) {
        this.metaProdColumn1 = metaProdColumn1;
    }

    public String getActividadColumn8() {
        return actividadColumn8;
    }

    public void setActividadColumn8(String actividadColumn8) {
        this.actividadColumn8 = actividadColumn8;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}