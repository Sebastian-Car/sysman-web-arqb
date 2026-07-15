package com.sysman.almacen;

import com.sysman.almacen.enums.IcuentaalmacendeprecControladorEnum;
import com.sysman.almacen.enums.IcuentaalmacendeprecControladorUrlEnum;
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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author NGOMEZ
 * @version 1, 15/04/2016
 * @version 2, 28/04/2017 jrodriguezr Se refactoriza el codigo SQL de
 * las listas para utilizar dss. Tambien los llamados a funciones,
 * procedimientos y metodos de la clase Acciones a llamados a EJB.
 * @version 3, 12/06/2017 asana, se implementa enum en formulario a
 * demas se modifica conexi�n.
 */
@ManagedBean
@ViewScoped
public class IcuentaalmacendeprecControlador extends BeanBaseModal {
    private static final String CODIGOELEMENTOCONST = "CODIGOELEMENTO";
    private static final String BODEGASCONST = "bodegas";
    private String compania;
    private String desde;
    private String hasta;
    private String claseBodega;
    private String nombreDesde;
    private String nombreHasta;
    private String placaInicial;
    private String placaFinal;
    private String digitosAgrupacion;
    private String fechaInicial;
    private String fechaFinal;
    private Date fechaInicialAux;
    private Date fechaFinalAux;
    private String companiaSel;
    private String grupo;
    private StreamedContent archivoDescarga;
    private List<Registro> listaFiltradoPor;
    private RegistroDataModelImpl listacmbElementoDesde;
    private RegistroDataModelImpl listacmbElementoHasta;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
	private Object excelplano;

