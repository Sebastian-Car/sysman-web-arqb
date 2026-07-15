package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.FrmsubestudiolocalizacionproysControladorEnum;
import com.sysman.precontractual.enums.FrmsubestudiolocalizacionproysControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
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

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author acaceres
 * @version 1, 04/04/2016
 * 
 * @modifier amonroy
 * @version 2, 30/08/2017 Proceso de Refactoring, Revision de buenas
 * practicas sugeridas por la herramienta SonarLint e implementación
 * de EJBs para la generacion del consecutivo
 */
@ManagedBean
@ViewScoped
public class FrmsubestudiolocalizacionproysControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que almacena el codigo del módulo en
     * la cual inicio sesion el usuario.
     */
    private final String modulo;

    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo CODIGO en el formulario, almacena el texto
     * CODIGO el cual es un campo del registro
     */
    private final String cCodigo;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo PAIS en el formulario, almacena el texto PAIS
     * el cual es un campo del registro
     */
    private final String cPais;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo DEPARTAMENTO en el formulario, almacena el
     * texto DEPARTAMENTO el cual es un campo del registro
     */
    private final String cDepartamento;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo CIUDAD en el formulario, almacena el texto
     * CIUDAD el cual es un campo del registro
     */
    private final String cCiudad;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo BARRIO en el formulario, almacena el texto
     * BARRIO el cual es un campo del registro
     */
    private final String cBarrio;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se almacena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    /**
     * Listado para seleccionar el barrio cuando se esta realizando la
     * insercion de un registro
     */
    private RegistroDataModelImpl listaBarrioUbicacion;
    /**
     * Listado para seleccionar el barrio cuando se esta realizando la
     * actualizacion de un registro
     */
    private RegistroDataModelImpl listaBarrioUbicacionE;
    /**
     * Listado para el combo de pais
     */
    private List<Registro> listaPais;
    /**
     * Listado para el combo de Departamento
     */
    private List<Registro> listaDepartamento;
    /**
     * Listado para el combo de ciudad
     */
    private List<Registro> listaCiudad;
    /**
     * Atributo que almacena el pais seleccionado al editar el
     * registro
     */
    private String pais;
    /**
     * Atributo que almacena el depatamento seleccionado al editar el
     * registro
     */
    private String departamento;
    /**
     * Atributo que almacena la ciudad seleccionada al editar el
     * registro
     */
    private String ciudad;
    /*
     * Estructura que contiene los parametros que se traen desde el
     * formulario principal
     */
    private HashMap<String, Object> ridL;
    /**
     * Atributo que almacena el Valor del CODIGOESTUDIO con el que se
     * esta trabajando
     */
    private String txtCodEstudio;
    /**
     * Atributo que almacena el valor del indicador para permitir las
     * operaciones DRUD en el formulario
     */
    private boolean esCreador;

    /**
     * Atributo que almacena el valor del indicador para permitir la
     * visualización del campo DIVISION MUNICIPAL en el formulario
     */
    private boolean verDivisionMunicipal;

    /**
     * Atributo que almacena el valor del parámetro MANEJA DIVISION
     * MUNICIPAL
     */
    private String manejaDivisionMunicipal;

    /**
     * Implementacion del EJB de EjbSysmanUtilRemote para hacer el
     * llamado a las funciones que se invocan dentro del Controlador y
     * se encuentran almacenadas en el paquete PCK_SYSMAN_UTL
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Atributo que permite identificar si se ha activado un registro
     * de la grilla o se ha ingresado al metodo cambiarCiudadC(), esto
     * con el fin de inicializar el valor del campo BARRIO en el
     * registro seleccionado por el usuario
     */
    private int indiceAuxCiudad;
    /**
     * Atributo que permite identificar si se ha activado un registro
     * de la grilla o se ha ingresado al metodo
     * cambiarDepartamentoC(), esto con el fin de inicializar el valor
     * de los campos CIUDAD y BARRIO en el registro seleccionado por
     * el usuario
     */
    private int indiceAuxDepartamento;
    private String vigenciaPeriodo;

    /**
     * Crea una nueva instancia de
     * FrmsubestudiolocalizacionproysControlador
     */
    public FrmsubestudiolocalizacionproysControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cDepartamento = GeneralParameterEnum.DEPARTAMENTO.getName();
        cCodigo = GeneralParameterEnum.CODIGO.getName();
        cCiudad = GeneralParameterEnum.CIUDAD.getName();
        cBarrio = GeneralParameterEnum.BARRIO.getName();
        cPais = FrmsubestudiolocalizacionproysControladorEnum.PAIS.getValue();
        indice = -1;
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMSUBESTUDIOLOCALIZACIONPROYS_CONTROLADOR
                            .getCodigo();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                ridL = (HashMap<String, Object>) parametrosEntrada.get("rid");
                txtCodEstudio = parametrosEntrada.get("codEstudio").toString();
                vigenciaPeriodo = parametrosEntrada.get("vigenciaPeriodo").toString();
                esCreador = Boolean.parseBoolean(
                                parametrosEntrada.get("esCreador").toString());
            }
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(FrmsubestudiolocalizacionproysControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
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

        try {
            manejaDivisionMunicipal = ejbSysmanUtil.consultarParametro(
                            compania, "MANEJA DIVISION MUNICIPAL", modulo,
                            new Date(), false);

            manejaDivisionMunicipal = manejaDivisionMunicipal == null ? "SI"
                : manejaDivisionMunicipal;

            if ("NO".equals(manejaDivisionMunicipal)) {
                verDivisionMunicipal = false;
            }
            else {
                verDivisionMunicipal = true;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        enumBase = GenericUrlEnum.ES_LOCALIZA;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListaPais();
        cargarListaDepartamento();
        cargarListaCiudad();
        abrirFormulario();
    }

    /**
     * Realiza el llamado a los servicios que permiten la ejecucion de
     * las operaciones CRUD en el formulario
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado
                        .put(FrmsubestudiolocalizacionproysControladorEnum.CODESTUDIO
                                        .getValue(), txtCodEstudio);

        if ("NO".equals(manejaDivisionMunicipal)) {
            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmsubestudiolocalizacionproysControladorUrlEnum.URL001
                                                            .getValue());

            urlActualizacion = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmsubestudiolocalizacionproysControladorUrlEnum.URL002
                                                            .getValue());
        }
    }

    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
        indiceAuxCiudad = 0;
        indiceAuxDepartamento = 0;
        pais = listaInicial.getDatasource().get(indice % 10).getCampos()
                        .get(cPais).toString();
        cargarListaDepartamento();
        cargarListaCiudad();

        if ("SI".equals(manejaDivisionMunicipal)) {
            cargarListaBarrioUbicacionE();
        }
    }

    public void cargarListaPais() {
        try {
            listaPais = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmsubestudiolocalizacionproysControladorUrlEnum.URL4340
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaDepartamento() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(FrmsubestudiolocalizacionproysControladorEnum.PAIS
                            .getValue(), pais);

            listaDepartamento = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmsubestudiolocalizacionproysControladorUrlEnum.URL4681
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCiudad() {
        Map<String, Object> param = new TreeMap<>();
        param.put(FrmsubestudiolocalizacionproysControladorEnum.PAIS.getValue(),
                        pais);
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(), departamento);

        try {
            listaCiudad = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmsubestudiolocalizacionproysControladorUrlEnum.URL5422
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaBarrioUbicacion() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmsubestudiolocalizacionproysControladorUrlEnum.URL5934
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(FrmsubestudiolocalizacionproysControladorEnum.PAIS.getValue(),
                        pais);
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(), departamento);
        param.put(GeneralParameterEnum.CIUDAD.getName(), ciudad);

        listaBarrioUbicacion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaBarrioUbicacionE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmsubestudiolocalizacionproysControladorUrlEnum.URL7148
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(FrmsubestudiolocalizacionproysControladorEnum.PAIS.getValue(),
                        pais);
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(), departamento);
        param.put(GeneralParameterEnum.CIUDAD.getName(), ciudad);

        listaBarrioUbicacionE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cambiarPais() {
        // <CODIGO_DESARROLLADO>
        departamento = null;
        ciudad = null;
        registro.getCampos().put(cDepartamento, null);
        registro.getCampos().put(cCiudad, null);
        if ("SI".equals(manejaDivisionMunicipal)) {
            registro.getCampos().put(cBarrio, null);
        }
        pais = registro.getCampos().get(cPais).toString();

        cargarListaDepartamento();
        cargarListaCiudad();

        if ("SI".equals(manejaDivisionMunicipal)) {
            cargarListaBarrioUbicacion();
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDepartamento() {
        // <CODIGO_DESARROLLADO>
        ciudad = null;
        registro.getCampos().put(cCiudad, null);
        if ("SI".equals(manejaDivisionMunicipal)) {
            registro.getCampos().put(cBarrio, null);
        }

        departamento = registro.getCampos().get(cDepartamento).toString();
        cargarListaCiudad();

        if ("SI".equals(manejaDivisionMunicipal)) {
            cargarListaBarrioUbicacion();
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCiudad() {
        // <CODIGO_DESARROLLADO>
        ciudad = registro.getCampos().get(cCiudad).toString();

        if ("SI".equals(manejaDivisionMunicipal)) {
            registro.getCampos().put(cBarrio, null);

            cargarListaBarrioUbicacion();
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Se adiciona la validacion del indice para que los valores para
     * el departamento, la ciudad y el barrio se inicialicen cuando se
     * actualice el valor del pais
     * 
     * @param rowNum
     * Registro de la grilla en el que se esta trabajando
     */
    public void cambiarPaisC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if (indice == -1) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(cDepartamento, null);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(cCiudad, null);
            if ("SI".equals(manejaDivisionMunicipal)) {
                listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                .put(cBarrio, null);
            }
        }

        pais = listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(cPais).toString();
        cargarListaDepartamento();
        cargarListaCiudad();
        if ("SI".equals(manejaDivisionMunicipal)) {
            cargarListaBarrioUbicacionE();
        }

        indice = -1;
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDepartamentoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if (indiceAuxDepartamento == -1) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(cCiudad, null);
            if ("SI".equals(manejaDivisionMunicipal)) {
                listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                .put(cBarrio, null);
            }
        }
        departamento = listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(cDepartamento).toString();
        cargarListaCiudad();

        if ("SI".equals(manejaDivisionMunicipal)) {
            cargarListaBarrioUbicacionE();
        }

        indiceAuxDepartamento = -1;
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCiudadC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if (indiceAuxCiudad == -1) {
            if ("SI".equals(manejaDivisionMunicipal)) {
                listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                .put(cBarrio, null);
            }
        }
        ciudad = listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(cCiudad).toString();

        if ("SI".equals(manejaDivisionMunicipal)) {
            cargarListaBarrioUbicacionE();
        }

        indiceAuxCiudad = -1;
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarBarrioUbicacion() {
        // <CODIGO_DESARROLLADO>
        if (registro.getCampos().get("BARRIO") == null) {
            JsfUtil.agregarMensajeAlerta("Debe ingresar un Barrio.");
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control BarrioUbicacion en la
     * fila seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarBarrioUbicacionC(int rowNum) {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        // listaInicial.getDatasource().get(rowNum %
        // 10).getCampos().put("FECHALARGA", "hola ");
        // Para el cambio en una fila selecciona (PARA SUBFORMULARIOS)
        // se realiza como lo muestra la siguiente linea
        // listaInicial.get(rowNum).getCampos().put("FECHALARGA",
        // "hola ");
        // <CODIGO_DESARROLLADO>
        if (listaInicial.getDatasource().get(rowNum %
            10).getCampos().get("BARRIO") == null) {
            JsfUtil.agregarMensajeAlerta("Debe ingresar un Barrio.");
        }
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaBarrioUbicacion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cBarrio,
                        registroAux.getCampos().get(cCodigo));

    }

    public void seleccionarFilaBarrioUbicacionE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(cCodigo).toString();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO> // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        String strCondicion = "";
        long consecutivo = 0;

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.COD_ESTUDIO.getName(),
                        txtCodEstudio);
        registro.getCampos()
                        .remove(FrmsubestudiolocalizacionproysControladorEnum.NOMBREPAIS
                                        .getValue());
        registro.getCampos()
                        .remove(FrmsubestudiolocalizacionproysControladorEnum.NOMBREDEPARTAMENTO
                                        .getValue());
        registro.getCampos()
                        .remove(FrmsubestudiolocalizacionproysControladorEnum.NOMBRECIUDAD
                                        .getValue());
        
        strCondicion = SysmanFunciones.concatenar(
                "COMPANIA     = ''", compania, "'' ",
                "AND COD_ESTUDIO  = ", txtCodEstudio, " ",
                " AND PAIS         = ''",
                registro.getCampos().get(cPais).toString(), "'' ",
                "AND DEPARTAMENTO = ''",
                registro.getCampos().get(cDepartamento).toString(),
                "'' ", " AND CIUDAD       = ''",
                registro.getCampos().get(cCiudad).toString(),
                "'' ");
		try {
			consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
			                FrmsubestudiolocalizacionproysControladorEnum.ES_LOCALIZA
			                                .getValue(),
			                strCondicion,
			                cCodigo,
			                "1");		
		}
			catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

        if ("SI".equals(manejaDivisionMunicipal)) {
            registro.getCampos()
                            .remove(FrmsubestudiolocalizacionproysControladorEnum.NOMBREBARRIO
                                            .getValue());

            strCondicion = SysmanFunciones.concatenar(
                            "COMPANIA     = ''", compania, "'' ",
                            "AND COD_ESTUDIO  = ", txtCodEstudio, " ",
                            " AND PAIS         = ''",
                            registro.getCampos().get(cPais).toString(), "'' ",
                            "AND DEPARTAMENTO = ''",
                            registro.getCampos().get(cDepartamento).toString(),
                            "'' ", " AND CIUDAD       = ''",
                            registro.getCampos().get(cCiudad).toString(), "'' ",
                            "AND BARRIO       = ''",
                            registro.getCampos().get(cBarrio).toString(),
                            "'' ");
            try {
                long consecutivoDM = ejbSysmanUtil.generarConsecutivoConValorInicial(
                                FrmsubestudiolocalizacionproysControladorEnum.ES_LOCALIZA
                                                .getValue(),
                                strCondicion,
                                cCodigo,
                                "1");
                if(consecutivoDM > 1)
                {
                	JsfUtil.agregarMensajeError(idioma.getString("TB_TB1326"));
                	return false;
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
        else
        {
        	if(consecutivo > 1)
            {
            	JsfUtil.agregarMensajeError(idioma.getString("TB_TB1326"));
            	return false;
            }
        }
        
        registro.getCampos().put(cCodigo, consecutivo);

        // </CODIGO_DESARROLLADO>
        return true;

    }
    
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
    	String strCondicion = "";
        long consecutivo = 0;
        
    	strCondicion = SysmanFunciones.concatenar(
                "COMPANIA     = ''", compania, "'' ",
                "AND COD_ESTUDIO  = ", txtCodEstudio, " ",
                " AND PAIS         = ''",
                registro.getCampos().get(cPais).toString(), "'' ",
                "AND DEPARTAMENTO = ''",
                registro.getCampos().get(cDepartamento).toString(),
                "'' ", " AND CIUDAD       = ''",
                registro.getCampos().get(cCiudad).toString(),
                "'' ");
		try {
			consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
			                FrmsubestudiolocalizacionproysControladorEnum.ES_LOCALIZA
			                                .getValue(),
			                strCondicion,
			                cCodigo,
			                "1");		
		}
			catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

        if ("SI".equals(manejaDivisionMunicipal)) {
            registro.getCampos()
                            .remove(FrmsubestudiolocalizacionproysControladorEnum.NOMBREBARRIO
                                            .getValue());

            strCondicion = SysmanFunciones.concatenar(
                            "COMPANIA     = ''", compania, "'' ",
                            "AND COD_ESTUDIO  = ", txtCodEstudio, " ",
                            " AND PAIS         = ''",
                            registro.getCampos().get(cPais).toString(), "'' ",
                            "AND DEPARTAMENTO = ''",
                            registro.getCampos().get(cDepartamento).toString(),
                            "'' ", " AND CIUDAD       = ''",
                            registro.getCampos().get(cCiudad).toString(), "'' ",
                            "AND BARRIO       = ''",
                            registro.getCampos().get(cBarrio).toString(),
                            "'' ");
            try {
                long consecutivoDM = ejbSysmanUtil.generarConsecutivoConValorInicial(
                                FrmsubestudiolocalizacionproysControladorEnum.ES_LOCALIZA
                                                .getValue(),
                                strCondicion,
                                cCodigo,
                                "1");
                if(consecutivoDM > 1)
                {
                	JsfUtil.agregarMensajeError(idioma.getString("TB_TB1326"));
                	return false;
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
        else
        {
        	if(consecutivo > 1)
            {
            	JsfUtil.agregarMensajeError(idioma.getString("TB_TB1326"));
            	return false;
            }
        }
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

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
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.COD_ESTUDIO.getName());
        registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
        registro.getCampos()
                        .remove(FrmsubestudiolocalizacionproysControladorEnum.NOMBREPAIS
                                        .getValue());
        registro.getCampos()
                        .remove(FrmsubestudiolocalizacionproysControladorEnum.NOMBREDEPARTAMENTO
                                        .getValue());
        registro.getCampos()
                        .remove(FrmsubestudiolocalizacionproysControladorEnum.NOMBRECIUDAD
                                        .getValue());

        if ("SI".equals(manejaDivisionMunicipal)) {
            registro.getCampos()
                            .remove(FrmsubestudiolocalizacionproysControladorEnum.NOMBREBARRIO
                                            .getValue());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario,
     * 
     * Redirecciona al formulario "frmestprevioproy"
     * 
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("ridEstPrevios", ridL);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRMESTPREVIOPROYS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public List<Registro> getListaPais() {
        return listaPais;
    }

    public void setListaPais(List<Registro> listaPais) {
        this.listaPais = listaPais;
    }

    public List<Registro> getListaDepartamento() {
        return listaDepartamento;
    }

    public void setListaDepartamento(List<Registro> listaDepartamento) {
        this.listaDepartamento = listaDepartamento;
    }

    public List<Registro> getListaCiudad() {
        return listaCiudad;
    }

    public void setListaCiudad(List<Registro> listaCiudad) {
        this.listaCiudad = listaCiudad;
    }

    public RegistroDataModelImpl getListaBarrioUbicacion() {
        return listaBarrioUbicacion;
    }

    public void setListaBarrioUbicacion(
        RegistroDataModelImpl listaBarrioUbicacion) {
        this.listaBarrioUbicacion = listaBarrioUbicacion;
    }

    public RegistroDataModelImpl getListaBarrioUbicacionE() {
        return listaBarrioUbicacionE;
    }

    public void setListaBarrioUbicacionE(
        RegistroDataModelImpl listaBarrioUbicacionE) {
        this.listaBarrioUbicacionE = listaBarrioUbicacionE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public boolean isEsCreador() {
        return esCreador;
    }

    public void setEsCreador(boolean esCreador) {
        this.esCreador = esCreador;
    }

    /**
     * @return the verDivisionMunicipal
     */
    public boolean isVerDivisionMunicipal() {
        return verDivisionMunicipal;
    }

    /**
     * @param verDivisionMunicipal
     * the verDivisionMunicipal to set
     */
    public void setVerDivisionMunicipal(boolean verDivisionMunicipal) {
        this.verDivisionMunicipal = verDivisionMunicipal;
    }

}
