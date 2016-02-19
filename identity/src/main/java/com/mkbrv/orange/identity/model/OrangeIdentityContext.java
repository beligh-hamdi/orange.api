package com.mkbrv.orange.identity.model;

import com.mkbrv.orange.client.OrangeContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by mikibrv on 17/02/16.
 */
public class OrangeIdentityContext extends OrangeContext implements Serializable {

    private static final long serialVersionUID = 42L;

    private final List<OrangeScope> orangeScopeList = new ArrayList<>();
    private final List<OrangePrompt> promptList = new ArrayList<>();
    private String state;


    public List<OrangeScope> getOrangeScopeList() {
        return Collections.unmodifiableList(orangeScopeList);
    }

    public OrangeIdentityContext addScope(final OrangeScope orangeScope) {
        this.orangeScopeList.add(orangeScope);
        return this;
    }

    public String getState() {
        return state;
    }

    public OrangeIdentityContext setState(final String state) {
        this.state = state;
        return this;
    }

    public List<OrangePrompt> getPromptList() {
        return Collections.unmodifiableList(promptList);
    }

    public OrangeIdentityContext addPrompt(final OrangePrompt orangePrompt) {
        this.promptList.add(orangePrompt);
        return this;
    }

}