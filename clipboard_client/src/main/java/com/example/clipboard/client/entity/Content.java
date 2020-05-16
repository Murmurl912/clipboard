package com.example.clipboard.client.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.util.Date;
import java.util.Objects;

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
        CONTENT_STATUS_SYNCED(0),
        CONTENT_STATUS_LOCAL(1),
        CONTENT_STATUS_SYNCING(2),
        CONTENT_STATUS_SYNC_FAILED(3);
        public int STATUS;

        ContentStatus(int status) {
            STATUS = status;
        }
    }
}
