
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.PedirciclosControladorEnum;
import com.sysman.serviciospublicos.enums.PedirciclosControladorUrlEnum;
import com.sysman.servpublicos.ejb.EjbServiciosPublicosCuatroGeneralRemote;

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

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author ybecerra
 * @version 1, 20/08/2016
 * @version 2, 12/05/2017 modificado por jcrodriguez
 * Descripcion:*Depuracion del controlador *Refactoring y creacion de
 * DSS
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped
public class PedirciclosControlador extends BeanBaseModal
{

    /**
     * Constante que almacena el codigo de la compania por la que se
     * ingresa a la aplicacion se utiliza para el filtro de la
     * consulta del combo cargarCiclo
     */
    private final String compania;

    /**
     * Constante que almacena el codigo del modulo por la que se
     * ingresa a la aplicacion, se utiliza para el llamado del metodo
     * getParametro
     */
    private final String modulo;
    /**
     * Almacena el codigo seleccionado en el combo ciclo del
     * formulario
     */
    private String ciclo;

    /**
     * Almacena el periodo seleccionado en el combo ciclo del
     * formulario
     */
    private String periodo;
    /**
     * Almacena el ano del ciclo selecciona en el combo ciclo del
     * formulario
     */
    private String anio;
    /**
     * Almacena el valor de prefacturacion de la consulta, segun el
     * ciclo seleccionado del formulario
     */
    private boolean prefacturacion;
    /**
     * Atributo local definido para validar si el formulario de una
     * opcion de menu se visualice o no
     */
    private boolean preFactura = false;
    /**
     * variable que almacena la lista de ciclos
     */
    private RegistroDataModelImpl listaCiclo;
    /**
     * Se define una consatante, porque se llama varias veces en el
     * evento de oprimirAceptar
     */
    private final String paramCiclo;

    /**
     * Creates a new instance of PedirciclosControlador
     */

    private String tasaRecargo;
    private final String menuActual;
    /**
     * Ejb para servicios
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbServiciosPublicosCuatroGeneralRemote ejbServiciosPublicosCuatroGeneral;

    public PedirciclosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        paramCiclo = GeneralParameterEnum.CICLO.getName().toLowerCase();
        modulo = SessionUtil.getModulo();
        menuActual = SessionUtil.getMenuActual();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.PEDIRCICLOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(PedirciclosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * metodo que es llamado al inicializar el formulario
     */
    @PostConstruct
    public void inicializar()
    {
        cargarListaCiclo();
        abrirFormulario();
    }

