package com.sysman.mantenimientoactivos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.mantenimientoactivos.enums.IprogramaciondemantenimientoexcelControladorEnum;
import com.sysman.mantenimientoactivos.enums.IprogramaciondemantenimientoexcelControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author NGOMEZ
 * @version 1, 13/01/2016
 * @author jcrodriguez, Refactoring y Depuracion
 * @version 2,16/08/2017
 */
@ManagedBean
@ViewScoped

public class IprogramaciondemantenimientoexcelControlador extends BeanBaseModal
{

    private final String compania;
    private String elementoDesde;
    private String elementoHasta;
    private Date desde;
    private String lblDesde;
    private String lblHasta;
    private Date hasta;
    private StreamedContent archivoDescarga;
    private ContenedorArchivo contArchivoSelector;
    private RegistroDataModelImpl listacmbActivoDesde;
    private RegistroDataModelImpl listacmbActivoHasta;

    /**
     * Creates a new instance of
     * IprogramaciondemantenimientoexcelControlador
     */
    public IprogramaciondemantenimientoexcelControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.IPROGRAMACIONDEMANTENIMIENTOEXCEL_CONTROLADOR.getCodigo();
            contArchivoSelector = new ContenedorArchivo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(IprogramaciondemantenimientoexcelControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        cargarListacmbActivoDesde();
        cargarListacmbActivoHasta();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // NO SE IMPLEMENTA
    }

