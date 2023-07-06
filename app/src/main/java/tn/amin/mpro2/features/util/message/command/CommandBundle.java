package tn.amin.mpro2.features.util.message.command;

import tn.amin.mpro2.messaging.MessageSender;

public class CommandBundle {
    MessageSender messageSender;

    public CommandBundle(MessageSender messageSender) {
        this.messageSender = messageSender;
    }
}
