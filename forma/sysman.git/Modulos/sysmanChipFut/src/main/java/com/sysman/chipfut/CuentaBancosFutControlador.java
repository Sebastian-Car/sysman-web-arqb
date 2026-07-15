/*-
 * CuentaBancosFutControlador.java
 *
 * 1.0
 * 
 * 23/03/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.chipfut;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.chipfut.ejb.EjbChipFutCeroRemote;
import com.sysman.chipfut.enums.CuentaBancosFutControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Esta clase es el controlador para el formulario Configuracion de
 * Cuentas Bancarias en Access "CuentaBancos", el cual es llamado
 * desde Entes de Control\Chip - Fut\Archivo\Configuraci�n
 * FUT\Configuraci�n de Cuentas
 *
 * 
 * @version 1.0, 23/03/2017
 * @author amonroy
 */
@ManagedBean
@ViewScoped
public class CuentaBancosFutControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo BANCO en el formulario, almacena el texto
     * BANCO el cual es un campo del registro
     */
    private final String cBanco;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo CODIGO en el formulario, almacena el texto
     * CODIGO
     */
    private final String cCodigo;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo IDCONTABLE en el formulario, almacena el texto
     * IDCONTABLE el cual es un campo del registro
     */
    private final String cIdContable;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el anio de destino seleccionado en el
     * formulario
     */
    private int anioDes;

    /**
     * Atributo que almacena el anio seleccionado en el formulario
     */
    private int anio;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Listado de registros para el comboBox que lista el tipo de
     * Cuenta Bancaria
     */
    private List<Registro> listaTipoc;
    /**
     * Listado de registros para el comboBox que lista los anios
     */
    private List<Registro> listaAnio;
    /**
     * Listado de registros para el comboBox que lista los posibles
     * anios de destino
     */
    private List<Registro> listaAnioDes;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Listado de registros para el comboBox que lista las fuentes
     * para la cuenta bancaria
     */
    private RegistroDataModelImpl listaFuente;
    /**
     * Listado de registros para el comboBox que lista las fuentes
     * para la cuenta bancaria, cuando se esta editando un registro
     */
    private RegistroDataModelImpl listaFuenteE;
    /**
     * Listado de registros para el comboBox que lista los codigos de
     * las cuentas contables
     */
    private RegistroDataModelImpl listaIdContable;
    /**
     * Listado de registros para el comboBox que lista los codigos de
     * las cuentas contables, cuando se esta editando un registro
     */
    private RegistroDataModelImpl listaIdContableE;
    /**
     * Listado de registros para el comboBox que lista las entidades
     * bancarias
     */
    private RegistroDataModelImpl listaBanco;
    /**
     * Listado de registros para el comboBox que lista las entidades
     * bancarias, cuando se esta editando un registro
     */
    private RegistroDataModelImpl listaBancoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    @EJB
    private EjbChipFutCeroRemote ejbChipFutCero;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de CuentaBancosFutControlador
     * 
     * Inicializa los valores de anio y anioDestino
     */
    public CuentaBancosFutControlador() {
        super();
        compania = SessionUtil.getCompania();
        cBanco = "BANCO";
        cCodigo = "CODIGO";
        cIdContable = "IDCONTABLE";
        try {
            numFormulario = GeneralCodigoFormaEnum.CUENTA_BANCOS_FUT_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            anio = SysmanFunciones.ano(new Date());
            anioDes = anio + 1;
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
        tabla = "CUENTABANCOS";
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaTipoc();
        cargarListaAnio();
        cargarListaAnioDes();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaFuente();
        cargarListaFuenteE();
        cargarListaIdContable();
        cargarListaIdContableE();
        cargarListaBanco();
        cargarListaBancoE();
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

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CuentaBancosFutControladorUrlEnum.URL9594
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CuentaBancosFutControladorUrlEnum.URL96521
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaTipoc
     *
     */
    public void cargarListaTipoc() {
        try {
            listaTipoc = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            CuentaBancosFutControladorUrlEnum.URL9986
                                                                                            .getValue())
                                                            .getUrl(),
                                            null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaAnio
     *
     */
    public void cargarListaAnio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnio = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CuentaBancosFutControladorUrlEnum.URL10317
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaAnioDes
     *
     */
    public void cargarListaAnioDes() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnioDes = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            CuentaBancosFutControladorUrlEnum.URL10734
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaFuente
     *
     */
    public void cargarListaFuente() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CuentaBancosFutControladorUrlEnum.URL11151
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaFuente = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);

    }

    /**
     * 
     * Carga la lista listaFuente
     *
     */
    public void cargarListaFuenteE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CuentaBancosFutControladorUrlEnum.URL11859
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaFuenteE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    /**
     * 
     * Carga la lista listaIdContable
     *
     */
    public void cargarListaIdContable() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CuentaBancosFutControladorUrlEnum.URL12575
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaIdContable = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");

    }

    /**
     * 
     * Carga la lista listaIdContable
     *
     */
    public void cargarListaIdContableE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CuentaBancosFutControladorUrlEnum.URL12869
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaIdContableE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");

    }

    /**
     * 
     * Carga la lista listaBanco
     *
     */
    public void cargarListaBanco() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CuentaBancosFutControladorUrlEnum.URL13153
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaBanco = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cBanco);

    }

    /**
     * 
     * Carga la lista listaBanco
     *
     */
    public void cargarListaBancoE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CuentaBancosFutControladorUrlEnum.URL13750
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaBancoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cBanco);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton PreparaAno en la vista
     *
     * Realiza el llamado a la funcion FC_PREPARAANIO ubicada en el
     * paquete PCK_SCHIP, donde se realiza la copia de cuentas de un
     * anio origen a uno destino que ha sido seleccionado en el
     * formulario
     *
     */
    public void oprimirPreparaAno() {
        // <CODIGO_DESARROLLADO>
        int respuesta = 0;
        try {
            respuesta = ejbChipFutCero.prepararAnio(compania, anio, anioDes);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (respuesta == 0) {
            String msj = idioma.getString("TB_TB3015");
            msj = msj.replace("s$anioDestino$s",
                            String.valueOf(anioDes));
            JsfUtil.agregarMensajeInformativo(msj);
        }
        String msj = idioma.getString("TB_TB3016");
        msj = msj.replace("s$anioDestino$s", String.valueOf(anioDes));
        JsfUtil.agregarMensajeInformativo(msj);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton ConFuentes en la vista
     *
     * Redirecciona al formulario ConfigurarFuenteFuts(1385) que
     * permite realizar la configuracion de las fuentes FUT
     *
     */
    public void oprimirConFuentes() {
        // <CODIGO_DESARROLLADO>
        SessionUtil.redireccionar("/configurarfuentefut.sysman");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     * Define las acciones necesarias para generar el informe
     * "Configuracion_cuentasBancos" realiza el reemplazo de valores
     * en la consulta del informe y exporta el archivo en formato
     * Excel
     *
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("anio", anio);

            String strSql = Reporteador
                            .resuelveConsulta("800101CuentasBancos",
                                            Integer.parseInt(
                                                            SessionUtil.getModulo()),
                                            reemplazar);
            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL97,
                            idioma.getString("TB_TB3018"));
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Al seleccionar un anio digerente en el formulario, se debe
     * recargar el formulario y el listado de los combos que dependen
     * del anio que se esta tranajando
     */
    public void cambiarAnio() {
        buscarLlave();
        reasignarOrigen();
        cargarListaFuente();
        cargarListaFuenteE();
        cargarListaIdContable();
        cargarListaIdContableE();
    }

    /**
     * Metodo ejecutado al cambiar el control IdContable en la fila
     * seleccionada dentro de la grilla
     * 
     * Valida que el nuevo valor seleccionado para IdContable no se
     * haya configurado previamente
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarIdContableC(int rowNum) {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cIdContable, auxiliar);
        if (auxiliar == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2999"));
        }
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuente
     *
     * Almacena el valor seleccionado en el campo RECURSOS al registro
     * con el que se esta trabajando
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuente(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("RECURSOS",
                        registroAux.getCampos().get(cCodigo));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuente
     *
     * Almacena el valor seleccionado en el campo RECURSOS al registro
     * que se esta editando
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get(cCodigo);
    }

    /**
     * Permite identificar si una cuenta seleccionada ya ha sido
     * configurada
     * 
     * @param id
     * Numero de cuenta seleccionada en el combo
     * @return falso cuando la cuenta ya existe
     */
    private boolean autorizarCuenta(String id) {
        boolean respuesta;
        String sql = "SELECT COUNT(*) TOTAL " +
            "  FROM CUENTABANCOS " +
            " WHERE COMPANIA   = '" + compania + "' " +
            "   AND IDCONTABLE = '" + id + "' " +
            "   AND ANO        = " + anio;
        Registro rs = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN, sql);

        if ((rs != null)
            && (Integer.parseInt(rs.getCampos().get("TOTAL").toString()) > 0)) {
            respuesta = false;
        }
        else {
            respuesta = true;
        }
        return respuesta;
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaIdContable
     *
     * Almacena el valor seleccionado en el campo IDCONTABLE al
     * registro con el que se esta trabajando, evalua que el valor de
     * la cuenta seleccionada no se haya configurado previamente
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaIdContable(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        if (autorizarCuenta(registroAux.getCampos().get("ID").toString())) {
            registro.getCampos().put(cIdContable,
                            registroAux.getCampos().get("ID"));
        }
        else {
            registro.getCampos().put(cIdContable, null);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2999"));
        }
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaIdContable
     *
     * Almacena el valor seleccionado en el campo IDCONTABLE al
     * registro que se esta editando, evalua que el valor de la cuenta
     * seleccionada no se haya configurado previamente
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaIdContableE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        auxiliar = (String) (autorizarCuenta(
                        (String) registroAux.getCampos().get("ID"))
                            ? registroAux.getCampos().get("ID")
                            : null);

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaBanco
     *
     * Almacena el valor que ha sido seleccionado en el campo BANCO al
     * registro con el que se esta trabajando
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBanco(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cBanco, registroAux.getCampos().get(cBanco));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaBanco
     *
     * Almacena el valor que ha sido seleccionado en el campo BANCO al
     * registro con que se esta editando
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBancoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get(cBanco);
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
        try {
            Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN,
                            tabla,
                            "ANO  = " + anio + " - 1",
                            "COMPANIA = '" + compania + "' " +
                                "  AND  (   ANO = 0 " +
                                "        OR ANO IS NULL)");
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
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
     * Almacena en el registro los campos de COMPANIA y ANO que forman
     * parte de la llave principal de la tabla CUENTABANCOS
     * 
     * @return Si el proceso previo a la insercion fue exitoso
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("ANO", anio);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return Si el proceso de insercion fue exitoso
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
     * Elimina del origen de datos los campos NOMBREBANCO,
     * NOMBREFUENTE, NOMBRETIPOCUENTA, NOMBRECIERREF que no son campos
     * pertenecientes a la tabla CUENTABANCOS, pero que han sido
     * definidos en el origen para su visualizacion en el formulario
     * 
     * @return Si el proceso previo a la actualizacion fue exitoso
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("NOMBREBANCO");
        registro.getCampos().remove("NOMBREFUENTE");
        registro.getCampos().remove("NOMBRETIPOCUENTA");
        registro.getCampos().remove("NOMBRECIERREF");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * @return Si el proceso de actualizacion fue exitoso
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
     * @return Si el proceso previo a la eliminacion fue exitoso
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
     * @return Si el proceso de eliminacion fue exitoso
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
        registro.getCampos().remove("NOMBRECIERREF");
        registro.getCampos().remove("NOMBREBANCO");
        registro.getCampos().remove("NOMBREFUENTE");
        registro.getCampos().remove("NOMBRETIPOCUENTA");
        registro.getCampos().remove("NOMBRETIPOSALUD");
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove("IDCONTABLE");
        registro.getCampos().remove("CUENTANUMERO");

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
     * Retorna la variable anio
     * 
     * @return
     */
    public int getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(int anio) {
        this.anio = anio;
    }

    /**
     * Retorna la variable anioDes
     * 
     * @return anioDes
     */
    public int getAnioDes() {
        return anioDes;
    }

    /**
     * Asigna la variable anioDes
     * 
     * @param anioDes
     * Variable a asignar en anioDes
     */
    public void setAnioDes(int anioDes) {
        this.anioDes = anioDes;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaTipoc
     * 
     * @return listaTipoc
     */
    public List<Registro> getListaTipoc() {
        return listaTipoc;
    }

    /**
     * Asigna la lista listaTipoc
     * 
     * @param listaTipoc
     * Variable a asignar en listaTipoc
     */
    public void setListaTipoc(List<Registro> listaTipoc) {
        this.listaTipoc = listaTipoc;
    }

    /**
     * Retorna la lista listaAnio
     * 
     * @return listaAnio
     */
    public List<Registro> getListaAnio() {
        return listaAnio;
    }

    /**
     * Asigna la lista listaAnio
     * 
     * @param listaAnio
     * Variable a asignar en listaAnio
     */
    public void setListaAnio(List<Registro> listaAnio) {
        this.listaAnio = listaAnio;
    }

    /**
     * Retorna la lista listaAnioDes
     * 
     * @return listaAnioDes
     */
    public List<Registro> getListaAnioDes() {
        return listaAnioDes;
    }

    /**
     * Asigna la lista listaAnioDes
     * 
     * @param listaAnioDes
     * Variable a asignar en listaAnioDes
     */
    public void setListaAnioDes(List<Registro> listaAnioDes) {
        this.listaAnioDes = listaAnioDes;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaFuente
     * 
     * @return listaFuente
     */
    public RegistroDataModelImpl getListaFuente() {
        return listaFuente;
    }

    /**
     * Asigna la lista listaFuente
     * 
     * @param listaFuente
     * Variable a asignar en listaFuente
     */
    public void setListaFuente(RegistroDataModelImpl listaFuente) {
        this.listaFuente = listaFuente;
    }

    /**
     * Retorna la lista listaFuente
     * 
     * @return listaFuente
     */
    public RegistroDataModelImpl getListaFuenteE() {
        return listaFuenteE;
    }

    /**
     * Asigna la lista listaFuente
     * 
     * @param listaFuente
     * Variable a asignar en listaFuente
     */
    public void setListaFuenteE(RegistroDataModelImpl listaFuenteE) {
        this.listaFuenteE = listaFuenteE;
    }

    /**
     * Retorna la lista listaIdContable
     * 
     * @return listaIdContable
     */
    public RegistroDataModelImpl getListaIdContable() {
        return listaIdContable;
    }

    /**
     * Asigna la lista listaIdContable
     * 
     * @param listaIdContable
     * Variable a asignar en listaIdContable
     */
    public void setListaIdContable(RegistroDataModelImpl listaIdContable) {
        this.listaIdContable = listaIdContable;
    }

    /**
     * Retorna la lista listaIdContable
     * 
     * @return listaIdContable
     */
    public RegistroDataModelImpl getListaIdContableE() {
        return listaIdContableE;
    }

    /**
     * Asigna la lista listaIdContable
     * 
     * @param listaIdContable
     * Variable a asignar en listaIdContable
     */
    public void setListaIdContableE(RegistroDataModelImpl listaIdContableE) {
        this.listaIdContableE = listaIdContableE;
    }

    /**
     * Retorna la lista listaBanco
     * 
     * @return listaBanco
     */
    public RegistroDataModelImpl getListaBanco() {
        return listaBanco;
    }

    /**
     * Asigna la lista listaBanco
     * 
     * @param listaBanco
     * Variable a asignar en listaBanco
     */
    public void setListaBanco(RegistroDataModelImpl listaBanco) {
        this.listaBanco = listaBanco;
    }

    /**
     * Retorna la lista listaBanco
     * 
     * @return listaBanco
     */
    public RegistroDataModelImpl getListaBancoE() {
        return listaBancoE;
    }

    /**
     * Asigna la lista listaBanco
     * 
     * @param listaBanco
     * Variable a asignar en listaBanco
     */
    public void setListaBancoE(RegistroDataModelImpl listaBancoE) {
        this.listaBancoE = listaBancoE;
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
