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

import org.tritonus.saol.sablecc.analysis.DepthFirstAdapter;
import org.tritonus.saol.sablecc.node.Node;
import org.tritonus.saol.sablecc.node.Token;


/**
 * Simple AST walker. This is simple Abstract Syntax Tree walker
 * which will visit each of the nodes and print on standard output the
 * name of particular node visited. This class can be very usefull while
 * testing the tree structure for a given input. Check PrintTree class.
 *
 * @author Mariusz Nowostawski
 */
public class PrintWalker extends DepthFirstAdapter {

    private static final int INDENT_STEP = 3;

    private int indent = 0;

    void indent() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            s.append(" ");
        }
        System.out.print(s);
    }

    public void defaultIn(Node node) {
        indent();
        System.out.print("in ");
        String str = getStrippedClassName(node);
        System.out.println(str);
        indent += INDENT_STEP;
    }

    public void defaultOut(Node node) {
        indent -= INDENT_STEP;
        indent();
        System.out.print("out ");
        String str = getStrippedClassName(node);
        System.out.println(str);
    }

    public void defaultCase(Node node) {
        indent();
        System.out.print("case ");
        String str = getStrippedClassName(node) + ": " + ((Token) node).getText();
        System.out.println(str);
    }

    private static String getStrippedClassName(Node node) {
        String strClassName = node.getClass().getName();
        return stripClassName(strClassName);
    }

    private static String stripClassName(String strClassName) {
        int nLastDotPosition = strClassName.lastIndexOf(".");
        if (nLastDotPosition >= 0) {
            strClassName = strClassName.substring(nLastDotPosition + 1);
        }
        return strClassName;
    }
}

/* PrintWalker.java */
