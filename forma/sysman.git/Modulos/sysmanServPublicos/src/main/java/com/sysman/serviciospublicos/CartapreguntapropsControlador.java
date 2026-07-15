/*-
 * CartapreguntapropsControlador.java
 *
 * 1.0
 *
 * 05/10/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.serviciospublicos.enums.CartapreguntapropsControladorEnum;
import com.sysman.serviciospublicos.enums.CartapreguntapropsControladorUrlEnum;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 * Clase migrada para registrar las preguntas de los usuarios con
 * desviaciones, se hace el llamado a este formulario en el boton ver
 * del SubSubDesviacionesCarta del formulario DESVIACIONES
 *
 * @version 1.0, 05/10/2016
 * @author ybecerra
 * 
 * @author ybecerra
 * @version 2, 15/05/2017 Revision Sonar y Refactoring
 * 
 * @version 3.0, 13/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.
 * 
 */
@ManagedBean
@ViewScoped
public class CartapreguntapropsControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Atributo que almacena el valor del paramatro claseCarta
     * recibido del formulario Desviaciones
     */
    private String claseCarta;
    /**
     * Atributo que almacena el valor del parametro subClase recibido
     * del formulario Desviaciones
     */
    private String subClase;
    /**
     * Atributo que almacena el valor del parametro consecutivo
     * recibido del formulario Desviaciones
     */
    private String consecutivo;

    /**
     * Atributo que almacena el valor del parametro codigoRuta
     * recibido del formulario Desviaciones
     */
    private String codigoRuta;

    /**
     * Atributo que almacena el valor del parametro suscriptor
     * recibido del formulario Desviaciones
     */
    private String suscriptor;

    /**
     * Atributo que almacena el valor del parametro nombreClase
     * recibido del formulario Desviaciones
     */
    private String nombreClase;

    /**
     * Atributo que almacena la concatenacion del los atributos
     * subClase - consecutivo
     */
    private String titulo;

    /**
     * Atributo que almacena la concatenacion de los atributos
     * codigoRuta - nombreUsuario
     */
    private String tituloUno;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listapregunta;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de CartapreguntapropsControlador
     */
    public CartapreguntapropsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 1133
            numFormulario = GeneralCodigoFormaEnum.CARTAPREGUNTAPROPS_CONTROLADOR
                            .getCodigo();

            validarPermisos();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                claseCarta = (String) parametrosEntrada.get("claseCarta");
                subClase = (String) parametrosEntrada.get("subClase");
                consecutivo = (String) parametrosEntrada.get("consecutivo");
                codigoRuta = (String) parametrosEntrada.get("codigoRuta");
                suscriptor = (String) parametrosEntrada.get("suscriptor");
                nombreClase = (String) parametrosEntrada.get("nombreClase");

            }
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {

            SessionUtil.cleanFlash();
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

        enumBase = GenericUrlEnum.SP_RESPUESTA_MODELO_PLANTILLA;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListapregunta();
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
        parametrosListado.put(GeneralParameterEnum.CLASE.getName(), claseCarta);
        parametrosListado.put(
                        CartapreguntapropsControladorEnum.PARAM0.getValue(),
                        subClase);
        parametrosListado.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        consecutivo);
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListapregunta() {
        // Apunta a la tabla SP_CARTA_VAR
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), claseCarta);
        param.put(CartapreguntapropsControladorEnum.PARAM0.getValue(),
                        subClase);

        try {
            listapregunta = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CartapreguntapropsControladorUrlEnum.URL5943
                                                                            .getValue())
                                            .getUrl(), param));
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
        /*
         * FR1133-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * Me!Eti_Titulo.Caption = "" & Me.OpenArgs DoCmd.Restore End
         * Sub
         */

        titulo = "" + nombreClase + " - " + consecutivo + " ";

        tituloUno = "" + codigoRuta + " - " + suscriptor + "";
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * inserta los campos de compania, clase, subclase,
     * consecutivoclase de la tabla SP_CARTA_PREGUNTA_PROP
     *
     * @return retorna un valor booleano
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.CLASE.getName(),
                        claseCarta);
        registro.getCampos().put(
                        CartapreguntapropsControladorEnum.PARAM0.getValue(),
                        subClase);
        registro.getCampos().put("CONSECUTIVOCLASE", consecutivo);
        registro.getCampos().remove("PREGUNTA");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     * @return retorna un valor booleano
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
     * @return retorna un valor booleano
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
     * @return retorna un valor booleano
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
     * @return retorna un valor booleano
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
     * @return retorna un valor booleano
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.CLASE.getName());
        registro.getCampos().remove("SUBCLASE");
        registro.getCampos().remove("CONSECUTIVOCLASE");
        registro.getCampos().remove("PREGUNTA");
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
        // No se realiza ningun procedimiento en este metodo
    }

    // <SET_GET_ATRIBUTOS>
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTituloUno() {
        return tituloUno;
    }

    public void setTituloUno(String tituloUno) {
        this.tituloUno = tituloUno;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listapregunta
     * 
     * @return listapregunta
     */
    public List<Registro> getListapregunta() {
        return listapregunta;
    }

    /**
     * Asigna la lista listapregunta
     * 
     * @param listapregunta
     * Variable a asignar en listapregunta
     */
    public void setListapregunta(List<Registro> listapregunta) {
        this.listapregunta = listapregunta;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        // no se ejecuta ningun proceso

    }
}