    /**
     * metodo que se llama al abrir el formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo que carga la lista de ciclos
     */
    public void cargarListaCiclo()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PedirciclosControladorUrlEnum.URL4065
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());
    }

    /**
     * metodo que consulta un parametro con el ejb
     *
     * @param nombre
     * @param isMayMin
     * @return
     */
    private String getParametro(String nombre, boolean isMayMin)
    {
        try
        {
            return ejbSysmanUtil.consultarParametro(compania, nombre, modulo,
                            new Date(), isMayMin);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return null;
    }

    /**
     * metodo el cual redirecciona a un formulario
     */
    private void redireccionar(Map<String, Object> parametros,
        String formulario)
    {
        parametros.put(paramCiclo, ciclo);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(formulario);
        direccionador.setParametros(parametros);
        direccionador.getRuta();
        RequestContext.getCurrentInstance().closeDialog(direccionador);
    }

    /**
     * metodo que consulta si un ciclo esta bloqueado
     *
     * @return
     */
    private boolean cicloIsBloqueado()
    {
        UrlBean url = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        PedirciclosControladorUrlEnum.URL4024.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        try
        {
            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(url.getUrl(), param));
            if ((rs != null) && (Boolean.parseBoolean(
                            rs.getCampos().get(
                                            PedirciclosControladorEnum.INDPREPARADO
                                                            .getValue())
                                            .toString()))
                && (Boolean.parseBoolean(
                                rs.getCampos().get(
                                                PedirciclosControladorEnum.INDBLOQUEOMANUAL
                                                                .getValue())
                                                .toString())))
            {
                JsfUtil.agregarMensajeAlerta(idioma
                                .getString(PedirciclosControladorEnum.TB_TB3156
                                                .getValue()));
                return true;
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return false;
    }

    /**
     * metodo que se llama al oprimir el boton de aceptar
     */
    public void oprimirAceptar()
    {
        // <CODIGO_DESARROLLADO>
        /*
         * Almacena el valor del parametro MANEJA PREFACTURACION
         */
        String manejaPrefacturacion = getParametro(
                        parametros.getString("PR_MANEJA_PREFACTURACION"), true);

        if (manejaPrefacturacion == null)
        {
            JsfUtil.agregarMensajeError(idioma.getString(
                            PedirciclosControladorEnum.TB_TB1678.getValue()));
            return;
        }

        if (PedirciclosControladorEnum.SI.getValue()
                        .equals(manejaPrefacturacion)
            && prefacturacion)
        {
            preFactura = true;
        }
        Map<String, Object> parametros = new HashMap<>();
        if ("740207".equals(SessionUtil.getMenuActual()))
        {
            menu740207(parametros);
        }
        else if ("740518".equals(SessionUtil.getMenuActual()))
        {
            menu740518(parametros);
        }
        else if ("74051901".equals(SessionUtil.getMenuActual()))
        {
            redireccionar(parametros, PedirciclosControladorEnum.FORMULARIO1046
                            .getValue());
        }
        else if ("740401".equals(SessionUtil.getMenuActual()))
        {
            if (menu740401(parametros))
            {
                return;
            }
        }
        else if ("74040501".equals(SessionUtil.getMenuActual()))
        {
            if (menu74040501(parametros))
            {
                return;
            }
        }
        else if ("740404".equals(SessionUtil.getMenuActual()))
        {
            menu740404(parametros);
        }
        else if ("740201".equals(SessionUtil.getMenuActual()))
        {
            if (formulario740201(parametros))
            {
                return;
            }
        }
        else if ("740202".equals(SessionUtil.getMenuActual()))
        {

            cargarConsultaFacturacion();
        }
        else if ("740206".equals(SessionUtil.getMenuActual()))
        {

            menu740206(parametros);
        }
        else if ("740420".equals(SessionUtil.getMenuActual()))
        {

            if (menu740420(parametros))
            {
                return;
            }
        }
        else if ("740421".equals(SessionUtil.getMenuActual()))
        {

            redireccionar(parametros, PedirciclosControladorEnum.FORMULARIO1123
                            .getValue());// CASE DESINCENTIVO
        }
        else if ("740419".equals(SessionUtil.getMenuActual()))
        {

            redireccionar(parametros, PedirciclosControladorEnum.FORMULARIO1095
                            .getValue());// CASE DESINCENTIVO
        }
        else if ("740418".equals(SessionUtil.getMenuActual()))
        {
            if (menu740418(parametros))
            {
                return;
            }
        }
        else if ("740417".equals(SessionUtil.getMenuActual()))
        {
            menu740417(parametros);
        }
        /* Opcion Asignar Empresas */
        else if ("7408010101".equals(SessionUtil.getMenuActual()))
        {
            abrirFormularioAsignarEmpresas(parametros);
        }
        else if ("7404022".equals(menuActual))
        {
            abrirCorreccionLecturas(parametros);
        }
    }

    private void abrirCorreccionLecturas(Map<String, Object> parametros)
    {
        parametros.put("ciclo", ciclo);
        redireccionar(parametros, Integer.toString(
                        GeneralCodigoFormaEnum.CORRECCIONCRITICAS_CONTROLADOR.getCodigo()));
    }

    private void abrirFormularioAsignarEmpresas(
        Map<String, Object> parametros)
    {
        parametros.put("ciclo", ciclo);
        redireccionar(parametros, Integer.toString(
                        GeneralCodigoFormaEnum.SPASIGNAREMPRESAS_CONTROLADOR
                                        .getCodigo()));
    }

    /**
     * metodo que redirecciona deacuerdo a un menu del
     * SessionUtil.getMenuActual()
     *
     * @param parametros
     * @return
     */
    private void menu740417(Map<String, Object> parametros)
    {
        parametros.put(PedirciclosControladorEnum.ANIO.getValue().toLowerCase(),
                        anio);
        parametros.put(GeneralParameterEnum.PERIODO.getName().toLowerCase(),
                        periodo);
        redireccionar(parametros,
                        PedirciclosControladorEnum.FORMULARIO1264.getValue());
    }

    /**
     * metodo que redirecciona deacuerdo a un menu del
     * SessionUtil.getMenuActual()
     *
     * @param parametros
     * @return
     */
    private void menu740206(Map<String, Object> parametros)
    {
        parametros.put(GeneralParameterEnum.PERIODO.getName().toLowerCase(),
                        periodo);
        parametros.put(PedirciclosControladorEnum.TASARECARGO.getValue(),
                        tasaRecargo);
        parametros.put(PedirciclosControladorEnum.ANIO.getValue().toLowerCase(),
                        anio);
        redireccionar(parametros,
                        PedirciclosControladorEnum.FORMULARIO1244.getValue());

    }

    /**
     * metodo que redirecciona deacuerdo a un menu del
     * SessionUtil.getMenuActual()
     *
     * @param parametros
     * @return
     */
    private void menu740404(Map<String, Object> parametros)
    {
        parametros.put(GeneralParameterEnum.PERIODO.getName().toLowerCase(),
                        periodo);
        parametros.put(PedirciclosControladorEnum.ANIO.getValue().toLowerCase(),
                        anio);
        redireccionar(parametros,
                        PedirciclosControladorEnum.FORMULARIO1107.getValue());
    }

    /**
     * metodo que redirecciona deacuerdo a un menu del
     * SessionUtil.getMenuActual()
     *
     * @param parametros
     * @return
     */
    private boolean menu740401(Map<String, Object> parametros)
    {
        if (cicloIsBloqueado())
        {
            return true;
        }
        parametros.put(GeneralParameterEnum.PERIODO.getName().toLowerCase(),
                        periodo);
        parametros.put(PedirciclosControladorEnum.ANIO.getValue().toLowerCase(),
                        anio);
        redireccionar(parametros,
                        PedirciclosControladorEnum.FORMULARIO1097.getValue());
        return false;
    }

    /**
     * metodo que redirecciona deacuerdo a un menu del
     * SessionUtil.getMenuActual()
     *
     * @param parametros
     * @return
     */
    private boolean menu74040501(Map<String, Object> parametros)
    {
        if (cicloIsBloqueado())
        {
            return true;
        }
        if (buscarCiclo())
        {
            return true;
        }
        parametros.put(GeneralParameterEnum.PERIODO.getName().toLowerCase(),
                        periodo);
        parametros.put(PedirciclosControladorEnum.ANIO.getValue().toLowerCase(),
                        anio);
        redireccionar(parametros,
                        PedirciclosControladorEnum.FORMULARIO1240.getValue());
        return false;
    }

    /**
     * metodo que redirecciona deacuerdo a un menu del
     * SessionUtil.getMenuActual()
     *
     * @param parametros
     * @return
     */
    private boolean formulario740201(Map<String, Object> parametros)
    {

        if (preFactura)
        {
            JsfUtil.agregarMensajeError(idioma.getString(
                            PedirciclosControladorEnum.TB_TB1678.getValue()));
            return true;
        }
        else
        {
            parametros.put(PedirciclosControladorEnum.ANIO.getValue()
                            .toLowerCase(), anio);
            redireccionar(parametros, PedirciclosControladorEnum.FORMULARIO1078
                            .getValue());
            return false;
        }
    }

    /**
     * metodo que redirecciona deacuerdo a un menu del
     * SessionUtil.getMenuActual()
     *
     * @param parametros
     * @return
     */
    private void menu740207(Map<String, Object> parametros)
    {
        parametros.put(GeneralParameterEnum.PERIODO.getName().toLowerCase(),
                        periodo);
        parametros.put(PedirciclosControladorEnum.ANIO.getValue().toLowerCase(),
                        anio);
        parametros.put(PedirciclosControladorEnum.TASARECARGO.getValue(),
                        tasaRecargo);
        redireccionar(parametros,
                        PedirciclosControladorEnum.FORMULARIO1246.getValue());
    }

    /**
     * metodo que redirecciona deacuerdo a un menu del
     * SessionUtil.getMenuActual()
     *
     * @param parametros
     * @return
     */
    private void menu740518(Map<String, Object> parametros)
    {
        parametros.put(GeneralParameterEnum.PERIODO.getName().toLowerCase(),
                        periodo);
        parametros.put(PedirciclosControladorEnum.ANIO.getValue().toLowerCase(),
                        anio);
        redireccionar(parametros,
                        PedirciclosControladorEnum.FORMULARIO1241.getValue());
    }

    /**
     * metodo que redirecciona deacuerdo a un menu del
     * SessionUtil.getMenuActual()
     *
     * @param parametros
     * @return
     */
    private boolean menu740420(Map<String, Object> parametros)
    {

        String parametroPeriodoAtraso = getParametro(
                        PedirciclosControladorEnum.PR_PERIODOATRASO.getValue(),
                        false);

        if (parametroPeriodoAtraso == null)
        {
            JsfUtil.agregarMensajeError(idioma.getString(
                            PedirciclosControladorEnum.TB_TB1667.getValue()));
            return true;
        }
        parametros.put(PedirciclosControladorEnum.PARAMETRO.getValue()
                        .toLowerCase(), parametroPeriodoAtraso);
        redireccionar(parametros,
                        PedirciclosControladorEnum.FORMULARIO1121.getValue());
        return false;
    }

    /**
     * metodo que redirecciona deacuerdo a un menu del
     * SessionUtil.getMenuActual()
     *
     * @param parametros
     * @return
     */
    private boolean menu740418(Map<String, Object> parametros)
    {
        if (preFactura)
        {
            JsfUtil.agregarMensajeError(idioma.getString(
                            PedirciclosControladorEnum.TB_TB1679.getValue()));
            return true;
        }
        else
        {
            redireccionar(parametros, PedirciclosControladorEnum.FORMULARIO1127
                            .getValue());// CASE
            return false;
        }
    }

    /**
     * metodo que busca si el ciclo esta bloqueado o desbloqueado
     *
     * @return
     */
    private boolean buscarCiclo()
    {
        HashMap<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.NUMERO.getName(), ciclo);
        try
        {
            Registro reg = listaCiclo.getRegistroUnico(param);
            if (reg != null)
            {
                if (!reg.getCampos().get(GeneralParameterEnum.ANO.getName())
                                .equals(reg.getCampos()
                                                .get(PedirciclosControladorEnum.ANOINICIAL
                                                                .getValue()))
                    && !reg.getCampos()
                                    .get(GeneralParameterEnum.PERIODO.getName())
                                    .equals(reg.getCampos()
                                                    .get(PedirciclosControladorEnum.PERIODOINICIAL
                                                                    .getValue())))
                {
                    JsfUtil.agregarMensajeError(idioma
                                    .getString(PedirciclosControladorEnum.TB_TB3034
                                                    .getValue()));
                    return true;
                }
            }
            else
            {
                return false;
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return false;
    }

    public void cargarConsultaFacturacion()
    {
        boolean bloqueado;
        try
        {
            bloqueado = ejbServiciosPublicosCuatroGeneral
                            .estarBloqueado(compania, Integer.parseInt(ciclo));
            if (bloqueado)
            {
                JsfUtil.agregarMensajeError(idioma
                                .getString(PedirciclosControladorEnum.TB_TB3156
                                                .getValue()));
            }
            else
            {
                Map<String, Object> parametros = new HashMap<>();
                parametros.put(paramCiclo, ciclo);
                parametros.put(PedirciclosControladorEnum.ANO.getValue()
                                .toLowerCase(), anio);
                parametros.put("periodo", periodo);
                Direccionador direccionador = new Direccionador();
                direccionador.setNumForm(
                                PedirciclosControladorEnum.FORMULARIO1109
                                                .getValue());// Factura
                // integrados
                direccionador.setParametros(parametros);
                direccionador.getRuta();
                RequestContext.getCurrentInstance()
                                .closeDialog(direccionador);
            }
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que se llama al oprimir el boton de cancelar
     */
    public void oprimirCancelar()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * metodo que se llama al seleccionar un registro de un combo
     * grande
     *
     * @param event
     */
    public void seleccionarFilaCiclo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        ciclo = registroAux.getCampos()
                        .get(GeneralParameterEnum.NUMERO.getName()).toString();
        periodo = registroAux.getCampos()
                        .get(GeneralParameterEnum.PERIODO.getName()).toString();
        anio = registroAux.getCampos().get(GeneralParameterEnum.ANO.getName())
                        .toString();
        tasaRecargo = registroAux.getCampos()
                        .get(PedirciclosControladorEnum.TASARECARGO.getValue())
                        .toString();
        prefacturacion = (boolean) registroAux.getCampos().get(
                        PedirciclosControladorEnum.PREFACTURANDO.getValue());
    }

    /**
     * metodos get y set
     *
     * @return
     */
    public String getCiclo()
    {
        return ciclo;
    }

    public void setCiclo(String ciclo)
    {
        this.ciclo = ciclo;
    }

    public RegistroDataModelImpl getListaCiclo()
    {
        return listaCiclo;
    }

    public void setListaCiclo(RegistroDataModelImpl listaCiclo)
    {
        this.listaCiclo = listaCiclo;
    }
}
