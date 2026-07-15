/*-
 * SubEncargosControlador.java
 *
 * 1.0
 * 
 * 19/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.SubEncargosControladorEnum;
import com.sysman.hojasdevida.enums.SubEncargosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Esta clase es el controlador para el formulario Encargos -
 * Traslados en Access "Nat_SubEncargos", el cual es llamado desde
 * Menu Principal\Hojas De Vida\ Hojas De Vida \Datos Hoja De
 * Vida\Datos Basicos --> Pestaña Adicionales Botón Encargos-Traslados
 *
 * @version 1.0, 09/04/2018, Se realiza desarrollo a partir del
 * analisis descrito en el tar 1000081152
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class SubEncargosControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo ID_DE_CARGO en el formulario, almacena el
     * texto ID_DE_CARGO el cual es un campo del registro
     */
    private final String cIdDeCargo;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo CLASE_ENCARGO en el formulario, almacena el
     * texto CLASE_ENCARGO el cual es un campo del registro
     */
    private final String cClaseEncargo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Codigo asignado al empleado con el que se esta trabajando
     */
    private String codigoPersona;
    /**
     * Estructura que almacena los campos llave del personal con el
     * que se eta trabajando
     */
    private Map<String, Object> ridDatosPersonales;
    /**
     * Indicador que controla la visibilidad del combo
     * "Clase de Novedad"
     */
    private boolean visibleClaseNovedad;
    /**
     * Atributo que indica la visibilidad de los campos relacionados
     * con la seccion "Factor Salarial" en el formulario
     */
    private boolean visibleFactorSalarial;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Listado de registros para el combo "Detalle de Novedad"
     */
    private List<Registro> listaDetalleNovedadCargo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Listado de registros para el combo "Dependencia"
     */
    private RegistroDataModelImpl listaDependencia;
    /**
     * Listado de registros para el combo "Cargo"
     */
    private RegistroDataModelImpl listaCargo;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de SubEncargosControlador
     */
    @SuppressWarnings("unchecked")
    public SubEncargosControlador() {
        super();
        compania = SessionUtil.getCompania();
        cIdDeCargo = SubEncargosControladorEnum.ID_DE_CARGO.getValue();
        cClaseEncargo = SubEncargosControladorEnum.CLASE_ENCARGO.getValue();
        try {
            // 1538
            numFormulario = GeneralCodigoFormaEnum.SUBENCARGOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                ridDatosPersonales = (Map<String, Object>) parametrosEntrada
                                .get("rid");
                codigoPersona = (String) parametrosEntrada.get("idEmpleado");
            }
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
        cargarListaDependencia();
        cargarListaCargo();
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
        tabla = SubEncargosControladorEnum.ENCARGOS.getValue();
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * Realiza la asignacion de Urls Listado y Lectura
     * 
     */
    @Override
    public void asignarOrigenDatos() {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(),
                        codigoPersona);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubEncargosControladorUrlEnum.URL005
                                                        .getValue());
        urlLectura = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubEncargosControladorUrlEnum.URL006
                                                        .getValue());
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaDetalleNovedadCargo
     *
     * Asigna el origen de datos a la lista listaDetalleNovedadCargo
     * dependiendo la "Clase Novedad" seleccionada
     */
    public void cargarListaDetalleNovedadCargo() {
        String urlService = "";
        if ("1".equals(retornarString(registro, cClaseEncargo))) {
            urlService = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubEncargosControladorUrlEnum.URL001
                                                            .getValue())
                            .getUrl();
        }
        else if ("2".equals(retornarString(registro, cClaseEncargo))) {
            urlService = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubEncargosControladorUrlEnum.URL002
                                                            .getValue())
                            .getUrl();
        }

        Map<String, Object> param = new TreeMap<>();
        try {
            listaDetalleNovedadCargo = RegistroConverter.toListRegistro(
                            requestManager.getList(urlService, param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaDependencia
     *
     */
    public void cargarListaDependencia() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubEncargosControladorUrlEnum.URL003
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCargo
     *
     */
    public void cargarListaCargo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubEncargosControladorUrlEnum.URL004
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCargo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        cIdDeCargo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control TipoNovedad
     * 
     * Asigna valores a los atributos visibleClaseNovedad y
     * visibleFactorSalarial para permitir la visibilidad de algunos
     * campos en el formulario
     * 
     */
    public void cambiarTipoNovedad() {
        // <CODIGO_DESARROLLADO>
        visibleClaseNovedad = "1"
                        .equals(retornarString(registro,
                                        SubEncargosControladorEnum.TIPO_NOVEDAD
                                                        .getValue())) ? true
                                                            : false;
        visibleFactorSalarial = visibleClaseNovedad
            && "2".equals(retornarString(registro, cClaseEncargo))
                ? true : false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ClaseNovedad
     * 
     * Reacrga el listado de registros para el combo
     * "Detalle de Novedad" de acuerdo a la clase seleccionada
     * 
     */
    public void cambiarClaseNovedad() {
        // <CODIGO_DESARROLLADO>
        cargarListaDetalleNovedadCargo();
        visibleFactorSalarial = "2"
                        .equals(retornarString(registro, cClaseEncargo))
                            ? true : false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FechaInicio
     * 
     * Valida que la fecha inicial sea menor a la final, si no se
     * cumple esta condicion se asigna nulo a la fecha final
     * 
     */
    public void cambiarFechaInicio() {
        // <CODIGO_DESARROLLADO>
        if (validarFechas(GeneralParameterEnum.FECHAINICIO.getName(),
                        GeneralParameterEnum.FECHAFINAL.getName())) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB3867"));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FechaFinal
     * 
     * Valida que la fecha inicial sea menor a la final, si no se
     * cumple esta condicion se asigna nulo a la fecha final
     * 
     */
    public void cambiarFechaFinal() {
        // <CODIGO_DESARROLLADO>
        if (validarFechas(GeneralParameterEnum.FECHAINICIO.getName(),
                        GeneralParameterEnum.FECHAFINAL.getName())) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB3867"));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FechaActa
     * 
     * Valida si la fecha de acta es menor a la fecha del acto
     * administrativo
     * 
     */
    public void cambiarFechaActa() {
        // <CODIGO_DESARROLLADO>
        if (validarFechas(SubEncargosControladorEnum.FECHAACTA.getValue(),
                        SubEncargosControladorEnum.FECHAACTO.getValue())) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB3866"));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FechaEfectividad
     * 
     * Valida si la fecha de efectividd del Acta es menor a la fecha
     * del acto administrativo
     * 
     */
    public void cambiarFechaEfectividad() {
        // <CODIGO_DESARROLLADO>
        if (validarFechas(
                        SubEncargosControladorEnum.FECHAEFECTIVIDAD.getValue(),
                        SubEncargosControladorEnum.FECHAACTO.getValue())) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB3866"));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Evalua si los campos ingresados por parametro tienen valor en
     * el registro actual del formulario, adicionalmente compara si la
     * fecha inicial es menor a la final, definidas en los parametros
     * 
     * @param fechaInicial
     * Fecha inicial a comparar
     * @param fechaFinal
     * Fecha final a comparar
     * @return Verdadero si la fecha inicial es menor a la final
     */
    private boolean validarFechas(String fechaInicial, String fechaFinal) {
        boolean respuesta = false;
        if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        fechaInicial)
            && !SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            fechaFinal)
            && SysmanFunciones.comparaFechas(
                            (Date) registro.getCampos().get(fechaInicial),
                            (Date) registro.getCampos().get(fechaFinal))) {
            registro.getCampos().put(fechaInicial, null);
            respuesta = true;
        }
        return respuesta;
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDependencia
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        retornarString(registroAux,
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("NOMBRE_DEPENDENCIA",
                        retornarString(registroAux,
                                        GeneralParameterEnum.NOMBRE.getName()));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCargo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCargo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cIdDeCargo,
                        retornarString(registroAux, cIdDeCargo));
        registro.getCampos().put("NOMBRE_DEL_CARGO",
                        retornarString(registroAux, "NOMBRE_DEL_CARGO"));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    /**
     * Evalua si el campo ingresado por parametro se encuentra nulo
     * dentro del registro que tambien ha sido ingresado por parametro
     * 
     * @param reg
     * Registro en el que se desea evaluar el campo
     * @param campo
     * Campo que se desea consultar
     * @return Cadena vacia o el valor del campo
     */
    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
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
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     * Asigna valor a los atributos que permiten la visibilidad de
     * algunos campos en el formulario
     * 
     * Recarga el listado para el combo "Detalle de Navidad"
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        visibleClaseNovedad = "1"
                        .equals(retornarString(registro,
                                        SubEncargosControladorEnum.TIPO_NOVEDAD
                                                        .getValue())) ? true
                                                            : false;
        visibleFactorSalarial = "2"
                        .equals(retornarString(registro, cClaseEncargo))
                            ? true : false;
        if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        cClaseEncargo)) {
            cargarListaDetalleNovedadCargo();
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return Si el proceso previo a la insercion fue exitoso
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
     * 
     * @return Si el proceso previo a la actualizacion fue exitoso
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
     * Metodo ejecutado cuando se cierra el formulario
     * 
     * Realiza la redireccion al formulario "NatDatosPersonales"
     * enviando como parámetro los campos llave del registro desde el
     * cual ha sido redireccionado
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", ridDatosPersonales);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.NAT_DATOS_PERSONALES_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>

    // </SET_GET_ATRIBUTOS>
    public boolean isVisibleClaseNovedad() {
        return visibleClaseNovedad;
    }

    public void setVisibleClaseNovedad(boolean visibleClaseNovedad) {
        this.visibleClaseNovedad = visibleClaseNovedad;
    }

    public boolean isVisibleFactorSalarial() {
        return visibleFactorSalarial;
    }

    public void setVisibleFactorSalarial(boolean visibleFactorSalarial) {
        this.visibleFactorSalarial = visibleFactorSalarial;
    }

    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaDetalleNovedadCargo
     * 
     * @return listaDetalleNovedadCargo
     */
    public List<Registro> getListaDetalleNovedadCargo() {
        return listaDetalleNovedadCargo;
    }

    /**
     * Asigna la lista listaDetalleNovedadCargo
     * 
     * @param listaDetalleNovedadCargo
     * Variable a asignar en listaDetalleNovedadCargo
     */
    public void setListaDetalleNovedadCargo(
        List<Registro> listaDetalleNovedadCargo) {
        this.listaDetalleNovedadCargo = listaDetalleNovedadCargo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaDependencia
     * 
     * @return listaDependencia
     */
    public RegistroDataModelImpl getListaDependencia() {
        return listaDependencia;
    }

    /**
     * Asigna la lista listaDependencia
     * 
     * @param listaDependencia
     * Variable a asignar en listaDependencia
     */
    public void setListaDependencia(RegistroDataModelImpl listaDependencia) {
        this.listaDependencia = listaDependencia;
    }

    /**
     * Retorna la lista listaCargo
     * 
     * @return listaCargo
     */
    public RegistroDataModelImpl getListaCargo() {
        return listaCargo;
    }

    /**
     * Asigna la lista listaCargo
     * 
     * @param listaCargo
     * Variable a asignar en listaCargo
     */
    public void setListaCargo(RegistroDataModelImpl listaCargo) {
        this.listaCargo = listaCargo;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
