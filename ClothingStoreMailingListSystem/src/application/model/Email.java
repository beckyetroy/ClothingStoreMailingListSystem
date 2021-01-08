package application.model;

import java.time.LocalDateTime;
import java.util.PriorityQueue;

public class Email implements PriorityQueueInterface {

    PriorityQueue<Integer> PriorityQueue = new PriorityQueue<>();

    private String username;
    private String title;
    private String dateTimeSent;
    private String messageBody;
    private int priority;

    public Email(String username, String title, String dateTimeSent, String messageBody, int priority) {
        this.username = username;
        this.title = title;
        this.dateTimeSent = dateTimeSent;
        this.messageBody = messageBody;
        this.priority = priority;
        add(priority);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateTimeSent() {
        return dateTimeSent;
    }

    public void setDateTimeSent(String dateTimeSent) {
        this.dateTimeSent = dateTimeSent;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public java.util.PriorityQueue<Integer> getPriorityQueue() {
        return PriorityQueue;
    }

    public void setPriorityQueue(java.util.PriorityQueue<Integer> priorityQueue) {
        PriorityQueue = priorityQueue;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Email{" +
                "PriorityQueue=" + PriorityQueue +
                ", username='" + username + '\'' +
                ", title='" + title + '\'' +
                ", dateTimeSent=" + dateTimeSent +
                ", messageBody='" + messageBody + '\'' +
                ", priority=" + priority +
                '}';
    }

    @Override
    public void add(Integer newEntry) {
        PriorityQueue.add(newEntry);
    }
}
