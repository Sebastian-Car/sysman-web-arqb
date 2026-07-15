/*-
 * SiifReportesControlador.java
 *
 * 1.0
 *
 * 04/10/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.ejb.impl.EjbPresupuestoTres;
import com.sysman.presupuesto.enums.SiifReportesControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.io.InputStream;
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

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 04/10/2018
 * @author jrojas
 */
@ManagedBean
@ViewScoped
public class SiifReportesControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>

    private StreamedContent archivoDescarga;

    private String formato;
    private String nombre;
    private String mes;
    private String ano;
    private String anoInforme;
    private int tipoPlantilla;

    private List<Registro> listames;
    private List<Registro> listaAno;
    private List<Registro> listaCbAno;
    private RegistroDataModelImpl listaNombre;

    private Workbook workbook;
    private String rutaArchivo = "";

    @EJB
    private EjbPresupuestoTres ejbPresupuestoTres;

    public SiifReportesControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = 1953;
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
        abrirFormulario();
        cargarListaAno();
        cargarListaCbAno();
        cargarListaNombre();
        cargarListames();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>

        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>

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
     * Carga la lista listames
     *
     */
    public void cargarListames() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoInforme);

        try {
            listames = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            SiifReportesControladorUrlEnum.URL1718
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e) {
            Logger.getLogger(SiifReportesControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            setListaAno(
                            RegistroConverter
                                            .toListRegistro(requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            SiifReportesControladorUrlEnum.URL1717
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param)));
        }
        catch (SystemException e) {
            Logger.getLogger(SiifReportesControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        //
    }

    public void cargarListaCbAno() {
        listaCbAno = listaAno;
    }

    /**
     *
     * Carga la lista listaNombre
     *
     */
    public void cargarListaNombre() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SiifReportesControladorUrlEnum.URL1719
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaNombre = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "CODIGO");
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton generaPDF en la vista
     *
     *
     */
    public void oprimirgeneraPDF() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void cargarArchivoExcel(FileUploadEvent event) {
        // <CODIGO_DESARROLLADO>
        workbook = null;
        try {
            InputStream is = event.getFile().getInputstream();
            if (is == null) {
                return;
            }
            rutaArchivo = event.getFile().getFileName();
            String extension = FilenameUtils.getExtension(rutaArchivo);
            // Inicializa el workbook de acuerdo a la extension del
            // archivo (xls o xlsx)
            if (!rutaArchivo.isEmpty()) {
                if ("xls".equals(extension)) {
                    workbook = new HSSFWorkbook(is);
                }
                else {
                    workbook = new XSSFWorkbook(is);
                }

            }
        }

        catch (IOException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirBtExcel() {
        try {
            String contenidoPlano;
            StringBuilder plano = new StringBuilder();
            if (SysmanFunciones.validarVariableVacio(rutaArchivo)) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1901"));
                return;
            }
            switch (tipoPlantilla) {
            case 1: // CDP DISPONIBILIDADES
                plano = leerHoja(workbook, 0, 23, 1);
                break;
            case 2: // Obligaciones
                plano = leerHoja(workbook, 0, 40, 1);
                break;
            case 3: // Ordenes de Pago
                plano = leerHoja(workbook, 0, 50, 1);
                break;
            case 4: // Regisro de compromiso
                plano = leerHoja(workbook, 0, 35, 1);
                break;
            case 5:
                plano = leerHoja(workbook, 0, 40, 15);
                break;
            default:
                break;
            }

            if (SysmanFunciones.esBdSqlServer()) {
                contenidoPlano = plano.toString();
                contenidoPlano = contenidoPlano.substring(0,
                                contenidoPlano.length() - 1);
                contenidoPlano = "'" + contenidoPlano + " '";
            }
            else {
                contenidoPlano = Acciones
                                .getClobConcatenado(plano.toString());
                if (",".equals(contenidoPlano
                                .substring(contenidoPlano.length() - 1))) {
                    contenidoPlano = contenidoPlano.substring(0,
                                    contenidoPlano.length() - 1);
                }
            }

            ejbPresupuestoTres.registrarPrSiif(compania,
                            contenidoPlano, Integer.parseInt(ano),
                            tipoPlantilla, SessionUtil.getUser().getCodigo());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
            workbook.close();
        }
        catch (IOException | NumberFormatException | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     *
     * @param workbook,
     * Libro excel.
     * @param hoja,
     * Hoja del excel a leer.
     * @param columnas,
     * Número total de columnas.
     * @param filainicial,
     * Fila donde comienza a leer.
     * @return StringBuilder con el separador de registros y columnas.
     */
    @SuppressWarnings("deprecation")
    public StringBuilder leerHoja(Workbook workbook, int hoja, int columnas,
        int filainicial) {
        StringBuilder cadena = new StringBuilder();
        Sheet sheet = workbook.getSheetAt(hoja);
        Row fila;
        Cell celda;
        Cell validaApropiacion;
        int num = 0;
        FILAS: for (int i = filainicial; i < (sheet.getLastRowNum() + 1); i++) {
            fila = sheet.getRow(i);
            COLUMNAS: for (int j = 0; j < columnas; j++) {
                celda = fila.getCell(j);
                if (celda != null) {
                    validaApropiacion = fila.getCell(3);
                    if (tipoPlantilla == 5 && "".equals(validaApropiacion.getStringCellValue())) {
                        continue FILAS;
                    
                    }
                    num = num + (celda.getCellType() == 1
                        ? celda.getStringCellValue().replaceFirst("'", " ")
                                        .length()
                        : NumberToTextConverter
                                        .toText(celda.getNumericCellValue())
                                        .length());
                    cadena.append(celda.getCellType() == 1
                        ? celda.getStringCellValue().replaceFirst("'", " ")
                                        .replace(",", "")
                        : NumberToTextConverter
                                        .toText(celda.getNumericCellValue()));
                }
                else {
                    cadena.append("");
                }

                cadena.append(SysmanConstantes.SEPARADOR_COL);
            }
            cadena.append(SysmanConstantes.SEPARADOR_REG);
        }
        // cadena.append("')");
        return cadena;
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton GeneraExcel en la vista
     *
     *
     */
    public void oprimirGeneraExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    private void generarReporte(ReportesBean.FORMATOS formatos) {
        // Creacion arreglos
        HashMap<String, Object> reemplazar = new HashMap<>();
        HashMap<String, Object> parametros = new HashMap<>();
        String reporte;
        // Codigo del reporte
        reporte = formato;

        // <REEMPLAZAR VARIABLES EN CONSULTA>

        reemplazar.put("compania", compania);
        reemplazar.put("anio", anoInforme);
        reemplazar.put("mes", mes);

        // </REEMPLAZAR VARIABLES EN CONSULTA
        try {
            // <ENVIAR PARAMETROS AL REPORTE>
            parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso()
                            .getNombre().toUpperCase());

            // </ENVIAR PARAMETROS AL REPORTE>
            Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                            reemplazar, parametros);
            /*-aqui reporte hace referencia al nombre del reporte*/
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }

        catch (JRException | IOException
                        | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listames
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void cambiarAno()

    {
        // <CODIGO_DESARROLLADO>

        cargarListames();

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarmes() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNombre
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNombre(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        nombre = registroAux.getCampos().get("NOMBRE").toString();
        formato = registroAux.getCampos().get("FORMATO").toString();

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ano
     *
     * @return ano
     */

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public RegistroDataModelImpl getListaNombre() {
        return listaNombre;
    }

    public void setListaNombre(RegistroDataModelImpl listaNombre) {
        this.listaNombre = listaNombre;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public List<Registro> getListaCbAno() {
        return listaCbAno;
    }

    public void setListaCbAno(List<Registro> listaCbAno) {
        this.listaCbAno = listaCbAno;
    }

    public List<Registro> getListames() {
        return listames;
    }

    public void setListames(List<Registro> listames) {
        this.listames = listames;
    }

    public int getTipoPlantilla() {
        return tipoPlantilla;
    }

    public void setTipoPlantilla(int tipoPlantilla) {
        this.tipoPlantilla = tipoPlantilla;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public String getAnoInforme() {
        return anoInforme;
    }

    public void setAnoInforme(String anoInforme) {
        this.anoInforme = anoInforme;
    }

}
