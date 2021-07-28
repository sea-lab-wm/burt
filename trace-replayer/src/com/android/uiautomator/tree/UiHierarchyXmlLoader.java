/*******************************************************************************
 * Copyright (c) 2016, SEMERU
 * All rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 *******************************************************************************/

package com.android.uiautomator.tree;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class UiHierarchyXmlLoader {

    private BasicTreeNode mRootNode;
    private String mXmlPath;
    private InputStream mInputStream;

    public UiHierarchyXmlLoader() {
    }

    /**
     * Uses a SAX parser to process XML dump
     * 
     * @param xmlPath
     * @return
     */
    public BasicTreeNode parseXml(String xmlPath) {
        this.mXmlPath = xmlPath;
        this.mInputStream = null;
        return parseXml();
    }

    /**
     * Uses a SAX parser to process XML dump
     * 
     * @param xmlContent
     * @return
     */
    public BasicTreeNode parseXml(InputStream xmlContent) {
        this.mXmlPath = null;
        this.mInputStream = xmlContent;
        return parseXml();
    }

    /**
     * Uses a SAX parser to process XML dump
     * 
     * @param xmlPath
     * @return
     */
    private BasicTreeNode parseXml() {
        mRootNode = null;
        // standard boilerplate to get a SAX parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = null;
        try {
            parser = factory.newSAXParser();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        }
        // handler class for SAX parser to receiver standard parsing events:
        // e.g. on reading "<foo>", startElement is called, on reading "</foo>",
        // endElement is called
        DefaultHandler handler = new DefaultHandler() {
            BasicTreeNode mParentNode;
            BasicTreeNode mWorkingNode;

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes)
                    throws SAXException {
                boolean nodeCreated = false;
                // starting an element implies that the element that has not yet
                // been closed
                // will be the parent of the element that is being started here
                mParentNode = mWorkingNode;
                if ("hierarchy".equals(qName)) {
                    mWorkingNode = new RootWindowNode(attributes.getValue("windowName"),
                            attributes.getValue("rotation"));
                    nodeCreated = true;
                } else if ("node".equals(qName)) {
                    UiNode tmpNode = new UiNode();
                    // System.out.println("-------");
                    for (int i = 0; i < attributes.getLength(); i++) {
                        // System.out.println(attributes.getQName(i) + " - " +
                        // attributes.getValue(i));
                        tmpNode.addAtrribute(attributes.getQName(i), attributes.getValue(i));
                    }
                    mWorkingNode = tmpNode;
                    nodeCreated = true;
                }
                // nodeCreated will be false if the element started is neither
                // "hierarchy" nor "node"
                if (nodeCreated) {
                    if (mRootNode == null) {
                        // this will only happen once
                        mRootNode = mWorkingNode;
                    }
                    if (mParentNode != null) {
                        mParentNode.addChild(mWorkingNode);
                    }
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                // mParentNode should never be null here in a well formed XML
                if (mParentNode != null) {
                    // closing an element implies that we are back to working on
                    // the parent node of the element just closed, i.e. continue
                    // to
                    // parse more child nodes
                    mWorkingNode = mParentNode;
                    mParentNode = mParentNode.getParent();
                }
            }
        };
        try {
            if (mXmlPath != null) {
                parser.parse(new File(mXmlPath), handler);
            } else {
                parser.parse(mInputStream, handler);
            }
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return mRootNode;
    }
}
