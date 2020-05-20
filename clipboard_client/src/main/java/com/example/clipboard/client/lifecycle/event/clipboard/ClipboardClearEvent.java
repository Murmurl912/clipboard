package com.example.clipboard.client.lifecycle.event.clipboard;

import com.example.clipboard.client.lifecycle.event.ClipboardEvent;

/**
 * indicate contents in local cache has benn cleared
 */
public class ClipboardClearEvent extends ClipboardEvent {
    public ClipboardClearEvent(Object source) {
        super(source);
    }
}
