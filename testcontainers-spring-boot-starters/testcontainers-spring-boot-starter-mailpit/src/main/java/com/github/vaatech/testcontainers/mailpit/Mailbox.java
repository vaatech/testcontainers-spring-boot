package com.github.vaatech.testcontainers.mailpit;

import com.github.vaatech.testcontainers.mailpit.model.*;
import com.github.vaatech.testcontainers.mailpit.rest.ApplicationRestApi;
import com.github.vaatech.testcontainers.mailpit.rest.MessageRestApi;
import com.github.vaatech.testcontainers.mailpit.rest.MessagesRestApi;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class Mailbox {

    private final ApplicationRestApi applicationApi;
    private final MessagesRestApi messagesApi;
    private final MessageRestApi messageApi;

    /**
     * Delete a single message.
     *
     * @param ID Database ID to delete
     */
    public Boolean delete(String ID) {
        final DeleteRequest request = new DeleteRequest();
        request.addIdsItem(ID);
        return messagesApi.delete(request);
    }

    /**
     * Delete all messages.
     */
    public Boolean clear() {
        final DeleteRequest request = new DeleteRequest();
        return messagesApi.delete(request);
    }

    /**
     * Search messages. Returns the latest messages matching a search.
     *
     * @param query Search query (required)
     * @param start Pagination offset (optional, default to 0)
     * @param limit Limit results (optional, default to 50)
     * @return List<Message>
     */
    public List<Message> find(String query, Integer start, Integer limit) {
        final List<Message> results = new ArrayList<>();
        final MessagesSummary messages = messagesApi.messagesSummary(query, start, limit);
        for (MessageSummary summary : Objects.requireNonNull(messages.getMessages())) {
            Message message = messageApi.message(summary.getID());
            results.add(message);
        }

        return results;
    }

    /**
     * Search messages. Returns the first message matching a search.
     *
     * @param query Search query (required)
     * @return Message
     */
    public Optional<Message> findFirst(String query) {
        final List<Message> results = find(query, 0, 1);
        if (results.isEmpty()) {
            return Optional.empty();
        }
        return results.stream().findFirst();
    }

    /**
     * Get application information
     * Returns basic runtime information, message totals and latest release version.
     *
     * @return AppInformation
     */
    public AppInformation getApplicationInfo() {
        return applicationApi.appInformation();
    }
}