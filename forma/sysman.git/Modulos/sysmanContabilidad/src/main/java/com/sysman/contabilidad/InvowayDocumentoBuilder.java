package com.sysman.contabilidad;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
	

import com.sysman.contabilidad.enums.ComprobantecntsControladorEnum;
import com.sysman.contabilidad.enums.ComprobantecntsControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.Proveedor;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.Cliente;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.Impuestos;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.Impuestos.Impuesto;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.Lineas;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.Lineas.Linea;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.DatosTotales;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.TotalesCop;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.CondicionesPago;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.CondicionesPago.CondicionPago;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.Retenciones;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.Retenciones.Retencion;

/**
 * Builder estático encargado de construir el objeto {@link Documento} que representa
 * el XML a enviar al Web Service de Invoway para documentos soporte y notas de ajuste.
 *
 * <p>Cada sección del documento electrónico está encapsulada en un método privado
 * independiente. El punto de entrada es {@link #construir}, que los orquesta todos.</p>
 */
public class InvowayDocumentoBuilder {
	
    protected static RequestManager requestManager  = new RequestManager();

	private static final Logger logger = LoggerFactory.getLogger(InvowayDocumentoBuilder.class);
	private static BigDecimal  totalImpuestos = BigDecimal.ZERO;
	
	 /**
     * Construye el objeto {@link Documento} completo listo para ser enviado a Invoway.
     * Orquesta la carga de todas las secciones del documento electrónico.
     *
     * @param factura               Datos principales del comprobante (cabecera).
     * @param productos             Lista de líneas/ítems del documento.
     * @param nitCompania           NIT de la compañía emisora.
     * @param numeroFactura         Consecutivo DIAN del documento.
     * @param impuestosRetenciones  Registro con los valores de retenciones del comprobante.
     * @param listaImpuestosTemp    Lista de retenciones detalladas (RetefFuente, ReteIVA, ReteICA).
     * @param tipoDocumento         Tipo de documento Invoway: {@code "CC"} para documento
     *                              soporte, {@code "NA"} para nota de ajuste.
     * @param subTipoDocumento      Subtipo: {@code "05"} para documento soporte,
     *                              {@code "95"} para nota de ajuste.
     * @param tipoOperacion         Tipo de operación, actualmente siempre {@code "10"}.
     * @param datosReferencia       Para notas de ajuste: registro con el número del documento
     *                              soporte afectado. {@code null} si es documento soporte.
     * @param compania              Código de la compañía.
     * @param fechaInicio           Fecha inicio del rango de búsqueda (contexto del envío masivo).
     * @param fechaFin              Fecha fin del rango de búsqueda.
     * @param tipoFactura           Tipo de comprobante(ej: {@code "DSE"}, {@code "NCR"}).
     * @return                      Objeto {@link Documento} con todas las secciones cargadas.
     */
    public static Documento construir(
            Registro factura,
            List<Registro> productos,
            String nitCompania,
            String numeroFactura,
            Registro impuestosRetenciones,
            List<Registro> listaImpuestosTemp,
            String tipoDocumento,
            String subTipoDocumento,
            String tipoOperacion,
            Registro datosReferencia,
            String compania,
            Date fechaInicio,
            Date fechaFin,
            String tipoFactura
            ) {


	        
        Documento documento = new Documento();

        String numeroComprobante = SysmanFunciones.toString(factura.getCampos().get("NUMEROFACTURA"));
        
        cargarDatosBasicos(documento, factura, numeroFactura, tipoDocumento, subTipoDocumento, tipoOperacion);
        cargarProveedor(documento, factura, nitCompania);
        cargarCliente(documento, factura);
        //cargarImpuestos(documento, compania, fechaInicio, fechaFin,numeroComprobante,tipoFactura);
        BigDecimal subtotalLineas = cargarLineas(documento, productos);
        BigDecimal totalRet = cargarRetenciones(documento, listaImpuestosTemp);
        cargarTotales(documento, subtotalLineas, totalRet);
        cargarFormaPago(documento, factura);
        cargarDocumentosReferenciados(documento, datosReferencia);

        return documento;
    }

