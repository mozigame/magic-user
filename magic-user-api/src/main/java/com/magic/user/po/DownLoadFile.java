package com.magic.user.po;

/**
 * DownLoadFile
 *
 * @author zj
 * @date 2017/5/8
 */
public class DownLoadFile {

    private String filename;
    private byte[] content;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
