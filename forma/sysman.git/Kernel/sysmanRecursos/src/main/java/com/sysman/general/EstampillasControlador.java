package com.sysman.general;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.EstampillasControladorEnum;
import com.sysman.general.enums.EstampillasControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 25/01/2016
 * @version 2, 06/04/2017 --Refactorización y correción sonar MZANGUNA
 * @version 3, 21/04/2017 --Cambio Ejb.
 */
@ManagedBean
@ViewScoped

public class EstampillasControlador extends BeanBaseDatosAcmeImpl
{

    private final String compania;
    private final String modulo;
    private final String regimenCons;
    private final String fechaCons;
    private final String valorTotalCons;
    private final String nombreCons;
    private final String terceroCons;
    private final String valorProculturaCons;
    private final String valorProAdultoMayorCons;
    private final String pagaProdeporteCons;
    private final String valorProDeporteCons;
    private final String sucursalCons;

    private String numeroContrato;
    private String anio;
    private String tipoContrato;
    private String ivaestampillas;
    private boolean textoVlEstDepVisible;
    private StreamedContent archivoDescarga;
    private int limiteValorDeporte;
    HashMap<String, Object> rs2 = new HashMap<>();
    HashMap<String, Object> rs2R = new HashMap<>();
    HashMap<String, Object> rsdep = new HashMap();

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    public EstampillasControlador()
    {
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        regimenCons = "REGIMEN";
        fechaCons = "FECHA";
        valorTotalCons = "VALORTOTAL";
        nombreCons = "NOMBRE";
        terceroCons = "TERCERO";
        valorProculturaCons = "VALOR_PROCULTURA";
        valorProAdultoMayorCons = "VALOR_PROADULTOMAYOR";
        pagaProdeporteCons = "PAGAPRODEPORTE";
        valorProDeporteCons = "VALOR_PRODEPORTE";
        sucursalCons = "SUCURSAL";
        try
        {
            registro = new Registro(new HashMap<String, Object>());
            numFormulario = GeneralCodigoFormaEnum.ESTAMPILLAS_CONTROLADOR
                            .getCodigo();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                tipoContrato = (String) parametrosEntrada.get("tipoContrato");
                anio = (String) parametrosEntrada.get("anio");
                numeroContrato = (String) parametrosEntrada
                                .get("numeroContrato");
            }
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(EstampillasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally
        {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.PAGO_ESTAMPILLAS;

        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametros.put(GeneralParameterEnum.TIPOCONTRATO.getName(),
                        tipoContrato);
        parametros.put(EstampillasControladorEnum.NUMCONTRATO.getValue(),
                        numeroContrato);

        HashMap<String, Object> rsVerifica = new HashMap();

        try
        {
            UrlBean urlReg = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            EstampillasControladorUrlEnum.URL19121
                                                            .getValue());
            rsVerifica = (HashMap<String, Object>) requestManager
                            .get(urlReg.getUrl(), parametros).getFields();
        }
        catch (SystemException e)
        {
            Logger.getLogger(EstampillasControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }

        if ((rsVerifica.get("CUENTA") == null) || rsVerifica.isEmpty()
            || ((Integer) rsVerifica.get("CUENTA") == 0))
        {

            Map<String, Object> parametrosInsert = new HashMap<>();
            parametrosInsert.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            parametrosInsert.put(GeneralParameterEnum.CLASEORDEN.getName(),
                            tipoContrato);
            parametrosInsert.put(GeneralParameterEnum.NUMERO.getName(),
                            numeroContrato);
            parametrosInsert.put(EstampillasControladorEnum.VPROCULTURA.getValue(),
                            0);
            parametrosInsert.put(GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            parametrosInsert.put(GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            Parameter parameter = new Parameter();

            parameter.setFields(parametrosInsert);

            try
            {
                UrlBean urlCreacion = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                EstampillasControladorUrlEnum.URL19151
                                                                .getValue());
                requestManager.save(urlCreacion.getUrl(),
                                urlCreacion.getMetodo(), parameter);
            }
            catch (SystemException e)
            {
                Logger.getLogger(EstampillasControlador.class.getName())
                                .log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());

            }

        }

        asignarOrigenDatos();
        registro.getLlave().put("KEY_COMPANIA", compania);
        registro.getLlave().put("KEY_CLASEORDEN", tipoContrato);
        registro.getLlave().put("KEY_NUMERO", numeroContrato);
        abrirFormulario();

    }

    @Override
    public void asignarOrigenDatos()
    {
        buscarUrls();

    }

    public void oprimirimpimir()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        String strRutaDoc = null;
        String strArchivobase = "C:\\sysman\\formatoLiquidacion2014-LILI.xls";
        try (FileInputStream file = new FileInputStream(
                        new File(strArchivobase));)
        {
            strRutaDoc = ejbSysmanUtil.consultarParametro(compania,
                            "RUTA DE UBICACION DE CONTRATOS", modulo,
                            new Date(), false);

            String strArchivo = strRutaDoc
                + "formatoLiquidacion2014-LILI_ContratoNo." + numeroContrato
                + ".xls";

            Workbook workbook = new HSSFWorkbook(file);

            Map<String, Object> parametrosExcel = new HashMap<>();
            parametrosExcel.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            parametrosExcel.put(GeneralParameterEnum.TIPOCONTRATO.getName(),
                            tipoContrato);
            parametrosExcel.put(
                            EstampillasControladorEnum.NUMCONTRATO.getValue(),
                            numeroContrato);

            HashMap<String, Object> rs = new HashMap();

            rs = peticionSelect(parametrosExcel,
                            EstampillasControladorUrlEnum.URL19208.getValue());

            if (!rs.isEmpty())
            {
                Font font = workbook.createFont();
                font.setBold(true);

                CellStyle style = workbook.createCellStyle();
                style.setBorderBottom(CellStyle.BORDER_MEDIUM);
                style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
                style.setBorderLeft(CellStyle.BORDER_MEDIUM);
                style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
                style.setBorderRight(CellStyle.BORDER_MEDIUM);
                style.setRightBorderColor(IndexedColors.BLACK.getIndex());
                style.setBorderTop(CellStyle.BORDER_MEDIUM);
                style.setTopBorderColor(IndexedColors.BLACK.getIndex());
                style.setFont(font);
                style.setAlignment(CellStyle.ALIGN_CENTER);

                if ("C".equals(rs.get(regimenCons) == null ? "S"
                    : rs.get(regimenCons)))
                {

                    Sheet sheet = workbook.getSheet("COMUN");
                    int i = 7;
                    Row row = sheet.getRow(7);
                    Cell cell = row.createCell(2);
                    cell.setCellValue(rs.get(EstampillasControladorEnum.PARAM0
                                    .getValue()).toString());
                    cell.setCellStyle(style);

                    cell = row.createCell(4);
                    Date fecha = rs.get(fechaCons) == null
                        ? new Date()
                        : (Date) rs.get(fechaCons);
                    cell.setCellStyle(style);
                    String fechaTxt = SysmanFunciones.convertirAFechaCadena(
                                    fecha,
                                    "dd 'de' MMMM 'de' yyyy. ");

                    cell.setCellValue(fechaTxt);
                    cell.setCellStyle(style);

                    cell = row.createCell(7);
                    cell.setCellValue((rs.get(valorTotalCons) == null ? "0"
                        : rs.get(valorTotalCons)).toString());
                    CellRangeAddress region = CellRangeAddress
                                    .valueOf("H" + i + ":I" + i + "");
                    sheet.addMergedRegion(region);
                    cell.setCellStyle(style);

                    row = sheet.getRow(8);
                    cell = row.createCell(2);
                    cell.setCellValue(((rs.get(nombreCons) == null ? "X"
                        : rs.get(nombreCons)).toString()).toUpperCase());
                    region = CellRangeAddress
                                    .valueOf("C" + i + 1 + ":E" + i + 1 + "");
                    sheet.addMergedRegion(region);
                    cell.setCellStyle(style);

                    cell = row.createCell(7);
                    cell.setCellValue(((rs.get(terceroCons) == null ? "0"
                        : rs.get(terceroCons)).toString())
                                        .toUpperCase());
                    region = CellRangeAddress
                                    .valueOf("H" + i + 1 + ":I" + i + 1 + "");
                    sheet.addMergedRegion(region);
                    cell.setCellStyle(style);

                    row = sheet.getRow(10);
                    cell = row.createCell(2);
                    cell.setCellValue("X");
                    cell.setCellStyle(style);

                    row = sheet.getRow(12);
                    cell = row.createCell(2);
                    cell.setCellValue("X");
                    cell.setCellStyle(style);

                    row = sheet.getRow(10);
                    cell = row.createCell(7);
                    cell.setCellValue((double) rs.get(EstampillasControladorEnum.VPROCULTURA.getValue()));
                    region = CellRangeAddress
                                    .valueOf("H11:I12");
                    sheet.addMergedRegion(region);
                    cell.setCellStyle(style);

                    row = sheet.getRow(12);
                    cell = row.createCell(7);
                    cell.setCellValue((double) rs.get(EstampillasControladorEnum.VPROADULTO.getValue()));
                    region = CellRangeAddress
                                    .valueOf("H13:I13");
                    sheet.addMergedRegion(region);
                    cell.setCellStyle(style);

                    row = sheet.getRow(13);
                    cell = row.createCell(7);
                    cell.setCellValue((double) rs.get(EstampillasControladorEnum.VPRODEPORTE.getValue()));
                    region = CellRangeAddress
                                    .valueOf("H14:I14");
                    sheet.addMergedRegion(region);
                    cell.setCellStyle(style);

                    row = sheet.getRow(3);
                    cell = row.createCell(12);
                    cell.setCellValue(Double.parseDouble(ivaestampillas));
                    cell.setCellStyle(style);

                    workbook.setActiveSheet(1);

                }
                regimenS(rs, workbook, style);

                workbook.setForceFormulaRecalculation(true);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                workbook.write(out);
                workbook.close();
                out.close();

                archivoDescarga = JsfUtil.getArchivoDescarga(
                                new ByteArrayInputStream(out.toByteArray()),
                                strArchivo);
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB874"));
            }

        }
        catch (IOException | ParseException | JRException | SystemException ex)

        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA") + "<br>"
                                + ex.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    private HashMap<String, Object> peticionSelect(
        Map<String, Object> parametrosConsulta, String url)
    {
        HashMap<String, Object> rsConsulta = new HashMap();
        try
        {
            UrlBean urlReg = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(url);
            rsConsulta = (HashMap<String, Object>) requestManager
                            .get(urlReg.getUrl(), parametrosConsulta)
                            .getFields();
        }
        catch (SystemException e)
        {
            Logger.getLogger(EstampillasControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
        return rsConsulta;
    }

    private void regimenS(HashMap<String, Object> rs, Workbook workbook,
        CellStyle style)
    {
        if ("S".equals(rs.get(regimenCons) == null ? "S"
            : rs.get(regimenCons).toString()))
        {
            Sheet sheet = workbook.getSheet("SIMPLI");
            int i = 7;
            Row row = sheet.getRow(7);
            Cell cell = row.createCell(2);
            cell.setCellValue(
                            rs.get(EstampillasControladorEnum.PARAM0.getValue())
                                            .toString());
            cell.setCellStyle(style);

            cell = row.createCell(4);
            Date fecha = rs.get(fechaCons) == null
                ? new Date()
                : (Date) rs.get(fechaCons);
            cell.setCellStyle(style);
            String fechaTxt = "";
            try
            {
                fechaTxt = SysmanFunciones.convertirAFechaCadena(
                                fecha,
                                "dd 'de' MMMM 'de' yyyy. ");
            }
            catch (ParseException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

            cell.setCellValue(fechaTxt);
            cell.setCellStyle(style);

            cell = row.createCell(7);
            cell.setCellValue((rs.get(valorTotalCons) == null ? "0"
                : rs.get(valorTotalCons)).toString());
            CellRangeAddress region = CellRangeAddress.valueOf("H8:I8");
            sheet.addMergedRegion(region);
            cell.setCellStyle(style);

            row = sheet.getRow(8);
            cell = row.createCell(2);
            cell.setCellValue(((rs.get(nombreCons) == null ? "X"
                : rs.get(nombreCons)).toString())
                                .toUpperCase());
            region = CellRangeAddress
                            .valueOf("C" + i + 1 + ":E" + i + 1 + "");
            sheet.addMergedRegion(region);
            cell.setCellStyle(style);

            cell = row.createCell(7);
            cell.setCellValue(((rs.get(terceroCons) == null ? "0"
                : rs.get(terceroCons)).toString()).toUpperCase());
            region = CellRangeAddress
                            .valueOf("H" + i + 1 + ":I" + i + 1 + "");
            sheet.addMergedRegion(region);
            cell.setCellStyle(style);

            row = sheet.getRow(10);
            cell = row.createCell(2);
            cell.setCellValue("X");
            cell.setCellStyle(style);

            cell = row.createCell(7);
            cell.setCellValue(registro.getCampos()
                            .get(valorProculturaCons).toString());
            region = CellRangeAddress.valueOf("H" + i + ":I" + i + "");
            sheet.addMergedRegion(region);
            cell.setCellStyle(style);

            row = sheet.getRow(12);
            cell = row.createCell(2);
            cell.setCellValue("X");
            cell.setCellStyle(style);

            cell = row.createCell(7);
            cell.setCellValue(registro.getCampos()
                            .get(valorProAdultoMayorCons).toString());
            region = CellRangeAddress.valueOf("H" + i + ":I" + i + "");
            sheet.addMergedRegion(region);
            cell.setCellStyle(style);

            validaciones(sheet, style);
            workbook.setActiveSheet(2);
        }

    }

    private void validaciones(Sheet sheet, CellStyle style)
    {
        if (Boolean.parseBoolean(registro.getCampos()
                        .get(pagaProdeporteCons).toString()))
        {
            Row row = sheet.getRow(14);

            Cell cell = row.createCell(7);
            cell.setCellValue(registro.getCampos()
                            .get(valorProDeporteCons).toString());
            CellRangeAddress region = CellRangeAddress.valueOf("H15:I16");
            sheet.addMergedRegion(region);
            cell.setCellStyle(style);
        }
    }

    public void cambiarVerificacion15()
    {
        // <CODIGO_DESARROLLADO>
        double prodeporte = 0;
        try
        {
            ivaestampillas = ejbSysmanUtil.consultarParametro(compania,
                            "IVA SOBRE LAS ESTAMPILLAS", modulo,
                            new Date(), true);

        }
        catch (SystemException ex)
        {
            Logger.getLogger(EstampillasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

        Map<String, Object> parametrosCk = new HashMap<>();
        parametrosCk.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosCk.put(GeneralParameterEnum.TIPOCONTRATO.getName(),
                        tipoContrato);
        parametrosCk.put(EstampillasControladorEnum.NUMCONTRATO.getValue(),
                        numeroContrato);

        rs2 = peticionSelect(parametrosCk,
                        EstampillasControladorUrlEnum.URL19554.getValue());

        if (!rs2.isEmpty())
        {
            String rs2Tercero = (rs2.get(terceroCons) == null ? ""
                : rs2.get(terceroCons)).toString();
            String rs2Sucursal = (rs2.get(sucursalCons) == null ? ""
                : rs2.get(sucursalCons)).toString();

            Map<String, Object> parametrosTercero = new HashMap();
            parametrosTercero.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            parametrosTercero.put(EstampillasControladorEnum.NIT.getValue(),
                            rs2Tercero);
            parametrosTercero.put(GeneralParameterEnum.SUCURSAL.getName(),
                            rs2Sucursal);

            HashMap<String, Object> rsTercero = new HashMap();
            peticionSelect(parametrosTercero,
                            EstampillasControladorUrlEnum.URL19479.getValue());

            double valorTotal = (Double) rs2.get(valorTotalCons);

            if ((!rsTercero.isEmpty())
                && !"S".equals(rsTercero.get(regimenCons)))
            {
                valorTotal = SysmanFunciones.redondear(
                                valorTotal
                                    / Double.parseDouble(ivaestampillas),
                                2);

            }
            if (Boolean.parseBoolean(
                            registro.getCampos().get(pagaProdeporteCons)
                                            .toString()))
            {
                limiteValorDeporte = 21163450;
                if (valorTotal > limiteValorDeporte)
                {
                    prodeporte = SysmanFunciones.redondear(valorTotal * 0.01,
                                    -3);
                    textoVlEstDepVisible = true;
                    registro.getCampos().put(valorProDeporteCons, prodeporte);
                }
            }
            else
            {
                registro.getCampos().put(valorProDeporteCons, prodeporte);
            }
        }
        else
        {
            registro.getCampos().put(valorProDeporteCons, 0);
        }

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        css = registro.getLlave();
        cargarRegistro(css, ACCION_MODIFICAR);

        registroIni = new HashMap<>(registro.getCampos());
        /*
         * FR414-AL_ABRIR Private Sub Form_Open(Cancel As Integer) Dim db As Dao.Database
         */
        double prodeporte = 0;
        double procultura = 0;
        try
        {
            ivaestampillas = ejbSysmanUtil.consultarParametro(compania,
                            "IVA SOBRE LAS ESTAMPILLAS", modulo,
                            new Date(), true);

        }
        catch (SystemException ex)
        {
            Logger.getLogger(EstampillasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

        Map<String, Object> parametrosCk = new HashMap<>();
        parametrosCk.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosCk.put(GeneralParameterEnum.TIPOCONTRATO.getName(),
                        tipoContrato);
        parametrosCk.put(EstampillasControladorEnum.NUMCONTRATO.getValue(),
                        numeroContrato);

        rs2R = peticionSelect(parametrosCk,
                        EstampillasControladorUrlEnum.URL19554.getValue());

        if (!rs2R.isEmpty())
        {
            String rs2Tercero = (rs2R.get(terceroCons) == null ? ""
                : rs2R.get(terceroCons)).toString();
            String rs2Sucursal = (rs2R.get(sucursalCons) == null ? ""
                : rs2R.get(sucursalCons)).toString();

            Map<String, Object> parametrosTerceros = new HashMap();
            parametrosTerceros.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            parametrosTerceros.put(EstampillasControladorEnum.NIT.getValue(),
                            rs2Tercero);
            parametrosTerceros.put(GeneralParameterEnum.SUCURSAL.getName(),
                            rs2Sucursal);

            HashMap<String, Object> rsTercero = new HashMap();
            peticionSelect(parametrosTerceros,
                            EstampillasControladorUrlEnum.URL19479.getValue());

            double valorTotal = Double.parseDouble(
                            rs2R.get(valorTotalCons) == null ? "0"
                                : rs2R.get(valorTotalCons)
                                                .toString());

            valorTotal = tercero(rsTercero, valorTotal);
            int limiteProCultura = 298981830;

            if (valorTotal <= limiteProCultura)
            {
                procultura = SysmanFunciones.redondear(valorTotal * 0.01, -3);
            }

            double proadulto = SysmanFunciones.redondear(valorTotal * 0.03,
                            -3);

            Map<String, Object> parametrosDep = new HashMap();
            parametrosDep.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            parametrosDep.put(
                            EstampillasControladorEnum.TIPOCONTRATO.getValue(),
                            tipoContrato);
            parametrosDep.put(EstampillasControladorEnum.NUMCONTRATO.getValue(),
                            numeroContrato);

            rsdep = peticionSelect(parametrosDep,
                            EstampillasControladorUrlEnum.URL19591.getValue());

            if (Boolean.parseBoolean(rsdep == null ? "0"
                : rsdep.get(pagaProdeporteCons).toString()))
            {
                limiteValorDeporte = 21163450;
                if (valorTotal > limiteValorDeporte)
                {
                    prodeporte = SysmanFunciones.redondear(valorTotal * 0.01,
                                    -3);
                    textoVlEstDepVisible = true;
                }
            }
            else
            {
                registro.getCampos().put(valorProDeporteCons, prodeporte);
            }
            registro.getCampos().put(valorProculturaCons, procultura);
            registro.getCampos().put(valorProAdultoMayorCons, proadulto);
            registro.getCampos().put(valorProDeporteCons, prodeporte);
        }
        else
        {
            registro.getCampos().put(valorProculturaCons, 0);
            registro.getCampos().put(valorProAdultoMayorCons, 0);
            registro.getCampos().put(valorProDeporteCons, 0);
        }
        // </CODIGO_DESARROLLADO>
    }

    private double tercero(HashMap<String, Object> rsTercero,
        double valorTotal)
    {
        double aux = valorTotal;
        if ((rsTercero != null)
            && !"S".equals(rsTercero.get(regimenCons)))
        {
            aux = SysmanFunciones.redondear(
                            valorTotal
                                / Double.parseDouble(ivaestampillas),
                            2);

        }
        return aux;

    }

    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.asignarLlaveOLD(llave);
        css = registro.getLlave();
        registro.getLlave().put("KEY_COMPANIA", compania);
        registro.getLlave().put("KEY_CLASEORDEN", tipoContrato);
        registro.getLlave().put("KEY_NUMERO", numeroContrato);

        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.name());
        registro.getCampos()
                        .remove(EstampillasControladorEnum.PARAM1.getValue());
        registro.getCampos().remove(GeneralParameterEnum.NUMERO.getName());

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public String getNumeroContrato()
    {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato)
    {
        this.numeroContrato = numeroContrato;
    }

    public String getAnio()
    {
        return anio;
    }

    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    public String getTipoContrato()
    {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato)
    {
        this.tipoContrato = tipoContrato;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isTextoVlEstDepVisible()
    {
        return textoVlEstDepVisible;
    }

    public void setTextoVlEstDepVisible(boolean textoVlEstDepVisible)
    {
        this.textoVlEstDepVisible = textoVlEstDepVisible;
    }

    @Override
    public void cargarRegistro()
    {
        // Metodo heredado de la clase BeanBase
    }

    @Override
    public void iniciarListasSubNulo()
    {
        // Metodo heredado de la clase BeanBase
    }

    @Override
    public void iniciarListasSub()
    {
        // Metodo heredado de la clase BeanBase
    }

    @Override
    public void iniciarListas()
    {
        // Metodo heredado de la clase BeanBase
    }

    @Override
    public boolean eliminarAntes()
    {
        // Metodo heredado de la clase BeanBase
        return false;

    }

    @Override
    public boolean eliminarDespues()
    {
        // Metodo heredado de la clase BeanBase
        return false;
    }

}