    /**
     * Carga los datos básicos de identificación del documento: número, tipo, subtipo,
     * fecha, divisa y dirección de facturación de la compañía.
     *
     * <p>El número de documento se forma concatenando el prefijo DIAN más el consecutivo DIAN.
     * La divisa se mapea: código {@code "602"} = {@code "COP"}.</p>
     */
    private static void cargarDatosBasicos(Documento documento, Registro rs, String numeroFactura, String tipoDocumento, String subTipoDocumento,
            String tipoOperacion) {

    	Date fechaDocumento =  new Date();
    	
        documento.setNumeroDocumento(SysmanFunciones.toString(
                SysmanFunciones.nvl(rs.getCampos().get("PREFIJO"), ""))
                        + numeroFactura);

        documento.setTipoDocumento(tipoDocumento);
        documento.setSubtipoDocumento(subTipoDocumento);
        documento.setTipoOperacion(tipoOperacion);

        Object fechaObj = rs.getCampos().get("FECHAFACTURA");

        String fechaDoc = "";

        if (fechaObj != null) {
            Date fecha = (Date) fechaObj;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            fechaDoc = sdf.format(fecha);
        }

        documento.setFechaDocumento(fechaDoc);
        	
        documento.setDivisa(SysmanFunciones.toString(
                SysmanFunciones.nvl(rs.getCampos().get("TIPO_MONEDA"), ""))
        .equals("602") ? "COP" : "");

        documento.setDireccionFactura(SysmanFunciones.toString(
                SysmanFunciones.nvl(rs.getCampos().get("DIRECCIONCOMPANIA"), "")));
        
        documento.setDepartamentoFactura(SysmanFunciones.toString(
        	    SysmanFunciones.nvl(rs.getCampos().get("DEPTOCOMPANIA"), "")));

        documento.setDistritoFactura(SysmanFunciones.toString(
                SysmanFunciones.nvl(rs.getCampos().get("DEPTOCOMPANIA"), "")));

        documento.setCiudadFactura(SysmanFunciones.toString(
                SysmanFunciones.nvl(rs.getCampos().get("DEPTOCOMPANIA"), ""))
                        + SysmanFunciones.toString(SysmanFunciones.nvl(
                        		rs.getCampos().get("CIUDADCOMPANIA"), "")));

        documento.setPaisFactura("CO");
    }

    /**
     * Carga los datos del proveedor (compañía emisora) en el documento.
     * El ID del proveedor se forma como: {@code NIT-DigitoVerificacion}.
     */
    private static void cargarProveedor(Documento documento, Registro rs, String nitCompania) {

        Proveedor proveedor = new Proveedor();
        
        proveedor.setIdProveedor(nitCompania +"-"+ SysmanFunciones
                .nvl(rs.getCampos().get("DIGITOVERIFICACIONCOMPANIA"), "").toString()); 

        documento.setProveedor(proveedor);
    }

