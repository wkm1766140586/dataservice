package com.abc.newsserversec.model.user;

public class UploadInfo {

    //id
    private long id;
    //名称
    private String name;
    //类型
    private String classtype;
    //创建时间
    private String createdate;

    public long getId() {
        return id;
    }

    public String getClasstype() {
        return classtype;
    }

    public void setClasstype(String classtype) {
        this.classtype = classtype;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }
}
