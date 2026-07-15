package com.sysman.almacen;

import com.sysman.almacen.enums.TipoEventoReqControladorEnum;
import com.sysman.almacen.enums.TipoEventoReqControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author OTORRES
 * @version 1, 03/02/2016
 * 
 * @author eamaya
 * @version 2, 10/05/2017 Proceso de Refactoring, Correcciones
 * SonarLint
 * 
 * @author ybecerra
 * @version 3, 13/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 */
@ManagedBean
@ViewScoped

public class TipoEventoReqControlador extends BeanBaseContinuoAcmeImpl
{

    private final String compania;
    private final String requisitoCons;

    private List<Registro> listaREQUISITO;

    private String tipoEvento;
    private Object requisito;

    /**
     * Creates a new instance of TipoEventoReqControlador
     */
    public TipoEventoReqControlador()
    {
        super();
        requisitoCons = "REQUISITO";

        compania = SessionUtil.getCompania();

        try
        {
            numFormulario = GeneralCodigoFormaEnum.TIPO_EVENTO_REQ_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException e)
        {

            logger.error(e.getMessage(), e);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar()
    {

        enumBase = GenericUrlEnum.REQUISITOS_TEVENTO;
        buscarLlave();
        Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
        if (parametrosEntrada != null)
        {
            tipoEvento = (String) parametrosEntrada.get("tipoEvento");
        }
        SessionUtil.cleanFlash();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaREQUISITO();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(TipoEventoReqControladorEnum.PARAM0.getValue(),
                        tipoEvento);
    }

    public List<Registro> getListaREQUISITO()
    {
        return listaREQUISITO;
    }

    public void setListaREQUISITO(List<Registro> listaREQUISITO)
    {
        this.listaREQUISITO = listaREQUISITO;
    }

    public void cargarListaREQUISITO()
    {
        try
        {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaREQUISITO = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TipoEventoReqControladorUrlEnum.URL2822
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirbotonOculto()
    {
        // <CODIGO_DESARROLLADO>
        if (Integer.parseInt(requisito.toString()) == -999)
        {
            registro.getCampos().put(requisitoCons, " ");
            SessionUtil.cargarModalDatos(
                            String.valueOf(GeneralCodigoFormaEnum.REQUISITOS_PRESTS_CONTROLADOR
                                            .getCodigo()),
                            SessionUtil.getModulo());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void retornarFormulariobotonOculto()
    {
        // <CODIGO_DESARROLLADO>
        cargarListaREQUISITO();
        // </CODIGO_DESARROLLADO>
    }

    public void ejecutarremotoModal()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarREQUISITO()
    {
        // <CODIGO_DESARROLLADO>
        requisito = registro.getCampos().get(requisitoCons);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarREQUISITOC(int rowNum)
    {

        // <CODIGO_DESARROLLADO>
        requisito = SysmanFunciones.nvl(listaInicial.getDatasource()
                        .get(rowNum % 10).getCampos()
                        .get(requisitoCons), " ").toString();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        /*
         * FR498-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * Me!Titulo.Caption = "REQUISITOS DE EVENTO " &
         * UCase(Forms!FRM_TIPOEVENTO!Nombre) End Sub
         */
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
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put("TIPO_EVENTO", tipoEvento);
        // </CODIGO_DESARROLLADO>
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
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove("NOMBRE_EVENTO");
        // </CODIGO_DESARROLLADO>
        return true;
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

    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void removerCombos()
    {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove("NOMBRE_EVENTO");
    }

    @Override
    public void asignarValoresRegistro()
    {
        // Metodo heredado de la clase BeanBase
    }

}
