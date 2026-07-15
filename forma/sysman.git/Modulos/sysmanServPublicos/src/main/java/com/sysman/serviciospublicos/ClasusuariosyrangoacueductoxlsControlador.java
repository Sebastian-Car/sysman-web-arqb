/*-
 * ClasusuariosyrangoacueductoxlsControlador.java
 *
 * 1.0
 *
 * 17/11/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.ClasusuariosyrangoacueductoxlsControladorEnum;
import com.sysman.serviciospublicos.enums.ClasusuariosyrangoacueductoxlsControladorUrlEnum;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador del formulario
 * ClasusuariosyrangoacueductoxlsControlador
 *
 * @version 1.0, 17/11/2016
 * @author cperez
 *
 * -- Modificado por lcortes 17,18,22/05/2017. Refactorizacion de
 * codigo y ajustes de SonarLint. -- Se modifica el metodo
 * generarExcel para no realizar nuevamente ni la consulta de los
 * periodos ni la consulta del reporte para generar el archivo excel.
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Se cambió el llamado del código del
 * formulario
 * 
 */
@ManagedBean
@ViewScoped
public class ClasusuariosyrangoacueductoxlsControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Obtiene el ano Inicial para mandarlo a la consulta
     */
    private String anoInicial;
    /**
     * Obtiene el periodo Inicial para mandarlo a la consulta
     */
    private String periodoInicial;
    /**
     * Obtiene el ano Final para mandarlo a la consulta
     */
    private String anoFinal;
    /**
     * Obtiene el periodo Final para mandarlo a la consulta
     */
    private String periodoFinal;
    /**
     * Obtiene el ciclo para mandarlo a la consulta
     */
    private String ciclo;
    /**
     * Obtiene el nombre de la hoja de xml para generar el informe en
     * excel
     */
    private String nombreHoja;
    /**
     * Obtiene el nombre del archivo de xml para generar el informe en
     * excel
     */
    private String nombreArchivo;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Este atributo se usa como auxiliar del componente selector de
     * archivos nombreExcel y funciona como contenedor del archivo que
     * se debe guardar
     */
    private ContenedorArchivo contArchivonombreExcel;
    /**
     * Constante para el literal "PERIODO"
     */
    private static final String PERIODO = "PERIODO";
    /**
     * Constante para el literal "3. Cons. acue."
     */
    private static final String NOMBRE_HOJA = "3. Cons. acue.";
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Necesario para obtener mandar la lista el año Inicial
     */
    private List<Registro> listaAnoInicial;
    /**
     * Necesario para obtener mandar la lista del peridod Inicial
     */
    private List<Registro> listaPeriodoInicial;
    /**
     * Necesario para obtener mandar la lista del año Final
     */
    private List<Registro> listaAnoFinal;
    /**
     * Necesario para obtener mandar la lista del periodo Final
     */
    private List<Registro> listaPeriodoFinal;
    /**
     * Necesario para obtener mandar la lista del ciclo
     */
    private List<Registro> listaCiclo;
    /**
     * Lista de hojas disponibles para el archivo seleccionado
     */
    private List<Registro> listaNombreHoja;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de
     * ClasusuariosyrangoacueductoxlsControlador
     */
    public ClasusuariosyrangoacueductoxlsControlador() {
        super();
        compania = SessionUtil.getCompania();
        anoInicial = String.valueOf(GregorianCalendar.getInstance()
                        .get(GregorianCalendar.YEAR));
        anoFinal = String.valueOf(GregorianCalendar.getInstance()
                        .get(GregorianCalendar.YEAR));
        periodoInicial = SysmanFunciones
                        .padl(String.valueOf(GregorianCalendar.getInstance()
                                        .get(GregorianCalendar.MONTH)
                            + 1), 2, "0");
        periodoFinal = SysmanFunciones
                        .padl(String.valueOf(GregorianCalendar.getInstance()
                                        .get(GregorianCalendar.MONTH)
                            + 1), 2, "0");
        nombreArchivo = "sol_tarifas_2003_ac_alc";
        contArchivonombreExcel = new ContenedorArchivo();
        try {
            numFormulario = GeneralCodigoFormaEnum.CLASUSUARIOSYRANGOACUEDUCTOXLS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
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
        // <CARGAR_LISTA>
        cargarListaAnoInicial();
        cargarListaPeriodoInicial();
        cargarListaAnoFinal();
        cargarListaPeriodoFinal();
        cargarListaCiclo();
        cargarListaNombreHoja();
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
        anoInicial = String.valueOf(GregorianCalendar.getInstance()
                        .get(GregorianCalendar.YEAR));
        anoFinal = String.valueOf(GregorianCalendar.getInstance()
                        .get(GregorianCalendar.YEAR));
        periodoInicial = SysmanFunciones
                        .padl(String.valueOf(GregorianCalendar.getInstance()
                                        .get(GregorianCalendar.MONTH)
                            + 1), 2, "0");
        periodoFinal = SysmanFunciones
                        .padl(String.valueOf(GregorianCalendar.getInstance()
                                        .get(GregorianCalendar.MONTH)
                            + 1), 2, "0");
        nombreArchivo = "sol_tarifas_2003_ac_alc";

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaAnoInicial
     */
    public void cargarListaAnoInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ClasusuariosyrangoacueductoxlsControladorUrlEnum.URL7812
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
     * Carga la lista listaPeriodoInicial
     */
    public void cargarListaPeriodoInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoInicial);

        try {
            listaPeriodoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ClasusuariosyrangoacueductoxlsControladorUrlEnum.URL8254
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
     * Carga la lista listaAnoFinal
     */
    public void cargarListaAnoFinal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoInicial);

        try {
            listaAnoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ClasusuariosyrangoacueductoxlsControladorUrlEnum.URL8766
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
     * Carga la lista listaPeriodoFinal
     */
    public void cargarListaPeriodoFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ClasusuariosyrangoacueductoxlsControladorEnum.PARAM0
                        .getValue(), anoFinal);
        param.put(GeneralParameterEnum.MES.getName(), periodoInicial);

        try {
            listaPeriodoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ClasusuariosyrangoacueductoxlsControladorUrlEnum.URL9282
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
     * Carga la lista listaCiclo
     */
    public void cargarListaCiclo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ClasusuariosyrangoacueductoxlsControladorUrlEnum.URL9865
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
     * Carga la lista listaNombreHoja
     */
    public void cargarListaNombreHoja() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaNombreHoja = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ClasusuariosyrangoacueductoxlsControladorUrlEnum.URL10507
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>

        archivoDescarga = null;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ClasusuariosyrangoacueductoxlsControladorEnum.PARAM2
                        .getValue(), anoInicial);
        param.put(ClasusuariosyrangoacueductoxlsControladorEnum.PARAM0
                        .getValue(), anoFinal);
        param.put(ClasusuariosyrangoacueductoxlsControladorEnum.PARAM1
                        .getValue(), periodoInicial);
        param.put(ClasusuariosyrangoacueductoxlsControladorEnum.PARAM3
                        .getValue(), periodoFinal);
        try {
            List<Registro> listaPivot = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ClasusuariosyrangoacueductoxlsControladorUrlEnum.URL13232
                                                                            .getValue())
                                            .getUrl(), param));
            String inPivot;
            StringBuilder aux = new StringBuilder();
            for (Registro registro : listaPivot) {
                aux.append("'" + registro.getCampos().get(PERIODO) + "' AS \""
                    + registro.getCampos().get(PERIODO) + "\",");
            }
            inPivot = aux.toString();
            if (inPivot.length() > 0) {
                inPivot = inPivot.substring(0, inPivot.length() - 1);
            }
            HashMap<String, Object> reemplazos = new HashMap<>();
            reemplazos.put("anoInicial", anoInicial);
            reemplazos.put("periodoInicial", periodoInicial);
            reemplazos.put("anoFinal", anoFinal);
            reemplazos.put("periodoFinal", periodoFinal);
            reemplazos.put("inPivot", inPivot);
            String strSql = Reporteador.resuelveConsulta(
                            "800069InformeClasificacionUsuariosRangoAcueductoXls",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos);
            List<Registro> rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                            strSql);
            if (rs.isEmpty()) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1853"));
            }
            else {
                generarExcel(rs, listaPivot);
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public String obtenerNombreMes(String mes) {
        String res = "";
        try {
            res = ejbSysmanUtil.mostrarNombreDeMes(Integer.parseInt(mes));
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return res;
    }

    /*
     * para generar el archivo de excel
     *
     */
    private void generarExcel(List<Registro> rs, List<Registro> listaPivot) {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ClasusuariosyrangoacueductoxlsControladorEnum.PARAM2
                        .getValue(), anoInicial);
        param.put(ClasusuariosyrangoacueductoxlsControladorEnum.PARAM0
                        .getValue(), anoFinal);
        param.put(ClasusuariosyrangoacueductoxlsControladorEnum.PARAM1
                        .getValue(), periodoInicial);
        param.put(ClasusuariosyrangoacueductoxlsControladorEnum.PARAM3
                        .getValue(), periodoFinal);

        try (FileInputStream fileIs = new FileInputStream(
                        contArchivonombreExcel.getArchivo());) {
            String inPivot = "";
            StringBuilder aux = new StringBuilder();
            for (Registro registro : listaPivot) {
                aux.append("'" + registro.getCampos().get(PERIODO) + "' AS \""
                    + registro.getCampos().get(PERIODO) + "\",");
            }
            inPivot = aux.toString();
            if (inPivot.length() > 0) {
                inPivot = inPivot.substring(0, inPivot.length() - 1);
            }
            HashMap<String, Object> reemplazos = new HashMap<>();
            reemplazos.put("anoInicial", anoInicial);
            reemplazos.put("periodoInicial", periodoInicial);
            reemplazos.put("anoFinal", anoFinal);
            reemplazos.put("periodoFinal", periodoFinal);
            reemplazos.put("inPivot", inPivot);

            Workbook workbook = new HSSFWorkbook(fileIs);
            Sheet sheet = workbook.getSheet(nombreHoja);
            sheet.shiftRows(0, sheet.getLastRowNum(), 5);

            CellStyle style = workbook.createCellStyle();
            style.setAlignment(CellStyle.ALIGN_CENTER);
            Font font = workbook.createFont();
            font.setFontName("SansSerif");
            font.setBold(true);
            style.setFont(font);
            Row encabezado = sheet.createRow(5);
            int index = 1;
            crearEncabezadoExcel(listaPivot, encabezado, index, style);

            for (Registro registro : rs) {
                Row row = sheet.createRow(registro.getIndice() + 8);
                index = 1;
                for (Registro reg : listaPivot) {
                    Cell cell = row.createCell(index);
                    Object value = registro.getCampos()
                                    .get(reg.getCampos()
                                                    .get(PERIODO)
                                                    .toString()) != null
                                                        ? registro.getCampos()
                                                                        .get(reg.getCampos()
                                                                                        .get(PERIODO)
                                                                                        .toString())
                                                        : 0;
                    cell.setCellValue(Double.parseDouble(value.toString()));
                    sheet.autoSizeColumn(index);
                    index++;
                }
            }
            Cell cell = sheet.createRow(3).createCell(0);
            cell.setCellValue(idioma.getString("TG_CIUDAD2")
                + " " + SessionUtil.getCompaniaIngreso().getCiudad()
                + StringUtils.repeat(" ", 20)
                + idioma.getString("TG_DEPARTAMENTO") + ": " +
                SessionUtil.getCompaniaIngreso().getDepartamento());
            Cell cell2 = sheet.createRow(4).createCell(0);
            cell2.setCellValue(idioma.getString("TB_TB2055")
                + " " + SessionUtil.getCompaniaIngreso().getNombre());

            workbook.setActiveSheet(workbook.getSheetIndex(sheet));
            workbook.setForceFormulaRecalculation(true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();
            workbook.close();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            contArchivonombreExcel.getArchivo()
                                            .getName());
            fileIs.close();
        }
        catch (IOException | JRException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());

        }

    }

    /**
     * Crea las columnas del emcabezado del reporte Excel
     *
     * @param lista
     * Lista de elementos del encabezado
     * @param encabezado
     * Fila delencabezado
     * @param inicio
     * posicion inicial de le los encabezados
     * @param style
     * Estilo de las celdas de los encabezados
     */
    public void crearEncabezadoExcel(List<Registro> lista, Row encabezado,
        int inicio, CellStyle style) {
        int index = inicio;
        for (Registro registro : lista) {
            Cell cell = encabezado.createCell(index);

            String nombreMes = obtenerNombreMes(registro
                            .getCampos()
                            .get(PERIODO).toString().substring(
                                            4,
                                            6));

            cell.setCellValue(nombreMes + "-" + (registro
                            .getCampos()
                            .get(PERIODO).toString().substring(
                                            0,
                                            4)));
            cell.setCellStyle(style);
            index++;
        }
    }

    /**
     *
     * Metodo invocado al ejecutar el comando remoto rcActualizarHojas
     * en la vista
     *
     */
    public void ejecutarrcActualizarHojas() {
        // <CODIGO_DESARROLLADO>
        Workbook workbook = null;
        nombreHoja = "";
        try (FileInputStream fileIs = new FileInputStream(
                        contArchivonombreExcel.getArchivo());) {
            workbook = new HSSFWorkbook(fileIs);
            cargarNombreHojas(workbook);
            for (int i = 0; i < listaNombreHoja.size(); i++) {
                if (NOMBRE_HOJA.equals(listaNombreHoja.get(i)
                                .getCampos().get("ANO"))) {
                    nombreHoja = NOMBRE_HOJA;
                    break;
                }
            }
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control AnoInicial
     *
     */
    public void cambiarAnoInicial() {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodoInicial();
        cargarListaAnoFinal();
        cargarListaPeriodoFinal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control PeriodoInicial
     *
     */
    public void cambiarPeriodoInicial() {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodoFinal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control AnoFinal
     *
     */
    public void cambiarAnoFinal() {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodoFinal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Carga la lista de hojas que contien el archivo Excel
     * seleccionado
     *
     * @param workbook
     */
    private void cargarNombreHojas(Workbook workbook) {
        listaNombreHoja.clear();
        int hojas = workbook.getNumberOfSheets();
        for (int i = 0; i < hojas; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            String hoja = sheet.getSheetName();
            Registro reg = new Registro();
            reg.getCampos().put("ANO", hoja);
            listaNombreHoja.add(reg);
        }
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable anoInicial
     *
     * @return anoInicial
     */
    public String getAnoInicial() {
        return anoInicial;
    }

    /**
     * Asigna la variable anoInicial
     *
     * @param anoInicial
     * Variable a asignar en anoInicial
     */
    public void setAnoInicial(String anoInicial) {
        this.anoInicial = anoInicial;
    }

    /**
     * Retorna la variable periodoInicial
     *
     * @return periodoInicial
     */
    public String getPeriodoInicial() {
        return periodoInicial;
    }

    /**
     * Retorna la variable nombreHoja
     *
     * @return nombreHoja
     */
    public String getNombreHoja() {
        return nombreHoja;
    }

    /**
     * Asigna la variable nombreHoja
     *
     * @param nombreHoja
     * Variable a asignar en nombreHoja
     */
    public void setNombreHoja(String nombreHoja) {
        this.nombreHoja = nombreHoja;
    }

    /**
     * Retorna la variable nombreArchivo
     *
     * @return nombreArchivo
     */
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    /**
     * Asigna la variable nombreArchivo
     *
     * @param nombreArchivo
     * Variable a asignar en nombreArchivo
     */
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    /**
     * Asigna la variable periodoInicial
     *
     * @param periodoInicial
     * Variable a asignar en periodoInicial
     */
    public void setPeriodoInicial(String periodoInicial) {
        this.periodoInicial = periodoInicial;
    }

    /**
     * Retorna la variable anoFinal
     *
     * @return anoFinal
     */
    public String getAnoFinal() {
        return anoFinal;
    }

    /**
     * Asigna la variable anoFinal
     *
     * @param anoFinal
     * Variable a asignar en anoFinal
     */
    public void setAnoFinal(String anoFinal) {
        this.anoFinal = anoFinal;
    }

    /**
     * Retorna la variable periodoFinal
     *
     * @return periodoFinal
     */
    public String getPeriodoFinal() {
        return periodoFinal;
    }

    /**
     * Asigna la variable periodoFinal
     *
     * @param periodoFinal
     * Variable a asignar en periodoFinal
     */
    public void setPeriodoFinal(String periodoFinal) {
        this.periodoFinal = periodoFinal;
    }

    /**
     * Retorna la variable ciclo
     *
     * @return ciclo
     */
    public String getCiclo() {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     *
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna el objeto contArchivonombreExcel
     *
     * @return contArchivonombreExcel
     */
    public ContenedorArchivo getContArchivonombreExcel() {
        return contArchivonombreExcel;
    }

    /**
     * Asigna el objeto contArchivonombreExcel
     *
     * @param contArchivonombreExcel
     * Variable a asignar en contArchivonombreExcel
     */
    public void setContArchivonombreExcel(
        ContenedorArchivo contArchivonombreExcel) {
        this.contArchivonombreExcel = contArchivonombreExcel;
    }

    /**
     * Retorna la lista listaAnoInicial
     *
     * @return listaAnoInicial
     */
    public List<Registro> getListaAnoInicial() {
        return listaAnoInicial;
    }

    /**
     * Asigna la lista listaAnoInicial
     *
     * @param listaAnoInicial
     * Variable a asignar en listaAnoInicial
     */
    public void setListaAnoInicial(List<Registro> listaAnoInicial) {
        this.listaAnoInicial = listaAnoInicial;
    }

    /**
     * Retorna la lista listaPeriodoInicial
     *
     * @return listaPeriodoInicial
     */
    public List<Registro> getListaPeriodoInicial() {
        return listaPeriodoInicial;
    }

    /**
     * Asigna la lista listaPeriodoInicial
     *
     * @param listaPeriodoInicial
     * Variable a asignar en listaPeriodoInicial
     */
    public void setListaPeriodoInicial(List<Registro> listaPeriodoInicial) {
        this.listaPeriodoInicial = listaPeriodoInicial;
    }

    /**
     * Retorna la lista listaAnoFinal
     *
     * @return listaAnoFinal
     */
    public List<Registro> getListaAnoFinal() {
        return listaAnoFinal;
    }

    /**
     * Asigna la lista listaAnoFinal
     *
     * @param listaAnoFinal
     * Variable a asignar en listaAnoFinal
     */
    public void setListaAnoFinal(List<Registro> listaAnoFinal) {
        this.listaAnoFinal = listaAnoFinal;
    }

    /**
     * Retorna la lista listaPeriodoFinal
     *
     * @return listaPeriodoFinal
     */
    public List<Registro> getListaPeriodoFinal() {
        return listaPeriodoFinal;
    }

    /**
     * Asigna la lista listaPeriodoFinal
     *
     * @param listaPeriodoFinal
     * Variable a asignar en listaPeriodoFinal
     */
    public void setListaPeriodoFinal(List<Registro> listaPeriodoFinal) {
        this.listaPeriodoFinal = listaPeriodoFinal;
    }

    /**
     * Retorna la lista listaCiclo
     *
     * @return listaCiclo
     */
    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     *
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    /**
     * Retorna la lista listaNombreHoja
     *
     * @return listaNombreHoja
     */
    public List<Registro> getListaNombreHoja() {
        return listaNombreHoja;
    }

    /**
     * Asigna la lista listaNombreHoja
     *
     * @param listaNombreHoja
     * Variable a asignar en listaNombreHoja
     */
    public void setListaNombreHoja(List<Registro> listaNombreHoja) {
        this.listaNombreHoja = listaNombreHoja;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
