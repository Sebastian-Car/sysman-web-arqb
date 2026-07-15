package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Map;
import javax.naming.NamingException;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 16/03/2016
 */
@ManagedBean
@ViewScoped
public class CalculoDiferenciasRetroactivoControlador
                extends BeanBaseDatosAcme {

    private final String compania;
    private final String modulo;
    private final String anioNomina;
    private final String procesoNomina;
    private final String mesNomina;
    private final String periodoNomina;
    private final String anioNominaStr;
    private final String mesNominaStr;
    private List<Registro> listaAno1;
    private List<Registro> listaMes1;
    private List<Registro> listaPeriodo1;
    private List<Registro> listaAno2;
    private List<Registro> listaMes2;
    private List<Registro> listaPeriodo2;
    private RegistroDataModel listaEMPLEADO1;
    private RegistroDataModel listaProceso;
    private RegistroDataModel listaConceptoI;
    private RegistroDataModel listaRR1;
    private RegistroDataModel listaRR2;
    private String parafiscales;
    private String neto;
    private String calcularRetencion;
    private String actualizarRF;
    private String anio;
    private String mes;
    private String periodo;
    private String empleado;
    private String proceso;
    private String concepto;
    private String anio2;
    private String mes2;
    private String periodo2;
    private String rr1;
    private String rr2;
    private String nomEmpleado;
    private String numProceso;

    private final String idConceptoCons;
    private final String pckAcmeCons;
    private final String msgInterrumpidaCons;
    private final String mensajeTbCons;
    private final String msgTbCons;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    private String resultado;

    public CalculoDiferenciasRetroactivoControlador() {

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        procesoNomina = (String) SessionUtil.getSessionVar("procesoNomina");
        anioNomina = (String) SessionUtil.getSessionVar("anioNomina");
        mesNomina = (String) SessionUtil.getSessionVar("mesNomina");
        periodoNomina = (String) SessionUtil.getSessionVar("periodoNomina");
        pckAcmeCons = "PCK_DATOS.FC_ACME";
        idConceptoCons = "ID_DE_CONCEPTO";
        msgInterrumpidaCons = "MSM_TRANS_INTERRUMPIDA";
        mensajeTbCons = "TB_TB837";
        msgTbCons = "TB_TB2507";
        anioNominaStr = "anioNomina";
        mesNominaStr = "mesNomina";
        try {
            numFormulario = 108;
            registro = new Registro(new HashMap<String, Object>());
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(CalculoDiferenciasRetroactivoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @Override
    public void iniciarListas() {
        // Metodo heredado de la clase BeanBase

    }

    @Override
    public void iniciarListasSub() {
        // Metodo heredado de la clase BeanBase
    }

    @Override
    public void iniciarListasSubNulo() {
        // Metodo heredado de la clase BeanBase
    }

    @PostConstruct
    public void inicializar() {
        tabla = "";
        asignarOrigenDatos();
        reasignarOrigenGrilla();
        anio = anio2 = anioNomina;
        mes = mes2 = mesNomina;
        periodo = periodo2 = periodoNomina;
        cargarListaEMPLEADO1();
        cargarListaProceso();
        numProceso = procesoNomina;
        proceso = listaProceso.getRegistroUnico(
                        "ID_DE_PROCESO = " + numProceso) == null
                            ? ""
                            : String.valueOf(listaProceso
                                            .getRegistroUnico("ID_DE_PROCESO = "
                                                + numProceso)
                                            .getCampos().get("NOMBRE_PROCESO"));
        cargarListaConceptoI();
        cargarListaRR1();
        cargarListaRR2();
        cargarListaAno1();
        cargarListaMes1();
        cargarListaPeriodo1();
        cargarListaAno2();
        cargarListaMes2();
        cargarListaPeriodo2();
    }

    @Override
    public void asignarOrigenDatos() {
        origenDatos = "";
    }

    @Override
    public void reasignarOrigenGrilla() {
        origenGrilla = "";
    }

    public void cargarListaAno1() {
        listaAno1 = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                        "      SELECT DISTINCT "
                            + "     PERIODOS.ANO "
                            + "    FROM "
                            + "     PERIODOS "
                            + " WHERE "
                            + "         PERIODOS.ANO <> 0"
                            + "  AND "
                            + "         PERIODOS.COMPANIA= '" + compania + "' "
                            + "    ORDER BY "
                            + "     PERIODOS.ANO DESC");
    }

    public void cargarListaMes1() {
        listaMes1 = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                        "SELECT  DISTINCT  PERIODOS.MES, "
                            + "                   MES.NOMBRE "
                            + " FROM MES "
                            + " LEFT JOIN PERIODOS "
                            + "    ON  MES.NUMERO = PERIODOS.MES"
                            + "    AND MES.COMPANIA = PERIODOS.COMPANIA "
                            + " WHERE PERIODOS.MES  NOT IN 0"
                            + "   AND MES.COMPANIA  =  '" + compania + "'"
                            + "   AND MES.ANO       =  " + anio + ""
                            + " ORDER BY 1");
    }

    public void cargarListaPeriodo1() {
        listaPeriodo1 = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                        "         SELECT DISTINCT "
                            + "     PERIODOS.PERIODO,"
                            + "    (PERIODOS.PERIODO || ' ' || UPPER(PERIODOS.NOM_PERIODO)) NOMBRE "
                            + " FROM PERIODOS "
                            + " WHERE PERIODOS.PERIODO     NOT IN  0 "
                            + " AND PERIODOS.COMPANIA      = '" + compania + "'"
                            + " AND PERIODOS.ANO           = " + anio + ""
                            + " AND PERIODOS.MES           = " + mes + ""
                            + " AND PERIODOS.ID_DE_PROCESO = " + numProceso
                            + "");
    }

    public void cargarListaAno2() {
        listaAno2 = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                        "   SELECT DISTINCT "
                            + "     PERIODOS.ANO "
                            + " FROM  PERIODOS "
                            + " WHERE PERIODOS.ANO      NOT IN 0"
                            + "   AND PERIODOS.COMPANIA = '" + compania + "' "
                            + " ORDER BY PERIODOS.ANO DESC");
    }

    public void cargarListaMes2() {
        listaMes2 = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                        "SELECT  DISTINCT  PERIODOS.MES, "
                            + "                   MES.NOMBRE "
                            + " FROM MES "
                            + "     LEFT JOIN PERIODOS "
                            + "       ON  MES.NUMERO   = PERIODOS.MES"
                            + "       AND MES.COMPANIA = PERIODOS.COMPANIA "
                            + " WHERE PERIODOS.MES NOT IN 0"
                            + "   AND MES.COMPANIA = '" + compania + "'"
                            + "   AND MES.ANO      = " + anio + ""
                            + " ORDER BY 1");
    }

    public void cargarListaPeriodo2() {
        listaPeriodo2 = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                        "   SELECT DISTINCT "
                            + "     PERIODOS.PERIODO,"
                            + "    (PERIODOS.PERIODO || ' ' || UPPER(PERIODOS.NOM_PERIODO)) NOMBRE "
                            + " FROM PERIODOS "
                            + " WHERE PERIODOS.PERIODO       NOT IN 0 "
                            + "   AND PERIODOS.COMPANIA      = '" + compania
                            + "'"
                            + "   AND PERIODOS.ANO           = " + anio + ""
                            + "   AND PERIODOS.MES           = " + mes + ""
                            + "   AND PERIODOS.ID_DE_PROCESO = " + numProceso
                            + "");
    }

    public void cargarListaEMPLEADO1() {
        listaEMPLEADO1 = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FR108_nuevo:TBCB385",
                        "SELECT DISTINCT  PERSONAL.NUMERO_DCTO, "
                            + "     PERSONAL.NOMBRECOMPLETO, "
                            + "     PERSONAL.ID_DE_EMPLEADO,"
                            + "     PERSONAL.ESTADO_ACTUAL"
                            + " FROM PERSONAL "
                            + " WHERE PERSONAL.ESTADO_ACTUAL IN (1,3) "
                            + "   AND PERSONAL.COMPANIA       = '" + compania
                            + "'"
                            + " ORDER BY PERSONAL.NOMBRECOMPLETO",
                        true, "NUMERO_DCTO");
    }

    public void cargarListaProceso() {
        listaProceso = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FR108_nuevo:TBCB387",
                        "SELECT DISTINCT  PERIODOS.ID_DE_PROCESO, "
                            + "     PROCESOS_DE_NOMINA.NOMBRE_PROCESO "
                            + " FROM PROCESOS_DE_NOMINA "
                            + "    LEFT JOIN PERIODOS "
                            + "      ON  PROCESOS_DE_NOMINA.COMPANIA      = PERIODOS.COMPANIA"
                            + "      AND PROCESOS_DE_NOMINA.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO"
                            + " WHERE PERIODOS.COMPANIA = '" + compania + "'",
                        true, "ID_DE_PROCESO");
    }

    public void cargarListaConceptoI() {
        listaConceptoI = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FR108_nuevo:TBCB388", "         SELECT DISTINCT "
                            + "       CONCEPTOS.ID_DE_CONCEPTO, "
                            + "     CONCEPTOS.NOMBRE_CONCEPTO "
                            + " FROM CONCEPTOS "
                            + " WHERE CONCEPTOS.COMPANIA = '" + compania + "' "
                            + " ORDER BY     CONCEPTOS.ID_DE_CONCEPTO",
                        true, idConceptoCons);
    }

    public void cargarListaRR1() {
        listaRR1 = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FR108_nuevo:TBCB392", "SELECT DISTINCT "
                            + "     CONCEPTOS.ID_DE_CONCEPTO, "
                            + "     CONCEPTOS.NOMBRE_CONCEPTO, "
                            + "     CONCEPTOS.C_RETRO "
                            + " FROM "
                            + "     CONCEPTOS "
                            + " WHERE CONCEPTOS.C_RETRO IS NOT NULL "
                            + " AND CONCEPTOS.COMPANIA = '" + compania + "'"
                            + " AND CONCEPTOS.CLASE = 3"
                            + " ORDER BY "
                            + "     CONCEPTOS.ID_DE_CONCEPTO",
                        true, idConceptoCons);
    }

    public void cargarListaRR2() {
        listaRR2 = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FR108_nuevo:TBCB393", "SELECT DISTINCT "
                            + "     CONCEPTOS.ID_DE_CONCEPTO, "
                            + "     CONCEPTOS.NOMBRE_CONCEPTO"
                            + " FROM "
                            + "     CONCEPTOS "
                            + " WHERE "
                            + "         CONCEPTOS.ID_DE_CONCEPTO BETWEEN 500 AND 599 "
                            + "             AND "
                            + "         CONCEPTOS.CLASE = 3 "
                            + "             AND "
                            + "         CONCEPTOS.COMPANIA = '" + compania
                            + "' "
                            + " ORDER BY "
                            + "     CONCEPTOS.ID_DE_CONCEPTO",
                        true, idConceptoCons);
    }

    /**
     * Hace el llamado a la funcion
     * PCK_NOMINA_COM5.FC_OPRIMIRSUMARRETROACTIVOS05 la cual realiza
     * la limpieza y poblacion de la tabla "TEMP_SUMA_RETROACTIVO" que
     * almacena los valores correspondientes a la suma de los
     * retroactivos
     */
    public void oprimirSUMARRETROACTIVOS05() {
        // <CODIGO_DESARROLLADO>
        try {
            resultado = Acciones.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA_COM5.FC_OPRIMIRSUMARRETROACTIVOS05",
                            " UN_COMPANIA => '" + compania + "'," +
                                " UN_ANIO     => " + anio,
                            Types.NUMERIC).toString();
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2505")
                            .replace("#$resultado#$", resultado));
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCONSULTARRETRO05() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        try {
            // <CODIGO_DESARROLLADO>

            String strSql = Reporteador
                            .resuelveConsulta("800039DatosRetroactivo05",
                                            Integer.parseInt(modulo),
                                            new HashMap<String, Object>());

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL97,
                            "Datos Retroactivo 05");

            // </CODIGO_DESARROLLADO>
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(msgInterrumpidaCons)
                                + ex.getMessage());
            Logger.getLogger(CalculoDiferenciasRetroactivoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirACTUALIZAANOMESPERIODO() {
        // <CODIGO_DESARROLLADO>
        ConectorPool conectorPool = new ConectorPool();
        try {
            // <CODIGO_DESARROLLADO>
            Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN,
                            "TEMP_SUMA_RETROACTIVO",
                            "ANO = " + anio + ",PERIODO= " + periodo + "",
                            "COMPANIA IS NOT NULL");
            conectorPool.conectar(ConectorPool.ESQUEMA_SYSMAN);
            JsfUtil.agregarMensajeInformativo(idioma.getString(mensajeTbCons));
            // </CODIGO_DESARROLLADO>
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex) {
            Logger.getLogger(CalculoDiferenciasRetroactivoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString(msgInterrumpidaCons)
                                + ex.getMessage());
        }
        finally {
            try {
                conectorPool.getConection().close();
            }
            catch (SQLException ex) {
                Logger.getLogger(CalculoDiferenciasRetroactivoControlador.class
                                .getName()).log(Level.SEVERE, null, ex);
                JsfUtil.agregarMensajeError(
                                idioma.getString(msgInterrumpidaCons)
                                    + ex.getMessage());
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirSUBIRPERIODODEPAGO() {
        // <CODIGO_DESARROLLADO>
        ConectorPool conectorPool = new ConectorPool();
        try {
            // <CODIGO_DESARROLLADO>
            String strSql = "SELECT TEMP_SUMA_RETROACTIVO.COMPANIA, \n"
                + "       TEMP_SUMA_RETROACTIVO.ID_DE_PROCESO, \n"
                + "       TEMP_SUMA_RETROACTIVO.ANO, \n"
                + "       " + mes + ", \n"
                + "       " + periodo + ", \n"
                + "       TEMP_SUMA_RETROACTIVO.ID_DE_EMPLEADO, \n"
                + "       TEMP_SUMA_RETROACTIVO.ID_DE_CONCEPTO, \n"
                + "       TEMP_SUMA_RETROACTIVO.VALOR, \n"
                + "       SYSDATE\n"
                + "FROM TEMP_SUMA_RETROACTIVO";
            conectorPool.conectar(ConectorPool.ESQUEMA_SYSMAN);
            Acciones.insertar(conectorPool, "HISTORICOS",
                            "COMPANIA, ID_DE_PROCESO, ANO, MES, PERIODO, ID_DE_EMPLEADO, ID_DE_CONCEPTO, VALOR, FECHA",
                            strSql);
            JsfUtil.agregarMensajeInformativo(idioma.getString(mensajeTbCons));
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex) {
            Logger.getLogger(CalculoDiferenciasRetroactivoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString(msgInterrumpidaCons)
                                + ex.getMessage());
        }
        finally {
            try {
                conectorPool.getConection().close();
            }
            catch (SQLException ex) {
                Logger.getLogger(CalculoDiferenciasRetroactivoControlador.class
                                .getName()).log(Level.SEVERE, null, ex);
                JsfUtil.agregarMensajeError(
                                idioma.getString(msgInterrumpidaCons)
                                    + ex.getMessage());
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirELIMINARRETROACTIVO05() {
        // <CODIGO_DESARROLLADO>
        ConectorPool conectorPool = new ConectorPool();
        try {
            // <CODIGO_DESARROLLADO>
            conectorPool.conectar(ConectorPool.ESQUEMA_SYSMAN);
            String condicion = "HISTORICOS.COMPANIA = " + compania + " \n"
                + "  AND   HISTORICOS.ANO      = " + anio + " \n"
                + "  AND   HISTORICOS.MES      = " + mes + " \n"
                + "  AND   HISTORICOS.PERIODO  = " + periodo + "";
            setResultado((String) Acciones
                            .ejecutarFuncion(conectorPool.getConection(),
                                            pckAcmeCons,
                                            "'HISTORICOS','E',NULL,NULL,NULL,'"
                                                + condicion + "'",
                                            Types.VARCHAR));
            JsfUtil.agregarMensajeInformativo(idioma.getString(mensajeTbCons));
        }
        catch (NamingException | SQLException | IllegalAccessException
                        | InstantiationException | ClassNotFoundException ex) {
            Logger.getLogger(CalculoDiferenciasRetroactivoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString(msgInterrumpidaCons)
                                + ex.getMessage());
        }
        finally {
            try {
                conectorPool.getConection().close();
            }
            catch (SQLException ex) {
                Logger.getLogger(CalculoDiferenciasRetroactivoControlador.class
                                .getName()).log(Level.SEVERE, null, ex);
                JsfUtil.agregarMensajeError(
                                idioma.getString(msgInterrumpidaCons)
                                    + ex.getMessage());
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCALCULAR() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(empleado)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2506"));
            return;
        }
        try {
            Acciones.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA_COM3.PR_CALCULARDIFRETROACTIVOS",
                            "'" + compania + "'," + empleado + "," + numProceso
                                + "," + anio + "," + mes + "," + periodo + "");
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(msgTbCons));
        }
        catch (NamingException | SQLException ex) {
            Logger.getLogger(CalculoDiferenciasRetroactivoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString(msgInterrumpidaCons)
                                + ex.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirNETOS() {
        // <CODIGO_DESARROLLADO>
        try {
            Acciones.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA_COM3.PR_NETOSRETROACTIVO",
                            "'" + compania + "'," + numProceso + "," + mes + ","
                                + anio + "," + periodo + "");
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(msgTbCons));

        }
        catch (NamingException | SQLException ex) {
            Logger.getLogger(CalculoDiferenciasRetroactivoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString(msgInterrumpidaCons)
                                + ex.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirRETEFTE() {
        // <CODIGO_DESARROLLADO>
        ConectorPool con = new ConectorPool();
        String parametro = null;
        try {
            con.conectar(ConectorPool.ESQUEMA_SYSMAN);
            parametro = Acciones.getParametro(con, compania,
                            "LIQUIDAR RETE FUENTE EN PERIODOS 05 RETROACTIVO",
                            SessionUtil.getModulo(), "SYSDATE");
        }
        catch (NamingException | SQLException ex) {
            Logger.getLogger(CalculoDiferenciasRetroactivoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString(msgInterrumpidaCons)
                                + ex.getMessage());
        }
        finally {
            try {
                con.getConection().close();
            }
            catch (SQLException ex) {
                Logger.getLogger(CalculoDiferenciasRetroactivoControlador.class
                                .getName()).log(Level.SEVERE, null, ex);
                JsfUtil.agregarMensajeError(
                                idioma.getString(msgInterrumpidaCons)
                                    + ex.getMessage());
            }
        }
        if ((parametro != null) && "SI".equals(parametro)) {
            try {
                Acciones.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                                "PCK_NOMINA_COM3.PR_RETEFTERETROACTIVOS",
                                "" + compania + "," + empleado + ","
                                    + numProceso + "," + anio + "," + mes + ","
                                    + periodo + "");
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(msgTbCons));
                // </CODIGO_DESARROLLADO>
            }
            catch (NamingException | SQLException ex) {
                Logger.getLogger(CalculoDiferenciasRetroactivoControlador.class
                                .getName()).log(Level.SEVERE, null, ex);
                JsfUtil.agregarMensajeError(
                                idioma.getString(msgInterrumpidaCons)
                                    + ex.getMessage());
            }
        }
        else {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TB_TB2508"));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirNEGATIVOS() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        HashMap<String, Object> reemplazar = new HashMap<>();
        try {
            // <CODIGO_DESARROLLADO>

            reemplazar.put("anio", anio);
            reemplazar.put("mes", mes);
            String strSql = Reporteador
                            .resuelveConsulta("800040ConceptosNegativos",
                                            Integer.parseInt(modulo),
                                            reemplazar);

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL97,
                            "Conceptos Negativos");
            // </CODIGO_DESARROLLADO>
        }
        catch (SQLException | JRException | IOException
                        | DRException | SysmanException ex) {
            Logger.getLogger(CalculoDiferenciasRetroactivoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString(msgInterrumpidaCons)
                                + ex.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirREVISIONPAGOS() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        HashMap<String, Object> reemplazar = new HashMap<>();
        try {
            // <CODIGO_DESARROLLADO>
            reemplazar.put("anio", anio);

            String strSql = Reporteador
                            .resuelveConsulta("800041RevisionPagos",
                                            Integer.parseInt(modulo),
                                            reemplazar);

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL97,
                            idioma.getString("TB_TB2952"));
            // </CODIGO_DESARROLLADO>
        }
        catch (SQLException | JRException | IOException
                        | DRException | SysmanException ex) {
            Logger.getLogger(CalculoDiferenciasRetroactivoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString(msgInterrumpidaCons)
                                + ex.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPAGORETRO05() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        HashMap<String, Object> reemplazar = new HashMap<>();
        try {
            // <CODIGO_DESARROLLADO>

            reemplazar.put("anio", anio);
            reemplazar.put("mes", mes);
            String strSql = Reporteador
                            .resuelveConsulta("800042PagoMesRetroactivo",
                                            Integer.parseInt(modulo),
                                            reemplazar);

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL97,
                            "Pago Mes Retroactivo 05");
            conectorPool.getConection().close();
            // </CODIGO_DESARROLLADO>
        }
        catch (SQLException | JRException | IOException
                        | DRException | SysmanException ex) {
            Logger.getLogger(CalculoDiferenciasRetroactivoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString(msgInterrumpidaCons)
                                + ex.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCerrarNomina() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirbasesparaf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        HashMap<String, Object> reemplazar = new HashMap<>();
        try {
            // <CODIGO_DESARROLLADO>

            reemplazar.put(anioNominaStr, anioNomina);
            reemplazar.put(mesNominaStr, mesNomina);
            reemplazar.put("concepto", concepto);
            String strSql = Reporteador
                            .resuelveConsulta("800043BasesParafiscales",
                                            Integer.parseInt(modulo),
                                            reemplazar);

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL97,
                            "Bases Parafiscales");
            // </CODIGO_DESARROLLADO>
        }
        catch (SQLException | JRException | IOException
                        | DRException | SysmanException ex) {
            Logger.getLogger(CalculoDiferenciasRetroactivoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString(msgInterrumpidaCons)
                                + ex.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirtrasladar() {
        // <CODIGO_DESARROLLADO>
        try {

            String valores = "HISTORICOS.ANO 		= " + anio2 + ",\n"
                + "	      HISTORICOS.MES 		= " + mes2 + ",\n"
                + "	      HISTORICOS.PERIODO 	= " + periodo2 + "";
            String condicion = "HISTORICOS.ANO 	= " + anio + "\n"
                + "  AND   HISTORICOS.MES  	= " + mes + "\n"
                + "  AND   HISTORICOS.PERIODO  = " + periodo + " \n"
                + "  AND   HISTORICOS.COMPANIA =" + compania + "";
            Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, "HISTORICOS",
                            valores, condicion);
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex) {
            Logger.getLogger(CalculoDiferenciasRetroactivoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString(msgInterrumpidaCons)
                                + ex.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirRr3DiferenciasV() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (SysmanFunciones.validarVariableVacio(rr2)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2510"));
            return;
        }
        HashMap<String, Object> reemplazar = new HashMap<>();
        try {

            reemplazar.put(anioNominaStr, anioNomina);
            reemplazar.put(mesNominaStr, mesNomina);
            reemplazar.put("rr2", rr2);
            String strSql = Reporteador
                            .resuelveConsulta("800044Rr3Diferencias",
                                            Integer.parseInt(modulo),
                                            reemplazar);

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL97,
                            "Rr3_Diferencias");

        }
        catch (SQLException | JRException | IOException
                        | DRException | SysmanException ex) {
            Logger.getLogger(CalculoDiferenciasRetroactivoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString(msgInterrumpidaCons)
                                + ex.getMessage());
        }
        finally {
            try {
                conectorPool.getConection().close();
            }
            catch (SQLException ex) {
                Logger.getLogger(CalculoDiferenciasRetroactivoControlador.class
                                .getName()).log(Level.SEVERE, null, ex);
                JsfUtil.agregarMensajeError(
                                idioma.getString(msgInterrumpidaCons)
                                    + ex.getMessage());
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirRRR44Fsp() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        HashMap<String, Object> reemplazar = new HashMap<>();
        try {
            // <CODIGO_DESARROLLADO>

            reemplazar.put(anioNominaStr, anioNomina);
            reemplazar.put(mesNominaStr, mesNomina);
            String strSql = Reporteador
                            .resuelveConsulta("800045FspAcumulado",
                                            Integer.parseInt(modulo),
                                            reemplazar);

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL97,
                            "FSP Acumulado");
            // </CODIGO_DESARROLLADO>
        }
        catch (SQLException | JRException | IOException
                        | DRException | SysmanException ex) {
            Logger.getLogger(CalculoDiferenciasRetroactivoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString(msgInterrumpidaCons)
                                + ex.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirRevisarFnaRetiradosRetroactivo() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        HashMap<String, Object> reemplazar = new HashMap<>();
        try {
            // <CODIGO_DESARROLLADO>

            reemplazar.put(anioNominaStr, anioNomina);
            reemplazar.put(mesNominaStr, mesNomina);
            String strSql = Reporteador
                            .resuelveConsulta("800046FnaRetirados",
                                            Integer.parseInt(modulo),
                                            reemplazar);

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL97,
                            "FNA Retirados");
            // </CODIGO_DESARROLLADO>
        }
        catch (SQLException | JRException | IOException
                        | DRException | SysmanException ex) {
            Logger.getLogger(CalculoDiferenciasRetroactivoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString(msgInterrumpidaCons)
                                + ex.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirActualizarSueldoAlRetroActivo() {
        // <CODIGO_DESARROLLADO>
        try {
            // <CODIGO_DESARROLLADO>
            String subconsulta = "   ' (SELECT CATEGORIA.SALARIO_BASE\n"
                + " 				FROM PERSONAL "
                + "                                     LEFT JOIN CATEGORIA ON PERSONAL.COMPANIA = CATEGORIA.COMPANIA \n"
                + "                                         AND PERSONAL.ESCALAFON = CATEGORIA.ESCALAFON \n"
                + "                                         AND PERSONAL.ID_DE_CATEGORIA = CATEGORIA.ID_DE_CATEGORIA \n"
                + "                                         AND PERSONAL.ANO = CATEGORIA.ANO   \n"
                + "              WHERE H.COMPANIA = PERSONAL.COMPANIA	\n"
                + "                AND H.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO \n"
                + "                AND(H.COMPANIA   = ''" + compania + "'' \n"
                + "                AND H.ANO        = " + anioNomina + " \n"
                + "                AND H.MES        = " + mesNomina + ") \n"
                + "                AND (H.PERIODO   = " + periodoNomina + " \n"
                + "                AND H.ID_DE_CONCEPTO = 001))' ";
            setResultado((String) Acciones.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN, pckAcmeCons,
                            "' HISTORICOS H', 'M', 'H.VALOR = '||" + subconsulta
                                + ", NULL, NULL, ' EXISTS '|| " + subconsulta
                                + " ",
                            Types.VARCHAR));
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(msgTbCons));
        }
        catch (SQLException | NamingException | IllegalAccessException
                        | InstantiationException | ClassNotFoundException ex) {
            Logger.getLogger(CalculoDiferenciasRetroactivoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString(msgInterrumpidaCons)
                                + ex.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno1() {
        // <CODIGO_DESARROLLADO>
        mes = null;
        periodo = null;
        cargarListaMes1();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes1() {
        // <CODIGO_DESARROLLADO>
        periodo = null;
        cargarListaPeriodo1();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno2() {
        // <CODIGO_DESARROLLADO>
        mes2 = null;
        periodo2 = null;
        cargarListaMes2();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes2() {
        // <CODIGO_DESARROLLADO>
        periodo2 = null;
        cargarListaPeriodo2();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPARAFISCALES() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcuadreneto() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCALCULARRETENCION() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarACTUALIZARF() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaEMPLEADO1(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        empleado = registroAux.getCampos().get("NUMERO_DCTO").toString();
        nomEmpleado = registroAux.getCampos().get("NOMBRECOMPLETO").toString();
    }

    public void seleccionarFilaProceso(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        proceso = registroAux.getCampos().get("NOMBRE_PROCESO").toString();
        numProceso = registroAux.getCampos().get("ID_DE_PROCESO").toString();
    }

    public void seleccionarFilaConceptoI(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        concepto = (String) registroAux.getCampos().get(idConceptoCons);
    }

    public void seleccionarFilaRR1(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        rr1 = (String) registroAux.getCampos().get(idConceptoCons);
    }

    public void seleccionarFilaRR2(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        rr2 = (String) registroAux.getCampos().get(idConceptoCons);
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR108-AL_ABRIR Private Sub Form_Open(Cancel As Integer) log
         * "Ingres� al Procesos, Calculo Diferencias Retroactivo" If
         * Me.Periodo1 = "13" Then Me.Preparar.visible = True
         * Me.Texto45.visible = True Me.Empleado.visible = True Else
         * Me.Preparar.visible = False Me.Texto45.visible = False
         * Me.Empleado.visible = False End If formularioAbrir 1,
         * Me.Name End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public List<Registro> getListaAno1() {
        return listaAno1;
    }

    public void setListaAno1(List<Registro> listaAno1) {
        this.listaAno1 = listaAno1;
    }

    public List<Registro> getListaMes1() {
        return listaMes1;
    }

    public void setListaMes1(List<Registro> listaMes1) {
        this.listaMes1 = listaMes1;
    }

    public List<Registro> getListaPeriodo1() {
        return listaPeriodo1;
    }

    public void setListaPeriodo1(List<Registro> listaPeriodo1) {
        this.listaPeriodo1 = listaPeriodo1;
    }

    public List<Registro> getListaAno2() {
        return listaAno2;
    }

    public void setListaAno2(List<Registro> listaAno2) {
        this.listaAno2 = listaAno2;
    }

    public List<Registro> getListaMes2() {
        return listaMes2;
    }

    public void setListaMes2(List<Registro> listaMes2) {
        this.listaMes2 = listaMes2;
    }

    public List<Registro> getListaPeriodo2() {
        return listaPeriodo2;
    }

    public void setListaPeriodo2(List<Registro> listaPeriodo2) {
        this.listaPeriodo2 = listaPeriodo2;
    }

    public RegistroDataModel getListaEMPLEADO1() {
        return listaEMPLEADO1;
    }

    public void setListaEMPLEADO1(RegistroDataModel listaEMPLEADO1) {
        this.listaEMPLEADO1 = listaEMPLEADO1;
    }

    public RegistroDataModel getListaProceso() {
        return listaProceso;
    }

    public void setListaProceso(RegistroDataModel listaProceso) {
        this.listaProceso = listaProceso;
    }

    public RegistroDataModel getListaConceptoI() {
        return listaConceptoI;
    }

    public void setListaConceptoI(RegistroDataModel listaConceptoI) {
        this.listaConceptoI = listaConceptoI;
    }

    public RegistroDataModel getListaRR1() {
        return listaRR1;
    }

    public void setListaRR1(RegistroDataModel listaRR1) {
        this.listaRR1 = listaRR1;
    }

    public RegistroDataModel getListaRR2() {
        return listaRR2;
    }

    public void setListaRR2(RegistroDataModel listaRR2) {
        this.listaRR2 = listaRR2;
    }

    public String getParafiscales() {
        return parafiscales;
    }

    public void setParafiscales(String parafiscales) {
        this.parafiscales = parafiscales;
    }

    public String getNeto() {
        return neto;
    }

    public void setNeto(String neto) {
        this.neto = neto;
    }

    public String getCalcularRetencion() {
        return calcularRetencion;
    }

    public void setCalcularRetencion(String calcularRetencion) {
        this.calcularRetencion = calcularRetencion;
    }

    public String getActualizarRF() {
        return actualizarRF;
    }

    public void setActualizarRF(String actualizarRF) {
        this.actualizarRF = actualizarRF;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getEmpleado() {
        return empleado;
    }

    public void setEmpleado(String empleado) {
        this.empleado = empleado;
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getAnio2() {
        return anio2;
    }

    public void setAnio2(String anio2) {
        this.anio2 = anio2;
    }

    public String getMes2() {
        return mes2;
    }

    public void setMes2(String mes2) {
        this.mes2 = mes2;
    }

    public String getPeriodo2() {
        return periodo2;
    }

    public void setPeriodo2(String periodo2) {
        this.periodo2 = periodo2;
    }

    public String getRr1() {
        return rr1;
    }

    public void setRr1(String rr1) {
        this.rr1 = rr1;
    }

    public String getRr2() {
        return rr2;
    }

    public void setRr2(String rr2) {
        this.rr2 = rr2;
    }

    public String getNomEmpleado() {
        return nomEmpleado;
    }

    public void setNomEmpleado(String nomEmpleado) {
        this.nomEmpleado = nomEmpleado;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
}
