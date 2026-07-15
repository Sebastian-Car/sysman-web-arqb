/*-
 * ConfigurarfutcategoriasControlador.java
 *
 * 1.0
 * 
 * 05/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.chipfut;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.chipfut.ejb.EjbChipFutCeroRemote;
import com.sysman.chipfut.enums.ConfigurarFutCategoriasControladorEnum;
import com.sysman.chipfut.enums.ConfigurarFutCategoriasControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 * Formulario que permite configurar las categorias nuevas del plan
 * presupuestal.
 *
 * @version 1.0, 05/07/2018
 * @author jreina
 * 
 * 
 * @version 2.0, 19/07/2018, Cambio en los DSS y agregación de
 * validaciones de los combos de anio y formato a configurar. Se creo
 * el proceso PCK_CHIPFUT.PR_TRASLADARCONFFUT y se creo el metodo
 * oprimirrevisarCuentas()
 * @author eamaya
 */

@ManagedBean
@ViewScoped
public class ConfigurarFutCategoriasControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que almacena el codigo del usuario
     * que inicio sesion en la aplicacion
     */
    private final String usuario;

    /**
     * Constante a nivel de clase que almacena el modulo que inicio
     * sesion en la aplicacion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>

    private String configurar;
    private String naturaleza;
    private String ano;
    private String destino;

    /**
     * Variable que carga el nombre del boton de estado de cuentas
     */
    private String tituloBtnCuentas;

    private boolean visibleDialogo;
    private boolean bloqueadoCampos;
    private boolean visibleDesplazados1;
    private boolean visibleCuentasPagar;
    private boolean visibleEjecucionFondoSalud;
    private boolean visibleTesoreriaSalud;
    private boolean visibleVigFuturaEjecucion;
    private boolean visibleIngresoReserva;
    private boolean visibleVictimas;
    private boolean visibleFuenteFls;
    private boolean visibleHechoVictimizante;
    private boolean visibleTipoSalud;
    private boolean visibleFuente;
    private boolean visibleCovid;// almacenamiento de el checkbox de covid
    
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;
    private RegistroDataModelImpl listaFuenteFut1;
    private RegistroDataModelImpl listaFuenteFut1E;
    private RegistroDataModelImpl listafuente;
    private RegistroDataModelImpl listafuenteE;
    private RegistroDataModelImpl listahechoVictimizante;
    private RegistroDataModelImpl listahechoVictimizanteE;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listacampoFut;
    private RegistroDataModelImpl listacampoFutE;

    /**
     * Lista que almacena los codigos fut de tipo cuenta a pagar
     */
    private RegistroDataModelImpl listacuentaPagar;
    /**
     * Lista que almacena los codigos fut de tipo cuenta a pagar
     */
    private RegistroDataModelImpl listacuentaPagarE;
    /**
     * Lista que almacena los codigos fut de tipo ejecucion salud
     */
    private RegistroDataModelImpl listaejecucionSalud;
    /**
     * Lista que almacena los codigos fut de tipo ejecucion salud
     */
    private RegistroDataModelImpl listaejecucionSaludE;
    /**
     * Lista que almacena los codigos fut de tipo salud tesoreria
     */
    private RegistroDataModelImpl listasaludTesoreria;
    /**
     * Lista que almacena los codigos fut de tipo salud tesoreria
     */
    private RegistroDataModelImpl listasaludTesoreriaE;
    /**
     * Lista que almacena los codigos fut de tipo vigencia futura
     */
    private RegistroDataModelImpl listavigenciaFutura;
    /**
     * Lista que almacena los codigos fut de tipo vigencia futura
     */
    private RegistroDataModelImpl listavigenciaFuturaE;
    /**
     * Lista que almacena los codigos fut de tipo ingreso reservas
     */
    private RegistroDataModelImpl listaingresoReservas;
    /**
     * Lista que almacena los codigos fut de tipo ingreso reservas
     */
    private RegistroDataModelImpl listaingresoReservasE;
    /**
     * Lista que almacena los codigos fut de tipo victimas1
     */
    private RegistroDataModelImpl listavictimas1;
    /**
     * Lista que almacena los codigos fut de tipo victimas1
     */
    private RegistroDataModelImpl listavictimas1E;
    /**
     * Lista que almacena los codigos fut de tipo covid seleccionado
     */
    private RegistroDataModelImpl listaCovid;
    /**
     * Lista que almacena los codigos fut de tipo covid seleccionado
     */
    private RegistroDataModelImpl listaCovidE;

  

	/**
     * Atributo que almacena el estado en el que esta el boton de
     * revisar cuentas
     */

    private int estado;

    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    /**
     * Variable que almacena el valor del parametro MANEJA AUXILIAR
     * POR FUENTE EN PRESUPUESTO
     */
    private String parametroManejaAuxiliar;

    @EJB
    private EjbChipFutCeroRemote ejbChipFutCero;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ConfigurarfutcategoriasControlador
     */
    public ConfigurarFutCategoriasControlador() {
        super();
        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser().getCodigo();
        ano = String.valueOf(SysmanFunciones.ano(new Date()));
        modulo = SessionUtil.getModulo();
        estado = 0;
        visibleDesplazados1 = visibleCuentasPagar = visibleEjecucionFondoSalud = visibleTesoreriaSalud = visibleVigFuturaEjecucion = visibleIngresoReserva = visibleVictimas =visibleCovid= false;
        tituloBtnCuentas = idioma.getString("TB_TB4168");

        try {
            numFormulario = GeneralCodigoFormaEnum.CONFIGURARFUTFORMULARIOS_CONTROLADOR
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

        try {
            parametroManejaAuxiliar = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "MANEJA AUXILIAR POR FUENTE EN PRESUPUESTO",
                                            "3", new Date(), false), "NO")
                            .toString();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        tabla = "PLAN_PPTAL_CONFIG";
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaAno();
        cargarListaFuenteFut1();
        cargarListaFuenteFut1E();
        cargarListafuente();
        cargarListahechoVictimizante();
        // </CARGAR_LISTA>
        
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacampoFut();
        cargarListacampoFutE();
        cargarListacuentaPagar();
        cargarListacuentaPagarE();
        cargarListaejecucionSalud();
        cargarListaejecucionSaludE();
        cargarListasaludTesoreria();
        cargarListasaludTesoreriaE();
        cargarListavigenciaFutura();
        cargarListavigenciaFuturaE();
        cargarListaingresoReservas();
        cargarListaingresoReservasE();
        cargarListavictimas1();
        cargarListavictimas1E();
        //cargarListaCovidE();
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
        parametrosListado.put(GeneralParameterEnum.ANO.getName(),
                        ano);
        parametrosListado.put(GeneralParameterEnum.NATURALEZA.getName(),
                        naturaleza);
        parametrosListado.put(GeneralParameterEnum.DESTINO.getName(),
                        destino);
        parametrosListado.put("PARAMETRO", parametroManejaAuxiliar);

        if (estado == 1) {

            parametrosListado.put("CONFIGURAR",
                            configurar);
        }
        else {

            parametrosListado.put("CONFIGURAR",
                            "");
        }
        
        
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        ConfigurarFutCategoriasControladorUrlEnum.URL15084
                                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarFutCategoriasControladorUrlEnum.URL9050
                                                        .getValue());
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConfigurarFutCategoriasControladorUrlEnum.URL181
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
     * Carga la lista listaFuenteFut1
     *
     */
    public void cargarListaFuenteFut1() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarFutCategoriasControladorUrlEnum.URL208
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano);

        param.put(GeneralParameterEnum.CODIGO.getName(),
                        configurar);

        listaFuenteFut1 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, ConfigurarFutCategoriasControladorEnum.CODIGO_FUT
                                        .getValue());
    }

    /**
     * 
     * Carga la lista listaFuenteFut1
     *
     */
    public void cargarListaFuenteFut1E() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarFutCategoriasControladorUrlEnum.URL209
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano);
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        configurar);

        listaFuenteFut1E = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, ConfigurarFutCategoriasControladorEnum.CODIGO_FUT
                                        .getValue());
    }
    /**
     * 
     * Carga la lista listafuente
     *
     */
    public void cargarListafuente() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarFutCategoriasControladorUrlEnum.URL226
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano);

        listafuente = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listahechoVictimizante
     *
     */
    public void cargarListahechoVictimizante() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarFutCategoriasControladorUrlEnum.URL5478
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano);

        listahechoVictimizante = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listahechoVictimizante
     *
     */
    public void cargarListahechoVictimizanteE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarFutCategoriasControladorUrlEnum.URL5478
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano);

        listahechoVictimizanteE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    
    /**public void cargarListaCovid() {
    	 Map<String, Object> param = new TreeMap<>();
         param.put(GeneralParameterEnum.COMPANIA.getName(),
                         compania);
         param.put(GeneralParameterEnum.ANO.getName(),
                         ano);
         
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarFutCategoriasControladorUrlEnum.URL5715
                                                        .getValue());

        listaCovid = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }**/
    
   /** public void cargarListaCovidE() {
   	 Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano);
        
       UrlBean urlBean = UrlServiceUtil.getInstance()
                       .getUrlServiceByUrlByEnumID(
                                       ConfigurarFutCategoriasControladorUrlEnum.URL5715
                                                       .getValue());
      
       System.out.println("prueba 3: paramters: "+param+" bean->>>>>"+urlBean);
       listaCovidE = new RegistroDataModelImpl(urlBean.getUrl(),
                       urlBean.getUrlConteo().getUrl(), param,
                       true, GeneralParameterEnum.CODIGO.getName());
       System.out.println("prueba 2: paramters: "+param);
       System.out.println("prueba 1: urlBean: "+urlBean);
   }
    **/
    
    /**
     * 
     * Carga la lista listacampoFut
     *
     */
    public void cargarListacampoFut() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.ANO.getName(), ano);

        param.put(GeneralParameterEnum.TIPO.getName(), configurar);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarFutCategoriasControladorUrlEnum.URL5715
                                                        .getValue());

        listacampoFut = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listacampoFut
     *
     */
    public void cargarListacampoFutE() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.ANO.getName(), ano);

        param.put(GeneralParameterEnum.TIPO.getName(), configurar);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarFutCategoriasControladorUrlEnum.URL5715
                                                        .getValue());

        listacampoFutE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listacuentaPagar
     *
     */
    public void cargarListacuentaPagar() {
        listacuentaPagar = listacampoFutE;
    }

    /**
     * 
     * Carga la lista listacuentaPagar
     *
     */
    public void cargarListacuentaPagarE() {
        listacuentaPagarE = listacampoFutE;
    }

    /**
     * 
     * Carga la lista listaejecucionSalud
     *
     */
    public void cargarListaejecucionSalud() {
        listaejecucionSalud = listacampoFutE;
    }

    /**
     * 
     * Carga la lista listaejecucionSalud
     *
     */
    public void cargarListaejecucionSaludE() {
        listaejecucionSaludE = listacampoFutE;
    }

    /**
     * 
     * Carga la lista listasaludTesoreria
     *
     */
    public void cargarListasaludTesoreria() {
        listasaludTesoreria = listacampoFutE;
    }

    /**
     * 
     * Carga la lista listasaludTesoreria
     *
     */
    public void cargarListasaludTesoreriaE() {
        listasaludTesoreriaE = listacampoFutE;
    }

    /**
     * 
     * Carga la lista listavigenciaFutura
     *
     */
    public void cargarListavigenciaFutura() {
        listavigenciaFutura = listacampoFutE;
    }

    /**
     * 
     * Carga la lista listavigenciaFutura
     *
     */
    public void cargarListavigenciaFuturaE() {
        listavigenciaFuturaE = listacampoFutE;
    }

    /**
     * 
     * Carga la lista listaingresoReservas
     *
     */
    public void cargarListaingresoReservas() {
        listaingresoReservas = listacampoFutE;
    }

    /**
     * 
     * Carga la lista listaingresoReservas
     *
     */
    public void cargarListaingresoReservasE() {
        listaingresoReservasE = listacampoFutE;
    }

    /**
     * 
     * Carga la lista listavictimas1
     *
     */
    public void cargarListavictimas1() {
        listavictimas1 = listacampoFutE;
    }

    /**
     * 
     * Carga la lista listavictimas1
     *
     */
    public void cargarListavictimas1E() {
        listavictimas1E = listacampoFutE;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton revisarCuentas en la vista
     *
     *
     */
    public void oprimirrevisarCuentas() {
        if (!SysmanFunciones.validarVariableVacio(configurar)) {

            if (estado == 1) {
                estado = 0;
                tituloBtnCuentas = idioma.getString("TB_TB4168");
            }
            else {
                estado = 1;
                tituloBtnCuentas = idioma.getString("TB_TB4169");
            }

            reasignarOrigen();
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4170"));
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton trasladarConfiguracion en
     * la vista
     *
     *
     */
    public void oprimirtrasladarConfiguracion() {
        try {
            ejbChipFutCero.trasladarConfSiguienteAnio(compania,
                            Integer.parseInt(ano), usuario);

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control formularioConfigurar
     * 
     */
    public void cambiarformularioConfigurar() {
        // <CODIGO_DESARROLLADO>

        switch (configurar) {
        case "D1":
            visibleDesplazados1 = true;
            visibleCuentasPagar = visibleEjecucionFondoSalud = visibleTesoreriaSalud = visibleVigFuturaEjecucion = visibleIngresoReserva = visibleVictimas = visibleCovid = false;
            break;
        case "CP":
            visibleCuentasPagar = true;
            visibleDesplazados1 = visibleEjecucionFondoSalud = visibleTesoreriaSalud = visibleVigFuturaEjecucion = visibleIngresoReserva = visibleVictimas =visibleCovid = false;
            break;
        case "FS":
            visibleEjecucionFondoSalud = true;
            visibleDesplazados1 = visibleCuentasPagar = visibleTesoreriaSalud = visibleVigFuturaEjecucion = visibleIngresoReserva = visibleVictimas =visibleCovid = false;
            break;
        case "TS":
            visibleTesoreriaSalud = true;
            visibleDesplazados1 = visibleCuentasPagar = visibleEjecucionFondoSalud = visibleVigFuturaEjecucion = visibleIngresoReserva = visibleVictimas =visibleCovid = false;
            break;
        case "VI":
            visibleVigFuturaEjecucion = true;
            visibleDesplazados1 = visibleCuentasPagar = visibleEjecucionFondoSalud = visibleTesoreriaSalud = visibleIngresoReserva = visibleVictimas =visibleCovid = false;
            break;
        case "IR":
            visibleIngresoReserva = true;
            visibleDesplazados1 = visibleCuentasPagar = visibleEjecucionFondoSalud = visibleTesoreriaSalud = visibleVigFuturaEjecucion = visibleVictimas =visibleCovid = false;
            break;
        case "V1":
            visibleVictimas = true;
            visibleDesplazados1 = visibleCuentasPagar = visibleEjecucionFondoSalud = visibleTesoreriaSalud = visibleVigFuturaEjecucion = visibleIngresoReserva =visibleCovid = false;
            break;
        case "IC":
            visibleCovid = true;
            visibleDesplazados1 = visibleCuentasPagar = visibleEjecucionFondoSalud = visibleTesoreriaSalud = visibleVigFuturaEjecucion = visibleIngresoReserva =visibleVictimas= false;
            break;
        default:
            break;
        
        }
        if (configurar.equals("IC")) {

            parametrosListado.put("COVID",
                            "-1");
        }
        else {
            parametrosListado.put("COVID",
                            "0");
        }
        reasignarOrigen();
        cargarListacampoFutE();
        cargarListacuentaPagarE();
        cargarListaejecucionSaludE();
        cargarListasaludTesoreriaE();
        cargarListavigenciaFuturaE();
        cargarListaingresoReservasE();
        cargarListavictimas1E();
        cargarListaFuenteFut1E();
        //cargarListaCovidE();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control naturaleza
     * 
     */
    public void cambiarnaturaleza() {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Ano
     * 
     */
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        cargarListacampoFutE();
        cargarListacuentaPagarE();
        cargarListaejecucionSaludE();
        cargarListasaludTesoreriaE();
        cargarListavigenciaFuturaE();
        cargarListaingresoReservasE();
        cargarListavictimas1E();
        cargarListaFuenteFut1E();
        //cargarListaCovid();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control destino
     * 
     */
    public void cambiardestino() {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * DgRevisaCuentas en la vista
     *
     */
    public void aceptarDgRevisaCuentas() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * DgRevisaCuentas en la vista
     *
     */
    public void cancelarDgRevisaCuentas() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control campoFut en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarcampoFutC(int rowNum) {

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacampoFut
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacampoFut(SelectEvent event) {

        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("FUT", registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacampoFut
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacampoFutE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenteFut1
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteFut1(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("FUENTE_FLS",
                        registroAux.getCampos().get(
                                        ConfigurarFutCategoriasControladorEnum.CODIGO_FUT
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenteFut1
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteFut1E(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        ConfigurarFutCategoriasControladorEnum.CODIGO_FUT
                                                        .getValue()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listafuente
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilafuente(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("FUENTE",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listafuente
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilafuenteE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listahechoVictimizante
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilahechoVictimizante(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("HECHOVICTIMIZANTE",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listahechoVictimizante
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilahechoVictimizanteE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacuentaPagar
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacuentaPagar(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("FUT_CUENTAXPAGAR",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacuentaPagar
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacuentaPagarE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaejecucionSalud
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaejecucionSalud(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("FUT_EJECUCIONSALUD",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaejecucionSalud
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaejecucionSaludE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listasaludTesoreria
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilasaludTesoreria(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("FUT_SALUD_TESORERIA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listasaludTesoreria
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilasaludTesoreriaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listavigenciaFutura
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilavigenciaFutura(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("FUT_VIGENCIA_FUTURA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listavigenciaFutura
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilavigenciaFuturaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaingresoReservas
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaingresoReservas(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("FUT_INGRESOS_RESERVAS",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaingresoReservas
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaingresoReservasE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listavictimas1
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilavictimas1(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("FUT_VICTIMAS1",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listavictimas1
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilavictimas1E(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
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
     * seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
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

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos()
                        .remove(GeneralParameterEnum.CENTRO_COSTO.getName());
        registro.getCampos().remove(GeneralParameterEnum.TERCERO.getName());
        registro.getCampos().remove(GeneralParameterEnum.SUCURSAL.getName());
        registro.getCampos().remove(GeneralParameterEnum.AUXILIAR.getName());
        registro.getCampos().remove(GeneralParameterEnum.REFERENCIA.getName());
        registro.getCampos()
                        .remove(GeneralParameterEnum.FUENTE_RECURSO.getName());
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
        registro.getCampos().remove(GeneralParameterEnum.DESTINO.getName());
        registro.getCampos().remove("NOMDESTINO");
        registro.getCampos().remove("TIPOVIGENCIA");

    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // METODO NO IMPLEMENTADO
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable configurar
     * 
     * @return configurar
     */
    public String getConfigurar() {
        return configurar;
    }

    /**
     * Asigna la variable configurar
     * 
     * @param configurar
     * Variable a asignar en configurar
     */
    public void setConfigurar(String configurar) {
        this.configurar = configurar;
    }

    /**
     * Retorna la variable naturaleza
     * 
     * @return naturaleza
     */
    public String getNaturaleza() {
        return naturaleza;
    }

    /**
     * Asigna la variable naturaleza
     * 
     * @param naturaleza
     * Variable a asignar en naturaleza
     */
    public void setNaturaleza(String naturaleza) {
        this.naturaleza = naturaleza;
    }

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
     * Retorna la variable destino
     * 
     * @return destino
     */
    public String getDestino() {
        return destino;
    }

    /**
     * Asigna la variable destino
     * 
     * @param destino
     * Variable a asignar en destino
     */
    public void setDestino(String destino) {
        this.destino = destino;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public RegistroDataModelImpl getListafuente() {
        return listafuente;
    }

    public void setListafuente(RegistroDataModelImpl listafuente) {
        this.listafuente = listafuente;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Retorna la lista listacampoFut
     * 
     * @return listacampoFut
     */
    public RegistroDataModelImpl getListacampoFut() {
        return listacampoFut;
    }

    public RegistroDataModelImpl getListahechoVictimizante() {
        return listahechoVictimizante;
    }

    public void setListahechoVictimizante(
        RegistroDataModelImpl listahechoVictimizante) {
        this.listahechoVictimizante = listahechoVictimizante;
    }

    public RegistroDataModelImpl getListaFuenteFut1() {
        return listaFuenteFut1;
    }

    public void setListaFuenteFut1(RegistroDataModelImpl listaFuenteFut1) {
        this.listaFuenteFut1 = listaFuenteFut1;
    }

    /**
     * Asigna la lista listacampoFut
     * 
     * @param listacampoFut
     * Variable a asignar en listacampoFut
     */
    public void setListacampoFut(RegistroDataModelImpl listacampoFut) {
        this.listacampoFut = listacampoFut;
    }

    /**
     * Retorna la lista listacampoFut
     * 
     * @return listacampoFut
     */
    public RegistroDataModelImpl getListacampoFutE() {
        return listacampoFutE;
    }

    /**
     * Asigna la lista listacampoFut
     * 
     * @param listacampoFut
     * Variable a asignar en listacampoFut
     */
    public void setListacampoFutE(RegistroDataModelImpl listacampoFutE) {
        this.listacampoFutE = listacampoFutE;
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

    public boolean isVisibleDialogo() {
        return visibleDialogo;
    }

    public void setVisibleDialogo(boolean visibleDialogo) {
        this.visibleDialogo = visibleDialogo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isBloqueadoCampos() {
        return bloqueadoCampos;
    }

    public void setBloqueadoCampos(boolean bloqueadoCampos) {
        this.bloqueadoCampos = bloqueadoCampos;
    }

    public boolean isVisibleFuenteFls() {
        return visibleFuenteFls;
    }

    public void setVisibleFuenteFls(boolean visibleFuenteFls) {
        this.visibleFuenteFls = visibleFuenteFls;
    }

    public boolean isVisibleHechoVictimizante() {
        return visibleHechoVictimizante;
    }

    public void setVisibleHechoVictimizante(boolean visibleHechoVictimizante) {
        this.visibleHechoVictimizante = visibleHechoVictimizante;
    }

    public boolean isVisibleTipoSalud() {
        return visibleTipoSalud;
    }

    public void setVisibleTipoSalud(boolean visibleTipoSalud) {
        this.visibleTipoSalud = visibleTipoSalud;
    }

    public boolean isVisibleFuente() {
        return visibleFuente;
    }

    public void setVisibleFuente(boolean visibleFuente) {
        this.visibleFuente = visibleFuente;
    }

    public RegistroDataModelImpl getListaFuenteFut1E() {
        return listaFuenteFut1E;
    }

    public void setListaFuenteFut1E(RegistroDataModelImpl listaFuenteFut1E) {
        this.listaFuenteFut1E = listaFuenteFut1E;
    }

    public RegistroDataModelImpl getListafuenteE() {
        return listafuenteE;
    }

    public void setListafuenteE(RegistroDataModelImpl listafuenteE) {
        this.listafuenteE = listafuenteE;
    }

    public RegistroDataModelImpl getListahechoVictimizanteE() {
        return listahechoVictimizanteE;
    }

    public void setListahechoVictimizanteE(
        RegistroDataModelImpl listahechoVictimizanteE) {
        this.listahechoVictimizanteE = listahechoVictimizanteE;
    }

    /**
     * Retorna la lista listacuentaPagar
     * 
     * @return listacuentaPagar
     */
    public RegistroDataModelImpl getListacuentaPagar() {
        return listacuentaPagar;
    }

    /**
     * Asigna la lista listacuentaPagar
     * 
     * @param listacuentaPagar
     * Variable a asignar en listacuentaPagar
     */
    public void setListacuentaPagar(RegistroDataModelImpl listacuentaPagar) {
        this.listacuentaPagar = listacuentaPagar;
    }

    /**
     * Retorna la lista listacuentaPagar
     * 
     * @return listacuentaPagar
     */
    public RegistroDataModelImpl getListacuentaPagarE() {
        return listacuentaPagarE;
    }

    /**
     * Asigna la lista listacuentaPagar
     * 
     * @param listacuentaPagar
     * Variable a asignar en listacuentaPagar
     */
    public void setListacuentaPagarE(RegistroDataModelImpl listacuentaPagarE) {
        this.listacuentaPagarE = listacuentaPagarE;
    }

    /**
     * Retorna la lista listaejecucionSalud
     * 
     * @return listaejecucionSalud
     */
    public RegistroDataModelImpl getListaejecucionSalud() {
        return listaejecucionSalud;
    }

    /**
     * Asigna la lista listaejecucionSalud
     * 
     * @param listaejecucionSalud
     * Variable a asignar en listaejecucionSalud
     */
    public void setListaejecucionSalud(
        RegistroDataModelImpl listaejecucionSalud) {
        this.listaejecucionSalud = listaejecucionSalud;
    }

    /**
     * Retorna la lista listaejecucionSalud
     * 
     * @return listaejecucionSalud
     */
    public RegistroDataModelImpl getListaejecucionSaludE() {
        return listaejecucionSaludE;
    }

    /**
     * Asigna la lista listaejecucionSalud
     * 
     * @param listaejecucionSalud
     * Variable a asignar en listaejecucionSalud
     */
    public void setListaejecucionSaludE(
        RegistroDataModelImpl listaejecucionSaludE) {
        this.listaejecucionSaludE = listaejecucionSaludE;
    }

    /**
     * Retorna la lista listasaludTesoreria
     * 
     * @return listasaludTesoreria
     */
    public RegistroDataModelImpl getListasaludTesoreria() {
        return listasaludTesoreria;
    }

    /**
     * Asigna la lista listasaludTesoreria
     * 
     * @param listasaludTesoreria
     * Variable a asignar en listasaludTesoreria
     */
    public void setListasaludTesoreria(
        RegistroDataModelImpl listasaludTesoreria) {
        this.listasaludTesoreria = listasaludTesoreria;
    }

    /**
     * Retorna la lista listasaludTesoreria
     * 
     * @return listasaludTesoreria
     */
    public RegistroDataModelImpl getListasaludTesoreriaE() {
        return listasaludTesoreriaE;
    }

    /**
     * Asigna la lista listasaludTesoreria
     * 
     * @param listasaludTesoreria
     * Variable a asignar en listasaludTesoreria
     */
    public void setListasaludTesoreriaE(
        RegistroDataModelImpl listasaludTesoreriaE) {
        this.listasaludTesoreriaE = listasaludTesoreriaE;
    }

    /**
     * Retorna la lista listavigenciaFutura
     * 
     * @return listavigenciaFutura
     */
    public RegistroDataModelImpl getListavigenciaFutura() {
        return listavigenciaFutura;
    }

    /**
     * Asigna la lista listavigenciaFutura
     * 
     * @param listavigenciaFutura
     * Variable a asignar en listavigenciaFutura
     */
    public void setListavigenciaFutura(
        RegistroDataModelImpl listavigenciaFutura) {
        this.listavigenciaFutura = listavigenciaFutura;
    }

    /**
     * Retorna la lista listavigenciaFutura
     * 
     * @return listavigenciaFutura
     */
    public RegistroDataModelImpl getListavigenciaFuturaE() {
        return listavigenciaFuturaE;
    }

    /**
     * Asigna la lista listavigenciaFutura
     * 
     * @param listavigenciaFutura
     * Variable a asignar en listavigenciaFutura
     */
    public void setListavigenciaFuturaE(
        RegistroDataModelImpl listavigenciaFuturaE) {
        this.listavigenciaFuturaE = listavigenciaFuturaE;
    }

    /**
     * Retorna la lista listaingresoReservas
     * 
     * @return listaingresoReservas
     */
    public RegistroDataModelImpl getListaingresoReservas() {
        return listaingresoReservas;
    }

    /**
     * Asigna la lista listaingresoReservas
     * 
     * @param listaingresoReservas
     * Variable a asignar en listaingresoReservas
     */
    public void setListaingresoReservas(
        RegistroDataModelImpl listaingresoReservas) {
        this.listaingresoReservas = listaingresoReservas;
    }

    /**
     * Retorna la lista listaingresoReservas
     * 
     * @return listaingresoReservas
     */
    public RegistroDataModelImpl getListaingresoReservasE() {
        return listaingresoReservasE;
    }

    /**
     * Asigna la lista listaingresoReservas
     * 
     * @param listaingresoReservas
     * Variable a asignar en listaingresoReservas
     */
    public void setListaingresoReservasE(
        RegistroDataModelImpl listaingresoReservasE) {
        this.listaingresoReservasE = listaingresoReservasE;
    }

    /**
     * Retorna la lista listavictimas1
     * 
     * @return listavictimas1
     */
    public RegistroDataModelImpl getListavictimas1() {
        return listavictimas1;
    }

    /**
     * Asigna la lista listavictimas1
     * 
     * @param listavictimas1
     * Variable a asignar en listavictimas1
     */
    public void setListavictimas1(RegistroDataModelImpl listavictimas1) {
        this.listavictimas1 = listavictimas1;
    }

    /**
     * Retorna la lista listavictimas1
     * 
     * @return listavictimas1
     */
    public RegistroDataModelImpl getListavictimas1E() {
        return listavictimas1E;
    }

    /**
     * Asigna la lista listavictimas1
     * 
     * @param listavictimas1
     * Variable a asignar en listavictimas1
     */
    public void setListavictimas1E(RegistroDataModelImpl listavictimas1E) {
        this.listavictimas1E = listavictimas1E;
    }

    public boolean isVisibleCovid() {
		return visibleCovid;
	}

	public void setVisibleCovid(boolean visibleCovid) {
		this.visibleCovid = visibleCovid;
	}

	public RegistroDataModelImpl getListaCovid() {
		return listaCovid;
	}

	public void setListaCovid(RegistroDataModelImpl listaCovid) {
		this.listaCovid = listaCovid;
	}

	public boolean isVisibleDesplazados1() {
        return visibleDesplazados1;
    }

    public void setVisibleDesplazados1(boolean visibleDesplazados1) {
        this.visibleDesplazados1 = visibleDesplazados1;
    }

    public boolean isVisibleCuentasPagar() {
        return visibleCuentasPagar;
    }

    public void setVisibleCuentasPagar(boolean visibleCuentasPagar) {
        this.visibleCuentasPagar = visibleCuentasPagar;
    }

    public boolean isVisibleEjecucionFondoSalud() {
        return visibleEjecucionFondoSalud;
    }

    public void setVisibleEjecucionFondoSalud(
        boolean visibleEjecucionFondoSalud) {
        this.visibleEjecucionFondoSalud = visibleEjecucionFondoSalud;
    }

    public boolean isVisibleTesoreriaSalud() {
        return visibleTesoreriaSalud;
    }

    public void setVisibleTesoreriaSalud(boolean visibleTesoreriaSalud) {
        this.visibleTesoreriaSalud = visibleTesoreriaSalud;
    }

    public boolean isVisibleIngresoReserva() {
        return visibleIngresoReserva;
    }

    public void setVisibleIngresoReserva(boolean visibleIngresoReserva) {
        this.visibleIngresoReserva = visibleIngresoReserva;
    }

    public boolean isVisibleVictimas() {
        return visibleVictimas;
    }

    public void setVisibleVictimas(boolean visibleVictimas) {
        this.visibleVictimas = visibleVictimas;
    }

    public boolean isVisibleVigFuturaEjecucion() {
        return visibleVigFuturaEjecucion;
    }

    public void setVisibleVigFuturaEjecucion(
        boolean visibleVigFuturaEjecucion) {
        this.visibleVigFuturaEjecucion = visibleVigFuturaEjecucion;
    }

    public String getTituloBtnCuentas() {
        return tituloBtnCuentas;
    }

    public void setTituloBtnCuentas(String tituloBtnCuentas) {
        this.tituloBtnCuentas = tituloBtnCuentas;
    }
    public RegistroDataModelImpl getListaCovidE() {
		return listaCovidE;
	}

	public void setListaCovidE(RegistroDataModelImpl listaCovidE) {
		this.listaCovidE = listaCovidE;
	}

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
