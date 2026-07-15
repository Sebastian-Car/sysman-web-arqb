/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.services;

import com.sysman.controladores.SessionUtil;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;
import javax.naming.NamingException;

/**
 *
 * @author USUARIO
 */

public class ServidorCorreo {

    private final Properties properties;

    private Session session;
    private MimeMessage message;

    private int smtpHostPort;
    private String smtpHostName;
    private String smtpAuthUser;
    private String smtpAuthName;
    private String smtpAuthPwd;
    public Transport transport;

    public ServidorCorreo() {
        properties = new Properties();
    }

    public ServidorCorreo(int smtpHostPort,
        String smtpHostName, String smtpAuthUser, String smtpAuthName,
        String smtpAuthPwd) {
        this.smtpHostPort = smtpHostPort;
        this.smtpHostName = smtpHostName;
        this.smtpAuthUser = smtpAuthUser;
        this.smtpAuthName = smtpAuthName;
        this.smtpAuthPwd = smtpAuthPwd;
        properties = new Properties();
    }

    /**
     * Inicializar variables de conexi�n
     *
     * @throws SQLException
     * @throws NamingException
     */
    private void init() throws NamingException {
        properties.put("mail.smtp.host", smtpHostName);
        properties.put("mail.smtp.port", smtpHostPort);
        properties.put("mail.smtp.mail.sender", smtpAuthUser);
        properties.put("mail.smtp.user", smtpAuthUser);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        session = Session.getInstance(properties);
    }

    /**
     *
     * @param message
     * @throws NoSuchProviderException
     * @throws MessagingException
     */
    private void enviarTransporte()
                    throws MessagingException {
        Transport transpor;

        transpor = session.getTransport("smtp");
        transpor.connect(smtpAuthUser, smtpAuthPwd);
        transpor.sendMessage(message, message.getAllRecipients());
        transpor.close();
    }
    
    /**
	 * Autor:CPEREZ 
	 * @param message
	 * @throws NoSuchProviderException
	 * @throws MessagingException
	 */
	private void enviarTransporteMasivo(long contar )
			throws MessagingException {
		if(contar == 0) {//Si la sesión no está abierta la abre
			transport = session.getTransport("smtp");
			transport.connect(smtpAuthUser, smtpAuthPwd); 
		}

		transport.sendMessage(message, message.getAllRecipients()); //envia mensaje
		if(contar == 1) {//cierra la conexión 
			transport.close(); 
		}

	}
	
	/**
	 * Autor:CPEREZ Se crea el método para evitar la autentificación masiva cuando son muchos correos  
	 * @param message
	 * @throws NoSuchProviderException
	 * @throws MessagingException
	 */
	public void enviarTransporteMasivoC(long contarRe, long totalRegistros ) throws MessagingException {
		if((contarRe == totalRegistros) && contarRe  == 1) {//envío un solo correo
            enviarTransporte();
        }else if((contarRe != totalRegistros) && contarRe  != 1) {//correos intermedios para enviar
            enviarTransporteMasivo(2);
        }else if((contarRe == totalRegistros) && contarRe  != 1) {//el último registro del envío masivo
            enviarTransporteMasivo(1);
        }else if( (contarRe != totalRegistros) &&  contarRe == 1 ) {//el primer registro de envió masivo
            enviarTransporteMasivo(0);
        }

	}

    public void enviar(String direccion, String asunto, String mensaje)
                    throws MessagingException, SQLException, NamingException {
        init();
        message = new MimeMessage(session);
        message.setFrom(new InternetAddress(
                        smtpAuthName + " <" + smtpAuthUser + ">"));
        message.setRecipients(Message.RecipientType.BCC,
                        InternetAddress.parse(direccion));
        message.setSubject(asunto);
        message.setText(cargarEmail(mensaje), "utf-8", "html");
        message.saveChanges();
        enviarTransporte();
    }

    public boolean enviarFirmado(String direccion, String asunto,
        String mensaje)
                    throws MessagingException, IOException, SQLException,
                    NamingException {
        init();
        message = new MimeMessage(session);
        message.setFrom(new InternetAddress(
                        smtpAuthName + " <" + smtpAuthUser + ">"));
        message.setRecipients(Message.RecipientType.BCC,
                        InternetAddress.parse(direccion));
        message.setSubject(asunto);
        String texto = obtenerTextoPlantilla(
                        "com\\sysman\\services\\plantillaCorreo.xhtml");
        texto = texto.replace("#CONTENIDO#", cargarEmail(mensaje));
        texto = texto.replace("#COMPANIA#",
                        SessionUtil.getCompaniaIngreso().getNombre());
        message.setText(texto, "utf-8", "html");
        message.saveChanges();
        enviarTransporte();
        return true;
    }

