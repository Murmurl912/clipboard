package com.example.clipboard.client.repository.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class ContentTextModel {
    @NotBlank
    public String content;
    @NotNull
    public Date contentVersion;
}
