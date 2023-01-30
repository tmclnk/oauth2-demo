package com.example.rs.webhook;


import java.util.Arrays;
import java.util.List;

public class WebhookPayload {
    public WebhookPayload(Command... commands) {
        this.commands = Arrays.asList(commands);
    }

    public static final class Command {
        public Command(String type, Value... value) {
            this.type = type;
            this.value = Arrays.asList(value);
        }

        public String type; // com.okta.access.patch
        public List<Value> value;
    }

    public static final class Value {
        public String op;
        public String path;
        public String value;

        public Value(String op, String path, String value) {
            this.op = op;
            this.path = path;
            this.value = value;
        }
    }

    public List<Command> commands;
}
