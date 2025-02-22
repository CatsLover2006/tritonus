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

package org.tritonus.saol.engine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

import org.tritonus.saol.compiler.Compiler;
import org.tritonus.share.TDebug;


/**
 * Main
 * <p>
 * This file is part of Tritonus: http://www.tritonus.org/
 */
public class Main {
    public static void main(String[] args) throws IOException {
        File saolFile = new File(args[0]);
        File saslFile = new File(args[1]);
        File outputFile = new File(args[2]);
        Compiler compiler = new Compiler(saolFile);
        try {
            compiler.compile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Class<AbstractInstrument>> instrumentMap = compiler.getInstrumentMap();
        TDebug.out("Main.main(): IM: " + instrumentMap);
        AudioFileFormat.Type targetType = AudioFileFormat.Type.WAVE;
        AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 2, 4, 44100.0F, false);
        SystemOutput output = new FileOutput(outputFile, targetType, audioFormat);
        RTSystem rtSystem = new RTSystem(output, instrumentMap);
        rtSystem.start();
        InputStream saslInputStream = Files.newInputStream(saslFile.toPath());
        SaslParser saslParser = new SaslParser(rtSystem, saslInputStream);
        new Thread(saslParser).start();
    }
}
