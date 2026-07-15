/*-
 * CargarExcedentesControlador.java
 *
 * 1.0
 * 
 * 22/02/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.predial;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialFinRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @version 1.0, 22/02/2017
 * @author eamaya
 * 
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 * 
 * @version 3.0, 23/06/2017, <strong>pespitia</strong>:<br>
 * Refactoring.<br>
 * Manejo de EJBs.
 */
@ManagedBean
@ViewScoped
public class CargarExcedentesControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual el usuario inicio sesion
     */
    private final String modulo;

    /**
     * Constante a nivel de clase que aloja el codigo del usuario que
     * inicio sesion.
     */
    private final String usuario;

    /**
     * Constante a nivel de clase que aloja el numero de orden predial
     */
    private final String numeroOrden;

    // <DECLARAR_ATRIBUTOS>
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
    /**
     * Atributo utilizado para contar el numero de registros
     * ingresados desde la plantilla de Excel a la aplciaci�n
     */
    private int contador;
    /**
     * Atributo en el que se almacenan todos los nombres de los
     * conceptos
     */
    private Registro nombreConcepto;

    /**
     * Atributo que almacena el nombre de la columna seleccionada en
     * el combo
     */
    private Map<String, Object> valorColumna;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     */
    private List<Registro> listacmbHoja;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    // <DECLARAR_EJBs>
    /**
     * Atributo que gestiona las funciones y procedimiento del paquete
     * <code>PCK_PREDIAL_FIN</code>
     */
    @EJB
    private EjbPredialFinRemote ejbPredialFinRemote;

    // </DECLARAR_EJBs>
    /**
     * Crea una nueva instancia de CargarExcedentesControlador
     */
    public CargarExcedentesControlador() {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        usuario = SessionUtil.getUser().getCodigo();
        numeroOrden = SysmanConstantes.NUMERO_ORDEN_PREDIAL;

        try {
            numFormulario = GeneralCodigoFormaEnum.CARGAR_EXCEDENTES_CONTROLADOR
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
        cargarListacmbHoja();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("anio", SysmanFunciones.ano(new Date()));

        String sql = Reporteador.resuelveConsulta("800118PivotConceptos",
                        Integer.parseInt(modulo), reemplazar);

        nombreConcepto = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN, sql);

        valorColumna = new HashMap<>();

        asignarOrigenDatos();
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
     * Carga la lista listacmbHoja
     */
    public void cargarListacmbHoja() {
        // <CODIGO_DESARROLLADO>
        listacmbHoja = new ArrayList<>();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar en la vista
     *
     */
    public void oprimirAceptar() {
        contador = 0;
        Workbook workbook = null;

        String rutaArchivo = contArchivoselectorArchivo.getArchivo()
                        .getPath();

        String extension = rutaArchivo.substring(
                        rutaArchivo.indexOf('.'), rutaArchivo.length());

        try (FileInputStream fileIs = new FileInputStream(
                        contArchivoselectorArchivo.getArchivo());) {

            if (".xlsx".equals(extension)) {
                workbook = new XSSFWorkbook(fileIs);
            }
            else {
                workbook = new HSSFWorkbook(fileIs);
            }

            Sheet sheet = workbook.getSheet(nombreHoja);

            for (Row row : sheet) {
                if (!validarCelda(row.getCell(0))) {
                    break;
                }

                iniciarFuncion(row);
            }

            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString("MSM_PROCESO_EJECUTADO"), " ",
                            idioma.getString("TB_TB2993").replace("#contador#",
                                            Integer.toString(contador))));

            workbook.close();
            fileIs.close();
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Verifica el valor de la celda y retorna false si esta vacia.
     * 
     * @param celda
     * Objeto de tipo <code>Cell</code>
     * @return false si la celda esta vacia.
     */
    private boolean validarCelda(Cell celda) {
        if (celda == null) {
            return false;
        }

        celda.setCellType(Cell.CELL_TYPE_STRING);

        return !celda.getStringCellValue().isEmpty();
    }

    public void iniciarFuncion(Row row) {
        try {
            ejbPredialFinRemote.cargarInformacionExcedentes(compania, usuario,
                            numeroOrden, recuperarValorCelda(0, row),
                            recuperarValorCelda(1, row),
                            recuperarValorCelda(2, row),
                            recuperarValorCelda(3, row),
                            recuperarValorCelda(4, row),
                            recuperarValorCelda(5, row),
                            recuperarValorCelda(6, row),
                            recuperarValorCelda(7, row),
                            recuperarValorCelda(8, row),
                            recuperarValorCelda(9, row),
                            recuperarValorCelda(10, row),
                            recuperarValorCelda(11, row),
                            recuperarValorCelda(12, row),
                            recuperarValorCelda(13, row),
                            recuperarValorCelda(14, row),
                            recuperarValorCelda(15, row),
                            recuperarValorCelda(16, row),
                            recuperarValorCelda(17, row));

            contador = contador + 1;
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Util para obtener el valor de una celda en una fila por su
     * posicion.
     * 
     * @param pos
     * Posicion de la celda.
     * @param row
     * Fila
     * @return El valor de la celda.
     */
    private String recuperarValorCelda(int pos, Row row) {
        Cell cell1 = row.getCell(Integer.valueOf(
                        (String) valorColumna.get(Integer.toString(pos)))
            - 1);

        if (cell1 != null) {
            cell1.setCellType(Cell.CELL_TYPE_STRING);

            return cell1.getStringCellValue();
        }

        return "";
    }

    public void evaluarRepetidos(String valor, String llave) {
        if (valorColumna.containsValue(valor)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2857"));
            return;
        }
        else {
            valorColumna.put(llave, valor);
        }
    }

    /**
     * 
     * Metodo invocado al ejecutar el comando remoto rcActualizarHojas
     * en la vista
     *
     */
    public void ejecutarrcActualizarHojas() {
        Workbook workbook = null;
        nombreHoja = "";

        if (contArchivoselectorArchivo.getArchivo() != null) {

            String rutaArchivo = contArchivoselectorArchivo.getArchivo()
                            .getPath();

            String extension = rutaArchivo.substring(
                            rutaArchivo.indexOf('.'), rutaArchivo.length());

            if (extension == null) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2761"));
            }
            try (FileInputStream fileIs = new FileInputStream(
                            contArchivoselectorArchivo.getArchivo());) {
                if (".xlsx".equals(extension)) {
                    workbook = new XSSFWorkbook(fileIs);
                }
                else {
                    workbook = new HSSFWorkbook(fileIs);
                }
                cargarNombreHojas(workbook);

            }
            catch (IOException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        else

        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1291"));
            return;
        }

    }

    /**
     * Carga la lista de hojas que contien el archivo Excel
     * seleccionado
     *
     * @param workbook
     */
    private void cargarNombreHojas(Workbook workbook) {
        listacmbHoja.clear();

        int hojas = workbook.getNumberOfSheets();

        for (int i = 0; i < hojas; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            String hoja = sheet.getSheetName();
            Registro reg = new Registro();
            reg.getCampos().put("ANO", hoja);
            reg.getCampos();
            listacmbHoja.add(reg);
        }
    }

    // </METODOS_BOTONES>

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>

    }

    @Override
    public void iniciarListasSubNulo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>

    }

    @Override
    public void iniciarListasSub() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>

    }

    @Override
    public void iniciarListas() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>

    }

    @Override
    public void asignarOrigenDatos() {
        origenDatos = "";
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return false;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return false;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return false;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return false;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return false;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return false;
    }

    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control cmbC1
     * 
     * 
     */
    public void cambiarcmbC1() {
        // <CODIGO_DESARROLLADO>
        String valor = (String) valorColumna.get("6");
        valorColumna.remove("6");
        evaluarRepetidos(valor, "6");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control cmbC2
     * 
     * 
     */
    public void cambiarcmbC2() {
        // <CODIGO_DESARROLLADO>
        String valor = (String) valorColumna.get("7");
        valorColumna.remove("7");
        evaluarRepetidos(valor, "7");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control cmbC3
     * 
     * 
     */
    public void cambiarcmbC3() {
        // <CODIGO_DESARROLLADO>
        String valor = (String) valorColumna.get("8");
        valorColumna.remove("8");
        evaluarRepetidos(valor, "8");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control cmbC4
     * 
     * 
     */
    public void cambiarcmbC4() {
        // <CODIGO_DESARROLLADO>
        String valor = (String) valorColumna.get("9");
        valorColumna.remove("9");
        evaluarRepetidos(valor, "9");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control cmbC13
     * 
     * 
     */
    public void cambiarcmbC13() {
        // <CODIGO_DESARROLLADO>
        String valor = (String) valorColumna.get("10");
        valorColumna.remove("10");
        evaluarRepetidos(valor, "10");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control cmbC14
     * 
     * 
     */
    public void cambiarcmbC14() {
        // <CODIGO_DESARROLLADO>
        String valor = (String) valorColumna.get("11");
        valorColumna.remove("11");
        evaluarRepetidos(valor, "11");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control cmbC15
     * 
     * 
     */
    public void cambiarcmbC15() {
        // <CODIGO_DESARROLLADO>
        String valor = (String) valorColumna.get("12");
        valorColumna.remove("12");
        evaluarRepetidos(valor, "12");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control cmbC16
     * 
     * 
     */
    public void cambiarcmbC16() {
        // <CODIGO_DESARROLLADO>
        String valor = (String) valorColumna.get("13");
        valorColumna.remove("13");
        evaluarRepetidos(valor, "13");
        // </CODIGO_DESARROLLADO>
    }

    /** Metodo ejecutado al cambiar el control c18 */
    public void cambiarc18() {
        // <CODIGO_DESARROLLADO>
        String valor = (String) valorColumna.get("14");
        valorColumna.remove("14");
        evaluarRepetidos(valor, "14");
        // </CODIGO_DESARROLLADO>
    }

    /** Metodo ejecutado al cambiar el control c17 */
    public void cambiarc17() {
        // <CODIGO_DESARROLLADO>
        String valor = (String) valorColumna.get("15");
        valorColumna.remove("15");
        evaluarRepetidos(valor, "15");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control cmbC19
     * 
     * 
     */
    public void cambiarcmbC19() {
        // <CODIGO_DESARROLLADO>
        String valor = (String) valorColumna.get("16");
        valorColumna.remove("16");
        evaluarRepetidos(valor, "16");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control cmbC20
     * 
     * 
     */
    public void cambiarcmbC20() {
        // <CODIGO_DESARROLLADO>
        String valor = (String) valorColumna.get("17");
        valorColumna.remove("17");
        evaluarRepetidos(valor, "17");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control cmbFactura
     * 
     * 
     */
    public void cambiarcmbFactura() {
        // <CODIGO_DESARROLLADO>
        String valor = (String) valorColumna.get("0");
        valorColumna.remove("0");
        evaluarRepetidos(valor, "0");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control cmbAno
     * 
     * 
     */
    public void cambiarcmbAno() {
        // <CODIGO_DESARROLLADO>
        String valor = (String) valorColumna.get("2");
        valorColumna.remove("2");
        evaluarRepetidos(valor, "2");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control cmbPredio
     * 
     * 
     */
    public void cambiarcmbPredio() {
        // <CODIGO_DESARROLLADO>
        String valor = (String) valorColumna.get("1");
        valorColumna.remove("1");
        evaluarRepetidos(valor, "1");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control cmbObservaciones
     * 
     * 
     */
    public void cambiarcmbObservaciones() {
        // <CODIGO_DESARROLLADO>
        String valor = (String) valorColumna.get("5");
        valorColumna.remove("5");
        evaluarRepetidos(valor, "5");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control cmbBanco
     * 
     * 
     */
    public void cambiarcmbBanco() {
        // <CODIGO_DESARROLLADO>
        String valor = (String) valorColumna.get("4");
        valorColumna.remove("4");
        evaluarRepetidos(valor, "4");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control cmbAplicaExc
     * 
     * 
     */
    public void cambiarcmbAplicaExc() {
        // <CODIGO_DESARROLLADO>
        String valor = (String) valorColumna.get("3");
        valorColumna.remove("3");
        evaluarRepetidos(valor, "3");
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
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
     * Retorna el objeto contArchivoselectorArchivo
     * 
     * @return contArchivoselectorArchivo
     */
    public ContenedorArchivo getContArchivoselectorArchivo() {
        return contArchivoselectorArchivo;
    }

    /**
     * Asigna el objeto contArchivoselectorArchivo
     * 
     * @param contArchivoselectorArchivo
     * Variable a asignar en contArchivoselectorArchivo
     */
    public void setContArchivoselectorArchivo(
        ContenedorArchivo contArchivoselectorArchivo) {
        this.contArchivoselectorArchivo = contArchivoselectorArchivo;
    }

    public Registro getNombreConcepto() {
        return nombreConcepto;
    }

    public void setNombreConcepto(Registro nombreConcepto) {
        this.nombreConcepto = nombreConcepto;
    }

    public Map<String, Object> getValorColumna() {
        return valorColumna;
    }

    public void setValorColumna(Map<String, Object> valorColumna) {
        this.valorColumna = valorColumna;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listacmbHoja
     * 
     * @return listacmbHoja
     */
    public List<Registro> getListacmbHoja() {
        return listacmbHoja;
    }

    /**
     * Asigna la lista listacmbHoja
     * 
     * @param listacmbHoja
     * Variable a asignar en listacmbHoja
     */
    public void setListacmbHoja(List<Registro> listacmbHoja) {
        this.listacmbHoja = listacmbHoja;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

}
