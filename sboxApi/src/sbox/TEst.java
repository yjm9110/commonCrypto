package sbox;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import jcpabe.util.PrintUtil;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CCMBlockCipher;
import org.bouncycastle.crypto.params.CCMParameters;
import org.bouncycastle.crypto.params.KeyParameter;

import java.io.*;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class TEst {
    private final static int SegmentSize =1024;
    private final static int MacSize =128;
    private final static int NonceSize =15;
    private final static int CBlockSize =1024 + MacSize/8;
    private static byte[] key;
    private static Random random=new SecureRandom();
    public static void enc(InputStream is, OutputStream os){
        byte[] nounce = new byte[NonceSize];
        byte[] tmp = new byte[SegmentSize];
        byte[] rtmp = null;
        CCMParameters params = new CCMParameters(new KeyParameter(key), MacSize, nounce, null);
        CCMBlockCipher cipher = new CCMBlockCipher(new AESEngine());
        cipher.init(true, params);
        int size=0;
        try {
            while ((size = is.read(tmp)) > 0){
                random.nextBytes(nounce);
                rtmp = cipher.processPacket(tmp, 0, size);
                os.write(nounce);
                os.write(rtmp);
                os.flush();
            }
        } catch (IOException e){
            e.printStackTrace();
        } catch (InvalidCipherTextException e) {
            e.printStackTrace();
        }
    }
    public static void dec(InputStream is,OutputStream os){
        byte[] nounce = new byte[NonceSize];
        byte[] tmp = new byte[CBlockSize];
        byte[] rtmp = null;
        CCMParameters params = new CCMParameters(new KeyParameter(key), MacSize, nounce, null);
        CCMBlockCipher cipher = new CCMBlockCipher(new AESEngine());
        cipher.init(false, params);
        int size=0;
        try {
            while ((size = is.read(nounce)) > 0){
                size = is.read(tmp);
                if(size < MacSize/8)
                    break;
                System.out.println(size);
                rtmp = cipher.processPacket(tmp, 0, size);
                os.write(rtmp);
                os.flush();
            }
        } catch (IOException e){
            e.printStackTrace();
        } catch (InvalidCipherTextException e) {
            e.printStackTrace();
        }
    }
    public static byte[] enc(byte[] content, byte[] key){
        byte[] result = null;
        byte[] nounce = new byte[15];
        random.nextBytes(nounce);
        int macSize = 128;
        try {
            CCMParameters params = new CCMParameters(new KeyParameter(key), macSize, nounce, null);
            CCMBlockCipher cipher = new CCMBlockCipher(new AESEngine());
            cipher.init(true, params);
            result = new byte[cipher.getOutputSize(content.length) + nounce.length];
            cipher.processBytes(content, 0, content.length, result, 0);
            //cipher.doFinal(result, 0);

            System.out.println(content.length);
            System.out.println(PrintUtil.bytesToHexString(content));
            byte[] c1 = cipher.processPacket(content, 0, 5);
            System.out.println(PrintUtil.bytesToHexString(content));
            System.out.println(c1.length);
            System.out.println(PrintUtil.bytesToHexString(c1));
            byte[] c2 = cipher.processPacket(content, 0, 5);
            System.out.println(c2.length);
            System.out.println(PrintUtil.bytesToHexString(c2));
            System.out.println(cipher.getMac().length);
            System.out.println(PrintUtil.bytesToHexString(cipher.getMac()));
            System.arraycopy(nounce, 0, result, result.length - nounce.length, nounce.length);
        } catch (InvalidCipherTextException e) {
            e.printStackTrace();
        }
        return result;
    }
    public static void dec(){

    }
    public static void main(String[] args){
        byte[] p = new byte[4098];
        Arrays.fill(p, (byte) 0xff);
        key = "1234567890123456".getBytes();
        ByteArrayInputStream bis = new ByteArrayInputStream(p);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        enc(bis, bos);
        byte[] c = bos.toByteArray();
        System.out.println(PrintUtil.bytesToHexString(c));
        System.out.println(new String(Base64.getEncoder().encode(c)));
        System.out.println(c.length);
        bis = new ByteArrayInputStream(c);
        bos.reset();
        dec(bis, bos);
        System.out.println(PrintUtil.bytesToHexString(bos.toByteArray()));
        System.out.println(bos.toByteArray().length);
    }
}