    public void enviar(String[] direcciones, String asunto, String mensaje)
                    throws MessagingException, SQLException, NamingException {
        init();
        message = new MimeMessage(session);
        String correos = Arrays.toString(direcciones);
        correos = correos.substring(1, correos.length() - 1);
        message.addRecipients(Message.RecipientType.BCC,
                        InternetAddress.parse(correos));
        message.setSubject(asunto);
        message.setText(cargarEmail(mensaje), "utf-8", "html");
        message.saveChanges();
        enviarTransporte();
    }

    public void enviarFirmado(String[] direcciones, String asunto,
        String mensaje)
                    throws MessagingException, IOException, SQLException,
                    NamingException {

        init();
        message = new MimeMessage(session);
        String correos = Arrays.toString(direcciones);
        correos = correos.substring(1, correos.length() - 1);
        message.addRecipients(Message.RecipientType.BCC,
                        InternetAddress.parse(correos));
        message.setSubject(asunto);
        String texto = obtenerTextoPlantilla(
                        "com\\sysman\\services\\plantillaCorreo.xhtml");
        texto = texto.replace("#CONTENIDO#", cargarEmail(mensaje));
        texto = texto.replace("#COMPANIA#",
                        SessionUtil.getCompaniaIngreso().getNombre());
        message.setText(texto, "utf-8", "html");
        message.saveChanges();
        enviarTransporte();

    }

    public void enviarAdjunto(String direccion, String asunto, String mensaje,
        String[] nombres, InputStream[] entradas,
        String[] tipos) throws MessagingException, IOException, SQLException,
                    NamingException {

        init();
        message = new MimeMessage(session);
        message.setFrom(new InternetAddress(
                        smtpAuthName + " <" + smtpAuthUser + ">"));
        message.setRecipients(Message.RecipientType.BCC,
                        InternetAddress.parse(direccion));
        message.setSubject(asunto);

        Multipart mp = new MimeMultipart();
        // Texto del mensaje
        BodyPart texto = new MimeBodyPart();
        texto.setContent(mensaje, "text/html; charset=UTF-8");
        mp.addBodyPart(texto);
        // Adjuntar del jasper.

        BodyPart adjunto;
        ByteArrayDataSource bads;
        for (int i = 0; i < nombres.length; i++) {
            adjunto = new MimeBodyPart();
            bads = new ByteArrayDataSource(entradas[i], tipos[i]);
            adjunto.setDataHandler(new DataHandler(bads));
            adjunto.setFileName(nombres[i]);
            mp.addBodyPart(adjunto);
        }
        message.setContent(mp);
        message.saveChanges();
        enviarTransporte();
    }

    public void enviarFirmadoAdjunto(String direccion, String asunto,
        String mensaje, String[] nombres,
        InputStream[] entradas, String[] tipos)
                    throws MessagingException, IOException, SQLException,
                    NamingException {

        init();
        message = new MimeMessage(session);
        message.setFrom(new InternetAddress(
                        smtpAuthName + " <" + smtpAuthUser + ">"));
        message.setRecipients(Message.RecipientType.BCC,
                        InternetAddress.parse(direccion));
        message.setSubject(asunto);

        String contenido = obtenerTextoPlantilla(
                        "com\\sysman\\services\\plantillaCorreo.xhtml");
        contenido = contenido.replace("#CONTENIDO#", cargarEmail(mensaje));
        contenido = contenido.replace("#COMPANIA#",
                        SessionUtil.getCompaniaIngreso().getNombre());

        Multipart mp = new MimeMultipart();
        // Texto del mensaje
        BodyPart texto = new MimeBodyPart();

        texto.setContent(contenido, "text/html; charset=UTF-8");
        mp.addBodyPart(texto);
        // Adjuntar del jasper.

        BodyPart adjunto;
        ByteArrayDataSource bads;
        for (int i = 0; i < nombres.length; i++) {
            adjunto = new MimeBodyPart();
            bads = new ByteArrayDataSource(entradas[i], tipos[i]);
            adjunto.setDataHandler(new DataHandler(bads));
            adjunto.setFileName(nombres[i]);
            mp.addBodyPart(adjunto);
        }
        message.setContent(mp);
        message.saveChanges();
        enviarTransporte();
    }

