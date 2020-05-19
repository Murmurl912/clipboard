package com.clipboard.clipboard_store.endpoint.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

public class ContentTextModel {
    @NotBlank
    public String content;
    @NotNull
    public Timestamp contentVersion;
}
