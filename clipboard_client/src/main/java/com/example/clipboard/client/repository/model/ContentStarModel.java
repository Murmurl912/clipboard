package com.example.clipboard.client.repository.model;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class ContentStarModel {
    @NotNull
    public Boolean star;
    @NotNull
    public Date starVersion;
}
