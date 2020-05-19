package com.clipboard.clipboard_store.endpoint.model;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

public class ContentStarModel {
    @NotNull
    public Boolean star;
    @NotNull
    public Timestamp starVersion;
}
