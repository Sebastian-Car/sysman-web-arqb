package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.LmaestrousuariosControladorEnum;
import com.sysman.serviciospublicos.enums.LmaestrousuariosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 15/09/2016
 * 
 * @version 2, 07/06/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 */

@ManagedBean
@ViewScoped
public class LmaestrousuariosControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    /**
     * Constante que identifica el nombre del campo CODIGO
     */
    private final String campoCodigo;
    // <DECLARAR_ATRIBUTOS>
    private boolean indicador;
    private String ciclo;
    private String usosInicial;
    private String estratoInicial;
    private String usoFinal;
    private String estratoFinal;
    private String estado;
    private String ubicacion;
    private String descripcion;
    private String etiqueta;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaEstratoIni;
    private List<Registro> listaEstratoFin;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCiclo;
    private RegistroDataModelImpl listaUsoIni;
    private RegistroDataModelImpl listaUsoFin;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    /**
     * Creates a new instance of LmaestrousuariosControlador
     */
    public LmaestrousuariosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        campoCodigo = "CODIGO";
        try {
            numFormulario = GeneralCodigoFormaEnum.LMAESTROUSUARIOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(LmaestrousuariosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaEstratoIni();

        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCiclo();
        cargarListaUsoIni();

        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        mensajesInicioModal();
        estado = "A";
        ubicacion = "U";
        ciclo = "T";
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaEstratoIni() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaEstratoIni = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LmaestrousuariosControladorUrlEnum.URL4627
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaEstratoFin() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LmaestrousuariosControladorEnum.PARAM0.getValue(),
                        estratoInicial);

        try {
            listaEstratoFin = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LmaestrousuariosControladorUrlEnum.URL5176
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCiclo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LmaestrousuariosControladorUrlEnum.URL5889
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CICLO");
    }

    public void cargarListaUsoIni() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LmaestrousuariosControladorUrlEnum.URL6830
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaUsoIni = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, campoCodigo);
    }

    public void cargarListaUsoFin() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LmaestrousuariosControladorUrlEnum.URL7530
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LmaestrousuariosControladorEnum.PARAM1.getValue(),
                        usosInicial);

        listaUsoFin = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, campoCodigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>

    public void generarInforme(ReportesBean.FORMATOS formato) {
        try {
            String condicion;
            String parReporte;
            String ubi;
            String consulta;
            String conEstrato;
            String nomEstrato;
            if (("T").equals(ciclo)) {
                condicion = "";
            }
            else {
                condicion = "AND SP_USUARIO.CICLO = '" + ciclo + "'";
            }

            if (("T").equals(ubicacion)) {
                ubi = "";

            }
            else {

                ubi = "AND SP_USUARIO.NOTADEBITO = '" + ubicacion + "'";
            }
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ciclo", condicion);
            reemplazar.put("ubicacion", ubi);
            reemplazar.put("usoInicial", "'" + usosInicial + "'");
            reemplazar.put("usoFinal", "'" + usoFinal + "'");
            reemplazar.put("estratoInicial", "'" + estratoInicial + "'");
            reemplazar.put("estratoFinal", "'" + estratoFinal + "'");
            reemplazar.put("estado", "'" + estado + "'");

            HashMap<String, Object> parametro = new HashMap<>();
            parametro.put("PR_DESCRIPCION", descripcion.toUpperCase());
            parametro.put("PR_ENCABEZADO",
                            "Ciclo: " + ciclo + ". Entre Uso " + usosInicial
                                + " y " + usoFinal + ", Estrato "
                                + estratoInicial
                                + " y " + estratoFinal);

            if (("SI").equals(ejbSysmanUtilRemote.consultarParametro(compania,
                            "FORMATO CALIDAD", modulo, new Date(), true))) {
                if (indicador) {
                    parReporte = "001079MaestroUsuarioDetCOS";

                }
                else {
                    parReporte = "001081MaestroUsuarioCOS";

                }
                consulta = "001079MaestroUsuarioDetCOS";
            }
            else {
                if (indicador) {
                    parReporte = "001083MaestroUsuario";
                    nomEstrato = "ESTRATOS.NOMBRE  NOM_ESTRATOASEO,";
                    conEstrato = "INNER JOIN SP_ESTRATOS ESTRATOS \n" +
                        "    ON SP_USUARIO.COMPANIA = ESTRATOS.COMPANIA \n" +
                        "    AND SP_USUARIO.USO = ESTRATOS.USO \n" +
                        "    AND SP_USUARIO.ESTRATOASEO = ESTRATOS.CODIGO";
                }
                else {
                    parReporte = "001084MaestroUSuarioAc";
                    nomEstrato = "";
                    conEstrato = "";
                }

                reemplazar.put("nomEstrato", nomEstrato);
                reemplazar.put("conEstrato", conEstrato);
                consulta = "001083MaestroUsuario";
            }

            Reporteador.resuelveConsulta(consulta, Integer.parseInt(modulo),
                            reemplazar, parametro);
            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametro,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_INFORME_NO_EXISTE"), "<br>",
                            e.getMessage()));
        }
    }

    // <METODOS_CAMBIAR>
    public void cambiarEstratoIni() {
        estratoFinal = null;
        cargarListaEstratoFin();
    }

    public void mensajesInicioModal() {
        parametro();
    }

    public boolean parametro() {
        boolean rta;
        rta = true;

        try {
            String para = ejbSysmanUtilRemote.consultarParametro(compania,
                            "FORMATO CALIDAD", modulo, new Date(), true);
            if (para == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1558"));
                rta = false;
            }
            else {
                if (("SI").equals(para)) {
                    etiqueta = "Informe 2:";
                }
                else {
                    etiqueta = "Indicadores:";
                }
            }
        }
        catch (SystemException e) {

            Logger.getLogger(LmaestrousuariosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return rta;
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void onRowSelectCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ciclo = registroAux.getCampos().get("CICLO").toString();
    }

    public void onRowSelectUsoIni(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        usosInicial = registroAux.getCampos().get(campoCodigo).toString();
        usoFinal = null;
        cargarListaUsoFin();
    }

    public void onRowSelectUsoFin(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        usoFinal = registroAux.getCampos().get(campoCodigo).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public boolean getIndicador() {
        return indicador;
    }

    public void setIndicador(boolean indicador) {
        this.indicador = indicador;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getUsosInicial() {
        return usosInicial;
    }

    public void setUsosInicial(String usosInicial) {
        this.usosInicial = usosInicial;
    }

    public String getEstratoInicial() {
        return estratoInicial;
    }

    public void setEstratoInicial(String estratoInicial) {
        this.estratoInicial = estratoInicial;
    }

    public String getUsoFinal() {
        return usoFinal;
    }

    public void setUsoFinal(String usoFinal) {
        this.usoFinal = usoFinal;
    }

    public String getEstratoFinal() {
        return estratoFinal;
    }

    public void setEstratoFinal(String estratoFinal) {
        this.estratoFinal = estratoFinal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaEstratoIni() {
        return listaEstratoIni;
    }

    public void setListaEstratoIni(List<Registro> listaEstratoIni) {
        this.listaEstratoIni = listaEstratoIni;
    }

    public List<Registro> getListaEstratoFin() {
        return listaEstratoFin;
    }

    public void setListaEstratoFin(List<Registro> listaEstratoFin) {
        this.listaEstratoFin = listaEstratoFin;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListaCiclo() {
        return listaCiclo;
    }

    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    public RegistroDataModelImpl getListaUsoIni() {
        return listaUsoIni;
    }

    public void setListaUsoIni(RegistroDataModelImpl listaUsoIni) {
        this.listaUsoIni = listaUsoIni;
    }

    public RegistroDataModelImpl getListaUsoFin() {
        return listaUsoFin;
    }

    public void setListaUsoFin(RegistroDataModelImpl listaUsoFin) {
        this.listaUsoFin = listaUsoFin;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
