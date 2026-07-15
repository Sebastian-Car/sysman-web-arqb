package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.serviciospublicos.enums.TarifasincindexsControladorEnum;
import com.sysman.serviciospublicos.enums.TarifasincindexsControladorUrlEnum;

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

/**
 *
 * @author ybecerra
 * @version 1, 14/09/2016
 * 
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 * 
 * @version 2, 16/06/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno de los combos.
 */

@ManagedBean
@ViewScoped
public class TarifasincindexsControlador extends BeanBaseModal
{
    private final String compania;
    private final String modulo;
    private final String consPorcentaje;
    // <DECLARAR_ATRIBUTOS>
    private int ano;
    private String periodo;
    private String porIndexAc;
    private String porIndexAl;
    private String porIndexAs;
    private String porIndexAlu;
    private String acueducto;
    private String alcantarillado;
    private String aseo;
    private String alumbrado;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;
    private List<Registro> listaPeriodo;
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of TarifasincindexsControlador
     */
    public TarifasincindexsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        consPorcentaje="Porcentaje ind.";
        try
        {
            numFormulario = GeneralCodigoFormaEnum.TARIFASINCINDEXS_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(TarifasincindexsControlador.class.getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init()
    {
        // <CARGAR_LISTA>
        cargarListaAno();

        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {

        mensajesInicioModal();
        porIndexAc = "0";
        porIndexAl = "0";
        porIndexAlu = "0";
        porIndexAs = "0";
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAno() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TarifasincindexsControladorUrlEnum.URL3177
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPeriodo() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano);

            listaPeriodo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TarifasincindexsControladorUrlEnum.URL3482
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put(TarifasincindexsControladorEnum.PARAM0.getValue(),
                            porIndexAl);
            parametros.put(TarifasincindexsControladorEnum.PARAM1.getValue(),
                            porIndexAc);
            parametros.put(TarifasincindexsControladorEnum.PARAM2.getValue(),
                            porIndexAs);
            parametros.put(TarifasincindexsControladorEnum.PARAM3.getValue(),
                            porIndexAlu);

            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametros.put(GeneralParameterEnum.ANO.getName(), ano);
            parametros.put(GeneralParameterEnum.PERIODO.getName(), periodo);
            parametros.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
            parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());

            Parameter parameter = new Parameter();
            parameter.setFields(parametros);

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            TarifasincindexsControladorUrlEnum.URL4657
                                                            .getValue());
            int rta = requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            parameter);

