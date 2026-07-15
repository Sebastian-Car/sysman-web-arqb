package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseContinuoNAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilidad.enums.MovimientosCuentasControladorEnum;
import com.sysman.contabilidad.enums.MovimientosCuentasControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author cmanrique
 *
 * @author jlramirez
 * @version 2, 10/04/2017, proceso de Refactoring y modificaciones
 * segun especificaciones de SONARLINT
 *
 * @author sdaza
 * @version 3, 05/06/2017, incluir en el metodo
 * ejecutaractualizarTotales parametro para aplicar el filtro.
 *
 * @version 4, 12/06/2017 jrodriguezr Se refactoriza el c�digo: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 */
@ManagedBean
@ViewScoped
public class MovimientosCuentasControlador extends BeanBaseContinuoNAcmeImpl {

    private final String compania;
    private String anio;
    private String cuenta;
    private Map<String, Object> rid;
    private String nombre;
    private int mesInicial;
    private int mesFinal;
    private double saldoInicial;
    private double saldoFinal;
    private String totalCredito;
    private String totalDebito;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of MovimientosCuentasControlador
     */
    public MovimientosCuentasControlador() {
        super();
        compania = SessionUtil.getCompania();

        numFormulario = GeneralCodigoFormaEnum.MOVIMIENTOS_CUENTAS_CONTROLADOR
                        .getCodigo();
        try {
            validarPermisos();
            Calendar calendario = new GregorianCalendar();
            mesInicial = calendario.get(Calendar.MONTH) + 1;
            mesFinal = mesInicial;
            Map<String, Object> parametros = SessionUtil.getFlash();
            if (parametros != null) {
                anio = (String) parametros.get("anio");
                rid = (Map<String, Object>) parametros.get("rid");
                cuenta = (String) parametros.get("cuenta");
            }
        }
        catch (SysmanException ex) {
            Logger.getLogger(MovimientosCuentasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = MovimientosCuentasControladorEnum.TABLA.getValue();
        reasignarOrigen();
        abrirFormulario();
    }

    public void reasignarOrigen() {
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        MovimientosCuentasControladorUrlEnum.URL3728
                                        .getValue());
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
        parametrosListado.put(GeneralParameterEnum.ID_PLAN.getName(), cuenta);
        parametrosListado.put(
                        MovimientosCuentasControladorEnum.PARAM0.getValue(),
                        mesInicial);
        parametrosListado.put(
                        MovimientosCuentasControladorEnum.PARAM1.getValue(),
                        mesFinal);
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTotalCredito() {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
        return new DecimalFormat("#,##0.00", dfs)
                        .format(Double.parseDouble(totalCredito));
    }

    public void setTotalCredito(String totalCredito) {
        this.totalCredito = totalCredito;
    }

    public String getTotalDebito() {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
        return new DecimalFormat("#,##0.00", dfs)
                        .format(Double.parseDouble(totalDebito));
    }

    public void setTotalDebito(String totalDebito) {
        this.totalDebito = totalDebito;
    }

    // <CODIGO_DESARROLLADO>
    public int getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(int mesInicial) {
        this.mesInicial = mesInicial;
    }

    public int getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(int mesFinal) {
        this.mesFinal = mesFinal;
    }

    public double getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(double saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public double getSaldoFinal() {
        return saldoFinal;
    }

    public void setSaldoFinal(double saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        String[] datos = buscarSaldoMeses(mesInicial, mesFinal);
        nombre = datos[0];
        saldoInicial = Double.parseDouble(datos[1]);
        saldoFinal = Double.parseDouble(datos[2]);
        totalDebito = datos[3];
        totalCredito = datos[4];

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMesInicial() {
        cargarOrigen();
    }

    public void cambiarMesFinal() {
        cargarOrigen();
    }

    public void ejecutaractualizarTotales() {
        // mzanguna - Metodo que se ejecuta al filtar en la grilla
        listaInicial.getFilters();
        HashMap<String, Object> rsTotales = new HashMap();
        Map<String, Object> parametros = listaInicial.getFilters();
        parametros.put("COMPANIA", compania);
        parametros.put(MovimientosCuentasControladorEnum.PARAM0.getValue(),
                        mesInicial);
        parametros.put(MovimientosCuentasControladorEnum.PARAM1.getValue(),
                        mesFinal);
        parametros.put(GeneralParameterEnum.ANO.getName(), anio);
        parametros.put(GeneralParameterEnum.ID_PLAN.getName(), cuenta);

        parametros.put("TIPO_CPTE", parametros.get("campos['TIPO_CPTE']"));
        parametros.remove("campos['TIPO_CPTE']");

        parametros.put("COMPROBANTE", parametros.get("campos['COMPROBANTE']"));
        parametros.remove("campos['COMPROBANTE']");

        parametros.put("FECHA", parametros.get("campos['FECHA']"));
        parametros.remove("campos['FECHA']");

        parametros.put("VALOR_DEBITO",
                        parametros.get("campos['VALOR_DEBITO']"));
        parametros.remove("campos['VALOR_DEBITO']");

        parametros.put("VALOR_CREDITO",
                        parametros.get("campos['VALOR_CREDITO']"));
        parametros.remove("campos['VALOR_CREDITO']");

        parametros.put("DESCRIPCION", parametros.get("campos['DESCRIPCION']"));
        parametros.remove("campos['DESCRIPCION']");

        parametros.put("TIPO_DOCUMENTO",
                        parametros.get("campos['TIPO_DOCUMENTO']"));
        parametros.remove("campos['TIPO_DOCUMENTO']");

        parametros.put("NRO_DOCUMENTO",
                        parametros.get("campos['NRO_DOCUMENTO']"));
        parametros.remove("campos['NRO_DOCUMENTO']");

        parametros.put("CENTRO_COSTO",
                        parametros.get("campos['CENTRO_COSTO']"));
        parametros.remove("campos['CENTRO_COSTO']");

        parametros.put("CENTRO_COSTO_NOM",
                        parametros.get("campos['CENTRO_COSTO_NOM']"));
        parametros.remove("campos['CENTRO_COSTO_NOM']");

        parametros.put("TERCERO", parametros.get("campos['TERCERO']"));
        parametros.remove("campos['TERCERO']");

        parametros.put("SUCURSAL", parametros.get("campos['SUCURSAL']"));
        parametros.remove("campos['SUCURSAL']");

        parametros.put("TERCERO_NOM", parametros.get("campos['TERCERO_NOM']"));
        parametros.remove("campos['TERCERO_NOM']");

        parametros.put("AUXILIAR", parametros.get("campos['AUXILIAR']"));
        parametros.remove("campos['AUXILIAR']");

        parametros.put("AUXILIAR_NOM",
                        parametros.get("campos['AUXILIAR_NOM']"));
        parametros.remove("campos['AUXILIAR_NOM']");

        parametros.put("REFERENCIA", parametros.get("campos['REFERENCIA']"));
        parametros.remove("campos['REFERENCIA']");

        parametros.put("REFERENCIA_NOMBRE",
                        parametros.get("campos['REFERENCIA_NOMBRE']"));
        parametros.remove("campos['REFERENCIA_NOMBRE']");

        parametros.put("FUENTE_RECURSO",
                        parametros.get("campos['FUENTE_RECURSO']"));
        parametros.remove("campos['FUENTE_RECURSO']");

        parametros.put("FUENTE_RECURSO_NOMBRE",
                        parametros.get("campos['FUENTE_RECURSO_NOMBRE']"));
        parametros.remove("campos['FUENTE_RECURSO_NOMBRE']");

        parametros.put("CUENTA", parametros.get("campos['CUENTA']"));
        parametros.remove("campos['CUENTA']");

        UrlBean urlReg = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MovimientosCuentasControladorUrlEnum.URL3198
                                                        .getValue());
        try {
            rsTotales = (HashMap<String, Object>) requestManager
                            .get(urlReg.getUrl(), parametros).getFields();
        }
        catch (SystemException e) {
            Logger.getLogger(MovimientosCuentasControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }

        totalCredito = rsTotales.get(
                        MovimientosCuentasControladorEnum.CREDITO.getValue())
                        .toString();
        totalDebito = rsTotales.get(
                        MovimientosCuentasControladorEnum.DEBITO.getValue())
                        .toString();

    }

    public void cargarOrigen() {
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        MovimientosCuentasControladorUrlEnum.URL3728
                                        .getValue());
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
        parametrosListado.put(GeneralParameterEnum.ID_PLAN.getName(), cuenta);
        parametrosListado.put(
                        MovimientosCuentasControladorEnum.PARAM0.getValue(),
                        mesInicial);
        parametrosListado.put(
                        MovimientosCuentasControladorEnum.PARAM1.getValue(),
                        mesFinal);
        abrirFormulario();
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        String ruta = "/plancontable.sysman";
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", rid);
        parametros.put("anio", anio);
        Direccionador direccionador = new Direccionador();
        direccionador.setRuta(ruta);
        direccionador.setParametros(parametros);
        SessionUtil.redireccionar(direccionador);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirImprimirMov() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("id", cuenta);
        reemplazar.put("mes", mesInicial);
        reemplazar.put("mesf", mesFinal);
        reemplazar.put("aniot", anio);
        Map<String, Object> parametros = new HashMap<>();
        Reporteador.resuelveConsulta("000552LisMovimientoPlan", 1,
                        reemplazar, parametros);

        parametros.put("PR_SALDOFINAL", saldoFinal);
        parametros.put("PR_PLANCONTABLE_ID", cuenta);
        parametros.put("PR_SALDOINICIAL", saldoInicial);
        parametros.put("PR_PLANCONTABLE_NOMBRE", nombre);
        parametros.put("PR_NOMBRE_MES_INI",
                        SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesInicial]);
        parametros.put("PR_NOMBRE_MES_FIN",
                        SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesFinal]);
        parametros.put("PR_PLANCONTABLE_ANO", anio);

        try {
            archivoDescarga = JsfUtil.exportarStreamed(
                            "000552LisMovimientoPlan", parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }
    
    public void oprimirGenerarExcel() {
        //<CODIGO_DESARROLLADO>
        archivoDescarga=null;
        
        try {

        	HashMap<String, Object> reemplazar = new HashMap<>();
        	reemplazar.put("compania", compania);
        	reemplazar.put("aniot", anio);
        	reemplazar.put("mes",mesInicial);
        	reemplazar.put("mesf",mesFinal);
        	reemplazar.put("id",cuenta);

        	String strSql = Reporteador.resuelveConsulta("800706MovimientosPorCuenta",
        			Integer.parseInt(SessionUtil.getModulo()), reemplazar);

        	archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql, ConectorPool.ESQUEMA_SYSMAN, 
        			ReportesBean.FORMATOS.EXCEL97); 

        } catch (OutOfMemoryError | JRException | IOException 
        		| SysmanException | SQLException | DRException e)
        {
        	logger.error(e.getMessage(), e);
        	JsfUtil.agregarMensajeError(e.getMessage());
        }
        
       //</CODIGO_DESARROLLADO>
   }

    public String[] buscarSaldoMeses(int mesIni, int mesFin) {
        String[] rta = new String[5];
        Map<String, Object> param = new TreeMap<>();
        try {
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            param.put(GeneralParameterEnum.CUENTA.getName(), cuenta);
            param.put(MovimientosCuentasControladorEnum.PARAM0.getValue(),
                            mesIni);
            param.put(MovimientosCuentasControladorEnum.PARAM1.getValue(),
                            mesFin);

            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MovimientosCuentasControladorUrlEnum.URL3194
                                                                            .getValue())
                                            .getUrl(), param));

            if (rs != null) {
                rta[0] = SysmanFunciones.nvl(rs.getCampos().get("NOMBRE"), " ")
                                .toString();
                rta[1] = SysmanFunciones
                                .nvl(rs.getCampos().get("SALDO_INICIAL"), " ")
                                .toString();
                rta[2] = SysmanFunciones
                                .nvl(rs.getCampos().get("SALDO_FINAL"), " ")
                                .toString();
            }

            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MovimientosCuentasControladorUrlEnum.URL3195
                                                                            .getValue())
                                            .getUrl(), param));

            if (rs != null) {
                rta[3] = SysmanFunciones
                                .nvl(rs.getCampos().get("TOTAL_DEBITO"), " ")
                                .toString();
                rta[4] = SysmanFunciones
                                .nvl(rs.getCampos().get("TOTAL_CREDITO"), " ")
                                .toString();
            }
        }
        catch (SystemException e) {
            Logger.getLogger(MovimientosCuentasControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return rta;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

}
