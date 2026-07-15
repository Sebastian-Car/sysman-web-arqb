package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.LibrodebancosControladorEnum;
import com.sysman.contabilidad.enums.LibrodebancosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
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

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 14/04/2016
 * @modified jsforero
 * @version 2. 11/04/2017 Se realizo el refactory. Ademas se hicieron las respectivas Correcciones del sonar.
 * @version 3, 20/04/2017 jrodriguezr Se refactoriza el codigo ajustando los llamdos a funciones, procedimiento y metodos de la clase Acciones a llamados a EJB.
 * @modified jsforero
 * @version 4. 27/04/2017 Se valida que se esten incluyendo las cuentas con movimiento cuando se elige el check
 *
 * @version 5.0, 23/05/2017, pespitia:<br>
 * Se depuro el proceso de generar reportes.
 *
 * -- Modificado por lcortes 12/06/2017. Se reemplaza el valor del atributo numFormulario por el enumerado correspondiente y se suprimen las conexiones a la base de datos.
 */
@ManagedBean
@ViewScoped
public class LibrodebancosControlador extends BeanBaseModal
{
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo con el que el usuario esta interactuando.
     */
    private final String modulo;

    private final String codigoConst;
    private boolean movimiento;
    private boolean resumido;
    private String tipoInicial;
    private String tipoFinal;
    private String cuentaInicial;
    private String cuentaFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private String referenciaIni;

    private String referenciaFin;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaTipoInicial;
    private RegistroDataModelImpl listaTipoFinal;
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    private RegistroDataModelImpl listaReferenciaIni;
    private RegistroDataModelImpl listaReferenciaFin;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of LibrodebancosControlador
     */
    public LibrodebancosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        referenciaIni = SysmanConstantes.DEFECTOINICIAL_STRING;
        referenciaFin = SysmanConstantes.DEFECTOFINAL_STRING;

