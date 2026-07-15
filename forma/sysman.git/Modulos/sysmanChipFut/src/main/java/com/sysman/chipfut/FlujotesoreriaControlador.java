/*-
 * FlujotesoreriaControlador.java
 *
 * 1.0
 * 
 * 24/03/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.chipfut;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.chipfut.enums.FlujotesoreriaControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * 
 *
 * @version 1.0, 24/03/2017
 * @author jlramirez
 */
@ManagedBean
@ViewScoped
public class FlujotesoreriaControlador extends BeanBaseModal {
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
    private String mes;
    /**
     * 
     */
    private String fuente;
    /**
     * 
     */
    private String anio;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Este atributo se usa como auxiliar del componente selector de
     * archivos ArchivoBase y funciona como contenedor del archivo que
     * se debe guardar
     */
    private ContenedorArchivo contArchivoArchivoBase;
    private CellStyle style;

    private String conIngresos;
    private String conGastos;
    private String conContabilidad;
    private String conSaldos;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * 
     */
    private List<Registro> listaAnio;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * 
     */
    private RegistroDataModelImpl listaAuxiliarInicial;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FlujotesoreriaControlador
     */
    public FlujotesoreriaControlador() {
        super();
        compania = SessionUtil.getCompania();
        anio = Integer.toString(SysmanFunciones.ano(new Date()));
        try {
            // 1376
            numFormulario = GeneralCodigoFormaEnum.FLUJOTESORERIA_CONTROLADOR
                            .getCodigo();
            contArchivoArchivoBase = new ContenedorArchivo();
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
        cargarListaAnio();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaAuxiliarInicial();
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
         * FR1376-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaAnio
     */
    public void cargarListaAnio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAnio = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FlujotesoreriaControladorUrlEnum.URL5406
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaAuxiliarInicial
     */
    public void cargarListaAuxiliarInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FlujotesoreriaControladorUrlEnum.URL5760
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaAuxiliarInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Excel en la vista
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarExcel();//
        // </CODIGO_DESARROLLADO>
    }

    public void generarExcel() {
        generarConsultas();
        try (FileInputStream fileIs = new FileInputStream(
                        contArchivoArchivoBase.getArchivo())) {
            Workbook workbook = new HSSFWorkbook(fileIs);
            Sheet sheet = workbook.getSheet("FlujoTesorer�a");

            style = workbook.createCellStyle();
            // style.setBorderTop(CellStyle.BORDER_THIN);
            // style.setTopBorderColor(IndexedColors.GREEN.getIndex());
            // style.setBorderLeft(CellStyle.BORDER_THIN);
            // style.setLeftBorderColor(IndexedColors.GREEN.getIndex());
            // style.setBorderRight(CellStyle.BORDER_THIN);
            // style.setRightBorderColor(IndexedColors.GREEN.getIndex());
            // style.setAlignment(CellStyle.ALIGN_RIGHT);
            // style.setBorderBottom(CellStyle.BORDER_THIN);
            // style.setBottomBorderColor(IndexedColors.GREEN.getIndex());

            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 10);
            font.setFontName("Arial");
            style.setFont(font);

            if (sheet == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB857"));
                return;
            }

            prepararInforme(sheet, conIngresos, "FlujoTesoreria", 5, true, "N",
                            "FORMULAID");
            // prepararInforme(sheet, conGastos, "FlujoTesoreria", 5,
            // true, "N",
            // "FORMULAID");
            // prepararInforme(sheet, conContabilidad,
            // "FlujoTesoreria", 5, true,
            // "N",
            // "FORMULAID");
            // prepararInforme(sheet, conSaldos, "FlujoTesoreria", 5,
            // true, "O",
            // "FORMULAID");
            workbook.setForceFormulaRecalculation(true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            "INFORMES CONTABLES.xls");
        }
        catch (IOException | JRException e) {
            Logger.getLogger(FlujotesoreriaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private boolean prepararInforme(Sheet sheet, String strSql,
        String strnombrehoja, int intfilainicial, boolean insertarfila,
        String strcolumnareferencia, String strCampoBuscar) {
        List<Registro> rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                        strSql);
        if (rs.isEmpty()) {
            return false;
        }
        envioDetalle(sheet, rs, strnombrehoja, intfilainicial, insertarfila,
                        strcolumnareferencia, strCampoBuscar);
        return true;
    }

    private void envioDetalle(Sheet sheet, List<Registro> rs,
        String strnombrehoja, int intfilainicial, boolean insertarfila,
        String strcolumnareferencia, String strCampoBuscar) {
        String signo1 = null;
        String signo2 = null;
        for (int i = 0; i < rs.size(); i++) {
            if ((strcolumnareferencia != null) && (strCampoBuscar != null)) {
                // seleccionar esta columna "CC" strcolumnareferencia
                // CellRangeAddress region =
                // CellRangeAddress.valueOf(""+strcolumnareferencia+":"+strcolumnareferencia+"");

                // ME REEMPLAZA MAL CUANDO ESTA DOS VECES EL MISMO ID
                signo1 = "+";
                signo2 = "+";
                String busqueda = signo1
                    + rs.get(i).getCampos().get(strCampoBuscar) + signo2;
                int column = CellRangeAddress
                                .valueOf("" + strcolumnareferencia + ":"
                                    + strcolumnareferencia + "")
                                .getFirstColumn();
                Cell cell = searchColumnSheet(busqueda, column, 0,
                                sheet.getLastRowNum(), sheet);
                if (cell != null) {

                    cell.getStringCellValue();
                    Set<String> keys = rs.get(i).getCampos().keySet();
                    Iterator<String> it = keys.iterator();
                    int columna = 1;
                    String keyAct = "";
                    while (it.hasNext()) {
                        String valorKey = it.next();

                        if (valorKey.substring(0, 4).equals("FORM")) {
                            keys.remove(valorKey);
                            it = keys.iterator();
                        }
                        else {
                            if (!valorKey.equals(keyAct)) {
                                keyAct = valorKey;
                                double aux;
                                Cell cellAux = sheet.getRow(cell.getRowIndex())
                                                .getCell(columna);
                                if (cellAux != null) {
                                    aux = cellAux
                                                    .getNumericCellValue()
                                        + Double.valueOf(rs.get(i).getCampos()
                                                        .get(valorKey)
                                                        .toString());
                                }
                                else {
                                    aux = Double.valueOf(rs.get(i).getCampos()
                                                    .get(valorKey).toString());
                                }

                                Cell newCell = sheet.getRow(cell.getRowIndex())
                                                .createCell(columna);
                                newCell.setCellStyle(style);
                                newCell.setCellValue(aux);

                                columna++;
                            }
                        }
                    }
                }
                // signo1 = "+";
                // signo2 = "+";
                // busqueda = signo1 +
                // rs.get(i).getCampos().get(strCampoBuscar)
                // + signo2;
                // cell = searchColumnSheet(busqueda, column, 0,
                // sheet.getLastRowNum(), sheet);
                // if (cell != null) {
                // cell.getStringCellValue();
                // for (int j = 0; j < rs.size(); j++) {
                // Set<String> keys = rs.get(i).getCampos().keySet();
                // Iterator<String> it = keys.iterator();
                // while (it.hasNext()) {
                // String valorKey = it.next();
                // if (!valorKey.substring(0, 7).equals("FORMULA")) {
                // // objexcel.Cells(lngfila, lngcolumna
                // // + 1).Select
                // if ((strcolumnareferencia != null)
                // && (strCampoBuscar != null)
                // && valorKey.substring(0, 5)
                // .equals("VALOR")) {
                //
                // // objexcel.Cells(lngfila,
                // // NUMCOLUMNA).Value =
                // // Eval(Nz(objexcel.Cells(lngfila,
                // // NUMCOLUMNA).Value, 0) & Signo1
                // // &
                // // Val(rsBase.Fields(lngcolumna).Value))
                //
                // }
                // else {
                // if (valorKey.substring(0, 5)
                // .equals("VALOR")) {
                // // objexcel.Cells(lngfila,
                // // NUMCOLUMNA).Value =
                // // Val(rsBase.Fields(lngcolumna).Value)
                // }
                // }
                // }
                //
                // }
                // }
                // }
                // signo1 = "-";
                // signo2 = "+";
                // busqueda = signo1 +
                // rs.get(i).getCampos().get(strCampoBuscar)
                // + signo2;
                // cell = searchColumnSheet(busqueda, column, 0,
                // sheet.getLastRowNum(), sheet);
                // if (cell != null) {
                // cell.getStringCellValue();
                // for (int j = 0; j < rs.size(); j++) {
                // Set<String> keys = rs.get(i).getCampos().keySet();
                // Iterator<String> it = keys.iterator();
                // while (it.hasNext()) {
                // String valorKey = it.next();
                // if (!valorKey.substring(0, 7).equals("FORMULA")) {
                // // objexcel.Cells(lngfila, lngcolumna
                // // + 1).Select
                // if ((strcolumnareferencia != null)
                // && (strCampoBuscar != null)
                // && valorKey.substring(0, 5)
                // .equals("VALOR")) {
                // // objexcel.Cells(lngfila,
                // // NUMCOLUMNA).Value =
                // // Eval(Nz(objexcel.Cells(lngfila,
                // // NUMCOLUMNA).Value, 0) & Signo1
                // // &
                // // Val(rsBase.Fields(lngcolumna).Value))
                //
                // }
                // else {
                // if (valorKey.substring(0, 5)
                // .equals("VALOR")) {
                // // objexcel.Cells(lngfila,
                // // NUMCOLUMNA).Value =
                // // Val(rsBase.Fields(lngcolumna).Value)
                // }
                // }
                // }
                //
                // }
                // }
                // }
                // signo1 = "-";
                // signo2 = "-";
                // busqueda = signo1 +
                // rs.get(i).getCampos().get(strCampoBuscar)
                // + signo2;
                // cell = searchColumnSheet(busqueda, column, 0,
                // sheet.getLastRowNum(), sheet);
                // if (cell != null) {
                // cell.getStringCellValue();
                // for (int j = 0; j < rs.size(); j++) {
                // Set<String> keys = rs.get(i).getCampos().keySet();
                // Iterator<String> it = keys.iterator();
                // while (it.hasNext()) {
                // String valorKey = it.next();
                // if (!valorKey.substring(0, 7).equals("FORMULA")) {
                // // objexcel.Cells(lngfila, lngcolumna
                // // + 1).Select
                // if ((strcolumnareferencia != null)
                // && (strCampoBuscar != null)
                // && valorKey.substring(0, 5)
                // .equals("VALOR")) {
                //
                // // objexcel.Cells(lngfila,
                // // NUMCOLUMNA).Value =
                // // Eval(Nz(objexcel.Cells(lngfila,
                // // NUMCOLUMNA).Value, 0) & Signo1
                // // &
                // // Val(rsBase.Fields(lngcolumna).Value))
                //
                // }
                // else {
                // if (valorKey.substring(0, 5)
                // .equals("VALOR")) {
                // // objexcel.Cells(lngfila,
                // // NUMCOLUMNA).Value =
                // // Val(rsBase.Fields(lngcolumna).Value)
                // }
                // }
                // }
                //
                // }
                // }
                // }
                else {
                    continue;
                    // goto sigue
                }
            }
        }
    }

    public Cell searchColumnSheet(String searchText, int column, int rowStart,
        int rowEnd, Sheet sheet) {
        Cell c = null;
        for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {
            if ((sheet.getRow(rowNum) != null)
                && (sheet.getRow(rowNum).getCell(column) != null)) {
                Row r = sheet.getRow(rowNum);
                if (r.getCell(column, Row.RETURN_BLANK_AS_NULL) == null) {
                    continue;
                }
                Cell aux = r.getCell(column, Row.RETURN_BLANK_AS_NULL);
                aux.setCellType(HSSFCell.CELL_TYPE_STRING);

                if ((searchText != null)
                    && aux.getStringCellValue().contains(searchText)) {
                    c = aux;
                }
            }
        }
        return c;

    }

    public void generarConsultas() {
        StringBuilder condicion = new StringBuilder();
        StringBuilder condicion2 = new StringBuilder();
        StringBuilder condicion3 = new StringBuilder();
        StringBuilder condicion4 = new StringBuilder();
        HashMap<String, Object> reemplazos = new HashMap<>();
        for (int i = 1; i <= Integer.parseInt(mes); i++) {
            condicion.append(" CASE WHEN MES = ").append(i)
                            .append(" THEN SUM(EJE_PPT_CREDITO-EJE_PPT_DEBITO) ")
                            .append("+ SUM(MODIF_INGRESOS) ELSE 0 END MES")
                            .append(i);
            condicion2.append("SUM(MES").append(i).append(") MES").append(i);
            condicion3.append(" CASE WHEN MES = ").append(i)
                            .append(" THEN SUM(EJE_PPT_DEBITO-EJE_PPT_CREDITO) ")
                            .append(" ELSE 0 END MES").append(i);
            condicion4.append("SUM(PLAN_CONTABLE.SALDO").append(i).append(")")
                            .append(" SALDO").append(i);
            if (i != Integer.parseInt(mes)) {
                condicion.append(",");
                condicion2.append(",");
                condicion3.append(",");
                condicion4.append(",");
            }
        }

        reemplazos.put("anio", anio);
        reemplazos.put("mes", mes);
        reemplazos.put("auxiliar", "'" + fuente + "'");
        reemplazos.put("condicion", condicion);
        reemplazos.put("condicion2", condicion2);
        reemplazos.put("condicion3", condicion3);
        reemplazos.put("condicion4", condicion4);
        conIngresos = Reporteador.resuelveConsulta("prIngresos",
                        Integer.parseInt(SessionUtil.getModulo()), reemplazos);
        // conGastos = Reporteador.resuelveConsulta("prGastos",
        // Integer.parseInt(SessionUtil.getModulo()), reemplazos);
        // conContabilidad =
        // Reporteador.resuelveConsulta("prContabilidad",
        // Integer.parseInt(SessionUtil.getModulo()), reemplazos);
        //
        // condicion4 = new StringBuilder();
        // for (int i = 0; i <= Integer.parseInt(mes) - 1; i++) {
        // condicion4.append("SUM(PLAN_CONTABLE.SALDO").append(i).append(")")
        // .append(" SALDO").append(i);
        // if (i != Integer.parseInt(mes) - 1) {
        // condicion4.append(",");
        // }
        // }
        // reemplazos.put("condicion4", condicion4);
        // conSaldos = Reporteador.resuelveConsulta("prSaldos",
        // Integer.parseInt(SessionUtil.getModulo()), reemplazos);

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Anio
     */
    public void cambiarAnio() {
        // <CODIGO_DESARROLLADO>
        cargarListaAuxiliarInicial();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAuxiliarInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAuxiliarInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuente = registroAux.getCampos().get("CODIGO").toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable mes
     * 
     * @return mes
     */
    public String getMes() {
        return mes;
    }

    /**
     * Asigna la variable mes
     * 
     * @param mes
     * Variable a asignar en mes
     */
    public void setMes(String mes) {
        this.mes = mes;
    }

    /**
     * Retorna la variable fuente
     * 
     * @return fuente
     */
    public String getFuente() {
        return fuente;
    }

    /**
     * Asigna la variable fuente
     * 
     * @param fuente
     * Variable a asignar en fuente
     */
    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    /**
     * Retorna la variable anio
     * 
     * @return anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna el objeto contArchivoArchivoBase
     * 
     * @return contArchivoArchivoBase
     */
    public ContenedorArchivo getContArchivoArchivoBase() {
        return contArchivoArchivoBase;
    }

    /**
     * Asigna el objeto contArchivoArchivoBase
     * 
     * @param contArchivoArchivoBase
     * Variable a asignar en contArchivoArchivoBase
     */
    public void setContArchivoArchivoBase(
        ContenedorArchivo contArchivoArchivoBase) {
        this.contArchivoArchivoBase = contArchivoArchivoBase;
    }

    public String getConIngresos() {
        return conIngresos;
    }

    public void setConIngresos(String conIngresos) {
        this.conIngresos = conIngresos;
    }

    public String getConGastos() {
        return conGastos;
    }

    public void setConGastos(String conGastos) {
        this.conGastos = conGastos;
    }

    public String getConContabilidad() {
        return conContabilidad;
    }

    public void setConContabilidad(String conContabilidad) {
        this.conContabilidad = conContabilidad;
    }

    public String getConSaldos() {
        return conSaldos;
    }

    public void setConSaldos(String conSaldos) {
        this.conSaldos = conSaldos;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAnio
     * 
     * @return listaAnio
     */
    public List<Registro> getListaAnio() {
        return listaAnio;
    }

    /**
     * Asigna la lista listaAnio
     * 
     * @param listaAnio
     * Variable a asignar en listaAnio
     */
    public void setListaAnio(List<Registro> listaAnio) {
        this.listaAnio = listaAnio;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaAuxiliarInicial
     * 
     * @return listaAuxiliarInicial
     */
    public RegistroDataModelImpl getListaAuxiliarInicial() {
        return listaAuxiliarInicial;
    }

    /**
     * Asigna la lista listaAuxiliarInicial
     * 
     * @param listaAuxiliarInicial
     * Variable a asignar en listaAuxiliarInicial
     */
    public void setListaAuxiliarInicial(
        RegistroDataModelImpl listaAuxiliarInicial) {
        this.listaAuxiliarInicial = listaAuxiliarInicial;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
