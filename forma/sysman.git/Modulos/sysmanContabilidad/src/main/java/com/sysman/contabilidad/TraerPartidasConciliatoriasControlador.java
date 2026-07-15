package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.TraerPartidasConciliatoriasControladorEnum;
import com.sysman.contabilidad.enums.TraerPartidasConciliatoriasControladorUrlEnum;
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

import java.util.Calendar;
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

import org.primefaces.context.RequestContext;

/**
 * Formulario modal que permite traer partidas conciliatorias del mes
 * anterior, con el fin de facilitar el registro de partidas de meses
 * anteriores que a la fecha de proceso no han sido contabilizadas.
 *
 * @author jrodrigueza
 * @version 1, 12/04/2016
 * @modified jsforero
 * @version 2. 11/04/2017 Se realizo el refactory. Ademas se hicieron
 * las respectivas Correcciones del sonar.
 * 
 * @version 3.0, 13/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.
 */
@ManagedBean
@ViewScoped
public class TraerPartidasConciliatoriasControlador extends BeanBaseModal {
    private final String compania;
    private int mesOrigen;
    private int mesDestino;
    private int anoOrigen;
    private int anoDestino;
    private String codCuenta;
    private String cuenta;
    private List<Registro> listaAnoOrigen;
    private List<Registro> listaAnoDestino;
    private boolean bloqueado;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of
     * TraerPartidasConciliatoriasControlador
     */
    public TraerPartidasConciliatoriasControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 627
            numFormulario = GeneralCodigoFormaEnum.TRAER_PARTIDAS_CONCILIATORIAS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            Map<String, Object> parametros = SessionUtil.getFlash();
            if (parametros != null) {
                anoOrigen = Integer.parseInt(parametros.get("ano").toString());
                mesOrigen = Integer.parseInt(parametros.get("mes").toString());
                codCuenta = parametros.get("codCuenta").toString();
                cuenta = codCuenta + " " + parametros.get("nombreCuenta");
            }
        }
        catch (Exception ex) {
            Logger.getLogger(TraerPartidasConciliatoriasControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaAnoOrigen();
        cargarListaAnoDestino();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // Valores predeterminados
        try {
        anoDestino = anoOrigen;
        mesDestino = mesOrigen;
        
        String estado;
        
            estado = ejbSysmanUtil.verificarEstadoPeriodoMensual(
                            compania, anoOrigen, mesOrigen,
                            Integer.parseInt(SessionUtil.getModulo()), 2);
            bloqueado= "A".equals(estado);
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAnoOrigen() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        try {
            UrlBean urlList = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            TraerPartidasConciliatoriasControladorUrlEnum.URL3251
                                                            .getValue());
            listaAnoOrigen = RegistroConverter.toListRegistro(
                            requestManager.getList(urlList.getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(TraerPartidasConciliatoriasControlador.class
                            .getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAnoDestino() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);

        try {
            UrlBean urlList = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            TraerPartidasConciliatoriasControladorUrlEnum.URL3251
                                                            .getValue());
            listaAnoDestino = RegistroConverter.toListRegistro(
                            requestManager.getList(urlList.getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(TraerPartidasConciliatoriasControlador.class
                            .getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Trae las partidas conciliatorias del a�o y mes origen, para
     * insertarlas con el a�o y mes destino especificados.
     */
    public void oprimirRealizarProceso() {
        if (anoOrigen == anoDestino && mesOrigen == mesDestino) {

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB66"));
            return;
        }
        if (listaPartidas(anoOrigen, mesOrigen).isEmpty()) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB69"));
            return;
        }
        if (!listaPartidas(anoDestino, mesDestino).isEmpty()) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('DG54').show();");
        }
        else {
            registrarPartidas();
        }
    }

    /**
     * Genera el consecutivo para almacenar la partida conciliatoria.
     * El consecutivo que se genera depende solamente de la
     * compa��a.
     *
     * @return entero que representa el consecutivo
     */
    private long genConsecutivo() {
        try {
            return ejbSysmanUtil.generarSiguienteConsecutivo(
                            "PARTIDAS_CONCILIATORIAS",
                            "COMPANIA = ''" + compania + "''", "CONSECUTIVO");
        }
        catch (SystemException e) {

            Logger.getLogger(TraerPartidasConciliatoriasControlador.class
                            .getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return -1;
        }

    }

    /**
     * Trae los registros para la cuenta en el a�o y mes
     * especificado.
     *
     * @param ano
     * a�o
     * @param mes
     * mes de la partida
     * @return <code>List</code> con las partidas conciliatorias
     */
    private List<Registro> listaPartidas(int ano, int mes) {

        List<Registro> lista = null;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), ano);
        param.put(TraerPartidasConciliatoriasControladorEnum.ANO_D.getValue(),
                        anoDestino);
        param.put(TraerPartidasConciliatoriasControladorEnum.MES.getValue(),
                        mes);
        param.put(TraerPartidasConciliatoriasControladorEnum.MES_D.getValue(),
        		mesDestino);
        param.put(TraerPartidasConciliatoriasControladorEnum.CODCUENTA
                        .getValue(), codCuenta);
        try {
            UrlBean urlList = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            TraerPartidasConciliatoriasControladorUrlEnum.URL3650
                                                            .getValue());
            lista = RegistroConverter.toListRegistro(
                            requestManager.getList(urlList.getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(TraerPartidasConciliatoriasControlador.class
                            .getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return lista;
    }

    public void cambiarAnoOrigen() {
        // Metodo Generado por combos
    }

    public void cambiarAnoDestino() {
        // Metodo Generado por combos
    }

    /**
     * Se ejecuta cuando el usuario hace clic en el bot�n SI del
     * dialogo de confirmaci�n.
     */
    public void aceptarinputBox() {
        registrarPartidas();
    }

    /**
     * Inserta cada una de las partidas en el a�o y mes destino.
     */
    private void registrarPartidas() {
        List<Registro> partidas = listaPartidas(anoOrigen, mesOrigen);

        for (Registro partida : partidas) {
            try {
                Map<String, Object> campos = new HashMap<>();
                campos.put("COMPANIA", compania);
                campos.put("ANO", anoDestino);
                campos.put("CUENTA", partida.getCampos().get("CUENTA"));
                campos.put("CONSECUTIVO", genConsecutivo());
                campos.put("FECHA", partida.getCampos().get("FECHA"));
                campos.put("PARTIDA_DEBITO",
                                partida.getCampos().get("PARTIDA_DEBITO"));
                campos.put("PARTIDA_CREDITO",
                                partida.getCampos().get("PARTIDA_CREDITO"));
                campos.put("OBSERVACIONES",
                                partida.getCampos().get("OBSERVACIONES"));
                campos.put("MES_PARTIDA", mesDestino);
                campos.put("CREATED_BY", SessionUtil.getUser().getCodigo());
                campos.put("DATE_CREATED", Calendar.getInstance().getTime());

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                TraerPartidasConciliatoriasControladorUrlEnum.URL8252
                                                                .getValue());
                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                                campos);
            }
            catch (SystemException e) {
                Logger.getLogger(TraerPartidasConciliatoriasControlador.class
                                .getName()).log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB81"));
    }

    /*
     * Getters and Setters
     */

    public String getCuenta() {
        return cuenta;
    }

    public int getMesOrigen() {
        return mesOrigen;
    }

    public void setMesOrigen(int mesOrigen) {
        this.mesOrigen = mesOrigen;
    }

    public int getMesDestino() {
        return mesDestino;
    }

    public void setMesDestino(int mesDestino) {
        this.mesDestino = mesDestino;
    }

    public int getAnoOrigen() {
        return anoOrigen;
    }

    public void setAnoOrigen(int anoOrigen) {
        this.anoOrigen = anoOrigen;
    }

    public int getAnoDestino() {
        return anoDestino;
    }

    public void setAnoDestino(int anoDestino) {
        this.anoDestino = anoDestino;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public List<Registro> getListaAnoOrigen() {
        return listaAnoOrigen;
    }

    public void setListaAnoOrigen(List<Registro> listaAnoOrigen) {
        this.listaAnoOrigen = listaAnoOrigen;
    }

    public List<Registro> getListaAnoDestino() {
        return listaAnoDestino;
    }

    public void setListaAnoDestino(List<Registro> listaAnoDestino) {
        this.listaAnoDestino = listaAnoDestino;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }
    
    
}
