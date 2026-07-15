/*-
 * IdiomasControlador.java
 *
 * 1.0
 *
 * 16/12/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.IdiomasControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 * Formulario que permite realizar el registro de los idiomas que
 * maneja una persona. Se accede desde la ruta Panel Principal\Hojas
 * de Vida\Hojas de Vida\Datos Hoja de Vida\Datos Basicos - Boton
 * Idiomas
 *
 * @version 1.0, 04/04/2018, Se realiza desarrollo a partir del
 * analisis descrito en el tar 1000081152
 * @author eamaya
 * 
 * @version 1.5, 04/04/2017
 * @author eamaya, Se hicieron cambios en los parametros traidos desde
 * el formulario de Datos Personales y se adiciono el metodo
 * ejecutarCerrar()
 * 
 */
@ManagedBean
@ViewScoped
public class IdiomasControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que identifica el numero de documento del registro
     * desde donde se accede al formulario.
     */
    private String numeroDcto;
    /**
     * Variable que identifica la sucursal correspondiente al numero
     * de documento del registro desde donde se accede al formulario.
     */
    private String sucursal;
    /**
     * Variable que identifica el codigo actual de la persona
     * correspondiente donde se accede al formulario.
     */
    private String niCodPersona;

    /**
     * Estructura que almacena los campos llave del personal con el
     * que se eta trabajando
     */
    private Map<String, Object> rid;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Listado de tipo de idiomas.
     */
    private List<Registro> listaNombreIdioma;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de IdiomasControlador
     */
    public IdiomasControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada
                                .get("rid");

                numeroDcto = (String) parametrosEntrada.get("numeroDcto");
                sucursal = (String) parametrosEntrada.get("sucursal");
                niCodPersona = (String) parametrosEntrada.get("niCodPersona");
            }
            // 1524
            numFormulario = GeneralCodigoFormaEnum.IDIOMAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
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
        enumBase = GenericUrlEnum.NAT_IDIOMAS;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaNombreIdioma();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.NUMERO.getName(),
                        numeroDcto);
        parametrosListado.put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaNombreIdioma
     *
     */
    public void cargarListaNombreIdioma() {
        try {
            listaNombreIdioma = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            IdiomasControladorUrlEnum.URL4529
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
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
     *
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                        numeroDcto);
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);
        registro.getCampos().put("NI_CODIGOPERSONA", niCodPersona);
        registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(),
                        service.buscarEnLista(
                                        registro.getCampos()
                                                        .get(GeneralParameterEnum.NOMBRE
                                                                        .getName())
                                                        .toString(),
                                        "I_NOMBRE", "I_CODIGO",
                                        listaNombreIdioma));
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
     *
     * @return true
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     *
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
     *
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     * Realiza la redireccion al formulario "Natdatospersonales"
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", rid);
        parametros.put("numeroDcto", numeroDcto);
        parametros.put("sucursal", sucursal);
        parametros.put("codigo", niCodPersona);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.NAT_DATOS_PERSONALES_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.NUMERO_DCTO.getName());
        registro.getCampos().remove(GeneralParameterEnum.SUCURSAL.getName());
        registro.getCampos().remove("NI_CODIGOPERSONA");
        registro.getCampos().remove("ESCRIBELB");
        registro.getCampos().remove("LEELB");
        registro.getCampos().remove("HABLALB");
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     *
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
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

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaNombreIdioma
     *
     * @return listaNombreIdioma
     */
    public List<Registro> getListaNombreIdioma() {
        return listaNombreIdioma;
    }

    /**
     * Asigna la lista listaNombreIdioma
     *
     * @param listaNombreIdioma
     * Variable a asignar en listaNombreIdioma
     */
    public void setListaNombreIdioma(List<Registro> listaNombreIdioma) {
        this.listaNombreIdioma = listaNombreIdioma;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
