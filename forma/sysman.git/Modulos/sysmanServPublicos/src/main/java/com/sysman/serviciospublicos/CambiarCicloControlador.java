package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
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
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosUnoRemote;
import com.sysman.serviciospublicos.enums.CambiarCicloControladorUrlEnum;

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

import org.primefaces.event.SelectEvent;

/**
 *
 * @author jrodriguezr
 * @version 1, 29/08/2016 11:47:04 -- Modificado por jrodriguezr
 * 
 * @version 2, 16/05/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 * @author ybecerra
 * @version 3, 13/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 * 
 */

@ManagedBean
@ViewScoped
public class CambiarCicloControlador extends BeanBaseModal
{
    private final String compania;
    private final String codigoRutaC;
    // <DECLARAR_ATRIBUTOS>
    private String codigoRuta;
    private String nuevoCiclo;
    private String codigoRutaNombre;
    private String codigoRutaCiclo;
    private String anioCodigoRuta;
    private String periodoRuta;
    private String anioCiclo;
    private String periodoCiclo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaTxtNuevoCiclo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCodigoRuta;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    @EJB
    private EjbServiciosPublicosUnoRemote ejbServiciosPublicosUnoRemote;

    /**
     * Creates a new instance of CambiarCicloControlador
     */
    public CambiarCicloControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        codigoRutaC = GeneralParameterEnum.CODIGORUTA.getName();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.CAMBIAR_CICLO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(CambiarCicloControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        cargarListaTxtNuevoCiclo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoRuta();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaTxtNuevoCiclo()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listaTxtNuevoCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CambiarCicloControladorUrlEnum.URL3143
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCodigoRuta()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CambiarCicloControladorUrlEnum.URL6368
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCodigoRuta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaC);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirCmdCambiar()
    {
        // <CODIGO_DESARROLLADO>
        String cambiarCiclo = null;
        if (codigoRutaCiclo.equals(nuevoCiclo))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1410"));
            return;
        }

        try
        {
            cambiarCiclo = ejbSysmanUtilRemote.consultarParametro(compania,
                            "CAMBIAR CICLO A USUARIOS CON PERIODO DIFERENTE",
                            SessionUtil.getModulo(), new Date(), true);
        }
        catch (SystemException e)
        {
            Logger.getLogger(CambiarCicloControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (cambiarCiclo == null)
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1382"));
        }
        if (!periodoRuta.equals(periodoCiclo) && "NO".equals(cambiarCiclo))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1383"));
        }
        else if (periodoRuta.equals(periodoCiclo)
            || "SI".equals(cambiarCiclo))
        {
            actualizarRangos();
        }