        codigoConst = "CODIGO";
        try
        {
            numFormulario = GeneralCodigoFormaEnum.LIBRODEBANCOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(LibrodebancosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init()
    {
        cargarListaTipoInicial();
        abrirFormulario();
        cargarListaCuentaInicial();
        cargarListaReferenciaIni();

    }

    @Override
    public void abrirFormulario()

    {

        fechaInicial = new Date();
        fechaFinal = new Date();

        // <CODIGO_DESARROLLADO>
        /*
         * FR630-AL_ABRIR Private Sub Form_Open(Cancel As Integer) formularioAbrir 1, Me.Name DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaTipoInicial()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LibrodebancosControladorUrlEnum.URL3361
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);

        listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoConst);
    }

    public void cargarListaTipoFinal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LibrodebancosControladorUrlEnum.URL4055
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(LibrodebancosControladorEnum.TIPOINICIAL.getValue(),
                        String.valueOf(tipoInicial));

        listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoConst);
    }

    public void cargarListaCuentaInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LibrodebancosControladorUrlEnum.URL4888
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(LibrodebancosControladorEnum.CLASECUENTA.getValue(), "B");
        param.put(GeneralParameterEnum.ANO.name(),
                        SysmanFunciones.ano(fechaInicial));

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoConst);
    }

    public void cargarListaCuentaFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LibrodebancosControladorUrlEnum.URL6232
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(LibrodebancosControladorEnum.CLASECUENTA.getValue(), "B");

        param.put(GeneralParameterEnum.ANO.name(),
                        SysmanFunciones.ano(fechaInicial));

        param.put(LibrodebancosControladorEnum.CUENTAINICIAL.getValue(),
                        cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoConst);
    }

    public void cargarListaReferenciaIni()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LibrodebancosControladorUrlEnum.URL6234
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);

        param.put(GeneralParameterEnum.ANO.name(),
                        SysmanFunciones.ano(fechaInicial));

        listaReferenciaIni = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoConst);

    }

    public void cargarListaReferenciaFin()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LibrodebancosControladorUrlEnum.URL6235
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);

        param.put(GeneralParameterEnum.ANO.name(),
                        SysmanFunciones.ano(fechaFinal));

        param.put(LibrodebancosControladorEnum.CODIGOINICIAL.getValue(),
                        referenciaIni);

        listaReferenciaFin = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoConst);

    }

    public void oprimirPresentar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        generarInforme(FORMATOS.EXCEL97);

        // </CODIGO_DESARROLLADO>
    }
    
    public void oprimirCsv()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.CSV);
        // </CODIGO_DESARROLLADO>
    }   

    public void generarInforme(ReportesBean.FORMATOS formato)
    {

        try
        {
        	
            if (SysmanFunciones.ano(fechaInicial) != SysmanFunciones
                            .ano(fechaFinal))
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB44"));
                return;
            }

            /* Anio respecto a la fecha incial */
            int anio = SysmanFunciones.ano(fechaInicial);

            /*- Mes respecto a la fecha inicial*/
            int mesIni = SysmanFunciones.mes(fechaInicial);

            /* Mes anterior al inicial */
            int mesAnt = mesIni - 1;

            /* Condicion debito credito */
            String condCD = " OR PLAN_CONTABLE.DEBITO" + mesIni
                            + " + PLAN_CONTABLE.CREDITO" + mesIni + " NOT IN(0) ";

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("anio", anio);
            reemplazar.put("codIni", "'" + cuentaInicial + "'");
            reemplazar.put("codFin", "'" + cuentaFinal + "'");
            reemplazar.put("tipoIni", "'" + tipoInicial + "'");
            reemplazar.put("tipoFin", "'" + tipoFinal + "'");
            reemplazar.put("saldo", " PLAN_CONTABLE.SALDO" + mesAnt);
            reemplazar.put("condCD", condCD);
            reemplazar.put("referenciaIni", referenciaIni);
            reemplazar.put("referenciaFin", referenciaFin);

            reemplazar.put("fechaIni",
                            SysmanFunciones.formatearFecha(fechaInicial));

            reemplazar.put("fechaFin",
                            SysmanFunciones.formatearFecha(fechaFinal));

            // parametro para la Firma del Libro de Boncos
            String firma = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA DE LIBRO DE BANCOS", modulo, new Date(),
                            true);

            // parametro para el cargo de la firma del libro de Boncos
            String cargo = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO FIRMA DE LIBRO DE BANCOS", modulo,
                            new Date(), true);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FIRMADELIBRO", firma);
            parametros.put("PR_CARGOFIRMADELIBRO", cargo);

            parametros.put("PR_CUENTAS",
                            idioma.getString("TB_TB526").toUpperCase() + " "
                                            + cuentaInicial + " Y " + cuentaFinal);

            parametros.put("PR_FECHAS",
                            idioma.getString("TB_TB527").toUpperCase() + " "
                                            + SysmanFunciones.convertirAFechaCadena(
                                                            fechaInicial)
                                            + " Y "
                                            + SysmanFunciones.convertirAFechaCadena(
                                                            fechaFinal));

            
            
            if(formato.equals(FORMATOS.CSV)) { 
            	
            	
           	 String strSql = Reporteador.resuelveConsulta(movimiento ? "800114LibroBancosConMov"
                     : "000629LibroDeBancos",
 						Integer.parseInt(SessionUtil.getModulo()), reemplazar);
           	 
           	 archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql, ConectorPool.ESQUEMA_SYSMAN, 
           			 ReportesBean.FORMATOS.CSV); 
           	
           }else { 
           	
           	Reporteador.resuelveConsulta(movimiento ? "800114LibroBancosConMov"
                    : "000629LibroDeBancos",
                       Integer.parseInt(modulo), reemplazar, parametros);

           	archivoDescarga = JsfUtil.exportarStreamed(reporteGe(), parametros,
                       ConectorPool.ESQUEMA_SYSMAN, formato);
           }
            

        }
        catch (OutOfMemoryError | JRException | IOException | SystemException
                        | ParseException | SysmanException | SQLException | DRException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    
    public String reporteGe()
    {
        String reporte;
        if (resumido)
        {
            reporte = "000632LibroDeBancosResumido";
        }
        else
        {
            reporte = "000629LibroDeBancos";
        }
        return reporte;

    }

    public void cambiarFechaInicial()
    {
        // <CODIGO_DESARROLLADO>
        cuentaInicial = null;
        cuentaFinal = null;
        referenciaIni = null;
        referenciaFin = null;

        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        cargarListaReferenciaIni();
        cargarListaReferenciaFin();
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaTipoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        tipoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoConst), "")
                        .toString();
        cargarListaTipoFinal();
    }

    public void seleccionarFilaTipoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        tipoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoConst), "")
                        .toString();
    }

    public void seleccionarFilaCuentaInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoConst), "")
                        .toString();
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoConst), "")
                        .toString();
    }

    public void seleccionarFilaReferenciaIni(SelectEvent event)
    {

        Registro registroAux = (Registro) event.getObject();
        referenciaIni = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoConst), "")
                        .toString();
        cargarListaReferenciaFin();

    }

    public void seleccionarFilaReferenciaFin(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        referenciaFin = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoConst), "")
                        .toString();

    }

    public boolean getMovimiento()
    {
        return movimiento;
    }

    public void setMovimiento(boolean movimiento)
    {
        this.movimiento = movimiento;
    }

    public boolean getResumido()
    {
        return resumido;
    }

    public void setResumido(boolean resumido)
    {
        this.resumido = resumido;
    }

    public String getTipoInicial()
    {
        return tipoInicial;
    }

    public void setTipoInicial(String tipoInicial)
    {
        this.tipoInicial = tipoInicial;
    }

    public String getTipoFinal()
    {
        return tipoFinal;
    }

    public void setTipoFinal(String tipoFinal)
    {
        this.tipoFinal = tipoFinal;
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

    public Date getFechaInicial()
    {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial)
    {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal()
    {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal)
    {
        this.fechaFinal = fechaFinal;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public RegistroDataModelImpl getListaTipoInicial()
    {
        return listaTipoInicial;
    }

    public void setListaTipoInicial(RegistroDataModelImpl listaTipoInicial)
    {
        this.listaTipoInicial = listaTipoInicial;
    }

    public RegistroDataModelImpl getListaTipoFinal()
    {
        return listaTipoFinal;
    }

    public void setListaTipoFinal(RegistroDataModelImpl listaTipoFinal)
    {
        this.listaTipoFinal = listaTipoFinal;
    }

    public RegistroDataModelImpl getListaCuentaInicial()
    {
        return listaCuentaInicial;
    }

    public void setListaCuentaInicial(
                    RegistroDataModelImpl listaCuentaInicial)
    {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    public RegistroDataModelImpl getListaCuentaFinal()
    {
        return listaCuentaFinal;
    }

    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal)
    {
        this.listaCuentaFinal = listaCuentaFinal;
    }

    public RegistroDataModelImpl getListaReferenciaIni()
    {
        return listaReferenciaIni;
    }

    public void setListaReferenciaIni(RegistroDataModelImpl listaReferenciaIni)
    {
        this.listaReferenciaIni = listaReferenciaIni;
    }

    public RegistroDataModelImpl getListaReferenciaFin()
    {
        return listaReferenciaFin;
    }

    public void setListaReferenciaFin(RegistroDataModelImpl listaReferenciaFin)
    {
        this.listaReferenciaFin = listaReferenciaFin;
    }

    public String getReferenciaIni()
    {
        return referenciaIni;
    }

    public void setReferenciaIni(String referenciaIni)
    {
        this.referenciaIni = referenciaIni;
    }

    public String getReferenciaFin()
    {
        return referenciaFin;
    }

    public void setReferenciaFin(String referenciaFin)
    {
        this.referenciaFin = referenciaFin;
    }

}
