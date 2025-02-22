/*
 * AbstractInstrument.java
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


public abstract class AbstractInstrument
        implements Output {
    private Output m_outputPort;
    private int m_nStartTime;
    private int m_nEndTime;


    protected AbstractInstrument() {
    }


    // should be a constructor argument, but is not to simplify instantiation and inheritance
    public void setOutput(Output output) {
        m_outputPort = output;
    }


    // should be a constructor argument, but is not to simplify instantiation and inheritance
    public void setStartAndEndTime(int nStartTime, int nEndTime) {
        m_nStartTime = nStartTime;
        m_nEndTime = nEndTime;
    }


    public int getStartTime() {
        return m_nStartTime;
    }


    public int getEndTime() {
        return m_nEndTime;
    }


    public void doIPass(RTSystem rtSystem) {
    }


    public void doKPass(RTSystem rtSystem) {
    }


    public void doAPass(RTSystem rtSystem) {
    }


    /**
     * Gives the width of the output port.
     *
     * @returns width of the output port
     * (number of channels)
     */
    public int getWidth() {
        return m_outputPort.getWidth();
    }


    /**
     * Initiate the output port of the instrument.
     * Sets the values of all samples to 0.0.
     * This method must be called in an a-cycle before
     * this instrument's a-cycle code is executed.
     */
    public void clear() {
        m_outputPort.clear();
    }


    /**
     * Add the sample value of one instrument.
     * This method can be called by instrument's a-cycle
     * code to output the sample value the instrument has
     * calculated for this a-cycle.
     */
    public void output(float fSample) {
        m_outputPort.output(fSample);
    }


    /**
     * Add sample values of one instrument.
     * This method can be called by instrument's a-cycle
     * code to output the sample value the instrument has
     * calculated for this a-cycle.
     * The current hacky version allows only for mono samples.
     */
    public void output(float[] afSamples) {
        m_outputPort.output(afSamples);
    }
}


/* AbstractInstrument.java */
