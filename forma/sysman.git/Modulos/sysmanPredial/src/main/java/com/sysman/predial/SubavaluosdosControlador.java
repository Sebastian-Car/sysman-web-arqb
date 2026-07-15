package com.sysman.predial;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.ejb.EjbPredialDosRemote;
import com.sysman.predial.enums.SubavaluosdosControladorEnum;
import com.sysman.predial.enums.SubavaluosdosControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author NGOMEZ
 * @version 1, 10/06/2016
 *
 * @author lcortes
 * @version 2, 18,19/07/2017. Refactorizacion del codigo, revision de
 * observaciones de la herramienta SonarLint y reemplazo del llamado a
 * la clase Acciones por el ejb respectivo.
 */
@ManagedBean
@ViewScoped
public class SubavaluosdosControlador extends BeanBaseContinuoAcmeImpl
{
    private final String compania;
    private final String nOrden;
    private int indice;
    private String resolucion;
    private String numOrden;
    private String codigo;
    private String codigoPadre;
    private String ultimoAnio;
    private String dblAvaluoAnt;
    private String trpran;
    private static final String AVALUO = "AVALUO";
    private static final String PREANO = "PREANO";
    private static final String TRPCOD = "TRPCOD";
    private static final String TRPRANN = "TRPRAN";
    private List<Registro> listaANO;
    private RegistroDataModelImpl listaTRPCOD;
    private RegistroDataModelImpl listaTRPCODE;
    private String auxiliar;

    @EJB
    private EjbPredialDosRemote ejbPredialDos;

