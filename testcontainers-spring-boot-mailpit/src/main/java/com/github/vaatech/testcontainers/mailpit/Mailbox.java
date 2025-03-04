package com.github.vaatech.testcontainers.mailpit;

import com.github.vaatech.testcontainers.mailpit.model.*;
import com.github.vaatech.testcontainers.mailpit.rest.ApplicationApi;
import com.github.vaatech.testcontainers.mailpit.rest.MessageApi;
import com.github.vaatech.testcontainers.mailpit.rest.MessagesApi;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.*;

@RequiredArgsConstructor
public class Mailbox {

    private final ApplicationApi applicationApi;
    private final MessagesApi messagesApi;
    private final MessageApi messageApi;

    /**
     * Delete a single message.
     *
     * @param ID Database ID to delete
     */
    @SneakyThrows
    public String delete(String ID) {
        final DeleteMessagesParamsRequest request = new DeleteMessagesParamsRequest();
        request.addIdsItem(ID);
        return messagesApi.deleteMessagesParams(request);
    }

    /**
     * Delete all messages.
     */
    @SneakyThrows
    public String clear() {
        final DeleteMessagesParamsRequest request = new DeleteMessagesParamsRequest();
        return messagesApi.deleteMessagesParams(request);
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
        return find(query, start, limit, null);
    }


    /**
     * Search messages. Returns the latest messages matching a search.
     *
     * @param query    Search query (required)
     * @param start    Pagination offset (optional, default to 0)
     * @param limit    Limit results (optional, default to 50)
     * @param timeZone Specify a timezone for before: and after: queries (optional, default null)
     * @return List<Message>
     */
    @SneakyThrows
    public List<Message> find(String query, Integer start, Integer limit, TimeZone timeZone) {
        final List<Message> results = new ArrayList<>();
        String startStr = null;
        if (start != null) {
            startStr = start.toString();
        }
        String limitStr = null;
        if (limit != null) {
            limitStr = limit.toString();
        }
        String timezoneID = null;
        if (timeZone != null) {
            timezoneID = timeZone.getID();
        }
        final MessagesSummary messages = messagesApi.searchParams(query, startStr, limitStr, timezoneID);
        for (MessageSummary summary : Objects.requireNonNull(messages.getMessages())) {
            Message message = messageApi.getMessageParams(summary.getID());
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
    @SneakyThrows
    public AppInformation getApplicationInfo() {
        return applicationApi.appInformation();
    }
}