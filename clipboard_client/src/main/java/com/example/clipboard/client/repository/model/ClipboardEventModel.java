package com.example.clipboard.client.repository.model;

import java.util.Date;

public class ClipboardEventModel {
    public String source; // account
    public String id; // content id
    public Integer type; // event type
    public Date version; // content version
    public Date timestamp; // timestamp

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getVersion() {
        return version;
    }

    public void setVersion(Date version) {
        this.version = version;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ClipboardEventModel{" +
                "source='" + source + '\'' +
                ", id='" + id + '\'' +
                ", type=" + type +
                ", version=" + version +
                ", timestamp=" + timestamp +
                '}';
    }
}
