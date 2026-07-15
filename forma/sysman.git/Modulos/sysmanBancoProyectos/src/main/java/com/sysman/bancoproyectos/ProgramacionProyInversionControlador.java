package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoTresRemote;
import com.sysman.bancoproyectos.enums.ProgramacionProyInversionControladorEnum;
import com.sysman.bancoproyectos.enums.ProgramacionProyInversionControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.RegionUtil;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Permite generar la programaci&oacute;n/ejecuci&oacute;n de los
 * proyectos de inversi&oacute;n de una vigencia determinada.
 * 
 * @author esarmiento
 * @version 1.0, 25/09/2015
 * 
 * @author jrodrigueza
 * @version 2.0, 27/09/2017 Proceso de refactoring.
 */
@ManagedBean
@ViewScoped
public class ProgramacionProyInversionControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Indica si el informe va a tener en cuenta todas las fuentes de
     * financiaci&oacute;n.
     */
    private boolean fntFinanciacion;
    /**
     * C&oacute;digo de la fuente de financiaci&oacute;n inicial.
     */
    private String fuenteFinanciacion;
    /**
     * C&oacute;digo de la fuente de financiaci&oacute;n final.
     */
    private String fuentesFinanciacionFin;
    /**
     * A&ntilde;o ingresado.
     */
    private String vigencia;
    /**
     * Fecha inicial.
     */
    private Date fechaInicial;
    /**
     * Fecha final
     */
    private Date fechaFin;
    /**
     * Archivo que representa el documento en Excel.
     */
    private StreamedContent archivoDescarga;
    /**
     * Contenedor de archivos para ser seleccionados como plantilla.
     */
    private ContenedorArchivo contArchivoselectorPlantilla;
    /**
     * Fecha actual.
     */
    private Date fechaActual;
    /**
     * Constante para el campo CODIGO
     */
    private final String campoCodigo;
    /**
     * Implementacion del EJB de Banco de Proyectos para hacer llamado
     * a las funciones que se invocan en el bean y se encuentran
     * almacenadas en el paqueta PCK_BANCOS_PROY3.
     */
    @EJB
    private EjbBancoProyectoTresRemote ejbBancoProyectoTres;
    // </DECLARAR_ATRIBUTOS>

    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>

    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>

    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de fuentes de financiaci&oacute;n.
     */
    private RegistroDataModelImpl listaFuentesFinanciacion;
    /**
     * Lista de fuentes de financiaci&oacute;n.
     */
    private RegistroDataModelImpl listaFuentesFinanciacionFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Crea una nueva instancia de
     * ProgramacionProyInversionControlador
     */
    public ProgramacionProyInversionControlador() {
        super();
        compania = SessionUtil.getCompania();
        campoCodigo = GeneralParameterEnum.CODIGO.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.PROGRAMACION_PROY_INVERSION_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            contArchivoselectorPlantilla = new ContenedorArchivo();
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
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
        setFntFinanciacion(true);
        fechaActual = new Date();
        setVigencia(String.valueOf(SysmanFunciones.getParteFecha(fechaActual,
                        Calendar.YEAR)));
        setFechaInicial(fechaActual);
        setFechaFin(fechaActual);
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaFuentesFinanciacion();
        cargarListaFuentesFinanciacionFinal();
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
     * Carga la lista listaFuentesFinanciacion
     */
    public void cargarListaFuentesFinanciacion() {
        String urlEnumId = ProgramacionProyInversionControladorUrlEnum.URL6560
                        .getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), vigencia);

        listaFuentesFinanciacion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        campoCodigo);
    }

    /**
     * Carga la lista listaFuentesFinanciacionFinal
     */
    public void cargarListaFuentesFinanciacionFinal() {
        String urlEnumId = ProgramacionProyInversionControladorUrlEnum.URL7693
                        .getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), vigencia);
        param.put(ProgramacionProyInversionControladorEnum.FUENTEINICIAL
                        .getValue(), fuenteFinanciacion);

        listaFuentesFinanciacionFinal = new RegistroDataModelImpl(
                        urlBean.getUrl(), urlBean.getUrlConteo().getUrl(),
                        param, true, campoCodigo);
    }
    // </METODOS_CARGAR_LISTA>

    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Exportar en la vista
     */
    public void oprimirExportar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (faltanCamposObligatorios() || !validarFechas()) {
            return;
        }
        if (contArchivoselectorPlantilla.getArchivo() == null) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2291"));
            return;
        }
        String datosSCV17 = traerDatosSCV17();
        if (datosSCV17 == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(
                            "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
            return;
        }
        File file = contArchivoselectorPlantilla.getArchivo();
        try (ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
                        FileInputStream inputStream = new FileInputStream(
                                        file);) {
            Workbook workbook = new HSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheet(idioma.getString("TB_TB3659"));
            Row row = sheet.getRow(2);
            Cell cell = row.createCell(16);

            // fecha actual
            int ano = SysmanFunciones.getParteFecha(fechaActual, Calendar.YEAR);
            int mes = SysmanFunciones.getParteFecha(fechaActual, Calendar.MONTH)
                + 1;
            int dia = SysmanFunciones.getParteFecha(fechaActual,
                            Calendar.DAY_OF_MONTH);
            String strFecha = ano + " - " + mes + " - " + dia;
            cell.setCellValue(strFecha);

            // nombre de la compania
            Row rowCompania = sheet.getRow(5);
            Cell cellCompania = rowCompania.getCell(2);
            cellCompania.setCellValue(
                            SessionUtil.getCompaniaIngreso().getNombre());

            // nit de la compania
            Cell cellNit = rowCompania.getCell(7);
            cellNit.setCellValue(SessionUtil.getCompaniaIngreso().getNit());

            // vigencia
            Cell cellVigencia = rowCompania.getCell(13);
            cellVigencia.setCellValue(vigencia);

            // estilo de celda para bordes
            CellStyle estiloContenido = workbook.createCellStyle();
            estiloContenido.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            estiloContenido.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            estiloContenido.setBorderRight(HSSFCellStyle.BORDER_THIN);
            estiloContenido.setBorderTop(HSSFCellStyle.BORDER_THIN);

            int filas = 9;
            int columnas = 0;
            int contador = 1;
            String[] registros = datosSCV17
                            .split(SysmanConstantes.SEPARADOR_REG);
            for (int i = 0; i < registros.length; i++) {
                String[] campos = registros[i]
                                .split(SysmanConstantes.SEPARADOR_COL);

                Row rowContenido = sheet.getRow(filas);
                Cell cellContenido = rowContenido.getCell(columnas);
                cellContenido.setCellValue(contador);
                cellContenido.setCellStyle(estiloContenido);

                // CODIGOBPIM
                Cell cellCodigo = rowContenido.getCell(columnas + 1);
                cellCodigo.setCellValue(campos[0]);
                cellCodigo.setCellStyle(estiloContenido);

                // DENOMINACION
                Cell cellDenominacion = rowContenido.getCell(columnas + 2);
                cellDenominacion.setCellValue(campos[1]);
                cellDenominacion.setCellStyle(estiloContenido);

                // FUENTES
                Cell cellFuentes = rowContenido.getCell(columnas + 3);
                cellFuentes.setCellValue(campos[2]);
                cellFuentes.setCellStyle(estiloContenido);

                // PROGRAMA
                Cell cellPrograma = rowContenido.getCell(columnas + 4);
                cellPrograma.setCellValue(campos[3]);
                cellPrograma.setCellStyle(estiloContenido);

                // SECTOR
                Cell cellSector = rowContenido.getCell(columnas + 5);
                cellSector.setCellValue(campos[4]);
                cellSector.setCellStyle(estiloContenido);

                // NOMBREACTIVIDAD
                Cell cellNombreActividad = rowContenido.getCell(columnas + 6);
                cellNombreActividad.setCellValue(campos[5]);
                cellNombreActividad.setCellStyle(estiloContenido);

                // UNIDAD
                Cell cellUnidad = rowContenido.getCell(columnas + 7);
                cellUnidad.setCellValue(campos[6]);
                cellUnidad.setCellStyle(estiloContenido);

                // PONDERACION
                Cell cellPonderacion = rowContenido.getCell(columnas + 8);
                cellPonderacion.setCellValue(campos[7]);
                cellPonderacion.setCellStyle(estiloContenido);

                // PERIODOINICIO
                Cell cellIni = rowContenido.getCell(columnas + 9);
                cellIni.setCellValue(campos[8]);
                cellIni.setCellStyle(estiloContenido);

                // PERIODOFIN
                Cell cellFin = rowContenido.getCell(columnas + 10);
                cellFin.setCellValue(campos[9]);
                cellFin.setCellStyle(estiloContenido);

                // CANTIDAD
                Cell cellCantidadEje = rowContenido.getCell(columnas + 11);
                cellCantidadEje.setCellValue(campos[10]);
                cellCantidadEje.setCellStyle(estiloContenido);

                // VALOR
                Cell cellValor = rowContenido.getCell(columnas + 12);
                cellValor.setCellValue(campos[11]);
                cellValor.setCellStyle(estiloContenido);

                // NCONTRATO
                Cell cellContrato = rowContenido.getCell(columnas + 13);
                cellContrato.setCellValue(campos[12]);
                cellContrato.setCellStyle(estiloContenido);

                // CANTIDAD_EJE
                Cell cellCantidad = rowContenido.getCell(columnas + 14);
                cellCantidad.setCellValue(campos[13]);
                cellCantidad.setCellStyle(estiloContenido);

                // VALOREJECUTADO
                Cell cellValorEjecutado = rowContenido.getCell(columnas + 15);
                cellValorEjecutado.setCellValue(campos[14]);
                cellValorEjecutado.setCellStyle(estiloContenido);

                // MESES
                int mesInicial = SysmanFunciones.getParteFecha(fechaInicial,
                                Calendar.MONTH)
                    + 1;
                int mesFinal = SysmanFunciones.getParteFecha(fechaFin,
                                Calendar.MONTH)
                    + 1;
                String meses = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesInicial]
                    + "-"
                    + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesFinal];
                Cell cellMeses = rowContenido.getCell(columnas + 16);
                cellMeses.setCellValue(meses);
                cellMeses.setCellStyle(estiloContenido);

                contador++;
                filas++;
            }
            filas = filas + 3;

            Font font = workbook.createFont();
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            CellStyle estiloRepresentante = workbook.createCellStyle();
            estiloRepresentante.setFont(font);

            Row rowPie = sheet.getRow(filas);
            Cell cellRepresentante = rowPie.getCell(2);
            cellRepresentante.setCellValue(idioma.getString("TB_TB3663"));
            cellRepresentante.setCellStyle(estiloRepresentante);
            CellUtil.setAlignment(cellRepresentante, workbook,
                            CellStyle.ALIGN_CENTER);

            CellRangeAddress rangoRepresentante = new CellRangeAddress(filas,
                            filas, 2, 6);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN,
                            rangoRepresentante, sheet, workbook);
            sheet.addMergedRegion(rangoRepresentante);

            Cell cellRDiligenciado = rowPie.getCell(8);
            cellRDiligenciado.setCellValue(idioma.getString("TB_TB3664"));
            cellRDiligenciado.setCellStyle(estiloRepresentante);
            CellUtil.setAlignment(cellRDiligenciado, workbook,
                            CellStyle.ALIGN_CENTER);

            CellRangeAddress rangoDiligenciado = new CellRangeAddress(filas,
                            filas, 8, 12);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoDiligenciado,
                            sheet, workbook);
            sheet.addMergedRegion(rangoDiligenciado);

            Row rowPie1 = sheet.getRow(filas + 1);
            Cell cellNombre = rowPie1.getCell(2);
            cellNombre.setCellValue(idioma.getString("TG_NOMBRE2"));
            cellNombre.setCellStyle(estiloRepresentante);

            CellRangeAddress rangoNombre = new CellRangeAddress(filas + 1,
                            filas + 1, 3, 5);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoNombre,
                            sheet, workbook);
            sheet.addMergedRegion(rangoNombre);

            Cell cellNombreR = rowPie1.getCell(3);
            cellNombreR.setCellValue(idioma.getString("TB_TB3665"));
            CellUtil.setAlignment(cellNombreR, workbook,
                            CellStyle.ALIGN_CENTER);

            Cell cellNombreD = rowPie1.getCell(8);
            cellNombreD.setCellValue(idioma.getString("TG_NOMBRE2"));
            cellNombreD.setCellStyle(estiloRepresentante);

            CellRangeAddress rangoNombreD = new CellRangeAddress(filas + 1,
                            filas + 1, 9, 11);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoNombreD,
                            sheet, workbook);
            sheet.addMergedRegion(rangoNombreD);

            Cell cellNombreDil = rowPie1.getCell(9);
            cellNombreDil.setCellValue(SessionUtil.getUser().getCodigo());
            CellUtil.setAlignment(cellNombreDil, workbook,
                            CellStyle.ALIGN_CENTER);

            // Cargo
            Row rowpie2 = sheet.getRow(filas + 2);
            Cell cellFirma = rowpie2.getCell(2);
            cellFirma.setCellValue(idioma.getString("TG_CARGO2"));
            cellFirma.setCellStyle(estiloRepresentante);

            CellRangeAddress rangoCargo = new CellRangeAddress(filas + 2,
                            filas + 2, 3, 5);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoCargo, sheet,
                            workbook);
            sheet.addMergedRegion(rangoCargo);

            Cell cellF = rowpie2.getCell(3);
            cellF.setCellValue(idioma.getString("TB_TB3666"));
            CellUtil.setAlignment(cellF, workbook, CellStyle.ALIGN_CENTER);

            Cell cellCargo = rowpie2.getCell(8);
            cellCargo.setCellValue(idioma.getString("TG_CARGO2"));
            cellCargo.setCellStyle(estiloRepresentante);

            // crea rango Representante
            CellRangeAddress rangoCargoD = new CellRangeAddress(filas + 2,
                            filas + 2, 9, 11);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoCargoD,
                            sheet, workbook);
            sheet.addMergedRegion(rangoCargoD);

            Cell cellcargoD = rowpie2.getCell(9);
            cellcargoD.setCellValue(
                            SessionUtil.getUser().getTituloProfesional());
            CellUtil.setAlignment(cellcargoD, workbook, CellStyle.ALIGN_CENTER);

            // firma
            Row rowpie3 = sheet.getRow(filas + 4);
            Cell cellFi = rowpie3.getCell(2);
            cellFi.setCellValue(idioma.getString("TG_FIRMA3"));
            cellFi.setCellStyle(estiloRepresentante);

            // crea rango Firma
            CellRangeAddress rangoFirma = new CellRangeAddress(filas + 4,
                            filas + 4, 3, 5);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoFirma, sheet,
                            workbook);
            sheet.addMergedRegion(rangoFirma);

            Cell cellFirma1 = rowpie3.getCell(3);
            cellFirma1.setCellValue(
                            idioma.getString("TG_FIRMA_REPRESENTANTE_LEGAL"));
            CellUtil.setAlignment(cellFirma1, workbook, CellStyle.ALIGN_CENTER);

            Cell cellFirmaD = rowpie3.getCell(8);
            cellFirmaD.setCellValue(idioma.getString("TG_FIRMA3"));
            cellFirmaD.setCellStyle(estiloRepresentante);

            // crea rango Firma
            CellRangeAddress rangoFirmaD = new CellRangeAddress(filas + 4,
                            filas + 4, 9, 11);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoFirmaD,
                            sheet, workbook);
            sheet.addMergedRegion(rangoFirmaD);

            Cell cellFirmD = rowpie3.getCell(9);
            cellFirmD.setCellValue("");
            CellUtil.setAlignment(cellFirmD, workbook, CellStyle.ALIGN_CENTER);

            workbook.write(fileOut);

            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(fileOut.toByteArray()),
                            idioma.getString("TB_TB3668") + ".xls");
        }
        catch (IOException | JRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_BOTONES>

    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Vigencia
     */
    public void cambiarVigencia() {
        if (validarVigencia()) {
            actualizarAnios();
        }
        fuenteFinanciacion = null;
        fuentesFinanciacionFin = null;
        cargarListaFuentesFinanciacion();
        cargarListaFuentesFinanciacionFinal();
    }

    /**
     * Metodo ejecutado al cambiar el control Fechainicial
     */
    public void cambiarFechainicial() {
        if (vigencia != null && SysmanFunciones.getParteFecha(fechaInicial,
                        Calendar.YEAR) != Integer.parseInt(vigencia)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB2293"));
            fechaInicial = null;
            return;

        }
        if (fechaFin != null && fechaInicial.after(fechaFin)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB574"));
            fechaInicial = null;

        }
    }

    /**
     * Metodo ejecutado al cambiar el control Fechafinal
     */
    public void cambiarFechafinal() {
        if (vigencia != null && SysmanFunciones.getParteFecha(fechaFin,
                        Calendar.YEAR) != Integer.parseInt(vigencia)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB2293"));
            fechaFin = null;
            return;

        }
        if (fechaInicial != null && fechaInicial.after(fechaFin)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB2295"));
            fechaFin = null;

        }
    }

    /**
     * Metodo ejecutado al cambiar el control fntFinanciacion
     */
    public void cambiarfntFinanciacion() {
        // <CODIGO_DESARROLLADO>
        fuenteFinanciacion = null;
        fuentesFinanciacionFin = null;
        archivoDescarga = null;
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_CAMBIAR>

    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuentesFinanciacion
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuentesFinanciacion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteFinanciacion = extraerString(
                        registroAux.getCampos().get(campoCodigo));
        cargarListaFuentesFinanciacionFinal();
        fuentesFinanciacionFin = null;
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuentesFinanciacionFinal
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuentesFinanciacionFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuentesFinanciacionFin = extraerString(registroAux.getCampos()
                        .get(campoCodigo));
    }
    // </METODOS_COMBOS_GRANDES>

    // <METODOS_ARBOL>
    // </METODOS_ARBOL>

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable fuenteFinanciacion
     * 
     * @return fuenteFinanciacion
     */
    public String getFuenteFinanciacion() {
        return fuenteFinanciacion;
    }

    /**
     * @return the fntFinanciacion
     */
    public boolean isFntFinanciacion() {
        return fntFinanciacion;
    }

    /**
     * @param fntFinanciacion
     * the fntFinanciacion to set
     */
    public void setFntFinanciacion(boolean fntFinanciacion) {
        this.fntFinanciacion = fntFinanciacion;
    }

    /**
     * Asigna la variable fuenteFinanciacion
     * 
     * @param fuenteFinanciacion
     * Variable a asignar en fuenteFinanciacion
     */
    public void setFuenteFinanciacion(String fuenteFinanciacion) {
        this.fuenteFinanciacion = fuenteFinanciacion;
    }

    /**
     * Retorna la variable fuentesFinanciacionFin
     * 
     * @return fuentesFinanciacionFin
     */
    public String getFuentesFinanciacionFin() {
        return fuentesFinanciacionFin;
    }

    /**
     * Asigna la variable fuentesFinanciacionFin
     * 
     * @param fuentesFinanciacionFin
     * Variable a asignar en fuentesFinanciacionFin
     */
    public void setFuentesFinanciacionFin(String fuentesFinanciacionFin) {
        this.fuentesFinanciacionFin = fuentesFinanciacionFin;
    }

    /**
     * Retorna la variable vigencia
     * 
     * @return vigencia
     */
    public String getVigencia() {
        return vigencia;
    }

    /**
     * Asigna la variable vigencia
     * 
     * @param vigencia
     * Variable a asignar en vigencia
     */
    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }

    /**
     * Retorna la variable fechaInicial
     * 
     * @return fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * Asigna la variable fechaInicial
     * 
     * @param fechaInicial
     * Variable a asignar en fechaInicial
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Retorna la variable fechaFin
     * 
     * @return fechaFin
     */
    public Date getFechaFin() {
        return fechaFin;
    }

    /**
     * Asigna la variable fechaFin
     * 
     * @param fechaFin
     * Variable a asignar en fechaFin
     */
    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna el objeto contArchivoselectorPlantilla
     * 
     * @return contArchivoselectorPlantilla
     */
    public ContenedorArchivo getContArchivoselectorPlantilla() {
        return contArchivoselectorPlantilla;
    }

    /**
     * Asigna el objeto contArchivoselectorPlantilla
     * 
     * @param contArchivoselectorPlantilla
     * Variable a asignar en contArchivoselectorPlantilla
     */
    public void setContArchivoselectorPlantilla(
        ContenedorArchivo contArchivoselectorPlantilla) {
        this.contArchivoselectorPlantilla = contArchivoselectorPlantilla;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaFuentesFinanciacion
     * 
     * @return listaFuentesFinanciacion
     */
    public RegistroDataModelImpl getListaFuentesFinanciacion() {
        return listaFuentesFinanciacion;
    }

    /**
     * Asigna la lista listaFuentesFinanciacion
     * 
     * @param listaFuentesFinanciacion
     * Variable a asignar en listaFuentesFinanciacion
     */
    public void setListaFuentesFinanciacion(
        RegistroDataModelImpl listaFuentesFinanciacion) {
        this.listaFuentesFinanciacion = listaFuentesFinanciacion;
    }

    /**
     * Retorna la lista listaFuentesFinanciacionFinal
     * 
     * @return listaFuentesFinanciacionFinal
     */
    public RegistroDataModelImpl getListaFuentesFinanciacionFinal() {
        return listaFuentesFinanciacionFinal;
    }

    /**
     * Asigna la lista listaFuentesFinanciacionFinal
     * 
     * @param listaFuentesFinanciacionFinal
     * Variable a asignar en listaFuentesFinanciacionFinal
     */
    public void setListaFuentesFinanciacionFinal(
        RegistroDataModelImpl listaFuentesFinanciacionFinal) {
        this.listaFuentesFinanciacionFinal = listaFuentesFinanciacionFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    // <METODOS_ADICIONALES>

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

    /**
     * Validaci&oacute;n de campos obligatorios.
     * 
     * @return verdadero si faltan campos obligatorios
     */
    private boolean faltanCamposObligatorios() {
        if (SysmanFunciones.validarVariableVacio(vigencia)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2288"));
            return true;
        }
        if (fechaInicial == null) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2155"));
            return true;
        }
        if (fechaFin == null) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_DEBE_FECHA_FIN"));
            return true;
        }
        // Si el indicador "Todas" esta activo
        if (!fntFinanciacion && !validarFuentes()) {
            return true;
        }
        return false;
    }

    /**
     * Verifica que el rango de fechas seleccionado sea correcto.
     * 
     * @return Verdadero si el rango de las fechas es correcto.
     */
    private boolean validarFechas() {
        if (fechaInicial.after(fechaFin)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB574"));
            return false;
        }
        if (!validarVigencia()) {
            return false;
        }
        return true;
    }

    /**
     * Valida que las fuentes de financiaci&oacute;n hayan sido
     * seleccionadas, cuando el indicador "Todas" est&aacute;
     * desactivado.
     * 
     * @return verdadero si las fuentes est&oacute;n seleccionadas.
     */
    private boolean validarFuentes() {
        if (SysmanFunciones.validarVariableVacio(fuenteFinanciacion)
            || SysmanFunciones.validarVariableVacio(fuentesFinanciacionFin)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2290"));
            return false;
        }
        return true;
    }

    /**
     * Trae los datos necesarios para generarl el informe SCV_17.
     * 
     * @return cadena que representa los registros almacenados en la
     * temporal TEMP_F17_ACTIVIDADESPROYECTO.
     */
    private String traerDatosSCV17() {
        String cadenaSCV17 = null;
        try {
            cadenaSCV17 = ejbBancoProyectoTres.prepararDatosSCV17(
                            compania, Integer.parseInt(vigencia), fechaInicial,
                            fechaFin, fuenteFinanciacion,
                            fuentesFinanciacionFin, fntFinanciacion);
            cadenaSCV17 = cadenaSCV17.isEmpty() ? null : cadenaSCV17;
        }
        catch (NumberFormatException | SystemException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }
        return cadenaSCV17;
    }

    /**
     * Actualiza el a&ntilde;o de la fecha inicial y final.
     */
    private void actualizarAnios() {
        Calendar calendario = new GregorianCalendar();
        if (fechaInicial != null) {
            calendario.set(Integer.parseInt(vigencia), SysmanFunciones
                            .getParteFecha(fechaInicial, Calendar.MONTH),
                            SysmanFunciones.getParteFecha(fechaInicial,
                                            Calendar.DAY_OF_MONTH));
            fechaInicial = calendario.getTime();
        }
        if (fechaFin != null) {
            calendario.set(Integer.parseInt(vigencia), SysmanFunciones
                            .getParteFecha(fechaFin, Calendar.MONTH),
                            SysmanFunciones.getParteFecha(fechaFin,
                                            Calendar.DAY_OF_MONTH));
            fechaFin = calendario.getTime();
        }
    }

    /**
     * Valida que la vigencia ingresada sea v&aacute;lida.
     * 
     * @return verdadero si la vigencia es correcta
     */
    private boolean validarVigencia() {
        if (Integer.parseInt(vigencia) < 2000) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB2292"));
            vigencia = null;
            return false;
        }
        else {
            return true;
        }
    }
    // </METODOS_ADICIONALES>

}