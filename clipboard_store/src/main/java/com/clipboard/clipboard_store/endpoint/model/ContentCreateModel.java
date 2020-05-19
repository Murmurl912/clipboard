package com.clipboard.clipboard_store.endpoint.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class ContentCreateModel {

    public String id;
    public String account;

    @NotBlank
    public String content;
    @NotNull
    public Date contentVersion;
    @NotNull
    public Boolean star;
    @NotNull
    public Date starVersion;
    @NotNull
    @Max(1)
    @Min(0)
    public Integer state;
    @NotNull
    public Date stateVersion;

    @NotNull
    public Date create;
    @NotNull
    public Date update;

}
