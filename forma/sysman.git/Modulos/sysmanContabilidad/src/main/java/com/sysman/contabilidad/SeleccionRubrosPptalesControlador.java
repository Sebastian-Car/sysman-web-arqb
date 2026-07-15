/*-
 * SeleccionRubrosPptalesControlador.java
 *
 * 1.0
 *
 * 23/05/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.contabilidad.ejb.EjbContabilidadUnoRemote;
import com.sysman.contabilidad.enums.SeleccionRubrosPptalesControladorEnum;
import com.sysman.contabilidad.enums.SeleccionRubrosPptalesControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.logica.Formulario;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite seleccionar las equivalencias presupuestales
 * por cada cuenta contable, para la generaci&oacute;n del comprobante
 * presupuestal de ingresos.
 *
 * @version 1.0, 23/05/2017
 * @author jrodrigueza
 *
 * @version 2, 12/06/2017 jrodriguezr Se refactoriza el c&oacute;digo:
 * Se pasa el numero del formulario al enumerado, se eliminan
 * conexiones y se ajustan metodos de generacion de reportes.
 * 
 * @version 3, 15/08/2017 Actualizaci&oacute;n del detalle contable,
 * asociando el rubro presupuestal seleccionado durante el proceso con
 * la respectiva cuenta contable.
 * @author jrodrigueza
 */
@ManagedBean
@ViewScoped
public class SeleccionRubrosPptalesControlador {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;
    /**
     * Administrador de peticiones API.
     */
    private RequestManager requestManager;
    // <DECLARAR_ATRIBUTOS>
    /**
     * N&uacute;mero de formulario
     */
    private int numFormulario;
    /**
     * Objeto para el registro de mensajes.
     */
    private final Log logger = LogFactory.getLog(this.getClass());
    /**
     * Recurso para acceder al properties de idiomas.
     */
    private ResourceBundle idioma;
    /**
     * Permisos concedidos al formulario.
     */
    private boolean[] permisos;
    /**
     * Objeto para simular un registro de la base de datos.
     */
    private Registro registro;
    /**
     * Anio de creacion del comprobante contable.
     */
    private Object anio;
    /**
     * Almacena el c&oacute;digo de la cuenta contable al activar un
     * registro.
     */
    private Object cuentaContable;
    /**
     * Parametro RUBRO_PPTAL.
     */
    private final String parRubroPptal;
    /**
     * Contiene los datos necesarios del comprobante contable, para
     * ejecutar el proceso:
     * {@link #generarComprobantePresupuestal(List)}.
     */
    private Map<String, Object> comprobanteCnt;
    /**
     * Implementacion del EJB de ejbContabilidadUno para hacer el
     * llamado a las funciones y EjbPresupuestoUnoRemote que se
     * invocan dentro del Controlador y se encuentran almacenadas en
     * el paquete PCK_PRESUPUESTO_COM1
     */
    @EJB
    private EjbContabilidadUnoRemote contabilidadUno;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista para cargar el origen de datos.
     */
    private List<Registro> listaInicial;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de rubros presupuestales configurados en la equivalencia
     * presupuestal de la cuenta contable. Lista de formulario nuevo
     * registro. (SIN USAR)
     */
    private RegistroDataModelImpl listaRubroPptal;
    /**
     * Lista de rubros presupuestales configurados en la equivalencia
     * presupuestal de la cuenta contable. Lista del formulario
     * continuo.
     */
    private RegistroDataModelImpl listaRubroPptalE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Crea una nueva instancia de SeleccionRubrosPptalesControlador
     */
    public SeleccionRubrosPptalesControlador() {
        super();
        compania = SessionUtil.getCompania();
        parRubroPptal = SeleccionRubrosPptalesControladorEnum.RUBRO_PPTAL
                        .getValue();
        listaInicial = new ArrayList<>();
        idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
        registro = new Registro(new HashMap<String, Object>());
        try {
            numFormulario = GeneralCodigoFormaEnum.SELECCION_RUBROS_PPTALES_CONTROLADOR
                            .getCodigo();
            requestManager = new RequestManager();
            validarPermisos();
            // <INI_ADICIONAL>
            traerParametrosFlash();
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.getFlash().clear();
        }
    }

    /**
     * Captura de parametros enviados por metodo <i>Flash</i>.
     * <ul>
     * <li>Captura de los datos del comprobante contable</li>
     * <li>Captura del anio del comprobante contable</li>
     * <li>Creaci&oacute;n del origen de datos a partir de la lista de
     * registros enviada desde el formulario de comprobante contable.
     * </li>
     * </ul>
     */
    @SuppressWarnings("unchecked")
    private void traerParametrosFlash() {
        Map<String, Object> parametros = SessionUtil.getFlash();
        if (parametros != null) {
            comprobanteCnt = (Map<String, Object>) parametros
                            .get("PAR_COMPTE_CNT");
            anio = comprobanteCnt.get(GeneralParameterEnum.ANO.getName());
            listaInicial = (List<Registro>) parametros
                            .get("PAR_LIST_EQUIVALENCIAS");
        }
    }

