package at.fhtw.mtcg.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Hash {
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16; // Länge des Salzes in Bytes

    // Methode zum Generieren eines zufälligen Salzes
    private static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return salt;
    }

    // Methode zum Hashen eines Passworts mit Salz
    public static String hashPassword(String password) {
        try {
            byte[] salt = generateSalt();
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            digest.reset();
            digest.update(salt);
            byte[] hash = digest.digest(password.getBytes());

            // Das gesalzene Passwort und das Hash kombinieren
            byte[] saltedHash = new byte[salt.length + hash.length];
            System.arraycopy(salt, 0, saltedHash, 0, salt.length);
            System.arraycopy(hash, 0, saltedHash, salt.length, hash.length);

            // In einen hexadezimalen String umwandeln
            StringBuilder hexString = new StringBuilder();
            for (byte b : saltedHash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing-Algorithmus nicht verfügbar.", e);
        }
    }

    // Methode zum Überprüfen eines Passworts gegen einen gespeicherten Hash
    public boolean verifyPassword(String password, String hashedPassword) {
        try {
            byte[] saltedHash = new byte[hashedPassword.length() / 2];
            for (int i = 0; i < hashedPassword.length(); i += 2) {
                saltedHash[i / 2] = (byte) Integer.parseInt(hashedPassword.substring(i, i + 2), 16);
            }

            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(saltedHash, 0, salt, 0, SALT_LENGTH);

            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            digest.reset();
            digest.update(salt);
            byte[] hash = digest.digest(password.getBytes());

            // Passwort hashen und mit dem gespeicherten Hash vergleichen
            for (int i = 0; i < hash.length; i++) {
                if (hash[i] != saltedHash[i + SALT_LENGTH]) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
