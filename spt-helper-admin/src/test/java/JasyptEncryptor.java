import cn.hy.common.utils.StringUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class JasyptEncryptor {
    public static void main(String[] args) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();

        String password = StringUtils.reverse("RPDMJJFMOPWLXWSI");
        System.out.println("password: " + password);

        encryptor.setPassword(password);
        encryptor.setAlgorithm("PBEWithMD5AndDES");

        System.out.println("db user: " + encryptor.encrypt("shs-adm"));
        System.out.println("db pwd: " + encryptor.encrypt("jGubYzQ2rpcUXP4sBhEYWzcb2fJJUp4Y"));
        System.out.println("druid user: " + encryptor.encrypt("singleDog"));
        System.out.println("druid pwd: " + encryptor.encrypt("TVCn3GGQ5Z2ZbtPwxJ5xASFtnNjSmXYu"));
        System.out.println("redis pwd: " + encryptor.encrypt("ePKznPKHdCY8GaSSfY3FxUWfRp5QecHn"));
        System.out.println("token secret: " + encryptor.encrypt("zZYCWGteHRDt68mRWYH26a6xwd8R4HYn"));
    }
}
