package com.smartbear.ready.plugin.jira.impl;

import com.atlassian.jira.rest.client.api.domain.BasicComponent;

import java.util.ArrayList;

/**
 * Created by avdeev on 26.03.2015.
 */
public class Utils {
    public static Object[] IterableValuesToArray(Iterable<Object> input){
        ArrayList<Object> objects = new ArrayList<>();
        for (Object obj:input){
            if (obj instanceof BasicComponent) {
                objects.add(((BasicComponent) obj).getName());
            }
        }
        return objects.toArray();
    }

}
