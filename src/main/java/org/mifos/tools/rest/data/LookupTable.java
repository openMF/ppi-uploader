/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifos.tools.rest.data;

import java.util.List;

public class LookupTable {

    private String key;
    private String description;
    private List<LookupTableEntry> entries;

    public LookupTable() {
        super();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<LookupTableEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<LookupTableEntry> entries) {
        this.entries = entries;
    }
}