    /**
     * Validaci&oacute;n de permisos para el formulario.
     *
     * @throws SysmanException
     */
    public void validarPermisos() throws SysmanException {
        if (permisos == null) {
            Formulario form = SessionUtil.cargarFormulario(
                            numFormulario + "," + SessionUtil.getModulo());
            if (form == null) {
                throw new SysmanException(
                                idioma.getString("MSM_REGISTRO_MODIFICADO"));
            }
            permisos = form.getPermisos();
            if ((permisos == null) || !permisos[3]) {
                throw new SysmanException(
                                idioma.getString("MSM_PERMISOS_ACCEDER"));
            }
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos,
     * inicializaci&oacute;n de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaRubroPptal();
        cargarListaRubroPptalE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * Retorna la variable indice
     *
     * @return indice
     */
    public int getIndice() {
        return indice;
    }

    /**
     * Acciones que se ejecutan antes de renderizar la forma.
     */
    public void cargarForma() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaRubroPptal del formulario nuevo registro.
     */
    public void cargarListaRubroPptal() {
        String urlEnumId = SeleccionRubrosPptalesControladorUrlEnum.URL1879
                        .getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CUENTA.getName(),
                        registro.getCampos().get("CUENTA"));

        listaRubroPptal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaCuentaPptal asociada al formulario
     * continuo.
     */
    public void cargarListaRubroPptalE() {
        String urlEnumId = SeleccionRubrosPptalesControladorUrlEnum.URL1879
                        .getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CUENTA.getName(), cuentaContable);

        listaRubroPptalE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Acciones ejecutadas al oprimir el bot&oacute;n generar en la
     * vista.
     */
    public void oprimirGenerar() {
        // <CODIGO_DESARROLLADO>
        // Validacion de rubros seleccionados.
        for (Registro reg : listaInicial) {
            String rubro = extraerString(
                            reg.getCampos().get(parRubroPptal));
            if (SysmanFunciones.validarVariableVacio(rubro)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3177"));
                return;
            }
        }
        // Ejecucion del procedimiento
        generarComprobantePresupuestal(listaInicial);
        // Guarda el rubro presupuestal referencia para la afectacion
        guardarCuentaPptal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * En el detalle contable asocia el rubro presupuestal
     * seleccionado para la generaci&oacute;n del comprobante
     * presupuestal, con la respectiva cuenta contable.
     */
    private void guardarCuentaPptal() {
        for (Registro reg : listaInicial) {
            Map<String, Object> campos = reg.getCampos();
            Map<String, Object> parametros = new HashMap<>();
            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametros.put(GeneralParameterEnum.ANO.getName(), anio);
            parametros.put(GeneralParameterEnum.TIPO_CPTE.getName(), campos
                            .get(GeneralParameterEnum.TIPO_CPTE.getName()));
            parametros.put(GeneralParameterEnum.COMPROBANTE.getName(), campos
                            .get(GeneralParameterEnum.COMPROBANTE.getName()));
            parametros.put(GeneralParameterEnum.CONSECUTIVO.getName(), campos
                            .get(GeneralParameterEnum.CONSECUTIVO.getName()));
            parametros.put(SeleccionRubrosPptalesControladorEnum.CUENTAPPTAL
                            .getValue(),
                            campos.get(SeleccionRubrosPptalesControladorEnum.RUBRO_PPTAL
                                            .getValue()));
            parametros.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            String ulrEnumId = SeleccionRubrosPptalesControladorUrlEnum.URL33613
                            .getValue();
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(ulrEnumId);
            Parameter parameter = new Parameter();
            parameter.setFields(parametros);
            try {
                requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                                parameter);
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    /**
     * Ejecuta el procedimiento que genera el comprobante
     * presupuestal.
     * <q>Desde un registro de comprobantes contables, realiza la
     * generaci&oacute;n de un comprobante presupuestal asociado.</q>
     */
    private void generarComprobantePresupuestal(List<Registro> lista) {
        int ano = Integer.parseInt(extraerString(comprobanteCnt.get("ANO")));
        BigInteger numero = new BigInteger(
                        extraerString(comprobanteCnt.get("NUMERO")));
        Date fecha = (Date) comprobanteCnt.get("FECHA");
        String tercero = extraerString(comprobanteCnt.get("TERCERO"));
        String sucursal = extraerString(comprobanteCnt.get("SUCURSAL"));
        String descripcion = extraerString(comprobanteCnt.get("DESCRIPCION"));
        String numeroDoc = extraerString(comprobanteCnt.get("NRO_DOCUMENTO"));
        BigDecimal valorDoc = new BigDecimal(
                        extraerString(comprobanteCnt.get("VLR_DOCUMENTO")));
        String tipoCnt = extraerString(comprobanteCnt.get("TIPO"));
        String tipoPptal = extraerString(comprobanteCnt.get("TIPOPPTAL"));
        String cadenaInsertar = traerCadenaRegistros(lista);
        int numeroRegistros = lista.size();
        String usuario = SessionUtil.getUser().getCodigo();
        try {
            contabilidadUno.generarComprobantePresupuestal(compania, ano,
                            tipoCnt, numero, fecha, tercero, sucursal,
                            descripcion, numeroDoc, valorDoc, tipoPptal,
                            cadenaInsertar, numeroRegistros, usuario);
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Construye una cadena que representan los registros de las
     * equivalencias presupuestales separadas por punto y coma. Donde
     * cada registro contiene los campos consecutivo, cuenta, valor y
     * rubro presupuestal, separados por coma.<br>
     * <b>Ej:</b>
     * CONSECUTIVO,CUENTA,VALOR,RUBRO_PPTAL;CONSECUTIVO,CUENTA,VALOR,
     * RUBRO_PPTAL
     *
     * @param equivalenciasPptales
     * Lista de equivalencias presupuestales.
     * @return cadena para insertar en el procedimiento que genera el
     * comprobante presupuestal.
     */
    private String traerCadenaRegistros(List<Registro> equivalenciasPptales) {
        StringBuilder stringBuilder = new StringBuilder();
        Object[] params = new Object[4];
        for (Registro equivalencia : equivalenciasPptales) {
            Map<String, Object> campos = equivalencia.getCampos();
            for (Map.Entry<String, Object> entry : campos.entrySet()) {
                switch (entry.getKey()) {
                case "CONSECUTIVO":
                    params[0] = entry.getValue();
                    break;
                case "CUENTA":
                    params[1] = entry.getValue();
                    break;
                case "VALOR":
                    params[2] = entry.getValue();
                    break;
                case "RUBRO_PPTAL":
                    params[3] = entry.getValue();
                    break;
                default:
                    break;
                }
            }
            stringBuilder.append("" + params[0] + (char) 44 + params[1]
                + (char) 44 + params[2] + (char) 44 + params[3]);
            stringBuilder.append((char) 59);
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    /**
     * Extrae la cadena que representa al objeto, solo si es diferente
     * de nulo.
     *
     * @param object
     * Un Objeto
     * @return String que representa al objeto
     */
    private String extraerString(Object object) {
        return object != null ? object.toString() : null;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaRubroPptal.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaRubroPptal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("RUBRO_PPTAL",
                        registroAux.getCampos().get("CODIGO"));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaRubroPptal del formulario continuo.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaRubroPptalE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = extraerString(registroAux.getCampos().get("CODIGO"));
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Acciones ejecutadas al editar un registro del formulario.
     *
     * @param event
     */
    public void editar(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        reg.getCampos().remove("RNUM");
        reg.getCampos().remove("RID");
        registro = reg;
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro.
     */
    public void cancelarEdicion(RowEditEvent event) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se activa la edici&oacute;n de un
     * registro del formulario.
     *
     * @param registro
     * registro del cual se activo la edici&oacute;n
     */
    public void activarEdicion(Registro registro) {
        indice = listaInicial.size();
        Map<String, Object> registroIni = registro.getCampos();
        cuentaContable = registroIni.get(GeneralParameterEnum.CUENTA.getName());
        cargarListaRubroPptalE();
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario.
     * Cancelaci&oacute;n del proceso.
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>

    public boolean[] getPermisos() {
        return permisos;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public void setPermisos(boolean[] permisos) {
        this.permisos = permisos;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaInicial() {
        return listaInicial;
    }

    public void setListaInicial(List<Registro> listaInicial) {
        this.listaInicial = listaInicial;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaRubroPptal
     *
     * @return listaRubroPptal
     */
    public RegistroDataModelImpl getListaRubroPptal() {
        return listaRubroPptal;
    }

    /**
     * Asigna la lista listaRubroPptal
     *
     * @param listaRubroPptal
     * Variable a asignar en listaRubroPptal
     */
    public void setListaRubroPptal(RegistroDataModelImpl listaRubroPptal) {
        this.listaRubroPptal = listaRubroPptal;
    }

    /**
     * Retorna la lista listaRubroPptal
     *
     * @return listaRubroPptal
     */
    public RegistroDataModelImpl getListaRubroPptalE() {
        return listaRubroPptalE;
    }

    /**
     * Asigna la lista listaRubroPptal
     *
     * @param listaRubroPptal
     * Variable a asignar en listaRubroPptal
     */
    public void setListaRubroPptalE(RegistroDataModelImpl listaRubroPptalE) {
        this.listaRubroPptalE = listaRubroPptalE;
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