    /**
     * Carga los datos del cliente (tercero receptor) en el documento.
     *
     * <p>Reglas de negocio aplicadas:</p>
     * <ul>
     *   <li>Tipo de persona: {@code "N"} (natural) = {@code "2"}, jurídica = {@code "1"}.</li>
     *   <li>Código DANE de ciudad: si tiene menos de 5 dígitos, se concatena el código
     *       de departamento al inicio.</li>
     *   <li>País: si no viene informado, se asume {@code "CO"}.</li>
     *   <li>Responsabilidad RUT: régimen {@code "48"} = {@code "O-48"},
     *       cualquier otro = {@code "R-99-PN"}.</li>
     * </ul>
     */
    private static void cargarCliente(Documento documento, Registro rs) {

        Cliente cliente = new Cliente();

        String tipoPersona = SysmanFunciones
                .nvl(rs.getCampos().get("NATURALEZATERCERO"), "")
                .toString();

        cliente.setTipoPersonaCliente(tipoPersona.equals("N") ? "2" : "1");

        cliente.setIdCliente(
                SysmanFunciones.nvl(rs.getCampos().get("NUMTERCERO"), "").toString()
                + "-"
                + SysmanFunciones.nvl(rs.getCampos().get("DIGITOVERIFICACIONTERCERO"), "").toString());

        cliente.setTipoDocumentoIdCliente("31");

        cliente.setRegimenCliente("49");

        cliente.setRazonSocialCliente(
                SysmanFunciones.nvl(rs.getCampos().get("RAZONSOCIAL"), "").toString());

        if (tipoPersona.equals("N")) {
            cliente.setNombreCliente(
                    SysmanFunciones.nvl(rs.getCampos().get("NOMBRETERCERO"), "").toString());
        } else {
            cliente.setNombreCliente(
                    SysmanFunciones.nvl(rs.getCampos().get("RAZONSOCIAL"), "").toString());
        }
        cliente.setApellido1Cliente(tipoPersona.equals("N")?SysmanFunciones
                .nvl(rs.getCampos().get("NOMBRETERCERO"), "")
                .toString():SysmanFunciones
                .nvl(rs.getCampos().get("RAZONSOCIAL"), "")
                .toString());
        cliente.setApellido2Cliente(tipoPersona.equals("N")?SysmanFunciones
                .nvl(rs.getCampos().get("NOMBRETERCERO"), "")
                .toString():SysmanFunciones
                .nvl(rs.getCampos().get("RAZONSOCIAL"), "")
                .toString());

        cliente.setDireccionCliente(
                SysmanFunciones.nvl(rs.getCampos().get("DIRECCIONTERCERO"), "").toString());


        String depto = SysmanFunciones.nvl(rs.getCampos().get("DEPTOTERCERO"), "").toString();
        cliente.setDistritoCliente(depto);
        String ciudad = SysmanFunciones.nvl(rs.getCampos().get("CIUDADTERCERO"), "").toString();

        String ciudadDane = ciudad.length() >= 5 ? ciudad : (depto + ciudad);
        cliente.setCiudadCliente(ciudadDane);

        cliente.setDepartamentoCliente(depto);

        cliente.setCodigoPostalCliente(
                SysmanFunciones.nvl(rs.getCampos().get("CODIGOPOSTALTERCERO"), "").toString());

        String paisCliente = SysmanFunciones.toString(
                SysmanFunciones.nvl(rs.getCampos().get("PAISTERCERO"), ""));
        cliente.setPaisCliente(paisCliente.isEmpty() ? "CO" : paisCliente);

        cliente.setTelefonoCliente(
                SysmanFunciones.nvl(rs.getCampos().get("TELEFONOS"), "").toString());

        cliente.setEmailCliente(
                SysmanFunciones.nvl(rs.getCampos().get("EMAILTERCERO"), "").toString());

        String regimen = SysmanFunciones.nvl(rs.getCampos().get("REGIMENTERCERO"), "49").toString();
        String responsabilidad = regimen.equals("48") ? "O-48" : "R-99-PN";
        cliente.setResponsabilidadesRutCliente(responsabilidad);

//        cliente.setTributosCliente("ZZ");

        documento.setCliente(cliente);
    }

