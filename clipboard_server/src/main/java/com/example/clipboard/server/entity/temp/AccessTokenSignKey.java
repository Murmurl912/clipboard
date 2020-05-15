package com.example.clipboard.server.entity.temp;

import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("access_token_sign_key")
public class AccessTokenSignKey {
    public String id;
    public Integer status;
    public Binary publicKey;
    public Binary privateKey;
    public String publicKeyFormat;
    public String privateKyeFormat;

    public static enum AccessTokenSignKeyStatus {
        ACCESS_TOKEN_SIGN_KEY_STATUS_MASTER(0),
        ACCESS_TOKEN_SIGN_KEY_STATUS_SECONDARY(1);
        public int STATUS;
        AccessTokenSignKeyStatus(int status) {
            this.STATUS = status;
        }
    }
}
