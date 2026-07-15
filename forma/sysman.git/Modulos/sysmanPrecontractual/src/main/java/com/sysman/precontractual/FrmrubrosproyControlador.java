package com.sysman.precontractual;

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
import com.sysman.precontractual.enums.FrmrubrosproyControladorEnum;
import com.sysman.precontractual.enums.FrmrubrosproyControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
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
 * @author otorres
 * @version 1, 28/03/2016
 * @author jcrodriguez,Refactoring y Depuracion
 * @version 2, 29/08/2017
 */
@ManagedBean
@ViewScoped
public class FrmrubrosproyControlador extends BeanBaseContinuoAcmeImpl
{

    private final String compania;
    private final String modulo;
    private int indice;

    /** Constante a nivel de clase que aloja el valor NOMBRE */

    private String anio;
    private RegistroDataModelImpl listaIdRubro;
    private RegistroDataModelImpl listaIdRubroE;
    private String auxiliar;
    private List<Registro> listaCmbAnos;
    private RegistroDataModelImpl listaFuente;
    private RegistroDataModelImpl listaFuenteE;
    private String estudio;
    private String destino;
    private String proyecto;
    private String nombreRubro;
    private String fuenteRecurso;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Atributo que gestiona la visibilidad de los botones de
     * insertar, editar y eliminar
     */
    private boolean esCreador;

    private String costoTotal;

    /**
     * Creates a new instance of FrmrubrosproyControlador
     */
    public FrmrubrosproyControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMRUBROSPROY_CONTROLADOR.getCodigo();
            validarPermisos();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            estudio = validarParametro(parametrosEntrada, "txtCodEstudio");
            destino = validarParametro(parametrosEntrada, "txtTipoDia");
            esCreador = Boolean.parseBoolean(SysmanFunciones.nvlStr(validarParametro(parametrosEntrada, "esCreador"), "false"));
            costoTotal = validarParametro(parametrosEntrada, "costoTotal");

