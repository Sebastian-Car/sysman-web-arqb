package com.sysman.almacen;

import com.sysman.almacen.enums.RelplacasanuladasControladorEnum;
import com.sysman.almacen.enums.RelplacasanuladasControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;

import java.io.FileNotFoundException;
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
 * @author ngomez
 * @version 1, 23/11/2015
 * 
 * @version 2, 08/05/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 */

@ManagedBean
@ViewScoped
public class RelplacasanuladasControlador extends BeanBaseModal
{
    private static final String CODELEMENTO = "CODIGOELEMENTO";
    private String compania;
    private String desde;
    private String hasta;
    private String desdeNombre;
    private String hastaNombre;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaCmbElementoDesde;
    private RegistroDataModelImpl listaCmbElementoHasta;

    /**
     * Creates a new instance of RelplacasanuladasControlador
     */
    public RelplacasanuladasControlador()
    {
        super();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.RELPLACASANULADAS_CONTROLADOR.getCodigo();
            compania = SessionUtil.getCompania();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(RelplacasanuladasControlador.class.getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar()
    {
        cargarListacmbElementoDesde();
        cargarListacmbElementoHasta();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // Viene desde forma
    }

    public void cargarListacmbElementoDesde()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(RelplacasanuladasControladorUrlEnum.URL2560.getValue());  
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(RelplacasanuladasControladorEnum.PARAM0.getValue(),"D,N");

        listaCmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param, true, CODELEMENTO);
    }

    public void cargarListacmbElementoHasta()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(RelplacasanuladasControladorUrlEnum.URL3223.getValue());      
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(RelplacasanuladasControladorEnum.PARAM0.getValue(),"D,N");
        param.put(RelplacasanuladasControladorEnum.PARAM1.getValue(),desde);

        listaCmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param, true, CODELEMENTO);
    }

    public void oprimircmdpantalla()
    {
        // <CODIGO_DESARROLLADO>
        genInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirComando21()
    {
        // <CODIGO_DESARROLLADO>
        genInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void genInforme(ReportesBean.FORMATOS formato)
    {
        archivoDescarga = null;
        String reporte="000393PlacaAnulada";
        try
        {

            HashMap <String, Object> reemplazar = new HashMap<>();
            reemplazar.put("desde", desde);
            reemplazar.put("hasta", hasta);
            // MANEJO DE PARAMETROS DE REEMPLAZO
            HashMap <String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            String strSql = Reporteador.resuelveConsulta(reporte, Integer.parseInt(SessionUtil.getModulo()), reemplazar);
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_FORMS_RELPLACASANULADAS_CMBELEMENTODESDE_COLUMN(1)", desdeNombre);
            parametros.put("PR_FORMS_RELPLACASANULADAS_CMBELEMENTOHASTA_COLUMN(1)", hastaNombre);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INFORME_NO_EXISTE")+" "+ex.getMessage()+" "+reporte);
            Logger.getLogger(RelplacasanuladasControlador.class.getName())
            .log(Level.SEVERE, null, ex);
        } catch (JRException | IOException ex)
        {
            JsfUtil.agregarMensajeError(idioma.getString("MSM_TRANS_INTERRUMPIDA") + ex.getMessage());
            Logger.getLogger(RelplacasanuladasControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SysmanException e) {       
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }        

    }

    public void seleccionarFilaCmbElementoDesde(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        desde = registroAux.getCampos().get(CODELEMENTO).toString();
        desdeNombre = registroAux.getCampos().get("NOMBRELARGO").toString();
        cargarListacmbElementoHasta();
    }

    public void seleccionarFilaCmbElementoHasta(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        hasta = registroAux.getCampos().get(CODELEMENTO).toString();
        hastaNombre = registroAux.getCampos().get("NOMBRELARGO").toString();
    }

    public String getDesde()
    {
        return desde;
    }

    public void setDesde(String desde)
    {
        this.desde = desde;
    }

    public String getHasta()
    {
        return hasta;
    }

    public void setHasta(String hasta)
    {
        this.hasta = hasta;
    }

    public String getDesdeNombre()
    {
        return desdeNombre;
    }

    public void setDesdeNombre(String desdeNombre)
    {
        this.desdeNombre = desdeNombre;
    }

    public String getHastaNombre()
    {
        return hastaNombre;
    }

    public void setHastaNombre(String hastaNombre)
    {
        this.hastaNombre = hastaNombre;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }



    public RegistroDataModelImpl getListaCmbElementoDesde() {
        return listaCmbElementoDesde;
    }

    public void setListaCmbElementoDesde(
        RegistroDataModelImpl listacmbElementoDesde) {
        this.listaCmbElementoDesde = listacmbElementoDesde;
    }

    public RegistroDataModelImpl getListaCmbElementoHasta() {
        return listaCmbElementoHasta;
    }

    public void setListaCmbElementoHasta(
        RegistroDataModelImpl listacmbElementoHasta) {
        this.listaCmbElementoHasta = listacmbElementoHasta;
    }



}
