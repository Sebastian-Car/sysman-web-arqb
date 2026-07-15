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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaBancosRemote;
import com.sysman.nomina.ejb.EjbNominaUnoRemote;
import com.sysman.nomina.enums.MediosMagneticosDIANControladorEnum;
import com.sysman.nomina.enums.MediosMagneticosDIANControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
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
import javax.naming.NamingException;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author sdaza
 * @version 1, 09/09/2015
 *
 * Revision Sonar
 * @author ybecerra
 * @version 2, 17/03/2017
 *
 * Se eliminaron parametros de ActionEvent ac llamados en los metodos
 * de los botones
 * @author ybecerra
 * @version 3, 23/03/2017
 * 
 * @author eamaya
 * @version 3.1, 13/10/2017, Proceso de Refactoring DSS,Manejo de EJBs
 * y cambio de numero de formulario por enum
 */
@ManagedBean
@ViewScoped

public class MediosMagneticosDIANControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante definida para almacenar el codigo del modulo por el
     * cual se ingreso en la aplicacion
     */
    private final String modulo;

    /**
     * Constante definida para almacenar la cadena "PR_ENCABEZADO"
     */
    private final String cPrEncabezado;

    /**
     * Constante definida para almacenar la cadena "PR_STRSQL"
     */
    private final String cPrSql;
    /**
     * Constante definida para almacenar la cadena "PR_NOMBREEMPRESA"
     */
    private final String cPrNombreEmpresa;
    /**
     * Constante definida para almacenar la cadena "idProceso"
     */
    private final String cIdProceso;
    /**
     * Constante definida para almacenar la cadena "rangoInicial"
     */
    private final String cRangoInicial;

    /**
     * Constante definida para almacenar la cadena "rangoFinal"
     */
    private final String cRangoFinal;
    /**
     * Constante definida para almacenar la cadena "Entre : "
     */
    private final String cEntre;
    /**
     * Constante definida para almacenar la cadena "PERIODO"
     */
    private final String cPeriodo;
    /**
     * Constante definida para almacenar la cadena "NOMPERIODO"
     */
    private final String cNomPeriodo;

    private final String procesoNomina;
    private final String anoNomina;
    private final String mesNomina;
    private final String periodoNomina;
    // <DECLARAR_ATRIBUTOS>
    private String opcion;
    private String idProceso;
    private String ano1;
    private String mes1;
    private String periodo1;
    private String ano2;
    private String mes2;
    private String periodo2;
    private String topeIngresos;
    private String topeIngresosC;
    private String rutaModelo;
    private String rutaArchivo;
    private Date fechaElaboracion;
    private String rangoInicial;
    private String rangoFinal;
    /**
     * Variable general que sirve para armar el excel
     */
    private int fila;

    /**
     * Variable general que almacena la hoja para armar el excel
     */
    private HSSFSheet sheet;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaProceso;
    private List<Registro> listaAno1;
    private List<Registro> listaMes1;
    private List<Registro> listaPeriodo1;
    private List<Registro> listaAno2;
    private List<Registro> listaMes2;
    private List<Registro> listaPeriodo2;
    // </DECLARAR_LISTAS>

    private StreamedContent archivoDescarga;
    private ContenedorArchivo contArchivosaDian;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbNominaUnoRemote ejbNominaUno;

    @EJB
    private EjbNominaBancosRemote ejbNominaBanco;

    /**
     * Creates a new instance of MediosMagneticosDIANControlador
     */
    public MediosMagneticosDIANControlador() {
        super();
        contArchivosaDian = new ContenedorArchivo();
        numFormulario = GeneralCodigoFormaEnum.MEDIOS_MAGNETICOS_DIANCONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        procesoNomina = (String) SessionUtil.getSessionVar("procesoNomina");
        anoNomina = (String) SessionUtil.getSessionVar("anioNomina");
        mesNomina = (String) SessionUtil.getSessionVar("mesNomina");
        periodoNomina = (String) SessionUtil.getSessionVar("periodoNomina");
        cPrEncabezado = "PR_ENCABEZADO";
        cPrSql = "PR_STRSQL";
        cPrNombreEmpresa = "PR_NOMBREEMPRESA";
        cIdProceso = "idProceso";
        cRangoInicial = "rangoInicial";
        cRangoFinal = "rangoFinal";
        cEntre = "Entre : ";
        cPeriodo = "PERIODO";
        cNomPeriodo = "NOMPERIODO";
        try {
            validarPermisos();
        }
        catch (Exception ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(MediosMagneticosDIANControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaProceso();
        cargarListaAno1();
        cargarListaMes1();
        cargarListaPeriodo1();
        cargarListaAno2();
        cargarListaMes2();
        cargarListaPeriodo2();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        ConectorPool con = new ConectorPool();
        try {

            con.conectar(ConectorPool.ESQUEMA_SYSMAN);
            idProceso = procesoNomina;
            cargarListaAno1();
            cargarListaAno2();
            ano1 = ano2 = anoNomina;
            cargarListaMes1();
            cargarListaMes2();
            mes1 = mes2 = mesNomina;
            cargarListaPeriodo1();
            cargarListaPeriodo2();
            periodo1 = periodo2 = periodoNomina;
            Date fechaParametros = SysmanFunciones.ultimoDiaDate(SysmanFunciones
                            .convertirAFecha("01/" + mesNomina
                                + "/" + anoNomina));
            topeIngresos = ejbSysmanUtil.consultarParametro(compania,
                            "VALOR INGRESOS SUPERIORES PARA CERTIFICADO",
                            modulo, fechaParametros, false);

            topeIngresosC = topeIngresos;
            DecimalFormat formato = new DecimalFormat("$#,###.###");
            topeIngresos = formato.format(Double.parseDouble(topeIngresos));
            fechaElaboracion = new Date();
            con.getConection().close();
        }
        catch (ParseException | NamingException | SQLException
                        | SystemException ex) {
            Logger.getLogger(MediosMagneticosDIANControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public void cargarListaProceso() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaProceso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MediosMagneticosDIANControladorUrlEnum.URL11062
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAno1() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(MediosMagneticosDIANControladorEnum.IDPROCESO.getValue(),
                        idProceso);

        try {
            listaAno1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MediosMagneticosDIANControladorUrlEnum.URL13125
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes1() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(MediosMagneticosDIANControladorEnum.ID_PROCESO.getValue(),
                        idProceso);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano1);

        try {
            listaMes1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MediosMagneticosDIANControladorUrlEnum.URL11537
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPeriodo1() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(MediosMagneticosDIANControladorEnum.PROCESO.getValue(),
                        idProceso);

        param.put(GeneralParameterEnum.ANO.getName(), ano1);

        param.put(GeneralParameterEnum.MES.getName(), mes1);

        try {
            listaPeriodo1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MediosMagneticosDIANControladorUrlEnum.URL12121
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAno2() {
        listaAno2 = listaAno1;
    }

    public void cargarListaMes2() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(MediosMagneticosDIANControladorEnum.ID_PROCESO.getValue(),
                        idProceso);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano2);

        try {
            listaMes2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MediosMagneticosDIANControladorUrlEnum.URL13652
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPeriodo2() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(MediosMagneticosDIANControladorEnum.PROCESO.getValue(),
                        idProceso);

        param.put(GeneralParameterEnum.ANO.getName(), ano2);

        param.put(GeneralParameterEnum.MES.getName(), mes2);

        try {
            listaPeriodo2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MediosMagneticosDIANControladorUrlEnum.URL131313
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (opcion == null) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB2622"));
            return;
        }
        if (!validarDatosEntrada()) {
            return;
        }

        if ("1".equals(opcion)) {
            genReportesAcumulados(FORMATOS.PDF);
        }
        if ("2".equals(opcion)) {
            genReportesSeguridadSocial(FORMATOS.PDF);
        }
        if ("4".equals(opcion)) {
            genReportesPagosCesantias(FORMATOS.PDF);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimiracumuladosAno() {
        archivoDescarga = null;
        HashMap<String, Object> reemplazar = new HashMap<>();
        try {
            // <CODIGO_DESARROLLADO>
            String condicionPivot = ejbNominaUno.getPrepararPivotDevengosAnio(
                            compania, Integer.parseInt(anoNomina));

            if (condicionPivot == null) {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TB_TB2628")
                                    + anoNomina);
                return;
            }
            reemplazar.put("anoNomina", anoNomina);
            reemplazar.put("condicionPivot", condicionPivot);
            String sql = Reporteador.resuelveConsulta("800020AcumuladosAno",
                            Integer.parseInt(modulo), reemplazar);

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL97);
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException | NumberFormatException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimiracumuladosMes() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        HashMap<String, Object> reemplazar = new HashMap<>();

        try {
            // <CODIGO_DESARROLLADO>
            String condicionPivot = ejbNominaUno.getPrepararPivotDevengosAnio(
                            compania, Integer.parseInt(anoNomina));

            if (condicionPivot == null) {
                JsfUtil.agregarMensajeAlerta(SysmanFunciones.concatenar(
                                idioma.getString("TB_TB2628"), " ",
                                anoNomina, ", mes ", mesNomina));
                return;
            }
            reemplazar.put("ano", anoNomina);
            reemplazar.put("mes", mesNomina);
            reemplazar.put("condicionPivot", condicionPivot);
            String sql = Reporteador.resuelveConsulta("800021AcumuladosMes",
                            Integer.parseInt(modulo), reemplazar);

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL97);
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException | NumberFormatException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    private boolean validarArchivo() {
        if (contArchivosaDian.getArchivo() == null) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB1291"));
            return true;
        }
        return false;
    }

    public void oprimir4704P01F14() {
        archivoDescarga = null;

        fila = 1;

        if (!validarDatosEntrada() || validarArchivo()) {
            return;
        }

        ConectorPool con = new ConectorPool();

        try (FileInputStream file = new FileInputStream(
                        contArchivosaDian.getArchivo());) {
            con.conectar(ConectorPool.ESQUEMA_SYSMAN);
            HSSFWorkbook workbook = new HSSFWorkbook(file);
            sheet = workbook.getSheetAt(0);

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            param.put("ANO1", ano1);
            param.put("MES1", mes1);
            param.put("PERIODO1", periodo1);
            param.put("TOPE", topeIngresosC);

            param.put("ANO2", ano2);
            param.put("MES2", mes2);
            param.put("PERIODO2", periodo2);

            List<Registro> listaPersonal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MediosMagneticosDIANControladorUrlEnum.URL4545
                                                                            .getValue())
                                            .getUrl(), param));

            insertarRegistros(listaPersonal);

            fila = fila + 1;

            List<Registro> listaCajaPersonal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MediosMagneticosDIANControladorUrlEnum.URL4646
                                                                            .getValue())
                                            .getUrl(), param));

            insertarRegistros(listaCajaPersonal);

            fila = fila + 1;

            List<Registro> listaFondoSalud = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MediosMagneticosDIANControladorUrlEnum.URL4747
                                                                            .getValue())
                                            .getUrl(), param));

            insertarRegistros(listaFondoSalud);

            fila = fila + 1;

            List<Registro> listaFondoPension = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MediosMagneticosDIANControladorUrlEnum.URL4848
                                                                            .getValue())
                                            .getUrl(), param));

            insertarRegistros(listaFondoPension);

            fila = fila + 1;

            List<Registro> listaFondoRiesgo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MediosMagneticosDIANControladorUrlEnum.URL4949
                                                                            .getValue())
                                            .getUrl(), param));

            insertarRegistros(listaFondoRiesgo);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            "MEDIOS_MAGNETICOS_DIAN.xls");

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1059"));
        }

        catch (IOException | JRException | NamingException | SQLException
                        | NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        finally {

            try {
                con.getConection().close();
            }
            catch (SQLException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());

            }
        }

    }

    private void insertarRegistros(List<Registro> lista) {
        int columna = 0;

        for (Registro valor : lista) {

            String[] nombres;

            Row row = sheet.createRow(fila);

            Cell cellCpt = row.createCell(columna);
            if (valor.getCampos().get("CPT") == null) {
                cellCpt.setCellValue("");

            }
            else {
                cellCpt.setCellValue(
                                valor.getCampos().get("CPT").toString());

            }

            Cell cellTdoc = row.createCell(columna + 1);
            if (valor.getCampos().get("TDOC") == null) {
                cellTdoc.setCellValue("");

            }
            else {
                cellTdoc.setCellValue(
                                valor.getCampos().get("TDOC").toString());

            }

            Cell cellNid = row.createCell(columna + 2);
            if (valor.getCampos().get("NID") == null) {
                cellNid.setCellValue("");

            }
            else {
                cellNid.setCellValue(
                                valor.getCampos().get("NID").toString());

            }

            Cell cellDv = row.createCell(columna + 3);
            if (valor.getCampos().get("DV") == null) {
                cellDv.setCellValue("");

            }
            else {
                cellDv.setCellValue(
                                valor.getCampos().get("DV").toString());

            }

            Cell cellApellido1 = row.createCell(columna + 4);
            if (valor.getCampos().get("APELLIDO1") == null) {
                cellApellido1.setCellValue("");

            }
            else {
                cellApellido1.setCellValue(
                                valor.getCampos().get("APELLIDO1")
                                                .toString());

            }

            Cell cellApellido2 = row.createCell(columna + 5);
            if (valor.getCampos().get("APELLIDO2") == null) {
                cellApellido2.setCellValue("");

            }
            else {
                cellApellido2.setCellValue(
                                valor.getCampos().get("APELLIDO2")
                                                .toString());

            }

            Cell cellNombre1 = row.createCell(columna + 6);

            if (valor.getCampos()
                            .get(MediosMagneticosDIANControladorEnum.NOMBRES
                                            .getValue()) == null) {
                cellNombre1.setCellValue("");

            }
            else {

                nombres = valor.getCampos()
                                .get(MediosMagneticosDIANControladorEnum.NOMBRES
                                                .getValue())
                                .toString()
                                .split(" ");

                cellNombre1.setCellValue(nombres[0]);

            }

            Cell cellNombre2 = row.createCell(columna + 7);
            if (valor.getCampos()
                            .get(MediosMagneticosDIANControladorEnum.NOMBRES
                                            .getValue()) == null) {
                cellNombre2.setCellValue("");

            }
            else {

                nombres = valor.getCampos()
                                .get(MediosMagneticosDIANControladorEnum.NOMBRES
                                                .getValue())
                                .toString()
                                .split(" ");

                if (nombres.length > 1) {
                    cellNombre2.setCellValue(nombres[1]);
                }
                else {
                    cellNombre2.setCellValue("");
                }

            }

            Cell cellRaz = row.createCell(columna + 8);
            if (valor.getCampos().get("RAZ") == null) {
                cellRaz.setCellValue("");

            }
            else {

                cellRaz.setCellValue(valor.getCampos().get("RAZ")
                                .toString());

            }

            Cell cellDir = row.createCell(columna + 9);
            if (valor.getCampos().get("DIR") == null) {
                cellDir.setCellValue("");

            }
            else {

                cellDir.setCellValue(valor.getCampos().get("DIR")
                                .toString());

            }

            Cell cellDpto = row.createCell(columna + 10);
            if (valor.getCampos().get(GeneralParameterEnum.DEPARTAMENTO
                            .getName()) == null) {
                cellDpto.setCellValue("");

            }
            else {

                cellDpto.setCellValue(valor.getCampos().get(
                                GeneralParameterEnum.DEPARTAMENTO.getName())
                                .toString());

            }

            Cell cellMun = row.createCell(columna + 11);
            if (valor.getCampos().get(GeneralParameterEnum.CIUDAD
                            .getName()) == null) {
                cellMun.setCellValue("");

            }
            else {

                cellMun.setCellValue(valor.getCampos()
                                .get(GeneralParameterEnum.CIUDAD.getName())
                                .toString());

            }

            Cell cellPais = row.createCell(columna + 12);
            if (valor.getCampos().get("PAIS") == null) {
                cellPais.setCellValue("");

            }
            else {

                cellPais.setCellValue(
                                valor.getCampos().get("PAIS").toString());

            }

            Cell cellPago = row.createCell(columna + 13);
            if (valor.getCampos().get("PAGO") == null) {
                cellPago.setCellValue("");

            }
            else {

                cellPago.setCellValue(
                                valor.getCampos().get("PAGO").toString());

            }

            Cell cellPnded = row.createCell(columna + 14);
            if (valor.getCampos().get("PNDED") == null) {
                cellPnded.setCellValue("");

            }
            else {

                cellPnded.setCellValue(
                                valor.getCampos().get("PNDED").toString());

            }

            Cell cellIdedf = row.createCell(columna + 15);
            if (valor.getCampos().get("IDEDF") == null) {
                cellIdedf.setCellValue("");

            }
            else {

                cellIdedf.setCellValue(
                                valor.getCampos().get("IDEDF").toString());

            }

            Cell cellIndedf = row.createCell(columna + 16);
            if (valor.getCampos().get("INDEDF") == null) {
                cellIndedf.setCellValue("");

            }
            else {

                cellIndedf.setCellValue(
                                valor.getCampos().get("INDEDF").toString());

            }

            Cell cellRetpf = row.createCell(columna + 17);
            if (valor.getCampos().get("RETPF") == null) {
                cellRetpf.setCellValue("");

            }
            else {

                cellRetpf.setCellValue(
                                valor.getCampos().get("RETPF").toString());

            }

            Cell cellRetaf = row.createCell(columna + 18);
            if (valor.getCampos().get("RETAF") == null) {
                cellRetaf.setCellValue("");

            }
            else {

                cellRetaf.setCellValue(
                                valor.getCampos().get("RETAF").toString());

            }

            Cell cellComunf = row.createCell(columna + 19);
            if (valor.getCampos().get("COMUNF") == null) {
                cellComunf.setCellValue("");

            }
            else {

                cellComunf.setCellValue(
                                valor.getCampos().get("COMUNF").toString());

            }

            Cell cellSimpf = row.createCell(columna + 20);
            if (valor.getCampos().get("SIMPF") == null) {
                cellSimpf.setCellValue("");

            }
            else {

                cellSimpf.setCellValue(
                                valor.getCampos().get("SIMPF").toString());

            }

            Cell cellNdomf = row.createCell(columna + 21);
            if (valor.getCampos().get("NDOMF") == null) {
                cellNdomf.setCellValue("");

            }
            else {

                cellNdomf.setCellValue(
                                valor.getCampos().get("NDOMF").toString());

            }

            Cell cellPracticada = row.createCell(columna + 22);
            if (valor.getCampos().get("CREE_PRACTICADA") == null) {
                cellPracticada.setCellValue("");

            }
            else {

                cellPracticada.setCellValue(
                                valor.getCampos().get("CREE_PRACTICADA")
                                                .toString());

            }

            Cell cellAsumida = row.createCell(columna + 23);
            if (valor.getCampos().get("CREE_ASUMIDA") == null) {
                cellAsumida.setCellValue("");

            }
            else {

                cellAsumida.setCellValue(
                                valor.getCampos().get("CREE_ASUMIDA")
                                                .toString());

            }

            fila++;

        }

    }

    public void oprimirRES0273() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        validarDatosEntrada();
        HashMap<String, Object> reemplazar = new HashMap<>();

        try {
            // <CODIGO_DESARROLLADO>
            reemplazar.put(cIdProceso, idProceso);
            reemplazar.put(cRangoInicial, rangoInicial);
            reemplazar.put(cRangoFinal, rangoFinal);
            String sql = Reporteador.resuelveConsulta("800022Resolucion0273",
                            Integer.parseInt(modulo), reemplazar);

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL97);
        }
        catch (SQLException | JRException | IOException
                        | DRException | SysmanException ex) {
            Logger.getLogger(MediosMagneticosDIANControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

        // </CODIGO_DESARROLLADO>
    }

    public void genReportesAcumulados(FORMATOS formato) {
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            reemplazar.put(cIdProceso, idProceso);
            reemplazar.put(cRangoInicial, rangoInicial);
            reemplazar.put(cRangoFinal, rangoFinal);
            reemplazar.put("topeIngresosC", topeIngresosC);
            String sql = Reporteador.resuelveConsulta("000214ReporteDIANSTR",
                            Integer.parseInt(modulo), reemplazar);
            parametros.put(cPrSql, sql);
            parametros.put(cPrNombreEmpresa,
                            SessionUtil.getCompaniaIngreso().getNombre());
            String encabezado = SysmanFunciones.concatenar(cEntre,
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes1)],
                            " de ", ano1, " ",
                            WordUtils.capitalize(cPeriodo.toLowerCase()),
                            periodo1, " y ",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes2)],
                            " de ", ano2, " ",
                            WordUtils.capitalize(cPeriodo.toLowerCase()),
                            periodo2, "  Superiores a : ", topeIngresos);

            parametros.put(cPrEncabezado, encabezado);
            parametros.put("PR_TOPE", topeIngresosC);

            archivoDescarga = JsfUtil.exportarStreamed("000214ReporteDIANSTR",
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void genReportesSeguridadSocial(FORMATOS formato) {
        try {
            Map<String, Object> parametros = new HashMap<>();

            HashMap<String, Object> reemplazar = new HashMap<>();

            // MANEJO DE PARAMETROS DEL REPORTE
            reemplazar.put(cIdProceso, idProceso);
            reemplazar.put(cIdProceso, idProceso);
            reemplazar.put(cRangoInicial, rangoInicial);
            reemplazar.put(cRangoFinal, rangoFinal);

            String encabezado = SysmanFunciones.concatenar(cEntre,
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes1)],
                            " de ", ano1, " ",
                            WordUtils.capitalize(cPeriodo.toLowerCase()),
                            service.buscarEnLista(periodo1,
                                            cPeriodo, cNomPeriodo,
                                            listaPeriodo1),
                            " y ",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes2)],
                            " de ", ano2, " ",
                            WordUtils.capitalize(cPeriodo.toLowerCase()),
                            service.buscarEnLista(periodo2,
                                            cPeriodo, cNomPeriodo,
                                            listaPeriodo2));

            parametros.put(cPrEncabezado, encabezado);

            parametros.put(cPrNombreEmpresa,
                            SessionUtil.getCompaniaIngreso().getNombre());

            Reporteador.resuelveConsulta(
                            "000218ResumenDescuentosDIAN",
                            Integer.parseInt(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000218ResumenDescuentosDIAN",
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void genReportesPagosCesantias(FORMATOS formato) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            reemplazar.put(cIdProceso, idProceso);
            reemplazar.put(cRangoInicial, rangoInicial);
            reemplazar.put(cRangoFinal, rangoFinal);
            String sql = Reporteador.resuelveConsulta(
                            "000224ResumenPagosCesantiasDIAN",
                            Integer.parseInt(modulo), reemplazar);
            parametros.put(cPrSql, sql);
            parametros.put(cPrNombreEmpresa,
                            SessionUtil.getCompaniaIngreso().getNombre());
            String encabezado = SysmanFunciones.concatenar(cEntre,
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes1)],
                            " de ", ano1, " Periodo ",
                            service.buscarEnLista(periodo1,
                                            cPeriodo, cNomPeriodo,
                                            listaPeriodo1),
                            " y ",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes2)],
                            " de ", ano2,
                            " Periodo ", service.buscarEnLista(periodo2,
                                            cPeriodo, cNomPeriodo,
                                            listaPeriodo2));

            parametros.put(cPrEncabezado, encabezado);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000224ResumenPagosCesantiasDIAN", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarTOPE() {
        // <CODIGO_DESARROLLADO>
        DecimalFormat formato = new DecimalFormat("$#,###.###");
        topeIngresosC = topeIngresos;
        topeIngresos = formato.format(Double.parseDouble(topeIngresos));
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarProceso() {
        // <CODIGO_DESARROLLADO>
        cargarListaAno1();
        cargarListaAno2();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno1() {
        // <CODIGO_DESARROLLADO>
        mes1 = null;
        periodo1 = null;
        cargarListaMes1();
        cargarListaPeriodo1();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes1() {
        // <CODIGO_DESARROLLADO>
        periodo1 = null;
        cargarListaPeriodo1();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno2() {

        // <CODIGO_DESARROLLADO>
        mes2 = null;
        periodo2 = null;
        cargarListaMes2();
        cargarListaPeriodo2();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes2() {
        // <CODIGO_DESARROLLADO>
        periodo2 = null;
        cargarListaPeriodo2();
        // </CODIGO_DESARROLLADO>
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public String getIdProceso() {
        return idProceso;
    }

    public void setIdProceso(String idProceso) {
        this.idProceso = idProceso;
    }

    public String getAno1() {
        return ano1;
    }

    public void setAno1(String ano1) {
        this.ano1 = ano1;
    }

    public String getMes1() {
        return mes1;
    }

    public void setMes1(String mes1) {
        this.mes1 = mes1;
    }

    public String getPeriodo1() {
        return periodo1;
    }

    public void setPeriodo1(String periodo1) {
        this.periodo1 = periodo1;
    }

    public String getAno2() {
        return ano2;
    }

    public void setAno2(String ano2) {
        this.ano2 = ano2;
    }

    public String getMes2() {
        return mes2;
    }

    public void setMes2(String mes2) {
        this.mes2 = mes2;
    }

    public String getPeriodo2() {
        return periodo2;
    }

    public void setPeriodo2(String periodo2) {
        this.periodo2 = periodo2;
    }

    public String getTopeIngresos() {
        return topeIngresos;
    }

    public void setTopeIngresos(String topeIngresos) {
        this.topeIngresos = topeIngresos;
    }

    public String getTopeIngresosC() {
        return topeIngresosC;
    }

    public void setTopeIngresosC(String topeIngresosC) {
        this.topeIngresosC = topeIngresosC;
    }

    public String getRutaModelo() {
        return rutaModelo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public void setRutaModelo(String rutaModelo) {
        this.rutaModelo = rutaModelo;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public Date getFechaElaboracion() {
        return fechaElaboracion;
    }

    public void setFechaElaboracion(Date fechaElaboracion) {
        this.fechaElaboracion = fechaElaboracion;
    }

    public List<Registro> getListaProceso() {
        return listaProceso;
    }

    public void setListaProceso(List<Registro> listaProceso) {
        this.listaProceso = listaProceso;
    }

    public List<Registro> getListaAno1() {
        return listaAno1;
    }

    public void setListaAno1(List<Registro> listaAno1) {
        this.listaAno1 = listaAno1;
    }

    public List<Registro> getListaMes1() {
        return listaMes1;
    }

    public void setListaMes1(List<Registro> listaMes1) {
        this.listaMes1 = listaMes1;
    }

    public List<Registro> getListaPeriodo1() {
        return listaPeriodo1;
    }

    public void setListaPeriodo1(List<Registro> listaPeriodo1) {
        this.listaPeriodo1 = listaPeriodo1;
    }

    public List<Registro> getListaAno2() {
        return listaAno2;
    }

    public void setListaAno2(List<Registro> listaAno2) {
        this.listaAno2 = listaAno2;
    }

    public List<Registro> getListaMes2() {
        return listaMes2;
    }

    public void setListaMes2(List<Registro> listaMes2) {
        this.listaMes2 = listaMes2;
    }

    public List<Registro> getListaPeriodo2() {
        return listaPeriodo2;
    }

    public void setListaPeriodo2(List<Registro> listaPeriodo2) {
        this.listaPeriodo2 = listaPeriodo2;
    }

    public ContenedorArchivo getContArchivosaDian() {
        return contArchivosaDian;
    }

    public void setContArchivosaDian(ContenedorArchivo contArchivosaDian) {
        this.contArchivosaDian = contArchivosaDian;
    }

    private boolean validarDatos(String variable, String mensaje) {
        if (SysmanFunciones.validarVariableVacio(variable)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(mensaje));
            return true;
        }
        return false;
    }

    private boolean validarDatosUno() {
        return validarDatos(idProceso, "TB_TB2615")
            || validarDatos(ano1, "TB_TB2616")
            || validarDatos(mes1, "TB_TB2617");
    }

    private boolean validarDatosDos() {
        return validarDatos(idProceso, "TB_TB2615")
            || validarDatos(ano1, "TB_TB2616")
            || validarDatos(mes1, "TB_TB2617");
    }

    public boolean validarDatosEntrada() {

        if (validarDatosUno() || validarDatosDos()
            || validarDatos(periodo2, "TB_TB2621")) {
            return false;
        }

        rangoInicial = ano1 + SysmanFunciones.padl(mes1, 2, "0")
            + SysmanFunciones.padl(periodo1, 2, "0");
        rangoFinal = ano2 + SysmanFunciones.padl(mes2, 2, "0")
            + SysmanFunciones.padl(periodo2, 2, "0");
        if (rangoInicial.compareTo(rangoFinal) > 0) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB2623"));
            return false;
        }
        return true;
    }

}
