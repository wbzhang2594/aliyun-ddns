package fun.lnex.aliyun.model;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class DnsRecordTest {

    @Test
    public void testToString() {
        String input = "lnex.fun,@,A,default,,600,ENABLE;";
        DnsRecord dnsRecord = new DnsRecord(input);

        String observedOutput = dnsRecord.toString();

        Assert.assertEquals(input,observedOutput);

    }
}