package com.example.clipboard.client.entity;

import java.sql.Timestamp;

public class Record {

    public String record;
    public Integer type;

    // store changed data
    public String target;
    public String content;
    public Integer state;
    public Boolean star;
    public String device;

    public Timestamp timestamp;
    public Boolean processing;

    public enum RecordType {
        RECORD_TYPE_CONTENT_CREATE(0),
        RECORD_TYPE_STATE_CHANGE(1),
        RECORD_TYPE_CONTENT_CHANGE(2),
        RECORD_TYPE_STAR_CHANGE(3);
        public int TYPE;

        RecordType(int type) {
            TYPE = type;
        }
    }
}
