/*-
 * ClasUsuariosyRangoAlCantXlsControlador.java
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
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.ClasUsuariosyRangoAlCantXlsControladorUrlEnum;
import com.sysman.util.ContenedorArchivo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Clase que se encarga de exportar en excel datos relacionados al
 * consumo. Usa una plantilla
 *
 * @version 1.0, 17/11/2016
 * @author jguerrero
 * @modified jguerrero
 * @version 2. 15/05/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Adem�s se ajustaron los errores del sonar
 * 
 * @author jreina
 * @version 3, 13/06/2017 Cambio c�digo formulario y actualizaci�n de
 * ConnectorPool
 */
@ManagedBean
@ViewScoped

public class ClasUsuariosyRangoAlCantXlsControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    /**
     * /** Variable declarada para almacenar temporalmente lo
     * seleccionado en el combo de a�o Inicial
     */
    private String anoInicial;
    /**
     * Variable declarada para almacenar temporalmente lo seleccionado
     * en el combo de periodo Inicial
     */
    private String periodoInicial;
    /**
     * Variable declarada para almacenar temporalmente lo seleccionado
     * en el combo de a�o Final
     */
    private String anoFinal;
    /**
     * Variable declarada para almacenar temporalmente lo seleccionado
     * en el combo de Periodo Final
     */
    private String periodoFinal;
    /**
     * Variable declarada para almacenar temporalmente el nombre de la
     * hoja de donde se quiere acceder a los datos. Este se selecciona
     * en el combo nombre hoja
     */
    private String nombreHoja;

    /**
     * Este atributo se usa como auxiliar del componente selector de
     * archivos selectorArchivo y funciona como contenedor del archivo
     * que se debe guardar
     */
    private ContenedorArchivo contArchivoselectorArchivo;
    private boolean cargarCiclo;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    /**
     * Lista encargadad de almacenar temporalmente los datos como
     * resultado del llamado a la base de datos a la tabla a�o
     */
    private List<Registro> listaAnoInicial;
    /**
     * Lista encargadad de almacenar temporalmente los datos como
     * resultado del llamado a la base de datos a la tabla SP_PERIODO
     */
    private List<Registro> listaPeriodoInicial;
    /**
     * Lista encargadad de almacenar temporalmente los datos como
     * resultado del llamado a la base de datos a la tabla a�o
     */
    private List<Registro> listaAnoFinal;
    /**
     * Lista encargadad de almacenar temporalmente los datos como
     * resultado del llamado a la base de datos a la tabla SP_PERIODO
     */
    private List<Registro> listaPeriodoFinal;

    /**
     * Lista encargada de almacenar los nombres de las hojas de la
     * plantilla de excel que se quiere cargar
     */
    private List<Registro> listaNombreHoja;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * ClasUsuariosyRangoAlCantXlsControlador
     */
    public ClasUsuariosyRangoAlCantXlsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.CLAS_USUARIOSY_RANGO_AL_CANT_XLS_CONTROLADOR
                            .getCodigo();
            contArchivoselectorArchivo = new ContenedorArchivo();
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
        // <CARGAR_LISTA>

        cargarListaAnoInicial();
        cargarListaPeriodoInicial();
        cargarListaAnoFinal();
        cargarListaPeriodoFinal();
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
        /*
         * FR1209-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * Dim db As DAO.Database Dim rs As DAO.Recordset Dim str As
         * String Set db = CurrentDb() str = "T" Set rs =
         * db.OpenRecordset("Select * from Ciclo where Compania='" &
         * Getcompany() & "' Order by Compania,Numero", ,
         * dbSQLPassThrough) While Not rs.EOF str = str & ";" &
         * rs!Numero rs.MoveNext Wend Me!Ciclo.RowSource = str End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>

    /**
     * 
     * Carga la lista listaAnoInicial
     *
     * Metodo encargado de hacer la peticion a la base de datos y
     * almacenar la respuesta en la lista ListaAnoInicial
     */
    public void cargarListaAnoInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ClasUsuariosyRangoAlCantXlsControladorUrlEnum.URL3992
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
     *
     * Metodo encargado de hacer la peticion a la base de datos y
     * almacenar la respuesta en la lista ListaPeriodoInicial
     */
    public void cargarListaPeriodoInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anoInicial);

        try {
            listaPeriodoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ClasUsuariosyRangoAlCantXlsControladorUrlEnum.URL3141
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
     *
     * Metodo encargado de hacer la peticion a la base de datos y
     * almacenar la respuesta en la lista ListaAnoFinal
     */
    public void cargarListaAnoFinal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anoInicial);

        try {
            listaAnoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ClasUsuariosyRangoAlCantXlsControladorUrlEnum.URL3142
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
     *
     * Metodo encargado de hacer la peticion a la base de datos y
     * almacenar la respuesta en la lista ListaPeriodoInicial
     */
    public void cargarListaPeriodoFinal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.MES.getName(), periodoInicial);

        param.put("ANOFINAL", anoFinal);
        try {
            listaPeriodoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ClasUsuariosyRangoAlCantXlsControladorUrlEnum.URL3143
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
     *
     * Metodo encargado de almacenar en la lista listaNombreHoja del
     * excel que se esta cargando
     */
    public void cargarListaNombreHoja() {

        listaNombreHoja = new ArrayList<>();

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    /**
     * 
     * Metodo ejecutado al oprimir el boton ImprimirExcel en la vista
     *
     * L
     *
     */
    public void oprimirImprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarExcel();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>

    /**
     * 
     * Metodo invocado al ejecutar el comando remoto rcActualizarHojas
     * en la vista
     *
     *
     *
     */
    public void ejecutarrcActualizarHojas() {
        // <CODIGO_DESARROLLADO>

        Workbook workbook = null;
        nombreHoja = "";
        try (FileInputStream fileIs = new FileInputStream(
                        contArchivoselectorArchivo.getArchivo());) {
            workbook = new HSSFWorkbook(fileIs);
            cargarNombreHojas(workbook);
            for (int i = 0; i < listaNombreHoja.size(); i++) {
                if ("4. vert. alcan.".equals(listaNombreHoja.get(i)
                                .getCampos().get("NOMBREHOJA"))) {
                    nombreHoja = "4. vert. alcan.";
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
            reg.getCampos().put("NOMBREHOJA", hoja);
            listaNombreHoja.add(reg);
        }
    }

    // <METODOS_CAMBIAR>

    /**
     * Metodo ejecutado al cambiar el control AnoInicial
     * 
     * 
     * 
     */
    public void cambiarAnoInicial() {
        // <CODIGO_DESARROLLADO>

        cargarListaAnoFinal();
        cargarListaPeriodoInicial();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control AnoFinal
     * 
     * 
     * 
     */
    public void cambiarAnoFinal() {
        // <CODIGO_DESARROLLADO>

        cargarListaPeriodoFinal();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPeriodoInicial() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado al cambiar el control AnoFinal
     * 
     * 
     * 
     */

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
     * Retorna la lista listaAnoInicial
     * 
     * @return listaAnoInicial
     */
    public List<Registro> getListaAnoInicial() {
        return listaAnoInicial;
    }

    public boolean isCargarCiclo() {
        return cargarCiclo;
    }

    public void setCargarCiclo(boolean cargarCiclo) {
        this.cargarCiclo = cargarCiclo;
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

    public String getNombreHoja() {
        return nombreHoja;
    }

    public void setNombreHoja(String nombreHoja) {
        this.nombreHoja = nombreHoja;
    }

    public ContenedorArchivo getContArchivoselectorArchivo() {
        return contArchivoselectorArchivo;
    }

    public void setContArchivoselectorArchivo(
        ContenedorArchivo contArchivoselectorArchivo) {
        this.contArchivoselectorArchivo = contArchivoselectorArchivo;
    }

    public List<Registro> getListaNombreHoja() {
        return listaNombreHoja;
    }

    public void setListaNombreHoja(List<Registro> listaNombreHoja) {
        this.listaNombreHoja = listaNombreHoja;
    }

    private String consultaPivot() {
        List<Registro> listaPivot;
        String inPivot = null;
        try {

            HashMap<String, Object> param = new HashMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            param.put("ANOINICIAL",
                            anoInicial);

            param.put("ANOFINAL",
                            anoFinal);

            param.put("PERIODOINICIAL",
                            periodoInicial);

            param.put("PERIODOFINAL",
                            periodoFinal);

            listaPivot = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ClasUsuariosyRangoAlCantXlsControladorUrlEnum.URL3145
                                                                            .getValue())
                                            .getUrl(), param));

            StringBuilder aux = new StringBuilder();
            for (Registro registro : listaPivot) {
                aux.append("'" + registro.getCampos().get("PERIODO") + "' AS \""
                    + registro.getCampos().get("PERIODO") + "\",");
            }
            inPivot = aux.toString();
            if (inPivot.length() > 0) {
                inPivot = inPivot.substring(0, inPivot.length() - 1);
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return inPivot;
    }

    private String selectHojaDatos() {

        List<Registro> listaPivot;
        String inPivot = null;
        try {
            HashMap<String, Object> param = new HashMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            param.put("ANOINICIAL",
                            anoInicial);

            param.put("ANOFINAL",
                            anoFinal);

            param.put("PERIODOINICIAL",
                            periodoInicial);

            param.put("PERIODOFINAL",
                            periodoFinal);

            listaPivot = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ClasUsuariosyRangoAlCantXlsControladorUrlEnum.URL3146
                                                                            .getValue())
                                            .getUrl(), param));

            StringBuilder aux = new StringBuilder();
            aux.append(" '' AS \"nombre\",");
            for (Registro registro : listaPivot) {
                aux.append("\"" + registro.getCampos().get("PERIODONUMERO")
                    + "\"   AS\""
                    + registro.getCampos().get("PERIODOLETRAS") + "\",");
            }
            inPivot = aux.toString();
            if (inPivot.length() > 0) {
                inPivot = inPivot.substring(0, inPivot.length() - 1);
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return inPivot;
    }

    private void generarExcel() {
        String alias = selectHojaDatos();
        String inPivot = consultaPivot();
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("anoInicial", anoInicial);
        reemplazar.put("periodoInicial", periodoInicial);
        reemplazar.put("anoFinal", anoFinal);
        reemplazar.put("periodoFinal", periodoFinal);

        reemplazar.put("inPivot", inPivot);
        reemplazar.put("Select", alias);

        String strSql = Reporteador.resuelveConsulta(
                        "800073XlsAlcantUnionLectRango_NoUsuarios",
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar);

        try (Workbook workbook = new HSSFWorkbook(
                        JsfUtil.exportarHojaDatosStreamed(strSql,
                                        ConectorPool.ESQUEMA_SYSMAN,
                                        FORMATOS.EXCEL97).getStream())) {

            Sheet sheet = workbook.getSheet("Report");
            if (sheet == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1858"));
                return;
            }

            sheet.shiftRows(0, sheet.getLastRowNum(), 5);

            for (int i = 0; i < 11; i++) {
                sheet.shiftRows(i * 6, (i * 6) + 1, 2);
            }

            /**
             * CellStyle style = workbook.createCellStyle();
             * style.setAlignment(CellStyle.ALIGN_LEFT); Font font =
             * workbook.createFont(); font.setFontName("Tahoma");
             * font.setBold(false); style.setFont(font);
             */
            Row rowTitulo = sheet.createRow(3);
            Cell cell = rowTitulo.createCell(0);
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(
                            "CIUDAD: "
                                + SessionUtil.getCompaniaIngreso().getCiudad()
                                + "                   DEPARTAMENTO: "
                                + (SessionUtil.getCompaniaIngreso()
                                                .getDepartamento())
                                                                .toUpperCase());

            Cell cell3 = sheet.createRow(4).createCell(0);
            cell3.setCellValue(
                            "NOMBRE DE LA EMPRESA: "
                                + SessionUtil.getCompaniaIngreso().getNombre());

            CellStyle styleNum = workbook.createCellStyle();
            styleNum.setDataFormat(workbook.createDataFormat()
                            .getFormat(" #,##0.00"));

            // Se hace el siguiente for para hacer espacios estaticos
            // segun la platilla

            formula(9, sheet);

            Row fila = sheet.createRow(77);

            for (int i = 3; i <= 14; i++) {

                Cell cellTotalesFormula = fila.createCell(i);
                cellTotalesFormula.setCellType(Cell.CELL_TYPE_FORMULA);
                CellReference cellRefIni = new CellReference(74, i);
                CellReference cellRefFin = new CellReference(76, i);
                String celdaIni = cellRefIni.formatAsString();
                String celdaFin = cellRefFin.formatAsString();
                cellTotalesFormula.setCellType(Cell.CELL_TYPE_FORMULA);
                cellTotalesFormula.setCellFormula(
                                "SUM(" + celdaIni + ":" + celdaFin + ")");
                cellTotalesFormula.setCellStyle(styleNum);

            }

            sheet.getRow(5).createCell(0).setCellValue("");

            workbook.setForceFormulaRecalculation(true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();
            workbook.close();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            "InfDescuentos.xls");
        }
        catch (SQLException | IOException | JRException
                        | DRException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void formula(int inicio, Sheet sheet) {
        String formulaCelda;
        Cell cellFormula;
        int iterador = inicio;

        char letra = 'D';

        for (int i = 74; i < 77; i++) {

            Row fila = sheet.createRow(i);
            for (int j = 3; j < 15; j++) {
                StringBuilder formula = new StringBuilder();

                formula.append("+");

                for (int k = iterador; k <= 72; k += 6) {

                    formula = formula.append(letra).append(k).append("+");

                }
                formulaCelda = formula.substring(0, formula.length() - 1);

                cellFormula = fila.createCell(j);
                cellFormula.setCellFormula(formulaCelda);

                letra++;

            }
            letra = 'D';
            iterador++;

        }
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
