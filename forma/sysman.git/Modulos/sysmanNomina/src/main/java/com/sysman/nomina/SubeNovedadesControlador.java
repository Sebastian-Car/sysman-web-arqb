package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCuatroRemote;
import com.sysman.nomina.enums.SubeNovedadesControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
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
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ngomez
 * @version 1, 14/08/2015
 *
 * @author ybecerra
 * @version 2, 30/10/2017, proceso de Refactoring
 */
@ManagedBean
@ViewScoped

public class SubeNovedadesControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para almacenar el codigo del modulo por la
     * cual se ingresa en la aplicacion
     */
    private final String modulo;
    private final String proceso;
    private final String ano = (String) SessionUtil.getSessionVar("anioNomina");
    private final String mes = (String) SessionUtil.getSessionVar("mesNomina");
    private final String periodo = (String) SessionUtil
                    .getSessionVar("periodoNomina");
    // <DECLARAR_ATRIBUTOS>
    private String opcion;
    private String anioUno;
    private String mesUno;
    private String periodoUno;
    private String concepto;
    private String tipo;
    private String tituloBoton;
    private boolean ejecutado;
    private boolean nsiaue; //jm cc 3810
   

	/**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Este atributo se usa como auxiliar del componente referencia de
     * archivos SelectorArchivo y funciona como contenedor del archivo
     * que se desea cargar
     */
    private UploadedFile archivoCargaSelectorArchivo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;
    private List<Registro> listaMes;
    private List<Registro> listaPeriodo;
    private List<Registro> listaTipo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaConcepto;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbNominaCuatroRemote ejbNominaCuatro;

    /**
     * Crea una nueva instancia de SubeNovedadesControlador
     */
    public SubeNovedadesControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        proceso = (String) SessionUtil.getSessionVar("procesoNomina");
        try {
            // 124
            numFormulario = GeneralCodigoFormaEnum.SUBE_NOVEDADES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(SubeNovedadesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
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
        cargarListaMes();
        cargarListaPeriodo();
        cargarListaTipo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaConcepto();
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
        opcion = "1";
        anioUno = ano;
        cargarListaMes();
        mesUno = mes;
        cargarListaPeriodo();
        periodoUno = periodo;
        tituloBoton = "Crear";
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaAno
     */
    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubeNovedadesControladorUrlEnum.URL5617
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     *
     * Carga la lista listaMes1
     */
    public void cargarListaMes() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioUno);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);

        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubeNovedadesControladorUrlEnum.URL6298
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     *
     * Carga la lista listaPeriodo
     */
    public void cargarListaPeriodo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
        param.put(GeneralParameterEnum.ANO.getName(), anioUno);
        param.put(GeneralParameterEnum.MES.getName(), mesUno);

        try {
            listaPeriodo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubeNovedadesControladorUrlEnum.URL7130
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     *
     * Carga la lista listaTipo
     */
    public void cargarListaTipo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaTipo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubeNovedadesControladorUrlEnum.URL8216
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     *
     * Carga la lista listaConcepto
     */
    public void cargarListaConcepto() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubeNovedadesControladorUrlEnum.URL8948
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaConcepto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.ID_DE_CONCEPTO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton SubirNov en la vista
     *
     */
    public void oprimirSubirNov() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        if ("1".equals(opcion)) {
            // Generar plano
            if (SysmanFunciones.validarVariableVacio(concepto)) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3760"));
                return;
            }
            generarPlano();
        }
        else if ("3".equals(opcion)) {
            // Subir de Excel
            subirExcel();

        }
    }

    /**
     *
     * Metodo invocado al ejecutar el comando remoto Mensaje en la
     * vista
     *
     */
    public void ejecutarMensaje() {
        // <CODIGO_DESARROLLADO>
        if (ejecutado) {
            JsfUtil.agregarMensajeInformativoDialogo(
                            idioma.getString("TB_TB81"));
            ejecutado = false;
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ano1
     *
     */
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        cargarListaMes();
        mesUno = null;
        periodoUno = null;
        listaPeriodo = null;
        cambiarMes();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Mes1
     *
     */
    public void cambiarMes() {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodo();
        periodoUno = null;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control TipoArchivo
     *
     */
    public void cambiarTipoArchivo() {
        // <CODIGO_DESARROLLADO>
        if ("1".equals(opcion)) {
            tituloBoton = "Crear";
        }
        else {
            tituloBoton = "Subir";
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConcepto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConcepto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        concepto = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.ID_DE_CONCEPTO
                                                        .getName()),
                                        "")
                        .toString();
    }
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>

    /**
     *
     */
    public void generarPlano() {
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("concepto", concepto);
            String strSql = Reporteador.resuelveConsulta(
                            "800125GenerarPlanoPersonal",
                            Integer.parseInt(modulo), reemplazar);

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL97);
        }
        catch (SQLException | JRException | IOException
                        | DRException | SysmanException ex) {
            Logger.getLogger(SubeNovedadesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     */
    public void subirExcel() {
        boolean errorConversion;
        try {
            if ("xls".equals(archivoCargaSelectorArchivo.getFileName()
                            .substring(archivoCargaSelectorArchivo.getFileName()
                                            .lastIndexOf('.')
                                + 1))) {

                Workbook workbook = new HSSFWorkbook(
                                archivoCargaSelectorArchivo.getInputstream());
                Sheet sheet = workbook.getSheetAt(0);

                int rowStart = 1;
                int rowEnd = sheet.getLastRowNum();

                for (int rowNum = rowStart; rowNum <= rowEnd; rowNum++) {
                    errorConversion = false;
                    ejecutarFuncionExcel(sheet, rowNum, errorConversion);

                }
                ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
                workbook.write(fileOut);

                archivoDescarga = JsfUtil.getArchivoDescarga(
                                new ByteArrayInputStream(fileOut.toByteArray()),
                                "novedades.xls");
                fileOut.close();
                ejecutado = true;

            }
            else {
                if ("".equals(archivoCargaSelectorArchivo.getFileName())) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1901"));
                }
                else {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2679"));
                }

            }

        }
        catch (JRException | IOException ex) {
            Logger.getLogger(SubeNovedadesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            SysmanFunciones.concatenar(
                                            idioma.getString(
                                                            "MSM_TRANS_INTERRUMPIDA"),
                                            ex.getMessage()));
        }

    }

    /**
     * @param sheet
     * @param rowNum
     * @param errorConversion
     */
    public void ejecutarFuncionExcel(Sheet sheet, int rowNum,
        boolean errorConversion) {
        boolean aux = false;
        int nconcepto = 0;
        int codigo = 0;
        double valor = 0;
        String msg = "";

        Row row = sheet.getRow(rowNum);
        int lastColumn = 5;

        for (int cn = 0; cn <= lastColumn; cn++) {
            Cell cell = row.getCell(cn);
            switch (cn) {
            case 0:
                nconcepto = (cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                    ? (int) cell.getNumericCellValue()
                    : Integer.parseInt(cell.getStringCellValue());
                break;
            case 2:
                valor = cell.getNumericCellValue();
                break;
            case 5:
                codigo = (int) cell.getNumericCellValue();
                break;
            default:
                break;
            }
        }

        if (errorConversion) {
            aux = false;
        }
        else {

            try {
                aux = ejbNominaCuatro.subirNovedadExcel(compania,
                                Integer.parseInt(proceso),
                                Integer.parseInt(anioUno),
                                Integer.parseInt(mesUno), nconcepto, codigo,
                                BigDecimal.valueOf(valor),
                                Integer.parseInt(periodoUno), tipo,
                                SessionUtil.getUser().getCodigo(), nsiaue ? 1 : 0 );

            }
            catch (SystemException | NumberFormatException e) {
            	msg = idioma.getString("MSG_CONCEPTOPERMITENOVEDAD").toString();
            	msg = msg.replace("s$concepto$s",nconcepto + "");
            	msg = msg.replace("s$linea$s",rowNum + "");
                JsfUtil.agregarMensajeError(msg);
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e.getMessage());

            }
        }

        // Para la celda 3
        Cell cell = getOrCreateCell(row, 3);
        
        // Para la celda 6
        Cell cell6 = getOrCreateCell(row, 6);

        if (aux) {
            cell.setCellValue("**SI**");
        }
        else {
            cell.setCellValue("**NO**");
            cell6.setCellValue("LOS CONCEPTOS PARA RETIRADOS, SOLO SE SUBEN EN EL PROCESO NÓMINA RETROACTIVO");
            sheet.autoSizeColumn(6);
            
        }
    }
    
 // Definir la funci�n auxiliar fuera de cualquier otro método
    private Cell getOrCreateCell(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex);
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        return cell;
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
     * Retorna la variable anioUno
     *
     * @return anioUno
     */
    public String getAnioUno() {
        return anioUno;
    }

    /**
     * Asigna la variable anioUno
     *
     * @param anioUno
     * Variable a asignar en anioUno
     */
    public void setAnioUno(String anioUno) {
        this.anioUno = anioUno;
    }

    /**
     * Retorna la variable mesUno
     *
     * @return mesUno
     */
    public String getMesUno() {
        return mesUno;
    }

    /**
     * Asigna la variable mesUno
     *
     * @param mesUno
     * Variable a asignar en mesUno
     */
    public void setMesUno(String mesUno) {
        this.mesUno = mesUno;
    }

    /**
     * Retorna la variable periodoUno
     *
     * @return periodoUno
     */
    public String getPeriodoUno() {
        return periodoUno;
    }

    /**
     * Asigna la variable periodoUno
     *
     * @param periodoUno
     * Variable a asignar en periodoUno
     */
    public void setPeriodoUno(String periodoUno) {
        this.periodoUno = periodoUno;
    }

    /**
     * Retorna la variable tipo
     *
     * @return tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Asigna la variable tipo
     *
     * @param tipo
     * Variable a asignar en tipo
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Retorna la variable concepto
     *
     * @return concepto
     */
    public String getConcepto() {
        return concepto;
    }

    /**
     * Asigna la variable concepto
     *
     * @param concepto
     * Variable a asignar en concepto
     */
    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna la variable ejecutado
     *
     * @return ejecutado
     */
    public boolean isEjecutado() {
        return ejecutado;
    }

    /**
     * Asigna la variable ejecutado
     *
     * @param ejecutado
     * Variable a asignar en ejecutado
     */
    public void setEjecutado(boolean ejecutado) {
        this.ejecutado = ejecutado;
    }

    /**
     * Retorna la variable tituloBoton
     *
     * @return tituloBoton
     */
    public String getTituloBoton() {
        return tituloBoton;
    }

    /**
     * Asigna la variable tituloBoton
     *
     * @param tituloBoton
     * Variable a asignar en tituloBoton
     */
    public void setTituloBoton(String tituloBoton) {
        this.tituloBoton = tituloBoton;
    }

    /**
     * Retorna el objeto contArchivoSelectorArchivo
     *
     * @return contArchivoSelectorArchivo
     */
    public UploadedFile getArchivoCargaSelectorArchivo() {
        return archivoCargaSelectorArchivo;
    }

    /**
     * Asigna el objeto contArchivoSelectorArchivo
     *
     * @param contArchivoSelectorArchivo
     * Variable a asignar en contArchivoSelectorArchivo
     */
    public void setArchivoCargaSelectorArchivo(
        UploadedFile archivoCargaSelectorArchivo) {
        this.archivoCargaSelectorArchivo = archivoCargaSelectorArchivo;
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
     * Retorna la lista listaMes
     *
     * @return listaMes
     */
    public List<Registro> getListaMes() {
        return listaMes;
    }

    /**
     * Asigna la lista listaMes
     *
     * @param listaMes
     * Variable a asignar en listaMes
     */
    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    /**
     * Retorna la lista listaPeriodo
     *
     * @return listaPeriodo
     */
    public List<Registro> getListaPeriodo() {
        return listaPeriodo;
    }

    /**
     * Asigna la lista listaPeriodo
     *
     * @param listaPeriodo
     * Variable a asignar en listaPeriodo
     */
    public void setListaPeriodo(List<Registro> listaPeriodo) {
        this.listaPeriodo = listaPeriodo;
    }

    /**
     * Retorna la lista listaTipo
     *
     * @return listaTipo
     */
    public List<Registro> getListaTipo() {
        return listaTipo;
    }

    /**
     * Asigna la lista listaTipo
     *
     * @param listaTipo
     * Variable a asignar en listaTipo
     */
    public void setListaTipo(List<Registro> listaTipo) {
        this.listaTipo = listaTipo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaConcepto
     *
     * @return listaConcepto
     */
    public RegistroDataModelImpl getListaConcepto() {
        return listaConcepto;
    }

    /**
     * Asigna la lista listaConcepto
     *
     * @param listaConcepto
     * Variable a asignar en listaConcepto
     */
    public void setListaConcepto(RegistroDataModelImpl listaConcepto) {
        this.listaConcepto = listaConcepto;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    
    public boolean isNsiaue() {
		return nsiaue;
	}

	public void setNsiaue(boolean nsiaue) {
		this.nsiaue = nsiaue;
	}

}
