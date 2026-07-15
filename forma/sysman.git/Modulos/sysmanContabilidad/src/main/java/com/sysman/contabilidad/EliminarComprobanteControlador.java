package com.sysman.contabilidad;

import com.google.gson.Gson;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadCincoRemote;
import com.sysman.contabilidad.enums.ComprobantecntsControladorUrlEnum;
import com.sysman.contabilidad.enums.EliminarComprobanteControladorEnum;
import com.sysman.contabilidad.enums.EliminarComprobanteControladorUrlEnum;
import com.sysman.contabilidad.enums.FrmestadodocsoporteControladorUrlEnum;
import com.sysman.contabilidad.enums.SubdetallecomprobantecntsControladorUrlEnum;
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.APIFrida;
import com.sysman.util.rest.RespuestaFormatoConsultas;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Base64;
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

import org.apache.jasper.tagplugins.jstl.core.ForEach;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author NGOMEZ
 * @version 1, 17/03/2016
 * @author yrojas
 * @version 2, 12/04/2017 Se cambiaron las consultas por la
 * invocaci�n de los DSS. Se cambio controlador segun
 * especificaciones del SonarLint.
 * @author yrojas
 * @version 3, 20/04/2017 Se cambiaron los llamados de Acciones por
 * las invocaciones de los ejb.
 * @author spina - refactorizo conexiones
 * @version 4, 12/06/2017
 * @author jcrodriguez, Depuracion y verificacion de la funcionalidad
 * del controlador
 * @version 5, 31/08/2017
 */
