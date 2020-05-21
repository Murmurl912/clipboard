package com.example.clipboard.client.repository.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class StateModel {
    public String id;
    @Min(0)
    @Max(1)
    public Integer state;
    @NotNull
    public Date update;
}
