/*
 * CddaUtils.java
 *
 * This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 2001 by Matthias Pfisterer
 *
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
 *
 */

/*
|<---            this code is formatted to fit into 80 columns             --->|
*/

package org.tritonus.lowlevel.cdda;

import org.tritonus.lowlevel.cdda.cdparanoia.CdparanoiaMidLevel;


/**
 * Gets the preferred CDDA implementation.
 */
public class CddaUtils {
    // TODO: use some automatic lookup mechanism.
    public static CddaMidLevel getCddaMidLevel() {
        CddaMidLevel cddaMidLevel = new CdparanoiaMidLevel();
        // CddaMidLevel cddaMidLevel = new CookedIoctlMidLevel();
        return cddaMidLevel;
    }
}


/* CddaUtils.java */
