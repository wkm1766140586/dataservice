package com.abc.newsserversec.model.user;

public class UserUploadPicture {

    //序号
    private long id;
    //目标名称
    private String objectname;
    //目标id
    private String objectid;
    //图片名称
    private String picturename;
    //状态
    private String state;
    //原因
    private String reason;
    //创建时间
    private String createdate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getObjectname() {
        return objectname;
    }

    public void setObjectname(String objectname) {
        this.objectname = objectname;
    }

    public String getObjectid() {
        return objectid;
    }

    public void setObjectid(String objectid) {
        this.objectid = objectid;
    }

    public String getPicturename() {
        return picturename;
    }

    public void setPicturename(String picturename) {
        this.picturename = picturename;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }
}
