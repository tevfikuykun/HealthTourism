package com.healthtourism.waitinglistservice.service;
import com.healthtourism.waitinglistservice.entity.WaitingListItem;
import com.healthtourism.waitinglistservice.repository.WaitingListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WaitingListService {
    @Autowired
    private WaitingListRepository repository;

    public List<WaitingListItem> getAll(Long userId) {
        return repository.findByUserId(userId);
    }

    public WaitingListItem create(WaitingListItem item) {
        return repository.save(item);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public WaitingListItem update(Long id, WaitingListItem item) {
        item.setId(id);
        return repository.save(item);
    }
}

