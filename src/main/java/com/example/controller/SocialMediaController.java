package com.example.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@RestController
public class SocialMediaController {

    AccountService accountService;
    MessageService messageService;

    SocialMediaController(AccountService accountService, MessageService messageService){
        this.accountService = accountService;
        this.messageService = messageService;
    }

    @PostMapping("/register")
    public ResponseEntity<Account> postAccount(@RequestBody Account account){
        if (account.getUsername() == null || account.getUsername().trim().isEmpty() ||
        account.getPassword() == null || account.getPassword().length() < 4) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (accountService.findByUsername(account.getUsername()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Account addedAccount = accountService.addAccount(account);
        return ResponseEntity.ok(addedAccount);
    }

    @PostMapping("/login")
    public ResponseEntity<Account> postLogin(@RequestBody Account loginRequest){
        Account existingAccount = accountService.findByUsername(loginRequest.getUsername());
        if (existingAccount != null && existingAccount.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.ok(existingAccount);
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
     
    @PostMapping("/messages")
    public ResponseEntity<Message> postMessage(@RequestBody Message message){
        if (message.getMessageText() == null || message.getMessageText().trim().isEmpty() ||
        message.getMessageText().length() > 255) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Optional<Account> account = accountService.findById(message.getPostedBy());
        if (account.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Message addedMessage = messageService.addMessage(message);
        return ResponseEntity.ok(addedMessage);
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages(){
        List<Message> messages = messageService.findAllMessages();
        return ResponseEntity.ok(messages);
    }
    
    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Optional<Message>> getMessageById(@PathVariable Integer messageId){
        Optional<Message> message = messageService.findMessageById(messageId);
        if (message.isPresent()){
            return ResponseEntity.ok(message);
        }
        else {
            return ResponseEntity.ok().build();
        }
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<?> deleteMessageById(@PathVariable Integer messageId) {
        if (messageService.existsById(messageId)) {
            messageService.deleteMessageById(messageId);
            return ResponseEntity.ok(1);
        }
        else {
            return ResponseEntity.ok().build();
        }
    }

    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<?> patchMessageById(@PathVariable Integer messageId, @RequestBody Map<String, String> payload) {
        String newMessageText = payload.get("messageText");

        if (newMessageText == null || newMessageText.trim().isEmpty() || newMessageText.length() > 255) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Optional<Message> message = messageService.findMessageById(messageId);
        if (message.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Message addedMessage = message.get();
        addedMessage.setMessageText(newMessageText);
        messageService.addMessage(addedMessage);

        return ResponseEntity.ok(1);
    }

    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getAllUserMessages(@PathVariable Integer accountId) {
        List<Message> messages = messageService.findMessagesByAccountId(accountId);
        return ResponseEntity.ok(messages);
    }
}
