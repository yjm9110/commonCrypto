package sbox;

import jcpabe.attribute.AccessPolicyTree;
import jcpabe.attribute.AttributeItem;
import jcpabe.attribute.AttributeItemSet;
import jcpabe.crypto.ODABEDecrypt;
import jcpabe.crypto.ODABEEncrypt;
import jcpabe.crypto.ODABETransform;
import jcpabe.generator.MSKGenerator;
import jcpabe.generator.UserKeyPairGen;
import jcpabe.module.*;
import jcpabe.module.key.ABEKeyPair;
import jcpabe.module.key.ABEMasterKey;
import jcpabe.module.key.ABEUserDK;
import jcpabe.module.key.ABEUserTK;
import jcpabe.util.ABEUtil;

import java.io.File;
import java.util.*;

public class APIs {
    public static Map<String, String> PROPS;
    public static String MSKPATH = "Keys/msks/";
    static {
        PROPS = new HashMap<>();
        PROPS.put("SS512","Keys/a512.properties");
        PROPS.put("MNT159","Keys/d159.properties");
        PROPS.put("MNT201","Keys/d201.properties");
        PROPS.put("MNT224","Keys/d224.properties");
        PROPS.put("MNT359","Keys/d359.properties");
        PROPS.put("f160","Keys/f160.properties");
        PROPS.put("f256","Keys/f256.properties");
        PROPS.put("f512","Keys/f512.properties");
    }

    public static String init(String PROP){
        String path = PROPS.get(PROP);
        if(path == null){
            return "Illegal property name!";
        }
        PublicParameters.Generator.genPublicParameters(path);
        MSKGenerator mskGen = new MSKGenerator();
        ABEKeyPair keyPair = mskGen.generateKeyPair();
        ABEUtil.getPublicParameters().setPp(keyPair.getPublicKey());
        File f = new File(MSKPATH+keyPair.getPublicKey().getUUID());
        if(!f.getParentFile().exists())
            f.getParentFile().mkdirs();
        keyPair.getMasterKey().save(MSKPATH+keyPair.getPublicKey().getUUID());
        return encode(ABEUtil.getPublicParameters().toBytes());
    }

    public static String genABEUUID(String pp){
        PublicParameters.newInstanceFromPublicParams(decode(pp));
        return AttributeItem.getRandomUUID();
    }

    public static String genUKs(String pp, String attrls){
        PublicParameters.newInstanceFromPublicParams(decode(pp));
        ABEMasterKey MK = ABEMasterKey.newInstance(MSKPATH+ABEUtil.getPublicParameters().getPK().getUUID());

        String[] attrs = attrls.split(",");
        ArrayList<AttributeItem> lattrs = new ArrayList<>();
        for(int i = 0; i < attrs.length; i++){
            lattrs.add(new AttributeItem("", "", attrs[i].trim()));
        }

        AttributeItemSet attrset = new AttributeItemSet(lattrs);
        UserKeyPairGen gen = new UserKeyPairGen();
        gen.setAttrItemSet(attrset);
        gen.setMasterKey(MK);
        gen.genUserKeyPair();

        String res = encode(gen.getUserKeyPairManager().getUserDK().toBytes()) + ",";
        res += encode(gen.getUserKeyPairManager().getUserTK().toBytes());
        return res;
    }

    public static String enc(String pp, String m, int aes, String policy){
        PublicParameters.newInstanceFromPublicParams(decode(pp));

        AccessPolicyTree tree = new AccessPolicyTree(policy);
        ODABEEncrypt encrypt = new ODABEEncrypt();
        encrypt.setAccessPolicyTree(tree);
        encrypt.setAESBit(aes);

        TotalCipher totalCipher = encrypt.encrypt(m.getBytes());
        ODABECipher ciphertext = (ODABECipher) totalCipher.getAbeCipher();
        AESCipher aesCipher = totalCipher.getAesCipher();

        String res = encode(ciphertext.toBytes()) + ",";
        res += encode(aesCipher.toBytes());
        return res;
    }

    public static String transform(String pp, String tk, String cipher){
        PublicParameters.newInstanceFromPublicParams(decode(pp));
        ABEUserTK TK = ABEUserTK.newInstance(decode(tk));
        ODABECipher Cipher = ODABECipher.newInstance(decode(cipher));


        ODABETransform transform = new ODABETransform();
        transform.setCiphertext(Cipher);
        transform.setABEUserTK(TK);

        ODABETransformed transformedText = transform.decrypt();
        return encode(transformedText.toBytes());
    }

    public static String dec(String pp, String aescipher, String abetcipher, String dk){
        PublicParameters.newInstanceFromPublicParams(decode(pp));
        ABEUserDK DK = ABEUserDK.newInstance(decode(dk));
        ODABETransformed transformedText = ODABETransformed.newInstance(decode(abetcipher));
        AESCipher aesCipher = AESCipher.newInstance(decode(aescipher));

        ODABEDecrypt decrypt = new ODABEDecrypt();
        decrypt.setUserDK(DK);
        decrypt.setCiphertext(transformedText);
        decrypt.setAesCipher(aesCipher);

        byte[] res = (byte[]) decrypt.decrypt().getMessage();

        return new String(res);
    }

    public static String encode(byte[] src){
        return new String(Base64.getEncoder().encode(src));
    }

    public static byte[] decode(String src){
        return Base64.getDecoder().decode(src.getBytes());
    }

    public static void test(String[] args){
        String pp = init("SS512");

        //gen attrs
        String attrs = genABEUUID(pp) + "," + genABEUUID(pp);
//        System.out.println(attrs);

        //gen keys
        String temp = genUKs(pp, attrs);
        String[] temps = temp.split(",");
        String dk = temps[0];
        String tk = temps[1];

        //enc
//        System.out.println(attrs.replace(',','&'));
        temps = enc(pp, "Hello World!", 128, attrs.replace(',','&')).split(",");
        String abec = temps[0];
        String aesc = temps[1];

        //transform
        String tranc = transform(pp, tk, abec);

        //dec
        String dec = dec(pp, aesc, tranc, dk);
        System.out.println(dec);

        temps = enc(pp, "Hello World!", 128, attrs.replace(',','&')).split(",");
        abec = temps[0];
        aesc = temps[1];

        //transform
        tranc = transform(pp, tk, abec);

        //dec
        dec = dec(pp, aesc, tranc, dk);
        System.out.println(dec);

    }

    public static void main(String[] args){
        test(args);
    }

}
