package com.example.clipboard.client.entity;

import oshi.SystemInfo;

import javax.persistence.*;
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
    public Date timestamp;
    public Integer previous; // for example when a content is recycled previous state should be keep
    public Integer state;
    public String hash;

    public Date create;
    public Date update;

    public Content() {

    }

    public Content(String id,
                   String account,
                   ContentStatus status,
                   String device,
                   String content,
                   Date timestamp,
                   ContentState previous,
                   ContentState state,
                   String hash,
                   Date create,
                   Date update) {
        this.id = id;
        this.account = account;
        this.status = status.STATUS;
        this.device = device;
        this.content = content;
        this.timestamp = timestamp;
        this.previous = previous.STATE;
        this.state = state.STATE;
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
                Objects.equals(timestamp, content1.timestamp) &&
                Objects.equals(previous, content1.previous) &&
                Objects.equals(state, content1.state) &&
                Objects.equals(hash, content1.hash) &&
                Objects.equals(create, content1.create) &&
                Objects.equals(update, content1.update);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, account, status, device, content, timestamp, previous, state, hash, create, update);
    }


    @Override
    public String toString() {
        return "Content{" +
                "id='" + id + '\'' +
                ", account='" + account + '\'' +
                ", status=" + status +
                ", device='" + device + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", previous=" + state +
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
        if(previous == null)
            previous = state;
        if(timestamp == null)
            timestamp = new Date();
        if(create == null)
            create = new Date();
        if(update == null)
            update = new Date();
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
        CONTENT_STATE_STAR(1),
        CONTENT_STATE_ARCHIVE(2),
        CONTENT_STATE_RECYCLE(3),
        CONTENT_STATE_DELETE(4);

        public int STATE;
        ContentState(int state) {
            STATE = state;
        }

        public static ContentState get(int state) {
            switch (state) {
                case 1: return CONTENT_STATE_STAR;
                case 2: return CONTENT_STATE_ARCHIVE;
                case 3: return CONTENT_STATE_RECYCLE;
                case 4: return CONTENT_STATE_DELETE;
                default: return CONTENT_STATE_NORMAL;
            }
        }
    }

}
