/*
 *	AlsaMidiOut.java
 */

/*
 *  Copyright (c) 1999 - 2001 by Matthias Pfisterer <Matthias.Pfisterer@gmx.de>
 *
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */


package	org.tritonus.midi.device.alsa;

import	javax.sound.midi.Sequence;
import	javax.sound.midi.Sequencer;
import	javax.sound.midi.Track;
import	javax.sound.midi.MidiMessage;
import	javax.sound.midi.ShortMessage;
import	javax.sound.midi.SysexMessage;
import	javax.sound.midi.MetaMessage;

import	org.tritonus.share.TDebug;
import	org.tritonus.lowlevel.alsa.AlsaSeq;



public class AlsaMidiOut
{
	/**	The low-level object to interface to the ALSA sequencer.
	 */
	private AlsaSeq		m_alsaSeq;

	/**	The source port to use for sending messages via the ALSA sequencer.
	 */
	private int		m_nSourcePort;

	/**	The sequencer queue to use inside the ALSA sequencer.
	 *	This value is only used (and valid) if m_bImmediately
	 *	false. Otherwise, events are sent directely to the destination
	 *	client, circumventing queues.
	 */
	private int		m_nQueue;

	private boolean		m_bImmediately;

	private boolean		m_bHandleMetaMessages;

	private AlsaSeq.Event	m_event = new AlsaSeq.Event();



	/*
	 *	Sends to all subscribers via queue.
	 */
	public AlsaMidiOut(AlsaSeq aSequencer, int nSourcePort,
			   int nQueue)
	{
		this(aSequencer, nSourcePort,
		     nQueue, false);
	}



	/*
	 *	Sends to all subscribers immediately.
	 */
	public AlsaMidiOut(AlsaSeq aSequencer, int nSourcePort)
	{
		this(aSequencer, nSourcePort,
		     -1, true);
	}



	private AlsaMidiOut(AlsaSeq aSequencer, int nSourcePort,
			    int nQueue, boolean bImmediately)
	{
		if (TDebug.TraceAlsaMidiOut) { TDebug.out("AlsaMidiOut.<init>(AlsaSeq, int, int, boolean): begin"); }
		m_alsaSeq = aSequencer;
		m_nSourcePort = nSourcePort;
		m_nQueue = nQueue;
		m_bImmediately = bImmediately;
		m_bHandleMetaMessages = false;
		if (TDebug.TraceAlsaMidiOut) { TDebug.out("AlsaMidiOut.<init>(AlsaSeq, int, int, boolean): end"); }
	}



	private AlsaSeq getAlsaSeq()
	{
		return m_alsaSeq;
	}



	public void subscribe(int nDestClient, int nDestPort)
	{
		if (TDebug.TraceAlsaMidiOut) { TDebug.out("AlsaMidiOut.subscribe(): begin"); }
		getAlsaSeq().subscribePort(
			getAlsaSeq().getClientId(), getSourcePort(),
			nDestClient, nDestPort);
		if (TDebug.TraceAlsaMidiOut) { TDebug.out("AlsaMidiOut.subscribe(): end"); }
	}



	public void unsubscribe(int nDestClient, int nDestPort)
	{
		if (TDebug.TraceAlsaMidiOut) { TDebug.out("AlsaMidiOut.unsubscribe(): begin"); }
/*	TODO:
		getAlsaSeq().subscribePort(
			getAlsaSeq().getClientId(), getSourcePort(),
			nDestClient, nDestPort);
*/
		if (TDebug.TraceAlsaMidiOut) { TDebug.out("AlsaMidiOut.unsubscribe(): end"); }
	}



	private int getSourcePort()
	{
		return m_nSourcePort;
	}



	private int getQueue()
	{
		return m_nQueue;
	}



	private boolean getImmediately()
	{
		return m_bImmediately;
	}


	public boolean getHandleMetaMessages()
	{
		return m_bHandleMetaMessages;
	}



	public void setHandleMetaMessages(boolean bHandleMetaMessages)
	{
		m_bHandleMetaMessages = bHandleMetaMessages;
	}



