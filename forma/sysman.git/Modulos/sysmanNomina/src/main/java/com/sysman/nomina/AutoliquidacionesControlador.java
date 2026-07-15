/*-
 * AutoliquidacionesControlador.java
 *
 * 1.0
 * 
 * 28/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
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
import com.sysman.nomina.enums.AutoliquidacionesControladorEnum;
import com.sysman.nomina.enums.AutoliquidacionesControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Clase migrada para administrar los riesgos de un empleado
 * dependiendo del ano y el mes del periodo de trabajo seleccionado
 *
 * @version 1.0, 28/12/2017
 * @author ybecerra
 * 
 */
@ManagedBean
@ViewScoped
public class AutoliquidacionesControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el nit de la compania en
     * la cual inicio sesion el usuario.
     */
    private final String nitCompania = SessionUtil.getCompaniaIngreso()
                    .getNit();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>.
     */
    private final String cCompania = GeneralParameterEnum.COMPANIA.getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>NIT</code>.
     */
    private final String cNit = AutoliquidacionesControladorEnum.NIT.getValue();

    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que almacena el valor del ano
     */
    private String ano;
    /**
     * Variable que almacena el valor del mes
     */
    private String mes;
    /**
     * Variable que almacena el valor del proceso recibido por
     * parametro
     */
    private String proceso;
    /**
     * Variable que almacena el valor del proceso recibido por
     * parametro
     */
    private String periodo;
    /**
     * Variable que almacena el valor del fondo de riesgo seleccionado
     */
    private String fondoRiesgos;
    /**
     * Variable que almacena el nombre del fondo seleccionado
     */
    private String nombreFondo;
    /**
     * variable que valida si el boton se activa o inactiva
     */
    private boolean activarRiesgos;
    /**
     * variable que almacena el total de los dias de vacaciones del
     * subformulario
     */
    private String diasVacaciones;
    /**
     * Variable que almacena el total de los dias de incapacidades del
     * subformulario
     */
    private String diasIncapacidades;
    /**
     * Variable que almacena el total del IBC del subformulario
     */
    private String ibc;
    /**
     * Variable que almacean el total de los dias del subformulario
     */
    private String dias;
    /**
     * Variable que almacena el total del salario basico del
     * subformulario
     */
    private String sueldo;
    /**
     * Variable que almacena el total de las cotizaciones del
     * subformulario
     */
    private String cotizacion;
    /**
     * variable que almacena el valor de la sucursal del codigo de
     * fondo seleccionado
     */
    private String sucursal;
    /**
     * variable que almacena el valor del nit del codigo de fondo
     * seleccionado
     */
    private String nitFondo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de registros de los fondos de riesgos
     */
    private RegistroDataModelImpl listaFondoRiesgos;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * Lista de registros del subformulario riesgos
     */
    private RegistroDataModelImpl listaAutoriesgos;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario
     */
    private Registro registroSub;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de AutoliquidacionesControlador
     */
    public AutoliquidacionesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 1554
            numFormulario = GeneralCodigoFormaEnum.AUTOLIQUIDACION_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>

            registroSub = new Registro(new HashMap<String, Object>());

            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaFondoRiesgos();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
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
        listaAutoriesgos = null;
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
        tabla = GenericUrlEnum.PARAMETROS_DE_ENTRADA.getTable();

        buscarLlave();
        asignarOrigenDatos();
        abrirFormulario();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        urlLectura = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AutoliquidacionesControladorUrlEnum.URL19179
                                                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AutoliquidacionesControladorUrlEnum.URL9702
                                                        .getValue());
        parametrosListado.put(
                        AutoliquidacionesControladorEnum.KEY_COMPANIA
                                        .getValue(),
                        compania);
        parametrosListado.put(
                        AutoliquidacionesControladorEnum.KEY_NIT.getValue(),
                        SessionUtil.getCompaniaIngreso().getNit());

    }

    /**
     * 
     * Carga la lista listaAutoriesgos
     */
    public void cargarListaAutoriesgos() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AutoliquidacionesControladorUrlEnum.URL10890
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(),
                        proceso);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano);

        param.put(GeneralParameterEnum.MES.getName(),
                        SessionUtil.getSessionVar(
                                        AutoliquidacionesControladorEnum.MESNOMINA
                                                        .getValue()));
        param.put(GeneralParameterEnum.PERIODO.getName(),
                        periodo);

        param.put(AutoliquidacionesControladorEnum.FONDORIESGOS.getValue(),
                        fondoRiesgos);

        try {
            listaAutoriesgos = new RegistroDataModelImpl(
                            urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            CacheUtil.getLlaveServicio(
                                            urlConexionCache,
                                            GenericUrlEnum.HISTORICOS
                                                            .getTable()));
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaFondoRiesgos
     */
    public void cargarListaFondoRiesgos() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AutoliquidacionesControladorUrlEnum.URL15182
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaFondoRiesgos = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "FONDO_RIESGOS");

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFondoRiesgos
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFondoRiesgos(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fondoRiesgos = SysmanFunciones
                        .nvl(registroAux.getCampos().get("FONDO_RIESGOS"), "")
                        .toString();
        nombreFondo = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get("NOMBRE_FONDO_RIESGOS"), "")
                        .toString();

        nitFondo = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(AutoliquidacionesControladorEnum.NIT
                                                        .getValue()),
                                        "")
                        .toString();

        sucursal = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()),
                                        "")
                        .toString();

        if (SysmanFunciones.validarVariableVacio(fondoRiesgos)) {
            activarRiesgos = true;
        }
        else {
            activarRiesgos = false;
        }

        cargarListaAutoriesgos();
        calcularTotales();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton TOTRIESGOS en la vista
     *
     */
    public void oprimirTOTRIESGOS() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "codigoFondo", "nit", "sucursal", "nombreFondo" };
        Object[] valores = { fondoRiesgos, nitFondo, sucursal, nombreFondo };
        SessionUtil.cargarModalDatosFlash(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.RIESGOS_CONTROLADOR
                                                        .getCodigo()),
                        SessionUtil.getModulo(), campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario Autoriesgos
     */
    public void agregarRegistroSubAutoriesgos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo de edicion del formulario Autoriesgos
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubAutoriesgos(RowEditEvent event) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo de eliminacion del formulario Autoriesgos
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubAutoriesgos(Registro reg) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Autoriesgos
     */
    public void cancelarEdicionAutoriesgos() {
        cargarListaAutoriesgos();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    /**
     * Metodo que se ejcuta al cargar registros del subformulario,
     * realiza la sumatoria de las columas
     */
    public void calcularTotales() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.MES.getName(),
                        SessionUtil.getSessionVar(
                                        AutoliquidacionesControladorEnum.MESNOMINA
                                                        .getValue()));
        param.put(GeneralParameterEnum.PERIODO.getName(), periodo);
        param.put(AutoliquidacionesControladorEnum.FONDORIESGOS.getValue(),
                        fondoRiesgos);

        try {
            Registro rsTotales = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AutoliquidacionesControladorUrlEnum.URL6873
                                                                            .getValue())
                                            .getUrl(), param));

            diasVacaciones = rsTotales.getCampos().get("DIASVAC")
                            .toString();
            diasIncapacidades = rsTotales.getCampos().get("DIASINCAPACIDAD")
                            .toString();
            dias = rsTotales.getCampos().get("TOTALDIAS").toString();
            sueldo = asignarFormato(rsTotales.getCampos()
                            .get("SUELDO").toString());

            ibc = asignarFormato(rsTotales.getCampos().get("IBC").toString());
            cotizacion = asignarFormato(
                            rsTotales.getCampos().get("COTRIESGOS").toString());

        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * Obtiene el valor del campo ingresado por parametro y le asigna
     * el formato de moneda
     * 
     * @param reg
     * Registro en el que se va a cosultar el valor
     * @param campo
     * Nombre del campo a consultar en el registro ingresao por
     * parametro
     * @return Valor del campo ingresado por parametro con formato
     * moneda
     */
    private String asignarFormato(String campo) {

        DecimalFormatSymbols valoresDecimales = new DecimalFormatSymbols(
                        Locale.US);
        return new DecimalFormat("#,##0.00", valoresDecimales)
                        .format(Double.parseDouble(campo));

    }

    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        proceso = (String) SessionUtil.getSessionVar("procesoNomina");
        ano = (String) SessionUtil.getSessionVar("anioNomina");
        mes = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(
                        SessionUtil.getSessionVar(
                                        AutoliquidacionesControladorEnum.MESNOMINA
                                                        .getValue())
                                        .toString())];
        periodo = (String) SessionUtil.getSessionVar("periodoNomina");
        activarRiesgos = true;

        validarEntrada();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
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
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
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
        if (ACCION_MODIFICAR.equals(accion)) {
            registro.getCampos().remove(cCompania);
            registro.getCampos().remove(cNit);
        }
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

    /**
     * Metodo que valida si el nit de la compania de ingreso existe en
     * los parametros de entrada.
     */
    private void validarEntrada() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cNit, nitCompania);

        Registro rsNit = null;

        try {
            rsNit = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AutoliquidacionesControladorUrlEnum.URL518
                                                                            .getValue())
                                            .getUrl(), param));

            if (rsNit != null) {
                cargarRegistro(parametrosListado, ACCION_MODIFICAR);
                calcularTotales();
            }
            else {
                SessionUtil.agregarMensajeErrorMenu(
                                idioma.getString("TB_TB3925").replace("#nit#",
                                                nitCompania));

                SessionUtil.redireccionarMenu();
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ano
     * 
     * @return ano
     */
    public String getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(String ano) {
        this.ano = ano;
    }

    /**
     * Retorna la variable mes
     * 
     * @return mes
     */
    public String getMes() {
        return mes;
    }

    /**
     * Asigna la variable mes
     * 
     * @param mes
     * Variable a asignar en mes
     */
    public void setMes(String mes) {
        this.mes = mes;
    }

    /**
     * Retorna la variable fondoRiesgos
     * 
     * @return fondoRiesgos
     */
    public String getFondoRiesgos() {
        return fondoRiesgos;
    }

    /**
     * Asigna la variable fondoRiesgos
     * 
     * @param fondoRiesgos
     * Variable a asignar en fondoRiesgos
     */
    public void setFondoRiesgos(String fondoRiesgos) {
        this.fondoRiesgos = fondoRiesgos;
    }

    /**
     * Retorna la variable proceso
     * 
     * @return proceso
     */
    public String getProceso() {
        return proceso;
    }

    /**
     * Asigna la variable proceso
     * 
     * @param proceso
     * Variable a asignar en proceso
     */
    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    /**
     * Retorna la variable periodo
     * 
     * @return periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * Asigna la variable periodo
     * 
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    /**
     * Retorna la variable nombreFondo
     * 
     * @return nombreFondo
     */
    public String getNombreFondo() {
        return nombreFondo;
    }

    /**
     * Asigna la variable nombreFondo
     * 
     * @param nombreFondo
     * Variable a asignar en nombreFondo
     */
    public void setNombreFondo(String nombreFondo) {
        this.nombreFondo = nombreFondo;
    }

    /**
     * Retorna la variable activarRiesgos
     * 
     * @return activarRiesgos
     */
    public boolean isActivarRiesgos() {
        return activarRiesgos;
    }

    /**
     * Asigna la variable activarRiesgos
     * 
     * @param activarRiesgos
     * Variable a asignar en activarRiesgos
     */
    public void setActivarRiesgos(boolean activarRiesgos) {
        this.activarRiesgos = activarRiesgos;
    }

    /**
     * Retorna la variable diasVacaciones
     * 
     * @return diasVacaciones
     */
    public String getDiasVacaciones() {
        return diasVacaciones;
    }

    /**
     * Asigna la variable diasVacaciones
     * 
     * @param diasVacaciones
     * Variable a asignar en diasVacaciones
     */
    public void setDiasVacaciones(String diasVacaciones) {
        this.diasVacaciones = diasVacaciones;
    }

    /**
     * Retorna la variable diasIncapacidades
     * 
     * @return diasIncapacidades
     */
    public String getDiasIncapacidades() {
        return diasIncapacidades;
    }

    /**
     * Asigna la variable diasIncapacidades
     * 
     * @param diasIncapacidades
     * Variable a asignar en diasIncapacidades
     */
    public void setDiasIncapacidades(String diasIncapacidades) {
        this.diasIncapacidades = diasIncapacidades;
    }

    /**
     * Retorna la variable ibc
     * 
     * @return ibc
     */
    public String getIbc() {
        return ibc;
    }

    /**
     * Asigna la variable ibc
     * 
     * @param ibc
     * Variable a asignar en ibc
     */
    public void setIbc(String ibc) {
        this.ibc = ibc;
    }

    /**
     * Retorna la variable dias
     * 
     * @return dias
     */
    public String getDias() {
        return dias;
    }

    /**
     * Asigna la variable dias
     * 
     * @param dias
     * Variable a asignar en dias
     */
    public void setDias(String dias) {
        this.dias = dias;
    }

    /**
     * Retorna la variable sueldo
     * 
     * @return sueldo
     */
    public String getSueldo() {
        return sueldo;
    }

    /**
     * Asigna la variable sueldo
     * 
     * @param sueldo
     * Variable a asignar en sueldo
     */
    public void setSueldo(String sueldo) {
        this.sueldo = sueldo;
    }

    /**
     * Retorna la variable cotizacion
     * 
     * @return cotizacion
     */
    public String getCotizacion() {
        return cotizacion;
    }

    /**
     * Asigna la variable cotizacion
     * 
     * @param cotizacion
     * Variable a asignar en cotizacion
     */
    public void setCotizacion(String cotizacion) {
        this.cotizacion = cotizacion;
    }

    /**
     * Retorna la variable sucursal
     * 
     * @return sucursal
     */
    public String getSucursal() {
        return sucursal;
    }

    /**
     * Asigna la variable sucursal
     * 
     * @param sucursal
     * Variable a asignar en sucursal
     */
    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    /**
     * Retorna la variable nitFondo
     * 
     * @return nitFondo
     */
    public String getNitFondo() {
        return nitFondo;
    }

    /**
     * Asigna la variable nitFondo
     * 
     * @param nitFondo
     * Variable a asignar en nitFondo
     */
    public void setNitFondo(String nitFondo) {
        this.nitFondo = nitFondo;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaFondoRiesgos
     * 
     * @return listaFondoRiesgos
     */
    public RegistroDataModelImpl getListaFondoRiesgos() {
        return listaFondoRiesgos;
    }

    /**
     * Asigna la lista listaFondoRiesgos
     * 
     * @param listaFondoRiesgos
     * Variable a asignar en listaFondoRiesgos
     */
    public void setListaFondoRiesgos(RegistroDataModelImpl listaFondoRiesgos) {
        this.listaFondoRiesgos = listaFondoRiesgos;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaAutoriesgos
     * 
     * @return listaAutoriesgos
     */
    public RegistroDataModelImpl getListaAutoriesgos() {
        return listaAutoriesgos;
    }

    /**
     * Asigna la lista listaAutoriesgos
     * 
     * @param listaAutoriesgos
     * Variable a asignar en listaAutoriesgos
     */
    public void setListaAutoriesgos(RegistroDataModelImpl listaAutoriesgos) {
        this.listaAutoriesgos = listaAutoriesgos;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSub
     * 
     * @return registroSub
     */
    public Registro getRegistroSub() {
        return registroSub;
    }

    /**
     * Asigna el objeto registroSub
     * 
     * @param registroSub
     * Variable a asignar en registroSub
     */
    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }
    // </SET_GET_ADICIONALES>
}
