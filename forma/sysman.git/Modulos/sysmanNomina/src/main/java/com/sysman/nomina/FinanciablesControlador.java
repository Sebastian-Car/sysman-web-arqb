package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCeroRemote;
import com.sysman.nomina.ejb.EjbNominaSeisRemote;
import com.sysman.nomina.ejb.EjbNominaTresRemote;
import com.sysman.nomina.enums.FinanciablesControladorEnum;
import com.sysman.nomina.enums.FinanciablesControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author esarmiento
 * @version 1, 24/07/2015
 * @modified jguerrero
 * @version 2. 26/09/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar.
 */
@ManagedBean
@ViewScoped

public class FinanciablesControlador extends BeanBaseDatosAcmeImpl {

    /**
     * variable que almacena la compaï¿½ia
     */
    private final String compania;
    /**
     * variabl que almacena el modulo
     */
    private final String modulo;
    /**
     * variable que lista los empleados
     */
    private RegistroDataModelImpl listaIDdeEmpleado;
    /**
     * variable que lista los conceptos
     */
    private RegistroDataModelImpl listaIDdeConcepto;

    /**
     * variable que almacena ael texto completo
     */
    private String texto;
    /**
     * variable que almacena el cupo total
     */
    private String cupoLibreCal;
    /**
     * variable que almacena el cupo asignado
     */
    private String cupoAsignado;
    /**
     * variable que almacena el total de deudas calculadas
     */
    private String totalDeudasCal;
    /**
     * variable que almacena el sueldo calculado
     */
    private String sueldoCal;
    /**
     * variable que almacena el nombre del concepto seleccionado
     */

    private String nombreConcepto;
    /**
     * variable que almacena el proceso
     */
    private final String proceso = (String) SessionUtil
                    .getSessionVar("procesoNomina");
    /**
     * variable que almacena el aï¿½o
     */
    private final String anio = (String) SessionUtil
                    .getSessionVar("anioNomina");
    /**
     * variable que almacena el mes
     */
    private final String mes = (String) SessionUtil.getSessionVar("mesNomina");
    private final String numeroDocCons;
    /**
     * variable que alamcena el periodo
     */
    private final String periodo = (String) SessionUtil
                    .getSessionVar("periodoNomina");
    /**
     * variable que alamcena el titulo
     */
    private String titulo;
    /**
     * variable que guarda el archivo de descarga o reporte
     */
    private StreamedContent archivoDescarga;
    /**
     * variable estaticas
     */
    private static final String VALOR_CUOTA = "VALOR_CUOTA";
    private static final String SALDO = "SALDO";
    private static final String VALDESCUENTO = "VALDESCUENTO";
    private static final String MONTO_INICIAL = "MONTO_INICIAL";
    private static final String NUMERO_DE_CUOTAS = "NUMERO_DE_CUOTAS";
    private static final String ID_DE_EMPLEADO = GeneralParameterEnum.ID_DE_EMPLEADO
                    .getName();
    private static final String ID_DE_CONCEPTO = GeneralParameterEnum.ID_DE_CONCEPTO
                    .getName();
    private static final String MSM_TRANS_INTERRUMPIDA = "MSM_TRANS_INTERRUMPIDA";
    private static final String NOMBRE_EMPLEADO = "NOMBREEMPLEADO";

    @EJB
    EjbNominaCeroRemote ejbNominaCero;

    @EJB
    EjbNominaSeisRemote ejbNominaSeis;

