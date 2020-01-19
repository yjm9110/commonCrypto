package sbox;

import jcpabe.util.PrintUtil;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CCMBlockCipher;
import org.bouncycastle.crypto.params.CCMParameters;
import org.bouncycastle.crypto.params.KeyParameter;

import javax.xml.soap.Node;
import java.io.*;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class AESCCMCipher {
    private final static int SegmentSize =4096;
    private final static int MacSize =128;
    private final static int NonceSize =15;
    private final static int CBlockSize =SegmentSize + MacSize/8;
    private CCMBlockCipher cipher;
    private byte[] nonce;
    private boolean isEnc = false;
    private Random random;
    private ByteArrayOutputStream data = new ByteArrayOutputStream();

    public AESCCMCipher(Random random){
        this.cipher = new CCMBlockCipher(new AESEngine());
        this.nonce = new byte[NonceSize];
        this.random = random;
    }

    public AESCCMCipher(){
        this(new SecureRandom());
    }

    public void reset(){
        this.cipher.reset();
        Arrays.fill(this.nonce, (byte) 0);
        this.data.reset();
    }

    public void init(byte[] key, boolean flag){
        CCMParameters params = new CCMParameters(new KeyParameter(key), MacSize, this.nonce, null);
        this.cipher.init(flag, params);
        this.isEnc = flag;
    }

    public int update(byte[] m){
        int pre = this.data.size();
        this.data.write(m, 0, m.length);
        return this.data.size() - pre;
    }

    public byte[] dofinal(byte[] m){
        this.update(m);
        return this.dofinal();
    }

    public byte[] dofinal(){
        byte[] m = data.toByteArray();
        int reLen = cipher.getOutputSize(data.size());
        byte[] res = new byte[this.isEnc ? (reLen + NonceSize) : (reLen - NonceSize)];
        try {
            if(this.isEnc) {
                this.random.nextBytes(this.nonce);
                System.arraycopy(this.nonce, 0, res, 0, NonceSize);
                System.arraycopy(cipher.processPacket(m, 0, m.length), 0, res, NonceSize, reLen);
            }else{
                System.arraycopy(m, 0, this.nonce, 0, NonceSize);
                res = cipher.processPacket(m, NonceSize, m.length - NonceSize);
            }
        } catch (InvalidCipherTextException e) {
            e.printStackTrace();
            return null;
        }
        return res;
    }

    public boolean dofinal(InputStream is, OutputStream os){
        int tsize = this.isEnc ? SegmentSize : CBlockSize;
        byte[] tmp = new byte[tsize];
        byte[] rtmp = null;
        int size = 0;
        try {
            if(this.isEnc){
                while ((size = is.read(tmp)) > 0) {
                    this.random.nextBytes(this.nonce);
                    rtmp = cipher.processPacket(tmp, 0, size);
                    os.write(this.nonce);
                    os.write(rtmp);
                    os.flush();
                }
            }else {
                while ((size = is.read(this.nonce)) == NonceSize) {
                    if((size = is.read(tmp)) <= 0){
                        is.reset();
                        return false;
                    }
                    rtmp = cipher.processPacket(tmp, 0, size);
                    os.write(rtmp);
                    os.flush();
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        } catch (InvalidCipherTextException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void large(){
        byte[] key = "1234567890123456".getBytes();
        try {
            FileInputStream fis = new FileInputStream("1.mp4");
            FileOutputStream fos = new FileOutputStream("1.cipher.mp4");
            AESCCMCipher cipher = new AESCCMCipher();
            cipher.init(key, true);
            cipher.dofinal(fis, fos);
            fos.flush();
            fis.close();
            fos.close();
            System.out.println("finish enc!");

            fis = new FileInputStream("1.cipher.mp4");
            fos = new FileOutputStream("1.plain.mp4");
            cipher = new AESCCMCipher();
            cipher.init(key, false);;
            cipher.dofinal(fis, fos);
            fos.flush();
            fis.close();
            fos.close();
            System.out.println("finish dec!");


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void str(String[] args) throws IOException {
        byte[] p = new byte[1024*1024];
        Arrays.fill(p, (byte) 0xff);
        byte[] key = "1234567890123456".getBytes();
        ByteArrayInputStream bis = new ByteArrayInputStream(p);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AESCCMCipher cipher = new AESCCMCipher();
        cipher.init(key, true);
        cipher.update(p);
        byte[] c = cipher.dofinal();
        System.out.println(c.length);
        System.out.println(PrintUtil.bytesToHexString(c));


        cipher.dofinal(bis, bos);
        System.out.println(bos.size());
        System.out.println(PrintUtil.bytesToHexString(bos.toByteArray()));
        bis = new ByteArrayInputStream(bos.toByteArray());
        bos.reset();

        cipher = new AESCCMCipher();
        cipher.init(key, false);
        cipher.update(c);
        byte[] m = cipher.dofinal();
        System.out.println(m.length);
        System.out.println(PrintUtil.bytesToHexString(m));

        cipher.dofinal(bis, bos);
        System.out.println(bos.size());
        System.out.println(PrintUtil.bytesToHexString(bos.toByteArray()));
    }

    public static void main(String[] args){
        large();
    }
}
