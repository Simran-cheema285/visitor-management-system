public class Visitor {
    private String name;
    private String email;
    private String mobile;
    private String host;
    private String purpose;
    private String timestamp;
    private String logoutTime;
    private String hostEmail;
    private String imageData;
    private boolean approved;
    private String qrCode;
    private String organizationName;
    private String visitDate;








    public Visitor(String name, String email, String mobile, String host, String purpose, String timestamp) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.host = host;
        this.purpose = purpose;
        this.timestamp = timestamp;
        this.logoutTime = null;
    }


    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public String getHostEmail() {
        return hostEmail;
    }
    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public void setHostEmail(String hostEmail) {
        this.hostEmail = hostEmail;
    }
    public void setApproved(boolean approved) {
        this.approved = approved;
    }
    public boolean getApproved() {
        return approved;
    }


    public String getLogoutTime() { return logoutTime; }
    public void setLogoutTime(String logoutTime) { this.logoutTime = logoutTime; }
    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

    public String getVisitDate() { return visitDate; }
    public void setVisitDate(String visitDate) { this.visitDate = visitDate; }

    public String getQrCode() {
        return qrCode;
    }
}
