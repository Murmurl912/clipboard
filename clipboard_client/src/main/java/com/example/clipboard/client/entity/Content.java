package com.example.clipboard.client.entity;

import oshi.SystemInfo;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table
public class Content {

    @Id
    public String id;
    public String account;
    public Integer status;

    public String device;

    @Lob
    public String content;

    public Boolean star;
    public Boolean archive;
    public Boolean recycle;

    public String hash;

    public Date create;
    public Date update;

    public Content() {

    }

    public Content(String id, String account,
                   ContentStatus status, String device,
                   String content, Boolean star,
                   Boolean archive, Boolean recycle,
                   String hash, Date create, Date update) {
        this.id = id;
        this.account = account;
        this.status = status.STATUS;
        this.device = device;
        this.content = content;
        this.star = star;
        this.archive = archive;
        this.recycle = recycle;
        this.hash = hash;
        this.create = create;
        this.update = update;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Content content1 = (Content) o;
        return Objects.equals(id, content1.id) &&
                Objects.equals(account, content1.account) &&
                Objects.equals(status, content1.status) &&
                Objects.equals(device, content1.device) &&
                Objects.equals(content, content1.content) &&
                Objects.equals(star, content1.star) &&
                Objects.equals(archive, content1.archive) &&
                Objects.equals(recycle, content1.recycle) &&
                Objects.equals(hash, content1.hash) &&
                Objects.equals(create, content1.create) &&
                Objects.equals(update, content1.update);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, account, status, device, content, star, archive, recycle, hash, create, update);
    }

    public static enum ContentStatus {
        CONTENT_STATUS_LOCAL(0),
        CONTENT_STATUS_LOCAL_SYNCED(1),
        CONTENT_STATUS_LOCAL_SYNCING(2),
        CONTENT_STATUS_CLOUD(3),
        CONTENT_STATUS_CLOUD_SYNCING(4),
        CONTENT_STATUS_CLOUD_SYNCED(5);
        public int STATUS;

        ContentStatus(int status) {
            STATUS = status;
        }
    }

    @Override
    public String toString() {
        return "Content{" +
                "id='" + id + '\'' +
                ", account='" + account + '\'' +
                ", status=" + status +
                ", device='" + device + '\'' +
                ", content='" + content + '\'' +
                ", star=" + star +
                ", archive=" + archive +
                ", recycle=" + recycle +
                ", hash='" + hash + '\'' +
                ", create=" + create +
                ", update=" + update +
                '}';
    }

    public void setDefaultIfAbsent() {
        if(id == null)
            id = UUID.randomUUID().toString();
        if(account == null)
            account = "local";
        if(status == null)
            status = ContentStatus.CONTENT_STATUS_LOCAL.STATUS;
        if(device == null)
            device = (new SystemInfo()).getOperatingSystem().getVersionInfo().getVersion();
        if(star == null)
            star = false;
        if(archive == null)
            archive = false;
        if(recycle == null)
            recycle = false;
        if(create == null)
            create = new Date();
        if(update == null)
            update = new Date();
    }

    public static enum ContentDifference {
        CONTENT_DIFFERENCE_TEXT,
        CONTENT_DIFFERENCE_FLAG,
    }


}
