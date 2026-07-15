/*-
 * ActaCambioMedidorControlador.java
 *
 * 1.0
 *
 * 06/02/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
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
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosSeisRemote;
import com.sysman.serviciospublicos.enums.ActaCambioMedidorControladorEnum;
import com.sysman.serviciospublicos.enums.ActaCambioMedidorControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.event.SelectEvent;

/**
 *
 *
 * @version 1.0, 06/02/2017
 * @author jsforero
 * @version 2, 12/05/2017 spina - se refactoriza para dss, depuracion
 * sonar y ejb
 *
 * -- Modificado por lcortes 13/06/2017. Se reemplaza el valor del
 * atributo numFormulario por el enumerado correspondiente y se
 * reemplaza el numero de formulario enviado en el metodo
 * cargarModalDatosFlash por el enumerado correspondiente al
 * formulario a cargar.
 */
@ManagedBean
@ViewScoped
public class ActaCambioMedidorControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private final String consCodigo;
    private final String consCodigoRuta;

    // Codigo inicial seleccionado en el combo
    private String codigoInicial;
    // Codigo final seleccionado en el combo
    private String codigoFinal;
    // ciclo para el que se va a sacar el acta de cambio de medidor
    private String ciclo;
    // Numero de acta desde la cual se va a empezar a generar las
    // nuevas actas
    private int nacta;
    // Variable que controla el checkbox de consecutivo
    private boolean consecutivo = true;
    // Variable que almacena el id del problema inicial desde el cual
    // se va a generar el reporte
    private String problemaInicial;
    // Variable que almacena el id del problema final desde el cual se
    // va a generar el reporte
    private String problemaFinal;
    // Numero de formato base para mostrar el archivo word
    private String formato;
    // Nombre de formato base para mostrar el archivo word
    private String formatonombre;
    // Formato de generacion del formato word
    private String formatoFecha;
    // Listados que contiene todos los ciclos
    private List<Registro> listaCiclo;
    // Listado de codigos , este se muestra en el combobox de codigo
    // inicial
    private RegistroDataModelImpl listaCodigoInicial;
    // Listado de formatos word
    private RegistroDataModelImpl listacmbFormato;
    // listado de codigos, este se muestra en el combobox de codigo
    // final
    private RegistroDataModelImpl listaCodigoFinal;
    // listado de problemas, este se muestra en el combobox de
    // problema inicial
    private RegistroDataModelImpl listacmbProblemaInicial;
    // listado de problemas, este se muestra en el combobox de
    // problema final
    private RegistroDataModelImpl listaCmbProblemaFinal;

    @EJB
    private EjbServiciosPublicosSeisRemote ejbServiciosPublicosSeis;

    public ActaCambioMedidorControlador() {
        super();
        compania = SessionUtil.getCompania();
        consCodigo = "CODIGO";
        consCodigoRuta = "CODIGORUTA";

        try {
            numFormulario = GeneralCodigoFormaEnum.ACTA_CAMBIO_MEDIDOR_CONTROLADOR
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
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoInicial();
        cargarListacmbProblemaInicial();
        cargarListacmbFormato();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaCiclo();
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
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
        iniciarListas();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     *
     *
     */
    @Override
    public void asignarOrigenDatos() {
        // Metodo Heredado
        buscarUrls();
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCiclo
     *
     */
    public void cargarListaCiclo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ActaCambioMedidorControladorUrlEnum.URL6417
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
     * Carga la lista listacmbFormato
     */
    public void cargarListacmbFormato() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActaCambioMedidorControladorUrlEnum.URL6418
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(ActaCambioMedidorControladorEnum.TIPO.getValue(), 38);

        listacmbFormato = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        consCodigo);
    }

    /**
     *
     * Carga la lista listaCodigoInicial
     */
    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActaCambioMedidorControladorUrlEnum.URL6419
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        consCodigoRuta);
    }

    /**
     *
     * Carga la lista listaCodigoFinal
     */
    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActaCambioMedidorControladorUrlEnum.URL6420
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(ActaCambioMedidorControladorEnum.CODIGOINICIAL.getValue(),
                        codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        consCodigoRuta);
    }

    /**
     *
     * Carga la lista listacmbProblemaInicial
     *
     */
    public void cargarListacmbProblemaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActaCambioMedidorControladorUrlEnum.URL6421
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), String.valueOf("AFR"));

        listacmbProblemaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        consCodigo);
    }

    /**
     *
     * Carga la lista listaCmbProblemaFinal
     */
    public void cargarListaCmbProblemaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActaCambioMedidorControladorUrlEnum.URL6422
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), "AFR");
        param.put(ActaCambioMedidorControladorEnum.PROBLEMAINICIAL.getValue(),
                        problemaInicial);

        listaCmbProblemaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        consCodigo);
    }

    /**
     *
     * Metodo que valida si el numero de acta que el usuario digita no
     * existe. si no existe, retorna null, y si existe retorna el
     * numero a partir del cual se puede ingresar para que sea valido
     */

    private int validarActaInicio() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ActaCambioMedidorControladorEnum.ACTA.getValue(), nacta);

        List<Registro> rs = null;
        int actaInicio = 0;
        try {
            rs = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ActaCambioMedidorControladorUrlEnum.URL6423
                                                                            .getValue())
                                            .getUrl(), param));
            actaInicio = Integer.parseInt(
                            rs.get(0).getCampos().get("NACTA").toString());
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return actaInicio;
    }

    /**
     *
     * Metodo que genera el numero consecutivo cuando el usuario no
     * ingresa uno
     */
    private int generarNumeroActa() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        List<Registro> rs;
        int numeroActa = 0;
        try {
            rs = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ActaCambioMedidorControladorUrlEnum.URL6424
                                                                            .getValue())
                                            .getUrl(), param));
            numeroActa = Integer.parseInt(
                            rs.get(0).getCampos().get("NACTA").toString())
                + 1;
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return numeroActa;
    }

    private int generarActas(int numacta)
                    throws IllegalAccessException, InstantiationException,
                    ClassNotFoundException, SQLException, NamingException,
                    SystemException {
        return ejbServiciosPublicosSeis.generarActa(compania, codigoInicial,
                        codigoFinal, Integer.parseInt(ciclo), problemaInicial,
                        problemaFinal,
                        numacta);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton CmdPrint en la vista
     *
     */

    public void oprimirCmdPrint() {

        try {
            int codInicial;
            int codFinal;
            if (consecutivo) {
                codInicial = generarNumeroActa();
                codFinal = generarActas(codInicial);
            }
            else if (validarActaInicio() == 0) {
                codInicial = nacta;
                codFinal = generarActas(nacta);

            }
            else {
                JsfUtil.agregarMensajeError(
                                idioma.getString("TB_TB2809")
                                    + validarActaInicio());
                return;
            }
            String[] campos = new String[3];
            String[] valores = new String[3];
            campos[0] = "codigoPlantilla";
            campos[1] = "fechaPlantilla";
            campos[2] = "nombreDocDescarga";

            valores[0] = formato;
            valores[1] = formatoFecha;
            valores[2] = formatonombre;

            HashMap<String, String> variablesConsultaW = new HashMap<>();
            variablesConsultaW.put("s$compania$s", "'" + compania + "'");
            variablesConsultaW.put("s$cod_inicial$s",
                            Integer.toString(codInicial));
            variablesConsultaW.put("s$cod_final$s", Integer.toString(codFinal));

            // variables por parametro para documento word
            SessionUtil.setSessionVar("variablesConsultaWord",
                            variablesConsultaW);
            SessionUtil.cargarModalDatosFlash(
                            Integer.toString(
                                            GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                                            .getCodigo()),
                            SessionUtil.getModulo(), campos, valores);
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException
                        | NamingException | SystemException ex) {
            Logger.getLogger(ActaCambioMedidorControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                + ex.getMessage());
        }
        catch (SQLException ex) {
            Logger.getLogger(ActaCambioMedidorControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString("TB_TB2947")
                                + validarActaInicio());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ciclo
     *
     *
     */
    public void cambiarCiclo() {
        codigoInicial = "";
        codigoFinal = "";
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     *
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
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
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(consCodigoRuta), "")
                        .toString();
        codigoFinal = "";
        cargarListaCodigoFinal();

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(consCodigoRuta), "")
                        .toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbProblemaInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbProblemaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        problemaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(consCodigo), "")
                        .toString();
        problemaFinal = "";
        cargarListaCmbProblemaFinal();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbProblemaFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbProblemaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        problemaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(consCodigo), "")
                        .toString();
    }

    public void seleccionarFilacmbFormato(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        formato = SysmanFunciones
                        .nvl(registroAux.getCampos().get(consCodigo), "")
                        .toString();
        formatonombre = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()),
                                        "")
                        .toString();
        try {
            String strFecha = (String) registroAux.getCampos()
                            .get("FECHA");
            if (!SysmanFunciones.validarVariableVacio(strFecha)) {
                formatoFecha = SysmanFunciones.formatearFecha(
                                SysmanFunciones.convertirAFecha(strFecha,
                                                "dd/MM/yyyy"));
            }
        }
        catch (ParseException ex) {
            logger.error(ex.getMessage(), ex);
            Logger.getLogger(ActaCambioMedidorControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeAlerta(ex.getMessage());
        }

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable codigoInicial
     *
     * @return codigoInicial
     */

    public void cambiarVerificacion56() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public String getCodigoInicial() {
        return codigoInicial;
    }

    /**
     * Asigna la variable codigoInicial
     *
     * @param codigoInicial
     * Variable a asignar en codigoInicial
     */
    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    /**
     * Retorna la variable codigoFinal
     *
     * @return codigoFinal
     */
    public String getCodigoFinal() {
        return codigoFinal;
    }

    /**
     * Asigna la variable codigoFinal
     *
     * @param codigoFinal
     * Variable a asignar en codigoFinal
     */
    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    /**
     * Retorna la variable ciclo
     *
     * @return ciclo
     */
    public String getCiclo() {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     *
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    /**
     * Retorna la variable problemaInicial
     *
     * @return problemaInicial
     */
    public String getProblemaInicial() {
        return problemaInicial;
    }

    /**
     * Asigna la variable problemaInicial
     *
     * @param problemaInicial
     * Variable a asignar en problemaInicial
     */
    public void setProblemaInicial(String problemaInicial) {
        this.problemaInicial = problemaInicial;
    }

    /**
     * Retorna la variable problemaFinal
     *
     * @return problemaFinal
     */
    public String getProblemaFinal() {
        return problemaFinal;
    }

    /**
     * Asigna la variable problemaFinal
     *
     * @param problemaFinal
     * Variable a asignar en problemaFinal
     */
    public void setProblemaFinal(String problemaFinal) {
        this.problemaFinal = problemaFinal;
    }

    /**
     * Retorna la variable formato
     *
     * @return formato
     */
    public String getFormato() {
        return formato;
    }

    public boolean isConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(boolean consecutivo) {
        this.consecutivo = consecutivo;
    }

    public int getNacta() {
        return nacta;
    }

    public void setNacta(int nacta) {
        this.nacta = nacta;
    }

    /**
     * Asigna la variable formato
     *
     * @param formato
     * Variable a asignar en formato
     */
    public void setFormato(String formato) {
        this.formato = formato;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCiclo
     *
     * @return listaCiclo
     */
    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     *
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }
    /**
     * Retorna la lista listacmbFormato
     *
     * @return listacmbFormato
     */

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodigoInicial
     *
     * @return listaCodigoInicial
     */
    public RegistroDataModelImpl getListacmbFormato() {
        return listacmbFormato;
    }

    public void setListacmbFormato(RegistroDataModelImpl listacmbFormato) {
        this.listacmbFormato = listacmbFormato;
    }

    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    /**
     * Asigna la lista listaCodigoInicial
     *
     * @param listaCodigoInicial
     * Variable a asignar en listaCodigoInicial
     */
    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    /**
     * Retorna la lista listaCodigoFinal
     *
     * @return listaCodigoFinal
     */
    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    /**
     * Asigna la lista listaCodigoFinal
     *
     * @param listaCodigoFinal
     * Variable a asignar en listaCodigoFinal
     */
    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }

    /**
     * Retorna la lista listacmbProblemaInicial
     *
     * @return listacmbProblemaInicial
     */
    public RegistroDataModelImpl getListacmbProblemaInicial() {
        return listacmbProblemaInicial;
    }

    /**
     * Asigna la lista listacmbProblemaInicial
     *
     * @param listacmbProblemaInicial
     * Variable a asignar en listacmbProblemaInicial
     */
    public void setListacmbProblemaInicial(
        RegistroDataModelImpl listacmbProblemaInicial) {
        this.listacmbProblemaInicial = listacmbProblemaInicial;
    }

    /**
     * Retorna la lista listaCmbProblemaFinal
     *
     * @return listaCmbProblemaFinal
     */
    public RegistroDataModelImpl getListaCmbProblemaFinal() {
        return listaCmbProblemaFinal;
    }

    /**
     * Asigna la lista listaCmbProblemaFinal
     *
     * @param listaCmbProblemaFinal
     * Variable a asignar en listaCmbProblemaFinal
     */
    public void setListaCmbProblemaFinal(
        RegistroDataModelImpl listaCmbProblemaFinal) {
        this.listaCmbProblemaFinal = listaCmbProblemaFinal;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    public String getFormatonombre() {
        return formatonombre;
    }

    public void setFormatonombre(String formatonombre) {
        this.formatonombre = formatonombre;
    }

    public String getFormatoFecha() {
        return formatoFecha;
    }

    public void setFormatoFecha(String formatoFecha) {
        this.formatoFecha = formatoFecha;
    }

}