@ManagedBean
@ViewScoped
public class EliminarComprobanteControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    private String numero;
    private String tipo;
    private String anio;
    private String mes;
    private boolean impreso;
    private String debitosAfectados;
    private String creditosAfectados;
    private List<Registro> listaAno;
    private RegistroDataModelImpl listaNumero;
    private RegistroDataModelImpl listaTipo;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbContabilidadCincoRemote ejbContabilidadCinco;
    private String nitCompania;
    private String nitSinDigito;
    /**
     * Creates a new instance of EliminarComprobanteControlador
     */
    public EliminarComprobanteControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        nitCompania = SessionUtil.getCompaniaIngreso().getNit();
		if (nitCompania.toString().contains("-")) {
			nitSinDigito = nitCompania.toString().split("-")[0];
		} else {
			nitSinDigito = nitCompania;
		}
        try {
            numFormulario = GeneralCodigoFormaEnum.ELIMINAR_COMPROBANTE_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(EliminarComprobanteControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaAno();
        cargarListaTipo();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            EliminarComprobanteControladorUrlEnum.URL2595
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaNumero() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EliminarComprobanteControladorUrlEnum.URL2901
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(EliminarComprobanteControladorEnum.TIPO.getValue(), tipo);

        listaNumero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.NUMERO.getName());
    }

    public void cargarListaTipo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EliminarComprobanteControladorUrlEnum.URL3998
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    private boolean verificarComprobantePptalCnt(Map<String, Object> param,
        String url, String mensaje) {
        Registro regAux;
        try {
            regAux = RegistroConverter
                            .toRegistro(
                                            requestManager.get(url,
                                                            param));

            if (Integer.parseInt(SysmanFunciones.nvlStr(regAux.getCampos()
                            .get(GeneralParameterEnum.CUENTA.getName())
                            .toString(), "0")) > 0) {
                if (!SysmanFunciones.validarVariableVacio(mensaje)) {
                    JsfUtil.agregarMensajeAlerta(mensaje);
                }
                return false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;

    }

    private boolean validar(Map<String, Object> param) {
        if (!verificarComprobantePptalCnt(param, UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EliminarComprobanteControladorUrlEnum.URL1802
                                                        .getValue())
                        .getUrl(), idioma.getString("TB_TB492"))) {
            return false;
        }

        try {
            if (!verificarComprobantePptalCnt(param, UrlServiceUtil
                            .getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            EliminarComprobanteControladorUrlEnum.URL7652
                                                            .getValue())
                            .getUrl(), null)) {

                String estadoPeriodo = ejbSysmanUtil
                                .verificarEstadoPeriodoMensual(compania,
                                                Integer.parseInt(anio),
                                                Integer.parseInt(mes),
                                                Integer.parseInt(modulo), 1);
                if ("C".equals(estadoPeriodo)) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB493"));
                    return false;
                }
                else if (SysmanFunciones.validarVariableVacio(estadoPeriodo)) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB494"));
                    return false;
                }
            }
        }
        catch (NumberFormatException | SystemException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

        if (impreso) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB490"));
            return false;
        }

        if (Double.doubleToRawLongBits(
                        Double.parseDouble(debitosAfectados)) != 0
            || Double.doubleToRawLongBits(
                            Double.parseDouble(creditosAfectados)) != 0) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB491"));
            return false;
        }

        return true;
    }

    public void oprimirIniciar() {
        // <CODIGO_DESARROLLADO>
    	String mensajeTiempo = " ";
    	try {
	    	// se valida que el comprobante a eleminar no sea un documento soporte enviado EXITOSO
	    	Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.TIPOCPTE.getName(), tipo);
			// Se valida como primera opcion que sea un documento soporte o nota de ajuste enviado con exito
			List<Registro> aux = RegistroConverter.toListRegistro(requestManager.getList(
					UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									SubdetallecomprobantecntsControladorUrlEnum.URL1895011.getValue())
							.getUrl(),
					param));
	
			for (Registro aux1 : aux) {
				if (Integer.parseInt(SysmanFunciones.nvl(aux1.getCampos().get("EXISTE"), "").toString()) > 0) {
					String consecutivoDianTemp = String.valueOf(numero).split(anio)[1];
					Long consecDianTemp = Long.valueOf(consecutivoDianTemp);
					param = new TreeMap<>();
					param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
					param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), tipo);
					Registro regAux = RegistroConverter
							.toRegistro(
									requestManager.get(
											UrlServiceUtil.getInstance()
													.getUrlServiceByUrlByEnumID(
															ComprobantecntsControladorUrlEnum.URL15078.getValue())
													.getUrl(),
											param));
					
					if(consultarEstadoDocDian(regAux.getCampos().get(GeneralParameterEnum.CLASE.getName()).toString().equals("05")?consecDianTemp.toString():numero, regAux.getCampos().get(GeneralParameterEnum.CLASE.getName()).toString(), regAux.getCampos().get(GeneralParameterEnum.CLASE.getName()).toString().equals("05")?tipo:"NA")) {
						JsfUtil.agregarMensajeError(idioma.getString("MSG_DOCUMENTO_ENVIADO"));
						return;
					}
				}
			}
	    	// se conuslta la informacion del comprobante antes de inicial el proceso de eliminacion
	    	//para datos que se usaran en otra eliminacion en la tabla detalle_cpte_afect_ws
	    	// se reutilizan las mismas variables de la conuslta anterior "aux" y "param"
	    	param = new TreeMap<>();
	        param.put(GeneralParameterEnum.KEY_COMPANIA.getName(), compania);
	        param.put(GeneralParameterEnum.KEY_ANO.getName(), anio);
	        param.put(GeneralParameterEnum.KEY_TIPO.getName(), tipo);
	        param.put(GeneralParameterEnum.KEY_NUMERO.getName(), numero);
	        // se consulta el comprobante para saber si pertencee a los creados desde el ws de acacias
	        List<Registro> auxFijo = RegistroConverter
	                .toListRegistro(
	                                requestManager.getList(
	                                                UrlServiceUtil.getInstance()
	                                                                .getUrlServiceByUrlByEnumID(
	                                                                                GenericUrlEnum.COMPROBANTE_CNT.getReadKey()
	                                                                                                )
	                                                                .getUrl(),
	                                               param));
	        // termina conuslta del comprobante que se eliminara en detalle_cpte_afect_ws
	    	param = new TreeMap<>();
	        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	        param.put(GeneralParameterEnum.ANO.getName(), anio);
	        param.put(EliminarComprobanteControladorEnum.TIPO.getValue(), tipo);
	        param.put(GeneralParameterEnum.NUMERO.getName(), numero);
	        
	        
	        if (!validar(param)) {
	            return;
	        }
	
	        aux = null;
        
            aux = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            EliminarComprobanteControladorUrlEnum.URL5748
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        

	        for (Registro aux1 : aux) {
	
	            if (!SysmanFunciones.validarCampoVacio(aux1.getCampos(),
	                            EliminarComprobanteControladorEnum.TIPO_CPTE_AFECT
	                                            .getValue())
	                && !"0".equals(
	                                SysmanFunciones.nvl(aux1.getCampos().get(
	                                                EliminarComprobanteControladorEnum.CMPTE_AFECTADO
	                                                                .getValue()),
	                                                "0"))) {
	                try {
	                    ejbContabilidadCinco.actualizarDebitosCreditosAfectados(
	                                    compania, Integer.parseInt(anio),
	                                    aux1.getCampos().get(
	                                                    EliminarComprobanteControladorEnum.TIPO_CPTE_AFECT
	                                                                    .getValue())
	                                                    .toString(),
	                                    new BigInteger(aux1.getCampos().get(
	                                                    EliminarComprobanteControladorEnum.CMPTE_AFECTADO
	                                                                    .getValue())
	                                                    .toString()),
	                                    aux1.getCampos().get(
	                                                    EliminarComprobanteControladorEnum.VALOR_DEBITO
	                                                                    .getValue())
	                                                    .toString(),
	                                    aux1.getCampos().get(
	                                                    EliminarComprobanteControladorEnum.VALOR_CREDITO
	                                                                    .getValue())
	                                                    .toString(),
	                                    Integer.parseInt(aux1.getCampos().get(
	                                                    EliminarComprobanteControladorEnum.CONSECUTIVOAFECTADO
	                                                                    .getValue())
	                                                    .toString()));
	                }
	                catch (SystemException | NumberFormatException ex) {
	                    Logger.getLogger(EliminarComprobanteControlador.class
	                                    .getName()).log(Level.SEVERE, null, ex);
	                    JsfUtil.agregarMensajeError(
	                                    idioma.getString("MSM_TRANS_INTERRUMPIDA")
	                                        + " " + ex.getMessage());
	                }
	            }
	        }
	        
	        
	        long minutos = 0 ;
	        long segundos = 0 ;
	        try {
	        	LocalTime tiempoInicial = LocalTime.now();
	            ejbContabilidadCinco.eliminarComprobantesCNT(compania,
	                            Integer.parseInt(anio), 
	                            tipo,
	                            new BigInteger(numero),
	                            SessionUtil.getUser().getCodigo());
	            LocalTime tiempoFinal = LocalTime.now();
	            Duration diferencia = Duration.between(tiempoInicial, tiempoFinal);
	            
	            minutos = diferencia.toMinutes() % 60;
	            segundos = diferencia.getSeconds() % 60;
	            if(minutos == 0) {
		            mensajeTiempo = "Se ha eliminado exitosamente el comprobante tipo " + tipo + " numero " + numero + " en un tiempo de " + segundos + " segundos";
		            }else { 
			        mensajeTiempo = "Se ha eliminado exitosamente el comprobante tipo " + tipo + " numero " + numero + " en un tiempo de " + minutos + " minutos " + segundos + " segundos";
		            }       
	            }
	        catch (SystemException | NumberFormatException ex) {
	            Logger.getLogger(EliminarComprobanteControlador.class.getName())
	                            .log(Level.SEVERE, null, ex);
	            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
	                            idioma.getString("MSM_TRANS_INTERRUMPIDA"), " ",
	                            ex.getMessage(), " ",
	                            idioma.getString("TB_TB3549").replace("s$tipo$s",
	                                            tipo)));
	        }
	        
	        //Luego de eliminado de la contabilidad se procesde a eliminarse de la tabla 	            
	        for(Registro registro: auxFijo) {
	        	// se realiza la eliminacion del regsitro en la tabla detalle_cpte_afect_ws
	            if(registro.getCampos().get(GeneralParameterEnum.CREATED_BY.getName()).toString().equals("webservice")) {
	    	        // se elimina el detalle del comprobante pero de la tabla detalle_cpte_afect_ws
	    		    	try {
	    			    	int delete = 0;
	    			    	
	    			    	Map<String, Object> param2 = new TreeMap<>();
	    			        param2.put(GeneralParameterEnum.KEY_COMPANIA.getName(), compania);
	    			        param2.put(GeneralParameterEnum.KEY_ANO.getName(), anio);
	    			        param2.put(GeneralParameterEnum.KEY_COMPROBANTE.getName(), registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString());
	    			        param2.put(GeneralParameterEnum.KEY_TIPO_CPTE.getName(), registro.getCampos().get(GeneralParameterEnum.TIPO.getName()).toString());
	    			            			        
	    			        UrlBean urlBean = UrlServiceUtil.getInstance()
	    						.getUrlServiceByUrlByEnumID(EliminarComprobanteControladorUrlEnum.URL1914001.getValue());
	    		
	    				
	    					delete = requestManager.delete(urlBean.getUrl(), param2);
	    				} catch (SystemException e) {
	    					Logger.getLogger(SubdetallecomprobantecntsControlador.class
	    		                    .getName()).log(Level.SEVERE, null, e);
	    					JsfUtil.agregarMensajeError(e.getMessage());
	    				}
	    	    }
	        }
        
        }
        catch (SystemException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }
        JsfUtil.agregarMensajeInformativo(
                        idioma.getString("MSM_PROCESO_EJECUTADO"));
        JsfUtil.agregarMensajeInformativo(mensajeTiempo); 

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        numero = null;
        cargarListaNumero();
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaNumero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numero = !("").equals(registroAux.getCampos()
                        .get(GeneralParameterEnum.NUMERO.getName()))
                            ? (registroAux.getCampos()
                                            .get(GeneralParameterEnum.NUMERO
                                                            .getName())).toString()
                            : "";
        mes = (registroAux.getCampos().get(GeneralParameterEnum.MES.getName()))
                        .toString();
        impreso = Boolean.parseBoolean(registroAux.getCampos()
                        .get(GeneralParameterEnum.IMPRESO.getName())
                        .toString());
        debitosAfectados = SysmanFunciones.nvlStr(
                        registroAux.getCampos().get(
                                        EliminarComprobanteControladorEnum.DEBITOSAFECTADOS
                                                        .getValue())
                                        .toString(),
                        "0");
        creditosAfectados = SysmanFunciones.nvlStr(
                        registroAux.getCampos().get(
                                        EliminarComprobanteControladorEnum.CREDITOSAFECTADOS
                                                        .getValue())
                                        .toString(),
                        "0");
    }

    public void seleccionarFilaTipo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipo = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        " ").toString();
        numero = null;
        cargarListaNumero();
    }
    /**
	 * metodo por el cual se le realizara la consulta a los docmentos soporte y notas de ajuste
	 * @param numeroFactura
	 * @param clase
	 * @param prefijoFactura
	 * @return
	 */
	private boolean consultarEstadoDocDian(String numeroFactura, String clase, String prefijoFactura) {
	
		try {
			String url = ejbSysmanUtil.consultarParametro(compania, "URL SERVICIO REST", "69", new Date(), false);
	
			if (SysmanFunciones.validarVariableVacio(url)) {
				JsfUtil.agregarMensajeAlerta("Asegurese de configurar el parametro URL SERVICIO REST");
			} else {
	
				Registro rs = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												FrmestadodocsoporteControladorUrlEnum.URL9457.getValue())
										.getUrl(),
								null));
	
				if (rs != null) {
	
					File archivo = new File(rs.getCampos().get("RUTA_CERTIFICADO").toString());
	
					String nombreCertificado = archivo.getName();
	
					byte[] archivoBytes = Files.readAllBytes(archivo.toPath());
	
					String certificado = Base64.getEncoder().encodeToString(archivoBytes);
	
					String passCertificado = Base64.getEncoder()
							.encodeToString(rs.getCampos().get("CONTRA_CERTIFICADO").toString().getBytes());
	
					String respuesta;
					APIFrida api = new APIFrida();
	
					respuesta = api.postFormatoConsultas(url, nitSinDigito, clase, numeroFactura, prefijoFactura,
							nombreCertificado, certificado, passCertificado);
	
					Gson gson = new Gson();
	
					RespuestaFormatoConsultas respuestaApi = gson.fromJson(respuesta, RespuestaFormatoConsultas.class);
	
					if (respuestaApi.getCuerpo().isIsValid()) {
						return true;
					} else {
						return false;
					}
	
				} else {
					JsfUtil.agregarMensajeAlerta(
							"Asegurese de configurar el certificado en el formulario CONFIGURACION CERTIFICADO DIAN");
	
					return false;
	
				}
			}
	
		} catch (SystemException | IOException | com.sysman.util.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return true;
	
	}
    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public RegistroDataModelImpl getListaNumero() {
        return listaNumero;
    }

    public void setListaNumero(RegistroDataModelImpl listaNumero) {
        this.listaNumero = listaNumero;
    }

    public RegistroDataModelImpl getListaTipo() {
        return listaTipo;
    }

    public void setListaTipo(RegistroDataModelImpl listaTipo) {
        this.listaTipo = listaTipo;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public boolean isImpreso() {
        return impreso;
    }

    public void setImpreso(boolean impreso) {
        this.impreso = impreso;
    }

    public String getDebitosAfectados() {
        return debitosAfectados;
    }

    public void setDebitosAfectados(String debitosAfectados) {
        this.debitosAfectados = debitosAfectados;
    }

    public String getCreditosAfectados() {
        return creditosAfectados;
    }

    public void setCreditosAfectados(String creditosAfectados) {
        this.creditosAfectados = creditosAfectados;
    }

}
