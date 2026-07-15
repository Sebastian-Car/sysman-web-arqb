package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.enums.ImpcodpostalesControladorEnum;
import com.sysman.predial.enums.ImpcodpostalesControladorUrlEnum;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanFunciones;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author acaceres
 * @version 1, 25/05/2016
 * 
 * @author eamaya
 * @version 2, 13/06/2017 Se cambio el llamado del codigo del
 * formulario
 * 
 * @modifier amonroy
 * @version 3, 06/07/2017 Se realiza el Proceso de Refactoring
 */
@ManagedBean
@ViewScoped
public class ImpcodpostalesControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private int contador;
    private ContenedorArchivo contArchivoArchivoBAse;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of ImpcodpostalesControlador
     */
    public ImpcodpostalesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.IMPCODPOSTALES_CONTROLADOR
                            .getCodigo();
            contador = 0;
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(ImpcodpostalesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            contArchivoArchivoBAse = new ContenedorArchivo();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    public void oprimirCommand1() {
        // <CODIGO_DESARROLLADO>
        if (contArchivoArchivoBAse.getArchivo() != null) {

            String rutaArchivo = contArchivoArchivoBAse.getArchivo()
                            .getPath();

            String extension = rutaArchivo.substring(
                            rutaArchivo.indexOf('.'), rutaArchivo.length());

            if (extension == null) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2761"));
            }

            try (FileInputStream file = new FileInputStream(
                            new File(rutaArchivo));) {

                Workbook workbook = null;

                if (".xlsx".equals(extension)) {
                    workbook = new XSSFWorkbook(file);
                }
                else {
                    workbook = new HSSFWorkbook(file);
                }

                workbook.getCreationHelper().createFormulaEvaluator();

                Sheet sheet = workbook.getSheetAt(0);

                int rowNum = 0;
                for (rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
                    if ((sheet.getRow(rowNum) != null)
                        && (sheet.getRow(rowNum).getCell(0) != null)
                        && sheet.getRow(rowNum).getCell(0,
                                        Row.RETURN_BLANK_AS_NULL) != null) {
                        contador += validarVacios(sheet.getRow(rowNum));
                    }
                }

                workbook.close();

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB3284"));

                contador = 0;
            }

            catch (IOException ex) {
                Logger.getLogger(ImpcodpostalesControlador.class.getName())
                                .log(Level.SEVERE, null, ex);
                JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                                idioma.getString("MSM_TRANS_INTERRUMPIDA"),
                                ex.getMessage()));
            }
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1291"));
            return;
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Verifica que las celdas de la fila no tengan como valor: null,
     * CODIGO, PREDIAL, COD_POSTAL Y CODIGO POSTAL.
     * 
     * @param row
     * Fila que contiene las celdas.
     * @return false, si las celdas contienen algun valor mencionado.
     */
    public int validarVacios(Row row) {
        boolean key = true;
        int registrosAlterados = 0;

        if ((row.getCell(0) != null) && (row.getCell(1) != null)) {
            /* Si el valor es igual a CODIGO retorne false */
            if ("CODIGO".equalsIgnoreCase(row.getCell(0).toString())) {
                key = false;
            }
            if ("PREDIAL".equalsIgnoreCase(row.getCell(0).toString())) {
                key = false;
            }
            if ("PREDIO".equalsIgnoreCase(row.getCell(0).toString())) {
                key = false;
            }
            if ("COD_POSTAL".equalsIgnoreCase(row.getCell(1).toString())) {
                key = false;
            }
            if ("CODIGO POSTAL".equalsIgnoreCase(row.getCell(1).toString())) {
                key = false;
            }

            if (key) {
                Cell cell1 = row.getCell(0);
                cell1.setCellType(Cell.CELL_TYPE_STRING);
                String predio = String.valueOf(
                                cell1.getStringCellValue());

                Cell cell2 = row.getCell(1);
                cell2.setCellType(Cell.CELL_TYPE_STRING);

                String codPostal = String.valueOf(cell2.getStringCellValue());
                registrosAlterados = actualizarCodigo(predio, codPostal);
            }
        }

        return registrosAlterados;
    }

    /**
     * Realiza la actualizacion del codigo postal para un predio en
     * especifico
     * 
     * @param predio
     * Numero del predio a modificar
     * @param codPostal
     * Codigo postal que se va a almacenar
     * @return Cantidad de registros afectados
     */
    public int actualizarCodigo(String predio, String codPostal) {
        int actualizadas = 0;
        UrlBean urlUpdate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ImpcodpostalesControladorUrlEnum.URL7405
                                                        .getValue());

        Map<String, Object> fields = new TreeMap<>();
        fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        fields.put(ImpcodpostalesControladorEnum.CODIGO_POSTAL.getValue(),
                        codPostal);
        fields.put(GeneralParameterEnum.CODIGO.getName(), predio);
        fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                        new Date());
        Parameter parameter = new Parameter();
        parameter.setFields(fields);

        try {
            actualizadas = requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            parameter);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return actualizadas;
    }

    public void oprimirSalir() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public ContenedorArchivo getContArchivoArchivoBAse() {
        return contArchivoArchivoBAse;
    }

    public int getContador() {
        return contador;
    }

    public void setContador(int contador) {
        this.contador = contador;
    }

    public void setContArchivoArchivoBAse(
        ContenedorArchivo contArchivoArchivoBAse) {
        this.contArchivoArchivoBAse = contArchivoArchivoBAse;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
