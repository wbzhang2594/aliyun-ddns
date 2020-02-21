package fun.lnex.aliyun.model;

public class DnsRecord {
    private String domainName;  //域名
    private String rR; //主机记录
    private String type; //记录类型
    private String line; //解析线路
    private String value; //记录值
    private String ttl;
    private String status; //状态

    public DnsRecord(String input) {
        String[] values = input.split("[,;]");

        if (values.length == 7) {
            domainName = values[0].strip();
            rR = values[1].strip();
            type = values[2].strip();
            line = values[3].strip();
            value = values[4].strip();
            ttl = values[5].strip();
            status = values[6].strip();
        }
        else{
            domainName = "NULL";
        }
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getrR() {
        return rR;
    }

    public void setrR(String rR) {
        this.rR = rR;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTtl() {
        return ttl;
    }

    public void setTtl(String ttl) {
        this.ttl = ttl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(domainName).append(',');
        sb.append(rR).append(',');
        sb.append(type).append(',');
        sb.append(line).append(',');
        sb.append(value).append(',');
        sb.append(ttl).append(',');
        sb.append(status).append(';');
        return sb.toString();
    }
}
