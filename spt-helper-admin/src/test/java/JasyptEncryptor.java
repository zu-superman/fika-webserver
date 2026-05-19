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
        System.out.println("db pwd: " + encryptor.encrypt("Y5huY8xcsvQmuc2P33UYP3nS8Sun8uER"));
        System.out.println("druid user: " + encryptor.encrypt("singleDog"));
        System.out.println("druid pwd: " + encryptor.encrypt("RR4EPEBmVZEZHwhsf2MU7BTnRAns3G3N"));
        System.out.println("redis pwd: " + encryptor.encrypt("D060VmIBwG"));
        System.out.println("token secret: " + encryptor.encrypt("fnKMSUmQPzYjcYfKcrYBhxUWA3ur5VRC"));
    }
}
