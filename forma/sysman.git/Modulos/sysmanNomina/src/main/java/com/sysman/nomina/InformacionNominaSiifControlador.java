/*-
 * InformacionNominaSiifControlador.java
 *
 * 1.0
 * 
 * 21/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaOchoRemote;
import com.sysman.nomina.enums.InformacionNominaSiifControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite generar el un excel con informacion de
 * nomina siif.
 *
 * @version 1.0, 21/02/2018
 * @author jreina
 */

@ManagedBean
@ViewScoped
public class InformacionNominaSiifControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private final String consTitulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que contiene el valor asignado al cuadre total en la
     * forma del formulario.
     */
    private boolean cuadreTotal;
    /**
     * Atributo que contiene el valor asignado al anio inicial en la
     * forma del formulario.
     */
    private String anioInicial;
    /**
     * Atributo que contiene el valor asignado al anio final en la
     * forma del formulario.
     */
    private String anioFinal;
    /**
     * Atributo que contiene el valor asignado al mes inicial en la
     * forma del formulario.
     */
    private String mesInicial;
    /**
     * Atributo que contiene el valor asignado al mes final en la
     * forma del formulario.
     */
    private String mesFinal;
    /**
     * Atributo que contiene el valor asignado al periodo inicial en
     * la forma del formulario.
     */
    private String periodoInicial;
    /**
     * Atributo que contiene el valor asignado al periodo final en la
     * forma del formulario.
     */
    private String periodoFinal;
    /**
     * Atributo que contiene el valor asignado al nivel en la forma
     * del formulario.
     */
    private String nivel;
    /**
     * Atributo que contiene el valor asignado a la fecha de solicitud
     * en la forma del formulario.
     */
    private Date fechaSolicitud;
    /**
     * Atributo que contiene el valor asignado al documento de soporte
     * en la forma del formulario.
     */
    private String docSoporte;
    /**
     * Atributo que contiene el valor asignado al numero de documento
     * en la forma del formulario.
     */
    private String numeroDoc;
    /**
     * Atributo que contiene el valor asignado al codigo de expedidor
     * en la forma del formulario.
     */
    private String codExpedidor;
    /**
     * Atributo que contiene el valor asignado a la justificacion en
     * la forma del formulario.
     */
    private String justificacion;

    /**
     * Atributo que contiene el valor asignado al proceso de nomina
     * con el cual se ingreso
     */
    private String varProceso;
    /**
     * Atributo para condicionar el botón de beneficios a empleados
     */
    private boolean verBeneficio;

    private final String modulo;

    private boolean visibleBoton;

    StreamedContent archivoDescarga;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /** Lista que contiene los detalles del combo anio inicial */
    private List<Registro> listaAno1;
    /** Lista que contiene los detalles del combo anio final */
    private List<Registro> listaAno2;
    /** Lista que contiene los detalles del combo mes inicial */
    private List<Registro> listaMes1;
    /** Lista que contiene los detalles del combo mes final */
    private List<Registro> listaMes2;
    /** Lista que contiene los detalles del combo periodo inicial */
    private List<Registro> listaPeriodo1;
    /** Lista que contiene los detalles del combo periodo final */
    private List<Registro> listaPeriodo2;

    @EJB
    private EjbSysmanUtilRemote ejbParametro;

    private ContenedorArchivo contArchivoSeleccionarArchivo;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>

    @EJB
    private EjbNominaOchoRemote ejbNominaOcho;

    /**
     * Crea una nueva instancia de InformacionNominaSiifControlador
     */
    public InformacionNominaSiifControlador() {
        super();
        compania = SessionUtil.getCompania();
        varProceso = (String) SessionUtil.getSessionVar("procesoNomina");
        consTitulo = "CARGA MASIVA NOMINA SIIF7";
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.INFORMACION_NOMINASIIF_CONTROLADOR
                            .getCodigo();
            contArchivoSeleccionarArchivo = new ContenedorArchivo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        } catch (Exception ex) {
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
        cargarListaAno1();
        cargarListaAno2();
        cargarListaMes1();
        cargarListaMes2();
        cargarListaPeriodo1();
        cargarListaPeriodo2();
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
        fechaSolicitud = new Date();
        docSoporte = "19";
        justificacion = "PAGO NÓMINA";
        numeroDoc = "NÓMINA";
        codExpedidor = "11";
        tabla = "";
        anioInicial = String.valueOf(SysmanFunciones.ano(new Date()));
        anioFinal = String.valueOf(SysmanFunciones.ano(new Date()));
        mesInicial = String.valueOf(SysmanFunciones.mes(new Date()));
        mesFinal = String.valueOf(SysmanFunciones.mes(new Date()));
        periodoInicial = (String) SessionUtil.getSessionVar("periodoNomina");
        periodoFinal = (String) SessionUtil.getSessionVar("periodoNomina");
        try {
            verBeneficio = "SI".equals(SysmanFunciones
                            .nvl(ejbParametro.consultarParametro(compania,
                                            "GENERA SIIF SEPARADO PARA BENEFICIOS DE EMPLEADOS",
                                            SessionUtil.getModulo(),
                                            new Date(), true), "NO"));
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
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
    /**
     * 
     * Carga la lista listaAno1
     *
     */
    public void cargarListaAno1() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listaAno1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformacionNominaSiifControladorUrlEnum.URL5999
                                                                            .getValue())
                                            .getUrl(), param));
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaAno2
     *
     */
    public void cargarListaAno2() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listaAno2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformacionNominaSiifControladorUrlEnum.URL5999
                                                                            .getValue())
                                            .getUrl(), param));
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaMes1
     *
     */
    public void cargarListaMes1() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anioInicial);

            listaMes1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformacionNominaSiifControladorUrlEnum.URL7227
                                                                            .getValue())
                                            .getUrl(), param));
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaMes2
     *
     */
    public void cargarListaMes2() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anioFinal);

            listaMes2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformacionNominaSiifControladorUrlEnum.URL7227
                                                                            .getValue())
                                            .getUrl(), param));
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaPeriodo1
     *
     */
    public void cargarListaPeriodo1() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anioInicial);
            param.put(GeneralParameterEnum.MES.getName(), mesInicial);
            param.put("PROCESO", varProceso);

            listaPeriodo1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformacionNominaSiifControladorUrlEnum.URL8815
                                                                            .getValue())
                                            .getUrl(), param));
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaPeriodo2
     *
     */
    public void cargarListaPeriodo2() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anioFinal);
            param.put(GeneralParameterEnum.MES.getName(), mesFinal);
            param.put("PROCESO", varProceso);

            listaPeriodo2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformacionNominaSiifControladorUrlEnum.URL8815
                                                                            .getValue())
                                            .getUrl(), param));
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    public void cambiarAno1() {
        cargarListaMes1();
        cargarListaPeriodo1();
    }

    public void cambiarAno2() {
        cargarListaMes2();
        cargarListaPeriodo2();
    }

    public void cambiarMes1() {
        cargarListaPeriodo1();
    }

    public void cambiarMes2() {
        cargarListaPeriodo2();
    }

    public void cambiarcuadreneto() {
        if (cuadreTotal) {
            visibleBoton = true;
        } else {
            visibleBoton = false;
        }
    }

    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>

    private void generarNominaBeneficio(boolean esBeneficio) {
        archivoDescarga = null;
        if (validarArchivo()) {
            try {
                String datos = ejbNominaOcho.generarInformacionSiif(compania,
                                Integer.parseInt(anioInicial),
                                Integer.parseInt(mesInicial),
                                Integer.parseInt(periodoInicial),
                                Integer.parseInt(anioInicial),
                                Integer.parseInt(mesInicial),
                                Integer.parseInt(periodoFinal),
                                fechaSolicitud,
                                docSoporte,
                                numeroDoc,
                                codExpedidor,
                                justificacion,
                                "1".equals(nivel) ? "'Nivel Administrativo'"
                                    : "2".equals(nivel) ? "'Nivel Operativo'"
                                        : null,
                                (esBeneficio ? -1 : 0));

                descargarArchivo(datos, consTitulo);
            } catch (NumberFormatException | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton EnviarExcel en la vista
     *
     */
    public void oprimirGeneracionNomina() {
        generarNominaBeneficio(false);
    }

    public void oprimirGeneracionBeneficios() {
        generarNominaBeneficio(true);
    }

    public void descargarArchivo(String datos, String nombreArchivo) {

        FileInputStream file = null;
        Workbook workbook = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            archivoDescarga = null;

            String rutaArchivo = contArchivoSeleccionarArchivo.getArchivo()
                            .getPath();

            file = new FileInputStream(new File(rutaArchivo));

            String extension = rutaArchivo.substring(
                            rutaArchivo.indexOf('.'), rutaArchivo.length());

            if (".xlsx".equals(extension)) {
                workbook = new XSSFWorkbook(file);
            } else {
                workbook = new HSSFWorkbook(file);
            }

            Sheet sheet = workbook.getSheet("DatosGenerales");

            String[] hojas = datos.split(",.HOJ.,");
            if (hojas.length > 2) {
                String[] datosGenerales = hojas[0]
                                .split(SysmanConstantes.SEPARADOR_REG);
                asignarValor(datosGenerales, sheet, 0);
                sheet = workbook.getSheet("Conceptos");
                datosGenerales = hojas[1].split(SysmanConstantes.SEPARADOR_REG);
                asignarValor(datosGenerales, sheet, 0);
                sheet = workbook.getSheet("Deducciones");
                datosGenerales = hojas[2].split(SysmanConstantes.SEPARADOR_REG);
                asignarValor(datosGenerales, sheet, 1);

                workbook.write(out);
                out.close();
                workbook.close();
                archivoDescarga = JsfUtil.getArchivoDescarga(
                                new ByteArrayInputStream(out.toByteArray()),
                                SysmanFunciones.concatenar(
                                                nombreArchivo,
                                                ".xls"));
            } else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4003"));

            }

        } catch (NumberFormatException | IOException | JRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        } finally {
            try {
                if (file != null) {
                    file.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    public void asignarValor(String[] datosGenerales, Sheet sheet, int tipo) {
        String[] colum;
        Row row;
        for (int i = 0; i < datosGenerales.length; i++) {
            colum = datosGenerales[i].split(SysmanConstantes.SEPARADOR_COL);
            row = sheet.createRow(i + 1);
            int k = 0;
            for (int j = 0; j < colum.length; j++) {
                Cell nCell = row.createCell(k);
                formatea(nCell, j, colum);
                if (tipo == 1 && j == 1) {
                    k = 3;
                }
                k++;
            }
        }
    }

    private void formatea(Cell cell, int columna, String[] colum) {
        switch (cell.getSheet().getSheetName()) {
        case "DatosGenerales":
            if (columna == 9 || columna == 10 || columna == 11) {
                formatoNumero(cell);
                cell.setCellValue(Double.parseDouble(colum[columna]));
            } else {
                cell.setCellValue(colum[columna]);
            }
            break;
        case "Conceptos":
            if (columna == 3) {
                formatoNumero(cell);
                cell.setCellValue(Double.parseDouble(colum[columna]));
            } else {
                cell.setCellValue(colum[columna]);
            }
            break;
        case "Deducciones":
            if (columna == 2) {
                formatoNumero(cell);
                cell.setCellValue(Double.parseDouble(colum[columna]));
            } else {
                cell.setCellValue(colum[columna]);
            }
            break;
        default:
            break;
        }
    }

    private void formatoNumero(Cell cell) {
        Workbook wb = cell.getSheet().getWorkbook();
        CellStyle style = wb.createCellStyle();
        DataFormat format = wb.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0.00"));
        style.setLocked(false);
        cell.setCellStyle(style);
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton revisardescuentos en la
     * vista
     *
     */
    public void oprimirGeneracionPatronal() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (validarArchivo()) {
            try {
                String datos = ejbNominaOcho.generarInformacionSiifPatronal(
                                compania,
                                Integer.parseInt(anioInicial),
                                Integer.parseInt(mesInicial),
                                Integer.parseInt(periodoInicial),
                                Integer.parseInt(anioInicial),
                                Integer.parseInt(mesInicial),
                                Integer.parseInt(periodoFinal),
                                fechaSolicitud,
                                docSoporte,
                                numeroDoc,
                                codExpedidor,
                                justificacion,
                                "1".equals(nivel) ? "Nivel Administrativo"
                                    : "2".equals(nivel) ? "Nivel Operativo"
                                        : "",
                                SessionUtil.getUser().getCodigo());
                descargarArchivo(datos, consTitulo);
            } catch (NumberFormatException | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton diferenciasneto en la
     * vista
     *
     */
    public void oprimirRevisarDiferencias() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme("32", "800134DiferenciasDevengosDescuentosNetoPeriodo",
                        "DIFERENCIAS_DEVENGOS_DESCUENTOS_NETO");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Comando65 en la vista
     *
     */
    public void oprimirRevisarConceptos8() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme("10", "800132ConceptossinconfigurarSIIF",
                        "CONCEPTOS SIN CONFIGURAR SIIF");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton NETOS en la vista
     *
     */
    public void oprimirCalcularNetos() {
        // <CODIGO_DESARROLLADO>
        try {
            ejbNominaOcho.calcularNetos(compania, Integer.parseInt(varProceso),
                            Integer.parseInt(anioFinal),
                            Integer.parseInt(mesFinal),
                            Integer.parseInt(periodoFinal), cuadreTotal,
                            SessionUtil.getUser().toString());

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2306"));

        } catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton PROVISIONES en la vista
     *
     */
    public void oprimirGeneracionProvisiones() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (validarArchivo()) {
            try {
                String datos = ejbNominaOcho.informacionProvisiones(compania,
                                Integer.parseInt(anioInicial),
                                Integer.parseInt(mesInicial),
                                Integer.parseInt(periodoInicial),
                                Integer.parseInt(anioFinal),
                                Integer.parseInt(mesFinal),
                                Integer.parseInt(periodoFinal),
                                "1".equals(nivel) ? "'Nivel Administrativo'"
                                    : "2".equals(nivel) ? "'Nivel Operativo'"
                                        : null,
                                fechaSolicitud,
                                docSoporte,
                                numeroDoc,
                                codExpedidor,
                                justificacion);

                descargarArchivo(datos, consTitulo);
            } catch (NumberFormatException | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    public boolean validarArchivo() {

        String archivo = String
                        .valueOf(contArchivoSeleccionarArchivo.getArchivo());
        if (contArchivoSeleccionarArchivo.getArchivo() == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4001"));
            return false;
        } else {
            String extension = archivo
                            .substring(archivo.indexOf('.'), archivo.length())
                            .toLowerCase();
            if ((".xlsx".equals(extension)) || (".xls".equals(extension))) {
                return true;
            } else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4002"));
                return false;
            }
        }
    }

    public void generarInforme(String concepto, String informe, String nombre) {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("compania", compania);
        parametros.put("anio", anioFinal);

        if ("32".equals(concepto)) {
            parametros.put("mes", mesFinal);
            parametros.put("periodo", periodoFinal);

        } else {
            parametros.put("concepto", concepto);
        }

        String consulta = Reporteador.resuelveConsulta(informe,
                        Integer.parseInt(SessionUtil.getModulo()),
                        parametros);

        try {
            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(consulta,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL,
                            nombre);
        } catch (JRException | IOException | SQLException | DRException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1719-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * 'formularioAbrir 1, Me.Name 'Me.Caption = NombreEmpresa(0)
         * End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
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
     * 
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
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void ejecutarrcCerrar() {
        SessionUtil.redireccionarMenuFormulario("60506");
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable cuadreTotal
     * 
     * @return cuadreTotal
     */

    public boolean getCuadreTotal() {
        return cuadreTotal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    /**
     * Asigna la variable cuadreTotal
     * 
     * @param cuadreTotal
     * Variable a asignar en cuadreTotal
     */
    public void setCuadreTotal(boolean cuadreTotal) {
        this.cuadreTotal = cuadreTotal;
    }

    /**
     * Retorna la variable anioInicial
     * 
     * @return anioInicial
     */
    public String getAnioInicial() {
        return anioInicial;
    }

    /**
     * Asigna la variable anioInicial
     * 
     * @param anioInicial
     * Variable a asignar en anioInicial
     */
    public void setAnioInicial(String anioInicial) {
        this.anioInicial = anioInicial;
    }

    /**
     * Retorna la variable anioFinal
     * 
     * @return anioFinal
     */
    public String getAnioFinal() {
        return anioFinal;
    }

    /**
     * Asigna la variable anioFinal
     * 
     * @param anioFinal
     * Variable a asignar en anioFinal
     */
    public void setAnioFinal(String anioFinal) {
        this.anioFinal = anioFinal;
    }

    /**
     * Retorna la variable mesInicial
     * 
     * @return mesInicial
     */
    public String getMesInicial() {
        return mesInicial;
    }

    /**
     * Asigna la variable mesInicial
     * 
     * @param mesInicial
     * Variable a asignar en mesInicial
     */
    public void setMesInicial(String mesInicial) {
        this.mesInicial = mesInicial;
    }

    /**
     * Retorna la variable mesFinal
     * 
     * @return mesFinal
     */
    public String getMesFinal() {
        return mesFinal;
    }

    /**
     * Asigna la variable mesFinal
     * 
     * @param mesFinal
     * Variable a asignar en mesFinal
     */
    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
    }

    /**
     * Retorna la variable periodoInicial
     * 
     * @return periodoInicial
     */
    public String getPeriodoInicial() {
        return periodoInicial;
    }

    /**
     * Asigna la variable periodoInicial
     * 
     * @param periodoInicial
     * Variable a asignar en periodoInicial
     */
    public void setPeriodoInicial(String periodoInicial) {
        this.periodoInicial = periodoInicial;
    }

    /**
     * Retorna la variable periodoFinal
     * 
     * @return periodoFinal
     */
    public String getPeriodoFinal() {
        return periodoFinal;
    }

    /**
     * Asigna la variable periodoFinal
     * 
     * @param periodoFinal
     * Variable a asignar en periodoFinal
     */
    public void setPeriodoFinal(String periodoFinal) {
        this.periodoFinal = periodoFinal;
    }

    /**
     * Retorna la variable nivel
     * 
     * @return nivel
     */
    public String getNivel() {
        return nivel;
    }

    /**
     * Asigna la variable nivel
     * 
     * @param nivel
     * Variable a asignar en nivel
     */
    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    /**
     * Retorna la variable fechaSolicitud
     * 
     * @return fechaSolicitud
     */
    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }

    /**
     * Asigna la variable fechaSolicitud
     * 
     * @param fechaSolicitud
     * Variable a asignar en fechaSolicitud
     */
    public void setFechaSolicitud(Date fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    /**
     * Retorna la variable docSoporte
     * 
     * @return docSoporte
     */
    public String getDocSoporte() {
        return docSoporte;
    }

    /**
     * Asigna la variable docSoporte
     * 
     * @param docSoporte
     * Variable a asignar en docSoporte
     */
    public void setDocSoporte(String docSoporte) {
        this.docSoporte = docSoporte;
    }

    /**
     * Retorna la variable numeroDoc
     * 
     * @return numeroDoc
     */
    public String getNumeroDoc() {
        return numeroDoc;
    }

    /**
     * Asigna la variable numeroDoc
     * 
     * @param numeroDoc
     * Variable a asignar en numeroDoc
     */
    public void setNumeroDoc(String numeroDoc) {
        this.numeroDoc = numeroDoc;
    }

    /**
     * Retorna la variable codExpedidor
     * 
     * @return codExpedidor
     */
    public String getCodExpedidor() {
        return codExpedidor;
    }

    /**
     * Asigna la variable codExpedidor
     * 
     * @param codExpedidor
     * Variable a asignar en codExpedidor
     */
    public void setCodExpedidor(String codExpedidor) {
        this.codExpedidor = codExpedidor;
    }

    /**
     * Retorna la variable justificacion
     * 
     * @return justificacion
     */
    public String getJustificacion() {
        return justificacion;
    }

    /**
     * Asigna la variable justificacion
     * 
     * @param justificacion
     * Variable a asignar en justificacion
     */
    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }

    public boolean isVisibleBoton() {
        return visibleBoton;
    }

    public void setVisibleBoton(boolean visibleBoton) {
        this.visibleBoton = visibleBoton;
    }

    public ContenedorArchivo getContArchivoSeleccionarArchivo() {
        return contArchivoSeleccionarArchivo;
    }

    public void setContArchivoSeleccionarArchivo(
        ContenedorArchivo contArchivoSeleccionarArchivo) {
        this.contArchivoSeleccionarArchivo = contArchivoSeleccionarArchivo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAno1
     * 
     * @return listaAno1
     */
    public List<Registro> getListaAno1() {
        return listaAno1;
    }

    /**
     * Asigna la lista listaAno1
     * 
     * @param listaAno1
     * Variable a asignar en listaAno1
     */
    public void setListaAno1(List<Registro> listaAno1) {
        this.listaAno1 = listaAno1;
    }

    /**
     * Retorna la lista listaAno2
     * 
     * @return listaAno2
     */
    public List<Registro> getListaAno2() {
        return listaAno2;
    }

    /**
     * Asigna la lista listaAno2
     * 
     * @param listaAno2
     * Variable a asignar en listaAno2
     */
    public void setListaAno2(List<Registro> listaAno2) {
        this.listaAno2 = listaAno2;
    }

    /**
     * Retorna la lista listaMes1
     * 
     * @return listaMes1
     */
    public List<Registro> getListaMes1() {
        return listaMes1;
    }

    /**
     * Asigna la lista listaMes1
     * 
     * @param listaMes1
     * Variable a asignar en listaMes1
     */
    public void setListaMes1(List<Registro> listaMes1) {
        this.listaMes1 = listaMes1;
    }

    /**
     * Retorna la lista listaMes2
     * 
     * @return listaMes2
     */
    public List<Registro> getListaMes2() {
        return listaMes2;
    }

    /**
     * Asigna la lista listaMes2
     * 
     * @param listaMes2
     * Variable a asignar en listaMes2
     */
    public void setListaMes2(List<Registro> listaMes2) {
        this.listaMes2 = listaMes2;
    }

    /**
     * Retorna la lista listaPeriodo1
     * 
     * @return listaPeriodo1
     */
    public List<Registro> getListaPeriodo1() {
        return listaPeriodo1;
    }

    /**
     * Asigna la lista listaPeriodo1
     * 
     * @param listaPeriodo1
     * Variable a asignar en listaPeriodo1
     */
    public void setListaPeriodo1(List<Registro> listaPeriodo1) {
        this.listaPeriodo1 = listaPeriodo1;
    }

    /**
     * Retorna la lista listaPeriodo2
     * 
     * @return listaPeriodo2
     */
    public List<Registro> getListaPeriodo2() {
        return listaPeriodo2;
    }

    /**
     * Asigna la lista listaPeriodo2
     * 
     * @param listaPeriodo2
     * Variable a asignar en listaPeriodo2
     */
    public void setListaPeriodo2(List<Registro> listaPeriodo2) {
        this.listaPeriodo2 = listaPeriodo2;
    }

    public boolean isVerBeneficio() {
        return verBeneficio;
    }

    public void setVerBeneficio(boolean verBeneficio) {
        this.verBeneficio = verBeneficio;
    }

}
