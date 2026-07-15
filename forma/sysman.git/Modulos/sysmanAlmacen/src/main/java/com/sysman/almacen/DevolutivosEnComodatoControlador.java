package com.sysman.almacen;

import com.sysman.almacen.enums.DevolutivosEnComodatoControladorEnum;
import com.sysman.almacen.enums.DevolutivosEnComodatoControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 27/01/2016
 * 
 * @version 2, 27/04/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno de los combos.
 * 
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped
public class DevolutivosEnComodatoControlador extends BeanBaseModal
{

    private final String compania;
    private final String codigoElemento;
    private String elementoDesde;
    private String elementoHasta;
    private String nombreElementoDesde;
    private String nombreElementoHAsta;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaCmbElementoDesde;
    private RegistroDataModelImpl listaCmbElementoHasta;
    private String titulo;

    /**
     * Creates a new instance of DevolutivosEnComodatoControlador
     */
    public DevolutivosEnComodatoControlador()
    {
        super();
        numFormulario = GeneralCodigoFormaEnum.DEVOLUTIVOS_EN_COMODATO_CONTROLADOR.getCodigo();
        compania = SessionUtil.getCompania();
        codigoElemento = "CODIGOELEMENTO";
        if("1004020117".equals(SessionUtil.getMenuActual())){
            titulo = "Listado de Componentes por Elemento";
        } else {
            titulo = "Listado de Elementos que se encuentran en Comodato";
        }
        try
        {
            validarPermisos();
        }
        catch (Exception ex)
        {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(DevolutivosEnComodatoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar()
    {
        cargarListacmbElementoDesde();
        cargarListacmbElementoHasta();
        abrirFormulario();
    }

    public void cargarListacmbElementoDesde()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(DevolutivosEnComodatoControladorUrlEnum.URL2539.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, codigoElemento);
    }

    public void cargarListacmbElementoHasta()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(DevolutivosEnComodatoControladorUrlEnum.URL3256.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(DevolutivosEnComodatoControladorEnum.PARAM0.getValue(), String.valueOf(elementoDesde));

        listaCmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, codigoElemento);
    }

    public void oprimirExcel()
    {
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.EXCEL97);
    }

    public void oprimirPresentar()
    {
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.PDF);
    }

    public void generaInforme(ReportesBean.FORMATOS formato)
    {
        
        String reporte;
        String subreporte;
        
        if (elementoDesde == null)
        {
            JsfUtil.agregarMensajeAlerta("Debe ingresar el elemento Inicial");
            return;
        }
        if (elementoHasta == null)
        {
            JsfUtil.agregarMensajeAlerta("Debe ingresar el elemento Final");
            return;
        }
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            // MANEJO DE PARAMETROS DE REEMPLAZO
            if("1004020117".equals(SessionUtil.getMenuActual())){
                reporte = "001894ELEMENTOSCOMPONENTES";
                subreporte = "";
            } else {
                reporte = "000479DevolutivosDadosEnComodatoCC";
                subreporte = "000477SubDevolutivosComodato";
            }
            reemplazar.put("s$compania$s", compania);
            reemplazar.put("elementodesde", elementoDesde);
            reemplazar.put("elementoHasta", elementoHasta);
            
            // MANEJO DE PARAMETROS DEL REPORTE
            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);
            String strSqlSub = "";
            if (subreporte.contains("0")) {
            	strSqlSub = Reporteador.resuelveConsulta(subreporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);
            }
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_STRSQL_RESUMEN", strSqlSub);
            parametros.put("PR_ELEMENTOINICIAL", elementoDesde);
            parametros.put("PR_ELEMENTOFINAL", elementoHasta);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException ex)
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA) + " "
                                + ex.getMessage());
            Logger.getLogger(DevolutivosEnComodatoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    public void seleccionarFilaCmbElementoDesde(SelectEvent event)
    
    {          
        Registro registroAux = (Registro) event.getObject();
        elementoDesde = registroAux.getCampos().get(codigoElemento).toString();
        nombreElementoDesde = registroAux.getCampos()
                        .get("NOMBRELARGO").toString();
        cargarListacmbElementoHasta();
    }

    public void seleccionarFilaCmbElementoHasta(SelectEvent event)
    {           
        Registro registroAux = (Registro) event.getObject();
        elementoHasta = registroAux.getCampos().get(codigoElemento).toString();
        nombreElementoHAsta = registroAux.getCampos()
                        .get("NOMBRELARGO").toString();
    }

    public String getElementoDesde()
    {
        return elementoDesde;
    }

    public void setElementoDesde(String elementoDesde)
    {
        this.elementoDesde = elementoDesde;
    }

    public String getElementoHasta()
    {
        return elementoHasta;
    }

    public void setElementoHasta(String elementoHasta)
    {
        this.elementoHasta = elementoHasta;
    }

    public String getNombreElementoDesde()
    {
        return nombreElementoDesde;
    }

    public void setNombreElementoDesde(String nombreElementoDesde)
    {
        this.nombreElementoDesde = nombreElementoDesde;
    }

    public String getNombreElementoHAsta()
    {
        return nombreElementoHAsta;
    }

    public void setNombreElementoHAsta(String nombreElementoHAsta)
    {
        this.nombreElementoHAsta = nombreElementoHAsta;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    

    public RegistroDataModelImpl getListaCmbElementoDesde() {
        return listaCmbElementoDesde;
    }

    public void setListaCmbElementoDesde(
        RegistroDataModelImpl listaCmbElementoDesde) {
        this.listaCmbElementoDesde = listaCmbElementoDesde;
    }

    public RegistroDataModelImpl getListaCmbElementoHasta() {
        return listaCmbElementoHasta;
    }

    public void setListaCmbElementoHasta(
        RegistroDataModelImpl listaCmbElementoHasta) {
        this.listaCmbElementoHasta = listaCmbElementoHasta;
    }

    @Override
    public void abrirFormulario()
    {
        // NO SE IMPLEMENTA
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    

}
