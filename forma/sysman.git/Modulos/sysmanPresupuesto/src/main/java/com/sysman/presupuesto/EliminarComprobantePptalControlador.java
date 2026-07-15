package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
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
import com.sysman.presupuesto.ejb.EjbPresupuestoCeroRemote;
import com.sysman.presupuesto.enums.EliminarComprobantePptalControladorEnum;
import com.sysman.presupuesto.enums.EliminarComprobantePptalControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalTime;
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
 * @author dmaldonado
 * @version 1, 13/06/2016
 * @modified jsforero
 * @version 2. 18/04/2017 Se realizo el refactory.
 * 
 * @author jlramirez
 * @version 3, 24/04/2017, Manejo de EJBs
 * 
 * @author eamaya
 * @version 4, 13/06/2017 Se cambio el llamado del codigo del
 * formulario
 * 
 * @author amonroy
 * @version 5, 15/06/2017 Se adiciona la verificacion del estado para
 * anio, mes y dia del comprobante que se desea eliminar. Se ajusta el
 * orden de las validaciones que se realizan al oprimir el boton de
 * "Eliminar Comprobante"
 * @version 6 18/07/2017 Se eliminan metodos que realizaban las
 * diferentes validaciones previas a la eliminacion del comprobante
 * debido a que se adicionaron al procedimiento
 * PCK_PRESUPUESTO.PR_ELIMINAR_COMPROBANTEPPTAL
 */
@ManagedBean
@ViewScoped
public class EliminarComprobantePptalControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String numero;
    private String tipo;
    private String ano;
    private String mes;
    private int dia;
    private String afectaciones;
    private boolean impreso;
    @EJB
    private EjbPresupuestoCeroRemote presupuestoCero;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaNumero;
    private RegistroDataModelImpl listaTipo;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of EliminarComprobantePptalControlador
     */
    public EliminarComprobantePptalControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.ELIMINAR_COMPROBANTE_PPTAL_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(
                            EliminarComprobantePptalControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipo();
        ano = Integer.toString(SysmanFunciones
                        .ano(new Date()));
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaNumero() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EliminarComprobantePptalControladorUrlEnum.URL3111
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), ano);
        param.put(EliminarComprobantePptalControladorEnum.TIPO.getValue(),
                        tipo);

        listaNumero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO");
    }

    public void cargarListaTipo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EliminarComprobantePptalControladorUrlEnum.URL4682
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);

        listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Realiza el llamado al procedimiento
     * PCK_PRESUPUESTO.PR_ELIMINAR_COMPROBANTEPPTAL, en donde se
     * definen las acciones necesarias para la eliminacion de un
     * comprobante presupuestal
     */
    public void oprimirIniciar() {
        // <CODIGO_DESARROLLADO> 
    	try {
	        String mensajeTiempo = " ";
	        // se consulta previamente el comprobante para tomar datos necesarios para la eliminacion en detalle_cpte_afect_ws
	        Map<String, Object>param = new TreeMap<>();
	        param.put(GeneralParameterEnum.KEY_COMPANIA.getName(), compania);
	        param.put(GeneralParameterEnum.KEY_ANO.getName(), ano);
	        param.put(GeneralParameterEnum.KEY_TIPO.getName(), tipo);
	        param.put(GeneralParameterEnum.KEY_NUMERO.getName(), numero);
	        // se consulta el comprobante para saber si pertencee a los creados desde el ws de acacias
	        List<Registro> auxFijo = RegistroConverter
	                .toListRegistro(
	                                requestManager.getList(
	                                                UrlServiceUtil.getInstance()
	                                                                .getUrlServiceByUrlByEnumID(
	                                                                                GenericUrlEnum.COMPROBANTE_PPTAL.getReadKey()
	                                                                                                )
	                                                                .getUrl(),
	                                               param));
	        
	        long minutos = 0 ;
	        long segundos = 0 ;
        
        	LocalTime tiempoInicial = LocalTime.now();
            presupuestoCero.eliminarComprobantePresupuestal(compania,
                            Integer.parseInt(ano),
                            tipo,
                            new BigInteger(numero),
                            Integer.valueOf(mes),
                            dia,
                            BigDecimal.valueOf(
                                            Double.parseDouble(afectaciones)),
                            SessionUtil.getUser().getCodigo(),
                            impreso);
            LocalTime tiempoFinal = LocalTime.now();
            Duration diferencia = Duration.between(tiempoInicial, tiempoFinal);            
            minutos = diferencia.toMinutes() % 60;
            segundos = diferencia.getSeconds() % 60;
            if(minutos == 0) {
	            mensajeTiempo = "Se ha eliminado exitosamente el comprobante tipo " + tipo + " numero " + numero + " en un tiempo de " + segundos + " segundos";
	        }else {
		        mensajeTiempo = "Se ha eliminado exitosamente el comprobante tipo " + tipo + " numero " + numero + " en un tiempo de " + minutos + " minutos " + segundos + " segundos";
	        }
	        // se toma el regsitro y se verifica si pertenece a un envio de webservice, para proceder a eliminarlo de la tabla detalle_cpte_afect_ws
	        for(Registro registro: auxFijo) {
	            if(registro.getCampos().get(GeneralParameterEnum.CREATED_BY.getName()).toString().equals("webservice")) {
		            try {
			        	// se verifica el usuario del comprobante y se elimina de la tabla detalle_cpte_afect_ws
			            int delete = 0;
				    	
				    	param = new TreeMap<>();
				        param.put(GeneralParameterEnum.KEY_COMPANIA.getName(), compania);
				        param.put(GeneralParameterEnum.KEY_ANO.getName(), ano);
				        param.put(GeneralParameterEnum.KEY_COMPROBANTE.getName(), numero);
				        param.put(GeneralParameterEnum.KEY_TIPO_CPTE.getName(), tipo);
				        			        
				        UrlBean urlBean = UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(EliminarComprobantePptalControladorUrlEnum.URL1914001.getValue());
			
					
						delete = requestManager.delete(urlBean.getUrl(), param);
						
						} catch (SystemException e) {
							Logger.getLogger(EliminarDetallePptalLoteControlador.class
				                    .getName()).log(Level.SEVERE, null, e);
							JsfUtil.agregarMensajeError(e.getMessage());
						}
				
			            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1473"));
			            JsfUtil.agregarMensajeInformativo(mensajeTiempo); 
			            numero = null;
			            cargarListaNumero();
		            }
	        	}
        	}
        catch (NumberFormatException | SystemException  e) {
            Logger.getLogger(
                            EliminarComprobantePptalControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        if ((ano != null) && !ano.isEmpty()) {
            cargarListaNumero();
            numero = null;
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaNumero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numero = registroAux.getCampos().get("NUMERO").toString();
        mes = registroAux.getCampos().get("MES").toString();
        impreso = Boolean.parseBoolean(
                        registroAux.getCampos().get("IMPRESO").toString());
        afectaciones = registroAux.getCampos()
                        .get("AFECTACIONES").toString();
        try {
            dia = SysmanFunciones.dia(SysmanFunciones.convertirAFecha(
                            registroAux.getCampos().get("FECHA").toString()));
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void seleccionarFilaTipo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipo = registroAux.getCampos().get("CODIGO").toString();
        cargarListaNumero();
        numero = null;
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
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

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getAfectaciones() {
        return afectaciones;
    }

    public void setAfectaciones(String afectaciones) {
        this.afectaciones = afectaciones;
    }

    public boolean isImpreso() {
        return impreso;
    }

    public void setImpreso(boolean impreso) {
        this.impreso = impreso;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
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
    // </SET_GET_LISTAS_COMBO_GRANDE>
}