package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.ejb.EjbContabilidadSieteRemote;
import com.sysman.contabilidad.ejb.EjbContabilidadUnoRemote;
import com.sysman.contabilidad.enums.ConsolidandosControladorEnum;
import com.sysman.contabilidad.enums.ConsolidandosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
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

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author NGOMEZ
 * @version 1, 17/03/2016
 * 
 * @author jlramirez
 * @version 2, 07/04/2017, proceso de Refactoring y modificaciones
 * segun especificaciones de SONARLINT
 * @version 3, 20/04/2017, Manejo EJBs
 */
@ManagedBean
@ViewScoped
public class ConsolidandosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String nomcompania;
    private int anio;
    private String salidaTexto;
    private String rta;
    private String companiaConsolidar;
    private String nombreConsolidar;
    private String auxiliar;
    private String auxNombre;
    private List<Registro> listaAno;
    private boolean cuadroVisible;
    private RegistroDataModelImpl listaCompAConsolidar;
    private RegistroDataModelImpl listaCompAConsolidarE;
    private RegistroDataModelImpl listaNITCompania;
    private RegistroDataModelImpl listaNITCompaniaE;
    private StreamedContent archivoDescarga;
    private boolean generaInconsistencia;
    private boolean generaConsolidacion;

    @EJB
    private EjbContabilidadUnoRemote contabilidadUno;

    @EJB
    private EjbContabilidadSieteRemote ejbContabilidadSieteRemote;

    /**
     * Creates a new instance of ConsolidandosControlador
     */
    public ConsolidandosControlador() {
        super();
        compania = SessionUtil.getCompania();
        nomcompania = "NOMBRECOMP";
        try {
            numFormulario = GeneralCodigoFormaEnum.CONSOLIDANDOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.CONSOLIDADA;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListaCompAConsolidar();
        cargarListaCompAConsolidarE();
        cargarListaNITCompania();
        cargarListaNITCompaniaE();
        cargarListaAno();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(),
                        companiaConsolidar);
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getCompaniaConsolidar() {
        return companiaConsolidar;
    }

    public void setCompaniaConsolidar(String companiaConsolidar) {
        this.companiaConsolidar = companiaConsolidar;
    }

    public String getNombreConsolidar() {
        return nombreConsolidar;
    }

    public void setNombreConsolidar(String nombreConsolidar) {
        this.nombreConsolidar = nombreConsolidar;
    }

    public boolean isCuadroVisible() {
        return cuadroVisible;
    }

    public void setCuadroVisible(boolean cuadroVisible) {
        this.cuadroVisible = cuadroVisible;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public RegistroDataModelImpl getListaCompAConsolidar() {
        return listaCompAConsolidar;
    }

    public void setListaCompAConsolidar(
        RegistroDataModelImpl listaCompAConsolidar) {
        this.listaCompAConsolidar = listaCompAConsolidar;
    }

    public RegistroDataModelImpl getListaCompAConsolidarE() {
        return listaCompAConsolidarE;
    }

    public void setListaCompAConsolidarE(
        RegistroDataModelImpl listaCompAConsolidarE) {
        this.listaCompAConsolidarE = listaCompAConsolidarE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public RegistroDataModelImpl getListaNITCompania() {
        return listaNITCompania;
    }

    public void setListaNITCompania(RegistroDataModelImpl listaNITCompania) {
        this.listaNITCompania = listaNITCompania;
    }

    public RegistroDataModelImpl getListaNITCompaniaE() {
        return listaNITCompaniaE;
    }

    public void setListaNITCompaniaE(RegistroDataModelImpl listaNITCompaniaE) {
        this.listaNITCompaniaE = listaNITCompaniaE;
    }

    public String getAuxNombre() {
        return auxNombre;
    }

    public void setAuxNombre(String auxNombre) {
        this.auxNombre = auxNombre;
    }

    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConsolidandosControladorUrlEnum.URL6547
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cargarListaCompAConsolidar() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConsolidandosControladorUrlEnum.URL6891
                                                        .getValue());
        listaCompAConsolidar = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCompAConsolidarE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConsolidandosControladorUrlEnum.URL6891
                                                        .getValue());
        listaCompAConsolidarE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaNITCompania() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConsolidandosControladorUrlEnum.URL7823
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.CODIGO.getName(), companiaConsolidar);
        listaNITCompania = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaNITCompaniaE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConsolidandosControladorUrlEnum.URL7823
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.CODIGO.getName(), companiaConsolidar);
        listaNITCompaniaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void ejecutarmostrarPantalla() {
        // COMANDO REMOTO
    }

    public void oprimirAceptar() {
        if (SessionUtil.getNivelGrupo(SessionUtil.getModulo()) != 9) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3075").replace(
                            "$#modulo#$",
                            String.valueOf(SessionUtil.getNivelGrupo(
                                            SessionUtil.getModulo()))));
            return;
        }
        archivoDescarga = null;
        try {

            rta = contabilidadUno.consolidarCompanias(companiaConsolidar,
                            anio);

            salidaTexto = ejbContabilidadSieteRemote.diferenciasPorMes(compania,
                            anio);

            generaInconsistencia = !("TRUE".equals(salidaTexto));
            generaConsolidacion = !"-1".equals(rta);

            if (generaConsolidacion && generaInconsistencia) {

                generarComprimidos(rta, salidaTexto);

            }

            else if (generaConsolidacion) {
                generarConsolidar(rta);
            }

            else if (generaInconsistencia) {
                generarInconsistecia(salidaTexto);
            }
            else {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB859"));
            }

        }
        
        catch (NumberFormatException
                        | SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                + ex.getMessage());
        }
    }

    private void generarInconsistecia(String salidaTexto1) {
        try {

            ByteArrayInputStream archivo = JsfUtil
                            .serializarPlano(salidaTexto1);

            archivoDescarga = JsfUtil.getArchivoDescarga(archivo,
                            "InconsistenciasPorMes" + ".txt");
        }
        catch (JRException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void generarConsolidar(String consolidar) {
        try {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB858"));
            ByteArrayInputStream aux = JsfUtil.serializarPlano(consolidar);

            archivoDescarga = JsfUtil.getArchivoDescarga(aux,
                            "inconsistencias_Consolidar.txt");
        }
        catch (JRException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void generarComprimidos(String Consolidar, String SalidaTexto2) {

        try {

            String[] nombresArchivos = new String[2];
            ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB858"));

            salidas[0] = JsfUtil.serializarPlano(Consolidar);

            salidas[1] = JsfUtil
                            .serializarPlano(SalidaTexto2);

            nombresArchivos[0] = SysmanFunciones.concatenar(
                            "inconsistencias_Consolidar",
                            ".txt");
            nombresArchivos[1] = SysmanFunciones.concatenar(
                            "InconsistenciasPorMes",
                            ".txt");

            archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                            salidas, nombresArchivos);

        }
        catch (JRException | IOException | DRException
                        | SQLException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirCancelar() {
        // NO SE IMPLEMENTA
    }

    public void oprimirColocarIndicador() {
        cuadroVisible = true;
    }

    public void oprimirreporte() {

        String modulo = SessionUtil.getModulo();

        String[] nombresInformes = new String[8];
        String informe = "001951ConsolidacionCompanias";

        Map<String, Object> parametros = new HashMap<>();

        Map<String, Object> reemplazos = new HashMap<>();

        parametros.put("PR_ANIO", anio);
        parametros.put("PR_COMPANIACONSOLIDADORA", companiaConsolidar);
        parametros.put("PR_NOMBREEMPRESA",
                        SessionUtil.getCompaniaIngreso().getNombre());

        reemplazos.put("companiaconsolidadora", companiaConsolidar);
        reemplazos.put("anio", anio);

        Reporteador.resuelveConsulta(
                        informe,
                        Integer.parseInt(modulo),
                        reemplazos, parametros);

        ByteArrayInputStream reporteConsolidacion = null;
        try {
            reporteConsolidacion = JsfUtil.serializarReporte(informe,
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.PDF);
        }
        catch (SysmanException | JRException | IOException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

        ByteArrayInputStream salidaReporte1 = null;
        ByteArrayInputStream salidaReporte2 = null;
        ByteArrayInputStream salidaReporte3 = null;
        ByteArrayInputStream salidaReporte4 = null;
        ByteArrayInputStream salidaReporte5 = null;
        ByteArrayInputStream salidaReporte6 = null;
        ByteArrayInputStream salidaReporte7 = null;
        try {

            String reporte1 = Reporteador.resuelveConsulta(
                            "800311CuentasPlanContableNoConsolidada",
                            Integer.parseInt(modulo), reemplazos);

            salidaReporte1 = JsfUtil.serializarHojaDatos(
                            reporte1,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL);
        }
        catch (SysmanException | JRException | IOException | DRException
                        | SQLException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

        try {
            String reporte2 = Reporteador.resuelveConsulta(
                            "800312CuentasBloqPlanContableExisConsolidada",
                            Integer.parseInt(modulo), reemplazos);

            salidaReporte2 = JsfUtil.serializarHojaDatos(
                            reporte2,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL);
        }
        catch (SysmanException | JRException | IOException | DRException
                        | SQLException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

        try {
            String reporte3 = Reporteador.resuelveConsulta(
                            "800313CuentasBloqConsolidadaExisColegios",
                            Integer.parseInt(modulo), reemplazos);

            salidaReporte3 = JsfUtil.serializarHojaDatos(
                            reporte3,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL);
        }
        catch (SysmanException | JRException | IOException | DRException
                        | SQLException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }
        try {
            String reporte4 = Reporteador.resuelveConsulta(
                            "800314CuentasExisColegiosMovNoConsolidada",
                            Integer.parseInt(modulo), reemplazos);
            salidaReporte4 = JsfUtil.serializarHojaDatos(
                            reporte4,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL);
        }
        catch (SysmanException | JRException | IOException | DRException
                        | SQLException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }
        try {
            String reporte5 = Reporteador.resuelveConsulta(
                            "800315CuentasBloqColegiosMovExisConsolidada",
                            Integer.parseInt(modulo), reemplazos);

            salidaReporte5 = JsfUtil.serializarHojaDatos(
                            reporte5,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL);
        }
        catch (SysmanException | JRException | IOException | DRException
                        | SQLException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }
        try {
            String reporte6 = Reporteador.resuelveConsulta(
                            "800316CuentasBloqConsolidadaExisColegiosMov",
                            Integer.parseInt(modulo), reemplazos);

            salidaReporte6 = JsfUtil.serializarHojaDatos(
                            reporte6,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL);
        }
        catch (SysmanException | JRException | IOException | DRException
                        | SQLException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }
        try {
            String reporte7 = Reporteador.resuelveConsulta(
                            "800325CuentasConSaldoNegativo",
                            Integer.parseInt(modulo), reemplazos);

            salidaReporte7 = JsfUtil.serializarHojaDatos(
                            reporte7,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL);
        }
        catch (SysmanException | JRException | IOException | DRException
                        | SQLException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }        
        ByteArrayInputStream[] salida = new ByteArrayInputStream[8];
        int cantidad = 0;

        if (reporteConsolidacion != null) {
            salida[cantidad] = reporteConsolidacion;
            nombresInformes[cantidad] = informe + ".pdf";
            cantidad++;
        }

        if (salidaReporte1 != null) {
            salida[cantidad] = salidaReporte1;
            nombresInformes[cantidad] = "CuentasPlanContableNoConsolidada.xlsx";
            cantidad++;
        }
        if (salidaReporte2 != null) {
            salida[cantidad] = salidaReporte2;
            nombresInformes[cantidad] = "CuentasBloqPlanContableExisConsolidada.xlsx";
            cantidad++;
        }

        if (salidaReporte3 != null) {
            salida[cantidad] = salidaReporte3;
            nombresInformes[cantidad] = "CuentasBloqConsolidadaExisColegios.xlsx";
            cantidad++;
        }

        if (salidaReporte4 != null) {
            salida[cantidad] = salidaReporte4;
            nombresInformes[cantidad] = "CuentasExisColegiosMovNoConsolidada.xlsx";
            cantidad++;
        }

        if (salidaReporte5 != null) {
            salida[cantidad] = salidaReporte5;
            nombresInformes[cantidad] = "CuentasBloqColegiosMovExisConsolidada.xlsx";
            cantidad++;
        }	

        if (salidaReporte6 != null) {
            salida[cantidad] = salidaReporte6;
            nombresInformes[cantidad] = "CuentasBloqConsolidadaExisColegiosMov.xlsx";
            cantidad++;
        }
        if (salidaReporte7 != null) {
            salida[cantidad] = salidaReporte7;
            nombresInformes[cantidad] = "CuentasConSaldoNegativo.xlsx";
            cantidad++;
        }        
        try {
            if (cantidad > 0) {
                archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                                salida,
                                nombresInformes,
                                "Revision_Consolidacion");
            }

        }
        catch (JRException | IOException
                        | SQLException | DRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirConfigEstados() {

        if (SysmanFunciones.validarVariableVacio(companiaConsolidar.trim())) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4014"));
        }
        else {

            String[] campos = { "companias", "ano" };
            Object[] valores = { nombreConsolidar, anio };

            SessionUtil.cargarModalDatosFlashCerrar(
                            String.valueOf(GeneralCodigoFormaEnum.CONFIG_ESTADOS_CONSOLIDADA_CONTROLADOR
                                            .getCodigo()),
                            SessionUtil.getModulo(), campos, valores);
        }
    }

    @Override
    public void abrirFormulario() {
        anio = SysmanFunciones.ano(new Date());
    }

    public void seleccionarFilaCompAConsolidar(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        companiaConsolidar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), " ")
                        .toString();
        nombreConsolidar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()), " ")
                        .toString();
        reasignarOrigen();
        cargarListaNITCompania();
        cargarListaNITCompaniaE();
    }

    public void seleccionarFilaCompAConsolidarE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), " ")
                        .toString();
    }

    public void seleccionarFilaNITCompania(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NITCOMPANIA",
                        registroAux.getCampos().get("NIT"));
        registro.getCampos().put(nomcompania,
                        registroAux.getCampos().get(nomcompania));
    }

    public void seleccionarFilaNITCompaniaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), " ")
                        .toString();
        auxNombre = SysmanFunciones
                        .nvl(registroAux.getCampos().get(nomcompania), " ")
                        .toString();
    }

    public void cambiarNITCompaniaC(int rowNum) {
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        nomcompania,
                        auxNombre);
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put("COMPANIACON", companiaConsolidar);
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        registro.getCampos().remove(nomcompania);
        if (companiaConsolidar == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB860"));
        }
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    @Override
    public void removerCombos() {
        // NO SE IMPLEMENTA
    }

    @Override
    public void asignarValoresRegistro() {
        // NO SE IMPLEMENTA
    }

    public void cambiarIndicadorCuadro() {
        // NO SE IMPLEMENTA
    }

    public void aceptarIndicadorCuadro() {
        StringBuilder companias = new StringBuilder();
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.CODIGO.getName(), companiaConsolidar);
        List<Registro> aux;
        try {
            aux = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConsolidandosControladorUrlEnum.URL13316
                                                                            .getValue())
                                            .getUrl(), param));
            for (Registro aux1 : aux) {
                companias.append(aux1.getCampos().get("NITCOMPANIA"))
                                .append(",");
            }
            companias.append("".equals(companias.toString()) ? "''"
                : companias.substring(0, companias.length() - 1));
            Map<String, Object> parametros = new HashMap<>();
            parametros.put(ConsolidandosControladorEnum.PARAM0.getValue(),
                            companias.toString());
            parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            parametros.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            Parameter parameter = new Parameter();
            parameter.setFields(parametros);
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ConsolidandosControladorUrlEnum.URL14023
                                                            .getValue());
            requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(), parameter);
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
            cuadroVisible = false;
            return;
        }
        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB853"));
        cuadroVisible = false;
    }

    public void cancelarIndicadorCuadro() {
        cuadroVisible = false;
    }

    /**
     * @return the salidaTexto
     */
    public String getSalidaTexto() {
        return salidaTexto;
    }

    /**
     * @param salidaTexto
     * the salidaTexto to set
     */
    public void setSalidaTexto(String salidaTexto) {
        this.salidaTexto = salidaTexto;
    }

}
