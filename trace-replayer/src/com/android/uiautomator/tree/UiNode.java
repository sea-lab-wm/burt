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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UiNode extends BasicTreeNode {
    private static final Pattern BOUNDS_PATTERN = Pattern
            .compile("\\[-?(\\d+),-?(\\d+)\\]\\[-?(\\d+),-?(\\d+)\\]");
    // use LinkedHashMap to preserve the order of the attributes
    private final Map<String, String> mAttributes = new LinkedHashMap<String, String>();
    private String mDisplayName = "ShouldNotSeeMe";
    private Object[] mCachedAttributesArray;

    public void addAtrribute(String key, String value) {
        mAttributes.put(key, value);
        updateDisplayName();
        if ("bounds".equals(key)) {
            updateBounds(value);
        }
    }

    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(mAttributes);
    }

    /**
     * Builds the display name based on attributes of the node
     */
    private void updateDisplayName() {
        String className = mAttributes.get("class");
        if (className == null)
            return;
        String text = mAttributes.get("text");
        if (text == null)
            return;
        String contentDescription = mAttributes.get("content-desc");
        if (contentDescription == null)
            return;
        String index = mAttributes.get("index");
        if (index == null)
            return;
        String bounds = mAttributes.get("bounds");
        if (bounds == null) {
            return;
        }
        // shorten the standard class names, otherwise it takes up too much space on UI
        className = className.replace("android.widget.", "");
        className = className.replace("android.view.", "");
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        builder.append(index);
        builder.append(") ");
        builder.append(className);
        if (!text.isEmpty()) {
            builder.append(':');
            builder.append(text);
        }
        if (!contentDescription.isEmpty()) {
            builder.append(" {");
            builder.append(contentDescription);
            builder.append('}');
        }
        builder.append(' ');
        builder.append(bounds);
        mDisplayName = builder.toString();
    }

    private void updateBounds(String bounds) {
        Matcher m = BOUNDS_PATTERN.matcher(bounds);
        if (m.matches()) {
            x = Integer.parseInt(m.group(1));
            y = Integer.parseInt(m.group(2));
            width = Integer.parseInt(m.group(3)) - x;
            height = Integer.parseInt(m.group(4)) - y;
            mHasBounds = true;
        } else {
            throw new RuntimeException("Invalid bounds: " + bounds);
        }
    }

    @Override
    public String toString() {
        return mDisplayName;
    }

    public String getAttribute(String key) {
        return mAttributes.get(key);
    }
    
    @Override
    public Object[] getAttributesArray() {
        // this approach means we do not handle the situation where an attribute is added
        // after this function is first called. This is currently not a concern because the
        // tree is supposed to be readonly
        if (mCachedAttributesArray == null) {
            mCachedAttributesArray = new Object[mAttributes.size()];
            int i = 0;
            for (String attr : mAttributes.keySet()) {
                mCachedAttributesArray[i++] = new AttributePair(attr, mAttributes.get(attr));
            }
        }
        return mCachedAttributesArray;
    }
}
