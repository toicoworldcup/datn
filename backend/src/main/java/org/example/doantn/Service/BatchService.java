package org.example.doantn.Service;

import org.example.doantn.Entity.Batch;
import org.example.doantn.Repository.BatchRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BatchService {

    @Autowired
    private BatchRepo batchRepo;

    public List<Batch> getAllBatches() {
        return batchRepo.findAll();
    }

    public Optional<Batch> getBatchById(Integer id) {
        return batchRepo.findById(id);
    }

    public Batch saveBatch(Batch batch) {
        return batchRepo.save(batch);
    }

    public Batch updateBatch(Integer id, Batch batchDetails) {
        return batchRepo.findById(id).map(batch -> {
            batch.setName(batchDetails.getName());
            return batchRepo.save(batch);
        }).orElseThrow(() -> new RuntimeException("Batch not found with id: " + id));
    }

    public void deleteBatch(Integer id) {
        batchRepo.deleteById(id);
    }
}
