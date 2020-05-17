package com.example.clipboard.client.entity;

import java.util.Date;

/**
 * A RECORD OF CONTENT OPERATION THAT NEED TO SYNCHRONIZED WITH CLOUD
 *
 */
public class OperationRecord {

    public String id;
    public String operation;
    public String localContent;
    public Integer status;
    public Date create;
    public Date update;

    public static enum OperationStatus {
        STATUS_WAITING,
        STATUS_CANCEL,
        STATUS_DONE
    }
}
