package com.example.clipboard.client.repository.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * STATE OF CONTENT
 * (STAR: FALSE,
 */
@Entity
@Table
public class Content {

    @Id
    public String uuid;
    public String id;
    public String account;
    public Integer status;

    @Lob
    public String content;
    public Date contentVersion;

    public Boolean star;
    public Date starVersion;

    public Integer state;
    public Date stateVersion;

    public byte[] hash;

    public Date create;
    public Date update;

    public Content() {

    }

    public Content(String id,
                   String account,
                   ContentStatus status,
                   String content,
                   Date contentVersion,
                   ContentState state,
                   Date stateVersion,
                   boolean star,
                   Date starVersion,
                   byte[] hash,
                   Date create,
                   Date update) {
        this.id = id;
        this.account = account;
        this.status = status.STATUS;
        this.content = content;
        this.state = state.STATE;
        this.hash = hash;
        this.create = create;
        this.update = update;
        this.contentVersion = contentVersion;
        this.starVersion = starVersion;
        this.stateVersion = stateVersion;
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

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public Date getCreate() {
        return create;
    }

    public void setCreate(Timestamp create) {
        this.create = create;
    }

    public Date getUpdate() {
        return update;
    }

    public void setUpdate(Timestamp update) {
        this.update = update;
    }

    public Boolean getStar() {
        return star;
    }

    public void setStar(Boolean star) {
        this.star = star;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Content content1 = (Content) o;
        return Objects.equals(id, content1.id) &&
                Objects.equals(account, content1.account) &&
                Objects.equals(status, content1.status) &&
                Objects.equals(content, content1.content) &&
                Objects.equals(state, content1.state) &&
                Arrays.equals(hash, content1.hash) &&
                Objects.equals(create, content1.create) &&
                Objects.equals(update, content1.update);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, account, status, content, state, hash, create, update);
    }


    @Override
    public String toString() {
        return "Content{" +
                "id='" + id + '\'' +
                ", account='" + account + '\'' +
                ", status=" + status +
                ", content='" + content + '\'' +
                ", state=" + state +
                ", hash='" + Arrays.toString(hash) + '\'' +
                ", create=" + create +
                ", update=" + update +
                '}';
    }

    public enum ContentStatus {
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

    public enum ContentState {
        CONTENT_STATE_NORMAL(0),
        CONTENT_STATE_DELETE(1);

        public int STATE;

        ContentState(int state) {
            STATE = state;
        }

        public static ContentState get(int state) {
            if (state == 1) {
                return CONTENT_STATE_DELETE;
            }
            return CONTENT_STATE_NORMAL;
        }
    }

}