    private static void cargarImpuestos(
            Documento documento,
            String compania,
            Date fechaInicio,
            Date fechaFin,
            String numeroComprobante,
            String tipoFactura) {

    	try {
        SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ComprobantecntsControladorEnum.FECHAINI.getValue(), formatFecha.format(fechaInicio));
        param.put(ComprobantecntsControladorEnum.FECHAFIN.getValue(), formatFecha.format(fechaFin));
        param.put(ComprobantecntsControladorEnum.NUMEROFACTURA.getValue(), numeroComprobante);
        param.put(ComprobantecntsControladorEnum.TIPOCOBRO.getValue(), tipoFactura);

        Impuestos impuestosFin = new Impuestos();

        // Definicion de cada impuesto: URL, campBase, campPorcentaje, campValor, codigo
        List<String[]> configuracionImpuestos = new ArrayList<>();
        configuracionImpuestos.add(new String[]{ ComprobantecntsControladorUrlEnum.URL5768.getValue(), "SUMADEBASEIMPUESTOIVA",        "PORCIVA",    "SUMADEVALORIMPUESTO",     "01" }); // IVA
        configuracionImpuestos.add(new String[]{ ComprobantecntsControladorUrlEnum.URL5773.getValue(), "SUMADEBASEIMPUESTOINC",        "PORCENTAJE", "SUMADEIMPUESTO_INC",       "02" }); // INC
        configuracionImpuestos.add(new String[]{ ComprobantecntsControladorUrlEnum.URL5770.getValue(), "SUMADEBASEIMPUESTOICA",        "PORCENTAJE", "SUMADEVALOR_ICA",          "03" }); // ICA
        configuracionImpuestos.add(new String[]{ ComprobantecntsControladorUrlEnum.URL5771.getValue(), "SUMADEBASEIMPUESTORETEIVA",    "PORCENTAJE", "SUMADERETEIVA",            "05" }); // ReteIVA
        configuracionImpuestos.add(new String[]{ ComprobantecntsControladorUrlEnum.URL5769.getValue(), "SUMADEBASEIMPUESTORETEFUENTE", "PORCENTAJE", "SUMADEVALOR_RETEFUENTE",   "06" }); // RetefFuente
        configuracionImpuestos.add(new String[]{ ComprobantecntsControladorUrlEnum.URL5772.getValue(), "SUMADEBASEIMPUESTORETEICA",    "PORCENTAJE", "SUMADEIMPUESTO_INC",       "07" }); // ReteICA
        

        for (String[] config : configuracionImpuestos) {
            String urlEnum    = config[0];
            String campBase   = config[1];
            String campPorc   = config[2];
            String campValor  = config[3];
            String codImp     = config[4];

            Registro rs;
				rs = RegistroConverter.toRegistro(
				        requestManager.get(
				                UrlServiceUtil.getInstance()
				                        .getUrlServiceByUrlByEnumID(urlEnum)
				                        .getUrl(),
				                param));

            if (rs == null) continue;

            BigDecimal valor = new BigDecimal(
                    SysmanFunciones.nvl(rs.getCampos().get(campValor), "0").toString());

            if (valor.doubleValue() <= 0) continue;

            BigDecimal base = new BigDecimal(
                    SysmanFunciones.nvl(rs.getCampos().get(campBase), "0").toString());

            BigDecimal porcentaje = new BigDecimal(
                    SysmanFunciones.nvl(rs.getCampos().get(campPorc), "0").toString());

            Impuesto impuesto = new Impuesto();
            impuesto.setCodImpuesto(codImp);
            impuesto.setBaseImpuesto(base);
            impuesto.setPorcImpuesto(porcentaje);
            impuesto.setValorImpuesto(valor);

            impuestosFin.getImpuesto().add(impuesto);
            totalImpuestos = totalImpuestos.add(valor);
		}

		documento.setImpuestos(impuestosFin);
	} catch (SystemException e) {
		logger.error(e.getMessage(), e);
		JsfUtil.agregarMensajeError(e.getMessage());
	}
}
    /**
     * Carga las retenciones del documento (RetefFuente, ReteIVA, ReteICA).
     * Mapea el tipo de retención de Sysman al código Invoway:
     * {@code "IVA"} = {@code "05"}, {@code "ICA"} = {@code "07"},
     * {@code "FUE"/"RENTA"} = {@code "06"}, otros = {@code "ZZ"}.
     *
     * <p><b>Nota:</b> actualmente el resultado se calcula pero no se asigna al documento
     * ({@code documento.setRetenciones} está comentado). Solo se usa el total
     * para el cálculo de {@link #cargarTotales}.</p>
     *
     * @return Total acumulado de retenciones.
     */
		private static BigDecimal cargarRetenciones(Documento documento, List<Registro> listaImpuestosTemp) {
			BigDecimal totalRetenciones = BigDecimal.ZERO;
			Retenciones retencionesFin = new Retenciones();
		
			for (Registro r : listaImpuestosTemp) {
				BigDecimal valorRetencion = new BigDecimal(SysmanFunciones.nvl(r.getCampos().get("VALOR"), "0").toString());
		
				if (valorRetencion.compareTo(BigDecimal.ZERO) > 0) {
					Retencion retencion = new Retencion();
		
					retencion.setBaseRetencion(
							new BigDecimal(SysmanFunciones.nvl(r.getCampos().get("VALORBASE"), "0.0").toString()));
		
					retencion.setPorcRetencion(
							new BigDecimal(SysmanFunciones.nvl(r.getCampos().get("PCT_APLICAR"), "0.0").toString()));
		
					retencion.setValorRetencion(valorRetencion); 
		
					String tipoRet = SysmanFunciones.nvl(r.getCampos().get("TIPORETENCION"), "").toString();
					if (tipoRet.equals("IVA")) {
						retencion.setCodRetencion("05");
					} else if (tipoRet.equals("ICA")) {
						retencion.setCodRetencion("07");
					} else if (tipoRet.contains("FUE") || tipoRet.contains("RENTA")) {
						retencion.setCodRetencion("06");
					} else {
						retencion.setCodRetencion("ZZ");
					}
		
					retencionesFin.getRetencion().add(retencion);
					totalRetenciones = totalRetenciones.add(valorRetencion);
				}
			}
		
			if (!retencionesFin.getRetencion().isEmpty()) {
//				documento.setRetenciones(retencionesFin);
			}
		
			return totalRetenciones;
		}

		 /**
	     * Carga las líneas (ítems) del documento a partir de la lista de productos.
	     * Por cada producto calcula subtotal, descuentos e impuesto IVA si aplica.
	     *
	     * <p>El IVA por línea solo se agrega si el porcentaje es mayor a cero.
	     * Los descuentos solo se incluyen si su valor es mayor a cero.</p>
	     *
	     * @return Subtotal acumulado de todas las líneas, usado luego en {@link #cargarTotales}.
	     */
    private static BigDecimal cargarLineas(Documento documento, List<Registro> productos) {

        Lineas lineas = new Lineas();
        int cont = 1;
        double sumProdPorFactura = 0;
        BigDecimal subtotalTotal = BigDecimal.ZERO; 


        if (productos != null && !productos.isEmpty()) {

            for (Registro reg : productos) {

                Linea linea = new Linea();

                linea.setNumLinea(cont);
                linea.setIdEstandarReferencia("00" + cont);

                linea.setDescripcionItem(SysmanFunciones.toString(
                        SysmanFunciones.nvl(
                                reg.getCampos().get("DESCRIPCIONPRODUCTO"),
                                "")));

                linea.setUnidadMedida("NIU");

                linea.setUnidadesLinea(
                        new BigDecimal(SysmanFunciones.toString(
                                SysmanFunciones.nvl(
                                        reg.getCampos().get(GeneralParameterEnum.CANTIDAD.getName()),
                                        1))));

                linea.setPrecioUnidad(
                	    new BigDecimal(SysmanFunciones.toString(
                	        SysmanFunciones.nvl(reg.getCampos().get("TOTALITEM"), "0"))));

                BigDecimal subtotal = linea.getUnidadesLinea()
                        .multiply(linea.getPrecioUnidad());

                linea.setSubtotalLinea(
                	    new BigDecimal(SysmanFunciones.toString(
                	        SysmanFunciones.nvl(reg.getCampos().get("TOTALITEM"), "0"))));

                // -------- DESCUENTOS --------
                if (Double.parseDouble(SysmanFunciones.toString(
                        SysmanFunciones.nvl(
                                reg.getCampos().get("VALOR_DESCUENTO"),
                                0))) > 0) {

                    Double porcentajeDescuento = 0d;

                    porcentajeDescuento =
                            (Double.parseDouble(SysmanFunciones.toString(SysmanFunciones.nvl(
                                    reg.getCampos().get("VALOR_DESCUENTO"),
                                    0))) * 100)

                                    / Double.parseDouble(SysmanFunciones.toString(SysmanFunciones.nvl(
                                    reg.getCampos().get("TOTALITEM"),
                                    0)));

                    linea.setPorcDescuentoLinea(new BigDecimal(porcentajeDescuento));

                    linea.setDescuentoLinea(
                            new BigDecimal(SysmanFunciones.toString(
                                    SysmanFunciones.nvl(
                                            reg.getCampos().get("VALOR_DESCUENTO"),
                                            0))));

                } else {

                    linea.setPorcDescuentoLinea(new BigDecimal("0"));
                    linea.setDescuentoLinea(new BigDecimal("0"));
                }

                linea.setTotalLinea(
                	    new BigDecimal(SysmanFunciones.toString(
                	        SysmanFunciones.nvl(reg.getCampos().get("TOTALITEM"), "0"))));

                sumProdPorFactura += linea.getTotalLinea().doubleValue();

                // -------- IMPUESTO IVA --------
                if (Double.parseDouble(SysmanFunciones.toString(
                        SysmanFunciones.nvl(
                                reg.getCampos().get("VALORIMPUESTO"),
                                0))) > 0) {

                	BigDecimal porcImpuesto = new BigDecimal(SysmanFunciones.toString(
                		    SysmanFunciones.nvl(reg.getCampos().get("PORCENTAJEIVAX"), "0")));

                		BigDecimal valorImpuesto = new BigDecimal(SysmanFunciones.toString(
                		    SysmanFunciones.nvl(reg.getCampos().get("VALORIMPUESTO"), "0")));

                		if (porcImpuesto.compareTo(BigDecimal.ZERO) > 0) {
                		    linea.setCodImpuestoLinea("01");
                		    linea.setPorcImpuestoLinea(porcImpuesto);
                		    linea.setValorImpuestoLinea(valorImpuesto);
                		}
                }
//                } else {
//
//                    linea.setPorcImpuestoLinea(new BigDecimal("0").setScale(2));
//                    linea.setValorImpuestoLinea(new BigDecimal("0").setScale(2));
//                    linea.setCodImpuestoLinea("01");
//                }

                lineas.getLinea().add(linea);
                subtotalTotal = subtotalTotal.add(linea.getTotalLinea()); 
                cont++;
            }
        }

        documento.setLineas(lineas);
        return subtotalTotal;
    }

  
    /**
     * Calcula y carga los totales del documento: subtotal, impuestos, total documento
     * y monto a pagar.
     *
     * <p><b>Nota:</b> la sección {@code TotalesCop} está disponible pero actualmente
     * comentada. Si se requiere en el futuro, está implementada y lista para activarse.</p>
     */
	private static void cargarTotales(Documento documento, BigDecimal subtotal, BigDecimal totalRetenciones) {

		subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);
		BigDecimal impuestos = (totalImpuestos != null ? totalImpuestos : BigDecimal.ZERO).setScale(2,
				RoundingMode.HALF_UP);
		BigDecimal total = subtotal.add(impuestos).setScale(2, RoundingMode.HALF_UP);
		BigDecimal aPagar = total;

		// DatosTotales
		DatosTotales datosTotales = new DatosTotales();
		datosTotales.setSubtotal(subtotal);
		datosTotales.setTotalBase(subtotal);
		datosTotales.setTotalImpuestos(impuestos);
		datosTotales.setTotalDocumento(total);
		datosTotales.setTotalRetenciones(totalRetenciones);
		datosTotales.setAPagar(aPagar);
		documento.setDatosTotales(datosTotales);

