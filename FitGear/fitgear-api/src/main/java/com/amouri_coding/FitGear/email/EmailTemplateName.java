package com.amouri_coding.FitGear.email;

import lombok.Getter;

@Getter
public enum EmailTemplateName {

    ACTIVATE_ACCOUNT("activate_account")

    ;
    private final String name;
    EmailTemplateName(final String name) {
        this.name = name;
    }
}
