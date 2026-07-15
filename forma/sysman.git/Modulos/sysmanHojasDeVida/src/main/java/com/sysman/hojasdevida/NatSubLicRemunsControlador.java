/*-
 * NatSubLicRemunsControlador.java
 *
 * 1.0
 * 
 * 18/12/2017
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
import com.sysman.hojasdevida.enums.NatSubLicRemunsControladorEnum;
import com.sysman.hojasdevida.enums.NatSubLicRemunsControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite realizar el registro de las licencias
 * remuneradas de los empleados.
 *
 * @version 1.0, 13/04/2018, Se realiza desarrollo a partir del
 * analisis descrito en el tar 1000081152
 * @author eamaya
 * 
 */

@ManagedBean
@ViewScoped
public class NatSubLicRemunsControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que contiene el valor de LICENCIA
     */
    private final String consLicencia;

    /**
     * Constante que contiene el valor de NOM_LICENCIA
     */
    private final String consNomLicencia;

    /**
     * Atributo que contiene el valor del documento del empleado el
     * cual se asigna por parametro
     */
    private String idEmpleado;

    private boolean bloqueadoActo;

    /**
     * Variable encargada de almacenar temporalmente la licencia
     * seleccionado
     */
    private String licencia;

    private Map<String, Object> parametrosEntrada;

    private Map<String, Object> ridDatos;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /** Lista que contiene los detalles del campo tipo de acto. */
    private List<Registro> listaTipoActo;
    /** Lista que contiene los detalles del campo anio. */
    private List<Registro> listaAno;
    /** Lista que contiene los detalles del campo mes. */
    private List<Registro> listaMes;
    /** Lista que contiene los detalles del campo periodo. */
    private List<Registro> listaPeriodo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /** Lista que contiene los detalles del combo de Licencias. */
    private RegistroDataModelImpl listaLicencia;

    private String remuneracion;

    private String titulo;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de NatSubLicRemunsControlador
     */
    @SuppressWarnings("unchecked")
    public NatSubLicRemunsControlador() {
        super();
        compania = SessionUtil.getCompania();
        consLicencia = "LICENCIA";
        consNomLicencia = "NOM_LICENCIA";
        parametrosEntrada = SessionUtil.getFlash();

        try {
            numFormulario = GeneralCodigoFormaEnum.NAT_SUBLICREMUNS_CONTROLADOR
                            .getCodigo();
            if (parametrosEntrada != null) {
                ridDatos = (Map<String, Object>) parametrosEntrada.get("rid");
                idEmpleado = (String) parametrosEntrada.get("idEmpleado");
                remuneracion = (String) parametrosEntrada.get("remuneracion");

                if ("-1".equals(remuneracion)) {
                    titulo = idioma.getString("TT_BT2765");
                }
                else if ("0".equals(remuneracion)) {
                    titulo = idioma.getString("TT_FR1534");
                }
                else if ("3".equals(remuneracion)) {
                    titulo = idioma.getString("TG_OTROS2");
                }
                else {
                    titulo = idioma.getString("TT_BT634");
                }

                titulo = titulo.toUpperCase();
            }

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
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaLicencia();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaTipoActo();
        cargarListaAno();
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
        tabla = NatSubLicRemunsControladorEnum.TABLA.getValue();
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(),
                        idEmpleado);
        parametrosListado.put(NatSubLicRemunsControladorEnum.PARAM0.getValue(),
                        remuneracion);

        if ("2".equals(remuneracion)) {
            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            NatSubLicRemunsControladorUrlEnum.URL4571
                                                            .getValue());
        }
        else if ("3".equals(remuneracion)) {
            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            NatSubLicRemunsControladorUrlEnum.URL5785
                                                            .getValue());
        }
        else {
            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            NatSubLicRemunsControladorUrlEnum.URL8754
                                                            .getValue());

        }
        urlLectura = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        NatSubLicRemunsControladorUrlEnum.URL5644.getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatSubLicRemunsControladorUrlEnum.URL6201
                                                        .getValue());
        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatSubLicRemunsControladorUrlEnum.URL4968
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatSubLicRemunsControladorUrlEnum.URL5473
                                                        .getValue());
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaLicencia
     *
     */
    public void cargarListaLicencia() {
        String servicio;
        if ("2".equals(remuneracion)) {
            servicio = NatSubLicRemunsControladorUrlEnum.URL7850
                            .getValue();
        }
        else if ("3".equals(remuneracion)) {
            servicio = NatSubLicRemunsControladorUrlEnum.URL5786
                            .getValue();
        }
        else {
            servicio = NatSubLicRemunsControladorUrlEnum.URL6502
                            .getValue();
        }
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(NatSubLicRemunsControladorEnum.PARAM0.getValue(),
                        remuneracion);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        servicio);
        listaLicencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, consLicencia);
    }

    /**
     * 
     * Carga la lista listaTipoActo
     *
     */
    public void cargarListaTipoActo() {
        try {
            String servicio;
            servicio = NatSubLicRemunsControladorUrlEnum.URL6541
                    .getValue();

    listaTipoActo = RegistroConverter.toListRegistro(
                    requestManager.getList(UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    servicio)
                                    .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAno() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            NatSubLicRemunsControladorUrlEnum.URL4234
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaMes
     *
     */
    public void cargarListaMes() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), "1");
            param.put(GeneralParameterEnum.ANO.getName(),
                            registro.getCampos().get("ANO"));

            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            NatSubLicRemunsControladorUrlEnum.URL4698
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPeriodo() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), "1");
            param.put(GeneralParameterEnum.ANO.getName(),
                            registro.getCampos().get("ANO"));
            param.put(GeneralParameterEnum.MES.getName(),
                            registro.getCampos().get("MES"));

            listaPeriodo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            NatSubLicRemunsControladorUrlEnum.URL5784
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>

    public void cambiarMes() {
        registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(), null);
        cargarListaPeriodo();
    }

    public void cambiarAno() {
        registro.getCampos().put(GeneralParameterEnum.MES.getName(), null);
        registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(), null);
        cargarListaMes();
        cargarListaPeriodo();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaLicencia
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaLicencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(consNomLicencia,
                        registroAux.getCampos().get("DESCRIPCION"));
        licencia = registroAux.getCampos().get(consLicencia).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
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
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        cargarListaMes();
        cargarListaPeriodo();
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
        registro.getCampos().put(consLicencia, licencia);
        registro.getCampos().remove(consNomLicencia);
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
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(),
                        idEmpleado);
        registro.getCampos().put("TIPOACTO", registro.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
        registro.getCampos().put(consLicencia, licencia);
        registro.getCampos().remove(consNomLicencia);
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
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
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void ejecutarrcCerrar() {

        Map<String, Object> param = new HashMap<>();

        param.put("rid", ridDatos);
        param.put("numeroDcto", idEmpleado);
        param.put("remuneracion", remuneracion);

        Direccionador direccionador = new Direccionador();

        if ("3".equals(remuneracion)) {
            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.OTROSITEMS_CONTROLADOR
                                            .getCodigo()));
        }
        else {
            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.NAT_DATOS_PERSONALES_CONTROLADOR
                                            .getCodigo()));
        }
        direccionador.setParametros(param);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaTipoActo() {
        return listaTipoActo;
    }

    public void setListaTipoActo(List<Registro> listaTipoActo) {
        this.listaTipoActo = listaTipoActo;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public List<Registro> getListaMes() {
        return listaMes;
    }

    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    public List<Registro> getListaPeriodo() {
        return listaPeriodo;
    }

    public void setListaPeriodo(List<Registro> listaPeriodo) {
        this.listaPeriodo = listaPeriodo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaLicencia
     * 
     * @return listaLicencia
     */
    public RegistroDataModelImpl getListaLicencia() {
        return listaLicencia;
    }

    /**
     * Asigna la lista listaLicencia
     * 
     * @param listaLicencia
     * Variable a asignar en listaLicencia
     */
    public void setListaLicencia(RegistroDataModelImpl listaLicencia) {
        this.listaLicencia = listaLicencia;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public boolean isBloqueadoActo() {
        return bloqueadoActo;
    }

    public void setBloqueadoActo(boolean bloqueadoActo) {
        this.bloqueadoActo = bloqueadoActo;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
