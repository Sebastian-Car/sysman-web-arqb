package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.DnprelacioncontratosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 14/12/2015
 * 
 * @author eamaya
 * @version 2.0, 08/08/2017 Proceso de refactoring DSS, cambio de
 * numero del formulario por enum , cambio de Sysdate por New Date()
 * 
 */

@ManagedBean
@ViewScoped

public class DnprelacioncontratosControlador extends BeanBaseModal {

    private final String compania;

    /**
     * Constante que almacenara la cadena "VISITAS"
     */
    private final String visitasC;

    /**
     * Constante que almacenara la cadena "VALOR_REGALIAS_VIGENCIAS"
     */
    private final String valorRegaliasVigenciasC;

    /**
     * Constante que almacenara la cadena "VALOR_REGALIAS"
     */
    private final String valorRegaliasC;

    /**
     * Constante que almacenara la cadena "VALOR_CONTRATO"
     */
    private final String valorContratoC;

    /**
     * Constante que almacenara la cadena "TOTAL_CHA"
     */
    private final String totalChaC;

    /**
     * Constante que almacenara la cadena "TIPO_COMPROBANTE"
     */
    private final String tipoComprobanteC;

    /**
     * Constante que almacenara la cadena "TIPOID"
     */
    private final String tipoIdC;

    /**
     * Constante que almacenara la cadena "TIPOCONTRATO"
     */
    private final String tipoContratoC;

    /**
     * Constante que almacenara la cadena "SECTOR"
     */
    private final String sectorC;

    /**
     * Constante que almacenara la cadena "PROPIETARIO"
     */
    private final String propietarioC;

    /**
     * Constante que almacenara la cadena "PORCENTAJE"
     */
    private final String porcentajeC;

    /**
     * Constante que almacenara la cadena "OBJETOCONTRATO"
     */
    private final String objetoContratoC;

    /**
     * Constante que almacenara la cadena "NUMEROCONTRATO"
     */
    private final String numeroContratoC;

    /**
     * Constante que almacenara la cadena "NOMBRE_PROYECTO"
     */
    private final String nombreProyectoC;

    /**
     * Constante que almacenara la cadena "NOMBRECONTRATISTA"
     */
    private final String nombreContratistaC;

    /**
     * Constante que almacenara la cadena "MODALIDADDECONTRATACION"
     */
    private final String modalidadContratacionC;

    /**
     * Constante que almacenara la cadena "INTERVENTOR"
     */
    private final String interventorC;

    /**
     * Constante que almacenara la cadena "FECHASUSCRIPCION"
     */
    private final String fechaSuscripcionC;

    /**
     * Constante que almacenara la cadena "FECHAFINALIZACION"
     */
    private final String fechaFinalizacionC;

    /**
     * Constante que almacenara la cadena "FECHA"
     */
    private final String fechaC;

    /**
     * Constante que almacenara la cadena "DURACION"
     */
    private final String duracionC;

    /**
     * Constante que almacenara la cadena "DIAS_SUSPENSION"
     */
    private final String diasSuspencionC;

    /**
     * Constante que almacenara la cadena "DIAS_PRORROGA"
     */
    private final String diasProrrogaC;

    /**
     * Constante que almacenara la cadena "CUENTA"
     */
    private final String cuentaC;

    /**
     * Constante que almacenara la cadena "CONSTITUCION_RESERVA"
     */
    private final String constitucionReservaC;

    /**
     * constante que almacenara la cadena "COMPROBANTE"
     */
    private final String comprobanteC;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    private ContenedorArchivo contArchivoArchivoBase;

