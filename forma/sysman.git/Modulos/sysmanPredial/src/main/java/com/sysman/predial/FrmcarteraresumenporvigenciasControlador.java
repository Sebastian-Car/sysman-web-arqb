package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.FrmcarteraresumenporvigenciasControladorEnum;
import com.sysman.predial.enums.FrmcarteraresumenporvigenciasControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
 * @author acaceres
 * @version 1, 31/05/2016
 * @author jcrodriguez -Depuracion del controlador,Refactoring y
 * eliminacion de logica innecesaria por los campos eliminados,tambien
 * se adicionaron nuevos filtros para la consulta general de acuerdo
 * al formulario ruta PREDIAL\Informes\De cartera\Deudas\Cartera
 * Resumida por Vigencias en access
 * @version 2, 29/06/2017
 */
@ManagedBean
@ViewScoped

public class FrmcarteraresumenporvigenciasControlador extends BeanBaseModal
{
    private final String compania;
    private final String modulo;
    private String predioIncluye;
    private String tano;
    private String codigoInicial;
    private String codigoFinal;
    private Boolean indCantPredios;
    private StreamedContent archivoDescarga;
    private List<Registro> listaTAno;
    private RegistroDataModelImpl listaTCodigoInicial;
    private RegistroDataModelImpl listaTCodigoFinal;

