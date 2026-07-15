/*-
 * ActividadesinscritosControlador.java
 *
 * 1.0
 * 
 * 06/02/2018
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
import com.sysman.hojasdevida.enums.ActividadesinscritosControladorEnum;
import com.sysman.hojasdevida.enums.ActividadesinscritosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.sql.Date;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Clase encargada de gestionar las operaciones basicas de la opción
 * de menu Inscritos a Actividades. accesible desde Bienestar y
 * Capacitación del modulo de Hojas de vida.
 *
 * @version 1.0, 06/02/2018
 * @author dnino
 * 
 * Se agrego el botón anexos que redirecciona al formulario
 * FrmDetalleDocumentoInscritoControlador
 * 
 * @version 2.0 21/06/2018
 * @version lbotia
 * 
 */
@ManagedBean
@ViewScoped
public class ActividadesinscritosControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que almacena el documento del empleado.
     */
    private String documento;
    /*
     * Constante que asigna el título al formulario.
     */
    private String titulo;
    /*
     * Constante que asigna el parámetro para el listado de Eventos.
     */
    private String tipoEvento;
    /*
     * Constante que asigna el parámetro para el listado de Eventos.
     */
    private String nombre;
    /*
     * Constante que asigna el parámetro para el listado de Eventos.
     */
    private boolean mostrar;
    /*
     * Variable que almacena la fecha inicial del tipo de Evento.
     */
    private Date fechaInicial;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista desplegable del numero de documento de los empleados.
     */
    private RegistroDataModelImpl listaNumeroDcto;
    /**
     * Lista desplegable para edicion del registro numero de documento
     * de los empleados.
     */
    private RegistroDataModelImpl listaNumeroDctoE;
    /**
     * Lista desplegable de los eventos asociados al tipo de
     * actividad.
     */
    private RegistroDataModelImpl listaEvento;
    /**
     * Lista desplegable para edicion de los eventos asociados al tipo
     * de actividad.
     */
    private RegistroDataModelImpl listaEventoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se almacena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    private String cIdEvento = ActividadesinscritosControladorEnum.IDEVENTO
                    .getValue();

    private String cTipoEvento = ActividadesinscritosControladorEnum.TIPOEVENTO
                    .getValue();

    private String cNumeroDcto = ActividadesinscritosControladorEnum.NUMERO_DCTO
                    .getValue();

    private String cSucursal = GeneralParameterEnum.SUCURSAL.getName();

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ActividadesinscritosControlador
     */
    public ActividadesinscritosControlador() {
        super();
        titulo = "INSCRITOS A ACTIVIDADES ";
        definirTipoActividad();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.ACTIVIDADES_INSCRITOS_CONTROLADOR
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
        enumBase = GenericUrlEnum.NAT_ACTIVIDADESINSCRITOS;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaNumeroDcto();
        cargarListaNumeroDctoE();
        cargarListaEvento();
        cargarListaEventoE();
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
        parametrosListado.put(ActividadesinscritosControladorEnum.TIPOEVENTO
                        .getValue(),
                        tipoEvento);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaNumeroDcto
     *
     */
    public void cargarListaNumeroDcto() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActividadesinscritosControladorUrlEnum.URL404
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        listaNumeroDcto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.NUMERO_DCTO
                                        .getName());
    }

    /**
     * 
     * Carga la lista listaNumeroDcto
     *
     */
    public void cargarListaNumeroDctoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActividadesinscritosControladorUrlEnum.URL404
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        listaNumeroDctoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        GeneralParameterEnum.NUMERO_DCTO
                                        .getName());
    }

    public void cargarListaEvento() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActividadesinscritosControladorUrlEnum.URL180
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(ActividadesinscritosControladorEnum.TIPOEVENTO.getValue(),
                        tipoEvento);
        listaEvento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        ActividadesinscritosControladorEnum.IDEVENTO
                                        .getValue());
    }

    public void cargarListaEventoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActividadesinscritosControladorUrlEnum.URL180
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(ActividadesinscritosControladorEnum.TIPOEVENTO.getValue(),
                        tipoEvento);
        listaEventoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        ActividadesinscritosControladorEnum.IDEVENTO
                                        .getValue());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton cmdAceptar enlazado al
     * formulario Inscribir Beneficiarios
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimircmdAceptar(Registro reg, int indice) {
        Direccionador direccionador = new Direccionador();
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", reg.getLlave());
        parametros.put("cerrar", "1");
        try {
            parametros.put(ActividadesinscritosControladorEnum.FECHAINICIAL
                            .getValue(),
                            SysmanFunciones.convertirAFecha(
                                            reg.getCampos()
                                                            .get(ActividadesinscritosControladorEnum.FECHAINICIAL
                                                                            .getValue())
                                                            .toString()));
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRM_INSCR_BENEFICIARIO_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton anexos
     * 
     *
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimiranexos(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>

        String[] campos = { "idevento", "tipoevento", "numerodcto",
                            "sucursal" };

        Object[] valores = {
                             reg.getCampos().get(cIdEvento),
                             reg.getCampos().get(cTipoEvento),
                             reg.getCampos().get(cNumeroDcto),
                             reg.getCampos().get(cSucursal) };

        SessionUtil.cargarModalDatosFlashCerrar(Integer
                        .toString(GeneralCodigoFormaEnum.FRM_DETALLE_DOCUMENTO_INSCRITO_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(), campos,
                        valores);

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    /**
     * Permite definir el Tipo de Actividad que se esta trabajando de
     * acuerdo a la opcion de menu por la que se ingresa al
     * formulario. Tambien permite la definicion del tiulo de
     * formulario
     */
    private void definirTipoActividad() {
        switch (SessionUtil.getMenuActual()) {
        case "210402010104":
            tipoEvento = "1";
            titulo += "DE SEGURIDAD Y SALUD EN EL TRABAJO";
            mostrar = true;
            break;

        case "210402010203":
            tipoEvento = "2";
            titulo += "DEPORTIVAS";
            mostrar = true;
            break;

        case "210402010303":
            tipoEvento = "3";
            titulo += "CULTURALES";
            mostrar = true;
            break;

        case "210402010403":
            tipoEvento = "4";
            titulo += "RECREATIVAS";
            mostrar = true;
            break;

        case "210402020103":
            tipoEvento = "7";
            titulo += "DE INDUCCIÓN / REINDUCCIÓN";
            mostrar = false;
            break;

        case "210402020204":
            tipoEvento = "6";
            titulo += "DE CAPACITACIÓN NO FORMAL";
            mostrar = false;
            break;

        case "210402020303":
            tipoEvento = "5";
            titulo += "DE CAPACITACIÓN FORMAL";
            mostrar = false;
            break;

        case "210802050103":
            tipoEvento = "102";
            titulo = idioma.getString("TB_TB4207");
            break;

        case "210802050302":
            tipoEvento = "103";
            titulo = idioma.getString("TB_TB4208");
            break;

        case "210802050204":
            tipoEvento = "101";
            titulo = idioma.getString("TB_TB4209");
            break;
        default:

            break;
        }
    }

    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista NumeroDcto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNumeroDcto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(ActividadesinscritosControladorEnum.NUMERO_DCTO
                        .getValue(),
                        registroAux.getCampos().get(
                                        ActividadesinscritosControladorEnum.NUMERO_DCTO
                                                        .getValue()));
        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));
        documento = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        ActividadesinscritosControladorEnum.NUMERO_DCTO
                                                        .getValue()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista Editar
     * Numero Dcto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNumeroDctoE(SelectEvent event) {
        Registro registroAuxE = (Registro) event.getObject();
        registro.getCampos().put(ActividadesinscritosControladorEnum.NUMERO_DCTO
                        .getValue(),
                        registroAuxE.getCampos().get(
                                        ActividadesinscritosControladorEnum.NUMERO_DCTO
                                                        .getValue()));
        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAuxE.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAuxE.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));
        auxiliar = SysmanFunciones
                        .nvl(registroAuxE.getCampos()
                                        .get(ActividadesinscritosControladorEnum.NUMERO_DCTO
                                                        .getValue()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista lista
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEvento(SelectEvent event) {
        Registro registroAuxE = (Registro) event.getObject();
        registro.getCampos().put(ActividadesinscritosControladorEnum.IDEVENTO
                        .getValue(),
                        registroAuxE.getCampos().get(
                                        ActividadesinscritosControladorEnum.IDEVENTO
                                                        .getValue()));
        registro.getCampos()
                        .put(ActividadesinscritosControladorEnum.FECHAINICIAL
                                        .getValue(),
                                        registroAuxE.getCampos().get(
                                                        ActividadesinscritosControladorEnum.FECHAINICIAL
                                                                        .getValue()));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista lista
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEventoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(
                        ActividadesinscritosControladorEnum.IDEVENTO.getValue())
                        .toString();
    }

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
     * 
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true si demanda operaciones posteriores a la insercion
     * de un registro.
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(ActividadesinscritosControladorEnum.TIPOEVENTO
                        .getValue(),
                        tipoEvento);
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return true si demanda operaciones posteriores a la insercion
     * de un registro.
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
     * @return true si demanda operaciones previas a la actualizacion
     * de un registro.
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
     * @return true si requiere operaciones posteriores a la
     * actualizacion de un registro.
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
     * @return true si requiere operaciones previas a la eliminacion
     * de un registro.
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
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        registro.getCampos()
                        .remove(GeneralParameterEnum.FECHAINICIAL.getName());
        registro.getCampos()
                        .remove(ActividadesinscritosControladorEnum.BENEFICIARIO
                                        .getValue());
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.NUMERO_DCTO.getName());
        registro.getCampos().remove(GeneralParameterEnum.SUCURSAL.getName());
        registro.getCampos()
                        .remove(ActividadesinscritosControladorEnum.TIPOEVENTO
                                        .getValue());
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove("NOMBREEVENTO");

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

    /**
     * Retorna la variable documento
     * 
     * @return documento
     */
    public String getDocumento() {
        return documento;
    }

    /**
     * @return the titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * @param titulo
     * the titulo to set
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * @return the tipoEvento
     */
    public String getTipoEvento() {
        return tipoEvento;
    }

    /**
     * @param tipoEvento
     * the tipoEvento to set
     */
    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    /**
     * @return the mostrar
     */
    public boolean isMostrar() {
        return mostrar;
    }

    /**
     * @param mostrar
     * the mostrar to set
     */
    public void setMostrar(boolean mostrar) {
        this.mostrar = mostrar;
    }

    /**
     * Asigna la variable documento
     * 
     * @param documento
     * Variable a asignar en documento
     */
    public void setDocumento(String documento) {
        this.documento = documento;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre
     * the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * @return the fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * @param fechaInicial
     * the fechaInicial to set
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * @return the listaNumeroDcto
     */
    public RegistroDataModelImpl getListaNumeroDcto() {
        return listaNumeroDcto;
    }

    /**
     * @return the listaEvento
     */
    public RegistroDataModelImpl getListaEvento() {
        return listaEvento;
    }

    /**
     * @param listaEvento
     * the listaEvento to set
     */
    public void setListaEvento(RegistroDataModelImpl listaEvento) {
        this.listaEvento = listaEvento;
    }

    /**
     * @return the listaEventoE
     */
    public RegistroDataModelImpl getListaEventoE() {
        return listaEventoE;
    }

    /**
     * @param listaEventoE
     * the listaEventoE to set
     */
    public void setListaEventoE(RegistroDataModelImpl listaEventoE) {
        this.listaEventoE = listaEventoE;
    }

    /**
     * @param listaNumeroDcto
     * the listaNumeroDcto to set
     */
    public void setListaNumeroDcto(RegistroDataModelImpl listaNumeroDcto) {
        this.listaNumeroDcto = listaNumeroDcto;
    }

    /**
     * @return the listaNumeroDctoE
     */
    public RegistroDataModelImpl getListaNumeroDctoE() {
        return listaNumeroDctoE;
    }

    /**
     * @param listaNumeroDctoE
     * the listaNumeroDctoE to set
     */
    public void setListaNumeroDctoE(RegistroDataModelImpl listaNumeroDctoE) {
        this.listaNumeroDctoE = listaNumeroDctoE;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