    /**
     * Creates a new instance of IcuentaalmacendeprecControlador
     */
    public IcuentaalmacendeprecControlador() {
        super();
        try {
            numFormulario = GeneralCodigoFormaEnum.ICUENTAALMACENDEPREC_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                companiaSel = (String) parametrosEntrada.get("companiaSel");
                fechaInicialAux = SysmanFunciones.convertirAFecha(
                                (String) parametrosEntrada.get("fechaInicial"));
                fechaFinalAux = SysmanFunciones.convertirAFecha(
                                (String) parametrosEntrada.get("fechaFinal"));
                grupo = (String) parametrosEntrada.get("grupo");
                
                excelplano = parametrosEntrada.get("excelplano");
            }
            compania = companiaSel;
        }
        catch (Exception ex) {
            Logger.getLogger(IcuentaalmacendeprecControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void init() {
        abrirFormulario();
        cargarListaFiltradoPor();
        cargarListacmbElementoDesde();
        cargarListacmbElementoHasta();
    }

    @Override
    public void abrirFormulario() {
        try {
            digitosAgrupacion = (String) SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "DIGITOS AGRUPACION INVENTARIO",
                                            SessionUtil.getModulo(), new Date(),
                                            true),
                                            "3");
        }
        catch (SystemException e) {
            Logger.getLogger(IcuentaalmacendeprecControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaFiltradoPor() {
        try {
            listaFiltradoPor = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            IcuentaalmacendeprecControladorUrlEnum.URL4758
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListacmbElementoDesde() {
        // 112028
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        IcuentaalmacendeprecControladorUrlEnum.URL5084
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(IcuentaalmacendeprecControladorEnum.DIGITOSAGRUPACION
                        .getValue(), digitosAgrupacion);
        listacmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGOELEMENTOCONST);
    }

    public void cargarListacmbElementoHasta() {
        // 112030
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        IcuentaalmacendeprecControladorUrlEnum.URL6072
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(IcuentaalmacendeprecControladorEnum.DIGITOSAGRUPACION
                        .getValue(), digitosAgrupacion);
        param.put(IcuentaalmacendeprecControladorEnum.DESDE.getValue(), desde);
        listacmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGOELEMENTOCONST);
    }

    public void oprimirPresentar() {
        genInforme(ReportesBean.FORMATOS.PDF);
    }
    
    public void excelPlano() {
    	
   try {
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("companiaSel", companiaSel);
        reemplazar.put("fechaInicial", SysmanFunciones
                        .convertirAFechaCadena(fechaInicialAux));
        reemplazar.put("fechaFinal", SysmanFunciones
                        .convertirAFechaCadena(fechaFinalAux));
        String fechaC = ejbSysmanUtil.consultarParametro(compania,
                        "FECHA DE CORTE PARA INICIO DEL ALMACEN",
                        SessionUtil.getModulo(),
                        new Date(), false);

        reemplazar.put("fechaCorte", fechaC);
        reemplazar.put("grupo", grupo);

        switch (claseBodega) {
        case "20":
            reemplazar.put(BODEGASCONST, "IN ('20')");
            break;
        case "30":
            reemplazar.put(BODEGASCONST,
                            " NOT IN ('20','40','50')");
            break;
        case "40":
            reemplazar.put(BODEGASCONST, "IN ('40')");
            break;
        case "50":
            reemplazar.put(BODEGASCONST, "IN ('50')");
            break;
        default:
            break;
        }

        reemplazar.put("manejaNiif", SysmanFunciones
                        .nvl(ejbSysmanUtil.consultarParametro(
                                        compania, "MANEJA NIIF EN ALMACEN",
                                        SessionUtil.getModulo(), new Date(),
                                        false), "NO"));

        Map<String, Object> parametros = new HashMap<>();
        // 102006
        Registro regAux = null;
        Map<String, Object> param = new TreeMap<>();
        param.put(IcuentaalmacendeprecControladorEnum.CLASEBODEGA
                        .getValue(),
                        claseBodega);
     
			regAux = RegistroConverter.toRegistro(
			                requestManager.get(
			                                UrlServiceUtil.getInstance()
			                                                .getUrlServiceByUrlByEnumID(
			                                                                IcuentaalmacendeprecControladorUrlEnum.URL4759
			                                                                                .getValue())
			                                                .getUrl(),
			                                param));

        
        String reporte = "000633ICuentaAlmDeprec";
        String strSql = Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar);
        
        
      
			archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql, ConectorPool.ESQUEMA_SYSMAN,
					FORMATOS.EXCEL,"000633ICuentaAlmDeprec");
			
			
		} catch (JRException | IOException | SQLException | DRException | SysmanException | SystemException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }

    public void oprimirExcel() {
  
    	if(excelplano.equals("true")) {		
    		excelPlano();			  		
    	}else
        genInforme(ReportesBean.FORMATOS.EXCEL97); 
    }

    public void seleccionarFilacmbElementoDesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        desde = SysmanFunciones
                        .nvl(registroAux.getCampos().get(CODIGOELEMENTOCONST),
                                        "")
                        .toString();
        nombreDesde = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
                        .toString();
        hasta = null;
        nombreHasta = null;
        cargarListacmbElementoHasta();
    }

    public void seleccionarFilacmbElementoHasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        hasta = SysmanFunciones
                        .nvl(registroAux.getCampos().get(CODIGOELEMENTOCONST),
                                        "")
                        .toString();
        nombreHasta = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
                        .toString();
    }

