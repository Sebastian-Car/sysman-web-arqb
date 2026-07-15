/*-
 * SubirInventarioInicialControlador.java
 *
 * 1.0
 * 
 * 10/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.sysman.almacen.ejb.EjbAlmacenCuatroRemote;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite subir el inventario inicial.
 *
 * @version 1.0, 10/04/2018
 * @author jreina
 */
@ManagedBean
@ViewScoped
public class SubirInventarioInicialControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String clob;

    private StringBuilder inventario;
    private StringBuilder dependencia;
    private StringBuilder dResponsable;
    private StringBuilder dOrdenCompra;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    /**
     * Este atributo se usa como auxiliar del componente selector de
     * archivos Inventario y funciona como contenedor del archivo que
     * se debe guardar
     */
    private ContenedorArchivo contArchivoInventario;

    @EJB
    private EjbAlmacenCuatroRemote ejbAlmacenCuatro;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de SubirInventarioInicialControlador
     */
    public SubirInventarioInicialControlador() {
        super();
        compania = SessionUtil.getCompania();
        clob = "TO_CLOB('";
        try {
            numFormulario = GeneralCodigoFormaEnum.SUBIR_IVENTARIOINICIAL_CONTROLADOR
                            .getCodigo();
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
        inventario = dependencia = dResponsable = dOrdenCompra = new StringBuilder();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton BT26 en la vista
     *
     */
    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton BT27 en la vista
     *
     */
    public void oprimirAceptar() {
        leerInventario(2);
    }

    /**
     * Metodo ejecutado al oprimir el boton BT27 en la vista
     *
     */
    public void oprimirValidar() {
        leerInventario(1);
    }

    public void leerInventario(int opcion) {
        // <CODIGO_DESARROLLADO>
        if (validarArchivo()) {
            String rutaArchivo = contArchivoInventario.getArchivo()
                            .getPath();
            try (FileInputStream file = new FileInputStream(
                            new File(rutaArchivo))) {

                Workbook workbook = new XSSFWorkbook(file);

                if (SysmanFunciones.esBdSqlServer()) {

                    inventario = new StringBuilder("'");
                    leerHoja(workbook, 0, 4, inventario);
                    dependencia = new StringBuilder("'");
                    leerHoja(workbook, 1, 4, dependencia);
                    dResponsable = new StringBuilder("'");
                    leerHoja(workbook, 2, 4, dResponsable);
                    dOrdenCompra = new StringBuilder("'");
                    leerHoja(workbook, 3, 21, dOrdenCompra);
                    workbook.close();
                }
                else {

                    inventario = new StringBuilder(clob);
                    leerHoja(workbook, 0, 4, inventario);
                    dependencia = new StringBuilder(clob);
                    leerHoja(workbook, 1, 4, dependencia);
                    dResponsable = new StringBuilder(clob);
                    leerHoja(workbook, 2, 4, dResponsable);
                    dOrdenCompra = new StringBuilder(clob);
                    leerHoja(workbook, 3, 21, dOrdenCompra);
                    workbook.close();

                }

                if (opcion == 1) {
                    String datos = ejbAlmacenCuatro.validarInventarioInicial(
                                    compania,
                                    inventario.toString(),
                                    dependencia.toString(),
                                    dResponsable.toString(),
                                    dOrdenCompra.toString());

                    archivoDescarga = JsfUtil.getArchivoDescarga(
                                    JsfUtil.serializarPlano(datos),
                                    "errores.txt");

                }
                else {
                    ejbAlmacenCuatro.subirInventarioInicial(compania,
                                    inventario.toString(),
                                    dependencia.toString(),
                                    dResponsable.toString(),
                                    dOrdenCompra.toString(), "ODI",
                                    SessionUtil.getUser().getCodigo());

                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("MSM_PROCESO_EJECUTADO"));
                }

            }
            catch (IOException | NumberFormatException | SystemException
                            | JRException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    public void leerHoja(Workbook workbook, int hoja, int columnas,
        StringBuilder cadena) {
        Sheet sheet = workbook.getSheetAt(hoja);
        Row fila;
        Cell celda;
        int num = 0;
        for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
            fila = sheet.getRow(i);
            if (fila != null) {
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
                    if (num >= 4000) {
                        cadena.append("') || TO_CLOB('");
                        num = 0;
                    }
                    cadena.append(SysmanConstantes.SEPARADOR_COL);
                }
                cadena.append(SysmanConstantes.SEPARADOR_REG);

            }
        }

        if (SysmanFunciones.esBdSqlServer()) {
            cadena.append("'");
        }
        else {
            cadena.append("')");
        }
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

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
