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
            System.out.println(APIs.enc(args[1],args[2],Integer.parseInt(args[3]),args[4]));
        }else if("trans".equals(name)){
            System.out.println(APIs.transform(args[1], args[2], args[3]));
        }else if("dec".equals(name)){
            System.out.println(APIs.dec(args[1],args[2],args[3],args[4]));
        }else{
            System.out.println("Illegal Instruction!");
        }
        return;
    }
}
