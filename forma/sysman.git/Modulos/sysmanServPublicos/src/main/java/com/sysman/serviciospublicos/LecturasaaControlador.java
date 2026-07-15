/*-
 * LecturasaaControlador.java
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

import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosOchoRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosTresRemote;
import com.sysman.serviciospublicos.enums.LecturasaaControladorEnum;
import com.sysman.serviciospublicos.enums.LecturasaaControladorUrlEnum;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanFunciones;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
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
import javax.naming.NamingException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite observar los suscriptores, macromedidores y cargar los consumos de los aforos manuales de un determinado ciclo. Se accede desde la ruta Panel Principal\Facturacion de
 * Servicios Publicos\Novedades\Aforo Consumos Manuales
 *
 * @version 1.0, 17/01/2017
 * @author lcortes
 * @version 2.0, 05/06/2017
 * @author spina - se refactoriza para dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class LecturasaaControlador extends BeanBaseDatosAcme
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que identifica el campo CODIGORUTA
     */
    private final String campoCodigoRuta;
    /**
     * Constante que identifica el campo LECTURA
     */
    private static final String CAMPOLECTURA = "LECTURA";
    /**
     * Constante que identifica el campo LECTURAAFORO
     */
    private static final String CAMPOLECTURAAFORO = "LECTURAAFORO";
    /**
     * Constante que identifica el nombre de la tabla SP_USUARIO
     */
    private final String tSpUSuario;

    /**
     * Atributo que permite visualizar en la pestana Macromedidores el valor del consumo del totalizador seleccionado.
     */
    private String consumo;
    /**
     * Atributo que permite visualizar en la pestana Macromedidores el valor de la diferencia del totalizador seleccionado.
     */
    private String diferencia;
    /**
     * Atributo que permite visualizar en la pestana Macromedidores el valor del consumo inicial del totalizador seleccionado.
     */
    private String consInicial;
    /**
     * Atributo que permite visualizar en la pestana Macromedidores el valor del consumo de acueducto del totalizador seleccionado.
     */
    private String consAcueducto;
    /**
     * Atributo que permite visualizar en la pestana Macromedidores el valor del consumo de alcantarillado del totalizador seleccionado.
     */
    private String consAlcantarillado;
    /**
     * Atributo que permite identificar en la pestana Cargar Consumos el nombre de las hojas del documento excel seleccionado en Consumos Manuales.
     */
    private String hojaConsMan;
    /**
     * Atributo que permite identificar en la pestana Cargar Consumos el nombre de las hojas del documento excel seleccionado en Consumos Promedios.
     */
    private String hojaConsProm;
    /**
     * Atributo que permite identificar el anio del ciclo seleccionado.
     */
    private String anio;
    /**
     * Atributo que permite identificar el periodo del ciclo seleccionado.
     */
    private String periodo;
    /**
     * Atributo que permite identificar el ciclo seleccionado.
     */
    private String ciclo;
    /**
     * Atributo para identificar el codigo interno del usuario al que corresponde el codigo de ruta seleccionado (totalizador).
     */
    private String codigoInterno;
    /**
     * Atributo que permite mostrar la suma de las lecturas en el pie de pagina del subformulario listaSublecturamacro en la pestana Macromedidores.
     */
    private String sumLectura;
    /**
     * Atributo que permite mostrar la suma del aforo en el pie de pagina del subformulario listaSublecturamacro en la pestana Macromedidores.
     */
    private String sumAforo;
    /**
     * Atributo que permite mostrar la suma de los consumos de alcantarillado en el pie de pagina del subformulario listaSublecturamacro en la pestana Macromedidores.
     */
    private String sumConsAlc;
    /**
     * Atributo que permite mostrar la suma de los consumos de acueducto en el pie de pagina del subformulario listaSublecturamacro en la pestana Macromedidores.
     */
    private String sumConsAcu;
    /**
     * Atributo que permite mostrar la suma de los consumos aforados en el pie de pagina del subformulario listaSublecturamacro en la pestana Macromedidores.
     */
    private String sumConsAforado;
    /**
     * Atributo que permite identificar la operaci�n seleccionada en la pestana Macromedidores.
     */
    private String operaciones;
    /**
     * Este atributo se usa como auxiliar del componente selector de archivos SelArcConsMan y funciona como contenedor del archivo que se debe guardar
     */
    private ContenedorArchivo contArchivoSelArcConsMan;
    /**
     * Este atributo se usa como auxiliar del componente selector de archivos SelArcConsProm y funciona como contenedor del archivo que se debe guardar
     */
    private ContenedorArchivo contArchivoSelArcConsProm;
    /**
     * Este atributo permite elaborar la sentencia de los campos a actualizar al cargar los consumos.
     */
    private StringBuilder strSql;
    /**
     * Este atributo permite elaborar el contenido del documento con las inconsistencias de los consumos al cargarlos.
     */
    private StringBuilder strLog;
    /**
     * Este atributo permite almacenar el numero de usuarios que tienen inconsistencias.
     */
    private int numUsuariosErr;
    /**
     * Este atributo permite almacenar el numero de usuarios que fueron actualizados correctamente.
     */
    private int numUsuariosOK;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de las hojas del documento seleccionado para consumos manuales en la pestana Cargar Consumos.
     */
    private List<Registro> listacmbHoja;
    /**
     * Lista de las hojas del documento seleccionado para consumos promedios en la pestana Cargar Consumos.
     */
    private List<Registro> listacmbHoja2;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de registros de los codigos de ruta que son totalizadores.
     */
    private RegistroDataModelImpl listaCmbTotaliza;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * Lista que contiene los registros de la tabla SP_USUARIO cuyo totalizador es nulo y que se visualiza en la pestana Suscriptores.
     */
    private RegistroDataModelImpl listaSublecturatotal;
    /**
     * Lista que contiene los registros de la tabla SP_USUARIO cuyo totalizador es equivalente al codigo interno del codigo de ruta seleccionado como totalizador y que se visualiza en la pestana
     * Macromedidores.
     */
    private RegistroDataModelImpl listaSublecturamacro;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario SUBLECTURATOTAL
     */
    private Registro registroSubSUBLECTURATOTAL;
    /**
     * Atributo de referencia para el subformulario SUBLECTURAMACRO
     */
    private Registro registroSubSUBLECTURAMACRO;
    /**
     * Atributo constante para identificar el formato del archivo de excel
     */
    private static final String FORMATOX = ".xlsx";
    /**
     * Atributo constante para referenciar el codigointerno
     */
    private static final String CTECODIGOINTERNO = "CODIGOINTERNO";

    @EJB
    private EjbServiciosPublicosTresRemote ejbServiciosPublicosTres;

    @EJB
    private EjbServiciosPublicosOchoRemote ejbServiciosPublicosOcho;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de LecturasaaControlador
     */
    public LecturasaaControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        campoCodigoRuta = "CODIGORUTA";
        tSpUSuario = "SP_USUARIO";
        try
        {
            numFormulario = GeneralCodigoFormaEnum.LECTURASAA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametros = SessionUtil.getFlash();
            ciclo = SysmanFunciones.nvl(parametros.get("ciclo"), "").toString();
            anio = SysmanFunciones.nvl(parametros.get("anio"), "").toString();
            periodo = SysmanFunciones.nvl(parametros.get("periodo"), "")
                            .toString();
            contArchivoSelArcConsMan = new ContenedorArchivo();
            contArchivoSelArcConsProm = new ContenedorArchivo();
            registroSubSUBLECTURATOTAL = new Registro(
                            new HashMap<String, Object>());
            registroSubSUBLECTURAMACRO = new Registro(
                            new HashMap<String, Object>());
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(LecturasaaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas()
    {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub()
    {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los subformularios
     */
    @Override
    public void iniciarListasSubNulo()
    {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaSublecturatotal = null;
        listaSublecturamacro = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        tabla = "";
        registro = new Registro(
                        new HashMap<String, Object>());
        abrirFormulario();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la consulta correspondiente del formulario
     *
     */
    @Override
    public void asignarOrigenDatos()
    {
        origenDatos = "";
    }

    /**
     * Se realiza la asignacion de la variable origenGrilla por la consulta correspondiente de la grilla del formulario, se hace la asignacion de dicha consulta a los objetos listaInicial y
     * listaInicialF
     *
     */
    @Override
    public void reasignarOrigenGrilla()
    {
        origenGrilla = "";
        if (listaInicial != null)
        {
            listaInicial.setOrigen(origenGrilla);
        }
        if (listaInicialF != null)
        {
            listaInicialF.setOrigen(origenGrilla);
        }
    }

    /**
     *
     * Carga la lista listaSublecturatotal correspondiente a la pestana Suscriptores.
     *
     */
    public void cargarListaSublecturatotal()
    {
        try
        {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            LecturasaaControladorUrlEnum.URL4404
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

            listaSublecturatotal = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            tSpUSuario));
        }
        catch (SysmanException e)
        {
            Logger.getLogger(LecturasaaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     *
     * Carga la lista listaSublecturamacro en la pestana Macromedidores.
     *
     */
    public void cargarListaSublecturamacro()
    {
        try
        {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            LecturasaaControladorUrlEnum.URL4406
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            param.put(CTECODIGOINTERNO, codigoInterno);

            listaSublecturamacro = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            CacheUtil.getLlaveServicio(
                                            urlConexionCache,
                                            tSpUSuario));
        }
        catch (SysmanException e)
        {
            Logger.getLogger(LecturasaaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCmbTotaliza de la pestana Macromedidores.
     *
     */
    public void cargarListaCmbTotaliza()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LecturasaaControladorUrlEnum.URL4403
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listaCmbTotaliza = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        false,
                        campoCodigoRuta);

    }

    /**
     *
     * Metodo que realiza la carga de la lista listacmbHoja pestana Cargar Consumos.
     *
     */
    public void cargarListacmbHoja()
    {
        try
        {
            listacmbHoja = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            LecturasaaControladorUrlEnum.URL4409
                                                                                            .getValue())
                                                            .getUrl(),
                                            null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Metodo que realiza la carga de la lista listacmbHoja2 pestana Cargar Consumos.
     *
     */
    public void cargarListacmbHoja2()
    {
        try
        {
            listacmbHoja2 = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            LecturasaaControladorUrlEnum.URL4409
                                                                                            .getValue())
                                                            .getUrl(),
                                            null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control AFOROCONSMANUAL en la fila seleccionada dentro de la grilla
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarAFOROCONSMANUALC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCmbTotaliza
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbTotaliza(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        if (SysmanFunciones.validarCampoVacio(registroAux.getCampos(),
                        campoCodigoRuta))
        {
            registro.getCampos().put(campoCodigoRuta, "");
            registro.getCampos().put(CAMPOLECTURA, "");
            registro.getCampos().put(CAMPOLECTURAAFORO, "");
            codigoInterno = "";
            registroSubSUBLECTURAMACRO.getCampos().put(CAMPOLECTURA, "");
            registroSubSUBLECTURAMACRO.getCampos().put(CAMPOLECTURAAFORO, "");
            consumo = "";
            consInicial = "";
            consAcueducto = "";
            consAlcantarillado = "";
            diferencia = "";
            listaSublecturamacro = null;
            sumLectura = "0";
            sumAforo = "0";
            sumConsAforado = "0";
            sumConsAcu = "0";
            sumConsAlc = "0";
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3225"));
            return;
        }
        registro.getCampos().put(campoCodigoRuta,
                        registroAux.getCampos().get(campoCodigoRuta));
        registro.getCampos().put(CAMPOLECTURA,
                        registroAux.getCampos().get(CAMPOLECTURA));
        registro.getCampos().put(CAMPOLECTURAAFORO,
                        registroAux.getCampos().get(CAMPOLECTURAAFORO));
        codigoInterno = SysmanFunciones
                        .nvl(registroAux.getCampos().get(CTECODIGOINTERNO), "")
                        .toString();
        registroSubSUBLECTURAMACRO.getCampos().put(CAMPOLECTURA,
                        registroAux.getCampos().get(CAMPOLECTURA));
        registroSubSUBLECTURAMACRO.getCampos().put(CAMPOLECTURAAFORO,
                        registroAux.getCampos().get(CAMPOLECTURAAFORO));
        consumo = String.valueOf(Double
                        .parseDouble(SysmanFunciones
                                        .nvl(registroAux.getCampos()
                                                        .get(CAMPOLECTURAAFORO),
                                                        "0")
                                        .toString())
            - Double.parseDouble(
                            SysmanFunciones.nvl(registroAux.getCampos()
                                            .get(CAMPOLECTURA), "0")
                                            .toString()));
        listaSublecturamacro = null;
        cargarListaSublecturamacro();
        calcularTotales();
        diferencia = String.valueOf(Double.parseDouble(consInicial) // consInicial)
            - Double.parseDouble(consumo));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton CmbOpera en la vista, pestana Macromedidores.
     *
     */
    public void oprimirCmbOpera()
    {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        campoCodigoRuta))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3225"));
            return;
        }
        else if (SysmanFunciones.validarVariableVacio(operaciones))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3226"));
            return;
        }
        try
        {
            ejbServiciosPublicosTres.operarConsumoManual(compania,
                            Integer.parseInt(ciclo), codigoInterno,
                            Integer.parseInt(operaciones),
                            new BigDecimal(consumo),
                            SessionUtil.getUser().getCodigo());
        }
        catch (NumberFormatException | SystemException e)
        {
            Logger.getLogger(LecturasaaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        cargarListaSublecturamacro();
        calcularTotales();
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton CargarConsMan en la vista. El boton se encarga de cargar los consumos manuales en la pestana Cargar Consumos.
     *
     */
    public void oprimirCargarConsMan()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (SysmanFunciones.validarVariableVacio(hojaConsMan))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3227"));
            return;
        }
        if (!validarFormato("1", contArchivoSelArcConsMan, hojaConsMan))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3228")
                            .replace("s$hojaConsumo$s", hojaConsMan));
            return;
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton CargarConsProm en la vista. El boton se encarga de cargar los consumos promedios en la pestana Cargar Consumos.
     *
     */
    public void oprimirCargarConsProm()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (SysmanFunciones.validarVariableVacio(hojaConsProm))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3227"));
            return;
        }
        if (!validarFormato("2", contArchivoSelArcConsProm, hojaConsProm))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3228")
                            .replace("s$hojaConsumo$s", hojaConsProm));
            return;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo invocado al ejecutar el comando remoto rcSelArcConsProm en la vista. Se activa al seleccionar el archivo de consumos promedios en la pestana Cargar Consumos.
     *
     */
    public void ejecutarrcSelArcConsProm()
    {
        // <CODIGO_DESARROLLADO>
        Workbook workbook = null;
        hojaConsProm = "";

        String rutaArchivo = contArchivoSelArcConsProm.getArchivo()
                        .getPath();
        String extension = rutaArchivo.substring(
                        rutaArchivo.indexOf('.'), rutaArchivo.length());
        try (FileInputStream fileIs = new FileInputStream(
                        contArchivoSelArcConsProm.getArchivo());)
        {
            if (FORMATOX.equals(extension))
            {
                workbook = new XSSFWorkbook(fileIs);
            }
            else
            {
                workbook = new HSSFWorkbook(fileIs);
            }
            cargarNombreHojas(workbook, listacmbHoja);
            cargarNombreHojas(workbook, listacmbHoja2);

        }
        catch (IOException e)
        {
            Logger.getLogger(LecturasaaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo invocado al ejecutar el comando remoto rcSelArcConsMan en la vista. Se activa al seleccionar el archivo de consumos manuales en la pestana Cargar Consumos.
     *
     */
    public void ejecutarrcSelArcConsMan()
    {
        // <CODIGO_DESARROLLADO>
        Workbook workbook = null;
        hojaConsMan = "";

        String rutaArchivo = contArchivoSelArcConsMan.getArchivo()
                        .getPath();
        String extension = rutaArchivo.substring(
                        rutaArchivo.indexOf('.'), rutaArchivo.length());
        try (FileInputStream fileIs = new FileInputStream(
                        contArchivoSelArcConsMan.getArchivo());)
        {
            if (FORMATOX.equals(extension))
            {
                workbook = new XSSFWorkbook(fileIs);
            }
            else
            {
                workbook = new HSSFWorkbook(fileIs);
            }
            cargarNombreHojas(workbook, listacmbHoja);

        }
        catch (IOException e)
        {
            Logger.getLogger(LecturasaaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo invocado al ejecutar el comando remoto rcMensaje en la vista. Se activa al oprimir el boton cargar el archivo de consumos manuales en la pestana Cargar Consumos cuando se generan
     * inconsistencias en el proceso de cargue.
     *
     */
    public void ejecutarrcMensaje()
    {
        JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3224").replace(
                        "s$numUsuariosOK$s", Integer.toString(numUsuariosOK))
                        .replace("s$numUsuariosErr$s",
                                        Integer.toString(numUsuariosErr)));
    }

    /**
     * Carga la lista de hojas que contien el archivo Excel seleccionado de los consumos manuales.
     *
     * @param workbook
     * @param listacmbHoja
     */
    private void cargarNombreHojas(Workbook workbook,
        List<Registro> listacmbHoja)
    {
        listacmbHoja.clear();
        int hojas = workbook.getNumberOfSheets();
        for (int i = 0; i < hojas; i++)
        {
            Sheet sheet = workbook.getSheetAt(i);
            String hoja = sheet.getSheetName();
            Registro reg = new Registro();
            reg.getCampos().put("NOMBREHOJA", hoja);
            listacmbHoja.add(reg);
        }
    }

    /**
     * Meotodo que permite validar el formato del archivo seleccionado en la pestana Cargar Consumos.
     *
     * @param contArchivoSelArc
     *
     * @param tipo:
     * 1 Si es el archivo correspondiente a consumos manuales. 2 Si es el archivo correspondiente a consumos promedios.
     * @return true si el formato del archivo es correcto.
     */
    private boolean validarFormato(String tipo,
        ContenedorArchivo contArchivoSelArc, String hoja)
    {
        String rutaArchivo = contArchivoSelArc.getArchivo()
                        .getPath();
        String extension = rutaArchivo.substring(
                        rutaArchivo.indexOf('.'), rutaArchivo.length());
        try (FileInputStream fileIs = new FileInputStream(
                        contArchivoSelArc.getArchivo());)
        {

            if (FORMATOX.equals(extension))
            {
                return leerArchivoXLSXXSSF(fileIs, hoja, tipo);
            }
            else
            {
                return leerArchivoXLSHSS(fileIs, hoja, tipo);
            }

        }
        catch (IOException e)
        {
            Logger.getLogger(LecturasaaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return true;
    }

    private boolean leerArchivoXLSHSS(FileInputStream fileIs, String hoja,
        String tipo) throws IOException
    {
        boolean rta = true;
        HSSFWorkbook workbook = new HSSFWorkbook(fileIs);
        Sheet sheet = workbook.getSheet(hoja);
        HSSFRow hssfRow = (HSSFRow) sheet.getRow(0);

        String codInterno = hssfRow.getCell(0).getStringCellValue();
        String codRuta = hssfRow.getCell(1).getStringCellValue();
        String lectura = hssfRow.getCell(3).getStringCellValue();
        if (("1").equals(tipo))
        {
            HSSFCell f4 = hssfRow.getCell(4);
            HSSFCell f5 = hssfRow.getCell(5);
            HSSFCell f6 = hssfRow.getCell(6);
            if (f4 == null || f5 == null || f6 == null)
            {
                rta = false;
            }
            else
            {
                String consumoAcu = hssfRow.getCell(4).getStringCellValue();
                String consumoAlc = hssfRow.getCell(5).getStringCellValue();
                String problema = hssfRow.getCell(6).getStringCellValue();
                if (!verificarTitConsMan(codInterno, codRuta, lectura,
                                consumoAcu, consumoAlc, problema))
                {
                    rta = false;
                }
                else
                {
                    cargarConsManualesHSS();
                }
            }

        }
        if (("2").equals(tipo))
        {
            HSSFCell f4 = hssfRow.getCell(4);
            if (f4 == null)
            {
                rta = false;
            }
            else
            {
                String problema = hssfRow.getCell(4).getStringCellValue();
                if (!verificarTitConsProm(codInterno, codRuta, lectura,
                                problema))
                {
                    rta = false;
                }
                else
                {
                    cargarConsPromediosHSS();
                }

            }
        }
        workbook.close();
        return rta;
    }

    private boolean leerArchivoXLSXXSSF(FileInputStream fileIs, String hoja,
        String tipo) throws IOException
    {
        boolean rta = true;
        XSSFWorkbook workbook = new XSSFWorkbook(fileIs);
        Sheet sheet = workbook.getSheet(hoja);
        XSSFRow xssfRow = (XSSFRow) sheet.getRow(0);
        String codInterno = xssfRow.getCell(0).getStringCellValue();
        String codRuta = xssfRow.getCell(1).getStringCellValue();
        String lectura = xssfRow.getCell(3).getStringCellValue();
        if (("1").equals(tipo))
        {
            XSSFCell f4 = xssfRow.getCell(4);
            XSSFCell f5 = xssfRow.getCell(5);
            XSSFCell f6 = xssfRow.getCell(6);
            if (f4 == null || f5 == null || f6 == null)
            {
                rta = false;
            }
            else
            {
                String consumoAcu = xssfRow.getCell(4).getStringCellValue();
                String consumoAlc = xssfRow.getCell(5).getStringCellValue();
                String problema = xssfRow.getCell(6).getStringCellValue();
                if (!verificarTitConsMan(codInterno, codRuta, lectura,
                                consumoAcu, consumoAlc, problema))
                {
                    rta = false;
                }
                else
                {
                    cargarConsManualesXSS();
                }
            }

        }
        if (("2").equals(tipo))
        {
            XSSFCell f4 = xssfRow.getCell(4);
            if (f4 == null)
            {
                rta = false;
            }
            else
            {
                String problema = xssfRow.getCell(4).getStringCellValue();
                if (!verificarTitConsProm(codInterno, codRuta, lectura,
                                problema))
                {
                    rta = false;
                }
                else
                {
                    cargarConsPromediosXSS();
                }

            }
        }
        workbook.close();
        return rta;
    }

    /**
     * Metodo que permite verificar los titulos de las columnas del archivo seleccionado para los consumos manuales.
     *
     * @param codInterno:
     * cadena con el valor de la primera columna de la primera fila del archivo.
     * @param codRuta:
     * cadena con el valor de la segunda columna de la primera fila del archivo.
     * @param lectura:
     * cadena con el valor de la cuarta columna de la primera fila del archivo.
     * @param consumoAcu:
     * cadena con el valor de la quinta columna de la primera fila del archivo.
     * @param consumoAlc:
     * cadena con el valor de la sexta columna de la primera fila del archivo.
     * @param problema:
     * cadena con el valor de la septima columna de la primera fila del archivo.
     *
     * @return true si el valor de las columnas de la fila coinciden con las del formato valido.
     */
    private boolean verificarTitConsMan(String codInterno, String codRuta,
        String lectura, String consumoAcu, String consumoAlc, String problema)
    {
        boolean respuesta = false;
        if (("CODIGO_INTERNO").equals(codInterno)
            && ("CODIGO_RUTA").equals(codRuta)
            && ("LECTURA_AFORADA").equals(lectura))
        {
            respuesta = true;
        }
        if (("CONSUMO_ACUEDUCTO").equals(consumoAcu)
            && ("CONSUMO_ALCANTARILLADO").equals(consumoAlc)
            && ("CODIGO_PROBLEMA").equals(problema))
        {
            respuesta = true;
        }
        return respuesta;
    }

    /**
     * * Metodo que permite verificar los titulos de las columnas del archivo seleccionado para los consumos promedios.
     *
     * @param codInterno:
     * cadena con el valor de la primera columna de la primera fila del archivo.
     * @param codRuta:
     * cadena con el valor de la segunda columna de la primera fila del archivo.
     * @param problema:
     * cadena con el valor de la cuarta columna de la primera fila del archivo.
     *
     * @return true si el valor de las columnas de la fila coinciden con las del formato valido.
     */
    private boolean verificarTitConsProm(String codInterno, String codRuta,
        String lectura, String problema)
    {
        boolean respuesta = false;
        if (("CODIGO_INTERNO").equals(codInterno)
            && ("CODIGO_RUTA").equals(codRuta)
            && ("LECTURA_AFORADA").equals(lectura)
            && ("CODIGO_PROBLEMA").equals(problema))
        {
            respuesta = true;
        }
        return respuesta;
    }

    /**
     * Metodo que se encarga de cargar la informacion del archivo seleccionado
     */
    private void cargarConsManualesXSS()
    {
        archivoDescarga = null;
        strLog = new StringBuilder();
        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(
                        contArchivoSelArcConsMan.getArchivo()));)
        {
            Sheet sheet = workbook.getSheet(hojaConsMan);
            numUsuariosErr = 0;
            numUsuariosOK = 0;
            for (int fila = 1; fila <= sheet.getLastRowNum(); fila++)
            {
                strSql = new StringBuilder();
                XSSFRow xssfRow = (XSSFRow) sheet.getRow(fila);
                XSSFCell cell = xssfRow.getCell(0);
                if (cell == null)
                {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB3229"));
                    return;
                }
                cell.setCellType(Cell.CELL_TYPE_STRING);
                String codInternoAct = cell.getStringCellValue();
                cell = xssfRow.getCell(1);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                String codRutaAct = cell.getStringCellValue();
                cell = xssfRow.getCell(3);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                String lecturaAforoActual = cell.getStringCellValue();
                cell = xssfRow.getCell(4);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                String consumoAcu = cell.getStringCellValue();
                cell = xssfRow.getCell(5);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                String consumoAlc = cell.getStringCellValue();
                cell = xssfRow.getCell(6);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                String problema = cell.getStringCellValue();

                String excel = codInternoAct + "," + codRutaAct + ","
                    + lecturaAforoActual + "," + consumoAcu + "," + consumoAlc
                    + ","
                    + problema + ",";

                cargarConsumos(excel, "0");

            }
            generarArchivo(idioma.getString("TB_TB3230"));
        }
        catch (Exception e)
        {
            Logger.getLogger(LecturasaaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que se encarga de cargar la informacion del archivo seleccionado
     */
    private void cargarConsManualesHSS()
    {
        archivoDescarga = null;
        strLog = new StringBuilder();
        try (Workbook workbook = new HSSFWorkbook(new FileInputStream(
                        contArchivoSelArcConsMan.getArchivo()));)
        {
            Sheet sheet = workbook.getSheet(hojaConsMan);
            numUsuariosErr = 0;
            numUsuariosOK = 0;
            for (int fila = 1; fila <= sheet.getLastRowNum(); fila++)
            {
                strSql = new StringBuilder();
                HSSFRow hssfRow = (HSSFRow) sheet.getRow(fila);
                HSSFCell cell = hssfRow.getCell(0);
                if (cell == null)
                {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB3229"));
                    return;
                }
                cell.setCellType(Cell.CELL_TYPE_STRING);
                String codInternoAct = cell.getStringCellValue();
                cell = hssfRow.getCell(1);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                String codRutaAct = cell.getStringCellValue();
                cell = hssfRow.getCell(3);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                String lecturaAforoActual = cell.getStringCellValue();
                cell = hssfRow.getCell(4);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                String consumoAcu = cell.getStringCellValue();
                cell = hssfRow.getCell(5);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                String consumoAlc = cell.getStringCellValue();
                cell = hssfRow.getCell(6);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                String problema = cell.getStringCellValue();

                String excel = codInternoAct + "," + codRutaAct + ","
                    + lecturaAforoActual + "," + consumoAcu + "," + consumoAlc
                    + ","
                    + problema + ",";

                cargarConsumos(excel, "0");

            }
            generarArchivo(idioma.getString("TB_TB3230"));
        }
        catch (Exception e)
        {
            Logger.getLogger(LecturasaaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void cargarConsumos(String excel, String tipoConsumo)
                    throws IOException, SQLException, IllegalAccessException,
                    InstantiationException,
                    ClassNotFoundException, NamingException, SystemException
    {

        String resultados = ejbServiciosPublicosOcho.cargarConsumosManYProm(
                        compania, Integer.parseInt(ciclo), excel,
                        Integer.parseInt(anio),
                        periodo,
                        SessionUtil.getUser().getCodigo(), strSql.toString(),
                        strLog.toString(), numUsuariosErr,
                        numUsuariosOK, tipoConsumo == "0" ? false : true);
        String[] cargados = resultados.split("#");
        if ("*".equals(cargados[0]))
        {
            numUsuariosOK = Integer.parseInt(cargados[1]);
        }
        else
        {
            strLog = new StringBuilder(cargados[0]);
            numUsuariosOK = Integer.parseInt(cargados[1]);
            numUsuariosErr = Integer.parseInt(cargados[2]);
        }

    }

    private void cargarConsPromediosXSS()
    {
        archivoDescarga = null;
        strLog = new StringBuilder();
        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(
                        contArchivoSelArcConsProm.getArchivo()));)
        {
            Sheet sheet = workbook.getSheet(hojaConsProm);
            numUsuariosErr = 0;
            numUsuariosOK = 0;
            for (int fila = 1; fila <= sheet.getLastRowNum(); fila++)
            {
                strSql = new StringBuilder();
                XSSFRow xssfRow = (XSSFRow) sheet.getRow(fila);
                XSSFCell cell = xssfRow.getCell(0);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                String codInternoAct = cell.getStringCellValue();
                cell = xssfRow.getCell(1);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                String codRutaAct = cell.getStringCellValue();
                cell = xssfRow.getCell(3);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                String lecturaAforoActual = cell.getStringCellValue();
                cell = xssfRow.getCell(4);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                String problema = cell.getStringCellValue();

                String excel = codInternoAct + "," + codRutaAct + ","
                    + lecturaAforoActual + "," + problema + ",";

                cargarConsumos(excel, "1");

            }
            generarArchivo(idioma.getString("TB_TB3231"));
        }
        catch (IOException | IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException | JRException | SystemException e)
        {
            Logger.getLogger(LecturasaaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void cargarConsPromediosHSS()
    {
        archivoDescarga = null;
        strLog = new StringBuilder();
        try (Workbook workbook = new HSSFWorkbook(new FileInputStream(
                        contArchivoSelArcConsProm.getArchivo()));)
        {
            Sheet sheet = workbook.getSheet(hojaConsProm);
            numUsuariosErr = 0;
            numUsuariosOK = 0;
            for (int fila = 1; fila <= sheet.getLastRowNum(); fila++)
            {
                strSql = new StringBuilder();
                HSSFRow xssfRow = (HSSFRow) sheet.getRow(fila);
                HSSFCell cell = xssfRow.getCell(0);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                String codInternoAct = cell.getStringCellValue();
                cell = xssfRow.getCell(1);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                String codRutaAct = cell.getStringCellValue();
                cell = xssfRow.getCell(3);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                String lecturaAforoActual = cell.getStringCellValue();
                cell = xssfRow.getCell(4);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                String problema = cell.getStringCellValue();

                String excel = codInternoAct + "," + codRutaAct + ","
                    + lecturaAforoActual + "," + problema + ",";

                cargarConsumos(excel, "1");

            }
            generarArchivo(idioma.getString("TB_TB3231"));
        }
        catch (IOException | IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException | JRException | SystemException e)
        {
            Logger.getLogger(LecturasaaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void generarArchivo(String nombreArchivo)
                    throws JRException, IOException
    {
        if (numUsuariosErr == 0)
        {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB3223").replace(
                                            "s$numusuariosok$s",
                                            Integer.toString(numUsuariosOK)));
        }
        else
        {
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            JsfUtil.serializarPlano(
                                            strLog.toString()),
                            nombreArchivo);
            ejecutarrcMensaje();

        }
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario Sublecturatotal
     *
     */
    public void agregarRegistroSubSublecturatotal()
    {
        // Metodo no utilizado por que se bloquea la funcion de guardado, se deja para evitar error en la vista
    }

    /**
     * Metodo de edicion del formulario Sublecturatotal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSublecturatotal(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();

        reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        reg.getCampos().remove(GeneralParameterEnum.CICLO.getName());
        reg.getCampos().remove(GeneralParameterEnum.CODIGORUTA.getName());
        reg.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        reg.getCampos().remove(CAMPOLECTURA);
        reg.getCampos().remove(CAMPOLECTURAAFORO);
        reg.getCampos().remove(LecturasaaControladorEnum.CONSUMO.getValue());
        reg.getCampos().remove("TOTALIZADOR");

        reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                        new Date());
        try
        {

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            LecturasaaControladorUrlEnum.URL4405
                                                            .getValue());

            int conteo = requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            if (conteo > 0)
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_MODIFICADO"));
            }
        }
        catch (SystemException ex)
        {
            Logger.getLogger(LecturasaaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            cargarListaSublecturatotal();
        }
    }

    /**
     * Metodo de eliminacion del formulario Sublecturatotal
     *
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubSublecturatotal(Registro reg)
    {
        // Metodo no utilizado por que se bloquea la funcion de eliminar, se deja para evitar error en la vista
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para el subformulario Sublecturatotal
     *
     */
    public void cancelarEdicionSublecturatotal()
    {
        cargarListaSublecturatotal();
        cargarListaSublecturamacro();
    }

    /**
     * Metodo de insercion del formulario Sublecturamacro
     *
     */
    public void agregarRegistroSubSublecturamacro()
    {
        // Metodo no utilizado por que se bloquea la funcion de guardado, se deja para evitar error en la vista
    }

    /**
     * Metodo de edicion del formulario Sublecturamacro
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSublecturamacro(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();

        reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        reg.getCampos().remove(GeneralParameterEnum.CICLO.getName());
        reg.getCampos().remove(GeneralParameterEnum.CODIGORUTA.getName());
        reg.getCampos().remove(GeneralParameterEnum.NOMBRES.getName());
        reg.getCampos().remove(
                        LecturasaaControladorEnum.CODTOTALIZADOR.getValue());
        reg.getCampos().remove(CAMPOLECTURA);
        reg.getCampos().remove(CAMPOLECTURAAFORO);
        reg.getCampos().remove(
                        LecturasaaControladorEnum.CONSUMOPROM.getValue());
        reg.getCampos().remove(LecturasaaControladorEnum.CONSUMO1.getValue());
        reg.getCampos().remove(
                        LecturasaaControladorEnum.CONSUMOPROM.getValue());
        reg.getCampos().remove(
                        LecturasaaControladorEnum.TIPOCALCULO.getValue());

        reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                        new Date());
        try
        {

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            LecturasaaControladorUrlEnum.URL4408
                                                            .getValue());

            int conteo = requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            if (conteo > 0)
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_MODIFICADO"));
            }
        }
        catch (SystemException ex)
        {
            Logger.getLogger(LecturasaaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            cargarListaSublecturamacro();
            calcularTotales();

        }
    }

    /**
     * Metodo de eliminacion del formulario Sublecturamacro
     *
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubSublecturamacro(Registro reg)
    {
        // Metodo no utilizado por que se bloquea la funcion de eliminar, se deja para evitar error en la vista
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para el subformulario Sublecturamacro
     *
     */
    public void cancelarEdicionSublecturamacro()
    {
        cargarListaSublecturamacro();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        cargarListaSublecturatotal();
        cargarListaCmbTotaliza();
        cargarListacmbHoja();
        cargarListacmbHoja2();
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     *
     */
    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo para calcular los valores totales de las lecturas, aforo, consumo aforado, consumo acueducto y consumo alcatarillado de los registros de la grilla del subformulario Sublecturamacro.
     */
    private void calcularTotales()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(CTECODIGOINTERNO, codigoInterno);

        try
        {
            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LecturasaaControladorUrlEnum.URL4407
                                                                            .getValue())
                                            .getUrl(), param));

            sumLectura = SysmanFunciones
                            .nvl(reg.getCampos().get("SUMLECTURA"), "0")
                            .toString().trim();
            sumAforo = SysmanFunciones.nvl(reg.getCampos().get("SUMAFORO"), "0")
                            .toString().trim();
            sumConsAforado = SysmanFunciones
                            .nvl(reg.getCampos().get("SUMCONSUMO1"), "0")
                            .toString().trim();
            sumConsAcu = SysmanFunciones
                            .nvl(reg.getCampos().get("SUMCONSUMOACU"), "0")
                            .toString().trim();
            sumConsAlc = SysmanFunciones
                            .nvl(reg.getCampos().get("SUMCONSUMOALC"), "0")
                            .toString().trim();
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        consInicial = SysmanFunciones.nvl("10".equals(sumConsAforado) ? "0"
            : sumConsAforado, "0").toString();
        consAcueducto = SysmanFunciones.nvl("10".equals(sumConsAcu) ? "0"
            : sumConsAcu, "0").toString();
        consAlcantarillado = SysmanFunciones.nvl("10".equals(sumConsAlc) ? "0"
            : sumConsAlc, "0").toString();

    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     * @return true
     */
    @Override
    public boolean insertarAntes()
    {
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
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     *
     * @return true
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     *
     * @return true
     */
    @Override
    public boolean actualizarDespues()
    {
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
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del registro
     *
     * @return true
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable consumo
     *
     * @return consumo
     */
    public String getConsumo()
    {
        return consumo;
    }

    /**
     * Asigna la variable consumo
     *
     * @param consumo
     * Variable a asignar en consumo
     */
    public void setConsumo(String consumo)
    {
        this.consumo = consumo;
    }

    /**
     * Retorna la variable diferencia
     *
     * @return diferencia
     */
    public String getDiferencia()
    {
        return diferencia;
    }

    /**
     * Asigna la variable diferencia
     *
     * @param diferencia
     * Variable a asignar en diferencia
     */
    public void setDiferencia(String diferencia)
    {
        this.diferencia = diferencia;
    }

    /**
     * Retorna la variable consInicial
     *
     * @return consInicial
     */
    public String getConsInicial()
    {
        return consInicial;
    }

    /**
     * Asigna la variable consInicial
     *
     * @param consInicial
     * Variable a asignar en consInicial
     */
    public void setConsInicial(String consInicial)
    {
        this.consInicial = consInicial;
    }

    /**
     * Retorna la variable consAcueducto
     *
     * @return consAcueducto
     */
    public String getConsAcueducto()
    {
        return consAcueducto;
    }

    /**
     * Asigna la variable consAcueducto
     *
     * @param consAcueducto
     * Variable a asignar en consAcueducto
     */
    public void setConsAcueducto(String consAcueducto)
    {
        this.consAcueducto = consAcueducto;
    }

    /**
     * Retorna la variable consAlcantarillado
     *
     * @return consAlcantarillado
     */
    public String getConsAlcantarillado()
    {
        return consAlcantarillado;
    }

    /**
     * Asigna la variable consAlcantarillado
     *
     * @param consAlcantarillado
     * Variable a asignar en consAlcantarillado
     */
    public void setConsAlcantarillado(String consAlcantarillado)
    {
        this.consAlcantarillado = consAlcantarillado;
    }

    /**
     * Retorna la variable hojaConsMan
     *
     * @return hojaConsMan
     */
    public String getHojaConsMan()
    {
        return hojaConsMan;
    }

    /**
     * Asigna la variable hojaConsMan
     *
     * @param hojaConsMan
     * Variable a asignar en hojaConsMan
     */
    public void setHojaConsMan(String hojaConsMan)
    {
        this.hojaConsMan = hojaConsMan;
    }

    /**
     * Retorna la variable hojaConsProm
     *
     * @return hojaConsProm
     */
    public String getHojaConsProm()
    {
        return hojaConsProm;
    }

    /**
     * Asigna la variable hojaConsProm
     *
     * @param hojaConsProm
     * Variable a asignar en hojaConsProm
     */
    public void setHojaConsProm(String hojaConsProm)
    {
        this.hojaConsProm = hojaConsProm;
    }

    /**
     * Retorna el objeto contArchivoSelArcConsMan
     *
     * @return contArchivoSelArcConsMan
     */
    public ContenedorArchivo getContArchivoSelArcConsMan()
    {
        return contArchivoSelArcConsMan;
    }

    /**
     * Asigna el objeto contArchivoSelArcConsMan
     *
     * @param contArchivoSelArcConsMan
     * Variable a asignar en contArchivoSelArcConsMan
     */
    public void setContArchivoSelArcConsMan(
        ContenedorArchivo contArchivoSelArcConsMan)
    {
        this.contArchivoSelArcConsMan = contArchivoSelArcConsMan;
    }

    /**
     * Retorna el objeto contArchivoSelArcConsProm
     *
     * @return contArchivoSelArcConsProm
     */
    public ContenedorArchivo getContArchivoSelArcConsProm()
    {
        return contArchivoSelArcConsProm;
    }

    /**
     * Asigna el objeto contArchivoSelArcConsProm
     *
     * @param contArchivoSelArcConsProm
     * Variable a asignar en contArchivoSelArcConsProm
     */
    public void setContArchivoSelArcConsProm(
        ContenedorArchivo contArchivoSelArcConsProm)
    {
        this.contArchivoSelArcConsProm = contArchivoSelArcConsProm;
    }

    public String getSumLectura()
    {
        return sumLectura;
    }

    public void setSumLectura(String sumLectura)
    {
        this.sumLectura = sumLectura;
    }

    public String getSumAforo()
    {
        return sumAforo;
    }

    public void setSumAforo(String sumAforo)
    {
        this.sumAforo = sumAforo;
    }

    public String getSumConsAlc()
    {
        return sumConsAlc;
    }

    public void setSumConsAlc(String sumConsAlc)
    {
        this.sumConsAlc = sumConsAlc;
    }

    public String getSumConsAcu()
    {
        return sumConsAcu;
    }

    public void setSumConsAcu(String sumConsAcu)
    {
        this.sumConsAcu = sumConsAcu;
    }

    public String getSumConsAforado()
    {
        return sumConsAforado;
    }

    public void setSumConsAforado(String sumConsAforado)
    {
        this.sumConsAforado = sumConsAforado;
    }

    public String getOperaciones()
    {
        return operaciones;
    }

    public void setOperaciones(String operaciones)
    {
        this.operaciones = operaciones;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listacmbHoja
     *
     * @return listacmbHoja
     */
    public List<Registro> getListacmbHoja()
    {
        return listacmbHoja;
    }

    /**
     * Asigna la lista listacmbHoja
     *
     * @param listacmbHoja
     * Variable a asignar en listacmbHoja
     */
    public void setListacmbHoja(List<Registro> listacmbHoja)
    {
        this.listacmbHoja = listacmbHoja;
    }

    /**
     * Retorna la lista listacmbHoja2
     *
     * @return listacmbHoja2
     */
    public List<Registro> getListacmbHoja2()
    {
        return listacmbHoja2;
    }

    /**
     * Asigna la lista listacmbHoja2
     *
     * @param listacmbHoja2
     * Variable a asignar en listacmbHoja2
     */
    public void setListacmbHoja2(List<Registro> listacmbHoja2)
    {
        this.listacmbHoja2 = listacmbHoja2;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCmbTotaliza
     *
     * @return listaCmbTotaliza
     */
    public RegistroDataModelImpl getListaCmbTotaliza()
    {
        return listaCmbTotaliza;
    }

    /**
     * Asigna la lista listaCmbTotaliza
     *
     * @param listaCmbTotaliza
     * Variable a asignar en listaCmbTotaliza
     */
    public void setListaCmbTotaliza(RegistroDataModelImpl listaCmbTotaliza)
    {
        this.listaCmbTotaliza = listaCmbTotaliza;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaSublecturatotal
     *
     * @return listaSublecturatotal
     */
    public RegistroDataModelImpl getListaSublecturatotal()
    {
        return listaSublecturatotal;
    }

    /**
     * Asigna la lista listaSublecturatotal
     *
     * @param listaSublecturatotal
     * Variable a asignar en listaSublecturatotal
     */
    public void setListaSublecturatotal(
        RegistroDataModelImpl listaSublecturatotal)
    {
        this.listaSublecturatotal = listaSublecturatotal;
    }

    /**
     * Retorna la lista listaSublecturamacro
     *
     * @return listaSublecturamacro
     */
    public RegistroDataModelImpl getListaSublecturamacro()
    {
        return listaSublecturamacro;
    }

    /**
     * Asigna la lista listaSublecturamacro
     *
     * @param listaSublecturamacro
     * Variable a asignar en listaSublecturamacro
     */
    public void setListaSublecturamacro(
        RegistroDataModelImpl listaSublecturamacro)
    {
        this.listaSublecturamacro = listaSublecturamacro;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSubSUBLECTURATOTAL
     *
     * @return registroSubSUBLECTURATOTAL
     */
    public Registro getRegistroSubSUBLECTURATOTAL()
    {
        return registroSubSUBLECTURATOTAL;
    }

    /**
     * Asigna el objeto registroSubSUBLECTURATOTAL
     *
     * @param registroSubSUBLECTURATOTAL
     * Variable a asignar en registroSubSUBLECTURATOTAL
     */
    public void setRegistroSubSUBLECTURATOTAL(
        Registro registroSubSUBLECTURATOTAL)
    {
        this.registroSubSUBLECTURATOTAL = registroSubSUBLECTURATOTAL;
    }

    /**
     * Retorna el objeto registroSubSUBLECTURAMACRO
     *
     * @return registroSubSUBLECTURAMACRO
     */
    public Registro getRegistroSubSUBLECTURAMACRO()
    {
        return registroSubSUBLECTURAMACRO;
    }

    /**
     * Asigna el objeto registroSubSUBLECTURAMACRO
     *
     * @param registroSubSUBLECTURAMACRO
     * Variable a asignar en registroSubSUBLECTURAMACRO
     */
    public void setRegistroSubSUBLECTURAMACRO(
        Registro registroSubSUBLECTURAMACRO)
    {
        this.registroSubSUBLECTURAMACRO = registroSubSUBLECTURAMACRO;
    }
    // </SET_GET_ADICIONALES>

}