    public void cargarListacmbActivoDesde()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(IprogramaciondemantenimientoexcelControladorUrlEnum.URL3542.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbActivoDesde = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());

    }

    public void cargarListacmbActivoHasta()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(IprogramaciondemantenimientoexcelControladorUrlEnum.URL4298.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.ELEMENTO.getName(), elementoDesde);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbActivoHasta = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());
    }

    public void oprimircmdPantalla()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (contArchivoSelector.getArchivo() == null)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1291"));
            return;
        }
        List<Registro> aux = listarInventario();

        if (aux.isEmpty())
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2479"));
        }
        else
        {
            if ("gsb".equals(contArchivoSelector.getArchivo().getName().substring(0, 3)))
            {
                generarArchivo(aux);
            }
            else
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3724"));
            }

        }
        // </CODIGO_DESARROLLADO>
    }

    public List<Registro> listarInventario()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(IprogramaciondemantenimientoexcelControladorUrlEnum.URL4222.getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(IprogramaciondemantenimientoexcelControladorEnum.ELEMENTODESDE.getValue(), elementoDesde);
        param.put(IprogramaciondemantenimientoexcelControladorEnum.ELEMENTOHASTA.getValue(), elementoHasta);
        param.put(IprogramaciondemantenimientoexcelControladorEnum.FECHADESDEAUX.getValue(), desde);
        param.put(IprogramaciondemantenimientoexcelControladorEnum.FECHAHASTAAUX.getValue(), hasta);
        List<Registro> lista = null;
        try
        {
            lista = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return lista;
    }

    public Cell crearCelda(Sheet sheet, int v, Workbook workbook,
        int inicioElemento, CellStyle estiloTitulo,
        String nombreAnterior)
    {
        Cell celda;
        CellRangeAddress reg = new CellRangeAddress(
                        v - 1, inicioElemento, 0, 0);
        sheet.addMergedRegion(reg);
        RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, reg,
                        sheet, workbook);
        RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, reg,
                        sheet, workbook);
        RegionUtil.setBorderRight(CellStyle.BORDER_THIN, reg,
                        sheet, workbook);
        RegionUtil.setBorderTop(CellStyle.BORDER_THIN, reg,
                        sheet, workbook);

        celda = sheet.getRow(inicioElemento).createCell(0);
        celda.setCellStyle(estiloTitulo);
        celda.setCellValue(nombreAnterior);
        return celda;
    }

    public CellStyle crearEstiloTitulo(Workbook workbook, Font font)
    {
        CellStyle estiloTitulo = workbook.createCellStyle();
        estiloTitulo.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        estiloTitulo.setFont(font);
        estiloTitulo.setBorderBottom(CellStyle.BORDER_THIN);
        estiloTitulo.setBorderLeft(CellStyle.BORDER_THIN);
        estiloTitulo.setBorderRight(CellStyle.BORDER_THIN);
        estiloTitulo.setBorderTop(CellStyle.BORDER_THIN);
        return estiloTitulo;
    }

    public CellStyle crearEstiloContenido(Workbook workbook, Font font)
    {
        CellStyle estiloContenido = workbook.createCellStyle();
        estiloContenido.setAlignment(CellStyle.ALIGN_CENTER);
        estiloContenido.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        estiloContenido.setFont(font);
        estiloContenido.setBorderBottom(CellStyle.BORDER_THIN);
        estiloContenido.setBorderLeft(CellStyle.BORDER_THIN);
        estiloContenido.setBorderRight(CellStyle.BORDER_THIN);
        estiloContenido.setBorderTop(CellStyle.BORDER_THIN);
        return estiloContenido;
    }

    public CellStyle crearEstiloContenido2(Workbook workbook)
    {
        CellStyle estiloContenido2 = workbook.createCellStyle();
        Font font2 = workbook.createFont();
        font2.setFontHeightInPoints((short) 12);
        font2.setFontName(IprogramaciondemantenimientoexcelControladorEnum.ARIAL.getValue());
        estiloContenido2.setFont(font2);
        estiloContenido2.setAlignment(CellStyle.ALIGN_CENTER);
        estiloContenido2.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        estiloContenido2.setBorderBottom(CellStyle.BORDER_DOTTED);
        estiloContenido2.setBorderLeft(CellStyle.BORDER_THIN);
        estiloContenido2.setBorderRight(CellStyle.BORDER_THIN);
        estiloContenido2.setBorderTop(CellStyle.BORDER_DOTTED);
        return estiloContenido2;
    }

    public boolean evaluarCondiciones(Registro aux1, String placaAnterior,
        String codigoAnterior)
    {
        if (!aux1.getCampos().get(IprogramaciondemantenimientoexcelControladorEnum.MARCA1.getValue()).equals(placaAnterior)
            && aux1.getCampos().get(GeneralParameterEnum.CODIGOELEMENTO.getName())
                            .equals(codigoAnterior)
            || !aux1.getCampos().get(GeneralParameterEnum.CODIGOELEMENTO.getName())
                            .equals(codigoAnterior))
        {
            return true;
        }
        return false;
    }

    public void generarArchivo(List<Registro> aux)
    {

        int v = 15;
        int h = 0;
        try (FileInputStream file = new FileInputStream(
                        contArchivoSelector.getArchivo()))
        {
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(1);
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 12);
            font.setFontName(IprogramaciondemantenimientoexcelControladorEnum.ARIAL.getValue());
            font.setBold(true);
            Row fila;

            for (Registro aux1 : aux)
            {
                sheet.shiftRows(v, sheet.getLastRowNum(), 1);
                fila = sheet.createRow(v);

                llenarCeldas(h, aux1, fila, workbook, font);

                v = v + 1;
                h = 0;
            }

            ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
            workbook.write(fileOut);
            fileOut.close();
            file.close();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(fileOut.toByteArray()),
                            idioma.getString("TB_TB3469"));

        }
        catch (FileNotFoundException ex)

        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2079"));
            Logger.getLogger(IprogramaciondemantenimientoexcelControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException | JRException ex)

        {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"), ex.getMessage()));
            Logger.getLogger(IprogramaciondemantenimientoexcelControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * metodo utilizado para concatenar los meses de todo el ano
     * 
     * @param meses
     */
    private void concatenarMeses(StringBuilder meses)
    {
        meses.append(IprogramaciondemantenimientoexcelControladorEnum.ENEROP.getValue());
        meses.append(",");
        meses.append(IprogramaciondemantenimientoexcelControladorEnum.ENEROE.getValue());
        meses.append(",");
        meses.append(IprogramaciondemantenimientoexcelControladorEnum.FEBREROP.getValue());
        meses.append(",");
        meses.append(IprogramaciondemantenimientoexcelControladorEnum.FEBREROE.getValue());
        meses.append(",");
        meses.append(IprogramaciondemantenimientoexcelControladorEnum.MARZOP.getValue());
        meses.append(",");
        meses.append(IprogramaciondemantenimientoexcelControladorEnum.MARZOE.getValue());
        meses.append(",");
        meses.append(IprogramaciondemantenimientoexcelControladorEnum.ABRILP.getValue());
        meses.append(",");
        meses.append(IprogramaciondemantenimientoexcelControladorEnum.ABRILE.getValue());
        meses.append(",");
        meses.append(IprogramaciondemantenimientoexcelControladorEnum.MAYOP.getValue());
        meses.append(",");
        meses.append(IprogramaciondemantenimientoexcelControladorEnum.MAYOE.getValue());
        meses.append(",");
        meses.append(IprogramaciondemantenimientoexcelControladorEnum.JUNIOP.getValue());
        meses.append(",");
        meses.append(IprogramaciondemantenimientoexcelControladorEnum.JUNIOE.getValue());
        meses.append(",");
        meses.append(IprogramaciondemantenimientoexcelControladorEnum.JULIOP.getValue());
        meses.append(",");
        meses.append(IprogramaciondemantenimientoexcelControladorEnum.JULIOE.getValue());
        meses.append(",");
        meses.append(IprogramaciondemantenimientoexcelControladorEnum.AGOSTOP.getValue());
        meses.append(",");
        meses.append(IprogramaciondemantenimientoexcelControladorEnum.AGOSTOE.getValue());
        meses.append(",");
        meses.append(IprogramaciondemantenimientoexcelControladorEnum.SEPTIEMBREP.getValue());
        meses.append(",");
        meses.append(IprogramaciondemantenimientoexcelControladorEnum.SEPTIEMBREE.getValue());
        meses.append(",");
        meses.append(IprogramaciondemantenimientoexcelControladorEnum.OCTUBREP.getValue());
        meses.append(",");
        meses.append(IprogramaciondemantenimientoexcelControladorEnum.OCTUBREE.getValue());
        meses.append(",");
        meses.append(IprogramaciondemantenimientoexcelControladorEnum.NOVIEMBREP.getValue());
        meses.append(",");
        meses.append(IprogramaciondemantenimientoexcelControladorEnum.NOVIEMBREE.getValue());
        meses.append(",");
        meses.append(IprogramaciondemantenimientoexcelControladorEnum.DICIEMBREP.getValue());
        meses.append(",");
        meses.append(IprogramaciondemantenimientoexcelControladorEnum.DICIEMBREE.getValue());
    }

    public void llenarCeldas(int h, Registro aux1, Row fila, Workbook workbook,
        Font font)
    {
        StringBuilder meses = new StringBuilder();
        concatenarMeses(meses);

        Cell celda;
        CellStyle estiloContenido = crearEstiloContenido(workbook, font);
        CellStyle estiloContenido2 = crearEstiloContenido2(workbook);
        celda = fila.createCell(h + 0);
        estiloContenido.setWrapText(true);
        celda.setCellStyle(estiloContenido);
        celda.setCellValue(validarCadena(aux1.getCampos(), IprogramaciondemantenimientoexcelControladorEnum.NOMBRELARGO.getValue()));
        celda = fila.createCell(h + 1);
        celda.setCellStyle(estiloContenido);
        celda.setCellValue(validarCadena(aux1.getCampos(), IprogramaciondemantenimientoexcelControladorEnum.MARCA1.getValue()));
        celda = fila.createCell(h + 2);
        celda.setCellStyle(estiloContenido);
        celda.setCellValue(validarCadena(aux1.getCampos(), IprogramaciondemantenimientoexcelControladorEnum.FRECUENCIA.getValue()));
        celda = fila.createCell(h + 3);
        celda.setCellStyle(estiloContenido2);
        celda.setCellValue(validarCadena(aux1.getCampos(), GeneralParameterEnum.NOMBRE.getName()));
        int j = 0;
        for (int i = 4; i <= 26; i += 2, j += 2)
        {
            construirCuerpo(fila, estiloContenido, aux1, meses, h, i, j);
        }
    }

    /**
     * metodo que adiciona el valor corrspondiente para cada una de
     * las celdas de acuerdo a las columnas del documento
     * 
     * @param fila
     * @param estiloContenido
     * @param aux1
     * @param meses
     * @param h
     * @param i
     * @param j
     */
    private void construirCuerpo(Row fila, CellStyle estiloContenido, Registro aux1, StringBuilder meses, int h, int i, int j)
    {
        String mes = meses.toString();

        Cell celda = fila.createCell(h + i);
        celda.setCellStyle(estiloContenido);
        celda.setCellValue(
                        !("0").equals(validarCadena(aux1.getCampos(), mes.split(",")[j]))
                            ? "X"
                            : "");

        celda = fila.createCell(h + (i + 1));
        celda.setCellStyle(estiloContenido);
        celda.setCellValue(
                        !("0").equals(validarCadena(aux1.getCampos(), mes.split(",")[j + 1]))
                            ? "X"
                            : "");

    }

    public void seleccionarFilacmbActivoDesde(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        elementoDesde = validarCadena(registroAux.getCampos(), GeneralParameterEnum.CODIGOELEMENTO.getName());
        elementoHasta = null;
        lblHasta = null;
        lblDesde = validarCadena(registroAux.getCampos(), IprogramaciondemantenimientoexcelControladorEnum.NOMBRECORTO.getValue());
        cargarListacmbActivoHasta();
    }

    public void seleccionarFilacmbActivoHasta(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        elementoHasta = validarCadena(registroAux.getCampos(), GeneralParameterEnum.CODIGOELEMENTO.getName());
        lblHasta = validarCadena(registroAux.getCampos(), IprogramaciondemantenimientoexcelControladorEnum.NOMBRECORTO.getValue());
    }

    private String validarCadena(Map<String, Object> campos, String var)
    {
        return SysmanFunciones.validarCampoVacio(campos, var) ? "" : campos.get(var).toString();
    }

    /**
     * metodos get y set
     * 
     * @return
     */
    public String getElementoDesde()
    {
        return elementoDesde;
    }

    public void setElementoDesde(String elementoDesde)
    {
        this.elementoDesde = elementoDesde;
    }

    public String getElementoHasta()
    {
        return elementoHasta;
    }

    public void setElementoHasta(String elementoHasta)
    {
        this.elementoHasta = elementoHasta;
    }

    public String getLblDesde()
    {
        return lblDesde;
    }

    public void setLblDesde(String lblDesde)
    {
        this.lblDesde = lblDesde;
    }

    public String getLblHasta()
    {
        return lblHasta;
    }

    public void setLblHasta(String lblHasta)
    {
        this.lblHasta = lblHasta;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public ContenedorArchivo getContArchivoSelector()
    {
        return contArchivoSelector;
    }

    public void setContArchivoSelector(ContenedorArchivo contArchivoSelector)
    {
        this.contArchivoSelector = contArchivoSelector;
    }

    public RegistroDataModelImpl getListacmbActivoDesde()
    {
        return listacmbActivoDesde;
    }

    public void setListacmbActivoDesde(RegistroDataModelImpl listacmbActivoDesde)
    {
        this.listacmbActivoDesde = listacmbActivoDesde;
    }

    public RegistroDataModelImpl getListacmbActivoHasta()
    {
        return listacmbActivoHasta;
    }

    public void setListacmbActivoHasta(RegistroDataModelImpl listacmbActivoHasta)
    {
        this.listacmbActivoHasta = listacmbActivoHasta;
    }

    public Date getDesde()
    {
        return desde;
    }

    public void setDesde(Date desde)
    {
        this.desde = desde;
    }

    public Date getHasta()
    {
        return hasta;
    }

    public void setHasta(Date hasta)
    {
        this.hasta = hasta;
    }

}
