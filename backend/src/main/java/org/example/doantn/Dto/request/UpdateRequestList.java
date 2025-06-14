package org.example.doantn.Dto.request;

import lombok.Data;
import java.util.List;

@Data
public class UpdateRequestList {
    private List<Integer> requestIds;
}