	public synchronized void enqueueMessage(MidiMessage event, long lTick)
	{
		if (TDebug.TraceAlsaMidiOut) { TDebug.out("AlsaMidiOut.enqueueMessage(): begin"); }
		if (event instanceof ShortMessage)
		{
			enqueueShortMessage((ShortMessage) event, lTick);
		}
		else if (event instanceof SysexMessage)
		{
			enqueueSysexMessage((SysexMessage) event, lTick);
		}
		else if (event instanceof MetaMessage && getHandleMetaMessages())
		{
			enqueueMetaMessage((MetaMessage) event, lTick);
		}
		else
		{
			// Ignore it.
		}
		if (TDebug.TraceAlsaMidiOut) { TDebug.out("AlsaMidiOut.enqueueMessage(): end"); }
	}



	private void enqueueShortMessage(ShortMessage shortMessage, long lTime)
	{
		int nChannel = shortMessage.getChannel();
		switch (shortMessage.getCommand())
		{
		case ShortMessage.NOTE_OFF:
			sendNoteOffEvent(lTime, nChannel, shortMessage.getData1(), shortMessage.getData2());
			break;

		case ShortMessage.NOTE_ON:
			sendNoteOnEvent(lTime, nChannel, shortMessage.getData1(), shortMessage.getData2());
			break;

		case ShortMessage.POLY_PRESSURE:
			sendKeyPressureEvent(lTime, nChannel, shortMessage.getData1(), shortMessage.getData2());
			break;

		case ShortMessage.CONTROL_CHANGE:
			sendControlChangeEvent(lTime, nChannel, shortMessage.getData1(), shortMessage.getData2());
			break;

		case ShortMessage.PROGRAM_CHANGE:
			sendProgramChangeEvent(lTime, nChannel, shortMessage.getData1());
			break;

		case ShortMessage.CHANNEL_PRESSURE:
			sendChannelPressureEvent(lTime, nChannel, shortMessage.getData1());
			break;

		case ShortMessage.PITCH_BEND:
			sendPitchBendEvent(lTime, nChannel, get14bitValue(shortMessage.getData1(), shortMessage.getData2()));
			break;

		case 0xF0:
			switch (shortMessage.getStatus())
			{
			case ShortMessage.MIDI_TIME_CODE:
				break;

			case ShortMessage.SONG_POSITION_POINTER:
				break;

			case ShortMessage.SONG_SELECT:
				break;

			case ShortMessage.TUNE_REQUEST:
				break;

			case ShortMessage.TIMING_CLOCK:
				break;

			case ShortMessage.START:
				break;

			case ShortMessage.CONTINUE:
				break;

			case ShortMessage.STOP:
				break;

			case ShortMessage.ACTIVE_SENSING:
				break;

			case ShortMessage.SYSTEM_RESET:
				break;

			default:
				TDebug.out("AlsaMidiOut.enqueueShortMessage(): UNKNOWN EVENT TYPE: " +shortMessage.getStatus() );
			}
			break;

		default:
			TDebug.out("AlsaMidiOut.enqueueShortMessage(): UNKNOWN EVENT TYPE: " +shortMessage.getStatus() );
		}
	}



	public static int get14bitValue(int nLSB, int nMSB)
	{
		return (nLSB & 0x7F) | ((nMSB & 0x7F) << 7);
	}



	private void sendNoteOffEvent(long lTime, int nChannel, int nNote, int nVelocity)
	{
		sendNoteEvent(AlsaSeq.SND_SEQ_EVENT_NOTEOFF, lTime, nChannel, nNote, nVelocity);
	}



	private void sendNoteOnEvent(long lTime, int nChannel, int nNote, int nVelocity)
	{
		sendNoteEvent(AlsaSeq.SND_SEQ_EVENT_NOTEON, lTime, nChannel, nNote, nVelocity);
	}



	private void sendNoteEvent(int nType, long lTime, int nChannel, int nNote, int nVelocity)
	{
		setCommon(nType, 0, lTime);
		m_event.setNote(nChannel, nNote, nVelocity, 0, 0);
		sendEvent();
	}



	private void sendKeyPressureEvent(long lTime, int nChannel, int nNote, int nPressure)
	{
		sendControlEvent(AlsaSeq.SND_SEQ_EVENT_KEYPRESS, lTime, nChannel, nNote, nPressure);
	}



	private void sendControlChangeEvent(long lTime, int nChannel, int nControl, int nValue)
	{
		sendControlEvent(AlsaSeq.SND_SEQ_EVENT_CONTROLLER, lTime, nChannel, nControl, nValue);
	}