    /**
     * Creates a new instance of
     * FrmcarteraresumenporvigenciasControlador
     */
    public FrmcarteraresumenporvigenciasControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMCARTERARESUMENPORVIGENCIAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmcarteraresumenporvigenciasControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {

        cargarListaTAno();
        cargarListaTCodigoInicial();
        cargarListaTCodigoFinal();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        tano = String.valueOf(SysmanFunciones
                        .ano(new Date()));
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaTAno()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaTAno = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(FrmcarteraresumenporvigenciasControladorUrlEnum.URL3773.getValue()).getUrl(),
                            param));
        }
        catch (SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaTCodigoInicial()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(FrmcarteraresumenporvigenciasControladorUrlEnum.URL4314.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listaTCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaTCodigoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(FrmcarteraresumenporvigenciasControladorUrlEnum.URL5307.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(FrmcarteraresumenporvigenciasControladorEnum.CODIGO_INICIAL.getValue(), codigoInicial);

        listaTCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPantalla()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * segun lo que seleccione en el combo sencillo se le adiciona a
     * la consulta del reporte un filtro
     * 
     * @return
     */
    private String seleccionarIncluyePredios()
    {
        String strIncluye;
        switch (predioIncluye)
        {
        case "Activos":
            strIncluye = " AND NVL(IP_USUARIOS_PREDIAL.INDBORRADO,0) IN(0) AND NVL(IP_USUARIOS_PREDIAL.CODIGO_NO_ACTIVO,0) IN(0) ";

            break;
        case "Bloqueados":
            strIncluye = " AND NVL(IP_USUARIOS_PREDIAL.INDBORRADO,0) IN(0) AND NVL(IP_USUARIOS_PREDIAL.CODIGO_NO_ACTIVO,0) IN(0) AND NVL(IP_USUARIOS_PREDIAL.BLOQUEADO,0) NOT IN(0) ";

            break;
        default:
            strIncluye = " AND NVL(IP_USUARIOS_PREDIAL.INDBORRADO,0) IN(0) AND NVL(IP_USUARIOS_PREDIAL.CODIGO_NO_ACTIVO,0) IN(0) AND NVL(IP_USUARIOS_PREDIAL.BLOQUEADO,0) IN(0)";
            break;
        }

        return strIncluye;
    }

    /***
     * cuando el check cambia de estado true=> se le adiciona una
     * funcion de grupo contar codigos (para que cuente los predios)
     * false=>solamente se le adiciona el campo
     * 
     * @return
     */
    private String seleccionarCantidadPredio()
    {
        if (!indCantPredios)
        {
            return " 0 AS CANTPREDIOS,";
        }
        else
        {
            return " COUNT(IP_USUARIOS_PREDIAL.CODIGO) AS CANTPREDIOS, ";
        }
    }

    public void obtenerReporte(FORMATOS formatos)
    {

        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("numeroOrden",
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL);
            reemplazar.put("strIncluye", seleccionarIncluyePredios());
            reemplazar.put("cantidadPredios", seleccionarCantidadPredio());
            reemplazar.put("ano", tano);
            HashMap<String, Object> parametros = new HashMap<>();

            if (!indCantPredios)
            {
                parametros.put(FrmcarteraresumenporvigenciasControladorEnum.PR_VISIBLE.getValue(), "SI");

            }
            else
            {
                parametros.put(FrmcarteraresumenporvigenciasControladorEnum.PR_VISIBLE.getValue(), "NO");
            }
            parametros.put(FrmcarteraresumenporvigenciasControladorEnum.PR_NOMBRECOMPANIA.getValue(),
                            SessionUtil.getCompaniaIngreso().getNombre());

            parametros.put(FrmcarteraresumenporvigenciasControladorEnum.PR_TCODIGOINICIAL.getValue(), codigoInicial);
            parametros.put(FrmcarteraresumenporvigenciasControladorEnum.PR_TCODIGOFINAL.getValue(), codigoFinal);
            parametros.put(FrmcarteraresumenporvigenciasControladorEnum.PR_NITCOMPANIA.getValue(),
                            SessionUtil.getCompaniaIngreso().getNit());

            Reporteador.resuelveConsulta(FrmcarteraresumenporvigenciasControladorEnum.REPORTE000841.getValue(),
                            Integer.valueOf(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(
                            FrmcarteraresumenporvigenciasControladorEnum.REPORTE000841.getValue(), parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString("MSM_INFORME_NO_EXISTE"), " ", ex.getMessage(), " ",
                            FrmcarteraresumenporvigenciasControladorEnum.REPORTE000841.getValue()));
            Logger.getLogger(FrmcarteraresumenporvigenciasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

        catch (JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(FrmcarteraresumenporvigenciasControladorEnum.MSM_TRANS_INTERRUMPIDA.getValue()),
                            e.getMessage()));
        }
    }

    public void oprimirPdf()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaTCodigoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
        cargarListaTCodigoFinal();
    }

    public void seleccionarFilaTCodigoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    public Boolean getIndCantPredios()
    {
        return indCantPredios;
    }

    public void setIndCantPredios(Boolean indCantPredios)
    {
        this.indCantPredios = indCantPredios;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public String getTano()
    {
        return tano;
    }

    public void setTano(String tano)
    {
        this.tano = tano;
    }

    public String getCodigoInicial()
    {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial)
    {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal()
    {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal)
    {
        this.codigoFinal = codigoFinal;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaTAno()
    {
        return listaTAno;
    }

    public void setListaTAno(List<Registro> listaTAno)
    {
        this.listaTAno = listaTAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaTCodigoInicial()
    {
        return listaTCodigoInicial;
    }

    public void setListaTCodigoInicial(RegistroDataModelImpl listaTCodigoInicial)
    {
        this.listaTCodigoInicial = listaTCodigoInicial;
    }

    public RegistroDataModelImpl getListaTCodigoFinal()
    {
        return listaTCodigoFinal;
    }

    public void setListaTCodigoFinal(RegistroDataModelImpl listaTCodigoFinal)
    {
        this.listaTCodigoFinal = listaTCodigoFinal;
    }

    /**
     * Retorna la variable predioIncluye
     * 
     * @return predioIncluye
     */
    public String getPredioIncluye()
    {
        return predioIncluye;
    }

    /**
     * Asigna la variable predioIncluye
     * 
     * @param predioIncluye
     * Variable a asignar en predioIncluye
     */
    public void setPredioIncluye(String predioIncluye)
    {
        this.predioIncluye = predioIncluye;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
