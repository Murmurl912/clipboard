package com.clipboard.clipboard_store.endpoint.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

public class ContentCreateModel {

    public String id;
    public String account;

    @NotBlank
    public String content;
    @NotNull
    public Timestamp contentVersion;
    @NotNull
    public Boolean star;
    @NotNull
    public Timestamp starVersion;
    @NotNull
    @Max(1)
    @Min(0)
    public Integer state;
    @NotNull
    public Timestamp stateVersion;

    @NotNull
    public Timestamp create;
    @NotNull
    public Timestamp update;

}