            SessionUtil.cleanFlash();
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmrubrosproyControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.ES_RUBROS_PROY;
        buscarLlave();
        anio = String.valueOf(SysmanFunciones.ano(new Date()));
        reasignarOrigen();
        registro = new Registro();
        cargarListaCmbAnos();
        cargarListaFuente();
        cargarListaFuenteE();
        cargarListaIdRubro();
        cargarListaIdRubroE();
        abrirFormulario();
    }

    private String validarParametro(Map<String, Object> campos, String var)
    {
        return SysmanFunciones.validarCampoVacio(campos, var) ? "" : campos.get(var).toString();
    }

    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(GeneralParameterEnum.COD_ESTUDIO.getName(), estudio);
        parametrosListado.put(GeneralParameterEnum.DESTINO.getName(), destino);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);

    }

    public void cargarListaCmbAnos()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaCmbAnos = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(FrmrubrosproyControladorUrlEnum.URL6374.getValue()).getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaFuente()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(FrmrubrosproyControladorUrlEnum.URL6650.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        listaFuente = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaFuente
     */
    public void cargarListaFuenteE()
    {
        listaFuenteE = listaFuente;

    }

    public void cargarListaIdRubro()
    {
        try
        {
            String parametro = ejbSysmanUtil.consultarParametro(compania, "CODIGOS DE FILTRO,RUBROS EN ESTUDIOS PREVIOS", modulo,
                            new Date(), true);

            UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(FrmrubrosproyControladorUrlEnum.URL7170.getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            param.put(GeneralParameterEnum.CLASE.getName(), parametro);
            listaIdRubro = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                            true, FrmrubrosproyControladorEnum.ID.getValue());
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaIdRubroE()
    {
        listaIdRubroE = listaIdRubro;
    }

    public void cambiarCmbAnos()
    {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        cargarListaFuente();
        cargarListaIdRubro();
        cargarListaIdRubroE();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarIdRubroC(int rowNum)
    {
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        nombreRubro);
    }

    /**
     * Metodo ejecutado al cambiar el control Fuente en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarFuenteC(int rowNum)
    {
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(FrmrubrosproyControladorEnum.FUENTE_RECURSOS.getValue(),
                        fuenteRecurso);
    }

    public void seleccionarFilaIdRubro(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(FrmrubrosproyControladorEnum.ID_RUBRO.getValue(),
                        registroAux.getCampos().get(FrmrubrosproyControladorEnum.ID.getValue()));
        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
    }

    public void seleccionarFilaIdRubroE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = validarParametro(registroAux.getCampos(), FrmrubrosproyControladorEnum.ID.getValue());
        nombreRubro = validarParametro(registroAux.getCampos(), GeneralParameterEnum.NOMBRE.getName());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuente *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuente(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(FrmrubrosproyControladorEnum.NOMBRE_FUENTE.getValue(),
                        validarParametro(registroAux.getCampos(), GeneralParameterEnum.NOMBRE.getName()));
        fuenteRecurso = validarParametro(registroAux.getCampos(), GeneralParameterEnum.CODIGO.getName());
    }

    public void seleccionarFilaFuenteE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = validarParametro(registroAux.getCampos(), GeneralParameterEnum.NOMBRE.getName());
        fuenteRecurso = validarParametro(registroAux.getCampos(), GeneralParameterEnum.CODIGO.getName());
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void buscarProyecto()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.COD_ESTUDIO.getName(), estudio);

        Registro auxiliarProyecto = null;
        try
        {
            auxiliarProyecto = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(FrmrubrosproyControladorUrlEnum.URL6331.getValue()).getUrl(), param));

            if (auxiliarProyecto != null)
            {
                proyecto = validarParametro(auxiliarProyecto.getCampos(), GeneralParameterEnum.PROYECTO.getName());
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

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
        boolean continuar = false;
        buscarProyecto();
        registro.getCampos().remove(FrmrubrosproyControladorEnum.NOMBRE_FUENTE.getValue());
        if (proyecto != null)
        {
            registro.getCampos().put(FrmrubrosproyControladorEnum.FUENTE_RECURSOS.getValue(), fuenteRecurso);
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
            registro.getCampos().put(GeneralParameterEnum.VIGENCIA.getName(), anio);
            registro.getCampos().put(GeneralParameterEnum.COD_ESTUDIO.getName(), estudio);
            registro.getCampos().put(FrmrubrosproyControladorEnum.COD_PROYECTO.getValue(), proyecto);
            registro.getCampos().put(GeneralParameterEnum.DESTINO.getName(), destino);
            continuar = true;
        }
        else
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2226"));
        }

        if (validarValorEstimado())
        {
            return false;
        }
        return continuar;
        // </CODIGO_DESARROLLADO>

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
        // <CODIGO_DESARROLLADO
        if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        GeneralParameterEnum.ANO.getName())
            || !SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            FrmrubrosproyControladorEnum.NOMBRE_FUENTE.getValue()))
        {
            registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
            registro.getCampos().remove(FrmrubrosproyControladorEnum.NOMBRE_FUENTE.getValue());
        }
        if (validarValorEstimado())
        {
            return false;
        }
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
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
        registro.getCampos().remove(FrmrubrosproyControladorEnum.NOMBRE_FUENTE.getValue());
    }

    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    private boolean validarValorEstimado()
    {
        double valorTexto = Double.parseDouble(registro.getCampos().get("VLR_SOLICITADO").toString());
        double valor = Double.parseDouble(costoTotal);
        if (valorTexto > valor)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3669"));
            return true;
        }

        return false;
    }

    public boolean isEsCreador()
    {
        return esCreador;
    }

    public void setEsCreador(boolean esCreador)
    {
        this.esCreador = esCreador;
    }

    public RegistroDataModelImpl getListaFuente()
    {
        return listaFuente;
    }

    public void setListaFuente(RegistroDataModelImpl listaFuente)
    {
        this.listaFuente = listaFuente;
    }

    public List<Registro> getListaCmbAnos()
    {
        return listaCmbAnos;
    }

    public void setListaCmbAnos(List<Registro> listaCmbAnos)
    {
        this.listaCmbAnos = listaCmbAnos;
    }

    public RegistroDataModelImpl getListaIdRubro()
    {
        return listaIdRubro;
    }

    public void setListaIdRubro(RegistroDataModelImpl listaIdRubro)
    {
        this.listaIdRubro = listaIdRubro;
    }

    public RegistroDataModelImpl getListaIdRubroE()
    {
        return listaIdRubroE;
    }

    public void setListaIdRubroE(RegistroDataModelImpl listaIdRubroE)
    {
        this.listaIdRubroE = listaIdRubroE;
    }

    public String getAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    public String getAnio()
    {
        return anio;
    }

    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    public RegistroDataModelImpl getListaFuenteE()
    {
        return listaFuenteE;
    }

    public void setListaFuenteE(RegistroDataModelImpl listaFuenteE)
    {
        this.listaFuenteE = listaFuenteE;
    }

    public String getFuenteRecurso()
    {
        return fuenteRecurso;
    }

    public void setFuenteRecurso(String fuenteRecurso)
    {
        this.fuenteRecurso = fuenteRecurso;
    }

    public int getIndice()
    {
        return indice;
    }

    public void setIndice(int indice)
    {
        this.indice = indice;
    }

}
