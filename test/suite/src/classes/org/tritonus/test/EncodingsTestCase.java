/*
 *	EncodingsTestCase.java
 */

/*
 *  Copyright (c) 2001 - 2002 by Matthias Pfisterer
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

package org.tritonus.test;

import junit.framework.TestCase;

import javax.sound.sampled.AudioFormat;

import org.tritonus.share.sampled.Encodings;



/**	Tests for class org.tritonus.share.sampled.Encodings.
 */
public class EncodingsTestCase
extends TestCase
{
	public EncodingsTestCase(String strName)
	{
		super(strName);
	}



	public void testGetEncodings()
	{
		AudioFormat.Encoding[]	aEncodings = Encodings.getEncodings();
		assertTrue(aEncodings != null);
		assertTrue(aEncodings.length > 0);
	}

}



/*** EncodingsTestCase.java ***/
