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
/**
 * DynamicObject.java
 * 
 * Created on Nov 3, 2015, 4:44:32 PM
 * 
 */
package edu.semeru.android.core.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {Insert class description here}
 *
 * @author Carlos Bernal
 * @since Nov 3, 2015
 */
public class DynamicObject implements Comparable<DynamicObject> {

    private int index;
    private String[] objects;

    /**
     * @param index
     * @param objects
     */
    public DynamicObject(int index, String... objects) {
        super();
        this.index = index;
        this.objects = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
            this.objects[i] = objects[i];
        }
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index
     *            the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return the objects
     */
    public String[] getObjects() {
        return objects;
    }

    /**
     * @param objects
     *            the objects to set
     */
    public void setObjects(String[] objects) {
        this.objects = objects;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(DynamicObject o) {
        if (this.index < o.getIndex()) {
            return -1;
        } else if (this.index > o.getIndex()) {
            return 1;
        } else {
            return 0;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{\"DynamicObject\" :{\"index\":\"" + index + "\", \"objects\":\"" + Arrays.toString(objects) + "\"}}";
    }

    public static void main(String[] args) {
        DynamicObject object = new DynamicObject(3, "3", "3", "3");
        List<DynamicObject> list1 = new ArrayList<DynamicObject>();

        list1.add(object);
        object = new DynamicObject(4, "4", "4", "4");
        list1.add(object);
        object = new DynamicObject(2, "2", "2", "2");
        list1.add(object);
        object = new DynamicObject(1, "1", "1", "1");
        list1.add(object);

        Collections.sort(list1);
        for (DynamicObject o : list1) {
            System.out.println(o);
        }
    }
}
