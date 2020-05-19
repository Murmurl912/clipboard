package com.clipboard.clipboard_store.endpoint.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class ContentStateModel {
    @Min(0)
    @Max(1)
    public Integer state;
    @NotNull
    public Date stateVersion;
}