    public void enviarAdjunto(String[] direcciones, String asunto,
        String mensaje, String[] nombres,
        InputStream[] entradas, String[] tipos)
                    throws MessagingException, IOException, SQLException,
                    NamingException {

        init();
        message = new MimeMessage(session);
        message.setFrom(new InternetAddress(
                        smtpAuthName + " <" + smtpAuthUser + ">"));
        String correos = Arrays.toString(direcciones);
        correos = correos.substring(1, correos.length() - 1);
        message.setRecipients(Message.RecipientType.BCC,
                        InternetAddress.parse(correos));
        message.setSubject(asunto);

        Multipart mp = new MimeMultipart();
        // Texto del mensaje
        BodyPart texto = new MimeBodyPart();
        texto.setContent(mensaje, "text/html; charset=UTF-8");
        mp.addBodyPart(texto);
        // Adjuntar del jasper.

        BodyPart adjunto;
        ByteArrayDataSource bads;
        for (int i = 0; i < nombres.length; i++) {
            adjunto = new MimeBodyPart();
            bads = new ByteArrayDataSource(entradas[i], tipos[i]);
            adjunto.setDataHandler(new DataHandler(bads));
            adjunto.setFileName(nombres[i]);
            mp.addBodyPart(adjunto);
        }
        message.setContent(mp);
        message.saveChanges();

        enviarTransporte();
    }

    public void enviarFirmadoAdjunto(String[] direcciones, String asunto,
        String mensaje, String[] nombres,
        InputStream[] entradas, String[] tipos)
                    throws MessagingException, IOException, SQLException,
                    NamingException {

        init();
        message = new MimeMessage(session);
        message.setFrom(new InternetAddress(
                        smtpAuthName + " <" + smtpAuthUser + ">"));
        String correos = Arrays.toString(direcciones);
        correos = correos.substring(1, correos.length() - 1);
        message.setRecipients(Message.RecipientType.BCC,
                        InternetAddress.parse(correos));
        message.setSubject(asunto);

        String contenido = obtenerTextoPlantilla(
                        "com\\sysman\\services\\plantillaCorreo.xhtml");
        contenido = contenido.replace("#CONTENIDO#", cargarEmail(mensaje));
        contenido = contenido.replace("#COMPANIA#",
                        SessionUtil.getCompaniaIngreso().getNombre());

        Multipart mp = new MimeMultipart();
        // Texto del mensaje
        BodyPart texto = new MimeBodyPart();
        texto.setContent(contenido, "text/html; charset=UTF-8");
        mp.addBodyPart(texto);
        // Adjuntar del jasper.

        BodyPart adjunto;
        ByteArrayDataSource bads;
        for (int i = 0; i < nombres.length; i++) {
            adjunto = new MimeBodyPart();
            bads = new ByteArrayDataSource(entradas[i], tipos[i]);
            adjunto.setDataHandler(new DataHandler(bads));
            adjunto.setFileName(nombres[i]);
            mp.addBodyPart(adjunto);
        }
        message.setContent(mp);
        message.saveChanges();

        enviarTransporte();
    }

    public void enviarAdjunto(String direccion, String asunto, String mensaje,
        String nombre, InputStream entrada,
        String tipo) throws SysmanException {

        try {
            init();
            message = new MimeMessage(session);
            message.setFrom(new InternetAddress(
                            smtpAuthName + " <" + smtpAuthUser + ">"));

            message.setRecipients(Message.RecipientType.TO,
                            InternetAddress.parse(direccion));
            message.setSubject(MimeUtility.encodeText(asunto, "utf-8", "B"));

            Multipart mp = new MimeMultipart();
            // Texto del mensaje
            BodyPart texto = new MimeBodyPart();
            texto.setContent(mensaje, "text/html; charset=UTF-8");
            mp.addBodyPart(texto);
            // Adjuntar del jasper.

            BodyPart adjunto = new MimeBodyPart();
            ByteArrayDataSource bads = new ByteArrayDataSource(entrada, tipo);
            adjunto.setDataHandler(new DataHandler(bads));
            adjunto.setFileName(nombre);
            mp.addBodyPart(adjunto);

            message.setContent(mp);
            message.saveChanges();

            enviarTransporte();
        }
        catch (MessagingException | IOException | NamingException e) {
            throw new SysmanException(e, e.getMessage());
        }
    }

    public void enviarFirmadoAdjunto(String direccion, String asunto,
        String mensaje, String nombre,
        InputStream entrada, String tipo) throws MessagingException,
                    IOException, SQLException, NamingException {

        init();
        message = new MimeMessage(session);
        message.setFrom(new InternetAddress(
                        smtpAuthName + " <" + smtpAuthUser + ">"));
        message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(direccion));
        message.setSubject(asunto);

        String contenido = obtenerTextoPlantilla(
                        "com\\sysman\\services\\plantillaCorreo.xhtml");
        contenido = contenido.replace("#CONTENIDO#", cargarEmail(mensaje));
        contenido = contenido.replace("#COMPANIA#",
                        SessionUtil.getCompaniaIngreso().getNombre());

        Multipart mp = new MimeMultipart();
        // Texto del mensaje
        BodyPart texto = new MimeBodyPart();
        texto.setContent(contenido, "text/html; charset=UTF-8");
        mp.addBodyPart(texto);
        // Adjuntar del jasper.