	private void sendProgramChangeEvent(long lTime, int nChannel, int nProgram)
	{
		sendControlEvent(AlsaSeq.SND_SEQ_EVENT_PGMCHANGE, lTime, nChannel, 0, nProgram);
	}



	private void sendChannelPressureEvent(long lTime, int nChannel, int nPressure)
	{
		sendControlEvent(AlsaSeq.SND_SEQ_EVENT_CHANPRESS, lTime, nChannel, 0, nPressure);
	}



	// TODO: recheck!!!!
	private void sendPitchBendEvent(long lTime, int nChannel, int nPitch)
	{
		sendControlEvent(AlsaSeq.SND_SEQ_EVENT_PITCHBEND, lTime, nChannel, 0, nPitch);
	}



	private void sendControlEvent(int nType, long lTime, int nChannel, int nParam, int nValue)
	{
		setCommon(nType, 0, lTime);
		m_event.setControl(nChannel, nParam, nValue);
		sendEvent();
	}



	private void enqueueSysexMessage(SysexMessage message, long lTick)
	{
		// data returned by getData() never contain a status byte
		byte[]	abData = message.getData();
		// TDebug.out("data length: " + abData.length);
		byte[]	abTransferData = null;
		if (message.getStatus() == 0xF0)
		{
			abTransferData = new byte[abData.length + 1];
			abTransferData[0] = (byte) 0xF0;
			System.arraycopy(abData, 0, abTransferData, 1, abData.length);
		}
		else
		{
			abTransferData = abData;
		}
		sendVarEvent(AlsaSeq.SND_SEQ_EVENT_SYSEX, lTick, abTransferData, abTransferData.length);
	}



	private void enqueueMetaMessage(MetaMessage message, long lTick)
	{
		/*
		 *	We pack the type byte in front of the data bytes.
		 */
		byte[]	abData = message.getData();
		byte[]	abTransferData = new byte[abData.length + 1];
		abTransferData[0] = (byte) message.getType();
		System.arraycopy(abData, 0, abTransferData, 1, abData.length);
		// TDebug.out("message data length: " + abTransferData.length);
		// TDebug.out("message length: " + message.getLength());
		sendVarEvent(AlsaSeq.SND_SEQ_EVENT_USR_VAR4, lTick, abTransferData, abTransferData.length);
	}



	private void sendVarEvent(int nType, long lTime, byte[] abData, int nLength)
	{
		setCommon(nType, AlsaSeq.SND_SEQ_EVENT_LENGTH_VARIABLE, lTime);
		m_event.setVar(abData, 0, nLength);
		sendEvent();
	}



	private void setCommon(int nType, int nAdditionalFlags, long lTime)
	{
		if (getImmediately())
		{
			if (TDebug.TraceAlsaMidiOut) { TDebug.out("AlsaMidiOut.enqueueShortMessage(): sending noteoff message (immediately)"); }
		m_event.setCommon(nType, AlsaSeq.SND_SEQ_TIME_STAMP_REAL | AlsaSeq.SND_SEQ_TIME_MODE_REL | nAdditionalFlags, 0, AlsaSeq.SND_SEQ_QUEUE_DIRECT, 0L,
		0, getSourcePort(), AlsaSeq.SND_SEQ_ADDRESS_SUBSCRIBERS, AlsaSeq.SND_SEQ_ADDRESS_UNKNOWN);
		}
		else	// send via queue
		{
			if (TDebug.TraceAlsaMidiOut) { TDebug.out("AlsaMidiOut.enqueueShortMessage(): sending noteoff message (timed)"); }
		m_event.setCommon(nType, AlsaSeq.SND_SEQ_TIME_STAMP_TICK | AlsaSeq.SND_SEQ_TIME_MODE_ABS | nAdditionalFlags, 0, getQueue(), lTime,
		0, getSourcePort(), AlsaSeq.SND_SEQ_ADDRESS_SUBSCRIBERS, AlsaSeq.SND_SEQ_ADDRESS_UNKNOWN);
		}
	}


	/**	Puts the event into the queue.
	 */
	private void sendEvent()
	{
		getAlsaSeq().eventOutput(m_event);
		getAlsaSeq().drainOutput();
	}
}



/*** AlsaMidiOut.java ***/
