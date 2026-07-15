/*-
 * BalanceAperturaNiifMgc.java
 *
 * 1.0
 *
 * 22/11/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import static com.sysman.util.SysmanFunciones.nvl;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.BalanceAperturaNiifMgcEnum;
import com.sysman.contabilidad.enums.BalanceAperturaNiifMgcUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
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
import java.util.GregorianCalendar;
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
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Controlador que permite generar el informe Balance de apertura NIIF
 * en formato Excel o XBRL para un anio, mes, rabgo de codigos y
 * digitos seleccionados
 *
 * @version 1.0, 22/11/2016
 * @author jlozano
 * @version 2. 06/04/2017 Se realizo el refactory.
 * @author jsforero
 * @version 3. 20/04/2017 Se adaptan llamados a EJBs
 * @author cmanrique
 * @version 4. 12/06/2017 Redireccion formulario
 * @author asana
 */
@ManagedBean
@ViewScoped
public class BalanceAperturaNiifMgc extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el valor del codigo inicial seleccionado
     */
    private String codigoInicial;
    /**
     * Atributo que almacena el valor del codigo final seleccionado
     */
    private String codigoFinal;
    /**
     * Atributo que almacena el valor del anio seleccionado
     */
    private String anoTrabajo;
    /**
     * Atributo que almacena el valor del mes seleccionado
     */
    private String mesTrabajo;
    /**
     * Atributo que almacena el valor del numero de digitos
     * seleccionado seleccionado
     */
    private String digitos;
    /**
     * Constante para el literal "CODIGO"
     */
    private static final String CODIGO = "CODIGO";

    /**
     * Constante para el literal "SYSDATE"
     */
    private static final String TAB = "\t\t\t\t ";
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de codigos disponibles
     */
    private RegistroDataModelImpl listaCodigoInicial;
    /**
     * Lista de codigos disponibles
     */
    private RegistroDataModelImpl listaCodigoFinal;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de BalanceAperturaNiifMgc
     */
    public BalanceAperturaNiifMgc() {
        super();
        compania = SessionUtil.getCompania();
        anoTrabajo = String.valueOf(GregorianCalendar.getInstance()
                        .get(GregorianCalendar.YEAR));
        mesTrabajo = "13";
        codigoInicial = "1";
        codigoFinal = "39999999999999999";
        digitos = "16";
        try {
            numFormulario = GeneralCodigoFormaEnum.BALANCE_APERTURA_NIIF_MGC.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
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
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoInicial();

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
        /*
         * FR1216-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * 'formularioAbrir 1, Me.Name DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCodigoInicial
     *
     */
    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceAperturaNiifMgcUrlEnum.URL6292
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), anoTrabajo);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);
    }

    /**
     *
     * Carga la lista listaCodigoFinal
     *
     */
    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceAperturaNiifMgcUrlEnum.URL7317
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), anoTrabajo);
        param.put(BalanceAperturaNiifMgcEnum.CODIGOINICIAL.getValue(),
                        codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     * Genera el reporte en formato Excel para los datos seleccionados
     *
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporteExcel();
        // </CODIGO_DESARROLLADO>
    }

    private void generarReporteExcel() {
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("mesTrabajo1", Integer.parseInt(mesTrabajo) - 1);
        reemplazar.put("mesTrabajo", Integer.parseInt(mesTrabajo));
        reemplazar.put("anoTrabajo", Integer.parseInt(anoTrabajo));
        reemplazar.put("codigoInicial", codigoInicial);
        reemplazar.put("codigoFinal", codigoFinal);
        reemplazar.put("digitos", digitos);
        asignarParametros(reemplazar);

        String strSql = Reporteador.resuelveConsulta(
                        "800070BalanceAperturaNiifMgc",
                        Integer.parseInt(SessionUtil.getModulo()), reemplazar);
        
        List<Registro> regAux = service.getListado(ConectorPool.ESQUEMA_SYSMAN,strSql);
        if (regAux == null || regAux.isEmpty()) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB747"));
        }
        else {
            try (Workbook workbook = new HSSFWorkbook(
                            JsfUtil.exportarHojaDatosStreamed(strSql,
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            FORMATOS.EXCEL97).getStream())) {

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
                sheet.createFreezePane(0, 5);

                CellStyle style = workbook.createCellStyle();
                style.setAlignment(CellStyle.ALIGN_LEFT);
                Font font = workbook.createFont();
                font.setFontName("SansSerif");
                font.setBold(true);
                style.setFont(font);
                Cell cell = sheet.createRow(0).createCell(0);
                cell.setCellValue(idioma.getString("TG_ENTIDAD") + "  :  "
                    + SessionUtil.getCompaniaIngreso().getNombre()
                                    .toUpperCase());
                cell.setCellStyle(style);

                Cell cell2 = sheet.createRow(1).createCell(0);
                cell2.setCellValue(idioma.getString("TB_TB501"));
                cell2.setCellStyle(style);

                Cell cell3 = sheet.createRow(2).createCell(0);
                cell3.setCellValue(idioma.getString("TB_TB502") + " "
                    + obtenerNombreMes(SysmanFunciones.mes(new Date()))
                    + "-" + SysmanFunciones.ano(new Date()));
                cell3.setCellStyle(style);

                workbook.setForceFormulaRecalculation(true);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                workbook.write(out);
                out.close();
                workbook.close();
                archivoDescarga = JsfUtil.getArchivoDescarga(
                                new ByteArrayInputStream(out.toByteArray()),
                                idioma.getString("TB_TB1760") + ".xls");
            }
            catch ( IOException | JRException
                            | DRException | SysmanException | SQLException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton XBRL en la vista
     *
     * Genera el reporte en formato XBRL para los datos seleccionados
     *
     */
    public void oprimirXBRL() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarXBRL();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que retorna el nombre de un mes
     *
     * @param mes
     * Numero del mes
     * @return Nombre del mes ingresado
     */
    public String obtenerNombreMes(int mes) {

        return SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes];

    }

    private void generarXBRL() {
        HashMap<String, Object> reemplazar = new HashMap<>();
        try {
            reemplazar.put("mesTrabajo1", Integer.parseInt(mesTrabajo) - 1);
            reemplazar.put("mesTrabajo", Integer.parseInt(mesTrabajo));
            reemplazar.put("anoTrabajo", Integer.parseInt(anoTrabajo));
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("digitos", digitos);

            asignarParametros(reemplazar);
      

        String strSql = Reporteador.resuelveConsulta(
                        "800070BalanceAperturaNiifMgc",
                        Integer.parseInt(SessionUtil.getModulo()), reemplazar);
        List<Registro> regAux = service.getListado(ConectorPool.ESQUEMA_SYSMAN, strSql);
        if (regAux == null || regAux.isEmpty()) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB747"));
        }
        else {
            String nombreArchivo = "AperturaNIIF.xbrl";
            String[] titulos = {
                                 "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <!-- Copyright 2014 SYSMAN,Paipa,Boyaca-Colombia. --> "
                                     + "\n"
                                     + "<xbrli:xbrl xmlns:xbrli=\"http://www.xbrl.org/2003/instance\" xmlns:link=\"http://www.xbrl.org/2003/linkbase\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:iso4217=\"http://www.xbrl.org/2003/iso4217\" xmlns:dt=\"http://xbrl.c-ebs.org/dt\" xmlns:xbrldi=\"http://xbrl.org/2005/xbrldi\" xmlns:d-hh=\"http://www.c-ebs.org/eu/fr/esrs/corep/2006-07-01/d-hh-2006-07-01\" xmlns:d-ty=\"http://www.c-ebs.org/eu/fr/esrs/corep/2006-07-01/d-ty-2006-07-01\" xmlns:ref=\"http://www.xbrl.org/2004/ref\" xmlns:xbrldt=\"http://xbrl.org/2005/xbrldt\" xmlns:ref-corep=\"http://www.c-ebs.org/eu/fr/esrs/corep/2005-09-30/ref-corep-2005-09-30\"> "
                                     + "\n"
                                     + "<xbrli:unit id=\"COP\"> <xbrli:measure>iso4217:COP</xbrli:measure> </xbrli:unit> <xbrli:unit id=\"Pure\"> <xbrli:measure>xbrli:pure</xbrli:measure> </xbrli:unit>"
                                     + "\n" };
            StringBuilder bld = new StringBuilder();
            for (String celda : titulos) {
                bld.append(celda + ",");
            }
            bld.deleteCharAt(bld.length() - 1);

            for (Registro registro : regAux) {
                bld.append("\t <Nivel> "
                    + nvl(registro.getCampos().get("NIVEL"), "ND")
                    + " </Nivel>" + "\n");
                bld.append("\t\t " + "<Codigo> "
                    + nvl(registro.getCampos().get(CODIGO), "ND")
                    + " </Codigo>" + "\n");
                bld.append("\t\t\t " + "<Cuenta> "
                    + nvl(registro.getCampos().get("CUENTA"), "ND")
                    + " </Cuenta>" + "\n");
                bld.append(TAB + "<SaldosPcga> "
                    + nvl(registro.getCampos().get("SALDOS PCGA"), "ND")
                                    .toString().replace(",", "")
                    + " </SaldosPcga>" + "\n");
                bld.append(TAB + "<ReclasificacionesDebito> "
                    + nvl(registro.getCampos()
                                    .get("RECLASIFICACIONES DEBITO"), "ND")
                                                    .toString()
                                                    .replace(",", "")
                    + " </ReclasificacionesDebito>" + "\n");
                bld.append(TAB + " <ReclasificacionesCredito> "
                    + nvl(registro.getCampos()
                                    .get("RECLASIFICACIONES CREDITO"), "ND")
                                                    .toString()
                                                    .replace(",", "")
                    + " </ReclasificacionesCredito>" + "\n");
                bld.append(TAB + " <AjustesPorErroresDebito> "
                    + nvl(registro.getCampos()
                                    .get("AJUSTES ERRORES DEBITO"), "ND")
                                                    .toString()
                                                    .replace(",", "")
                    + " </AjustesPorErroresDebito>" + "\n");
                bld.append(TAB + "<AjustesPorErroresCredito> "
                    + nvl(registro.getCampos()
                                    .get("AJUSTES ERRORES CREDITO"), "ND")
                                                    .toString()
                                                    .replace(",", "")
                    + " </AjustesPorErroresCredito>" + "\n");
                bld.append(TAB
                    + "<AjustePorConvergenciaDebito> "
                    + nvl(registro.getCampos().get(
                                    "AJUSTES CONVERGENCIA DEBITO"), "ND")
                                                    .toString()
                                                    .replace(",", "")
                    + " </AjustePorConvergenciaDebito> " + "\n");
                bld.append(TAB
                    + " <AjustesPorConvergenciaCredito> "
                    + nvl(registro.getCampos().get(
                                    "AJUSTES CONVERGENCIA CREDITO"), "ND")
                                                    .toString()
                                                    .replace(",", "")
                    + " </AjustesPorConvergenciaCredito>" + "\n");

                bld.append(TAB
                    + " <SaldosNiif> "
                    + nvl(registro.getCampos().get(
                                    "SALDOS NIIF"), "ND")
                                                    .toString()
                                                    .replace(",", "")
                    + " </SaldosNiif>" + "\n");
            }
            bld.append("</xbrli:xbrl>");
            String textoArchivo = bld.toString();

          
                ByteArrayInputStream archivo = JsfUtil
                                .serializarPlano(textoArchivo);
                archivoDescarga = JsfUtil.getArchivoDescarga(archivo,
                                nombreArchivo, "application/xml");
        }   
        }
            catch (JRException | IOException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
       
    }

    public void asignarParametros(Map<String, Object> reemplazar) {
        Date fechaAct = new Date();

        try {

            reemplazar.put("parAenValor",
                            SysmanFunciones.nvl(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "TIPO DE COMPROBANTE AJUSTES POR ERRORES",
                                                            SessionUtil.getModulo(),
                                                            fechaAct, true),
                                            " "));
            reemplazar.put("parAcnValor",
                            SysmanFunciones.nvl(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "TIPO DE COMPROBANTE AJUSTES POR CONVERGENCIA",
                                                            SessionUtil.getModulo(),
                                                            fechaAct, true),
                                            " "));
            reemplazar.put("parReValor",
                            SysmanFunciones.nvl(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "TIPO DE COMPROBANTE RECLASIFICACIONES NIIF",
                                                            SessionUtil.getModulo(),
                                                            fechaAct, true),
                                            " "));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control AnoTrabajo
     *
     * Recarga la lista Codigo inicial filtrando por el anio
     * seleccionado
     *
     */
    public void cambiarAnoTrabajo() {
        // <CODIGO_DESARROLLADO>
        cargarListaCodigoInicial();
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al cambiar el control MesTrabajo
     *
     */
    public void cambiarMesTrabajo() {
        // <CODIGO_DESARROLLADO>
        mesTrabajo = String.valueOf(Integer.parseInt(mesTrabajo));
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoInicial
     *
     * Recarga la lista Codigo final filtrando por los codigos mayores
     * o iguales al seleccionado en la lista Codigo inicial. Asigna el
     * codigo seleccionado al atributo codigoInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(CODIGO).toString();
        cargarListaCodigoFinal();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFinal
     *
     * Asigna el codigo seleccionado al atributo codigoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(CODIGO).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable codigoInicial
     *
     * @return codigoInicial
     */
    public String getCodigoInicial() {
        return codigoInicial;
    }

    /**
     * Asigna la variable codigoInicial
     *
     * @param codigoInicial
     * Variable a asignar en codigoInicial
     */
    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    /**
     * Retorna la variable codigoFinal
     *
     * @return codigoFinal
     */
    public String getCodigoFinal() {
        return codigoFinal;
    }

    /**
     * Asigna la variable codigoFinal
     *
     * @param codigoFinal
     * Variable a asignar en codigoFinal
     */
    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    /**
     * Retorna la variable anoTrabajo
     *
     * @return anoTrabajo
     */
    public String getAnoTrabajo() {
        return anoTrabajo;
    }

    /**
     * Asigna la variable anoTrabajo
     *
     * @param anoTrabajo
     * Variable a asignar en anoTrabajo
     */
    public void setAnoTrabajo(String anoTrabajo) {
        this.anoTrabajo = anoTrabajo;
    }

    /**
     * Retorna la variable mesTrabajo
     *
     * @return mesTrabajo
     */
    public String getMesTrabajo() {
        return mesTrabajo;
    }

    /**
     * Asigna la variable mesTrabajo
     *
     * @param mesTrabajo
     * Variable a asignar en mesTrabajo
     */
    public void setMesTrabajo(String mesTrabajo) {
        this.mesTrabajo = mesTrabajo;
    }

    /**
     * Asigna la variable digitos
     *
     * @return
     */
    public String getDigitos() {
        return digitos;
    }

    /**
     * Retorna la variable digitos
     *
     * @param digitos
     */
    public void setDigitos(String digitos) {
        this.digitos = digitos;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodigoInicial
     *
     * @return listaCodigoInicial
     */
    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    /**
     * Asigna la lista listaCodigoInicial
     *
     * @param listaCodigoInicial
     * Variable a asignar en listaCodigoInicial
     */
    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    /**
     * Retorna la lista listaCodigoFinal
     *
     * @return listaCodigoFinal
     */
    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    /**
     * Asigna la lista listaCodigoFinal
     *
     * @param listaCodigoFinal
     * Variable a asignar en listaCodigoFinal
     */
    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
