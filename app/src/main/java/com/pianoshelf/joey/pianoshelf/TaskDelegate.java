package com.pianoshelf.joey.pianoshelf;

/**
 * Delegate to communicate between LoginTask and LoginView
 * Created by joey on 11/25/14.
 */
public interface TaskDelegate {
    void taskCompleted(String s);
}
