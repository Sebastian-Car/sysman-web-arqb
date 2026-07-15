/*-
 * MultiusuariosControlador.java
 *
 * 1.0
 *
 * 17/01/2017
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosOchoRemote;
import com.sysman.serviciospublicos.enums.MultiusuariosControladorEnum;
import com.sysman.serviciospublicos.enums.MultiusuariosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 * Clase migrada para registrar multiusuarios, se visualiza en la
 * pestana Informacion de Caculo boton multiusuarios
 *
 * @version 1.0, 17/01/2017
 * @author ybecerra
 *
 * -- Modificado por lcortes 09,10,14,15/06/2017. Refactorizacion de
 * las consultas para usar dss, reemplazos a los llamados a la clase
 * Acciones por los ejb respectivos y ajustes de observaciones de la
 * herramienta SonarLint.
 *
 * -- Modificado por lcortes 14/06/2017. Se reemplaza el valor del
 * atributo numFormulario por el enumerado correspondiente y se
 * suprimen las conexiones a la base de datos.
 */
@ManagedBean
@ViewScoped
public class MultiusuariosControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para almacenar el codigo del modulo por el
     * cual se ingreso en la aplicacion
     */
    private final String modulo;

    /**
     * Constante definida que almacena la cadena "SP_MULTIUSUARIOS"
     */
    private final String cConsecutivo;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el ciclo del registro seleccionado en el
     * formulario USUARIO
     */
    private String ciclo;
    /**
     * Atributo que almacena el codigoRuta del registro seleccionado
     * en el formulario USUARIO
     */
    private String codigoRuta;

    /**
     * Atributo que visualiza el codigoRuta del registro seleccionado
     * en el formulario USUARIO
     */
    private String codRuta;
    /**
     * Atributo que visualiza el nombre del usuario del registro
     * seleccionado en el formulario USUARIO
     */
    private String nombreUsuario;

    /**
     * Atributo que valida si la columna Peso Aseo se hace visible o
     * no
     */
    private boolean visibleAseo;

    /**
     * Atributo que valida si las columnas TAFNA y TAFA se hacen
     * visibles o no
     */
    private boolean visibleTarifa;

    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;
    /**
     * Variable que almacena el uso del suscriptor actual.
     */
    private String usoActual;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de Registro de los Usos
     */
    private List<Registro> listaUso;
    /**
     * Lista de Registros de los Estratos
     */
    private List<Registro> listaEstrato;
    /**
     * Lista de Registros de Estratos de Aseo
     */
    private List<Registro> listaEstratoAseo;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbServiciosPublicosOchoRemote ejbFacturacionOcho;

    /**
     * Crea una nueva instancia de MultiusuariosControlador
     */
    public MultiusuariosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cConsecutivo = GeneralParameterEnum.CONSECUTIVO.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.MULTIUSUARIOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                ciclo = (String) parametrosEntrada.get("ciclo");
                codigoRuta = (String) parametrosEntrada.get("codigoruta");
            }

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
        enumBase = GenericUrlEnum.SP_MULTIUSUARIOS;

        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaUso();

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
        parametrosListado.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        parametrosListado.put(GeneralParameterEnum.CODIGORUTA.getName(),
                        codigoRuta);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaUso
     *
     */
    public void cargarListaUso() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaUso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MultiusuariosControladorUrlEnum.URL6791
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
     * Carga la lista listaEstrato
     *
     */
    public void cargarListaEstrato() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(MultiusuariosControladorEnum.PARAM0.getValue(), usoActual);

        try {
            listaEstrato = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MultiusuariosControladorUrlEnum.URL7292
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
     * Carga la lista listaEstratoAseo
     *
     */
    public void cargarListaEstratoAseo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(MultiusuariosControladorEnum.PARAM0.getValue(), usoActual);

        try {
            listaEstratoAseo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MultiusuariosControladorUrlEnum.URL8121
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
    /**
     * Metodo que se ejecuta al momento de cambiar el registro del
     * combo Uso en la grilla del formulario
     *
     * @param rowNum:
     * Registro de la fila que esta seleccionada
     */
    public void cambiarUsoC(int rowNun) {
        usoActual = (String) listaInicial.getDatasource().get(rowNun % 10)
                        .getCampos().get("USO");

        cargarListaEstrato();
        cargarListaEstratoAseo();

    }

    /**
     * Metodo que se ejecuta al momento de insertar un registro
     */
    public void cambiarUso() {
        usoActual = (String) registro.getCampos().get("USO");
        cargarListaEstrato();
        cargarListaEstratoAseo();
    }

    // </METODOS_CAMBIAR>

    /**
     * Metodo que se ejecuta despues insertar o actualizar un registro
     */
    private void actualizarDatos() {
        try {
            ejbFacturacionOcho.actualizarDatosMultiusuarios(compania,
                            Integer.parseInt(ciclo), codigoRuta,
                            SessionUtil.getUser().getCodigo());
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    public void mensajesInicioModal() {

        JsfUtil.agregarMensajeError(idioma.getString("TB_TB2755"));
        return;

    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        visibleAseo = false;
        visibleTarifa = false;
        codRuta = codigoRuta;

        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            param.put(MultiusuariosControladorEnum.PARAM0.getValue(),
                            codigoRuta);

            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MultiusuariosControladorUrlEnum.URL12753
                                                                            .getValue())
                                            .getUrl(), param));

            if (rs == null) {
                nombreUsuario = "";
            }
            else {
                nombreUsuario = rs.getCampos().get("NOMBREUSUARIO").toString();
            }
        }
        catch (SystemException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

        String parametro;
        try {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            "APLICA RESOLUCION CRA 720", modulo, new Date(),
                            true);

            if (parametro == null) {
                mensajesInicioModal();
            }
            else if ("SI".equals(parametro)) {
                visibleTarifa = true;
                visibleAseo = false;
            }
            else {
                visibleAseo = true;
                visibleTarifa = false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        registro.getCampos().put(GeneralParameterEnum.CANTIDAD.getName(), "0");
        registro.getCampos().put("PESOASEO", "0");
        registro.getCampos().put("TAFNA_720", "0");
        registro.getCampos().put("TAFA_720", "0");
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
        reasignarOrigen();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        try {
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registro.getCampos().put(GeneralParameterEnum.CICLO.getName(),
                            ciclo);
            registro.getCampos().put(GeneralParameterEnum.CODIGORUTA.getName(),
                            codigoRuta);
            registro.getCampos().remove("NOMBREUSO");
            registro.getCampos().get("NOMBREESTRATO");
            registro.getCampos().get("NOMBREESTRATOASEO");
            StringBuilder condicion = new StringBuilder();
            condicion.append("SP_MULTIUSUARIOS.COMPANIA = ''");
            condicion.append(compania);
            condicion.append("''   AND SP_MULTIUSUARIOS.CICLO = ");
            condicion.append(ciclo);
            condicion.append("    AND SP_MULTIUSUARIOS.CODIGORUTA = ''");
            condicion.append(codigoRuta);
            condicion.append("''");

            Long consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            "SP_MULTIUSUARIOS", condicion.toString(),
                            cConsecutivo, "1");

            registro.getCampos().put(cConsecutivo, consecutivo);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
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
        actualizarDatos();
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

        if (!validarCamposVacios()) {
            return false;
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    private boolean validarCamposVacios() {
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(), "USO")) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3216"));
            return false;
        }
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        GeneralParameterEnum.ESTRATO.getName())) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3217"));
            return false;
        }
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        "ESTRATOASEO")) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3218"));
            return false;
        }
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        GeneralParameterEnum.CANTIDAD.getName())) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3219"));
            return false;
        }
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
        actualizarDatos();
        /*
         * FR1261-DESPUES_ACTUALIZAR Private Sub Form_AfterUpdate()
         * ACTUALIZARDATOS End Sub
         */
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
        actualizarDatos();
        /*
         * FR1261-DESPUES_ELIMINAR Private Sub
         * Form_AfterDelConfirm(Status As Integer) ACTUALIZARDATOS End
         * Sub
         */
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
        registro.getCampos().remove(GeneralParameterEnum.CICLO.getName());
        registro.getCampos().remove(GeneralParameterEnum.CODIGORUTA.getName());
        registro.getCampos().remove(cConsecutivo);
        registro.getCampos().remove("NOMBREUSO");
        registro.getCampos().remove("NOMBREESTRATO");
        registro.getCampos().remove("NOMBREESTRATOASEO");
        //
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     *
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     *
     */
    public void activarEdicion(Registro reg) {

        indice = listaInicial.getRowIndex();

    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        //
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna el atributo codRuta
     *
     * @return codRuta
     */
    public String getCodRuta() {
        return codRuta;
    }

    /**
     * Asigna el atributo codRuta
     *
     * @param codRuta
     */
    public void setCodRuta(String codRuta) {
        this.codRuta = codRuta;
    }

    /**
     * Retorna el atributo nombreUsuario
     *
     * @return nombreUsuario
     */
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    /**
     * Asigna el atributo nombreUsuario
     *
     * @param nombreUsuario
     */
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    /**
     * Retorna la variable visibleAseo
     *
     * @return visibleAseo
     */
    public boolean isVisibleAseo() {
        return visibleAseo;
    }

    /**
     * Asigna la variable visibleAseo
     *
     * @param visibleAseo
     */
    public void setVisibleAseo(boolean visibleAseo) {
        this.visibleAseo = visibleAseo;
    }

    /**
     * Retorna la variable visibleTarifa
     *
     * @return visibleTarifa
     */
    public boolean isVisibleTarifa() {
        return visibleTarifa;
    }

    /**
     * Asigna la variable visibleTarifa
     *
     * @param visibleTarifa
     */
    public void setVisibleTarifa(boolean visibleTarifa) {
        this.visibleTarifa = visibleTarifa;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    /**
     * Retorna la variable indice
     *
     * @return indice
     */
    public int getIndice() {
        return indice;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaUso
     *
     * @return listaUso
     */
    public List<Registro> getListaUso() {
        return listaUso;
    }

    /**
     * Asigna la lista listaUso
     *
     * @param listaUso
     * Variable a asignar en listaUso
     */
    public void setListaUso(List<Registro> listaUso) {
        this.listaUso = listaUso;
    }

    /**
     * Retorna la lista listaEstrato
     *
     * @return listaEstrato
     */
    public List<Registro> getListaEstrato() {
        return listaEstrato;
    }

    /**
     * Asigna la lista listaEstrato
     *
     * @param listaEstrato
     * Variable a asignar en listaEstrato
     */
    public void setListaEstrato(List<Registro> listaEstrato) {
        this.listaEstrato = listaEstrato;
    }

    /**
     * Retorna la lista listaEstratoAseo
     *
     * @return listaEstratoAseo
     */
    public List<Registro> getListaEstratoAseo() {
        return listaEstratoAseo;
    }

    /**
     * Asigna la lista listaEstratoAseo
     *
     * @param listaEstratoAseo
     * Variable a asignar en listaEstratoAseo
     */
    public void setListaEstratoAseo(List<Registro> listaEstratoAseo) {
        this.listaEstratoAseo = listaEstratoAseo;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
