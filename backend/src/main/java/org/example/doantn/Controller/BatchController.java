package org.example.doantn.Controller;

import org.example.doantn.Dto.response.BatchDto;
import org.example.doantn.Entity.Batch;
import org.example.doantn.Service.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/batches") // Giữ nguyên hoặc điều chỉnh endpoint theo nhu cầu
public class BatchController {

    @Autowired
    private BatchService batchService;

    @GetMapping
    public ResponseEntity<List<BatchDto>> getAllBatches() {
        List<Batch> batches = batchService.getAllBatches();
        // Map danh sách Batch sang danh sách BatchDto sử dụng stream và map
        List<BatchDto> batchDtos = batches.stream()
                .map(batch -> new BatchDto(batch.getName()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(batchDtos, HttpStatus.OK);
    }

    // Các endpoint khác của BatchController (nếu có)
}