package fun.lnex;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.*;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import fun.lnex.tools.http.HttpUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DDns {
    public static void main(String[] args) {

        DDns dDns = new DDns();
        dDns.updateDDns();

    }
    IAcsClient acsClient;
    Properties properties;
    public DDns()
    {
        properties= new Properties();
        InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("ddns.properties");
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String ACCESS_KEY_ID = properties.getProperty("ACCESS_KEY_ID", "");
        String ACCESS_SECRET = properties.getProperty("ACCESS_SECRET", "");


        // 创建DefaultAcsClient实例并初始化
        DefaultProfile profile = DefaultProfile.getProfile(
                "cn-hangzhou",          // 地域ID https://help.aliyun.com/document_detail/40654.html
                ACCESS_KEY_ID,      // RAM账号的AccessKey ID
                ACCESS_SECRET); // RAM账号AccessKey Secret
        acsClient = new DefaultAcsClient(profile);
    }

    private void updateDDns() {
        //get public ip
        String myPublicIP = HttpUtils.getPublicIP();
        if (!HttpUtils.validateIP(myPublicIP)) {
            System.out.println("Failed to get my public ip.");
            return;
        }

        updateDDns(myPublicIP);

    }

    //https://www.alibabacloud.com/help/zh/doc-detail/34308.htm?spm=a2c63.p38356.b99.71.6e2a6f5fInXzSe
    public void updateDDns(String myPublicIP) {
        DescribeDomainRecordsRequest request = new DescribeDomainRecordsRequest();
        request.setDomainName(properties.getProperty("DOMAIN_NAME",""));
        DescribeDomainRecordsResponse response;
        try {
            response = acsClient.getAcsResponse(request);

            response.getDomainRecords().stream().filter(record -> !record.getValue().equalsIgnoreCase(myPublicIP))
                                                .forEach(record -> updateDDnsReally(record, myPublicIP));
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    //https://www.alibabacloud.com/help/zh/doc-detail/34306.htm?spm=a2c63.p38356.b99.69.63386384JNOiCS
    private void updateDDnsReally(DescribeDomainRecordsResponse.Record record, String myPublicIP) {
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


        } catch (ClientException e) {
            e.printStackTrace();
        }
    }


}
