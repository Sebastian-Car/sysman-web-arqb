/*-
 * SubAcademicasControlador.java
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

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.CuentasControladorEnum;
import com.sysman.hojasdevida.ejb.EjbHojasDeVidaCeroRemote;
import com.sysman.hojasdevida.enums.SubAcademicasControladorEnum;
import com.sysman.hojasdevida.enums.SubAcademicasControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Clase migrada para administrar los estudios de las personas
 *
 * @version 1.0, 24/04/2018, Se realiza desarrollo a partir del
 * analisis descrito en el tar 1000081152
 * @author eamaya
 * 
 * @version 2.0, 23/06/2018, Se cambiaron los campos de los
 * establecimientos educativos por listas que cargan los registros de
 * la tabla INSTITUCIONES_EDUCATIVAS
 * @author eamaya
 * 
 */
@ManagedBean
@ViewScoped

public class SubAcademicasControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable definida para almacenar el valor del indicador 1
     */
    private boolean gradoUno;
    /**
     * Variable definida para almacenar el valor del indicador 2
     */

    /**
     * Variable definida para almacenar el numero de documento
     * recibido por parametro
     */
    private String numeroDcto;
    /**
     * Variable definida para almacenar la sucursal recibida por
     * parametro
     */
    private String sucursal;
    /**
     * variable definida para almacena el codigo de la persona
     * recibida por parametro
     */
    private String codigoPersona;
    /**
     * Registro creado para validar llave GRADO, esto es para abrir el
     * formulario ya sea en modo nuevo o edicion
     */
    private Registro rsExiste;

    /**
     * Variable que valida en que grado sse educion basica se
     * encuentra la persona
     */
    private String grado;

    /**
     * varible que valida si el boton eliminar se inactiva o no
     */
    private boolean inactivarEliminar;

    /**
     * variable que almacena el codigo del pais seleccionado en e sub
     * de otros estudios
     */
    private String paisSub;

    /**
     * variable que almacena el codigo del departamento seleccionado
     * en el sub de otros estudios
     */
    private String departamentoSub;

    /**
     * variable que almacena el codigo del pais seleccionado en e sub
     * de Educacion superiror
     */
    private String paisSubAca;

    private String paisSubBasico ;

    /**
     * variable que almacena el codigo del departamento seleccionado
     * en el sub de Educacion superior
     */
    private String departamentoSubAca;
    
    private String departamentoSubBasico;

    /**
     * atributo que permite validar si se puede realizar la insercion
     * de un registro
     */
    private boolean insertaEducacionSuperior;

    /**
     * atributo que permite validar si se puede realizar la insercion
     * de un registro
     */

    private RegistroDataModelImpl listaEducacionbasica;
    
    private RegistroDataModelImpl listaCodigoTituloBasic;
    private RegistroDataModelImpl listaCodigoTituloBasicE;

    private boolean insertaOtrosEstudios;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registro del combo modalidad
     */
    private List<Registro> listaCodigoModalidad;

    /**
     * Lista de registros de la tabla PAISES
     */
    private List<Registro> listaPais;
    
    private List<Registro> listaPaisBasic;

    private List<Registro> listaEstablecBasic;
    
    private List<Registro> listaDepartBasic;
    
    private List<Registro> listaMunicBasic;
    /**
     * lista de registros de los departamentos de acuerdo al pais
     * seleccionado
     */
    private List<Registro> listaDepTermino;
    /**
     * Lista de registros de las ciudades de acuerdo al pais
     * seleccionado
     */
    private List<Registro> listaMunTermino;
    /**
     * Lista de registros de los paises
     */
    private List<Registro> listaPaisSub;
    /**
     * Lista de registros de los departamentos
     */
    private List<Registro> listaDepartamento;
    /**
     * Lista de registros de las ciudades
     */
    private List<Registro> listaMunicipio;
    /**
     * Lista de registros de los paises
     */
    private List<Registro> listaPaisAca;
    /**
     * Lista de registros de los departamentos
     */
    private List<Registro> listaDepartamentoAca;
    /**
     * Lista de registros de las ciudades
     */
    private List<Registro> listaMunicipioAca;

    /**
     * Lista de registros de establecimientos educativos
     */
    private List<Registro> listaEstablecimiento;

    /**
     * Lista de registros de establecimientos educativos
     */
    private List<Registro> listaEstablecimientoEduSup;

    /**
     * Lista de registros de establecimientos educativos
     */
    private List<Registro> listaEstablecimientoOtros;

    private List<Registro> listaGrado;

    private Registro registroSubEducacionBasica;
    /**
     * Lista de registros de las modalidades de otros estudios
     */
    private List<Registro> listaNombre;
    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro del subformulario otros
     * estudios. Toma el valor del indice dentro de la grilla del
     * registro seleccionado para editar
     */
    private int indiceSubotrosestudios;
    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro del subformulario estudios
     * superiores. Toma el valor del indice dentro de la grilla del
     * registro seleccionado para editar
     */
    private int indiceSubeducacionsuperior;

    private int indiceEducacionbasica;
    /**
     * Map recibida por parametro que trae la llave del registro por
     * el cual se carga este formulario
     */
    Map<String, Object> ridDatosBasicos;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de registros de la tabla profesiones
     */
    private RegistroDataModelImpl listaCodigoTitulo;
    /**
     * Lista de registros de la tabla profesiones
     */
    private RegistroDataModelImpl listaCodigoTituloE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * Lista que carga los registros del subformulario eduacion
     * superior
     */
    private RegistroDataModelImpl listaSubeducacionsuperior;
    /**
     * Lista que carga los registros del subformulario otros estudios
     */
    private RegistroDataModelImpl listaSubotrosestudios;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario
     * SubEducacionSuperior
     */
    private Registro registroSubSubEducacionSuperior;
    /**
     * Atributo de referencia para el subformulario SubOtrosEstudios
     */
    private Registro registroSubSubOtrosEstudios;

    // </DECLARAR_ADICIONALES>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbHojasDeVidaCeroRemote ejbHojasDeVidaCero;

    /**
     * Crea una nueva instancia de SubAcademicasControlador
     */
    @SuppressWarnings("unchecked")
    public SubAcademicasControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {

            // 1527
            numFormulario = GeneralCodigoFormaEnum.ACADEMICA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSubSubEducacionSuperior = new Registro(
                            new HashMap<String, Object>());
            registroSubSubOtrosEstudios = new Registro(
                            new HashMap<String, Object>());
            registroSubEducacionBasica = new Registro(
                            new HashMap<String, Object>());
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {

                ridDatosBasicos = (Map<String, Object>) parametrosEntrada
                                .get("rid");
                numeroDcto = ridDatosBasicos.get(
                                SubAcademicasControladorEnum.KEY_NUMERO_DCTO
                                .getValue())
                                .toString();

                sucursal = ridDatosBasicos.get(
                                SubAcademicasControladorEnum.KEY_SUCURSAL
                                .getValue())
                                .toString();
                codigoPersona = parametrosEntrada.get("codigo").toString();
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
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>

        cargarListaPaisSub();
        cargarListaDepartamento();
        cargarListaMunicipio();
        cargarListaPais();
        cargarListaPaisAca();
        cargarListaNombre();
        cargarListaCodigoTitulo();
        cargarListaCodigoTituloE();
        cargarListaEstablecimiento();
        cargarListaEstablecBasic();
        cargarListaEstablecimientoEduSup();
        cargarListaEstablecimientoOtros();
        cargarListaEducacionbasica();
        cargarListaPaisBasic();
        cargarListaEstablecBasic();
        cargarListaCodigoTituloBasic(); 
        cargarListaCodigoTituloBasicE();
        cargarListaCodigoModalidad();
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaCodigoModalidad();
        cargarListaSubeducacionsuperior();
        cargarListaPaisSub();
        cargarListaDepartamento();
        cargarListaMunicipio();
        cargarListaSubotrosestudios();
        

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
        listaSubeducacionsuperior = null;
        listaSubotrosestudios = null;
        listaEducacionbasica = null;

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
        enumBase = GenericUrlEnum.NAT_EDUCACION_BASICAYMEDIA;
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
        buscarUrls();
        parametrosListado.put("KEY_COMPANIA", compania);
        parametrosListado.put("KEY_NUMERO_DCTO", numeroDcto);
        parametrosListado.put("KEY_SUCURSAL", sucursal);

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_DCTO.getName(), numeroDcto);
        param.put(GeneralParameterEnum.SUCURSAL.getName(), sucursal);

        try {
            rsExiste = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubAcademicasControladorUrlEnum.URL255
                                                            .getValue())
                                            .getUrl(), param));

            if (rsExiste != null) {
                grado = rsExiste.getCampos().get("GRADO").toString();
            }
            else {
                grado = "0";
            }
            parametrosListado.put("KEY_GRADO", grado);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaSubeducacionsuperior
     */
    public void cargarListaSubeducacionsuperior() {

        try {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.NAT_FORMACION_ACADEMICA
                                            .getGridKey());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                            numeroDcto);
            param.put(GeneralParameterEnum.SUCURSAL.getName(),
                            sucursal);

            listaSubeducacionsuperior = new RegistroDataModelImpl(
                            urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            CacheUtil.getLlaveServicio(
                                            urlConexionCache,
                                            GenericUrlEnum.NAT_FORMACION_ACADEMICA
                                            .getTable()));

        }
        catch (SysmanException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaSubotrosestudios
     */
    public void cargarListaSubotrosestudios() {

        try {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.NAT_OTROS_ESTUDIOS
                                            .getGridKey());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                            numeroDcto);
            param.put(GeneralParameterEnum.SUCURSAL.getName(),
                            sucursal);

            listaSubotrosestudios = new RegistroDataModelImpl(
                            urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            CacheUtil.getLlaveServicio(
                                            urlConexionCache,
                                            GenericUrlEnum.NAT_OTROS_ESTUDIOS
                                            .getTable()));

        }
        catch (SysmanException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    public void cargarListaEducacionbasica() {

        try {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.NAT_EDUCACION_BASICAYMEDIA
                                            .getGridKey());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                            numeroDcto);
            param.put(GeneralParameterEnum.SUCURSAL.getName(),
                            sucursal);

            listaEducacionbasica = new RegistroDataModelImpl(
                            urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            CacheUtil.getLlaveServicio(
                                            urlConexionCache,
                                            GenericUrlEnum.NAT_EDUCACION_BASICAYMEDIA
                                            .getTable()));

        }
        catch (SysmanException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }


    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCodigoModalidad
     * 
     */
    public void cargarListaCodigoModalidad() {
        try {
            listaCodigoModalidad = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubAcademicasControladorUrlEnum.URL13976
                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     * Carga la lista listaPais
     */
    public void cargarListaPais() {
        try {
            listaPais = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubAcademicasControladorUrlEnum.URL415
                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    
    public void cargarListaPaisBasic(){
        try {
            listaPaisBasic = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubAcademicasControladorUrlEnum.URL9092
                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
       }

    /**
     * 
     * Carga la lista listaDepTermino
     */
    public void cargarListaDepTermino() {

        Map<String, Object> param = new TreeMap<>();
        param.put(SubAcademicasControladorEnum.PAIS.getValue(),
                        registro.getCampos().get(SubAcademicasControladorEnum.PAIST.getValue()));

        try {
            listaDepTermino = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubAcademicasControladorUrlEnum.URL439
                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaMunTermino
     */
    public void cargarListaMunTermino() {
        Map<String, Object> param = new TreeMap<>();
        param.put(SubAcademicasControladorEnum.PAIS.getValue(),
                        registro.getCampos().get(SubAcademicasControladorEnum.PAIST.getValue()));
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(),
                        registro.getCampos().get(SubAcademicasControladorEnum.DEPART.getValue()));

        try {
            listaMunTermino = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubAcademicasControladorUrlEnum.URL466
                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaPaisSub
     */
    public void cargarListaPaisSub() {

        try {
            listaPaisSub = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubAcademicasControladorUrlEnum.URL415
                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaDepartBasic(){
        
        Map<String, Object> param = new TreeMap<>();
        param.put(SubAcademicasControladorEnum.PAIS.getValue(),
                        paisSubBasico);

        try {
            listaDepartBasic = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubAcademicasControladorUrlEnum.URL9093
                                                            .getValue())
                                            .getUrl(), param));
        
        
        
        
        
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        
        
       }
           /**
            * 
            * Carga la lista listaMunicBasic
            *
            */
       public void cargarListaMunicBasic(){
           
           Map<String, Object> param = new TreeMap<>();
           
           param.put(SubAcademicasControladorEnum.PAIS.getValue(), paisSubBasico);
           param.put(GeneralParameterEnum.DEPARTAMENTO.getName(), departamentoSubBasico
                           );

           try {
               listaMunicBasic = RegistroConverter.toListRegistro(
                               requestManager.getList(UrlServiceUtil.getInstance()
                                               .getUrlServiceByUrlByEnumID(
                                                               SubAcademicasControladorUrlEnum.URL9094
                                                               .getValue())
                                               .getUrl(), param));
           }
           catch (SystemException e) {
               JsfUtil.agregarMensajeError(e.getMessage());
               logger.error(e.getMessage(), e);

           }
           
           
       }
    
    /**
     * 
     * Carga la lista listaDepartamento
     */
    public void cargarListaDepartamento() {

        Map<String, Object> param = new TreeMap<>();
        param.put(SubAcademicasControladorEnum.PAIS.getValue(), paisSub);

        try {
            listaDepartamento = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubAcademicasControladorUrlEnum.URL439
                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaMunicipio
     */
    public void cargarListaMunicipio() {

        Map<String, Object> param = new TreeMap<>();
        param.put(SubAcademicasControladorEnum.PAIS.getValue(), paisSub);
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(),
                        departamentoSub);

        try {
            listaMunicipio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubAcademicasControladorUrlEnum.URL466
                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaPaisAca
     *
     */
    public void cargarListaPaisAca() {
        try {
            listaPaisAca = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubAcademicasControladorUrlEnum.URL415
                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaDepartamentoAca
     *
     */
    public void cargarListaDepartamentoAca() {
        Map<String, Object> param = new TreeMap<>();
        param.put(SubAcademicasControladorEnum.PAIS.getValue(), paisSubAca);

        try {
            listaDepartamentoAca = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubAcademicasControladorUrlEnum.URL439
                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     * Carga la lista listaMunicipioAca
     *
     */
    public void cargarListaMunicipioAca() {
        Map<String, Object> param = new TreeMap<>();
        param.put(SubAcademicasControladorEnum.PAIS.getValue(), paisSubAca);
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(),
                        departamentoSubAca);

        try {
            listaMunicipioAca = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubAcademicasControladorUrlEnum.URL466
                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     * Carga la lista listaEstablecimiento
     *
     */
    public void cargarListaEstablecimiento() {
        try {
            listaEstablecimiento = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubAcademicasControladorUrlEnum.URL9090
                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    public void cargarListaEstablecBasic(){
        try {
            listaEstablecBasic =  RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubAcademicasControladorUrlEnum.URL9095
                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    /**
     * 
     * Carga la lista listaEstablecimientoEduSup
     *
     */
    public void cargarListaEstablecimientoEduSup() {
        try {
            listaEstablecimientoEduSup = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubAcademicasControladorUrlEnum.URL9090
                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaEstablecimientoOtros
     *
     */
    public void cargarListaEstablecimientoOtros() {
        try {
            listaEstablecimientoOtros = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubAcademicasControladorUrlEnum.URL9090
                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaCodigoTitulo
     *
     */
    public void cargarListaCodigoTitulo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubAcademicasControladorUrlEnum.URL767
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        listaCodigoTitulo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        SubAcademicasControladorEnum.CODIGOPROF.getValue());
    }

    /**
     * 
     * Carga la lista listaCodigoTitulo
     *
     */
    public void cargarListaCodigoTituloE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubAcademicasControladorUrlEnum.URL767
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        listaCodigoTituloE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        SubAcademicasControladorEnum.CODIGOPROF.getValue());

    }
    
    public void cargarListaCodigoTituloBasic(){
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubAcademicasControladorUrlEnum.URL767
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        listaCodigoTituloBasic =  new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        SubAcademicasControladorEnum.CODIGOPROF.getValue());
        }
            /**
             * 
             * Carga la lista listaCodigoTituloBasic
             *
             */
        public void  cargarListaCodigoTituloBasicE(){
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubAcademicasControladorUrlEnum.URL767
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        listaCodigoTituloBasicE =  new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        SubAcademicasControladorEnum.CODIGOPROF.getValue());
        
            
        }
    /**
     * 
     * Carga la lista listaNombre
     */
    public void cargarListaNombre() {
        try {
            listaNombre = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubAcademicasControladorUrlEnum.URL19754
                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }


    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Pais
     * 
     */
    public void cambiarPais() {
        // <CODIGO_DESARROLLADO>
        cargarListaDepTermino();
        cargarListaMunTermino();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPaisC(int rowNum) {
     // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control DepTermino
     * 
     */
    public void cambiarDepTermino() {
        // <CODIGO_DESARROLLADO>
        //paisSub
        cargarListaMunTermino();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control PaisSub
     * 
     * 
     */
    public void cambiarPaisSub() {
        // <CODIGO_DESARROLLADO>
        paisSub = registroSubSubOtrosEstudios.getCampos()
                        .get(SubAcademicasControladorEnum.PAIS
                                        .getValue()) == null
                                        ? ""
                                            : registroSubSubOtrosEstudios
                                            .getCampos()
                                            .get(SubAcademicasControladorEnum.PAIS
                                                            .getValue())
                                            .toString();
        cargarListaDepartamento();
        cargarListaMunicipio();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Departamento
     * 
     */
    public void cambiarDepartamento() {
        // <CODIGO_DESARROLLADO>
        departamentoSub = registroSubSubOtrosEstudios.getCampos()
                        .get(SubAcademicasControladorEnum.DEPTO
                                        .getValue()) == null
                                        ? ""
                                            : registroSubSubOtrosEstudios
                                            .getCampos()
                                            .get(SubAcademicasControladorEnum.DEPTO
                                                            .getValue())
                                            .toString();
        cargarListaMunicipio();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control PaisAca
     * 
     * 
     */
    public void cambiarPaisAca() {
        // <CODIGO_DESARROLLADO>
        paisSubAca = registroSubSubEducacionSuperior.getCampos()
                        .get(SubAcademicasControladorEnum.PAIS
                                        .getValue()) == null
                                        ? ""
                                            : registroSubSubEducacionSuperior
                                            .getCampos()
                                            .get(SubAcademicasControladorEnum.PAIS
                                                            .getValue())
                                            .toString();
        cargarListaDepartamentoAca();
        cargarListaMunicipioAca();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control DepartamentoAca
     * 
     * 
     */
    public void cambiarDepartamentoAca() {
        // <CODIGO_DESARROLLADO>
        departamentoSubAca = registroSubSubEducacionSuperior.getCampos()
                        .get(GeneralParameterEnum.DEPARTAMENTO
                                        .getName()) == null
                                        ? ""
                                            : registroSubSubEducacionSuperior
                                            .getCampos()
                                            .get(GeneralParameterEnum.DEPARTAMENTO
                                                            .getName())
                                            .toString();
        cargarListaMunicipioAca();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPaisBasic() {
        //<CODIGO_DESARROLLADO>
        paisSubBasico = registroSubEducacionBasica.getCampos()
                        .get(SubAcademicasControladorEnum.PAIST.getValue()) == null
                                        ? ""
                                            : registroSubEducacionBasica
                                            .getCampos()
                                            .get(SubAcademicasControladorEnum.PAIST.getValue())
                                            .toString();
        cargarListaDepartBasic();
        cargarListaMunicBasic();
        //</CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado al cambiar el control DepartBasic
     * 
     * 
     */
    public void cambiarDepartBasic() {
        //<CODIGO_DESARROLLADO>
        departamentoSubBasico = registroSubEducacionBasica.getCampos()
        .get(SubAcademicasControladorEnum.DEPART.getValue()) == null
                        ? ""
                            : registroSubEducacionBasica
                            .getCampos()
                            .get(SubAcademicasControladorEnum.DEPART.getValue())
                            .toString();
        cargarListaMunicBasic();
        //</CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado al cambiar el control PaisAca
     * 
     * 
     */

    /**
     * Metodo ejecutado al cambiar el control FechaInicioOtros
     * 
     */
    public void cambiarFechaInicioOtros() {
        // <CODIGO_DESARROLLADO>

        if (validarFechas(registroSubSubOtrosEstudios.getCampos()
                        .get(SubAcademicasControladorEnum.FECHAINICIO
                                        .getValue()),
                        registroSubSubOtrosEstudios.getCampos()
                        .get(SubAcademicasControladorEnum.FECHATERMINACION
                                        .getValue()),
                        "1")) {
            registroSubSubOtrosEstudios.getCampos().put(
                            SubAcademicasControladorEnum.FECHATERMINACION
                            .getValue(),
                            null);
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FechaTerminacion
     * 
     */
    public void cambiarFechaTerminacion() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(
                        registroSubSubEducacionSuperior.getCampos(),
                        SubAcademicasControladorEnum.FECHATERMINACION
                        .getValue())) {
            registroSubSubEducacionSuperior.getCampos().put(
                            SubAcademicasControladorEnum.GRADUADO.getValue(),
                            false);

        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FechaTerminacionOtros
     * 
     */
    public void cambiarFechaTerminacionOtros() {
        // <CODIGO_DESARROLLADO>

        if (SysmanFunciones.validarCampoVacio(
                        registroSubSubOtrosEstudios.getCampos(),
                        SubAcademicasControladorEnum.FECHATERMINACION
                        .getValue())) {
            registroSubSubOtrosEstudios.getCampos().put(
                            SubAcademicasControladorEnum.GRADUADO.getValue(),
                            false);

        }
        else {
            if (validarFechas(registroSubSubOtrosEstudios.getCampos()
                            .get(SubAcademicasControladorEnum.FECHAINICIO
                                            .getValue()),
                            registroSubSubOtrosEstudios.getCampos()
                            .get(SubAcademicasControladorEnum.FECHATERMINACION
                                            .getValue()),
                            "2")) {
                registroSubSubOtrosEstudios.getCampos().put(
                                SubAcademicasControladorEnum.FECHATERMINACION
                                .getValue(),
                                null);
                registroSubSubOtrosEstudios.getCampos().put(
                                SubAcademicasControladorEnum.GRADUADO
                                .getValue(),
                                false);
            }
        }
        // </CODIGO_DESARROLLADO>
    }
   

    /**
     * Metodo ejecutado al cambiar el control Graduado
     * 
     */
    public void cambiarGraduado() {
        // <CODIGO_DESARROLLADO>
        if (validarGraduado(registroSubSubEducacionSuperior.getCampos()
                        .get(SubAcademicasControladorEnum.FECHATERMINACION
                                        .getValue()))) {

            registroSubSubEducacionSuperior.getCampos().put(
                            SubAcademicasControladorEnum.GRADUADO.getValue(),
                            false);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control GraduadoSubOtros
     * 
     */
    public void cambiarGraduadoSubOtros() {
        // <CODIGO_DESARROLLADO>

        if (validarGraduado(registroSubSubOtrosEstudios.getCampos()
                        .get(SubAcademicasControladorEnum.FECHATERMINACION
                                        .getValue()))) {

            registroSubSubOtrosEstudios.getCampos().put(
                            SubAcademicasControladorEnum.GRADUADO.getValue(),
                            false);
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control PaisSub en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarPaisSubC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        paisSub = listaSubotrosestudios.getDatasource().get(rowNum % 10)
                        .getCampos()
                        .get(SubAcademicasControladorEnum.PAIS
                                        .getValue()) == null
                                        ? ""
                                            : listaSubotrosestudios
                                            .getDatasource()
                                            .get(rowNum % 10)
                                            .getCampos()
                                            .get(SubAcademicasControladorEnum.PAIS
                                                            .getValue())
                                            .toString();
        cargarListaDepartamento();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Departamento en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarDepartamentoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        departamentoSub = listaSubotrosestudios.getDatasource().get(rowNum % 10)
                        .getCampos()
                        .get(SubAcademicasControladorEnum.DEPTO
                                        .getValue()) == null
                                        ? ""
                                            : listaSubotrosestudios
                                            .getDatasource()
                                            .get(rowNum % 10)
                                            .getCampos()
                                            .get(SubAcademicasControladorEnum.DEPTO
                                                            .getValue())
                                            .toString();
        cargarListaMunicipio();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control PaisAca en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarPaisAcaC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        paisSubAca = listaSubeducacionsuperior.getDatasource().get(rowNum % 10)
                        .getCampos()
                        .get(SubAcademicasControladorEnum.PAIS
                                        .getValue()) == null
                                        ? ""
                                            : listaSubeducacionsuperior
                                            .getDatasource()
                                            .get(rowNum % 10)
                                            .getCampos()
                                            .get(SubAcademicasControladorEnum.PAIS
                                                            .getValue())
                                            .toString();
        cargarListaDepartamentoAca();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control DepartamentoAca en la
     * fila seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarDepartamentoAcaC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        departamentoSubAca = listaSubeducacionsuperior.getDatasource()
                        .get(rowNum % 10)
                        .getCampos()
                        .get(GeneralParameterEnum.DEPARTAMENTO
                                        .getName()) == null
                                        ? ""
                                            : listaSubeducacionsuperior
                                            .getDatasource()
                                            .get(rowNum % 10)
                                            .getCampos()
                                            .get(GeneralParameterEnum.DEPARTAMENTO
                                                            .getName())
                                            .toString();
        cargarListaMunicipioAca();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPaisBasicC(int rowNum) {
        
        //<CODIGO_DESARROLLADO>
        
        paisSubBasico = listaEducacionbasica.getDatasource()
                        .get(rowNum % 10)
                        .getCampos()
                        .get(SubAcademicasControladorEnum.PAIST.getValue()) == null
                                        ? ""
                                            : listaEducacionbasica
                                            .getDatasource()
                                            .get(rowNum % 10)
                                            .getCampos()
                                            .get(SubAcademicasControladorEnum.PAIST.getValue())
                                            .toString();
                        
            cargarListaDepartBasic();
            cargarListaMunicBasic();
            
            
            
            
            
        
       //</CODIGO_DESARROLLADO>
   }
    
    public void cambiarDepartBasicC(int rowNum) {
        //<CODIGO_DESARROLLADO>
        departamentoSubBasico = listaEducacionbasica.getDatasource()
                        .get(rowNum % 10)
                        .getCampos()
                        .get(SubAcademicasControladorEnum.DEPART.getValue()) == null
                                        ? ""
                                            : listaEducacionbasica
                                            .getDatasource()
                                            .get(rowNum % 10)
                                            .getCampos()
                                            .get(SubAcademicasControladorEnum.DEPART.getValue())
                                            .toString();
        cargarListaMunicBasic();
       //</CODIGO_DESARROLLADO>
   }
    
    /**
     * Metodo ejecutado al cambiar el control FechaInicioOtros en la
     * fila seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarFechaInicioOtrosC(int rowNum) {

        // <CODIGO_DESARROLLADO>

        if (validarFechas(listaSubotrosestudios
                        .getDatasource()
                        .get(rowNum % 10)
                        .getCampos()
                        .get(SubAcademicasControladorEnum.FECHAINICIO
                                        .getValue()),
                        listaSubotrosestudios
                        .getDatasource()
                        .get(rowNum % 10)
                        .getCampos()
                        .get(SubAcademicasControladorEnum.FECHATERMINACION
                                        .getValue()),
                        "1")) {
            listaSubotrosestudios
            .getDatasource()
            .get(rowNum % 10)
            .getCampos().put(
                            SubAcademicasControladorEnum.FECHATERMINACION
                            .getValue(),
                            null);
            listaSubotrosestudios
            .getDatasource()
            .get(rowNum % 10)
            .getCampos().put(
                            SubAcademicasControladorEnum.GRADUADO
                            .getValue(),
                            false);
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FechaTerminacion en la
     * fila seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarFechaTerminacionC(int rowNum) {
        // <CODIGO_DESARROLLADO>

        if (SysmanFunciones.validarCampoVacio(listaSubeducacionsuperior
                        .getDatasource()
                        .get(rowNum % 10)
                        .getCampos(),
                        SubAcademicasControladorEnum.FECHATERMINACION
                        .getValue())) {

            listaSubeducacionsuperior
            .getDatasource()
            .get(rowNum % 10)
            .getCampos().put(
                            SubAcademicasControladorEnum.GRADUADO
                            .getValue(),
                            false);

        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FechaTerminacionOtros en
     * la fila seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarFechaTerminacionOtrosC(int rowNum) {

        // <CODIGO_DESARROLLADO>

        if (SysmanFunciones.validarCampoVacio(listaSubotrosestudios
                        .getDatasource()
                        .get(rowNum % 10)
                        .getCampos(),
                        SubAcademicasControladorEnum.FECHATERMINACION
                        .getValue())) {

            listaSubotrosestudios
            .getDatasource()
            .get(rowNum % 10)
            .getCampos().put(
                            SubAcademicasControladorEnum.GRADUADO
                            .getValue(),
                            false);

        }
        else {

            if (validarFechas(listaSubotrosestudios
                            .getDatasource()
                            .get(rowNum % 10)
                            .getCampos()
                            .get(SubAcademicasControladorEnum.FECHAINICIO
                                            .getValue()),
                            listaSubotrosestudios
                            .getDatasource()
                            .get(rowNum % 10)
                            .getCampos()
                            .get(SubAcademicasControladorEnum.FECHATERMINACION
                                            .getValue()),
                            "2")) {
                listaSubotrosestudios
                .getDatasource()
                .get(rowNum % 10)
                .getCampos().put(
                                SubAcademicasControladorEnum.FECHATERMINACION
                                .getValue(),
                                null);
                listaSubotrosestudios
                .getDatasource()
                .get(rowNum % 10)
                .getCampos().put(
                                SubAcademicasControladorEnum.GRADUADO
                                .getValue(),
                                false);
            }
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Graduado en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarGraduadoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if (validarGraduado(listaSubeducacionsuperior.getDatasource()
                        .get(rowNum % 10).getCampos()
                        .get(SubAcademicasControladorEnum.FECHATERMINACION
                                        .getValue()))) {

            listaSubeducacionsuperior.getDatasource().get(rowNum % 10)
            .getCampos()
            .put(
                            SubAcademicasControladorEnum.GRADUADO
                            .getValue(),
                            false);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control GraduadoSubOtros en la
     * fila seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarGraduadoSubOtrosC(int rowNum) {
        // <CODIGO_DESARROLLADO>

        if (validarGraduado(listaSubotrosestudios.getDatasource()
                        .get(rowNum % 10).getCampos()
                        .get(SubAcademicasControladorEnum.FECHATERMINACION
                                        .getValue()))) {

            listaSubotrosestudios.getDatasource().get(rowNum % 10).getCampos()
            .put(
                            SubAcademicasControladorEnum.GRADUADO
                            .getValue(),
                            false);
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDepTermino
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDepTermino(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(SubAcademicasControladorEnum.DEPART.getValue(),
                        registroAux.getCampos().get("DEPARTAMENTO"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaMunTermino
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaMunTermino(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("MUNTERMINO",
                        registroAux.getCampos().get("CIUDAD"));
    }

    
    public void seleccionarFilaCodigoTituloBasicE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
               auxiliar =  (String) registroAux.getCampos().get("CODIGOPROF");
        }
    
    public void seleccionarFilaCodigoTituloBasic(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        
        registroSubEducacionBasica.getCampos().put("TITULO", 
                                registroAux.getCampos().get("CODIGOPROF"));
        registroSubEducacionBasica.getCampos().put("NOMBRE_PROFESION", 
                        registroAux.getCampos().get("NOMBRE_PROFESION"));
        }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoTitulo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoTitulo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSubSubEducacionSuperior.getCampos().put("TITULOOBTENIDO",
                        registroAux.getCampos()
                        .get(SubAcademicasControladorEnum.CODIGOPROF
                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoTitulo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoTituloE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(
                        SubAcademicasControladorEnum.CODIGOPROF.getValue()), "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEstablecimiento
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEstablecimiento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("ESTABLECIMIENTO",
                        registroAux.getCampos()
                        .get(SubAcademicasControladorEnum.NIT
                                        .getValue()));
        registro.getCampos().put("NOMBREESTABLECIMIENTO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEstablecimientoEduSup
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEstablecimientoEduSup(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSubSubEducacionSuperior.getCampos().put("ESTABLECIMIENTO",
                        registroAux.getCampos()
                        .get(SubAcademicasControladorEnum.NIT
                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEstablecimientoEduSup
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEstablecimientoEduSupE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(SubAcademicasControladorEnum.NIT.getValue()), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    public void oprimireliminar() {
        try {

            ejbHojasDeVidaCero.validarEstudiosSuperiores(compania, numeroDcto,
                            sucursal);
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            SubAcademicasControladorEnum.MSM_REGISTRO_ELIMINADO
                                            .getValue()));

            cargarRegistro(null, ACCION_INSERTAR);

        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton ActualizaEducacion en la
     * vista
     *
     */
    public void oprimirActualizaEducacion() {
        try {
            ejbHojasDeVidaCero.ActualizarDetallesProfesiones(compania,
                            numeroDcto,
                            sucursal,
                            new BigDecimal(codigoPersona),
                            SysmanFunciones.toString(SessionUtil
                                            .getUser()
                                            .getCodigo()));

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB4018"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB4019"));
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario Subeducacionsuperior
     */
    public void agregarRegistroSubSubeducacionsuperior() {
        try {
            registroSubSubEducacionSuperior.getCampos().put(
                            GeneralParameterEnum.COMPANIA.getName(), compania);
            registroSubSubEducacionSuperior.getCampos().put(
                            GeneralParameterEnum.NUMERO_DCTO.getName(),
                            numeroDcto);
            registroSubSubEducacionSuperior.getCampos().put(
                            GeneralParameterEnum.SUCURSAL.getName(), sucursal);
            registroSubSubEducacionSuperior
            .getCampos().put(
                            SubAcademicasControladorEnum.NFA_CODIGOPERSONA
                            .getValue(),
                            codigoPersona);
            registroSubSubEducacionSuperior.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            registroSubSubEducacionSuperior.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            insertarAnoMesDia(registroSubSubEducacionSuperior, "1");
            Long consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            GenericUrlEnum.NAT_FORMACION_ACADEMICA.getTable(),
                            SysmanFunciones.concatenar("COMPANIA = ''",
                                            compania,
                                            "''"),
                            GeneralParameterEnum.NUMERO.getName(), "1");

            registroSubSubEducacionSuperior.getCampos().put(
                            GeneralParameterEnum.NUMERO.getName(), consecutivo);
            registroSubSubEducacionSuperior.getCampos()
            .remove(SubAcademicasControladorEnum.NOMBREMODALIDAD
                            .getValue());
            registroSubSubEducacionSuperior.getCampos()
            .remove(SubAcademicasControladorEnum.NOMBREPAISACA
                            .getValue());
            registroSubSubEducacionSuperior.getCampos()
            .remove(SubAcademicasControladorEnum.NOMBREDEPARTAMENTOACA
                            .getValue());
            registroSubSubEducacionSuperior.getCampos()
            .remove(SubAcademicasControladorEnum.NOMBRECIUDADACA
                            .getValue());
            registroSubSubEducacionSuperior.getCampos()
            .remove(SubAcademicasControladorEnum.NOMBRE_PROFESION
                            .getValue());

            registroSubSubEducacionSuperior.getCampos()
            .remove(SubAcademicasControladorEnum.NOMBREESTABLECIMIENTO
                            .getValue());
            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.NAT_FORMACION_ACADEMICA
                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubSubEducacionSuperior.getCampos());
            cargarListaSubeducacionsuperior();
            JsfUtil.agregarMensajeInformativo(idioma
                            .getString(SubAcademicasControladorEnum.MSM_REGISTRO_INGRESADO
                                            .getValue()));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        finally {
            registroSubSubEducacionSuperior = new Registro(
                            new HashMap<String, Object>());
        }
    }

    /**
     * Metodo de insercion del formulario Educacionbasica
     */   
    public void agregarRegistroSubEducacionbasica() {
        try {
            registroSubEducacionBasica.getCampos().put(
                            GeneralParameterEnum.COMPANIA.getName(), compania);
            registroSubEducacionBasica.getCampos().put(
                            GeneralParameterEnum.NUMERO_DCTO.getName(),
                            numeroDcto);
            registroSubEducacionBasica.getCampos().put(
                            GeneralParameterEnum.SUCURSAL.getName(), sucursal);
            registroSubEducacionBasica
            .getCampos().put(
                            SubAcademicasControladorEnum.NEB_CODIGOPERSONA
                            .getValue(),
                            codigoPersona);
           
            registroSubEducacionBasica.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            registroSubEducacionBasica.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            registroSubEducacionBasica.getCampos().remove("NOMBRE_PROFESION");
            
            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.NAT_EDUCACION_BASICAYMEDIA
                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubEducacionBasica.getCampos());
            cargarListaEducacionbasica();
            JsfUtil.agregarMensajeInformativo(idioma
                            .getString(SubAcademicasControladorEnum.MSM_REGISTRO_INGRESADO
                                            .getValue()));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        finally {
            registroSubEducacionBasica = new Registro(
                            new HashMap<String, Object>());
        }
    }




    /**
     * Metodo de edicion del formulario Subeducacionsuperior
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSubeducacionsuperior(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            insertarAnoMesDia(reg, "1");
            reg.getCampos().remove(SubAcademicasControladorEnum.NOMBREMODALIDAD
                            .getValue());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(GeneralParameterEnum.NUMERO_DCTO.getName());
            reg.getCampos().remove(GeneralParameterEnum.SUCURSAL.getName());
            reg.getCampos().remove(
                            SubAcademicasControladorEnum.NFA_CODIGOPERSONA
                            .getValue());
            reg.getCampos().remove(SubAcademicasControladorEnum.NOMBREPAISACA
                            .getValue());
            reg.getCampos().remove(
                            SubAcademicasControladorEnum.NOMBREDEPARTAMENTOACA
                            .getValue());
            reg.getCampos().remove(SubAcademicasControladorEnum.NOMBRECIUDADACA
                            .getValue());
            reg.getCampos().remove(SubAcademicasControladorEnum.NOMBRE_PROFESION
                            .getValue());
            reg.getCampos().remove(
                            SubAcademicasControladorEnum.NOMBREESTABLECIMIENTO
                            .getValue());

            reg.getCampos().remove("NOMBRE_ESTABLECIMIENTO");

            if (validarCamposObligatoriosSuperior(reg)) {
                return;
            }
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.NAT_FORMACION_ACADEMICA
                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(idioma
                            .getString(CuentasControladorEnum.MSM_REGISTRO_MODIFICADO
                                            .getValue()));

        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        finally {
            cargarListaSubeducacionsuperior();
        }
    }

    public void editarRegSubEducacionbasica(RowEditEvent event) {
        try {

            Registro reg = (Registro) event.getObject();

            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            reg.getCampos().remove("NOMBREINSTITUCION");
            reg.getCampos().remove("NOMBREDEPARTAMENTO");
            reg.getCampos().remove("GRADONOMBRE");
            reg.getCampos().remove("NMBREMUNICIPIO");
            reg.getCampos().remove("NOMBREPAIS");
            reg.getCampos().remove("NOMBRE_PROFESION");
            
            reg.getCampos().put(SubAcademicasControladorEnum.NEB_CODIGOPERSONA
                            .getValue(), codigoPersona);
           

            if (validarCamposObligatoriosBasica(reg)) {
                return;
            }
                    UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    GenericUrlEnum.NAT_EDUCACION_BASICAYMEDIA
                                                    .getUpdateKey());
        
                    requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                                    reg.getCampos(), reg.getLlave());
            
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        JsfUtil.agregarMensajeInformativo(idioma
                        .getString(CuentasControladorEnum.MSM_REGISTRO_MODIFICADO
                                        .getValue()));    

    }

    /**
     * Metodo de eliminacion del formulario Subeducacionsuperior
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubSubeducacionsuperior(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.NAT_FORMACION_ACADEMICA
                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(idioma
                            .getString(CuentasControladorEnum.MSM_REGISTRO_ELIMINADO
                                            .getValue()));
            cargarListaSubeducacionsuperior();
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Subeducacionsuperior
     */
    public void cancelarEdicionSubeducacionsuperior() {
        cargarListaSubeducacionsuperior();
        cargarListaSubotrosestudios();
    }

    /**
     * Metodo de insercion del formulario Subotrosestudios
     */
    public void agregarRegistroSubSubotrosestudios() {
        try {

            registroSubSubOtrosEstudios.getCampos().put(
                            GeneralParameterEnum.COMPANIA.getName(), compania);
            registroSubSubOtrosEstudios.getCampos().put(
                            GeneralParameterEnum.NUMERO_DCTO.getName(),
                            numeroDcto);
            registroSubSubOtrosEstudios.getCampos().put(
                            GeneralParameterEnum.SUCURSAL.getName(), sucursal);
            registroSubSubOtrosEstudios
            .getCampos().put(
                            SubAcademicasControladorEnum.NOE_CODIGOPERSONA
                            .getValue(),
                            codigoPersona);
            registroSubSubOtrosEstudios.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            registroSubSubOtrosEstudios.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            insertarAnoMesDia(registroSubSubOtrosEstudios, "2");
            Long consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            GenericUrlEnum.NAT_OTROS_ESTUDIOS.getTable(),
                            SysmanFunciones.concatenar("COMPANIA = ''",
                                            compania,
                                            "''"),
                            GeneralParameterEnum.NUMERO.getName(), "1");

            registroSubSubOtrosEstudios.getCampos().put(
                            GeneralParameterEnum.NUMERO.getName(), consecutivo);

            registroSubSubOtrosEstudios.getCampos().remove(
                            SubAcademicasControladorEnum.NOMBREPAIS.getValue());
            registroSubSubOtrosEstudios.getCampos()
            .remove(SubAcademicasControladorEnum.NOMBREDEPARTAMENTO
                            .getValue());
            registroSubSubOtrosEstudios.getCampos()
            .remove(SubAcademicasControladorEnum.NOMBRECIUDAD
                            .getValue());
            registroSubSubOtrosEstudios.getCampos()
            .remove(GeneralParameterEnum.NOMBRE.getName());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.NAT_OTROS_ESTUDIOS
                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubSubOtrosEstudios.getCampos());
            cargarListaSubotrosestudios();
            JsfUtil.agregarMensajeInformativo(idioma
                            .getString(SubAcademicasControladorEnum.MSM_REGISTRO_INGRESADO
                                            .getValue()));

        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        finally {
            registroSubSubOtrosEstudios = new Registro(
                            new HashMap<String, Object>());
        }
    }

    /**
     * Metodo de edicion del formulario Subotrosestudios
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSubotrosestudios(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            insertarAnoMesDia(reg, "2");
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(GeneralParameterEnum.NUMERO_DCTO.getName());
            reg.getCampos().remove(GeneralParameterEnum.SUCURSAL.getName());
            reg.getCampos().remove(GeneralParameterEnum.NUMERO.getName());
            reg.getCampos().remove(
                            SubAcademicasControladorEnum.NOE_CODIGOPERSONA
                            .getValue());
            reg.getCampos().remove(
                            SubAcademicasControladorEnum.NOMBREPAIS.getValue());
            reg.getCampos().remove(
                            SubAcademicasControladorEnum.NOMBREDEPARTAMENTO
                            .getValue());
            reg.getCampos().remove(SubAcademicasControladorEnum.NOMBRECIUDAD
                            .getValue());

            reg.getCampos().remove("NOMBRE_ESTABLECIMIENTO");

            insertarAnoMesDia(reg, "2");
            reg.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());

            if (validarCamposObligatoriosOtros(reg)) {
                return;
            }
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.NAT_OTROS_ESTUDIOS
                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(idioma
                            .getString(CuentasControladorEnum.MSM_REGISTRO_MODIFICADO
                                            .getValue()));

        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        finally {
            cargarListaSubotrosestudios();
        }
    }

    /**
     * Metodo de eliminacion del formulario Subotrosestudios
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubSubotrosestudios(Registro reg) {
        try {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.NAT_OTROS_ESTUDIOS
                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(idioma
                            .getString(CuentasControladorEnum.MSM_REGISTRO_ELIMINADO
                                            .getValue()));

            cargarListaSubotrosestudios();
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    public void eliminarRegSubEducacionbasica(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.NAT_EDUCACION_BASICAYMEDIA
                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(idioma
                            .getString(CuentasControladorEnum.MSM_REGISTRO_ELIMINADO
                                            .getValue()));
            cargarListaEducacionbasica();
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        
        
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Subotrosestudios
     */
    public void cancelarEdicionSubotrosestudios() {
        cargarListaSubotrosestudios();
    }

    public void cancelarEdicionEducacionbasica(){
        cargarListaEducacionbasica();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>

    /**
     * Metodo que valida si se puede o no insertar o actualizar en el
     * subformulario educaciopn superior
     */
    public void validarGradoOnce() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_DCTO.getName(), numeroDcto);
        param.put(GeneralParameterEnum.SUCURSAL.getName(), sucursal);

        try {
            Registro rsGrado = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubAcademicasControladorUrlEnum.URL255
                                                            .getValue())
                                            .getUrl(), param));

            if (rsGrado != null) {

                if ("11".equals(rsGrado.getCampos()
                                .get(SubAcademicasControladorEnum.GRADO
                                                .getValue())
                                .toString())) {
                    insertaEducacionSuperior = true;
                }
                else {
                    insertaEducacionSuperior = false;
                }

            }
            else {
                insertaEducacionSuperior = false;
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que valida si el indicador de graduado se permite
     * seleccionar o no
     * 
     * @param fechaTer
     * @return
     */
    public boolean validarGraduado(Object fechaTer) {
        if (SysmanFunciones.validarVariableVacio(SysmanFunciones
                        .nvl(fechaTer,
                                        "")
                        .toString())) {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3879"));
            return true;

        }
        return false;
    }

    /**
     * Metodo que valida que la fecha inicial no pueda ser mayor a la
     * fecha final del subformualario otros
     * 
     * @param fechaIni
     * @param fechaFin
     * @param tipoFecha
     * @return
     */
    public boolean validarFechas(Object fechaIni, Object fechaFin,
        String tipoFecha) {
        if (!SysmanFunciones
                        .validarVariableVacio(SysmanFunciones
                                        .nvl("1".equals(tipoFecha) ? fechaFin
                                            : fechaIni,
                                                        "")
                                        .toString())) {
            Date fecIni = (Date) fechaIni;
            Date fecFin = (Date) fechaFin;

            if (fecFin.before(fecIni)) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB147"));
                return true;

            }
        }
        return false;
    }

    /**
     * Metodo que insetar los campos de
     * DIAINICIO,MESINICIO,ANOINCIO,DIATERMINACION,MESTERMINACION,
     * ANOTERMINACION de acuerdo a las fechas seleccionadas
     * 
     * @param registro
     * @param subFormulario
     */
    public void insertarAnoMesDia(Registro registro, String subFormulario) {

        if ("2".equals(subFormulario)) {
            int diaInicio = SysmanFunciones
                            .dia((Date) registro.getCampos()
                                            .get(SubAcademicasControladorEnum.FECHAINICIO
                                                            .getValue()));
            int mesInicio = SysmanFunciones
                            .mes((Date) registro.getCampos()
                                            .get(SubAcademicasControladorEnum.FECHAINICIO
                                                            .getValue()));
            int anoInicio = SysmanFunciones
                            .ano((Date) registro.getCampos()
                                            .get(SubAcademicasControladorEnum.FECHAINICIO
                                                            .getValue()));

            registro.getCampos().put(
                            SubAcademicasControladorEnum.DIAINICIO.getValue(),
                            diaInicio);
            registro.getCampos().put(
                            SubAcademicasControladorEnum.MESINICIO.getValue(),
                            mesInicio);
            registro.getCampos().put(
                            SubAcademicasControladorEnum.ANOINICIO.getValue(),
                            anoInicio);

        }

        if (SysmanFunciones.validarVariableVacio(
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get(SubAcademicasControladorEnum.FECHATERMINACION
                                                        .getValue()),
                                        "").toString())) {
            registro.getCampos().put(
                            SubAcademicasControladorEnum.DIATERMINACION
                            .getValue(),
                            null);
            registro.getCampos().put(
                            SubAcademicasControladorEnum.MESTERMINACION
                            .getValue(),
                            null);
            registro.getCampos().put(
                            SubAcademicasControladorEnum.ANOTERMINACION
                            .getValue(),
                            null);
        }
        else {
            int diaTerminacion = SysmanFunciones
                            .dia((Date) registro.getCampos()
                                            .get(SubAcademicasControladorEnum.FECHATERMINACION
                                                            .getValue()));
            int mesTerminacion = SysmanFunciones
                            .mes((Date) registro.getCampos()
                                            .get(SubAcademicasControladorEnum.FECHATERMINACION
                                                            .getValue()));
            int anoTer = SysmanFunciones
                            .ano((Date) registro.getCampos()
                                            .get(SubAcademicasControladorEnum.FECHATERMINACION
                                                            .getValue()));
            registro.getCampos().put(
                            SubAcademicasControladorEnum.DIATERMINACION
                            .getValue(),
                            diaTerminacion);
            registro.getCampos().put(
                            SubAcademicasControladorEnum.MESTERMINACION
                            .getValue(),
                            mesTerminacion);
            registro.getCampos().put(
                            SubAcademicasControladorEnum.ANOTERMINACION
                            .getValue(),
                            anoTer);
        }

    }

    /**
     * metodo que valida los campos obligatorios
     * 
     * @param registro
     * @return
     */
    public boolean validarCamposObligatoriosSuperior(Registro registro) {
        if (validarCamposSubComunes(registro)) {
            return true;
        }

        if (validarCamposSub(SysmanFunciones.nvl(registro.getCampos()
                        .get(SubAcademicasControladorEnum.NIVELAPROBADO
                                        .getValue()),
                        "").toString(),
                        "TB_TB3872")
                        || validarCamposSub(SysmanFunciones.nvl(registro.getCampos()
                                        .get(GeneralParameterEnum.DEPARTAMENTO
                                                        .getName()),
                                        "").toString(),
                                        "TB_TB3869")) {
            return true;
        }

        return false;
    }

    public boolean validarCamposObligatoriosBasica(Registro registro) {
       
        if (validarCamposSub(SysmanFunciones.nvl(registro.getCampos()
                        .get(SubAcademicasControladorEnum.GRADO
                                        .getValue()),
                        "").toString(),
                        "TB_TB3864")
                        || validarCamposSub(SysmanFunciones.nvl(registro.getCampos()
                                        .get(SubAcademicasControladorEnum.ESTABLECIMIENTO
                                                        .getValue()),
                                        "").toString(),
                                        "TB_TB3875")) {
            return true;
        }

        return false;
    }

    /**
     * Metodo que se ejecuta al aceptar la edicion del registro del
     * subformulario otros
     * 
     * @param reg
     * @return
     */
    public boolean validarCamposObligatoriosOtros(Registro registro) {
        if (validarCamposSubComunes(registro)
                        || validarCamposSub(SysmanFunciones.nvl(registro.getCampos()
                                        .get(SubAcademicasControladorEnum.HORAS.getValue()),
                                        "").toString(),
                                        "TB_TB3865")
                        || validarCamposSub(SysmanFunciones.nvl(registro.getCampos()
                                        .get(SubAcademicasControladorEnum.FECHAINICIO
                                                        .getValue()),
                                        "").toString(),
                                        "TB_TB3868")
                        || validarCamposSub(SysmanFunciones.nvl(registro.getCampos()
                                        .get(SubAcademicasControladorEnum.DEPTO.getValue()),
                                        "").toString(),
                                        "TB_TB3869")) {
            return true;
        }

        return false;
    }

    /**
     * Metodo que se ejecuta al actualizar un registro de cualquiera
     * de los 2 subformulario
     * 
     * @param registro
     * @return
     */
    public boolean validarCamposSubComunes(Registro registro) {
        if (validarCamposSub(SysmanFunciones.nvl(registro.getCampos()
                        .get(SubAcademicasControladorEnum.MODALIDAD.getValue()),
                        "").toString(),
                        "TB_TB3870")
                        || validarCamposSub(SysmanFunciones.nvl(registro.getCampos()
                                        .get(SubAcademicasControladorEnum.TITULOOBTENIDO
                                                        .getValue()),
                                        "").toString(),
                                        "TB_TB3871")
                        || validarCamposSub(SysmanFunciones.nvl(registro.getCampos()
                                        .get(SubAcademicasControladorEnum.ESTABLECIMIENTO
                                                        .getValue()),
                                        "").toString(),
                                        "TB_TB3875")) {
            return true;
        }

        if (validarCamposSub(SysmanFunciones.nvl(registro.getCampos()
                        .get(SubAcademicasControladorEnum.PAIS
                                        .getValue()),
                        "").toString(),
                        "TB_TB3874")
                        || validarCamposSub(SysmanFunciones.nvl(registro.getCampos()
                                        .get(GeneralParameterEnum.MUNICIPIO.getName()), "")
                                        .toString(),
                                        "TB_TB3873")) {
            return true;

        }
        return false;
    }

    /**
     * Metodo que valida si el ano o el mes son nulos
     * 
     * @param anoMes
     * @param mensaje
     * @return
     */
    public boolean validarCamposSub(String anoMes, String mensaje) {
        if (SysmanFunciones.validarVariableVacio(anoMes)) {
            JsfUtil.agregarMensajeError(idioma.getString(mensaje));
            return true;
        }
        return false;
    }

   

    /**
     * metodo que valida indicadores de educacion media
     */
 

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicionSubotrosestudios(Registro registro) {

        indiceSubotrosestudios = registro.getIndice();
        paisSub = registro.getCampos()
                        .get(SubAcademicasControladorEnum.PAIS.getValue())
                        .toString();
        departamentoSub = registro.getCampos()
                        .get(SubAcademicasControladorEnum.DEPTO.getValue())
                        .toString();
        cargarListaPaisSub();
        cargarListaDepartamento();
        cargarListaMunicipio();

    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicionSubeducacionsuperior(Registro registro) {

        indiceSubeducacionsuperior = registro.getIndice();
        paisSubAca = registro.getCampos()
                        .get(SubAcademicasControladorEnum.PAIS.getValue())
                        .toString();
        departamentoSubAca = registro.getCampos()
                        .get(GeneralParameterEnum.DEPARTAMENTO.getName())
                        .toString();
        cargarListaPaisAca();
        cargarListaDepartamentoAca();
        cargarListaMunicipioAca();

    }
    public void activarEdicionEducacionbasica(Registro registro) {

        indiceEducacionbasica = registro.getIndice();
        paisSubBasico = registro.getCampos()
                        .get(SubAcademicasControladorEnum.PAIST.getValue())
                        .toString();
        departamentoSubBasico = registro.getCampos()
                        .get(SubAcademicasControladorEnum.DEPART.getValue())
                        .toString();
        cargarListaPaisBasic();
        cargarListaDepartBasic();
        cargarListaMunicBasic();

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
        precargarRegistro();
        if (rsExiste != null) {
            cargarRegistro(parametrosListado, ACCION_MODIFICAR);
        }
        else {
            cargarRegistro(null, ACCION_INSERTAR);

        }
        cargarListaSubeducacionsuperior();
        cargarListaSubeducacionsuperior();
        cargarListaEducacionbasica();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        insertaOtrosEstudios = true;
        if (ACCION_INSERTAR.equals(accion)) {
            inactivarEliminar = true;
            insertaEducacionSuperior = false;
        }
        else {
            inactivarEliminar = false;
            if ("11".equals(registro.getCampos().get(
                            SubAcademicasControladorEnum.GRADO.getValue())
                            .toString())) {
                insertaEducacionSuperior = true;
            }
            else {
                insertaEducacionSuperior = false;
            }


        }
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
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                        numeroDcto);
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);
        registro.getCampos().put(SubAcademicasControladorEnum.NEB_CODIGOPERSONA
                        .getValue(), codigoPersona);

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
            registro.getCampos()
            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos()
            .remove(GeneralParameterEnum.NUMERO_DCTO.getName());
            registro.getCampos()
            .remove(GeneralParameterEnum.SUCURSAL.getName());

        }

        registro.getCampos()
        .remove(SubAcademicasControladorEnum.NOMBREESTABLECIMIENTO
                        .getValue());

        if ("0".equals(grado)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3864"));
            return false;
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
     * Metodo ejecutado cuando se cierra el formulario
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "rid" };
        Object[] valores = { ridDatosBasicos };

        SessionUtil.redireccionarPorFormulario(SessionUtil.getModulo(),
                        Integer.toString(
                                        GeneralCodigoFormaEnum.NAT_DATOS_PERSONALES_CONTROLADOR
                                        .getCodigo()),
                        campos, valores, true);
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable gradoUno
     * 
     * @return gradoUno
     */
    public boolean getGradoUno() {
        return gradoUno;
    }

    /**
     * Asigna la variable gradoUno
     * 
     * @param gradoUno
     * Variable a asignar en gradoUno
     */
    public void setGradoUno(boolean gradoUno) {
        this.gradoUno = gradoUno;
    }

    

    /**
     * Retorna la variable paisSub
     * 
     * @return paisSub
     */
    public String getPaisSub() {
        return paisSub;
    }

    /**
     * Asigna la variable paisSub
     * 
     * @param paisSub
     * Variable a asignar en paisSub
     */
    public void setPaisSub(String paisSub) {
        this.paisSub = paisSub;
    }

    /**
     * Retorna la variable departamentoSub
     * 
     * @return departamentoSub
     */
    public String getDepartamentoSub() {
        return departamentoSub;
    }

    /**
     * Asigna la variable departamentoSub
     * 
     * @param departamentoSub
     * Variable a asignar en departamentoSub
     */
    public void setDepartamentoSub(String departamentoSub) {
        this.departamentoSub = departamentoSub;
    }

    /**
     * Retorna la variable paisSubAca
     * 
     * @return paisSubAca
     */
    public String getPaisSubAca() {
        return paisSubAca;
    }

    /**
     * Asigna la variable paisSubAca
     * 
     * @param paisSubAca
     * Variable a asignar en paisSubAca
     */
    public void setPaisSubAca(String paisSubAca) {
        this.paisSubAca = paisSubAca;
    }

    /**
     * Retorna la variable departamentoSubAca
     * 
     * @return departamentoSubAca
     */
    public String getDepartamentoSubAca() {
        return departamentoSubAca;
    }

    /**
     * Asigna la variable departamentoSubAca
     * 
     * @param departamentoSubAca
     * Variable a asignar en departamentoSubAca
     */
    public void setDepartamentoSubAca(String departamentoSubAca) {
        this.departamentoSubAca = departamentoSubAca;
    }

    /**
     * Retorna la variable inactivarEliminar
     * 
     * @return inactivarEliminar
     */
    public boolean isInactivarEliminar() {
        return inactivarEliminar;
    }

    /**
     * Asigna la variable inactivarEliminar
     * 
     * @param inactivarEliminar
     * Variable a asignar en inactivarEliminar
     */
    public void setInactivarEliminar(boolean inactivarEliminar) {
        this.inactivarEliminar = inactivarEliminar;
    }

    /**
     * Retorna la variable indiceSubotrosestudios
     * 
     * @return indiceSubotrosestudios
     */
    public int getIndiceSubotrosestudios() {
        return indiceSubotrosestudios;
    }

    /**
     * Asigna la variable indiceSubotrosestudios
     * 
     * @param indiceSubotrosestudios
     * Variable a asignar en indiceSubotrosestudios
     */
    public void setIndiceSubotrosestudios(int indiceSubotrosestudios) {
        this.indiceSubotrosestudios = indiceSubotrosestudios;
    }

    /**
     * Retorna la variable indiceSubeducacionsuperior
     * 
     * @return indiceSubeducacionsuperior
     */
    public int getIndiceSubeducacionsuperior() {
        return indiceSubeducacionsuperior;
    }

    /**
     * Asigna la variable indiceSubeducacionsuperior
     * 
     * @param indiceSubeducacionsuperior
     * Variable a asignar en indiceSubeducacionsuperior
     */
    public void setIndiceSubeducacionsuperior(int indiceSubeducacionsuperior) {
        this.indiceSubeducacionsuperior = indiceSubeducacionsuperior;
    }


    public int getIndiceEducacionbasica() {
        return indiceEducacionbasica;
    }

    public void setIndiceEducacionbasica(int indiceEducacionbasica) {
        this.indiceEducacionbasica = indiceEducacionbasica;
    }

    /**
     * Retorna la variable insertaEducacionSuperior
     * 
     * @return insertaEducacionSuperior
     */
    public boolean isInsertaEducacionSuperior() {
        return insertaEducacionSuperior;
    }

    /**
     * Asigna la variable insertaEducacionSuperior
     * 
     * @param insertaEducacionSuperior
     * Variable a asignar en insertaEducacionSuperior
     */
    public void setInsertaEducacionSuperior(boolean insertaEducacionSuperior) {
        this.insertaEducacionSuperior = insertaEducacionSuperior;
    }

    /**
     * Retorna la variable insertaOtrosEstudios
     * 
     * @return insertaOtrosEstudios
     */
    public boolean isInsertaOtrosEstudios() {
        return insertaOtrosEstudios;
    }

    /**
     * Asigna la variable insertaOtrosEstudios
     * 
     * @param insertaOtrosEstudios
     * Variable a asignar en insertaOtrosEstudios
     */
    public void setInsertaOtrosEstudios(boolean insertaOtrosEstudios) {
        this.insertaOtrosEstudios = insertaOtrosEstudios;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCodigoModalidad
     * 
     * @return listaCodigoModalidad
     */
    public List<Registro> getListaCodigoModalidad() {
        return listaCodigoModalidad;
    }

    /**
     * Asigna la lista listaCodigoModalidad
     * 
     * @param listaCodigoModalidad
     * Variable a asignar en listaCodigoModalidad
     */
    public void setListaCodigoModalidad(List<Registro> listaCodigoModalidad) {
        this.listaCodigoModalidad = listaCodigoModalidad;
    }

    /**
     * Retorna la lista listaPais
     * 
     * @return listaPais
     */
    public List<Registro> getListaPais() {
        return listaPais;
    }

    /**
     * Asigna la lista listaPais
     * 
     * @param listaPais
     * Variable a asignar en listaPais
     */
    public void setListaPais(List<Registro> listaPais) {
        this.listaPais = listaPais;
    }

    /**
     * Retorna la lista listaDepTermino
     * 
     * @return listaDepTermino
     */
    public List<Registro> getListaDepTermino() {
        return listaDepTermino;
    }

    /**
     * Asigna la lista listaDepTermino
     * 
     * @param listaDepTermino
     * Variable a asignar en listaDepTermino
     */
    public void setListaDepTermino(List<Registro> listaDepTermino) {
        this.listaDepTermino = listaDepTermino;
    }

    /**
     * Retorna la lista listaMunTermino
     * 
     * @return listaMunTermino
     */
    public List<Registro> getListaMunTermino() {
        return listaMunTermino;
    }

    /**
     * Asigna la lista listaMunTermino
     * 
     * @param listaMunTermino
     * Variable a asignar en listaMunTermino
     */
    public void setListaMunTermino(List<Registro> listaMunTermino) {
        this.listaMunTermino = listaMunTermino;
    }

    /**
     * Retorna la lista listaPaisSub
     * 
     * @return listaPaisSub
     */
    public List<Registro> getListaPaisSub() {
        return listaPaisSub;
    }

    /**
     * Asigna la lista listaPaisSub
     * 
     * @param listaPaisSub
     * Variable a asignar en listaPaisSub
     */
    public void setListaPaisSub(List<Registro> listaPaisSub) {
        this.listaPaisSub = listaPaisSub;
    }

    /**
     * Retorna la lista listaDepartamento
     * 
     * @return listaDepartamento
     */
    public List<Registro> getListaDepartamento() {
        return listaDepartamento;
    }

    /**
     * Asigna la lista listaDepartamento
     * 
     * @param listaDepartamento
     * Variable a asignar en listaDepartamento
     */
    public void setListaDepartamento(List<Registro> listaDepartamento) {
        this.listaDepartamento = listaDepartamento;
    }

    /**
     * Retorna la lista listaMunicipio
     * 
     * @return listaMunicipio
     */
    public List<Registro> getListaMunicipio() {
        return listaMunicipio;
    }

    /**
     * Asigna la lista listaMunicipio
     * 
     * @param listaMunicipio
     * Variable a asignar en listaMunicipio
     */
    public void setListaMunicipio(List<Registro> listaMunicipio) {
        this.listaMunicipio = listaMunicipio;
    }

    /**
     * Retorna la lista listaNombre
     * 
     * @return listaNombre
     */
    public List<Registro> getListaNombre() {
        return listaNombre;
    }

    /**
     * Asigna la lista listaNombre
     * 
     * @param listaNombre
     * Variable a asignar en listaNombre
     */
    public void setListaNombre(List<Registro> listaNombre) {
        this.listaNombre = listaNombre;
    }

    /**
     * Retorna la lista listaPaisAca
     * 
     * @return listaPaisAca
     */
    public List<Registro> getListaPaisAca() {
        return listaPaisAca;
    }

    /**
     * Asigna la lista listaPaisAca
     * 
     * @param listaPaisAca
     * Variable a asignar en listaPaisAca
     */
    public void setListaPaisAca(List<Registro> listaPaisAca) {
        this.listaPaisAca = listaPaisAca;
    }

    /**
     * Retorna la lista listaDepartamentoAca
     * 
     * @return listaDepartamentoAca
     */
    public List<Registro> getListaDepartamentoAca() {
        return listaDepartamentoAca;
    }

    /**
     * Asigna la lista listaDepartamentoAca
     * 
     * @param listaDepartamentoAca
     * Variable a asignar en listaDepartamentoAca
     */
    public void setListaDepartamentoAca(List<Registro> listaDepartamentoAca) {
        this.listaDepartamentoAca = listaDepartamentoAca;
    }

    /**
     * Retorna la lista listaMunicipioAca
     * 
     * @return listaMunicipioAca
     */
    public List<Registro> getListaMunicipioAca() {
        return listaMunicipioAca;
    }

    /**
     * Asigna la lista listaMunicipioAca
     * 
     * @param listaMunicipioAca
     * Variable a asignar en listaMunicipioAca
     */
    public void setListaMunicipioAca(List<Registro> listaMunicipioAca) {
        this.listaMunicipioAca = listaMunicipioAca;
    }

    /**
     * Retorna la lista listaEstablecimiento
     * 
     * @return listaEstablecimiento
     */
    public List<Registro> getListaEstablecimiento() {
        return listaEstablecimiento;
    }

    /**
     * Asigna la lista listaEstablecimiento
     * 
     * @param listaEstablecimiento
     * Variable a asignar en listaEstablecimiento
     */
    public void setListaEstablecimiento(List<Registro> listaEstablecimiento) {
        this.listaEstablecimiento = listaEstablecimiento;
    }

    /**
     * Retorna la lista listaEstablecimientoEduSup
     * 
     * @return listaEstablecimientoEduSup
     */
    public List<Registro> getListaEstablecimientoEduSup() {
        return listaEstablecimientoEduSup;
    }

    /**
     * Asigna la lista listaEstablecimientoEduSup
     * 
     * @param listaEstablecimientoEduSup
     * Variable a asignar en listaEstablecimientoEduSup
     */
    public void setListaEstablecimientoEduSup(
        List<Registro> listaEstablecimientoEduSup) {
        this.listaEstablecimientoEduSup = listaEstablecimientoEduSup;
    }

    /**
     * Retorna la lista listaEstablecimientoOtros
     * 
     * @return listaEstablecimientoOtros
     */
    public List<Registro> getListaEstablecimientoOtros() {
        return listaEstablecimientoOtros;
    }

    /**
     * Asigna la lista listaEstablecimientoOtros
     * 
     * @param listaEstablecimientoOtros
     * Variable a asignar en listaEstablecimientoOtros
     */
    public void setListaEstablecimientoOtros(
        List<Registro> listaEstablecimientoOtros) {
        this.listaEstablecimientoOtros = listaEstablecimientoOtros;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodigoTitulo
     * 
     * @return listaCodigoTitulo
     */
    public RegistroDataModelImpl getListaCodigoTitulo() {
        return listaCodigoTitulo;
    }

    /**
     * Asigna la lista listaCodigoTitulo
     * 
     * @param listaCodigoTitulo
     * Variable a asignar en listaCodigoTitulo
     */
    public void setListaCodigoTitulo(RegistroDataModelImpl listaCodigoTitulo) {
        this.listaCodigoTitulo = listaCodigoTitulo;
    }

    /**
     * Retorna la lista listaCodigoTituloE
     * 
     * @return listaCodigoTituloE
     */
    public RegistroDataModelImpl getListaCodigoTituloE() {
        return listaCodigoTituloE;
    }

    /**
     * Asigna la lista listaCodigoTituloE
     * 
     * @param listaCodigoTituloE
     * Variable a asignar en listaCodigoTituloE
     */
    public void setListaCodigoTituloE(
        RegistroDataModelImpl listaCodigoTituloE) {
        this.listaCodigoTituloE = listaCodigoTituloE;
    }


    public List<Registro> getListaGrado() {
        return listaGrado;
    }

    public void setListaGrado(List<Registro> listaGrado) {
        this.listaGrado = listaGrado;
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
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaSubeducacionsuperior
     * 
     * @return listaSubeducacionsuperior
     */
    public RegistroDataModelImpl getListaSubeducacionsuperior() {
        return listaSubeducacionsuperior;
    }

    /**
     * Asigna la lista listaSubeducacionsuperior
     * 
     * @param listaSubeducacionsuperior
     * Variable a asignar en listaSubeducacionsuperior
     */
    public void setListaSubeducacionsuperior(
        RegistroDataModelImpl listaSubeducacionsuperior) {
        this.listaSubeducacionsuperior = listaSubeducacionsuperior;
    }

    /**
     * Retorna la lista listaSubotrosestudios
     * 
     * @return listaSubotrosestudios
     */
    public RegistroDataModelImpl getListaSubotrosestudios() {
        return listaSubotrosestudios;
    }

    /**
     * Asigna la lista listaSubotrosestudios
     * 
     * @param listaSubotrosestudios
     * Variable a asignar en listaSubotrosestudios
     */
    public void setListaSubotrosestudios(
        RegistroDataModelImpl listaSubotrosestudios) {
        this.listaSubotrosestudios = listaSubotrosestudios;
    }



    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>

    public RegistroDataModelImpl getListaEducacionbasica() {
        return listaEducacionbasica;
    }

    public void setListaEducacionbasica(
        RegistroDataModelImpl listaEducacionbasica) {
        this.listaEducacionbasica = listaEducacionbasica;
    }

    /**
     * Retorna el objeto registroSubSubEducacionSuperior
     * 
     * @return registroSubSubEducacionSuperior
     */
    public Registro getRegistroSubSubEducacionSuperior() {
        return registroSubSubEducacionSuperior;
    }

    /**
     * Asigna el objeto registroSubSubEducacionSuperior
     * 
     * @param registroSubSubEducacionSuperior
     * Variable a asignar en registroSubSubEducacionSuperior
     */
    public void setRegistroSubSubEducacionSuperior(
        Registro registroSubSubEducacionSuperior) {
        this.registroSubSubEducacionSuperior = registroSubSubEducacionSuperior;
    }

    /**
     * Retorna el objeto registroSubSubOtrosEstudios
     * 
     * @return registroSubSubOtrosEstudios
     */
    public Registro getRegistroSubSubOtrosEstudios() {
        return registroSubSubOtrosEstudios;
    }

    /**
     * Asigna el objeto registroSubSubOtrosEstudios
     * 
     * @param registroSubSubOtrosEstudios
     * Variable a asignar en registroSubSubOtrosEstudios
     */
    public void setRegistroSubSubOtrosEstudios(
        Registro registroSubSubOtrosEstudios) {
        this.registroSubSubOtrosEstudios = registroSubSubOtrosEstudios;
    }

    public Registro getRegistroSubEducacionBasica() {
        return registroSubEducacionBasica;
    }

    public void setRegistroSubEducacionBasica(Registro registroSubEducacionBasica) {
        this.registroSubEducacionBasica = registroSubEducacionBasica;
    }

    public String getPaisSubBasico() {
        return paisSubBasico;
    }

    public void setPaisSubBasico(String paisSubBasico) {
        this.paisSubBasico = paisSubBasico;
    }

    public List<Registro> getListaEstablecBasic() {
        return listaEstablecBasic;
    }

    public void setListaEstablecBasic(List<Registro> listaEstablecBasic) {
        this.listaEstablecBasic = listaEstablecBasic;
    }

    public List<Registro> getListaPaisBasic() {
        return listaPaisBasic;
    }

    public void setListaPaisBasic(List<Registro> listaPaisBasic) {
        this.listaPaisBasic = listaPaisBasic;
    }

    public List<Registro> getListaDepartBasic() {
        return listaDepartBasic;
    }

    public void setListaDepartBasic(List<Registro> listaDepartBasic) {
        this.listaDepartBasic = listaDepartBasic;
    }

    public List<Registro> getListaMunicBasic() {
        return listaMunicBasic;
    }

    public void setListaMunicBasic(List<Registro> listaMunicBasic) {
        this.listaMunicBasic = listaMunicBasic;
    }

    public RegistroDataModelImpl getListaCodigoTituloBasic() {
        return listaCodigoTituloBasic;
    }

    public void setListaCodigoTituloBasic(
        RegistroDataModelImpl listaCodigoTituloBasic) {
        this.listaCodigoTituloBasic = listaCodigoTituloBasic;
    }

    public RegistroDataModelImpl getListaCodigoTituloBasicE() {
        return listaCodigoTituloBasicE;
    }

    public void setListaCodigoTituloBasicE(
        RegistroDataModelImpl listaCodigoTituloBasicE) {
        this.listaCodigoTituloBasicE = listaCodigoTituloBasicE;
    }

    
    // </SET_GET_ADICIONALES>
}
