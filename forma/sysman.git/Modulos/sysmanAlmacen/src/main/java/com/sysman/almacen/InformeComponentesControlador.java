/*-
 * InformeComponentesControlador.java
 *
 * 1.0
 * 
 * 07/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.sysman.almacen.ejb.EjbAlmacenCincoRemote;
import com.sysman.almacen.enums.InformeComponentesControladorUrlEnum;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite generar informe por componentes
 *
 * @version 1.0, 07/09/2018
 * @author asana
 */
@ManagedBean
@ViewScoped
public class InformeComponentesControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * filtro a realizar n informe
     */
    private String opcion;
    /**
     * bodega a filtrar en informe
     */
    private String bodega;
    /**
     * plantilla a generar informe
     */
    private String plantilla;
    /**
     * Estación a filtrar en informe
     */
    private String estacion;
    /**
     * responsable a filtrar en informe
     */
    private String responsable;

    private String dependencia;

    private boolean infIndividuall;

    private Date fecha;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * lista de las bodegas
     */
    private RegistroDataModelImpl listabodega;
    private RegistroDataModelImpl listaestacion;
    private RegistroDataModelImpl listaresponsable;
    private RegistroDataModelImpl listadependencia;
    List<Registro> listaTipos = null;

    private boolean visibleBodega;
    private boolean visibleEstacion;
    private boolean visibleResponsable;
    private boolean visibleDependencia;
    private boolean camposVisibleGer;
    private boolean gerencial;
    private ContenedorArchivo contArchivoSeleccionarArchivo;
    private StreamedContent archivoDescarga;
    @EJB
    private EjbAlmacenCincoRemote ejbAlmacenCinco;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de InformeComponentesControlador
     */
    public InformeComponentesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_INFORMECOMPONENTES_CONTROLADOR
                            .getCodigo();
            contArchivoSeleccionarArchivo = new ContenedorArchivo();
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
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListabodega();
        cargarListaestacion();
        cargarListaresponsable();
        cargarListaDependencia();
        visibleResponsable = true;
        visibleBodega = false;
        visibleEstacion = false;
        visibleDependencia = false;
        opcion = "1";
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
        asignarOrigenDatos();
        iniciarListas();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        origenDatos = "";
    }

    // <METODOS_CARGAR_LISTA>

    public void cambiaropcion() {
        if ("1".equals(opcion)) {
            visibleResponsable = true;
            visibleBodega = false;
            visibleEstacion = false;
            visibleDependencia = false;
        }
        else if ("2".equals(opcion)) {
            visibleResponsable = false;
            visibleBodega = false;
            visibleEstacion = true;
            visibleDependencia = false;
        }
        else if ("3".equals(opcion)) {
            visibleResponsable = false;
            visibleBodega = true;
            visibleEstacion = false;
            visibleDependencia = false;
        }
        else {
            visibleResponsable = false;
            visibleBodega = false;
            visibleEstacion = false;
            visibleDependencia = true;
        }
    }

    public void cambiargerencial() {

    }

    public void cargarListabodega() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformeComponentesControladorUrlEnum.URL4055
                                                        .getValue());

        listabodega = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaestacion
     *
     */
    public void cargarListaestacion() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformeComponentesControladorUrlEnum.URL4991
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaestacion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CONSECUTIVO.getName());
    }

    /**
     * 
     * Carga la lista listaresponsable
     *
     */
    public void cargarListaresponsable() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformeComponentesControladorUrlEnum.URL5683
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaresponsable = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CEDULA.getName());

    }

    public void cargarListaDependencia() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformeComponentesControladorUrlEnum.URL5684
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listadependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listabodega
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilabodega(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        bodega = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaestacion objeto que encapsula la accion proveniente de la
     * vista
     */
    public void seleccionarFilaestacion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        estacion = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CONSECUTIVO
                                                        .getName()),
                                        "")
                        .toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaresponsable
     *
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaresponsable(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        responsable = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CEDULA.getName()),
                        "").toString();

    }

    public void seleccionarFiladependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependencia = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();

    }
    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>

    private boolean validaciones() {
        boolean rta = true;

        if (contArchivoSeleccionarArchivo.getArchivo() == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1901"));
            rta = false;
        }

        if ((visibleDependencia
            && SysmanFunciones.validarVariableVacio(dependencia))
            || (visibleBodega
                && SysmanFunciones.validarVariableVacio(bodega))
            || (visibleEstacion
                && SysmanFunciones.validarVariableVacio(estacion))
            || (visibleResponsable
                && SysmanFunciones.validarVariableVacio(responsable))) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1906"));
            rta = false;
        }

        return rta;
    }

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>

        archivoDescarga = null;

        int contResponsab = 0;
        int contador = 0;
        if ("1".equals(opcion)) {
            estacion = "";
            bodega = "";
            dependencia = "";
        }
        else if ("2".equals(opcion)) {
            responsable = "";
            bodega = "";
            dependencia = "";
        }
        else if ("3".equals(opcion)) {
            responsable = "";
            estacion = "";
            dependencia = "";
        }
        else if ("4".equals(opcion)) {
            responsable = "";
            estacion = "";
            bodega = "";
        }

        if (!(gerencial)) {
            if (!validaciones()) {
                return;
            }

            try {
                String ruta = contArchivoSeleccionarArchivo.getArchivo()
                                .getPath();

                FileInputStream file;
                file = new FileInputStream(new File(ruta));
                XSSFWorkbook workbook = new XSSFWorkbook(file);

                CellStyle estilo1 = workbook.createCellStyle();
                estilo1.setBorderBottom((short) 1);
                estilo1.setBorderLeft((short) 1);
                estilo1.setBorderRight((short) 1);
                estilo1.setAlignment(HorizontalAlignment.CENTER);

                CellStyle estilo2 = workbook.createCellStyle();
                estilo2.setBorderBottom((short) 1);
                estilo2.setBorderLeft((short) 1);
                estilo2.setBorderRight((short) 1);
                estilo2.setAlignment(HorizontalAlignment.LEFT);

                String datos = ejbAlmacenCinco.generarPlantComponentes(compania,
                                estacion, bodega, responsable,
                                infIndividuall ? false : true, dependencia);

                String filas[] = null;
                String columnas[] = null;
                String responsables[] = null;
                String posicionResponsable[][] = null;

                if ("".equals(datos) || datos == null) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString(
                                    "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
                }
                else {

                    contador = 11;
                    if ("".equals(dependencia)) {

                        filas = datos.split(SysmanConstantes.SEPARADOR_REG);
                        columnas = filas[0]
                                        .split(SysmanConstantes.SEPARADOR_COL);
                    }
                    else {

                        responsables = datos.split(",.RESP.,");
                        filas = responsables[0]
                                        .split(SysmanConstantes.SEPARADOR_REG);
                        columnas = filas[0]
                                        .split(SysmanConstantes.SEPARADOR_COL);
                        posicionResponsable = new String[responsables.length
                            - 1][2];
                    }

                    XSSFSheet sheet = workbook.getSheetAt(0);
                    Row row = sheet.getRow(0);
                    Cell cell = row.getCell(9);
                    cell.setCellValue(SessionUtil.getUser().getCodigo());

                    row = sheet.getRow(1);
                    cell = row.getCell(9);
                    cell.setCellValue(new Date());

                    row = sheet.getRow(3);
                    cell = row.getCell(3);
                    cell.setCellValue(columnas[0]);
                    cell = row.getCell(infIndividuall ? 14 : 12);
                    cell.setCellValue(!"".equals(responsable) ? "X" : "");
                    cell.setCellStyle(estilo1);

                    row = sheet.getRow(4);
                    cell = row.getCell(3);
                    cell.setCellValue(SessionUtil.getCompaniaIngreso()
                                    .getCiudad());
                    cell = row.getCell(infIndividuall ? 14 : 12);
                    cell.setCellValue(!"".equals(estacion) ? "X" : "");
                    cell.setCellStyle(estilo1);

                    row = sheet.getRow(5);
                    cell = row.getCell(infIndividuall ? 3 : 4);
                    cell.setCellValue(infIndividuall
                        ? SysmanFunciones.convertirAFechaCadena(new Date())
                        : columnas[1]);
                    cell = row.getCell(infIndividuall ? 14 : 12);
                    cell.setCellValue(!"".equals(bodega) ? "X" : "");
                    cell.setCellStyle(estilo1);

                    row = sheet.getRow(6);
                    cell = row.getCell(4);
                    cell.setCellValue(
                                    infIndividuall ? columnas[1] : columnas[2]);
                    cell = row.getCell(infIndividuall ? 14 : 12);
                    cell.setCellValue(!"".equals(dependencia) ? "X" : "");
                    cell.setCellStyle(estilo1);

                    row = sheet.getRow(infIndividuall ? 7 : 6);
                    cell = row.getCell(3);
                    cell.setCellValue(columnas[2]);

                    String maximo[] = null;
                    String[] total = datos
                                    .split(SysmanConstantes.SEPARADOR_REG);
                    if ("".equals(dependencia)) {
                        maximo = filas;
                        contResponsab = 1;

                    }
                    else {
                        maximo = responsables;
                        contResponsab = maximo.length - 1;
                    }

                    sheet.shiftRows(12, sheet.getLastRowNum(),
                                    total.length - 1);
                    int a = 1;

                    for (int r = 1; r <= contResponsab; r = a) {

                        if (!"".equals(dependencia)) {

                            contador++;
                            a++;
                            Row row1r = sheet.createRow(contador);
                            filas = maximo[r].split(
                                            SysmanConstantes.SEPARADOR_REG);
                            Cell cellr = row1r.createCell(1);
                            columnas = filas[0].split(
                                            SysmanConstantes.SEPARADOR_COL);
                            cellr.setCellValue(columnas[0]);
                            cellr.setCellStyle(estilo2);
                            posicionResponsable[r - 1][0] = String
                                            .valueOf(contador);
                            posicionResponsable[r - 1][1] = columnas[0];
                        }
                        else {
                            a = 2;
                        }

                        for (int i = 1; i <= filas.length - 1; i++) {
                            contador++;
                            Row row1 = sheet.createRow(contador);
                            columnas = filas[i].split(
                                            SysmanConstantes.SEPARADOR_COL);
                            Cell cell1 = row1.createCell(1);
                            cell1.setCellValue(i);
                            cell1.setCellStyle(estilo1);

                            // MI_SERIEANT
                            Cell cel2 = row1.createCell(2);
                            cel2.setCellValue(columnas[0]);
                            cel2.setCellStyle(estilo1);
                            // MI_SERIE
                            Cell cel3 = row1.createCell(3);
                            cel3.setCellValue(columnas[1]);
                            cel3.setCellStyle(estilo1);
                            // MI_DESCRIPCION
                            Cell cell4 = row1.createCell(4);
                            cell4.setCellValue(columnas[2]);
                            cell4.setCellStyle(estilo2);
                            // MI_MARCA
                            Cell cell5 = row1.createCell(5);
                            cell5.setCellValue(columnas[3]);
                            cell5.setCellStyle(estilo2);

                            // Modelo
                            Cell cell6 = row1.createCell(6);
                            cell6.setCellValue(columnas[4]);
                            cell6.setCellStyle(estilo2);

                            Cell cell7 = row1.createCell(7);
                            cell7.setCellValue(columnas[1]);
                            cell7.setCellStyle(estilo1);
                            // MI_UBICACION
                            Cell cell8 = row1.createCell(8);
                            cell8.setCellValue(columnas[5]);
                            cell8.setCellStyle(estilo2);

                            if (!infIndividuall) {
                                // MI_VALORLIBROS
                                Cell cell9 = row1.createCell(9);
                                cell9.setCellValue(columnas[6]);
                                cell9.setCellStyle(estilo1);
                                // MI_ESTBUENO
                                Cell cell10 = row1.createCell(10);
                                cell10.setCellValue(columnas[7]);
                                cell10.setCellStyle(estilo1);
                                // MI_ESTMALO
                                Cell cell11 = row1.createCell(11);
                                cell11.setCellStyle(estilo1);
                                // MI_ESTREGULAR
                                Cell cell12 = row1.createCell(12);
                                cell10.setCellValue(columnas[8]);
                                cell12.setCellStyle(estilo1);
                            }
                            else {

                                Cell cell9 = row1.createCell(9);
                                cell9.setCellValue(" ");
                                cell9.setCellStyle(estilo1);

                                Cell cell10 = row1.createCell(10);
                                cell10.setCellStyle(estilo1);

                                Cell cell11 = row1.createCell(11);
                                cell11.setCellStyle(estilo1);

                                Cell cell12 = row1.createCell(12);
                                cell12.setCellStyle(estilo1);

                                Cell cell13 = row1.createCell(13);
                                cell13.setCellStyle(estilo1);

                                Cell cell14 = row1.createCell(14);
                                cell14.setCellStyle(estilo1);
                            }
                        }
                    }
                    // Da el borde del total de los registros
                    CellRangeAddress region = new CellRangeAddress(12,
                                    12 + total.length - 2, 1,
                                    infIndividuall ? 14 : 12);
                    RegionUtil.setBorderBottom(2, region, sheet);
                    RegionUtil.setBorderTop(2, region, sheet);
                    RegionUtil.setBorderLeft(2, region, sheet);
                    RegionUtil.setBorderRight(2, region, sheet);

                    // Se agrega el parámetro de cargo Almacenista
                    Row rowFirma = sheet.createRow(
                                    !infIndividuall ? 20 + total.length - 3
                                        : 27 + total.length - 3);
                    Cell cellFirma = rowFirma.createCell(4);
                    cellFirma.setCellValue(ejbSysmanUtl.consultarParametro(
                                    compania, "CARGO ALMACENISTA",
                                    SessionUtil.getModulo(), new Date(),
                                    false));

                    if (!"".equals(dependencia)) {
                        for (int m = 0; m < posicionResponsable.length; m++) {

                            int posicion = Integer.parseInt(
                                            posicionResponsable[m][0]);

                            CellReference celdaTitulo1Ini = new CellReference(
                                            posicion, 1);
                            String titulo1Ini = celdaTitulo1Ini
                                            .formatAsString();
                            CellReference celdaTitulo1Fin = new CellReference(
                                            posicion, infIndividuall ? 14 : 12);
                            String titulo1Fin = celdaTitulo1Fin
                                            .formatAsString();

                            CellRangeAddress regionRespons = CellRangeAddress
                                            .valueOf("" + titulo1Ini + ":"
                                                + titulo1Fin);

                            if (m == 0) {
                                RegionUtil.setBorderTop(2, regionRespons,
                                                sheet);
                            }
                            else {
                                RegionUtil.setBorderTop(1, regionRespons,
                                                sheet);
                            }

                            RegionUtil.setBorderLeft(2, regionRespons, sheet);
                            RegionUtil.setBorderRight(2, regionRespons, sheet);

                            if (m == posicionResponsable.length + 1) {
                                RegionUtil.setBorderBottom(2, regionRespons,
                                                sheet);
                            }
                            else {
                                RegionUtil.setBorderBottom(1, regionRespons,
                                                sheet);
                            }
                            sheet.addMergedRegion(regionRespons);
                        }
                    }

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    workbook.write(out);
                    out.close();
                    workbook.close();

                    archivoDescarga = JsfUtil.getArchivoDescarga(
                                    new ByteArrayInputStream(out.toByteArray()),
                                    "InformeComponentes.xlsx");

                }

            }
            catch (IOException | JRException | SystemException
                            | ParseException e) {

                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        else {
            informeGerencial();
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    public void informeGerencial() {
        String alias = "";
        String condicion = "";
        String sql = null;

        try {
            Map<String, Object> param = new HashMap<>();
            Map<String, Object> reemplazar = new HashMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaTipos = RegistroConverter.toListRegistro(requestManager
                            .getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformeComponentesControladorUrlEnum.URL5685
                                                                            .getValue())
                                            .getUrl(), param));

            int aux = 65;
            for (int i = 0; i < listaTipos.size(); i++) {
                if (SysmanFunciones.esBdSqlServer()) {
                    condicion = SysmanFunciones.concatenar(condicion, "['",
                                    listaTipos.get(i).getCampos()
                                                    .get(GeneralParameterEnum.CODIGO
                                                                    .getName())
                                                    .toString(),
                                    "']");
                    alias = SysmanFunciones.concatenar(alias, "['",
                                    listaTipos.get(i).getCampos()
                                                    .get(GeneralParameterEnum.CODIGO
                                                                    .getName())
                                                    .toString(),
                                    "'] AS ", String.valueOf((char) aux), "_",
                                    listaTipos.get(i).getCampos()
                                                    .get(GeneralParameterEnum.NOMBRE
                                                                    .getName())
                                                    .toString()
                                                    .replace(" ", "_")
                                                    .replace(".", "")
                                                    .replace(",", ""));
                }
                else {
                    condicion = SysmanFunciones.concatenar(condicion, "'",
                                    listaTipos.get(i).getCampos()
                                                    .get(GeneralParameterEnum.CODIGO
                                                                    .getName())
                                                    .toString(),
                                    "' ", String.valueOf((char) aux), "_",
                                    listaTipos.get(i).getCampos()
                                                    .get(GeneralParameterEnum.NOMBRE
                                                                    .getName())
                                                    .toString()
                                                    .replace(" ", "_")
                                                    .replace(".", "")
                                                    .replace(",", ""));
                }
                aux++;
                if (aux == 91) {
                    aux = 65;
                }
                if (i < listaTipos.size() - 1) {
                    condicion = SysmanFunciones.concatenar(condicion, ", ");
                    alias = SysmanFunciones.concatenar(alias, ", ");
                }
            }

            reemplazar.put("condicion", condicion);
            reemplazar.put("alias", alias);

            sql = Reporteador.resuelveConsulta("800200InformeGerencial",
                            Integer.valueOf(SessionUtil.getModulo()),
                            reemplazar);

            Workbook workBook = null;
            workBook = new XSSFWorkbook(JsfUtil.exportarHojaDatosStreamed(sql,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL)
                            .getStream());

            Sheet sheet = workBook.getSheetAt(0);
            sheet.shiftRows(0, sheet.getLastRowNum(), 2);
            sheet.createFreezePane(0, 3);

            Font font2 = workBook.createFont();
            font2.setFontName("Tahoma");
            font2.setFontHeightInPoints((short) 14);
            font2.setBold(true);

            Font font3 = workBook.createFont();
            font3.setFontName("Tahoma");
            font3.setFontHeightInPoints((short) 12);

            CellStyle style2 = workBook.createCellStyle();
            style2.setAlignment(CellStyle.ALIGN_CENTER);
            style2.setFont(font2);

            CellStyle style3 = workBook.createCellStyle();
            style3.setAlignment(CellStyle.ALIGN_CENTER);
            style3.setBorderBottom((short) 1);
            style3.setBorderLeft((short) 1);
            style3.setBorderTop((short) 1);
            style3.setBorderRight((short) 1);
            style3.setFont(font3);

            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue(SessionUtil.getCompaniaIngreso().getNombre());
            cell.setCellStyle(style2);

            Row row1 = sheet.createRow(1);
            Cell cell1 = row1.createCell(0);
            cell1.setCellValue("Informe Gerencial de Inventarios ");
            cell1.setCellStyle(style2);

            // Se crean regiones(titulos)
            CellReference celdaTitulo1Ini = new CellReference(0, 0);
            String titulo1Ini = celdaTitulo1Ini.formatAsString();
            CellReference celdaTitulo1Fin = new CellReference(0,
                            listaTipos.size() + 1);
            String titulo1Fin = celdaTitulo1Fin.formatAsString();

            CellRangeAddress regionRespons = CellRangeAddress
                            .valueOf("" + titulo1Ini + ":" + titulo1Fin);
            sheet.addMergedRegion(regionRespons);

            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN,
                            regionRespons, sheet, workBook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN,
                            regionRespons, sheet, workBook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN,
                            regionRespons, sheet, workBook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN,
                            regionRespons, sheet, workBook);

            // filaInicial, ColumnaInicial
            CellReference celdaTitulo2Ini = new CellReference(1, 0);
            String titulo2Ini = celdaTitulo2Ini.formatAsString();
            // filaFinal, ColumnaFinal
            CellReference celdaTitulo2Fin = new CellReference(1,
                            listaTipos.size() + 1);
            String titulo2Fin = celdaTitulo2Fin.formatAsString();

            CellRangeAddress regionRespons2 = CellRangeAddress
                            .valueOf("" + titulo2Ini + ":" + titulo2Fin);
            sheet.addMergedRegion(regionRespons2);

            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN,
                            regionRespons2, sheet, workBook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN,
                            regionRespons2, sheet, workBook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN,
                            regionRespons2, sheet, workBook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN,
                            regionRespons2, sheet, workBook);

            // Fin Titulos

            Row row2 = sheet.getRow(2);
            Cell cell2 = row2.createCell(listaTipos.size() + 1);
            cell2.setCellValue("TOTAL");
            cell2.setCellStyle(style2);

            cell1.setCellValue("Informe Gerencial de Inventarios ");
            cell1.setCellStyle(style2);

            for (int i = 3; i < sheet.getLastRowNum() + 1; i++) {
                Row rowAux = sheet.getRow(i);

                Row rowTot = sheet.getRow(i);
                Cell celda = rowTot.createCell(listaTipos.size() + 1);
                StringBuilder celdaIni = new StringBuilder("");
                for (int j = 0; j < listaTipos.size(); j++) {
                    Cell cellAux = rowAux.getCell(j);
                    cellAux.setCellStyle(style3);
                    celda.setCellStyle(style2);
                    CellReference cellRefIni = new CellReference(i, j);
                    celdaIni.append(cellRefIni.formatAsString() + ",");

                }
                celda.setCellFormula("SUM(" + celdaIni.toString() + ")");
            }

            Row rowTot = sheet.createRow(sheet.getLastRowNum() + 1);
            Cell cell3 = rowTot.createCell(0);
            cell3.setCellValue("TOTAL");
            cell3.setCellStyle(style2);

            for (int j = 1; j < listaTipos.size() + 1; j++) {
                Cell celda = rowTot.createCell(j);
                StringBuilder celdaIni = new StringBuilder("");
                for (int i = 3; i < sheet.getLastRowNum(); i++) {
                    celda.setCellStyle(style2);
                    CellReference cellRefIni = new CellReference(i, j);
                    celdaIni.append(cellRefIni.formatAsString() + ",");

                }
                celda.setCellFormula("SUM(" + celdaIni.toString() + ")");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workBook.write(out);
            out.close();

            archivoDescarga = JsfUtil
                            .getArchivoDescarga(
                                            new ByteArrayInputStream(
                                                            out.toByteArray()),
                                            "Informe Gerencial.xlsx");

            workBook.close();

        }
        catch (IOException | JRException | SQLException | DRException
                        | SysmanException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

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
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
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
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable opcion
     * 
     * @return opcion
     */
    public String getOpcion() {
        return opcion;
    }

    /**
     * Asigna la variable opcion
     * 
     * @param opcion
     * Variable a asignar en opcion
     */
    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    /**
     * Retorna la variable bodega
     * 
     * @return bodega
     */
    public String getBodega() {
        return bodega;
    }

    /**
     * Asigna la variable bodega
     * 
     * @param bodega
     * Variable a asignar en bodega
     */
    public void setBodega(String bodega) {
        this.bodega = bodega;
    }

    /**
     * Retorna la variable plantilla
     * 
     * @return plantilla
     */
    public String getPlantilla() {
        return plantilla;
    }

    /**
     * Asigna la variable plantilla
     * 
     * @param plantilla
     * Variable a asignar en plantilla
     */
    public void setPlantilla(String plantilla) {
        this.plantilla = plantilla;
    }

    /**
     * Retorna la variable estacion
     * 
     * @return estacion
     */
    public String getEstacion() {
        return estacion;
    }

    /**
     * Asigna la variable estacion
     * 
     * @param estacion
     * Variable a asignar en estacion
     */
    public void setEstacion(String estacion) {
        this.estacion = estacion;
    }

    /**
     * Retorna la variable responsable
     * 
     * @return responsable
     */
    public String getResponsable() {
        return responsable;
    }

    /**
     * Asigna la variable responsable
     * 
     * @param responsable
     * Variable a asignar en responsable
     */
    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listabodega
     * 
     * @return listabodega
     */
    public RegistroDataModelImpl getListabodega() {
        return listabodega;
    }

    /**
     * Asigna la lista listabodega
     * 
     * @param listabodega
     * Variable a asignar en listabodega
     */
    public void setListabodega(RegistroDataModelImpl listabodega) {
        this.listabodega = listabodega;
    }

    /**
     * Retorna la lista listaestacion
     * 
     * @return listaestacion
     */
    public RegistroDataModelImpl getListaestacion() {
        return listaestacion;
    }

    /**
     * Asigna la lista listaestacion
     * 
     * @param listaestacion
     * Variable a asignar en listaestacion
     */
    public void setListaestacion(RegistroDataModelImpl listaestacion) {
        this.listaestacion = listaestacion;
    }

    /**
     * Retorna la lista listaresponsable
     * 
     * @return listaresponsable
     */
    public RegistroDataModelImpl getListaresponsable() {
        return listaresponsable;
    }

    /**
     * Asigna la lista listaresponsable
     * 
     * @param listaresponsable
     * Variable a asignar en listaresponsable
     */
    public void setListaresponsable(RegistroDataModelImpl listaresponsable) {
        this.listaresponsable = listaresponsable;
    }

    public boolean isVisibleBodega() {
        return visibleBodega;
    }

    public void setVisibleBodega(boolean visibleBodega) {
        this.visibleBodega = visibleBodega;
    }

    public boolean isVisibleEstacion() {
        return visibleEstacion;
    }

    public void setVisibleEstacion(boolean visibleEstacion) {
        this.visibleEstacion = visibleEstacion;
    }

    public boolean isVisibleResponsable() {
        return visibleResponsable;
    }

    public void setVisibleResponsable(boolean visibleResponsable) {
        this.visibleResponsable = visibleResponsable;
    }

    public ContenedorArchivo getContArchivoSeleccionarArchivo() {
        return contArchivoSeleccionarArchivo;
    }

    public void setContArchivoSeleccionarArchivo(
        ContenedorArchivo contArchivoSeleccionarArchivo) {
        this.contArchivoSeleccionarArchivo = contArchivoSeleccionarArchivo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isInfIndividuall() {
        return infIndividuall;
    }

    public void setInfIndividuall(boolean infIndividuall) {
        this.infIndividuall = infIndividuall;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    public RegistroDataModelImpl getListadependencia() {
        return listadependencia;
    }

    public void setListadependencia(RegistroDataModelImpl listadependencia) {
        this.listadependencia = listadependencia;
    }

    public boolean isVisibleDependencia() {
        return visibleDependencia;
    }

    public void setVisibleDependencia(boolean visibleDependencia) {
        this.visibleDependencia = visibleDependencia;
    }

    public boolean isCamposVisibleGer() {
        return camposVisibleGer;
    }

    public void setCamposVisibleGer(boolean camposVisibleGer) {
        this.camposVisibleGer = camposVisibleGer;
    }

    public boolean isGerencial() {
        return gerencial;
    }

    public void setGerencial(boolean gerencial) {
        this.gerencial = gerencial;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
