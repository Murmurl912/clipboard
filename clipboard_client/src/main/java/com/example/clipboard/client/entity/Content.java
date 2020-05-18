package com.example.clipboard.client.entity;

import oshi.SystemInfo;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * STATE OF CONTENT
 * (STAR: FALSE,
 */
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
    public Integer state;
    public String hash;

    public Timestamp create;
    public Timestamp update;

    public Content() {

    }

    public Content(String id,
                   String account,
                   ContentStatus status,
                   String device,
                   String content,
                   Timestamp timestamp,
                   ContentState state,
                   String hash,
                   Timestamp create,
                   Timestamp update) {
        this.id = id;
        this.account = account;
        this.status = status.STATUS;
        this.device = device;
        this.content = content;
        this.state = state.STATE;
        this.hash = hash;
        this.create = create;
        this.update = update;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Timestamp getCreate() {
        return create;
    }

    public void setCreate(Timestamp create) {
        this.create = create;
    }

    public Timestamp getUpdate() {
        return update;
    }

    public void setUpdate(Timestamp update) {
        this.update = update;
    }

    public void setStar(Boolean star) {
        this.star = star;
    }

    public Boolean getStar() {
        return star;
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
                Objects.equals(state, content1.state) &&
                Objects.equals(hash, content1.hash) &&
                Objects.equals(create, content1.create) &&
                Objects.equals(update, content1.update);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, account, status, device, content, state, hash, create, update);
    }


    @Override
    public String toString() {
        return "Content{" +
                "id='" + id + '\'' +
                ", account='" + account + '\'' +
                ", status=" + status +
                ", device='" + device + '\'' +
                ", content='" + content + '\'' +
                ", state=" + state +
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
        if(state == null)
            state = ContentState.CONTENT_STATE_NORMAL.STATE;
        if(star == null)
            star = false;
        if(create == null)
            create = new Timestamp(System.currentTimeMillis());
        if(update == null)
            update = new Timestamp(System.currentTimeMillis());
    }

    public static enum ContentStatus {
        CONTENT_STATUS_LOCAL(0),
        CONTENT_STATUS_CLOUD(1);
        public int STATUS;

        ContentStatus(int status) {
            STATUS = status;
        }

        public static ContentStatus get(int status) {
            if (status == 1) {
                return CONTENT_STATUS_CLOUD;
            }
            return CONTENT_STATUS_LOCAL;
        }
    }

    public static enum ContentState {
        CONTENT_STATE_NORMAL(0),
        CONTENT_STATE_ARCHIVE(1),
        CONTENT_STATE_RECYCLE(2),
        CONTENT_STATE_DELETE(3);

        public int STATE;
        ContentState(int state) {
            STATE = state;
        }

        public static ContentState get(int state) {
            switch (state) {
                case 1: return CONTENT_STATE_ARCHIVE;
                case 2: return CONTENT_STATE_RECYCLE;
                case 3: return CONTENT_STATE_DELETE;
                default: return CONTENT_STATE_NORMAL;
            }
        }
    }

}
