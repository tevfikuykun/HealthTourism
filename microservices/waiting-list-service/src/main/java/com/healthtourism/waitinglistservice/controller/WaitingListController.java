package com.healthtourism.waitinglistservice.controller;
import com.healthtourism.waitinglistservice.entity.WaitingListItem;
import com.healthtourism.waitinglistservice.service.WaitingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/waiting-list")
@CrossOrigin(origins = "*")
public class WaitingListController {
    @Autowired
    private WaitingListService service;

    @GetMapping
    public ResponseEntity<List<WaitingListItem>> getAll(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) userId = 1L;
        return ResponseEntity.ok(service.getAll(userId));
    }

    @PostMapping
    public ResponseEntity<WaitingListItem> create(@RequestBody WaitingListItem item,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId != null) item.setUserId(userId);
        return ResponseEntity.ok(service.create(item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<WaitingListItem> update(@PathVariable Long id, @RequestBody WaitingListItem item) {
        return ResponseEntity.ok(service.update(id, item));
    }
}

