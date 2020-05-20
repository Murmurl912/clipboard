package com.example.clipboard.client.lifecycle.event.clipboard;

import com.example.clipboard.client.lifecycle.event.AppEvent;

public class ClipboardReportEvent extends AppEvent {

    public ClipboardReportEvent(String text) {
        super(text);
    }

    @Override
    public String getSource() {
        return (String)super.source;
    }

}
