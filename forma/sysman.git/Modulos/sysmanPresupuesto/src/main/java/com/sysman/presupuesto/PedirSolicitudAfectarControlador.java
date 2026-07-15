package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.ejb.EjbPresupuestoDosRemote;
import com.sysman.presupuesto.enums.PedirSolicitudAfectarControladorEnum;
import com.sysman.presupuesto.enums.PedirSolicitudAfectarControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
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
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dmaldonado
 * @version 1, 01/07/2016
 * @version 2, 19/04/2017 jrodriguezr Se refactoriza el codigo SQL de
 * las listas para utilizar dss.
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped
public class PedirSolicitudAfectarControlador extends BeanBaseModal {
    private final String compania;
    /**
     * Constante que identifica el nombre del campo CODIGO
     */
    private final String campoCodigo;
    /**
     * Constante que identifica el nombre del campo CARGO
     */
    private final String campoCargo;
    /**
     * Constante que identifica el nombre del campo DEPENDENCIA
     */
    private final String campoDependencia;
    /**
     * Constante que identifica el nombre del campo TIPOT
     */
    private final String campoTipoT;
    /**
     * Constante que identifica el nombre del campo VALORSOLICITADO
     */
    private final String campoValSolicitado;

    private final String consNumero;
    /**
     * Constante que identifica el valor de la fecha actual SYSDATE
     */
    // <DECLARAR_ATRIBUTOS>
    private String cpteAfectado;
    private String tipoCpteAfect;
    private String tituloComprobante;
    private String destino;
    private boolean manejaBanco;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCmpteAfectado;
    private RegistroDataModelImpl listaTipoCpteAfect;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private String ano;
    private Date fechaComprobante;
    private String nombreComprobante;
    private String numeroComprobante;
    private Object descripcionComprobante;
    private String tipoComprobante;
    private String tipoT;
    private String claseT;
    private String novedad;
    private String dependencia;
    private String valorSolicitado;
    private String cargo;
    private String proyecto;
    private String terceroComprobante;

    private Map<String, Object> parametrosEntrada;

    private Map<String, Object> ridComp;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbPresupuestoDosRemote ejbPresupuestoDos;