        // </CODIGO_DESARROLLADO>
    }

    public void actualizarRangos()
    {
        List<Registro> rs = null;
        try
        {
            rs = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CambiarCicloControladorUrlEnum.URL5380
                                                                            .getValue())
                                            .getUrl(), null));

            if (!rs.isEmpty())
            {
                StringBuilder usuarioErrado = new StringBuilder();
                for (Registro registro : rs)
                {
                    usuarioErrado.append(idioma.getString("TB_TB1384").replace(
                                    "#ciclo#",
                                    registro.getCampos().get("CICLO")
                                                    .toString())
                                    .replace("#ruta#", registro.getCampos()
                                                    .get(codigoRutaC)
                                                    .toString())
                                    .replace("#nombres#", registro.getCampos()
                                                    .get("SEGUNDOAPELLIDO")
                                        + " "
                                        + registro.getCampos()
                                                        .get("PRIMERAPELLIDO")
                                        + " "
                                        + registro.getCampos().get("NOMBRES")));
                }
                JsfUtil.agregarMensajeAlerta(usuarioErrado.toString());
                return;
            }

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);

            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CambiarCicloControladorUrlEnum.URL5649
                                                                            .getValue())
                                            .getUrl(), param));

            if (reg == null)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1385"));
                return;
            }

            Map<String, Object> param2 = new TreeMap<>();
            param2.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param2.put(GeneralParameterEnum.MES.getName(), periodoCiclo);
            param2.put(GeneralParameterEnum.ANO.getName(), anioCiclo);

            reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CambiarCicloControladorUrlEnum.URL2349
                                                                            .getValue())
                                            .getUrl(), param2));

            if (reg == null)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1386"));
                return;
            }
            else
            {
                String rtaFuncion = ejbServiciosPublicosUnoRemote.cambiarCiclo(
                                compania, Integer.parseInt(nuevoCiclo),
                                codigoRuta, Integer.parseInt(anioCiclo),
                                periodoCiclo, compania,
                                Integer.parseInt(codigoRutaCiclo),
                                codigoRuta, SessionUtil.getUser().getCodigo());
                if ("1".equals(rtaFuncion))
                {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB1511"));
                }

                ejbServiciosPublicosUnoRemote.actualizarRangos(compania,
                                Integer.parseInt(codigoRutaCiclo));
                ejbServiciosPublicosUnoRemote.actualizarRangos(compania,
                                Integer.parseInt(nuevoCiclo));
            }
        }
        catch (NumberFormatException | SystemException e1)
        {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarTxtNuevoCiclo()
    {
        // <CODIGO_DESARROLLADO>
        anioCiclo = service.buscarEnLista(nuevoCiclo,
                        GeneralParameterEnum.NUMERO.getName(),
                        GeneralParameterEnum.ANO.getName(),
                        listaTxtNuevoCiclo);
        periodoCiclo = service.buscarEnLista(nuevoCiclo,
                        GeneralParameterEnum.NUMERO.getName(),
                        GeneralParameterEnum.PERIODO.getName(),
                        listaTxtNuevoCiclo);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void onRowSelectCodigoRuta(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoRuta = registroAux.getCampos().get(codigoRutaC).toString();
        codigoRutaNombre = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
        codigoRutaCiclo = registroAux.getCampos()
                        .get(GeneralParameterEnum.CICLO.getName()).toString();
        anioCodigoRuta = registroAux.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()).toString();
        periodoRuta = registroAux.getCampos()
                        .get(GeneralParameterEnum.PERIODO.getName()).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getCodigoRuta()
    {
        return codigoRuta;
    }

    public void setCodigoRuta(String codigoRuta)
    {
        this.codigoRuta = codigoRuta;
    }

    public String getNuevoCiclo()
    {
        return nuevoCiclo;
    }

    public void setNuevoCiclo(String nuevoCiclo)
    {
        this.nuevoCiclo = nuevoCiclo;
    }

    public String getCodigoRutaNombre()
    {
        return codigoRutaNombre;
    }

    public void setCodigoRutaNombre(String codigoRutaNombre)
    {
        this.codigoRutaNombre = codigoRutaNombre;
    }

    public String getCodigoRutaCiclo()
    {
        return codigoRutaCiclo;
    }

    public void setCodigoRutaCiclo(String codigoRutaCiclo)
    {
        this.codigoRutaCiclo = codigoRutaCiclo;
    }

    public String getAnioCodigoRuta()
    {
        return anioCodigoRuta;
    }

    public void setAnioCodigoRuta(String anioCodigoRuta)
    {
        this.anioCodigoRuta = anioCodigoRuta;
    }

    public String getPeriodoRuta()
    {
        return periodoRuta;
    }

    public void setPeriodoRuta(String periodoRuta)
    {
        this.periodoRuta = periodoRuta;
    }

    public String getAnioCiclo()
    {
        return anioCiclo;
    }

    public void setAnioCiclo(String anioCiclo)
    {
        this.anioCiclo = anioCiclo;
    }

    public String getPeriodoCiclo()
    {
        return periodoCiclo;
    }

    public void setPeriodoCiclo(String periodoCiclo)
    {
        this.periodoCiclo = periodoCiclo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaTxtNuevoCiclo()
    {
        return listaTxtNuevoCiclo;
    }

    public void setListaTxtNuevoCiclo(List<Registro> listaTxtNuevoCiclo)
    {
        this.listaTxtNuevoCiclo = listaTxtNuevoCiclo;
    }

    public RegistroDataModelImpl getListaCodigoRuta()
    {
        return listaCodigoRuta;
    }

    public void setListaCodigoRuta(RegistroDataModelImpl listaCodigoRuta)
    {
        this.listaCodigoRuta = listaCodigoRuta;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