            if (rta != 0) {
                String msj = idioma.getString("TB_TB1546");
                msj = msj.replace("s$rta$s", Integer.toString(rta));
                JsfUtil.agregarMensajeInformativo(msj);
                return;
            }
            else {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1547"));
                return;
            }
        }
        catch (SystemException ex) {
            Logger.getLogger(TarifasincindexsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

            JsfUtil.agregarMensajeError(ex.getMessage());

        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirSalir()
    {
        // <CODIGO_DESARROLLADO>
        JsfUtil.ejecutarJavaScript("cerrarModalDefault();");
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno()
    {
        cargarListaPeriodo();
    }
    
    public String consultarParametro(String nombre, boolean may) throws SystemException{
        return ejbSysmanUtilRemote.consultarParametro(compania, nombre, modulo, new Date(), may);
    }

    public boolean parametro() {
        String strNomServicio;
        boolean rta;
        rta = true;
        try {
            strNomServicio = consultarParametro(
                            "NOMBRE SERVICIO A REMPLAZAR ACUEDUCTO", false);
            String parAcu = consultarParametro(
                            "CAMBIAR NOMBRE SERVICIO ACUEDUCTO", true);
            if (strNomServicio == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1471"));
                rta = false;
            }
            else {
                if (parAcu == null) {
                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB1470"));
                    rta = false;
                }
                else if ("SI".equals(parAcu)) {
                    acueducto = consPorcentaje + strNomServicio + ":";
                }
                else {
                    acueducto = idioma.getString("TB_TB3253");
                }

            }

            strNomServicio = consultarParametro(
                            "NOMBRE SERVICIO A REMPLAZAR ALCANTARILLADO",
                            false);
            String parAlc = consultarParametro(
                            "CAMBIAR NOMBRE SERVICIO ALCANTARILLADO", true);

            if (strNomServicio == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1474"));
                rta = false;
            }
            else {
                if (parAlc == null) {
                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB1472"));
                    rta = false;
                }
                else if ("SI".equals(parAlc)) {
                    alcantarillado = consPorcentaje + strNomServicio + ":";
                }
                else {
                    alcantarillado = idioma.getString("TB_TB3254");
                }
            }

            strNomServicio = consultarParametro(
                            "NOMBRE SERVICIO A REMPLAZAR ASEO", false);
            String parAseo = consultarParametro("CAMBIAR NOMBRE SERVICIO ASEO",
                            true);

            if (strNomServicio == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1476"));
                rta = false;
            }
            else {
                if (parAseo == null) {
                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB1475"));
                    rta = false;
                }
                else if ("SI".equals(parAseo)) {
                    aseo = consPorcentaje + strNomServicio + ":";
                }
                else {
                    aseo = idioma.getString("TB_TB3255");
                }
            }

            strNomServicio = consultarParametro(
                            "NOMBRE SERVICIO A REMPLAZAR ALUMBRADO", false);
            String parAlu = consultarParametro(
                            "CAMBIAR NOMBRE SERVICIO ALUMBRADO", true);

            if (strNomServicio == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1544"));
                rta = false;
            }
            else {
                if (parAlu == null) {
                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB1545"));
                    rta = false;
                }
                else if ("SI".equals(parAlu)) {
                    alumbrado = consPorcentaje + strNomServicio + ":";
                }
                else {
                    alumbrado = idioma.getString("TB_TB3256");
                }
            }

        }
        catch (SystemException ex) {

            Logger.getLogger(TarifasincindexsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());

        }
        return rta;
    }

    public void mensajesInicioModal()
    {
        parametro();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public int getAno()
    {
        return ano;
    }

    public void setAno(int ano)
    {
        this.ano = ano;
    }

    public String getPeriodo()
    {
        return periodo;
    }

    public void setPeriodo(String periodo)
    {
        this.periodo = periodo;
    }

    public String getPorIndexAc()
    {
        return porIndexAc;
    }

    public void setPorIndexAc(String porIndexAc)
    {
        this.porIndexAc = porIndexAc;
    }

    public String getPorIndexAl()
    {
        return porIndexAl;
    }

    public void setPorIndexAl(String porIndexAl)
    {
        this.porIndexAl = porIndexAl;
    }

    public String getPorIndexAs()
    {
        return porIndexAs;
    }

    public void setPorIndexAs(String porIndexAs)
    {
        this.porIndexAs = porIndexAs;
    }

    public String getPorIndexAlu()
    {
        return porIndexAlu;
    }

    public void setPorIndexAlu(String porIndexAlu)
    {
        this.porIndexAlu = porIndexAlu;
    }

    public String getAcueducto()
    {
        return acueducto;
    }

    public void setAcueducto(String acueducto)
    {
        this.acueducto = acueducto;
    }

    public String getAlcantarillado()
    {
        return alcantarillado;
    }

    public void setAlcantarillado(String alcantarillado)
    {
        this.alcantarillado = alcantarillado;
    }

    public String getAseo()
    {
        return aseo;
    }

    public void setAseo(String aseo)
    {
        this.aseo = aseo;
    }

    public String getAlumbrado()
    {
        return alumbrado;
    }

    public void setAlumbrado(String alumbrado)
    {
        this.alumbrado = alumbrado;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }

    public List<Registro> getListaPeriodo()
    {
        return listaPeriodo;
    }

    public void setListaPeriodo(List<Registro> listaPeriodo)
    {
        this.listaPeriodo = listaPeriodo;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
