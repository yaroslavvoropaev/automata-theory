package org.parser.interfaces;

import java.util.Set;
import java.util.Map;

public interface IHandler {
    boolean handleString(String str);
    Map<String, Set<Character>> getStatistics();
}
