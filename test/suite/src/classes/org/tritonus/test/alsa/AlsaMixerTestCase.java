/*
 *	AlsaMixerTestCase.java
 */

/*
 *  Copyright (c) 2001 - 2002 by Matthias Pfisterer <Matthias.Pfisterer@web.de>
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

package org.tritonus.test.alsa;

import java.util.List;

import junit.framework.TestCase;

//import org.tritonus.lowlevel.alsa.AlsaCtl;
import org.tritonus.lowlevel.alsa.AlsaMixer;



public class AlsaMixerTestCase
extends TestCase
{
	private static final boolean	DEBUG = true;



	public AlsaMixerTestCase(String strName)
	{
		super(strName);
	}



	public void testOpenClose()
		throws Exception
	{
		int	nDefaultMixerCard = 0;
		String	strMixerName = "hw:" + nDefaultMixerCard;
		AlsaMixer	mixer = new AlsaMixer(strMixerName);
		assertTrue(mixer != null);
		mixer.close();
		// Intentionally a second time to test idempotence of close().
		mixer.close();
	}



	public void testControls()
		throws Exception
	{
		int	nDefaultMixerCard = 0;
		String	strMixerName = "hw:" + nDefaultMixerCard;
		AlsaMixer	mixer = new AlsaMixer(strMixerName);
		List	controlsList = null;	// mixer.getControls();
		assertTrue(controlsList != null);
		assertTrue(controlsList.size() > 0);
		mixer.close();
	}

}



/*** AlsaMixerTestCase.java ***/
