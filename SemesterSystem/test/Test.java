/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Utils.ProtocolStrings;
import static org.junit.Assert.*;

/**
 *
 * @author Frederik
 */
public class Test {

    @org.junit.Test
    public void sendProtocol() {
        System.out.println("Testing SEND# Protocol");
        assertEquals(ProtocolStrings.SEND, "SEND#");
    }

    @org.junit.Test
    public void stopProtocol() {
        System.out.println("Testing the CLOSE# Protocol");
        assertEquals(ProtocolStrings.STOP, "CLOSE#");
    }
}
