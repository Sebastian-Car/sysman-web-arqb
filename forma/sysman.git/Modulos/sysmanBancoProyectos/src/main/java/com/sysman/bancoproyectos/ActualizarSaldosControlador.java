package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoDosRemote;
import com.sysman.bancoproyectos.enums.ActualizarSaldosControladorEnum;
import com.sysman.bancoproyectos.enums.ActualizarSaldosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodrigueza
 * @version 1, 25/08/2015
 * 
 * @author asana
 * @version 2, 18/09/2017 Se realiza refactoring.
 */
@ManagedBean
@ViewScoped
public class ActualizarSaldosControlador extends BeanBaseModal {

    private String compania;
    private String proyectoInicial;
    private String proyectoFinal;
    private String proceso;
    private String codigo;
    private boolean actualizarTotal;
    private RegistroDataModelImpl listaProyectoinicial;
    private RegistroDataModelImpl listaProyectofinal;
    private StreamedContent archivoDescarga;

    @EJB
    private EjbBancoProyectoDosRemote ejbBancoProyectoDos;

    /**
     * Creates a new instance of ActualizarSaldosControlador
     */
    public ActualizarSaldosControlador() {
        super();
        try {
            compania = SessionUtil.getCompania();
            numFormulario = GeneralCodigoFormaEnum.ACTUALIZAR_SALDOS_CONTROLADOR_PROYECTOS
                            .getCodigo();
            proceso = "1";
            proyectoInicial = "00000000";
            proyectoFinal = "99999999";
            codigo = "CODIGO";
            actualizarTotal = false;
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ActualizarSaldosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init() {
        cargarListaProyectoinicial();
        cargarListaProyectofinal();
        abrirFormulario();
    }

    /**
     * Muestra el listado de los proyectos, para seleccionar uno como
     * el inicial. Si no se selecciona alguno se buscara el primero
     * que est� en la base de datos.
     */
    public void cargarListaProyectoinicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActualizarSaldosControladorUrlEnum.URL3222
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaProyectoinicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);

    }

