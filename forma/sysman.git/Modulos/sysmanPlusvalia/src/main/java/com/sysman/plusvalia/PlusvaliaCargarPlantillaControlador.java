/*-
 * PlusvaliaCargarPlantillaControlador.java
 *
 * 1.0
 * 
 * 07/02/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plusvalia;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.plusvalia.ejb.EjbPlusvaliaCeroRemote;
import com.sysman.plusvalia.enums.PlusvaliaCargarPlantillaControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

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
import org.primefaces.event.SelectEvent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 07/02/2019
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class PlusvaliaCargarPlantillaControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    private BigInteger proyecto;
    private BigInteger id;
    private String nombreProyecto;
    /**
     * Este atributo se usa como auxiliar del componente selector de
     * archivos cargarPlantilla y funciona como contenedor del archivo
     * que se debe guardar
     */
    private ContenedorArchivo contArchivocargarPlantilla;

    // </DECLARAR_ATRIBUTOS>
    private String extension;
    private StringBuilder Plantilla;

    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaIdProyecto;

    @EJB
    private EjbPlusvaliaCeroRemote ejbPlusvaliaCeroRemote;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de PlusvaliaCargarPlantillaControlador
     */
    public PlusvaliaCargarPlantillaControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = 2031;
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            contArchivocargarPlantilla = new ContenedorArchivo();
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
        cargarListaIdProyecto();
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
     * Carga la lista listaIdProyecto
     *
     */
    public void cargarListaIdProyecto() {

        Map<String, Object> param = new HashMap<>();
        String Clase = "44";

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), Clase);
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaCargarPlantillaControladorUrlEnum.URL1767
                                                        .getValue());

        listaIdProyecto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

        /*
         * listaIdProyecto = new
         * RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
         * ":FR2031_nuevo:TBCB6894","SELECT ID,CODIGO,DESCRIPCION "+
         * " FROM VP_PROYECTOS"+
         * " WHERE COMPANIA = :COMPANIA",true,"ID");
         */
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Cargar en la vista
     *
     *
     */
    public void oprimirCargar() {
        // <CODIGO_DESARROLLADO>
        FileInputStream file = null;
        try {
            if (validarArchivo()) {
                String rutaArchivo = contArchivocargarPlantilla.getArchivo()
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

                Plantilla = new StringBuilder();

                leerHoja(workbook, 0, 34, Plantilla, 2);

                ejbPlusvaliaCeroRemote.plusvaliaCargarPlantilla(
                                Plantilla.toString(), compania,
                                Integer.parseInt(id.toString()),
                                SessionUtil.getUser().getCodigo());

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(
                                                "MSM_PROCESO_EJECUTADO"));
                workbook.close();

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
    }

    public boolean validarArchivo() {

        String archivo = String
                        .valueOf(contArchivocargarPlantilla.getArchivo());
        if (contArchivocargarPlantilla.getArchivo() == null) {
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

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaIdProyecto
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaIdProyecto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        proyecto = (BigInteger) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());

        id = (BigInteger) registroAux.getCampos().get("ID");

        nombreProyecto = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.DESCRIPCION
                                                        .getName()),
                        "").toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna el objeto contArchivocargarPlantilla
     * 
     * @return contArchivocargarPlantilla
     */
    public ContenedorArchivo getContArchivocargarPlantilla() {
        return contArchivocargarPlantilla;
    }

    /**
     * Asigna el objeto contArchivocargarPlantilla
     * 
     * @param contArchivocargarPlantilla
     * Variable a asignar en contArchivocargarPlantilla
     */
    public void setContArchivocargarPlantilla(
        ContenedorArchivo contArchivocargarPlantilla) {
        this.contArchivocargarPlantilla = contArchivocargarPlantilla;
    }
    // </SET_GET_ATRIBUTOS>

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

    /**
     * @return the proyecto
     */
    public BigInteger getProyecto() {
        return proyecto;
    }

    /**
     * @param proyecto
     * the proyecto to set
     */
    public void setProyecto(BigInteger proyecto) {
        this.proyecto = proyecto;
    }

    /**
     * @return the nombreProyecto
     */
    public String getNombreProyecto() {
        return nombreProyecto;
    }

    /**
     * @param nombreProyecto
     * the nombreProyecto to set
     */
    public void setNombreProyecto(String nombreProyecto) {
        this.nombreProyecto = nombreProyecto;
    }

    /**
     * @return the plantilla
     */
    public StringBuilder getPlantilla() {
        return Plantilla;
    }

    /**
     * @param plantilla
     * the plantilla to set
     */
    public void setPlantilla(StringBuilder plantilla) {
        Plantilla = plantilla;
    }

    /**
     * @return the listaIdProyecto
     */
    public RegistroDataModelImpl getListaIdProyecto() {
        return listaIdProyecto;
    }

    /**
     * @param listaIdProyecto
     * the listaIdProyecto to set
     */
    public void setListaIdProyecto(RegistroDataModelImpl listaIdProyecto) {
        this.listaIdProyecto = listaIdProyecto;
    }

    /**
     * @return the id
     */
    public BigInteger getId() {
        return id;
    }

    /**
     * @param id
     * the id to set
     */
    public void setId(BigInteger id) {
        this.id = id;
    }

    // <SET_GET_PARAMETROS>

    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