    /**
     * Creates a new instance of DnprelacioncontratosControlador
     */
    public DnprelacioncontratosControlador() {
        super();

        numFormulario = GeneralCodigoFormaEnum.DNPRELACIONCONTRATOS_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        visitasC = "VISITAS";
        valorRegaliasVigenciasC = "VALOR_REGALIAS_VIGENCIAS";
        valorRegaliasC = "VALOR_REGALIAS";
        valorContratoC = "VALOR_CONTRATO";
        totalChaC = "TOTAL_CHA";
        tipoComprobanteC = "TIPO_COMPROBANTE";
        tipoIdC = "TIPOID";
        tipoContratoC = "TIPOCONTRATO";
        sectorC = "SECTOR";
        propietarioC = "PROPIETARIO";
        porcentajeC = "PORCENTAJE";
        objetoContratoC = "OBJETOCONTRATO";
        numeroContratoC = "NUMEROCONTRATO";
        nombreProyectoC = "NOMBRE_PROYECTO";
        nombreContratistaC = "NOMBRECONTRATISTA";
        modalidadContratacionC = "MODALIDADDECONTRATACION";
        interventorC = "INTERVENTOR";
        fechaSuscripcionC = "FECHASUSCRIPCION";
        fechaFinalizacionC = "FECHAFINALIZACION";
        fechaC = "FECHA";
        duracionC = "DURACION";
        diasSuspencionC = "DIAS_SUSPENSION";
        diasProrrogaC = "DIAS_PRORROGA";
        cuentaC = "CUENTA";
        constitucionReservaC = "CONSTITUCION_RESERVA";
        comprobanteC = "COMPROBANTE";

        try {
            contArchivoArchivoBase = new ContenedorArchivo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(DnprelacioncontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {

        fechaInicial = new Date();
        fechaFinal = new Date();
        abrirFormulario();
    }

    public void oprimirContratosAdicionales(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirContratosPrincipales() {
        // <CODIGO_DESARROLLADO>
        armarExcel();
        // </CODIGO_DESARROLLADO>
    }

    public void armarExcel() {

        if (contArchivoArchivoBase.getArchivo() == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1901"));
            return;
        }

        double valorContrato = 0;
        double valorCha = 0;
        double valorRegalias = 0;
        int fila = 8;
        int columna = 0;
        String codigoRubros = "0";

        try (FileInputStream fileIn = new FileInputStream(
                        contArchivoArchivoBase.getArchivo())) {

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            param.put(GeneralParameterEnum.FECHAINICIAL.getName(),
                            fechaInicial);

            param.put(GeneralParameterEnum.FECHAFINAL.getName(), fechaFinal);

            param.put(GeneralParameterEnum.VIGENCIA.getName(),
                            SysmanFunciones.ano(fechaInicial));

            List<Registro> listaPlanCompras = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DnprelacioncontratosControladorUrlEnum.URL8111
                                                                            .getValue())
                                            .getUrl(), param));

            Workbook workbook = new HSSFWorkbook(fileIn);
            Sheet sheet = workbook.getSheetAt(0);

            CellStyle style1 = workbook.createCellStyle();
            style1.setAlignment(CellStyle.ALIGN_CENTER);
            style1.setBorderBottom((short) 1);
            style1.setBorderLeft((short) 1);
            style1.setBorderTop((short) 1);
            style1.setBorderRight((short) 1);

            CellStyle style2 = workbook.createCellStyle();
            style2.setAlignment(CellStyle.ALIGN_CENTER);
            style2.setBorderBottom((short) 1);
            style2.setBorderLeft((short) 1);
            style2.setBorderTop((short) 1);
            style2.setBorderRight((short) 1);
            style2.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
            style2.setFillPattern(CellStyle.SOLID_FOREGROUND);

            CellStyle style3 = workbook.createCellStyle();
            style3.setBorderBottom((short) 1);
            style3.setBorderLeft((short) 1);
            style3.setBorderTop((short) 1);
            style3.setBorderRight((short) 1);

            StringBuilder builder = new StringBuilder();
            // ljdiaz (Luis Jacobo Diaz Muñoz)
            // se crea varibale para poder manipular el texto y 
            //permitir que este tome la marca blanca configurada.
            String tgSalirSysman = idioma.getString("TG_SALIR_SYSMAN");
            tgSalirSysman = tgSalirSysman.replace("s$empresaparam$s",JsfUtil.getTituloPaginaEmpresaParametrizada());
            builder.append(tgSalirSysman.toUpperCase()
                + "\r\n" + "");
            builder.append(idioma.getString("TB_TB3447") + "\r\n");

            for (Registro valor : listaPlanCompras) {

                builder.append("\r\n");
                builder.append("\r\n");

                if (valor.getCampos().get(numeroContratoC) == null) {
                    builder.append(idioma.getString("TB_TB3448") + " ");
                }
                else {
                    builder.append(idioma.getString("TB_TB3448")
                        + valor.getCampos().get(numeroContratoC).toString());

                }
                if (valor.getCampos().get(comprobanteC) == null) {

                    builder.append(" " + idioma.getString("TB_TB3449") + " ");
                }
                else {
                    builder.append(" " + idioma.getString("TB_TB3449")
                        + valor.getCampos().get(comprobanteC).toString());

                }

                builder.append(" " + idioma.getString("TB_TB3450") + " \r\n");

                Row rowEncabezado = sheet.getRow(0);
                Cell cellVigencia = rowEncabezado.getCell(columna + 6);
                cellVigencia.setCellValue(
                                valor.getCampos()
                                                .get(GeneralParameterEnum.VIGENCIA
                                                                .getName())
                                                .toString());
                Row rowDepartamento = sheet.getRow(1);
                Cell cellDepartamento = rowDepartamento.getCell(columna + 6);
                cellDepartamento.setCellValue(SessionUtil.getCompaniaIngreso()
                                .getDepartamento());
                Row rowCiudad = sheet.getRow(2);
                Cell cellCiudad = rowCiudad.getCell(columna + 6);
                cellCiudad.setCellValue(
                                SessionUtil.getCompaniaIngreso().getCiudad());

                Row rowContratos = sheet.createRow(fila);

                Cell cellSector = rowContratos.createCell(columna);
                if (valor.getCampos().get(sectorC) == null) {
                    cellSector.setCellValue("");
                    builder.append(sectorC + ", ");
                    cellSector.setCellStyle(style2);

                }
                else {
                    cellSector.setCellValue(
                                    valor.getCampos().get(sectorC).toString());
                    cellSector.setCellStyle(style2);
                }

                Cell cellCobertura = rowContratos.createCell(columna + 1);
                cellCobertura.setCellStyle(style2);

                Cell cellCuenta = rowContratos.createCell(columna + 2);

                if (valor.getCampos().get(cuentaC) == null) {

                    cellCuenta.setCellValue("");
                    cellCuenta.setCellStyle(style2);
                    builder.append(cuentaC + ", ");
                }
                else {
                    cellCuenta.setCellValue(
                                    valor.getCampos().get(cuentaC).toString());
                    cellCuenta.setCellStyle(style2);
                }

                Cell cellTipoComp = rowContratos.createCell(columna + 3);
                if (valor.getCampos().get(tipoComprobanteC) == null) {
                    cellTipoComp.setCellValue("");
                    cellTipoComp.setCellStyle(style2);
                    builder.append(tipoComprobanteC + ", ");

                }
                else {
                    cellTipoComp.setCellValue(valor.getCampos()
                                    .get(tipoComprobanteC).toString());
                    cellTipoComp.setCellStyle(style2);
                }
                Cell cellcomp = rowContratos.createCell(columna + 4);
                if (valor.getCampos().get(comprobanteC) == null) {
                    cellcomp.setCellValue("");
                    cellcomp.setCellStyle(style2);
                }
                else {
                    cellcomp.setCellValue(valor.getCampos().get(comprobanteC)
                                    .toString());
                    cellcomp.setCellStyle(style2);
                }
                Cell cellNomProy = rowContratos.createCell(columna + 5);
                if (valor.getCampos().get(nombreProyectoC) == null) {
                    cellNomProy.setCellValue("");
                    cellNomProy.setCellStyle(style1);
                    builder.append(nombreProyectoC + ", ");
                }
                else {
                    cellNomProy.setCellValue(valor.getCampos()
                                    .get(nombreProyectoC).toString());
                    cellNomProy.setCellStyle(style1);
                }

                Cell cellModalidad = rowContratos.createCell(columna + 6);
                if (valor.getCampos().get(modalidadContratacionC) == null) {
                    cellModalidad.setCellValue("");
                    cellModalidad.setCellStyle(style1);
                    builder.append(modalidadContratacionC + ", ");
                }
                else {
                    cellModalidad.setCellValue(valor.getCampos()
                                    .get(modalidadContratacionC).toString());
                    cellModalidad.setCellStyle(style1);
                }
                Cell cellTipoCon = rowContratos.createCell(columna + 7);
                if (valor.getCampos().get(tipoContratoC) == null) {
                    cellTipoCon.setCellValue("");
                    cellTipoCon.setCellStyle(style1);
                    builder.append(tipoContratoC + ", ");
                }
                else {
                    cellTipoCon.setCellValue(valor.getCampos()
                                    .get(tipoContratoC).toString());
                    cellTipoCon.setCellStyle(style1);
                }

                Cell cellCausal = rowContratos.createCell(columna + 8);
                cellCausal.setCellStyle(style1);

                Cell cellNumCon = rowContratos.createCell(columna + 9);

                if (valor.getCampos().get(numeroContratoC) == null) {
                    cellNumCon.setCellValue("");
                    cellNumCon.setCellStyle(style1);
                    builder.append(numeroContratoC + ", ");
                }
                else {
                    cellNumCon.setCellValue(valor.getCampos()
                                    .get(numeroContratoC).toString());
                    cellNumCon.setCellStyle(style1);
                }
                Cell cellFechSus = rowContratos.createCell(columna + 10);
                if (valor.getCampos().get(fechaSuscripcionC) == null) {
                    cellFechSus.setCellValue("");
                    cellFechSus.setCellStyle(style1);
                    builder.append(fechaSuscripcionC + ", ");
                }
                else {
                    cellFechSus.setCellValue(valor.getCampos()
                                    .get(fechaSuscripcionC).toString());
                    cellFechSus.setCellStyle(style1);
                }
                Cell cellObjCon = rowContratos.createCell(columna + 11);
                if (valor.getCampos().get(objetoContratoC) == null) {
                    cellObjCon.setCellValue("");
                    cellObjCon.setCellStyle(style2);
                    builder.append(objetoContratoC + ", ");
                }
                else {
                    cellObjCon.setCellValue(valor.getCampos()
                                    .get(objetoContratoC).toString());
                    cellObjCon.setCellStyle(style2);
                }
                Cell cellValorTotal = rowContratos.createCell(columna + 12);
                cellValorTotal.setCellValue(Cell.CELL_TYPE_NUMERIC);

                if (valor.getCampos().get(valorContratoC) == null) {
                    cellValorTotal.setCellType(0);
                    cellValorTotal.setCellStyle(style2);
                    builder.append(valorContratoC + ", ");

                }
                else {
                    cellValorTotal.setCellValue(Double.parseDouble(
                                    valor.getCampos().get(valorContratoC)
                                                    .toString()));
                    cellValorTotal.setCellStyle(style2);
                    valorContrato = valorContrato
                        + (Double.parseDouble(valor.getCampos()
                                        .get(valorContratoC).toString()));

                }

                Cell cellValorTotalRegalias = rowContratos
                                .createCell(columna + 13);
                cellValorTotalRegalias.setCellValue(Cell.CELL_TYPE_NUMERIC);

                if (valor.getCampos().get(valorRegaliasC) == null) {
                    cellValorTotalRegalias.setCellType(0);
                    cellValorTotalRegalias.setCellStyle(style2);
                    builder.append(valorRegaliasC + ", ");

                }
                else {
                    cellValorTotalRegalias.setCellValue(Double.parseDouble(
                                    valor.getCampos().get(valorRegaliasC)
                                                    .toString()));
                    cellValorTotalRegalias.setCellStyle(style2);

                }
                Cell cellConRes = rowContratos.createCell(columna + 14);
                cellConRes.setCellValue(Cell.CELL_TYPE_NUMERIC);
                if (valor.getCampos().get(constitucionReservaC) == null) {
                    cellConRes.setCellValue(0);
                    cellConRes.setCellStyle(style2);
                    builder.append(constitucionReservaC + ", ");
                }
                else {
                    cellConRes.setCellValue(Double.parseDouble(valor.getCampos()
                                    .get(constitucionReservaC).toString()));
                    cellConRes.setCellStyle(style2);
                }

                Cell cellPagVigencia = rowContratos.createCell(columna + 15);
                cellPagVigencia.setCellValue(Cell.CELL_TYPE_NUMERIC);

                if (valor.getCampos().get(totalChaC) == null) {
                    cellPagVigencia.setCellValue(0);
                    cellPagVigencia.setCellStyle(style2);
                    builder.append(totalChaC + ", ");

                }
                else {
                    cellPagVigencia.setCellValue(Double.parseDouble(valor
                                    .getCampos().get(totalChaC).toString()));
                    cellPagVigencia.setCellStyle(style2);
                    valorCha = valorCha + (Double.parseDouble(valor.getCampos()
                                    .get(totalChaC).toString()));

                }

                Cell cellRegVig = rowContratos.createCell(columna + 16);
                cellRegVig.setCellValue(Cell.CELL_TYPE_NUMERIC);

                if (valor.getCampos().get(valorRegaliasVigenciasC) == null) {
                    cellRegVig.setCellValue(0);
                    cellRegVig.setCellStyle(style2);
                    builder.append(valorRegaliasVigenciasC + ", ");
                }
                else {
                    cellRegVig.setCellValue(Double.parseDouble(valor.getCampos()
                                    .get(valorRegaliasVigenciasC)
                                    .toString()));
                    cellRegVig.setCellStyle(style2);
                    valorRegalias = valorRegalias + (Double.parseDouble(valor
                                    .getCampos().get(valorRegaliasVigenciasC)
                                    .toString()));

                }

                Cell cellValRegalias = rowContratos.createCell(columna + 17);
                cellValRegalias.setCellStyle(style2);

                Cell cellTipoId = rowContratos.createCell(columna + 18);
                cellTipoId.setCellValue(Cell.CELL_TYPE_STRING);
                if (valor.getCampos().get(tipoIdC) == null) {
                    cellTipoId.setCellValue("");
                    cellTipoId.setCellStyle(style2);
                    builder.append(tipoIdC + ", ");
                }
                else {
                    cellTipoId.setCellValue(
                                    valor.getCampos().get(tipoIdC).toString());
                    cellTipoId.setCellStyle(style2);
                }

                Cell cellNitCon = rowContratos.createCell(columna + 19);
                cellNitCon.setCellValue(Cell.CELL_TYPE_STRING);
                if (valor.getCampos().get("NIT") == null) {
                    cellNitCon.setCellValue("");
                    cellNitCon.setCellStyle(style2);
                    builder.append("NIT" + ", ");
                }
                else {
                    cellNitCon.setCellValue(
                                    valor.getCampos().get("NIT").toString());
                    cellNitCon.setCellStyle(style2);
                }

                Cell cellNomCon = rowContratos.createCell(columna + 20);
                cellNomCon.setCellValue(Cell.CELL_TYPE_STRING);
                if (valor.getCampos().get(nombreContratistaC) == null) {
                    cellNomCon.setCellValue("");
                    cellNomCon.setCellStyle(style2);
                    builder.append(nombreContratistaC + ", ");
                }
                else {
                    cellNomCon.setCellValue(valor.getCampos()
                                    .get(nombreContratistaC).toString());
                    cellNomCon.setCellStyle(style2);
                }
                Cell cellFecIni = rowContratos.createCell(columna + 21);
                if (valor.getCampos().get(fechaC) == null) {
                    cellFecIni.setCellValue("");
                    cellFecIni.setCellStyle(style1);
                    builder.append(fechaC + ", ");
                }
                else {
                    cellFecIni.setCellValue(
                                    valor.getCampos().get(fechaC).toString());
                    cellFecIni.setCellStyle(style1);
                }
                Cell cellDuracion = rowContratos.createCell(columna + 22);
                if (valor.getCampos().get(duracionC) == null) {
                    cellDuracion.setCellValue("");
                    cellDuracion.setCellStyle(style1);
                    builder.append(duracionC + ", ");
                }
                else {
                    cellDuracion.setCellValue(valor.getCampos().get(duracionC)
                                    .toString());
                    cellDuracion.setCellStyle(style1);
                }
                Cell cellDiasSuspension = rowContratos.createCell(columna + 23);

                if (valor.getCampos().get(diasSuspencionC) == null) {
                    cellDiasSuspension.setCellValue("");
                    cellDiasSuspension.setCellStyle(style1);
                    builder.append(diasSuspencionC + ", ");
                }
                else {
                    cellDiasSuspension.setCellValue(valor.getCampos()
                                    .get(diasSuspencionC).toString());
                    cellDiasSuspension.setCellStyle(style1);
                }

                Cell cellDiasProrroga = rowContratos.createCell(columna + 24);

                if (valor.getCampos().get(diasProrrogaC) == null) {
                    cellDiasProrroga.setCellValue("");
                    cellDiasProrroga.setCellStyle(style1);
                    builder.append(diasProrrogaC + ", ");
                }
                else {
                    cellDiasProrroga.setCellValue(valor.getCampos()
                                    .get(diasProrrogaC).toString());
                    cellDiasProrroga.setCellStyle(style1);
                }

                Cell cellFecFin = rowContratos.createCell(columna + 25);

                if (valor.getCampos().get(fechaFinalizacionC) == null) {
                    cellFecFin.setCellValue("");
                    cellFecFin.setCellStyle(style1);
                    builder.append(fechaFinalizacionC + ", ");
                }
                else {
                    cellFecFin.setCellValue(valor.getCampos()
                                    .get(fechaFinalizacionC).toString());
                    cellFecFin.setCellStyle(style1);
                }
                Cell cellPropietario = rowContratos.createCell(columna + 26);
                if (valor.getCampos().get(propietarioC) == null) {
                    cellPropietario.setCellValue("");
                    cellPropietario.setCellStyle(style1);
                    builder.append(propietarioC + ", ");

                }
                else {
                    cellPropietario.setCellValue(valor.getCampos()
                                    .get(propietarioC).toString());
                    cellPropietario.setCellStyle(style1);

                }

                Cell cellInterventor = rowContratos.createCell(columna + 27);
                if (valor.getCampos().get(interventorC) == null) {
                    cellInterventor.setCellValue("");
                    cellInterventor.setCellStyle(style1);
                    builder.append(interventorC + ", ");
                }
                else {
                    cellInterventor.setCellValue(valor.getCampos()
                                    .get(interventorC).toString());
                    cellInterventor.setCellStyle(style1);
                }
                Cell cellPorcentaje = rowContratos.createCell(columna + 28);
                if (valor.getCampos().get(porcentajeC) == null) {
                    cellPorcentaje.setCellValue("");
                    cellPorcentaje.setCellStyle(style1);
                    builder.append(porcentajeC + ", ");
                }
                else {
                    cellPorcentaje.setCellValue(valor.getCampos()
                                    .get(porcentajeC).toString());
                    cellPorcentaje.setCellStyle(style1);
                }

                Cell cellPorcAvance = rowContratos.createCell(columna + 29);
                cellPorcAvance.setCellStyle(style2);

                Cell cellVisitas = rowContratos.createCell(columna + 30);
                if (valor.getCampos().get(visitasC) == null) {
                    cellVisitas.setCellValue("");
                    cellVisitas.setCellStyle(style1);
                    builder.append(visitasC + "\r\n");
                }
                else {
                    cellVisitas.setCellValue(
                                    valor.getCampos().get(visitasC).toString());
                    cellVisitas.setCellStyle(style1);
                }

                fila++;
            }

            int nColumnas = Math.max(sheet.getRow(fila - 1).getLastCellNum(),
                            0);

            crearRegionTotal(fila, sheet);

            // Insertar datos fila Total
            Row rowTotal = sheet.createRow(fila);

            Cell cellEncabezado = rowTotal.createCell(2);
            cellEncabezado.setCellValue(idioma.getString("TB_TB3517"));

            Cell cellValorSub = rowTotal.createCell(columna + 12);
            cellValorSub.setCellValue(valorContrato);

            Cell cellValortot = rowTotal.createCell(columna + 15);
            Cell cellValortot1 = rowTotal.createCell(columna + 16);
            for (Registro next : listaPlanCompras) {
                if (codigoRubros != next.getCampos().get(cuentaC)) {
                    cellValortot.setCellValue(valorCha);
                    cellValortot1.setCellValue(valorRegalias);
                }
            }

            // Asignar estilo a la fila de total
            pintarFila(rowTotal, 0, nColumnas, style1);

            // Crear fila en blanco
            Row rowBlanco = sheet.createRow(fila + 1);
            pintarFila(rowBlanco, 0, nColumnas, style3);

            crearRegionObservacion(fila, sheet);

            Row rowRP = sheet.createRow(fila + 2);
            Cell cellRp = rowRP.createCell(2);
            cellRp.setCellValue(idioma.getString("TB_TB3451"));
            pintarFila(rowRP, 0, nColumnas, style3);

            Row rowNota = sheet.createRow(fila + 3);
            Cell cellNota = rowNota.createCell(2);
            cellNota.setCellValue(idioma.getString("TB_TB3452"));
            pintarFila(rowNota, 0, nColumnas, style3);

            Row rowObservacion = sheet.createRow(fila + 4);
            Cell cellObservacion = rowObservacion.createCell(2);
            cellObservacion.setCellValue(idioma.getString("TB_TB3453"));
            pintarFila(rowObservacion, 0, nColumnas, style3);

            // Crear fila en blanco1
            Row rowBlanco1 = sheet.createRow(fila + 5);
            pintarFila(rowBlanco1, 0, nColumnas, style3);

            crearRegionDiligenciado(fila, sheet);

            Row rowDiligenciado = sheet.createRow(fila + 6);
            Cell cellDiligenciado = rowDiligenciado.createCell(columna + 2);
            cellDiligenciado.setCellValue(idioma.getString("TB_TB3454"));

            Row rowNombre = sheet.createRow(fila + 7);
            Cell cellNombre = rowNombre.createCell(columna + 2);
            cellNombre.setCellValue(idioma.getString("TG_NOMBRE4"));

            Row rowCargo = sheet.createRow(fila + 8);
            Cell cellCargo = rowCargo.createCell(columna + 2);
            cellCargo.setCellValue(idioma.getString("TG_CARGO4"));

            Row rowFirma = sheet.createRow(fila + 9);
            Cell cellFirma = rowFirma.createCell(columna + 2);
            cellFirma.setCellValue(idioma.getString("TB_TB3457"));

            crearRegionRepresentante(fila, sheet);

            Cell cellRepresentante = rowDiligenciado.createCell(columna + 12);
            cellRepresentante.setCellValue(idioma.getString("TB_TB3455"));
            pintarFila(rowDiligenciado, 0, nColumnas, style1);

            Cell cellNombre1 = rowNombre.createCell(columna + 12);
            cellNombre1.setCellValue(idioma.getString("TG_NOMBRE4"));
            pintarFila(rowNombre, 0, nColumnas, style3);

            Cell cellAlcaldesa = rowCargo.createCell(columna + 12);
            cellAlcaldesa.setCellValue(idioma.getString("TB_TB3456"));
            pintarFila(rowCargo, 0, nColumnas, style3);

            Cell cellFirma2 = rowFirma.createCell(columna + 12);
            cellFirma2.setCellValue(idioma.getString("TB_TB3457"));
            pintarFila(rowFirma, 0, nColumnas, style3);

            Row rowGobernador = sheet.createRow(fila + 10);
            Cell cellGobernador = rowGobernador.createCell(columna + 12);
            cellGobernador.setCellValue(idioma.getString("TB_TB3458"));
            pintarFila(rowGobernador, 0, nColumnas, style3);

            ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
            workbook.write(fileOut);
            fileOut.close();
            fileIn.close();

            ByteArrayInputStream planoSerializado = JsfUtil
                            .serializarPlano(builder.toString());
            ByteArrayInputStream excelSerializado = new ByteArrayInputStream(
                            fileOut.toByteArray());

            ByteArrayInputStream[] archivoSerial = { planoSerializado,
                                                     excelSerializado };
            String[] nombres = { "SysmanINCONSISTENCIAS.txt",
                                 "INFORME DNP.xls" };

            archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                            archivoSerial, nombres);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2079"));
            Logger.getLogger(DnprelacioncontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SQLException | JRException | IOException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                + ex.getMessage());
            Logger.getLogger(DnprelacioncontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (DRException | SystemException ex) {
            Logger.getLogger(DnprelacioncontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    private void crearRegionRepresentante(int fila, Sheet sheet) {
        for (int i = fila + 6; i < fila + 11; i++) {

            CellReference celdaIni = new CellReference(i, 12);
            String regIni = celdaIni.formatAsString();
            CellReference celdaFin = new CellReference(i, 23);
            String regFin = celdaFin.formatAsString();

            CellRangeAddress region = CellRangeAddress
                            .valueOf("" + regIni + ":" + regFin);

            sheet.addMergedRegion(region);
        }

    }

    private void crearRegionDiligenciado(int fila, Sheet sheet) {

        for (int i = fila + 6; i < fila + 11; i++) {

            CellReference celdaIni = new CellReference(i, 2);
            String regIni = celdaIni.formatAsString();
            CellReference celdaFin = new CellReference(i, 11);
            String regFin = celdaFin.formatAsString();

            CellRangeAddress region = CellRangeAddress
                            .valueOf("" + regIni + ":" + regFin);

            sheet.addMergedRegion(region);
        }
    }

    private void crearRegionObservacion(int fila, Sheet sheet) {

        for (int i = fila + 2; i < fila + 5; i++) {

            CellReference celdaObsvIni = new CellReference(i, 2);
            String obsvIni = celdaObsvIni.formatAsString();
            CellReference celdaObsvFin = new CellReference(i, 21);
            String obsvFin = celdaObsvFin.formatAsString();

            CellRangeAddress region2 = CellRangeAddress
                            .valueOf("" + obsvIni + ":" + obsvFin);

            sheet.addMergedRegion(region2);
        }
    }

    private void crearRegionTotal(int fila, Sheet sheet) {
        CellReference celdaTotalIni = new CellReference(fila, 2);
        String totalIni = celdaTotalIni.formatAsString();
        CellReference celdaTotalFin = new CellReference(fila, 11);
        String totalFin = celdaTotalFin.formatAsString();

        CellRangeAddress region = CellRangeAddress
                        .valueOf("" + totalIni + ":" + totalFin);

        sheet.addMergedRegion(region);

    }

    public static void pintarFila(Row row, int startColumn, int endColumn,
        CellStyle style) {
        for (int column = startColumn; column < endColumn; column++) {
            if (row.getCell(column) != null) {
                row.getCell(column).setCellStyle(style);
            }
            else {
                Cell cell = row.createCell(column);
                cell.setCellStyle(style);
            }
        }
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public ContenedorArchivo getContArchivoArchivoBase() {
        return contArchivoArchivoBase;
    }

    public void setContArchivoArchivoBase(
        ContenedorArchivo contArchivoArchivoBase) {
        this.contArchivoArchivoBase = contArchivoArchivoBase;
    }
}
