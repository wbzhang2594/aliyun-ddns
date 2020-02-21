package fun.lnex.aliyun;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsRequest;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse.Record;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordRequest;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.utils.StringUtils;
import fun.lnex.aliyun.model.DnsRecord;
import fun.lnex.aliyun.tools.http.HttpUtils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class DDns {

    public static final String DDNS_PROPERTIES = "private/ddns.properties";
    IAcsClient acsClient;
    Properties properties;
    Map<String, List<DnsRecord>> dnsRecordDictionary;

    public DDns() {
        properties = new Properties();
        InputStream is = null;
        try {
            File file = new File(DDNS_PROPERTIES);
            if (file.exists()) {
                is = new BufferedInputStream(new FileInputStream(file));
            } else {
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream(DDNS_PROPERTIES);
            }

            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String ACCESS_KEY_ID = properties.getProperty("ACCESS_KEY_ID", "");
        String ACCESS_SECRET = properties.getProperty("ACCESS_SECRET", "");
        String updateDomainInWhiteList = properties.getProperty("UpdateDomainInWhiteList", "");
        dnsRecordDictionary = buildDnsRecordDict(updateDomainInWhiteList);

        System.out.println((new Date()).toString() + ": " + properties.toString());

        // 创建DefaultAcsClient实例并初始化
        DefaultProfile profile = DefaultProfile.getProfile(
                "cn-hangzhou",          // 地域ID https://help.aliyun.com/document_detail/40654.html
                ACCESS_KEY_ID,      // RAM账号的AccessKey ID
                ACCESS_SECRET); // RAM账号AccessKey Secret
        acsClient = new DefaultAcsClient(profile);
    }

    public static void main(String[] args) {

        DDns dDns = new DDns();
        dDns.updateDDns();

    }

    private Map<String, List<DnsRecord>> buildDnsRecordDict(String updateDomainInWhiteList) {
//        Map<String, List<DnsRecord>> output = new HashMap<>();
        List<String> strDnsRecordList = Arrays.asList(updateDomainInWhiteList.replace("\"", "").split("[;]"));
        Map<String, List<DnsRecord>> output = strDnsRecordList.stream()
                .map(strItem -> new DnsRecord(strItem))
                .collect(Collectors.groupingBy(dnsRecord -> dnsRecord.getDomainName()));
        return output;
    }

    private void updateDDns() {
        //get public ip
        String myPublicIP = HttpUtils.getPublicIP();
        if (!HttpUtils.validateIP(myPublicIP)) {
            System.out.println("Failed to get my public ip.");
            return;
        }


        boolean updated = updateDDns(myPublicIP);
        if (!updated) {
            System.out.println("No DNS record was updated!");
        } else {
            System.out.println("DNS records were updated!");
        }

    }

    //https://www.alibabacloud.com/help/zh/doc-detail/34308.htm?spm=a2c63.p38356.b99.71.6e2a6f5fInXzSe
    public boolean updateDDns(String myPublicIP) {
        final boolean[] updated = {false};
        DescribeDomainRecordsRequest request = new DescribeDomainRecordsRequest();

        for (String domainName : dnsRecordDictionary.keySet()) {
            if (domainName.equalsIgnoreCase("NULL")) {
                continue;
            }

            request.setDomainName(domainName);
            DescribeDomainRecordsResponse response;
            try {
                response = acsClient.getAcsResponse(request);
                List<Record> records = response.getDomainRecords();

                records.stream()
                        .filter(record -> whetherMatch(record, dnsRecordDictionary.get(domainName)))
                        .filter(record -> !record.getValue().equalsIgnoreCase(myPublicIP))
                        .forEach(record -> {
                            updateDDnsReally(record, myPublicIP);
                            updated[0] = true;
                        });

                return updated[0];
            } catch (ClientException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean whetherMatch(Record record, List<DnsRecord> dnsRecords) {
        return dnsRecords.stream().anyMatch(r -> r.getDomainName().equalsIgnoreCase(record.getDomainName())
                && (StringUtils.isEmpty(r.getLine()) || r.getLine().equalsIgnoreCase(record.getLine()))
                && (StringUtils.isEmpty(r.getrR()) || r.getrR().equalsIgnoreCase(record.getRR()))
                && (StringUtils.isEmpty(r.getStatus()) || r.getStatus().equalsIgnoreCase(record.getStatus()))
                && (StringUtils.isEmpty(r.getTtl()) || Long.valueOf(r.getTtl()).equals(record.getTTL()))
                && (StringUtils.isEmpty(r.getType()) || r.getType().equalsIgnoreCase(record.getType()))
                && (StringUtils.isEmpty(r.getValue()) || r.getValue().equalsIgnoreCase(record.getValue()))
        );
    }

    //https://www.alibabacloud.com/help/zh/doc-detail/34306.htm?spm=a2c63.p38356.b99.69.63386384JNOiCS
    private void updateDDnsReally(Record record, String myPublicIP) {
        UpdateDomainRecordRequest request = new UpdateDomainRecordRequest();
        request.setRecordId(record.getRecordId());
        request.setRR(record.getRR());
        request.setType(record.getType());
        request.setValue(myPublicIP);
        request.setLine(record.getLine());
        request.setPriority(record.getPriority());
        request.setTTL(record.getTTL());


        UpdateDomainRecordResponse response;
        try {
            response = acsClient.getAcsResponse(request);

            System.out.println("updated from: " + record.getValue() + " to: " + myPublicIP);

        } catch (ClientException e) {
            e.printStackTrace();
        }
    }


}
