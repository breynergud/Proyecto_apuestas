package util;

public class HashUtil {
    
    /**
     * Cifra una contraseña en texto plano utilizando el algoritmo BCrypt.
     * @param password Contraseña en texto plano
     * @return Hash BCrypt
     */
    public static String hashPassword(String password) {
        if (password == null) {
            return null;
        }
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Verifica si una contraseña en texto plano coincide con su hash almacenado en formato BCrypt.
     * @param plainPassword Contraseña en texto plano
     * @param storedHash Hash BCrypt almacenado
     * @return true si coincide, false en caso contrario
     */
    public static boolean checkPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, storedHash);
        } catch (Exception e) {
            return false;
        }
    }
}
