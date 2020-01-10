import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;
import it.unisa.dia.gas.plaf.jpbc.pairing.a1.TypeA1CurveGenerator;
import it.unisa.dia.gas.plaf.jpbc.pairing.f.TypeFCurveGenerator;

public class TestCurve {
    public static void testA1(){
        Pairing pairing = PairingFactory.getPairing(new TypeA1CurveGenerator(3, 1024).generate());
        System.out.println(pairing.getG1().getOrder());
    }

    public static void test(){
        Pairing pairing = PairingFactory.getPairing(new TypeACurveGenerator(160, 512).generate());
        Element g1 = pairing.getG1().newRandomElement().getImmutable();
        Element g2 = pairing.getG2().newRandomElement().getImmutable();
        int times = 1000;
        long time = 0, t1, t2;
        for(int i = 0 ; i < times; i++){
            t1 = System.currentTimeMillis();
            pairing.pairing(g1, g2);
            t2 = System.currentTimeMillis();
            time += t2 - t1;
        }
        System.out.println(time/times);


        pairing = PairingFactory.getPairing("params/d159.properties");
        g1 = pairing.getG1().newRandomElement().getImmutable();
        g2 = pairing.getG2().newRandomElement().getImmutable();
        time = 0;
        for(int i = 0 ; i < times; i++){
            t1 = System.currentTimeMillis();
            pairing.pairing(g1, g2);
            t2 = System.currentTimeMillis();
            time += t2 - t1;
        }
        System.out.println(time/times);


        pairing = PairingFactory.getPairing("params/d201.properties");
        g1 = pairing.getG1().newRandomElement().getImmutable();
        g2 = pairing.getG2().newRandomElement().getImmutable();
        time = 0;
        for(int i = 0 ; i < times; i++){
            t1 = System.currentTimeMillis();
            pairing.pairing(g1, g2);
            t2 = System.currentTimeMillis();
            time += t2 - t1;
        }
        System.out.println(time/times);


        pairing = PairingFactory.getPairing("params/d224.properties");
        g1 = pairing.getG1().newRandomElement().getImmutable();
        g2 = pairing.getG2().newRandomElement().getImmutable();
        time = 0;
        for(int i = 0 ; i < times; i++){
            t1 = System.currentTimeMillis();
            pairing.pairing(g1, g2);
            t2 = System.currentTimeMillis();
            time += t2 - t1;
        }
        System.out.println(time/times);


        pairing = PairingFactory.getPairing(new TypeFCurveGenerator(160).generate());
        g1 = pairing.getG1().newRandomElement().getImmutable();
        g2 = pairing.getG2().newRandomElement().getImmutable();
        time = 0;
        for(int i = 0 ; i < times; i++){
            t1 = System.currentTimeMillis();
            pairing.pairing(g1, g2);
            t2 = System.currentTimeMillis();
            time += t2 - t1;
        }
        System.out.println(time/times);
    }

    public static void main(String[] args){
        test();
    }
}