// TotalesCop
		/*
		String cero = "0.00";
		TotalesCop totalesCop = new TotalesCop();
		totalesCop.setFctConvCop("1.00");
		totalesCop.setMonedaCop("COP");
		totalesCop.setSubtotalCop(subtotal.toString());
		totalesCop.setDescuentoDetalleCop(cero);
		totalesCop.setCargoDetalleCop(cero);
		totalesCop.setTotalBrutoCop(subtotal.toString());
		totalesCop.setTotalIvaCop(cero);
		totalesCop.setTotalIncCop(cero);
		totalesCop.setTotalBolsasCop(cero);
		totalesCop.setTotalOtroImpCop(cero);
		totalesCop.setMontoImpuestosCop(impuestos.toString());
		totalesCop.setTotalNetoCop(total.toString());
		totalesCop.setMontoDctoCop(cero);
		totalesCop.setMontoCargoCop(cero);
		totalesCop.setValorPagarCop(aPagar.toString());
		totalesCop.setReteFuenteCop(cero);
		totalesCop.setReteIvaCop(cero);
		totalesCop.setReteIcaCop(cero);
		totalesCop.setTotalAnticiposCop(cero);
		documento.setTotalesCop(totalesCop);
		*/
	}


    /**
     * Carga la forma y condición de pago del documento (medio de pago, tipo y fecha).
     * La fecha de pago toma la fecha actual si el campo {@code FECHA_PAGO} es nulo.
     */
    private static void cargarFormaPago(Documento documento, Registro rs) {
    	try {
        CondicionPago condicionPago = new CondicionPago();

    	Date fechaPago = (Date) SysmanFunciones.nvl(rs.getCampos().get("FECHA_PAGO"), new Date());

        condicionPago.setMedioPago(SysmanFunciones.toString(
                SysmanFunciones.nvl(rs.getCampos().get("MEDIOPAGO"), "")));

        condicionPago.setFormaPago(SysmanFunciones.toString(
                SysmanFunciones.nvl(rs.getCampos().get("TIPO_PAGO"), "")));
        
        condicionPago.setFechaPago(SysmanFunciones.convertirAFechaCadena(fechaPago, "yyyy-MM-dd HH:mm:ss"));

        CondicionesPago condicionesPago = new CondicionesPago();

        condicionesPago.getCondicionPago().add(condicionPago);

        documento.setCondicionesPago(condicionesPago);
	    } catch (ParseException e1) {
			logger.error(e1.getMessage(), e1);
			JsfUtil.agregarMensajeError(e1.getMessage());
		}
    }
    
    /**
     * Carga el documento soporte referenciado en una nota de ajuste.
     * Si {@code facturaRef} es {@code null}, no agrega referencias 
     */
    private static void cargarDocumentosReferenciados(
            Documento documento, 
            Registro facturaRef) {

        if (facturaRef == null) {
            return;
        }

        Documento.DocumentosReferenciados docsRef =
            new Documento.DocumentosReferenciados();

        Documento.DocumentosReferenciados.DocumentoReferenciado ref =
            new Documento.DocumentosReferenciados.DocumentoReferenciado();

        ref.setNumDocumentoRef(
            SysmanFunciones.nvlStr(SysmanFunciones.toString(facturaRef.getCampos().get("numDocumentoRef")), "")
        );


        docsRef.getDocumentoReferenciado().add(ref);

        documento.setDocumentosReferenciados(docsRef);
    }

}