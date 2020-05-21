package com.example.clipboard.client.repository.entity;

public class Record {

    public String id;
    public Integer operation;

    public static enum Operation {
        OPERATION_CREATE(0),
        OPERATION_UPDATE(1),
        OPERATION_DELETE(2);
        public int OPERATION;
        Operation(int operation) {
            this.OPERATION = operation;
        }
    }
}
