package com.clipboard.clipboard_store.endpoint.model;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class ContentStarModel {
    @NotNull
    public Boolean star;
    @NotNull
    public Date starVersion;
}
