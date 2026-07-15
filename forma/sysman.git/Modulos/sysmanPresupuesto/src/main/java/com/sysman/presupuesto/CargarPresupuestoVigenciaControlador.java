/*-
 * CargarPresupuestoVigenciaControlador.java
 *
 * 1.0
 * 
 * 22/11/2018
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
import com.sysman.jsfutil.ArchivosBean;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.ejb.EjbPresupuestoTresRemote;
import com.sysman.presupuesto.enums.CargarPresupuestoVigenciaControladorUrlEnum;
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
import java.util.logging.Level;
import java.util.logging.Logger;

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

/**
 *
 * @version 1.0, 22/11/2018
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class CargarPresupuestoVigenciaControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>

    private boolean cargarPlan;
    private boolean cargarApropiacion;
    private int anio;
    private String extension;
    private StringBuilder planApropiacion;
    private final StringBuilder clob;
    private int opcion;
    private boolean errorAnio;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * lista donde se carga el año
     */
    private List<Registro> listaAno;

    /**
     * Este atributo se usa como auxiliar del componente selector de
     * archivos cargarPrueba y funciona como contenedor del archivo
     * que se debe guardar
     */

    private ContenedorArchivo contArchivocargarExcel;

    @EJB
    private EjbPresupuestoTresRemote ejbPresupuestoTresRemote;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * CargarPresupuestoVigenciaControlador
     */
    public CargarPresupuestoVigenciaControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        clob = new StringBuilder();

        try {
            numFormulario = 1992;
            validarPermisos();
            // <INI_ADICIONAL>
            anio = SysmanFunciones.ano(new Date());

            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            contArchivocargarExcel = new ContenedorArchivo();
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
        cargarListaAno();
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
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            setListaAno(RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CargarPresupuestoVigenciaControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), param)));
        }
        catch (SystemException e) {
            Logger.getLogger(SiifReportesControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public boolean validarArchivo() {

        String archivo = String.valueOf(contArchivocargarExcel.getArchivo());
        if (contArchivocargarExcel.getArchivo() == null) {
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

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Cargar en la vista donde
     * ejecuta procedimiento
     *
     */
    public void oprimirCargar() {
        // <CODIGO_DESARROLLADO>
        errorAnio = false;
        FileInputStream file = null;
        try {
            if (validarArchivo()) {
                String rutaArchivo = contArchivocargarExcel.getArchivo()
                                .getPath();
                extension = rutaArchivo
                                .substring(rutaArchivo.indexOf('.'),
                                                rutaArchivo.length())
                                .substring(1, rutaArchivo.substring(
                                                rutaArchivo.indexOf('.'),
                                                rutaArchivo.length()).length());
                file = new FileInputStream(new File(rutaArchivo));
                Workbook workbook = null;

                if (workbook == null) {
                    if ("xls".equals(extension)) {
                        workbook = new HSSFWorkbook(file);
                    }
                    else {
                        workbook = new XSSFWorkbook(file);
                    }
                }

                planApropiacion = new StringBuilder();
                if (cargarPlan && cargarApropiacion) {
                    leerHoja(workbook, 0, 16, planApropiacion, 1);
                    if (cargarPlanVigencia(1)) {
                        planApropiacion.setLength(0);
                        leerHoja(workbook, 1, 12, planApropiacion, 1);
                        cargarPlanVigencia(2);
                    }
                }
                else {
                    if (cargarPlan) {
                        opcion = 1;
                        leerHoja(workbook, 0, 16, planApropiacion, 1);
                        cargarPlanVigencia(opcion);
                    }
                    else if (cargarApropiacion) {
                        opcion = 2;
                        leerHoja(workbook, 1, 13, planApropiacion, 1);
                        cargarPlanVigencia(opcion);
                    }
                }

                workbook.close();

            }
        }
        catch (IOException | NumberFormatException e) {
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
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>PCK_CGR.FC_ACTUALIZAR_CLASIFICADOR_DETALLE
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
	private boolean cargarPlanVigencia(int opcion) {
		boolean existoso = false;
		if (!errorAnio) {
			try {
				String parametro = (SysmanFunciones.esBdSqlServer())
						? planApropiacion.toString().replace("TO_CLOB(", "").replace(")", "")
						: planApropiacion.toString();

				String variable = ejbPresupuestoTresRemote.cargarPlanVigencia(parametro,
						SessionUtil.getUser().getCodigo(), opcion);		

					existoso = true;					
					ArchivosBean.generarPlano("Novedades Cargar Plan.txt", variable);		
					JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));	
			}

			catch (SystemException | IOException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}

		}
		return existoso;
		
	}
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    public void leerHoja(Workbook workbook, int hoja, int columnas,
        StringBuilder cadena, int filainicial) {
        cadena.append("TO_CLOB('");
        Sheet sheet = workbook.getSheetAt(hoja);
        Row fila;
        Cell celda;
        int num = 0;
        for (int i = filainicial; i < sheet.getLastRowNum() + 1; i++) {
            fila = sheet.getRow(i);
            for (int j = 0; j < columnas; j++) {
                celda = fila.getCell(j);
                if (celda != null) {
                    num = num
                        + (celda.getCellType() == 1 ? celda.getStringCellValue()
                                        .replaceFirst("'", " ").length()
                            : NumberToTextConverter
                                            .toText(celda.getNumericCellValue())
                                            .length());
                    cadena.append(celda.getCellType() == 1
                        ? celda.getStringCellValue().replaceFirst("'", " ")
                        : NumberToTextConverter
                                        .toText(celda.getNumericCellValue()));
                    
                    if ( j == 1 && anio != celda.getNumericCellValue()) {
                        JsfUtil.agregarMensajeAlerta(
                                        idioma.getString("TB_TB4265"));
                        errorAnio = true;
                        return;
                    }
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
        cadena.append("')"
            + "");
    }

    /**
     * @return the cargarPlan
     */
    public boolean isCargarPlan() {
        return cargarPlan;
    }

    /**
     * @param cargarPlan
     * the cargarPlan to set
     */
    public void setCargarPlan(boolean cargarPlan) {
        this.cargarPlan = cargarPlan;
    }

    /**
     * @return the cargarApropiacion
     */
    public boolean isCargarApropiacion() {
        return cargarApropiacion;
    }

    /**
     * @param cargarApropiacion
     * the cargarApropiacion to set
     */
    public void setCargarApropiacion(boolean cargarApropiacion) {
        this.cargarApropiacion = cargarApropiacion;
    }

    /**
     * Retorna la variable anio
     * 
     * @return anio
     */
    public int getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(int anio) {
        this.anio = anio;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    /**
     * @return the contArchivocargarExcel
     */
    public ContenedorArchivo getContArchivocargarExcel() {
        return contArchivocargarExcel;
    }

    /**
     * @param contArchivocargarExcel
     * the contArchivocargarExcel to set
     */
    public void setContArchivocargarExcel(
        ContenedorArchivo contArchivocargarExcel) {
        this.contArchivocargarExcel = contArchivocargarExcel;
    }

    /**
     * @return the extension
     */
    public String getExtension() {
        return extension;
    }

    /**
     * @param extension
     * the extension to set
     */
    public void setExtension(String extension) {
        this.extension = extension;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