    /**
     * Creates a new instance of Subavaluos2sControlador
     */
    public SubavaluosdosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        nOrden = SysmanConstantes.NUMERO_ORDEN_PREDIAL;
        try
        {
            numFormulario = GeneralCodigoFormaEnum.SUBAVALUOSDOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                resolucion = parametrosEntrada.get("resolucion").toString();
                numOrden = parametrosEntrada.get("norden").toString();
                codigo = parametrosEntrada.get("codigo").toString();
                ultimoAnio = parametrosEntrada.get("ultimo_anio").toString();
                codigoPadre = parametrosEntrada.get("codigoPadre").toString();
            }
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(SubavaluosdosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally
        {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void init()
    {
        tabla = GenericUrlEnum.IP_FACTURADOS.getTable();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListaANO();
        cargarListaTRPCOD();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen()
    {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        nOrden);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(), codigo);

        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        SubavaluosdosControladorUrlEnum.URL0001.getValue());
        urlCreacion = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        SubavaluosdosControladorUrlEnum.URL0002.getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubavaluosdosControladorUrlEnum.URL0003
                                                        .getValue());
        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubavaluosdosControladorUrlEnum.URL0004
                                                        .getValue());

    }

    public int getIndice()
    {
        return indice;
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaANO()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaANO = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubavaluosdosControladorUrlEnum.URL4703
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaTRPCOD()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubavaluosdosControladorUrlEnum.URL4979
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        registro.getCampos().get(PREANO));

        listaTRPCOD = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, TRPCOD);
    }

    public void cargarListaTRPCODE(int rowNum)
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubavaluosdosControladorUrlEnum.URL5659
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SubavaluosdosControladorEnum.PARAM0.getValue(),
                        listaInicial.getDatasource().get(rowNum % 10)
                                        .getCampos().get(PREANO));

        listaTRPCODE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        TRPCOD);
    }

    public void cambiarANO()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(TRPCOD, null);
        cargarListaTRPCOD();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarANOC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(TRPCOD,
                        null);
        cargarListaTRPCODE(rowNum);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTRPCODC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(TRPRANN,
                        trpran);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAVALUO()
    {
        registro.getCampos().put("AVALUOANT", dblAvaluoAnt);
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaTRPCOD(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(TRPCOD, registroAux.getCampos().get(TRPCOD));
        registro.getCampos().put(TRPRANN, registroAux.getCampos().get(TRPRANN));
    }

    public void seleccionarFilaTRPCODE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(TRPCOD).toString();
        trpran = registroAux.getCampos().get(TRPRANN).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), nOrden);
        param.put(GeneralParameterEnum.CODIGO.getName(), codigoPadre);
        param.put(GeneralParameterEnum.PREANO.getName(),
                        registro.getCampos().get(PREANO).toString());

        try
        {
            Registro regi = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubavaluosdosControladorUrlEnum.URL0005
                                                                            .getValue())
                                            .getUrl(), param));

            if (regi != null)
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2907")
                                .replace("s$predio$s", codigoPadre)
                                .replace("s$ano$s",
                                                registro.getCampos().get(PREANO)
                                                                .toString()));
                return false;
            }
            else
            {
                registro.getCampos().put(
                                GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                                codigo);
                registro.getCampos().put(
                                GeneralParameterEnum.NUMERO_ORDEN.getName(),
                                numOrden);
                registro.getCampos().put("NUMRESOLUCION", resolucion);
                registro.getCampos().put("TARIFAANT",
                                registro.getCampos().get(TRPCOD));
                registro.getCampos().put("PREIND", "M");
                registro.getCampos().put("AVALUOANT",
                                registro.getCampos().get(AVALUO));
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        dblAvaluoAnt = registro.getCampos().get(AVALUO).toString();
        String strTarifaAnt = trpran;
        registro.getCampos().put("TARIFAANT",
                        SysmanFunciones.nvl(strTarifaAnt, "00"));
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>

        try
        {
            ejbPredialDos.actualizarAvaluoAnterior(compania, resolucion,
                            Integer.valueOf(registro.getCampos()
                                            .get("PREANO")
                                            .toString()),
                            codigo, Integer.parseInt(ultimoAnio),
                            new BigDecimal(registro.getCampos().get(AVALUO)
                                            .toString()),
                            SysmanFunciones.nvl(trpran, "00").toString());
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public void removerCombos()
    {
        // heredado del bean base
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
        registro.getCampos()
                        .remove(GeneralParameterEnum.NUMERO_ORDEN.getName());
        registro.getCampos().remove(GeneralParameterEnum.PREANO.getName());
    }

    public void activarEdicion(Registro registro)
    {
        indice = listaInicial.getRowIndex();
        cargarListaANO();
        cargarListaTRPCODE(indice);
    }

    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void asignarValoresRegistro()
    {
        // heredado del bean base
    }

    // <SET_GET_ATRIBUTOS>
    public String getTrpran()
    {
        return trpran;
    }

    public void setTrpran(String trpran)
    {
        this.trpran = trpran;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    public String getResolucion()
    {
        return resolucion;
    }

    public void setResolucion(String resolucion)
    {
        this.resolucion = resolucion;
    }

    public String getNorden()
    {
        return numOrden;
    }

    public void setNorden(String norden)
    {
        this.numOrden = norden;
    }

    public String getCodigo()
    {
        return codigo;
    }

    public void setCodigo(String codigo)
    {
        this.codigo = codigo;
    }

    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaANO()
    {
        return listaANO;
    }

    public void setListaANO(List<Registro> listaANO)
    {
        this.listaANO = listaANO;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaTRPCOD()
    {
        return listaTRPCOD;
    }

    public void setListaTRPCOD(RegistroDataModelImpl listaTRPCOD)
    {
        this.listaTRPCOD = listaTRPCOD;
    }

    public RegistroDataModelImpl getListaTRPCODE()
    {
        return listaTRPCODE;
    }

    public void setListaTRPCODE(RegistroDataModelImpl listaTRPCODE)
    {
        this.listaTRPCODE = listaTRPCODE;
    }

    public String getAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    public void setIndice(int indice)
    {
        this.indice = indice;
    }

    public String getCodigoPadre()
    {
        return codigoPadre;
    }
}
