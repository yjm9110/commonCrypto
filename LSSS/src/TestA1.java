import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a1.TypeA1CurveGenerator;
import it.unisa.dia.gas.plaf.jpbc.util.ElementUtils;

public class TestA1 {
    public static void main(String[] args){
        int numPrimes = 4;
        int qBit = 512;
        TypeA1CurveGenerator pg = new TypeA1CurveGenerator(numPrimes, qBit);
        PairingParameters typeA1Params = pg.generate();
        Pairing pairing = PairingFactory.getPairing(typeA1Params);

        // generator
        Element g = pairing.getG1().newRandomElement().getImmutable();
        // random gp1
        Element gp1 = ElementUtils.getGenerator(pairing, g, typeA1Params, 0, numPrimes).getImmutable();
        // random gp2
        Element gp2 = ElementUtils.getGenerator(pairing, g, typeA1Params, 1, numPrimes).getImmutable();
        // random gp3
        Element gp3 = ElementUtils.getGenerator(pairing, g, typeA1Params, 2, numPrimes).getImmutable();
        // random gp4
        Element gp4 = ElementUtils.getGenerator(pairing, g, typeA1Params, 3, numPrimes).getImmutable();

        Element e_p1_p2 = pairing.pairing(gp1, gp2);
        Element e_p1_p3 = pairing.pairing(gp1, gp3);
        Element e_p1_p4 = pairing.pairing(gp1, gp4);
        System.out.println(e_p1_p2);
        System.out.println(e_p1_p3);
        System.out.println(e_p1_p4);
        System.out.println(pairing.getGT().newOneElement());
    }
}
