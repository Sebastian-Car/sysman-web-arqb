/*-
 * SubirSaldosInicialesControlador.java
 *
 * 1.0
 * 
 * 25/11/2016
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadTresRemote;
import com.sysman.contabilidad.enums.SubirSaldosInicialesControladorEnum;
import com.sysman.contabilidad.enums.SubirSaldosInicialesControladorUrlEnum;
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
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
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
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Controlador de los eventos asociados a la vista
 * subirsaldosiniciales del formulario 1227.
 *
 * @version 1.0, 25/11/2016
 * @author pespitia
 * 
 * author jlramirez
 * @version 2, 10/04/2017, proceso de Refactoring y modificaciones
 * segun especificaciones de SONARLINT
 * @version 3, 20/04/2017, Manejo EJBs
 * 
 */
@ManagedBean
@ViewScoped
public class SubirSaldosInicialesControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del usuario que
     * inicio sesion
     */
    private final String usuario;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo que contiene el valor del anio del cual se van a
     * obtener los saldos iniciales NIIF.
     */
    private String anio;

    /**
     * Atributo que contiene el valor del anio en el que se van a
     * replicar los saldos iniciales NIIF.
     */
    private String anioInsertar;

    /**
     * Atributo que contiene la compania equivalente NIIF.
     */
    private String companiaNiif;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /** Lista que contiene los items del combo Anio. */
    private List<Registro> listaAno;

    /** Lista que contiene los items del combo Anio insertar */
    private List<Registro> listaAnoInsertar;

    @EJB
    private EjbSysmanUtilRemote sysmanUtil;

    @EJB
    private EjbContabilidadTresRemote contabilidadTres;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de SubirSaldosInicialesControlador
     */
    public SubirSaldosInicialesControlador() {
        super();

        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser().getCodigo();

        try {
            numFormulario = GeneralCodigoFormaEnum.SUBIR_SALDOS_INICIALES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    public void extraerCompaniaNiif() {
        try {
            companiaNiif = sysmanUtil.consultarParametro(compania,
                            idioma.getString("TB_TB2444"),
                            SessionUtil.getModulo(), new Date(), true);
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("TB_TB2445"), " ",
                            ex.getMessage()));
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
        cargarListaAnoInsertar();
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
        /*- Recuperar el codigo de la compania NIIF antes de abrir el formulario. */
        extraerCompaniaNiif();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>

    /** Carga los items del combo anio. */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubirSaldosInicialesControladorUrlEnum.URL5740
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /** Carga los items del combo anio a insertar */
    public void cargarListaAnoInsertar() {
        listaAnoInsertar = listaAno;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton XBRL en la vista. Se
     * encarga de subir los saldos iniciales a NIIF del anio al
     * anioInsertar.
     */
    public void oprimirXBRL() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        try {
            /* Ajusta las cuentas del plan_contable */
            contabilidadTres.subirSaldosInicialesNiif(compania, companiaNiif,
                            Integer.parseInt(anio),
                            Integer.parseInt(anioInsertar), false, usuario);

            /* Si no hay inconsistencias */
            if (!generarInfomePlano()) {

                /* Adiciona los saldos iniciales al nuevo anio */
                contabilidadTres.subirSaldosInicialesNiif(compania,
                                companiaNiif,
                                Integer.parseInt(anio),
                                Integer.parseInt(anioInsertar), true, usuario);

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1323"));
            }
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Verifica si la compania equivalente existe en la tabla
     * COMPANIA.
     */
    public boolean verificarCompania() {
        boolean res = false;
        Registro registro;

        HashMap<String, Object> param = new HashMap<>();
        param.put(SubirSaldosInicialesControladorEnum.COMPANIANIIF.getValue(),
                        companiaNiif);

        try {
            registro = RegistroConverter.toRegistro(
                            requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SubirSaldosInicialesControladorUrlEnum.URL6071
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            res = registro == null ? false : true;
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return res;
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista.
     * Gestiona para que se genere el reporte en formato EXCEL.
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarExcel();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que genera un archivo plano con formato xls. Presenta
     * los saldos iniciales que se van a adicionar en el anio a
     * insertar.
     */
    public void generarExcel() {
        try {
            HashMap<String, Object> reemplazos = new HashMap<>();
            reemplazos.put("anio", anio);
            reemplazos.put("anioInsertar", anioInsertar);
            reemplazos.put("companiaNiif", "'" + companiaNiif + "'");

            /* Reemplaza las variables en la consulta de la bd */
            String consulta = Reporteador
                            .resuelveConsulta("800074Saldosinicialesniif",
                                            Integer.parseInt(
                                                            SessionUtil.getModulo()),
                                            reemplazos);

            Workbook workbook = new HSSFWorkbook(
                            JsfUtil.exportarHojaDatosStreamed(consulta,
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            FORMATOS.EXCEL97).getStream());

            Sheet sheet = workbook.getSheet("Report");
            sheet.shiftRows(0, sheet.getLastRowNum(), 4);

            int nColumnas = Math.max(sheet.getRow(4).getLastCellNum(), 0)
                - 1;

            CellReference celdaTitulo1Ini = new CellReference(0, 0);
            String titulo1Ini = celdaTitulo1Ini.formatAsString();
            CellReference celdaTitulo1Fin = new CellReference(0, nColumnas);
            String titulo1Fin = celdaTitulo1Fin.formatAsString();

            CellRangeAddress region = CellRangeAddress
                            .valueOf("" + titulo1Ini + ":" + titulo1Fin);

            sheet.addMergedRegion(region);

            /* Propiedades letra encabezado */
            Font font = workbook.createFont();
            font.setFontName("Calibri");
            font.setBold(true);

            // Tamaño de letra
            font.setFontHeightInPoints((short) 8);

            /* Estilo encabezado */
            CellStyle style = workbook.createCellStyle();
            style.setAlignment(CellStyle.ALIGN_LEFT);
            style.setFont(font);

            /* Titulo 1 */
            Cell cell1 = sheet.createRow(0).createCell(0);
            cell1.setCellValue("ENTIDAD : "
                + SessionUtil.getCompaniaIngreso().getNombre());
            cell1.setCellStyle(style);

            /* Titulo 2 */
            Cell cell2 = sheet.createRow(1).createCell(0);
            cell2.setCellValue("INFORME : SALDOS INICIALES PARA NIIF");
            cell2.setCellStyle(style);

            String fecha = SysmanFunciones.convertirAFechaCadena(
                            new Date(),
                            "MMMM' De 'YYYY");

            /* Titulo 3 */
            Cell cell3 = sheet.createRow(2).createCell(0);
            cell3.setCellValue("FECHA DE GENERACIÓN INFORME : "
                + fecha.toUpperCase());

            cell3.setCellStyle(style);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            outputStream.close();

            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(
                                            outputStream.toByteArray()),
                            "SALDOS INICIALES PARA NIIF.xls");
            workbook.close();
        }
        catch (JRException | IOException | DRException | ParseException
                        | SysmanException | SQLException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que genera un archivo plano con formato txt. Muestra las
     * incosistencias de subir los saldos iniciales.
     */
    public boolean generarInfomePlano() {
        /* Contiene el nombre del informe. */
        String nombre = idioma.getString("TB_TB2468");

        /* Formato del saldo13. */
        DecimalFormat df = new DecimalFormat("#,###.00");

        /* Contenido inicial del archivo plano. */
        StringBuilder contenido = new StringBuilder();
        contenido.append(idioma.getString("TB_TB2469")).append("\r\n\r\n");

        /* Advertencia */
        contenido.append(idioma.getString("TB_TB2722")).append("\r\n");

        /*- Cantidad de veces que imprime una linea en el txt, inconsistencias */
        int cont = 0;

        /* Cuentas que no tienen configurado el codigo NIIF */
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        try {
            List<Registro> list = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubirSaldosInicialesControladorUrlEnum.URL15010
                                                                            .getValue())
                                            .getUrl(), param));

            for (Registro r : list) {
                /* Codigo del registro */
                String codigo = SysmanFunciones.nvl(r.getCampos()
                                .get(GeneralParameterEnum.CODIGO.getName()),
                                " ")
                                .toString();

                String saldo = SysmanFunciones
                                .nvl(r.getCampos().get("SALDO13"), " ")
                                .toString();
                double saldo13 = Double.parseDouble(saldo);

                String valor = SysmanFunciones
                                .nvl(r.getCampos().get("VALOR"), " ")
                                .toString();

                /* Saldo13 diferente de 0 */
                boolean cond = !"0".equals(saldo) || !"0.0".equals(saldo);

                /* Existe detalle para el codigo */
                if (!"0".equals(valor) && cond) {
                    cont++;
                    contenido.append(idioma.getString("TB_TB2513")
                                    .replace("#VAR1#",
                                                    SysmanFunciones.padr(codigo,
                                                                    15, " "))
                                    .replace("#VAR2#",
                                                    SysmanFunciones.padl(
                                                                    df.format(saldo13
                                                                        + Double.parseDouble(
                                                                                        valor)),
                                                                    15, " ")));
                }
                else if (cond) { // no tiene detalle
                    cont++;
                    contenido.append(idioma.getString("TB_TB2513")
                                    .replace("#VAR1#",
                                                    SysmanFunciones.padr(codigo,
                                                                    18, " "))
                                    .replace("#VAR2#",
                                                    SysmanFunciones.padl(
                                                                    df.format(saldo13),
                                                                    20, " ")));
                }
            }

            /*-Planes contables que tienen codigo niif que no se encuentra en la compania equivalente.*/
            Map<String, Object> param2 = new TreeMap<>();
            param2.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param2.put(GeneralParameterEnum.ANO.getName(), anio);
            param2.put(SubirSaldosInicialesControladorEnum.COMPANIANIIF
                            .getValue(), companiaNiif);

            list = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubirSaldosInicialesControladorUrlEnum.URL18707
                                                                            .getValue())
                                            .getUrl(), param2));

            for (Registro r : list) {
                String codigo = SysmanFunciones.nvl(r.getCampos()
                                .get(GeneralParameterEnum.CODIGO.getName()),
                                " ")
                                .toString();

                String niif = SysmanFunciones
                                .nvl(r.getCampos().get("CODIGO_NIIF"), " ")
                                .toString();

                cont++;

                contenido.append(idioma.getString("TB_TB2519")
                                .replace("#CUENTA#",
                                                SysmanFunciones.padr(codigo, 18,
                                                                " "))
                                .replace("#NIIF#", niif));
            }

            if (cont > 0) {
                JsfUtil.agregarMensajeAlerta(idioma
                                .getString("TB_TB2714")
                                .replace("#INCONSISTENCIA#",
                                                String.valueOf(cont))
                    + idioma.getString("TB_TB2468"));

                archivoDescarga = JsfUtil.getArchivoDescarga(
                                JsfUtil.serializarPlano(contenido.toString()),
                                nombre);

                return true;
            }

        }
        catch (JRException | IOException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return false;
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
     * Retorna la variable companiaNiif
     * 
     * @return companiaNiif
     */
    public String getCompaniaNiif() {
        return companiaNiif;
    }

    /**
     * Asigna la variable companiaNiif
     * 
     * @param companiaNiif
     * Variable a asignar en companiaNiif
     */
    public void setCompaniaNiif(String companiaNiif) {
        this.companiaNiif = companiaNiif;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getAnioInsertar() {
        return anioInsertar;
    }

    public void setAnioInsertar(String anioInsertar) {
        this.anioInsertar = anioInsertar;
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
     * Retorna la lista listaAnoInsertar
     * 
     * @return listaAnoInsertar
     */
    public List<Registro> getListaAnoInsertar() {
        return listaAnoInsertar;
    }

    /**
     * Asigna la lista listaAnoInsertar
     * 
     * @param listaAnoInsertar
     * Variable a asignar en listaAnoInsertar
     */
    public void setListaAnoInsertar(List<Registro> listaAnoInsertar) {
        this.listaAnoInsertar = listaAnoInsertar;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
