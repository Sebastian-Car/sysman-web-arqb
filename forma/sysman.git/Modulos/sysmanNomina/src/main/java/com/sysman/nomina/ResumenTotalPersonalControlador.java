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
import com.sysman.nomina.ejb.EjbNominaUnoRemote;
import com.sysman.nomina.enums.ResumenTotalPersonalControladorEnum;
import com.sysman.nomina.enums.ResumenTotalPersonalControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Map;
import java.util.TreeMap;

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 01/08/2015
 * 
 * @author eamaya
 * @version 2.0,27/10/2017, Proceso de Refactoring DSS, Manejo de EJBs
 * y cambio de numero de formulario enum
 * 
 */
@ManagedBean
@ViewScoped
public class ResumenTotalPersonalControlador extends BeanBaseModal {

    private final String nombreCompania;
    private final String compania;
    private String escalafon;
    private String ano1;
    private String ano2;
    private String mes1;
    private String mes2;
    private String periodo1;
    private String periodo2;
    private String proceso;
    private String nivel;
    private List<Registro> listaAno1;
    private List<Registro> listaAno2;
    private List<Registro> listaMes1;
    private List<Registro> listaMes2;
    private List<Registro> listaPeriodo1;
    private List<Registro> listaPeriodo2;
    private List<Registro> listaProceso;
    private List<Registro> listaNIVEL;
    private final String anioSession = (String) SessionUtil
                    .getSessionVar("anioNomina");
    private final String procesoSession = (String) SessionUtil
                    .getSessionVar("procesoNomina");
    private final String mesSession = (String) SessionUtil
                    .getSessionVar("mesNomina");
    private final String periodoSession = (String) SessionUtil
                    .getSessionVar("periodoNomina");
    private StreamedContent archivoDescarga;
    private static final String CTELINEA = "LINEA";

    @EJB
    private EjbNominaUnoRemote ejbNominaUno;

