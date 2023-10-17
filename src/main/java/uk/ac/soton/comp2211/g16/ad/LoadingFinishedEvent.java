package uk.ac.soton.comp2211.g16.ad;

import javafx.event.Event;
import javafx.event.EventType;

public class LoadingFinishedEvent extends Event {
    public static final EventType<LoadingFinishedEvent> LOADING_FINISHED = new EventType<>(Event.ANY, "LOADING_FINISHED");

    public LoadingFinishedEvent() {
        super(LOADING_FINISHED);
    }
}
