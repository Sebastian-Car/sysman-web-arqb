package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.FrminformecarteraControladorEnum;
import com.sysman.predial.enums.FrminformecarteraControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author lcortes
 * @version 1, 03/06/2016
 *
 * @author spina
 * @version 2, 04/07/2017 - se refactoriza para dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class FrminformecarteraControlador extends BeanBaseModal
{
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String codigoInicial;
    private String codigoFinal;
    private String nombrePropIni;
    private String nombrePropFin;
    private String cantVigencias;
    private String predioIncluye;
    private StreamedContent archivoDescarga;
    private static final String THEN = " THEN ";

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private int[] vecCodCcpI;

    /**
     * Creates a new instance of FrminformecarteraControlador
     */
    public FrminformecarteraControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMINFORMECARTERA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrminformecarteraControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init()
    {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCodigoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminformecarteraControladorUrlEnum.URL3613
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public Boolean validarVacios()
    {

        if (SysmanFunciones.validarVariableVacio(codigoInicial)
            || SysmanFunciones.validarVariableVacio(codigoFinal)
            || SysmanFunciones.validarVariableVacio(cantVigencias)
            || SysmanFunciones.validarVariableVacio(predioIncluye))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB580"));
            return false;
        }

        return true;
    }

    public void cargarListaCodigoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminformecarteraControladorUrlEnum.URL4743
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(FrminformecarteraControladorEnum.CODIGOINICIAL.getValue(),
                        codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cantidadVigencias(StringBuilder vigencias, StringBuilder vigCap,
        StringBuilder vigInt, StringBuilder ccpK, StringBuilder ccpI,
        int anioActual, int i)
    {

        if (cantVigencias.equals(String.valueOf(i)))
        {
            vigencias.append("SUM(CASE WHEN PREANO <= "
                + (anioActual - i) + THEN
                + (SysmanFunciones.validarVariableVacio(ccpK.toString()) ? "0"
                    : ccpK.toString())
                + " ELSE 0 END) AS CAPITAL_ANT_" + ((anioActual - i) + 1)
                + " \n");
            vigCap.append("(CASE WHEN PREANO <= " + (anioActual - i)
                + THEN + (SysmanFunciones.validarVariableVacio(ccpK.toString())
                    ? "0" : ccpK.toString())
                + " ELSE  0 END) \n");
            vigencias.append(", " + "SUM(CASE WHEN PREANO <= "
                + (anioActual - i) + " THEN    "
                + (SysmanFunciones.validarVariableVacio(ccpI.toString()) ? "0"
                    : ccpI.toString())
                + " ELSE 0 END) AS INTERES_ANT_" + ((anioActual - i) + 1)
                + " \n");
            vigInt.append("(CASE WHEN PREANO <= " + (anioActual - i)
                + THEN + (SysmanFunciones.validarVariableVacio(ccpI.toString())
                    ? "0" : ccpI.toString())
                + " ELSE 0 END) \n");

        }
        else
        {
            vigencias.append(", " + "SUM(CASE WHEN PREANO = "
                + (anioActual - i) + THEN
                + (SysmanFunciones.validarVariableVacio(ccpK.toString()) ? "0"
                    : ccpK.toString())
                + " ELSE 0 END) AS CAPITAL_" + (anioActual - i) + " \n");
            vigCap.append(" + " + "(CASE WHEN PREANO = "
                + (anioActual - i) + THEN
                + (SysmanFunciones.validarVariableVacio(ccpK.toString()) ? "0"
                    : ccpK.toString())
                + " ELSE 0 END) \n ");
            vigencias.append(", " + "SUM(CASE WHEN PREANO = "
                + (anioActual - i) + THEN
                + (SysmanFunciones.validarVariableVacio(ccpI.toString()) ? "0"
                    : ccpI.toString())
                + " ELSE 0 END) AS INTERES_" + (anioActual - i) + " \n");
            vigInt.append(" + " + "(CASE WHEN PREANO = "
                + (anioActual - i) + THEN
                + (SysmanFunciones.validarVariableVacio(ccpI.toString()) ? "0"
                    : ccpI.toString())
                + " ELSE 0 END) \n");

        }
    }

    private boolean buscarVect(int j)
    {

        for (int i = 0; i < vecCodCcpI.length; i++)
        {
            if (vecCodCcpI[i] == j)
            {
                return true;
            }
        }

        return false;
    }

    public void facturados(int j, StringBuilder ccpI, StringBuilder ccpK)
    {
        if ((j < 5) || (j > 12))
        {

            if (buscarVect(j))
            {
                ccpI.append(ccpI.toString().isEmpty() ? " FACTURADOS.C" + j
                    : "+ FACTURADOS.C" + j);
            }
            else
            {
                ccpK.append(ccpK.toString().isEmpty() ? " FACTURADOS.C" + j
                    : "+ FACTURADOS.C" + j);
            }

        }
    }

    public void predioIncluye(StringBuilder strIncluye)
    {
        if ("Activos".equals(predioIncluye))
        {

            strIncluye.append(
                            "AND NVL(USUARIOS_PREDIAL.INDBORRADO,0) IN(0) AND NVL(USUARIOS_PREDIAL.CODIGO_NO_ACTIVO,0) IN(0) ");
        }
        else if ("Bloqueados".equals(predioIncluye))
        {

            strIncluye.append(
                            "AND NVL(USUARIOS_PREDIAL.INDBORRADO,0) IN(0) AND NVL(USUARIOS_PREDIAL.CODIGO_NO_ACTIVO,0) IN(0) AND NVL(USUARIOS_PREDIAL.BLOQUEADO,0) NOT IN(0) ");

        }
        else if ("Activos sin bloqueo".equals(predioIncluye))
        {
            strIncluye.append(
                            "AND NVL(USUARIOS_PREDIAL.INDBORRADO,0) IN(0) AND NVL(USUARIOS_PREDIAL.CODIGO_NO_ACTIVO,0) IN(0) AND NVL(USUARIOS_PREDIAL.BLOQUEADO,0) IN(0) ");
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirCmdExcel()
    {
        if (!validarVacios())
        {
            return;
        }
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        try
        {
            generarReporte();
        }
        catch (IOException | JRException | NamingException | SQLException
                        | DRException | ParseException | SysmanException
                        | SystemException e)
        {
            Logger.getLogger(FrminformecarteraControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void generarReporte()
                    throws IOException, NamingException, SQLException,
                    SystemException, JRException, DRException, SysmanException,
                    ParseException
    {
        StringBuilder vigencias = new StringBuilder();
        StringBuilder vigCap = new StringBuilder();
        StringBuilder vigInt = new StringBuilder();
        StringBuilder configurar = new StringBuilder("");
        StringBuilder pI;
        StringBuilder pK;
        String vig;
        String vigC;
        String vigI;

        for (int i = Integer.parseInt(cantVigencias); i >= 0; i--)
        {
            // consultar en la tabla conceptos que conceptos se han
            // configurado para el manejo de interes
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.NUMERO.getName(), i);
            List<Registro> rs = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrminformecarteraControladorUrlEnum.URL4744
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            if (!rs.isEmpty())
            {
                sinConceptos(rs);

            }
            if (vecCodCcpI != null)
            {
                StringBuilder ccpI = new StringBuilder();
                StringBuilder ccpK = new StringBuilder();
                for (int j = 1; j <= 20; j++)
                {
                    facturados(j, ccpI, ccpK);
                }
                pI = ccpI;
                pK = ccpK;

            }
            else
            {
                configurar.append(Integer.toString(i) + ",");
                pI = new StringBuilder("");
                pK = new StringBuilder("");

            }

            int anioActual = SysmanFunciones.ano(new Date());

            cantidadVigencias(vigencias, vigCap, vigInt, pK, pI,
                            anioActual,
                            i);

        }

        vig = vigencias.toString();
        vigC = vigCap.toString();
        vigI = vigInt.toString();

        StringBuilder strIncluye = new StringBuilder();

        predioIncluye(strIncluye);
        HashMap<String, Object> reemplazos = new HashMap<>();

        // Reemplazos valores consulta informe
        reemplazos.put("vigencias", vig);
        reemplazos.put("vigCap", vigC);
        reemplazos.put("vigInt", vigI);
        reemplazos.put("codigoInicial", codigoInicial);
        reemplazos.put("codigoFinal", codigoFinal);
        reemplazos.put("strIncluye", strIncluye.toString());
        reemplazos.put("numerOrden", "'001'");
        String strSql = Reporteador.resuelveConsulta("800051InfCartera",
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazos);

        long total = service.getConteoConsulta(strSql);

        if (total > 0)
        {
            Workbook workbook = new HSSFWorkbook(
                            JsfUtil.exportarHojaDatosStreamed(strSql,
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            FORMATOS.EXCEL97).getStream());
            Sheet sheet = workbook.getSheet("Report");
            sheet.shiftRows(0, sheet.getLastRowNum(), 4);

            CellReference cellRefIniTitulo = new CellReference(0, 0);
            String celdaIniTitulo = cellRefIniTitulo.formatAsString();
            CellReference cellRefFinTitulo = new CellReference(0,
                            Math.max(sheet.getRow(4).getLastCellNum(), 0)
                                - 1);
            String celdaFinTitulo = cellRefFinTitulo.formatAsString();
            CellRangeAddress region = CellRangeAddress.valueOf(
                            "" + celdaIniTitulo + ":" + celdaFinTitulo);
            sheet.addMergedRegion(region);

            CellReference cellRefIniTitulo1 = new CellReference(1, 0);
            String celdaIniTitulo1 = cellRefIniTitulo1.formatAsString();
            CellReference cellRefFinTitulo1 = new CellReference(1,
                            Math.max(sheet.getRow(4).getLastCellNum(), 0)
                                - 1);
            String celdaFinTitulo1 = cellRefFinTitulo1.formatAsString();
            CellRangeAddress region1 = CellRangeAddress.valueOf(
                            "" + celdaIniTitulo1 + ":" + celdaFinTitulo1);
            sheet.addMergedRegion(region1);

            CellReference cellRefIniTitulo2 = new CellReference(2, 0);
            String celdaIniTitulo2 = cellRefIniTitulo2.formatAsString();
            CellReference cellRefFinTitulo2 = new CellReference(2,
                            Math.max(sheet.getRow(4).getLastCellNum(), 0)
                                - 1);
            String celdaFinTitulo2 = cellRefFinTitulo2.formatAsString();
            CellRangeAddress region2 = CellRangeAddress.valueOf(
                            "" + celdaIniTitulo2 + ":" + celdaFinTitulo2);
            sheet.addMergedRegion(region2);

            CellStyle style = workbook.createCellStyle();
            style.setAlignment(CellStyle.ALIGN_CENTER);
            Font font = workbook.createFont();
            font.setFontName("SansSerif");
            font.setBold(true);
            style.setFont(font);
            Cell cell = sheet.createRow(0).createCell(0);
            cell.setCellValue(idioma.getString("TB_TB1683"));
            cell.setCellStyle(style);
            Cell cell2 = sheet.createRow(1).createCell(0);
            String textoCelda = idioma.getString("TB_TB1684");
            textoCelda = textoCelda.replace("s$codigoInicial$s",
                            codigoInicial);
            textoCelda = textoCelda.replace("s$codigoFinal$s", codigoFinal);
            cell2.setCellValue(textoCelda);
            cell2.setCellStyle(style);
            Cell cell3 = sheet.createRow(2).createCell(0);

            cell3.setCellValue(idioma.getString("TB_TB1685") + " "
                + SysmanFunciones.convertirAFechaCadena(new Date(),
                                "dd' - 'MMMM' - 'yyyy"));

            cell3.setCellStyle(style);

            int filaFinal = sheet.getLastRowNum();
            sheet.createRow(filaFinal + 1).createCell(4);
            CellStyle styleNum = workbook.createCellStyle();
            for (int i = 6; i <= (Math.max(sheet.getRow(4).getLastCellNum(),
                            0)
                - 1); i++)
            {
                Cell cellTotalesFormula = sheet.getRow(filaFinal + 1)
                                .createCell(i);
                cellTotalesFormula.setCellType(Cell.CELL_TYPE_FORMULA);
                CellReference cellRefIni = new CellReference(5, i);
                CellReference cellRefFin = new CellReference(filaFinal, i);
                String celdaIni = cellRefIni.formatAsString();
                String celdaFin = cellRefFin.formatAsString();
                cellTotalesFormula.setCellFormula(
                                "SUM(" + celdaIni + ":" + celdaFin + ")");
                styleNum.setDataFormat(workbook.createDataFormat()
                                .getFormat("$ #,##0.00"));
                cellTotalesFormula.setCellStyle(styleNum);
            }

            workbook.setForceFormulaRecalculation(true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            "800051InfCartera.xls");
            workbook.close();
            if (!"".equals(configurar.toString()))
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2894").replace(
                                                "s$configuracion$s",
                                                configurar.toString()));
            }
        }
        else
        {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB2842"));
        }
    }

    private void sinConceptos(List<Registro> rs)
    {
        vecCodCcpI = new int[rs.size()];
        int aux = 0;
        for (Registro registro : rs)
        {

            vecCodCcpI[aux] = Integer.parseInt(
                            "" + registro.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName()));
            aux++;

        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCodigoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        nombrePropIni = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
        codigoFinal = "";
        nombrePropFin = "";
        cargarListaCodigoFinal();
    }

    public void seleccionarFilaCodigoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        nombrePropFin = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getCodigoInicial()
    {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial)
    {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal()
    {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal)
    {
        this.codigoFinal = codigoFinal;
    }

    public String getNombrePropIni()
    {
        return nombrePropIni;
    }

    public void setNombrePropIni(String nombrePropIni)
    {
        this.nombrePropIni = nombrePropIni;
    }

    public String getNombrePropFin()
    {
        return nombrePropFin;
    }

    public void setNombrePropFin(String nombrePropFin)
    {
        this.nombrePropFin = nombrePropFin;
    }

    public String getCantVigencias()
    {
        return cantVigencias;
    }

    public void setCantVigencias(String cantVigencias)
    {
        this.cantVigencias = cantVigencias;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCodigoInicial()
    {
        return listaCodigoInicial;
    }

    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial)
    {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    public RegistroDataModelImpl getListaCodigoFinal()
    {
        return listaCodigoFinal;
    }

    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal)
    {
        this.listaCodigoFinal = listaCodigoFinal;
    }

    public String getPredioIncluye()
    {
        return predioIncluye;
    }

    public void setPredioIncluye(String predioIncluye)
    {
        this.predioIncluye = predioIncluye;
    }

}
