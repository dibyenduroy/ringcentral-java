package com.ringcentral.definitions;
import com.alibaba.fastjson.annotation.JSONField;
public class PresetInfo
{
    // Link to a greeting resource
    public String uri;
    public PresetInfo uri(String uri) {
        this.uri = uri;
        return this;
    }
    // Internal identifier of greeting
    public String id;
    public PresetInfo id(String id) {
        this.id = id;
        return this;
    }
    // Name of a greeting
    public String name;
    public PresetInfo name(String name) {
        this.name = name;
        return this;
    }
}
