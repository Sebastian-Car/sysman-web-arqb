/*-
 * GeneracionPlanosBancolombiaControlador.java
 *
 * 1.0
 * 
 * 15/08/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadTresRemote;
import com.sysman.contabilidad.enums.GeneracionPlanosBancolombiaControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
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
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Clase que permite la generación del archivo plano Bancolombia y las inconsistencias relacionadas.
 *
 * @version 1.0, 15/08/2018
 * @author jmalaver
 */
@ManagedBean
@ViewScoped
public class GeneracionPlanosBancolombiaControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que almacena el codigo del usuario
     */
    private final String usuario;

    // <DECLARAR_ATRIBUTOS>

    /**
     * Constante a nivel de clase que almacena el egreso inicial
     */
    private String egresoInicial;

    /**
     * Constante a nivel de clase que almacena el egreso final
     */
    private String egresoFinal;

    /**
     * Constante a nivel de clase que almacena el tipo de egreso
     */
    private String tipoEgreso;

    /**
     * Constante a nivel de clase que almacena el código banco
     */
    private String codigoBanco;

    /**
     * Constante a nivel de clase que almacena la cuenta inicial
     */
    private String cuentaInicial;

    /**
     * Constante a nivel de clase que almacena la cuenta final
     */
    private String cuentaFinal;

    /**
     * Constante a nivel de clase que almacena la cuenta debitar
     */
    private String cuentaDebitar;

    /**
     * Constante a nivel de clase que almacena el año
     */
    private int ano;

    /**
     * Constante a nivel de clase que almacena el nit cuenta
     */
    private String nitCuenta;

    /**
     * Constante a nivel de clase que almacena la clase transacción
     */
    private String claseTransaccion;

    /**
     * Constante a nivel de clase que almacena el tipo cuenta cliente
     */
    private String tipoCuentaCliente;

    /**
     * Constante a nivel de clase que almacena el tipo aplicación
     */
    private String tipoAplicacion;

    /**
     * Constante a nivel de clase que almacena la secuencia envios lotes
     */
    private String secuenciaEnviosLotes;

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;

    @EJB
    EjbContabilidadTresRemote ejbContabilidadTresRemote;

    @EJB
    EjbSysmanUtil ejbSysmanUtil;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    private List<Registro> listaCbAno;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Listado de registros para el combo CbCuentaInicial
     */
    private RegistroDataModelImpl listaCbCuentaInicial;

    /**
     * Listado de registros para el combo CbCuentaFinal
     */
    private RegistroDataModelImpl listaCbCuentaFinal;

    /**
     * Listado de registros para el combo CbEgresoInicial
     */
    private RegistroDataModelImpl listaCbEgresoInicial;

    /**
     * Listado de registros para el combo CbEgresoFinal
     */
    private RegistroDataModelImpl listaCbEgresoFinal;

    /**
     * Listado de registros para el combo CbTipoEgreso
     */
    private RegistroDataModelImpl listaCbTipoEgreso;

    /**
     * Listado de registros para el combo CbBanco
     */
    private RegistroDataModelImpl listaCbBanco;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de GeneracionPlanosBancolombiaControlador
     */
    public GeneracionPlanosBancolombiaControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser().getCodigo();
        ano = SysmanFunciones.ano(new Date());

        try
        {
            numFormulario = GeneralCodigoFormaEnum.GENERACION_PLANOS_BANCOLOMBIA_CONTROLADOR
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
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como son
     * tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        cargarListaCbAno();
        cargarListaCbCuentaInicial();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCbTipoEgreso();
        cargarListaCbBanco();
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
     * Carga la lista listaCbAno
     *
     */
    public void cargarListaCbAno()
    {
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaCbAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            GeneracionPlanosBancolombiaControladorUrlEnum.URL007
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCbEgresoInicial
     *
     */
    public void cargarListaCbEgresoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GeneracionPlanosBancolombiaControladorUrlEnum.URL001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.TIPO.getName(), tipoEgreso);

        listaCbEgresoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO");
    }

    /**
     * 
     * Carga la lista listaCbEgresoFinal
     *
     */
    public void cargarListaCbEgresoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GeneracionPlanosBancolombiaControladorUrlEnum.URL002
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.TIPO.getName(), tipoEgreso);
        param.put("NUMEROINICIAL", egresoInicial);

        listaCbEgresoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO");
    }

    /**
     * 
     * Carga la lista listaCbTipoEgreso
     *
     */
    public void cargarListaCbTipoEgreso()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GeneracionPlanosBancolombiaControladorUrlEnum.URL003
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCbTipoEgreso = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    /**
     * 
     * Carga la lista listaCbBanco
     *
     */
    public void cargarListaCbBanco()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GeneracionPlanosBancolombiaControladorUrlEnum.URL004
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCbBanco = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "BANCO");
    }

    /**
     * 
     * Carga la lista listaCbCuentaInicial
     *
     */
    public void cargarListaCbCuentaInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GeneracionPlanosBancolombiaControladorUrlEnum.URL005
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaCbCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    /**
     * 
     * Carga la lista listaCbCuentaFinal
     *
     */
    public void cargarListaCbCuentaFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GeneracionPlanosBancolombiaControladorUrlEnum.URL006
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put("CUENTAINICIAL", cuentaInicial);

        listaCbCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }
    // </METODOS_CARGAR_LISTA>

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnGenerarArchivo en la vista
     *
     */
    public void oprimirBtnGenerarArchivo()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control CbAno *
     * 
     */
    public void cambiarCbAno()
    {
        // <CODIGO_DESARROLLADO>
        cuentaFinal = null;
        cuentaInicial = null;
        egresoFinal = null;
        egresoInicial = null;
        tipoEgreso = null;

        cargarListaCbTipoEgreso();
        cargarListaCbCuentaInicial();
        cargarListaCbCuentaFinal();
        cargarListaCbEgresoInicial();
        cargarListaCbEgresoFinal();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCbEgresoInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbEgresoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        egresoInicial = registroAux.getCampos().get("NUMERO").toString();

        cargarListaCbEgresoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCbEgresoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbEgresoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        egresoFinal = registroAux.getCampos().get("NUMERO").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCbTipoEgreso
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbTipoEgreso(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        tipoEgreso = registroAux.getCampos().get("CODIGO").toString();

        cargarListaCbEgresoInicial();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCbBanco
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbBanco(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoBanco = registroAux.getCampos().get("BANCO").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCbCuentaInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbCuentaInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos().get("CODIGO").toString();

        cargarListaCbCuentaFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCbCuentaFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbCuentaFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos().get("CODIGO").toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    
    public void generarInforme(FORMATOS formato)
    {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream())
        {
            Map<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("compania", compania);
            reemplazar.put("ano", ano);
            reemplazar.put("tipo", tipoEgreso);
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("numeroInicial", egresoInicial);
            reemplazar.put("numeroFinal", egresoFinal);

            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametros.put(GeneralParameterEnum.USUARIO.getName(),
                            SessionUtil.getUser().getCodigo());
            parametros.put("NUMEROINICIAL", egresoInicial);
            parametros.put("NUMEROFINAL", egresoFinal);
            parametros.put("ANO", ano);
            parametros.put("TIPO", tipoEgreso);
            parametros.put("CUENTAINICIAL", cuentaInicial);
            parametros.put("CUENTAFINAL", cuentaFinal);

            String clob = ejbContabilidadTresRemote
                            .inconsistenciasPlanoBancolombia(
                                            compania, ano,
                                            tipoEgreso,
                                            cuentaInicial,
                                            cuentaFinal,
                                            egresoInicial,
                                            egresoFinal,
                                            usuario);

            if (clob.equals("0"))
            {
                String sql = Reporteador.resuelveConsulta(
                                "800192NuevoFormatoNomina",
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar);

                Workbook workbook = new XSSFWorkbook(
                                JsfUtil.exportarHojaDatosStreamed(sql,
                                                ConectorPool.ESQUEMA_SYSMAN,
                                                formato).getStream());

                Sheet sheet = workbook.getSheetAt(0);

                sheet.shiftRows(0, sheet.getLastRowNum(), 2);
                sheet.createFreezePane(0, 3);

                Font font2 = workbook.createFont();
                font2.setFontName("Calibri");
                font2.setFontHeightInPoints((short) 11);
                font2.setBold(false);

                CellStyle style2 = workbook.createCellStyle();
                style2.setAlignment(CellStyle.ALIGN_CENTER);
                style2.setFont(font2);
                style2.setBorderBottom(CellStyle.BORDER_THIN);
                style2.setBottomBorderColor(IndexedColors.BLACK.getIndex());
                style2.setBorderLeft(CellStyle.BORDER_THIN);
                style2.setLeftBorderColor(IndexedColors.BLACK.getIndex());
                style2.setBorderTop(CellStyle.BORDER_THIN);
                style2.setTopBorderColor(IndexedColors.BLACK.getIndex());
                style2.setBorderRight(CellStyle.BORDER_THIN);
                style2.setRightBorderColor(IndexedColors.BLACK.getIndex());

                Row r = sheet.createRow(0);

                Cell cell1 = r.createCell(0);
                cell1.setCellValue("NIT PAGADOR");

                cell1 = r.createCell(1);
                cell1.setCellValue("TIPO DE PAGO");
                
                cell1 = r.createCell(2);
                cell1.setCellValue("APLICACION");

                cell1 = r.createCell(3);
                cell1.setCellValue("SECUENCIA DE ENVÍO");

                cell1 = r.createCell(4);
                cell1.setCellValue("NRO CUENTA A DEBITAR");
                sheet.setColumnWidth(4, 6000);

                cell1 = r.createCell(5);
                cell1.setCellValue("TIPO DE CUENTA A DEBITAR");
                sheet.setColumnWidth(5, 9000);

                cell1 = r.createCell(6);
                cell1.setCellValue("DESCRIPCIÓN DEL PAGO");

                Row r1 = sheet.createRow(1);

                cell1 = r1.createCell(0);
                cell1.setCellValue(nitCuenta);

                cell1 = r1.createCell(1);
                cell1.setCellValue(claseTransaccion);
                
                cell1 = r1.createCell(2);
                cell1.setCellValue(tipoAplicacion);

                cell1 = r1.createCell(3);
                cell1.setCellValue(secuenciaEnviosLotes);

                cell1 = r1.createCell(4);
                cell1.setCellValue(cuentaDebitar);

                cell1 = r1.createCell(5);
                cell1.setCellValue(tipoCuentaCliente);

                cell1 = r1.createCell(6);
                cell1.setCellValue(ejbSysmanUtil.mostrarNombreDeMes(
                                SysmanFunciones.mes(new Date())));

                for (int i = 3; i < sheet.getLastRowNum() + 1; i++)
                {
                    Row r3 = sheet.getRow(i);
                    for (int j = 0; j < 12; j++)
                    {
                        Cell cell2 = r3.getCell(j);
                        cell2.setCellStyle(style2);
                    }
                }

                workbook.write(out);

                out.close();

                archivoDescarga = JsfUtil
                                .getArchivoDescarga(
                                                new ByteArrayInputStream(
                                                                out.toByteArray()),
                                                "NuevoFormatoNomina.xlsx");
                workbook.close();

                Parameter parameter = new Parameter();
                parameter.setFields(parametros);

                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GeneracionPlanosBancolombiaControladorUrlEnum.URL008
                                                                .getValue());
                requestManager.update(urlUpdate.getUrl(),
                                urlUpdate.getMetodo(),
                                parameter);

            }
            else
            {
                ByteArrayInputStream streamTexto = JsfUtil
                                .serializarPlano(clob);
                archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
                                "InconsistenciasTerceros.txt");
            }

        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException | NumberFormatException
                        | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage()
                            + ". Verifique que no hayan sido generados previamente.");
        }
    }
    
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>

    /**
     * Retorna la lista listaCbAno
     * 
     * @return listaCbAno
     */
    public List<Registro> getListaCbAno()
    {
        return listaCbAno;
    }

    /**
     * Asigna la lista listaCbAno
     * 
     * @param listaCbAno
     * Variable a asignar en listaCbAno
     */
    public void setListaCbAno(List<Registro> listaCbAno)
    {
        this.listaCbAno = listaCbAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCbEgresoInicial
     * 
     * @return listaCbEgresoInicial
     */
    public RegistroDataModelImpl getListaCbEgresoInicial()
    {
        return listaCbEgresoInicial;
    }

    /**
     * Asigna la lista listaCbEgresoInicial
     * 
     * @param listaCbEgresoInicial
     * Variable a asignar en listaCbEgresoInicial
     */
    public void setListaCbEgresoInicial(
                    RegistroDataModelImpl listaCbEgresoInicial)
    {
        this.listaCbEgresoInicial = listaCbEgresoInicial;
    }

    /**
     * Retorna la lista listaCbEgresoFinal
     * 
     * @return listaCbEgresoFinal
     */
    public RegistroDataModelImpl getListaCbEgresoFinal()
    {
        return listaCbEgresoFinal;
    }

    /**
     * Asigna la lista listaCbEgresoFinal
     * 
     * @param listaCbEgresoFinal
     * Variable a asignar en listaCbEgresoFinal
     */
    public void setListaCbEgresoFinal(
                    RegistroDataModelImpl listaCbEgresoFinal)
    {
        this.listaCbEgresoFinal = listaCbEgresoFinal;
    }

    /**
     * Retorna la lista listaCbTipoEgreso
     * 
     * @return listaCbTipoEgreso
     */
    public RegistroDataModelImpl getListaCbTipoEgreso()
    {
        return listaCbTipoEgreso;
    }

    /**
     * Asigna la lista listaCbTipoEgreso
     * 
     * @param listaCbTipoEgreso
     * Variable a asignar en listaCbTipoEgreso
     */
    public void setListaCbTipoEgreso(RegistroDataModelImpl listaCbTipoEgreso)
    {
        this.listaCbTipoEgreso = listaCbTipoEgreso;
    }

    /**
     * Retorna la lista listaCbBanco
     * 
     * @return listaCbBanco
     */
    public RegistroDataModelImpl getListaCbBanco()
    {
        return listaCbBanco;
    }

    /**
     * Asigna la lista listaCbBanco
     * 
     * @param listaCbBanco
     * Variable a asignar en listaCbBanco
     */
    public void setListaCbBanco(RegistroDataModelImpl listaCbBanco)
    {
        this.listaCbBanco = listaCbBanco;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * @return the egresoInicial
     */
    public String getEgresoInicial()
    {
        return egresoInicial;
    }

    /**
     * @param egresoInicial
     * the egresoInicial to set
     */
    public void setEgresoInicial(String egresoInicial)
    {
        this.egresoInicial = egresoInicial;
    }

    /**
     * @return the egresoFinal
     */
    public String getEgresoFinal()
    {
        return egresoFinal;
    }

    /**
     * @param egresoFinal
     * the egresoFinal to set
     */
    public void setEgresoFinal(String egresoFinal)
    {
        this.egresoFinal = egresoFinal;
    }

    /**
     * @return the tipoEgreso
     */
    public String getTipoEgreso()
    {
        return tipoEgreso;
    }

    /**
     * @param tipoEgreso
     * the tipoEgreso to set
     */
    public void setTipoEgreso(String tipoEgreso)
    {
        this.tipoEgreso = tipoEgreso;
    }

    /**
     * @return the codigoBanco
     */
    public String getCodigoBanco()
    {
        return codigoBanco;
    }

    /**
     * @param codigoBanco
     * the codigoBanco to set
     */
    public void setCodigoBanco(String codigoBanco)
    {
        this.codigoBanco = codigoBanco;
    }

    /**
     * @return the cuentaInicial
     */
    public String getCuentaInicial()
    {
        return cuentaInicial;
    }

    /**
     * @param cuentaInicial
     * the cuentaInicial to set
     */
    public void setCuentaInicial(String cuentaInicial)
    {
        this.cuentaInicial = cuentaInicial;
    }

    /**
     * @return the cuentaFinal
     */
    public String getCuentaFinal()
    {
        return cuentaFinal;
    }

    /**
     * @param cuentaFinal
     * the cuentaFinal to set
     */
    public void setCuentaFinal(String cuentaFinal)
    {
        this.cuentaFinal = cuentaFinal;
    }

    /**
     * @return the cuentaDebitar
     */
    public String getCuentaDebitar()
    {
        return cuentaDebitar;
    }

    /**
     * @param cuentaDebitar
     * the cuentaDebitar to set
     */
    public void setCuentaDebitar(String cuentaDebitar)
    {
        this.cuentaDebitar = cuentaDebitar;
    }

    /**
     * @return the ano
     */
    public int getAno()
    {
        return ano;
    }

    /**
     * @param ano
     * the ano to set
     */
    public void setAno(int ano)
    {
        this.ano = ano;
    }

    /**
     * @return the nitCuenta
     */
    public String getNitCuenta()
    {
        return nitCuenta;
    }

    /**
     * @param nitCuenta
     * the nitCuenta to set
     */
    public void setNitCuenta(String nitCuenta)
    {
        this.nitCuenta = nitCuenta;
    }

    /**
     * @return the claseTransaccion
     */
    public String getClaseTransaccion()
    {
        return claseTransaccion;
    }

    /**
     * @param claseTransaccion
     * the claseTransaccion to set
     */
    public void setClaseTransaccion(String claseTransaccion)
    {
        this.claseTransaccion = claseTransaccion;
    }

    /**
     * @return the tipoCuentaCliente
     */
    public String getTipoCuentaCliente()
    {
        return tipoCuentaCliente;
    }

    /**
     * @param tipoCuentaCliente
     * the tipoCuentaCliente to set
     */
    public void setTipoCuentaCliente(String tipoCuentaCliente)
    {
        this.tipoCuentaCliente = tipoCuentaCliente;
    }

    /**
     * @return the tipoAplicacion
     */
    public String getTipoAplicacion()
    {
        return tipoAplicacion;
    }

    /**
     * @param tipoAplicacion
     * the tipoAplicacion to set
     */
    public void setTipoAplicacion(String tipoAplicacion)
    {
        this.tipoAplicacion = tipoAplicacion;
    }

    /**
     * @return the secuenciaEnviosLotes
     */
    public String getSecuenciaEnviosLotes()
    {
        return secuenciaEnviosLotes;
    }

    /**
     * @param secuenciaEnviosLotes
     * the secuenciaEnviosLotes to set
     */
    public void setSecuenciaEnviosLotes(String secuenciaEnviosLotes)
    {
        this.secuenciaEnviosLotes = secuenciaEnviosLotes;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    /**
     * @return the listaCbCuentaInicial
     */
    public RegistroDataModelImpl getListaCbCuentaInicial()
    {
        return listaCbCuentaInicial;
    }

    /**
     * @param listaCbCuentaInicial
     * the listaCbCuentaInicial to set
     */
    public void setListaCbCuentaInicial(
                    RegistroDataModelImpl listaCbCuentaInicial)
    {
        this.listaCbCuentaInicial = listaCbCuentaInicial;
    }

    /**
     * @return the listaCbCuentaFinal
     */
    public RegistroDataModelImpl getListaCbCuentaFinal()
    {
        return listaCbCuentaFinal;
    }

    /**
     * @param listaCbCuentaFinal
     * the listaCbCuentaFinal to set
     */
    public void setListaCbCuentaFinal(
                    RegistroDataModelImpl listaCbCuentaFinal)
    {
        this.listaCbCuentaFinal = listaCbCuentaFinal;
    }

}
