package cc.bocang.bocang.data.model;

import java.io.Serializable;

/**
 * Created by xpHuang on 2016/8/18.
 */
public class GoodsGallery  implements Serializable {
    private int id;//
    private String goods_id;//
    private String img_url;//
    private String sort;//
    private String filetype;//
    private String filesize;//
    private String uploadtime;//

    @Override
    public String toString() {
        return "GoodsGallery{" +
                "id='" + id + '\'' +
                ", goods_id='" + goods_id + '\'' +
                ", img_url='" + img_url + '\'' +
                ", sort='" + sort + '\'' +
                ", filetype='" + filetype + '\'' +
                ", filesize='" + filesize + '\'' +
                ", uploadtime='" + uploadtime + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public String getFilesize() {
        return filesize;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }

    public String getUploadtime() {
        return uploadtime;
    }

    public void setUploadtime(String uploadtime) {
        this.uploadtime = uploadtime;
    }
}
