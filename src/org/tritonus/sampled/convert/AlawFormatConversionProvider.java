/*
 *	AlawFormatConversionProvider.java
 */

/*
 *  Copyright (c) 2000 by Florian Bomers <florian@bome.com>
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


package	org.tritonus.sampled.convert;


import	java.util.Arrays;

import	javax.sound.sampled.AudioSystem;
import	javax.sound.sampled.AudioFormat;
import	javax.sound.sampled.AudioFormat.Encoding;
import	javax.sound.sampled.AudioInputStream;

import	org.tritonus.TDebug;
import	org.tritonus.sampled.TConversionTool;

/**
 * This provider (currently) supports these conversions:
 * <ul><li>PCM 8 Signed -> alaw
 * <li>PCM 8 Unsigned -> alaw
 * <li>PCM 16 signed big endian -> alaw
 * <li>PCM 16 signed little endian -> alaw
 * <li>ulaw -> alaw
 * </ul>
 * and vice versa.
 * <p>FrameRate, SampleRate, Channels CANNOT be converted.
 *
 * @author Florian Bomers
 */

public class AlawFormatConversionProvider
	extends	TEncodingFormatConversionProvider
{
	private static final int ALL=AudioSystem.NOT_SPECIFIED;
	
	// TODO:
	// see UlawFormatConversionProvider
	
	/*private static final AudioFormat[]	INPUT_FORMATS =
	{
		// encoding, sampleRate, sampleSize, channels, frameSize, frameRate, bigEndian
		new AudioFormat(AudioFormat.Encoding.ALAW, ALL, 8, ALL, ALL, ALL, false),
		new AudioFormat(AudioFormat.Encoding.ALAW, ALL, 8, ALL, ALL, ALL, true)
	};*/
	
	private static final AudioFormat[]	OUTPUT_FORMATS =
	{
		new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, ALL, 8, ALL, ALL, ALL, false),
		new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, ALL, 8, ALL, ALL, ALL, false),
		new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, ALL, 8, ALL, ALL, ALL, false),
		new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, ALL, 8, ALL, ALL, ALL, false),
		new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, ALL, 8, ALL, ALL, ALL, true),
		new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, ALL, 8, ALL, ALL, ALL, true),
		new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, ALL, 8, ALL, ALL, ALL, true),
		new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, ALL, 8, ALL, ALL, ALL, true),
		new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, ALL, 16, ALL, ALL, ALL, false),
		new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, ALL, 16, ALL, ALL, ALL, false),
		new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, ALL, 16, ALL, ALL, ALL, true),
		new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, ALL, 16, ALL, ALL, ALL, true),
		new AudioFormat(AudioFormat.Encoding.ULAW, ALL, 8, ALL, ALL, ALL, false),
		new AudioFormat(AudioFormat.Encoding.ULAW, ALL, 8, ALL, ALL, ALL, true),

		new AudioFormat(AudioFormat.Encoding.ALAW, ALL, 8, ALL, ALL, ALL, false),
		new AudioFormat(AudioFormat.Encoding.ALAW, ALL, 8, ALL, ALL, ALL, true)
	};

	/**	Constructor.
	 */
	public AlawFormatConversionProvider()
	{
		//super(Arrays.asList(INPUT_FORMATS),
		//      Arrays.asList(OUTPUT_FORMATS));
		super(Arrays.asList(OUTPUT_FORMATS),
		      Arrays.asList(OUTPUT_FORMATS));
	}

	public AudioInputStream getAudioInputStream(AudioFormat targetFormat, AudioInputStream sourceStream) {
    	AudioFormat sourceFormat=sourceStream.getFormat();
    	// the non-conversion case
    	// TODO: does this work OK when some fields are AudioSystem.NOT_SPECIFIED ?
    	if (sourceFormat.matches(targetFormat)) {
    		return sourceStream;
		}
   		if (doMatch(targetFormat.getFrameRate(), sourceFormat.getFrameRate())
   				&& doMatch(targetFormat.getChannels(), sourceFormat.getChannels())) {
    		if (doMatch(targetFormat.getSampleSizeInBits(),8)
    			&& targetFormat.getEncoding()==AudioFormat.Encoding.ALAW) {
    			// OK, the targetFormat seems fine, so we convert it to ALAW
    			// let the remaining checks be done by ToAlawStream
    			return new ToAlawStream(sourceStream);
    		} else
    		if (doMatch(sourceFormat.getSampleSizeInBits(),8)
    			&& sourceFormat.getEncoding()==AudioFormat.Encoding.ALAW) {
    			// convert ALAW to the target format
    			return new FromAlawStream(sourceStream, targetFormat);
    		}
    	}
		throw new IllegalArgumentException("format conversion not supported");
    }

	private static final int UNSIGNED8=1;
	private static final int SIGNED8=2;
	private static final int BIG_ENDIAN16=3;
	private static final int LITTLE_ENDIAN16=4;
	private static final int ULAW=5;
	
	private boolean isSupportedFormat(AudioFormat format) {
		return getConvertType(format)!=0;
	}
	
	private int getConvertType(AudioFormat af) {
	   	int result=0;
	   	AudioFormat.Encoding encoding=af.getEncoding();
    	boolean bigEndian=af.isBigEndian();
    	int ssib=af.getSampleSizeInBits();
    	// now set up the convert type
    	if (encoding.equals(AudioFormat.Encoding.PCM_SIGNED)) {
    		if (ssib==16) {
    			if (bigEndian) {
    				result=BIG_ENDIAN16;
    			} else {
    				result=LITTLE_ENDIAN16;
    			}
    		} else
    		if (ssib==8) {
    			result=SIGNED8;
    		}
    	} else
    	if (encoding.equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
    		if (ssib==8) {
   				result=UNSIGNED8;
   			}
   		} else
    	if (encoding.equals(AudioFormat.Encoding.ULAW)) {
    		result=ULAW;
    	}
    	return result;
	}

	class ToAlawStream extends TSynchronousFilteredAudioInputStream {
		private int convertType;
		
		public ToAlawStream(AudioInputStream sourceStream) {
			// transform the targetFormat so that 
			// FrameRate, SampleRate, and Channels match the sourceFormat
			// we only retain encoding, samplesize and endian of targetFormat.
			super (sourceStream, new AudioFormat(
				AudioFormat.Encoding.ALAW,
				sourceStream.getFormat().getSampleRate(),
				8,
				sourceStream.getFormat().getChannels(),
				sourceStream.getFormat().getChannels(), // sampleSize
				sourceStream.getFormat().getFrameRate(),
				sourceStream.getFormat().isBigEndian()));
	    	convertType=getConvertType(sourceStream.getFormat());
		enableConvertInPlace();
			if (convertType==0)
				throw new IllegalArgumentException("format conversion not supported");
		}
		
		protected int convert(byte[] inBuffer, byte[] outBuffer, int outByteOffset, int inFrameCount) {
			int sampleCount=inFrameCount*getFormat().getChannels();
			switch (convertType) {
				case UNSIGNED8:		TConversionTool.pcm82alaw(inBuffer, 0, 
										outBuffer, outByteOffset, sampleCount, false); break;
				case SIGNED8:		TConversionTool.pcm82alaw(inBuffer, 0, 
										outBuffer, outByteOffset, sampleCount, true); break;
				case BIG_ENDIAN16: 	TConversionTool.pcm162alaw(inBuffer, 0, 
										outBuffer, outByteOffset, sampleCount, true); break;
				case LITTLE_ENDIAN16:TConversionTool.pcm162alaw(inBuffer, 0, 
										outBuffer, outByteOffset, sampleCount, false); break;
				case ULAW:			TConversionTool.ulaw2alaw(inBuffer, 0, 
										outBuffer, outByteOffset, sampleCount); break;
			}
			return inFrameCount;
		}

		protected void convertInPlace(byte[] buffer, int byteOffset, int frameCount) {
			int sampleCount=frameCount*getFormat().getChannels();
			switch (convertType) {
				case UNSIGNED8:		TConversionTool.pcm82alaw(
										buffer, byteOffset, sampleCount, false); break;
				case SIGNED8:		TConversionTool.pcm82alaw(
										buffer, byteOffset, sampleCount, true); break;
				case ULAW:			TConversionTool.ulaw2alaw(
										buffer, byteOffset, sampleCount); break;
				default: throw new RuntimeException("ToAlawStream: Call to convertInPlace, but it cannot convert in place.");
			}
		}
	}

	class FromAlawStream extends TSynchronousFilteredAudioInputStream {
		private int convertType;
		
		public FromAlawStream(AudioInputStream sourceStream, AudioFormat targetFormat) {
			// transform the targetFormat so that 
			// FrameRate, SampleRate, and Channels match the sourceFormat
			// we only retain encoding, samplesize and endian of targetFormat.
			super (sourceStream, new AudioFormat(
				targetFormat.getEncoding(),
				sourceStream.getFormat().getSampleRate(),
				targetFormat.getSampleSizeInBits(),
				sourceStream.getFormat().getChannels(),
				targetFormat.getSampleSizeInBits()*sourceStream.getFormat().getChannels()/8,
				sourceStream.getFormat().getFrameRate(),
				targetFormat.isBigEndian()));
	    	convertType=getConvertType(getFormat());
		enableConvertInPlace();
			if (convertType==0)
				throw new IllegalArgumentException("format conversion not supported");
		}
		
		protected int convert(byte[] inBuffer, byte[] outBuffer, int outByteOffset, int inFrameCount) {
			int sampleCount=inFrameCount*getFormat().getChannels();
			switch (convertType) {
				case UNSIGNED8:		TConversionTool.alaw2pcm8(inBuffer, 0, 
										outBuffer, outByteOffset, sampleCount, false); break;
				case SIGNED8:		TConversionTool.alaw2pcm8(inBuffer, 0, 
										outBuffer, outByteOffset, sampleCount, true); break;
				case BIG_ENDIAN16: 	TConversionTool.alaw2pcm16(inBuffer, 0, 
										outBuffer, outByteOffset, sampleCount, true); break;
				case LITTLE_ENDIAN16:TConversionTool.alaw2pcm16(inBuffer, 0, 
										outBuffer, outByteOffset, sampleCount, false); break;
				case ULAW:			TConversionTool.alaw2ulaw(inBuffer, 0, 
										outBuffer, outByteOffset, sampleCount); break;
			}
			return inFrameCount;
		}

		protected void convertInPlace(byte[] buffer, int byteOffset, int frameCount) {
			int sampleCount=frameCount*format.getChannels();
			switch (convertType) {
				case UNSIGNED8:		TConversionTool.alaw2pcm8(
										buffer, byteOffset, sampleCount, false); break;
				case SIGNED8:		TConversionTool.alaw2pcm8(
										buffer, byteOffset, sampleCount, true); break;
				case ULAW:			TConversionTool.alaw2ulaw(
										buffer, byteOffset, sampleCount); break;
				default: throw new RuntimeException("FromAlawStream: Call to convertInPlace, but it cannot convert in place.");
			}
		}
	}
	
}

/*** AlawFormatConversionProvider.java ***/
