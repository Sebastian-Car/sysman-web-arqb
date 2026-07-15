/*-
 * FrmInformesSireciControlador.java
 *
 * 1.0
 * 
 * 13/03/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCincoRemote;
import com.sysman.bancoproyectos.enums.FrmInformesSireciControladorUrlEnum;
import com.sysman.bancoproyectos.reportes.FrmInformesSireciReporteador;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Migracion de los formularios en access
 * FRMINF_F238,FRMINF_EFICACIA,FRMINF_F19_2,FRMINF_F20_2 a web con el
 * controlador FrmInformesSireciControlador, forma
 * frminformessireci.xhtml creacion de diferentes opciones de menu
 * para abrir el formulario modal, creacion de properties , asi como
 * generacion del archivo descargable en excel con el reporte a partir
 * de un boton.
 * 
 * @version 1.0, 13/03/2018
 * @author crodriguez
 * 
 * @version 1.0, 20/03/2018
 * @author mvenegas
 * 
 * Se agrego la funcionalidad para que acceda desde la opcion de menu
 * 5203060102
 * 
 * @version 2, 03/04/2018, <strong>pespitia</strong>: Se ajusta el
 * controlador para abrir el formulario desde la opcion de menu
 * 5203060103.
 */
@ManagedBean
@ViewScoped
public class FrmInformesSireciControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * 
     */
    private String vigenciaInicial;
    /**
     * 
     */
    private String vigenciaFinal;
    private final String fXlsxCons;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */

    private String titulo;

    private String tituloVentana;

    /**
     * mvenegas Esta variable se utiliza para que si se ingresa por la
     * opcion 5203060102 la etiqueta "Vigencia Inicial:" debe tomar
     * solo le valor de "Vigencia:"
     */
    private String etiquetaVigenciaInicial;
    /**
     * mvenegas Esta variable se utiliza para mostrar u ocultar le
     * etiqueta "Vigencia Final:" y el combo "Vigencia Final"
     */
    private boolean elementosVisibles;

    /**
     * mvenegas Esta variable me controla si es visible o no la el
     * combo de "Vigencia"
     */
    private boolean vigenciaVisibles;

    /**
     * Indicador que controla la visibilidad del campo Vigencia
     * (CP5793).
     */
    private boolean indVigIni;

    /**
     * Indicador que controla la visibilidad de los campos fecha
     * inicial (CP54304) y final (CP54305).
     */
    private boolean verFechas;

    /**
     * Atributo que indica el boton de Excel que se debe visualizar
     * respecto a la opcion de menu desde la cual se abre el
     * formulario.
     */
    private int verBtExcel;

    /**
     * mvenegas Esta variable me indica la opcion de menu desde la que
     * se ingreso
     */
    private String opcionMenu;

    /**
     * Esta variable me captura la vigencia seleccionada
     */
    private String vigencia;

    /**
     * Atributo que contiene el valor ingresado en el campo Fecha
     * Inicial (CP54304).
     */
    private Date fechaInicial;

    /**
     * Atributo que contiene el valor ingresado en el campo Fecha
     * Final (CP54305).
     */
    private Date fechaFinal;

    /**
     * mvenegas Esta constante me almacena el valor de la opcion de
     * menu 5203060102
     */
    static final String OPCIONDOS = "5203060102";

    private StreamedContent archivoDescarga;

    /**
     * Este atributo se usa como auxiliar del componente selector de
     * archivos selPlantilla y funciona como contenedor del archivo
     * que se debe guardar
     */
    private ContenedorArchivo contArchivoselPlantilla;

    /**
     * Variable que permite acceder a las funciones y procedimientos
     * del paquete: <code>PCK_BANCOS_PROY5</code>.
     */
    @EJB
    private EjbBancoProyectoCincoRemote ejbBancoProyectoCinco;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    /**
     * Lista que contiene los detalles del combo Vigencia Inicial
     * (CB5787).
     */
    private List<Registro> listaVigenciaInicial;

    /**
     * Lista que contiene los detalles del combo Vigencia Final
     * (CB5788).
     */
    private List<Registro> listaVigenciaFinal;

    /**
     * Esta variable me almacena los datostraidos del servicio para
     * "listaVigencia"
     */
    private List<Registro> listaVigencia;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Instancia de la clase FrmInformesSireciReporteador que se
     * encarga de personalizar la hoja de datos del excel a generar.
     */
    private FrmInformesSireciReporteador frmInformesSireciReporteador;

    /**
     * Crea una nueva instancia de FrmInformesSireciControlador
     */
    public FrmInformesSireciControlador() {
        super();

        compania = SessionUtil.getCompania();
        fXlsxCons = ".xlsx";
        etiquetaVigenciaInicial = "Vigencia Inicial:";
        elementosVisibles = true;
        opcionMenu = SessionUtil.getMenuActual();

        frmInformesSireciReporteador = new FrmInformesSireciReporteador();

        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_INFORMESSIRECI_CONTROLADOR
                            .getCodigo();

            contArchivoselPlantilla = new ContenedorArchivo();

            switch (opcionMenu) {

            case "5203060101":
                cambiarTitulosFrmIF238();
                break;
            case OPCIONDOS:
                cambiarTitulosFrmEficacia();
                break;
            case "5203060103":
                cambiarIfProyOrientados();
                break;
            case "5203060104":
                cambiarSaneamientoAguaPotable();
                break;
            default:
                // prueba
                break;
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
     * Metodo que se encarga de cambiar los titulos segun la opcion de
     * menu 5203060101
     */
    private void cambiarTitulosFrmIF238() {
        titulo = idioma.getString("TB_TB4025");
        tituloVentana = idioma.getString("TB_TB4026");
        indVigIni = true;
        verBtExcel = 1;
    }

    /**
     * mvenegas Metodo que se encarga de cambiar los titulos segun la
     * opcion de menu 5203060102
     */
    private void cambiarTitulosFrmEficacia() {
        titulo = idioma.getString("TB_TB4029");
        tituloVentana = idioma.getString("TB_TB4030");
        etiquetaVigenciaInicial = "Vigencia:";
        elementosVisibles = false;
        vigenciaVisibles = true;
        indVigIni = true;
        verBtExcel = 2;
    }

    /**
     * Metodo que se encarga de asignar el titulo y cargar las
     * etiquetas, campos y botones de la opcion de menu: 5203060103.
     */
    private void cambiarIfProyOrientados() {
        titulo = idioma.getString("TB_TB4039");
        tituloVentana = idioma.getString("TB_TB4040");
        elementosVisibles = false;
        vigenciaVisibles = false;
        verFechas = true;
        verBtExcel = 3;
    }

    /**
     * Metodo que se encarga de asignar el titulo y cargar las
     * etiquetas, campos y botones de la opcion de menu: 5203060104.
     */
    private void cambiarSaneamientoAguaPotable() {
        titulo = idioma.getString("TB_TB4300");
        tituloVentana = idioma.getString("TB_TB4301");
        elementosVisibles = false;
        vigenciaVisibles = false;
        verFechas = true;
        verBtExcel = 4;
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
        // <CARGAR_LISTA>
        if (opcionMenu.equals(OPCIONDOS)) {
            cargarListaVigencia();
        }
        else {
            cargarListaVigenciaInicial();
        }

        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

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

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaVigenciaInicial
     *
     * 
     */
    public void cargarListaVigenciaInicial() {
        try {
            Map<String, Object> params = new TreeMap<>();

            params.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaVigenciaInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmInformesSireciControladorUrlEnum.URL_224
                                                                                            .getValue())
                                                            .getUrl(),
                                            params));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaVigencia
     *
     */
    public void cargarListaVigencia() {
        try {
            Map<String, Object> params = new TreeMap<>();

            params.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaVigencia = RegistroConverter.toListRegistro(
                            requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmInformesSireciControladorUrlEnum.URL_300
                                                                                            .getValue())
                                                            .getUrl(),
                                            params));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaVigenciaFinal
     */
    public void cargarListaVigenciaFinal() {
        try {
            Map<String, Object> params = new TreeMap<>();
            params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            params.put("VIGENCIAINICIAL", vigenciaInicial);

            listaVigenciaFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmInformesSireciControladorUrlEnum.URL_247
                                                                            .getValue())
                                            .getUrl(), params));

        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Excel (BT3050) en la
     * vista. Aplica para el menu: 5203060102.
     */
    public void oprimirExcelInfEficacia() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        frmInformesSireciReporteador.setLogger(logger);

        try {
            String datosExcel = ejbBancoProyectoCinco.generarInformeEficacia(
                            compania, Integer.parseInt(vigencia));

            generarReporte("INFORME_EFICACIA_DNP", "Ejecucion", datosExcel);
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Excel (BT3056) en la
     * vista. Aplica para el menu: 5203060101.
     */
    public void oprimirExcelProyInv() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        frmInformesSireciReporteador.setLogger(logger);

        try {
            String datosExcel = ejbBancoProyectoCinco
                            .generarProyectosInversion(
                                            compania,
                                            Integer.parseInt(vigenciaInicial),
                                            Integer.parseInt(vigenciaFinal));

            generarReporte("F23.8_PROYECTOS_INVERSIÓN",
                            "F23.8  PROYECTOS DE INVERSIÓ...", datosExcel);
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Excel (BT3079) en la
     * vista. Aplica para el menu: 5203060103.
     */
    public void oprimirExcelProyOrientados() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        /*-pespitia esta modificando esta controlador*/
        /*-     frmInformesSireciReporteador.setLogger(logger);
        
        try {
            String datosExcel = null;
        
            generarReporte("IF19_2 PROY. ORIENTADOS A ATENCIÓN DE INFANCIA, NIÑEZ Y ADOLESCENCIA",
                            "F19.2 PROYECTOS ORIENTADOS A...", datosExcel);
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }*/

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcelSaneAgua() {
        archivoDescarga = null;

        try {
            String datosExcel = ejbBancoProyectoCinco
                            .prepararDatosF202ProyectosDestinados(
                                            compania,
                                            fechaInicial,
                                            fechaFinal);

            generarReporte("F20.2 PROYECTOS DESTINADOS A AGUA POTABLE Y SANEAMIENTO BASICO",
                            "F20.2 PROYECTOS DESTINADOS A...", datosExcel);
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo que valida las condiciones iniciales de la plantilla en
     * Excel, los datos a ingresar y la configuracion de la plantilla.
     * 
     * @param nomReporte
     * -> Nombre asignado al archivo excel.
     * @param nomHoja
     * -> Hoja que se configura e ingresan los datos.
     */
    private void generarReporte(String nomReporte, String nomHoja,
        String datosExcel) {
        if (validarArchivo()) {
            String rutaArchivo = contArchivoselPlantilla.getArchivo()
                            .getPath();

            File fileA = new File(rutaArchivo);

            String extension = rutaArchivo.substring(rutaArchivo.indexOf('.'),
                            rutaArchivo.length()).toLowerCase();

            try (FileInputStream file = new FileInputStream(fileA)) {
                XSSFWorkbook workbookX = new XSSFWorkbook(file);

                if (fXlsxCons.equals(extension)
                    && nomHoja.equals(workbookX.getSheetAt(0).getSheetName())) {

                    archivoDescarga = JsfUtil.getArchivoDescarga(
                                    seleccionarEntradaDatos(workbookX,
                                                    datosExcel),
                                    SysmanFunciones.concatenar(nomReporte,
                                                    fXlsxCons));
                }
                else {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4028"));
                }

                file.close();
            }
            catch (NumberFormatException | IOException | JRException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    /**
     * 
     * Metodo que valida si el archivo seleccionado de la plantilla es
     * valido.
     * 
     */
    private boolean validarArchivo() {
        File fArchivo = contArchivoselPlantilla.getArchivo();

        if (fArchivo != null) {
            String rutaArchivo = String
                            .valueOf(contArchivoselPlantilla.getArchivo());

            String extension = rutaArchivo.substring(rutaArchivo.indexOf('.'),
                            rutaArchivo.length()).toLowerCase();

            if (fXlsxCons.equals(extension)) {
                return true;
            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4028"));
                return false;
            }
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4027"));
            return false;
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control VigenciaInicial
     * 
     * 
     */
    public void cambiarVigenciaInicial() {
        // <CODIGO_DESARROLLADO>
        listaVigenciaFinal = null;

        cargarListaVigenciaFinal();

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    /**
     * @todo
     */
    private ByteArrayInputStream seleccionarEntradaDatos(
        XSSFWorkbook workbook, String datosExcel) {
        ByteArrayInputStream inputStream = null;

        switch (opcionMenu) {
        case "5203060101":
            inputStream = frmInformesSireciReporteador
                            .exportarHojaDatosProyInv(workbook, datosExcel);
            break;
        case "5203060102":
            inputStream = frmInformesSireciReporteador
                            .exportarHojaDatosInfEficacia(workbook, datosExcel);
            break;
        case "5203060103":
            inputStream = frmInformesSireciReporteador
                            .exportarHojaDatosProyOrientados(workbook,
                                            datosExcel);
            break;
        case "5203060104":
            inputStream = frmInformesSireciReporteador
                            .exportarHojaDatosSaneAgua(workbook,
                                            datosExcel);
            break;
        default:
            break;
        }

        return inputStream;
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable vigenciaInicial
     * 
     * @return vigenciaInicial
     */
    public String getVigenciaInicial() {
        return vigenciaInicial;
    }

    /**
     * Asigna la variable vigenciaInicial
     * 
     * @param vigenciaInicial
     * Variable a asignar en vigenciaInicial
     */
    public void setVigenciaInicial(String vigenciaInicial) {
        this.vigenciaInicial = vigenciaInicial;
    }

    /**
     * @return the indVigIni
     */
    public boolean isIndVigIni() {
        return indVigIni;
    }

    /**
     * @param indVigIni
     * the indVigIni to set
     */
    public void setIndVigIni(boolean indVigIni) {
        this.indVigIni = indVigIni;
    }

    /**
     * Retorna la variable vigenciaFinal
     * 
     * @return vigenciaFinal
     */
    public String getVigenciaFinal() {
        return vigenciaFinal;
    }

    /**
     * Asigna la variable vigenciaFinal
     * 
     * @param vigenciaFinal
     * Variable a asignar en vigenciaFinal
     */
    public void setVigenciaFinal(String vigenciaFinal) {
        this.vigenciaFinal = vigenciaFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna el objeto contArchivoselPlantilla
     * 
     * @return contArchivoselPlantilla
     */
    public ContenedorArchivo getContArchivoselPlantilla() {
        return contArchivoselPlantilla;
    }

    /**
     * Asigna el objeto contArchivoselPlantilla
     * 
     * @param contArchivoselPlantilla
     * Variable a asignar en contArchivoselPlantilla
     */
    public void setContArchivoselPlantilla(
        ContenedorArchivo contArchivoselPlantilla) {
        this.contArchivoselPlantilla = contArchivoselPlantilla;
    }

    /**
     * @return the fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * @param fechaInicial
     * the fechaInicial to set
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * @return the fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * @param fechaFinal
     * the fechaFinal to set
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    /**
     * @return the verFechas
     */
    public boolean isVerFechas() {
        return verFechas;
    }

    /**
     * @param verFechas
     * the verFechas to set
     */
    public void setVerFechas(boolean verFechas) {
        this.verFechas = verFechas;
    }

    /**
     * @return the verBtExcel
     */
    public int getVerBtExcel() {
        return verBtExcel;
    }

    /**
     * @param verBtExcel
     * the verBtExcel to set
     */
    public void setVerBtExcel(int verBtExcel) {
        this.verBtExcel = verBtExcel;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaVigenciaInicial
     * 
     * @return listaVigenciaInicial
     */
    public List<Registro> getListaVigenciaInicial() {
        return listaVigenciaInicial;
    }

    /**
     * Asigna la lista listaVigenciaInicial
     * 
     * @param listaVigenciaInicial
     * Variable a asignar en listaVigenciaInicial
     */
    public void setListaVigenciaInicial(List<Registro> listaVigenciaInicial) {
        this.listaVigenciaInicial = listaVigenciaInicial;
    }

    /**
     * Retorna la lista listaVigencia
     * 
     * @return listaVigencia
     */
    public List<Registro> getListaVigencia() {
        return listaVigencia;
    }

    /**
     * Asigna la lista listaVigencia
     * 
     * @param listaVigencia
     * Variable a asignar en listaVigencia
     */
    public void setListaVigencia(List<Registro> listaVigencia) {
        this.listaVigencia = listaVigencia;
    }

    /**
     * Retorna la lista listaVigenciaFinal
     * 
     * @return listaVigenciaFinal
     */
    public List<Registro> getListaVigenciaFinal() {
        return listaVigenciaFinal;
    }

    /**
     * Asigna la lista listaVigenciaFinal
     * 
     * @param listaVigenciaFinal
     * Variable a asignar en listaVigenciaFinal
     */
    public void setListaVigenciaFinal(List<Registro> listaVigenciaFinal) {
        this.listaVigenciaFinal = listaVigenciaFinal;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTituloVentana() {
        return tituloVentana;
    }

    public void setTituloVentana(String tituloVentana) {
        this.tituloVentana = tituloVentana;
    }

    public String getEtiquetaVigenciaInicial() {
        return etiquetaVigenciaInicial;
    }

    public void setEtiquetaVigenciaInicial(String etiquetaVigenciaInicial) {
        this.etiquetaVigenciaInicial = etiquetaVigenciaInicial;
    }

    public boolean isElementosVisibles() {
        return elementosVisibles;
    }

    public void setElementosVisibles(boolean elementosVisibles) {
        this.elementosVisibles = elementosVisibles;
    }

    public boolean isVigenciaVisibles() {
        return vigenciaVisibles;
    }

    public void setVigenciaVisibles(boolean vigenciaVisibles) {
        this.vigenciaVisibles = vigenciaVisibles;
    }

    public String getVigencia() {
        return vigencia;
    }

    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
