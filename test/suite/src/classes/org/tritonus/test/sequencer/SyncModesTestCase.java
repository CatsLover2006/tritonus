/*
 *	SyncModesTestCase.java
 */

/*
 *  Copyright (c) 2003 by Matthias Pfisterer
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

package org.tritonus.test.sequencer;

import javax.sound.midi.Sequencer;
import javax.sound.midi.Sequencer.SyncMode;



/**	Tests for class javax.sound.midi.MidiMessage.
 */
public class SyncModesTestCase
extends BaseSequencerTestCase
{
	private static final Sequencer.SyncMode[] MASTER_SYNC_MODES =
	{
		Sequencer.SyncMode.INTERNAL_CLOCK,
		Sequencer.SyncMode.MIDI_SYNC,
		Sequencer.SyncMode.MIDI_TIME_CODE
	};

	private static final Sequencer.SyncMode[] SLAVE_SYNC_MODES =
	{
		Sequencer.SyncMode.NO_SYNC,
		Sequencer.SyncMode.MIDI_SYNC,
		Sequencer.SyncMode.MIDI_TIME_CODE
	};


	public SyncModesTestCase(String strName)
	{
		super(strName);
	}



	protected void checkSequencer(Sequencer seq)
		throws Exception
	{
		Sequencer.SyncMode syncMode;
		Sequencer.SyncMode[] syncModes;

		// slave sync modes
		syncMode = seq.getSlaveSyncMode();
		assertNotNull("initial slave sync mode", syncMode);
		assertTrue(isSlaveSyncMode(syncMode));

		syncModes = seq.getSlaveSyncModes();
		assertNotNull("available slave sync modes",
					  syncModes);
		assertTrue("number of available slave sync modes",
				   syncModes.length >= 1);
		for (int i = 0; i < syncModes.length; i++)
		{
			assertTrue(isSlaveSyncMode(syncModes[i]));
			checkSyncModeAccepted(seq, syncModes[i], false);
		}
		checkSyncModeAccepted(seq, Sequencer.SyncMode.NO_SYNC, false);

		// master sync modes
		syncMode = seq.getMasterSyncMode();
		assertNotNull("initial master sync mode", syncMode);
		assertTrue(isMasterSyncMode(syncMode));

		syncModes = seq.getMasterSyncModes();
		assertNotNull("available master sync modes",
					  syncModes);
		assertTrue("number of available master sync modes",
				   syncModes.length >= 1);
		for (int i = 0; i < syncModes.length; i++)
		{
			assertTrue(isMasterSyncMode(syncModes[i]));
			checkSyncModeAccepted(seq, syncModes[i], true);
		}
		checkSyncModeAccepted(seq, Sequencer.SyncMode.INTERNAL_CLOCK, true);
	}


	private void checkSyncModeAccepted(Sequencer seq,
									   Sequencer.SyncMode syncMode,
									   boolean bMaster)
	{
		String strErrorMessage = constructErrorMessage(seq, syncMode,
													   bMaster);
		if (bMaster)
		{
			seq.setMasterSyncMode(syncMode);
			assertSame(strErrorMessage, syncMode, seq.getMasterSyncMode());
		}
		else
		{
			seq.setSlaveSyncMode(syncMode);
			assertSame(strErrorMessage, syncMode, seq.getSlaveSyncMode());
		}
	}



	private boolean isMasterSyncMode(SyncMode syncMode)
	{
		return contains(MASTER_SYNC_MODES, syncMode);
	}


	private boolean isSlaveSyncMode(SyncMode syncMode)
	{
		return contains(SLAVE_SYNC_MODES, syncMode);
	}


	private boolean contains(SyncMode[] list, SyncMode test)
	{
		for (int i = 0; i < list.length; i++)
		{
			if (list[i].equals(test))
			{
				return true;
			}
		}
		return false;
	}



	private static String constructErrorMessage(Sequencer seq,
												Sequencer.SyncMode syncMode,
												boolean bMaster)
	{
		String strMessage =  seq.getDeviceInfo().getName() + ": ";
		strMessage += syncMode.toString();
		strMessage += bMaster ? " as master " : " as slave ";
		return strMessage;
	}
}



/*** SyncModesTestCase.java ***/
