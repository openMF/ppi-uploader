/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifos.tools.rest.data;

import java.util.List;

public class Survey {

    private Long id;
    private List<Component> componentDatas;
    private List<Question> questionDatas;
    private String key;
    private String name;
    private String description;
    private String countryCode;

    public Survey() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Component> getComponentDatas() {
        return componentDatas;
    }

    public void setComponentDatas(List<Component> componentDatas) {
        this.componentDatas = componentDatas;
    }

    public List<Question> getQuestionDatas() {
        return questionDatas;
    }

    public void setQuestionDatas(List<Question> questionDatas) {
        this.questionDatas = questionDatas;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
