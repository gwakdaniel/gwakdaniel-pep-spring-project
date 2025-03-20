package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Message;
import com.example.repository.MessageRepository;

@Service
public class MessageService {

    public MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message addMessage(Message message) {
        return messageRepository.save(message);
    }

    public List<Message> findAllMessages() {
        return messageRepository.findAll();
    }

    public Optional<Message> findMessageById(Integer messageId) {
        return messageRepository.findById(messageId);
    }

    public boolean existsById(Integer messageId) {
        return messageRepository.existsById(messageId);
    }

    public void deleteMessageById(Integer messageId){
        messageRepository.deleteById(messageId);
    }

    public List<Message> findMessagesByAccountId(Integer accountId){
        return messageRepository.findByPostedBy(accountId);
    }
}