    @EJB
    EjbNominaTresRemote ejbNominaTres;
    @EJB
    EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of FactoresliqfinalControlador
     */
    public FinanciablesControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        numeroDocCons = GeneralParameterEnum.NUMERO_DCTO.getName();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FINANCIABLES_CONTROLADOR
                            .getCodigo();
            validarPermisos();

        }
        catch (Exception ex)
        {
            Logger.getLogger(FinanciablesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.FINANCIABLES_DE_NOMINA;
        buscarLlave();
        asignarOrigenDatos();

    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put("PROCESO", proceso);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
        parametrosListado.put(GeneralParameterEnum.MES.getName(), mes);
        parametrosListado.put(GeneralParameterEnum.PERIODO.getName(), periodo);
    }

    public String getCompania() {
        return compania;
    }

    public String getProceso() {
        return proceso;
    }

    public String getAnio() {
        return anio;
    }

    public String getMes() {
        return mes;
    }

    public String getPeriodo() {
        return periodo;
    }

    public RegistroDataModelImpl getListaIDdeEmpleado() {
        return listaIDdeEmpleado;
    }

    public void setListaIDdeEmpleado(RegistroDataModelImpl listaIDdeEmpleado) {
        this.listaIDdeEmpleado = listaIDdeEmpleado;
    }

    public RegistroDataModelImpl getListaIDdeConcepto() {
        return listaIDdeConcepto;
    }

    public void setListaIDdeConcepto(RegistroDataModelImpl listaIDdeConcepto) {
        this.listaIDdeConcepto = listaIDdeConcepto;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getCupoLibreCal() {
        return cupoLibreCal;
    }

    public void setCupoLibreCal(String cupoLibreCal) {
        this.cupoLibreCal = cupoLibreCal;
    }

    public String getCupoAsignado() {
        return cupoAsignado;
    }

    public void setCupoAsignado(String cupoAsignado) {
        this.cupoAsignado = cupoAsignado;
    }

    public String getTotalDeudasCal() {
        return totalDeudasCal;
    }

    public void setTotalDeudasCal(String totalDeudasCal) {
        this.totalDeudasCal = totalDeudasCal;
    }

    public String getSueldoCal() {
        return sueldoCal;
    }

    public void setSueldoCal(String sueldoCal) {
        this.sueldoCal = sueldoCal;
    }

    public String getNombreConcepto() {
        return nombreConcepto;
    }

    public void setNombreConcepto(String nombreConcepto) {
        this.nombreConcepto = nombreConcepto;
    }

    public void cargarListaIDdeEmpleado() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FinanciablesControladorUrlEnum.URL7525
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaIDdeEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, ID_DE_EMPLEADO);

        // 210029 ESTADO
    }

    public void cargarListaIDdeConcepto() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FinanciablesControladorUrlEnum.URL8301
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FinanciablesControladorEnum.CONCEPTOINI.getValue(), "600");
        param.put(FinanciablesControladorEnum.CONCEPTOFIN.getValue(), "799");
        param.put(FinanciablesControladorEnum.CONCEPTOADICIONALINI.getValue(),
                        "1600");
        param.put(FinanciablesControladorEnum.CONCEPTOADICIONALFIN.getValue(),
                        "1698");
        listaIDdeConcepto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, ID_DE_CONCEPTO);

        // 151019 CONCEPTOINI CONCEPTOFIN
    }

    /**
     * Metodo ejecutado al cambiar el control ValorCuota
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     */
    public void cambiarValorCuota() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("VALDESCUENTO",
                        registro.getCampos().get("VALOR_CUOTA"));
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPreparar() {
        // <CODIGO_DESARROLLADO>
        SessionUtil.cargarModal(String.valueOf(
                        GeneralCodigoFormaEnum.PREPARAR_FINANCIABLES_CONTROLADOR
                                        .getCodigo()),
                        modulo);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirimprimirExcel() {
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL97);
    }

    public void oprimirBorrarDiferidos() {

        try
        {
            boolean estado;
            estado = ejbNominaCero.validarPeriodoActivoNomina(compania,
                            Integer.parseInt(proceso), Integer.parseInt(anio),
                            Integer.parseInt(mes), Integer.parseInt(periodo));

            if (estado)
            {
                int eliminado = ejbNominaSeis.borrarFinaciables(compania,
                                Integer.parseInt(proceso),
                                Integer.parseInt(anio), Integer.parseInt(mes),
                                Integer.parseInt(periodo),
                                SessionUtil.getUser().getCodigo());

                if (eliminado > 0)
                {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB2568"));
                }
                else if (eliminado == 0)
                {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB2569"));
                }
                else
                {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString(MSM_TRANS_INTERRUMPIDA));
                }
            }
            else
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2570"));
            }
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void generarReporte(FORMATOS formato) {
        try
        {
            // <CODIGO_DESARROLLADO>
            Map<String, Object> parametros = new HashMap<>();
            Map<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("compania", compania);
            reemplazar.put("procesoNomina", proceso);
            reemplazar.put("anioNomina", anio);
            reemplazar.put("mesNomina", mes);
            reemplazar.put("periodoNomina", periodo);

            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            Reporteador.resuelveConsulta("000047Diferidosquincenales",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000047Diferidosquincenales", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>

    }

    public void oprimirimprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaIDdeEmpleado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(NOMBRE_EMPLEADO,
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
        registro.getCampos().put(numeroDocCons,
                        registroAux.getCampos().get(numeroDocCons));

        registro.getCampos().put(ID_DE_EMPLEADO,
                        registroAux.getCampos().get(ID_DE_EMPLEADO));
    }

    public void seleccionarFilaIDdeConcepto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        nombreConcepto = (String) registroAux.getCampos()
                        .get("NOMBRE_CONCEPTO");
        registro.getCampos().put(ID_DE_CONCEPTO,
                        registroAux.getCampos().get(ID_DE_CONCEPTO));
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        cargarTitulo();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        try
        {
            if (!"i".equals(accion))
            {

                sueldoCal = String.valueOf(ejbNominaTres.miSueldo(compania,
                                Integer.parseInt(registro.getCampos()
                                                .get(ID_DE_EMPLEADO)
                                                .toString()),
                                Integer.parseInt(anio)));
                Date fechaAux = ejbNominaCero.getFechaPeriodoIniFin(compania,
                                Integer.parseInt(proceso),
                                Integer.parseInt(anio), Integer.parseInt(mes),
                                Integer.parseInt(periodo), false, false);

                totalDeudasCal = String.valueOf(ejbNominaSeis.getCupoDeuda(
                                compania,
                                Integer.parseInt(registro.getCampos()
                                                .get(ID_DE_EMPLEADO)
                                                .toString()),
                                fechaAux));

                cupoAsignado = String
                                .valueOf(ejbNominaSeis.getCupoAsigando(compania,
                                                BigDecimal.valueOf(Double
                                                                .parseDouble(sueldoCal))));

                cupoLibreCal = String.valueOf(SysmanFunciones
                                .redondear(Double.parseDouble(cupoAsignado)
                                    - Double.parseDouble(totalDeudasCal), 0));

                if (registro.getCampos().get(ID_DE_CONCEPTO) == null)
                {
                    nombreConcepto = "";
                }
                else
                {

                    nombreConcepto = ejbNominaTres.nombreConcepto(compania,
                                    Integer.parseInt(registro.getCampos()
                                                    .get(ID_DE_CONCEPTO)
                                                    .toString()));

                }

            }
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarNumerodeCuotas() {
        int numCuotas;
        Long montoInicial;
        if (((!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        MONTO_INICIAL))
            && !"0".equals(registro.getCampos().get(MONTO_INICIAL).toString()))
            && ((!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            NUMERO_DE_CUOTAS))
                && !"0".equals(registro.getCampos().get(NUMERO_DE_CUOTAS)
                                .toString())))
        {
            numCuotas = Integer.parseInt(registro.getCampos()
                            .get(NUMERO_DE_CUOTAS).toString());
            montoInicial = Long.parseLong(registro.getCampos()
                            .get(MONTO_INICIAL).toString());
            JsfUtil.agregarMensajeAlerta(registro.getCampos()
                            .get(MONTO_INICIAL).toString());
            registro.getCampos().put(VALOR_CUOTA,
                            montoInicial / numCuotas);
            registro.getCampos().put(SALDO,
                            (montoInicial / numCuotas) * numCuotas);
            registro.getCampos().put(VALDESCUENTO,
                            montoInicial / numCuotas);

        }

    }

    public void cambiarMontoInicial() {
        int numCuotas;
        Long montoInicial;

        if (((!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        MONTO_INICIAL))
            && !"0".equals(registro.getCampos().get(MONTO_INICIAL).toString()))
            && ((!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            NUMERO_DE_CUOTAS))
                && !"0".equals(registro.getCampos().get(NUMERO_DE_CUOTAS)
                                .toString())))
        {
            numCuotas = Integer.parseInt(registro.getCampos()
                            .get(NUMERO_DE_CUOTAS).toString());
            montoInicial = Long.parseLong(registro.getCampos()
                            .get(MONTO_INICIAL).toString());
            registro.getCampos().put(VALOR_CUOTA,
                            montoInicial / numCuotas);
            registro.getCampos().put(SALDO, montoInicial);
            registro.getCampos().put(VALDESCUENTO,
                            montoInicial / numCuotas);
        }
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    @Override
    public void iniciarListasSubNulo() {
        nombreConcepto = "";

        sueldoCal = "";
        totalDeudasCal = "";
        cupoAsignado = "";
        cupoLibreCal = "";
        registro = new Registro(new HashMap<String, Object>());
        registro.getCampos().put(MONTO_INICIAL, "0");
        registro.getCampos().put(NUMERO_DE_CUOTAS, "1");
        registro.getCampos().put(VALOR_CUOTA, "0");
        registro.getCampos().put(SALDO, "0");
        registro.getCampos().put(VALDESCUENTO, "0");

    }

    @Override
    public void iniciarListasSub() {
        // heredado del bean base
    }

    @Override
    public void iniciarListas() {

        nombreConcepto = "";
        cargarListaIDdeEmpleado();
        cargarListaIDdeConcepto();
    }

    @Override
    public boolean insertarAntes() {

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anio);
        registro.getCampos().put(GeneralParameterEnum.MES.getName(), mes);
        registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(),
                        periodo);
        registro.getCampos().put(GeneralParameterEnum.ID_DE_PROCESO.getName(),
                        proceso);
        registro.getCampos().remove(numeroDocCons);
        registro.getCampos().remove(NOMBRE_EMPLEADO);

        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        registro.getCampos().remove(numeroDocCons);
        registro.getCampos().remove(NOMBRE_EMPLEADO);
        Date fechaInicial = (Date) registro.getCampos().get("FECHA_LIBRANZA");
        Date fechaFin = (Date) registro.getCampos().get("FECHAFIN");

        if ((fechaInicial == null) && (fechaFin != null))
        {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB168"));
        }
        if ((fechaFin != null) && fechaFin.before(fechaInicial))
        {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB574"));
        }

        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
    		
     	 try {
     		HashMap param = new HashMap();

    			param.put("KEY_COMPANIA", registro.getCampos().get("COMPANIA"));
    			param.put("KEY_ID_DE_PROCESO", registro.getCampos().get("ID_DE_PROCESO"));
    			param.put("KEY_ANO", registro.getCampos().get("ANO"));
    			param.put("KEY_MES", registro.getCampos().get("MES"));
    			param.put("KEY_PERIODO", registro.getCampos().get("PERIODO"));
    			param.put("KEY_ID_DE_EMPLEADO", registro.getCampos().get("ID_DE_EMPLEADO"));
    			param.put("KEY_ID_DE_CONCEPTO", registro.getCampos().get("ID_DE_CONCEPTO"));
    			       
    	        
             UrlBean urlDelete = UrlServiceUtil.getInstance()
                             .getUrlServiceByUrlByEnumID(
                                             GenericUrlEnum.NOVEDADES
                                                             .getDeleteKey());
             
             requestManager.delete(urlDelete.getUrl(), param);            
         }
         catch (SystemException ex) {
             logger.error(ex.getMessage(), ex);
             JsfUtil.agregarMensajeError(ex.getMessage());
         }
    	 return true;
     }
    	
    
    

    @Override
    public boolean eliminarDespues() {
    	
        return true;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    private void cargarTitulo() {
        try
        {
            titulo = ejbSysmanUtil.mostrarNombreDeMes(Integer.parseInt(mes));
            String tituloAux = idioma.getString("TB_TB3806");
            titulo = tituloAux
                            .replace("s$nombreMes$s",
                                            ejbSysmanUtil.mostrarNombreDeMes(
                                                            Integer.parseInt(
                                                                            mes)))
                            .replace("s$ano$s", anio)
                            .replace("s$periodo$s", periodo);

        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

}
