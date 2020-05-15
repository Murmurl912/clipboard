package com.example.clipboard.server.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
public class Account {
    @Id
    public String id;
    @Indexed(unique = true)
    public String username;
    public String password;
    @Indexed(unique = true)
    public String email;
    public String avatar;
    public Date latestEmailChange;
    public String oldEmail;
    public Integer status;

    public Date create;
    public Date update;

    public enum AccountStatus {
        ACCOUNT_STATUS_ACTIVATE(0),
        ACCOUNT_STATUS_REGISTERED(1),
        ACCOUNT_STATUS_BLOCK(2);

        public Integer STATUS;
        AccountStatus(int status) {
            this.STATUS = status;
        }
    }

    public static class AccountSupport {
        public final static String id = "id";
        public final static String username = "username";
        public final static String email = "email";
        public final static String avatar = "avatar";
        public final static String oldEmail = "oldEmail";
        public final static String password = "password";
        public final static String status = "status";
        public final static String latestEmailChange = "latestEmailChange";
        public final static String create = "create";
        public final static String update = "update";

    }
}
