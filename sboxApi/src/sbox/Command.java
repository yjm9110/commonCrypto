package sbox;

public class Command {
    public static void main(String[] args){
//        if(args.length < 2) {
//            System.out.println("Missing Parameters!");
//            return;
//        }

        String name = args[0];
        if("init".equals(name)){
            System.out.println(APIs.init(args[1]));
        }else if("genUUID".equals(name)){
            System.out.println(APIs.genABEUUID(args[1]));
        }else if("genUKs".equals(name)){
            System.out.println(APIs.genUKs(args[1],args[2]));
        }else if("enc".equals(name)){
            System.out.println(APIs.enc(args[1],Integer.parseInt(args[2]),args[3],args[4]));
        }else if("trans".equals(name)){
            System.out.println(APIs.transform(args[1], args[2], args[3]));
        }else if("dec".equals(name)){
            System.out.println(APIs.dec(args[1],args[2],args[3],args[4]));
        }else if("-help".equals(name)){
            System.out.println("-help;      Commands instruction;");
            System.out.println("init;       Init public parameters;     propName --> pp");
            System.out.println("genUUID;    Generate ABE UUID;          pp --> uuid");
            System.out.println("genUKs;     Generate user keys;         pp, attrs --> dk, tk");
            System.out.println("enc;        Encryption;                 pp, aesBit, policystr, m --> abeCipher, aesCipher");
            System.out.println("trans;      Transformation decryption;  pp, tk, abeCipher --> abetCipher");
            System.out.println("dec;        final decryption;           pp, dk, abetCipher, aesCipher  --> m");
        }else{
            System.out.println("Illegal Instruction!\nuse \"-help\" to check commands.");
        }
        return;
    }
}
