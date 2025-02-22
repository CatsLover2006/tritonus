/*
 * Output.java
 *
 * This file is part of Tritonus: http://www.tritonus.org/
 */

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

/*
|<---            this code is formatted to fit into 80 columns             --->|
*/

package org.tritonus.saol.engine;


/**
 * Output method for the SA engine.
 * This interface abstracts the way calculated samples are
 * output from the engine. The engine only calls this interface,
 * while implementations of this interface write the samples to a
 * file, a line, a network socket or whatever else.
 *
 * @author Matthias Pfisterer
 */
public interface Output {
    /**
     * Gives the width of this output.
     *
     * @returns width of the output (number of channels)
     */
    int getWidth();


    /**
     * Initiate the cumulation of a sample value.
     * Sets the values of all samples to 0.0.
     * This method must be called in an a-cycle before
     * any instrument's a-cycle code is executed.
     */
    void clear();


    /**
     * Add the sample value of one instrument.
     * This method can be called by instrument's a-cycle
     * code to output the sample value the instrument has
     * calculated for this a-cycle.
     * The current hacky version allows only for mono samples.
     */
    void output(float fSample);


    /**
     * Add sample values of one instrument.
     * This method can be called by instrument's a-cycle
     * code to output the sample value the instrument has
     * calculated for this a-cycle.
     * The current hacky version allows only for mono samples.
     */
    void output(float[] afSamples);
}


/* Output.java */
