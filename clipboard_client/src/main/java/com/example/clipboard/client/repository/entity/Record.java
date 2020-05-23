package com.example.clipboard.client.repository.entity;

public class Record {

    public String id;
    public Integer operation;

    public static enum Operation {
        OPERATION_UPDATE(0),
        OPERATION_DELETE(1);
        public int OPERATION;

        Operation(int operation) {
            this.OPERATION = operation;
        }
    }
}
