package com.sysman.util.rest;

import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Clase que permite
 * 
 * @version 1.0
 * @author Jhon Fredy Hernández Castro
 * @fecha 8/05/2018
 *
 */

/**
 * Metodos de encripcion y desencripcion
 * 
 * @author Maria Fernanda Ochoa
 *
 */
public class Cripto {
    /**
     * Objeto para conversion base64
     */
    private Base64 base64;
    /**
     * Variable para manejo de lo que se cifra
     */
    private String encodedVersion;
    /**
     * variable para decifrado
     */
    private String decodedVersion;

    /**
     * Constructor de la clase
     */
    public Cripto() {
        setBase64(new Base64());
    }

    /**
     * Método que permite generar un cifrado asimétrico Base64
     * 
     * @param cadena
     * @return
     * @throws Exception
     */
    public String encodeBase64(String cadena) throws Exception {
        setEncodedVersion(new String(base64.encode(cadena.getBytes())));
        return encodedVersion;
    }

    /**
     * Método que permite generar un des cifrado asimétrico Base64
     * 
     * @param cadena
     * @return
     * @throws Exception
     */
    public String decodeBAse64(String cadena) throws Exception {
        setDecodedVersion(new String(base64.decode(cadena.getBytes())));
        return decodedVersion;
    }

    /**
     * Método para codificar en algo
     * 
     * @param cadena
     * @return
     */
    public String encodeMd5(String cadena) {
        String digest = DigestUtils.md5Hex(cadena);
        return digest;
    }

    /**
     * @return the base64
     */
    public Base64 getBase64() {
        return base64;
    }

    /**
     * @param base64
     * the base64 to set
     */
    public void setBase64(Base64 base64) {
        this.base64 = base64;
    }

    /**
     * @return the encodedVersion
     */
    public String getEncodedVersion() {
        return encodedVersion;
    }

    /**
     * @param encodedVersion
     * the encodedVersion to set
     */
    public void setEncodedVersion(String encodedVersion) {
        this.encodedVersion = encodedVersion;
    }

    /**
     * @return the decodedVersion
     */
    public String getDecodedVersion() {
        return decodedVersion;
    }

    /**
     * @param decodedVersion
     * the decodedVersion to set
     */
    public void setDecodedVersion(String decodedVersion) {
        this.decodedVersion = decodedVersion;
    }

    /**
     * Metodo que se encarga de la encripcion con semilla de un String
     * 
     * @param texto
     * @return String conl cadena encriptada
     */
    public String Encriptar(String texto) {

        String secretKey = "SYSMANA"; // llave para encriptar datos
        String base64EncryptedString = "";

        try {

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digestOfPassword = md.digest(secretKey.getBytes("utf-8"));
            byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

            SecretKey key = new SecretKeySpec(keyBytes, "DESede");
            Cipher cipher = Cipher.getInstance("DESede");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] plainTextBytes = texto.getBytes("utf-8");
            byte[] buf = cipher.doFinal(plainTextBytes);
            byte[] base64Bytes = Base64.encodeBase64(buf);
            base64EncryptedString = new String(base64Bytes);

        } catch (Exception ex) {
        }
        return base64EncryptedString;
    }

    /**
     * Metodo que se encarga de la desencripcion con semilla de un
     * String
     * 
     * @param texto
     * @return String conl cadena desencriptada
     */
    public String Desencriptar(String textoEncriptado) throws Exception {

        String secretKey = "SYSMANA"; // llave para desenciptar datos
        String base64EncryptedString = "";

        try {
            byte[] message = Base64
                            .decodeBase64(textoEncriptado.getBytes("utf-8"));
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digestOfPassword = md.digest(secretKey.getBytes("utf-8"));
            byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
            SecretKey key = new SecretKeySpec(keyBytes, "DESede");

            Cipher decipher = Cipher.getInstance("DESede");
            decipher.init(Cipher.DECRYPT_MODE, key);

            byte[] plainText = decipher.doFinal(message);

            base64EncryptedString = new String(plainText, "UTF-8");

        } catch (Exception ex) {
        }
        return base64EncryptedString;
    }

}