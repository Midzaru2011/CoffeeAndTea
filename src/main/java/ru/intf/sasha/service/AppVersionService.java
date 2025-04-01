package ru.intf.sasha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

@Service
public class AppVersionService {

    private final BuildProperties buildProperties;

    @Autowired
    public AppVersionService(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    public String getVersion() {
        return buildProperties.getVersion();
    }
}
