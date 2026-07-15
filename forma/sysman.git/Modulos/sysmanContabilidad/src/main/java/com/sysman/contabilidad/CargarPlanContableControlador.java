/*-
 * CargarPlanContableControlador.java
 *
 * 1.0
 *
 * 26 abr. 2019
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadSieteLocal;
import com.sysman.contabilidad.enums.CargarPlanContableControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

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

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Formulario que permite cargar el plan contable o los saldos contables
 *
 * @version 1.0, 26/04/2019
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class CargarPlanContableControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena la seleccion del check cargar plan
     */
    private boolean cargarPlan;
    /**
     * Atributo que almacena la seleccion del check cargar saldo
     */
    private boolean cargarSaldo;
    /**
     * Variable que almacena el anio seleccionado en la forma
     */
    private String anio;
    /**
     * Este atributo se usa como auxiliar del componente selector de archivos cargarExcel y funciona como contenedor del archivo que se debe guardar
     */
    private ContenedorArchivo contArchivocargarExcel;

    /**
     * Atributo que sirve como bandera para validar los anios de la vista y del archivo excel
     */
    private boolean errorAnio;

    /**
     * Variable que almacena la informacion del excel
     */
    private StringBuilder cadena;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que carga los anios
     */
    private List<Registro> listaAnio;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbContabilidadSieteLocal ejbContabilidadSiete;

    /**
     * Crea una nueva instancia de CargarPlanContableControlador
     */
    public CargarPlanContableControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        cargarSaldo = false;
        cargarPlan = false;
        anio = Integer.toString(SysmanFunciones.ano(new Date()));
        contArchivocargarExcel = new ContenedorArchivo();
        try
        {
            numFormulario = 2067;
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        cargarListaAnio();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaAnio
     *
     */
    public void cargarListaAnio()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaAnio = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CargarPlanContableControladorUrlEnum.URL3809
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton CargarExcel en la vista
     *
     */
    public void oprimirCargarExcel()
    {
        int opcion;
        FileInputStream file = null;
        cadena = new StringBuilder();
        errorAnio = false;
        try
        {

            if (validarChecks() && validarArchivo())
            {

                String rutaArchivo = contArchivocargarExcel.getArchivo()
                                .getPath();

                String extension = rutaArchivo
                                .substring(rutaArchivo.indexOf('.'),
                                                rutaArchivo.length())
                                .substring(1, rutaArchivo.substring(
                                                rutaArchivo.indexOf('.'),
                                                rutaArchivo.length()).length());

                file = new FileInputStream(new File(rutaArchivo));

                Workbook workbook = null;

                if ("xls".equals(extension))
                {
                    workbook = new HSSFWorkbook(file);
                }
                else
                {
                    workbook = new XSSFWorkbook(file);
                }
                
                if (!validarHojas(workbook)) {
                	return;
                }

                if (cargarPlan)
                {
                    opcion = 1;
                    leerHoja(workbook, 0, 13, cadena, 1);
                    cargarDatos(opcion);
                }
                else if (cargarSaldo)
                {
                    opcion = 2;
                    leerHoja(workbook, 1, 12, cadena, 1);
                    cargarDatos(opcion);
                }

                file.close();
                workbook.close();
            }
        }
        catch (IOException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }

    }

    private boolean validarChecks()
    {
        if (!cargarPlan && !cargarSaldo)
        {
            JsfUtil.agregarMensajeAlerta("Debe seleccionar una opción");
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean validarArchivo()
    {

        if (contArchivocargarExcel.getArchivo() == null)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4001"));
            return false;
        }
        else
        {
            return true;
        }
    }

    public void leerHoja(Workbook workbook, int hoja, int columnas,
        StringBuilder cadena, int filainicial)
    {
        cadena.append("TO_CLOB('");
        Sheet sheet = workbook.getSheetAt(hoja);
        DataFormatter formatter = new DataFormatter();
        Row fila;
        Cell celda;
        int num = 0;

        for (int i = filainicial; i < sheet.getLastRowNum() + 1; i++)
        {
            fila = sheet.getRow(i);

            if (fila.getCell(0) != null)
            {

                for (int j = 0; j < columnas; j++)
                {
                    celda = fila.getCell(j);
                    if (celda != null)
                    {
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

                        if (i == 1 && j == 0) {

                            boolean esTexto = celda.getCellTypeEnum() == CellType.STRING
                                    || (celda.getCellTypeEnum() == CellType.FORMULA
                                        && celda.getCachedFormulaResultTypeEnum() == CellType.STRING);

                            if (!esTexto) {
                                JsfUtil.agregarMensajeAlerta("La celda de compañía debe ser tipo texto.");
                                errorAnio = true;
                                return;
                            }

                            String valorCelda = formatter.formatCellValue(celda).trim();

                            if (!valorCelda.equalsIgnoreCase(compania.trim())) {
                                JsfUtil.agregarMensajeAlerta("La compañía no correspondea a la activa en la sesión.");
                                errorAnio = true;
                                return;
                            }
                        }
                        
                        if (i == 1 && j == 1) {
                        	String valorCelda = formatter.formatCellValue(celda).trim();

                            if (!anio.trim().equals(valorCelda)) {
                                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4265"));
                                errorAnio = true;
                                return;
                            }
                        }
                    }
                    else
                    {
                        cadena.append("");
                    }
                    if (num >= 10000)
                    {
                        cadena.append("') || TO_CLOB('");
                        num = 0;
                    }
                    cadena.append(SysmanConstantes.SEPARADOR_COL);
                }
                cadena.append(SysmanConstantes.SEPARADOR_REG);
            }

        }
        cadena.append("')"
            + "");
    }

    private void cargarDatos(int opcion)
    {
        if (!errorAnio)
        {

            try
            {
                String parametro = SysmanFunciones.esBdSqlServer()
                    ? cadena.toString().replace("TO_CLOB(", "")
                                    .replace(")", "")
                    : cadena.toString();

                ejbContabilidadSiete.cargarPlanContable(compania, parametro,
                                opcion,
                                SessionUtil.getUser().getCodigo());

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(
                                                "MSM_PROCESO_EJECUTADO"));

            }
            catch (SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }

    }
    
    public boolean validarHojas(Workbook workbook)
    {

    	 String hoja0Esperada = "PLAN CONTABLE";
    	 String hoja1Esperada = "SALDOS INICIALES";

    	 String hoja0Real = workbook.getSheetName(0).trim();
    	 String hoja1Real = workbook.getSheetName(1).trim();

    	 boolean existePlanContable = workbook.getSheet(hoja0Esperada) != null;
    	 boolean existeSaldosIniciales = workbook.getSheet(hoja1Esperada) != null;

    	 if (!existePlanContable) {
    	     JsfUtil.agregarMensajeError("No existe la hoja PLAN CONTABLE.");
    	     return false;
    	 }

    	 if (!existeSaldosIniciales) {
    	     JsfUtil.agregarMensajeError("No existe la hoja SALDOS INICIALES.");
    	     return false;
    	 }

    	 if (!hoja0Esperada.equalsIgnoreCase(hoja0Real) ||
    	     !hoja1Esperada.equalsIgnoreCase(hoja1Real)) {

    	     JsfUtil.agregarMensajeError("El orden de las hojas no corresponde. "
    	             + "La hoja 1 debe ser PLAN CONTABLE y la hoja 2 debe ser SALDOS INICIALES.");
    	     return false;
    	 }
    	 
    	 return true;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control CargarPlan
     *
     *
     */
    public void cambiarCargarPlan()
    {
        cargarSaldo = false;
    }

    /**
     * Metodo ejecutado al cambiar el control CargarSaldo
     *
     *
     */
    public void cambiarCargarSaldo()
    {
        cargarPlan = false;
    }
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    public boolean isCargarPlan()
    {
        return cargarPlan;
    }

    public void setCargarPlan(boolean cargarPlan)
    {
        this.cargarPlan = cargarPlan;
    }

    public boolean isCargarSaldo()
    {
        return cargarSaldo;
    }

    public void setCargarSaldo(boolean cargarSaldo)
    {
        this.cargarSaldo = cargarSaldo;
    }

    /**
     * Retorna la variable anio
     *
     * @return anio
     */
    public String getAnio()
    {
        return anio;
    }

    /**
     * Asigna la variable anio
     *
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    /**
     * Retorna el objeto contArchivocargarExcel
     *
     * @return contArchivocargarExcel
     */
    public ContenedorArchivo getContArchivocargarExcel()
    {
        return contArchivocargarExcel;
    }

    /**
     * Asigna el objeto contArchivocargarExcel
     *
     * @param contArchivocargarExcel
     * Variable a asignar en contArchivocargarExcel
     */
    public void setContArchivocargarExcel(
        ContenedorArchivo contArchivocargarExcel)
    {
        this.contArchivocargarExcel = contArchivocargarExcel;
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
    public List<Registro> getListaAnio()
    {
        return listaAnio;
    }

    /**
     * Asigna la lista listaAnio
     *
     * @param listaAnio
     * Variable a asignar en listaAnio
     */
    public void setListaAnio(List<Registro> listaAnio)
    {
        this.listaAnio = listaAnio;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