    /**
     * Muestra el listado de los proyectos, para seleccionar uno como
     * el final. Si no se selecciona alguno se buscara el �ltimo que
     * est� en la base de datos.
     */
    public void cargarListaProyectofinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActualizarSaldosControladorUrlEnum.URL17434
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ActualizarSaldosControladorEnum.PARAM0.getValue(),
                        proyectoInicial);

        listaProyectofinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);

    }

    /**
     * Ejecuta un proceso determinado.
     *
     * @param ac
     * n�mero de la opci�n
     */
    public void oprimirEjecutar() {
        String cadena;
        archivoDescarga = null;

        try {
            if (proceso != null) {
                cadena = ejbBancoProyectoDos.validarProcesos(compania,
                                Integer.parseInt(SysmanFunciones
                                                .nvl(proceso, "1")
                                                .toString()),
                                proyectoInicial,
                                proyectoFinal,
                                actualizarTotal,
                                SessionUtil.getUser().getCodigo(),
                                Integer.parseInt(SessionUtil.getModulo()));

                switch (SysmanFunciones.nvl(proceso, "1").toString()) {
                case "1":
                    revisarTotalesProyecto(cadena);
                    break;
                case "2":
                    generarArchivoPlano(cadena);
                    break;
                case "3":
                    verInconsistenciasActualizacion(cadena);
                    break;
                case "4":
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB2274")
                                                    .replace("#$cadena#$",
                                                                    cadena));
                    break;
                default:
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB2260"));
                    break;
                }
            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2260"));
            }

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void seleccionarFilaProyectoinicial(SelectEvent event) {

        Registro registroAux = (Registro) event.getObject();
        proyectoInicial = registroAux.getCampos().get(codigo).toString();
        cargarListaProyectofinal();

    }

    public void seleccionarFilaProyectofinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        proyectoFinal = registroAux.getCampos().get(codigo).toString();
        cargarListaProyectoinicial();
    }

    @Override
    public void abrirFormulario() {
        // NO IMPLEMENTADO
    }

    public void cambiarProceso() {

        // NO IMPLEMENTADO
    }

    /**
     * Exporta en un archivo excel la revisi�n de los totales del
     * proyecto y componentes.
     */
    private void revisarTotalesProyecto(String cadena) {

        if (cadena == null) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3589"));
        }
        else {

            Workbook workbook = null;
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                workbook = new XSSFWorkbook();

                String[] hojas = cadena.split(
                                ActualizarSaldosControladorEnum.SEPHOJA
                                                .getValue());

                String componente = hojas[0];
                // String proyecto = hojas[0];
                String proyecto = hojas[1];

                Sheet sheetComponente = workbook
                                .createSheet(idioma.getString("TB_TB3581"));
                Sheet sheetProyecto = workbook
                                .createSheet("Proyectos");

                Font font = workbook.createFont();
                font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                font.setFontHeightInPoints((short) 10);
                font.setFontName("Arial");

                CellStyle cellStyleEncabezado = workbook.createCellStyle();
                cellStyleEncabezado.setFont(font);

                Row rowDetalleEncabe = sheetComponente.createRow(0);

                Cell cellProyEncab = rowDetalleEncabe.createCell(0);
                cellProyEncab.setCellValue(idioma.getString("TG_PROYECTOS"));
                cellProyEncab.setCellStyle(cellStyleEncabezado);

                Cell cellCompEncab = rowDetalleEncabe.createCell(1);
                cellCompEncab.setCellValue(idioma.getString("TB_TB3581"));
                cellCompEncab.setCellStyle(cellStyleEncabezado);

                Cell cellNomCompEncab = rowDetalleEncabe.createCell(2);
                cellNomCompEncab.setCellValue(
                                idioma.getString("TG_NOMBRE_COMPONENTE"));
                cellNomCompEncab.setCellStyle(cellStyleEncabezado);

                Cell cellVigencEncab = rowDetalleEncabe.createCell(3);
                cellVigencEncab.setCellValue(idioma.getString("TG_VIGENCIA3"));
                cellVigencEncab.setCellStyle(cellStyleEncabezado);

                Cell cellVltTtCompEncab = rowDetalleEncabe.createCell(4);
                cellVltTtCompEncab.setCellValue(idioma.getString("TB_TB3584"));
                cellVltTtCompEncab.setCellStyle(cellStyleEncabezado);

                Cell cellSumatoriaEncab = rowDetalleEncabe.createCell(5);
                cellSumatoriaEncab.setCellValue(
                                idioma.getString("TB_TB3585"));
                cellSumatoriaEncab.setCellStyle(cellStyleEncabezado);

                Cell cellAlertaEncab = rowDetalleEncabe.createCell(6);
                cellAlertaEncab.setCellValue(idioma.getString("TB_TB3586"));
                cellAlertaEncab.setCellStyle(cellStyleEncabezado);

                String[] componentes = componente
                                .split(SysmanConstantes.SEPARADOR_COL);

                for (int i = 1; i < componentes.length; i++) {
                    String[] valorcolumna = componentes[i]
                                    .split(SysmanConstantes.SEPARADOR_REG);

                    Row rowDetalles = sheetComponente.createRow(i);

                    Cell cellProyecto = rowDetalles.createCell(0);
                    cellProyecto.setCellValue(valorcolumna[0]);

                    Cell cellComponente = rowDetalles.createCell(1);
                    cellComponente.setCellValue(valorcolumna[1]);

                    Cell cellNombComponente = rowDetalles.createCell(2);
                    cellNombComponente.setCellValue(valorcolumna[2]);

                    Cell cellVigencia = rowDetalles.createCell(3);
                    cellVigencia.setCellValue(valorcolumna[3]);

                    Cell cellVlrTotal = rowDetalles.createCell(4);
                    cellVlrTotal.setCellValue(valorcolumna[4]);

                    Cell cellSumatoria = rowDetalles.createCell(5);
                    cellSumatoria.setCellValue(valorcolumna[5]);

                    Cell cellAlerta = rowDetalles.createCell(6);
                    cellAlerta.setCellValue(valorcolumna[6]);

                }

                sheetComponente.autoSizeColumn(0);
                sheetComponente.autoSizeColumn(1);
                sheetComponente.autoSizeColumn(2);
                sheetComponente.autoSizeColumn(3);
                sheetComponente.autoSizeColumn(4);
                sheetComponente.autoSizeColumn(5);
                sheetComponente.autoSizeColumn(6);

                Row rowProyectoEncabezado = sheetProyecto.createRow(0);

                Cell cellProyecPEncabe = rowProyectoEncabezado.createCell(0);
                cellProyecPEncabe
                                .setCellValue(idioma.getString("TG_PROYECTOS"));
                cellProyecPEncabe.setCellStyle(cellStyleEncabezado);

                Cell cellVlrTotPEncabe = rowProyectoEncabezado.createCell(1);
                cellVlrTotPEncabe.setCellValue(
                                idioma.getString("TG_VALOR_TOTAL3"));
                cellVlrTotPEncabe.setCellStyle(cellStyleEncabezado);

                Cell cellSumatoriaPEncabe = rowProyectoEncabezado.createCell(2);
                cellSumatoriaPEncabe
                                .setCellValue(idioma.getString("TB_TB3588"));
                cellSumatoriaPEncabe.setCellStyle(cellStyleEncabezado);

                Cell cellAlertaPEncabe = rowProyectoEncabezado.createCell(3);
                cellAlertaPEncabe.setCellValue(idioma.getString("TB_TB3586"));
                cellAlertaPEncabe.setCellStyle(cellStyleEncabezado);

                String[] proyectos = proyecto
                                .split(SysmanConstantes.SEPARADOR_COL);

                for (int i = 1; i < proyectos.length; i++) {
                    String[] valorColumna = proyectos[i]
                                    .split(SysmanConstantes.SEPARADOR_REG);

                    Row rowDetalle1 = sheetProyecto.createRow(i);

                    Cell celProyectos = rowDetalle1.createCell(0);
                    celProyectos.setCellValue(valorColumna[0]);

                    Cell celVlrTotal = rowDetalle1.createCell(1);
                    celVlrTotal.setCellValue(valorColumna[1]);

                    Cell cellSumatoria = rowDetalle1.createCell(2);
                    cellSumatoria.setCellValue(valorColumna[2]);

                    Cell cellAlerta = rowDetalle1.createCell(3);
                    cellAlerta.setCellValue(valorColumna[3]);
                }

                sheetProyecto.autoSizeColumn(0);
                sheetProyecto.autoSizeColumn(1);
                sheetProyecto.autoSizeColumn(2);
                sheetProyecto.autoSizeColumn(3);

                workbook.write(out);
                out.close();

                archivoDescarga = JsfUtil
                                .getArchivoDescarga(new ByteArrayInputStream(
                                                out.toByteArray()),
                                                SysmanFunciones.concatenar(
                                                                "Revision_de_totales_de_proyectos",
                                                                ".xlsx"));

            }
            catch (JRException | IOException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());

            }
            finally {
                if (workbook != null) {
                    try {
                        workbook.close();
                    }
                    catch (IOException e) {
                        logger.error(e.getMessage(), e);
                        JsfUtil.agregarMensajeError(e.getMessage());
                    }
                }

            }
        }
    }

    /**
     * Exporta en archivo plano las inconsistencias que se encontraron
     * en el proceso de mantenimiento.
     *
     * @param contenido
     * cadena que contiene las inconsistencias
     */
    public void generarArchivoPlano(String contenido) {

        try {
            ejbBancoProyectoDos.actualizarProyectoMante(compania,
                            proyectoInicial,
                            proyectoFinal, SessionUtil.getUser().getCodigo());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB4254").replace(
                                            "#$proyecto#$",
                                            proyectoInicial));

        }
        catch (SystemException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // byte[] bytes = contenido.getBytes();
        // ByteArrayInputStream byteArray = new
        // ByteArrayInputStream(bytes);
        // try {
        // archivoDescarga = JsfUtil.getArchivoDescarga(byteArray,
        // "bp_anomalias");
        // }
        // catch (JRException | IOException ex) {
        // JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
        // idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA),
        // ex.getMessage()));
        // Logger.getLogger(ActualizarSaldosControlador.class.getName())
        // .log(Level.SEVERE, null, ex);
        // }
        // finally {
        // try {
        // byteArray.close();
        // }
        // catch (IOException ex) {
        // Logger.getLogger(ActualizarSaldosControlador.class.getName())
        // .log(Level.SEVERE, null, ex);
        // }
        // }
    }

    /**
     * Permite generar el reporte que muestra el listado de
     * inconsistencias de actualizar proyectos desde novedades
     */
    private void verInconsistenciasActualizacion(String cadena) {
        try {
            if ("1".equals(cadena)) {
                Map<String, Object> parametros = new HashMap<>();

                parametros.put("PR_DEPARTAMENTOCOMPANIA",
                                SessionUtil.getCompaniaIngreso()
                                                .getDepartamento());
                parametros.put("PR_CIUDADCOMPANIA",
                                SessionUtil.getCompaniaIngreso().getCiudad());

                Reporteador.resuelveConsulta(
                                "000177RptInconsistenciaNov",
                                Integer.parseInt(SessionUtil.getModulo()),
                                new HashMap<String, Object>(), parametros);

                archivoDescarga = JsfUtil.exportarStreamed(
                                "000177RptInconsistenciaNov",
                                parametros,
                                ConectorPool.ESQUEMA_SYSMAN,
                                FORMATOS.PDF);

            }
            else {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2271"));
            }
        }
        catch (FileNotFoundException ex) {

            JsfUtil.agregarMensajeInformativo(
                            SysmanFunciones.concatenar(
                                            idioma.getString(
                                                            Constantes.MSM_INFORME_NO_EXISTE),
                                            " ", ex.getMessage()));
            Logger.getLogger(ActualizarSaldosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public String getProyectoInicial() {
        return proyectoInicial;
    }

    public void setProyectoInicial(String proyectoInicial) {
        this.proyectoInicial = proyectoInicial;
    }

    public String getProyectoFinal() {
        return proyectoFinal;
    }

    public void setProyectoFinal(String proyectoFinal) {
        this.proyectoFinal = proyectoFinal;
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public RegistroDataModelImpl getListaProyectoinicial() {
        return listaProyectoinicial;
    }

    public void setListaProyectoinicial(
        RegistroDataModelImpl listaProyectoinicial) {
        this.listaProyectoinicial = listaProyectoinicial;
    }

    public RegistroDataModelImpl getListaProyectofinal() {
        return listaProyectofinal;
    }

    public void setListaProyectofinal(
        RegistroDataModelImpl listaProyectofinal) {
        this.listaProyectofinal = listaProyectofinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean isActualizarTotal() {
        return actualizarTotal;
    }

    public void setActualizarTotal(boolean actualizarTotal) {
        this.actualizarTotal = actualizarTotal;
    }

}