    /**
     * Creates a new instance of ResumenTotalPersonalControlador
     */
    public ResumenTotalPersonalControlador() {
        super();
        ano1 = ano2 = anioSession;
        periodo1 = periodo2 = periodoSession;
        proceso = procesoSession;
        mes1 = mes2 = mesSession;
        numFormulario = GeneralCodigoFormaEnum.RESUMEN_TOTAL_PERSONAL_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();

        try {
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ResumenTotalPersonalControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        cargarListaAno1();
        cargarListaAno2();
        cargarListaMes1();
        cargarListaMes2();
        cargarListaPeriodo1();
        cargarListaPeriodo2();
        cargarListaProceso();
        cargarListaNIVEL();
        abrirFormulario();
    }

    public void cargarListaProceso() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));

        try {
            listaProceso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenTotalPersonalControladorUrlEnum.URL11015
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
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAno1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenTotalPersonalControladorUrlEnum.URL4521
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAno2() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAno2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenTotalPersonalControladorUrlEnum.URL5330
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
        param.put(ResumenTotalPersonalControladorEnum.ID_PROCESO.getValue(),
                        proceso);
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano1);

        try {
            listaMes1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenTotalPersonalControladorUrlEnum.URL6134
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes2() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano2);
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(ResumenTotalPersonalControladorEnum.ID_PROCESO.getValue(),
                        proceso);

        try {
            listaMes2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenTotalPersonalControladorUrlEnum.URL7301
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

        param.put(GeneralParameterEnum.ANO.getName(), ano1);

        param.put(GeneralParameterEnum.MES.getName(), mes1);

        param.put(ResumenTotalPersonalControladorEnum.PROCESO.getValue(),
                        proceso);

        try {
            listaPeriodo1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenTotalPersonalControladorUrlEnum.URL5555
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

        param.put(GeneralParameterEnum.ANO.getName(), ano2);

        param.put(GeneralParameterEnum.MES.getName(), mes2);

        param.put(ResumenTotalPersonalControladorEnum.PROCESO.getValue(),
                        proceso);

        try {
            listaPeriodo2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenTotalPersonalControladorUrlEnum.URL4444
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaNIVEL() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.CLASE.getName(),
                        "08,98");

        try {
            listaNIVEL = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenTotalPersonalControladorUrlEnum.URL12191
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
        getInforme(FORMATOS.PDF);

    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        getInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public String getInconsNominaNiveles() {

        StringBuilder textOutput = new StringBuilder();

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ResumenTotalPersonalControladorEnum.ANO1.getValue(), ano1);
        param.put(ResumenTotalPersonalControladorEnum.MES1.getValue(), mes1);
        param.put(ResumenTotalPersonalControladorEnum.PERIODO1.getValue(),
                        periodo1);
        param.put(ResumenTotalPersonalControladorEnum.ANO2.getValue(), ano2);
        param.put(ResumenTotalPersonalControladorEnum.MES2.getValue(), mes2);
        param.put(ResumenTotalPersonalControladorEnum.PERIODO2.getValue(),
                        periodo2);

        List<Registro> listaEscalafon;
        try {
            listaEscalafon = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenTotalPersonalControladorUrlEnum.URL4545
                                                                            .getValue())
                                            .getUrl(), param));

            textOutput.append("Funcionarios sin Escalafon \r\n");
            textOutput.append("\r\n");
            textOutput.append(
                            "Nombres        Apellidos                                                         Codigo \r\n");
            textOutput.append(
                            "--------------------------------------------------------------------------------------- \r\n");
            for (Registro next : listaEscalafon) {
                textOutput.append(next.getCampos().get(CTELINEA).toString()
                    + "   \r\n");
            } // </CODIGO_DESARROLLADO>

            textOutput.append(
                            "--------------------------------------------------------------------------------------- \r\n");
            textOutput.append("Total de Inconsistencias SIN ESCALAFON: "
                + listaEscalafon.size() + "  \r\n");

            textOutput.append("Funcionarios sin Genero \r\n");
            textOutput.append(
                            "Nombres        Apellidos                                                          Codigo \r\n");
            textOutput.append(
                            "------------------------------------------------------------------------------------- \r\n");

            List<Registro> listaGenero = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenTotalPersonalControladorUrlEnum.URL4646
                                                                            .getValue())
                                            .getUrl(), param));

            for (Registro next : listaGenero) {
                textOutput.append(next.getCampos().get(CTELINEA).toString()
                    + " \r\n  ");
            } // </CODIGO_DESARROLLADO>

            textOutput.append(
                            " ---------------------------------------------------------------------------------------- \r\n");
            textOutput.append("Total de Inconsistencias SIN GENERO: "
                + listaGenero.size() + " \r\n ");

            textOutput.append("Funcionarios sin Tipo de Contrato \r\n");
            textOutput.append(
                            "Nombres        Apellidos                                                          Codigo \r\n");
            textOutput.append(
                            "---------------------------------------------------------------------------------------- \r\n");

            List<Registro> listaTipoContrato = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenTotalPersonalControladorUrlEnum.URL4747
                                                                            .getValue())
                                            .getUrl(), param));

            for (Registro next : listaTipoContrato) {
                textOutput.append(next.getCampos().get(CTELINEA).toString()
                    + " \r\n");
            } // </CODIGO_DESARROLLADO>

            textOutput.append(
                            "---------------------------------------------------------------------------------------- \r\n");
            textOutput.append("Total de Inconsistencias SIN TIPO CONTRATO: "
                + listaTipoContrato.size() + " \r\n");

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return textOutput.toString();
    }

    public void getInforme(FORMATOS formato) {
        archivoDescarga = null;
        String cond1 = "";
        String cond2 = "";
        String nombreReporte = "";
        try {
            if ("true".equals(escalafon)) {
                cond2 = " AND ACUM_DANE_DISTIC.escalafon IN('" + nivel + "')";
                cond1 = "WHERE ACUM_DANE_NIVELES_INI.escalafon IN('" + nivel
                    + "')";
            }
            else if ("false".equals(escalafon) && !"0".equals(proceso)) {
                cond2 = "";
                cond1 = " WHERE ACUM_DANE_NIVELES_INI.ID_DE_PROCESO IN('"
                    + proceso
                    + "') ";
            }
            else if ("false".equals(escalafon) && "0".equals(proceso)) {

                cond2 = "";
                cond1 = " ";
            }

            String titulo2 = SysmanFunciones.concatenar("Proceso  ",
                            service.buscarEnLista(proceso, "ID_DE_PROCESO",
                                            "NOMBRE_PROCESO",
                                            listaProceso),
                            " Entre: ",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes1)],
                            " de ", ano1, " Periodo ", periodo1, " y ",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes2)],
                            " de ", ano2, " Periodo ", periodo2);

            String titulo3 = obtenerTitulo();

            String amperiodo1 = SysmanFunciones.concatenar(ano1,
                            SysmanFunciones.padl(mes1, 2, "0"),
                            SysmanFunciones.padl(periodo1, 2, "0"));
            String amperiodo2 = SysmanFunciones.concatenar(ano2,
                            SysmanFunciones.padl(mes2, 2, "0"),
                            SysmanFunciones.padl(periodo2, 2, "0"));

            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE

            parametros.put("PR_TITULO2", titulo2);
            parametros.put("PR_TITULO3", titulo3);
            parametros.put("PR_NOMBREEMPRESA", nombreCompania);

            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("periodo1", amperiodo1);
            reemplazar.put("periodo2", amperiodo2);
            reemplazar.put("COND1", cond1);
            reemplazar.put("COND2", cond2);
            Reporteador.resuelveConsulta("000165INFORMERESUMENTOTALPERMPV",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            ByteArrayInputStream planoSerializado = JsfUtil
                            .serializarPlano(getInconsNominaNiveles());

            ByteArrayInputStream reporteSerializado = JsfUtil
                            .serializarReporte(
                                            "000165INFORMERESUMENTOTALPERMPV",
                                            parametros,
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            formato);

            if (FORMATOS.PDF.equals(formato)) {
                nombreReporte = "ReporteDaneNiveles.pdf";
            }
            else {
                nombreReporte = "ReporteDaneNiveles.xls";
            }

            ByteArrayInputStream[] archiSerial = { reporteSerializado,
                                                   planoSerializado };
            String[] nombres = { nombreReporte,
                                 "InconsNominaNiveles.txt" };

            archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                            archiSerial, nombres);
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private String obtenerTitulo() {
        if ("true".equals(escalafon)) {
            return "Nivel: "
                + service.buscarEnLista(nivel, "CODIGO", "NOMBRE", listaNIVEL);
        }
        else {
            return "Todos los Niveles";
        }
    }

    public void oprimirdanemeses() {
        // <CODIGO_DESARROLLADO>

        getInformeAcumuladoDanePorMeses();
        // </CODIGO_DESARROLLADO>
    }

    public void getInformeAcumuladoDanePorMeses() {
        // </CODIGO_DESARROLLADO>
        archivoDescarga = null;
        try {

            String condicionPivot = SysmanFunciones.nvl(ejbNominaUno
                            .getPrepararPivotDaneEmpleado(compania, ano1
                                + String.format("%02d", Integer.parseInt(mes1))
                                + String.format("%02d",
                                                Integer.parseInt(periodo1)),
                                            ano2 + String.format("%02d", Integer
                                                            .parseInt(mes2))
                                                + String.format("%02d",
                                                                Integer.parseInt(
                                                                                periodo2))),
                            "").toString();

            if (SysmanFunciones.validarVariableVacio(condicionPivot)
                || condicionPivot == null) {

                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3751"));
            }
            else {

                Map<String, Object> reemplazar = new TreeMap<>();

                reemplazar.put("ano1", ano1);
                reemplazar.put("mes1", mes1);
                reemplazar.put("periodo1", periodo1);
                reemplazar.put("ano2", ano2);
                reemplazar.put("mes2", mes2);
                reemplazar.put("periodo2", periodo2);
                reemplazar.put("pivot", condicionPivot);

                String sql = Reporteador
                                .resuelveConsulta("800095ResumenTotalPersonal",
                                                Integer.parseInt(
                                                                SessionUtil.getModulo()),
                                                reemplazar);

                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(
                                sql,
                                ConectorPool.ESQUEMA_SYSMAN,
                                ReportesBean.FORMATOS.EXCEL97);
            }
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException | NumberFormatException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarProceso() {
        ano1 = ano2 = mes1 = mes2 = periodo1 = periodo2 = null;
    }

    public void cambiarAno1() {
        // <CODIGO_DESARROLLADO>
        cargarListaMes1();
        cargarListaPeriodo1();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno2() {
        // <CODIGO_DESARROLLADO>
        cargarListaMes2();
        cargarListaPeriodo2();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes2() {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodo2();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPeriodo1() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPeriodo2() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarescalafon() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes1() {

        // <CODIGO_DESARROLLADO>
        cargarListaPeriodo1();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getEscalafon() {
        return escalafon;
    }

    public void setEscalafon(String escalafon) {
        this.escalafon = escalafon;
    }

    public String getAno1() {
        return ano1;
    }

    public void setAno1(String ano1) {
        this.ano1 = ano1;
    }

    public String getAno2() {
        return ano2;
    }

    public void setAno2(String ano2) {
        this.ano2 = ano2;
    }

    public String getMes1() {
        return mes1;
    }

    public void setMes1(String mes1) {
        this.mes1 = mes1;
    }

    public String getMes2() {
        return mes2;
    }

    public void setMes2(String mes2) {
        this.mes2 = mes2;
    }

    public String getPeriodo1() {
        return periodo1;
    }

    public void setPeriodo1(String periodo1) {
        this.periodo1 = periodo1;
    }

    public String getPeriodo2() {
        return periodo2;
    }

    public void setPeriodo2(String periodo2) {
        this.periodo2 = periodo2;
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public List<Registro> getListaAno1() {
        return listaAno1;
    }

    public void setListaAno1(List<Registro> listaAno1) {
        this.listaAno1 = listaAno1;
    }

    public List<Registro> getListaAno2() {
        return listaAno2;
    }

    public void setListaAno2(List<Registro> listaAno2) {
        this.listaAno2 = listaAno2;
    }

    public List<Registro> getListaMes1() {
        return listaMes1;
    }

    public void setListaMes1(List<Registro> listaMes1) {
        this.listaMes1 = listaMes1;
    }

    public List<Registro> getListaMes2() {
        return listaMes2;
    }

    public void setListaMes2(List<Registro> listaMes2) {
        this.listaMes2 = listaMes2;
    }

    public List<Registro> getListaPeriodo1() {
        return listaPeriodo1;
    }

    public void setListaPeriodo1(List<Registro> listaPeriodo1) {
        this.listaPeriodo1 = listaPeriodo1;
    }

    public List<Registro> getListaPeriodo2() {
        return listaPeriodo2;
    }

    public void setListaPeriodo2(List<Registro> listaPeriodo2) {
        this.listaPeriodo2 = listaPeriodo2;
    }

    public List<Registro> getListaProceso() {
        return listaProceso;
    }

    public void setListaProceso(List<Registro> listaProceso) {
        this.listaProceso = listaProceso;
    }

    public List<Registro> getListaNIVEL() {
        return listaNIVEL;
    }

    public void setListaNIVEL(List<Registro> listaNIVEL) {
        this.listaNIVEL = listaNIVEL;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

}