        BodyPart adjunto = new MimeBodyPart();
        ByteArrayDataSource bads = new ByteArrayDataSource(entrada, tipo);
        adjunto.setDataHandler(new DataHandler(bads));
        adjunto.setFileName(nombre);
        mp.addBodyPart(adjunto);

        message.setContent(mp);
        message.saveChanges();

        enviarTransporte();
    }

    public void enviarAdjunto(String[] direcciones, String asunto,
        String mensaje, String nombre, InputStream entrada,
        String tipo) throws MessagingException, IOException, SQLException,
                    NamingException {

        init();
        message = new MimeMessage(session);
        message.setFrom(new InternetAddress(
                        smtpAuthName + " <" + smtpAuthUser + ">"));
        String correos = Arrays.toString(direcciones);
        correos = correos.substring(1, correos.length() - 1);
        message.setRecipients(Message.RecipientType.BCC,
                        InternetAddress.parse(correos));
        message.setSubject(asunto);

        Multipart mp = new MimeMultipart();
        // Texto del mensaje
        BodyPart texto = new MimeBodyPart();
        texto.setContent(mensaje, "text/html; charset=UTF-8");
        mp.addBodyPart(texto);
        // Adjuntar del jasper.
        BodyPart adjunto = new MimeBodyPart();
        ByteArrayDataSource bads = new ByteArrayDataSource(entrada, tipo);
        adjunto.setDataHandler(new DataHandler(bads));
        adjunto.setFileName(nombre);
        mp.addBodyPart(adjunto);

        message.setContent(mp);
        message.saveChanges();

        enviarTransporte();
    }

    public void enviarFirmadoAdjunto(String[] direcciones, String asunto,
        String mensaje, String nombre,
        InputStream entrada, String tipo) throws MessagingException,
                    IOException, SQLException, NamingException {

        init();
        message = new MimeMessage(session);
        message.setFrom(new InternetAddress(
                        smtpAuthName + " <" + smtpAuthUser + ">"));
        String correos = Arrays.toString(direcciones);
        correos = correos.substring(1, correos.length() - 1);
        message.setRecipients(Message.RecipientType.BCC,
                        InternetAddress.parse(correos));
        message.setSubject(asunto);

        String contenido = obtenerTextoPlantilla(
                        "com\\sysman\\services\\plantillaCorreo.xhtml");
        contenido = contenido.replace("#CONTENIDO#", cargarEmail(mensaje));
        contenido = contenido.replace("#COMPANIA#",
                        SessionUtil.getCompaniaIngreso().getNombre());

        Multipart mp = new MimeMultipart();
        // Texto del mensaje
        BodyPart texto = new MimeBodyPart();
        texto.setContent(contenido, "text/html; charset=UTF-8");
        mp.addBodyPart(texto);
        // Adjuntar del jasper.
        BodyPart adjunto = new MimeBodyPart();
        ByteArrayDataSource bads = new ByteArrayDataSource(entrada, tipo);
        adjunto.setDataHandler(new DataHandler(bads));
        adjunto.setFileName(nombre);
        mp.addBodyPart(adjunto);

        message.setContent(mp);
        message.saveChanges();

        enviarTransporte();
    }

    /**
     * Traer el texto desde la plantilla base a una cadena
     *
     * @param plantilla
     * @return
     * @throws IOException
     */
    private String obtenerTextoPlantilla(String plantilla) throws IOException {
        InputStream is = getClass().getClassLoader()
                        .getResourceAsStream(plantilla);
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String linea;
        while ((linea = br.readLine()) != null) {
            sb.append(linea);
        }
        return sb.toString();
    }

    public String cargarEmail(String msm) {
        return msm;
    }

    /**
     * @param smtpHostName
     * the smtpHostName to set
     */
    public final void setSmtpHostName(String smtpHostName) {
        this.smtpHostName = smtpHostName;
    }

    /**
     * @return
     */
    public String getSmtpHostName() {
        return smtpHostName;
    }

    /**
     * @param smtpHostPort
     * the smtpHostPort to set
     */
    public final void setSmtpHostPort(int smtpHostPort) {
        this.smtpHostPort = smtpHostPort;
    }

    /**
     * @param smtpAuthUser
     * the smtpAuthUser to set
     */
    public final void setSmtpAuthUser(String smtpAuthUser) {
        this.smtpAuthUser = smtpAuthUser;
    }

    /**
     * @return
     */
    public String getSmtpAuthUser() {
        return smtpAuthUser;
    }

    /**
     * @param smtpAuthPwd
     * the smtpAuthPwd to set
     */
    public final void setSmtpAuthPwd(String smtpAuthPwd) {
        this.smtpAuthPwd = smtpAuthPwd;
    }

    /**
     * @param smtpAuthName
     * the smtpAuthName to set
     */
    public final void setSmtpAuthName(String smtpAuthName) {
        this.smtpAuthName = smtpAuthName;
    }
}