    private void genInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;

        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("companiaSel", companiaSel);
            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicialAux));
            reemplazar.put("fechaFinal", SysmanFunciones
                            .convertirAFechaCadena(fechaFinalAux));
            String fechaC = ejbSysmanUtil.consultarParametro(compania,
                            "FECHA DE CORTE PARA INICIO DEL ALMACEN",
                            SessionUtil.getModulo(),
                            new Date(), false);

            reemplazar.put("fechaCorte", fechaC);
            reemplazar.put("grupo", grupo);

            switch (claseBodega) {
            case "20":
                reemplazar.put(BODEGASCONST, "IN ('20')");
                break;
            case "30":
                reemplazar.put(BODEGASCONST,
                                " NOT IN ('20','40','50')");
                break;
            case "40":
                reemplazar.put(BODEGASCONST, "IN ('40')");
                break;
            case "50":
                reemplazar.put(BODEGASCONST, "IN ('50')");
                break;
            default:
                break;
            }

            reemplazar.put("manejaNiif", SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(
                                            compania, "MANEJA NIIF EN ALMACEN",
                                            SessionUtil.getModulo(), new Date(),
                                            false), "NO"));

            Map<String, Object> parametros = new HashMap<>();
            // 102006
            Registro regAux = null;
            Map<String, Object> param = new TreeMap<>();
            param.put(IcuentaalmacendeprecControladorEnum.CLASEBODEGA
                            .getValue(),
                            claseBodega);
            regAux = RegistroConverter.toRegistro(
                            requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            IcuentaalmacendeprecControladorUrlEnum.URL4759
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
            parametros.put("PR_FORMS_I_CUENTAALMACENDEPREC_FILTRADOPOR_COLUMN(0)",
                            regAux != null ? regAux.getCampos().get("NOMBRE")
                                : "");
            String reporte = "000633ICuentaAlmDeprec";
            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);
            parametros.put("PR_STRSQL", strSql);

            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException | SystemException
                        | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarModal() {
        // CODIGO DESARROLLADO
    }

    public String getDesde() {
        return desde;
    }

    public void setDesde(String desde) {
        this.desde = desde;
    }

    public String getHasta() {
        return hasta;
    }

    public void setHasta(String hasta) {
        this.hasta = hasta;
    }

    public String getClaseBodega() {
        return claseBodega;
    }

    public void setClaseBodega(String claseBodega) {
        this.claseBodega = claseBodega;
    }

    public String getNombreDesde() {
        return nombreDesde;
    }

    public void setNombreDesde(String nombreDesde) {
        this.nombreDesde = nombreDesde;
    }

    public String getNombreHasta() {
        return nombreHasta;
    }

    public void setNombreHasta(String nombreHasta) {
        this.nombreHasta = nombreHasta;
    }

    public String getPlacaInicial() {
        return placaInicial;
    }

    public void setPlacaInicial(String placaInicial) {
        this.placaInicial = placaInicial;
    }

    public String getPlacaFinal() {
        return placaFinal;
    }

    public void setPlacaFinal(String placaFinal) {
        this.placaFinal = placaFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaFiltradoPor() {
        return listaFiltradoPor;
    }

    public void setListaFiltradoPor(List<Registro> listaFiltradoPor) {
        this.listaFiltradoPor = listaFiltradoPor;
    }

    public RegistroDataModelImpl getListacmbElementoDesde() {
        return listacmbElementoDesde;
    }

    public void setListacmbElementoDesde(
        RegistroDataModelImpl listacmbElementoDesde) {
        this.listacmbElementoDesde = listacmbElementoDesde;
    }

    public RegistroDataModelImpl getListacmbElementoHasta() {
        return listacmbElementoHasta;
    }

    public void setListacmbElementoHasta(
        RegistroDataModelImpl listacmbElementoHasta) {
        this.listacmbElementoHasta = listacmbElementoHasta;
    }

    public String getDigitosAgrupacion() {
        return digitosAgrupacion;
    }

    public void setDigitosAgrupacion(String digitosAgrupacion) {
        this.digitosAgrupacion = digitosAgrupacion;
    }

    public String getCompania() {
        return compania;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(String fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public String getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(String fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public Date getFechaInicialAux() {
        return fechaInicialAux;
    }

    public void setFechaInicialAux(Date fechaInicialAux) {
        this.fechaInicialAux = fechaInicialAux;
    }

    public Date getFechaFinalAux() {
        return fechaFinalAux;
    }

    public void setFechaFinalAux(Date fechaFinalAux) {
        this.fechaFinalAux = fechaFinalAux;
    }

    public String getCompaniaSel() {
        return companiaSel;
    }

    public void setCompaniaSel(String companiaSel) {
        this.companiaSel = companiaSel;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public void setCompania(String compania) {
        this.compania = compania;
    }

}