    /**
     * Creates a new instance of PedirSolicitudAfectarControlador
     */
    @SuppressWarnings("unchecked")
    public PedirSolicitudAfectarControlador() {
        super();
        compania = SessionUtil.getCompania();
        campoCodigo = "CODIGO";
        campoCargo = "CARGO";
        campoDependencia = "DEPENDENCIA";
        campoTipoT = "TIPOT";
        campoValSolicitado = "VALORSOLICITADO";
        consNumero = "NUMERO_SOLICITUD";
        parametrosEntrada = SessionUtil.getFlash();
        try {
            numFormulario = GeneralCodigoFormaEnum.PEDIR_SOLICITUD_AFECTAR_CONTROLADOR
                            .getCodigo();
            if (parametrosEntrada != null) {

                ridComp = (Map<String, Object>) parametrosEntrada
                                .get("rid");
            }
            validarPermisos();
            cargarFlash();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(PedirSolicitudAfectarControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarlistaTipoCpteAfect();
        cargarlistaCmpteAfectado();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    public void cargarFlash() {
        Map<String, Object> parametrosEntrada;
        parametrosEntrada = SessionUtil.getFlash();
        if (parametrosEntrada != null) {
            ano = (String) parametrosEntrada.get("ano");
            tipoComprobante = (String) parametrosEntrada.get("tipoComprobante");
            numeroComprobante = (String) parametrosEntrada
                            .get("numeroComprobante");
            nombreComprobante = (String) parametrosEntrada
                            .get("nombreComprobante");
            fechaComprobante = (Date) parametrosEntrada
                            .get("fechaComprobante");
            descripcionComprobante = parametrosEntrada
                            .get("descripcionComprobante");
            terceroComprobante = parametrosEntrada
            .get("terceroComprobante").toString();
        }
        else {
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            tituloComprobante = nombreComprobante + " -  No. -  "
                + numeroComprobante;

            manejaBanco = "SI".equals(ejbSysmanUtil.consultarParametro(compania,
                            "MANEJA BANCO DE PROYECTOS",
                            SessionUtil.getModulo(), new Date(), true));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
	public void cargarlistaCmpteAfectado() {
		if ("F".equals(destino)) {

			try {
				if ("SI".equals(ejbSysmanUtil.consultarParametro(compania,
						"MANEJA TERCERO EN SOLICITUD DE DISPONIBILIDAD FUNCIONAMIENTO", SessionUtil.getModulo(),
						new Date(), true))) {
					UrlBean urlBean = UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(PedirSolicitudAfectarControladorUrlEnum.URL431012.getValue());
					Map<String, Object> param = new TreeMap<>();
					param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
					param.put(PedirSolicitudAfectarControladorEnum.TIPO.getValue(), tipoCpteAfect);
					param.put(PedirSolicitudAfectarControladorEnum.TERCERO.getValue(), terceroComprobante);
/*mrosero CC1543*/	param.put(PedirSolicitudAfectarControladorEnum.FECHAACTUAL.getValue(),
							SysmanFunciones.convertirAFechaCadena(fechaComprobante));

					listaCmpteAfectado = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(),
							param, true, consNumero);
				} else {

					UrlBean urlBean = UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(PedirSolicitudAfectarControladorUrlEnum.URL8572.getValue());
					Map<String, Object> param = new TreeMap<>();
					param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
					param.put(PedirSolicitudAfectarControladorEnum.TIPO.getValue(), tipoCpteAfect);
/*mrosero CC1543*/	param.put(PedirSolicitudAfectarControladorEnum.FECHAACTUAL.getValue(),
							SysmanFunciones.convertirAFechaCadena(fechaComprobante));

					listaCmpteAfectado = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(),
							param, true, consNumero);
				}
			} catch (SystemException | ParseException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}

		} else {
			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(PedirSolicitudAfectarControladorUrlEnum.URL5945.getValue());
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(PedirSolicitudAfectarControladorEnum.TIPO.getValue(), tipoCpteAfect);
			param.put(GeneralParameterEnum.ANO.getName(), ano);
			try {
				param.put(PedirSolicitudAfectarControladorEnum.FECHAACTUAL.getValue(),
						SysmanFunciones.convertirAFechaCadena(fechaComprobante));
			} catch (ParseException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
			listaCmpteAfectado = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
					true, consNumero);

		}

	}

    public void cargarlistaTipoCpteAfect() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PedirSolicitudAfectarControladorUrlEnum.URL9837
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaTipoCpteAfect = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, campoTipoT);

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new HashMap<>();
        param.put("rid", ridComp);
        SessionUtil.setFlash(param);
        RequestContext.getCurrentInstance()
                        .closeDialog("CICLO");
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        generaInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void generaInforme(FORMATOS formato) {
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("tipoT", tipoCpteAfect + "'/*");
            reemplazar.put("codigo", "*/ AND BPNOVEDADPROYECTO.CODIGO = "
                + cpteAfectado);

            parametros.put("PR_JEFE_BANCO_PROYECTOS",
                            SysmanFunciones.nvlStr(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "JEFE DE BANCO DE PROYECTOS",
                                                            Integer.toString(
                                                                            SysmanConstantes.MODULO_BANCOPROY),
                                                            new Date(), true),
                                            " "));
            parametros.put("PR_PROFESIONAL_UNIVERSITARIO_EGR_ESM",
                            SysmanFunciones.nvlStr(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "PROFESIONAL UNIVERSITARIO EGR ESM",
                                                            Integer.toString(
                                                                            SysmanConstantes.MODULO_BANCOPROY),
                                                            new Date(), true),
                                            " "));
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            Reporteador.resuelveConsulta("000175SolicitudCDP",
                            SysmanConstantes.MODULO_BANCOPROY, reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed("000175SolicitudCDP",
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | OutOfMemoryError | IOException | SystemException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        String rta = null;
        try {
            rta = ejbPresupuestoDos.afectarCptesDesdeSolicitudes(compania,
                            Integer.parseInt(ano),
                            fechaComprobante,
                            tipoT,
                            SessionUtil.getUser().getCodigo(), tipoCpteAfect,
                            new BigInteger(cpteAfectado), dependencia,
                            new BigInteger(novedad),
                            claseT,
                            valorSolicitado, valorSolicitado, cargo, proyecto,
                            descripcionComprobante.toString(), tipoComprobante,
                            new BigInteger(numeroComprobante),
                            SysmanFunciones.nvl(destino, "").toString());
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB874"));
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (rta != null) {
            try {
                archivoDescarga = JsfUtil.getArchivoDescarga(
                                JsfUtil.serializarPlano(rta),
                                "Inconsistencias.txt");
            }
            catch (JRException | IOException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarDestino() {
        tipoCpteAfect = null;
        cpteAfectado = null;
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCmpteAfectado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cpteAfectado = SysmanFunciones.nvl(
                        registroAux.getCampos().get(consNumero), "")
                        .toString();
        tipoT = SysmanFunciones
                        .nvl(registroAux.getCampos().get(campoTipoT), "0")
                        .toString();
        claseT = SysmanFunciones.nvl(registroAux.getCampos().get("CLASET"), "0")
                        .toString();
        novedad = SysmanFunciones
                        .nvl(registroAux.getCampos().get(campoCodigo), "0")
                        .toString();
        dependencia = SysmanFunciones
                        .nvl(registroAux.getCampos().get(campoDependencia), "0")
                        .toString();
        valorSolicitado = SysmanFunciones
                        .nvl(registroAux.getCampos().get(campoValSolicitado),
                                        "0")
                        .toString();
        cargo = SysmanFunciones
                        .nvl(registroAux.getCampos().get(campoCargo), "0")
                        .toString();
        proyecto = SysmanFunciones
                        .nvl(registroAux.getCampos().get("PROYECTO"), "0")
                        .toString();
    }

    public void seleccionarFilaTipoCpteAfect(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoCpteAfect = SysmanFunciones
                        .nvl(registroAux.getCampos().get(campoTipoT), "")
                        .toString();
        cargarlistaCmpteAfectado();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getCpteAfectado() {
        return cpteAfectado;
    }

    public void setCpteAfectado(String cpteAfectado) {
        this.cpteAfectado = cpteAfectado;
    }

    public String getTipoCpteAfect() {
        return tipoCpteAfect;
    }

    public void setTipoCpteAfect(String tipoCpteAfect) {
        this.tipoCpteAfect = tipoCpteAfect;
    }

    public String getTituloComprobante() {
        return tituloComprobante;
    }

    public void setTituloComprobante(String tituloComprobante) {
        this.tituloComprobante = tituloComprobante;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public boolean isManejaBanco() {
        return manejaBanco;
    }

    public void setManejaBanco(boolean manejaBanco) {
        this.manejaBanco = manejaBanco;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getlistaCmpteAfectado() {
        return listaCmpteAfectado;
    }

    public void setlistaCmpteAfectado(
        RegistroDataModelImpl listaCmpteAfectado) {
        this.listaCmpteAfectado = listaCmpteAfectado;
    }

    public RegistroDataModelImpl getlistaTipoCpteAfect() {
        return listaTipoCpteAfect;
    }

    public void setlistaTipoCpteAfect(
        RegistroDataModelImpl listaTipoCpteAfect) {
        this.listaTipoCpteAfect = listaTipoCpteAfect;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
