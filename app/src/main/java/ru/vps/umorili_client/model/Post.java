package ru.vps.umorili_client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Post implements Serializable {
    private static final long serialVersionUId = 1L;
    @SerializedName("site")
    @Expose
    private String site;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("desc")
    @Expose
    private String desc;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("elementPureHtml")
    @Expose
    private String elementPureHtml;

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getElementPureHtml() {
        return elementPureHtml;
    }

    public void setElementPureHtml(String elementPureHtml) {
        this.elementPureHtml = elementPureHtml;
    }

    @Override
    public String toString() {
        return "Post{" +
                "site='" + site + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", link='" + link + '\'' +
                ", elementPureHtml='" + elementPureHtml + '\'' +
                '}';
    }
}
