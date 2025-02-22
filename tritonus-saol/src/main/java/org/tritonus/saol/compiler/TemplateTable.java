/*
 *  Copyright (c) 2002 by Matthias Pfisterer
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.tritonus.saol.compiler;

import java.util.HashMap;
import java.util.Map;


/**
 * The template table.
 * TODO: use generics
 */
public class TemplateTable {

    /**
     * Map that holds the template entries.
     * Key: the name of the template.
     * Value: a TemplateEntry instance.
     */
    private Map<String, TemplateEntry> m_templateMap;

    public TemplateTable() {
        m_templateMap = new HashMap<>();
    }

    public void add(TemplateEntry templateEntry) {
        m_templateMap.put(templateEntry.getTemplateName(), templateEntry);
    }

    public TemplateEntry get(String strTemplateName) {
        return m_templateMap.get(strTemplateName);
    }
}

/* TemplateTable.java */
