/*-
 * LisejecucioningresospaControlador.java
 *
 * 1.0
 *
 * 06/12/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

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
import com.sysman.presupuesto.enums.LisejecucioningresospaControladorEnum;
import com.sysman.presupuesto.enums.LisejecucioningresospaControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 06/12/2017
 * @author spina
 */
@ManagedBean
@ViewScoped
public class LisejecucioningresospaControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     */
    private RegistroDataModelImpl listaCuentaInicial;
    /**
     */
    private RegistroDataModelImpl listaCuentaFinal;
    /**
     */
    private List<Registro> listaAno;
    /**
     */
    private String ano;

    private int mes;

    private String cuentaInicial;

    private String cuentaFinal;

    private boolean auxiliares;

    private boolean especial;

    private boolean especialexcel;
    
    private boolean reconocimientos;

    private StreamedContent archivoDescarga;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de LisejecucioningresospaControlador
     */
    public LisejecucioningresospaControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.LISEJECUCIONINGRESOSPA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        cargarListaAno();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCuentaInicial
     *
     */
    public void cargarListaCuentaInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisejecucioningresospaControladorUrlEnum.URL9260
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaCuentaFinal
     *
     */
    public void cargarListaCuentaFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisejecucioningresospaControladorUrlEnum.URL9261
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(LisejecucioningresospaControladorEnum.CUENTAINICIAL
                        .getValue(), cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LisejecucioningresospaControladorUrlEnum.URL9262
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>

    public void cambiarAno()
    {
        cuentaInicial = null;
        cuentaFinal = null;
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()) == null ? ""
                            : registroAux.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName())
                                            .toString();
        cuentaFinal = null;
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()) == null ? ""
                            : registroAux.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName())
                                            .toString();
    }

    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al z el boton excel en la vista
     *
     *
     */
    public void oprimirexcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        String reporte = "LISEJECUCIONINGRESOS_EXCELESPECIAL";
        if(reconocimientos)
        {
        	reporte = "800615LISEJECUCIONINGRESOSRECONOCIMIENTOS";
        }
        else if (!especialexcel)
        {
            reporte = "LISEJECUCIONINGRESOS_EXCEL";
        }
        

        Map<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("ano", ano);
        reemplazar.put("mes", mes);
        reemplazar.put("codigoInicial", cuentaInicial);
        reemplazar.put("codigoFinal", cuentaFinal);

        String sql = Reporteador.resuelveConsulta(
                        reporte,
                        Integer.parseInt(modulo), reemplazar);

        try (

                        ByteArrayOutputStream out = new ByteArrayOutputStream();)

        {
            long total = service.getConteoConsulta(sql);
            if (total > 0)
            {

                Workbook workbook = new XSSFWorkbook(
                                JsfUtil.exportarHojaDatosStreamed(sql,
                                                ConectorPool.ESQUEMA_SYSMAN,
                                                FORMATOS.EXCEL).getStream());

                Sheet sheet = workbook.getSheet("Report");
                sheet.shiftRows(0, sheet.getLastRowNum(), 4);

                sheet.setAutobreaks(true);

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
                cell.setCellValue(SessionUtil.getCompaniaIngreso().getNombre());
                cell.setCellStyle(style);

                Cell cell2 = sheet.createRow(1).createCell(0);
                cell2.setCellValue(idioma.getString("TB_TB3922"));
                cell2.setCellStyle(style);

                workbook.setForceFormulaRecalculation(true);
                workbook.write(out);
                out.close();
                workbook.close();
                archivoDescarga = JsfUtil.getArchivoDescarga(
                                new ByteArrayInputStream(out.toByteArray()),
                                SysmanFunciones.concatenar(reporte, ".xlsx"));
            }
            else
            {
                JsfUtil.agregarMensajeAlerta(idioma
                                .getString("TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS2"));
            }

        }
        catch (SQLException | JRException | IOException | DRException |

        SysmanException | SystemException ex)

        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            logger.error(ex.getMessage(), ex);
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     *
     */
    public void oprimirImprimir()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        try
        {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();

            String reporte;

            if ("SI".equals(ejbSysmanUtil.consultarParametro(compania,
                            "FORMATO ESPECIAL EJECUCION DE INGRESOS TRINIDAD",
                            modulo, new Date(), true)))
            {
                reporte = "001614CCEJECUCIONINGRESOSTRINI";

                parametros.put("PR_NITCOMPANIA",
                                SessionUtil.getCompaniaIngreso().getNit());

                parametros.put("PR_FIRMA_REPRESENTANTE_LEGAL",
                                ejbSysmanUtil.consultarParametro(compania,
                                                "FIRMA REPRESENTANTE LEGAL",
                                                modulo,
                                                new Date(), true));

                parametros.put("PR_NOMBRE_DE_SECRETARIA_DE_HACIENDA",
                                ejbSysmanUtil.consultarParametro(compania,
                                                "NOMBRE DE SECRETARIA DE HACIENDA",
                                                modulo,
                                                new Date(), true));

                parametros.put("PR_NOMBRE_REPRESENTANTE_LEGAL",
                                ejbSysmanUtil.consultarParametro(compania,
                                                "NOMBRE REPRESENTANTE LEGAL",
                                                modulo,
                                                new Date(), true));

            }
            else if (auxiliares)
            {
                reporte = "001612LisEjecucionIngresosPAESAUXILIARES";
            }
            else if (especial)
            {
                reporte = "001613LisEjecucionIngresosPAES";
            }
            else if (reconocimientos)
            {
                reporte = "002564LISEJECUCIONINGRESOSRECONOCIMIENTOS";
            }
            else
            {
                reporte = "001615LisEjecucionIngresosPA";
            }

            reemplazar.put("ano", ano);
            reemplazar.put("mes", mes);
            reemplazar.put("codigoInicial", cuentaInicial);
            reemplazar.put("codigoFinal", cuentaFinal);

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            parametros.put("PR_FORMS_LISEJECUCIONINGRESOS_PA_ANO", ano);
            parametros.put("PR_FORMS_LISEJECUCIONINGRESOS_PA_MES",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes]);

            parametros.put("PR_FIRMA_EJECUCION_1",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "FIRMA EJECUCION 1", modulo,
                                            new Date(), true));
            parametros.put("PR_CARGO_EJECUCION_1",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO EJECUCION 1", modulo,
                                            new Date(), true));

            parametros.put("PR_FIRMA_EJECUCION_2",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "FIRMA EJECUCION 2", modulo,
                                            new Date(), true));
            parametros.put("PR_CARGO_EJECUCION_2",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO EJECUCION 2", modulo,
                                            new Date(), true));

            parametros.put("PR_FIRMA_EJECUCION_3",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "FIRMA EJECUCION 3", modulo,
                                            new Date(), true));
            parametros.put("PR_CARGO_EJECUCION_3",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO EJECUCION 3", modulo,
                                            new Date(), true));

            Reporteador.resuelveConsulta(reporte,
                            Integer.valueOf(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            reporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            FORMATOS.PDF);
        }
        catch (SystemException | JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarEspecial()
    {
        if (especial)
        {
            auxiliares = especialexcel = reconocimientos = false;
        }
    }

    public void cambiarauxiliares()
    {
        if (auxiliares)
        {
            especialexcel = especial = reconocimientos = false;
        }

    }

    public void cambiarespecialexcel()
    {
        if (especialexcel)
        {
            auxiliares = especial = reconocimientos = false;
        }

    }
    
    public void cambiarReconocimietos()
    {
        if (reconocimientos)
        {
            auxiliares = especial = especialexcel = false;
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCuentaInicial
     *
     * @return listaCuentaInicial
     */
    public RegistroDataModelImpl getListaCuentaInicial()
    {
        return listaCuentaInicial;
    }

    /**
     * Asigna la lista listaCuentaInicial
     *
     * @param listaCuentaInicial
     * Variable a asignar en listaCuentaInicial
     */
    public void setListaCuentaInicial(RegistroDataModelImpl listaCuentaInicial)
    {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    /**
     * Retorna la lista listaCuentaFinal
     *
     * @return listaCuentaFinal
     */
    public RegistroDataModelImpl getListaCuentaFinal()
    {
        return listaCuentaFinal;
    }

    /**
     * Asigna la lista listaCuentaFinal
     *
     * @param listaCuentaFinal
     * Variable a asignar en listaCuentaFinal
     */
    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal)
    {
        this.listaCuentaFinal = listaCuentaFinal;
    }

    /**
     * Retorna la lista listaAno
     *
     * @return listaAno
     */
    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     *
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }

    public String getAno()
    {
        return ano;
    }

    public void setAno(String ano)
    {
        this.ano = ano;
    }

    public String getCuentaInicial()
    {
        return cuentaInicial;
    }

    public void setCuentaInicial(String cuentaInicial)
    {
        this.cuentaInicial = cuentaInicial;
    }

    public String getCuentaFinal()
    {
        return cuentaFinal;
    }

    public void setCuentaFinal(String cuentaFinal)
    {
        this.cuentaFinal = cuentaFinal;
    }

    public boolean isAuxiliares()
    {
        return auxiliares;
    }

    public void setAuxiliares(boolean auxiliares)
    {
        this.auxiliares = auxiliares;
    }

    public boolean isEspecial()
    {
        return especial;
    }

    public void setEspecial(boolean especial)
    {
        this.especial = especial;
    }

    public boolean isEspecialexcel()
    {
        return especialexcel;
    }

    public void setEspecialexcel(boolean especialexcel)
    {
        this.especialexcel = especialexcel;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public int getMes()
    {
        return mes;
    }

    public void setMes(int mes)
    {
        this.mes = mes;
    }

	/**
	 * @return the reconocimientos
	 */
	public boolean isReconocimientos() {
		return reconocimientos;
	}

	/**
	 * @param reconocimientos the reconocimientos to set
	 */
	public void setReconocimientos(boolean reconocimientos) {
		this.reconocimientos = reconocimientos;
	}

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
