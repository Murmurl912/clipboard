package com.example.clipboard.server.entity.temp;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("verification_code")
public class VerificationCode {
    public String id;
    public String account;

    public String code;
    public Integer type;
    public String destination;
    public Boolean active;
    public Date expire;
    public Date create;
    public Date update;

    public static enum VerificationCodeType {
        VERIFICATION_CODE_TYPE_LOGIN(0),
        VERIFICATION_CODE_TYPE_ACTIVATION(1),
        VERIFICATION_CODE_TYPE_PASSWORD(2),
        VERIFICATION_CODE_TYPE_EMAIL(4);

        public int TYPE;
        VerificationCodeType(int type) {
            this.TYPE = type;
        }
    }

}
