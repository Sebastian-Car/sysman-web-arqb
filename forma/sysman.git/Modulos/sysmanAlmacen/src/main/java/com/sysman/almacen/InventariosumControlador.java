package com.sysman.almacen;

import com.sysman.almacen.enums.InventariosumControladorEnum;
import com.sysman.almacen.enums.InventariosumControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
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
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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

import net.sf.jasperreports.engine.JRException;

/** 
 * @author eorozco
 * @version Se hace copia del controlador de Suministros por fecha, y de  el Enum y UrlEnum,
 * no se migra debido a que es identico al de fecha solo se debe suprimir el campo fecha.
 *   
 * 
 */
@ManagedBean
@ViewScoped


public class InventariosumControlador extends BeanBaseModal {

    private final String compania;
    private final String cNombreLargo;
    private final String cInvCodElemento;
    private String elemntoDesde;
    private String elemntoHasta;
    private String dependenciaIncial;
    private String dependencialFinal;
    private String nombreElementoInicial;
    private String nombreElementoFinal;
    private RegistroDataModelImpl listacmbElementoDesde;
    private RegistroDataModelImpl listacmbElementoHasta;
    private RegistroDataModelImpl listaDepInicial;
    private RegistroDataModelImpl listaDepFinal;
    private boolean visibleDependencias;
    private Date fecha;
    private StreamedContent archivoDescarga;

    private String conSaldoCero;
    private String ordenadoPor;
    private String presentadoPor;

    @EJB
    private EjbSysmanUtilRemote ejbParametro;

