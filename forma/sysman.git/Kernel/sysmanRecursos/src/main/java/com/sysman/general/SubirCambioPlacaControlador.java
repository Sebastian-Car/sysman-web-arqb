/*-
 * SubirCambioPlacaControlador.java
 *
 * 1.0
 * 
 * 08/06/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.almacen.ejb.impl.EjbAlmacenCuatroGeneral;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.chipfut.ejb.impl.EjbChipFutUnoGeneral;
import com.sysman.controladores.SessionUtil;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.precontractual.ejb.impl.EjbPrecontractualUnoGeneral;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.context.RequestContext;

/**
 * Este formulario permite cargar el archivo para la carga de cambios
 * de palaca
 *
 * @version 1.0, 08/06/2018
 * @author mvenegas
 */
@ManagedBean
@ViewScoped
public class SubirCambioPlacaControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Este atributo se usa como auxiliar del componente selector de
     * archivos Inventario y funciona como contenedor del archivo que
     * se debe guardar
     */
    private ContenedorArchivo contArchivoInventario;

    /**
     * Esta variable va a almacenar los datos contenidos en el excel
     */
    private StringBuilder cambios;
    /**
     * Esta variable almacena los parametros que se envian desde la
     * opcion de menu de cambiar placa
     */
    private Map<String, Object> parametrosRecibidos;
    /**
     * Esta variable arma el CLOB
     */
    private final StringBuilder clob;
    /**
     * nombres del formulario en mayuscula y minuscula
     */
    private String nombreMayuscula;
    private String nombreMinuscula;
    private String extension;

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de SubirCambioPlacaControlador
     */
    @EJB
    private EjbAlmacenCuatroGeneral ejbAlmacenCuatro;

    @EJB
    private EjbPrecontractualUnoGeneral ejbPrecontractualUno;
    @EJB
    private EjbChipFutUnoGeneral ejbChipFutUno;

    public SubirCambioPlacaControlador() {
        super();
        compania = SessionUtil.getCompania();
        parametrosRecibidos = new TreeMap<>();
        parametrosRecibidos = SessionUtil.getFlash();
        clob = new StringBuilder();
        clob.append("TO_CLOB('");

        if ("190135".equals(parametrosRecibidos.get("opcion").toString())) {

            nombreMayuscula = "ACTUALIZAR CODIGOS UNSPSC";
            nombreMinuscula = "Actualizar Codigos UNSPSC";
        }
        else if ("99020203"
                        .equals(parametrosRecibidos.get("opcion").toString())) {
            nombreMayuscula = "SUBIR SEGUIMIENTO RECIPROCAS";
            nombreMinuscula = "Subir Seguimiento Reciprocas";
        }
        else {
            nombreMayuscula = "SUBIR CAMBIOS DE PLACA";
            nombreMinuscula = "Subir Cambios De Placa";
        }

        try {
            numFormulario = 1813;
            validarPermisos();
            contArchivoInventario = new ContenedorArchivo();
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
        cambios = new StringBuilder();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar en la vista
     *
     *
     */
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        // <CODIGO_DESARROLLADO>
        FileInputStream file = null;
        try {
            if (validarArchivo()) {
                String rutaArchivo = contArchivoInventario.getArchivo()
                                .getPath();
                extension = rutaArchivo.substring(
                                rutaArchivo.indexOf('.'), rutaArchivo.length())
                                .substring(1, rutaArchivo.substring(
                                                rutaArchivo.indexOf('.'),
                                                rutaArchivo.length()).length());
                file = new FileInputStream(
                                new File(rutaArchivo));
                Workbook workbook = null;
                if (workbook == null) {
                    if ("xls".equals(extension)) {
                        workbook = new HSSFWorkbook(file);
                    }
                    else {
                        workbook = new XSSFWorkbook(file);
                    }
                }
                cambios = new StringBuilder(clob);

                if ("190135".equals(
                                parametrosRecibidos.get("opcion").toString())) {

                    leerHoja(workbook, 0, 8, cambios, 1);
                    workbook.close();

                    ejbPrecontractualUno.subirCodigosUnspsc(compania,
                                    cambios.toString(),
                                    SessionUtil.getUser().getCodigo());

                }
                else if ("99020203".equals(
                                parametrosRecibidos.get("opcion").toString())) {
                    String consecutivocreado = parametrosRecibidos
                                    .get("consecutivo").toString();

                    leerHoja(workbook, 0, 7, cambios, 10);// ese
                                                          // ultimo 10
                                                          // indica la
                                                          // fila por
                                                          // la que
                                                          // comienza(ROW)
                    workbook.close();
                    ejbChipFutUno.subirSeguimientoReciprocas(compania,
                                    cambios.toString(),
                                    SessionUtil.getUser().getCodigo(),
                                    consecutivocreado);

                }
                else {
                    leerHoja(workbook, 0, 4, cambios, 1);
                    workbook.close();
                    ejbAlmacenCuatro.subirCambioPlaca(compania,
                                    cambios.toString(),
                                    SessionUtil.getUser().getCodigo(),
                                    Long.parseLong(SysmanFunciones.toString(
                                                    parametrosRecibidos.get(
                                                                    "cambio"))));
                }

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_PROCESO_EJECUTADO"));

            }
        }
        catch (IOException | NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            try {
                if (file != null) {
                    file.close();
                }
            }
            catch (IOException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }

        // </CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @SuppressWarnings("deprecation")
    public void leerHoja(Workbook workbook, int hoja, int columnas,
        StringBuilder cadena, int filainicial) {

        Sheet sheet = workbook.getSheetAt(hoja);
        Row fila;
        Cell celda;
        int num = 0;
        for (int i = filainicial; i < sheet.getLastRowNum() + 1; i++) {
            fila = sheet.getRow(i);
            if (fila.getCell(0) != null
                && !SysmanFunciones.validarVariableVacio(
                                SysmanFunciones.nvl(fila.getCell(0), "")
                                                .toString())) {
                for (int j = 0; j < columnas; j++) {
                    celda = fila.getCell(j);
                    if (celda != null) {
                        num = num
                            + (celda.getCellType() == 1
                                ? celda.getStringCellValue()
                                                .replaceFirst("'", " ").length()
                                : NumberToTextConverter
                                                .toText(celda.getNumericCellValue())
                                                .length());
                        cadena.append(celda.getCellType() == 1
                            ? celda.getStringCellValue().replaceFirst("'", " ")
                            : NumberToTextConverter
                                            .toText(celda.getNumericCellValue()));
                    }
                    else {
                        cadena.append("");
                    }
                    if (num >= 10000) {
                        cadena.append("') || TO_CLOB('");
                        num = 0;
                    }
                    cadena.append(SysmanConstantes.SEPARADOR_COL);
                }
                cadena.append(SysmanConstantes.SEPARADOR_REG);
            }
        }
        cadena.append("')");
    }

    public boolean validarArchivo() {
        String archivo = String.valueOf(contArchivoInventario.getArchivo());
        if (contArchivoInventario.getArchivo() == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4001"));
            return false;
        }
        else {
            String extension = archivo
                            .substring(archivo.indexOf('.'), archivo.length())
                            .toLowerCase();
            if ((".xlsx".equals(extension)) || (".xls".equals(extension))) {
                return true;
            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4002"));
                return false;
            }
        }
    }

    /**
     * Metodo ejecutado al oprimir el boton BT26 en la vista
     *
     */
    public void oprimirsalir() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance()
                        .closeDialog("CICLO");
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna el objeto contArchivoInventario
     * 
     * @return contArchivoInventario
     */
    public ContenedorArchivo getContArchivoInventario() {
        return contArchivoInventario;
    }

    /**
     * Asigna el objeto contArchivoInventario
     * 
     * @param contArchivoInventario
     * Variable a asignar en contArchivoInventario
     */
    public void setContArchivoInventario(
        ContenedorArchivo contArchivoInventario) {
        this.contArchivoInventario = contArchivoInventario;
    }

    public Map<String, Object> getParametrosRecibidos() {
        return parametrosRecibidos;
    }

    public void setParametrosRecibidos(
        Map<String, Object> parametrosRecibidos) {
        this.parametrosRecibidos = parametrosRecibidos;
    }

    public String getNombreMayuscula() {
        return nombreMayuscula;
    }

    public void setNombreMayuscula(String nombreMayuscula) {
        this.nombreMayuscula = nombreMayuscula;
    }

    public String getNombreMinuscula() {
        return nombreMinuscula;
    }

    public void setNombreMinuscula(String nombreMinuscula) {
        this.nombreMinuscula = nombreMinuscula;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