    /**
     * Creates a new instance of InventariosumdevafechaControlador
     */
    public InventariosumControlador() {
        super();

        numFormulario = GeneralCodigoFormaEnum.INVENTARIOSUM_CONTROLADOR.getCodigo();
        compania = SessionUtil.getCompania();
        cNombreLargo = "INVENTARIO.NOMBRELARGO";
        cInvCodElemento = "INVENTARIO.CODIGOELEMENTO";

        try {
            fecha = new Date();
            conSaldoCero = "2";
            ordenadoPor = "1";
            presentadoPor = "1";

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(InventariosumControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {

        cargarListacmbElementoDesde();
        cargarListaDepInicial();
        abrirFormulario();
    }

    public void cargarListacmbElementoDesde() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		InventariosumControladorUrlEnum.URL3619
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listacmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());
    }

    public void cargarListacmbElementoHasta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		InventariosumControladorUrlEnum.URL4263
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(InventariosumControladorEnum.PARAM1.getValue(),
                        String.valueOf(elemntoDesde));

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listacmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());
    }

    public void cargarListaDepInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		InventariosumControladorUrlEnum.URL5117
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaDepInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaDepFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		InventariosumControladorUrlEnum.URL5671
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(InventariosumControladorEnum.PARAM2.getValue(),
                        dependenciaIncial);

        listaDepFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }
   

    public void oprimircmdPantalla() {
        getInforme(FORMATOS.PDF);
    }

    public void oprimirexcel() {
        getInforme(FORMATOS.EXCEL97);
    }

    public void getInforme(ReportesBean.FORMATOS formato) {
        String valorParametro = null;
        try {
            // <CODIGO_DESARROLLADO>

            valorParametro = ejbParametro.consultarParametro(compania,
                            "MANEJA PEPS EN CONSUMO ALMACEN",
                            SessionUtil.getModulo(), new Date(), false);
        }
        catch (SystemException ex) {
            Logger.getLogger(InventariosumdevafechaControlador.class.getName())
            .log(Level.SEVERE, null, ex);
        }
        boolean manejaPepsEnConsumoAlmacen = valorParametro == null ? false
            : "SI".equals(valorParametro);

        if (visibleDependencias) {
            // LINEAS COMENTADAS YA QUE EL LA CONSULTA
            // ULTIMOMOVDEVFECHA_PEPS NO EXISTE, Y ES UTILIZADA
            // POR EL
            // REPORTE InvDevAFechaPlaca_PEPS
            getReporteInvDevAFechaPlaca("000501InvDevAFechaUnificado",
                            formato);

        }
        else if ((elemntoHasta != null) && !elemntoHasta.isEmpty()) {
            if (manejaPepsEnConsumoAlmacen) {
                getReporteInvSuministro(
                                "000508InvSuministrosDevAFechaUnificado",
                                formato);
            }
            else {
                getReporteInvSuministro(
                                "002201InventarioSuministroPEPS",
                                formato);
            }
        }

    }

    public void getReporteInvDevAFechaPlaca(String reporte, FORMATOS formato) {
        try {
            String digitosAgrupacionInventario = ejbParametro
                            .consultarParametro(compania,
                                            "DIGITOS AGRUPACION INVENTARIO",
                                            SessionUtil.getModulo(), new Date(),
                                            false);
            digitosAgrupacionInventario = digitosAgrupacionInventario == null
                            ? "3" : digitosAgrupacionInventario;
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ORDEN2",
                            "2".equals(ordenadoPor)
                            ? cNombreLargo
                                : cInvCodElemento);
            reemplazar.put("ORDEN3",
                            "2".equals(ordenadoPor)
                            ? cInvCodElemento
                                : cNombreLargo);
            reemplazar.put("FECHA", SysmanFunciones.formatearFecha(fecha));
            reemplazar.put("ELEMENTODESDE", "'" + elemntoDesde + "'");
            reemplazar.put("ELEMENTOHASTA", "'" + elemntoHasta + "'");
            reemplazar.put("DEPENDENCIAINICIAL", dependenciaIncial);
            reemplazar.put("DEPENDENCIAFINAL", dependencialFinal);
            reemplazar.put("PR_DIGITOSAGRUPACIONINV",
                            digitosAgrupacionInventario);

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FECHATITULO", SysmanFunciones
                            .convertirAFechaCadena(fecha, "dd/MM/yyyy"));
            parametros.put("PR_PRESENTADOPOR",
                            Integer.parseInt(presentadoPor));

            // MANEJO DE PARAMETROS DEL REPORTE
            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);

            parametros.put("PR_STRSQL", strSql);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }




    public void getReporteInvSuministro(String reporte, FORMATOS formato) {
        try {
            String saldo1 = "";
            String saldo2 = "";
            String saldoInvSuministrosDevAFecha = "";
            String digitosAgrupacionInventario = ejbParametro
                            .consultarParametro(compania,
                                            "DIGITOS AGRUPACION INVENTARIO",
                                            SessionUtil.getModulo(), new Date(),
                                            false);
            digitosAgrupacionInventario = digitosAgrupacionInventario == null
                            ? "" : digitosAgrupacionInventario;

            if ("2".equals(conSaldoCero)) {
                saldo1 = " AND ULTIMOMOVIMIENTOC_P.CSALDO IS NOT NULL ";
                saldo2 = " AND ULTIMOMOVIMIENTOC_PEPS_D.CSALDO IS NOT NULL ";
                saldoInvSuministrosDevAFecha = "AND ULTIMOMOVIMIENTOC.CSALDO IS NOT NULL";
            }
            else {
                saldo1 = "  AND ULTIMOMOVIMIENTOC_P.CSALDO > 0 ";
                                //+ "AND INVENTARIO.EXISTENCIA > 0 "; Se quita validación dado que el valor de inventario es a la fecha y no tiene en cuenta la fecha de filtro
                saldo2 = "  AND ULTIMOMOVIMIENTOC_PEPS_D.CSALDO > 0  "
                                + " AND (CASE WHEN (SYSDATE >=  ULTIMOMOVIMIENTOC_PEPS_D.ULTIMODEFECHASALIDA OR ULTIMOMOVIMIENTOC_PEPS_D.ULTIMODEFECHASALIDA IS NOT NULL) THEN ULTIMOMOVIMIENTOC_PEPS_D.SALDO_PEPS ELSE ULTIMOMOVIMIENTOC_PEPS_D.CSALDO END) > 0 ";
                saldoInvSuministrosDevAFecha = " AND ULTIMOMOVIMIENTOC.CSALDO > 0 " ;
            }
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ORDEN2",
                            "2".equals(ordenadoPor)
                            ? cNombreLargo
                                : cInvCodElemento);
            reemplazar.put("ORDEN3",
                            "2".equals(ordenadoPor)
                            ? cInvCodElemento
                                : cNombreLargo);
            reemplazar.put("FECHA", SysmanFunciones.formatearFecha(fecha));
            reemplazar.put("ELEMENTODESDE", "'" + elemntoDesde + "'");
            reemplazar.put("ELEMENTOHASTA", "'" + elemntoHasta + "'");
            reemplazar.put("SALDO1", saldo1);
            reemplazar.put("SALDO2", saldo2);
            reemplazar.put("SALDOINVSUMAXISTROSDEVAFECHA",
                            saldoInvSuministrosDevAFecha);
            reemplazar.put("PR_DIGITOSAGRUPACIONINV",
                            digitosAgrupacionInventario);

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FECHATITULO", SysmanFunciones
                            .convertirAFechaCadena(fecha, "dd/MM/yyyy"));
            parametros.put("PR_PRESENTADOPOR",
                            Integer.parseInt(presentadoPor));

            // MANEJO DE PARAMETROS DEL REPORTE
            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);

            parametros.put("PR_STRSQL", strSql);

            
                archivoDescarga = JsfUtil.exportarStreamed(
                                "002201InventarioSuministroPEPS",
                                parametros,
                                ConectorPool.ESQUEMA_SYSMAN, 
                                formato);
            }
            catch (JRException | IOException | SysmanException | ParseException | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
     
        
    }
 

    public void seleccionarFilacmbElementoDesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elemntoDesde = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGOELEMENTO
                                                        .getName()),
                                        "")
                        .toString();

        nombreElementoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
                        .toString();
        elemntoHasta = null;
        nombreElementoFinal = null;
        cargarListacmbElementoHasta();
    }

    public void seleccionarFilacmbElementoHasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elemntoHasta = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGOELEMENTO
                                                        .getName()),
                                        "")
                        .toString();

        nombreElementoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
                        .toString();
    }



 
    public boolean isVisibleDependencias() {
        return visibleDependencias;
    }

    public void setVisibleDependencias(boolean visibleDependencias) {
        this.visibleDependencias = visibleDependencias;
    }

    public String getElemntoDesde() {
        return elemntoDesde;
    }

    public void setElemntoDesde(String elemntoDesde) {
        this.elemntoDesde = elemntoDesde;
    }

    public String getElemntoHasta() {
        return elemntoHasta;
    }

    public void setElemntoHasta(String elemntoHasta) {
        this.elemntoHasta = elemntoHasta;
    }

    public String getDependenciaIncial() {
        return dependenciaIncial;
    }

    public void setDependenciaIncial(String dependenciaIncial) {
        this.dependenciaIncial = dependenciaIncial;
    }

    public String getDependencialFinal() {
        return dependencialFinal;
    }

    public void setDependencialFinal(String dependencialFinal) {
        this.dependencialFinal = dependencialFinal;
    }

    public String getNombreElementoInicial() {
        return nombreElementoInicial;
    }

    public void setNombreElementoInicial(String nombreElementoInicial) {
        this.nombreElementoInicial = nombreElementoInicial;
    }

    public String getNombreElementoFinal() {
        return nombreElementoFinal;
    }

    public void setNombreElementoFinal(String nombreElementoFinal) {
        this.nombreElementoFinal = nombreElementoFinal;
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

    public RegistroDataModelImpl getListaDepInicial() {
        return listaDepInicial;
    }

    public void setListaDepInicial(RegistroDataModelImpl listaDepInicial) {
        this.listaDepInicial = listaDepInicial;
    }

    public RegistroDataModelImpl getListaDepFinal() {
        return listaDepFinal;
    }

    public void setListaDepFinal(RegistroDataModelImpl listaDepFinal) {
        this.listaDepFinal = listaDepFinal;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public String getConSaldoCero() {
        return conSaldoCero;
    }

    public void setConSaldoCero(String conSaldoCero) {
        this.conSaldoCero = conSaldoCero;
    }

    public String getOrdenadoPor() {
        return ordenadoPor;
    }

    public void setOrdenadoPor(String ordenadoPor) {
        this.ordenadoPor = ordenadoPor;
    }

    public String getPresentadoPor() {
        return presentadoPor;
    }

    public void setPresentadoPor(String presentadoPor) {
        this.presentadoPor = presentadoPor;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

}
